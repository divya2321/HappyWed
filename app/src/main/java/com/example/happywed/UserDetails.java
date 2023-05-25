package com.example.happywed;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.happywed.DBCon.HappyWed;
import com.example.happywed.DBCon.HappyWedDB;
import com.example.happywed.DBModel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

import static com.example.happywed.R.id.noDateChecked;
import static com.example.happywed.R.id.profilePic;

public class UserDetails extends AppCompatActivity {

    private EditText userNameEditText, partnerNameEditText, estimatedBudgetText;
    private CircleImageView profilePicImageView;
    private DatePicker weddingDatePicker;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private Timestamp timestamp;
    private Uri selectImgUri;
    private CheckedTextView checkedTextView;
    private View userProgressBar;

    private static final String USERDETAILSADD = "UserDetailsAdd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        userNameEditText = findViewById(R.id.userName);
        partnerNameEditText = findViewById(R.id.partnerName);
        estimatedBudgetText = findViewById(R.id.estimatedBudget);
        weddingDatePicker = findViewById(R.id.weddingDate);
        profilePicImageView = findViewById(R.id.profilePic);
        checkedTextView = findViewById(R.id.noDateChecked);
        userProgressBar = findViewById(R.id.userProgressBar);

        userProgressBar.setVisibility(View.GONE);

        weddingDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        databaseReference = HappyWedDB.getDBConnection().child("users");
        storageReference = HappyWedDB.getStorageReference().child("users").child("profile pic");

        currentUser = HappyWedDB.getFirebaseAuth().getCurrentUser();
        String userName = currentUser.getDisplayName();
        userNameEditText.setText(userName);

        selectImgUri =  Uri.parse("android.resource://"+getPackageName()+"/drawable/default_user_profile_picture");
    }


    private String yourName, partnerName, estBudget;
    private int day, month, year;

    public void saveUserDetails(View view){
        yourName = userNameEditText.getText().toString().trim();
        partnerName = partnerNameEditText.getText().toString().trim();
        estBudget = estimatedBudgetText.getText().toString().trim();
        day = weddingDatePicker.getDayOfMonth();
        month = weddingDatePicker.getMonth()+1;
        year = weddingDatePicker.getYear();


        if (!yourName.isEmpty()){
            userProgressBar.setVisibility(View.VISIBLE);

             final UserInfo profile = currentUser.getProviderData().get(0);

            String weddingDate =year+"-"+month+"-"+day;

             if(checkedTextView.isChecked()){
               weddingDate = "noDate";
             }
             if(estBudget.isEmpty()){
                 estBudget ="0.0";
             }
            HappyWed.resetUserDatabase(UserDetails.this);
            HappyWed.iud(UserDetails.this, "INSERT INTO user ( userName, partnerName, weddingDate, estBudget)" +
                    "VALUES ('"+yourName+"', '"+partnerName+"', '"+weddingDate+"', '"+estBudget+"')");


            timestamp = new Timestamp(System.currentTimeMillis());
            storageReference.child("img_"+timestamp).putFile(selectImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                            Log.d(USERDETAILSADD, "Add img to storage: "+uri);

                            UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(selectImgUri)
                                    .setDisplayName(yourName)
                                    .build();
                            currentUser.updateProfile(changeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Log.d(USERDETAILSADD, "Add details to auth");


                                    User user= new User()
                                            .setUserName(yourName)
                                            .setUid(profile.getUid())
                                            .setProfilePicUrl(uri.toString())
                                            .setEmail(profile.getEmail())
                                            .setMobileNo(profile.getPhoneNumber())
                                            .setProvider(currentUser.getIdToken(false).getResult().getSignInProvider())
                                            .setStatus("offline");

                                    databaseReference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d(USERDETAILSADD, "Add user details to db: "+currentUser);
                                                startActivity(new Intent(getApplicationContext(),Home.class));
                                                Toast.makeText(UserDetails.this, "Successfully saved!", Toast.LENGTH_LONG).show();
                                                userProgressBar.setVisibility(View.GONE);
                                                UserDetails.this.finish();
                                            }else{
                                                Log.w(USERDETAILSADD, "Cannot add user details to db: "+task.getException());
                                                Toast.makeText(UserDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                                userProgressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });




                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(USERDETAILSADD, "Cannot add details to auth: "+e);
                                    Toast.makeText(UserDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                    userProgressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(USERDETAILSADD, "Cannot add img to storage: "+e);
                    Toast.makeText(UserDetails.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    userProgressBar.setVisibility(View.GONE);
                }
            });




        }else {
            userNameEditText.setError("Your name is required!");
            userNameEditText.setFocusable(true);
        }



    }

    private final static int REQUEST_GALLERY_PERMISSION_CODE = 1;
    private final static int REQUEST_CAMERA_PERMISSION_CODE = 2;
    private final static int GALLERY_ACCESS_CODE = 3;
    private final static int CAMERA_ACCESS_CODE = 4;

    public void enterToUpload(View view){
        chooseOptions();
    }

    public void accessGallery(){
        String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED){

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_ACCESS_CODE);

        }else {

            ActivityCompat.requestPermissions(this,permissions, REQUEST_GALLERY_PERMISSION_CODE);

        }

    }

   private Uri captureImageUri=null;

    public void accessCamera(){

        String permissions[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),permissions[1])== PackageManager.PERMISSION_GRANTED){


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                captureImageUri = Uri.fromFile(createImageFile());

            } catch (IOException e) {
                e.printStackTrace();
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri);
            startActivityForResult(intent, CAMERA_ACCESS_CODE);

        }else {
            ActivityCompat.requestPermissions(this,permissions, REQUEST_CAMERA_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
             switch (requestCode){
                    case REQUEST_CAMERA_PERMISSION_CODE:
                    if ((grantResults.length==2) && (grantResults[0]==PackageManager.PERMISSION_GRANTED) && (grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                       accessCamera();
                    }
                    break;

                    case REQUEST_GALLERY_PERMISSION_CODE:
                    if ((grantResults.length==1) && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                       accessGallery();
                    }
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==GALLERY_ACCESS_CODE || requestCode==CAMERA_ACCESS_CODE) && (resultCode==RESULT_OK)){
            Uri imageUri;
            if(requestCode==GALLERY_ACCESS_CODE){
                imageUri= data.getData();
            }else{
                imageUri = captureImageUri;
            }
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                selectImgUri = result.getUri();
                profilePicImageView.setImageURI(selectImgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void chooseOptions(){
        final CharSequence options[] = {"Take Photo","Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Your Option");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Take Photo")){
                    accessCamera();
                } else if (options[i].equals("Choose from Gallery")){
                    accessGallery();
                }
            }
        });

        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public void checkNoDate(View view) {

        if(checkedTextView.isChecked()){
            checkedTextView.setChecked(false);
            weddingDatePicker.setEnabled(true);
            weddingDatePicker.setFocusable(true);
        }else{
            checkedTextView.setChecked(true);
            weddingDatePicker.setEnabled(false);
            weddingDatePicker.setFocusable(false);
        }
    }
}
