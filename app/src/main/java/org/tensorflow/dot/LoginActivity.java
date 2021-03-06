package org.tensorflow.dot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private EditText emailField,passwordField;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button button1 = (Button) findViewById(R.id.button_register1);
        emailField = (EditText) findViewById(R.id.editText4);
        passwordField = (EditText) findViewById(R.id.editText2);
        mAuth = FirebaseAuth.getInstance();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(View view){

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter your email.", Toast.LENGTH_SHORT).show();
        }
        else if(password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user!=null){
                                    Toast.makeText(LoginActivity.this, "Welcome! "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this,MainPage.class));
                                }

                            } else {
                                Toast.makeText(LoginActivity.this,"Email and password don't match",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

}