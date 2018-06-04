package com.ceren.salesorders;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableViewFragment extends Fragment {
    String companies[] = {"Google", "Windows", "iPhone", "Nokia", "Samsung",
            "Google", "Windows", "iPhone", "Nokia", "Samsung",
            "Google", "Windows", "iPhone", "Nokia", "Samsung"};
    String os[] = {"Android", "Mango", "iOS", "Symbian", "Bada",
            "Android", "Mango", "iOS", "Symbian", "Bada",
            "Android", "Mango", "iOS", "Symbian", "Bada"};

    public TableViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TableViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TableViewFragment newInstance() {
        TableViewFragment fragment = new TableViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_table_view, container, false);
        addHeaders(view);
        addData(view);
        return view;
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(getContext());
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(40, 40, 40, 40);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                TextView tv = view.findViewById(id);
                if (null != tv) {
                    Log.i("onClick", "Clicked on row :: " + id);
                    Toast.makeText(getActivity(), "Clicked on row :: " + id + ", Text :: " + tv.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return tv;
    }

    @NonNull
    private TableLayout.LayoutParams getLayoutParams() {
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        //params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * This function add the headers to the table
     **/
    public void addHeaders(View view) {
        Log.d("TableViewFragment", "addHeaders() invoked");
        TableLayout tl = view.findViewById(R.id.tableLayout);
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "COMPANY", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(0, "OS", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tl.addView(tr, getTblLayoutParams());
    }

    /**
     * This function add the data to the table
     **/
    public void addData(View view) {
        int numCompanies = companies.length;
        TableLayout tl = view.findViewById(R.id.tableLayout);
        Log.d("TableViewFragment", "addData() invoked");
        for (int i = 0; i < numCompanies; i++) {
            Log.d("TableViewFragment", "addData()" + (i));
            TableRow tr = new TableRow(getContext());
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(i + 1, companies[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(getContext(), R.color.colorAccent)));
            tr.addView(getTextView(i + numCompanies, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(getContext(), R.color.colorAccent)));
            tl.addView(tr, getTblLayoutParams());
        }
    }
}
