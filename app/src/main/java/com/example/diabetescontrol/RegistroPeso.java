package com.example.diabetescontrol;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class RegistroPeso extends Fragment {

    private EditText weightInput, heightInput;
    private TextView resultText, descriptionText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_peso, container, false);
        initViews(view);

        Button calculateButton = view.findViewById(R.id.btnCalcular);
        Button clearButton = view.findViewById(R.id.btnClearData);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularIMC();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });

        return view;
    }

    public void initViews(View view) {
        weightInput = view.findViewById(R.id.lblWeightValue);
        heightInput = view.findViewById(R.id.lblHeightValue);
        resultText = view.findViewById(R.id.lblIMCValue);
        descriptionText = view.findViewById(R.id.lblDescription);
    }

    private void calcularIMC() {
        double weight = Double.parseDouble(weightInput.getText().toString());
        double height = Double.parseDouble(heightInput.getText().toString());
        double bmi = weight / (height * height);

        resultText.setText(String.format(getString(R.string.bmi_result_format), bmi));

        String description;
        if (bmi <= 0) {
            description = getString(R.string.zero_values);
        } else if (bmi >= 1 && bmi < 18.5) {
            description = getString(R.string.under_weight);
        } else if (bmi < 25) {
            description = getString(R.string.normal_weight);
        } else if (bmi < 30) {
            description = getString(R.string.overweight);
        } else if (bmi < 35) {
            description = getString(R.string.type_1_obesity);
        } else if (bmi < 40) {
            description = getString(R.string.type_2_obesity);
        } else {
            description = getString(R.string.type_3_obesity);
        }
        descriptionText.setText(description);
    }

    public void limpiar() {
        weightInput.setText("");
        heightInput.setText("");
        resultText.setText("");
        descriptionText.setText("");
    }
}

