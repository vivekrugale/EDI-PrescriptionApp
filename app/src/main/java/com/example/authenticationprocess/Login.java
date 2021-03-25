package com.example.authenticationprocess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText textemail, textpassword;
    Button btnlogin;
    TextView createTextView;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    // Next possible problem is deleting accounts. Patient account delete not working properly (Not getting deleted from doc node)
    // If a doc deletes his acc, pats under him has to go to select doc page
    // From their again pat info has to re-register. HOW?
    // By the way, these are accessories. MAIN CONCEPT FULLY FUNCTIONAL!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textemail = findViewById(R.id.textemail);
        textpassword = findViewById(R.id.textpassword);
        btnlogin = findViewById(R.id.btnlogin);
        createTextView = findViewById(R.id.createTextView);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences sp = getSharedPreferences("PatientsData", MODE_PRIVATE);

                String email = textemail.getText().toString().trim();
                String password = textpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    textemail.setError("Plese enter the email");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    textpassword.setError("Please enter the password");
                    return;
                }

                if (password.length() < 6) {
                    textpassword.setError("Password must be at least 6 characters long");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //finish();

                            String loginRole = sp.getString("loginRole", "");
                            String loginEmail = textemail.getText().toString().trim();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            loginEmail = loginEmail.replace(".", "");
                            final DatabaseReference myRef = database.getReference("Login").child(loginEmail);

                            final String finalLoginEmail = loginEmail;
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.hasChild("Doctor")) {

                                        myRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String docName = snapshot.child("DocNode").getValue().toString();

                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                intent.putExtra("docName", docName);
                                                intent.putExtra("nodeEmail", finalLoginEmail);
                                                startActivity(intent);
                                                progressBar.setVisibility(View.GONE);

//                                                SharedPreferences sp = getSharedPreferences("Snapshot", MODE_PRIVATE);
//                                                final SharedPreferences.Editor editSnap = sp.edit();
//                                                editSnap.putString("docNode", docName);
//                                                editSnap.putString("patNode", patName);
//                                                editSnap.apply();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                    else {
                                        myRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String docName = snapshot.child("DocNode").getValue().toString(); //This DocNode is not getting retrieved for some reason.
                                                String patName = snapshot.child("PatNode").getValue().toString();

                                                Intent intent = new Intent(Login.this, ViewForPatient.class);
                                                intent.putExtra("docName", docName);
                                                intent.putExtra("patName", patName);
                                                intent.putExtra("nodeEmail", finalLoginEmail);
                                                startActivity(intent);
                                                progressBar.setVisibility(View.GONE);

//                                                SharedPreferences sp = getSharedPreferences("Snapshot", MODE_PRIVATE);
//                                                final SharedPreferences.Editor editSnap = sp.edit();
//                                                editSnap.putString("docNode", docName);
//                                                editSnap.putString("patNode", patName);
//                                                editSnap.apply();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        createTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}