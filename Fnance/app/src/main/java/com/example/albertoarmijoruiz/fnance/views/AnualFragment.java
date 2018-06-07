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
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnualFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private GeneralContainerAdapter adapter;
    private DbHelper helper;
    private ArrayList<GeneralContainerElement> elements = new ArrayList<>();
    private HashMap<String,HashMap<String,Float>> ordered = new HashMap<>();


    public AnualFragment() {
        // Required empty public constructor
    }

    public static AnualFragment newInstance(){
        AnualFragment fragment = new AnualFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_anual, container, false);

        helper = new DbHelper(getContext());

        // Inflate the layout for this fragment
        // Obtenemos vistas.
        recyclerView = v.findViewById(R.id.diario_recycler);
        recyclerView.setHasFixedSize(true);
        getAnualElements();
        adapter = new GeneralContainerAdapter(elements,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));

        return v;
    }

    private void getAnualElements(){
        ordered = new HashMap<>();
        ArrayList<ContainerElement> elem = helper.getAllElements();
        String concepto;
        Float cantidad,aux;
        String time;

        for(ContainerElement element : elem){
            time  = formatDate(element.getTimeStamp());
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
            elements.add(new GeneralContainerElement(entry.getValue(),entry.getKey()));
        }

    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

}
