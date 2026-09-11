package com.fractal.forge

import android.Manifest
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import android.widget.Toast
import android.os.Bundle
import android.view.WindowManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

class MainActivity : ComponentActivity() {

    private lateinit var web: WebView
    private var pendingPermissionRequest: PermissionRequest? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    // assets/www served over https://appassets.androidplatform.net — a secure
    // context, so ES module scripts, fetch and getUserMedia all work (file://
    // blocks module scripts via CORS and was the white-screen root cause).
    // ---- chunked export bridge: blob -> base64 chunks -> MediaStore Downloads ----
    inner class ForgeBridge {
        private var stream: java.io.OutputStream? = null
        private var name: String = "export.bin"
        @JavascriptInterface
        fun startFile(fileName: String, mime: String) {
            name = fileName
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mime)
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/FractalForge")
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                stream = uri?.let { contentResolver.openOutputStream(it) }
            } catch (e: Exception) { stream = null }
        }
        @JavascriptInterface
        fun appendChunk(b64: String) {
            try { stream?.write(Base64.decode(b64, Base64.DEFAULT)); stream?.flush() } catch (e: Exception) {}
        }
        @JavascriptInterface
        fun endFile() {
            try { stream?.close() } catch (e: Exception) {}
            stream = null
            runOnUiThread { Toast.makeText(this@MainActivity, "Saved to Downloads/FractalForge", Toast.LENGTH_LONG).show() }
        }
    }
    private val exportBridge = ForgeBridge()

    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
        .build()

    // Runtime mic permission -> then answer the WebView's PermissionRequest
    private val micPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            pendingPermissionRequest?.let { req ->
                if (granted) req.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                else req.deny()
                pendingPermissionRequest = null
            }
        }

    // <input type="file"> bridge: system document picker -> back to the WebView.
    // Without this, the upload button in the web app is dead (default WebView behavior).
    private val filePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            filePathCallback?.onReceiveValue(if (uri != null) arrayOf(uri) else null)
            filePathCallback = null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Immersive edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }

        web = WebView(this)
        setContentView(web)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true          // localStorage graph persistence
            mediaPlaybackRequiresUserGesture = false
            allowContentAccess = true         // content:// URIs from the file picker
        }

        web.addJavascriptInterface(exportBridge, "ForgeBridge")

        web.webViewClient = object : WebViewClientCompat() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean = false
            // THE v4.0.1 BUG: without this override the appassets URL hits the
            // real network -> net::ERR_CACHE_MISS. Intercept and serve locally.
            override fun shouldInterceptRequest(
                view: WebView, request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                if (request.resources.any { it == PermissionRequest.RESOURCE_AUDIO_CAPTURE }) {
                    pendingPermissionRequest = request
                    micPermission.launch(Manifest.permission.RECORD_AUDIO)
                } else {
                    request.deny()
                }
            }

            override fun onShowFileChooser(
                webView: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                filePathCallback?.onReceiveValue(null)   // cancel any dangling request
                filePathCallback = callback
                val accepts = params.acceptTypes.filter { it.isNotEmpty() }.toTypedArray()
                filePicker.launch(if (accepts.isNotEmpty()) accepts else arrayOf("*/*"))
                return true
            }
        }

        web.loadUrl("https://appassets.androidplatform.net/assets/www/index.html")
    }

    override fun onDestroy() {
        filePathCallback?.onReceiveValue(null)
        web.destroy()
        super.onDestroy()
    }
}
