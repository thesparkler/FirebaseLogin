package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class OtpVerificationActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mPhoneCallBack;

    EditText edtOTP;
    Button btnVerify;
    TextView tvOtpSentPhoneNumberText, resendOtpText;
    String verificationId, resendToken, phoneNumber;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        init();
        getDataFromLoginActivity();
        onClicks();
        setOtpTimer();
        setResendFirebasePhoneAuthCallBack();
    }

    private void init() {
        edtOTP = findViewById(R.id.edt_otp);
        btnVerify = findViewById(R.id.btn_verify);
        tvOtpSentPhoneNumberText = findViewById(R.id.tv_otp_sent_phone_number);
        resendOtpText = findViewById(R.id.tv_resend_otp);
        mProgressDialog = new ProgressDialog(this);
    }

    private void getDataFromLoginActivity() {
        phoneNumber = getIntent().getStringExtra("phone_number");
        resendToken = getIntent().getStringExtra("resendToken");
        verificationId = getIntent().getStringExtra("verification_id");
        tvOtpSentPhoneNumberText.setText("+91-"+ phoneNumber);
    }

    private void setOtpTimer() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                resendOtpText.setTextColor(getResources().getColor(R.color.grey));
                resendOtpText.setText("Wait 30 seconds : "+ l / 1000);
                resendOtpText.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendOtpText.setTextColor(getResources().getColor(R.color.blue));
                resendOtpText.setText("Resend Otp");
                resendOtpText.setEnabled(true);
            }
        }.start();
    }

    private void onClicks() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOtpEntered()) {
                    mProgressDialog.setTitle("Verifying..");
                    mProgressDialog.setMessage("Please wait verifying entered OTp..");
                    verifyOtp();
                }
                else
                {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter 6 digit OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resendOtpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendOtpToNumber(phoneNumber);
            }
        });
    }

    private boolean isOtpEntered() {
        return edtOTP.getText().toString().length() == 6;
    }

    private void resendOtpToNumber(String phoneNumber)
    {
        mProgressDialog.setTitle("Sending..");
        mProgressDialog.setMessage("Please Wait while sending OTP..");
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber
                , 30
                , TimeUnit.SECONDS
                , this
                , mPhoneCallBack);
    }

    private void verifyOtp() {
        String enteredOtp = edtOTP.getText().toString();
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, enteredOtp);
        loginWithGivenCredential(phoneAuthCredential);
    }

    private void loginWithGivenCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpVerificationActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            checkUserIsAlreadyExistInDatabase(task);
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                            mProgressDialog.hide();
                        }
                    }
                });
    }

    private void checkUserIsAlreadyExistInDatabase(Task<AuthResult> task) {
        final String loggedUserUniqueId = task.getResult().getUser().getUid();
        mDataBaseRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(loggedUserUniqueId).exists()) {
                    startActivity(new Intent(OtpVerificationActivity.this, UserDetailActivity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(OtpVerificationActivity.this, SubmitUserDetailActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setResendFirebasePhoneAuthCallBack() {
        mPhoneCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mProgressDialog.hide();
                loginWithGivenCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mProgressDialog.hide();
                Toast.makeText(OtpVerificationActivity.this, "Error Login :", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mProgressDialog.hide();
                Toast.makeText(OtpVerificationActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                setOtpTimer();

            }
        };
    }
}
