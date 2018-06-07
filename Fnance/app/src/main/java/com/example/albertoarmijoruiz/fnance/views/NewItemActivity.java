package com.example.albertoarmijoruiz.fnance.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.helpers.DbHelper;

public class NewItemActivity extends AppCompatActivity {

    private EditText cant_edit, concept_edit, description;
    private Button add_new;
    private DbHelper mHelper;
    private String TAG = NewItemActivity.class.getSimpleName();
    private String[] listConceptos = new String[] {"Supermecados","Ocio","Ropa y complementos","Pagos mensuales","Otros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        mHelper = new DbHelper(this);

        cant_edit = findViewById(R.id.cantidad_param);
        concept_edit = findViewById(R.id.concepto_param);

        concept_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewItemActivity.this);
                mBuilder.setTitle("Elija un tipo");
                mBuilder.setSingleChoiceItems(listConceptos, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        concept_edit.setText(listConceptos[which]);
                        dialog.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        concept_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewItemActivity.this);
                    mBuilder.setTitle("Elija un tipo");
                    mBuilder.setSingleChoiceItems(listConceptos, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            concept_edit.setText(listConceptos[which]);
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            }
        });

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
