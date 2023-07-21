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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.activities.CrearGrupoActivity;
import com.example.mychat.activities.UsersActivity;
import com.example.mychat.adapters.GrupoAdapter;
import com.example.mychat.models.Grupo;
import com.example.mychat.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupListFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rv;
    ArrayList<Grupo> listaGrupos;
    DatabaseReference database;
    Button msgBTN;

    public GroupListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupListFragment newInstance(String param1, String param2) {
        GroupListFragment fragment = new GroupListFragment();
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
        View vista = inflater.inflate(R.layout.fragment_group_list, container, false);
        msgBTN = vista.findViewById(R.id.crearGrupo);

        msgBTN.setOnClickListener(this);
        rv = vista.findViewById(R.id.Chats);
        listaGrupos = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> mischats = new ArrayList<>();
        GrupoAdapter adapter=new GrupoAdapter(listaGrupos);
        rv.setAdapter(adapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("Users/"+firebaseUser.getUid()+"/Grupo");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaGrupos.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mischats.add(dataSnapshot.getValue(String.class));
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
                    reference.child(dataSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Grupo grupo = new Grupo(snapshot.child("id").getValue(String.class),snapshot.child("nombre").getValue(String.class));
                            listaGrupos.add(grupo);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Algo salio mal", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return vista;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.crearGrupo) {
            startActivity(new Intent(v.getContext(), CrearGrupoActivity.class));
        }
    }
}