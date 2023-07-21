package com.example.mychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.activities.ChatGrupalActivity;
import com.example.mychat.activities.TareaActivity;
import com.example.mychat.models.Grupo;
import com.example.mychat.models.Tarea;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.ViewHolder> {
    ArrayList<Tarea> listaTarea;

    public TareaAdapter(ArrayList<Tarea> listaTarea) {
        this.listaTarea=listaTarea;
    }

    @Override
    public TareaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarea_item_list,null,false);
        return new TareaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TareaAdapter.ViewHolder holder, int position){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(listaTarea.get(position).getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                holder.usuariotarea.setText (username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.fechatarea.setText (listaTarea.get(position).getTime().toString());
        holder.titulotarea.setText (listaTarea.get(position).getTitulo());
        holder.estadotarea.setText (listaTarea.get(position).getDescripcion());
        holder.idtarea.setText (listaTarea.get(position).getId());

        holder.setOnClickListeners();
    }
    @Override
    public int getItemCount() {return listaTarea.size();}

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        TextView idtarea, usuariotarea, fechatarea, titulotarea, estadotarea;
        LinearLayout layouttarea;

        public ViewHolder (View itemView){
            super(itemView);
            context = itemView.getContext();
            idtarea = (TextView) itemView.findViewById(R.id.idtarea);
            usuariotarea = (TextView) itemView.findViewById(R.id.usuariotarea);
            fechatarea = (TextView) itemView.findViewById(R.id.fechatarea);
            titulotarea =(TextView) itemView.findViewById(R.id.titulotarea);
            estadotarea =(TextView) itemView.findViewById(R.id.estadotarea);
            layouttarea =(LinearLayout) itemView.findViewById(R.id.layouttarea);
        }

        public void setOnClickListeners() {
            layouttarea.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.layouttarea) {
                Intent intent = new Intent(context, TareaActivity.class);
                intent.putExtra("IDTarea", idtarea.getText());
                context.startActivity(intent);
            }
        }
    }
}
