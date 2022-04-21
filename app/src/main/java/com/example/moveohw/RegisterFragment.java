package com.example.moveohw;

import android.content.Context;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment {
 Button loginbtnreg,registerbtnreg;
    private Context mContext;
 EditText username,password,confirmation;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register,container,false);
        mAuth = FirebaseAuth.getInstance();
        Button btn=view.findViewById(R.id.loginbtn2);
        username=view.findViewById(R.id.usernameregisteredit);
        password=view.findViewById(R.id.passwordregisteredit);
        confirmation=view.findViewById(R.id.confirmationgisteredit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).navigateFrag(new LoginFragment(),false);


            }
        });

        registerbtnreg=view.findViewById(R.id.regeisterbtn2);
        registerbtnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkEmail(username)&&check_password(password,confirmation)){

                    mAuth.createUserWithEmailAndPassword(username.getText().toString().trim()
                            , password.getText().toString().trim())
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(mContext, "Authentication successful.",
                                                Toast.LENGTH_SHORT).show();

                                        }
                                    else {
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(mContext, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                  }


                                }
                            });

                    ((MainActivity)getActivity()).navigateFrag(new LoginFragment(),false);
                }

                username.setText("");
                password.setText("");
                confirmation.setText("");



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
