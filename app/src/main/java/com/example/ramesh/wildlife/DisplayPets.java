package com.example.ramesh.wildlife;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hp on 10/7/2017.
 */

public class DisplayPets extends Fragment {

    DatabaseReference databaseReference;
    ListView petLists;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Button logout, addPet;
    ArrayList<HashMap<String,String>> arrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.display_pets, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        petLists = (ListView)view.findViewById(R.id.listPets);
        logout = (Button)view.findViewById(R.id.logout);
        addPet = (Button)view.findViewById(R.id.add_pet);
        addPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.add_pet_details);
                dialog.setTitle("Add pet");
                final EditText petName = (EditText)dialog.findViewById(R.id.editPetName);
                final EditText petColor = (EditText)dialog.findViewById(R.id.editPetColor);
                final EditText petSize = (EditText)dialog.findViewById(R.id.editPetSize);
                dialog.show();
                Button add = (Button) dialog.findViewById(R.id.action_add_pet);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> petInfo = new HashMap<String, Object>();
                        petInfo.put("PetName", petName.getText().toString().trim());
                        petInfo.put("PetColor", petColor.getText().toString().trim());
                        petInfo.put("PetSize", petSize.getText().toString().trim());
                        petInfo.put("PetImage", "empty");
                        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wildlife-6ff82.firebaseio.com/Users");
                        final FirebaseUser user=firebaseAuth.getCurrentUser();
                    databaseReference.child(user.getUid()).push().updateChildren(petInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Pet added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        retrievePets();
                    }
                });
                    }
                });

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Login login = new Login();
                fragmentTransaction.replace(R.id.fragment_container, login).commit();
            }
        });
        retrievePets();
    }

    private void retrievePets() {
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://wildlife-6ff82.firebaseio.com/Users");
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {

                    if(postSnapshot.child("Email").getValue(String.class).equals(user.getEmail())){

                        for(DataSnapshot s : postSnapshot.getChildren()){
                            if(!s.getKey().equals("Name") && !s.getKey().equals("Email") &&
                                    !s.getKey().equals("Contact") && !s.getKey().equals("User ID")){
                                String petName = s.child("PetName").getValue(String.class);
                                String petImage = s.child("PetImage").getValue(String.class);
                                String petSize = s.child("PetSize").getValue(String.class);
                                String petColor = s.child("PetColor").getValue(String.class);
                                HashMap<String, String> pet = new HashMap<String, String>();
                                pet.put("PetName", petName);
                                pet.put("PetColor", petColor);
                                pet.put("PetSize", petSize);
                                pet.put("PetImage", petImage);
                                arrayList.add(pet);
                            }
                        }

                    }
                }
                setArrayList(arrayList);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    private void setArrayList(ArrayList<HashMap<String, String>> arrayList) {
        this.arrayList = arrayList;
        petLists.setAdapter(new SetAdapter(arrayList));
    }


    public class SetAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> listPets;
        SetAdapter(ArrayList<HashMap<String, String>> arrayList){
            listPets = arrayList;
        }

        @Override
        public int getCount() {
            return listPets.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflate = getActivity().getLayoutInflater();
            convertView = inflate.inflate(R.layout.pet_item, null);
            TextView petName = (TextView) convertView.findViewById(R.id.pet_item_name);
            final ImageView petImage = (ImageView) convertView.findViewById(R.id.pet_item_image);
            final TextView petColor = (TextView) convertView.findViewById(R.id.pet_item_color);
            TextView petSize = (TextView) convertView.findViewById(R.id.pet_item_size);
            petName.setText(listPets.get(position).get("PetName"));
            petColor.setText(listPets.get(position).get("PetColor"));
            petSize.setText(listPets.get(position).get("PetSize"));
            String image = listPets.get(position).get("PetImage");
            if(image.equals("empty")){
                petImage.setImageResource(R.drawable.blank_pet);
            }else {
                Picasso.with(getContext()).load(Uri.parse(image)).error(R.drawable.blank_pet).into(petImage);
            }
            return convertView;
        }
    }
}
