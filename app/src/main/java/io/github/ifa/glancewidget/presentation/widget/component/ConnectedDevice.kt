package io.github.ifa.glancewidget.presentation.widget.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ifa.glancewidget.R
import io.github.ifa.glancewidget.model.BonedDevice
import io.github.ifa.glancewidget.model.BonnedDeviceSettings
import io.github.ifa.glancewidget.ui.component.SessionText

@Composable
fun ConnectedDevice(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SessionText(
            text = stringResource(id = R.string.connected_devices),
            modifier = Modifier.padding(4.dp)
        )

        Text(
            text = "Bluetooth paired devices. Toggle to hide or show in widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

@Preview
@Composable
fun ConnectedDevicePreview() {
    ConnectedDevice()
}