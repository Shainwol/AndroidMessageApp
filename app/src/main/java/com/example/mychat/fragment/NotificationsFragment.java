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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.activities.CrearGrupoActivity;
import com.example.mychat.activities.CrearTareaActivity;
import com.example.mychat.adapters.PostAdapter;
import com.example.mychat.adapters.TareaAdapter;
import com.example.mychat.models.Message;
import com.example.mychat.models.Tarea;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseUser userFB;
    DatabaseReference reference;

    ArrayList<Tarea> listaTareas;
    TareaAdapter tareaAdapter;
    RecyclerView rv;
    String carrera;
    Button crearTarea;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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
        // Inflate the layout for this fragment

        View vista = inflater.inflate(R.layout.fragment_notifications, container, false);

        userFB = FirebaseAuth.getInstance().getCurrentUser();
        crearTarea = vista.findViewById(R.id.crearTarea);
        crearTarea.setOnClickListener(this);
        rv = vista.findViewById(R.id.Tareas);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userFB.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carrera = snapshot.child("carrera").getValue(String.class);

                listaTareas = new ArrayList<>();
                TareaAdapter tareaAdapter=new TareaAdapter(listaTareas);
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(tareaAdapter);
                DatabaseReference tareas = FirebaseDatabase.getInstance().getReference("Grupos").child(carrera).child("Tareas");
                tareas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaTareas.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String id = snapshot.child("id").getValue(String.class);
                            String sender = snapshot.child("sender").getValue(String.class);
                            String titulo = snapshot.child("titulo").getValue(String.class);
                            String descripcion = snapshot.child("descripcion").getValue(String.class);
                            long time = snapshot.child("time").getValue(long.class);
                            Timestamp timestamp = new Timestamp(time * 1000);

                            Tarea tarea = new Tarea(id, sender, titulo, descripcion, timestamp);
                            listaTareas.add(tarea);
                        }
                        tareaAdapter.notifyDataSetChanged();
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


        return vista;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.crearTarea) {
            startActivity(new Intent(view.getContext(), CrearTareaActivity.class));
        }
    }
}