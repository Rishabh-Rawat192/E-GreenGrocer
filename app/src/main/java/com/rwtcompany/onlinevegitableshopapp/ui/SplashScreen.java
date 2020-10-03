package com.rwtcompany.onlinevegitableshopapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwtcompany.onlinevegitableshopapp.ui.admin.home.AdminHomeActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.login.MainActivity;
import com.rwtcompany.onlinevegitableshopapp.ui.user.home.UserHomePageActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("admin");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        if(currentUser!=null){
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (currentUser.getEmail().equals(dataSnapshot.child("user").getValue())) {
                        startActivity(new Intent(SplashScreen.this, AdminHomeActivity.class));
                    }
                    else
                    {
                        startActivity(new Intent(SplashScreen.this, UserHomePageActivity.class));
                    }
                    overridePendingTransition(0, 0);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
            });
        }else {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }
    }
}
