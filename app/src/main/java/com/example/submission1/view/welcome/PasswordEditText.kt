package com.example.submission1.view.welcome

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.example.submission1.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Tidak perlu dilakukan apa pun sebelum teks berubah.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Set pesan kesalahan jika panjang kata sandi kurang dari 8 karakter, atau hapus pesan kesalahan jika panjang kata sandi memenuhi syarat.
                if (s.toString().length < 8) {
                    setError(context.getString(R.string.passMustbe), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu dilakukan apa pun setelah teks berubah.
            }
        })
    }
}
