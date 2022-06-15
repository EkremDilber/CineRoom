package com.example.cineroom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChange extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    EditText databasePassword, password,password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        databasePassword=findViewById(R.id.editTextTextPassword4);
        password=findViewById(R.id.editTextTextPassword5);
        password2=findViewById(R.id.editTextTextPassword6);
    }

    public void passwordChanced(View view){
        String database=databasePassword.getText().toString();
        String newPassword=password.getText().toString();
        String newPassword2=password2.getText().toString();


        if (database.matches("")&&newPassword.matches("")&&newPassword2.matches("")){
            Toast.makeText(getApplicationContext(),"Boş Bırakılamaz!",Toast.LENGTH_LONG).show();
        }else if (!newPassword.matches(newPassword2)){
            Toast.makeText(getApplicationContext(),"Şifreler Farklı",Toast.LENGTH_LONG).show();
        }else if (newPassword.length()<6){
            Toast.makeText(getApplicationContext(),"Şifre 6 Karakterden Küçük Olamaz",Toast.LENGTH_LONG).show();
        }else{
            firebaseUser.updatePassword(newPassword).addOnSuccessListener(aVoid -> {
                AlertDialog.Builder dialog=new AlertDialog.Builder(PasswordChange.this);
                dialog.setTitle("Şifre Değişikliği");
                dialog.setMessage("Şifreniz Başarıyla Değiştirildi");
                dialog.setPositiveButton("Tamam", (dialog1, which) -> {
                    Intent intent=new Intent(PasswordChange.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                });
                dialog.show();
            }).addOnFailureListener(e -> {
                AlertDialog.Builder dialog=new AlertDialog.Builder(PasswordChange.this);
                dialog.setTitle("Hatalı");
                dialog.setMessage("Şifreniz Değiştirilemedi Tekrar Deneyiniz");
                dialog.setPositiveButton("Tamam", (dialog12, which) -> {
                    databasePassword.setText("");
                    password.setText("");
                    password2.setText("");
                });
            });
        }

    }
}