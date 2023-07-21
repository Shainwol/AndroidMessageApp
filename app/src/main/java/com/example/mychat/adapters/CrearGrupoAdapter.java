package com.example.mychat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CrearGrupoAdapter extends RecyclerView.Adapter<CrearGrupoAdapter.CrearGrupoViewHolder>{
    ArrayList<User> listaUsuarios;
    static ArrayList<String> integrantes;

    public CrearGrupoAdapter(ArrayList<User> listaUsuarios){
        this.listaUsuarios=listaUsuarios;
        this.integrantes=new ArrayList<String>();
    }

    @Override
    public CrearGrupoAdapter.CrearGrupoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_list,null,false);
        return new CrearGrupoAdapter.CrearGrupoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CrearGrupoAdapter.CrearGrupoViewHolder holder, int position){
        holder.txtCorreo.setText (listaUsuarios.get(position).getEmail());
        holder.txtID.setText (listaUsuarios.get(position).getId());

        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {return listaUsuarios.size();}

    public ArrayList<String> getIntegrantes() {
        return integrantes;
    }

    public static class CrearGrupoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        TextView txtCorreo, txtID, txtClick;
        CardView cardView;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Grupos");

        public CrearGrupoViewHolder (View itemView){
            super(itemView);
            context = itemView.getContext();
            txtCorreo = (TextView) itemView.findViewById(R.id.idCorreo);
            txtID = (TextView) itemView.findViewById(R.id.idUser);
            cardView =(CardView) itemView.findViewById(R.id.CardView);
            txtClick=(TextView) itemView.findViewById(R.id.click);
        }

        public void setOnClickListeners() {
            cardView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.CardView) {
                if (txtClick.getText().equals("no")){
                    txtClick.setText("si");
                    cardView.setCardBackgroundColor(Color.parseColor("#b1cc8f"));
                    integrantes.add(txtID.getText().toString());
                }else{
                    if (txtClick.getText() == "si") {
                        txtClick.setText("no");
                        cardView.setCardBackgroundColor(Color.parseColor("#00FFFFFF"));
                        integrantes.removeIf(n -> (n.equals(txtID.getText().toString())));
                    }
                }
            }
        }
    }
}
