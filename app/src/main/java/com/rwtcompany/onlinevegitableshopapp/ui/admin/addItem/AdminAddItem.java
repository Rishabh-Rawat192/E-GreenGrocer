package com.rwtcompany.onlinevegitableshopapp.ui.admin.addItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminAddItemBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AdminAddItem extends AppCompatActivity {
    ActivityAdminAddItemBinding binding;

    String name,price,unit,imageUrl;

    Uri imageUri;

    ProgressDialog dialog;

    private FirebaseDatabase firebaseDatabase;

    String []units;
    ArrayAdapter<String> adapter;

    private final static int galleryIntentCode=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Add Item");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        units = getResources().getStringArray(R.array.units);

        dialog = new ProgressDialog(this);
        dialog.setMessage("adding item...");
        dialog.setCancelable(false);

        firebaseDatabase=FirebaseDatabase.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units);

        binding.ivEditImage.setOnClickListener(v -> {
            Intent galleryIntent=new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,galleryIntentCode);
        });

        binding.spinnerEditUnit.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryIntentCode && resultCode == RESULT_OK) {
            Uri imageUri=data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowFlipping(false)
                    .setAllowRotation(false)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                binding.ivEditImage.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    public void submitDetail(View view) {
        name = binding.etEditName.getText().toString().trim().toLowerCase();
        price=binding.etEditPrice.getText().toString();
        unit=binding.spinnerEditUnit.getSelectedItem().toString().trim();
        if(name.isEmpty()||price.isEmpty())
            Toast.makeText(AdminAddItem.this,"enter name and price of product please!!",Toast.LENGTH_LONG).show();
        else
        {
            if(imageUri==null)
            {
                Toast.makeText(AdminAddItem.this,"Please select a image",Toast.LENGTH_LONG).show();
            }
            else
            {
                dialog.show();
                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(name);
                UploadTask uploadTask = ref.putFile(imageUri);

                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageUrl=downloadUri.toString();
                        DatabaseReference reference=firebaseDatabase.getReference().child("items").child(name);
                        reference.child("name").setValue(name);
                        reference.child("price").setValue(price);
                        reference.child("unit").setValue(unit);
                        reference.child("imageUrl").setValue(imageUrl);
                        if(unit.equals("out of stock"))
                            reference.child("available").setValue(false);
                        else
                            reference.child("available").setValue(true);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                dialog.dismiss();
                                Toast.makeText(AdminAddItem.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"failed:"+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
