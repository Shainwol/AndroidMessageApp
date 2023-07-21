package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.adapters.UsuariosAdapter;
import com.example.mychat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rv;
    ArrayList<User> listaUsuarios;
    DatabaseReference database;
    Button nuevoBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        rv = findViewById(R.id.Usuarios);
        listaUsuarios = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        nuevoBTN = findViewById(R.id.pr_bttn_create_group);

        nuevoBTN.setOnClickListener(this);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users");
        final String[] carrera = new String[1];
        database.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera[0] = snapshot.child("carrera").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        UsuariosAdapter adapter=new UsuariosAdapter(listaUsuarios);
        rv.setAdapter(adapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = new User(dataSnapshot.child("id").getValue(String.class),
                            dataSnapshot.child("email").getValue(String.class),
                            dataSnapshot.child("carrera").getValue(String.class),
                            dataSnapshot.child("username").getValue(String.class),
                            dataSnapshot.child("estatus").getValue(String.class));
                    //User user = dataSnapshot.getValue(User.class);
                    if((!firebaseUser.getUid().equals(user.getId())) && (carrera[0].equals(user.getCarrera()))){
                        listaUsuarios.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        //if (view.getId() == R.id.nuevoGrupo) {
        //    startActivity(new Intent(view.getContext(), CrearGrupoActivity.class));
        //}
    }
}