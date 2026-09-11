# FRACTAL FORGE

Audio-reactive fractal engine for music-video generation. Single-file WebGL2 app —
no build step — wrapped in the hardened Android WebView shell.

## What it is
An Apollonian sphere-inversion fractal raymarched in a fragment shader. The AUDIO
constructs the geometry in real time:
- **Bass** -> inversion constant (structure density) + camera dolly
- **Mids** -> box-fold clamp (symmetry tightness) + camera height
- **Treble** -> iteration detail + orbit-trap color gain (hue spin)
- **Beats** -> zoom punch + detail burst (adaptive bass-threshold detector, 240ms refractory)
- **Spectral flux** -> tunnel travel + camera roll
- **RMS** -> exposure

3 palettes: NEBULA (indigo/magenta/cyan), INFERNO (gold/red), ABYSS (teal/green).

## Music-video workflow
1. LOAD TRACK (file picker) -> PLAY
2. Pick quality (720p/1080p render target) + palette
3. RECORD -> engine captures canvas @60fps + mix audio -> .webm download on stop
4. UI auto-hides while playing/recording for clean captures

## Verification (2026-09-10)
Headless Chromium (SwiftShader): shader compiles clean, zero JS errors, 565/576
distinct sample colors, full-frame non-black, frames animate. Multidex smoke gates
pass (shouldInterceptRequest + onShowFileChooser present).

## Android
GitHub Actions builds debug+release (debug-key signed) APKs. Sideload release;
bump versionCode per release. WebGL2 needs an up-to-date System WebView.

## License
COMMERCIAL LICENSE AGREEMENT (All Rights Reserved, Taylor Christian Matthesen).
