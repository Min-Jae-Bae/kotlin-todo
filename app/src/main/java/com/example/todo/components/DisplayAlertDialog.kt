package com.example.todo.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.R
import com.example.todo.ui.theme.Black
import com.example.todo.ui.theme.Purple200
import com.example.todo.ui.theme.White

/*DisplayAlertDialog - (제목, 메시지, 공개, 닫힘, Yes 클릭시)
* Dialog가 열려 있을 때 보여준다.
*
* AlertDialog
* title - Alert 제목 부분
* text - Alert 내용(body) 부분
* confirmButton - 확인 버튼 (확인 버튼 클릭 후 Dialog 종료)
* dismissButton - 취소 버튼 (Dialog 종료)
* onDismissRequest - Alert 외부를 터치 했을 때 (Dialog 종료)*/
@Composable
fun DisplayAlertDialog(
    title: String,
    message: String,
    openDialog: Boolean,
    closeDialog: () -> Unit,
    onYesClicked: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.h5.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.subtitle1.fontSize,
                    fontWeight = FontWeight.Normal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onYesClicked()
                        closeDialog()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Black,
                        contentColor = White
                    )
                ) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                Button(
                    onClick = { closeDialog() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Black,
                        contentColor = White
                    )
                )

                {
                    Text(text = stringResource(id = R.string.no))
                }
            },
            onDismissRequest = { closeDialog() }
        )
    }
}

@Composable
@Preview
private fun DisplayAlertDialogPreview() {
    DisplayAlertDialog(
        title = "Alert Dialog title",
        message = "Alert Dialog body",
        openDialog = true,
        closeDialog = {},
        onYesClicked = {}
    )
}