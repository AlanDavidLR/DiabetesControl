package com.example.diabetescontrol.ui.gallery;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.diabetescontrol.R;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {
    private List<Cita> citas;

    public CitaAdapter(List<Cita> citas) {
        this.citas = citas;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citas.get(position);
        holder.tipoConsulta.setText(cita.getTipoConsulta());
        holder.fecha.setText(cita.getFecha());
        holder.hora.setText(cita.getHora());
        holder.nota.setText(cita.getNota());
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tipoConsulta, fecha, hora, nota;

        CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tipoConsulta = itemView.findViewById(R.id.tipoConsulta);
            fecha = itemView.findViewById(R.id.fecha);
            hora = itemView.findViewById(R.id.hora);
            nota = itemView.findViewById(R.id.nota);
        }
    }
}
