package com.example.moveohw;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NotesFragment extends Fragment  {
private Context mContext;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    ImageButton addnotebtn;
    LocationManager locationManager;
    Double latitude, longitude;
    public static final String MYPREF = "MyPref";
    SharedPreferences pref;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
    private static final int REQUEST_LOCATION = 1;
    int f=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        pref = mContext.getSharedPreferences(MYPREF,0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("mode",false);
        editor.commit();
        NoteAdapter noteAdapter;
        ArrayList<NoteItem> noteItemArrayList = new ArrayList<NoteItem>();





        addnotebtn=view.findViewById(R.id.addnotebtnid);
        addnotebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions( getActivity(),
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


                 locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    OnGPS();
                } else {
                    getLocation();
                }
                Map<String, Object> newnote = new HashMap<>();
                newnote.put("first", "");
                newnote.put("last", "");
                newnote.put("date", new Date());
                newnote.put("latitude",latitude);
                newnote.put("longitude",longitude);

                db.collection("notes").document(user.getUid()).collection("mynotes")
                        .add(newnote)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                            }
                        });

                Query query = db.collection("notes").document(user.getUid())
                        .collection("mynotes").orderBy("date", Query.Direction.DESCENDING);
                if(pref.getInt("sorting",0)==1) {
                    System.out.println("old to new");
                     query = db.collection("notes").document(user.getUid())
                            .collection("mynotes").orderBy("date", Query.Direction.ASCENDING);
                }

                query.get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData());
                                        editor.putString("ID", document.getId());
                                        editor.putBoolean("mode",true);
                                        editor.commit();
                                        break;
                                    }

                                } else {
                                    Log.w("TAG", "Error getting documents.", task.getException());

                                }
                            }
                        });

                ((Home)getActivity()).navigateFrag(new Note(),false);


            }


        });
        RecyclerView recyclerView = view.findViewById(R.id.recylenotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        noteAdapter = new NoteAdapter(noteItemArrayList);
        noteAdapter.setCtx(mContext);
        recyclerView.setAdapter(noteAdapter);

        String now = ISO_8601_FORMAT.format(new Date());
Query query=db.collection("notes").document(user.getUid())
        .collection("mynotes").orderBy("date",Query.Direction.DESCENDING);

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());


                                    NoteItem noteItem = new NoteItem( (String)document.get("content"),
                                            ((Timestamp)document.get("date")).toDate(),(String)document.get("title"),
                                            (Double)document.get("latitude"),(Double)document.get("longitude"));
                                    noteItem.setid(document.getId());
                                    System.out.println("document id is"+document.getId());
                                    noteItemArrayList.add(noteItem);






                            }
                            noteAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());

                        }
                    }
                });





        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
               latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();


            } else {
                Toast.makeText(mContext, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}