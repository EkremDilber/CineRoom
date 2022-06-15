package com.example.cineroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.security.Permission;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    DatabaseReference databaseReference;
    EditText emailText,passwordText,passwordText2,personText;
    Button button;
    Bitmap selectedImage;
    ImageView imageView;
    Uri imageData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference();

        emailText=findViewById(R.id.textPersonEmailAddress);
        passwordText=findViewById(R.id.textPersonPassword);
        passwordText2=findViewById(R.id.textPersonPassword2);
        personText=findViewById(R.id.textPersonName);
        button=findViewById(R.id.button);
        imageView=findViewById(R.id.imageView2);

    }
    public void signUpClicked(View view){

        String email=emailText.getText().toString();
        String password=passwordText.getText().toString();
        String passwordTekrar=passwordText2.getText().toString();
        String adi=personText.getText().toString();


        if(email.matches("")||password.matches("")){
            Toast.makeText(SignUp.this,"Boş Bırakılamaz",Toast.LENGTH_LONG).show();
        }else if(!password.matches(passwordTekrar)){
            Toast.makeText(SignUp.this,"Şifreler Farklı",Toast.LENGTH_LONG).show();
        }else if(password.length()<6){
            Toast.makeText(SignUp.this,"Şifre 6 Karakterden Küçük Olamaz",Toast.LENGTH_LONG).show();
        }else {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    user=firebaseAuth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
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
                                        String email=firebaseUser.getEmail();

                                        Date date = new Date();
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                        String strDate= formatter.format(date);


                                        HashMap<String,Object> userData=new HashMap<>();
                                        userData.put("userEmail",email);
                                        userData.put("profilePhoto",downloadUri);
                                        userData.put("userName",userName);
                                        userData.put("date", strDate);

                                        databaseReference.child("users").child(user.getUid()).setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(SignUp.this);
                                                    builder.setTitle("Aramıza Hoşgeldin");
                                                    builder.setMessage(adi+" CineRoom'da Olmanın Tadını Çıkar!\nDoğrulama Mesajınız Yollandı.");
                                                    builder.setPositiveButton("Devam Et", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent =new Intent(SignUp.this,MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();
                                                }else{
                                                    Toast.makeText(SignUp.this,"Kayıt Başarısız!",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUp.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1 && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED ){
            Intent intentToGallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                    imageView.setImageBitmap(selectedImage);

                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}