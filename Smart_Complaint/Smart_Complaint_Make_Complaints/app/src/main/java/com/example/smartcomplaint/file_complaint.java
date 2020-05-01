package com.example.smartcomplaint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class file_complaint extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    ImageView iv;
    ImageView a;
    Uri downloadUri;
    SmartComplaint smartComplaint;
    private ListView mMessageListView;
    private ComplaintAdapter mMessageAdapter;
    private LinearLayout mPhotoPickerButton;
    private EditText mMessageEditText;
    private EditText mMessageTitle;
    private LinearLayout mSendButton;
    private String mUsername;
    private String mUserId;
    private String receiver;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUsername = user.getDisplayName();
            mUserId = user.getUid();
        } else {
            mUsername = ANONYMOUS;
            mUserId = ANONYMOUS;
        }
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("complaint_photos");

        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        mSendButton = findViewById(R.id.postComplainButton);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.enter_complaint);
        mMessageTitle = findViewById(R.id.enter_issue_topic);
        iv = findViewById(R.id.nakli);
        a = findViewById(R.id.asli);

        //Spinnner (Select Receiver of the Complaint)
        Spinner spinner = findViewById(R.id.select_receiver);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.receivers_of_complaint, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                if (downloadUri != null) {
                    smartComplaint = new SmartComplaint(mMessageTitle.getText().toString(), receiver, mMessageEditText.getText().toString(), mUsername, mUserId, downloadUri.toString());
                    mMessagesDatabaseReference.push().setValue(smartComplaint);
                    // Clear input box
                    mMessageTitle.setText("");
                    mMessageEditText.setText("");
                    Toast.makeText(file_complaint.this, "Complaint Sent Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(file_complaint.this, "Please Upload an Image as Proof", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);

        if (requestcode == RC_PHOTO_PICKER && resultcode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            try {
                assert selectedImageUri != null;
                iv.setImageURI(null);
                iv.setImageURI(selectedImageUri);
                a.setImageURI(selectedImageUri);
                iv.setDrawingCacheEnabled(true);
                iv.buildDrawingCache();
                Bitmap b = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] final_image = baos.toByteArray();
                final StorageReference photoRef = mChatPhotosStorageReference.child(Objects.requireNonNull(selectedImageUri.getLastPathSegment()));

                // Upload file to Firebase Storage
                UploadTask uploadTask = photoRef.putBytes(final_image);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return photoRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();
                        } else {
                            Toast.makeText(file_complaint.this, "upload failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //On Selecting Spinner Item
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        receiver = adapterView.getItemAtPosition(i).toString();
        //receiver stores the receiver of the complaint
        Toast.makeText(adapterView.getContext(), receiver, Toast.LENGTH_SHORT).show();
    }

    //On Not Selecting Any Item from Spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
