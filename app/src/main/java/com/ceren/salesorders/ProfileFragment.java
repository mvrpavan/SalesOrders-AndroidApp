package com.ceren.salesorders;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ceren.salesorders.handlers.FirebaseDBHandler;
import com.ceren.salesorders.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static FirebaseUser mUser;
    private View mProfileFormView, mProgressView;
    private TextView textViewSellerName, textViewAddress, textViewContactPerson, textViewContactNumber;
    private EditText editTextDisplayName, editTextOldPassword, editTextNewPassword, editTextRetypeNewPassword;
    private Button buttonUpdateProfile, buttonChangePassword;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(FirebaseUser user) {
        ProfileFragment fragment = new ProfileFragment();
        mUser = user;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProfileFormView = view.findViewById(R.id.profile_form);
        mProgressView = view.findViewById(R.id.profile_progress);

        textViewSellerName = view.findViewById(R.id.profile_textView_seller_name);
        textViewAddress = view.findViewById(R.id.profile_textView_address);
        textViewContactPerson = view.findViewById(R.id.profile_textView_contact_person);
        textViewContactNumber = view.findViewById(R.id.profile_textView_contact_number);

        editTextDisplayName = view.findViewById(R.id.profile_editText_name);
        editTextOldPassword = view.findViewById(R.id.profile_editText_OldPassword);
        editTextNewPassword = view.findViewById(R.id.profile_editText_newPassword);
        editTextRetypeNewPassword = view.findViewById(R.id.profile_editText_RetypeNewPassword);

        buttonUpdateProfile = view.findViewById(R.id.profile_button_update_profile);
        buttonChangePassword = view.findViewById(R.id.profile_button_change_password);

        final FirebaseDBHandler fb = FirebaseDBHandler.getInstance(MainActivity.UID);
        UserData currentUserData = fb.getCurrentUserData();

        if (currentUserData == null) {
            showProgress(true);
            fb.LoadCurrentUserData(new Runnable() {
                @Override
                public void run() {
                    UserData currentUserData = fb.getCurrentUserData();
                    SetViewDataFromCurrentUserData(currentUserData);
                    showProgress(false);
                }
            });
        }
        else {
            SetViewDataFromCurrentUserData(currentUserData);
        }

        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset errors.
                editTextDisplayName.setError(null);

                String DisplayName = editTextDisplayName.getText().toString();
                if (!TextUtils.isEmpty(DisplayName)) {
                    if (mUser != null) {
                        fb.UpdateDisplayName(DisplayName, new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), R.string.profile_update_succeeded,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    editTextDisplayName.setError("Field cannot be empty");
                    editTextDisplayName.requestFocus();
                }
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset errors.
                editTextOldPassword.setError(null);
                editTextNewPassword.setError(null);
                editTextRetypeNewPassword.setError(null);

                if (TextUtils.isEmpty(editTextOldPassword.getText().toString())) {
                    editTextOldPassword.setError("Field cannot be empty");
                    editTextOldPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(editTextNewPassword.getText().toString())) {
                    editTextNewPassword.setError("Field cannot be empty");
                    editTextNewPassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(editTextRetypeNewPassword.getText().toString())) {
                    editTextRetypeNewPassword.setError("Field cannot be empty");
                    editTextRetypeNewPassword.requestFocus();
                    return;
                }
                if (!editTextRetypeNewPassword.getText().toString().equals(editTextNewPassword.getText().toString())) {
                    editTextRetypeNewPassword.setError("Both New passwords are not same");
                    editTextRetypeNewPassword.requestFocus();
                    return;
                }
                if (editTextOldPassword.getText().toString().equals(editTextNewPassword.getText().toString())) {
                    editTextNewPassword.setError("Old & New password cannot be same");
                    editTextNewPassword.requestFocus();
                    return;
                }

                //ReAuthenticate User
                if (mUser != null) {
                    showProgress(true);
                    AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), editTextOldPassword.getText().toString());
                    mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mUser.updatePassword(editTextNewPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(view.getContext(), "Password changed, please login using new password.",
                                                    Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            showProgress(false);
                                            LoadLoginActivity(view);
                                        }
                                        else {
                                            Toast.makeText(view.getContext(), "Something went wrong, please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else {
                                showProgress(false);
                                Toast.makeText(view.getContext(), "Incorrect Old Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }

    private void SetViewDataFromCurrentUserData(UserData currentUserData) {
        textViewSellerName.setText(currentUserData.getSellerName());
        textViewAddress.setText(currentUserData.getAddress());
        textViewContactPerson.setText(currentUserData.getContactPerson());
        textViewContactNumber.setText(currentUserData.getContactNumber());
        editTextDisplayName.setText(currentUserData.getDisplayName());

        if (!MainActivity.UID.equals(mUser.getUid())) {
            editTextDisplayName.setEnabled(false);
            editTextOldPassword.setEnabled(false);
            editTextNewPassword.setEnabled(false);
            editTextRetypeNewPassword.setEnabled(false);
            buttonUpdateProfile.setEnabled(false);
            buttonChangePassword.setEnabled(false);
        }
    }

    void LoadLoginActivity(View view) {
        this.getActivity().finish();
        Intent intent = new Intent(view.getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mProfileFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
}
