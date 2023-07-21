package com.example.mychat.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychat.R;
import com.example.mychat.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.MensajeViewHolder> {
    List<Message> listaMensajes;
    private Context mContext;
    public static final int MSG_TYPE_LEFT =0;
    public static final int MSG_TYPE_RIGHT =1;
    public static final int MSG_TYPE_LEFT_FILE =2;
    public static final int MSG_TYPE_RIGHT_FILE =3;

    FirebaseUser fuser;

    public MensajesAdapter(Context mContext, List<Message> listaMensajes){
        this.listaMensajes=listaMensajes;
        this.mContext=mContext;
    }


    @NonNull
    @Override
    public MensajesAdapter.MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_box_right, parent, false);
            return new MensajesAdapter.MensajeViewHolder(view);
        } else if(viewType == MSG_TYPE_RIGHT_FILE){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_box_right_file, parent, false);
            return new MensajesAdapter.MensajeViewHolder(view);
        } else if(viewType == MSG_TYPE_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_box_left, parent, false);
            return new MensajesAdapter.MensajeViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.message_box_left_file, parent, false);
            return new MensajesAdapter.MensajeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MensajesAdapter.MensajeViewHolder holder, int position){
        Message message = listaMensajes.get(position);
        holder.show_message.setText(message.getMessage());
        if(message.getMultimedia() != null){
            Picasso.get().load(message.getMultimedia()).into(holder.show_multimedia);
        }

    }

    @Override
    public int getItemCount() {return listaMensajes.size();}

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView show_multimedia;

        public MensajeViewHolder (View itemView){
            super(itemView);
            show_message = (TextView) itemView.findViewById(R.id.textMessage);
            if(itemView.findViewById(R.id.imageView) != null) {
                show_multimedia = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }

    }

    @Override
    public int getItemViewType(int position){
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(listaMensajes.get(position).getMultimedia() == null) {
            if (listaMensajes.get(position).getSender().equals(fuser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        } else {
            if (listaMensajes.get(position).getSender().equals(fuser.getUid())) {
                return MSG_TYPE_RIGHT_FILE;
            } else {
                return MSG_TYPE_LEFT_FILE;
            }
        }
    }
}