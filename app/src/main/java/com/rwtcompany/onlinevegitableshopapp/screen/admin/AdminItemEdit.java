package com.rwtcompany.onlinevegitableshopapp.screen.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminItemEditBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AdminItemEdit extends AppCompatActivity {
    ActivityAdminItemEditBinding binding;

    ProgressDialog dialog;

    String name,price,unit,imageUrl;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    Uri imageUri;

    String [] units;

    private final static int galleryIntentCode=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Make Changes");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        dialog = new ProgressDialog(this);
        dialog.setMessage("saving data...");
        dialog.setCancelable(false);

        firebaseDatabase=FirebaseDatabase.getInstance();
        storageReference=FirebaseStorage.getInstance().getReference().child("images");

        units = getResources().getStringArray(R.array.units);
        binding.spinnerEditUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units));

        Intent intent=getIntent();
        name = intent.getStringExtra("name");
        price = intent.getStringExtra("price");
        imageUrl = intent.getStringExtra("imageUrl");

        Glide.with(AdminItemEdit.this).load(imageUrl).into(binding.ivEditImage);

        binding.etEditName.setText(name);
        binding.etEditPrice.setText(price);

        binding.ivEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryIntentCode);
            }
        });
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

    public void submitChange(View view) {

        price=binding.etEditPrice.getText().toString();
        unit=binding.spinnerEditUnit.getSelectedItem().toString().trim();
        if(name.isEmpty()||price.isEmpty())
            Toast.makeText(AdminItemEdit.this,"enter name and price of product please!!",Toast.LENGTH_LONG).show();
        else
        {
            dialog.show();
            if(imageUri==null)
            {
                //still need to find a way to delete image if name changed and then
                //upload again with new name

                DatabaseReference reference=firebaseDatabase.getReference().child("items").child(name);
                reference.removeValue();

                name = binding.etEditName.getText().toString().trim().toLowerCase();
                reference=firebaseDatabase.getReference().child("items").child(name);

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
                        Toast.makeText(AdminItemEdit.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
            else
            {
                storageReference.child(name).delete();

                final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(binding.etEditName.getText().toString().trim().toLowerCase());
                UploadTask uploadTask = ref.putFile(imageUri);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imageUrl=downloadUri.toString();
                            DatabaseReference reference=firebaseDatabase.getReference().child("items").child(name);
                            reference.removeValue();

                            name = binding.etEditName.getText().toString().trim().toLowerCase();
                            reference=firebaseDatabase.getReference().child("items").child(name);

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
                                    Toast.makeText(AdminItemEdit.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                            //Picasso.get().load(downloadUri).into(ivAddImage);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"failed:"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
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
