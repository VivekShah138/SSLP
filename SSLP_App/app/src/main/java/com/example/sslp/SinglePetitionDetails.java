package com.example.sslp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SinglePetitionDetails extends AppCompatActivity {

    private TextView textViewPetitionTitle, textViewPetitionDetails, textViewSignatureNumber, textViewThresholdValue,textViewPetitionStatus,textViewPetitionResponse;
    private Button buttonSignPetition,buttonClosePetition;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_petition_details);
        textViewPetitionTitle = findViewById(R.id.textViewSinglePetitionDetailsPetitionTitle);
        textViewPetitionDetails = findViewById(R.id.textViewSinglePetitionDetailsPetitionDetails);
        textViewSignatureNumber = findViewById(R.id.textViewSinglePetitionDetailsSignatureNumber);
        textViewThresholdValue = findViewById(R.id.textViewSinglePetitionDetailsThresholdValue);
        textViewPetitionStatus = findViewById(R.id.textViewSinglePetitionDetailsPetitionStatus);
        textViewPetitionResponse = findViewById(R.id.textViewSinglePetitionDetailsPetitionResponse);
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBarSinglePetitionDetails);
        buttonSignPetition = findViewById(R.id.buttonSinglePetitionDetailsSignPetition);
        buttonClosePetition = findViewById(R.id.buttonSinglePetitionDetailsClosePetition);
        toolbar = findViewById(R.id.topAppBarSinglePetitionPage);

        Intent intent = getIntent();
        Petitions petition = (Petitions) intent.getSerializableExtra("Petition");

        setDetails(petition);
        signPetitionAction(petition);
        getUserRole(petition);
        onClickedBackButton();

    }

    private void onClickedBackButton() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SinglePetitionDetails.this, DisplayPetitions.class);
                startActivity(intent);
            }
        });
    }
    private void getUserRole(Petitions petition) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        UserRole.getUserRole(firebaseAuth, databaseReference, new UserRole.UserRoleCallback() {
            @Override
            public void onUserRoleRetrieved(String userRole) {
                setButtonVisibility(userRole,petition);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(SinglePetitionDetails.this,"UserRole Not Retried " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setButtonVisibility(String role,Petitions petition) {

        if(role.equals("Admin")){
            setResponse(petition);
        }
    }

    private void setResponse(Petitions petition) {
        Integer threshold = petition.getThresholdSignatures_();
        int signatures = petition.getSignatureSize_();
        if(threshold == null){
            Toast.makeText(SinglePetitionDetails.this,"Please Assign Threshold Signature",Toast.LENGTH_SHORT).show();
        }
        else if((threshold != null) && (signatures >= threshold)){
            buttonClosePetition.setVisibility(View.VISIBLE);
            closePetition(petition);
        }
    }

    private void closePetition(Petitions petition) {

        buttonClosePetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Enter Your Response");

                View dialogView = getLayoutInflater().inflate(R.layout.enter_response, null);
                builder.setView(dialogView);

                EditText addResponse = dialogView.findViewById(R.id.editTextEnterResponse);


                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String response = addResponse.getText().toString();
                        if (!response.isEmpty()) {

                            saveResponseValue(response,petition);

                            Toast.makeText(view.getContext(), "Response Added And Petition is Close", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(view.getContext(), "Please enter a value.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss(); // Close the dialog
                    }
                });
                builder.create().show();


            }
        });
    }

    private void saveResponseValue(String response,Petitions petition) {

        petition.setResponse_(response);
        petition.setClosed(true);
        petition.setStatus_();
        databaseReference = FirebaseDatabase.getInstance().getReference("Petitions");
        databaseReference.child(petition.getPetitionId_()).setValue(petition).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SinglePetitionDetails.this, "Response For Petition is Set Successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SinglePetitionDetails.this, "Unable to Set Response", Toast.LENGTH_SHORT).show();
                }
                setDetails(petition);
            }
        });


    }

    private void signPetitionAction(Petitions petition) {
        buttonSignPetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(petition.getIsClosed()){
                    Toast.makeText(SinglePetitionDetails.this,R.string.petitionClosed,Toast.LENGTH_SHORT).show();
                }
                else {
                    String signedUserID = firebaseAuth.getCurrentUser().getUid();

                    List<String> signatureUsers = petition.getSignature_();
                    if (signatureUsers == null) {
                        signatureUsers = new ArrayList<>();
                    }

                    if (!signatureUsers.contains(signedUserID)) {
                        petition.addSignature(signedUserID);
                        updateDatabase(petition);
                    } else {
                        Toast.makeText(SinglePetitionDetails.this, "You have Already Signed This Petition", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateDatabase(Petitions petition) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Petitions");
        databaseReference.child(petition.getPetitionId_()).setValue(petition).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SinglePetitionDetails.this,"Petition Signed ",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(SinglePetitionDetails.this,"Signing Petition Failed",Toast.LENGTH_SHORT).show();
                }
                setDetails(petition);
                getUserRole(petition);
            }
        });
    }

    private void setDetails(Petitions petition) {
        if(petition != null){
            textViewPetitionTitle.setText(petition.getTitle_());
            textViewPetitionDetails.setText(petition.getDetails_());
            textViewSignatureNumber.setText(String.format("%d",petition.getSignatureSize_()) + " Signatures");
            textViewPetitionStatus.setText(petition.getStatus_());
            if(!petition.getIsClosed()){

                textViewPetitionStatus.setTextColor(ContextCompat.getColor(SinglePetitionDetails.this, R.color.GreenA700));
            }
            else{
                textViewPetitionStatus.setTextColor(ContextCompat.getColor(SinglePetitionDetails.this, R.color.RedA700));
            }
            textViewPetitionResponse.setText(petition.getResponse_());
            int progress = 0;
            if(petition.getThresholdSignatures_() != null){
                textViewThresholdValue.setText(String.format("%d",petition.getThresholdSignatures_()));
                progress = progressBarUpdate(petition);
            }
            else if(petition.getThresholdSignatures_() == null){
                textViewThresholdValue.setText("No Threshold Assigned");
            }
            progressBar.setProgress(progress);
        }
    }

    private int progressBarUpdate(Petitions petition) {
        int progress = 0;
        if(petition.getThresholdSignatures_() != null){
            int thresholdValue = petition.getThresholdSignatures_();
            int signatures = petition.getSignatureSize_();
            progress = (signatures * 100) / thresholdValue;
        }
        return progress;
    }
}