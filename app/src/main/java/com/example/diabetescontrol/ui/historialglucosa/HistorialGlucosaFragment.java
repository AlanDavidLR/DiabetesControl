package com.example.diabetescontrol.ui.historialglucosa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabetescontrol.databinding.FragmentHistorialGlucosaBinding;

public class HistorialGlucosaFragment extends Fragment {

    private FragmentHistorialGlucosaBinding binding;
    private HistorialGlucosaViewModel historialViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento utilizando el enlace de datos
        binding = FragmentHistorialGlucosaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Crear una instancia del ViewModel asociado con este fragmento
        historialViewModel = new ViewModelProvider(this).get(HistorialGlucosaViewModel.class);

        // Vincular el ViewModel con el layout utilizando el enlace de datos
        binding.setViewModel(historialViewModel);
        binding.setLifecycleOwner(this);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
