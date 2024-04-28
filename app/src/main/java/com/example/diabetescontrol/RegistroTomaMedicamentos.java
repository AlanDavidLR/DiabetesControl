package com.example.diabetescontrol;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class RegistroTomaMedicamentos extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RegistroTomaMedicamentos() {
        // Required empty public constructor
    }

    public static RegistroTomaMedicamentos newInstance(String param1, String param2) {
        RegistroTomaMedicamentos fragment = new RegistroTomaMedicamentos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_toma_medicamentos, container, false);

        // Obtener referencia al TextView
        TextView textView = view.findViewById(R.id.tv_medicine_time);

        // Establecer texto inicial
        textView.setText("00:00");

        // Configurar OnClickListener para mostrar el diálogo del selector de tiempo
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog((TextView) v);
            }
        });

        return view;
    }

    public void showTimePickerDialog(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        textView.setText(time);
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }
}
