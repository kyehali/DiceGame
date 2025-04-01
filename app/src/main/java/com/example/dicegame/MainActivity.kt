package com.example.dicegame

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GUI()
        }
    }
}

// The alert dialog/ pop up window
@Composable
fun GUI() {
    // is the pop up window open?
    var openAboutDialog by remember { mutableStateOf(false) }
    var openSettingsDialog by remember { mutableStateOf(false) }
    var targetPoints by remember { mutableIntStateOf(101) } // Default target points
    var pointsInput by remember { mutableStateOf("101") }

    Column (
        modifier = Modifier.fillMaxSize()
            .padding(top = 200.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.dicepic), //added the dice roll image
            contentDescription = "Image description",
            modifier = Modifier
                .size(250.dp)
                .padding(2.dp)
        )


        Text("Welcome to the Dice game",
            fontSize = 27.sp,
            fontWeight = FontWeight .Bold
            )
        // Get the current context
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val i = Intent(context, NewGame::class.java)  //new intent that navigates to the newgame activity
            i.putExtra("Target_Points", targetPoints) //it passes the targetPoints variable as an extra parameter called "Target_Points"
            context.startActivity(i)
        }) {
            Text("New game")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { openAboutDialog = true }) {
            Text("About")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            openSettingsDialog = true
            pointsInput = targetPoints.toString()
        }) {
            Text("Settings")
        }
    }

    // About Dialog
    if (openAboutDialog) {   //referred to the lecture slides
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = "Example Icon")
            },
            title = {
                Text(text = "20230827/ Yehali Kossinna")
            },
            text = {
                Text(text = "I confirm that I understand what plagiarism is and have read and\n" +
                        "understood the section on Assessment Offences in the Essential\n" +
                        "Information for Students. The work that I have submitted is\n" +
                        "entirely my own. Any work from other authors is duly referenced\n" +
                        "and acknowledged.")
            },
            onDismissRequest = {
                openAboutDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openAboutDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openAboutDialog = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Simple Settings Dialog (styled like About dialog)
    if (openSettingsDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Settings, contentDescription = "Settings Icon")
            },
            title = {
                Text(text = "Game Settings")
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Target points to win (default: 101)")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pointsInput,
                        onValueChange = { pointsInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            onDismissRequest = {
                openSettingsDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pointsInput.toIntOrNull()?.let {
                            if (it > 0) targetPoints = it
                        }
                        openSettingsDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { openSettingsDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}