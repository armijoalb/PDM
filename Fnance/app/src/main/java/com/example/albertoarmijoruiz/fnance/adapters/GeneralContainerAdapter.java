package com.example.albertoarmijoruiz.fnance.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.models.GeneralContainerElement;
import com.example.albertoarmijoruiz.fnance.views.MoreContentActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralContainerAdapter extends RecyclerView.Adapter<GeneralContainerAdapter.GeneralContainerViewHolder> {
    private ArrayList<GeneralContainerElement> elements;
    private Context mContext;

    public static final String TOTAL_CANTIDAD = "TOTAL";
    public static final String SUPERMERCADOS = "SUPERMERCADOS";
    public static final String PAGOS = "PAGOS";
    public static final String OTROS = "OTROS";
    public static final String OCIO = "OCIO";
    public static final String ROPA = "ROPA";

    public GeneralContainerAdapter(ArrayList<GeneralContainerElement> elements,
                                   Context context){
        this.elements = elements;
        this.mContext = context;
    }

    @NonNull
    @Override
    public GeneralContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.general_container,parent,false);

        return new GeneralContainerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralContainerViewHolder holder, final int position) {
        holder.cantidad.setText(elements.get(position).getTotal());
        holder.time.setText(elements.get(position).getTimeStamp());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,MoreContentActivity.class);
                HashMap<String,Float> aux = elements.get(position).getConcept_cantidad();

                if(aux.containsKey("Supermercados")){
                    intent.putExtra(SUPERMERCADOS,aux.get("Supermercados"));
                }else{
                    intent.putExtra(SUPERMERCADOS,0.0);
                }

                if(aux.containsKey("Otros")) {
                    intent.putExtra(OTROS,aux.get("Otros"));
                }else{
                    intent.putExtra(OTROS,0.0);
                }

                if(aux.containsKey("Ocio")) {
                    intent.putExtra(OCIO,aux.get("Ocio"));
                }else{
                    intent.putExtra(OCIO,0.0);
                }

                if(aux.containsKey("Ropa y complementos")) {
                    intent.putExtra(ROPA,aux.get("Ropa y complementos"));
                }else{
                    intent.putExtra(ROPA,0.0);
                }

                if(aux.containsKey("Pagos mensuales")) {
                    intent.putExtra(PAGOS,aux.get("Pagos mensuales"));
                }else{
                    intent.putExtra(PAGOS,0.0);
                }

                intent.putExtra(TOTAL_CANTIDAD,elements.get(position).getTotal());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class GeneralContainerViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView time,cantidad;
        public GeneralContainerViewHolder(View v){
            super(v);
            cardView = v.findViewById(R.id.element_card);
            time = v.findViewById(R.id.timeText);
            cantidad = v.findViewById(R.id.cantidad_general);
        }
    }
}
