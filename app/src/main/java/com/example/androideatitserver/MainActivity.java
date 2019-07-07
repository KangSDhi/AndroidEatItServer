package com.example.androideatitserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatitserver.Common.Common;
import com.example.androideatitserver.Model.ModelPengguna;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView TxtHeader;
    Typeface customHeader, customTextButton;
    MaterialButton SignButton;
    TextInputLayout LayUser, LayPassword;
    TextInputEditText EdtUser, EdtPassword;
    MaterialButton BtnSignIn;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Pengguna");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TxtHeader = (TextView)findViewById(R.id.textHeader);
        customHeader = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Bold.ttf");
        TxtHeader.setTypeface(customHeader);

        SignButton = (MaterialButton)findViewById(R.id.btnSignIn);
        customTextButton = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Light.ttf");
        SignButton.setTypeface(customTextButton);

        LayUser = (TextInputLayout)findViewById(R.id.LayUsername);
        LayPassword = (TextInputLayout)findViewById(R.id.LayPassword);

        EdtUser = (TextInputEditText)findViewById(R.id.edtUsername);
        EdtPassword = (TextInputEditText)findViewById(R.id.edtPassword);

        BtnSignIn = (MaterialButton)findViewById(R.id.btnSignIn);

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funLogin();
            }
        });


    }

    private void funLogin() {
        final String Username = EdtUser.getText().toString();
        final String Password = EdtPassword.getText().toString();

        if (Username.isEmpty() && Password.isEmpty()){
            LayUser.setError("Mohon Form Username Diisi!");
            LayPassword.setError("Mohon Form Password Diisi!");
        }else if (Username.isEmpty()) {
            LayUser.setError("Mohon Form Username Diisi!");
            LayPassword.setError(null);
        }else if (Password.isEmpty()){
            LayUser.setError(null);
            LayPassword.setError("Mohon Form Password Diisi!");
        }else{
            LayUser.setError(null);
            LayPassword.setError(null);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Username).exists()){
                        ModelPengguna modelPengguna = dataSnapshot.child(Username).getValue(ModelPengguna.class);
                        if (Boolean.parseBoolean(modelPengguna.getAdalahStaff())){
                            if (modelPengguna.getSandi().equals(Password)){
                                Common.currentModelPengguna = modelPengguna;
                                startActivity(new Intent(MainActivity.this, Dasbor.class));
                            }else {
                                LayUser.setError(null);
                                LayPassword.setError("Sandi Salah!");
                            }
                        }else {
                            LayUser.setError("Anda Bukan Admin!");
                            LayPassword.setError(null);
                        }
                    }else {
                        LayUser.setError("Pengguna Tidak Ditemukan!");
                        LayPassword.setError(null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
