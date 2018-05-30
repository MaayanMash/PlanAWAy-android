package com.example.maayanmash.planaway;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private View mProgressView;
    private UserLoginTask mLoginTask = null;
    private UserRegisterTask mRegisterTask = null;

    public Context getSelfContext(){
        return getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.inputEmail =findViewById(R.id.login_email);
        this.inputPassword=findViewById(R.id.login_password);

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button button_login = findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        Button button_register=findViewById(R.id.button_Register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mLoginTask != null) {
            return;
        }
        inputEmail.setError(null);
        inputPassword.setError(null);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        boolean cancel = credentialsValidation(email, password);

        if (!cancel) {
            showProgress(true);
            Log.d("TAG", "user : " + email+"  " + password );
            mLoginTask = new UserLoginTask(email, password);
            mLoginTask.execute((Void) null);
        }

    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        inputEmail.setError(null);
        inputPassword.setError(null);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        boolean cancel = credentialsValidation(email, password);

        if (!cancel) {
            showProgress(true);
            mRegisterTask = new UserRegisterTask(email, password);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean credentialsValidation(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Required");
            return true;
        } else if (!isEmailValid(email)) {
            inputEmail.setError("invalid email");
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Required");
            return true;
        }else if(!isPasswordValid(password)) {
            inputPassword.setError("Password must be at least 6 characters");
            return true;
        }
        return false;
    }

    // email and password validtion
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        if (show) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mProgressView.setVisibility(View.GONE);
        }
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        showProgress(false);
                        Intent intent = new Intent(getSelfContext(), MapsActivity.class);
                        intent.putExtra("uid",task.getResult().getUser().getUid());
                        startActivity(intent);

                    } else {
                        showProgress(false);
                        Toast.makeText(getSelfContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLoginTask = null;
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }

    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;

        UserRegisterTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        showProgress(false);
                        Intent intent = new Intent(getSelfContext(), MapsActivity.class);
                        startActivity(intent);
                    } else {
                        showProgress(false);
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            Toast.makeText(getApplicationContext(), "Weak password", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "User already exist", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }
    }
}
