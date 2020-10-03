package com.rwtcompany.onlinevegitableshopapp.ui.admin.editItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminItemEditBinding;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItemWithKey;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AdminItemEditActivity extends AppCompatActivity {
    private ActivityAdminItemEditBinding binding;
    private AdminItemEditViewModel viewModel;

    private ProgressDialog dialog;

    private final static int GALLERY_INTENT_CODE =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Make Changes");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(AdminItemEditViewModel.class);

        dialog = new ProgressDialog(this);
        dialog.setMessage("saving data...");
        dialog.setCancelable(false);

        String [] units = getResources().getStringArray(R.array.units);
        binding.spinnerEditUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units));

        Intent intent=getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("imageUrl");
        String unit=intent.getStringExtra("unit");
        String key = intent.getStringExtra("key");

        viewModel.setItem(new AdminItemWithKey(new AdminItem(imageUrl,name,price,unit),key));
        viewModel.setKey(name);

        Glide.with(AdminItemEditActivity.this).load(imageUrl).into(binding.ivEditImage);

        binding.etEditName.setText(name);
        binding.etEditPrice.setText(price);

        binding.ivEditImage.setOnClickListener(v -> {
            Intent galleryIntent=new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_INTENT_CODE);
        });

        setUpSpinnerPosition();
    }

    private void setUpSpinnerPosition(){
        switch (viewModel.getItem().getAdminItem().getUnit()) {
            case "out of stock":
                binding.spinnerEditUnit.setSelection(0);
                break;
            case "1 kg":
                binding.spinnerEditUnit.setSelection(1);
                break;
            case "50 gram":
                binding.spinnerEditUnit.setSelection(2);
                break;
            case "100 gram":
                binding.spinnerEditUnit.setSelection(3);
                break;
            case "250 gram":
                binding.spinnerEditUnit.setSelection(4);
                break;
            case "500 gram":
                binding.spinnerEditUnit.setSelection(5);
                break;
            case "12 piece":
                binding.spinnerEditUnit.setSelection(6);
                break;
            case "6 piece":
                binding.spinnerEditUnit.setSelection(7);
                break;
            case "1 piece":
                binding.spinnerEditUnit.setSelection(8);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_CODE && resultCode == RESULT_OK) {
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
                viewModel.setImageUri(result.getUri());
                binding.ivEditImage.setImageURI(viewModel.getImageUri());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void submitChange(View view) {
        String price=binding.etEditPrice.getText().toString();
        String unit=binding.spinnerEditUnit.getSelectedItem().toString().trim();
        String name=binding.etEditName.getText().toString().trim();
        if(name.isEmpty()||price.isEmpty())
            Toast.makeText(AdminItemEditActivity.this,"enter name and price of product please!!",Toast.LENGTH_LONG).show();
        else
        {
            dialog.show();
            viewModel.getItem().getAdminItem().setName(name);
            viewModel.getItem().getAdminItem().setPrice(price);
            viewModel.getItem().getAdminItem().setUnit(unit);

            viewModel.updateItem().observe(this,taskCompleted -> {
                dialog.dismiss();
                if(taskCompleted.isSuccessful())
                    finish();
                else {
                    Toast.makeText(this,taskCompleted.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
