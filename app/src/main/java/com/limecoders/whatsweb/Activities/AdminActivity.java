package com.limecoders.whatsweb.Activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.limecoders.whatsweb.R;

import java.io.IOException;
import java.util.HashMap;

public class AdminActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1001;
    private ImageView bannerImage;
    private EditText title, url;
    private Button bannerBtn;
    private Button allBannerBtn;
    private Bitmap bannerBitmap;
    private Uri imageUri;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        bannerImage = findViewById(R.id.imageBanner);
        title = findViewById(R.id.title);
        url = findViewById(R.id.url);
        bannerBtn = findViewById(R.id.bannerBtn);
        allBannerBtn = findViewById(R.id.allbanners);

        initpDialog();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        allBannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this,SlidingBannerActivity.class));
            }
        });

        bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        bannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titlestr = title.getText().toString().trim();
                String urlstr = url.getText().toString().trim();
                if(TextUtils.isEmpty(titlestr) || TextUtils.isEmpty(urlstr) || bannerBitmap==null){
                    Toast.makeText(AdminActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    if (uploadTask != null && uploadTask.isInProgress()){
                        Toast.makeText(AdminActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        showpDialog();
                        uploadImage();
                    }
                }

            }
        });

    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        final String mUri = downloadUri.toString();

                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Banner");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("title",title.getText().toString());
                                map.put("url",url.getText().toString());
                                map.put("imageURL", mUri.toString());

                                reference.push().setValue(map);
                                hidepDialog();
                                Toast.makeText(AdminActivity.this,"Uploaded",Toast.LENGTH_LONG).show();
                                title.setText("");
                                url.setText("");
                                bannerImage.setImageBitmap(null);
                                bannerImage.setBackgroundResource(R.drawable.ic_action_camera);
                            }
                        },3000);

                    } else {
                        Toast.makeText(AdminActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AdminActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bannerImage.setImageBitmap(bitmap);
                bannerImage.setBackgroundResource(R.drawable.camera_iris);
                bannerBitmap = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected void initpDialog() {
        pDialog = new ProgressDialog(AdminActivity.this);
        pDialog.setMessage(getString(R.string.uploading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}