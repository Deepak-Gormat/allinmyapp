package com.example.myapp.userEntery;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.security.EncDec;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserInfoActivity extends AppCompatActivity {

    private TextView errorMsg;
    private Button saveBtn;
    private EditText uName,uEmail,uPhoneN;
    private FirebaseDatabase database;
    private String securityCode = "MySecure";
    DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference().child("User Details");


        saveBtn = (Button) findViewById(R.id.saveBtn);
        uName = (EditText) findViewById(R.id.uNameT);
        uEmail = (EditText) findViewById(R.id.uEmailT);
        uPhoneN = (EditText) findViewById(R.id.uPhoneNoT);
        errorMsg = (TextView) findViewById(R.id.error_msg);

        retriveUserInfo();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                savUserDetails();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void savUserDetails() {
        final String userName = uName.getText().toString();
        final String userMail = uEmail.getText().toString();
        final String uPhoneNo = uPhoneN.getText().toString();

        final String userSname = EncDec.encrypt(userName,securityCode);

        if (userName.isEmpty() || userMail.isEmpty() || uPhoneNo.isEmpty()){
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText("All Fields Are Mandatory!");
        }
        else{
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap<String,Object> saveUserDatilsDB = new HashMap<>();
                    saveUserDatilsDB.put("name" , userSname);
                    saveUserDatilsDB.put("mail",userMail);
                    saveUserDatilsDB.put("phNumber", uPhoneNo);
                    userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(saveUserDatilsDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(UserInfoActivity.this, "Save Success!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void retriveUserInfo(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            String nameFromDb = snapshot.child("name").getValue().toString();
                            String decryptName = EncDec.decrypt(nameFromDb,securityCode);
                            errorMsg.setVisibility(View.VISIBLE);
                            errorMsg.setText(decryptName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}