# Reelio Settings — Correction Pass Summary

## Source of truth

- `preview.html`
- GLM report: `Rapports/GLM_Validation_Report.md`
- Base validation branch: `agent/html-to-compose-settings`
- Correction branch: `agent/html-to-compose-settings-correction-active`

## Confirmed defects addressed

- **E01** — Added status-bar inset handling while preserving the 66dp HTML header geometry below system bars.
- **N01** — Added Android `BackHandler` to pop the internal Settings navigation stack.
- **C01** — Connection Check now enters a disabled `Checking…` state for 650ms and restores the status.
- **T01** — Toasts use the HTML copy and auto-dismiss after 1800ms.
- **V01** — Edit Profile validates blank name and invalid email before saving.
- **S01** — Edit Profile, Processing Defaults, and Credentials use a fixed bottom action bar with top divider and 21/13dp padding.
- **L01** — Localized previously uncovered Theme values, processing-language options, Check/Configured labels, credentials labels, About version labels, and related Arabic UI text.
- **D01** — Replaced the Material AlertDialog presentation with a bottom-aligned custom dialog matching the HTML card, icon tile, padding, and stacked 48/44dp actions.
- **F01** — Credential visibility controls are external 52dp buttons for Gemini/ElevenLabs only; remaining secret fields are plain password fields.
- **R01** — Credential fields, including R2 bucket and endpoint, use 52dp height.
- **P01** — Privacy title is 21sp and the legal-note block is a plain 12sp secondary surface without an icon.
- **A01** — About discovery-flow content is a 16dp bordered card without the previous info icon.
- **CH01** — Selected choice borders now use the semantic Reelio ring token through `outlineVariant`.

## Preserved behavior and geometry

- 21dp screen horizontal padding
- 21dp main vertical rhythm
- 13dp component padding
- 55dp setting rows
- 68dp choice cards
- 52/55dp field geometry according to the HTML
- 132x42dp processing language selector
- Light/Dark semantic Reelio palettes
- LTR/RTL direction mirroring
- 390x844 Compose preview target

## Remaining fidelity item requiring visual revalidation

The HTML uses the exact Hugeicons Stroke icon set and Geist/Noto Sans Arabic web fonts. The correction pass intentionally does not introduce an unvalidated icon/font dependency before the Android build is re-run. Material outlined icon substitutes therefore remain a known visual-fidelity delta until the corrected branch passes build/runtime validation and an exact Hugeicons/font integration can be validated safely.

## Required next validation

Run the same GLM validation flow against `agent/html-to-compose-settings-correction-active`, build with `:app:assembleDebug`, install on the emulator, and regenerate comparison screenshots. Do not merge to `main` during validation.
