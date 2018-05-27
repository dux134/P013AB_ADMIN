package in.dux.p013ab_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URISyntaxException;

import in.dux.p013ab_admin.Database.FireStore;
import in.dux.p013ab_admin.utils.FileUtils;

public class Websites extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    private static final String STORAGE_PATH_UPLOADS = "Websites";
    private static final String DATABASE_PATH_UPLOADS = "uploads";
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private TextView name,locatonNo;
    private Button submit,select;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websites);

        storageReference = FirebaseStorage.getInstance().getReference();

        name = findViewById(R.id.websiteName);
        locatonNo = findViewById(R.id.websiteNo);

        submit = findViewById(R.id.websiteSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAction();
            }
        });

        select = findViewById(R.id.websiteSelect);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showFileChooser();
            }
        });

    }

    private void submitAction() {

        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + "/" + name.getText().toString());

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //adding an upload to firebase database
//                            String uploadId = mDatabase.push().getKey();
                            //mDatabase.child(uploadId).setValue(upload);
                            FireStore fireStore = new FireStore();
                            fireStore.postWebsite( name.getText().toString(), taskSnapshot.getDownloadUrl().toString(),locatonNo.getText().toString().trim());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    filePath = data.getData();
                    Log.d("m", "File Uri: " + filePath.toString());

                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, filePath);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d("k", "File Path: " + path);
                    name.setText(filePath.getLastPathSegment().toString());
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


//    public class UploadToFirebase {
//
//        public String name;
//        public String url;
//        public String location;
//
//        // Default constructor required for calls to
//        // DataSnapshot.getValue(User.class)
//        public UploadToFirebase() {
//        }
//
//        public UploadToFirebase(String name, String url,String loc) {
//            this.name = name;
//            this.url = url;
//            this.location = loc;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public String getUrl() {
//            return url;
//        }
//
//        public String getLocation() {
//            return location;
//        }
//    }
}
