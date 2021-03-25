package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewForPatient extends AppCompatActivity {
    RecyclerView recview;
    myadapter_for_patient adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_for_patient);

        Toolbar toolbarP = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbarP);

        swipeRefreshLayout = findViewById(R.id.swipeLayout);

        recview = (RecyclerView)findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));

//        SharedPreferences nsp = getSharedPreferences("Navigation", MODE_PRIVATE);
//        String docNode = nsp.getString("VFPdocNode", "");
//        String patNode = nsp.getString("VFPpatNode", "");

        Intent i = getIntent();
        String docName = i.getStringExtra("docName");
        String patName = i.getStringExtra("patName");

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        final DatabaseReference myRef = database.getReference("Login").child(nodeEmail);
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String docName = snapshot.child("DocNode").getValue().toString(); //OR I can extract this info in login itself and pass intent!
//                String patName = snapshot.child("PatNode").getValue().toString();
//
//                SharedPreferences sp = getSharedPreferences("Snapshot", MODE_PRIVATE);
//                final SharedPreferences.Editor editSnap = sp.edit();
//                editSnap.putString("docNode", docName);
//                editSnap.putString("patNode", patName);
//                editSnap.apply();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        //THIS IS SWIPE REFRESH.........
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                Intent i = getIntent();
//                String nodeEmail = i.getStringExtra("nodeEmail");
//
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                final DatabaseReference myRef = database.getReference("Login").child(nodeEmail);
//                myRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String docName = snapshot.child("DocNode").getValue().toString();
//                        String patName = snapshot.child("PatNode").getValue().toString();
//
//                        SharedPreferences sp = getSharedPreferences("Snapshot", MODE_PRIVATE);
//                        final SharedPreferences.Editor editSnap = sp.edit();
//                        editSnap.putString("docNode", docName);
//                        editSnap.putString("patNode", patName);
//                        editSnap.apply();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

                adapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        SharedPreferences prefs = getSharedPreferences("Snapshot", MODE_PRIVATE);
//        final String docName = prefs.getString("docNode",""); // Doc name in main tree
//        final String patName = prefs.getString("patNode","");

        FirebaseRecyclerOptions<model_patient_recview> options =
                new FirebaseRecyclerOptions.Builder<model_patient_recview>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Doctors").child(docName).child("Patients").child(patName).child("Prescription"), model_patient_recview.class)
                        .build();

        adapter = new myadapter_for_patient(options);
        recview.setAdapter(adapter);

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
                        String patName = i.getStringExtra("patName");
                        String nodeEmail = i.getStringExtra("nodeEmail");

                        FirebaseAuth.getInstance().getCurrentUser().delete();
                        FirebaseDatabase.getInstance().getReference().child("Doctors").child(docName).child(patName).removeValue();
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