package com.ceren.salesorders.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ceren.salesorders.R;
import com.ceren.salesorders.models.TransactionDetails;

import java.util.List;

public class TransactionDetailsAdapter extends ArrayAdapter<TransactionDetails> {

    private Activity mActivity;
    private List<TransactionDetails> mListTransactionDetails;

    public TransactionDetailsAdapter(Activity activity, List<TransactionDetails> ListTransactions) {
        super(activity.getApplicationContext(), R.layout.transaction_list_item_parent, ListTransactions);
        mActivity = activity;
        mListTransactionDetails = ListTransactions;
        Log.d("TransDetailsAdapter", "mListTransactionDetails count: " + mListTransactionDetails.size());
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null)
            view = mActivity.getLayoutInflater().inflate(R.layout.transaction_list_item_parent, parent, false);

        Log.d("TransDetailsAdapter", "getView(): View is null:" + (view == null) + " pos:" + position);
        TransactionDetails transactionDetails = mListTransactionDetails.get(position);

        TextView tvDate = view.findViewById(R.id.trans_history_listItem_Date);
        tvDate.setText(transactionDetails.getCreateTime());

        TextView tvBillNumber = view.findViewById(R.id.trans_history_listItem_BillNumber);
        tvBillNumber.setText(transactionDetails.getBillNumber());

        TextView tvBillAmount = view.findViewById(R.id.trans_history_listItem_Bill_Amount);
        tvBillAmount.setText(transactionDetails.getNetSaleAmount().toString());

        Log.d("TransDetailsAdapter", "getView(): return view");
        return view;
    }
}
