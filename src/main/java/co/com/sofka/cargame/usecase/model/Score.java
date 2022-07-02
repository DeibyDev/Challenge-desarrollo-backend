package co.com.sofka.cargame.usecase.model;

import java.util.Date;

public class Score {
    private String juegoId;
    private String carrilId;
    private String carroId;
    private String nombre;
    private Date fechaInicio;
    private Date fechaFin;
    private String distanciaJuego;
    private String tiempoRecorrido;



    public Score() {
    }

    public Score(String juegoId, String carrilId, String carroId, String nombre, Date fechaInicio, Date fechaFin, String distanciaJuego) {
        this.juegoId = juegoId;
        this.carrilId = carrilId;
        this.carroId = carroId;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.distanciaJuego = distanciaJuego;
        this.tiempoRecorrido = calcularTiempoRecorrido();

    }

    public String calcularTiempoRecorrido(){
        long difference_In_Time = fechaFin.getTime() - fechaInicio.getTime();
        long difference_In_Seconds = (difference_In_Time / 1000) % 60;
        long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
        long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
        return convert(difference_In_Hours)+":"+convert(difference_In_Minutes)+":"+convert(difference_In_Seconds);
    }

    public String getTiempoRecorrido() {
        return tiempoRecorrido;
    }

    public void setTiempoRecorrido(String tiempoRecorrido) {
        this.tiempoRecorrido = tiempoRecorrido;
    }

    public String convert(Long aLong){
        return String.format("%02d",aLong);
    }

    public String getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }

    public String getCarrilId() {
        return carrilId;
    }

    public void setCarrilId(String carrilId) {
        this.carrilId = carrilId;
    }

    public String getCarroId() {
        return carroId;
    }

    public void setCarroId(String carroId) {
        this.carroId = carroId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDistanciaJuego() {
        return distanciaJuego;
    }

    public void setDistanciaJuego(String distanciaJuego) {
        this.distanciaJuego = distanciaJuego;
    }
}
