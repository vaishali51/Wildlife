package com.example.ramesh.wildlife;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by hp on 10/7/2017.
 */

public class MyPets extends Fragment {

    Button moreInfo, loginRegister;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_pets, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        moreInfo = (Button) view.findViewById(R.id.viewInfo);
        loginRegister = (Button) view.findViewById(R.id.login_register);
        firebaseAuth = FirebaseAuth.getInstance();
        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Login login = new Login();
                    fragmentTransaction.replace(R.id.fragment_container, login).commit();
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }
            }
        };
    }
}
