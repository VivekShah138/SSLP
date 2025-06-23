package com.example.sslp;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAdminDetails {
    public static void addAdmin(Context context){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        User adminUser = new User("admin@petition.parliament.sr","Admin","13-08-1950","2025%shangrila","30MY51J1CJ");
        adminUser.setUserRole("Admin");

        String adminUserID = firebaseAuth.getCurrentUser().getUid();
        adminUser.setUserID(adminUserID);

        databaseReference.child(adminUserID).setValue(adminUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"Admin Added Successfully",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"Failed Adding Admin",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
