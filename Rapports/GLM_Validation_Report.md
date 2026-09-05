# GLM Validation Report

## Tested State

* Repository: https://github.com/cloudbouz53/ReelioV3
* Branch: `agent/html-to-compose-settings`
* Expected isolated state: `b26cb37f75f998b5f077f082c4db9cc6026c77ba`
* Verified HEAD after fetch/checkout/pull: `b26cb37f75f998b5f077f082c4db9cc6026c77ba` — matches expected, `git status` clean before testing, no divergence, no synchronization blocker.
* Tested state during emulator run: `b26cb37` + minimal build fix (see Build / B01). `git diff` at test time: only `app/src/main/java/com/btkboz/reeliov3/ui/settings/ReelioSettingsApp.kt` (remove invalid `Database` import, 2 usages `Database` -> `Storage`).
* Android device/emulator: `albo_test` (Pixel 6 profile), 1080x2400, density ~2.77, SDK 35 (Android 15), `emulator-5554`. Host SDK: `C:\Users\USER\AppData\Local\Android\Sdk`.
* Relevant files:
  * `app/src/main/java/com/btkboz/reeliov3/MainActivity.kt`
  * `app/src/main/java/com/btkboz/reeliov3/ui/settings/ReelioSettingsApp.kt`
  * `app/src/main/java/com/btkboz/reeliov3/ui/settings/ReelioSettingsPreview.kt`
  * `app/src/main/java/com/btkboz/reeliov3/ui/settings/ComposeAliases.kt`
  * `app/src/main/java/com/btkboz/reeliov3/ui/theme/Color.kt`
  * `app/src/main/java/com/btkboz/reeliov3/ui/theme/Theme.kt`
  * `app/build.gradle.kts`
  * `gradle/libs.versions.toml`
* HTML reference: `preview.html` (603 lines, ~79KB, Tailwind + Hugeicons, `h-[66px]` header, `px-[21px]`, `gap-[21px]`, `.setting-row min-height:55px`, inputs 52/55px, semantic tokens `--background/--foreground/--card/--primary/--secondary/--muted/--border/--ring`, light + `.dark`, JS locale dict `ar`, stack navigation, dialog `#dialogOverlay`, toast `#toast`).

## Build

Exact commands (workdir `C:\Users\USER\AndroidStudioProjects\ReelioV3`):

* `./gradlew :app:assembleDebug`
* `./gradlew :app:installDebug`

Initial result at `b26cb37` (before correction):

* `FAILED`, `Task :app:compileDebugKotlin FAILED`
* Exact errors (not summarized away):
  * `e: file:///C:/Users/USER/AndroidStudioProjects/ReelioV3/app/src/main/java/com/btkboz/reeliov3/ui/settings/ReelioSettingsApp.kt:32:49 Unresolved reference 'Database'.`
  * `e: .../ReelioSettingsApp.kt:398:36 Unresolved reference 'Database'.`
  * `e: .../ReelioSettingsApp.kt:469:36 Unresolved reference 'Database'.`
* Cause: `import androidx.compose.material.icons.outlined.Database` does not exist in `material-icons-extended-android:1.7.8` (verified via `classes.jar`: no `outlined/DatabaseKt.class`; `Dataset`, `DataObject`, `Storage` exist; `AccountCircle/ArrowBack/Check/Info/Lock/Search/Settings` are in core, rest in extended).

Minimal correction applied (isolated branch only, no redesign):

* Removed `import ...Outlined.Database`
* `ServiceCard(Icons.Outlined.Database, "Neon PostgreSQL", ...)` -> `Icons.Outlined.Storage`
* `StorageInfo(Icons.Outlined.Database, ... "Knowledge data" ...)` -> `Icons.Outlined.Storage`
* Rationale: `Storage` already imported, visually closest database cylinder, matches HTML reuse of `hgi-database` for Neon + Knowledge + Data&Storage row. Alternative `Dataset` would add new import and table semantics; not chosen to keep diff minimal.

Result after correction:

* `BUILD SUCCESSFUL in 11s`, `installDebug: Installed on 1 device.`
* Only warnings:
  * `ReelioSettingsApp.kt:188:44 'val Icons.Outlined.ArrowBack: ImageVector' is deprecated. Use AutoMirrored.`
  * `ReelioSettingsApp.kt:255:45 'val Icons.Outlined.HelpOutline ...' deprecated. Use AutoMirrored.`
