package com.example.calcul

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var editText1: EditText      // сумма счета
    private lateinit var editText2: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var sliderValueText: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var buttonShow: Button
    private lateinit var rootLayout: android.view.View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Привязка элементов
        editText1 = findViewById(R.id.editText1)
        editText2 = findViewById(R.id.editText2)
        seekBar = findViewById(R.id.seekBar)
        sliderValueText = findViewById(R.id.sliderValueText)
        radioGroup = findViewById(R.id.radioGroup)
        buttonShow = findViewById(R.id.buttonShow)
        rootLayout = findViewById(R.id.rootLayout)   // LinearLayout, но подходит как View

        // Настройка слайдера с шагом 5% и диапазоном 0-25%
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Округляем прогресс до ближайшего числа, кратного 5
                val roundedPercent = getRoundedPercent(progress)
                if (roundedPercent != progress) {
                    seekBar?.setProgress(roundedPercent)
                }
                // Обновляем текстовую метку
                sliderValueText.text = "Процент чаевых: $roundedPercent%"

                // Получаем сумму из первого поля и вычисляем чаевые
                val amount = parseAmount(editText1.text.toString())
                if (amount == null) {
                    Snackbar.make(rootLayout, "Введите корректную сумму счета", Snackbar.LENGTH_SHORT).show()
                    return
                }
                val tipAmount = calculateTip(amount, roundedPercent)
                // Показываем Snackbar с суммой чаевых
                Snackbar.make(rootLayout, "Чаевые: ${String.format("%.2f", tipAmount)}", Snackbar.LENGTH_SHORT).show()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Кнопка для отображения всех данных (оставлена для совместимости)
        buttonShow.setOnClickListener {
            val input1 = editText1.text.toString()
            val input2 = editText2.text.toString()
            val sliderValue = getRoundedPercent(seekBar.progress)
            val selectedRadioId = radioGroup.checkedRadioButtonId
            val selectedOption = when (selectedRadioId) {
                R.id.radioOption1 -> "Опция 1"
                R.id.radioOption2 -> "Опция 2"
                R.id.radioOption3 -> "Опция 3"
                else -> "не выбрана"
            }
            val message = """
                Сумма счета: $input1
                Доп. данные: $input2
                Чаевые: $sliderValue%
                Выбор: $selectedOption
            """.trimIndent()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    // Функция округления прогресса до шага 5 (0, 5, 10, 15, 20, 25)
    private fun getRoundedPercent(progress: Int): Int {
        return (progress + 2) / 5 * 5  // целочисленное деление с округлением
    }

    // Функция безопасного преобразования строки в число (сумма счета)
    private fun parseAmount(input: String): Double? {
        return try {
            input.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    // Функция вычисления суммы чаевых
    private fun calculateTip(amount: Double, percent: Int): Double {
        return amount * percent / 100.0
    }
}