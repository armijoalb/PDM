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
import android.widget.TextView;
import android.widget.Toast;

import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.ContainerAdaper;
import com.example.albertoarmijoruiz.fnance.helpers.DbHelper;
import com.example.albertoarmijoruiz.fnance.models.ContainerElement;

public class EditActivity extends AppCompatActivity {

    private EditText cantidad, concepto, descripcion;
    private Button updateButton;
    private String[] listConceptos = new String[] {"Supermecados","Ocio","Ropa y complementos","Pagos mensuales","Otros"};
    private DbHelper helper;
    private final String TAG = EditActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Bundle extras = getIntent().getExtras();

        helper = new DbHelper(this);

        cantidad = (EditText)findViewById(R.id.cantidad_param);
        concepto = (EditText)findViewById(R.id.concepto_param);
        descripcion = (EditText)findViewById(R.id.desc_param);
        updateButton = (Button)findViewById(R.id.add_new);

        final int id = extras.getInt(ContainerAdaper.ID);
        String can = extras.getString(ContainerAdaper.CANTIDAD);
        final String conc = extras.getString(ContainerAdaper.CONCETO);
        String desc = extras.getString(ContainerAdaper.DESC);
        final String time = extras.getString(ContainerAdaper.TIME);

        cantidad.setText(can, TextView.BufferType.EDITABLE);
        concepto.setText(conc,TextView.BufferType.EDITABLE);
        descripcion.setText(desc,TextView.BufferType.EDITABLE);

        concepto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditActivity.this);
                mBuilder.setTitle("Elija un tipo");
                mBuilder.setSingleChoiceItems(listConceptos, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        concepto.setText(listConceptos[which]);
                        dialog.dismiss();
                    }
                });
                mBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        concepto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditActivity.this);
                    mBuilder.setTitle("Elija un tipo");
                    mBuilder.setSingleChoiceItems(listConceptos, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            concepto.setText(listConceptos[which]);
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!concepto.getText().toString().isEmpty() && !cantidad.getText().toString().isEmpty()){
                    ContainerElement element = new ContainerElement(concepto.getText().toString(),
                            cantidad.getText().toString(),
                            id,
                            time,
                            descripcion.getText().toString());
                    helper.updateElement(element);
                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(),"Rellene al menos los campos de cantidad y concepto",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
