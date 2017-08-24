package com.syshuman.kadir.haircolor3.view.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.syshuman.kadir.haircolor3.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.spTCompany) Spinner spTCompany;
    @BindView(R.id.spTCatalog) Spinner spTCatalog;
    @BindView(R.id.spTColor) Spinner spTColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainig);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getData();
    }

    public void getData() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.companies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTCompany.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.catalog, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTCatalog.setAdapter(adapter1);
        //adapter.clear();

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.color, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTColor.setAdapter(adapter2);
    }

}
