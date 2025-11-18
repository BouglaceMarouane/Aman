package com.example.aman.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.aman.R
import com.example.aman.data.db.AmanDatabase
import com.example.aman.data.repository.WaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HydrationReminderReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.aman.ACTION_ADD_WATER") {
            val amount = intent.getIntExtra("amount", 200)
            addWater(context, amount)
        }
    }

    private fun addWater(context: Context, amountMl: Int) {
        receiverScope.launch {
            try {
                val database = AmanDatabase.getDatabase(context)
                val repository = WaterRepository(database.waterIntakeDao())
                repository.addWaterIntake(amountMl)

                // Show toast on main thread
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.water_added, amountMl),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}