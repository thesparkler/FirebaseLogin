package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText edtPhoneNumber;
    Button btnPhoneNumber;
    ProgressDialog mProgressDialog;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mPhoneCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        init();
        onClicks();
        setCallBackFromFireBase();
    }

    private void init() {
        edtPhoneNumber = findViewById(R.id.edt_phone_number);
        btnPhoneNumber = findViewById(R.id.btn_next);
        mProgressDialog = new ProgressDialog(this);
    }

    private void onClicks() {
        btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPhoneNumberValid()) {
                    sendOtpToNumber(edtPhoneNumber.getText().toString());
                }
                else
                {
                    Toast.makeText(PhoneLoginActivity.this,
                            "Please enter valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // validation
    public boolean isPhoneNumberValid() {
        return edtPhoneNumber.getText().toString().length() == 10;
    }

    private void sendOtpToNumber(String phoneNumber) {
        mProgressDialog.setTitle("Sending Otp..");
        mProgressDialog.setMessage("Please wait while sending otp");
        mProgressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber
            , 30
            , TimeUnit.SECONDS
            , this
            , mPhoneCallBack);
    }

    private void setCallBackFromFireBase() {
        mPhoneCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mProgressDialog.hide();
                loginWithGivenCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mProgressDialog.show();
                Toast.makeText(PhoneLoginActivity.this, "Error Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                mProgressDialog.hide();
                Intent intent = new Intent(PhoneLoginActivity.this, OtpVerificationActivity.class);
                intent.putExtra("verification_id", verificationId);
                intent.putExtra("resendToken", forceResendingToken);
                intent.putExtra("phone_number", edtPhoneNumber.getText().toString());
                startActivity(intent);
                finish();
            }
        };
    }

    private void loginWithGivenCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(PhoneLoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                   checkUserIsAlreadyExistInDatabase(task);
                }
                else
                {
                    mProgressDialog.dismiss();
                    Toast.makeText(PhoneLoginActivity.this, "Error in Login :", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserIsAlreadyExistInDatabase(Task<AuthResult> task) {
        final String loggedUserUniqueId = task.getResult().getUser().getUid();
        mDatabaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(loggedUserUniqueId).exists()) {
                    startActivity(new Intent(PhoneLoginActivity.this, UserDetailActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(PhoneLoginActivity.this, SubmitUserDetailActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
