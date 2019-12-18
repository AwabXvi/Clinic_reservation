package com.bahri.crimnal.clinical;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Verification extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String VeridicationId;
    private Button Continue;
    private EditText Codefield;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        Continue = (Button) findViewById(R.id.Signin);
        Codefield = (EditText) findViewById(R.id.code);
        progressDoalog = new ProgressDialog(Verification.this);
        progressDoalog.setIndeterminate(true);
        progressDoalog.setMessage("loading..");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mAuth = FirebaseAuth.getInstance();
        String PhoneNumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(PhoneNumber);
        Continue.setOnClickListener(
                    new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code = Codefield.getText().toString().trim();
                        if (code.isEmpty() || code.length() < 6) {
                            Codefield.setError("Enter valid code");
                            Codefield.requestFocus();
                            return;
                        }
                        progressDoalog.show();
                        //verifying the code entered manually
                        verifyCode(code);
                    }
                });

    }

    void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VeridicationId, code);
        signInWithPhoneAuthCredential(credential);

    }

    void sendVerificationCode(String PhoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                PhoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks
        );

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            progressDoalog.dismiss();
            super.onCodeSent(s, forceResendingToken);
            VeridicationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            progressDoalog.dismiss();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Codefield.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressDoalog.dismiss();
            Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(Verification.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    finish();
                    Intent intent = new Intent(Verification.this, PhoneProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(Verification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

