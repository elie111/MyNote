package com.example.moveohw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigateFragments   {
   // private FirebaseAuth mAuth;
    public static final String MYPREF = "MyPref";
    private FirebaseAuth mAuth;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, Home.class));

        }
    navigateFrag(new LoginFragment(),false);
       // mAuth = FirebaseAuth.getInstance();
        //pref = getSharedPreferences(MYPREF,0);


//        mAuth.createUserWithEmailAndPassword(pref.getString("username",""), pref.getString("password",""))
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
////                                        Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Toast.makeText(getApplicationContext(), "Authentication successful.",
//                                    Toast.LENGTH_SHORT).show();
//                            //  updateUI(user);
//                        }
////                                     else {
////                                        // If sign in fails, display a message to the user.
////                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                        Toast.makeText(getApplicationContext(), "Authentication failed.",
//                                Toast.LENGTH_SHORT).show();
////                                        updateUI(null);
////                                    }
//
//                        // ...
//                    }
//                });



    }
    public void navigateFrag(Fragment fragment, Boolean addToStack) {;
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction().replace(R.id.HomePage,fragment);
        if(addToStack){
            transaction.addToBackStack(null);

        }
        transaction.commit();
    }



}