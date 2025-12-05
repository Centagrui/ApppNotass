package com.example.proyectofinal.alarmas

import java.time.LocalDateTime
// estructura de datos para la alarma
data class AlarmItem(
    val idAlarma: String,
    val taskId: String,
    val alarmTime: String,
    val message: String
)
// karla aqui ya puse para cada alarma
// el ultimo es para la notificacion que va a mostar
