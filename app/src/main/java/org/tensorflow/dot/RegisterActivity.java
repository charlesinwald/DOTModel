package org.tensorflow.dot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    Button button;
    ImageView profile;
    EditText emailField, usernameField, passwordField,confirmationField;
    Uri imageUri;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        button = (Button) findViewById(R.id.button_register2);
        usernameField = (EditText) findViewById(R.id.editText4);
        passwordField = (EditText) findViewById(R.id.editText2);
        confirmationField = (EditText) findViewById(R.id.editText3);
        emailField = (EditText) findViewById(R.id.editText5);
        profile = (ImageView) findViewById(R.id.imageView);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void upload(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }

    private void signup(){
        final String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmation = confirmationField.getText().toString();
        final String email = emailField.getText().toString();

        if (!password.equals(confirmation)){
            Toast.makeText(RegisterActivity.this, "The password did not match the confirmed password.", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(RegisterActivity.this, "The password cannot be empty.", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users").child(uid);
                        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String imageurl = task.toString();
                                            UserModel userModel = new UserModel();
                                            userModel.uid = uid;
                                            userModel.email = email;
                                            userModel.name = username;
                                            userModel.imageurl = imageurl;
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel);
                                        }
                                    });
                                }
                            }
                        });
                        Intent intent = new Intent();
                        intent.setClass(RegisterActivity.this, MainPage.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(RegisterActivity.this,"Connect Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

}