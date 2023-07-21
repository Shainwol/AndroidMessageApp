package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mychat.CifradoTools;
import com.example.mychat.R;
import com.example.mychat.activities.ChatIndividualActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class EnviarMultimediaActivity extends AppCompatActivity {
    ImageView imagenEnviar;
    ImageButton lock;
    EditText mensaje;
    FirebaseUser fuser;
    Intent intent;
    Uri uri;
    ImageButton enviar;
    String userid;
    boolean chatgrupal;
    boolean encriptacion;

    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        encriptacion = false;
        setContentView(R.layout.activity_enviar_multimedia);
        imagenEnviar =findViewById(R.id.ImagenEnviar);
        enviar =findViewById(R.id.sendBTN4);
        mensaje =findViewById(R.id.message4);
        lock = findViewById(R.id.lockmultimedia);

        intent = getIntent();
        uri = intent.getData();

        imagenEnviar.setImageURI(uri);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userid = intent.getStringExtra("ID");
        chatgrupal = intent.getBooleanExtra("grupal", false);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encriptacion = !encriptacion;
                if(encriptacion){
                    lock.setBackgroundResource(R.drawable.ic_lock_solid);
                } else {
                    lock.setBackgroundResource(R.drawable.ic_lock_open_solid);
                }
            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mensaje.getText().toString();
                if(chatgrupal){
                    sendMessageGrupal(fuser.getUid(), userid, msg, encriptacion);
                } else {
                    sendMessage(fuser.getUid(), userid, msg, encriptacion);
                }
                mensaje.setText("");
            }
        });

    }

    public void sendMessageGrupal(String sender, String receiver, String message, boolean encriptacion) {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Groups/"+receiver+"/mensajes");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        if(encriptacion) message = CifradoTools.INSTANCE.cifrar(message, sender);
        hashMap.put("message", message);
        hashMap.put("encriptacion", encriptacion);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        hashMap.put("time", timestamp.toString());
        final String[] ruta = new String[1];
        String name = uri.getLastPathSegment();
        mStorageRef.child("chats/"+name).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mStorageRef.child("chats/"+name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ruta[0] = uri.toString();
                                hashMap.put("path", ruta[0]);

                                reference2.push().setValue(hashMap);
                                finish();
                            }
                        });

                    }
                });
    }


    public void sendMessage(String sender, String receiver, String message, boolean encriptacion) {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chats = FirebaseDatabase.getInstance().getReference("Chats");
        //Message enviarMensaje = new Message(sender, receiver, message);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        if(encriptacion) message = CifradoTools.INSTANCE.cifrar(message, sender);
        hashMap.put("message", message);
        hashMap.put("encriptacion", encriptacion);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        long unixTime = System.currentTimeMillis() / 1000L;

        hashMap.put("time", unixTime);
        String name = uri.getLastPathSegment();
        final String[] ruta = new String[1];
        mStorageRef.child("chats/"+name).putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /*SharedPreferences prefs = getSharedPreferences("demo", MODE_PRIVATE);
                        prefs.edit().putString("urlFirebase", mStorageRef.child("chats/"+name).getDownloadUrl().getResult().toString()).apply();*/
                        mStorageRef.child("chats/"+name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ruta[0] = uri.toString();
                                hashMap.put("path", ruta[0]);

                                chats.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Boolean existe2 = false;
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            if (dataSnapshot.getKey().equals(sender+"-"+receiver) || dataSnapshot.getKey().equals(receiver+"-"+sender)){
                                                existe2 = true;
                                                chats.child(dataSnapshot.getKey()).push().setValue(hashMap);
                                                finish();
                                            }
                                        }
                                        if (!existe2){
                                            chats.child(sender+"-"+receiver).push().setValue(hashMap);
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                    }
                });


        reference2.child("Users").child(receiver).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean existe1 = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().equals(sender)){
                        existe1 = true;
                    }
                }
                if (!existe1){
                    reference2.child("Users").child(receiver).child("chats").push().setValue(sender);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference2.child("Users").child(sender).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean existe3 = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getValue().equals(receiver)){
                        existe3 = true;
                    }
                }
                if(!existe3){
                    reference2.child("Users").child(sender).child("chats").push().setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}