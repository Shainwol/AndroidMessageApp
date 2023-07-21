package com.example.mychat.models;

import java.util.ArrayList;
import java.util.List;

public class Grupo {
    String id, nombre;
    List<String> integrantes;

    public Grupo(ArrayList<String> integrantes, String nombre) {
        this.integrantes = integrantes;
        this.nombre = nombre;
    }
    public Grupo(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public Grupo(){}

    public String getID() {
        return id;
    }

    public List<String> getIntegrantes() {
        return integrantes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setID(String ID) {
        this.id = ID;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes= integrantes;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
