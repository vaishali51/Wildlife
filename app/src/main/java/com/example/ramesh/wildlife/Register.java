package com.example.ramesh.wildlife;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hp on 10/7/2017.
 */

public class Register extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText mEmailField, mPasswordField, mNameField, mContactField, petSize, petColor, petName;
    Uri mProfilePic;
    Button register_btn;
    ImageView profileImage;
    Button uploadButton;
    Uri downloadUrl = null;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mEmailField = (EditText) view.findViewById(R.id.email_edit);
        mPasswordField = (EditText) view.findViewById(R.id.password_edit);
        mNameField = (EditText) view.findViewById(R.id.name_edit);
        mContactField = (EditText) view.findViewById(R.id.phone_edit);
        petName = (EditText) view.findViewById(R.id.namePet_edit);
        petColor = (EditText)view.findViewById(R.id.color_edit);
        petSize = (EditText)view.findViewById(R.id.size_edit);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        register_btn = (Button) view.findViewById(R.id.register_button);
        uploadButton = (Button) view.findViewById(R.id.upload_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    if (mEmailField.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Email field cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (mPasswordField.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Password field cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (mContactField.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter your Contact Number", Toast.LENGTH_SHORT).show();
                    } else if (mNameField.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Name field cannot be empty", Toast.LENGTH_SHORT).show();
                    }else if (petName.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter name of your pet", Toast.LENGTH_SHORT).show();
                    } else if (petSize.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter size of your pet", Toast.LENGTH_SHORT).show();
                    } else if (petColor.getText().toString().trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Enter color of your pet", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "All fields are filled", Toast.LENGTH_SHORT).show();
                        String email = mEmailField.getText().toString();
                        String password = mPasswordField.getText().toString();
                        createAccount(email, password);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please wait");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                    }
                }else {
                    Toast.makeText(getActivity(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mProfilePic = data.getData();
            uploadImage();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        StorageReference imageRef = storage.getReference();
        final String path = "petimages/" + UUID.randomUUID().toString() + "/" + mProfilePic.getLastPathSegment();
        StorageReference img = imageRef.child(path);
        img.putFile(mProfilePic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri url = taskSnapshot.getDownloadUrl();
                Picasso.with(getContext()).load(url).error(R.drawable.blank_pet).into(profileImage);
                setUrl(url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to Upload Image", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //calculating progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                //displaying percentage in progress dialog
                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }

    private void setUrl(Uri url){
        downloadUrl = url;
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    HashMap<String, Object> insertData = new HashMap<String, Object>();
                    insertData.put("User ID", user.getUid());
                    insertData.put("Email", user.getEmail());
                    insertData.put("Name", mNameField.getText().toString().trim());
                    //insertData.put("PetName", petName.getText().toString().trim());
                    //insertData.put("PetColor", petColor.getText().toString().trim());
                    //insertData.put("PetSize", petSize.getText().toString().trim());
                    insertData.put("Contact", mContactField.getText().toString().trim());

                    mDatabase.child(user.getUid()).setValue(insertData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                        }
                    });

                    HashMap<String, Object> petInfo = new HashMap<String, Object>();
                    petInfo.put("PetName", petName.getText().toString().trim());
                    petInfo.put("PetColor", petColor.getText().toString().trim());
                    petInfo.put("PetSize", petSize.getText().toString().trim());
                    if(downloadUrl==null)
                        petInfo.put("PetImage", "empty");
                    else
                        petInfo.put("PetImage", downloadUrl.toString());

                    mDatabase.child(user.getUid()).push().updateChildren(petInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "Registration Successful", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Registration FAILED:" + task.getException(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }
    }
