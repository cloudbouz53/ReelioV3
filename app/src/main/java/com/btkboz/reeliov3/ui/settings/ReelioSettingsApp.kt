package com.btkboz.reeliov3.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.outlined.Database
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.btkboz.reeliov3.ui.theme.ReelioThemeMode
import com.btkboz.reeliov3.ui.theme.ReelioV3Theme

private enum class SettingsScreen {
    Settings, EditProfile, Theme, AppLanguage, ProcessingDefaults,
    Connections, Credentials, DataStorage, Privacy, Help, About
}

private enum class AppLanguage { English, Arabic }

private val radius10 = RoundedCornerShape(10.dp)
private val radius12 = RoundedCornerShape(12.dp)
private val radius16 = RoundedCornerShape(16.dp)

@Composable
fun ReelioSettingsApp() {
    var themeMode by remember { mutableStateOf(ReelioThemeMode.Light) }
    var language by remember { mutableStateOf(AppLanguage.English) }

    ReelioV3Theme(mode = themeMode) {
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.ui.platform.LocalLayoutDirection provides
                if (language == AppLanguage.Arabic) LayoutDirection.Rtl else LayoutDirection.Ltr
        ) {
            SettingsNavigator(
                themeMode = themeMode,
                onThemeModeChange = { themeMode = it },
                language = language,
                onLanguageChange = { language = it },
            )
        }
    }
}

@Composable
private fun SettingsNavigator(
    themeMode: ReelioThemeMode,
    onThemeModeChange: (ReelioThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    val stack = remember { mutableStateListOf(SettingsScreen.Settings) }
    val screen = stack.last()
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    var toast by remember { mutableStateOf<String?>(null) }

    fun open(target: SettingsScreen) { stack.add(target) }
    fun back() { if (stack.size > 1) stack.removeAt(stack.lastIndex) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ReelioHeader(
                title = titleFor(screen, language),
                canGoBack = stack.size > 1,
                rtl = rtl,
                onBack = ::back,
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                SettingsScreen.Settings -> SettingsHome(language, themeMode, ::open)
                SettingsScreen.EditProfile -> EditProfileScreen(language) { toast = t(language, "Saved", "تم الحفظ") }
                SettingsScreen.Theme -> ThemeScreen(language, themeMode, onThemeModeChange)
                SettingsScreen.AppLanguage -> LanguageScreen(language, onLanguageChange)
                SettingsScreen.ProcessingDefaults -> ProcessingDefaultsScreen(language) { toast = t(language, "Defaults saved", "تم حفظ الإعدادات") }
                SettingsScreen.Connections -> ConnectionsScreen(language)
                SettingsScreen.Credentials -> CredentialsScreen(language) { toast = t(language, "Credentials saved", "تم حفظ بيانات الاعتماد") }
                SettingsScreen.DataStorage -> DataStorageScreen(language) { toast = t(language, "Temporary cache cleared", "تم مسح الملفات المؤقتة") }
                SettingsScreen.Privacy -> PrivacyScreen(language)
                SettingsScreen.Help -> HelpScreen(language) { open(SettingsScreen.Connections) }
                SettingsScreen.About -> AboutScreen(language) { open(SettingsScreen.Privacy) }
            }

            toast?.let {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(21.dp).fillMaxWidth(),
                    shape = radius12,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    tonalElevation = 1.dp,
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
private fun ReelioHeader(title: String, canGoBack: Boolean, rtl: Boolean, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(66.dp).padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
            if (canGoBack) {
                Surface(
                    modifier = Modifier.size(44.dp).clickable(onClick = onBack),
                    shape = radius10,
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(20.dp).graphicsLayer(scaleX = if (rtl) -1f else 1f),
                        )
                    }
                }
            }
        }
        Text(
            title,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(44.dp))
    }
}

