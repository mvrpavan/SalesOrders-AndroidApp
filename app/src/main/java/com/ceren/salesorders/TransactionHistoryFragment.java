package com.ceren.salesorders;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ceren.salesorders.handlers.FirebaseDBHandler;
import com.ceren.salesorders.models.TransactionDetails;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionHistoryFragment extends Fragment {

    //ExpandableListView trans_history_expandableListView;
    TableLayout tableLayoutHeader, tableLayoutRows;
    EditText editTextFromDate, editTextToDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
    private View mTransHistoryFormView, mProgressView;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("hi", "IN"));

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TransactionHistoryFragment.
     */
    public static TransactionHistoryFragment newInstance() {
        return new TransactionHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        editTextFromDate = view.findViewById(R.id.trans_history_fromDate);
        editTextToDate = view.findViewById(R.id.trans_history_toDate);
        //trans_history_expandableListView = view.findViewById(R.id.trans_history_expandableListView);
        mTransHistoryFormView = view.findViewById(R.id.trans_history_form);
        mProgressView = view.findViewById(R.id.tans_history_progress);
        tableLayoutHeader = view.findViewById(R.id.trans_history_table_header);
        tableLayoutRows = view.findViewById(R.id.trans_history_table_rows);

//        editTextFromDate.setText("22 May 2017");
//        editTextToDate.setText("22 May 2018");

        View.OnClickListener datePickerListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar c = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Log.d("TransHistFrag.OnDateSet", String.format("%d, %d, %d", year, month, dayOfMonth));
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (view.getId() == R.id.trans_history_fromDate) {
                            editTextFromDate.setText(dateFormat2.format(cal.getTime()));
                        } else {
                            editTextToDate.setText(dateFormat2.format(cal.getTime()));
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        };

        editTextFromDate.setOnClickListener(datePickerListener);
        editTextToDate.setOnClickListener(datePickerListener);

        Button applyButton = view.findViewById(R.id.trans_history_button_applyDateRange);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (TextUtils.isEmpty(editTextFromDate.getText()))
                {
                    Toast.makeText(view.getContext(), "From Date cannot be empty",
                            Toast.LENGTH_SHORT).show();

                    editTextFromDate.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(editTextToDate.getText()))
                {
                    Toast.makeText(view.getContext(), "To Date cannot be empty",
                            Toast.LENGTH_SHORT).show();

                    editTextToDate.requestFocus();
                    return;
                }

                Date fromDate = null, toDate;
                try {
                    fromDate = dateFormat2.parse(editTextFromDate.getText().toString());
                    toDate = dateFormat2.parse(editTextToDate.getText().toString());

                    if (fromDate.after(toDate)) {
                        Toast.makeText(view.getContext(), "From Date must be same or before To Date",
                                Toast.LENGTH_SHORT).show();

                        editTextToDate.requestFocus();
                    } else {
                        showProgress(true);
                        final FirebaseDBHandler fb = FirebaseDBHandler.getInstance(FirebaseAuth.getInstance().getUid());
                        if (fromDate.before(dateFormat.parse(fb.getCurrentUserData().getFirstTransactionDate()))) {
                            fromDate = dateFormat.parse(fb.getCurrentUserData().getFirstTransactionDate());
                        }
                        if (toDate.after(dateFormat.parse(fb.getCurrentUserData().getLastTransactionDate()))) {
                            toDate = dateFormat.parse(fb.getCurrentUserData().getLastTransactionDate());
                        }
                        fb.LoadTransactionsDataForDateRange(fromDate, toDate, new Runnable() {
                            @Override
                            public void run() {
                            List<TransactionDetails> ListTransactionDetails = fb.GetListTransactionDetails();
/*
                                TransactionHistoryAdapter transactionHistoryAdapter = new TransactionHistoryAdapter(getActivity(), ListTransactionDetails);
                                trans_history_expandableListView.setAdapter(transactionHistoryAdapter);
*/
                                LoadTransactionHistoryTable(ListTransactionDetails);
                                showProgress(false);

                                if (ListTransactionDetails.size() == 0) {
                                    Toast.makeText(view.getContext(), "No records found",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ParseException e) {
                    if (fromDate == null) {
                        Toast.makeText(view.getContext(), "From Date is invalid",
                                Toast.LENGTH_SHORT).show();

                        editTextFromDate.requestFocus();
                    }
                    else {
                        Toast.makeText(view.getContext(), "To Date is invalid",
                                Toast.LENGTH_SHORT).show();

                        editTextToDate.requestFocus();
                    }
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void LoadTransactionHistoryTable(List<TransactionDetails> ListTransactionDetails) {
        addHeaders();
        addData(ListTransactionDetails);
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor, int width) {
        TextView tv = new TextView(getContext());
        tv.setId(id);
        tv.setText(title);
        tv.setTextColor(color);
        tv.setPadding(0, 20, 0, 20);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setLayoutParams(getLayoutParams());
        tv.setWidth(width);
        tv.setBackgroundColor(bgColor);
        return tv;
    }

    @NonNull
    private TableRow.LayoutParams getLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableRow.LayoutParams getTableRowLayoutParams() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * This function add the headers to the table
     **/
    private void addHeaders() {
        TableLayout tl = tableLayoutHeader;
        tl.removeAllViews();
        //tl.setStretchAllColumns(true);
        int bgColor = ContextCompat.getColor(getContext(), R.color.colorGray), textColor = Color.WHITE;

        TableRow tr = new TableRow(getContext());
        tr.setLayoutParams(getTableRowLayoutParams());
        tr.addView(getTextView(0, "Bill Date", textColor, Typeface.BOLD, bgColor, 250));
        tr.addView(getTextView(1, "Bill No.", textColor, Typeface.BOLD, bgColor, 200));
        tr.addView(getTextView(2, "Sale Amount", textColor, Typeface.BOLD, bgColor, 350));
        tr.addView(getTextView(3, "Discount", textColor, Typeface.BOLD, bgColor, 250));
        tr.addView(getTextView(4, "Cash Paid", textColor, Typeface.BOLD, bgColor, 350));
        tr.addView(getTextView(5, "Balance Amount", textColor, Typeface.BOLD, bgColor, 350));
        tl.addView(tr, getTblLayoutParams());
    }

    /**
     * This function add the data to the table
     **/
    private void addData(List<TransactionDetails> ListTransactionDetails) {
        int numTransactions = ListTransactionDetails.size();
        TableLayout tl = tableLayoutRows;
        tl.removeAllViews();
        //tl.setStretchAllColumns(true);
        int bgColor = ContextCompat.getColor(getContext(), R.color.colorLightGray), textColor = Color.WHITE;
        int numColumns = 6;
        for (int i = 0; i < numTransactions; i++) {
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(getTableRowLayoutParams());

            String CreateDate = null;
            try {
                CreateDate = dateFormat2.format(dateFormat.parse(ListTransactionDetails.get(i).getCreateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String BillNum = ListTransactionDetails.get(i).getBillNumber();
            String Sale = currencyFormat.format(ListTransactionDetails.get(i).getSaleAmount());
            String Discount = currencyFormat.format(ListTransactionDetails.get(i).getDiscountAmount());
            String Cash = currencyFormat.format(ListTransactionDetails.get(i).getCashPaidAmount());
            String Balance = currencyFormat.format(ListTransactionDetails.get(i).getBalanceAmount());

            tr.addView(getTextView((i + 1) * numColumns, CreateDate, textColor, Typeface.NORMAL, bgColor, 250));
            tr.addView(getTextView(((i + 1) * numColumns) + 1, BillNum, textColor, Typeface.NORMAL, bgColor, 200));
            tr.addView(getTextView(((i + 1) * numColumns) + 2, Sale, textColor, Typeface.NORMAL, bgColor, 350));
            tr.addView(getTextView(((i + 1) * numColumns) + 3, Discount, textColor, Typeface.NORMAL, bgColor, 250));
            tr.addView(getTextView(((i + 1) * numColumns) + 4, Cash, textColor, Typeface.NORMAL, bgColor, 350));
            tr.addView(getTextView(((i + 1) * numColumns) + 5, Balance, textColor, Typeface.NORMAL, bgColor, 350));
            tl.addView(tr, getTblLayoutParams());
        }
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mTransHistoryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mTransHistoryFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTransHistoryFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
