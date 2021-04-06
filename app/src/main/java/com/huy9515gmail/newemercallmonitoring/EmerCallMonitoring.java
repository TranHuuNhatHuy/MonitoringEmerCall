package com.huy9515gmail.newemercallmonitoring;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class EmerCallMonitoring extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.lvWaiting)
    ListView lvWaiting;
    @BindView(R.id.lvProcessing)
    ListView lvProcessing;

    ArrayList<User> waitingList = new ArrayList<>();
    ArrayList<User> processingList = new ArrayList<>();

    final DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference("userStatus");
    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    Timer t = new Timer();

    public static final String CURRENTUSER = "currentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emer_call_monitoring);
        ButterKnife.bind(this);

        toolbar.setTitle("Điều phối yêu cầu cấp cứu");
        toolbar.setTitleTextColor(Color.WHITE);

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_requests: break;
                    case R.id.action_users:
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        Intent intent1 = new Intent(EmerCallMonitoring.this, UsersActivity.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_ambulances:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        Intent intent2 = new Intent(EmerCallMonitoring.this, AmbulancesActivity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                //build waiting listview
                waitingList = new ArrayList<>();
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if (child.child("status").getValue(Integer.class) == 1) {
                                String name = child.child("userInfo").child("name").getValue(String.class);
                                String dob = child.child("userInfo").child("dob").getValue(String.class);
                                String gender = child.child("userInfo").child("gender").getValue(String.class);
                                String bhyt = child.child("userInfo").child("bhyt").getValue(String.class);
                                String address = child.child("userInfo").child("address").getValue(String.class);
                                String cmnd = child.child("userInfo").child("cmnd").getValue(String.class);
                                String id = child.getKey();
                                User currentUser = new User(name, dob, gender, bhyt, address, cmnd, id);
                                waitingList.add(currentUser);
                            }
                        }

                        //refresh listview
                        UserCustomAdapter userCustomAdapter = new UserCustomAdapter(getBaseContext(), R.layout.row_user_listview, waitingList);
                        lvWaiting.setAdapter(userCustomAdapter);

                        lvWaiting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                if (waitingList.size() > 0) {
                                    //get the corresponding user
                                    User currentUser = waitingList.get(i);

                                    //initialize intent
                                    Intent intent = new Intent(EmerCallMonitoring.this, UserChoosedActivity.class);
                                    intent.putExtra(CURRENTUSER, currentUser);
                                    startActivity(intent);
                                }

                            }
                        });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                //build processing listview
                processingList = new ArrayList<>();
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if (child.child("status").getValue(Integer.class) == 2) {
                                String name = child.child("userInfo").child("name").getValue(String.class);
                                String dob = child.child("userInfo").child("dob").getValue(String.class);
                                String gender = child.child("userInfo").child("gender").getValue(String.class);
                                String bhyt = child.child("userInfo").child("bhyt").getValue(String.class);
                                String address = child.child("userInfo").child("address").getValue(String.class);
                                String cmnd = child.child("userInfo").child("cmnd").getValue(String.class);
                                String id = child.getKey();
                                User currentUser = new User(name, dob, gender, bhyt, address, cmnd, id);
                                processingList.add(currentUser);
                            }
                        }

                        //refresh listview
                        UserCustomAdapter userCustomAdapter = new UserCustomAdapter(getBaseContext(), R.layout.row_user_listview, processingList);
                        lvProcessing.setAdapter(userCustomAdapter);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                //check for banned users
                final DatabaseReference bannedUsers = FirebaseDatabase.getInstance().getReference("bannedUsers");
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            int fakeCount = child.child("callCount").child("fake").getValue(Integer.class);
                            if (fakeCount >= 3) {
                                DatabaseReference thisBannedUser = bannedUsers.child(child.getKey().toString());
                                thisBannedUser.setValue("");
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }
        }, 0, 5000);

    }

}
