package com.example.albertoarmijoruiz.fnance.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.GeneralContainerAdapter;

public class MoreContentActivity extends AppCompatActivity {
    private TextView ropa,pagos,ocio,supermecados,otros;
    private TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_content);
        Bundle argu = getIntent().getExtras();
        Float ropa_c = argu.getFloat(GeneralContainerAdapter.ROPA);
        Float otros_c = argu.getFloat(GeneralContainerAdapter.OTROS);
        Float super_c = argu.getFloat(GeneralContainerAdapter.SUPERMERCADOS);
        Float ocio_c = argu.getFloat(GeneralContainerAdapter.OCIO);
        Float pagos_c = argu.getFloat(GeneralContainerAdapter.PAGOS);
        String getTotal = argu.getString(GeneralContainerAdapter.TOTAL_CANTIDAD);

        ropa = findViewById(R.id.ropa);
        otros = findViewById(R.id.otros);
        supermecados = findViewById(R.id.supermecados);
        ocio = findViewById(R.id.ocio);
        pagos = findViewById(R.id.pagos);
        total = findViewById(R.id.total);

        total.setText(getTotal);
        ropa.setText(ropa_c.toString());
        pagos.setText(pagos_c.toString());
        ocio.setText(ocio_c.toString());
        otros.setText(otros_c.toString());
        supermecados.setText(super_c.toString());

    }
}
