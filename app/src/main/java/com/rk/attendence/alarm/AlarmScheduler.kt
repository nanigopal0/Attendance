package com.rk.attendence.alarm

import java.time.LocalDate

interface AlarmScheduler {
    fun schedule(localDate: LocalDate)
}