# 📖 My Business — The Ultimate App Architecture & System Manual (The App Bible)

> **Current Version:** `v5.3` | **Application ID:** `com.aistudio.dailyreport.bzrp` | **Target SDK:** Android 16 (API 36.1) | **Min SDK:** Android 7.0 (API 24)

---

## 🌟 Executive Summary

**My Business** is an offline-first, enterprise-grade Android management suite tailored for modern service businesses, salons, beauty clinics, consultancies, and retail shops. It bridges operational retail checkout, continuous double-entry accounting, automated appointment scheduling with proactive client reminders, intelligent caller-ID popups, professional PDF financial reporting, and bidirectional Google Sheets cloud sync into a unified, lightweight, and resilient mobile application.

---

## 🏗️ Complete Technology Stack & Libraries

The application adheres strictly to modern Android development best practices, Clean Architecture, and Android Jetpack guidelines.

```
┌─────────────────────────────────────────────────────────────┐
│                       User Interface                        │
│          Jetpack Compose M3 (Material Design 3)             │
├─────────────────────────────────────────────────────────────┤
│                     Presentation State                      │
│             MVVM + StateFlow / Coroutines                   │
├─────────────────────────────────────────────────────────────┤
│                     Data Access Layer                       │
│    Room SQLite (Offline-First)  +  Encrypted SharedPreferences │
├──────────────────────────────┬──────────────────────────────┤
│         Local Device         │          Cloud Sync          │
│   WorkManager + AlarmManager │   Google Apps Script REST    │
│  Telephony + Overlay Service │   OkHttp3 + Moshi JSON       │
└──────────────────────────────┴──────────────────────────────┘
```

### 1. Core Language & Runtime
- **Kotlin (`2.1.20-RC3`)**: 100% Kotlin codebase utilizing structured concurrency, sealed classes, and immutability.
- **Java Virtual Machine**: Java 11 bytecode compatibility.
- **Android Gradle Plugin (AGP `8.9.0`)**: Modern build pipeline with Kotlin DSL (`build.gradle.kts`).

### 2. UI & Styling Framework
- **Jetpack Compose (`BOM 2025.02.00`)**: Fully declarative reactive UI with zero XML layout files.
- **Material Design 3 (`androidx.compose.material3`)**: Full Material You theming support including dynamic light/dark theming, tinted surfaces, high-contrast states, and standardized 8dp elevation/spacing grids.
- **Compose Navigation (`androidx.navigation.compose:2.8.8`)**: Single-Activity architecture with robust route management and backstack transitions.
- **Material Extended Icons (`androidx.compose.material.icons.extended`)**: Comprehensive icon set for tactile navigation and clear visual scanning.

### 3. Asynchronous & Reactive Architecture
- **Kotlin Coroutines (`1.10.1`)**: Non-blocking asynchronous threading for file I/O, database access, alarms, and network calls.
- **Kotlin Flow & StateFlow**: Reactive state observation with lifecycle-safe collectors (`collectAsStateWithLifecycle`).
- **Android Lifecycle (`androidx.lifecycle`)**: ViewModel lifecycle-bound scope, preventing memory leaks during configuration changes.

### 4. Local Persistence & Storage
- **Room Database (`2.7.0-alpha13`) with KSP**:
  - Embedded SQLite database engine.
  - Type-safe compile-time SQL verification.
  - Entities: `TransactionEntity`, `AppointmentEntity`.
  - Continuous transaction indices, reactive Flow queries, and automated offline queuing.
- **DataStore & SharedPreferences**:
  - `AppPreferences`: Stores business profile, Google Web App script URL, theme preferences (System/Light/Dark), SMS templates, notification toggles, and sync timestamps.

### 5. Cloud Integration & Networking
- **Google Apps Script Web App Engine**: Serverless backend connecting Android directly to the user's private Google Sheets workbook.
- **OkHttp3 & HttpURLConnection (`4.12.0`)**: High-performance HTTP client handling redirects (`HTTP 302/307`) typical of Google Script deployments with custom timeouts and backoff.
- **Moshi (`1.15.2`) + Kotlin CodeGen**: Reflection-free JSON parsing and serialization between Android Room entities and Google Sheets rows.
- **WorkManager (`androidx.work.runtime.ktx:2.10.0`)**: Background sync worker (`SyncWorker`) ensuring pending offline transactions sync reliably when internet connectivity is re-established.

