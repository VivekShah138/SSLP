package com.example.sslp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DisplayPetitions extends AppCompatActivity {

    private RecyclerView recyclerViewPetitionsDisplay;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewNumberOfPetitions;
    private Button buttonSetThreshold;
    private FloatingActionButton buttonAddPetitions;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;

    private SharedPreferences sharedPreferences;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_petitions);

        recyclerViewPetitionsDisplay = findViewById(R.id.recyclerviewDisplayPetitionsPetitionDisplay);
        textViewNumberOfPetitions = findViewById(R.id.textViewDisplayPetitionsNumberOfPetitions);
        buttonAddPetitions = findViewById(R.id.buttonDisplayPetitionsAddPetitions);
        buttonSetThreshold = findViewById(R.id.buttonDisplayPetitionsSetThreshold);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutDisplayPetitionsPetitionDisplay);
        toolbar = findViewById(R.id.topAppBarDisplayPage);
        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        setToolbarTitle();


        getUserRole();
        getPetitionDetails();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPetitionDetails();
                setToolbarTitle();
            }
        });
    }

    private void setToolbarTitle() {
        sharedPreferences = getSharedPreferences("LoginPrefs",MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName",null);
        if(userName != null){
            toolbar.setTitle("Hey, " + userName);
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
            int color = typedValue.data;
            toolbar.setTitleTextColor(color);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout){
            signOutFromFirebase();
            clearSharedPreferences();
        }
        return true;
    }

    private void signOutFromFirebase() {
        firebaseAuth.signOut();


        Intent intent = new Intent(DisplayPetitions.this, MainActivity.class);
        startActivity(intent);

        Toast.makeText(DisplayPetitions.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
    }

    private void clearSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }



    private void getUserRole() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        UserRole.getUserRole(firebaseAuth, databaseReference, new UserRole.UserRoleCallback() {
            @Override
            public void onUserRoleRetrieved(String userRole) {
                setButtonVisibility(userRole);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DisplayPetitions.this,"UserRole Not Retried " + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setButtonVisibility(String userRole) {
        if(userRole.equals("Admin")){
            buttonSetThreshold.setVisibility(View.VISIBLE);
            setThreshold();
        }
        else if(userRole.equals("User")){
            buttonAddPetitions.setVisibility(View.VISIBLE);


            buttonAddPetitions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DisplayPetitions.this, AddPetition.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void setThreshold() {
        buttonSetThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Set Threshold Value");

                    View dialogView = getLayoutInflater().inflate(R.layout.add_threshold, null);
                    builder.setView(dialogView);

                    EditText addThreshold = dialogView.findViewById(R.id.editTextAddThreshold);

                    builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String thresholdValue = addThreshold.getText().toString();
                            if (!thresholdValue.isEmpty()) {

                                int threshold = Integer.parseInt(thresholdValue);
                                saveThresholdValue(threshold);

                                Toast.makeText(view.getContext(), "Threshold set to: " + threshold, Toast.LENGTH_SHORT).show();
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
//            }
        });
    }

    private void saveThresholdValue(int threshold) {
        databaseReference = FirebaseDatabase.getInstance().getReference("ThresholdValue");
        databaseReference.setValue(threshold).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DisplayPetitions.this, "Threshold For Petition is Set Successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DisplayPetitions.this, "Unable To Set Threshold", Toast.LENGTH_SHORT).show();
                }
                setThresholdForAllPetitions(threshold);
            }

        });
    }

    private void setThresholdForAllPetitions(int threshold) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Petitions");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot petitionSnapshot : snapshot.getChildren()){
                    Petitions petition = petitionSnapshot.getValue(Petitions.class);

                    if(!petition.getIsClosed() && petition != null){
                        petition.setThresholdSignatures_(threshold);
                        petitionSnapshot.getRef().setValue(petition);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayPetitions.this, "Failed to update petitions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPetitionDetails() {
        swipeRefreshLayout.setRefreshing(true);
        List<Petitions>data = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Petitions");
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot dataSnapshot = task.getResult();
                    if(dataSnapshot.exists()){

                        for(DataSnapshot petitionSnapshot : dataSnapshot.getChildren()){
                            Petitions petitions = petitionSnapshot.getValue(Petitions.class);
                            if(petitions != null){
                                data.add(petitions);
                            }
                        }
                        updateRecyclerView(data);

                    }
                    else{
                        updateRecyclerView(data);
                    }
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    private void updateRecyclerView(List<Petitions> data) {

        data.sort(new Comparator<Petitions>() {
            @Override
            public int compare(Petitions p1, Petitions p2) {
                // Compare based on status first ("open" comes before "closed")
                int statusComparison = p2.getStatus_().compareToIgnoreCase(p1.getStatus_());
                if (statusComparison != 0) {
                    return statusComparison; // Return the result if statuses are different
                }

                // If statuses are the same, compare by signature count in descending order
                return Integer.compare(p2.getSignatureSize_(), p1.getSignatureSize_());
            }
        });


        RecyclerViewPetitions recyclerViewPetitions = new RecyclerViewPetitions(this,data);
        recyclerViewPetitionsDisplay.setAdapter(recyclerViewPetitions);
        recyclerViewPetitionsDisplay.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPetitions.notifyDataSetChanged();
        if(data.isEmpty()){
            textViewNumberOfPetitions.setText("No Petitions To Display");
        }
        else{
            textViewNumberOfPetitions.setText("Number of Petitions -: " + data.size());
        }

    }
}