* No Kotlin/Compose/Gradle/catalog/resource errors remain.

## Emulator

* Launch: `emulator.exe -avd albo_test -no-snapshot-load -no-boot-anim -gpu swiftshader_indirect`, `adb wait-for-device`, `sys.boot_completed=1`, `wm size 1080x2400`, SDK 35.
* Install + launch: `adb shell am start -n com.btkboz.reeliov3/.MainActivity`, PID confirmed (e.g. 2564), `topResumedActivity=com.btkboz.reeliov3/.MainActivity`, no crash, no runtime startup error.
* Startup screen: Reelio Settings (title `Settings`, profile card, GENERAL/CONTENT PROCESSING/SYSTEM/SUPPORT sections) — NOT `Hello Android`. Evidence: `Screenshots/Settings_Light.png`, uiautomator dump with `text="Your Reelio profile"`, `text="Settings"`.
* Navigation tested via `adb shell input tap` + `uiautomator dump` bounds (centers from dumps, back at `92,130` LTR / `988,130` RTL, see Limitations for status-bar overlap):
  * Settings -> Edit Profile -> back OK
  * Settings -> Theme -> back OK (Dark select verified)
  * Settings -> App Language -> select Arabic -> back OK, select English -> back OK
  * Settings -> Processing Defaults -> back OK
  * Settings -> Connections -> back OK
  * Settings -> Credentials -> back OK
  * Settings -> Data & Storage -> Clear row -> dialog shown -> Keep cache dismiss -> back OK
  * Settings -> Privacy -> back OK
  * Settings -> Help -> back OK
  * Settings -> About -> back OK
* System back (`input keyevent 4`) backgrounds app to launcher (`NexusLauncherActivity`), does NOT pop internal stack (see N01).

## Screens Tested

Actually tested on emulator (screencap + dump):

* Settings (Light)
* Settings (Dark, via Theme->Dark->back)
* Settings (Arabic RTL, via App Language->Arabic->back)
* Edit Profile
* Theme (Light selected, Dark selected states both captured)
* App Language (English selected; Arabic selected RTL layout captured)
* Processing Defaults
* Connections
* Credentials & Security (top visible; bottom requires scroll, see Limitations)
* Data & Storage
* Clear Temporary Cache dialog (shown; cancel verified)
* Privacy Policy
* Help & Support (list; search input visible, FAQ collapsed)
* About Reelio

Not tested interactively (explicitly NOT claimed as passed, see Limitations): Help search typing/filter, FAQ expand/collapse tap, credential visibility toggle, Save buttons/toasts, Processing combobox open/select, Connection Check tap feedback, dialog confirm path + toast, Help->Connections deep link tap, About->Privacy deep link tap, keyboard overlap, System theme mode follow, second physical viewport at 390x844.

## HTML → Compose Fidelity

