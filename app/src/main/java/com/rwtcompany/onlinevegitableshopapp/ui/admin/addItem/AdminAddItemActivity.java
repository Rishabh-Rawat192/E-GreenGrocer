package com.rwtcompany.onlinevegitableshopapp.ui.admin.addItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rwtcompany.onlinevegitableshopapp.R;
import com.rwtcompany.onlinevegitableshopapp.databinding.ActivityAdminAddItemBinding;
import com.rwtcompany.onlinevegitableshopapp.model.AdminItem;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AdminAddItemActivity extends AppCompatActivity {
    private ActivityAdminAddItemBinding binding;
    private AdminAddItemViewModel viewModel;

    private ProgressDialog dialog;

    private final static int GALLERY_INTENT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Add Item");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setMessage("adding item...");
        dialog.setCancelable(false);

        viewModel = new ViewModelProvider(this).get(AdminAddItemViewModel.class);

        String[] units = getResources().getStringArray(R.array.units);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units);

        binding.spinnerEditUnit.setAdapter(adapter);

        binding.ivEditImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_INTENT_CODE);
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAllowFlipping(false)
                    .setAllowRotation(false)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                viewModel.setImageUri(result.getUri());
                binding.ivEditImage.setImageURI(viewModel.getImageUri());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void submitDetail(View view) {
        String name = binding.etEditName.getText().toString().trim().toLowerCase();
        String price = binding.etEditPrice.getText().toString();
        String unit = binding.spinnerEditUnit.getSelectedItem().toString().trim();
        if (name.isEmpty() || price.isEmpty())
            Toast.makeText(AdminAddItemActivity.this, "enter name and price of product please!!", Toast.LENGTH_LONG).show();
        else {
            if (viewModel.getImageUri() == null) {
                Toast.makeText(AdminAddItemActivity.this, "Please select a image", Toast.LENGTH_LONG).show();
            } else {
                dialog.show();
                viewModel.addNewProduct(new AdminItem(null, name, price, unit)).observe(this, taskCompleted -> {
                    dialog.dismiss();
                    if(taskCompleted.isSuccessful()){
                        viewModel.productAdded();
                        finish();
                    }else {
                        Toast.makeText(this,taskCompleted.getMessage(),Toast.LENGTH_LONG).show();
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
