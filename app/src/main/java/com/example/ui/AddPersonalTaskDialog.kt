package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddPersonalTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Personal") }
    val types = listOf("Personal", "Work", "Urgent")

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1B2A41),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Task Personal",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFFEAEFC9)
                )
                Spacer(Modifier.height(16.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Memo...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White
                    )
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Type:", color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    types.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEAEFC9))
                            )
                            Text(type, color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (description.isNotBlank()) {
                            onAddTask(description.trim(), selectedType)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF4B6587),
                        contentColor = Color(0xFFEAEFC9)
                    )
                ) {
                    Text("Add")
                }
            }
        }
    }
}
