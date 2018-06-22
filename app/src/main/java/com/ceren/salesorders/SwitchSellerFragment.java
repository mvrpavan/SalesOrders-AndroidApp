package com.ceren.salesorders;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ceren.salesorders.handlers.FirebaseDBHandler;
import com.ceren.salesorders.models.UserData;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SwitchSellerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SwitchSellerFragment extends Fragment {
    private static FirebaseUser mUser;
    private Spinner switch_seller_spinner;
    private Button btnSwitch;
    private View mProgressView;
    private View mSwitchSellerFormView;
    private static UserData LoggedInUserData;

    public SwitchSellerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment SwitchSellerFragment.
     */
    public static SwitchSellerFragment newInstance(FirebaseUser user) {
        SwitchSellerFragment fragment = new SwitchSellerFragment();
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
        final View view = inflater.inflate(R.layout.fragment_switch_seller, container, false);

        switch_seller_spinner = view.findViewById(R.id.switch_seller_spinner);
        btnSwitch = view.findViewById(R.id.switch_seller_btnSwitch);
        mSwitchSellerFormView = view.findViewById(R.id.switch_seller_form);
        mProgressView = view.findViewById(R.id.switch_seller_progress);

        if (MainActivity.UID.equals(mUser.getUid())) {
            FirebaseDBHandler fb = FirebaseDBHandler.getInstance(mUser.getUid());
            LoggedInUserData = fb.getCurrentUserData();
        }

        showProgress(true);
        if (LoggedInUserData == null) {
            final FirebaseDBHandler fb = FirebaseDBHandler.getInstance(mUser.getUid());
            fb.LoadCurrentUserData(new Runnable() {
                @Override
                public void run() {
                    LoggedInUserData = fb.getCurrentUserData();
                    if (LoggedInUserData == null) {
                        Toast.makeText(getContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PopulateLinkedSellersSpinner(LoggedInUserData);
                }
            });
        }
        else {
            PopulateLinkedSellersSpinner(LoggedInUserData);
        }

        switch_seller_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                btnSwitch.setEnabled(true);
                if (MainActivity.UID.equals(LoggedInUserData.getLinkedSellerUIDs().get(switch_seller_spinner.getSelectedItemPosition()))) {
                    btnSwitch.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.UID = LoggedInUserData.getLinkedSellerUIDs().get(switch_seller_spinner.getSelectedItemPosition());

                getActivity().finish();
                Intent MainIntent = new Intent(getContext(), MainActivity.class);
                startActivity(MainIntent);
            }
        });

        return view;
    }

    private void PopulateLinkedSellersSpinner(UserData userData) {
        btnSwitch.setEnabled(true);
        if (userData.getLinkedSellerUIDs() == null || userData.getLinkedSellerUIDs().size() == 0) {
            btnSwitch.setEnabled(false);
            showProgress(false);
            Toast.makeText(getContext(), "No other Sellers are linked to this account", Toast.LENGTH_LONG).show();
            return;
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_dropdown_item, userData.getLinkedSellerNames());
        switch_seller_spinner.setAdapter(arrayAdapter);
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the switch_seller form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSwitchSellerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mSwitchSellerFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSwitchSellerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
