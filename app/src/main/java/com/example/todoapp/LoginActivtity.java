package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.PrivateKey;
import java.security.ProviderException;

enum ProviderType{
    EMAIL,
    GOOGLE
}

public class LoginActivtity extends AppCompatActivity {

    private final int GOOGLE_SIGN_IN = 100;

    private EditText edt_email;
    private EditText edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activtity);

        edt_email = findViewById(R.id.edtEmail);
        edt_password = findViewById(R.id.edtPassword);

        session();
    }

    private void session(){
        SharedPreferences preferences = getSharedPreferences("com.example.todoapp", MODE_PRIVATE);
        String email = preferences.getString("email", null);
        String provider = preferences.getString("provider", null);

        //Si tenemos ya una cuenta guardada localmente, iniciamos sesión en esa cuenta
        if(email != null && provider != null){
            logIn(ProviderType.valueOf(provider), email);
        }
    }

    public void signInGoogleOnClick(View view){
        GoogleSignInOptions googleConf = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("262823118525-2jjvg2qcbfuoni84k2vpfs207fb2hhtu.apps.googleusercontent.com")
                .requestEmail().build();
        GoogleSignInClient googleClient = GoogleSignIn.getClient(this, googleConf);
        startActivityForResult(googleClient.getSignInIntent(), GOOGLE_SIGN_IN);
    }

    public void signInOnClick(View view){
        String email = String.valueOf(edt_email.getText());
        String password = String.valueOf(edt_password.getText());
        if(email != null && password != null){
            signIn(ProviderType.EMAIL, email, password);
        }else{
            Toast.makeText(LoginActivtity.this, getString(R.string.Introduce_credentials),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void signIn(ProviderType provider, String email, String password){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    logIn(provider, email);
                }else{
                    Toast.makeText(LoginActivtity.this, getString(R.string.Could_not_sign_in),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signUpOnClick(View view){
        String email = String.valueOf(edt_email.getText());
        String password = String.valueOf(edt_password.getText());
        if( !email.isEmpty() && !password.isEmpty() ){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        signIn(ProviderType.EMAIL, email, password);
                    }else{
                        Toast.makeText(LoginActivtity.this, getString(R.string.Could_not_register),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void logIn(ProviderType provider, String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("provider", provider.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Si se inicia sesión con Google
        if(requestCode == GOOGLE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null){
                    //Obtenemos las credenciales e iniciamos sesi'ón
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                logIn(ProviderType.GOOGLE, account.getEmail());
                            }else{
                                Toast.makeText(LoginActivtity.this, getString(R.string.Could_not_sign_in),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}