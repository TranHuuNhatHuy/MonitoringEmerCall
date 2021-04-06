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

public class UsersActivity extends AppCompatActivity {

    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.lvUsers)
    ListView lvUsers;

    public Timer t = new Timer();

    ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        toolbar.setTitle("Thông tin người dùng");
        toolbar.setTitleTextColor(Color.WHITE);

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_users: break;
                    case R.id.action_requests:
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        Intent intent1 = new Intent(UsersActivity.this, EmerCallMonitoring.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_ambulances:
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        Intent intent2 = new Intent(UsersActivity.this, AmbulancesActivity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                //renew user list
                userList = new ArrayList<>();

                //acquire data on Firebase
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            String name = child.child("userInfo").child("name").getValue(String.class);
                            String dob = child.child("userInfo").child("dob").getValue(String.class);
                            String gender = child.child("userInfo").child("gender").getValue(String.class);
                            String bhyt = child.child("userInfo").child("bhyt").getValue(String.class);
                            String address = child.child("userInfo").child("address").getValue(String.class);
                            String cmnd = child.child("userInfo").child("cmnd").getValue(String.class);
                            String id = child.getKey();
                            User currentUser = new User(name, dob, gender, bhyt, address, cmnd, id);
                            userList.add(currentUser);
                        }

                        //refresh listview
                        UserCustomAdapter userCustomAdapter = new UserCustomAdapter(getBaseContext(), R.layout.row_user_listview, userList);
                        lvUsers.setAdapter(userCustomAdapter);
                        Toast.makeText(UsersActivity.this, "Danh sách người dùng vừa được cập nhật!", Toast.LENGTH_SHORT).show();

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

