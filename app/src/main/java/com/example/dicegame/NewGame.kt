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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.font.FontWeight

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

//To create 5 unique numbers
fun generateDiceNumbers(): List<Int> {
    val numbers = mutableListOf<Int>()
    while (numbers.size < 5) {
        val new_number = 1 + Random.nextInt(6)
        if (new_number !in numbers) //only add if it's not in the list
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

@Composable
fun GUI2(uri: Uri? = null,targetPoints: Int = 101) {
    var humanResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var computerResults by remember { mutableStateOf<List<Int>>(emptyList()) }
    var humanScore by remember { mutableStateOf(0) }
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

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(vertical = 50.dp)
    ) {
        // First row (scores)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "H : $humanTotalWins | C : $computerTotalWins", //display the total wins of both computer and human
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                "Player: $humanScore | Computer: $computerScore", //display the total scores of computer and player
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Second row (dice results) - using a single Column with fixed height
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(800.dp)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (humanResults.isEmpty()) Arrangement.Center else Arrangement.Top // Center content when empty, align to top when showing results
        ) {

            if (humanResults.isEmpty()) {
                Text(
                    "Let's Play",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.size(16.dp))

                Button( //throw button
                    onClick = {
                        humanResults = generateDiceNumbers()
                        computerResults = generateDiceNumbers()
                        rollCount++
                        rerollCount = 0
                        hasScored = false
                    }
                ) {
                    Text("Throw")
                }
            }

            // Player results shown only when results available
            if (humanResults.isNotEmpty()) {
                Text(
                    "Player Results: ${humanResults.joinToString(" ")}",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.size(10.dp))
                //display dice images
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    humanResults.forEachIndexed { index, diceValue ->
                        val isSelected = index in selectedDiceIndices
                        //get the correct dice image based on the value
                        val imageId =
                            when (diceValue) {
                                1 -> R.drawable.one
                                2 -> R.drawable.two
                                3 -> R.drawable.three
                                4 -> R.drawable.four
                                5 -> R.drawable.five
                                6 -> R.drawable.six
                                else -> R.drawable.one
                            }
                        //card containing dice images with slection
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

                // show computers dice results when its not empty
                if (computerResults.isNotEmpty()) {
                    Text("Computer Results: ${computerResults.joinToString(" ")}", fontSize = 18.sp)

                    Spacer(modifier = Modifier.size(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in computerResults) {
                            val imageId = when (i) {
                                1 -> R.drawable.one
                                2 -> R.drawable.two
                                3 -> R.drawable.three
                                4 -> R.drawable.four
                                5 -> R.drawable.five
                                6 -> R.drawable.six
                                else -> R.drawable.one
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

                // To show the count and reroll count  in each throw
                if (rollCount > 0) {
                    Text(
                        "Roll count is $rollCount | Reroll count is $rerollCount",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.size(10.dp))
                }
                //to show the tie message
                if (humanScore == computerScore && rollCount > 0 && (humanScore >= targetPoints && computerScore >= targetPoints)) {
                    Text("The game is tied. Throw again!")

                    Spacer(modifier = Modifier.size(10.dp))
                }


                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {  //throw button showed in thr first screen
                    if (rollCount == 0 || hasScored || (humanScore == computerScore && humanScore > targetPoints)) {
                        Button(
                            onClick = {
                                humanResults = generateDiceNumbers() //generate new dice for both players
                                computerResults = generateDiceNumbers()
                                rollCount++
                                rerollCount = 0
                                hasScored = false
                                selectedDiceIndices = emptyList()
                                tempSelectedIndices = emptyList()

                                if (humanScore == computerScore && humanScore > targetPoints) {
                                    humanScore += humanResults.sum()
                                    computerScore += computerResults.sum()

                                    //check if te tie is not there anymore
                                    if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)) {
                                        humanWon =
                                            humanScore >= targetPoints && humanScore > computerScore

                                        //update win counts
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
                        } //reroll button
                    } else if (!hasScored && rollCount > 0 && rerollCount < 2) {
                        Button(
                            onClick = {
                                if (rerollCount < 2) {  // Initialize new result
                                    val newHumanResults = MutableList(humanResults.size) { -1 }

                                    selectedDiceIndices.forEach { index -> //keep selected dice values
                                        newHumanResults[index] = humanResults[index]
                                    }

                                    // Generate new values for unselected dice
                                    val keptValues = selectedDiceIndices.map { humanResults[it] }
                                    var index = 0
                                    while (newHumanResults.contains(-1)) {
                                        val position = newHumanResults.indexOfFirst { it == -1 }
                                        val newValue =
                                            generateNewDiceValue(keptValues + newHumanResults.filter { it != -1 })
                                        newHumanResults[position] = newValue
                                        index++

                                        if (index > 100) break //to stop infinite loops
                                    }
                                    //update dice values and crement reroll count
                                    humanResults = newHumanResults.toList()
                                    computerResults = generateDiceNumbers()
                                    rerollCount++
                                    //clear it before the next round
                                    selectedDiceIndices = emptyList()
                                    tempSelectedIndices = emptyList()
                                }
                                //auto score after second reroll
                                if (rerollCount == 2) {
                                    humanScore += humanResults.sum()
                                    computerScore += computerResults.sum()
                                    //check the winner
                                    if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)) {
                                        humanWon =
                                            humanScore >= targetPoints && humanScore > computerScore
                                        ///update the win count
                                        if (humanWon) {
                                            humanTotalWins++
                                        } else {
                                            computerTotalWins++
                                        }

                                        openWinDialog = true
                                    }
                                    //clear for the next roll
                                    humanResults = emptyList()
                                    computerResults = emptyList()
                                    hasScored = true
                                    selectedDiceIndices = emptyList()
                                    tempSelectedIndices = emptyList()
                                }
                            }
                        ) {
                            Text("Reroll")
                        }
                    }

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

                    if (!(humanScore == computerScore && humanScore > targetPoints) && !hasScored && rollCount > 0) {
                        Button(
                            onClick = {
                                if (humanResults.isNotEmpty() && computerResults.isNotEmpty()) {
                                    humanScore += humanResults.sum()
                                    computerScore += computerResults.sum()

                                    if (((humanScore >= targetPoints && humanScore > computerScore) || computerScore >= targetPoints) && !(computerScore == humanScore)) {
                                        humanWon =
                                            humanScore >= targetPoints && humanScore > computerScore

                                        if (humanWon) {
                                            humanTotalWins++
                                        } else {
                                            computerTotalWins++
                                        }
                                        openWinDialog = true
                                    }

                                    humanResults = emptyList()
                                    computerResults = emptyList()
                                    hasScored = true
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
    }

    // Win dialog
    if (openWinDialog) {
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = "Example Icon")
            },
            title = {
                Text(
                    text = if (humanWon) "You Win!" else "You Lose",
                    fontSize = 35.sp,
                    color = if (humanWon) Color.Green else Color.Red
                )
            },
            text = {
                Text(
                    text = "Player: $humanScore | Computer: $computerScore",
                    fontSize = 16.sp
                )
            },
            onDismissRequest = {
                openWinDialog = false //resetting it before starting the new one
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

    // Selection dialog
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
                    //row of dice with checkboxes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        humanResults.forEachIndexed { index, diceValue ->
                            val isSelected = index in tempSelectedIndices
                            val selectionAllowed = isSelected || tempSelectedIndices.size < 4 //only can slect upto 4 dices

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
                                //card with dice images
                                Card(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) Color.Green else Color.Transparent
                                        )
                                        .padding(2.dp),
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
                                            tempSelectedIndices + index // Add to selection if checked and under limit
                                        } else if (!checked) {
                                            tempSelectedIndices - index// Remove from selection if unchecked
                                        } else {
                                            tempSelectedIndices// Keep current selection if limit reached
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
}
