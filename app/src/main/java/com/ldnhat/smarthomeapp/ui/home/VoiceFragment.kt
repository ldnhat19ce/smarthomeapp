package com.ldnhat.smarthomeapp.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ldnhat.smarthomeapp.R
import com.ldnhat.smarthomeapp.data.network.Resource
import com.ldnhat.smarthomeapp.databinding.FragmentVoiceBinding
import com.ldnhat.smarthomeapp.ui.handleApiError
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.lang3.RegExUtils
import java.util.*

@AndroidEntryPoint
class VoiceFragment : Fragment() {
    private val viewModel by viewModels<VoiceViewModel>()
    private lateinit var speechRecognizer: SpeechRecognizer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentVoiceBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_voice, container, false)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
                Log.d("BEGIN_SPEECH", "Begin speech")
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(results: Bundle?) {
                Log.d(
                    "SPEECH_RESULT",
                    results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString()
                )
                val messageRequest: String =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).toString()
                Log.d("HANDLE_MESSAGE_REQUEST", RegExUtils.removeAll(messageRequest, "[\\[\\]]"))
                viewModel.handleMessageRequest(
                    RegExUtils.removeAll(messageRequest, "[\\[\\]]").trim()
                )
                viewModel.speechData.observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            Toast.makeText(
                                requireContext(),
                                it.value.messageResponse,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Failure -> handleApiError(it) {
                        }
                        else -> {}
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {

            }
        })

        binding.voice.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                speechRecognizer.startListening(intent)
            }
            false
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}