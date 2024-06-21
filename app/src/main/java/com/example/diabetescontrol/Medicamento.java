package com.example.diabetescontrol;

public class Medicamento {

    private String nombreMedicamento;
    private String diasToma;
    private String hora;
    private String dosis;
    private String tipoMedicamento;

    public Medicamento(String nombreMedicamento, String diasToma, String hora, String dosis, String tipoMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
        this.diasToma = diasToma;
        this.hora = hora;
        this.dosis = dosis;
        this.tipoMedicamento = tipoMedicamento;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public String getDiasToma() {
        return diasToma;
    }

    public String getHora() {
        return hora;
    }

    public String getDosis() {
        return dosis;
    }

    public String getTipoMedicamento() {
        return tipoMedicamento;
    }
}
