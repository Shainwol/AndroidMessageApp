package com.example.mychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.activities.ChatGrupalActivity;
import com.example.mychat.models.Grupo;

import java.util.ArrayList;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder> {
    ArrayList<Grupo> listaGrupos;

    public GrupoAdapter(ArrayList<Grupo> listaGrupos) {
        this.listaGrupos=listaGrupos;
    }

    @Override
    public GrupoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_list,null,false);
        return new GrupoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GrupoAdapter.ViewHolder holder, int position){
        holder.txtNombre.setText (listaGrupos.get(position).getNombre());
        holder.txtID.setText (listaGrupos.get(position).getID());

        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {return listaGrupos.size();}

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        TextView txtNombre, txtID;
        CardView cardView;

        public ViewHolder (View itemView){
            super(itemView);
            context = itemView.getContext();
            txtNombre = (TextView) itemView.findViewById(R.id.idCorreo);
            txtID = (TextView) itemView.findViewById(R.id.idUser);
            cardView =(CardView) itemView.findViewById(R.id.CardView);
        }

        public void setOnClickListeners() {
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.CardView) {
                Intent intent = new Intent(context, ChatGrupalActivity.class);
                intent.putExtra("IDGrupo", txtID.getText());
                context.startActivity(intent);
            }
        }
    }
}
