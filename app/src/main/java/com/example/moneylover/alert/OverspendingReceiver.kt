package com.example.moneylover.alert

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.moneylover.data.repository.TransactionRepository
import com.example.moneylover.data.repository.WalletRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class OverspendingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val uid = intent.getStringExtra("uid") ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val walletRepository = WalletRepository(context.applicationContext as Application)
            val transactionRepository = TransactionRepository(context.applicationContext as Application)

            val (startMonth, endMonth) = getMonthStartAndEnd()

            val wallet = walletRepository.getWalletFromRoomByUid(uid)
            val totalExpense = transactionRepository.calculateTotalExpenses(startMonth, endMonth)

            if (wallet != null && totalExpense != null && wallet.limitAmount > 0 && totalExpense > wallet.limitAmount) {
                val percent = ((totalExpense - wallet.limitAmount) / wallet.limitAmount) * 100
                val msg = "Tháng này bạn đã chi tiêu vượt mức ${"%.0f".format(percent)}%"
                OverspendingAlarmManager(context).showNotification(msg)
            }

            OverspendingAlarmManager(context).scheduleDailyAlarm(uid)
        }
    }

    private fun getMonthStartAndEnd(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }
}
