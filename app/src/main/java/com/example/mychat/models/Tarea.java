package com.example.mychat.models;

import java.sql.Timestamp;

public class Tarea {
    String id, sender, titulo, descripcion;
    java.sql.Timestamp time;
    boolean entregado;

    public Tarea(String id, String sender, String titulo, String descripcion, java.sql.Timestamp time){
        this.id = id;
        this.sender = sender;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public boolean isEntregado() {
        return entregado;
    }
}
