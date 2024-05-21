package com.example.diabetescontrol.ui.gallery;

public class Cita {
    private String tipoConsulta;
    private String fecha;
    private String hora;
    private String nota;

    public Cita(String tipoConsulta, String fecha, String hora, String nota) {
        this.tipoConsulta = tipoConsulta;
        this.fecha = fecha;
        this.hora = hora;
        this.nota = nota;
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getNota() {
        return nota;
    }
}
