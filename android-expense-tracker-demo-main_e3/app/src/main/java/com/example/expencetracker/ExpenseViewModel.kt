package com.example.expencetracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

class ExpenseViewModel(
    private val repo: ExpenseRepository,
    private val incomeRepo: IncomeRepository
) : ViewModel() {

    var currentMonth by mutableStateOf(YearMonth.now())
    var monthlyBudget by mutableStateOf(2000.0)
    var savingGoal by mutableStateOf(0.0)

    fun expensesForMonth(): List<Expense> =
        repo.items.sortedByDescending { it.occurredAt }

    fun loadMonth(month: YearMonth, zone: ZoneId) {
        viewModelScope.launch {
            repo.loadMonth(month, zone)
            incomeRepo.loadMonth(month, zone)
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        date: LocalDateTime,
        zone: ZoneId
    ) {
        viewModelScope.launch {
            repo.add(
                Expense(
                    title = title,
                    amount = amount,
                    category = category,
                    occurredAt = date
                )
            )
            repo.loadMonth(currentMonth, zone)
        }
    }

    fun deleteExpense(id: String, zone: ZoneId) {
        viewModelScope.launch {
            repo.delete(id)
            repo.loadMonth(currentMonth, zone)
        }
    }

    fun updateExpense(expense: Expense, zone: ZoneId) {
        viewModelScope.launch {
            repo.update(expense)
            repo.loadMonth(currentMonth, zone)
        }
    }

    suspend fun monthlyTotal(month: YearMonth, zone: ZoneId): Double {
        return repo.monthlyTotal(month, zone)
    }

    suspend fun totalsByCategory(month: YearMonth, zone: ZoneId): List<CategoryTotal> {
        return repo.totalsByCategory(month, zone)
    }





    fun incomesForMonth(): List<Income> =
        incomeRepo.items.sortedByDescending { it.occurredAt }


    fun addIncome(
        source: String,
        amount: Double,
        date: LocalDateTime,
        zone: ZoneId
    ) {
        viewModelScope.launch {
            incomeRepo.add(
                Income(
                    source = source,
                    amount = amount,
                    occurredAt = date
                )
            )
            incomeRepo.loadMonth(currentMonth, zone)
        }
    }




    fun deleteIncome(id: String, zone: ZoneId) {
        viewModelScope.launch {
            incomeRepo.delete(id)
            incomeRepo.loadMonth(currentMonth, zone)
        }
    }

    fun updateIncome(income: Income, zone: ZoneId) {
        viewModelScope.launch {
            incomeRepo.update(income)
            incomeRepo.loadMonth(currentMonth, zone)
        }
    }


    suspend fun grandTotalExpense(zone: ZoneId, fromMonth: YearMonth = YearMonth.of(2023,1)): Double {
        val now = YearMonth.now()
        var total = 0.0
        var month = fromMonth
        while (!month.isAfter(now)) {
            total += monthlyTotal(month, zone)
            month = month.plusMonths(1)
        }
        return total
    }

    suspend fun totalIncome(month: YearMonth, zone: ZoneId): Double {
        return incomeRepo.monthlyTotal(month, zone)
    }

    suspend fun grandTotalIncome(zone: ZoneId, fromMonth: YearMonth = YearMonth.of(2023,1)): Double {
        val now = YearMonth.now()
        var total = 0.0
        var month = fromMonth
        while (!month.isAfter(now)) {
            total += totalIncome(month, zone)
            month = month.plusMonths(1)
        }
        return total
    }


}
