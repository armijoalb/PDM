package com.example.albertoarmijoruiz.fnance;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ContainerAdaper extends RecyclerView.Adapter<ContainerAdaper.ContainerViewHolder> {

    private ArrayList<ContainerElement> elements = new ArrayList<>();

    public ContainerAdaper(ArrayList<ContainerElement> elem){
        this.elements = elem;
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.finance_container,parent,false);
        return new ContainerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, int position) {
        holder.cantidadText.setText(elements.get(position).getCantidad());
        holder.conceptoText.setText(elements.get(position).getConcepto());
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class ContainerViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView cantidadText;
        private TextView conceptoText;

        public ContainerViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.elementView);
            cantidadText = itemView.findViewById(R.id.cantidad);
            conceptoText = itemView.findViewById(R.id.concepto);
        }
    }

}
