package com.example.dicegame

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.foundation.border

class NewGame:ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri: Uri? = intent.data
        val targetPoints = intent.getIntExtra("Target_Points", 101)
        setContent{
            GUI2(uri,targetPoints)
        }
    }
}

@Composable
fun GUI2(uri: Uri? = null,targetPoints: Int = 101){
    var humanResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var computerResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var humanScore by remember { mutableStateOf(0)}
    var computerScore by remember { mutableStateOf(0) }
    var rollCount by remember { mutableStateOf(0) }
    var rerollCount by remember { mutableStateOf(0) }
    var hasScored by remember { mutableStateOf(false) }
    var openWinDialog by remember { mutableStateOf(false) }
    var humanWon by remember { mutableStateOf(false) }
    var humanTotalWins by remember { mutableStateOf(0) }
    var computerTotalWins by remember { mutableStateOf(0) }
    var selectedDiceIndices by remember { mutableStateOf<List<Int>>(emptyList()) }
    var openSelectionDialog by remember { mutableStateOf(false) }
    var tempSelectedIndices by remember { mutableStateOf<List<Int>>(emptyList()) }

    // pop up window that says the message whether if they won or lose
    if (openWinDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = "Example Icon")
            },
            title = {
                Text(
                    text = if (humanWon) "You Win!"  //checking if player won or not
                    else "You Lose",
                    fontSize = 35.sp,
                    color = if (humanWon) Color.Green
                    else Color.Red     //Changing the colour according to the result
                )
            },
            text = {
                Text(
                    text = "Player: $humanScore | Computer: $computerScore",
                    fontSize = 16.sp
                )

            },
            // the process when you click outside the dialog
            onDismissRequest = {
                openWinDialog = false
                humanResults = emptyList()
                computerResults = emptyList()
                humanScore = 0
                computerScore = 0
                rerollCount = 0
                rollCount = 0
                selectedDiceIndices = emptyList()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openWinDialog = false
                        humanResults = emptyList()
                        computerResults = emptyList()
                        humanScore = 0
                        computerScore = 0
                        rerollCount = 0
                        rollCount = 0
                        selectedDiceIndices = emptyList()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    // pop up window that shows the dice selection process
    if (openSelectionDialog) {
        AlertDialog(
            title = { Text("Select Dice to Keep ", fontSize = 18.sp) },
            text = {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Check the dice you want to keep for the next roll:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.size(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        humanResults.forEachIndexed { index, diceValue ->
                            val isSelected = index in tempSelectedIndices
                            val selectionAllowed = isSelected || tempSelectedIndices.size < 4

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                val imageId = when(diceValue) {
                                    1 -> R.drawable.one
                                    2 -> R.drawable.two
                                    3 -> R.drawable.three
                                    4 -> R.drawable.four
                                    5 -> R.drawable.five
                                    6 -> R.drawable.six
                                    else -> R.drawable.one
                                }

                                Card(
                                    modifier = Modifier
                                        .size(45.dp)  // use the smaller dices cuz to manage the soace un pop up window
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) Color.Green else Color.Transparent
                                        )
                                        .padding(2.dp),  //reduced padding for the same reason also
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isSelected) 8.dp else 4.dp
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(id = imageId),
                                        contentDescription = "Dice showing $diceValue",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        tempSelectedIndices = if (checked && selectionAllowed) {
                                            tempSelectedIndices + index
                                        } else if (!checked) {
                                            tempSelectedIndices - index
                                        } else {
                                            tempSelectedIndices
                                        }
                                    },
                                    enabled = selectionAllowed,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color.Green
                                    )
                                )
                            }
                        }
                    }

                    if (tempSelectedIndices.size == 4) {
                        Text(
                            text = "Maximum 4 dice can be selected",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Text(
                        text = "Selected: ${tempSelectedIndices.size}/4",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            onDismissRequest = {
                openSelectionDialog = false
                tempSelectedIndices = selectedDiceIndices
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDiceIndices = tempSelectedIndices
                        openSelectionDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openSelectionDialog = false
                        tempSelectedIndices = selectedDiceIndices
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            // win counter of the computer andplayer displays on the top left side
            Text(
                "H : $humanTotalWins | C : $computerTotalWins",
                fontSize = 18.sp
            )

            //player and computer scores displaying on the top right side
            Text(
                "Player: $humanScore | Computer: $computerScore",
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.size(16.dp))

        Text("Let's Play",
            fontSize = 25.sp)
        Spacer(modifier = Modifier.size(10.dp))

        if (humanResults.isNotEmpty()) {
            Text("Player Results: ${humanResults.joinToString (" ") }", fontSize = 18.sp) // make it into a string and then seperate
            Spacer(modifier = Modifier.size(10.dp))

            //displaying the dice images for player results
            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                humanResults.forEachIndexed { index, diceValue ->
                    val isSelected = index in selectedDiceIndices
                    val imageId = when(diceValue) {  //getting the image based on the number
                        1 -> R.drawable.one
                        2 -> R.drawable.two
                        3 -> R.drawable.three
                        4 -> R.drawable.four
                        5 -> R.drawable.five
                        6 -> R.drawable.six
                        else -> R.drawable.one // Fallback
                    }

                    Card(
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) Color.Green else Color.Transparent
                            ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (isSelected) 8.dp else 2.dp
                        )
                    ) {
                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = "Dice showing $diceValue",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        if (computerResults.isNotEmpty()) {
            Text("Computer Results: ${computerResults.joinToString(" ")}", fontSize = 18.sp)
            Spacer(modifier = Modifier.size(10.dp))
            //displaying the dice images for computer results
            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in computerResults) {
                    val imageId = when(i) {  //getting the image based on the number
                        1-> R.drawable.one
                        2-> R.drawable.two
                        3-> R.drawable.three
                        4-> R.drawable.four
                        5-> R.drawable.five
                        6-> R.drawable.six
                        else -> R.drawable.one // Fallback
                    }
                    Card(
                        modifier = Modifier.size(50.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = "Dice showing $i",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        // To show the count in each throw
        if (rollCount > 0) {
            Text("Roll count is $rollCount | Reroll count is $rerollCount", fontSize = 16.sp)
            Spacer(modifier = Modifier.size(10.dp))
        }

        if (humanScore == computerScore && rollCount > 0 && (humanScore >= targetPoints && computerScore >= targetPoints) ) {
            Text("The game is tied. Throw again!")
            Spacer(modifier = Modifier.size(10.dp))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Throw button
            if (rollCount == 0 || hasScored || (humanScore == computerScore && humanScore > targetPoints)) {
                Button(
                    onClick = {
                        humanResults = generateDiceNumbers()
                        computerResults = generateDiceNumbers()
                        rollCount++
                        rerollCount = 0
                        hasScored = false
                        selectedDiceIndices = emptyList()
                        tempSelectedIndices = emptyList()

                        if (humanScore == computerScore && humanScore > targetPoints) {
                            humanScore += humanResults.sum()
                            computerScore += computerResults.sum()

                            //checking the winnner
                            if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)) {
                                humanWon = humanScore >= targetPoints && humanScore > computerScore

                                // getting the total wins count to update
                                if (humanWon) {
                                    humanTotalWins++
                                } else {
                                    computerTotalWins++
                                }
                                openWinDialog = true
                            }
                            hasScored = true
                        }
                    },
                ) {
                    Text("Throw")
                }
            } else if (!hasScored && rollCount > 0 && rerollCount < 2) {
                // Reroll button that respects selected dice
                Button(
                    onClick = {
                        if (rerollCount < 2) {
                            // Keeeping the selected ones and generate new once for the not sleected ones
                            val newHumanResults = MutableList(humanResults.size) { -1 }

                            // First add the dice we want to keep
                            selectedDiceIndices.forEach { index ->
                                newHumanResults[index] = humanResults[index]
                            }

                            // Now filling the rest with new values
                            val keptValues = selectedDiceIndices.map { humanResults[it] }
                            var index = 0
                            while (newHumanResults.contains(-1)) {
                                val position = newHumanResults.indexOfFirst { it == -1 }
                                val newValue = generateNewDiceValue(keptValues + newHumanResults.filter { it != -1 })
                                newHumanResults[position] = newValue
                                index++

                                // To top the infinite loop from happening
                                if (index > 100) break
                            }

                            humanResults = newHumanResults.toList()
                            computerResults = generateDiceNumbers()
                            rerollCount++

                            // Clear selection after reroll
                            selectedDiceIndices = emptyList()
                            tempSelectedIndices = emptyList()
                        }

                        if (rerollCount == 2) {
                            humanScore += humanResults.sum()
                            computerScore += computerResults.sum()

                            //checking the winnner
                            if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)){
                                humanWon = humanScore >= targetPoints && humanScore > computerScore

                                // getting the total wins count to update
                                if (humanWon) {
                                    humanTotalWins++
                                } else {
                                    computerTotalWins++
                                }

                                openWinDialog = true
                            }

                            //reset for the next roll
                            humanResults = emptyList()
                            computerResults = emptyList()
                            hasScored = true // after scoring it sets to true
                            selectedDiceIndices = emptyList()
                            tempSelectedIndices = emptyList()
                        }
                    }
                ) {
                    Text("Reroll")
                }
            }

            // Select dice button
            if (!hasScored && rollCount > 0 && rerollCount < 2 && !(humanScore == computerScore && humanScore > targetPoints)) {
                Button(
                    onClick = {
                        tempSelectedIndices = selectedDiceIndices.toList()
                        openSelectionDialog = true
                    }
                ) {
                    Text("Select Dice")
                }
            }

            // Score Button
            if (!(humanScore == computerScore && humanScore > targetPoints) && !hasScored && rollCount > 0) {
                Button(
                    onClick = {
                        if (humanResults.isNotEmpty() && computerResults.isNotEmpty()) {
                            // adding the new results to the current results
                            humanScore += humanResults.sum()
                            computerScore += computerResults.sum()

                            // checking the winnner
                            if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)) {
                                humanWon = humanScore >= targetPoints && humanScore > computerScore
                                // getting the total wins count to update
                                if (humanWon) {
                                    humanTotalWins++
                                } else {
                                    computerTotalWins++
                                }
                                openWinDialog = true
                            }

                            // reset for the next roll
                            humanResults = emptyList()
                            computerResults = emptyList()
                            hasScored = true // after scoring it sets to true
                            selectedDiceIndices = emptyList()
                            tempSelectedIndices = emptyList()
                        }
                    },
                    enabled = humanResults.isNotEmpty()
                ) {
                    Text("Score")
                }
            }
        }
    }
}

fun generateDiceNumbers(): List<Int> {
    val numbers = mutableListOf<Int>()
    while (numbers.size < 5) {
        val new_number = 1 + Random.nextInt(6)
        if (new_number !in numbers)
            numbers.add(new_number)
    }
    return numbers
}

// Generate a single new dice value that doesn't conflict with existing ones
fun generateNewDiceValue(existingValues: List<Int>): Int {
    var newValue: Int
    do {
        newValue = 1 + Random.nextInt(6)
    } while (newValue in existingValues)
    return newValue
}