package de.marcelknupfer.studentenausweisguthaben;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Currency;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import de.marcelknupfer.studentenausweisguthaben.cardreader.ValueData;

public class ValueFragment extends Fragment {
	public static final String VALUE = "value";
	private ValueData valueData;
	private TextView tvCurrentValue;
	private TextView tvLastValue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.value_card, container, false);

		tvCurrentValue = ((TextView) v.findViewById(R.id.current));
		tvLastValue = (TextView) v.findViewById(R.id.last);

		ViewCompat.setTransitionName(tvCurrentValue, "current");
		ViewCompat.setTransitionName(tvLastValue, "last");

		if (savedInstanceState!=null) {
			valueData = (ValueData) savedInstanceState.getSerializable(VALUE);
		}

		updateView();

		return v;
	}

	private String moneyStr(int i) {
		Locale germany = Locale.GERMANY;
		String currencySymbol = Currency.getInstance(germany).getSymbol();

		float amount = ((float) i) / 1000;

		return String.format(germany, "%.2f%s", amount, currencySymbol);
	}


	private void updateView() {
		if (valueData==null) {
			tvCurrentValue.setVisibility(View.INVISIBLE);
			tvLastValue.setVisibility(View.INVISIBLE);
		} else {
			String current = moneyStr(valueData.value);
			tvCurrentValue.setVisibility(View.VISIBLE);
			tvCurrentValue.setText(current);
			if (valueData.lastTransaction != null) {
				String last = moneyStr(valueData.lastTransaction);
				tvLastValue.setText(last);
				tvLastValue.setVisibility(View.VISIBLE);
			} else {
				tvLastValue.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable(VALUE, valueData);
	}

	public ValueData getValueData() {
		return valueData;
	}

	public void setValueData(ValueData valueData) {
		this.valueData = valueData;

		if (tvCurrentValue !=null)
			updateView();
	}
}
