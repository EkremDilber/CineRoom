package com.example.cineroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileEdit extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    EditText userName,userEmail;
    ImageView profilePhoto;
    Bitmap selectedImage;
    Uri imageData;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");

        userName=findViewById(R.id.editTextTextPersonName);
        userEmail=findViewById(R.id.databaseEmailAddress);
        profilePhoto=findViewById(R.id.imageView4);


        getDataFromFirebase();

    }
    public void updateProfile(View view){
        String email=userEmail.getText().toString();
        String adi=userName.getText().toString();


        if(email.matches("")||adi.matches("")){
            Toast.makeText(getApplicationContext(),"Boş bırakılamaz",Toast.LENGTH_LONG).show();
        }else {


            if(imageData!=null){
                UUID uuid=UUID.randomUUID();
                final String photoName="profilePhoto/"+uuid+".jpg";
                storageReference.child(photoName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        StorageReference newReference=FirebaseStorage.getInstance().getReference(photoName);
                        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUri=uri.toString();
                                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                                String userName=adi;
                                String userEmail=email;

                                HashMap<String,Object> userData=new HashMap<>();
                                userData.put("profilePhoto",downloadUri);
                                userData.put("userName",adi);
                                userData.put("userEmail",userEmail);

                               databaseReference.child(firebaseUser.getUid()).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           Toast.makeText(ProfileEdit.this,"Başarılı",Toast.LENGTH_SHORT).show();
                                       }else {
                                           Toast.makeText(ProfileEdit.this,"Tekrar Deneyiniz",Toast.LENGTH_SHORT).show();
                                       }

                                   }
                               });
                            }
                        });

                        firebaseUser.updateEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    AlertDialog.Builder dialog=new AlertDialog.Builder(ProfileEdit.this);
                                    dialog.setTitle("Üyelik Bilgilerim");
                                    dialog.setMessage("Bilgiler Güncellendi");
                                    dialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            firebaseAuth.signOut();
                                            Intent intent =new Intent(ProfileEdit.this,Login.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                dialog.show();
                                }

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileEdit.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                Toast.makeText(ProfileEdit.this,"Fotoğraf Seçiniz",Toast.LENGTH_LONG).show();
            }

        }
    }
    public void imageSelected(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }
    public void getDataFromFirebase(){
        if (firebaseUser==null){
            System.out.println("Kullanıcı yok");
        }
        else{
            System.out.println(firebaseUser.getUid());
            databaseReference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println(snapshot.getValue());
                    ArrayList<String> data = new ArrayList<>();
                    for (DataSnapshot snp : snapshot.getChildren()) {
                        data.add(snp.getValue().toString());
                    }
                    System.out.println(data);
                    Picasso.get().load(data.get(1)).into(profilePhoto);
                    userEmail.setText(data.get(2));
                    userName.setText(data.get(3));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println(error.getMessage());
                }
            });

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1 && grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED ){
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2&&resultCode==RESULT_OK&&data!=null){
            imageData=data.getData();
            try {
                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source= ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage=ImageDecoder.decodeBitmap(source);
                    profilePhoto.setImageBitmap(selectedImage);

                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    profilePhoto.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}