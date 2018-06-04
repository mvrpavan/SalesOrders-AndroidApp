package com.ceren.salesorders;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class TableViewActivity extends AppCompatActivity {
    String companies[] = {"Google", "Windows", "iPhone", "Nokia", "Samsung",
            "Google", "Windows", "iPhone", "Nokia", "Samsung",
            "Google", "Windows", "iPhone", "Nokia", "Samsung"};

    String os[] = {"Android", "Mango", "iOS", "Symbian", "Bada",
            "Android", "Mango", "iOS", "Symbian", "Bada",
            "Android", "Mango", "iOS", "Symbian", "Bada"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);
        addHeaders();
        addData();
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(40, 40, 40, 40);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setLayoutParams(getLayoutParams());
        //int screenWidth = getResources().getDisplayMetrics().widthPixels;
        //tv.setWidth(50 * screenWidth / 100);
        //tv.setHeight(60);
        tv.setBackgroundColor(bgColor);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                TextView tv = findViewById(id);
                if (null != tv) {
                    Log.i("onClick", "Clicked on row :: " + id);
                    Toast.makeText(TableViewActivity.this, "Clicked on row :: " + id + ", Text :: " + tv.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * This function add the headers to the table
     **/
    public void addHeaders() {
        TableLayout tl = findViewById(R.id.tableLayout2);
        tl.setStretchAllColumns(true);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "COMPANY", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(1, "COMPANY1", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(2, "COMPANY2", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(3, "COMPANY3", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(4, "COMPANY4", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(5, "OS5", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(6, "OS6", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tl.addView(tr, getTblLayoutParams());
    }

    /**
     * This function add the data to the table
     **/
    public void addData() {
        int numCompanies = companies.length;
        TableLayout tl = findViewById(R.id.tableLayout3);
        for (int i = 0; i < numCompanies; i++) {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(getLayoutParams());
            tr.addView(getTextView(((i + 1) * numCompanies) + 1, companies[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 2, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 3, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 4, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 5, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 6, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tr.addView(getTextView(((i + 1) * numCompanies) + 7, os[i], Color.WHITE, Typeface.NORMAL, ContextCompat.getColor(this, R.color.colorAccent)));
            tl.addView(tr, getTblLayoutParams());
        }
    }
}
