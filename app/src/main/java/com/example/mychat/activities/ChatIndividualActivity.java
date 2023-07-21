package com.example.mychat.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.CifradoTools;
import com.example.mychat.R;
import com.example.mychat.adapters.MensajesAdapter;
import com.example.mychat.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.firebase.database.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class ChatIndividualActivity extends AppCompatActivity {
    TextView username;
    EditText mensaje;
    ImageButton enviar;
    Button back;
    ImageButton camera;
    ImageButton lock;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MensajesAdapter mensajesAdapter;
    List<Message> listaMensajes;
    RecyclerView rv;

    String userid;
    boolean encriptacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_individual);
        encriptacion = false;
        username =findViewById(R.id.idCorreo);
        final ImageView estadoimagen = findViewById(R.id.ms_on_offline2);
        mensaje =findViewById(R.id.message);
        enviar =findViewById(R.id.sendBTN);
        back =findViewById(R.id.ng_return2);
        camera =findViewById(R.id.camera);
        lock = findViewById(R.id.lock);
        intent = getIntent();
        userid = intent.getStringExtra("ID");

        rv = findViewById(R.id.mensajes);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mensaje.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg, encriptacion);
                } else {
                    Toast.makeText(ChatIndividualActivity.this, "No puedes enviar un mensaje vacio", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(ChatIndividualActivity.this, EnviarMultimediaActivity.class);
                        intent.setData(uri);
                        intent.putExtra("ID", userid);
                        intent.putExtra("grupal", false);
                        startActivity(intent);
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                    }
                });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(ChatIndividualActivity.this)
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

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.child("username").getValue(String.class);
                String estado = dataSnapshot.child("estatus").getValue(String.class);
                username.setText(user);
                if(estado.equals("ACTIVO")){
                    ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(ChatIndividualActivity.this, R.color.green)));
                } else if (estado.equals("AUSENTE")){
                    ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(ChatIndividualActivity.this, R.color.red)));
                } else {
                    ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(ChatIndividualActivity.this, R.color.grey)));
                }

                readMessage(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean existe2 = false;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if (dataSnapshot.getKey().equals(sender+"-"+receiver) || dataSnapshot.getKey().equals(receiver+"-"+sender)){
                        existe2 = true;
                        chats.child(dataSnapshot.getKey()).push().setValue(hashMap);
                    }
                }
                if (!existe2){
                    chats.child(sender+"-"+receiver).push().setValue(hashMap);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        Uri uri = data.getData();
        //cover.setImageURI(uri);
    }

    public void readMessage(final String myid, final String userid){
        listaMensajes = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensajes.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    if(key.equals(userid+"-"+myid) || key.equals(myid+"-"+userid)){
                        for (DataSnapshot snapshot2: snapshot.getChildren()) {
                            String sender = snapshot2.child("sender").getValue(String.class);
                            String receiver = snapshot2.child("receiver").getValue(String.class);
                            String contenido = snapshot2.child("message").getValue(String.class);
                            long time = snapshot2.child("time").getValue(long.class);
                            Timestamp timestamp = new Timestamp(time * 1000);
                            boolean estaencriptado = snapshot2.child("encriptacion").getValue(boolean.class);
                            if(estaencriptado) contenido = CifradoTools.INSTANCE.descifrar(contenido, sender);

                            String downloadUrl = null;
                            if(snapshot2.child("path").getValue(String.class) != null) {
                                downloadUrl = snapshot2.child("path").getValue(String.class);
                            }
                            Message message = new Message(sender, receiver, contenido, timestamp, downloadUrl);
                            listaMensajes.add(message);
                        }
                    }
                    mensajesAdapter = new MensajesAdapter(ChatIndividualActivity.this, listaMensajes);
                    rv.setAdapter(mensajesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}