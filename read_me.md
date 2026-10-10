# Expense Tracker — Mobile Design Spec

Brief for designing / generating the mobile UI (Claude Design or any tool). Mobile-first; desktop is a progressive enhancement. Source of truth for tokens: `assets/ds/tokens/*.css`. Screen intent: `Intent Docs/*.dc.html`.

## 1. Principles
- **Glanceable money**: the first thing on every screen is a single number or sentence ("₱X due today").
- **One-thumb use**: primary actions in the bottom 40% of the screen; no hover-only affordances.
- **Calm, trustworthy, low-noise**: white canvas, one accent (indigo), color only for meaning (green = up/ok, red = down/overdue).
- **Fast entry**: adding an expense takes ≤ 3 taps + amount.
- **Secure by default**: sensitive values maskable (eye / eye-off), clear login/logout, session timeout messaging.

## 2. Viewport & Layout
| Item | Value |
|---|---|
| Design frame | 390 × 844 (iPhone 14); verify at 360 × 640 and 430 × 932 |
| Side gutter | 16px (`--ent-space-base`) |
| Vertical rhythm | 8px grid; section gap 24–32px |
| Max content width | full width on mobile; `--container-max` 1200px from ≥ 1024px |
| Safe areas | respect `env(safe-area-inset-*)` top and bottom |
| Breakpoints | `<640` mobile · `640–1023` tablet (2-col cards) · `≥1024` desktop |
| No horizontal page scroll | tables become stacked cards |

## 3. Design Tokens
**Color**
- Primary `--ent-primary` #3b2fd1 · active #2c22a8 · disabled #b7b3e0 · tint #efeefb
- Accent (illustrative only) amber #e8a317
- Surfaces: canvas #fff · soft #f7f7f8 · strong #eef0f2 · dark #0b0c0f / #17191e / #22252b
- Text: ink #0b0c0f · body #5c616b · muted #80858d · disabled #a9adb4
- Borders: hairline #dfe2e6, soft #eef0f2
- Semantic: up/positive #0a9b61 · down/negative #c92a38
- Dark mode: use the `--surface-dark*` and `--text-on-dark*` tokens; same indigo for primary.

**Typography** — Manrope (UI), JetBrains Mono (all amounts)
| Role | Spec |
|---|---|
| Hero number / headline | 36px / 1.11, 400, −0.6 tracking (mono for amount) |
| Screen title (H1) | 28–32px, 700, −0.8 tracking |
| Section title (H2) | 22px, 700, −0.4 tracking |
| Card title | 16–18px, 600 |
| Body | 16px/1.5 (14px for secondary) |
| Caption / label | 12–13px, 600 caps label with 0.6 tracking |
| Amount | mono 500, 18px (14px in dense rows), right-aligned, tabular |

**Radius**: inputs/buttons 12px · cards 16px · sheets 24px top corners · chips/pills 100px.
**Elevation**: prefer hairline borders over shadows; shadow only on bottom sheet and floating button (`tokens/elevation.css`).
**Icons**: Lucide, 24px, 1.75 stroke, from `assets/icons/*.svg`.
**Touch targets**: min 44 × 44; primary buttons 56 high; inputs 48 high.

## 4. Navigation
Bottom tab bar (64px + safe area), 4 tabs, icon + 12px label, active = indigo with tint pill:
1. **Dashboard** — `layout-grid`
2. **Expenses** — `receipt` / `list`
3. **Budget** — `wallet` / `piggy-bank`
4. **History** — `chart-column`

Top bar: screen title (left), profile `user` / logout `log-out` (right). Center **FAB "+"** (56px, indigo, `plus`) above the tab bar opens *New expense* sheet. Hide FAB on History.

## 5. Screens

### 5.1 Login
- Logo + app name, email field, password field with `eye`/`eye-off` toggle, primary "Log in" (56px).
- Inline error under field (`circle-alert`, red). Lock icon + "Secure session" note.
- Future: "Continue with Google" secondary button above the form.

### 5.2 Dashboard (`Dashboard.dc.html`)
1. Hero: **"₱X due today"** (mono amount) + subline (e.g. next due date).
2. **Due today** list — rows: category icon, name, amount, "Mark paid" check button (`circle-check`). Overdue rows get red `triangle-alert`.
3. **Where it goes** — donut or stacked bar by category, legend as list below (no side legend on mobile).
4. **Groups / cutoffs** cards (per-cutoff totals), horizontal scroll-snap if > 2.
5. **Categories** — 2-col grid of tiles (icon, name, amount, % of budget).
6. Insight card (`lightbulb`/`sparkles`) — one tip, dismissible.

