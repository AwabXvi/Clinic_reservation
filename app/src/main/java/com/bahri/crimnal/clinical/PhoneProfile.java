package com.bahri.crimnal.clinical;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhoneProfile extends AppCompatActivity {

    private EditText name;
    private Button Contunie;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String Name, number, Phoro_URl;

    private String TAG = "com.bahri.crimnal.clinical";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        name = (EditText) findViewById(R.id.Name);
        Contunie = (Button) findViewById(R.id.continue_btn);

        Contunie.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Name = name.getText().toString().trim();
                        if (Name.isEmpty() || number.length() < 11) {
                            name.setError("Enter a valid name.");
                            name.requestFocus();
                            return;
                        }

                        saveProfile();
                    }

                }
        );


    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    private void saveProfile() {
        Map<String, Object> User = new HashMap<>();
        User.put("Name", name.getText().toString());
        User.put("Phone", user.getPhoneNumber());
        User.put("PhotoUrl", user.getPhotoUrl().toString());
        User.put("UID", user.getUid());
        db.collection("Users").document(user.getUid()).set(User).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Intent i = new Intent(PhoneProfile.this, MainActivity.class);
                startActivity(i);
                return;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }
}
