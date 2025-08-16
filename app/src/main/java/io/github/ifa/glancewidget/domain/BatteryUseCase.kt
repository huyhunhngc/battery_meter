package io.github.ifa.glancewidget.domain

import android.os.Build
import io.github.ifa.glancewidget.model.AppSettings
import io.github.ifa.glancewidget.model.BatteryData
import io.github.ifa.glancewidget.model.ChargeDisChargeCurrent
import io.github.ifa.glancewidget.model.ExtraBatteryInfo
import io.github.ifa.glancewidget.model.wrapper.BatteryDataWrapper
import io.github.ifa.glancewidget.model.wrapper.PowerDetails
import io.github.ifa.glancewidget.utils.Constants.NUMBER_OF_CYCLES_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import javax.inject.Inject

class BatteryUseCase @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val appSettingsRepository: AppSettingsRepository,
) {
    fun getBatteryWrapper(): Flow<BatteryDataWrapper> {
        return combine(
            batteryStateRepository.batteryFlow(),
            batteryStateRepository.extraBatteryFlow(),
            appSettingsRepository.get(),
            getNumberOfCyclesAndroidApi33()
        ) { batteryData, extraBatteryInfo, appSettings, cycles ->
            val chargeDisChargeCurrent =
                batteryStateRepository.chargeCurrentFlow().firstOrNull() ?: ChargeDisChargeCurrent()
            BatteryDataWrapper(
                batteryData = batteryData.applySetting(appSettings, cycles),
                extraBatteryInfo = extraBatteryInfo,
                powerDetails = extractPower(batteryData, extraBatteryInfo),
                chargeDisChargeCurrent = chargeDisChargeCurrent
            )
        }
    }

    fun getPower(): Flow<PowerDetails> {
        return combine(
            batteryStateRepository.batteryFlow(),
            batteryStateRepository.extraBatteryFlow()
        ) { batteryData, extraBatteryInfo ->
            extractPower(batteryData, extraBatteryInfo)
        }
    }

    private fun BatteryData.applySetting(appSettings: AppSettings, cycles: Int): BatteryData {
        return copy(
            myDevice = myDevice.copy(cycleCount = if (cycles == 0) myDevice.cycleCount else cycles),
            batteryConnectedDevices = if (appSettings.notificationSetting.showPairedDevices) {
                batteryConnectedDevices.distinctBy { it.address }
            } else {
                emptyList()
            }
        )
    }

    private fun extractPower(
        batteryData: BatteryData,
        extraBatteryInfo: ExtraBatteryInfo
    ): PowerDetails {
        val power = extraBatteryInfo.powerInWatt(
            batteryData.myDevice.voltage
        ).toFloat()
        val powerPercentage = power / extraBatteryInfo.maxWattsChargeInput
        return PowerDetails(
            power = power,
            powerPercentage = powerPercentage
        )
    }

    private fun getNumberOfCyclesAndroidApi33(): Flow<Int> = flow {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            emit(0)
            return@flow
        }
        if (!File(NUMBER_OF_CYCLES_PATH).exists()) {
            emit(0)
            return@flow
        }
        var numberOfCycles: Int
        val cycleCount = File(NUMBER_OF_CYCLES_PATH).absolutePath
        withContext(Dispatchers.IO) {
            try {
                val br: BufferedReader? = try {
                    BufferedReader(FileReader(cycleCount))
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    null
                }
                numberOfCycles = br?.readLine()?.toInt() ?: 0
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                numberOfCycles = 0
            }
        }
        emit(numberOfCycles)
    }
}