### 6. System & Hardware Integrations
- **Exact Alarms (`AlarmManager`)**: Triggers precise 24-hour pre-appointment client reminders.
- **Broadcast Receivers**:
  - `AppointmentReminderReceiver`: Dispatches interactive local notifications with direct SMS action triggers.
  - `BootReceiver`: Listens for device restarts (`BOOT_COMPLETED`) to immediately restore all upcoming appointment reminder alarms.
  - `CallReceiver`: Telephony listener (`READ_PHONE_STATE`, `READ_CALL_LOG`) detecting incoming customer phone numbers.
- **Floating Overlay Service (`CallOverlayService`)**: High-priority foreground service (`SYSTEM_ALERT_WINDOW`) displaying an on-screen customer card during incoming calls with their next scheduled appointment and quick notes.
- **Contacts Contract (`android.provider.ContactsContract`)**: Integrates with Android's system phonebook to match incoming unknown numbers to saved clients.
- **Native Document Rendering (`android.graphics.pdf.PdfDocument`)**: Generates vector-sharp A4 financial statements and balance sheet reports directly on-device without third-party cloud engines.
- **Android FileProvider**: Secure `content://` URI file sharing for receipts and PDF exports.

---

## 📱 Detailed Features & Functional Architecture

### 1. 🛒 Point of Sale (POS) & Income Logging
- **Visual Catalog & Quick Cart**: Pre-configured menu with salon and beauty service categories (Threading, Waxing, Tinting, Facials, Hair Care, Massage, Retail Products).
- **Custom Item Creation**: Add custom, unlisted, or ad-hoc services on the fly with custom pricing and itemized notes.
- **Payment Method Differentiation**: Distinguishes between **Cash** and **Card / EFTPOS** for end-of-day bank reconciliations.
- **Instant Tax Invoice Generation**:
  - Displays business header: Business Name, ABN/ACN, Address, Phone, Email.
  - Itemized breakdown with unit counts, rates, subtotal, and total amount.
  - Generates shareable text/receipts formatted for instant dispatch to clients via WhatsApp, SMS, or Bluetooth thermal receipt printers.

### 2. 💸 Operational Expenses & Fixed Bills Management
- **Expenses Module (`- Expenses`)**: Dedicated tracker for day-to-day consumable purchases, salon supplies, sanitizers, refreshments, and petty cash expenditures.
- **Bills Module (`- Bills`)**: Tracks scheduled fixed operational costs such as shop rent, power, water, broadband, merchant terminal fees, and software licenses.
- **Smart Date Allocation**: Entries can be stamped for today or retroactively recorded for any selected date in the past.

### 3. 📊 Continuous Day-by-Day Balance Sheet
- **Cumulative Running Balance**: Tracks net financial health continuously over time:
  $$\text{Running Balance} = \sum (\text{Income}) - \sum (\text{Expenses}) - \sum (\text{Bills})$$
- **Day-by-Day Grouping**: Expandable daily ledger cards showing:
  - Total daily earnings broken down by cash vs. card.
  - Total daily outgoings (expenses + bills).
  - Daily net surplus or deficit.
  - Opening balance brought forward vs. closing balance carried forward.
- **Granular Record Management**:
  - **Inline Editing**: Tap the pencil icon to modify amounts, notes, or payment methods.
  - **Safe Deletion**: Remove erroneous transactions with instant automatic recalculation of subsequent running balances.
- **Sync Status Badges**: Real-time visual indicators on each record (`Synced` in green vs. `Pending Sync` in amber).

### 4. 📑 Professional Financial Statement & PDF Export
- **Flexible Statement Scopes**:
  - **Monthly Statements**: Full monthly audit statements (e.g., "September 2026").
  - **Quarterly Statements**: Fiscal quarters aligned with Australian/UK/US tax periods (e.g., "Q1 (Jun - Aug) FY 2026-27").
  - **Annual Statements**: Complete annual financial years (e.g., "FY 2026-27").
  - **Custom Range / Balance Sheet**: Any arbitrary date range selected via calendar dialog.
