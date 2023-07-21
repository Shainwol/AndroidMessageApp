package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.adapters.CrearGrupoAdapter;
import com.example.mychat.models.Grupo;
import com.example.mychat.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CrearGrupoActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv;
    ArrayList<User> listaUsuarios;
    ArrayList<String> integrantes;
    DatabaseReference database;

    Grupo grupo;
    EditText groupName;
    ImageButton crearBTN;
    Button back;
    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_grupo);

        rv = findViewById(R.id.Usuarios);
        listaUsuarios = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        crearBTN = findViewById(R.id.crearBTN);
        back = findViewById(R.id.ng_return5);
        groupName = findViewById(R.id.groupName);

        CrearGrupoAdapter adapter=new CrearGrupoAdapter(listaUsuarios);
        rv.setAdapter(adapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users");
        final String[] carrera = new String[1];
        database.child(firebaseUser.getUid()).child("carrera").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera[0] = snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = new User(dataSnapshot.child("id").getValue(String.class),
                                        dataSnapshot.child("email").getValue(String.class),
                                        dataSnapshot.child("username").getValue(String.class),
                                        dataSnapshot.child("carrera").getValue(String.class),
                                        dataSnapshot.child("estatus").getValue(String.class));
                    if((!firebaseUser.getUid().equals(user.getId())) && (carrera[0].equals(user.getCarrera()))){
                        listaUsuarios.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                integrantes = adapter.getIntegrantes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        crearBTN.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.crearBTN){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String IDlog = firebaseUser.getUid();
            integrantes.add(IDlog);
            nombre = groupName.getText().toString().trim();
            if(nombre.isEmpty()){
                groupName.setError("Escribe un nombre");
                groupName.requestFocus();
                return;
            }
            grupo = new Grupo(integrantes, nombre);
            database = FirebaseDatabase.getInstance().getReference("Groups");
            DatabaseReference key = database.push();
            grupo.setID(key.getKey());
            database.child(key.getKey()).setValue(grupo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        for (String integrante: integrantes) {
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users/"+integrante+"/Grupo");
                            reference2.push().setValue(grupo.getID());
                        }
                        Toast.makeText(CrearGrupoActivity.this, "Se ha generado un nuevo grupo!", Toast.LENGTH_LONG).show();

                        DatabaseReference logro = FirebaseDatabase.getInstance().getReference("Users/"+IDlog+"/logros");
                        logro.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean existe1 = false;
                                for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                                    if(datasnapshot.getValue(int.class) == 1){
                                        existe1 = true;
                                    }
                                }
                                if(!existe1){
                                    Toast.makeText(CrearGrupoActivity.this, "Logro 'Crea un grupo nuevo' obtenido", Toast.LENGTH_LONG).show();
                                    logro.push().setValue(1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        Intent intent = new Intent(CrearGrupoActivity.this, ChatGrupalActivity.class);
                        intent.putExtra("IDGrupo", grupo.getID());
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(CrearGrupoActivity.this, "Error en el registro 1", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (view.getId() == R.id.ng_return5){
            finish();
        }
    }
}