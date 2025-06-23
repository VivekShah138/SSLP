package com.example.sslp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddPetition extends AppCompatActivity {
    private EditText editTextHomePagePetitionTitle,editTextHomePagePetitionDetails;
    private Button buttonAddPetition;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_petition);

        editTextHomePagePetitionTitle = findViewById(R.id.editTextHomePagePetitionTitle);
        editTextHomePagePetitionDetails = findViewById(R.id.editTextHomePagePetitionDetails);
        buttonAddPetition = findViewById(R.id.buttonAddPetition);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.topAppBarAddPetitionPage);

        onClickedBackButton();
        buttonAddPetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getThresholdValue();
            }
        });
    }

    private void onClickedBackButton() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show the back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPetition.this, DisplayPetitions.class);
                startActivity(intent);
            }
        });
    }

    private void addToDatabase(Integer thresholdValue) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Petitions");
        String title = editTextHomePagePetitionTitle.getText().toString().trim();
        String details = editTextHomePagePetitionDetails.getText().toString().trim();

        if(title.isEmpty() || details.isEmpty()){
            Toast.makeText(AddPetition.this,"Petitions Cannot be empty",Toast.LENGTH_SHORT).show();
        }
        else{
            String userId = firebaseAuth.getCurrentUser().getUid();
            String petitionId = databaseReference.push().getKey();

            Petitions petition = new Petitions(title,details,userId,new ArrayList<>());
            petition.setResponse_("Parliament will respond once signature reaches the threshold");
            petition.setPetitionId_(petitionId);
            petition.setThresholdSignatures_(thresholdValue);


            if(petition != null){
                databaseReference.child(petitionId).setValue(petition).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddPetition.this, "Petitions Added Sucessfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddPetition.this, DisplayPetitions.class);
                            startActivity(intent);
                            clearSections();
                        }
                        else {
                            Toast.makeText(AddPetition.this, "Failed to add Petition", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void getThresholdValue() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ThresholdValue");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    Integer thresholdValue = dataSnapshot.getValue(Integer.class);
                    addToDatabase(thresholdValue);
                }
                else{
                    Toast.makeText(AddPetition.this,"Unable to Retrieve Threshold Value",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearSections() {
        editTextHomePagePetitionDetails.setText("");
        editTextHomePagePetitionTitle.setText("");
    }
}