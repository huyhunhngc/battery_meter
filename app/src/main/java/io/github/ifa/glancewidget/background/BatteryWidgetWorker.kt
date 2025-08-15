package io.github.ifa.glancewidget.background

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.ifa.glancewidget.domain.BatteryStateRepository
import io.github.ifa.glancewidget.model.MyDevice
import io.github.ifa.glancewidget.utils.updateBatteryWidget

@HiltWorker
class BatteryWidgetWorker @AssistedInject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
                context.registerReceiver(null, it)
            }
            batteryStatus?.let { batteryStateRepository.setMyDevice(MyDevice.fromIntent(it)) }
            context.updateBatteryWidget()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}