package com.aspiration.mileagemaster;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jonathan.cook on 5/25/2016.
 */
public class EditTextAdHocCharge extends EditText {

    EditTextCurrency mCurrencyField;

    public EditTextAdHocCharge(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public EditTextAdHocCharge(Context context) {
        super(context);
    }

    public void setCurrencyField(EditTextCurrency editTextCurrency) {
        mCurrencyField = editTextCurrency;
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mCurrencyField.setText("");
                    mCurrencyField.setEnabled(false);
                } else {
                    mCurrencyField.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
