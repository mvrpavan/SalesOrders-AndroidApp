package com.ceren.salesorders.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ceren.salesorders.R;
import com.ceren.salesorders.models.TransactionDetails;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryAdapter extends BaseExpandableListAdapter {

    private Activity mActivity;
    private List<TransactionDetails> mListTransactionDetails;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("hi", "IN"));

    public TransactionHistoryAdapter(Activity activity, List<TransactionDetails> listTransactionDetails) {
        this.mActivity = activity;
        this.mListTransactionDetails = listTransactionDetails;
    }

    @Override
    public int getGroupCount() {
        return mListTransactionDetails.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return mListTransactionDetails.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mListTransactionDetails.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpandable, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mActivity.getLayoutInflater().inflate(R.layout.transaction_list_item_parent, viewGroup, false);

        Log.d("TransHistoryAdapter", "getView(): View is null:" + (view == null) + " pos:" + position);
        TransactionDetails transactionDetails = mListTransactionDetails.get(position);

        NumberFormat format = currencyFormat;

        TextView tvDate = view.findViewById(R.id.trans_history_listItem_Date);
        tvDate.setText(transactionDetails.getCreateTime());

        TextView tvBillNumber = view.findViewById(R.id.trans_history_listItem_BillNumber);
        tvBillNumber.setText(transactionDetails.getBillNumber());

        TextView tvBillAmount = view.findViewById(R.id.trans_history_listItem_Bill_Amount);
        tvBillAmount.setText(format.format(transactionDetails.getNetSaleAmount()));

        Log.d("TransHistoryAdapter", "getView(): return view");
        return view;
    }

    @Override
    public View getChildView(int position, int childPos, boolean isExpandable, View view, ViewGroup viewGroup) {
        if (view == null)
            view = mActivity.getLayoutInflater().inflate(R.layout.transaction_list_item_child, viewGroup, false);

        Log.d("TransHistoryAdapter", "getChildView(): View is null:" + (view == null) + " pos:" + position);
        TransactionDetails transactionDetails = mListTransactionDetails.get(position);

        NumberFormat format = currencyFormat;

        /*
        TextView textViewSaleAmount = view.findViewById(R.id.trans_listItem_child_saleAmount);
        textViewSaleAmount.setText(format.format(transactionDetails.getSaleAmount()));

        TextView textViewCancelAmount = view.findViewById(R.id.trans_listItem_child_cancelAmount);
        textViewCancelAmount.setText(format.format(transactionDetails.getCancelAmount()));

        TextView textViewReturnAmount= view.findViewById(R.id.trans_listItem_child_returnAmount);
        textViewReturnAmount.setText(format.format(transactionDetails.getReturnAmount()));

        TextView textViewTotalTaxAmount = view.findViewById(R.id.trans_listItem_child_totalTaxAmount);
        textViewTotalTaxAmount.setText(format.format(transactionDetails.getTotalTaxAmount()));

        TextView textViewOBAmount = view.findViewById(R.id.trans_listItem_child_oldBalanceAmount);
        textViewOBAmount.setText(format.format(transactionDetails.getOBAmount()));
        */

        TextView textViewNetSaleAmount = view.findViewById(R.id.trans_listItem_child_saleAmount);
        textViewNetSaleAmount.setText(format.format(transactionDetails.getSaleAmount()));

        TextView textViewDiscountAmount = view.findViewById(R.id.trans_listItem_child_discountAmount);
        textViewDiscountAmount.setText(format.format(transactionDetails.getDiscountAmount()));

        TextView textViewCashPaidAmount = view.findViewById(R.id.trans_listItem_child_cashPaidAmount);
        textViewCashPaidAmount.setText(format.format(transactionDetails.getCashPaidAmount()));

        TextView textViewBalanceAmount = view.findViewById(R.id.trans_listItem_child_balanceAmount);
        textViewBalanceAmount.setText(format.format(transactionDetails.getBalanceAmount()));

        Log.d("TransHistoryAdapter", "getChildView(): return view");
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
