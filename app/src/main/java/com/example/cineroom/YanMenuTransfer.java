package com.example.cineroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class YanMenuTransfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
    }

    public void SifreDegis(View view) {
        Intent intent=new Intent(YanMenuTransfer.this,PasswordChange.class);
        startActivity(intent);
    }

    public void UygulamaHakkinda(View view) {
        Intent intent = new Intent(YanMenuTransfer.this,AppInformationActivity.class);
        startActivity(intent);
    }
}
