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
                }else{
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    DisplayPets displayPets = new DisplayPets();
                    transaction.replace(R.id.fragment_container, displayPets).commit();
                }
            }
        });
    }
}
