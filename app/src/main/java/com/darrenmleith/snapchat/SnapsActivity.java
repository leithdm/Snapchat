package com.darrenmleith.snapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Snapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class SnapsActivity extends AppCompatActivity {
    private FirebaseAuth _mAuth;

    private ListView _snapsListView;
    private ArrayList<String> _emails;
    private ArrayList<DataSnapshot> _snaps;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.snaps_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.createSnap:
                Intent intent = new Intent(this, CreateSnapActivity.class);
                startActivity(intent);
                return true;

            case R.id.logout:
                _mAuth.signOut();
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        _mAuth.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        _emails = new ArrayList<>();
        _mAuth = FirebaseAuth.getInstance();
        _snapsListView = findViewById(R.id.snapsListView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _emails);
        _snapsListView.setAdapter(arrayAdapter);
        _snaps = new ArrayList<>();

        //need to access logged in users snaps directory. Again, we do this by using the addChildEventListener.
        //Once we have access to it, we add the "from" component to the _emails array list and populate the ListView
        //Ee also add the DataSnapShot to a _snaps Array so we can pass through an intent to ViewSnap activity with all relevant information
        FirebaseDatabase.getInstance().getReference().child("users").child(_mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                _emails.add(dataSnapshot.child("from").getValue().toString());
                _snaps.add(dataSnapshot);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            //also make use of this so that when we delete from ViewSnapActivity the ListView gets updated
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap: _snaps) {
                    int index = 0;
                    if (snap.getKey() == dataSnapshot.getKey()) {
                        _emails.remove(index);
                        _snaps.remove(index);
                    }
                    index++;
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        _snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot snapshot = _snaps.get(position);
                Intent intent = new Intent(SnapsActivity.this, ViewSnapActivity.class);
                intent.putExtra("snapKey", snapshot.getKey());
                intent.putExtra("imageName", snapshot.child("imageName").toString());
                intent.putExtra("imageURL", snapshot.child("imageURL").toString());
                intent.putExtra("message", snapshot.child("message").toString());
            }
        });

    }

}
