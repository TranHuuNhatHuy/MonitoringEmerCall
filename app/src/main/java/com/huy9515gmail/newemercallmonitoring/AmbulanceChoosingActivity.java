package com.huy9515gmail.newemercallmonitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmbulanceChoosingActivity extends AppCompatActivity {

    @BindView(R.id.lvAvailableAmbulances)
    ListView lvAvailableAmbulances;
    @BindView(R.id.linear_noambulances)
    LinearLayout linearNoAmbulances;
    @BindView(R.id.btnNoAmbulances)
    Button btnNoAmbulancesInform;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<Ambulance> availableAmbulances = new ArrayList<>();

    User currentUser;

    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    final DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference("userStatus");
    final DatabaseReference ambulances = FirebaseDatabase.getInstance().getReference("ambulances");
    final DatabaseReference ambulanceStatus = FirebaseDatabase.getInstance().getReference("ambulanceStatus");

    public static final String CURRENTUSER = "currentUser";
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_choosing);
        ButterKnife.bind(this);

        //set toolbar
        toolbar.setTitle("Chọn xe cấp cứu");
        toolbar.setTitleTextColor(Color.WHITE);

        //get current user object
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(CURRENTUSER);
        //acquire user ID
        userID = currentUser.getID();

        //set available ambulances listview
        ambulances.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if (child.child("status").getValue(Integer.class) == 0) {
                        String number = child.child("id").getValue(String.class);
                        Double lat = child.child("latitude").getValue(Double.class);
                        Double lng = child.child("longitude").getValue(Double.class);
                        boolean status = (child.child("status").getValue(Integer.class) == 1) ? (true) : (false);
                        int times = child.child("times").getValue(Integer.class);
                        String id = child.getKey();
                        Ambulance currentAmbulance = new Ambulance(number, lat, lng, status, times, id);
                        availableAmbulances.add(currentAmbulance);
                    }
                }

                ChooseAmbulanceCustomAdapter chooseAmbulanceCustomAdapter = new ChooseAmbulanceCustomAdapter(getBaseContext(), R.layout.row_chooseambulance_listview, availableAmbulances);
                lvAvailableAmbulances.setAdapter(chooseAmbulanceCustomAdapter);

                //handle listview reaction
                lvAvailableAmbulances.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        //get the corresponding ambulance
                        final Ambulance currentAmbulance = availableAmbulances.get(i);
                        //show an analog
                        AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceChoosingActivity.this);
                        builder.setTitle("Xác nhận điều phối");
                        builder.setMessage("Xác nhận giao nhiệm vụ chuyên chở cho xe cấp cứu biển số: " + currentAmbulance.getNumber() + " với số lần hoạt động: " + currentAmbulance.getTimes() + " lần?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //update Firebase database
                                ambulances.child(currentAmbulance.getID()).child("times").setValue((currentAmbulance.getTimes()) + 1); //increase times by 1
                                ambulances.child(currentAmbulance.getID()).child("status").setValue(1); //mark this ambulance active
                                ambulanceStatus.child(currentAmbulance.getID()).setValue(1); //mark this ambulance active
                                userStatus.child(userID).setValue(2); //mark this user have a target ambulance
                                users.child(userID).child("status").setValue(2); //mark inner status of user
                                users.child(userID).child("targetAmbulance").setValue(currentAmbulance.getID()); //set user's target ambulance
                                ambulances.child(currentAmbulance.getID()).child("destination").setValue(userID); //set ambulance's destination
                                //back to main activity
                                UserChoosedActivity.userChoosedAct.finish();
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }
                });

                //if there is no available ambulances
                if (availableAmbulances.size() == 0) {
                    lvAvailableAmbulances.setVisibility(View.GONE);
                    linearNoAmbulances.setVisibility(View.VISIBLE);
                    btnNoAmbulancesInform.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //show an analog
                            AlertDialog.Builder builder = new AlertDialog.Builder(AmbulanceChoosingActivity.this);
                            builder.setTitle("Xác nhận hành động thông báo");
                            builder.setMessage("Xác nhận thông báo cho người dùng: hiện tại không có xe cấp cứu, nên tìm cách chuyên chở nạn nhân đến cơ sở y tế hoặc bệnh viện?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //update user Firebase database
                                    userStatus.child(userID).setValue(0); //mark this user no longer related to EmerCall active state
                                    users.child(userID).child("status").setValue(0); //mark inner status of user
                                    users.child(userID).child("targetAmbulance").setValue("none"); //set user's target ambulance to "none" - which means there is no ambulances
                                    //back to main activity
                                    UserChoosedActivity.userChoosedAct.finish();
                                    finish();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