- **PDF Document Engine**:
  - Built with native Android Canvas vector graphics at standard A4 resolution (595 x 842 pt).
  - **Business Header**: Prominent business name, registration numbers, and contact details.
  - **Distinctive Period Banner**: Centered, high-contrast dark blue banner (`#1A365D`) displaying the active period in bold white typography.
  - **Executive Summary Box**: Aggregated totals for Gross Income, Expenses, Bills, and Net Balance (highlighted in emerald green or crimson red).
  - **Itemized Ledger Table**: Tabular layout with Date, Type, Description/Notes, Income (+), Outgoings (-), and Running Balance columns.
  - **Multi-Page Pagination**: Automatic page overflow handling with recurring table headers and "Page X of Y" footers.
  - **Device Storage & Sharing**: Saves directly into the public `Downloads` directory and triggers the Android share sheet.

### 5. 📅 Client Appointments & Booking Manager
- **Complete Booking Lifecycle**: Manage appointments with statuses: `Scheduled`, `Completed`, `Cancelled`, `No-Show`.
- **Contact Integration**: Pick client phone numbers directly from Android contacts or enter them manually.
- **Automated 24-Hour Reminders**:
  - Schedules precise alarms using Android's `AlarmManager`.
  - Dispatches high-priority notifications 24 hours prior to appointment time.
  - Interactive notification actions allow instant 1-tap dispatch of pre-filled confirmation SMS messages.
- **Reboot Resilience**: Listens to system boot events (`BOOT_COMPLETED`) to ensure no alarms are lost when the device is turned off or updated.

### 6. 📞 Intelligent Caller ID Overlay
- **Incoming Call Detection**: Intercepts telephone state changes when a client calls.
- **Instant Client Lookup**: Queries local Room database and device contacts to find matching appointment records.
- **Floating Heads-Up Display**:
  - Renders a floating window over the phone's incoming call screen.
  - Displays client name, appointment date, time, booked service, and special notes.
  - Allows staff to greet returning clients by name and answer booking inquiries without opening the app.

### 7. ☁️ Two-Way Google Sheets Cloud Synchronization
- **Zero-Cost Serverless Backend**: Connects directly to a Google Spreadsheet via a lightweight Google Apps Script (`Code.gs`).
- **Clean Workbook Architecture**:
  - **Monthly Tabs (`Sep26`, `Oct26`)**: Each month is filed into its own clean tab with pre-formatted currency headers and formulas.
  - **Product Catalog Tab (`Products`)**: Cloud-based menu where prices or services can be edited directly from any laptop or browser, syncing back to mobile.
  - **Summary Tab (`Sheet1`)**: High-level cross-month overview.
- **Resilient Offline Sync**: Transactions created while offline are queued in SQLite. When internet connectivity returns, the background worker pushes all pending batches automatically.

### 8. 🛠️ Business Setup & Menu Customization
- **Business Profile**: Configure trading name, tax number (ABN/VAT/EIN), street address, phone, email, and receipt disclaimers.
- **Theme Customization**: Switch between System Default, Modern Light, and Dark Mode palettes.
- **Menu Management**: Create, edit, reorder, or delete salon services and product items with assigned icons, category groupings, and default retail prices.
- **One-Tap Apps Script Copy**: Built-in viewer allowing the business owner to copy the backend script directly from their phone.

---

