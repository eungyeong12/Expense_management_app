package jo.budget;

import static jo.budget.MainActivity.date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.StringTokenizer;

public class DatePicker extends DialogFragment {
    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    Button confirm;
    Button cancel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.date_picker, null);

        confirm = dialog.findViewById(R.id.btn_confirm);
        cancel = dialog.findViewById(R.id.btn_cancel);

        NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker.this.getDialog().cancel();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                DatePicker.this.getDialog().cancel();
            }
        });

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMaxValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);

        String d = date.getText().toString();
        StringTokenizer st = new StringTokenizer(d, " ");
        String yearS = st.nextToken();
        String monthS = st.nextToken();
        int year = Integer.parseInt(yearS.substring(0,4));
        int month = Integer.parseInt(monthS.substring(0,2));

        monthPicker.setValue(month);
        yearPicker.setValue(year);

        builder.setView(dialog);
        return builder.create();
    }
}
