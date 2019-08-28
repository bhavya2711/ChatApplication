package com.example.inclass01;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import androidx.annotation.NonNull;

public  class ImageUpload extends AsyncTask<Bitmap, Integer, String> {

    String getUrl;
    String UUID;

    Bitmap bitmap;
    public ImageUpload(Bitmap bitmap, String UUID)
    {
        this.bitmap=bitmap;
        this.UUID = UUID;
    }

    final StorageReference ref = FirebaseStorage.getInstance().getReference().child(UUID + ".jpg");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
    byte[] data = baos.toByteArray();

    final UploadTask uploadTask = ref.putBytes(data);


    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                    getUrl = downloadUri.toString();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

        return getUrl;
    }

    @Override
    protected void onPostExecute(String s) {
        this.getUrl=s;
    }
}
