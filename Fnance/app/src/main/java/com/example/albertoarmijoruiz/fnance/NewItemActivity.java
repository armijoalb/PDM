package com.example.albertoarmijoruiz.fnance;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewItemActivity extends AppCompatActivity {

    private EditText cant_edit, concept_edit, description;
    private Button add_new;
    private DbHelper mHelper;
    private String TAG = NewItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        mHelper = new DbHelper(this);

        cant_edit = findViewById(R.id.cantidad_param);
        concept_edit = findViewById(R.id.concepto_param);
        description = findViewById(R.id.desc_param);
        add_new = findViewById(R.id.add_new);

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String can = cant_edit.getText().toString();
                String con = concept_edit.getText().toString();
                String desc = description.getText().toString();

                if(!can.isEmpty() && !con.isEmpty()){
                    // Añadir elemento a la base de datos.
                    mHelper.insertElement(can,con,desc);
                    Log.i(TAG,"Número de elementos"+mHelper.getNumberOfElements()+"");
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(),"Rellene los campos cantidad y concepto",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
