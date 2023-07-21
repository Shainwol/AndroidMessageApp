package com.example.mychat.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.CifradoTools;
import com.example.mychat.R;
import com.example.mychat.adapters.MensajesAdapter;
import com.example.mychat.models.Message;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ChatGrupalActivity extends AppCompatActivity {

    Intent intent;
    TextView groupName;
    EditText mensaje;
    ImageButton enviar;
    ImageButton lock;
    ImageButton camera;
    Button back;
    FirebaseUser fuser;
    DatabaseReference reference;

    MensajesAdapter mensajesAdapter;
    List<Message> listaMensajes;
    RecyclerView rv;
    boolean encriptacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupal);
        encriptacion = false;
        intent = getIntent();
        String grupoid = intent.getStringExtra("IDGrupo");
        mensaje =findViewById(R.id.message2);
        enviar =findViewById(R.id.sendBTN2);
        back =findViewById(R.id.ng_return3);
        groupName =findViewById(R.id.groupName2);
        lock = findViewById(R.id.lock2);
        camera =findViewById(R.id.camera);
        rv = findViewById(R.id.mensajes);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Groups").child(grupoid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.child("nombre").getValue(String.class);
                groupName.setText(nombre);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readMessage(grupoid);


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
        ActivityResultLauncher<Intent> launcher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri uri = result.getData().getData();
                        // Use the uri to load the image
                        //imagenMiembro.setImageURI(uri);
                        Intent intent = new Intent(ChatGrupalActivity.this, EnviarMultimediaActivity.class);
                        intent.setData(uri);
                        intent.putExtra("ID", grupoid);
                        intent.putExtra("grupal", true);
                        startActivity(intent);
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ChatGrupalActivity.this)
                        .crop()
                        .cropSquare()
                        .maxResultSize(320, 320, true)
                        .provider(ImageProvider.BOTH)
                        .createIntentFromDialog(new Function1() {
                            public Object invoke(Object var1) {
                                this.invoke((Intent) var1);
                                return Unit.INSTANCE;
                            }

                            public final void invoke(@NotNull Intent it) {
                                Intrinsics.checkNotNullParameter(it, "it");
                                launcher.launch(it);
                            }
                        });
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mensaje.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), grupoid, msg, encriptacion);
                } else {
                    Toast.makeText(ChatGrupalActivity.this, "No puedes enviar un mensaje vacio", Toast.LENGTH_LONG).show();
                }
                mensaje.setText("");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public void sendMessage(String sender, String receiver, String message, boolean encriptacion) {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Groups/"+receiver+"/mensajes");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        if(encriptacion) message = CifradoTools.INSTANCE.cifrar(message, sender);
        hashMap.put("message", message);
        hashMap.put("encriptacion", encriptacion);
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        hashMap.put("time", timestamp.toString());
        reference2.push().setValue(hashMap);
    }

    public void readMessage(final String grupoid){
        listaMensajes = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Groups/"+grupoid+"/mensajes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String sender = snapshot.child("sender").getValue(String.class);
                    String receiver = snapshot.child("receiver").getValue(String.class);
                    String contenido = snapshot.child("message").getValue(String.class);
                    boolean estaencriptado = snapshot.child("encriptacion").getValue(boolean.class);
                    if(estaencriptado) contenido = CifradoTools.INSTANCE.descifrar(contenido, sender);
                    Timestamp timestamp = Timestamp.valueOf(snapshot.child("time").getValue(String.class));
                    String downloadUrl = null;
                    if(snapshot.child("path").getValue(String.class) != null) {
                        downloadUrl = snapshot.child("path").getValue(String.class);
                    }
                    Message message = new Message(sender, receiver, contenido, timestamp, downloadUrl);
                    listaMensajes.add(message);
                    mensajesAdapter = new MensajesAdapter(ChatGrupalActivity.this, listaMensajes);
                    rv.setAdapter(mensajesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}