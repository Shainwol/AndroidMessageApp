package com.example.mychat.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.activities.ProfileActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser userFB;
    DatabaseReference reference;
    String userID;
    RecyclerView rv;
    ArrayList<User> listaUsuarios;

    TextView texto, carreratexto;
    ImageView estadoimagen;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        Log.i("homefragment", "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        texto = vista.findViewById(R.id.ms_nombre);
        carreratexto = vista.findViewById(R.id.ms_class);
        estadoimagen = vista.findViewById(R.id.ms_on_offline);
        rv = vista.findViewById(R.id.Usuarios);
        inicializarUI();

        /*userFB = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);
        userID = userFB.getUid();

        final TextView texto = vista.findViewById(R.id.ms_nombre);
        final TextView carreratexto = vista.findViewById(R.id.ms_class);
        final ImageView estadoimagen = vista.findViewById(R.id.ms_on_offline);
        estadoimagen.setOnClickListener(this);
        texto.setOnClickListener(this);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String carrera = snapshot.child("carrera").getValue(String.class);
                String estatus = snapshot.child("estatus").getValue(String.class);
                //User userProfile = snapshot.getValue(User.class);
                if(email != null && carrera != null && estatus != null){
                    //String email = userProfile.email;
                    texto.setText(email);
                    carreratexto.setText(carrera);
                    if(estatus.equals("ACTIVO")){
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.green)));
                    } else if (estatus.equals("AUSENTE")){
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red)));
                    } else {
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Algo salio mal", Toast.LENGTH_LONG).show();
            }
        });

        rv = vista.findViewById(R.id.Usuarios);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        listaUsuarios = new ArrayList<>();

        UsuariosAdapter adapter=new UsuariosAdapter(listaUsuarios);
        rv.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = new User (dataSnapshot.child("id").getValue(String.class),
                            dataSnapshot.child("email").getValue(String.class),
                            dataSnapshot.child("username").getValue(String.class),
                            dataSnapshot.child("carrera").getValue(String.class),
                            dataSnapshot.child("estatus").getValue(String.class));
                    if((!userFB.getUid().equals(user.getId())) && (carreratexto.getText().equals(user.getCarrera()))){
                        listaUsuarios.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        return vista;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ms_on_offline) {
            DatabaseReference cambiarestado = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("estatus");
            cambiarestado.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue(String.class).equals("ACTIVO")){
                        cambiarestado.setValue("AUSENTE");
                    } else{
                        cambiarestado.setValue("ACTIVO");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (v.getId() == R.id.ms_nombre) {
            startActivity(new Intent(v.getContext(), ProfileActivity.class));
        }
    }

    private void inicializarUI(){
        userFB = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if (userFB != null) {
            userID = userFB.getUid();
        }


        estadoimagen.setOnClickListener(this);
        texto.setOnClickListener(this);

        reference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String carrera = snapshot.child("carrera").getValue(String.class);
                String estatus = snapshot.child("estatus").getValue(String.class);
                //User userProfile = snapshot.getValue(User.class);
                if(email != null && carrera != null && estatus != null){
                    //String email = userProfile.email;
                    texto.setText(email);
                    carreratexto.setText(carrera);
                    if(estatus.equals("ACTIVO")){
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(0xff66ff99));
                    } else if (estatus.equals("AUSENTE")){
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(0xffff1a1a));
                    } else {
                        ImageViewCompat.setImageTintList(estadoimagen, ColorStateList.valueOf(0xff889191));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "Algo salio mal", Toast.LENGTH_LONG).show();
            }
        });


        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        listaUsuarios = new ArrayList<>();

        UsuariosAdapter adapter=new UsuariosAdapter(listaUsuarios);
        rv.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = new User (dataSnapshot.child("id").getValue(String.class),
                            dataSnapshot.child("email").getValue(String.class),
                            dataSnapshot.child("username").getValue(String.class),
                            dataSnapshot.child("carrera").getValue(String.class),
                            dataSnapshot.child("estatus").getValue(String.class));
                    if((!userFB.getUid().equals(user.getId())) && (carreratexto.getText().equals(user.getCarrera()))){
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
    public void onPause() {
        super.onPause();
        Log.i("homefragment", "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("homefragment", "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("homefragment", "onDestroy: ");
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}