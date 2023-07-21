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
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mychat.R;
import com.example.mychat.activities.UsersActivity;
import com.example.mychat.adapters.UsuariosAdapter;
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
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rv;
    ArrayList<User> mUsers;
    ArrayList<User> listaUsuarios;
    DatabaseReference database;
    ImageButton msgBTN;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        View vista = inflater.inflate(R.layout.fragment_chat, container, false);

        rv = vista.findViewById(R.id.Chats);
        listaUsuarios = new ArrayList<>();
        mUsers = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        UsuariosAdapter adapter=new UsuariosAdapter(listaUsuarios);
        rv.setAdapter(adapter);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        List<String> mischats = new ArrayList<>();
        database = FirebaseDatabase.getInstance().getReference("Users/"+firebaseUser.getUid()+"/chats");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaUsuarios.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mischats.add(dataSnapshot.getValue(String.class));
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(dataSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = new User(snapshot.child("id").getValue(String.class),
                                    snapshot.child("email").getValue(String.class),
                                    snapshot.child("username").getValue(String.class),
                                    snapshot.child("carrera").getValue(String.class),
                                    snapshot.child("estatus").getValue(String.class));
                            listaUsuarios.add(user);
                            //User userProfile = snapshot.getValue(User.class);
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Algo salio mal", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        return vista;

    }

}