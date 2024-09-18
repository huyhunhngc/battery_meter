package io.github.ifa.glancewidget.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val WORK_NAME = "update-battery-meter-widget"

@HiltWorker
class BatteryMonitorWorker @AssistedInject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        startRecording()
        return Result.success()
    }

    private suspend fun startRecording() = coroutineScope {
        launch {
            combine(
                batteryStateRepository.batteryFlow().map { it.myDevice.isCharging }
                    .distinctUntilChanged(),
                batteryStateRepository.extraBatteryFlow().distinctUntilChanged { old, new ->
                    old.chargeCurrent == new.chargeCurrent
                }
            ) { isCharging, extraBatteryInfo ->
                Pair(isCharging, extraBatteryInfo)
            }.conflate().onEach { delay(5000) }.collect { (isCharging, extraBatteryInfo) ->
                batteryStateRepository.saveChargeCurrent(
                    extraBatteryInfo.getChargeDisChargeCurrent(isCharging)
                )
            }
        }
        launch {
            while (true) {
                batteryStateRepository.saveExtraBatteryInformation()
                delay(2000)
            }
        }
    }
}

fun Context.enqueueBatteryMonitorRequest() {
    val request = OneTimeWorkRequest.Builder(BatteryMonitorWorker::class.java)
        .addTag(WORK_NAME)
        .build()

    WorkManager.getInstance(this).enqueue(request)
}

fun Context.cancelBatteryMonitorRequest() {
    WorkManager.getInstance(this).cancelAllWorkByTag(WORK_NAME)
}