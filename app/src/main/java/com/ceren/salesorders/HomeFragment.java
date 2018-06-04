package com.ceren.salesorders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ceren.salesorders.adapters.TransactionHistoryAdapter;
import com.ceren.salesorders.handlers.FirebaseDBHandler;
import com.ceren.salesorders.models.TransactionDetails;
import com.ceren.salesorders.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static FirebaseUser mUser = null;
    TextView textView_Hello, textView_BalanceAmount, textView_LastLogin;
    FirebaseDBHandler firebaseDBHandler;
    ExpandableListView listViewRecentTransactions;
    private View mProgressView;
    private View mHomeFormView;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("hi", "IN"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user FirebaseUser.
     * @return A new instance of fragment Login.
     */
    public static HomeFragment newInstance(FirebaseUser user) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textView_Hello = view.findViewById(R.id.textView_Hello);
        listViewRecentTransactions = view.findViewById(R.id.listViewRecentTransactions);
        textView_BalanceAmount = view.findViewById(R.id.textView_Balance);
        textView_LastLogin = view.findViewById(R.id.textView_LastLogin);
        mHomeFormView = view.findViewById(R.id.home_form);
        mProgressView = view.findViewById(R.id.home_progress);
        final FloatingActionButton fabReload = getActivity().findViewById(R.id.fabReload);
        //Animations
        final Animation fabReloadRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_reload_rotate);

        fabReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /*Snackbar.make(view, "Reload Recent Transactions", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                fabReload.startAnimation(fabReloadRotate);
                fabReload.setClickable(false);

                showProgress(true);
                final FirebaseDBHandler firebaseDBHandler = FirebaseDBHandler.getInstance(mUser.getUid());
                firebaseDBHandler.LoadCurrentUserData(new Runnable() {
                    @Override
                    public void run() {
                        firebaseDBHandler.LoadRecentTransactionsData(new Runnable() {
                            @Override
                            public void run() {
                                List<TransactionDetails> ListTransactionDetails = firebaseDBHandler.GetListRecentTransactionDetails();

                                /*DatabaseHandler db = new DatabaseHandler(getContext(), mUser.getUid());
                                db.deleteAllTransactionDetails();
                                for (TransactionDetails transactionDetails : ListTransactionDetails) {
                                    db.addTransactionDetailsToRecent(transactionDetails);
                                }
                                db.close();*/
                                TransactionHistoryAdapter transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity(), ListTransactionDetails);
                                listViewRecentTransactions.setAdapter(transactionHistoryAdapter);
                                fabReload.clearAnimation();
                                fabReload.setClickable(true);
                                showProgress(false);
                                Snackbar.make(view, "Recent Transactions refreshed", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }
                });
            }
        });

        showProgress(true);
        firebaseDBHandler = FirebaseDBHandler.getInstance(mUser.getUid());
        UserData currentUserData = firebaseDBHandler.getCurrentUserData();
        if (currentUserData == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                getActivity().finish();
                Intent LoginIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(LoginIntent);
            }

            firebaseDBHandler.LoadCurrentUserData(new Runnable() {
                @Override
                public void run() {
                    UserData currentUserData = firebaseDBHandler.getCurrentUserData();

                    textView_Hello.setText(String.format("Welcome %s", currentUserData.getDisplayName()));
                    textView_BalanceAmount.setText(currencyFormat.format(currentUserData.getBalanceAmount()));
                    textView_LastLogin.setText(String.format("%s", currentUserData.getLastLoginDate()));

                    LoadRecentTransactionDetails();
                }
            });
        }
        else {
            textView_Hello.setText(String.format("Welcome %s", currentUserData.getDisplayName()));
            textView_BalanceAmount.setText(currencyFormat.format(currentUserData.getBalanceAmount()));
            textView_LastLogin.setText(String.format("%s", currentUserData.getLastLoginDate()));

            /*DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext(), mUser.getUid());
            List<TransactionDetails> ListTransactionDetails = db.getAllTransactions();
            TransactionHistoryAdapter transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity(), ListTransactionDetails);
            listViewRecentTransactions.setAdapter(transactionHistoryAdapter);*/
            LoadRecentTransactionDetails();
        }

        return view;
    }

    private void LoadRecentTransactionDetails() {
        /*DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext(), mUser.getUid());
        List<TransactionDetails> ListTransactionDetails = db.getAllTransactions();*/
        List<TransactionDetails> ListTransactionDetails = firebaseDBHandler.GetListRecentTransactionDetails();
        if (ListTransactionDetails == null) {
            firebaseDBHandler.LoadRecentTransactionsData(new Runnable() {
                @Override
                public void run() {
                    List<TransactionDetails> ListRecentTransactionDetails = firebaseDBHandler.GetListRecentTransactionDetails();
                    TransactionHistoryAdapter transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity(), ListRecentTransactionDetails);
                    listViewRecentTransactions.setAdapter(transactionHistoryAdapter);
                    showProgress(false);
                }
            });
        }
        else {
            TransactionHistoryAdapter transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity(), ListTransactionDetails);
            listViewRecentTransactions.setAdapter(transactionHistoryAdapter);
            showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the home form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mHomeFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
