package com.example.sslp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public interface UserRole {

    public interface UserRoleCallback {
        void onUserRoleRetrieved(String userRole);
        void onError(Exception e);
    }

    public static void getUserRole(FirebaseAuth firebaseAuth, DatabaseReference databaseReference, UserRoleCallback callback) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    String userRole = dataSnapshot.child("userRole").getValue(String.class);
                    callback.onUserRoleRetrieved(userRole);
                } else {
                    callback.onError(task.getException());
                }

            }
        });
    }

}