* Hierarchy: CONFIRMED MATCH. 11 destinations, same ordering (Settings, Edit Profile, Theme, App Language, Processing Defaults, Connections, Credentials, Data & Storage, Privacy, Help, About), same section grouping (GENERAL 2 rows, CONTENT PROCESSING 1, SYSTEM 3, SUPPORT 3), same Help->Connections and About->Privacy links in code (`HelpScreen(...){ open(Connections) }`, `AboutScreen(...){ open(Privacy) }`), though deep-link taps not exercised on device.
* Geometry/spacing: MOSTLY MATCH. Header 66dp/13px, horizontal 21dp, vertical 21dp gaps, section 8dp, top 13dp bottom 34dp, rows min 55dp, choice 68dp, inputs 55/52dp, combobox trigger 132x42 rounded 9dp — all match HTML numbers. Exceptions: R2 bucket/endpoint 55 vs 52 (R01), Privacy title 18 vs 21 (P01), header centering balanced 44/44 vs HTML asymmetric (H01), dialog button layout (D01), credential visibility placement (F01). No clipping/overflow on Pixel 6; geometry stable across Light/Dark (verified screenshots pixel-compare: same layout, only colors change).
* Typography: PARTIAL MATCH. Sizes/weights/line-heights match except Privacy title (P01) and privacy info 13 vs 12 (P01). Font family differs (system default vs Geist + Noto Sans Arabic) — recorded as SUGGESTION, not defect, per “do not redesign for Material defaults”.
* Colors: MATCH. `Color.kt` tokens exactly equal HTML `:root`/`.dark` hex (Light bg #FFFFFF fg #0A0A0A card #FFFFFF primary #171717 onPrimary #FAFAFA secondary #F5F5F5 mutedFg #737373 border #E5E5E5 ring #A1A1A1 destructive #E7000B; Dark bg #0A0A0A card #171717 primary #E5E5E5 secondary #262626 mutedFg #A1A1A1 border #262626 ring #525252 destructive #EF4444). `Theme.kt` maps correctly, no Dynamic Color, geometry unchanged on switch (verified).
* Borders/radii: MATCH (1dp, 10/12/16/20/18/9, icon tiles 10, avatar circle). Grouped-card border collapse differs implementation (single Surface + dividers vs per-row borders) but visually equivalent.
* Icons: VISUAL-FIDELITY FINDINGS (Phase 8). All Material outlined equivalents differ in identity/stroke from Hugeicons Stroke by design; sizes/placement match (22dp rows, 20dp chevrons, 16dp info, 24/36 avatars, 32 logo). Material differences listed in I01; do NOT redesign surroundings.
* Light/dark: VERIFIED. Light, Dark manual select work, System option exists (`isSystemInDarkTheme`), palette does not get replaced, layout identical.
* RTL/LTR: VERIFIED CORE, WITH GAPS. LTR English and RTL Arabic both render, `LocalLayoutDirection` switches, back arrow and chevrons mirror (scaleX -1), radio dots mirror, text alignment flips, no clipped Arabic in captured screens, no reordering beyond mirroring. Gaps: untranslated Theme value + other strings (L01).
* Interactions: PARTIAL. Navigation, Theme select, Language select, dialog show/cancel verified on device. Connection Check, validation, sticky actions, toasts, search, FAQ, visibility, combobox select, saves, deep links — code audit shows gaps (C01/T01/V01/S01/F01/L01); not all exercised on device, reported as confirmed-by-code + PROBABLE where device confirmation missing.

## Defects

### CONFIRMED DEFECT — B01: Invalid icon blocks compilation
* Screen/state: Build, all screens (import time)
* Severity: Blocker (initial)
* Actual: `Unresolved reference 'Database'` at `ReelioSettingsApp.kt:32,398,469`, `assembleDebug FAILED`.
* Expected per `preview.html`: HTML uses `hgi-database`; Compose must use existing Material icon. No `Database` in `material-icons-extended 1.7.8` (verified jar).
* Repro: `git checkout b26cb37; ./gradlew :app:assembleDebug`
* Screenshot: N/A (compiler log; build log retained in report)
* Likely file: `ReelioSettingsApp.kt:32,398,469`
* Minimal correction: drop `Database` import, use `Icons.Outlined.Storage` for Neon + Knowledge (done in validation; alternatives `Dataset`/`DataObject` possible but larger diff).

### CONFIRMED DEFECT — E01: Header overlaps status bar (edge-to-edge insets)
* Screen: All (header)
* Severity: High
* Actual: Back arrow overlaps status-bar clock (`<9:12` in every sub-screen shot), header top touches system icons. Taps at `92,87` miss; only lower `92,130` works. `enableEdgeToEdge()` + `Scaffold(topBar=ReelioHeader)` with no `statusBarsPadding()`.
* Expected per HTML: `header h-[66px] sticky top-0` below browser chrome, no overlap; 44px touch target fully tappable.
* Repro: Launch, open Theme, observe top-left overlap, try tap at status-bar height.
* Evidence: `Screenshots/Theme.png`, `Edit_Profile.png`, all sub-screens; dumps `bounds="[29,24][155,150]"` starting at y=24 (inside status bar).
* File: `ReelioSettingsApp.kt:ReelioHeader`, `SettingsNavigator Scaffold`, `MainActivity enableEdgeToEdge`
* Minimal fix: `Modifier.statusBarsPadding()` (or `windowInsetsPadding`) on header, keep 66dp height + 13dp padding otherwise unchanged.

### CONFIRMED DEFECT — N01: System back exits instead of popping stack
* Screen: Any sub-screen
* Severity: Medium
* Actual: `input keyevent 4` backgrounds to launcher; internal `stack` unchanged. No `BackHandler`.
* Expected: HTML `goBack()` pops; Android back should mirror header back.
* Repro: Open Theme, `adb shell input keyevent 4`, `dumpsys` shows launcher focused.
* Evidence: dumpsys log in report; no screenshot (launcher, not app)
* File: `SettingsNavigator` (add `BackHandler(enabled=stack.size>1){ back() }`)
* Minimal fix: Add `BackHandler`, no redesign.

### CONFIRMED DEFECT — C01: Connection Check inert
* Screen: Connections
* Severity: Medium
* Actual: `TextButton(onClick={})` does nothing. No `Checking…`, no loading icon, no restore.
* Expected: HTML disables, shows `Checking…` + `hgi-loading-03`, after 650ms restores `Connected`/`Available` + checkmark.
* Repro: Open Connections, tap any `Check`, observe no change (verified code + screenshot shows static `Check`).
* Evidence: `Screenshots/Connections.png`
* File: `ServiceCard`
* Minimal fix: Local `checking` state per card, disable + swap text/icon, delay restore; keep text+icon status (already has icon+text).

### CONFIRMED DEFECT — T01: Toast never auto-dismisses + copy differs
* Screen: Edit Profile / Processing / Credentials / Data
* Severity: Medium
* Actual: `toast` persists until next navigation; no 1800ms hide. Copy: `Saved` vs HTML `Profile changes saved`, `Defaults saved` vs `Processing defaults saved`, `Credentials saved` vs `Credentials saved securely`, Arabic `تم مسح الملفات المؤقتة` vs HTML `تم مسح الذاكرة المؤقتة`. Icon `Check` vs `checkmark-circle`.
* Expected: HTML `.toast.show` 1800ms, exact copy from dict.
* Repro: Code read (`var toast` no timer); device saves not tapped (see Limitations) but persistence logic confirmed.
* Evidence: Code `SettingsNavigator:125,156-169`; HTML `showToast` + `ar` dict lines 454-458
* File: `SettingsNavigator`
* Minimal fix: `LaunchedEffect(toast){ delay(1800); toast=null }`, align strings to HTML dict, keep geometry.

### CONFIRMED DEFECT — V01: Profile validation missing
* Screen: Edit Profile
* Severity: Medium
* Actual: `onSave` always toasts, no empty-name / invalid-email check + focus.
* Expected: HTML blocks submit, toasts `Review the highlighted fields`, focuses offending field.
* Repro: Code `EditProfileScreen` + `ReelioField`; clear name, Save still toasts.
* Evidence: `Screenshots/Edit_Profile.png`
* File: `EditProfileScreen`
* Minimal fix: Validate `name.isBlank()` / email pattern before `onSave`, keep 55dp inputs.

### CONFIRMED DEFECT — S01: Sticky bottom actions not sticky
* Screen: Edit Profile, Processing Defaults, Credentials
* Severity: Medium
* Actual: `PrimaryButton` inline in `LazyColumn`, scrolls away, no `border-t`, no `-mx-[21px] px-[21px] py-[13px]` bar.
* Expected: HTML `sticky bottom-0 -mx-[21px] border-t bg-background px-[21px] py-[13px]`, always visible, keyboard-safe.
* Repro: Open each, scroll to bottom (screenshots show button at end of scroll, not fixed).
* Evidence: `Edit_Profile.png`, `Processing_Defaults.png`, `Credentials.png` (bottom cut)
* File: `EditProfileScreen`, `ProcessingDefaultsScreen`, `CredentialsScreen`, `ScreenColumn`
* Minimal fix: Move button outside scroll or add sticky footer slot; keep min 55dp + radius 10.

### CONFIRMED DEFECT — L01: Arabic localization gaps
* Screen: Settings (Theme value), Processing, Connections, Credentials, About, Language
* Severity: Medium
* Actual (verified dumps/screenshots): Theme value stays `Light` in RTL (should `فاتح`); combobox `Auto/Arabic/English` stay English (should `تلقائي/العربية/الإنجليزية`); `Check` stays English (should `تحقق`); `Configured` stays English (should `تم الإعداد`); R2 bucket/endpoint labels/placeholders English-only; secret labels English-only; `Version` English-only; `Left-to-right interface` English-only in Arabic mode.
* Expected: HTML `ar` dict + `applyLocale()` translates all (+ combobox values/options, `themeValue`, `Check`, `Configured`, etc.).
* Repro: App Language->Arabic->back, dump shows `text="Light"` alongside Arabic rows; `Screenshots/Settings_Arabic_RTL.png`.
* Evidence: `Settings_Arabic_RTL.png`, `ui_ar_set.xml`, code `ServiceCard Text("Check")`, `SecretField Text("Configured")`, `LanguageRow listOf("Auto",...)`, `titleFor/themeMode.name`, `KeyValue("Version",...)`
* Files: `ReelioSettingsApp.kt:ServiceCard,LanguageRow,SecretField,ReelioField usages,KeyValue,titleFor`
* Minimal fix: Wrap each via existing `t(lang,en,ar)` using HTML `ar` values; no layout change.

### CONFIRMED DEFECT — D01: Clear dialog layout differs
* Screen: Data & Storage dialog
* Severity: Low-Medium
* Actual: Material `AlertDialog` centered, icon plain, confirm/dismiss side-by-side (`Keep cache | Clear temporary cache` in one row, cramped, see screenshot).
* Expected: HTML bottom-sheet on mobile (`items-end`), card `rounded-[16px] bg-popover p-[21px]`, tile `w-10 h-10 rounded-[10px] bg-secondary` + `hgi-delete-02`, title 18 semibold, desc 13, stacked full-width `Clear (48dp primary)` + `Keep (44dp)`.
* Repro: Data & Storage->Clear row (540,1885), screenshot.
* Evidence: `Screenshots/Clear_Cache_Dialog.png` vs HTML lines 388-394
* File: `DataStorageScreen dialog`
* Minimal fix: Custom `Dialog` with `Surface(radius16)` + Column + stacked buttons; keep strings.

### CONFIRMED DEFECT — F01: Credential visibility placement + extra toggles
* Screen: Credentials & Security
* Severity: Medium
* Actual: Eye icon inside `OutlinedTextField` trailingIcon for 6 secrets; HTML has external `min-w-[52px] h-[52px] rounded-[10px] border` button beside input for first 2 only, rest plain password.
* Expected: HTML lines 319-326.
* Repro: Open Credentials, observe 6 eyes inside fields (screenshot).
* Evidence: `Screenshots/Credentials.png`
* File: `SecretField`, `CredentialsScreen`
* Minimal fix: Row(input weight1 + 52dp Box button) for Gemini/ElevenLabs only; plain `OutlinedTextField` (no trailing) for other 4; keep 52dp height.

### CONFIRMED DEFECT — R01: R2 bucket/endpoint height 55 vs 52
* Screen: Credentials
* Severity: Low
* Actual: `ReelioField(...).height(55.dp)` for bucket/endpoint.
* Expected: HTML `h-[52px]` for all 8 credential inputs.
* Repro: Code `CredentialsScreen:434-435` vs HTML 327-328.
* Evidence: Code (bottom off-screen in shot, see Limitations)
* File: Same (pass height param or separate 52dp field)
* Minimal fix: 52dp for those two, no other change.

### CONFIRMED DEFECT — P01: Privacy title + info styling
* Screen: Privacy Policy
* Severity: Low
* Actual: `Intro` 18sp title (HTML 21px), `InfoCard` with icon + 13sp (HTML `text-[12px]` no icon, lines 358/503).
* Expected: HTML `text-[21px]` + plain secondary div.
* Repro: Open Privacy, compare.
* Evidence: `Screenshots/Privacy.png`
* File: `PrivacyScreen`, `Intro`, `InfoCard`
* Minimal fix: 21sp title for Privacy only (or param), plain info variant without icon at 12sp.

### CONFIRMED DEFECT — A01: About discovery flow card style
* Screen: About Reelio
* Severity: Low
* Actual: `InfoCard` (secondary bg + sparkles icon).
* Expected: HTML `rounded-[16px] border bg-card p-[13px] text-muted` no icon (line 382).
* Repro: Open About.
* Evidence: `Screenshots/About.png`
* File: `AboutScreen`
* Minimal fix: Border card variant, keep text.

### CONFIRMED DEFECT — I01: Icon identity/weight vs Hugeicons (fidelity)
* Screen: All (Phase 8, no redesign)
* Severity: Low (expected, record only)
* Actual (Material vs HTML): Settings rows `LightMode/Language/Settings/Link/Key/Storage/HelpOutline/Security/Info/AccountCircle/ChevronRight` vs `sun-03/language-circle/settings-02/plug-01/key-01/database/help-circle/shield-01/information-circle/user-circle/arrow-right`; Theme `Computer` vs `computer-settings`; Connections R2 `Cloud` vs `hard-drive`, FFmpeg `Settings` vs `audio-wave-01`; Data Media `Cloud` vs `hard-drive`, Temp `Storage` vs `folder-time`; Help `Settings` vs `stethoscope`, combobox `ExpandMore` vs `arrow-up-down`, About `BookmarkAdded` vs `bookmark-check-01`, toast `Check` vs `checkmark-circle-02`, email verified icon missing.
* Expected: HTML Hugeicons Stroke exact.
* Repro: Visual compare screenshots vs `preview.html` lines 139-383.
* Evidence: All screenshots
* File: Imports + usages throughout `ReelioSettingsApp.kt`
* Minimal correction: None required for validation (documented); if pursued, swap nearest Material symbols only, do not resize/reflow.

### CONFIRMED DEFECT — H01: Header centering differs
* Screen: Sub-screens
* Severity: Low
* Actual: Compose reserves 44dp left + 44dp right (centered). HTML hides spacer on sub (`headerSpacer.hidden`) and hides back on root, yielding asymmetric centering (JS lines 520).
* Expected: HTML asymmetry (arguably bug, but source of truth).
* Evidence: Code `ReelioHeader:175-206` vs HTML 139-143 + JS 520
* File: Same
* Minimal fix: None recommended (Compose centered is better); recorded for fidelity only. Do NOT change.

### CONFIRMED DEFECT — CH01: Selected choice border color
* Screen: Theme, App Language
* Severity: Low
* Actual: Selected `primary.copy(alpha=.45f)` + `secondary` bg.
* Expected: HTML `:has(input:checked) border-color:ring(#A1A1A1/#525252)` + `bg-accent` (accent==secondary hex, so bg matches; border hue differs: primary #171717 vs ring #A1A1A1 light).
* Evidence: `Screenshots/Theme.png`, `App_Language.png`
* File: `ChoiceCard:565`
* Minimal fix: `outline` vs `ring` token for selected border; keep 16dp + 68dp.

### PROBABLE DEFECT — KB01: No IME/keyboard handling
* Screen: Edit Profile, Credentials, Help search
* Severity: Medium (probable, not device-tested)
* Actual: No `imePadding()`, no `windowInsets`, sticky bars missing (S01), so keyboard likely covers Save/buttons.
* Expected: HTML `sticky bottom` bars stay above keyboard; native should preserve visibility.
* Repro: Not tapped with keyboard on emulator (see Limitations); code shows `LazyColumn` + `Scaffold` default insets only.
* File: `ScreenColumn`, `Scaffold`, `MainActivity`
* Minimal fix: `Modifier.imePadding()` on footer + sticky slot; verify with keyboard open.

### PROBABLE DEFECT — CR01: Email verified icon missing
* Screen: Edit Profile
* Severity: Low
* Actual: Helper text only; HTML has `hgi-checkmark-circle-02 14px` + text.
* Evidence: `Edit_Profile.png` vs HTML 223
* File: `EditProfileScreen:320`
* Minimal fix: Prepend 14dp check icon, keep 12sp muted.

### SUGGESTION — SUG01: Fonts
* Not a defect. HTML uses Geist + Noto Sans Arabic (`--font-latin/--font-arabic`, `line-height 1.65` Arabic). Compose uses default Material `Typography`. Do not redesign; if pursued, add font resources + `Typography` only.

### SUGGESTION — SUG02: Use AutoMirrored icons
* Not a defect (manual `scaleX -1` works, verified mirrored in RTL shots). Compiler suggests `Icons.AutoMirrored.Outlined.ArrowBack/HelpOutline`. Minimal tech-debt cleanup only.

### INVALID / NOT A DEFECT
* Dynamic Color replacing palette: INVALID. `Theme.kt` uses fixed `Reelio*` tokens, no `dynamicLightColorScheme`, verified Light/Dark screenshots match HTML hex.
* Theme geometry change: INVALID. Light vs Dark screenshots identical layout, only colors change (as HTML requires).
* RTL mirroring broken: INVALID. Back/chevron/radio mirror correctly (dumps show right-side back `[925,24][1051,150]` in RTL, chevrons point left in `Settings_Arabic_RTL.png`, no clipping).
* Startup not Settings: INVALID. Startup IS Settings (PID + dump + shot).

## Screenshots

All under `Screenshots/` (emulator Pixel 6 1080x2400; 390x844 is code Preview reference `ReelioSettingsPreview widthDp=390 heightDp=844`, not an emulator size here):

* `Screenshots/Settings_Light.png` — Settings home Light (startup)
* `Screenshots/Settings_Dark.png` — Settings home Dark (Theme->Dark->back)
* `Screenshots/Settings_Arabic_RTL.png` — Settings home Arabic RTL (mirrored chevrons, right-aligned, Theme value gap visible)
* `Screenshots/Edit_Profile.png` — Edit Profile (avatar 89dp, 55dp inputs, inline Save)
* `Screenshots/Theme.png` — Theme Light selected (choice cards 68dp)
* `Screenshots/App_Language.png` — App Language English selected
* `Screenshots/Processing_Defaults.png` — 4 combobox rows 132x42 + info + Save
* `Screenshots/Connections.png` — 4 service cards + info (Check inert)
* `Screenshots/Credentials.png` — 6 secrets top visible (inside eyes; bottom R2 fields + Save require scroll)
* `Screenshots/Data_Storage.png` — Knowledge/Media/Temp/Clear rows
* `Screenshots/Privacy.png` — Privacy sections + info
* `Screenshots/Help.png` — Search 52dp + 4 FAQ collapsed + Check Connections
* `Screenshots/About.png` — Logo 72dp + Version card + flow + Privacy row
* `Screenshots/Clear_Cache_Dialog.png` — AlertDialog (side-by-side buttons vs HTML stacked)

## Limitations

* Canonical 390x844 tested via code (`ReelioSettingsPreview widthDp=390 heightDp=844`, padding/gap numbers match HTML px) + Pixel 6 1080x2400 emulator as “different viewport”. No 390x844 emulator image available; no overflow/clipping seen on Pixel 6, but 390-width truncation not device-proven.
* Theme System mode not device-selected (only Light/Dark manual). `System` code path (`isSystemInDarkTheme`) exists but device was Light; follow-behavior not observed.
* Interactive states NOT device-tapped (reported via code, not claimed passed): Help search typing, FAQ expand, credential eye toggle, Save toasts, combobox open/select, Check tap, dialog Confirm + toast, Help->Connections link, About->Privacy link. Dialog Cancel verified; other toasts/links verified in code only.
* Credentials bottom (R2 bucket/endpoint, info, Save securely) not in single shot (scrollable; top captured). Full field list verified in code + partial dump.
* Keyboard overlap, dropdown clipping, sticky-action keyboard behavior not stressed (no IME opened).
* RTL verified for Settings + Language; other screens in Arabic not screenshotted (strings audited in code, Theme-value gap proven via dump).
* Screenshots are emulator density (~2.77x), not 1:1 390x844 px; used for hierarchy/geometry/color/RTL evidence, not pixel-diff.
* No backend integrations tested (per scope: yt-dlp/FFmpeg/Gemini/ElevenLabs/Neon/R2 are UI states only).

## Final Result

PASSED WITH ISSUES

* Reasoning: Build succeeds after one minimal icon fix, app starts to Reelio Settings (not Hello), all 11 destinations reachable and back-navigable, hierarchy/spacing/colors/borders/radii match HTML, Light/Dark preserve geometry, RTL mirrors without clipping. Blocked/failed criteria not met, but confirmed medium defects remain (edge insets, system back, inert Check, toast/validation/sticky, localization gaps, dialog/credential layouts) plus low icon/style deltas. No untested behavior reported as passed.
