package com.example.moveohw;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    ArrayList<NoteItem> noteItemArrayList;

    Context ctx;
    public static final String MYPREF = "MyPref";
    SharedPreferences pref;
    int pos;
    String itemid;




    public NoteAdapter(ArrayList<NoteItem> noteItemArrayList) {
        this.noteItemArrayList = noteItemArrayList;

    }
    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
    public int getpos() {
        return this.pos;
    }

    public Context getCtxA() {
        return this.ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_note_item,parent,false);




        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        pref = ctx.getSharedPreferences(MYPREF,0);
        SharedPreferences.Editor editor = pref.edit();
         pos=holder.getAdapterPosition();
        NoteItem noteItem = noteItemArrayList.get(position);
        itemid=noteItem.getid();
        holder.title.setBackgroundColor(pref.getInt("theme1",ContextCompat.getColor(ctx,R.color.purple_700)));
        holder.ntext_linearlayout.setBackgroundColor(pref.getInt("theme2", ContextCompat.getColor(ctx,R.color.teal_200)));

        holder.ntext.setText(noteItem.getNoteText());
        holder.ndate=noteItem.getDate();
        holder.title.setText(noteItem.getTitle());
        SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("MMMM dd\nHH:mm aa");

        String now = ISO_8601_FORMAT.format(noteItem.getDate());
        holder.datetxt.setText(now);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemid=noteItemArrayList.get(holder.getAdapterPosition()).getid();
                editor.putString("ID",itemid);
                editor.commit();
                System.out.println("adapter id is "+pref.getString("ID",""));
                ((Home)getCtxA()).navigateFrag(new Note(),false);
            }
        });


    }

    @Override
    public int getItemCount() {
        return noteItemArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        TextView ntext;
        Date ndate;
        LinearLayout ntext_linearlayout;
        TextView title;
        TextView datetxt;
        Double latitude;
        Double longitude;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            ntext = itemView.findViewById(R.id.notiText);

            ntext_linearlayout=(LinearLayout)itemView.findViewById(R.id.notilayoutitem);

            title=itemView.findViewById(R.id.titleid);
            datetxt=itemView.findViewById(R.id.dateofnoteid);

        }
    }


}
