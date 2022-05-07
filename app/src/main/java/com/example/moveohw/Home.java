package com.example.moveohw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity implements NavigateFragments {
    private BottomNavigationView navigationView;
    private FirebaseAuth mAuth;
    Context ctx;
    public static final String MYPREF = "MyPref";
    SharedPreferences pref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = getSharedPreferences(MYPREF,0);
        SharedPreferences.Editor editor = pref.edit();
        mAuth = FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        Toast toast = Toast.makeText(this, "welcome", Toast.LENGTH_SHORT);
        toast.show();
        navigationView = findViewById(R.id.navibarAdminMain);


        navigateFrag(new NotesFragment(),false);
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                        switch (item.getItemId()) {
                            case R.id.bottomNavigationUserMenuId:


                                navigateFrag(new NotesFragment(),false);
                                break;
                            case R.id.bottomNavigationLeadersMenuId:

                                navigateFrag(new MapsFragment(),false);



                                break;
                            case R.id.bottomNavigationMoreMenuId:

                                PopupMenu popup = new PopupMenu(Home.this, findViewById(R.id.bottomNavigationMoreMenuId));
                                MenuInflater inflater = popup.getMenuInflater();
                                inflater.inflate(R.menu.mymenu, popup.getMenu());
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    public boolean onMenuItemClick(MenuItem item) {
                                        if(item.getItemId()==R.id.logout) {
                                            startActivity(new Intent(Home.this, MainActivity.class));

                                       mAuth.signOut();
                                        }
                                        if(item.getItemId()==R.id.oldtonew) {
                                            editor.putInt("sorting",1);
                                            editor.commit();
                                            navigateFrag(new NotesFragment(),false);
                                        }
                                        if(item.getItemId()==R.id.newtoold) {
                                            editor.putInt("sorting",0);
                                            editor.commit();
                                            navigateFrag(new NotesFragment(),false);



                                        }
                                        return true;
                                    }
                                });
                                popup.show();


                                break;

                        }
                        return true;
                    }
                });
    }
    public void navigateFrag(Fragment fragment, Boolean addToStack) {;
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction().replace(R.id.homemainlayout,fragment);
        if(addToStack){
            transaction.addToBackStack(null);

        }
        transaction.commit();
    }
    @Override
    public void onBackPressed() {

    }
}