@Composable
private fun SettingsHome(language: AppLanguage, themeMode: ReelioThemeMode, open: (SettingsScreen) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 21.dp),
        verticalArrangement = Arrangement.spacedBy(21.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 13.dp, bottom = 34.dp),
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { open(SettingsScreen.EditProfile) },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp,
            ) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                        Box(Modifier.size(55.dp), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.AccountCircle, null, Modifier.size(24.dp)) }
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t(language, "Your Reelio profile", "ملفك الشخصي في Reelio"), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(t(language, "Edit your name and profile details", "عدّل اسمك وتفاصيل ملفك الشخصي"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DirectionChevron()
                }
            }
        }
        item {
            SettingSection(t(language, "GENERAL", "عام"), listOf(
                SettingEntry(Icons.Outlined.LightMode, t(language, "Theme", "المظهر"), value = themeMode.name) { open(SettingsScreen.Theme) },
                SettingEntry(Icons.Outlined.Language, t(language, "App Language", "لغة التطبيق"), value = if (language == AppLanguage.Arabic) "العربية" else "English") { open(SettingsScreen.AppLanguage) },
            ))
        }
        item {
            SettingSection(t(language, "CONTENT PROCESSING", "معالجة المحتوى"), listOf(
                SettingEntry(Icons.Outlined.Settings, t(language, "Processing Defaults", "إعدادات المعالجة الافتراضية"), t(language, "Explanation, summary, classification & indexing", "الشرح، التلخيص، التصنيف والفهرسة")) { open(SettingsScreen.ProcessingDefaults) }
            ))
        }
        item {
            SettingSection(t(language, "SYSTEM", "النظام"), listOf(
                SettingEntry(Icons.Outlined.Link, t(language, "Connections", "الاتصالات")) { open(SettingsScreen.Connections) },
                SettingEntry(Icons.Outlined.Key, t(language, "Credentials & Security", "بيانات الاعتماد والأمان")) { open(SettingsScreen.Credentials) },
                SettingEntry(Icons.Outlined.Storage, t(language, "Data & Storage", "البيانات والتخزين")) { open(SettingsScreen.DataStorage) },
            ))
        }
        item {
            SettingSection(t(language, "SUPPORT & INFORMATION", "الدعم والمعلومات"), listOf(
                SettingEntry(Icons.Outlined.HelpOutline, t(language, "Help & Support", "المساعدة والدعم")) { open(SettingsScreen.Help) },
                SettingEntry(Icons.Outlined.Security, t(language, "Privacy Policy", "سياسة الخصوصية")) { open(SettingsScreen.Privacy) },
                SettingEntry(Icons.Outlined.Info, t(language, "About Reelio", "حول Reelio")) { open(SettingsScreen.About) },
            ))
        }
    }
}

private data class SettingEntry(
    val icon: ImageVector,
    val title: String,
    val subtitle: String? = null,
    val value: String? = null,
    val onClick: () -> Unit,
)

