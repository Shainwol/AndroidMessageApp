package com.example.mychat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPass;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
        //mAuth.signOut();

        TextView register = findViewById(R.id.si_sign_up);
        register.setOnClickListener(this);

        Button signIn = findViewById(R.id.si_button_acount);
        signIn.setOnClickListener(this);

        editTextEmail =(EditText) findViewById(R.id.email);
        editTextPass =(EditText) findViewById(R.id.si_password);

        //progressBar =(ProgressBar) findViewById(R.id.progress);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.si_sign_up:
                startActivity(new Intent(MainActivity.this, MainSignupActivity.class));
                break;
            case R.id.si_button_acount:
                loginUser();
                break;
        }

    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Escribe tu correo");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Correo invalido");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPass.setError("Escribe tu contraseña");
            editTextPass.requestFocus();
            return;
        }
        //progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    editTextPass.setText("");
                    Toast.makeText(MainActivity.this, "Login", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("estatus").setValue("ACTIVO")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Error en el estado", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                }else{
                    Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_LONG).show();
                    //progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}