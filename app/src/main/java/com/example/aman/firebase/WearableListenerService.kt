package com.example.aman.firebase

import android.util.Log
import com.example.aman.data.db.AmanDatabase
import com.example.aman.data.entities.WaterIntake
import com.example.aman.data.repository.WaterRepository
import com.example.aman.utils.WaterDataSyncManager
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearableListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            WaterDataSyncManager.PATH_ADD_INTAKE -> {
                val amount = String(messageEvent.data).toIntOrNull() ?: return
                handleWaterIntakeFromWear(amount)
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)
        // Handle data changes if needed
    }

    private fun handleWaterIntakeFromWear(amountMl: Int) {
        serviceScope.launch {
            try {
                val database = AmanDatabase.getDatabase(applicationContext)
                val repository = WaterRepository(database.waterIntakeDao())

                // Add water intake to database
                repository.addWaterIntake(amountMl)

                Log.d("WearableListener", "Added $amountMl ml from watch")

                // Sync back to watch with updated totals
                val syncManager = WaterDataSyncManager(applicationContext)
                // You can get today's total and sync back

            } catch (e: Exception) {
                Log.e("WearableListener", "Error adding water from watch", e)
            }
        }
    }
}