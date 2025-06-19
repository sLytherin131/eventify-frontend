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
            modifier = Modifier
                .padding(16.dp)
                .width(400.dp) // Lebar cukup agar tombol tidak dempet
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
                        backgroundColor = Color.White,
                        textColor = Color.Black
                    )
                )

                Spacer(Modifier.height(16.dp))

                Text("Type:", color = Color.White, modifier = Modifier.align(Alignment.Start))

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f) // Bagi ruang merata
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFEAEFC9))
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = type,
                                color = Color.White,
                                softWrap = false,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

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
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Add")
                }
            }
        }
    }
}
