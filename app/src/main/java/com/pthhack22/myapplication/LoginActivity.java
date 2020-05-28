package com.pthhack22.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    TextView btn;
    EditText inputEmail, inputUsername;
    Button btnLogin;
    private FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;
    private Button btnGoogle;
    GoogleSignInClient mGoogleSignInClient;
    private Button btnFacebook;
    private CallbackManager mCallbackManager;
    TextView textView;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        mCallbackManager=CallbackManager.Factory.create();
        btn=findViewById( R.id.textViewSignUp );
        inputEmail=findViewById( R.id.inputEmail );
        inputUsername=findViewById( R.id.inputUsername );
        btnLogin=findViewById( R.id.btnLogin );
        textView = findViewById( R.id.fogotPassword );
        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCrededentials();
            }
        } );
        mAuth=FirebaseAuth.getInstance();
        mLoadingBar=new ProgressDialog( LoginActivity.this );
        btnGoogle=findViewById( R.id.btnGoogle );
        textView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( LoginActivity.this,ResetPasswordActivity.class ) );
            }
        } );



        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(LoginActivity.this,RegisterActivity.class ) );
            }
        } );
        textView = findViewById( R.id.txtJustSkip );
        textView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( LoginActivity.this,HomeActivity.class ) );
            }
        } );


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient( this, gso );
        btnGoogle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        } );

    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult( ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText( this, e.toString(), Toast.LENGTH_SHORT ).show();
                //                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText( LoginActivity.this, user.getEmail()+user.getDisplayName(), Toast.LENGTH_SHORT ).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText( LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT ).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent=new Intent( LoginActivity.this,HomeActivity.class );
        startActivity( intent );
    }


    private void checkCrededentials() {

        String email=inputEmail.getText().toString();
        String password=inputUsername.getText().toString();


        if(email.isEmpty() || !email.contains( "@" ))
        {
            showError( inputEmail,"Email is not valid!" );
        }
        else if(password.isEmpty() || password.length()<7)
        {
            showError( inputUsername,"Password must be 7 character" );
        }
        else
        {
            mLoadingBar.setTitle( "Login " );
            mLoadingBar.setMessage( "Please wait while check your credentials" );
            mLoadingBar.setCanceledOnTouchOutside( false );
            mLoadingBar.show();

            mAuth.signInWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        mLoadingBar.dismiss();
                        Intent intent=new Intent( LoginActivity.this,HomeActivity.class );
                        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( intent );
                    }
                }
            } );
        }
    }

    private void showError(EditText input, String s) {

        input.setError( s );
        input.requestFocus();
    }


}
