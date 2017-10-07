package com.example.ramesh.wildlife;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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

/**
 * Created by hp on 10/7/2017.
 */

public class Login extends Fragment {

    EditText mEmail, mPassword;
    Button mLogin, mRegister;
    ProgressDialog dialog;
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mEmail = (EditText)view.findViewById(R.id.email);
        mPassword = (EditText)view.findViewById(R.id.password);
        mLogin = (Button)view.findViewById(R.id.login_button);
        mRegister = (Button)view.findViewById(R.id.register);
        firebaseAuth = FirebaseAuth.getInstance();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Please wait");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.show();
                    startSignIn();
                }else{
                    Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Register register = new Register();
                fragmentTransaction.replace(R.id.fragment_container, register).commit();
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void startSignIn() {

        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "Fields cannot be Empty", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        MyPets myPets = new MyPets();
                        fragmentTransaction.replace(R.id.fragment_container, myPets).commit();
                    } else {
                        Toast.makeText(getActivity(), "Login Failed:" + task.getException(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }
}
