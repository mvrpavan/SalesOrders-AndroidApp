package com.ceren.salesorders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ceren.salesorders.handlers.FileHandler;
import com.ceren.salesorders.handlers.FirebaseDBHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG;
    private FileHandler mFileHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TAG = "LoginActivity";

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mEmailSignInButton.requestFocus();

        Button CancelButton = findViewById(R.id.login_button_cancel);
        CancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mFileHandler = new FileHandler(this);
        String Username = mFileHandler.getUserName();
        if (Username != null) {
            mEmailView.setText(Username);
            mPasswordView.requestFocus();
        }
        getSupportActionBar().hide();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (!isOnline(this)) {
            focusView = mEmailView;
            cancel = true;
            Toast.makeText(LoginActivity.this, "Please check Internet connection",
                    Toast.LENGTH_SHORT).show();
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            hideKeyboard(this);
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mFileHandler.setUserName(mEmailView.getText().toString());
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Login Successful",
                                        Toast.LENGTH_SHORT).show();

                                LoadRecentTransactionsDataToLocalDB(new Runnable() {
                                    @Override
                                    public void run() {
                                        showProgress(false);
                                        finish();
                                        Intent MainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(MainIntent);
                                    }
                                });

                            } else {
                                showProgress(false);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Username or Password does not match/exists.",
                                        Toast.LENGTH_SHORT).show();
                                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                                //mPasswordView.requestFocus();
                                mEmailSignInButton.requestFocus();
                            }
                        }
                    });

        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void LoadRecentTransactionsDataToLocalDB(final Runnable callbackFunc) {
        final String UID = mAuth.getCurrentUser().getUid();

        final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance(UID);
        firebaseDBHandler.LoadCurrentUserData(new Runnable() {
            @Override
            public void run() {
                //FirebaseDBHandler fb = FirebaseDBHandler.getInstance(mAuth.getUid());
                firebaseDBHandler.updateLastLoginDate(Calendar.getInstance().getTime());

                firebaseDBHandler.LoadRecentTransactionsData(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                        /*List<TransactionDetails> ListTransactionDetails = firebaseDBHandler.GetListTransactionDetails();

                        DatabaseHandler db = new DatabaseHandler(getApplicationContext(), UID);
                        db.deleteAllTransactionDetails();
                        for (TransactionDetails transactionDetails : ListTransactionDetails) {
                            db.addTransactionDetailsToRecent(transactionDetails);
                        }
                        db.close();*/

                        if (callbackFunc != null) callbackFunc.run();
                    }
                });
            }
        });
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

