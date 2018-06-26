package com.ciberdictionary.ciberdictionary.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ciberdictionary.ciberdictionary.database.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

/**
 * Created by inmobitec on 6/24/18.
 */

public class MyDbHelper {

    private DbConnection dbc;
    private final SQLiteDatabase db_write,db_read;

    Context contexto;

    //instantiating connection and Writable and readable connection variables
    public MyDbHelper(Context context){
        contexto = context;
        dbc= new DbConnection(context);
        db_write = dbc.getWritableDatabase();
        db_read = dbc.getReadableDatabase();
    }

    //Do the same as ChupApp, where each of these methods return a JSONObject and passed a ContextValues parameter
    //Activity was the one that called the Model class and transform it to ContextValues format

    public JSONArray init(){
        JSONArray data = readItems();

        Log.v("INICIANDO", "length: "+data.length());

        if (data.length()<=0){

            /*
            ContentValues values = new ContentValues();
            values.put(dbc.COLUMN_WORD, "");
            values.put(dbc.COLUMN_TYPE, "");
            values.put(dbc.COLUMN_DEFINITION, "");
            values.put(dbc.COLUMN_EXAMPLE, "");

            data= createItem(values);
            */
        }

        return data;
    }

    public JSONObject createItem(ContentValues values){

        db_write.insert(dbc.TABLE_WORD, null,values);


        //Get the last element in the list after having inserted
        JSONArray item_list = readItems();

        Log.v("ITEM LIST: ", String.valueOf(item_list));

        JSONObject item_created = new JSONObject();
        try {
            item_created = item_list.getJSONObject(item_list.length()-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item_created;
    }

    /*
    public JSONObject readItems(){

        String[] projection = {
                dbc.COLUMN_ID,
                dbc.COLUMN_WORD,
                dbc.COLUMN_TYPE,
                dbc.COLUMN_DEFINITION,
                dbc.COLUMN_EXAMPLE
        };

        String sortOrder = dbc.COLUMN_ID + " DESC";

        Cursor c =db_read.query(dbc.TABLE_WORD, projection, null, null, null, null, sortOrder);

        return getData(c);

    }*/

    public JSONArray readItems(){
        JSONArray items_array = new JSONArray();

        String selectQuery= "SELECT * FROM " + dbc.TABLE_WORD;
        Cursor c = db_read.rawQuery(selectQuery, null);

        if (c.moveToFirst()){
            do{
                JSONObject item = new JSONObject();
                try {
                    item.put("id", c.getInt(c.getColumnIndex(dbc.COLUMN_ID)));
                    item.put("word", c.getString(c.getColumnIndex(dbc.COLUMN_WORD)));
                    item.put("type", c.getString(c.getColumnIndex(dbc.COLUMN_TYPE)));
                    item.put("definition", c.getString(c.getColumnIndex(dbc.COLUMN_DEFINITION)));
                    item.put("example", c.getString(c.getColumnIndex(dbc.COLUMN_EXAMPLE)));
                    //insert into JSONArray
                    items_array.put(item);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while(c.moveToNext());
        }

        return items_array;

    }

    public JSONObject updateItem(ContentValues values, int id){
        String[] selectionArgs = {String.valueOf(id)};

        Log.v("Selection Args: ", String.valueOf(selectionArgs));

        db_read.update(dbc.TABLE_WORD, values, dbc.COLUMN_ID+"= ?", selectionArgs);

        //Get the last element in the list after having inserted
        JSONArray item_list = readItems();

        Log.v("ITEM LIST: ", String.valueOf(item_list));

        JSONObject item_updated = new JSONObject();
        try {
            item_updated = item_list.getJSONObject(id-1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item_updated;
    }

    public void deleteItem(int id){
        String[] selectionArgs = {String.valueOf(id)};

        db_write.delete(dbc.TABLE_WORD, dbc.COLUMN_ID+"= ?", selectionArgs);
    }

    public JSONObject lookUpItem(String word){

        JSONObject item = new JSONObject();

        String selectQuery= "SELECT * FROM " + dbc.TABLE_WORD + " WHERE " + dbc.COLUMN_WORD + " = ?";
        String[] selectionArgs= {word};

        Log.v("SELECT QUERY:", selectQuery);

        Cursor c = db_read.rawQuery(selectQuery, selectionArgs);

        if (c != null){
            c.moveToFirst();
            Log.v("C:", String.valueOf(c.getCount()));
            Log.v("c.getColumnIndex:", String.valueOf(c.getColumnIndex(dbc.COLUMN_ID)));
            Log.v("c.getInt:", String.valueOf(c.getColumnIndex(dbc.COLUMN_ID)));

            if (c.getCount()!=0){
                try {
                    item.put("id", c.getInt(c.getColumnIndex(dbc.COLUMN_ID)));
                    item.put("word", c.getString(c.getColumnIndex(dbc.COLUMN_WORD)));
                    item.put("type", c.getString(c.getColumnIndex(dbc.COLUMN_TYPE)));
                    item.put("definition", c.getString(c.getColumnIndex(dbc.COLUMN_DEFINITION)));
                    item.put("example", c.getString(c.getColumnIndex(dbc.COLUMN_EXAMPLE)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        return item;

    }

    /*
    public JSONObject getData(Cursor cursor){
        JSONObject values = new JSONObject();

        //Log.v("GETDATA","PASO");

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //Log.v("GETDATA",cursor.toString());
            values = setCursor(cursor);
        }

        cursor.close();

        return values;
    }

    public JSONObject setCursor(Cursor cursor){
        JSONObject c = new JSONObject();
        //Log.v("GETDATA","PASO - - -");

        try {
            c.put(dbc.COLUMN_ID, cursor.getString(0));
            c.put(dbc.COLUMN_WORD, cursor.getString(1));
            c.put(dbc.COLUMN_TYPE, cursor.getString(2));
            c.put(dbc.COLUMN_DEFINITION, cursor.getString(3));
            c.put(dbc.COLUMN_EXAMPLE, cursor.getString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }
    */


}
