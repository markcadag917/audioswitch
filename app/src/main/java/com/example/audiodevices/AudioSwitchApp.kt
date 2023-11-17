package com.example.audiodevices

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch




class AudioSwitchApp() : AppCompatActivity() {

    private lateinit var spinnerAudioOutput: Spinner
    private lateinit var buttonPlay: Button

    private  lateinit var adapter: ArrayAdapter<AudioDevice>
    private  lateinit var audioSwitch: AudioSwitch
    private lateinit var mediaPlayer: MediaPlayer
    private  var selectedDevice: AudioDevice? = null

    private var audioOptions = mutableListOf<AudioDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        spinnerAudioOutput = findViewById(R.id.audio_output_spinner)
        buttonPlay = findViewById(R.id.button_play)

        adapter = AudioOptionAdapter(this, audioOptions)
        spinnerAudioOutput.adapter = adapter

        audioSwitch = AudioSwitch(applicationContext, loggingEnabled = true)

        audioSwitch.start { audioDevices, selectedAudioDevice ->
            this.audioOptions.clear()
            this.audioOptions.addAll(audioDevices)
            this.selectedDevice = selectedAudioDevice
            adapter.notifyDataSetChanged()
        }

        buttonPlay.setOnClickListener {
            mediaPlayer = MediaPlayer.create(this, R.raw.sample_data)
            mediaPlayer.start()
        }

        setupSpinner()
    }



    private fun setupSpinner() {
        spinnerAudioOutput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val audioDevice = audioOptions[position]
                audioSwitch.selectDevice(audioDevice)
                audioSwitch.activate()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioSwitch.stop()
    }
}

class AudioOptionAdapter(context: Context, private val options: MutableList<AudioDevice>)
    : ArrayAdapter<AudioDevice>(context, android.R.layout.simple_spinner_item, options) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        view.findViewById<TextView>(android.R.id.text1).text = getItem(position)?.name
        return view
    }
}