package com.example.diabetescontrol.ui.historialglucosa;


public class Glucosa {
    private String fecha;
    private String hora;
    private String nivelGlucosa;
    private String tipoToma;

    public Glucosa(String fecha, String hora, String nivelGlucosa, String tipoToma) {
        this.fecha = fecha;
        this.hora = hora;
        this.nivelGlucosa = nivelGlucosa;
        this.tipoToma = tipoToma;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getNivelGlucosa() {
        return nivelGlucosa;
    }

    public String getTipoToma() {
        return tipoToma;
    }
}

