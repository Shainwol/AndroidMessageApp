package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.models.Grupo;
import com.example.mychat.models.Tarea;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CrearTareaActivity extends AppCompatActivity implements View.OnClickListener{
    Button back, crear;
    DatabaseReference database;
    EditText titulotarea, descripciontarea;
    Tarea tarea;
    String titulo, descripcion, carrera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_tarea);
        back = findViewById(R.id.ng_return6);
        crear = findViewById(R.id.crearT);
        titulotarea = findViewById(R.id.titulotarea);
        descripciontarea = findViewById(R.id.descripciontarea);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users");
        
        database.child(firebaseUser.getUid()).child("carrera").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera = snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(this);
        crear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ng_return6){
            finish();
        }
        if (view.getId() == R.id.crearT){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String IDlog = firebaseUser.getUid();

            titulo = titulotarea.getText().toString().trim();
            descripcion = descripciontarea.getText().toString().trim();

            if(titulo.isEmpty()){
                titulotarea.setError("Escribe un titulo");
                titulotarea.requestFocus();
                return;
            }
            if(descripcion.isEmpty()){
                descripciontarea.setError("Escribe la descripcion");
                descripciontarea.requestFocus();
                return;
            }

            database = FirebaseDatabase.getInstance().getReference("Grupos").child(carrera).child("Tareas");
            DatabaseReference key = database.push();
            long unixTime = System.currentTimeMillis() / 1000L;
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", key.getKey());
            hashMap.put("sender", IDlog);
            hashMap.put("titulo", titulo);
            hashMap.put("descripcion", descripcion);
            hashMap.put("time", unixTime);
            database.child(key.getKey()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    DatabaseReference logro = FirebaseDatabase.getInstance().getReference("Users/"+IDlog+"/logros");
                    logro.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean existe3 = false;
                            for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                                if(datasnapshot.getValue(int.class) == 3){
                                    existe3 = true;
                                }
                            }
                            if(!existe3){
                                Toast.makeText(CrearTareaActivity.this, "Logro 'Crear una tarea' obtenido", Toast.LENGTH_LONG).show();
                                logro.push().setValue(3);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    finish();
                }
            });



        }
    }
}