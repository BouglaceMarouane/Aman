package com.example.aman.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.*
import kotlinx.coroutines.tasks.await

class WaterDataSyncManager(private val context: Context) {

    private val dataClient: DataClient by lazy {
        Wearable.getDataClient(context)
    }

    private val messageClient: MessageClient by lazy {
        Wearable.getMessageClient(context)
    }

    companion object {
        private const val TAG = "WaterDataSync"
        const val PATH_HYDRATION_DATA = "/hydration_data"
        const val PATH_ADD_INTAKE = "/add_intake"

        const val KEY_DAILY_GOAL = "dailyGoal"
        const val KEY_TODAY_INTAKE = "todayIntake"
        const val KEY_TODAY_PROGRESS = "todayProgress"
        const val KEY_TIMESTAMP = "timestamp"
    }

    suspend fun syncWaterData(todayIntake: Int, dailyGoal: Int) {
        try {
            val progress = (todayIntake.toFloat() / dailyGoal * 100).toInt()

            val putDataReq = PutDataMapRequest.create(PATH_HYDRATION_DATA).apply {
                dataMap.putInt(KEY_DAILY_GOAL, dailyGoal)
                dataMap.putInt(KEY_TODAY_INTAKE, todayIntake)
                dataMap.putInt(KEY_TODAY_PROGRESS, progress)
                dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }.asPutDataRequest()

            putDataReq.setUrgent()

            dataClient.putDataItem(putDataReq).await()
            Log.d(TAG, "Synced water data: intake=$todayIntake, goal=$dailyGoal")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync water data", e)
        }
    }

    suspend fun sendWaterIntakeToWatch(amountMl: Int) {
        try {
            val nodes = Wearable.getNodeClient(context).connectedNodes.await()

            nodes.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    PATH_ADD_INTAKE,
                    amountMl.toString().toByteArray()
                ).await()

                Log.d(TAG, "Sent water intake to watch: $amountMl ml")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send water intake to watch", e)
        }
    }
}