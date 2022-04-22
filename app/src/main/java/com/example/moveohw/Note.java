package com.example.moveohw;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Note extends Fragment {

EditText header,body;
ImageButton returnbtn,editbtn,deletebtn,gallerybtn;
    public static final String MYPREF = "MyPref";
    SharedPreferences pref;
    Context mContext;
    LocationManager locationManager;
    Double latitude, longitude;
    private static final int REQUEST_LOCATION = 1;
    Boolean editMode=false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    DocumentReference docref;
    LinearLayout background;
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
        deletebtn=view.findViewById(R.id.trashbtn);
        gallerybtn=view.findViewById(R.id.galleryid);
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

    docref=db.collection("notes").document(user.getUid())
            .collection("mynotes").document(id);
    gallerybtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            openSomeActivityForResult();
        }
    });
    deletebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog diaBox = AskOption();
            diaBox.show();


        }
    });
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref.getInt("mapornote",0)==1){
                    editor.putInt("mapornote",0);
                    editor.commit();
                    ((Home)getActivity()).navigateFrag(new MapsFragment(),false);
                }
                else{
                ((Home)getActivity()).navigateFrag(new NotesFragment(),false);
            }}
        });
        editMode=pref.getBoolean("mode",false);
        if(editMode){
            editor.putBoolean("mode",false);
            header.setEnabled(true);
            body.setEnabled(true);
            editbtn.setImageResource(R.drawable.check);
        }
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode){
                    ActivityCompat.requestPermissions( getActivity(),
                            new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);


                    locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        OnGPS();
                    } else {
                        getLocation();
                    }

                    header.setEnabled(false);
                    body.setEnabled(false);
                    editMode=false;
                    editbtn.setImageResource(R.drawable.pencil);
                    Map<String,Object> note=new HashMap<>();
                    note.put("title",header.getText().toString());
                    note.put("content",body.getText().toString());
                    note.put("date",new Date());
                    note.put("latitude",latitude);
                    note.put("longitude",longitude);

                    docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    });
                    Toast toast=Toast.makeText(mContext,"saved",Toast.LENGTH_SHORT);

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
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(mContext)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.trash)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        docref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Error deleting document", e);
                                    }
                                });

                        if(pref.getInt("mapornote",0)==1){
                            editor.putInt("mapornote",0);
                            editor.commit();
                            ((Home)getActivity()).navigateFrag(new MapsFragment(),false);
                        }
                        else{
                            ((Home)getActivity()).navigateFrag(new NotesFragment(),false);
                        }
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }


    public void openSomeActivityForResult() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        someActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        System.out.println("entered if");
                        Uri photoUri =data.getData();

                        background=getActivity().findViewById(R.id.notelayouttextid);

                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(photoUri);
                            background .setBackground(Drawable.createFromStream(inputStream, photoUri.toString() ));
                        } catch (FileNotFoundException e) {
                            background .setBackground( getResources().getDrawable(R.drawable.notebook));

                        }
                    }
                }
            });
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
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, mLocationListener);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();


            } else {
                Toast.makeText(mContext, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };
}