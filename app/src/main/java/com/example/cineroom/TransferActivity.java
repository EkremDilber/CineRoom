package com.example.cineroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TransferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
    }

    //profileEdit artık transfer sayfasında kullanılmıyor.
    public void profileEdit(View view){
        Intent intent=new Intent(TransferActivity.this,ProfileEdit.class);
        startActivity(intent);
    }

    public void passwordChange(View view){
        Intent intent=new Intent(TransferActivity.this, PasswordChange.class);
        startActivity(intent);
    }
    public void appInformation(View view){
        Intent intent=new Intent(TransferActivity.this,AppInformationActivity.class);
        startActivity(intent);
    }
}