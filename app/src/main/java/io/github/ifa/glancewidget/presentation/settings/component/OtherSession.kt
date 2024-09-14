package io.github.ifa.glancewidget.presentation.settings.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.ui.component.TextWithImage
import io.github.ifa.glancewidget.ui.component.TextWithRightArrow

@Composable
fun OtherSession(onOpenAboutScreen: () -> Unit) {
    TextWithImage(
        text = stringResource(R.string.other),
        image = painterResource(id = R.drawable.ic_info),
        modifier = Modifier.padding(vertical = 16.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp),
    ) {
        TextWithRightArrow(
            text = stringResource(id = R.string.about_tab),
            onClick = onOpenAboutScreen
        )
        HorizontalDivider(thickness = 0.5.dp)
        TextWithRightArrow(text = stringResource(id = R.string.license)) {

        }
        HorizontalDivider(thickness = 0.5.dp)
        TextWithRightArrow(text = stringResource(id = R.string.privacy_policy)) {

        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}