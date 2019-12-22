package com.rohindh.firechat.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.print.PrinterId;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rohindh.firechat.R;
import com.rohindh.firechat.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private CircleImageView profileimage;
    private TextView username;

    private FirebaseUser fuser;
    private DatabaseReference reference;

    private StorageReference storageReference;
    private StorageTask uploadtask;
    private static final int IMAGE_REQUEST =1;
    private Uri imageuri;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        profileimage = view.findViewById(R.id.profile_profileimage);
        username = view.findViewById(R.id.profile_username);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(profileimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openimage();

            }
        });





        // Inflate the layout for this fragment
        return view;
    }

    private void openimage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading");
        pd.show();

        if(imageuri!=null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageuri));
            uploadtask = fileReference.putFile(imageuri);

            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        Map<String,Object> map = new HashMap<>();
                        map.put("imageurl",mUri);
                        reference.updateChildren(map);

                        pd.dismiss();

                    }else {
                        Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            }) ;
        }else {
            pd.dismiss();
            Toast.makeText(getContext(),"No image selected",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null&data.getData()!=null){
            imageuri = data.getData();
            if(uploadtask != null && uploadtask.isInProgress()){
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }
        }
    }
}
