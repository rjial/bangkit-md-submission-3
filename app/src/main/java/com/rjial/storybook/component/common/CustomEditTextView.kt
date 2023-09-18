package com.rjial.storybook.component.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rjial.storybook.R

enum class TYPE_TEXT {
    EMAIL,
    PASSWORD,
    TEXT
}
class CustomEditTextView: AppCompatEditText, View.OnTouchListener {

    private lateinit var clearButtonImage: Drawable
    var typeText = TYPE_TEXT.TEXT
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        setBackgroundResource(R.drawable.bg_custom_edittext)
    }
    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }
    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun init() {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
        setOnTouchListener(this)
        addTextChangedListener(object : TextWatcher {
            private var beforeText = ""
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    showClearButton()
                    when (inputType - InputType.TYPE_CLASS_TEXT) {
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> if (!s.toString().contains('@')) error = "Email harus mengandung @"
                        InputType.TYPE_TEXT_VARIATION_PASSWORD -> if (s.toString().length < 8) error = "Password harus lebih dari 8 karakter"
                    }
                } else {
                    hideClearButton()
                }
            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event!!.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event!!.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.baseline_close_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

}