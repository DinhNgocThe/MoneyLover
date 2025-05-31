package com.example.moneylover.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.example.moneylover.R
import com.example.moneylover.data.room.model.User
import com.example.moneylover.databinding.FragmentHomeBinding
import com.example.moneylover.viewmodel.TransactionViewModel
import com.example.moneylover.viewmodel.UserViewModel
import com.example.moneylover.viewmodel.WalletViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var user: User
    private lateinit var binding: FragmentHomeBinding
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(
            this,
            UserViewModel.UserViewModelFactory(requireContext().applicationContext as Application)
        )[UserViewModel::class.java]
    }
    private val walletViewModel: WalletViewModel by lazy {
        ViewModelProvider(
            this,
            WalletViewModel.WalletViewModelFactory(requireContext().applicationContext as Application)
        )[WalletViewModel::class.java]
    }
    private val transactionViewModel: TransactionViewModel by lazy {
        ViewModelProvider(
            this,
            TransactionViewModel.TransactionViewModelFactory(requireContext().applicationContext as Application)
        )[TransactionViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserInformation()
        loadWalletInformation()
        editBalance()
        initBarChart()
    }

    private fun loadUserInformation() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                user = userViewModel.getUserFromRoomByUid(firebaseAuth.currentUser?.uid.toString())!!
            }
            binding.txtDisplayNameHomeFragment.text = user.displayName
            Glide.with(requireContext())
                .load(user.photoUrl)
                .signature(ObjectKey(user.photoUrl))
                .placeholder(R.drawable.img_default_user_photo)
                .error(R.drawable.img_default_user_photo)
                .into(binding.imgPhotoHomeFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWalletInformation() {
        walletViewModel.loadWalletFromRoomByUid(firebaseAuth.currentUser?.uid.toString())
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                walletViewModel.wallet.collect { value ->
                    value?.let {
                        binding.txtBalanceHomeFragment.text = formatCurrency(it.balance) + " " + getString(R.string.vnd)
                    }
                }
            }
        }
    }

    private fun editBalance() {
        binding.btnEditBalanceHomeFragment.setOnClickListener {
            val intent = Intent(this.requireContext(), EditBalanceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun formatCurrency(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN"))
        return DecimalFormat("#,###", symbols).format(value)
    }

    @SuppressLint("SetTextI18n")
    private fun initBarChart() {
        val now = Calendar.getInstance()
        val thisMonthStart = getStartOfMonthTimestamp(now)
        val thisMonthEnd = getEndOfMonthTimestamp(now)
        now.add(Calendar.MONTH, -1)
        val lastMonthStart = getStartOfMonthTimestamp(now)
        val lastMonthEnd = getEndOfMonthTimestamp(now)

        var percent = 0
        transactionViewModel.getMonthlyExpenses(lastMonthStart, lastMonthEnd, false)
        transactionViewModel.getMonthlyExpenses(thisMonthStart, thisMonthEnd, true)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    transactionViewModel.lastMonthExpenses,
                    transactionViewModel.thisMonthExpenses
                ) { lastMonth, thisMonth ->
                    listOf(lastMonth ?: 0f, thisMonth ?: 0f)
                }.collect { expenseList ->
                    // expenseList[0] is lastMonth, [1] is thisMonth
                    binding.txtAmountHomeFragment.text = formatCurrency(expenseList[1].toDouble()) + " " + getString(R.string.vnd)

                    if (expenseList[0] > expenseList[1]) {
                        percent = ((expenseList[0] - expenseList[1]) / expenseList[0] * 100).toInt()
                        binding.imgPercentHomeFragment.setImageResource(R.drawable.ic_decrease)
                        binding.txtPercentHomeFragment.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
                        binding.txtPercentHomeFragment.text = percent.toString() + getString(R.string.percent)
                    } else {
                        percent = ((expenseList[1] - expenseList[0]) / expenseList[0] * 100).toInt()
                        binding.imgPercentHomeFragment.setImageResource(R.drawable.ic_increase)
                        binding.txtPercentHomeFragment.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                        binding.txtPercentHomeFragment.text = percent.toString() + getString(R.string.percent)
                    }

                    val data = listOf(
                        getString(R.string.last_month) to expenseList[0],
                        getString(R.string.this_month) to expenseList[1]
                    )
                    binding.barChartHomeFragment.animation.duration = 1000L
                    binding.barChartHomeFragment.animate(data)
                }
            }
        }
    }

     private fun getStartOfMonthTimestamp(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getEndOfMonthTimestamp(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

}
