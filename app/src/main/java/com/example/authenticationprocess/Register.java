package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.HashMap;
import java.util.Map;

import static android.widget.ProgressBar.*;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText textfullname, textemail, textpassword, textphone;
    Button btnregister;
    TextView loginTextView;
    ProgressBar progressBar2;
    String role;
    Spinner spinner;

    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textfullname = findViewById(R.id.textfullname);
        textemail = findViewById(R.id.textemail);
        textpassword = findViewById(R.id.textpassword);
        textphone = findViewById(R.id.textphone);
        btnregister = findViewById(R.id.btnregister);
        loginTextView = (TextView) findViewById(R.id.loginTextView);
        progressBar2 = findViewById(R.id.progressBar2);
        spinner = findViewById(R.id.spinner);

        fAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this); //

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = textemail.getText().toString().trim();
                String password = textpassword.getText().toString().trim();
                final String fullname = textfullname.getText().toString();
                final String phone = textphone.getText().toString();

                function(email, password, fullname, phone, role);
            }
        });

        loginTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }

    //This is for spinner button
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        role = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), "You are a " + role, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    //


    private void function(final String email, String password, final String fullname, final String phone, final String role) {

        if (TextUtils.isEmpty(fullname)){
            textpassword.setError("Please enter your name");
            return;
        }

        if (TextUtils.isEmpty(email)){
            textemail.setError("Plese enter the email");
            return;
        }

        if (TextUtils.isEmpty(password)){
            textpassword.setError("Please enter the password");
            return;
        }

        if (password.length() < 6){
            textpassword.setError("Password must be at least 6 characters long");
            return;
        }

        if (TextUtils.isEmpty(phone)){
            textpassword.setError("Please enter your phone number");
            return;
        }

        progressBar2.setVisibility(View.VISIBLE);

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Register.this ,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    SharedPreferences sp = getSharedPreferences("PatientsData", MODE_PRIVATE);
                    final SharedPreferences.Editor Edit = sp.edit();

                    users info = new users(fullname, email, phone, role);
                    final String nodeName = textfullname.getText().toString();

                    if (role.equals("Doctor")) {
                        SharedPreferences nsp = getSharedPreferences("Navigation", MODE_PRIVATE);
                        final SharedPreferences.Editor NAVED = nsp.edit();

                        FirebaseDatabase.getInstance().getReference("Doctors")
                                .child(nodeName)
                                .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String nodeEmail = textemail.getText().toString();
                                nodeEmail = nodeEmail.replace(".", "");

                                final DatabaseReference myRef = database.getReference("Login").child(nodeEmail);
                                myRef.child("Doctor").setValue("Doctor");
                                myRef.child("DocNode").setValue(nodeName);

                                String tryEmail = nodeEmail;

                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                String docNode = textfullname.getText().toString();
                                NAVED.putString("loginRole", role);
                                NAVED.putString("docNode", docNode);
                                NAVED.putString("nodeEmail",tryEmail);
                                NAVED.apply();

                                Intent intent = new Intent(Register.this, MainActivity.class);
                                intent.putExtra("docName", docNode);
                                intent.putExtra("nodeEmail", tryEmail);
                                startActivity(intent);
                            }
                        });
                    }
                    else if (role.equals("Patient")){

                        Edit.putString("Pfullname", fullname);
                        Edit.putString("Pemail", email);
                        Edit.putString("Pphone", phone);
                        Edit.putString("Prole", role);
                        Edit.apply();

                        startActivity(new Intent(getApplicationContext(), doctorList.class));
                    }

                }
                else {
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar2.setVisibility(View.GONE);
                }

            }
            });
    }
}
