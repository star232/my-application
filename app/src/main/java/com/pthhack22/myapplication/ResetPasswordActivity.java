package com.pthhack22.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText resetEt;
    Button resetBtn;
    String email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reset_password );
        resetEt=findViewById( R.id.ResetEmailId );
        resetBtn=findViewById( R.id.btnReset );
        email=resetBtn.getText().toString();
        firebaseAuth=FirebaseAuth.getInstance();

        resetBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email=resetEt.getText().toString();
                if(email.isEmpty())
                {
                    resetEt.setError( "Please enter your email" );
                    resetEt.requestFocus();
                    return;
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail( email ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText( ResetPasswordActivity.this, "Please check your email", Toast.LENGTH_SHORT ).show();
                                startActivity( new Intent( ResetPasswordActivity.this,LoginActivity.class ) );
                                finish();
                            }
                            else
                            {
                                Toast.makeText( ResetPasswordActivity.this, "The email address is not correct! ", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
                }
            }
        } );
    }
}
