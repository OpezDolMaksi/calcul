package com.example.calcul

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextAmount: EditText
    private lateinit var editTextDishes: EditText
    private lateinit var seekBar: SeekBar
    private lateinit var sliderValueText: TextView
    private lateinit var radioGroupDiscount: RadioGroup
    private lateinit var resultDisplayText: TextView
    private lateinit var buttonTotal: Button
    private lateinit var rootLayout: android.widget.LinearLayout

    private var isShowingTotal = false
    private var currentDiscountAmount = 0.0
    private var currentFinalTotal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextAmount = findViewById(R.id.editTextAmount)
        editTextDishes = findViewById(R.id.editTextDishes)
        seekBar = findViewById(R.id.seekBar)
        sliderValueText = findViewById(R.id.sliderValueText)
        radioGroupDiscount = findViewById(R.id.radioGroupDiscount)
        resultDisplayText = findViewById(R.id.resultDisplayText)
        buttonTotal = findViewById(R.id.buttonTotal)
        rootLayout = findViewById(R.id.rootLayout)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val roundedPercent = getRoundedPercent(progress)
                if (roundedPercent != progress) {
                    seekBar?.progress = roundedPercent
                }
                sliderValueText.text = getString(R.string.tip_percent, roundedPercent)
                calculateAndDisplay()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        editTextDishes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val dishesCount = parseDishesCount(s.toString())
                val discountPercent = getDiscountPercent(dishesCount)
                selectDiscountRadioButton(discountPercent)
                if (dishesCount != null && s.toString().isNotEmpty()) {
                    Snackbar.make(rootLayout, "Количество блюд: $dishesCount, скидка: $discountPercent%", Snackbar.LENGTH_SHORT).show()
                } else if (s.toString().isNotEmpty()) {
                    Snackbar.make(rootLayout, "Введите корректное количество блюд", Snackbar.LENGTH_SHORT).show()
                }
                calculateAndDisplay()
            }
        })

        editTextAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateAndDisplay()
            }
        })

        buttonTotal.setOnClickListener {
            if (!isShowingTotal) {
                resultDisplayText.text = String.format(Locale.US, "Итого: %.2f", currentFinalTotal)
                isShowingTotal = true
            } else {
                resultDisplayText.text = String.format(Locale.US, "Скидка: %.2f", currentDiscountAmount)
                isShowingTotal = false
            }
        }

        calculateAndDisplay()
    }

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
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun applyDiscount(amount: Double, discountPercent: Int): Double {
        return amount * discountPercent / 100.0
    }

    private fun getTipAmount(amount: Double, tipPercent: Int): Double {
        return amount * tipPercent / 100.0
    }

    private fun parseAmount(input: String): Double? {
        return try {
            input.toDouble()
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun getRoundedPercent(progress: Int): Int {
        return (progress + 2) / 5 * 5
    }

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
            radioGroupDiscount.clearCheck()
        }
    }

    private fun calculateAndDisplay() {
        val amount = parseAmount(editTextAmount.text.toString()) ?: 0.0
        val dishesCount = parseDishesCount(editTextDishes.text.toString())
        val discountPercent = getDiscountPercent(dishesCount)
        val tipPercent = getRoundedPercent(seekBar.progress)

        val discountAmount = applyDiscount(amount, discountPercent)
        val amountAfterDiscount = amount - discountAmount
        val tipAmount = getTipAmount(amountAfterDiscount, tipPercent)
        val finalTotal = amountAfterDiscount + tipAmount

        currentDiscountAmount = discountAmount
        currentFinalTotal = finalTotal

        isShowingTotal = false
        resultDisplayText.text = String.format(Locale.US, "Скидка: %.2f", currentDiscountAmount)
    }
}