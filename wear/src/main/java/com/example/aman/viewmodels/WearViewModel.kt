package com.example.aman.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WearViewModel(application: Application) : AndroidViewModel(application) {

    private val _todayIntake = MutableLiveData<Int>(0)
    val todayIntake: LiveData<Int> = _todayIntake

    private val _dailyGoal = MutableLiveData<Int>(2000)
    val dailyGoal: LiveData<Int> = _dailyGoal

    private val messageClient: MessageClient by lazy {
        Wearable.getMessageClient(application)
    }

    private val dataClient: DataClient by lazy {
        Wearable.getDataClient(application)
    }

    companion object {
        private const val TAG = "WearViewModel"
        private const val PATH_ADD_INTAKE = "/add_intake"
    }

    init {
        setupDataListener()
    }

    fun addWater(amountMl: Int) {
        // Update local value immediately
        _todayIntake.value = (_todayIntake.value ?: 0) + amountMl

        // Send to phone
        sendWaterIntakeToPhone(amountMl)
    }

    private fun sendWaterIntakeToPhone(amount: Int) {
        viewModelScope.launch {
            try {
                val nodeClient = Wearable.getNodeClient(getApplication())
                val nodes = nodeClient.connectedNodes.await()

                if (nodes.isEmpty()) {
                    Log.w(TAG, "No connected nodes")
                    return@launch
                }

                val bytes = amount.toString().toByteArray()

                nodes.forEach { node ->
                    try {
                        messageClient.sendMessage(node.id, PATH_ADD_INTAKE, bytes).await()
                        Log.d(TAG, "Sent $amount ml to phone")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to send to node", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send water intake", e)
            }
        }
    }

    private fun setupDataListener() {
        val listener = DataClient.OnDataChangedListener { dataEvents ->
            for (event in dataEvents) {
                if (event.type == DataEvent.TYPE_CHANGED) {
                    val item = event.dataItem
                    if (item.uri.path == "/hydration_data") {
                        val dataMap = DataMapItem.fromDataItem(item).dataMap

                        val intake = dataMap.getInt("todayIntake", 0)
                        val goal = dataMap.getInt("dailyGoal", 2000)

                        _todayIntake.postValue(intake)
                        _dailyGoal.postValue(goal)

                        Log.d(TAG, "Received: intake=$intake, goal=$goal")
                    }
                }
            }
        }

        dataClient.addListener(listener)
    }
}