package com.huy9515gmail.newemercallmonitoring;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmbulancesActivity extends AppCompatActivity {

    final DatabaseReference ambulances = FirebaseDatabase.getInstance().getReference("ambulances");

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.lvAmbulances)
    ListView lvAmbulances;

    public Timer t = new Timer();

    ArrayList<Ambulance> ambulanceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulances);
        ButterKnife.bind(this);

        toolbar.setTitle("Thông tin xe cấp cứu");
        toolbar.setTitleTextColor(Color.WHITE);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ambulances: break;
                    case R.id.action_requests:
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        Intent intent1 = new Intent(AmbulancesActivity.this, EmerCallMonitoring.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_users:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent2 = new Intent(AmbulancesActivity.this, UsersActivity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                //renew ambulance list
                ambulanceList = new ArrayList<>();

                //acquire data on Firebase
                ambulances.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            String number = child.child("id").getValue(String.class);
                            Double lat = child.child("latitude").getValue(Double.class);
                            Double lng = child.child("longitude").getValue(Double.class);
                            boolean status = (child.child("status").getValue(Integer.class) == 1) ? (true) : (false);
                            int times = child.child("times").getValue(Integer.class);
                            String id = child.getKey();
                            Ambulance currentAmbulance = new Ambulance(number, lat, lng, status, times, id);
                            ambulanceList.add(currentAmbulance);
                        }

                        //refresh listview
                        AmbulanceCustomAdapter ambulanceCustomAdapter = new AmbulanceCustomAdapter(getBaseContext(), R.layout.row_ambulance_listview, ambulanceList);
                        lvAmbulances.setAdapter(ambulanceCustomAdapter);
                        Toast.makeText(AmbulancesActivity.this, "Danh sách xe cấp cứu vừa được cập nhật!", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }, 0, 10000);

    }

    @Override
    public void onPause() {
        super.onPause();
        t.cancel();
        t.purge();
    }

}
