package com.example.moveohw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Note extends Fragment {

EditText header,body;
ImageButton returnbtn,editbtn;
    public static final String MYPREF = "MyPref";
    SharedPreferences pref;
    Context mContext;
    Boolean editMode=false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    private FirebaseAuth mAuth;
    SharedPreferences.Editor editor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = mContext.getSharedPreferences(MYPREF,0);
         editor= pref.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        returnbtn=view.findViewById(R.id.returnbtnid);
        editbtn=view.findViewById(R.id.editbtnid);
        header=view.findViewById(R.id.headernotetxtid);
        body=view.findViewById(R.id.noteitselftxtid);
        header.setEnabled(false);
        body.setEnabled(false);
        //editor.putString("ID","blahs");
       // editor.commit();
        String id=pref.getString("ID",null);
        System.out.println("id is "+id);

        Query query=db.collection("notes").document(user.getUid())
                .collection("mynotes").orderBy("date",Query.Direction.DESCENDING);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                if (document.getId() .equals( id)) {
                                    System.out.println("inside if");
                                    body.setText((String) document.get("content"));
                                    header.setText((String) document.get("title"));
                                }

                            }
                        }
                    }
                });

    DocumentReference docref=db.collection("notes").document(user.getUid())
            .collection("mynotes").document(id);
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Home)getActivity()).navigateFrag(new NotesFragment(),false);
            }
        });
        editMode=pref.getBoolean("mode",false);
        if(editMode){
            header.setEnabled(true);
            body.setEnabled(true);
            editbtn.setImageResource(R.drawable.check);
        }
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode){
                    header.setEnabled(false);
                    body.setEnabled(false);
                    editMode=false;
                    editbtn.setImageResource(R.drawable.pencil);
                    Map<String,Object> note=new HashMap<>();
                    note.put("title",header.getText().toString());
                    note.put("content",body.getText().toString());
                    note.put("date",new Date());

                    docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast toast=Toast.makeText(mContext,"changes saved",Toast.LENGTH_SHORT);
                        }
                    });


                }
                else{
                    header.setEnabled(true);
                    body.setEnabled(true);
                    editMode=true;
                    editbtn.setImageResource(R.drawable.check);

                }
            }
        });

        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}