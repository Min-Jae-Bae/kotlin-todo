package com.example.todo.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo.R
import com.example.todo.data.models.Priority
import com.example.todo.ui.theme.PRIORITY_DROP_DOWN_HEIGHT
import com.example.todo.ui.theme.PRIORITY_INDICATOR_SIZE

//PriorityDropDown - 속성(중요성, 중요성 선택)
@Composable
fun PriorityDropDown(
    priority: Priority,
    onPrioritySelected: (Priority) -> Unit,
) {
    // 확장 초기 상태를 false(default-닫힘) 지정
    /*animateFloatAsState
    * Compose에서 단일 값을 애니메이션 처리하는 가장 간단한 Animtation API
    * 최종 값(타겟 값)만 제공하면 현재 값에서 지정된 값으로 애니메이션 시작 가능*/
    var expanded by remember { mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    /*Row - 가로(수평으로 - x 값을 다르게)배치
    * clickable - 클릭 가능하게 만듬(누르면 확장)
    * border - 테두리 만듬(간격 1dp, 컬러 표면 색인데 추가적으로 사용할 수 없게 만듬
    * verticalAlignment - (x축은 이미 결정 됌) y축을 어떻게 배치할 것인지 결정, CenteerVertically - 레이아웃의 y축 중앙 결정*/
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(PRIORITY_DROP_DOWN_HEIGHT)
            .clickable { expanded = true }
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        /*Canvas
        * weight - 차지하는 비율
        * 도화지를 생성하고 속성 색깔에 맞는 동그라미를 그린다*/
        Canvas(
            modifier = Modifier
                .size(PRIORITY_INDICATOR_SIZE)
                .weight(1f)
        ) {
            drawCircle(color = priority.color)
        }
        /*Text
        * weight - 차지하는 비율*/
        Text(
            modifier = Modifier
                .weight(8f),
            text = priority.name,
            style = MaterialTheme.typography.subtitle2
        )
        /*IconButton - 버튼 공간의 틀을 만든다고 생각 & Icon과 짝궁 (Icon은 내용을 넣는다고 생각하면 됌)
        * alpha - 불투명도를 설정하는 공간
        * rotate - 버튼 모양을 회전
        * weight - 차지하는 비율
        * onClick - 클릭시 확장*/
        IconButton(
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .rotate(degrees = angle)
                .weight(1.5f),
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = stringResource(id = R.string.drop_down_arrow)
            )
        }
        /*DropdownMenu(확장, 다른 곳 클릭시 닫는 기능 제공)
        * MenuItem(클릭시 닫고, 선택한 것을 저장) - LOW, MEDIUM, HIGH*/
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.LOW)
                }
            ) {
                PriorityItem(priority = Priority.LOW)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.MEDIUM)
                }
            ) {
                PriorityItem(priority = Priority.MEDIUM)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onPrioritySelected(Priority.HIGH)
                }
            ) {
                PriorityItem(priority = Priority.HIGH)
            }
        }
    }
}

@Composable
@Preview
fun PriorityDropDownPreview() {
    PriorityDropDown(
        priority = Priority.LOW,
        onPrioritySelected = {}
    )
}