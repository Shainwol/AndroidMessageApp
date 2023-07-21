package com.example.mychat.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mychat.R;
import com.example.mychat.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainSignupActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPass, editTextName;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_singup);

        mAuth = FirebaseAuth.getInstance();

        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.carreras, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextName = (EditText) findViewById(R.id.su_full_name);
        editTextPass = (EditText) findViewById(R.id.si_password);

        //progressBar =(ProgressBar) findViewById(R.id.progress);

        TextView signIn = findViewById(R.id.si_sign_up);
        signIn.setOnClickListener(this);

        Button register = findViewById(R.id.si_button_acount);
        register.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.si_button_acount:
                registerUser();

                break;
            case R.id.si_sign_up:
                startActivity(new Intent(MainSignupActivity.this, MainActivity.class));
                break;
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();
        String username = editTextName.getText().toString().trim();
        String carrera = spinner.getSelectedItem().toString();

        if(username.isEmpty()){
            editTextName.setError("Escribe tu nombre");
            editTextName.requestFocus();
            return;
        }
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
        if(carrera.isEmpty()){
            spinner.requestFocus();
            return;
        }
        if(password.length() < 6){
            editTextPass.setError("Minimo 6 caracteres");
            editTextPass.requestFocus();
        }
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User user = new User(firebaseUser.getUid(), email, username, carrera, "INACTIVO");

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        String ID = mAuth.getCurrentUser().getUid();
                                        Toast.makeText(MainSignupActivity.this, "Has sido registrado!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(MainSignupActivity.this, "Error en el registro 1", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainSignupActivity.this, "Error en el registro 2", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
