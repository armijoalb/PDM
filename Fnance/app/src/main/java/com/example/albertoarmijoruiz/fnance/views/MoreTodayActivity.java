package com.example.albertoarmijoruiz.fnance.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.ContainerAdaper;

public class MoreTodayActivity extends AppCompatActivity {

    private TextView description, cuantity, concept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_today);
        Bundle extras = getIntent().getExtras();
        description = findViewById(R.id.real_desciption);
        cuantity = findViewById(R.id.real_cantidad);
        concept = findViewById(R.id.real_concept);

        description.setText(extras.getString(ContainerAdaper.DESC));
        cuantity.setText(extras.getString(ContainerAdaper.CANTIDAD));
        concept.setText(extras.getString(ContainerAdaper.CONCETO));
    }
}