## 🗂️ File System & Architecture Map

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Root host activity, navigation router, and permission handlers
├── alarm/
│   └── AppointmentAlarmScheduler.kt     # Exact AlarmManager scheduler for appointment notifications
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt               # Room database definition with schema migrations
│   │   ├── TransactionEntity.kt         # Ledger table (income, expenses, bills, sync state)
│   │   ├── TransactionDao.kt            # Room DAO for transactions with reactive Flow queries
│   │   ├── AppointmentEntity.kt         # Booking entity (client, phone, date, time, status)
│   │   └── AppointmentDao.kt            # Room DAO for appointments and caller lookup
│   ├── model/
│   │   ├── ProductItem.kt               # Menu and catalog data models
│   │   └── AppointmentSettings.kt       # SMS reminder templates and reminder time offsets
│   ├── pref/
│   │   ├── AppPreferences.kt            # SharedPreferences / DataStore wrapper
│   │   └── BusinessProfile.kt           # Business identity and contact data class
│   ├── remote/
│   │   └── SheetsApiService.kt          # HTTP networking engine communicating with Google Apps Script
│   └── repository/
│       └── TransactionRepository.kt     # Single source of truth unifying Room and Cloud Sync
├── notification/
│   ├── NotificationHelper.kt            # Android NotificationChannel setup and builder
│   └── AppointmentNotificationManager.kt# Appointment reminder dispatch logic
├── receiver/
│   ├── AppointmentReminderReceiver.kt   # Receiver executed when reminder alarm triggers
│   ├── BootReceiver.kt                  # Receiver restoring alarms on device restart
│   └── CallReceiver.kt                  # Telephony listener detecting incoming calls
├── service/
│   └── CallOverlayService.kt            # Foreground window overlay service during phone calls
├── ui/
│   ├── appointments/
│   │   └── AppointmentsScreen.kt        # Booking calendar, client list, and appointment editor
│   ├── balancesheet/
│   │   ├── BalanceSheetScreen.kt        # Continuous day-by-day running ledger and actions
│   │   └── StatementSelectionScreen.kt  # Monthly, quarterly, and annual date range picker
│   ├── dashboard/
│   │   └── DashboardScreen.kt           # Primary home overview, quick action buttons, today's metrics
│   ├── entry/
│   │   └── EntryScreen.kt               # Point of Sale (POS), item selector, expense/bill logger
│   ├── setup/
│   │   ├── SetupScreen.kt               # Business profile, Google Sheets setup, and app settings
│   │   └── MenuManagementScreen.kt      # Catalog manager for products, services, and prices
│   └── theme/
│       ├── Color.kt                     # M3 dynamic and brand color tokens
│       ├── Theme.kt                     # M3 theme wrapper (Light / Dark mode)
│       └── Type.kt                      # Typography scales (Display, Headline, Body, Label)
├── util/
│   ├── ContactHelper.kt                 # System contacts query utility
│   ├── GoogleAppsScriptSnippet.kt       # Embedded Apps Script source for easy user copying
│   ├── PdfExportHelper.kt               # Canvas-based vector A4 PDF generation engine
│   └── SmsHelper.kt                     # SMS intent and direct SMS dispatch utility
└── worker/
    └── SyncWorker.kt                    # WorkManager periodic and triggered background sync task
```

---

## 🔒 Permissions & Security Model

The app adheres to least-privilege principles, requesting permissions contextually:

| Permission | Scope | Justification |
|---|---|---|
| `INTERNET` & `ACCESS_NETWORK_STATE` | Normal | Synchronizing ledger entries and product menus with Google Sheets. |
| `POST_NOTIFICATIONS` | Runtime (API 33+) | Alerting staff to upcoming client appointments and sync statuses. |
| `SCHEDULE_EXACT_ALARM` & `USE_EXACT_ALARM` | System | Guaranteeing appointment reminders trigger precisely 24 hours in advance. |
| `RECEIVE_BOOT_COMPLETED` | Normal | Rescheduling appointment alarms after device reboots. |
| `READ_PHONE_STATE` & `READ_CALL_LOG` | Dangerous | Identifying incoming caller phone numbers to query appointment records. |
| `READ_CONTACTS` | Dangerous | Matching caller phone numbers to saved address book names. |
| `SYSTEM_ALERT_WINDOW` | Special Access | Displaying the floating appointment reminder card over the phone app. |
| `FOREGROUND_SERVICE` (`specialUse`) | Service | Maintaining the caller ID overlay window during active phone calls. |
| `SEND_SMS` | Runtime | Direct 1-tap dispatch of appointment reminders from notifications. |

---

## 🚀 Building & Exporting

### Root Directory APK Build Rule
As mandated by project deployment rules, every successful feature update increments the version by `+0.1` and outputs the compiled APK directly into the root folder:
- Current active release: **`MyBusiness-v5.3-debug.apk`** (23 MB)

### Local Build via Android Studio
1. Clone or download the project repository.
2. Open the directory in **Android Studio Meerkat or newer**.
3. Allow Gradle to resolve dependencies (`BOM 2025.02.00`, `Kotlin 2.1.20-RC3`, `Room 2.7.0-alpha13`).
4. Execute via terminal:
   ```bash
   gradle assembleDebug
   ```
5. The compiled artifact will be generated at:
   ```
   app/build/outputs/apk/debug/MyBusiness-v5.3-debug.apk
   ```

---

## 📄 License & Attribution
*Designed and built with modern Android Jetpack Compose, Room SQLite, and Google Workspace Integration.*
