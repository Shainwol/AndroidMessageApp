package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mychat.R;
import com.example.mychat.adapters.PostAdapter;
import com.example.mychat.models.Message;
import com.example.mychat.models.Tarea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.ArrayList;

public class TareaActivity extends AppCompatActivity {
    Intent intent;
    FirebaseUser fuser;
    TextView Titulo, Fecha, Descripcion, estatus;
    Button back, entregar;
    DatabaseReference tareas;
    DatabaseReference user;
    Tarea tarea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        back =findViewById(R.id.ng_return7);
        entregar =findViewById(R.id.entregar);
        estatus = findViewById(R.id.estatus);
        estatus.setVisibility(View.GONE);
        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.descripcion);
        Fecha = findViewById(R.id.hora);

        intent = getIntent();
        String tareaid = intent.getStringExtra("IDTarea");
        final String[] carrera = new String[1];
        user = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        user.child("tareas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    if(datasnapshot.child("id").getValue(String.class).equals(tareaid)){
                        String estado = datasnapshot.child("estatus").getValue(String.class);
                        if(estado != null) {
                            if (estado.equals("ENTREGADA")) {
                                entregar.setVisibility(View.GONE);
                                estatus.setVisibility(View.VISIBLE);
                                estatus.setText("Entregada");
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        user.child("carrera").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera[0] = snapshot.getValue(String.class);
                tareas = FirebaseDatabase.getInstance().getReference("Grupos").child(carrera[0]).child("Tareas");
                tareas.child(tareaid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long time = snapshot.child("time").getValue(long.class);
                        Timestamp timestamp = new Timestamp(time * 1000);
                        tarea = new Tarea(snapshot.child("id").getValue(String.class),
                                            snapshot.child("sender").getValue(String.class),
                                            snapshot.child("titulo").getValue(String.class),
                                            snapshot.child("descripcion").getValue(String.class),
                                            timestamp);
                        Titulo.setText(tarea.getTitulo());
                        Descripcion.setText(tarea.getDescripcion());
                        Fecha.setText(tarea.getTime().toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        entregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference tareasusuario = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("tareas");
                String key = tareasusuario.push().getKey();
                tareasusuario.child(key).child("id").setValue(tareaid);
                tareasusuario.child(key).child("estatus").setValue("ENTREGADA");

                DatabaseReference logro = FirebaseDatabase.getInstance().getReference("Users/"+fuser.getUid()+"/logros");
                logro.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean existe4 = false;
                        for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                            if(datasnapshot.getValue(int.class) == 4){
                                existe4 = true;
                            }
                        }
                        if(!existe4){
                            Toast.makeText(TareaActivity.this, "Logro 'Entregar una tarea' obtenido", Toast.LENGTH_LONG).show();
                            logro.push().setValue(4);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}