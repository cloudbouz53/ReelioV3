package com.btkboz.reeliov3.ui.settings

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.btkboz.reeliov3.ui.theme.ReelioThemeMode
import com.btkboz.reeliov3.ui.theme.ReelioV3Theme
import kotlinx.coroutines.delay

private enum class CorrectedScreen {
    Settings, EditProfile, Theme, AppLanguage, ProcessingDefaults,
    Connections, Credentials, DataStorage, Privacy, Help, About
}

private enum class CorrectedLanguage { English, Arabic }

private val r9 = RoundedCornerShape(9.dp)
private val r10 = RoundedCornerShape(10.dp)
private val r12 = RoundedCornerShape(12.dp)
private val r16 = RoundedCornerShape(16.dp)

@Composable
fun ReelioSettingsCorrectedApp() {
    var themeMode by remember { mutableStateOf(ReelioThemeMode.Light) }
    var language by remember { mutableStateOf(CorrectedLanguage.English) }

    ReelioV3Theme(mode = themeMode) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides
                if (language == CorrectedLanguage.Arabic) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            CorrectedNavigator(
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
                language = language,
                onLanguageChange = { language = it },
            )
        }
    }
}

@Composable
private fun CorrectedNavigator(
    themeMode: ReelioThemeMode,
    onThemeModeChange: (ReelioThemeMode) -> Unit,
    language: CorrectedLanguage,
    onLanguageChange: (CorrectedLanguage) -> Unit,
) {
    val stack = remember { mutableStateListOf(CorrectedScreen.Settings) }
    val screen = stack.last()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    var toast by remember { mutableStateOf<String?>(null) }

    fun open(target: CorrectedScreen) = stack.add(target)
    fun back() { if (stack.size > 1) stack.removeAt(stack.lastIndex) }

    BackHandler(enabled = stack.size > 1) { back() }
    LaunchedEffect(toast) {
        if (toast != null) {
            delay(1800)
            toast = null
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CorrectedHeader(
                title = titleFor(screen, language),
                canGoBack = stack.size > 1,
                rtl = rtl,
                onBack = ::back,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                CorrectedScreen.Settings -> SettingsHomeCorrected(language, themeMode, ::open)
                CorrectedScreen.EditProfile -> EditProfileCorrected(language) { toast = tr(language, "Profile changes saved", "تم حفظ تغييرات الملف الشخصي") }
                CorrectedScreen.Theme -> ThemeCorrected(language, themeMode, onThemeModeChange)
                CorrectedScreen.AppLanguage -> LanguageCorrected(language, onLanguageChange)
                CorrectedScreen.ProcessingDefaults -> ProcessingCorrected(language) { toast = tr(language, "Processing defaults saved", "تم حفظ إعدادات المعالجة الافتراضية") }
                CorrectedScreen.Connections -> ConnectionsCorrected(language)
                CorrectedScreen.Credentials -> CredentialsCorrected(language) { toast = tr(language, "Credentials saved securely", "تم حفظ بيانات الاعتماد بأمان") }
                CorrectedScreen.DataStorage -> DataStorageCorrected(language) { toast = tr(language, "Temporary cache cleared", "تم مسح الذاكرة المؤقتة") }
                CorrectedScreen.Privacy -> PrivacyCorrected(language)
                CorrectedScreen.Help -> HelpCorrected(language) { open(CorrectedScreen.Connections) }
                CorrectedScreen.About -> AboutCorrected(language) { open(CorrectedScreen.Privacy) }
            }

            toast?.let {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(21.dp).fillMaxWidth(),
                    shape = r12,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp,
                ) {
                    Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Check, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(it, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CorrectedHeader(title: String, canGoBack: Boolean, rtl: Boolean, onBack: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().height(66.dp).padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                if (canGoBack) {
                    Box(
                        Modifier.size(44.dp).clickable(onClick = onBack),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            null,
                            Modifier.size(20.dp).graphicsLayer { scaleX = if (rtl) -1f else 1f },
                        )
                    }
                }
            }
            Text(
                title,
                modifier = Modifier.weight(1f),
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Spacer(Modifier.width(44.dp))
        }
    }
}

@Composable
private fun SettingsHomeCorrected(lang: CorrectedLanguage, theme: ReelioThemeMode, open: (CorrectedScreen) -> Unit) {
    ScreenList {
        item {
            Surface(
                Modifier.fillMaxWidth().clickable { open(CorrectedScreen.EditProfile) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
            ) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile(Icons.Outlined.AccountCircle, 55)
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tr(lang, "Your Reelio profile", "ملفك الشخصي في Reelio"), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(tr(lang, "Edit your name and profile details", "عدّل اسمك وتفاصيل ملفك الشخصي"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Chevron(lang)
                }
            }
        }
        section(tr(lang, "General", "عام")) {
            SettingRow(Icons.Outlined.LightMode, tr(lang, "Theme", "المظهر"), themeLabel(theme, lang)) { open(CorrectedScreen.Theme) }
            SettingRow(Icons.Outlined.Language, tr(lang, "App Language", "لغة التطبيق"), if (lang == CorrectedLanguage.Arabic) "العربية" else "English") { open(CorrectedScreen.AppLanguage) }
        }
        section(tr(lang, "Content processing", "معالجة المحتوى")) {
            SettingRow(Icons.Outlined.Settings, tr(lang, "Processing Defaults", "إعدادات المعالجة الافتراضية"), null, tr(lang, "Explanation, summary, classification & indexing", "الشرح، التلخيص، التصنيف والفهرسة")) { open(CorrectedScreen.ProcessingDefaults) }
        }
        section(tr(lang, "System", "النظام")) {
            SettingRow(Icons.Outlined.Link, tr(lang, "Connections", "الاتصالات")) { open(CorrectedScreen.Connections) }
            SettingRow(Icons.Outlined.Key, tr(lang, "Credentials & Security", "بيانات الاعتماد والأمان")) { open(CorrectedScreen.Credentials) }
            SettingRow(Icons.Outlined.Storage, tr(lang, "Data & Storage", "البيانات والتخزين")) { open(CorrectedScreen.DataStorage) }
        }
        section(tr(lang, "Support & information", "الدعم والمعلومات")) {
            SettingRow(Icons.Outlined.HelpOutline, tr(lang, "Help & Support", "المساعدة والدعم")) { open(CorrectedScreen.Help) }
            SettingRow(Icons.Outlined.Security, tr(lang, "Privacy Policy", "سياسة الخصوصية")) { open(CorrectedScreen.Privacy) }
            SettingRow(Icons.Outlined.Info, tr(lang, "About Reelio", "حول Reelio")) { open(CorrectedScreen.About) }
        }
    }
}

@Composable
private fun EditProfileCorrected(lang: CorrectedLanguage, onSaved: () -> Unit) {
    var name by remember { mutableStateOf("Reelio User") }
    var email by remember { mutableStateOf("user@example.com") }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    StickyScreen(
        footer = {
            PrimaryAction(tr(lang, "Save changes", "حفظ التغييرات")) {
                nameError = name.isBlank()
                emailError = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if (!nameError && !emailError) onSaved()
            }
        },
    ) {
        item {
            Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(Modifier.size(89.dp), CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.AccountCircle, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                TextButton(onClick = {}) { Text(tr(lang, "Change photo", "تغيير الصورة"), fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                Text(tr(lang, "JPG or PNG. Use a clear square photo.", "JPG أو PNG. استخدم صورة مربعة وواضحة."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { Text(tr(lang, "Profile information", "معلومات الملف الشخصي"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
        item {
            FieldLabel(tr(lang, "Full name", "الاسم الكامل"))
            OutlinedTextField(name, { name = it; nameError = false }, Modifier.fillMaxWidth().height(55.dp), singleLine = true, isError = nameError, shape = r10)
            Text(tr(lang, "This name appears on your Reelio profile.", "يظهر هذا الاسم في ملفك الشخصي على Reelio."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            FieldLabel(tr(lang, "Email address", "عنوان البريد الإلكتروني"))
            OutlinedTextField(email, { email = it; emailError = false }, Modifier.fillMaxWidth().height(55.dp), singleLine = true, isError = emailError, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), shape = r10)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Check, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(5.dp))
                Text(tr(lang, "Verified email address", "عنوان بريد إلكتروني موثّق"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ThemeCorrected(lang: CorrectedLanguage, selected: ReelioThemeMode, onSelect: (ReelioThemeMode) -> Unit) {
    ScreenList {
        item { Intro(tr(lang, "Choose how Reelio looks", "اختر مظهر Reelio"), tr(lang, "The layout stays identical. Only semantic theme colors change.", "يبقى التخطيط كما هو، وتتغير ألوان المظهر الدلالية فقط.")) }
        item { Choice(Icons.Outlined.Computer, tr(lang, "System", "النظام"), tr(lang, "Follow your device appearance", "اتبع مظهر جهازك"), selected == ReelioThemeMode.System) { onSelect(ReelioThemeMode.System) } }
        item { Choice(Icons.Outlined.LightMode, tr(lang, "Light", "فاتح"), tr(lang, "Use the light Reelio palette", "استخدم لوحة Reelio الفاتحة"), selected == ReelioThemeMode.Light) { onSelect(ReelioThemeMode.Light) } }
        item { Choice(Icons.Outlined.DarkMode, tr(lang, "Dark", "داكن"), tr(lang, "Use the dark Reelio palette", "استخدم لوحة Reelio الداكنة"), selected == ReelioThemeMode.Dark) { onSelect(ReelioThemeMode.Dark) } }
    }
}

@Composable
private fun LanguageCorrected(lang: CorrectedLanguage, onSelect: (CorrectedLanguage) -> Unit) {
    ScreenList {
        item { Intro(tr(lang, "Choose the interface language", "اختر لغة الواجهة"), tr(lang, "This changes Reelio navigation, labels, alignment, and directional icons.", "يغيّر هذا لغة التنقل والتسميات والمحاذاة والأيقونات الاتجاهية في Reelio.")) }
        item { LanguageChoice("EN", "English", "Left-to-right interface", lang == CorrectedLanguage.English) { onSelect(CorrectedLanguage.English) } }
        item { LanguageChoice("AR", "العربية", "واجهة من اليمين إلى اليسار", lang == CorrectedLanguage.Arabic) { onSelect(CorrectedLanguage.Arabic) } }
        item { InfoBlock(tr(lang, "Changing the app language does not change your processing-language defaults.", "تغيير لغة التطبيق لا يغيّر إعدادات لغات المعالجة الافتراضية.")) }
    }
}

@Composable
private fun ProcessingCorrected(lang: CorrectedLanguage, onSaved: () -> Unit) {
    var explanation by remember { mutableStateOf("Arabic") }
    var summary by remember { mutableStateOf("Arabic") }
    var classification by remember { mutableStateOf("Arabic") }
    var indexing by remember { mutableStateOf("Arabic") }
    StickyScreen(footer = { PrimaryAction(tr(lang, "Save defaults", "حفظ الإعدادات الافتراضية"), onSaved) }) {
        item { Intro(tr(lang, "Default languages for new videos", "اللغات الافتراضية للفيديوهات الجديدة"), tr(lang, "Use these automatically when adding content. You can still override them for an individual video.", "استخدم هذه اللغات تلقائيًا عند إضافة المحتوى، ويمكنك تغييرها لكل فيديو بشكل مستقل.")) }
        item {
            Surface(shape = r16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
                Column {
                    LanguageSetting(tr(lang, "Explanation language", "لغة الشرح"), tr(lang, "Language used to explain the reel's idea", "اللغة المستخدمة لشرح فكرة الريل"), explanation, lang) { explanation = it }
                    HorizontalDivider()
                    LanguageSetting(tr(lang, "Summary language", "لغة التلخيص"), tr(lang, "Language used for the concise summary", "اللغة المستخدمة للتلخيص المختصر"), summary, lang) { summary = it }
                    HorizontalDivider()
                    LanguageSetting(tr(lang, "Classification language", "لغة التصنيف"), tr(lang, "Category and subcategory labels", "تسميات التصنيف والتصنيف الفرعي"), classification, lang) { classification = it }
                    HorizontalDivider()
                    LanguageSetting(tr(lang, "Indexing language", "لغة الفهرسة"), tr(lang, "Keywords and search terms", "الكلمات المفتاحية ومصطلحات البحث"), indexing, lang) { indexing = it }
                }
            }
        }
        item { InfoBlock(tr(lang, "A video can be in English while its explanation, summary, classification, and indexing are created in Arabic.", "يمكن أن يكون الفيديو بالإنجليزية بينما يتم إنشاء الشرح والتلخيص والتصنيف والفهرسة بالعربية.")) }
    }
}

@Composable
private fun ConnectionsCorrected(lang: CorrectedLanguage) {
    ScreenList {
        item { Intro(tr(lang, "Service readiness", "جاهزية الخدمات"), tr(lang, "Check the four components Reelio needs. Status always includes text and an icon.", "تحقق من المكوّنات الأربعة التي يحتاجها Reelio. تتضمن الحالة دائمًا نصًا وأيقونة.")) }
        section(tr(lang, "Database & storage", "قاعدة البيانات والتخزين")) {
            Service(Icons.Outlined.Storage, "Neon PostgreSQL", tr(lang, "Connected", "متصل"), lang)
            Service(Icons.Outlined.Cloud, "Cloudflare R2", tr(lang, "Connected", "متصل"), lang)
        }
        section(tr(lang, "Media tools", "أدوات الوسائط")) {
            Service(Icons.Outlined.Download, "yt-dlp", tr(lang, "Available", "متاح"), lang)
            Service(Icons.Outlined.Settings, "FFmpeg", tr(lang, "Available", "متاح"), lang)
        }
        item { InfoBlock(tr(lang, "Technical output is kept out of the interface. Failures are translated into clear user-facing messages.", "لا يظهر الإخراج التقني في الواجهة. يتم تحويل حالات الفشل إلى رسائل واضحة للمستخدم.")) }
    }
}

@Composable
private fun CredentialsCorrected(lang: CorrectedLanguage, onSaved: () -> Unit) {
    StickyScreen(footer = { PrimaryAction(tr(lang, "Save securely", "حفظ بأمان"), onSaved) }) {
        item { Intro(tr(lang, "Secure service credentials", "بيانات اعتماد الخدمات الآمنة"), tr(lang, "Saved secrets remain masked. Reelio should store them using Android Keystore-backed encrypted storage.", "تبقى البيانات السرية المحفوظة مخفية، ويجب على Reelio تخزينها بشكل مشفّر ومدعوم بـ Android Keystore.")) }
        item { Text(tr(lang, "AI services", "خدمات الذكاء الاصطناعي"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
        item { ExternalSecret(tr(lang, "Gemini API key", "مفتاح Gemini API"), lang) }
        item { ExternalSecret(tr(lang, "ElevenLabs API key", "مفتاح ElevenLabs API"), lang) }
        item { Text(tr(lang, "Database & storage", "قاعدة البيانات والتخزين"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
        item { PlainCredential(tr(lang, "Database URL", "رابط قاعدة البيانات"), KeyboardType.Password) }
        item { PlainCredential(tr(lang, "Cloudflare account ID", "معرّف حساب Cloudflare"), KeyboardType.Password) }
        item { PlainCredential(tr(lang, "R2 access key ID", "معرّف مفتاح الوصول R2"), KeyboardType.Password) }
        item { PlainCredential(tr(lang, "R2 secret access key", "مفتاح الوصول السري R2"), KeyboardType.Password) }
        item { PlainCredential(tr(lang, "R2 bucket name", "اسم حاوية R2"), KeyboardType.Text, tr(lang, "Your bucket name", "اسم الحاوية"), false) }
        item { PlainCredential(tr(lang, "R2 endpoint", "نقطة نهاية R2"), KeyboardType.Uri, "https://…", false) }
        item { InfoBlock(tr(lang, "Never show full saved credentials in the UI or include them in logs and error reports.", "لا تعرض بيانات الاعتماد المحفوظة كاملة في الواجهة ولا تدرجها في السجلات أو تقارير الأخطاء.")) }
    }
}

@Composable
private fun DataStorageCorrected(lang: CorrectedLanguage, onCleared: () -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    ScreenList {
        item { Intro(tr(lang, "Where Reelio keeps your data", "أين يحتفظ Reelio ببياناتك"), tr(lang, "Structured knowledge and media are stored separately. Temporary processing files are cleaned up after use.", "يتم تخزين المعرفة المنظمة والوسائط بشكل منفصل، وتُنظّف ملفات المعالجة المؤقتة بعد الاستخدام.")) }
        section(tr(lang, "Permanent storage", "التخزين الدائم")) {
            StorageInfo(Icons.Outlined.Storage, tr(lang, "Knowledge data", "بيانات المعرفة"), tr(lang, "Titles, explanations, summaries, key points, categories, tags and indexing data.", "العناوين والشروحات والملخصات والنقاط الرئيسية والتصنيفات والوسوم وبيانات الفهرسة."), "Neon PostgreSQL")
            StorageInfo(Icons.Outlined.Cloud, tr(lang, "Media", "الوسائط"), tr(lang, "Saved video and thumbnail files associated with your library items.", "ملفات الفيديو والصور المصغرة المحفوظة والمرتبطة بعناصر مكتبتك."), "Cloudflare R2")
        }
        section(tr(lang, "On this device", "على هذا الجهاز")) {
            StorageInfo(Icons.Outlined.Storage, tr(lang, "Temporary processing cache", "ذاكرة المعالجة المؤقتة"), tr(lang, "Automatically removed after processing completes or fails.", "تتم إزالتها تلقائيًا بعد اكتمال المعالجة أو فشلها."), null)
            ActionRow(Icons.Outlined.DeleteOutline, tr(lang, "Clear temporary cache", "مسح الذاكرة المؤقتة"), tr(lang, "Does not remove saved Library items.", "لا يزيل العناصر المحفوظة في المكتبة."), tr(lang, "Clear", "مسح")) { dialog = true }
        }
    }
    if (dialog) {
        Dialog(onDismissRequest = { dialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.fillMaxSize().padding(21.dp), contentAlignment = Alignment.BottomCenter) {
                Surface(Modifier.fillMaxWidth(), shape = r16, color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), shadowElevation = 3.dp) {
                    Column(Modifier.padding(21.dp)) {
                        IconTile(Icons.Outlined.DeleteOutline, 40)
                        Spacer(Modifier.height(13.dp))
                        Text(tr(lang, "Clear temporary cache?", "هل تريد مسح الذاكرة المؤقتة؟"), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(5.dp))
                        Text(tr(lang, "Temporary processing files will be removed. Your saved Library items will be kept.", "ستتم إزالة ملفات المعالجة المؤقتة، وستبقى عناصر المكتبة المحفوظة."), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(21.dp))
                        Button(onClick = { dialog = false; onCleared() }, Modifier.fillMaxWidth().height(48.dp), shape = r10) { Text(tr(lang, "Clear temporary cache", "مسح الذاكرة المؤقتة")) }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { dialog = false }, Modifier.fillMaxWidth().height(44.dp), shape = r10) { Text(tr(lang, "Keep cache", "الاحتفاظ بالذاكرة المؤقتة")) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyCorrected(lang: CorrectedLanguage) {
    ScreenList {
        item {
            Column(Modifier.padding(top = 13.dp)) {
                Text(tr(lang, "Privacy in Reelio", "الخصوصية في Reelio"), fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(5.dp))
                Text(tr(lang, "A concise product-facing summary based on the Reelio implementation requirements.", "ملخص موجز موجّه للمنتج استنادًا إلى متطلبات تنفيذ Reelio."), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        privacySection(lang, "What Reelio processes", "ما الذي يعالجه Reelio", "Reelio processes video links and content you choose to add so it can create organized knowledge such as a clear title, explanation, summary, key points, categories, tags and indexing terms.", "يعالج Reelio روابط الفيديو والمحتوى الذي تختار إضافته لإنشاء معرفة منظمة مثل العنوان والشرح والملخص والنقاط الرئيسية والتصنيفات والوسوم ومصطلحات الفهرسة.")
        privacySection(lang, "Internal transcript", "النص الداخلي", "Speech-to-text output is an internal processing layer used to understand the content. It is not presented as a user-facing transcript feature.", "ناتج تحويل الكلام إلى نص هو طبقة معالجة داخلية لفهم المحتوى، ولا يُعرض كميزة نص للمستخدم.")
        privacySection(lang, "Credentials", "بيانات الاعتماد", "Service credentials are sensitive. Saved values must remain encrypted, masked in the interface, and redacted from logs and error reports.", "بيانات اعتماد الخدمات حساسة ويجب أن تبقى مشفّرة ومخفية في الواجهة ومحجوبة عن السجلات وتقارير الأخطاء.")
        privacySection(lang, "Storage", "التخزين", "Structured library data is stored in Neon PostgreSQL and permanent media is stored in Cloudflare R2. Temporary local files should be deleted after they are no longer needed.", "تُخزّن بيانات المكتبة المنظمة في Neon PostgreSQL والوسائط الدائمة في Cloudflare R2، ويجب حذف الملفات المحلية المؤقتة بعد انتهاء الحاجة إليها.")
        privacySection(lang, "User-facing errors", "الأخطاء الموجّهة للمستخدم", "Reelio should present understandable error messages instead of stack traces, shell output, exit codes or raw infrastructure details.", "يجب أن يعرض Reelio رسائل خطأ مفهومة بدل تتبعات المكدس أو مخرجات الطرفية أو رموز الخروج أو تفاصيل البنية التحتية الخام.")
        item {
            Surface(shape = r12, color = MaterialTheme.colorScheme.secondary) {
                Text(tr(lang, "This mockup is product UI copy, not a substitute for a finalized legal privacy policy.", "هذا النموذج نص لواجهة المنتج وليس بديلًا عن سياسة خصوصية قانونية نهائية."), Modifier.padding(13.dp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}

@Composable
private fun HelpCorrected(lang: CorrectedLanguage, openConnections: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val all = listOf(
        tr(lang, "How do I add a video?", "كيف أضيف فيديو؟") to tr(lang, "Use Share to Reelio from a supported app, or copy the video URL and paste it on Home.", "استخدم المشاركة إلى Reelio من تطبيق مدعوم، أو انسخ رابط الفيديو والصقه في الصفحة الرئيسية."),
        tr(lang, "Can each processing output use a different language?", "هل يمكن أن يستخدم كل ناتج معالجة لغة مختلفة؟") to tr(lang, "Yes. Explanation, summary, classification and indexing can each have their own language default.", "نعم. يمكن أن يكون للشرح والتلخيص والتصنيف والفهرسة إعداد لغة افتراضي مستقل."),
        tr(lang, "Why can't Reelio process a video?", "لماذا لا يستطيع Reelio معالجة فيديو؟") to tr(lang, "Open Connections to check database, storage and local media-tool readiness.", "افتح الاتصالات للتحقق من جاهزية قاعدة البيانات والتخزين وأدوات الوسائط المحلية."),
        tr(lang, "Where is the transcript?", "أين النص؟") to tr(lang, "Reelio does not expose the transcript as a user feature. It is used internally only to understand the video.", "لا يعرض Reelio النص كميزة للمستخدم، بل يُستخدم داخليًا فقط لفهم الفيديو."),
    )
    val filtered = all.filter { query.isBlank() || it.first.contains(query, true) || it.second.contains(query, true) }
    ScreenList {
        item { Intro(tr(lang, "How can we help?", "كيف يمكننا مساعدتك؟"), tr(lang, "Start with common questions or run a connection check if Reelio cannot process content.", "ابدأ بالأسئلة الشائعة أو افحص الاتصالات إذا تعذر على Reelio معالجة المحتوى.")) }
        item {
            OutlinedTextField(
                query,
                { query = it },
                Modifier.fillMaxWidth().height(52.dp),
                placeholder = { Text(tr(lang, "Search help", "بحث في المساعدة")) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, Modifier.size(18.dp)) },
                singleLine = true,
                shape = r10,
            )
        }
        items(filtered) { Faq(it.first, it.second) }
        section(tr(lang, "Troubleshooting", "استكشاف الأخطاء وإصلاحها")) {
            SettingRow(Icons.Outlined.Settings, tr(lang, "Check Connections", "فحص الاتصالات"), onClick = openConnections)
        }
    }
}

@Composable
private fun AboutCorrected(lang: CorrectedLanguage, openPrivacy: () -> Unit) {
    ScreenList {
        item {
            Column(Modifier.fillMaxWidth().padding(top = 21.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(Modifier.size(72.dp), RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primary, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.BookmarkAdded, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onPrimary) }
                }
                Spacer(Modifier.height(13.dp))
                Text("Reelio", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text(tr(lang, "Save → Understand → Organize → Find", "احفظ ← افهم ← نظّم ← اعثر"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { Text(tr(lang, "Reelio turns useful short-form videos into an organized personal knowledge library. Instead of saving a video and forgetting why it mattered, Reelio helps preserve the knowledge inside it.", "يحوّل Reelio مقاطع الفيديو القصيرة المفيدة إلى مكتبة معرفة شخصية منظمة، ليساعدك على الاحتفاظ بالمعرفة الموجودة داخلها بدل نسيان سبب حفظها."), fontSize = 14.sp, lineHeight = 24.sp) }
        item {
            Surface(shape = r16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                    KeyValue(tr(lang, "Version", "الإصدار"), "1.0.0")
                    KeyValue(tr(lang, "Interface languages", "لغات الواجهة"), "English, العربية")
                    KeyValue(tr(lang, "Theme", "المظهر"), tr(lang, "System / Light / Dark", "النظام / فاتح / داكن"))
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionTitle(tr(lang, "Supported discovery flow", "مسار الاكتشاف المدعوم"))
                Surface(shape = r16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
                    Text(tr(lang, "Share to Reelio or Paste Link → understand and organize → save to Library → search and find later.", "شارك إلى Reelio أو الصق الرابط ← افهم ونظّم ← احفظ في المكتبة ← ابحث واعثر لاحقًا."), Modifier.padding(13.dp), fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { SettingRow(Icons.Outlined.Security, tr(lang, "Privacy Policy", "سياسة الخصوصية"), onClick = openPrivacy) }
    }
}

@Composable
private fun ScreenList(content: LazyListScope.() -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 13.dp, bottom = 34.dp),
        verticalArrangement = Arrangement.spacedBy(21.dp),
        content = content,
    )
}

@Composable
private fun StickyScreen(footer: @Composable () -> Unit, content: LazyListScope.() -> Unit) {
    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 21.dp, end = 21.dp, top = 13.dp, bottom = 21.dp),
            verticalArrangement = Arrangement.spacedBy(21.dp),
            content = content,
        )
        HorizontalDivider()
        Box(Modifier.fillMaxWidth().padding(horizontal = 21.dp, vertical = 13.dp)) { footer() }
    }
}

private fun LazyListScope.section(title: String, rows: @Composable Column.() -> Unit) {
    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionTitle(title)
            Surface(shape = r16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
                Column(content = rows)
            }
        }
    }
}

private fun LazyListScope.privacySection(lang: CorrectedLanguage, enTitle: String, arTitle: String, enBody: String, arBody: String) {
    item {
        Column {
            Text(tr(lang, enTitle, arTitle), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(5.dp))
            Text(tr(lang, enBody, arBody), fontSize = 14.sp, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun SectionTitle(text: String) = Text(text.uppercase(), Modifier.padding(horizontal = 5.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
private fun SettingRow(icon: ImageVector, title: String, value: String? = null, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 55.dp).clickable(onClick = onClick).padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(22.dp))
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        value?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(8.dp)) }
        Chevron(if (LocalLayoutDirection.current == LayoutDirection.Rtl) CorrectedLanguage.Arabic else CorrectedLanguage.English)
    }
}

@Composable private fun Chevron(lang: CorrectedLanguage) = Icon(Icons.Outlined.ChevronRight, null, Modifier.size(20.dp).graphicsLayer { scaleX = if (lang == CorrectedLanguage.Arabic) -1f else 1f }, tint = MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
private fun Choice(icon: ImageVector, title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().heightIn(min = 68.dp).clickable(onClick = onClick),
        shape = r16,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline),
        color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(icon, 40)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            RadioDot(selected)
        }
    }
}

@Composable
private fun LanguageChoice(code: String, title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().heightIn(min = 68.dp).clickable(onClick = onClick), shape = r16, border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.outline), color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(40.dp), r10, color = MaterialTheme.colorScheme.secondary) { Box(contentAlignment = Alignment.Center) { Text(code, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) } }
            Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }; RadioDot(selected)
        }
    }
}

@Composable
private fun RadioDot(selected: Boolean) {
    Surface(Modifier.size(20.dp), CircleShape, color = androidx.compose.ui.graphics.Color.Transparent, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
        Box(contentAlignment = Alignment.Center) { if (selected) Surface(Modifier.size(10.dp), CircleShape, color = MaterialTheme.colorScheme.primary) {} }
    }
}

@Composable
private fun LanguageSetting(title: String, subtitle: String, value: String, lang: CorrectedLanguage, onValue: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 55.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Spacer(Modifier.width(13.dp)); LanguageMenu(value, lang, onValue)
    }
}

@Composable
private fun LanguageMenu(value: String, lang: CorrectedLanguage, onValue: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(Modifier.width(132.dp).height(42.dp).clickable { expanded = true }, r9, color = MaterialTheme.colorScheme.background, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
            Row(Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) { Text(languageValue(value, lang), Modifier.weight(1f), fontSize = 13.sp); Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        DropdownMenu(expanded, { expanded = false }) {
            listOf("Auto", "Arabic", "English").forEach { option -> DropdownMenuItem(text = { Text(languageValue(option, lang), fontSize = 13.sp) }, trailingIcon = { if (option == value) Icon(Icons.Outlined.Check, null, Modifier.size(16.dp)) }, onClick = { onValue(option); expanded = false }) }
        }
    }
}

@Composable
private fun Service(icon: ImageVector, name: String, status: String, lang: CorrectedLanguage) {
    var checking by remember { mutableStateOf(false) }
    LaunchedEffect(checking) { if (checking) { delay(650); checking = false } }
    Row(Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        IconTile(icon, 40); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(name, fontSize = 15.sp, fontWeight = FontWeight.Medium); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Check, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.width(5.dp)); Text(status, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        TextButton(onClick = { checking = true }, enabled = !checking, modifier = Modifier.height(40.dp)) {
            if (checking) { CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 1.5.dp); Spacer(Modifier.width(6.dp)); Text(tr(lang, "Checking…", "جارٍ التحقق…"), fontSize = 13.sp) } else Text(tr(lang, "Check", "تحقق"), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ExternalSecret(label: String, lang: CorrectedLanguage) {
    var value by remember { mutableStateOf("") }; var visible by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium); Text(tr(lang, "Configured", "تم الإعداد"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value, { value = it }, Modifier.weight(1f).height(52.dp), singleLine = true, visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(), shape = r10)
            Surface(Modifier.size(52.dp).clickable { visible = !visible }, r10, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.background) { Box(contentAlignment = Alignment.Center) { Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, Modifier.size(20.dp)) } }
        }
    }
}

@Composable
private fun PlainCredential(label: String, type: KeyboardType, placeholder: String = "••••••••••••••••", password: Boolean = true) {
    var value by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FieldLabel(label)
        OutlinedTextField(value, { value = it }, Modifier.fillMaxWidth().height(52.dp), placeholder = { Text(placeholder) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = type), visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None, shape = r10)
    }
}

@Composable
private fun StorageInfo(icon: ImageVector, title: String, subtitle: String, provider: String?) {
    Row(Modifier.fillMaxWidth().heightIn(min = 55.dp).padding(13.dp), verticalAlignment = Alignment.Top) { Icon(icon, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Column { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); provider?.let { Text(it, Modifier.padding(top = 5.dp), fontSize = 12.sp, fontWeight = FontWeight.Medium) } } }
}

@Composable
private fun ActionRow(icon: ImageVector, title: String, subtitle: String, action: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 55.dp).clickable(onClick = onClick).padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Text(action, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
}

@Composable
private fun Faq(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(Modifier.fillMaxWidth(), r16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
        Column {
            Row(Modifier.fillMaxWidth().heightIn(min = 55.dp).clickable { expanded = !expanded }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Text(question, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium); Icon(Icons.Outlined.ExpandMore, null, Modifier.size(20.dp).graphicsLayer { rotationZ = if (expanded) 180f else 0f }, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (expanded) Text(answer, Modifier.padding(start = 13.dp, end = 13.dp, bottom = 13.dp), fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable private fun Intro(title: String, subtitle: String) { Column(Modifier.padding(top = 0.dp)) { Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(5.dp)); Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
@Composable private fun InfoBlock(text: String) { Surface(shape = r12, color = MaterialTheme.colorScheme.secondary) { Row(Modifier.padding(13.dp)) { Icon(Icons.Outlined.Info, null, Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text(text, fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSecondary) } } }
@Composable private fun IconTile(icon: ImageVector, size: Int) { Surface(Modifier.size(size.dp), if (size == 55) CircleShape else r10, color = MaterialTheme.colorScheme.secondary, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) { Box(contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(if (size == 55) 24.dp else 20.dp)) } } }
@Composable private fun FieldLabel(text: String) = Text(text, fontSize = 13.sp, fontWeight = FontWeight.Medium)
@Composable private fun PrimaryAction(text: String, onClick: () -> Unit) = Button(onClick, Modifier.fillMaxWidth().height(55.dp), shape = r10, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) { Text(text, fontWeight = FontWeight.SemiBold) }
@Composable private fun KeyValue(key: String, value: String) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(key, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium) } }

private fun titleFor(screen: CorrectedScreen, lang: CorrectedLanguage): String = when (screen) {
    CorrectedScreen.Settings -> tr(lang, "Settings", "الإعدادات")
    CorrectedScreen.EditProfile -> tr(lang, "Edit Profile", "تعديل الملف الشخصي")
    CorrectedScreen.Theme -> tr(lang, "Theme", "المظهر")
    CorrectedScreen.AppLanguage -> tr(lang, "App Language", "لغة التطبيق")
    CorrectedScreen.ProcessingDefaults -> tr(lang, "Processing Defaults", "إعدادات المعالجة الافتراضية")
    CorrectedScreen.Connections -> tr(lang, "Connections", "الاتصالات")
    CorrectedScreen.Credentials -> tr(lang, "Credentials & Security", "بيانات الاعتماد والأمان")
    CorrectedScreen.DataStorage -> tr(lang, "Data & Storage", "البيانات والتخزين")
    CorrectedScreen.Privacy -> tr(lang, "Privacy Policy", "سياسة الخصوصية")
    CorrectedScreen.Help -> tr(lang, "Help & Support", "المساعدة والدعم")
    CorrectedScreen.About -> tr(lang, "About Reelio", "حول Reelio")
}

private fun themeLabel(mode: ReelioThemeMode, lang: CorrectedLanguage): String = when (mode) {
    ReelioThemeMode.System -> tr(lang, "System", "النظام")
    ReelioThemeMode.Light -> tr(lang, "Light", "فاتح")
    ReelioThemeMode.Dark -> tr(lang, "Dark", "داكن")
}

private fun languageValue(value: String, lang: CorrectedLanguage): String = when (value) {
    "Auto" -> tr(lang, "Auto", "تلقائي")
    "Arabic" -> tr(lang, "Arabic", "العربية")
    "English" -> tr(lang, "English", "الإنجليزية")
    else -> value
}

private fun tr(lang: CorrectedLanguage, en: String, ar: String): String = if (lang == CorrectedLanguage.Arabic) ar else en
