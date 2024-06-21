package com.example.diabetescontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private ArrayList<Medicamento> medicamentosList;

    public MedicamentoAdapter(ArrayList<Medicamento> medicamentosList) {
        this.medicamentosList = medicamentosList;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicamento, parent, false);
        return new MedicamentoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = medicamentosList.get(position);
        holder.nombreMedicamentoTextView.setText(medicamento.getNombreMedicamento());
        holder.diasTomaTextView.setText(medicamento.getDiasToma());
        holder.horaTextView.setText(medicamento.getHora());
        holder.dosisTextView.setText(medicamento.getDosis());
        holder.tipoMedicamentoTextView.setText(medicamento.getTipoMedicamento());
    }

    @Override
    public int getItemCount() {
        return medicamentosList.size();
    }

    public class MedicamentoViewHolder extends RecyclerView.ViewHolder {

        TextView nombreMedicamentoTextView;
        TextView diasTomaTextView;
        TextView horaTextView;
        TextView dosisTextView;
        TextView tipoMedicamentoTextView;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreMedicamentoTextView = itemView.findViewById(R.id.nombreMedicamentoTextView);
            diasTomaTextView = itemView.findViewById(R.id.diasTomaTextView);
            horaTextView = itemView.findViewById(R.id.horaTextView);
            dosisTextView = itemView.findViewById(R.id.dosisTextView);
            tipoMedicamentoTextView = itemView.findViewById(R.id.tipoMedicamentoTextView);
        }
    }
}
