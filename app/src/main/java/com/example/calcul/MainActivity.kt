package com.example.calcul

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var sliderValueText: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var buttonShow: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText1 = findViewById(R.id.editText1)
        editText2 = findViewById(R.id.editText2)
        seekBar = findViewById(R.id.seekBar)
        sliderValueText = findViewById(R.id.sliderValueText)
        radioGroup = findViewById(R.id.radioGroup)
        buttonShow = findViewById(R.id.buttonShow)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sliderValueText.text = "Текущее значение: $progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        buttonShow.setOnClickListener {
            val input1 = editText1.text.toString()
            val input2 = editText2.text.toString()
            val sliderValue = seekBar.progress

            val selectedRadioId = radioGroup.checkedRadioButtonId
            val selectedOption = when (selectedRadioId) {
                R.id.radioOption1 -> "Опция 1"
                R.id.radioOption2 -> "Опция 2"
                R.id.radioOption3 -> "Опция 3"
                else -> "не выбрана"
            }

            val message = """
                Данные 1: $input1
                Данные 2: $input2
                Слайдер: $sliderValue
                Выбор: $selectedOption
            """.trimIndent()

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}