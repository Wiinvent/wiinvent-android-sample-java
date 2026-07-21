# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A **sample/demo app** for the Wiinvent Android advertising SDK (`tv.wiinvent:wiinvent-sdk-android`). Its purpose is to demonstrate how partners integrate each ad format the SDK offers. The app itself is intentionally thin: each ad format is one `Activity` wired to a screen button in `MainActivity`. When updating this repo, the goal is usually to keep these samples in sync with a new SDK version, not to build product features.

## Build & run

```bash
./gradlew assembleDebug          # build debug APK
./gradlew installDebug           # build + install on connected device/emulator
./gradlew test                   # JVM unit tests
./gradlew connectedAndroidTest   # instrumented tests (device/emulator required)
./gradlew clean
```

- Single unit test: `./gradlew test --tests "tv.wiinvent.android.wiinvent_android_sample_java.SomeTest"`
- There are no meaningful tests in this sample — the `test`/`androidTest` source sets are effectively empty scaffolding.
- Language is **Java** (a separate Kotlin sample exists elsewhere). App is single-module (`:app`), Java 11, minSdk 21, compileSdk/targetSdk 34.

### SDK dependency & repositories
- The SDK is pulled from a custom Maven repo declared in the root `build.gradle`: `https://maven.wiinvent.tv/repository/maven-releases/`. Bitmovin artifacts come from the Bitmovin Artifactory repo.
- Bumping the SDK means changing the `tv.wiinvent:wiinvent-sdk-android:<version>` line in `app/build.gradle`, then reconciling the sample code with any signature changes (see git history — commits are almost always "upgrade for <version>").
- `local.properties` (SDK dir) and `github.properties` are machine-local and not used by the build logic for auth.

## Version-documentation workflow (important)

This repo doubles as the SDK's integration documentation. Each SDK release has a matching markdown file:
- `readme.md` — the canonical parameter/constant/callback reference (fields like `tenantId`, `adSize`, `contentType`, `EventType`, written in Vietnamese).
- `docs/readme_<version>.md` — a snapshot per SDK version (e.g. `readme_1.10.18.md`, `readme_1.10.19.md`).

When upgrading the SDK version, the established pattern is: update `app/build.gradle`, adjust the sample activities, and **add a new `docs/readme_<newversion>.md`** describing what changed. Match the style and language of the existing version docs. (Note: some historical `readme_*.md` files at repo root are being moved into `docs/` — new ones go in `docs/`.)

## Architecture: the SDK Manager pattern

Every ad format is driven by a **singleton Manager** from the SDK, accessed via `Manager.Companion.getInstance()`. The integration shape is identical across all of them and is the key thing to understand:

1. **`init(...)`** — one-time setup with `tenantId`/`accountId` (the sample uses `"14"`), an `Environment` (`SANDBOX`/`PRODUCTION`), timeouts, bitrate, log level, and a skip offset.
2. **`addListener(...)` / `setLoaderListener(...)`** — an event listener whose callbacks fire on background threads. **Callbacks that touch views must be wrapped in `runOnUiThread(...)`** — every sample does this. Listeners drive showing/hiding the ad view, the skip button, and the report button.
3. **Build a `*RequestData` via its `.Builder()`** — carries content metadata (channelId, streamId, title, category, transId, uid20, segments, adSize, etc.). See `readme.md` for the full field reference.
4. **`requestAds(...)`** — hands the request + the target SDK view(s) to the manager.
5. **Lifecycle** — forward `onPause`/`onResume`/`onDestroy` to `pause()`/`resume()`/`release()`. Banners also expose `releaseBanner(view)` for per-view teardown (called on dismiss/error/recycle).

The Managers, one per format:

| Manager | Activity | Ad format |
|---|---|---|
| `InStreamManager` | `feature/InStreamActivity` | Video ads inserted into an ExoPlayer stream (VAST/IMA). Most complex sample. |
| `DisplayBannerManager` | `feature/DisplayBannerActivity` | Banners in a scrolling `RecyclerView` (multiple simultaneous banners). |
| `OverlayBannerManager` | `feature/OverlayBannerActivity` | Banner shown over a paused video player. |
| `AdsWelcomeManager` | `MainActivity` (`initWelcome`) | Full-screen "welcome" ad (TVC / combo / banner) shown on app entry. |

Two flows also exist but are commented out in `AndroidManifest.xml` / `MainActivity`: `BitmovinInStreamActivity` (in-stream using the Bitmovin player instead of ExoPlayer) and the `feature/backup/` activities (`GameActivity`, `OverlayActivity`, `ProfileActivity`) — older/backup demos. Don't assume these are live.

### Player integration (InStream / Overlay)
- The app owns the content player (**ExoPlayer 2.16.1** via `com.google.android.exoplayer2`); the SDK owns ad playback through an `AdPlayerView`.
- `InStreamActivity` builds media sources by inferring type (DASH/HLS/SS/Progressive) and wraps the upstream in a read-only cache (`VideoCache` / `CacheDataSource`).
- **Friendly obstructions**: `InStreamActivity.registerFriendlyObstruction()` registers every overlay UI view with the SDK (via `InStreamManager...createFriendlyObstruction`) so IMA viewability measurement ignores them. Any new overlay view on the player must be added here.
- The `showContentPlayer`/`hideContentPlayer` loader callbacks branch on `ContentType`: for `TV` (live) they mute/unmute; for `FILM`/`VIDEO` (VOD) they pause/play. Preserve that distinction when editing.

### Custom UI overriding SDK views
The SDK exposes overridable UI classes; the sample subclasses them to apply app branding (`feature/ui/`):
- `TV360SkipAdsButtonAds extends SkipAdsButtonAds` — inflates `layout_skip_button`, sets skip label/icon.
- `TV360ReportAdsButton` — the "report ad" button, shown/hidden via listener callbacks and passed into `requestAds`.
- `DisplayBannerAdapter` — creates a `BannerAdView` + report button per row programmatically and requests a banner per bound position; releases the banner on `onViewRecycled`.

## Conventions
- Sample values are hardcoded for the SANDBOX environment (accountId `"14"`, demo stream URLs, placeholder ids). These are demonstration constants, not secrets — but keep production credentials out.
- Log tags follow `ClassName.class.getCanonicalName()`; debug logs use a distinctive `=========` prefix for grep-ability.
- Inline comments in activities are in Vietnamese and explain each SDK parameter — keep this style when adding new sample parameters, since these files serve as documentation.
