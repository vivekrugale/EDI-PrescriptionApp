package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class doctorList extends AppCompatActivity {

    private myadapter.RecyclerViewClickListener listener;
    RecyclerView recview;
    myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recview = (RecyclerView) findViewById(R.id.recviewd);
        recview.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //

    private void setAdapter() {
        setOnclickListener();
        FirebaseRecyclerOptions<model> options =
                new FirebaseRecyclerOptions.Builder<model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Doctors"), model.class)
                        .build();
        adapter = new myadapter(options, listener);
        recview.setLayoutManager(new LinearLayoutManager(this));
        recview.setAdapter(adapter);
    }

    private void setOnclickListener() {
        listener = new myadapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, final String name) {
                SharedPreferences sp = getSharedPreferences("PatientsData", MODE_PRIVATE);
                final SharedPreferences.Editor Edit = sp.edit();

                final String fullname = sp.getString("Pfullname","");
                final String[] email = {sp.getString("Pemail", "")};
                String phone = sp.getString("Pphone","");
                String role = sp.getString("Prole","");

                SharedPreferences nsp = getSharedPreferences("Navigation", MODE_PRIVATE);
                final SharedPreferences.Editor NAVED = nsp.edit();
                NAVED.putString("loginRole",role);
                NAVED.putString("VFPdocNode", name);
                NAVED.putString("VFPpatNode", fullname);
                NAVED.apply();

                users info = new users(fullname, email[0], phone, role);

                FirebaseDatabase.getInstance().getReference("Doctors")
                        .child(name).child("Patients").child(fullname)
                        .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        email[0] = email[0].replace(".", "");
                        final DatabaseReference myRef = database.getReference("Login").child(email[0]);
                        myRef.child("Patient").setValue("Patient");
                        myRef.child("DocNode").setValue(name);
                        myRef.child("PatNode").setValue(fullname);

                        Toast.makeText(doctorList.this, "Registered", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(doctorList.this, ViewForPatient.class);
                        intent.putExtra("docName", name);
                        intent.putExtra("patName", fullname);
                        intent.putExtra("nodeEmail", email);
                        startActivity(intent);
                    }
                });

            }
        };
    }

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