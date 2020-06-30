package com.example.myapp.userEntery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.R;
import com.example.myapp.security.EncDec;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private String securityKey = "MySecurityKeyEnc";

    private ImageView imageView;
    private Button saveIntoDbBtn;
    private int GalleryPick = 1;
    private Uri imageUrl;
    private StorageReference userProfileImgRef;
    private String downloadImgUrl;
    private DatabaseReference userRef;
    private String currentUserID;
    private Bitmap bitmap;
    private File imgFile = null;
    private  String name, path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("Users Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("User Details");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        imageView = (ImageView) findViewById(R.id.imageViewEnc);
        saveIntoDbBtn = (Button) findViewById(R.id.buttonEnc);



//        retriveImg();
        retriveImg();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);


            }
        });


        saveIntoDbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImgIntoDB();

//                userRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        HashMap<String, Object> setImgMap = new HashMap<>();
//                        setImgMap.put("image",encImgPath);
//                        userRef.child(currentUserID)
//                                .updateChildren(setImgMap)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    Toast.makeText(ProfileActivity.this, "Updated Success!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        });
    }




    private void retriveImg(){
            StorageReference imgRef = userProfileImgRef.child(currentUserID);
            imgRef.getBytes(1024*1024)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            imageView.setImageBitmap(bitmap);

                        }
                    });
    }


//    private void retriveImg() {
//            userRef.child(currentUserID)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()){
//                                    String ImageFromDbUrl = snapshot.child("image").getValue().toString();
//                                     Picasso.get().load(ImageFromDbUrl).into(imageView);
//
//                            }
//                            else {
//                                Toast.makeText(ProfileActivity.this, "Please Upload Image", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//    }

    private void saveImgIntoDB(){
        final StorageReference filePath =userProfileImgRef.child(currentUserID);

        UploadTask uploadTask =filePath.putFile(imageUrl);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    String errorMsg = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();

//                    saveInternal();
                }
                downloadImgUrl =filePath.getDownloadUrl().toString();

                return filePath.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        downloadImgUrl =task.getResult().toString();

                        HashMap<String ,Object> saveProIntoDbMap = new HashMap<>();
                        saveProIntoDbMap.put("image",downloadImgUrl);
                        userRef.child(currentUserID)
                                .updateChildren(saveProIntoDbMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(ProfileActivity.this, "Image Upload Success", Toast.LENGTH_SHORT).show();

                                            }
                                    }
                                });

                    }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
                imageUrl = data.getData();
                imageView.setImageURI(imageUrl);

        }
    }

//    private void saveInternal() throws IOException {
//        path = getRealPathFromUri(this,imageUrl);
//        name = getFileName(imageUrl);
//        insertIntoPrivateStorage(path,name);
//    }
//
//    private String getFileName(Uri uri) {
//            String result = null;
//            if (uri.getScheme().equals("content")){
//                Cursor cursor = getContentResolver().query(uri,null,null,null,null);
//                try {
//                 if (cursor != null && cursor.moveToFirst()){
//                     result  = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                 }
//                }finally {
//                    cursor.close();
//                }
//            }
//            if (result == null){
//                result = uri.getPath();
//                int cut = result.lastIndexOf("/");
//                if (cut != -1){
//                    result  = result.substring(cut +1);
//                }
//            }
//            return result;
//    }
//
//    private String getRealPathFromUri(ProfileActivity profileActivity, Uri imageUrl) {
//
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = profileActivity.getContentResolver().query(imageUrl,proj,null,null,null);
//        if (cursor != null){
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//        return null;
//    }
//
//    private void insertIntoPrivateStorage(String nameA, String pathA) throws IOException {
//        FileOutputStream fos = openFileOutput(nameA,MODE_PRIVATE);
//
//        imgFile = new File(pathA);
//        byte[] bytes = getBytesFromFile(imgFile);
//        fos.write(bytes);
//        fos.close();
//        Toast.makeText(this, "File Saved In Storage!"+getFilesDir(), Toast.LENGTH_SHORT).show();
//    }
//
//    private byte[] getBytesFromFile(File imgFileA) throws IOException {
//            byte[] data = FileUtils.readFileToByteArray(imgFileA);
//            return data;
//    }

}