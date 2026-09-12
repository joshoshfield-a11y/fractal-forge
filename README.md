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

## 1.1.0 (2026-09-10)
Core redesign after user feedback ("chaotic bubbles and foam, very hard to look at"):
Apollonian inversion replaced with a MANDELBULB — coherent sculptural form, soft-shadow lit,
AO, rim + spec, starfield void. Tight analogous palettes with slow drift (was rainbow spin).
Audio still sculpts: bass->power exponent, mids->symmetry rotation, treble->orbit-trap tint,
beats->camera breath, flux->orbit, RMS->exposure.

## 1.2.0 (2026-09-10)
MV-quality pass: HDR pipeline (scene RGBA16F -> soft-knee bright pass -> 2x separable
gaussian bloom -> composite), chromatic aberration pulsing on beats, stage floor with
emissive audio-driven grid, nebula + starfield atmosphere, handheld shake, iridescent
thin-film speculars. SCENE DIRECTION: sustained-bass drop detector auto-engages wide dolly
+ 6-wedge kaleidoscope + hot bloom; quiet passages = intimate close orbit. Debug hook:
__forgeSet('drop'|'kaleido'|'bass'|'beat', value).

## 1.3.0 (2026-09-10)
Audio-generated INFINITE FRACTAL ZOOM: log-polar fractal space (periodic in log r ->
dives forever, zero float degradation). Bass=zoom velocity+cell size, beats=inward kicks,
mids=fold symmetry/corridor, treble=detail density, flux=spiral, RMS=exposure.
Singularity core flares on beats. Zoom factor readout (x1eN).
EXPORT: SNAP = PNG frame; RECORD = WebM (video+audio) — both saved natively to
Downloads/FractalForge via chunked ForgeBridge (startFile/appendChunk/endFile,
MediaStore.Downloads API 29+); desktop browsers use blob download.

## 1.4.0 (2026-09-11)
COLOR FIELD REDESIGN (the white-screen fix). Root cause chain: (1) reversed-edge smoothstep
with exploding fwidth = UB garbage on real drivers (v1.3.0 white screen); (2) palette centered
at 0.5 brightness painted the whole frame hot with no dark anchor. New field: bounded-frequency
layered cosines + abs-folds in log-polar space (infinite zoom preserved), dark-anchored walls,
SQUARED palette for jewel-tone saturation, structural hue injection (fold-direction qh +
per-octave phase ph). Verified gates (hard): nearwhite<=2%, sat>=0.25, hueStd>=0.15,
ASCII composition preview. Idle: lum .42 sat .56 hueStd .28. Dive: lum .54 sat .47 hueStd .33.
RGBA8 FBO path forced (half-float sampling unreliable across drivers).

## 1.5.0 (2026-09-11)
INSTRUMENT CONTROLS. TUNE panel (12 live params): dive speed, cell density, fold symmetry,
color drift, saturation, vein glow, bloom, beat sensitivity, beat kick, exposure, kaleido
max, fold count (3/4/6/8) + reset-to-defaults. All map to shader uniforms every frame.
INTERACTION: drag x = spiral + corridor drift, drag y = dive speed (clamped, decays),
double-tap = beat kick, wheel = manual zoom. Verified headless: zero JS errors, panel
binds, sliders measurably change render, steering advances zoom, dbl-tap kicks.
