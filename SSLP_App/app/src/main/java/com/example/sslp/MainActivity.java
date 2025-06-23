package com.example.sslp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayoutToAdmin,linearLayoutToPetitioner;

    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayoutToAdmin = findViewById(R.id.linearLayoutMainToAdmin);
        linearLayoutToPetitioner = findViewById(R.id.linearLayoutMainToPetitioner);


        linearLayoutToAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
//                AddAdminDetails.addAdmin(MainActivity.this);
                checkLoggedIn();
            }
        });

        linearLayoutToPetitioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PetitionerEntryActivity.class);
                startActivity(intent);
                checkLoggedIn();
            }
        });
    }

    private void checkLoggedIn() {
        sharedPreferences = getSharedPreferences("LoginPrefs",MODE_PRIVATE);

        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        if(isLoggedIn){
            Toast.makeText(MainActivity.this,"You Are Already Logged In !!!!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,DisplayPetitions.class);
            startActivity(intent);
        }
    }

}