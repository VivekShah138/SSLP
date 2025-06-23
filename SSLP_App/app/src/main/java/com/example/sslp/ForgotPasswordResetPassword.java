package com.example.sslp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class ForgotPasswordResetPassword extends AppCompatActivity {

    private ImageButton buttonBackButton;
    private String oobCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_reset_password);
        buttonBackButton = findViewById(R.id.imageButtonBackForgotPasswordResetPassword);

//        // Get the link that opened this activity
//        Uri data = getIntent().getData();
//
//        if (data != null) {
//            // Extract the oobCode parameter from the link
//            oobCode = data.getQueryParameter("oobCode");
//            if (oobCode == null) {
//                Toast.makeText(this, "Invalid reset link.", Toast.LENGTH_LONG).show();
//                finish();
//                return;
//            }
//        }

        onClickedBackButton();
    }
    private void onClickedBackButton() {

        buttonBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordResetPassword.this, ForgotPasswordEmail.class);
                startActivity(intent);
            }
        });
    }
}