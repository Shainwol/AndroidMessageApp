package com.example.mychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.activities.ChatIndividualActivity;

import com.example.mychat.R;
import com.example.mychat.models.Message;
import com.example.mychat.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    ArrayList<Message> listaPost;

    public PostAdapter(ArrayList<Message> listaPost){
        this.listaPost=listaPost;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_list,null,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position){
        final String[] username = new String[1];
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(listaPost.get(position).getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username[0] = snapshot.child("username").getValue(String.class);
                holder.txtUsuario.setText (username[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.txtMensaje.setText (listaPost.get(position).getMessage());
        holder.txtFecha.setText (listaPost.get(position).getTime().toString());
    }

    @Override
    public int getItemCount() {return listaPost.size();}

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView txtUsuario, txtMensaje, txtFecha;

        public PostViewHolder (View itemView){
            super(itemView);
            context = itemView.getContext();
            txtUsuario = (TextView) itemView.findViewById(R.id.usuariopost);
            txtMensaje = (TextView) itemView.findViewById(R.id.contenidopost);
            txtFecha = (TextView) itemView.findViewById(R.id.fechapost);
        }

    }
}
