package com.aspiration.mileagemaster;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.aspiration.mileagemaster.data.Util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class EditTextCurrency extends EditText {

	private double mValue;

	public EditTextCurrency(Context context, AttributeSet attr) {
		super(context, attr);
		mValue = 0.00;
	}

	// @Override
	/*
	 * protected void onFocusChanged(boolean gainFocus, int direction, Rect
	 * previouslyFocusedRect) { if (gainFocus == false) { formatCharge(); }
	 * super.onFocusChanged(gainFocus, direction, previouslyFocusedRect); }
	 */

	public double getValue() {
		return mValue;
	}

	public void setValue(double value) {
		this.mValue = value;
		this.setText(String.valueOf(mValue));
		formatAmount();
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (!focused) {
			this.formatAmount();
		} else {
			this.setText(this.getText().toString().replace(Util.getCurrency(),""));
		}
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	public void formatAmount() {
		if (this.getText().toString().length() == 0) {
			this.setText("0");
		}
		NumberFormat fmt = NumberFormat.getCurrencyInstance();
		mValue = Double.parseDouble(this.getText().toString().replace(Currency.getInstance(Locale.getDefault()).getSymbol(),"").replace(",",""));
		this.setText(fmt.format(mValue).toString());
	}
}
