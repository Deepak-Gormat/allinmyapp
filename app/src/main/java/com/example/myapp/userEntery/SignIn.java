package com.example.myapp.userEntery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignIn extends AppCompatActivity {

    EditText phoneNumber, verifyCode;
    Button sendVerifyCodeBtn, verifyCodeBtn;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_sign_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initilizeFields();

        sendVerifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userPhoneNumber = phoneNumber.getText().toString();
                if (userPhoneNumber.isEmpty()){
                    Toast.makeText(SignIn.this, "Wirte Phone Number", Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please Wait, While Verify Number ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            userPhoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            SignIn.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                verifyCode.setVisibility(View.GONE);
                verifyCodeBtn.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.VISIBLE);
                sendVerifyCodeBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                Toast.makeText(SignIn.this, "Code Sent, Please Check!", Toast.LENGTH_SHORT).show();
                verifyCode.setVisibility(View.VISIBLE);
                verifyCodeBtn.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.GONE);
                sendVerifyCodeBtn.setVisibility(View.GONE);

            }

        };



        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode.setVisibility(View.VISIBLE);
                verifyCodeBtn.setVisibility(View.VISIBLE);
                phoneNumber.setVisibility(View.GONE);
                sendVerifyCodeBtn.setVisibility(View.GONE);

                String verificationCode = verifyCode.getText().toString();
                if (verificationCode.isEmpty()) {
                    Toast.makeText(SignIn.this, "Enter 6 Digit Code.", Toast.LENGTH_SHORT).show();
                }else {

                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Please Wait, While Verify Code ");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            sendToMainActivity();
        }
    }

    private void initilizeFields() {
        phoneNumber = findViewById(R.id.userPhone);
        verifyCode = findViewById(R.id.verifyCode);
        sendVerifyCodeBtn = findViewById(R.id.sendVerifyCodeBtn);
        verifyCodeBtn = findViewById(R.id.VerifyCodeBtn);
        loadingBar = new ProgressDialog(SignIn.this);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(SignIn.this, "Congrats!, You Are Now Family Of My App", Toast.LENGTH_SHORT).show();
                            sendToMainActivity();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void sendToMainActivity() {
    Intent sendToMain = new Intent(SignIn.this, MainActivity.class);
    startActivity(sendToMain);
    finish();
    }


}