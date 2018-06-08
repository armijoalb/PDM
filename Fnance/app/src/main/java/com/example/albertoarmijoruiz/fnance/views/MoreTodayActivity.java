package com.example.albertoarmijoruiz.fnance.views;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.ContainerAdaper;

public class MoreTodayActivity extends AppCompatActivity {

    private TextView description, cuantity, concept;
    private FloatingActionButton editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_today);
        final Bundle extras = getIntent().getExtras();
        description = findViewById(R.id.real_desciption);
        cuantity = findViewById(R.id.real_cantidad);
        concept = findViewById(R.id.real_concept);
        editButton = findViewById(R.id.editButton);

        description.setText(extras.getString(ContainerAdaper.DESC));
        cuantity.setText(extras.getString(ContainerAdaper.CANTIDAD));
        concept.setText(extras.getString(ContainerAdaper.CONCETO));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(getBaseContext(),EditActivity.class);
                editIntent.putExtras(extras);
                startActivity(editIntent);
            }
        });

    }
}
