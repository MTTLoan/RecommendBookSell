package com.example.app.utils;

import android.text.InputType;
import android.widget.EditText;

import com.example.app.R;
import com.google.android.material.textfield.TextInputLayout;

public class UIUtils {
    public static void setupPasswordToggle(TextInputLayout textInputLayout) {
        if (textInputLayout == null || textInputLayout.getEditText() == null) {
            return;
        }

        textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        textInputLayout.setEndIconDrawable(R.drawable.ic_visibility_off_24px);
        textInputLayout.setEndIconOnClickListener(v -> {
            EditText editText = textInputLayout.getEditText();
            if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                textInputLayout.setEndIconDrawable(R.drawable.ic_visibility_24px);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                textInputLayout.setEndIconDrawable(R.drawable.ic_visibility_off_24px);
            }
            editText.setSelection(editText.getText().length());
        });
    }
}
