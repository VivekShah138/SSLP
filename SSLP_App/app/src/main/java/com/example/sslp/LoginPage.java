package com.example.sslp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

public class LoginPage extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPassword;
    private TextView textViewForgotPassword;
    private Button buttonLoginPage;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmailId);
        editTextLoginPassword = findViewById(R.id.editTextLoginPagePassword);
        buttonLoginPage = findViewById(R.id.buttonLoginPageLogin);
        toolbar = findViewById(R.id.topAppBarLoginPage);
        checkBox = findViewById(R.id.checkBoxIsUserLoggedIn);
        textViewForgotPassword = findViewById(R.id.textViewLoginPageForgetPassword);
        firebaseAuth = FirebaseAuth.getInstance();


        onClickedRegisterButton();
        onClickedBackButton();
        onClickForgotPassword();
    }

    private void onClickForgotPassword() {
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, ForgotPasswordEmail.class);
                startActivity(intent);

            }
        });
    }

    private void onClickedBackButton() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onClickedRegisterButton() {

        buttonLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput()){
                    String email = editTextLoginEmail.getText().toString().trim();
                    String securedPassword = editTextLoginPassword.getText().toString().trim();
                    userLoggedInToFirebase(email,securedPassword);
                }
            }
        });
    }

    private void userLoggedInToFirebase(String email, String password) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    saveUserFromFirebase(firebaseAuth);
                    clearAllSections();
                    Toast.makeText(LoginPage.this,"You Are Logged In !!!!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginPage.this,DisplayPetitions.class);
                    startActivity(intent);
                }
                else{
                    String errorMessage = "Wrong Credentials,Login Failed";
                    if(task.getException() != null){
                        Toast.makeText(LoginPage.this,errorMessage,Toast.LENGTH_SHORT).show();
                        clearAllSections();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String errorMessage = "Wrong Credentials,Login Failed";
                Toast.makeText(LoginPage.this,errorMessage,Toast.LENGTH_SHORT).show();
                clearAllSections();
            }
        });

    }

    private void saveUserFromFirebase(FirebaseAuth firebaseAuth) {
        String userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if(dataSnapshot.exists()){
                        User user = dataSnapshot.getValue(User.class);
                        if(user != null){
                            saveSharedPreferences(user);
                        }
                    }
                    else {
                        Toast.makeText(LoginPage.this,"Unable to Retrieve Data From firebase",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void saveSharedPreferences(User user) {
        sharedPreferences = getSharedPreferences("LoginPrefs",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(checkBox.isChecked()){
            editor.putBoolean("isLoggedIn",true);
        }
        else{
            editor.putBoolean("isLoggedIn",false);
        }
        editor.putString("userEmail", user.getUserEmail());
        editor.putString("userName", user.getUserName());
        editor.putString("userId", user.getUserID());
        editor.commit();
    }

    private void clearAllSections(){
        editTextLoginEmail.setText("");
        editTextLoginPassword.setText("");


    }

    private boolean validInput() {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            int errorMessage = R.string.errorRegisterPageEmptyEmail;
            showAlertDialog(errorMessage);
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            int errorMessage = R.string.errorRegisterPageInvalidEmail;
            showAlertDialog(errorMessage);
            editTextLoginEmail.setText("");
        }
        else if (TextUtils.isEmpty(password)) {
            int errorMessage = R.string.errorRegisterPageEmptyPassword;
            showAlertDialog(errorMessage);
        }
        else{
            return true;
        }
        return false;
    }

    private void showAlertDialog(int errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
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

    private String encryptPassword() {
        String password = editTextLoginPassword.getText().toString().trim();
        try{
            byte[] bytePassword = password.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] byteSecuredPassword = messageDigest.digest(bytePassword);
            StringBuilder stringBuilder = new StringBuilder();
            for(byte b : byteSecuredPassword){

                stringBuilder.append(String.format("%02X",(b & 0xFF)));
            }
            String securedPassword = stringBuilder.toString().trim();
            return securedPassword;
        }catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                Toast.makeText(LoginPage.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginPage.this, R.string.errorEncryption, Toast.LENGTH_SHORT).show();
            }
            return "Encryption Error";
        }
    }
}