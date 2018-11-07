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

public class Profile extends AppCompatActivity {

    private EditText name;
    private ImageView Profile_img;
    private ImageButton button;
    private Button Contunie;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private String Name, number, Phoro_URl;
    private EditText Phone;
    private String TAG = "com.bahri.crimnal.clinical";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Profile_img = findViewById(R.id.Profile_img);
        name = (EditText) findViewById(R.id.Name);
        Phone = (EditText) findViewById(R.id.phone);
        Contunie = (Button) findViewById(R.id.continue_btn);
        name.setEnabled(false);
        button = (ImageButton) findViewById(R.id.Edit_btn);


        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        name.setEnabled(true);

                    }
                }
        );
        Contunie.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        number = Phone.getText().toString().trim();
                        if (number.isEmpty() || number.length() < 11) {
                            Phone.setError("Enter a valid mobile");
                            Phone.requestFocus();
                            return;
                        }

                        saveProfile();
                    }

                }
        );

        loadProfile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadProfile();

    }

    private void loadProfile() {
        if (user.getPhotoUrl() != null) {
            Name = user.getDisplayName();
            Phoro_URl = user.getPhotoUrl().toString();
            Glide.with(this).load(Phoro_URl).into(Profile_img);
            if (Name != null) {
                name.setText(Name);
            }


        }
    }

    private void saveProfile() {
        Map<String, Object> User = new HashMap<>();
        User.put("Name", name.getText().toString());
        User.put("Phone", Phone.getText().toString().trim());
        User.put("PhotoUrl", user.getPhotoUrl().toString());
        User.put("UID", user.getUid());
        db.collection("Users").document(user.getUid()).set(User).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                Intent i = new Intent(Profile.this, MainActivity.class);
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
