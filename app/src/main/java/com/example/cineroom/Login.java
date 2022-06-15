package com.example.cineroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    EditText email,password;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        data=new ArrayList<>();

        email=findViewById(R.id.textEmail);
        password=findViewById(R.id.textPassword);

        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            Intent intent=new Intent(Login.this,MainActivity.class);
            intent.putExtra("uid",firebaseUser.getUid());
            startActivity(intent);
            finish();
        }
    }

    public void getData(){

    }

    public void signInClicked(View view){
        String mail=email.getText().toString();
        String sifre=password.getText().toString();

        if(mail.matches("")||sifre.matches("")){
            AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
            builder.setTitle("Dikkat");
            builder.setMessage("E-mail veya Şifre Boş Bırakılamaz!");
            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    email.setText("");
                    password.setText("");
                }
            });
            builder.show();
        }else {
            firebaseAuth.signInWithEmailAndPassword(mail, sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setTitle("Dikkat");
                    builder.setMessage("Kullanıcı Bulunamadı, Bilgilerinizi Kontrol Ediniz!");
                    builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            email.setText("");
                            password.setText("");
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    public void forgotPass(View view){
        Intent intent=new Intent (Login.this,ForgotPassword.class);
        startActivity(intent);
    }

    public void signUpClicked(View view){
        Intent intent =new Intent(Login.this,SignUp.class);
        startActivity(intent);

    }

    public void twit(View view){
        Uri link=Uri.parse("https://twitter.com/cineroom24");
        Intent intent =new Intent(Intent.ACTION_VIEW,link);
        startActivity(intent);
    }

    public void insta(View view){
        Uri link=Uri.parse("https://www.instagram.com/cineroom24/");
        Intent intent =new Intent(Intent.ACTION_VIEW,link);
        startActivity(intent);
    }
    public void face(View view){
        Uri link=Uri.parse("https://www.facebook.com/CineRomm");
        Intent intent =new Intent(Intent.ACTION_VIEW,link);
        startActivity(intent);
    }
    public void linkedin(View view){
        Uri link=Uri.parse("https://www.linkedin.com/in/cine-room-a0b2361bb/");
        Intent intent=new Intent(Intent.ACTION_VIEW,link);
        startActivity(intent);
    }
}