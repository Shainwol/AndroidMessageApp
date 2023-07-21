package com.example.mychat.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseUser fuser;
    Button back;
    LinearLayout logro1,logro2,logro3,logro4;
    TextView nombreperfil, claseperfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        back =findViewById(R.id.ng_return9);
        back.setOnClickListener(this);
        nombreperfil =findViewById(R.id.nombreperfil);
        claseperfil = findViewById(R.id.claseperfil);
        logro1 = findViewById(R.id.logro1);
        logro2 = findViewById(R.id.logro2);
        logro3 = findViewById(R.id.logro3);
        logro4 = findViewById(R.id.logro4);

        DatabaseReference user = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nombreperfil.setText(snapshot.child("username").getValue(String.class));
                claseperfil.setText(snapshot.child("carrera").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        user.child("logros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean existe1 = false, existe2 = false, existe3 = false, existe4 = false;
                for (DataSnapshot datasnapshot: snapshot.getChildren()) {
                    if(datasnapshot.getValue(int.class) == 1){
                        existe1 = true;
                    }
                    if(datasnapshot.getValue(int.class) == 2){
                        existe2 = true;
                    }
                    if(datasnapshot.getValue(int.class) == 3){
                        existe3 = true;
                    }
                    if(datasnapshot.getValue(int.class) == 4){
                        existe4 = true;
                    }
                }
                if (existe1){
                    logro1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.lightgreen)));
                }
                if (existe2){
                    logro2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.lightgreen)));
                }
                if (existe3){
                    logro3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.lightgreen)));
                }
                if (existe4){
                    logro4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.lightgreen)));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ng_return9){
            finish();
        }
    }
}
