package com.example.composeTutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeTutorial.ui.theme.AndroidBookPracticeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidBookPracticeTheme {
                Box(Modifier.safeContentPadding()) {
                    Image(painterResource(R.drawable.ic_launcher_background), null)
                }

            }
        }
//        startActivity(Intent(this, SecondActivity::class.java))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Column(
        Modifier
            .padding(3.dp)
            .fillMaxSize()
    ) { }
    Column() { }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidBookPracticeTheme {
        Greeting("Android")
    }
}

@Preview
@Composable
private fun CustomCheckbox() {
    var checked by remember { mutableStateOf(value = true) }
    Checkbox(checked, onCheckedChange = { newValue -> checked = newValue })
}