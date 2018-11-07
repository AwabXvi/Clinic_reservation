package com.bahri.crimnal.clinical;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuth extends AppCompatActivity {
    private static final String TAG = "com.bahri.crimnal.clinical";
    private FirebaseAuth mAuth;
    private EditText phoneNumber;
    private Spinner spinner;
    private Button Verify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        mAuth = FirebaseAuth.getInstance();
        Verify = (Button) findViewById(R.id.Verify);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryCode.countryNames));
        spinner.setSelection(173);
        phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        Verify.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code = CountryCode.countryAreaCodes[spinner.getSelectedItemPosition()];
                        String PhoneNumber = phoneNumber.getText().toString().trim();

                        if (PhoneNumber.isEmpty() || PhoneNumber.length() < 9) {
                            phoneNumber.setError("Enter a valid mobile");
                            phoneNumber.requestFocus();
                            return;
                        }
                        String mobile = "+" + code + PhoneNumber;
                        Intent intent = new Intent(PhoneAuth.this, Verification.class);
                        intent.putExtra("phonenumber", mobile);
                        startActivity(intent);


                    }
                }
        );
    }
}


