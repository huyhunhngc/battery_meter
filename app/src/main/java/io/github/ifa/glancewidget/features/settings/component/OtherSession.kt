package io.github.ifa.glancewidget.features.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.TextWithRightArrow
import io.github.ifa.glancewidget.ui.component.appPadding

@Composable
fun OtherSession(
    onOpenAboutScreen: () -> Unit,
    onOpenLicenseScreen: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit
) {
    Column(
        modifier = Modifier
            .appPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        TextWithRightArrow(
            text = stringResource(id = R.string.about_tab),
            icon = painterResource(id = R.drawable.ic_groups),
            onClick = onOpenAboutScreen
        )
        Divider()
        TextWithRightArrow(
            text = stringResource(id = R.string.license),
            icon = painterResource(id = R.drawable.ic_license),
            onClick = onOpenLicenseScreen
        )
        Divider()
        TextWithRightArrow(
            text = stringResource(id = R.string.privacy_policy),
            icon = painterResource(id = R.drawable.ic_policy),
            onClick = onOpenPrivacyPolicy
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun Divider() {
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(2.0.dp))
}

@Preview
@Composable
fun OtherSessionPreview() {
    OtherSession({}, {}, {})
}