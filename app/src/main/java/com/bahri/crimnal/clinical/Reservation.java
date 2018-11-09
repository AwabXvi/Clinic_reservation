package com.bahri.crimnal.clinical;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bahri.crimnal.clinical.models.Clinic;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reservation extends AppCompatActivity {

    private String TAG = "Reservation";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button Confirm;
    private Spinner doctors, clinics;
    private CollectionReference ref;
    private CollectionReference ref2;
    private CollectionReference ref3;
    private TextView price;
    private ArrayList<String> clinic_array;
    private ArrayList<String> doctors_array;
    private String name, doctor_price, price_String, start, end, phone;
    private static final String CHANNEL_ID = "channel_1";
    private TextView start_time, end_time;


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification_channel1";
            String description = "Channel_1 description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        createNotificationChannel();
        db = FirebaseFirestore.getInstance();
        ref = db.collection("Clinics");
        ref2 = db.collection("Prices");
        ref3 = db.collection("Reservation");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        clinic_array = new ArrayList<String>();
        doctors_array = new ArrayList<String>();
        Confirm = (Button) findViewById(R.id.Confirm_btn);
        doctors = (Spinner) findViewById(R.id.doctors_spinner);
        clinics = (Spinner) findViewById(R.id.clinic_spinner);
        price = (TextView) findViewById(R.id.price_txt);
        start_time = (TextView) findViewById(R.id.start_txt);
        end_time = (TextView) findViewById(R.id.end_txt);


        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle("Reservation completed")
                .setContentText("Your appointment has been successfully made")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        ref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {

                    String string = (String) documentSnapshots.get("name");
                    clinic_array.add(string);


                }

                clinics.setAdapter(new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_dropdown_item, clinic_array));
                clinics.setSelection(0);
                clinics.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                name = adapterView.getItemAtPosition(i).toString();

                                ref.document(name).get().addOnSuccessListener(
                                        new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                doctors_array = (ArrayList<String>) documentSnapshot.get("doctors");
                                                start = (String) documentSnapshot.get("start_time");
                                                end = (String) documentSnapshot.get("end_time");
                                                start_time.setText(start);
                                                end_time.setText(end);
                                                doctors.setAdapter(new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_dropdown_item, doctors_array));
                                                doctors.setSelection(0);
                                                doctors.setOnItemSelectedListener(
                                                        new AdapterView.OnItemSelectedListener() {
                                                            @Override
                                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                                doctor_price = adapterView.getItemAtPosition(i).toString();

                                                                getprice(doctor_price, name);
                                                                db.collection("Users").document(user.getUid()).get()
                                                                        .addOnSuccessListener(
                                                                                new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        phone = (String) documentSnapshot.get("Phone");
                                                                                    }
                                                                                }
                                                                        );
                                                                Confirm.setOnClickListener(
                                                                        new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View view) {
                                                                                final Map<String, Object> reservation = new HashMap<>();
                                                                                reservation.put("clinic_name", name);
                                                                                reservation.put("patient", user.getDisplayName());
                                                                                reservation.put("phone", phone);
                                                                                reservation.put("uid", user.getUid());
                                                                                reservation.put("photo_url", user.getPhotoUrl().toString());
                                                                                reservation.put("doctor", doctor_price);
                                                                                reservation.put("price", price_String);
                                                                                reservation.put("acceptence", false);
                                                                                ref3.document().set(reservation).addOnSuccessListener(
                                                                                        new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Toast.makeText(Reservation.this, "Your appointment has been successfully made", Toast.LENGTH_LONG).show();
                                                                                                Intent i = new Intent(Reservation.this, MainActivity.class);
                                                                                                startActivity(i);
                                                                                                notificationManager.notify(1, mBuilder.build());


                                                                                            }
                                                                                        }
                                                                                ).addOnFailureListener(
                                                                                        new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {

                                                                                            }
                                                                                        }
                                                                                );

                                                                            }
                                                                        }
                                                                );
                                                            }

                                                            @Override
                                                            public void onNothingSelected(AdapterView<?> adapterView) {
                                                                Log.d(TAG, "Doctors_spinner onNothing selected");
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                Log.d(TAG, "Clinics_spinner onNothing selected");
                            }
                        }
                );


            }

        });


    }

    private void getprice(final String doctor_price, String name) {

        ref2.document(name).get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Toast.makeText(Reservation.this, "Success " + doctor_price, Toast.LENGTH_LONG).show();
                        price_String = (String) documentSnapshot.get(doctor_price);

                        price.setText(price_String);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Reservation.this, "Failed " + price_String, Toast.LENGTH_LONG).show();
                    }
                }
        );

    }


    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user == null) {
            Intent i = new Intent(Reservation.this, StartActivity.class);
            startActivity(i);
            finish();
        } else {
            return;
        }
    }
}

