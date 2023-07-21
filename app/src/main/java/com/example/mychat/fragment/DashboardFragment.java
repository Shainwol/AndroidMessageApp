package com.example.mychat.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.CifradoTools;
import com.example.mychat.R;
import com.example.mychat.activities.ChatIndividualActivity;
import com.example.mychat.activities.CrearGrupoActivity;
import com.example.mychat.activities.UsersActivity;
import com.example.mychat.adapters.MensajesAdapter;
import com.example.mychat.adapters.PostAdapter;
import com.example.mychat.adapters.UsuariosAdapter;
import com.example.mychat.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser userFB;
    DatabaseReference reference;
    ImageButton enviar;
    EditText mensaje;
    TextView textocarrera;
    ArrayList<Message> listaPublicaciones;
    PostAdapter postAdapter;
    RecyclerView rv;
    String carrera;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_dashboard, container, false);

        userFB = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        textocarrera = vista.findViewById(R.id.carrerafragment);
        rv = vista.findViewById(R.id.posts);

        reference.child(userFB.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera = snapshot.child("carrera").getValue(String.class);
                if(carrera != null){
                    textocarrera.setText(carrera);
                }
                listaPublicaciones = new ArrayList<>();
                PostAdapter postAdapter=new PostAdapter(listaPublicaciones);
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(postAdapter);
                DatabaseReference grupo = FirebaseDatabase.getInstance().getReference("Grupos").child(carrera).child("Publicaciones");
                grupo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaPublicaciones.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String sender = snapshot.child("sender").getValue(String.class);
                            String contenido = snapshot.child("message").getValue(String.class);
                            long time = snapshot.child("time").getValue(long.class);
                            Timestamp timestamp = new Timestamp(time * 1000);

                            Message message = new Message(sender, contenido, timestamp);
                            listaPublicaciones.add(message);
                        }
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Algo salio mal", Toast.LENGTH_LONG).show();
            }
        });
        enviar = vista.findViewById(R.id.sendBTN3);
        mensaje = vista.findViewById(R.id.message3);
        enviar.setOnClickListener(this);



        // Inflate the layout for this fragment
        return vista;
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendBTN3) {
            String msg = mensaje.getText().toString();
            if(!msg.equals("")){
                sendMessage(userFB.getUid(), textocarrera.getText().toString(), msg);
            } else {
                Toast.makeText(getContext(), "No puedes publicar un mensaje vacio", Toast.LENGTH_LONG).show();
            }
            mensaje.setText("");
        }
    }
    public void sendMessage(String sender, String carrera, String message) {
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference grupo = FirebaseDatabase.getInstance().getReference("Grupos").child(carrera);
        //Message enviarMensaje = new Message(sender, receiver, message);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);

        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        long unixTime = System.currentTimeMillis() / 1000L;
        hashMap.put("time", unixTime);

        grupo.child("Publicaciones").push().setValue(hashMap);

        DatabaseReference logro = FirebaseDatabase.getInstance().getReference("Users/"+sender+"/logros");
        logro.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean existe2 = false;
                for (DataSnapshot datasnapshot:snapshot.getChildren()) {
                    if(datasnapshot.getValue(int.class) == 2){
                        existe2 = true;
                    }
                }
                if(!existe2){
                    Toast.makeText(getContext(), "Logro 'Publicar en tu grupo' obtenido", Toast.LENGTH_LONG).show();
                    logro.push().setValue(2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}