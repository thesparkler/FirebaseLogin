package com.example.firebaselogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SubmitUserDetailActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDataBaseRef = FirebaseDatabase.getInstance().getReference();

    EditText fname, lname;
    Button btnSubmit;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_user_detail);

        init();
        onClicks();
    }

    private void init()
    {
        fname = findViewById(R.id.edt_first_name);
        lname = findViewById(R.id.edt_last_name);
        btnSubmit = findViewById(R.id.btn_submit);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Setting up account..");
        mProgressDialog.setMessage("Please wait while setting up your account");
    }

    private void onClicks() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    mProgressDialog.show();
                    storeDetailInDataBase(fname.getText().toString(), lname.getText().toString());
                }
            }
        });
    }

    private void storeDetailInDataBase(String firstName, String lastName) {
        String loggedUserUniqueId = mAuth.getCurrentUser().getUid();
        String loggedUserPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        HashMap<String, String> map = new HashMap<>();
        map.put("FirstName", firstName );
        map.put("Last Name", lastName);
        map.put("Phone", loggedUserPhoneNumber);

        mDataBaseRef.child("Users").child(loggedUserUniqueId).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(SubmitUserDetailActivity.this, "Detail Submitted Successfully..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SubmitUserDetailActivity.this, UserDetailActivity.class));
                            finish();
                        }
                        else
                        {
                            mProgressDialog.hide();
                            Toast.makeText(SubmitUserDetailActivity.this, "Error while submitting detail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateData() {
        if(fname.getText().toString().isEmpty()) {
            Toast.makeText(SubmitUserDetailActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(lname.getText().toString().isEmpty()) {
            Toast.makeText(SubmitUserDetailActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
