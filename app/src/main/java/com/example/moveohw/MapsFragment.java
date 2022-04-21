package com.example.moveohw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MapsFragment extends Fragment {
    Context mContext;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    int boundflag=0;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            mAuth = FirebaseAuth.getInstance();
            user=mAuth.getCurrentUser();
            LatLngBounds.Builder boundsbuilder=LatLngBounds.builder();
            Query query=db.collection("notes").document(user.getUid())
                    .collection("mynotes").orderBy("date",Query.Direction.DESCENDING);

            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                    boundflag=1;
                                    LatLng newmark = new LatLng((Double)document.get("latitude"), (Double)document.get("longitude"));
                                    System.out.println("long and lat"+(Double)document.get("longitude")+" "+ (Double)document.get("latitude"));
                                    boundsbuilder.include(newmark);
                                    googleMap.addMarker(new MarkerOptions().position(newmark).title((String)document.get("title")
                                    ).snippet((String)document.get("content")));







                                }
                                if(boundflag==1) {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsbuilder.build(), 1000, 1000, 0));
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());

                            }
                        }
                    });
           // LatLng sydney = new LatLng(-34, 151);

            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}