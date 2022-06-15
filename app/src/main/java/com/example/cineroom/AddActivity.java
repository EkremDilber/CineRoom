package com.example.cineroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    Calendar calendar;
    CalendarView calendarView;
    int hour,dakika;
    TextView tarih,saat,paketDetay,filmAdi,filmKonusu;
    EditText phone,adi;
    ImageView imageView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseFirestore firebaseFirestore;
    Spinner AcilirListe, turList,filmSecim;


    String[] filmTurleri={"Aksiyon","Aile","Gerilim","Korku","Dram","Macera","Suç","Komedi","BilimKurgu","Çocuk"};

    String[] paket={"Standart ","Sulu Dünya","Sınısız Paket","Kutlama","Çikolata Dünyası"};
    String[] detay={"2 Adet İçecek + 1 Adet Popcorn \n 50TL","Sınırsız İçecek + 2 Adet Popcorn\n 75TL","Sınırsız içecek + Sınırsız Popcorn\n 100TL","Pasta + Sınırsız İçecek + Sınırsız Popcorn + Süslenmiş Ada\n 200TL","Sınırsız İçecek + Sınırsız Popcorn + Sınırsız Çikolata\n 120TL"};
    ArrayAdapter adp,filmAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        calendar=Calendar.getInstance();
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        dakika=calendar.get(Calendar.MINUTE);
        calendarView=findViewById(R.id.calendarView);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();


        tarih=findViewById(R.id.textView5);
        saat=findViewById(R.id.textView6);
        paketDetay=findViewById(R.id.textView38);
        phone=findViewById(R.id.editTextPhone);

        filmAdi=findViewById(R.id.textView41);
        filmKonusu=findViewById(R.id.textView42);
        imageView=findViewById(R.id.imageView24);

        adi=findViewById(R.id.editTextTextPersonName2);

        AcilirListe=findViewById(R.id.acilirList);
        turList=findViewById(R.id.filmList);

        adp=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,paket);
        AcilirListe.setAdapter(adp);

        filmAdapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,filmTurleri);
        turList.setAdapter(filmAdapter);

        bottomNavigationView=findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.appointment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.appointment:

                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });


        calendarView.setMinDate(System.currentTimeMillis()-1000);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date=dayOfMonth+"/"+(month+1)+"/"+year;
                tarih.setText(date);

                TimePickerDialog timePickerDialog=new TimePickerDialog(AddActivity.this,new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        saat.setText(hourOfDay + ":"+ minute);
                    }
                },hour,dakika,true);
                timePickerDialog.show();
            }
        });


        AcilirListe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paketDetay.setText(detay[AcilirListe.getSelectedItemPosition()]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        turList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CollectionReference collectionReference=firebaseFirestore.collection("filmler");
                collectionReference.whereEqualTo("turu",turList.getSelectedItem().toString()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error!=null){
                            Toast.makeText(AddActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                        if(value!=null){
                            ArrayList<String> filmler=new ArrayList<>();
                            ArrayList<String> image=new ArrayList<>();
                            ArrayList<String>  konu=new ArrayList<>();
                            ListView listView=findViewById(R.id.listView);
                            for (DocumentSnapshot snapshot: value.getDocuments()){
                                Map<String,Object> data=snapshot.getData();
                                String filmName=(String) data.get("adi");
                                String afis=(String) data.get("afisi");
                                String konusu=(String) data.get("konusu");

                               filmler.add(filmName);
                               image.add(afis);
                               konu.add(konusu);


                            }
                            ArrayAdapter arrayAdapter =new ArrayAdapter(AddActivity.this,android.R.layout.simple_dropdown_item_1line,filmler);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    filmAdi.setText(filmler.get(position));
                                    Picasso.get().load(image.get(position)).into(imageView);
                                    filmKonusu.setText(konu.get(position));
                                }
                            });
                        }

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void randevuAl(View view){

        Calendar saniye=Calendar.getInstance();

        String kayıtYeri=phone.getText().toString()+(saniye.get(Calendar.SECOND));
        System.out.println(kayıtYeri);

        HashMap<String,Object> userData=new HashMap<>();
        userData.put("Tarih",tarih.getText().toString());
        userData.put("Seans",saat.getText().toString());
        userData.put("FilmAdi",filmAdi.getText().toString());
        userData.put("Paket",AcilirListe.getSelectedItem().toString());
        userData.put("Tel",phone.getText().toString());
        userData.put("Oda","");
        userData.put("Mail",firebaseUser.getEmail());
        userData.put("Durum","Onay Bekliyor");
        userData.put("AdSoyad:",adi.getText().toString());
        userData.put("randevuid",kayıtYeri);

        System.out.println(userData);

        databaseReference.child("randevu").child(kayıtYeri).setValue(userData).addOnCompleteListener(AddActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddActivity.this,"Randevunuz başarıyla alındı",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AddActivity.this,"Randevunuz alınamadı!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}