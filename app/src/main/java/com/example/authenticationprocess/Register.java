package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Role, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = textemail.getText().toString().trim();
                String password = textpassword.getText().toString().trim();
                final String fullname = textfullname.getText().toString();
                final String phone = textphone.getText().toString();

                function(email, password, fullname, phone, role);


//                if (TextUtils.isEmpty(email)){
//                    textemail.setError("Plese enter the email");
//                    return;
//                }
//
//                if (TextUtils.isEmpty(password)){
//                    textpassword.setError("Please enter the password");
//                    return;
//                }
//
//                if (password.length() < 6){
//                    textpassword.setError("Password must be at least 6 characters long");
//                    return;
//                }
//
//                progressBar2.setVisibility(View.VISIBLE);
//
//                //Register the user in firebase
//
//                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
//                            //To refer to perticular document in database
//                            userID = fAuth.getCurrentUser().getUid();
//                            DocumentReference documentReference = fStore.collection("users").document(userID);
//                            //store data in document..... string as a key and object as data
//                            Map<String,Object> user = new HashMap<>();
//                            user.put("fName",fullname);
//                            user.put("email",email);
//                            user.put("phone",phone);
//
//                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    Log.d("TAG", "onSuccess: User profile is created for "+ userID);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("TAG", "OnFailure: "+ e.toString());
//                                }
//                            });
//                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                        }
//                        else {
//                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            progressBar2.setVisibility(View.GONE);
//                        }
//
//                    }
//                });
            }
        });

        loginTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        role = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(getApplicationContext(), "You are a " + role, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


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

                    users info = new users(fullname, email, phone, role);
                    String node = email.toString();

                    if (role.equals("Doctor")) {

                        FirebaseDatabase.getInstance().getReference("Doctors")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        });
                    }
                    else if (role.equals("Patient")){

                        FirebaseDatabase.getInstance().getReference("Patients")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), doctorList.class));
                            }
                        });
                    }

//                    DocumentReference documentReference = fStore.collection("Users").document(userID);
//                    //store data in document..... string as a key and object as data
//                    Map<String,Object> user = new HashMap<>();
//                    user.put("Name",fullname);
//                    user.put("Email",email);
//                    user.put("Phone No.",phone);
//                    user.put("Role as a",role);
//
//                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d("TAG", "onSuccess: User profile is created for "+ userID);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("TAG", "OnFailure: "+ e.toString());
//                        }
//                    });
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else {
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar2.setVisibility(View.GONE);
                }

            }
            });
    }
}
