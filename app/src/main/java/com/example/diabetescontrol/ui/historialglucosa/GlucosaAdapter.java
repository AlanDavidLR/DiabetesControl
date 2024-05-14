package com.example.diabetescontrol.ui.historialglucosa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.diabetescontrol.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GlucosaAdapter extends RecyclerView.Adapter<GlucosaAdapter.GlucosaViewHolder> {

    private List<Glucosa> glucosaList;

    public GlucosaAdapter(List<Glucosa> glucosaList) {
        this.glucosaList = glucosaList;
    }

    @NonNull
    @Override
    public GlucosaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glucosa, parent, false);
        return new GlucosaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GlucosaViewHolder holder, int position) {
        Glucosa glucosa = glucosaList.get(position);
        holder.textViewFecha.setText(glucosa.getFecha());
        holder.textViewHora.setText(glucosa.getHora());
        holder.textViewGlucosa.setText(glucosa.getNivelGlucosa());
        holder.textViewTipoToma.setText(glucosa.getTipoToma());
    }

    @Override
    public int getItemCount() {
        return glucosaList.size();
    }

    static class GlucosaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFecha;
        TextView textViewHora;
        TextView textViewGlucosa;
        TextView textViewTipoToma;

        public GlucosaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewHora = itemView.findViewById(R.id.textViewHora);
            textViewGlucosa = itemView.findViewById(R.id.textViewGlucosa);
            textViewTipoToma = itemView.findViewById(R.id.textViewTipoToma);
        }
    }
}
