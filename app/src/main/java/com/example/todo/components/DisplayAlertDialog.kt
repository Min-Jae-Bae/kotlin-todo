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