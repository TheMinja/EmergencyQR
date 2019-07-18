package com.retro.emergencyqr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.retro.emergencyqr.R;
import com.retro.emergencyqr.framework.manager.FirebaseUIManager;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.signUpButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpButton: {
                String user = ((EditText) findViewById(R.id.signUpUsername)).getText().toString();
                String password = ((EditText) findViewById(R.id.signUpPassword)).getText().toString();
                String confPassword = ((EditText) findViewById(R.id.signUpConfPassword)).getText().toString();

                if (!password.equals(confPassword)) {
                    Toast.makeText(this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    FirebaseUIManager.getInstance(this).createUserWithEmailAndPassword(user, password, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }
                break;
            }
        }
    }
}
