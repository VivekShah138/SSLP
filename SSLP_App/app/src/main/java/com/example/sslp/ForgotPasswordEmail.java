package com.example.sslp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordEmail extends AppCompatActivity {
    private TextView textViewResendEmail;
    private EditText editTextEmail;
    private Button buttonSendLink;
    private ImageButton buttonBackButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_email);
        editTextEmail = findViewById(R.id.editTextForgotPasswordEmailId);
        textViewResendEmail = findViewById(R.id.textViewResendLinkForgotPasswordEmailId);
        buttonSendLink = findViewById(R.id.buttonForgotPasswordEmailSendLink);
        buttonBackButton = findViewById(R.id.imageButtonBackForgotPasswordEmail);
        firebaseAuth = FirebaseAuth.getInstance();

        onClickSendLink();
        onClickResendLink();
        onClickedBackButton();
    }

    private void onClickResendLink() {


        textViewResendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = editTextEmail.getText().toString().trim();
                if(validEmail(userEmail)){
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordEmail.this,"Reset link sent to your email",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordEmail.this, MainActivity.class); // Change MainActivity to your app's entry point
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack and start fresh
                                startActivity(intent);
                                System.exit(0);
                            }
                            else {
                                Toast.makeText(ForgotPasswordEmail.this,"Error: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });
    }

    private void onClickSendLink() {

        buttonSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = editTextEmail.getText().toString().trim();
                if(validEmail(userEmail)){
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordEmail.this,"Reset link sent to your email",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPasswordEmail.this, MainActivity.class); // Change MainActivity to your app's entry point
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the stack and start fresh
                                startActivity(intent);
                                System.exit(0);
                            }
                            else {
                                Toast.makeText(ForgotPasswordEmail.this,"Error: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });



    }

    private boolean validEmail(String userEmail) {

        if(TextUtils.isEmpty(userEmail)){
            int errorMessage = R.string.errorRegisterPageEmptyEmail;
            showAlertDialog(errorMessage);
        }

        // Check if the email is valid or not
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            int errorMessage = R.string.errorRegisterPageInvalidEmail;
            showAlertDialog(errorMessage);
            editTextEmail.setText("");
        }
        else{
            return true;
        }
        return false;
    }

    private void showAlertDialog(int errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordEmail.this);
        builder.setTitle("Error");
        builder.setIcon(R.drawable.toast_error_icon);
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onClickedBackButton() {

        buttonBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordEmail.this, LoginPage.class);
                startActivity(intent);
            }
        });
    }
}