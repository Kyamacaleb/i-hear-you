package com.example.i_hear_you

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.i_hear_you.ui.theme.I_hear_youTheme
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    private var color by mutableStateOf(Color.White)
    private var speech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speech = TextToSpeech(
            this
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                val result = speech!!.setLanguage(Locale.US)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.e("TTS", "The Language not supported!")
            }
        }
        setContent {
            I_hear_youTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(color, this)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            when (result?.get(0).toString()) {
                "blue" -> {
                    color = Color.Blue
                    speech!!.speak("Here is the blue screen", TextToSpeech.QUEUE_FLUSH, null, "")
                }
                "red" -> {
                    color = Color.Red
                    speech!!.speak("Here is the red screen", TextToSpeech.QUEUE_FLUSH, null, "")
                }
                else -> {
                    color = Color.White
                    speech!!.speak(
                        "You can only select two colors: blue or red",
                        TextToSpeech.QUEUE_FLUSH,
                        null
                    )
                }
            }
        }
    }

    public override fun onDestroy() {
        if (speech != null) {
            speech!!.stop()
            speech!!.shutdown()
        }
        super.onDestroy()
    }

    @Composable
    fun MainScreen(color: Color, activity: MainActivity) {
        val animatedColor = animateColorAsState(targetValue = color)
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedColor.value)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { getSpeechInput(activity.baseContext, activity) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic),
                        contentDescription = "Microphone icon"
                    )
                }
            }
        }
    }

    private fun getSpeechInput(context: Context, activity: MainActivity) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Speech not Available", Toast.LENGTH_SHORT).show()
            return
        }
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What color would you like to see?")

        startActivityForResult(activity, speechIntent, 101, null)
    }
}