package com.darrenmleith.snapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {

    ListView _chooseUserListView;
    ArrayList<String> _emails;
    ArrayList<String> _keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        _chooseUserListView = findViewById(R.id.chooseUserListView);
        _emails = new ArrayList<>();
        _keys = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, _emails);
        _chooseUserListView.setAdapter(arrayAdapter);


        //This is how we populate the ListView with all the users. This is NOT like a SQL SELECT * USERS statement.
        //We use the .addChildEventListener method and add each users UID to a local _keys array and their email to an _emails array
        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                _keys.add(dataSnapshot.getKey());
                _emails.add((String) dataSnapshot.child("email").getValue());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



        _chooseUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> snapMap = new HashMap<>();
                snapMap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                snapMap.put("imageName", getIntent().getStringExtra("imageName"));
                snapMap.put("imageURL", getIntent().getStringExtra("imageURL"));
                snapMap.put("message", getIntent().getStringExtra("message"));

                //after selecting the user we want to post image/message to, we get that users UID from Firebase using _keys and then add a "snaps" child along with a
                // > UID and > from,imageName,imageURL,message from the HashMap.
                FirebaseDatabase.getInstance().getReference().child("users").child(_keys.get(position)).child("snaps").push().setValue(snapMap); //push() puts a random UID
                Intent intent = new Intent(getApplicationContext(), SnapsActivity.class); //shoot back to the SnapsActivity where we can display all snaps for logged in user
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //wipe everything out of the back button history
                startActivity(intent);
            }
        });
    }
}
