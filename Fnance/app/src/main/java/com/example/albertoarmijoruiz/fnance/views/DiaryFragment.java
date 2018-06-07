package com.example.albertoarmijoruiz.fnance.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.albertoarmijoruiz.fnance.R;
import com.example.albertoarmijoruiz.fnance.adapters.GeneralContainerAdapter;
import com.example.albertoarmijoruiz.fnance.helpers.DbHelper;
import com.example.albertoarmijoruiz.fnance.models.ContainerElement;
import com.example.albertoarmijoruiz.fnance.models.GeneralContainerElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private GeneralContainerAdapter adapter;
    private DbHelper helper;
    private ArrayList<GeneralContainerElement> elements = new ArrayList<>();
    private HashMap<String,HashMap<String,Float>> ordered = new HashMap<>();

    public DiaryFragment() {
        // Required empty public constructor
    }

    public static DiaryFragment newInstance() {
        DiaryFragment fragment = new DiaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_diary, container, false);

        helper = new DbHelper(getContext());

        // Obtenemos vistas.
        recyclerView = v.findViewById(R.id.diario_recycler);
        recyclerView.setHasFixedSize(true);
        // Establecemos valor para el array elements.
        getDiaryElements();
        adapter = new GeneralContainerAdapter(elements,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));

        return v;
    }

    private void getDiaryElements(){
        ordered = new HashMap<>();
        ArrayList<ContainerElement> elem = helper.getAllElements();
        String concepto;
        Float cantidad,aux;
        String time;

        for(ContainerElement element : elem){
            time  = element.getTimeStamp();
            concepto = element.getConcepto();
            cantidad = Float.parseFloat(element.getCantidad());

            // Comprobamos si ya hay una entrada con el mismo día.
            if(ordered.containsKey(time)){
                // Comprobamos si ya hay un elemento del hashmap con el mismo concepto.
                if(ordered.get(time).containsKey(concepto)){
                    aux = ordered.get(time).get(concepto);
                    // Añadimos la cantidad a la cantidad actual.
                    ordered.get(time).put(concepto,aux+cantidad);
                }else{
                    // Añadimos un nuevo tipo de concepto.
                    ordered.get(time).put(concepto,cantidad);
                }

            }else{
                // Creamos un nuevo hash_map.
                HashMap<String,Float> aux_hash = new HashMap<>();
                aux_hash.put(concepto,cantidad);

                // Introducimos el nuevo hashmap con la fecha correspondiente.
                ordered.put(time,aux_hash);
            }
        }

        // Una vez hemos obtenido los elementos ordenados, los introducimos como elementos
        // del tipo GeneralContainerElement.
        elements = new ArrayList<>();
        for(Map.Entry<String,HashMap<String,Float>> entry:ordered.entrySet()){
            elements.add(new GeneralContainerElement(entry.getValue(),formatDate(entry.getKey())));
        }

    }


    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("d MMM yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }






}
