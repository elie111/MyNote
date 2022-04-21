package com.example.moveohw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {
   private Button loginbtnlog,registerbtnlog;
    private EditText username,password;
    private FirebaseAuth mAuth;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(mContext, Home.class));

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_login,container,false);
        username=view.findViewById(R.id.usernameloginedit);
        password=view.findViewById(R.id.passwordloginedit);
      Button btn=view.findViewById(R.id.regeisterbtn);
              btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).navigateFrag(new RegisterFragment(),false);

            }
        });
        loginbtnlog=view.findViewById(R.id.loginbtn);
        loginbtnlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    mAuth.signInWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString().trim())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent myIntent = new Intent(mContext, Home.class);
                                        mContext.startActivity(myIntent);

                                    }

                                    else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });



                username.setText("");
                password.setText("");




            }
        });


        return view;
    }
    public boolean check_password(EditText password,EditText confirmation) {
        String pass1 = password.getText().toString();
        String pass2 = confirmation.getText().toString();
        if (! pass1.equals(pass2)){
            password.setError("Passwords are not the same");
            return false;
        }

        else if (! pass1.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
            password.setError("Password must be at least 8 characters, containing at least one number,one lower case and one upper case");
            return false;
        }
        return true;
    }
    public boolean checkEmail(EditText username){
        if (!username.getText().toString().matches(Patterns.EMAIL_ADDRESS.pattern())) {
            username.setError("Email must contain @domain. etc");
            return false;
        }
        return true;
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
