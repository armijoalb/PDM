package com.example.albertoarmijoruiz.fnance.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.albertoarmijoruiz.fnance.models.ContainerElement;
import com.example.albertoarmijoruiz.fnance.helpers.DbHelper;
import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.views.EditActivity;
import com.example.albertoarmijoruiz.fnance.views.MoreTodayActivity;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ContainerAdaper extends RecyclerView.Adapter<ContainerAdaper.ContainerViewHolder> {

    private ArrayList<ContainerElement> elements = new ArrayList<>();
    private Context mContext;
    private DbHelper helper;
    private CharSequence[] opciones_long_pressed = new CharSequence[]{"Borrar"};

    public static final String CANTIDAD = "CANTIDAD";
    public static final String CONCETO = "CONCEPTO";
    public static final String DESC = "DESCRIPCION";
    public static final String TIME = "TIME";
    public static final String ID = "ID";

    public ContainerAdaper(ArrayList<ContainerElement> elem, Context context){
        this.elements = elem;
        this.mContext = context;
        helper = new DbHelper(context);
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.finance_container,parent,false);
        return new ContainerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, final int position) {
        holder.cantidadText.setText(elements.get(position).getCantidad());
        holder.conceptoText.setText(elements.get(position).getConcepto());
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setItems(opciones_long_pressed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG,"posicion elegida: "+position);
                        Log.i(TAG,"opcion: "+opciones_long_pressed[position]);

                        Log.i(TAG,"borrando elemento");
                        helper.deleteElement(elements.get(position));
                        elements.remove(position);
                        notifyDataSetChanged();
                    }
                });

                builder.create().show();
                return true;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreTodayActivity.class);
                intent.putExtra(CANTIDAD,elements.get(position).getCantidad());
                intent.putExtra(CONCETO,elements.get(position).getConcepto());
                intent.putExtra(DESC,elements.get(position).getDescription());
                intent.putExtra(ID,elements.get(position).getId());
                intent.putExtra(TIME,elements.get(position).getTimeStamp());
                mContext.startActivity(intent);
            }
        });
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
