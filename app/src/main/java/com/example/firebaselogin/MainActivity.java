package com.example.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btn;
    FirebaseAuth mFireBaseAuth;
    FirebaseUser mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUserLoggedIn();

        mFireBaseAuth = FirebaseAuth.getInstance();
        btn = (Button)findViewById(R.id.login);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneLoginActivity.class));
            }
        });

    }

    private void checkUserLoggedIn() {
        if (mFireBaseUser != null) {
            startActivity(new Intent(this, HomeScreen.class));
            finish();
        }
    }
}
