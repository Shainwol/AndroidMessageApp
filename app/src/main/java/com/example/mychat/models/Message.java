package com.example.mychat.models;

import android.net.Uri;

import com.google.firebase.Timestamp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Message {
    String sender, receiver, message, id, multimedia;
    java.sql.Timestamp time;
    public Message(){ }

    public Message(String sender, String receiver, String message, java.sql.Timestamp time){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
    }
    public Message(String sender, String receiver, String message, java.sql.Timestamp time, String multimedia){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.multimedia = multimedia;
    }
    public Message(String sender, String message, java.sql.Timestamp time){
        this.sender = sender;
        this.time = time;
        this.message = message;
    }

    public Message(@Nullable String sender, @NotNull String receiver, @NotNull String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(java.sql.Timestamp time) {
        this.time = time;
    }

    public java.sql.Timestamp getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }
}
