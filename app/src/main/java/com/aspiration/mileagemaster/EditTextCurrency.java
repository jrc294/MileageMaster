package com.aspiration.mileagemaster;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.aspiration.mileagemaster.data.Util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EditTextCurrency extends EditText {

	private Double value;

	public EditTextCurrency(Context context, AttributeSet attr) {
		super(context, attr);
		value = 0.00;
	}

	// @Override
	/*
	 * protected void onFocusChanged(boolean gainFocus, int direction, Rect
	 * previouslyFocusedRect) { if (gainFocus == false) { formatCharge(); }
	 * super.onFocusChanged(gainFocus, direction, previouslyFocusedRect); }
	 */

	public double getValue() {
		return value;
	}

	public void formatCharge(boolean isCurrency) {
		if (this.getText().length() > 0) {
			this.setText(this.getText().toString()
					.replace(Util.getCurrency(), "").replace(",", ""));
			value = Double.valueOf(this.getText().toString());
			if ((isCurrency) || (isLessThanTwoDPS())) {
				NumberFormat nf = NumberFormat.getCurrencyInstance();
				this.setText(nf.format(value));
			} else {
				DecimalFormat df = new DecimalFormat("#.####");
				this.setText(Util.getCurrency()
						+ df.format(Double.parseDouble(this.getText()
								.toString()
								.replace(Util.getCurrency(), ""))));
			}
		} else {
			value = 0.00;
		}
	}

	private boolean isLessThanTwoDPS() {
		// 1 - Get the length of the string
		boolean ret = false;
		String myText = this.getText().toString();
		int myLength = myText.length();

		int dpPos = myText.indexOf(".", 0);
		if ((myLength - dpPos) < 3) {
			ret = true;
		}
		return ret;
	}
}
