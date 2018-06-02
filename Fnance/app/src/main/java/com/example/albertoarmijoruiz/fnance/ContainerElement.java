package com.example.albertoarmijoruiz.fnance;

public class ContainerElement {

    public static final String TABLE_NAME = "elements";
    public static final String COLUMN_CANTIDAD = "cantidad";
    public static final String COLUMN_CONCEPTO = "concepto";
    public static final String COLUMN_DESC = "descripcion";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public static final String  CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CANTIDAD + " TEXT," +
            COLUMN_CONCEPTO + " TEXT, " +
            COLUMN_DESC + " TEXT," +
            COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP) ";


    private String concepto;
    private String cantidad;
    private String timeStamp;
    private String desc;
    private int id;

    public ContainerElement(String concept, String cuantity){
        concepto = concept;
        cantidad = cuantity;
    }

    public ContainerElement(String concept, String cuantity, int mId, String time, String descripcion){
        concepto = concept;
        cantidad = cuantity;
        id = mId;
        timeStamp = time;
        this.desc = descripcion;
    }

    public String getConcepto(){
        return concepto;
    }

    public String getCantidad(){
        return cantidad;
    }

    public int getId(){
        return id;
    }

    public String getDescription(){
        return desc;
    }

    public String getTimeStamp(){
        return timeStamp;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setCantidad(String ca){
        cantidad = ca;
    }

    public void setConcepto(String co){
        concepto = co;
    }

    public void setTimeStamp(String time){
        timeStamp = time;
    }

    public void setDesc(String description){
        desc = description;
    }

}
