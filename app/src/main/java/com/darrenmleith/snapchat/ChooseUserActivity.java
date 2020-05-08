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

        _chooseUserListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> snapMap = new HashMap<>();
                snapMap.put("from", "");
                snapMap.put("imageName", "");
                snapMap.put("imageURL", "");
                snapMap.put("message", "");
                FirebaseDatabase.getInstance().getReference().child("users").child(_keys.get(position)).child("snaps").push().setValue(snapMap); //push() puts a random UID
                Intent intent = new Intent(getApplicationContext(), SnapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //wipe everything out of the back button history
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            _emails.add((String) dataSnapshot.child("email").getValue());
            _keys.add(dataSnapshot.getKey());
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
    }


}
