package com.example.mychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.activities.ChatIndividualActivity;

import com.example.mychat.R;
import com.example.mychat.models.User;

import java.util.ArrayList;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder> {
    ArrayList<User> listaUsuarios;

    public UsuariosAdapter(ArrayList<User> listaUsuarios){
        this.listaUsuarios=listaUsuarios;
    }

    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position){
        holder.txtCorreo.setText (listaUsuarios.get(position).getUsername());
        holder.txtID.setText (listaUsuarios.get(position).getId());
        if(listaUsuarios.get(position).getEstatus().equals("ACTIVO")){
            ImageViewCompat.setImageTintList(holder.estado, ColorStateList.valueOf(ContextCompat.getColor(holder.estado.getContext(), R.color.green)));
        } else if (listaUsuarios.get(position).getEstatus().equals("AUSENTE")){
            ImageViewCompat.setImageTintList(holder.estado, ColorStateList.valueOf(ContextCompat.getColor(holder.estado.getContext(), R.color.red)));
        } else {
            ImageViewCompat.setImageTintList(holder.estado, ColorStateList.valueOf(ContextCompat.getColor(holder.estado.getContext(), R.color.grey)));
        }
        holder.setOnClickListeners();

    }

    @Override
    public int getItemCount() {return listaUsuarios.size();}

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        TextView txtCorreo, txtID;
        CardView cardView;
        ImageView estado;

        public UsuarioViewHolder (View itemView){
            super(itemView);
            context = itemView.getContext();
            txtCorreo = (TextView) itemView.findViewById(R.id.idCorreo);
            txtID = (TextView) itemView.findViewById(R.id.idUser);
            cardView =(CardView) itemView.findViewById(R.id.CardView);
            estado =(ImageView) itemView.findViewById(R.id.ms_on_offline3);
        }

        public void setOnClickListeners() {
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.CardView) {
                Intent intent = new Intent(context, ChatIndividualActivity.class);
                intent.putExtra("ID", txtID.getText());
                context.startActivity(intent);
            }
        }
    }
}
