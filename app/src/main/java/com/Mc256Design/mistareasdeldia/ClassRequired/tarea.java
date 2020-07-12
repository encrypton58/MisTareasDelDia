package com.Mc256Design.mistareasdeldia.ClassRequired;

public class tarea {

    private String titulo, descripcion, fecha,horasDesignadas, horaStr;
    private int hora , minuto;
    private int id;

    public tarea(){

    }

    public tarea(int id, String titulo,int hora, int minuto , String descripcion, String fecha,String horas_designadas) {
        this.id = id;
        this.titulo = titulo;
        this.hora = hora;
        this.minuto = minuto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horasDesignadas = horas_designadas;
    }

    public tarea(int id, String titulo, String hora, String descripcion, String fecha, String horasDesignadas){
        this.id = id;
        this.titulo = titulo;
        this.horaStr = hora;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horasDesignadas = horasDesignadas;
    }

    public int getId(){return id;}

    public String getTitulo() {
        return titulo;
    }

    public String getHoraStr(){ return horaStr;}

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHorasDesignadas() {
        return horasDesignadas;
    }

}
