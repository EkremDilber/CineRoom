package com.example.cineroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView userName, userEmail,tarih;
    ImageView profilePhoto;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        userName=findViewById(R.id.databasePersonName);
        userEmail=findViewById(R.id.databasePersonEmail);
        profilePhoto=findViewById(R.id.imageView3);
        tarih=findViewById(R.id.editTextDate);

        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.appointment:
                    startActivity(new Intent(getApplicationContext(),AddActivity.class));
                    finish();
                    overridePendingTransition(0,0);
                    return true;
                case R.id.profile:
                    return true;
            }
            return false;
        });

        getDataFromFirebase();
    }
    public void profileEdit(View view){
        Intent intent =new Intent(ProfileActivity.this,TransferActivity.class);
        startActivity(intent);
    }
    public void signOut(View view) {
        Toast.makeText(ProfileActivity.this,"Çıkış yapılıyor",Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        Intent intent=new Intent(ProfileActivity.this,Login.class);
        startActivity(intent);
        finish();
    }

    public void toSettings(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProfileEdit.class);
        startActivity(intent);
    }

    public void getDataFromFirebase(){
        if (user==null){
            System.out.println("Kullanıcı yok");
        }
        else{
            System.out.println(user.getUid());
            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //System.out.println(snapshot.getValue());
                    ArrayList<String> data = new ArrayList<>();
                    for (DataSnapshot snp : snapshot.getChildren()) {
                        data.add(snp.getValue().toString());
                    }
                    System.out.println(data);
                    tarih.setText(data.get(0));
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
}