@Composable
private fun SettingSection(title: String, entries: List<SettingEntry>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionLabel(title)
        Surface(shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
            Column {
                entries.forEachIndexed { index, entry ->
                    if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 55.dp).clickable(onClick = entry.onClick).padding(13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(entry.icon, null, Modifier.size(22.dp))
                        Spacer(Modifier.width(13.dp))
                        Column(Modifier.weight(1f)) {
                            Text(entry.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            entry.subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                        entry.value?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Spacer(Modifier.width(8.dp))
                        DirectionChevron()
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectionChevron() {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Icon(Icons.Outlined.ChevronRight, null, Modifier.size(20.dp).graphicsLayer(scaleX = if (rtl) -1f else 1f), tint = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun EditProfileScreen(language: AppLanguage, onSave: () -> Unit) {
    var name by remember { mutableStateOf("Reelio User") }
    var email by remember { mutableStateOf("user@example.com") }
    ScreenColumn {
        Column(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Box(Modifier.size(89.dp), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.AccountCircle, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            TextButton(onClick = {}) { Text(t(language, "Change photo", "تغيير الصورة"), fontSize = 14.sp) }
            Text(t(language, "JPG or PNG. Use a clear square photo.", "JPG أو PNG. استخدم صورة مربعة وواضحة."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(t(language, "Profile information", "معلومات الملف الشخصي"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        ReelioField(t(language, "Full name", "الاسم الكامل"), name, { name = it }, t(language, "This name appears on your Reelio profile.", "يظهر هذا الاسم في ملفك الشخصي على Reelio."))
        ReelioField(t(language, "Email address", "عنوان البريد الإلكتروني"), email, { email = it }, t(language, "Verified email address", "عنوان بريد إلكتروني موثّق"), KeyboardType.Email)
        PrimaryButton(t(language, "Save changes", "حفظ التغييرات"), onSave)
    }
}

@Composable
private fun ThemeScreen(language: AppLanguage, selected: ReelioThemeMode, onSelected: (ReelioThemeMode) -> Unit) {
    ScreenColumn {
        Intro(t(language, "Choose how Reelio looks", "اختر مظهر Reelio"), t(language, "The layout stays identical. Only semantic theme colors change.", "يبقى التخطيط كما هو، وتتغير ألوان المظهر الدلالية فقط."))
        ChoiceCard(Icons.Outlined.Computer, t(language, "System", "النظام"), t(language, "Follow your device appearance", "اتبع مظهر جهازك"), selected == ReelioThemeMode.System) { onSelected(ReelioThemeMode.System) }
        ChoiceCard(Icons.Outlined.LightMode, t(language, "Light", "فاتح"), t(language, "Use the light Reelio palette", "استخدم لوحة Reelio الفاتحة"), selected == ReelioThemeMode.Light) { onSelected(ReelioThemeMode.Light) }
        ChoiceCard(Icons.Outlined.DarkMode, t(language, "Dark", "داكن"), t(language, "Use the dark Reelio palette", "استخدم لوحة Reelio الداكنة"), selected == ReelioThemeMode.Dark) { onSelected(ReelioThemeMode.Dark) }
    }
}

@Composable
private fun LanguageScreen(language: AppLanguage, onSelected: (AppLanguage) -> Unit) {
    ScreenColumn {
        Intro(t(language, "Choose the interface language", "اختر لغة الواجهة"), t(language, "This changes Reelio navigation, labels, alignment, and directional icons.", "يغيّر هذا لغة التنقل والتسميات والمحاذاة والأيقونات الاتجاهية في Reelio."))
        ChoiceCard(null, "English", "Left-to-right interface", language == AppLanguage.English, "EN") { onSelected(AppLanguage.English) }
        ChoiceCard(null, "العربية", "واجهة من اليمين إلى اليسار", language == AppLanguage.Arabic, "AR") { onSelected(AppLanguage.Arabic) }
        InfoCard(t(language, "Changing the app language does not change your processing-language defaults.", "تغيير لغة التطبيق لا يغيّر إعدادات لغات المعالجة الافتراضية."), Icons.Outlined.Info)
    }
}

@Composable
private fun ProcessingDefaultsScreen(language: AppLanguage, onSave: () -> Unit) {
    var explanation by remember { mutableStateOf("Arabic") }
    var summary by remember { mutableStateOf("Arabic") }
    var classification by remember { mutableStateOf("Arabic") }
    var indexing by remember { mutableStateOf("Arabic") }
    ScreenColumn {
        Intro(t(language, "Default languages for new videos", "اللغات الافتراضية للفيديوهات الجديدة"), t(language, "Use these automatically when adding content. You can still override them for an individual video.", "استخدم هذه اللغات تلقائيًا عند إضافة المحتوى، ويمكنك تغييرها لكل فيديو بشكل مستقل."))
        Surface(shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
            Column {
                LanguageRow(t(language, "Explanation language", "لغة الشرح"), t(language, "Language used to explain the reel's idea", "اللغة المستخدمة لشرح فكرة الريل"), explanation) { explanation = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                LanguageRow(t(language, "Summary language", "لغة التلخيص"), t(language, "Language used for the concise summary", "اللغة المستخدمة للتلخيص المختصر"), summary) { summary = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                LanguageRow(t(language, "Classification language", "لغة التصنيف"), t(language, "Category and subcategory labels", "تسميات التصنيف والتصنيف الفرعي"), classification) { classification = it }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                LanguageRow(t(language, "Indexing language", "لغة الفهرسة"), t(language, "Keywords and search terms", "الكلمات المفتاحية ومصطلحات البحث"), indexing) { indexing = it }
            }
        }
        InfoCard(t(language, "A video can be in English while its explanation, summary, classification, and indexing are created in Arabic.", "يمكن أن يكون الفيديو بالإنجليزية بينما يتم إنشاء الشرح والتلخيص والتصنيف والفهرسة بالعربية."), Icons.Outlined.AutoAwesome)
        PrimaryButton(t(language, "Save defaults", "حفظ الإعدادات الافتراضية"), onSave)
    }
}

@Composable
private fun LanguageRow(title: String, subtitle: String, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().heightIn(min = 68.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box {
            Surface(modifier = Modifier.width(132.dp).height(42.dp).clickable { expanded = true }, shape = RoundedCornerShape(9.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.background) {
                Row(Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(selected, Modifier.weight(1f), fontSize = 13.sp)
                    Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp))
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("Auto", "Arabic", "English").forEach { value ->
                    DropdownMenuItem(text = { Text(value, fontSize = 13.sp) }, onClick = { onSelected(value); expanded = false }, trailingIcon = { if (value == selected) Icon(Icons.Outlined.Check, null, Modifier.size(16.dp)) })
                }
            }
        }
    }
}

@Composable
private fun ConnectionsScreen(language: AppLanguage) {
    ScreenColumn {
        Intro(t(language, "Service readiness", "جاهزية الخدمات"), t(language, "Check the four components Reelio needs. Status always includes text and an icon.", "تحقق من المكوّنات الأربعة التي يحتاجها Reelio. تتضمن الحالة دائمًا نصًا وأيقونة."))
        SectionLabel(t(language, "DATABASE & STORAGE", "قاعدة البيانات والتخزين"))
        ServiceCard(Icons.Outlined.Database, "Neon PostgreSQL", t(language, "Connected", "متصل"))
        ServiceCard(Icons.Outlined.Cloud, "Cloudflare R2", t(language, "Connected", "متصل"))
        SectionLabel(t(language, "MEDIA TOOLS", "أدوات الوسائط"))
        ServiceCard(Icons.Outlined.Download, "yt-dlp", t(language, "Available", "متاح"))
        ServiceCard(Icons.Outlined.Settings, "FFmpeg", t(language, "Available", "متاح"))
        InfoCard(t(language, "Technical output is kept out of the interface. Failures are translated into clear user-facing messages.", "لا يظهر الإخراج التقني في الواجهة. يتم تحويل حالات الفشل إلى رسائل واضحة للمستخدم."), Icons.Outlined.Security)
    }
}

@Composable
private fun ServiceCard(icon: ImageVector, title: String, status: String) {
    Surface(shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            IconTile(icon)
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Check, null, Modifier.size(14.dp)); Spacer(Modifier.width(5.dp)); Text(status, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            TextButton(onClick = {}) { Text("Check", fontSize = 13.sp) }
        }
    }
}

@Composable
private fun CredentialsScreen(language: AppLanguage, onSave: () -> Unit) {
    ScreenColumn {
        Intro(t(language, "Secure service credentials", "بيانات اعتماد الخدمات الآمنة"), t(language, "Saved secrets remain masked. Reelio should store them using Android Keystore-backed encrypted storage.", "تبقى البيانات السرية المحفوظة مخفية ويجب تخزينها بأمان."))
        Text(t(language, "AI services", "خدمات الذكاء الاصطناعي"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        SecretField("Gemini API key")
        SecretField("ElevenLabs API key")
        Text(t(language, "Database & storage", "قاعدة البيانات والتخزين"), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        SecretField("Database URL")
        SecretField("Cloudflare account ID")
        SecretField("R2 access key ID")
        SecretField("R2 secret access key")
        ReelioField("R2 bucket name", "", {}, placeholder = "Your bucket name")
        ReelioField("R2 endpoint", "", {}, placeholder = "https://…")
        InfoCard(t(language, "Never show full saved credentials in the UI or include them in logs and error reports.", "لا تعرض بيانات الاعتماد المحفوظة كاملة ولا تُضمّنها في السجلات أو تقارير الأخطاء."), Icons.Outlined.Lock)
        PrimaryButton(t(language, "Save securely", "حفظ بأمان"), onSave)
    }
}

@Composable
private fun SecretField(label: String) {
    var value by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text("Configured", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            singleLine = true,
            placeholder = { Text("••••••••••••••••") },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { Icon(if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, Modifier.clickable { visible = !visible }) },
            shape = radius10,
        )
    }
}

@Composable
private fun DataStorageScreen(language: AppLanguage, onCacheCleared: () -> Unit) {
    var dialog by remember { mutableStateOf(false) }
    ScreenColumn {
        Intro(t(language, "Where Reelio keeps your data", "أين يحتفظ Reelio ببياناتك"), t(language, "Structured knowledge and media are stored separately. Temporary processing files are cleaned up after use.", "يتم تخزين المعرفة المنظمة والوسائط بشكل منفصل وتُنظف الملفات المؤقتة بعد الاستخدام."))
        SectionLabel(t(language, "PERMANENT STORAGE", "التخزين الدائم"))
        StorageInfo(Icons.Outlined.Database, t(language, "Knowledge data", "بيانات المعرفة"), t(language, "Titles, explanations, summaries, key points, categories, tags and indexing data.", "العناوين والشروحات والملخصات والنقاط والتصنيفات والوسوم وبيانات الفهرسة."), "Neon PostgreSQL")
        StorageInfo(Icons.Outlined.Cloud, t(language, "Media", "الوسائط"), t(language, "Saved video and thumbnail files associated with your library items.", "الفيديوهات والصور المصغرة المرتبطة بعناصر المكتبة."), "Cloudflare R2")
        SectionLabel(t(language, "ON THIS DEVICE", "على هذا الجهاز"))
        StorageInfo(Icons.Outlined.Storage, t(language, "Temporary processing cache", "ذاكرة المعالجة المؤقتة"), t(language, "Automatically removed after processing completes or fails.", "تُحذف تلقائيًا بعد انتهاء المعالجة أو فشلها."), null)
        Surface(modifier = Modifier.fillMaxWidth().clickable { dialog = true }, shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
            Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DeleteOutline, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(t(language, "Clear temporary cache", "مسح الذاكرة المؤقتة"), fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(t(language, "Does not remove saved Library items.", "لا يحذف عناصر المكتبة المحفوظة."), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Text(t(language, "Clear", "مسح"), fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            icon = { Icon(Icons.Outlined.DeleteOutline, null) },
            title = { Text(t(language, "Clear temporary cache?", "مسح الذاكرة المؤقتة؟")) },
            text = { Text(t(language, "Temporary processing files will be removed. Your saved Library items will be kept.", "ستُحذف ملفات المعالجة المؤقتة مع الاحتفاظ بعناصر المكتبة المحفوظة.")) },
            confirmButton = { TextButton(onClick = { dialog = false; onCacheCleared() }) { Text(t(language, "Clear temporary cache", "مسح الذاكرة المؤقتة")) } },
            dismissButton = { TextButton(onClick = { dialog = false }) { Text(t(language, "Keep cache", "الاحتفاظ بها")) } },
        )
    }
}

@Composable
private fun PrivacyScreen(language: AppLanguage) {
    val sections = listOf(
        t(language, "What Reelio processes", "ما الذي يعالجه Reelio") to t(language, "Reelio processes video links and content you choose to add so it can create organized knowledge such as a clear title, explanation, summary, key points, categories, tags and indexing terms.", "يعالج Reelio روابط الفيديو والمحتوى الذي تختار إضافته لإنشاء معرفة منظمة."),
        t(language, "Internal transcript", "النص الداخلي") to t(language, "Speech-to-text output is an internal processing layer used to understand the content. It is not presented as a user-facing transcript feature.", "تحويل الكلام إلى نص طبقة معالجة داخلية لفهم المحتوى ولا يُعرض كميزة للمستخدم."),
        t(language, "Credentials", "بيانات الاعتماد") to t(language, "Saved values must remain encrypted, masked in the interface, and redacted from logs and error reports.", "يجب أن تبقى القيم المحفوظة مشفرة ومخفية في الواجهة والسجلات."),
        t(language, "Storage", "التخزين") to t(language, "Structured library data is stored in Neon PostgreSQL and permanent media is stored in Cloudflare R2.", "تُخزن بيانات المكتبة المنظمة في Neon PostgreSQL والوسائط الدائمة في Cloudflare R2."),
        t(language, "User-facing errors", "الأخطاء الموجهة للمستخدم") to t(language, "Reelio should present understandable error messages instead of stack traces, shell output, exit codes or raw infrastructure details.", "يعرض Reelio رسائل أخطاء مفهومة بدل التفاصيل التقنية الخام."),
    )
    ScreenColumn {
        Intro(t(language, "Privacy in Reelio", "الخصوصية في Reelio"), t(language, "A concise product-facing summary based on the Reelio implementation requirements.", "ملخص موجز للخصوصية مستند إلى متطلبات تنفيذ Reelio."))
        sections.forEach { (title, body) -> Column { Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(5.dp)); Text(body, fontSize = 14.sp, lineHeight = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        InfoCard(t(language, "This mockup is product UI copy, not a substitute for a finalized legal privacy policy.", "هذه نسخة واجهة للمنتج وليست بديلًا عن سياسة خصوصية قانونية نهائية."), Icons.Outlined.Info)
    }
}

@Composable
private fun HelpScreen(language: AppLanguage, onConnections: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val faq = listOf(
        t(language, "How do I add a video?", "كيف أضيف فيديو؟") to t(language, "Use Share to Reelio from a supported app, or copy the video URL and paste it on Home.", "استخدم المشاركة إلى Reelio أو الصق رابط الفيديو في الصفحة الرئيسية."),
        t(language, "Can each processing output use a different language?", "هل يمكن لكل مخرج معالجة استخدام لغة مختلفة؟") to t(language, "Yes. Explanation, summary, classification and indexing can each have their own language default.", "نعم، يمكن للشرح والتلخيص والتصنيف والفهرسة استخدام لغات افتراضية مختلفة."),
        t(language, "Why can't Reelio process a video?", "لماذا لا يستطيع Reelio معالجة فيديو؟") to t(language, "Open Connections to check database, storage and local media-tool readiness.", "افتح الاتصالات للتحقق من جاهزية قاعدة البيانات والتخزين وأدوات الوسائط."),
        t(language, "Where is the transcript?", "أين النص؟") to t(language, "Reelio does not expose the transcript as a user feature. It is used internally only to understand the video.", "لا يعرض Reelio النص كميزة للمستخدم، بل يستخدم داخليًا لفهم الفيديو."),
    )
    ScreenColumn {
        Intro(t(language, "How can we help?", "كيف يمكننا مساعدتك؟"), t(language, "Start with common questions or run a connection check if Reelio cannot process content.", "ابدأ بالأسئلة الشائعة أو تحقق من الاتصالات عند تعذر معالجة المحتوى."))
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().height(52.dp), singleLine = true, leadingIcon = { Icon(Icons.Outlined.Search, null, Modifier.size(18.dp)) }, placeholder = { Text(t(language, "Search help", "البحث في المساعدة")) }, shape = radius10)
        faq.filter { query.isBlank() || it.first.contains(query, ignoreCase = true) }.forEach { (q, a) -> FaqCard(q, a) }
        SectionLabel(t(language, "TROUBLESHOOTING", "استكشاف الأخطاء"))
        Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onConnections), shape = radius12, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
            Row(Modifier.heightIn(min = 55.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Settings, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Text(t(language, "Check Connections", "فحص الاتصالات"), Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium); DirectionChevron() }
        }
    }
}

@Composable
private fun FaqCard(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }, shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text(question, Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium); Icon(Icons.Outlined.ExpandMore, null, Modifier.size(20.dp).graphicsLayer(rotationZ = if (expanded) 180f else 0f), tint = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (expanded) { Spacer(Modifier.height(8.dp)); Text(answer, fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun AboutScreen(language: AppLanguage, onPrivacy: () -> Unit) {
    ScreenColumn {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = RoundedCornerShape(18.dp), color = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) { Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.BookmarkAdded, null, Modifier.size(32.dp)) } }
            Spacer(Modifier.height(13.dp)); Text("Reelio", fontSize = 22.sp, fontWeight = FontWeight.SemiBold); Text(t(language, "Save → Understand → Organize → Find", "احفظ ← افهم ← نظّم ← اعثر"), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(t(language, "Reelio turns useful short-form videos into an organized personal knowledge library. Instead of saving a video and forgetting why it mattered, Reelio helps preserve the knowledge inside it.", "يحوّل Reelio الفيديوهات القصيرة المفيدة إلى مكتبة معرفة شخصية منظمة ويحفظ المعرفة الموجودة داخلها."), fontSize = 14.sp, lineHeight = 24.sp)
        Surface(shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) { Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) { KeyValue("Version", "1.0.0"); KeyValue(t(language, "Interface languages", "لغات الواجهة"), "English, العربية"); KeyValue(t(language, "Theme", "المظهر"), "System / Light / Dark") } }
        SectionLabel(t(language, "SUPPORTED DISCOVERY FLOW", "مسار الاكتشاف المدعوم"))
        InfoCard(t(language, "Share to Reelio or Paste Link → understand and organize → save to Library → search and find later.", "شارك إلى Reelio أو الصق الرابط ← افهم ونظّم ← احفظ في المكتبة ← ابحث واعثر لاحقًا."), Icons.Outlined.AutoAwesome)
        Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onPrivacy), shape = radius12, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) { Row(Modifier.heightIn(min = 55.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Security, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Text(t(language, "Privacy Policy", "سياسة الخصوصية"), Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.Medium); DirectionChevron() } }
    }
}

@Composable
private fun KeyValue(key: String, value: String) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(key, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium) } }

@Composable
private fun StorageInfo(icon: ImageVector, title: String, body: String, service: String?) {
    Surface(shape = radius16, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.fillMaxWidth().padding(13.dp)) { Icon(icon, null, Modifier.size(22.dp)); Spacer(Modifier.width(13.dp)); Column { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); service?.let { Text(it, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 5.dp)) } } }
    }
}

@Composable
private fun ChoiceCard(icon: ImageVector?, title: String, subtitle: String, selected: Boolean, shortLabel: String? = null, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = radius16, border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary.copy(alpha = .45f) else MaterialTheme.colorScheme.outline), color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface) {
        Row(Modifier.heightIn(min = 68.dp).padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = radius10, color = MaterialTheme.colorScheme.surfaceVariant) { Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { if (icon != null) Icon(icon, null, Modifier.size(20.dp)) else Text(shortLabel.orEmpty(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) } }
            Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Surface(shape = CircleShape, border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant), color = androidx.compose.ui.graphics.Color.Transparent) { Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) { if (selected) Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary) { Spacer(Modifier.size(10.dp)) } } }
        }
    }
}

@Composable
private fun ReelioField(label: String, value: String, onValueChange: (String) -> Unit, helper: String? = null, keyboardType: KeyboardType = KeyboardType.Text, placeholder: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().height(55.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), placeholder = placeholder?.let { { Text(it) } }, shape = radius10)
        helper?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
private fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 21.dp),
        verticalArrangement = Arrangement.spacedBy(21.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 13.dp, bottom = 34.dp),
    ) {
        item { Column(verticalArrangement = Arrangement.spacedBy(21.dp), content = content) }
    }
}

@Composable
private fun Intro(title: String, subtitle: String) { Column { Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(5.dp)); Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } }

@Composable
private fun SectionLabel(text: String) { Text(text, modifier = Modifier.padding(horizontal = 5.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = .8.sp) }

@Composable
private fun IconTile(icon: ImageVector) { Surface(shape = radius10, color = MaterialTheme.colorScheme.surfaceVariant) { Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { Icon(icon, null, Modifier.size(20.dp)) } } }

@Composable
private fun InfoCard(text: String, icon: ImageVector) { Surface(shape = radius12, color = MaterialTheme.colorScheme.secondary) { Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.Top) { Icon(icon, null, Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Text(text, fontSize = 13.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSecondary) } } }

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) { Button(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 55.dp), shape = radius10, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)) { Text(text, fontWeight = FontWeight.SemiBold) } }

private fun titleFor(screen: SettingsScreen, language: AppLanguage): String = when (screen) {
    SettingsScreen.Settings -> t(language, "Settings", "الإعدادات")
    SettingsScreen.EditProfile -> t(language, "Edit Profile", "تعديل الملف الشخصي")
    SettingsScreen.Theme -> t(language, "Theme", "المظهر")
    SettingsScreen.AppLanguage -> t(language, "App Language", "لغة التطبيق")
    SettingsScreen.ProcessingDefaults -> t(language, "Processing Defaults", "إعدادات المعالجة الافتراضية")
    SettingsScreen.Connections -> t(language, "Connections", "الاتصالات")
    SettingsScreen.Credentials -> t(language, "Credentials & Security", "بيانات الاعتماد والأمان")
    SettingsScreen.DataStorage -> t(language, "Data & Storage", "البيانات والتخزين")
    SettingsScreen.Privacy -> t(language, "Privacy Policy", "سياسة الخصوصية")
    SettingsScreen.Help -> t(language, "Help & Support", "المساعدة والدعم")
    SettingsScreen.About -> t(language, "About Reelio", "حول Reelio")
}

private fun t(language: AppLanguage, en: String, ar: String): String = if (language == AppLanguage.Arabic) ar else en