### 5.3 My Expenses (`My Expenses.dc.html`)
- Sticky header: title, search (`search`), filter chip row (category, status, month) — horizontally scrollable.
- List grouped by date; row = category icon · name + meta (category, due) · mono amount · status chip (Paid / Due / Overdue).
- Swipe left: delete (`trash-2`, confirm); swipe right: mark paid. Overflow `ellipsis-vertical` as fallback.
- Empty / no results state: `search-x`, "No expenses match", clear-filters button.
- **New expense bottom sheet** (24px top radius, drag handle): amount (large mono, numeric keypad), name, category picker (icon grid), due date, repeat (monthly / once), split (`split`) optional, notes. Sticky "Save expense" button at the bottom.

### 5.4 Budget Flow (`Budget Flow.dc.html`)
- Headline sentence: "You have **₱X** left after bills" (green) / "over by ₱X" (red).
- Flow list: income → fixed bills → savings → remaining, as stacked horizontal bars with labels.
- Per-cutoff toggle (segmented control: 1st / 2nd cutoff / Month).
- Edit budget per category via inline stepper or tap-to-edit sheet.

### 5.5 History (`History.dc.html`)
- Cycle selector (`calendar-range`) with prev/next arrows; H2 cycle label.
- **Monthly movement** — column chart (`chart-column`), scrollable, tap bar for tooltip sheet.
- **Per cutoff** — compare two bars with up/down delta chip (green/red + `trending-up`).
- **Month by month** — expandable rows (chevron-down) with category breakdown; `download` for CSV export.

## 6. Core Components
| Component | Notes |
|---|---|
| Button primary | 56h, indigo, white 600 16px, radius 12; pressed #2c22a8; disabled #b7b3e0 |
| Button secondary | 48h, white, hairline border, ink text |
| Input | 48h, hairline border, 12 radius, label above (caption-strong), focus 2px indigo ring, error red |
| Chip | 32h pill; selected = indigo tint bg + indigo text |
| Status chip | Paid green tint · Due indigo tint · Overdue red tint (icon + text, never color alone) |
| Card | white, hairline border, 16 radius, 16 padding |
| List row | min 64h, 16 gutter, icon in 40px tint circle |
| Bottom sheet | 24 top radius, scrim 40% ink, drag handle, max 90vh |
| Toast | bottom above tab bar, `circle-check`, 3s, Undo for destructive actions |
| Segmented control | 40h, pill, indigo selected |
| Skeleton | soft gray shimmer for loading lists |

## 7. States & Behavior
- **Loading**: skeletons, never spinners over full screens.
- **Empty**: icon + one-line explanation + primary action ("Add your first expense").
- **Error**: inline message + Retry; keep entered form data.
- **Offline**: banner "You're offline — changes can't be saved".
- **Destructive**: confirm sheet with red button; offer Undo toast after.
- **Currency/format**: ₱ with thousands separators, 2 decimals, minus sign for outflow, mono + right aligned.
- **Pull to refresh** on Dashboard and Expenses.
- **Motion**: 150–250ms ease-out; sheet slide-up 250ms; respect `prefers-reduced-motion`.

## 8. Accessibility
- Contrast ≥ 4.5:1 for text (muted #80858d only for ≥ 14px non-essential text).
- Status never by color alone (icon + label).
- Min tap target 44px; visible focus ring; labels on all inputs; `aria-label` on icon-only buttons.
- Supports dynamic type up to 200% without clipping; amounts wrap below label instead of truncating.
- Dark mode via `prefers-color-scheme`.

## 9. Implementation Notes (PHP + MySQL stack)
- Server-rendered PHP pages (`index.php`, `login.php`, `logout.php`); icons via `app/icons.php`.
- Plain CSS using the token files; add `<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">`.
- Progressive enhancement: sheet and swipe are JS extras; every action must also work via normal links/forms.
- All forms use CSRF tokens, prepared statements, and output escaping.
- Future Google login: keep the login layout slot for an OAuth button.

## 10. Prompt for Claude Design
> Design a mobile-first (390×844) personal expense tracker UI using the tokens in `DESIGN-MOBILE.md`: Manrope + JetBrains Mono for amounts, indigo #3b2fd1 primary, white canvas, hairline borders, Lucide icons. Produce Login, Dashboard, My Expenses (with New-expense bottom sheet), Budget Flow and History, with a 4-tab bottom bar and center FAB. Include empty, loading, error and overdue states, plus a dark-mode variant.
