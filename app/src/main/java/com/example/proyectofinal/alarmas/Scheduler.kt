package com.example.proyectofinal.alarmas

import java.time.LocalDateTime
// las funciones para cuando se programa la alamra
interface AlarmScheduler {
    //para la alarma nueva
    fun schedule(alarmItem: AlarmItem)
    fun cancel(alarmItem: AlarmItem)
    fun edit(alarmItem: AlarmItem, newAlarmTime: LocalDateTime)
}