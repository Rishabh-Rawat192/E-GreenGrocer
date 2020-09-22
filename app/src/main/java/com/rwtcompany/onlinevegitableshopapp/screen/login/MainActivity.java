package com.rwtcompany.onlinevegitableshopapp.screen.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityMainBinding;
import com.rwtcompany.onlinevegitableshopapp.screen.admin.AdminHomePage;
import com.rwtcompany.onlinevegitableshopapp.screen.user.home.UserHomePageActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private String adminPin,email,password;

    private String getAdminPin;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("getting you in...");
        dialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        currentUser=mAuth.getCurrentUser();


        if(currentUser!=null)
        {   dialog.show();
            database.getReference("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (currentUser.getEmail().equals(dataSnapshot.child("user").getValue())) {
                        startActivity(new Intent(MainActivity.this, AdminHomePage.class));
                    }
                    else
                    {
                        startActivity(new Intent(MainActivity.this, UserHomePageActivity.class));
                    }
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    try{dialog.dismiss();}
                    catch (Exception e) {}
                    Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.admin)
        {
            binding.etAdminPin.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        email=binding.etEmail.getText().toString().trim();
        password=binding.etPassword.getText().toString().trim();
        adminPin=binding.etAdminPin.getText().toString().trim();
        if(email.isEmpty()||password.isEmpty())
        {
            Toast.makeText(this,"Enter email and password please!!",Toast.LENGTH_LONG).show();
        }
        else
        {
            signIn();
        }
    }

    private void signIn()
    {   dialog.show();
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    currentUser=mAuth.getCurrentUser();
                    if (!adminPin.isEmpty()) {
                        adminTest();
                    }
                    else {
                        startActivity(new Intent(MainActivity.this, UserHomePageActivity.class));
                        finish();
                    }
                }
                else if(task.getException()!=null){
                    Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void adminTest()
    {
        dialog.show();
        database.getReference("admin/pin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getAdminPin =dataSnapshot.getValue().toString();
                if (getAdminPin.equals(adminPin)) {
                    database.getReference("admin").child("user").setValue(currentUser.getEmail());
                    saveTokenForFCM();
                    startActivity(new Intent(MainActivity.this,AdminHomePage.class));
                    finish();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"incorrect admin pin",Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                try {
                    dialog.dismiss();
                }catch (Exception e){}
                Toast.makeText(MainActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }});
    }

    public void signUp(View view)
    {
        email=binding.etEmail.getText().toString().trim();
        password=binding.etPassword.getText().toString().trim();
        adminPin=binding.etAdminPin.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this,"please enter email and password...",Toast.LENGTH_LONG).show();
        }
        else
        {   dialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        currentUser=mAuth.getCurrentUser();
                        addToUsers();
                        if (!adminPin.isEmpty()) {

                            adminTest();
                        }
                        else {
                            startActivity(new Intent(MainActivity.this, UserHomePageActivity.class));
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }


    public void forgetPassword(View view) {
        String email=binding.etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(MainActivity.this,"Enter email for recovery",Toast.LENGTH_LONG).show();
        }
        else
        {
            dialog.show();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    if(task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this,"Password recovery link send to your email!!",Toast.LENGTH_LONG).show();
                    }
                    else if(task.getException()!=null) {
                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void addToUsers()
    {
        database.getReference().child("users").child(currentUser.getUid()).child("email").setValue(currentUser.getEmail());
    }

    private void saveTokenForFCM(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("Main", task.getException().toString());
                            Toast.makeText(MainActivity.this, "Notification service is not activated. Please logout and then login again later.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        String token = task.getResult().getToken();
                        database.getReference().child("admin").child("token").setValue(token);
                    }
                });
    }
}
