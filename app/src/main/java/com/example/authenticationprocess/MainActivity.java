package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private myadapter.RecyclerViewClickListener listener;
    RecyclerView recview;
    myadapter adapter;
//    String nodeEmail;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recview = (RecyclerView) findViewById(R.id.recview);

//        Intent intent = getIntent();
//        Bundle extras = intent.getExtras();
//        if(extras != null) {
//            String nodeEmail = extras.getString("nodeEmail");
//        }

        setAdapter();
    }

    private void setAdapter() {
        setOnclickListener();

//        final SharedPreferences nsp = getSharedPreferences("Navigation", MODE_PRIVATE);
//        String nodeEmail = nsp.getString("nodeEmail", "");

//        Intent intent = getIntent();
//        String nodeEmail = intent.getStringExtra("nodeEmail");

        Intent i = getIntent();
        String docName = i.getStringExtra("docName");
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference myRef = database.getReference("Login").child(nodeEmail);
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String docName = snapshot.child("DocNode").getValue().toString();
//
//                SharedPreferences sp = getSharedPreferences("Snapshot", MODE_PRIVATE);
//                final SharedPreferences.Editor editSnap = sp.edit();
//                editSnap.putString("docNode", docName);
//                editSnap.apply();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        SharedPreferences prefs = getSharedPreferences("Snapshot", MODE_PRIVATE);
//        String docName = prefs.getString("docNode",""); // Doc name in main tree

        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Doctors").child(docName).child("Patients"), model.class)
                        .build();
        adapter = new myadapter(options, listener);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setAdapter(adapter);
    }

    private void setOnclickListener() {
        listener = new myadapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, String name) {
                SharedPreferences nsp = getSharedPreferences("Navigation", MODE_PRIVATE);
                final SharedPreferences.Editor NAVED = nsp.edit();
                NAVED.putString("patNode", name);
                NAVED.apply();

                Intent i = getIntent();
                String nodeEmail = i.getStringExtra("nodeEmail");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("Login").child(nodeEmail);
                myRef.child("PatNode").setValue(name);

                Intent intent = new Intent(MainActivity.this, viewForDoctor.class);
                intent.putExtra("nodeEmail", nodeEmail);
                startActivity(intent);
            }
        };
    }

    //This is for toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnsearch:
                Toast.makeText(this, "You just pressed search icon", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.btnquit:
                Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                return true;
            case R.id.btnDelAcc:
                Toast.makeText(this, "This will delete you registered account", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Your account will be deleted!");
                builder.setMessage("Proceed?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = getIntent();
                        String docName = i.getStringExtra("docName");
                        String nodeEmail = i.getStringExtra("nodeEmail");

                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(docName).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("Login").child(nodeEmail).removeValue();
                        startActivity(new Intent(getApplicationContext(), Register.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //Up to this

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}