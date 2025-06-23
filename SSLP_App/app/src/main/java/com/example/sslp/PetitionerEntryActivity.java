package com.example.sslp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PetitionerEntryActivity extends AppCompatActivity {

    private Button buttonToLoginPage,buttonToRegisterPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petitioner_entry);

        buttonToLoginPage = findViewById(R.id.buttonStartUpPageLogin);
        buttonToRegisterPage = findViewById(R.id.buttonStartUpPageRegister);
        buttonSelect();

    }


    private void buttonSelect() {
        buttonToLoginPage.setSelected(true);
        buttonToLoginPage.setTextColor(ContextCompat.getColorStateList(this,R.color.startup_page_login_button_text_bg));
        buttonToRegisterPage.setTextColor(ContextCompat.getColorStateList(this,R.color.startup_page_login_button_text_bg));


        buttonToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToLoginPage.setSelected(true);
                buttonToRegisterPage.setSelected(false);
                Intent intent = new Intent(PetitionerEntryActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });

        buttonToRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonToRegisterPage.setSelected(true);
                buttonToLoginPage.setSelected(false);
                Intent intent = new Intent(PetitionerEntryActivity.this, RegisterPage.class);
                startActivity(intent);
            }
        });
    }

}