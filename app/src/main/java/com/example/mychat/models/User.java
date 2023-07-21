package com.example.mychat.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    String id, email, username, carrera, estatus;
    List<String> chats;

    public User(){ }

    public User(String id, String email, String username, String carrera, String estatus){
        this.id = id;
        this.email = email;
        this.username = username;
        this.carrera = carrera;
        this.estatus = estatus;
    }
    public User(String id, String email, String username, String carrera){
        this.id = id;
        this.email = email;
        this.username = username;
        this.carrera = carrera;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() { return username; }

    public String getCarrera() {
        return carrera;
    }

    public List<String> getChats() {
        return chats;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public void setChats(List<String> chats) {
        this.chats = chats;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
