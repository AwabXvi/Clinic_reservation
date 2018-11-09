package com.bahri.crimnal.clinical;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class IntentService extends android.app.IntentService {

    private static final String CHANNEL_ID = "channel_2";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "IntentService";
    private CollectionReference ref;

    public IntentService() {
        super("IntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        ref = db.collection("Accepted_reservation");
        createNotificationChannel();
        Intent i = new Intent(IntentService.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Appointment Accepted")
                .setContentText("Your appointment has been accepted")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        final NotificationManagerCompat manger = NotificationManagerCompat.from(this);
        ref.whereEqualTo("name", user.getDisplayName()).addSnapshotListener(
                new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    manger.notify(2, builder.build());
                                    Log.d(TAG, "appointment accepted");
                                    break;
                                case MODIFIED:

                                    break;
                                case REMOVED:
                                    break;

                            }
                        }

                    }
                }
        );


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification_channel2";
            String description = "Channel_2 description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
