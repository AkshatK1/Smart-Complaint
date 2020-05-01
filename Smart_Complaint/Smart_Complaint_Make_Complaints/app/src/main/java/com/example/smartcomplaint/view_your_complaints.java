package com.example.smartcomplaint;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class view_your_complaints extends AppCompatActivity {
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
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
        setContentView(R.layout.activity_view_your_complaints);

        mMessageListView = findViewById(R.id.messageListView);
        mFirebaseDatabase = FirebaseDatabase.getInstance();//for messages, i think
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();//for image, i think

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUsername = user.getDisplayName();
            mUserId = user.getUid();
        } else {
            mUsername = ANONYMOUS;
            mUserId = ANONYMOUS;
        }
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("complaint_photos");

        // Initialize message ListView and its adapter
        List<SmartComplaint> smartComplaints = new ArrayList<>();
        mMessageAdapter = new ComplaintAdapter(this, R.layout.item_complaint, smartComplaints);
        mMessageListView.setAdapter(mMessageAdapter);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SmartComplaint smartComplaint = dataSnapshot.getValue(SmartComplaint.class);
                assert smartComplaint != null;
                if (user != null && mUserId.equals(smartComplaint.getId()))
                    mMessageAdapter.add(smartComplaint);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
    }
}
