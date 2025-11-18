package com.example.aman.receiver

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class WearDataLayerListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WearDataListener"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        for (event in dataEvents) {
            val uri = event.dataItem.uri
            val path = uri.path

            when (path) {
                "/hydration_data" -> {
                    val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap

                    val todayIntake = dataMap.getInt("todayIntake", 0)
                    val dailyGoal = dataMap.getInt("dailyGoal", 2000)
                    val progress = dataMap.getInt("todayProgress", 0)

                    Log.d(TAG, "Data updated: intake=$todayIntake, goal=$dailyGoal, progress=$progress%")
                }
            }
        }
    }
}