package io.github.ifa.glancewidget.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import io.github.ifa.glancewidget.R

fun Context.navigateLicencesScreen() {
    startActivity(Intent(this, OssLicensesMenuActivity::class.java))
}

fun Context.navigateUrl(url: String) {
    val uri: Uri = url.toUri()
    val launched = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        navigateToNativeAppApi30(uri = uri)
    } else {
        navigateToNativeApp(uri = uri)
    }
    if (launched.not()) {
        navigateToCustomTab(uri = uri)
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.sendMail() {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.setData(Uri.parse("mailto:"))
    intent.putExtra(Intent.EXTRA_EMAIL, "huyhunhngc@gmail.com")
    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    startActivity(Intent.createChooser(intent, ""))
}

@Suppress("SwallowedException")
@RequiresApi(Build.VERSION_CODES.R)
private fun Context.navigateToNativeAppApi30(uri: Uri): Boolean {
    val nativeAppIntent = Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER)
    return try {
        startActivity(nativeAppIntent)
        true
    } catch (ex: ActivityNotFoundException) {
        false
    }
}

@SuppressLint("QueryPermissionsNeeded")
private fun Context.navigateToNativeApp(
    uri: Uri,
): Boolean {
    // Get all Apps that resolve a generic url
    val browserActivityIntent =
        Intent().setAction(Intent.ACTION_VIEW).addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.fromParts("http", "", null))
    val genericResolvedList: Set<String> =
        packageManager.queryIntentActivities(browserActivityIntent, 0)
            .map { it.activityInfo.packageName }
            .toSet()

    // Get all apps that resolve the specific Url
    val specializedActivityIntent =
        Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE)
    val resolvedSpecializedList: MutableSet<String> =
        packageManager.queryIntentActivities(browserActivityIntent, 0)
            .map { it.activityInfo.packageName }
            .toMutableSet()

    // Keep only the Urls that resolve the specific, but not the generic urls.
    resolvedSpecializedList.removeAll(genericResolvedList)

    // If the list is empty, no native app handlers were found.
    if (resolvedSpecializedList.isEmpty()) {
        return false
    }

    // We found native handlers. Launch the Intent.
    specializedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(specializedActivityIntent)
    return true
}

private fun Context.navigateToCustomTab(uri: Uri) {
    CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(this, uri)
}