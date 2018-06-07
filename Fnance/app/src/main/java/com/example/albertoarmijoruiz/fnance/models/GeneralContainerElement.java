package com.example.albertoarmijoruiz.fnance.models;

import com.example.albertoarmijoruiz.fnance.adapters.GeneralContainerAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GeneralContainerElement {

    private HashMap<String,Float> concept_cantidad;
    private String timeStamp;


    public GeneralContainerElement(HashMap<String,Float> cc,String time){
        this.concept_cantidad = cc;
        this.timeStamp = time;
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public HashMap<String, Float> getConcept_cantidad() {
        return concept_cantidad;
    }

    public String getTotal(){
        float total = 0;
        for(Map.Entry<String,Float> entry : concept_cantidad.entrySet()){
            total += entry.getValue();
        }

        return ""+total;
    }

    public ArrayList<Float> getCantidades() {
        ArrayList<Float> cantidades = new ArrayList<>();

        for(Map.Entry<String,Float> elem : concept_cantidad.entrySet()){
            cantidades.add(elem.getValue());
        }

        return cantidades;
    }

    public ArrayList<String> getConceptos() {
        ArrayList<String> conceptos = new ArrayList<>();

        for(Map.Entry<String,Float> elem : concept_cantidad.entrySet()){
            conceptos.add(elem.getKey());
        }

        return conceptos;
    }


    public String getTimeStamp(){
        return timeStamp;
    }


}
