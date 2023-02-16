package com.example.todo.data.models

import androidx.compose.ui.graphics.Color
import com.example.todo.ui.theme.HighPriorityColor
import com.example.todo.ui.theme.LowPriorityColor
import com.example.todo.ui.theme.MediumPriorityColor
import com.example.todo.ui.theme.NonePriorityColor

enum class Priority(val color: Color) {
    HIGH(HighPriorityColor),
    MEDIUM(MediumPriorityColor),
    LOW(LowPriorityColor),
    NONE(NonePriorityColor)
}