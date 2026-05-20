package com.example.calcul

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextAmount: EditText   // сумма счета
    private lateinit var editTextDishes: EditText   // количество блюд
    private lateinit var editTextExtra: EditText    // доп. данные
    private lateinit var seekBar: SeekBar
    private lateinit var sliderValueText: TextView
    private lateinit var radioGroupDiscount: RadioGroup
    private lateinit var discountAmountText: TextView
    private lateinit var totalAfterDiscountText: TextView
    private lateinit var finalTotalText: TextView
    private lateinit var buttonShow: Button
    private lateinit var rootLayout: android.widget.LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Привязка элементов
        editTextAmount = findViewById(R.id.editText1)
        editTextDishes = findViewById(R.id.editTextDishes)
        editTextExtra = findViewById(R.id.editText2)
        seekBar = findViewById(R.id.seekBar)
        sliderValueText = findViewById(R.id.sliderValueText)
        radioGroupDiscount = findViewById(R.id.radioGroupDiscount)
        discountAmountText = findViewById(R.id.discountAmountText)
        totalAfterDiscountText = findViewById(R.id.totalAfterDiscountText)
        finalTotalText = findViewById(R.id.finalTotalText)
        buttonShow = findViewById(R.id.buttonShow)
        rootLayout = findViewById(R.id.rootLayout)

        // Настройка слайдера (чаевые)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val roundedPercent = getRoundedPercent(progress)
                if (roundedPercent != progress) {
                    seekBar?.progress = roundedPercent
                }
                sliderValueText.text = getString(R.string.tip_percent, roundedPercent)
                updateTotals()   // пересчёт итогов при изменении чаевых
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Слушатель для поля "Количество блюд" – программный выбор радиокнопки и пересчёт
        editTextDishes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val dishesCount = parseDishesCount(s.toString())
                val discountPercent = getDiscountPercent(dishesCount)
                // Программно выбираем радиокнопку в зависимости от количества блюд
                selectDiscountRadioButton(discountPercent)
                // Показываем Snackbar с информацией о скидке
                if (dishesCount != null) {
                    Snackbar.make(rootLayout, "Количество блюд: $dishesCount, скидка: $discountPercent%", Snackbar.LENGTH_SHORT).show()
                } else if (s.toString().isNotEmpty()) {
                    Snackbar.make(rootLayout, "Введите корректное количество блюд", Snackbar.LENGTH_SHORT).show()
                }
                updateTotals()   // пересчёт итогов
            }
        })

        // Слушатель для поля суммы счета
        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateTotals()
            }
        })

        // Кнопка для отображения всех данных (оставлена для обратной совместимости)
        buttonShow.setOnClickListener {
            val amount = editTextAmount.text.toString()
            val dishes = editTextDishes.text.toString()
            val extra = editTextExtra.text.toString()
            val tipPercent = getRoundedPercent(seekBar.progress)
            val discountPercent = getDiscountPercent(parseDishesCount(dishes))
            val message = """
                Сумма счета: $amount
                Блюд: ${dishes.ifEmpty { "не указано" }}
                Скидка: $discountPercent%
                Чаевые: $tipPercent%
                Доп. данные: $extra
            """.trimIndent()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        // Изначально обновляем итоги (чтобы не было нулей)
        updateTotals()
    }

    // --- Функции расчёта скидки ---
    private fun getDiscountPercent(dishesCount: Int?): Int {
        return when (dishesCount) {
            null -> 0
            in 1..2 -> 3
            in 3..5 -> 5
            in 6..10 -> 7
            else -> if (dishesCount > 10) 10 else 0
        }
    }

    private fun parseDishesCount(input: String): Int? {
        return try {
            val count = input.toInt()
            if (count >= 0) count else null
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun applyDiscount(amount: Double, discountPercent: Int): Double {
        return amount * discountPercent / 100.0
    }

    private fun getTipAmount(amount: Double, tipPercent: Int): Double {
        return amount * tipPercent / 100.0
    }

    // Программный выбор радиокнопки на основе процента скидки
    private fun selectDiscountRadioButton(discountPercent: Int) {
        val radioButtonId = when (discountPercent) {
            3 -> R.id.radioDiscount1_2
            5 -> R.id.radioDiscount3_5
            7 -> R.id.radioDiscount6_10
            10 -> R.id.radioDiscountMore10
            else -> -1
        }
        if (radioButtonId != -1) {
            radioGroupDiscount.check(radioButtonId)
        } else {
            radioGroupDiscount.clearCheck() // если скидки нет (0%), снимаем выбор
        }
    }

    // --- Функции для слайдера (чаевые) ---
    private fun getRoundedPercent(progress: Int): Int {
        return (progress + 2) / 5 * 5
    }

    private fun parseAmount(input: String): Double? {
        return try {
            input.toDouble()
        } catch (_: NumberFormatException) {
            null
        }
    }

    // --- Основная логика пересчёта всех сумм ---
    private fun updateTotals() {
        val amount = parseAmount(editTextAmount.text.toString()) ?: 0.0
        val dishesCount = parseDishesCount(editTextDishes.text.toString())
        val discountPercent = getDiscountPercent(dishesCount)
        val tipPercent = getRoundedPercent(seekBar.progress)

        val discountAmount = applyDiscount(amount, discountPercent)
        val amountAfterDiscount = amount - discountAmount
        val tipAmount = getTipAmount(amountAfterDiscount, tipPercent)   // чаевые от суммы после скидки
        val finalTotal = amountAfterDiscount + tipAmount

        discountAmountText.text = String.format(Locale.US, "Скидка: %.2f", discountAmount)
        totalAfterDiscountText.text = String.format(Locale.US, "Сумма после скидки: %.2f", amountAfterDiscount)
        finalTotalText.text = String.format(Locale.US, "Итог (с чаевыми): %.2f", finalTotal)

        // Также можно показывать Snackbar при расчёте итога (опционально)
    }
}