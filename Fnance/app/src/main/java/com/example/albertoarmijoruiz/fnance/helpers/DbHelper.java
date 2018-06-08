package com.example.albertoarmijoruiz.fnance.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.albertoarmijoruiz.fnance.models.ContainerElement;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FnanceDb";
    private static final String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ContainerElement.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContainerElement.TABLE_NAME);
        onCreate(db);
    }

    public long insertElement(String cantidad, String concepto, String descripcion){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ContainerElement.COLUMN_CANTIDAD,cantidad);
        values.put(ContainerElement.COLUMN_CONCEPTO,concepto);
        values.put(ContainerElement.COLUMN_DESC,descripcion);

        long id = db.insert(ContainerElement.TABLE_NAME,null, values);
        db.close();

        return id;
    }

    public ArrayList<ContainerElement> getTodayElements(){
        ArrayList<ContainerElement> todayElements = new ArrayList<>();

        // Coger solo los que coincidan con el día de hoy.
        String selectQuery = "SELECT * FROM " + ContainerElement.TABLE_NAME + " WHERE " +
                ContainerElement.COLUMN_TIMESTAMP + " = CURRENT_DATE";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        Log.i(TAG,"numero de elementos encontrados "+cursor.getCount());
        if(cursor.moveToFirst()){
            do{
                ContainerElement elem = new ContainerElement(
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_CONCEPTO)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_CANTIDAD)),
                        cursor.getInt(cursor.getColumnIndex(ContainerElement.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_DESC))
                );

                todayElements.add(elem);
            }while(cursor.moveToNext());
        }

        return todayElements;
    }

    public ArrayList<ContainerElement> getAllElements(){
        ArrayList<ContainerElement> allElements = new ArrayList<>();

        // Coger todos los datos.
        String selectQuery = "SELECT * FROM " + ContainerElement.TABLE_NAME + " ORDER BY " +
                ContainerElement.COLUMN_TIMESTAMP + " DESC ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                ContainerElement elem = new ContainerElement(
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_CONCEPTO)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_CANTIDAD)),
                        cursor.getInt(cursor.getColumnIndex(ContainerElement.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_TIMESTAMP)),
                        cursor.getString(cursor.getColumnIndex(ContainerElement.COLUMN_DESC))
                );

                allElements.add(elem);
            }while(cursor.moveToNext());
        }

        db.close();

        return allElements;
    }

    public int getNumberOfElements(){
        String countQuery = "SELECT * FROM " + ContainerElement.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();

        return count;
    }

    public int updateElement(ContainerElement elem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContainerElement.COLUMN_DESC, elem.getDescription());
        values.put(ContainerElement.COLUMN_CONCEPTO,elem.getConcepto());
        values.put(ContainerElement.COLUMN_CANTIDAD,elem.getCantidad());

        Log.i(TAG,"updating element");
        // Devuelve la nueva
        return db.update(ContainerElement.TABLE_NAME, values, ContainerElement.COLUMN_ID + " = ?",
                new String[]{String.valueOf(elem.getId())});
    }

    public void deleteElement(ContainerElement elem){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ContainerElement.TABLE_NAME,ContainerElement.COLUMN_ID + " = ?",
                new String[]{String.valueOf(elem.getId())});
        db.close();
    }
}
