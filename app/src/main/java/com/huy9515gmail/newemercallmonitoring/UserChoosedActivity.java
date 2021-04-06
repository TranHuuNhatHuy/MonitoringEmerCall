package com.huy9515gmail.newemercallmonitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserChoosedActivity extends AppCompatActivity {

    public static AppCompatActivity userChoosedAct;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDOB)
    TextView tvDOB;
    @BindView(R.id.tvGender)
    TextView tvGender;
    @BindView(R.id.tvBHYT)
    TextView tvBHYT;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvCMND)
    TextView tvCMND;

    @BindView(R.id.img_gender)
    ImageView img_Gender;
    @BindView(R.id.tv_gender)
    TextView tv_Gender;
    @BindView(R.id.img_age)
    ImageView img_Age;
    @BindView(R.id.tv_age)
    TextView tv_Age;

    @BindView(R.id.btn_patient_symptoms)
    Button btnPatientSymptoms;
    @BindView(R.id.btn_choose_ambulance)
    Button btnChooseAmbulance;

    User currentUser;

    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");

    public static final String CURRENTUSER = "currentUser";
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_choosed);
        ButterKnife.bind(this);

        //get this activity responding key
        userChoosedAct = this;

        //set toolbar
        toolbar.setTitle("Thông tin về yêu cầu cấp cứu");
        toolbar.setTitleTextColor(Color.WHITE);

        //get current user object
        Intent intent = getIntent();
        currentUser = (User) intent.getSerializableExtra(CURRENTUSER);
        //set current user info
        tvName.setText(currentUser.getName());
        tvDOB.setText(currentUser.getDOB());
        tvGender.setText(currentUser.getGender());
        tvBHYT.setText(currentUser.getBHYT());
        tvAddress.setText(currentUser.getAddress());
        tvCMND.setText(currentUser.getCMND());

        //acquire user ID
        userID = currentUser.getID();
        //get patient database via user Firebase database
        DatabaseReference targetUserRef = users.child(userID);
        DatabaseReference targetPatientRef = targetUserRef.child("patient");
        DatabaseReference targetPatientAge = targetPatientRef.child("age");
        DatabaseReference targetPatientGender = targetPatientRef.child("gender");
        //acquire patient info
        targetPatientAge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int age = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (age <= 2) img_Age.setImageResource(R.drawable.ic_newborn);
                else if (age <= 9) img_Age.setImageResource(R.drawable.ic_children);
                else if (age <= 15) img_Age.setImageResource(R.drawable.ic_youngkid);
                else if (age <= 32) img_Age.setImageResource(R.drawable.ic_youngman);
                else if (age <= 50) img_Age.setImageResource(R.drawable.ic_adult);
                else img_Age.setImageResource(R.drawable.ic_oldman); //displaying age icon
                tv_Age.setText(age + " tuổi"); //displaying age
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        targetPatientGender.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gender = dataSnapshot.getValue(String.class);
                //displaying patient gender and age
                switch (gender) {
                    case "Nam": img_Gender.setImageResource(R.drawable.ic_male); break;
                    case "Nữ": img_Gender.setImageResource(R.drawable.ic_female); break;
                } //displaying gender icon
                tv_Gender.setText(gender);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //set patient symptoms button
        btnPatientSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get patient Firebase database
                DatabaseReference targetUserRef = users.child(userID);
                DatabaseReference targetPatientRef = targetUserRef.child("patient");
                DatabaseReference targetPatientSymptoms = targetPatientRef.child("symptoms");
                targetPatientSymptoms.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get patient symptoms
                        ArrayList<String> symptoms = new ArrayList<>();
                        //create dialog message
                        String builderMessage = new String("");
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            symptoms.add(child.getKey().toString());
                            builderMessage = builderMessage + "- " + child.getKey().toString() +"\n";
                        }
                        //prompt the controller the symptoms via dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserChoosedActivity.this);
                        builder.setTitle("Các tình trạng, triệu chứng của nạn nhân");
                        builder.setMessage(builderMessage)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

        //set ambulance choosing button
        btnChooseAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //initialize intent
                Intent intent = new Intent(UserChoosedActivity.this, AmbulanceChoosingActivity.class);
                intent.putExtra(CURRENTUSER, currentUser);
                startActivity(intent);
            }
        });

    }
}
