package com.ciberdictionary.ciberdictionary;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ciberdictionary.ciberdictionary.database.DbConnection;
import com.ciberdictionary.ciberdictionary.database.MyDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ProgressDialog dialog;
    Button boton_buscar, ver_mapa, edit_word, delete_word, getWeb_word, clear_fields, clear_db, modify_btn;
    EditText word_input;
    String data_result;
    JSONArray data_array;
    TextView word_id;
    EditText word_type, word_definition, word_example;
    private MyDbHelper db;
    private JSONObject item;
    private JSONArray table;
    private DbConnection dbc;
    boolean webUpdate=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boton_buscar = (Button)findViewById(R.id.btn_buscar);
        ver_mapa = (Button)findViewById(R.id.btn_vermapa);
        word_input = (EditText)findViewById(R.id.wordInput);
        word_id = (TextView)findViewById(R.id.word_id);
        word_type = (EditText)findViewById(R.id.word_type);
        word_definition = (EditText)findViewById(R.id.word_definition);
        word_example = (EditText)findViewById(R.id.word_example);
        edit_word = (Button)findViewById(R.id.edit_button);
        delete_word = (Button)findViewById(R.id.delete_button);
        getWeb_word = (Button)findViewById(R.id.web_button);
        clear_fields = (Button)findViewById(R.id.btn_clearFields);
        clear_db = (Button)findViewById(R.id.btn_clearDB);
        modify_btn = (Button)findViewById(R.id.modify_btn);

        db = new MyDbHelper(getApplicationContext());
        table= db.init();

        Log.v("TABLE Json array:", String.valueOf(table));

        loadEvents();
    }

    public void loadEvents(){

        boton_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //execute search on word
                search_word();
            }
        });

        clear_fields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear the fields
                word_input.setText("");
                word_id.setText("");
                word_type.setText("");
                word_definition.setText("");
                word_example.setText("");
                modify_btn.setVisibility(View.GONE);
                edit_word.setVisibility(View.GONE);
                delete_word.setVisibility(View.GONE);
                getWeb_word.setVisibility(View.GONE);
                word_input.setFocusableInTouchMode(true);
            }
        });

        clear_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete ALL from database
                //db.deleteAllItems();
            }
        });

        //load map event

        ver_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //preparing the view to be loaded
                Intent i = new Intent(getApplicationContext(), MapActivity.class);

                Bundle b = new Bundle();
                //Using latitude and longitude of Ermirna Turquia
                b.putString("latitude", "38.4120769");
                b.putString("longitude", "27.130687");
                i.putExtras(b);
                //inicio
                startActivity(i);
            }
        });
    }

    public void search_word(){

        String word = String.valueOf(word_input.getText());

        Log.v("The word is :", word);

        //check if word already exists in database

        JSONObject item = new JSONObject();
        item = db.lookUpItem(word);

        Log.v("Item found is:", String.valueOf(item));

        try {
            if (item.length()!=0){
                //the word is already saved in local db already...get it from local db

                Log.v("WORD:", "word exists");
                //fill out type of word
                word_type.setText(item.getString("type"));
                //fill out definition of word
                word_definition.setText(item.getString("definition"));
                //fill out example of word usage
                word_example.setText(item.getString("example"));

                //Set item_id in hidden textview
                word_id.setText(item.getString("id"));

                //assign event to modify button
                modify_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        word_input.setFocusable(false);
                        word_type.setFocusableInTouchMode(true);
                        word_example.setFocusableInTouchMode(true);
                        word_definition.setFocusableInTouchMode(true);
                        modify_btn.setVisibility(View.GONE);
                        edit_word.setVisibility(View.VISIBLE);
                        delete_word.setVisibility(View.VISIBLE);
                        getWeb_word.setVisibility(View.VISIBLE);
                        loadWordEvents();
                    }
                });

                //enable edit, delete, getfromWeb button
                modify_btn.setVisibility(View.VISIBLE);
                edit_word.setVisibility(View.GONE);
                delete_word.setVisibility(View.GONE);
                getWeb_word.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(),
                        "Palabra existe en BD", Toast.LENGTH_LONG).show();

            }else{
                //the word has not been looked up yet, so consult the web
                Log.v("WORD:", "word DOES not exist");
                String strURL= "https://owlbot.info/api/v2/dictionary/"+word;

                wsAsyncTask ws = new wsAsyncTask();
                ws.execute(strURL);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void fillOutResults(){
        Log.v("FILLING:", "OUT RESULTS");

        try {
            //fill out type of word
            word_type.setText(data_array.getJSONObject(0).getString("type"));
            //fill out definition of word
            word_definition.setText(data_array.getJSONObject(0).getString("definition"));
            //fill out example of word usage
            //first check if example is null
            String example_received= data_array.getJSONObject(0).getString("example");
            if (example_received=="null"){
                example_received= "Example does not exist";
            }


            word_example.setText(example_received);

            ContentValues cv_word = new ContentValues();
            cv_word.put(dbc.COLUMN_WORD, String.valueOf(word_input.getText()));
            cv_word.put(dbc.COLUMN_TYPE, data_array.getJSONObject(0).getString("type"));
            cv_word.put(dbc.COLUMN_DEFINITION, data_array.getJSONObject(0).getString("definition"));
            cv_word.put(dbc.COLUMN_EXAMPLE, example_received);

            if (!webUpdate){
                //SAVE TO DATABASE!!!

                item = db.createItem(cv_word);
                Log.v("SAVED TO DB:", String.valueOf(item));

                //Set item_id in hidden textview
                word_id.setText(item.getString("id"));
            }else{
                //Update the DATABASE
                updateWord();
            }

            //assign event to modify button
            modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    word_input.setFocusable(false);
                    word_type.setFocusableInTouchMode(true);
                    word_example.setFocusableInTouchMode(true);
                    word_definition.setFocusableInTouchMode(true);
                    modify_btn.setVisibility(View.GONE);
                    edit_word.setVisibility(View.VISIBLE);
                    delete_word.setVisibility(View.VISIBLE);
                    getWeb_word.setVisibility(View.VISIBLE);

                    loadWordEvents();
                }
            });


            //enable edit, delete, getfromWeb button
            modify_btn.setVisibility(View.VISIBLE);
            edit_word.setVisibility(View.GONE);
            delete_word.setVisibility(View.GONE);
            getWeb_word.setVisibility(View.GONE);
            word_input.setFocusableInTouchMode(true);
            word_type.setFocusable(false);
            word_definition.setFocusable(false);
            word_example.setFocusable(false);

            //assign event to edit button
            loadWordEvents();

            webUpdate=false;


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadWordEvents(){

        edit_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm("update");
            }
        });

        delete_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm("delete");
            }
        });

        getWeb_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm("web");
            }
        });
    }

    public void confirm(final String action){

        AlertDialog.Builder confirm = new AlertDialog.Builder(this)
                .setTitle("Confirmar accion")
                .setMessage("Estás seguro que deseas realizar esta acción?");


        confirm.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.v("ACTION:", action);

                switch (action){
                    case "update": {
                        //do something to udpate in database
                        updateWord();
                        break;
                    }
                    case "delete":{
                        //do something
                        deleteWord();
                        break;
                    }
                    case "web":{
                        //extract from web
                        //the word has not been looked up yet, so consult the web
                        webUpdate=true;
                        //the word has not been looked up yet, so consult the web
                        Log.v("WORD:", "word DOES not exist");
                        String strURL= "https://owlbot.info/api/v2/dictionary/"+word_input.getText();

                        wsAsyncTask ws = new wsAsyncTask();
                        ws.execute(strURL);

                        break;
                    }
                }
            }
        });

        confirm.setCancelable(false);

        confirm.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        confirm.show();

    }

    public void updateWord(){
        //update values in database
        int id;
        String word, type, definition, example;
        id = Integer.parseInt(String.valueOf(word_id.getText()));
        word = String.valueOf(String.valueOf(word_input.getText()));
        type=String.valueOf(word_type.getText());
        definition= String.valueOf(word_definition.getText());
        example = String.valueOf(word_example.getText());

        ContentValues cv = new ContentValues();
        cv.put(dbc.COLUMN_WORD, word);
        cv.put(dbc.COLUMN_TYPE, type);
        cv.put(dbc.COLUMN_DEFINITION, definition);
        cv.put(dbc.COLUMN_EXAMPLE, example);

        Log.v("CV UPDATE:", String.valueOf(cv));

        item = db.updateItem(cv, id);

        Log.v("ITEM UPDATED: ", String.valueOf(item));

        Toast.makeText(getApplicationContext(),
                "Palabra actualizada", Toast.LENGTH_LONG).show();

        //enable edit, delete, getfromWeb button
        modify_btn.setVisibility(View.VISIBLE);
        edit_word.setVisibility(View.GONE);
        delete_word.setVisibility(View.GONE);
        getWeb_word.setVisibility(View.GONE);
        word_input.setFocusableInTouchMode(true);
        word_type.setFocusable(false);
        word_definition.setFocusable(false);
        word_example.setFocusable(false);

    }

    public void deleteWord(){
        //do something to delete from database
        //delete word in database
        int id;
        id = Integer.parseInt(String.valueOf(word_id.getText()));

        db.deleteItem(id);
        word_input.setText("");
        word_id.setText("");
        word_type.setText("");
        word_definition.setText("");
        word_example.setText("");

        Log.v("DELETED: ", "ok");
        Log.v("NEW TABLE:", String.valueOf(db.readItems()));

        Toast.makeText(getApplicationContext(),
                "Palabra borrada permanentemente", Toast.LENGTH_LONG).show();

        //enable edit, delete, getfromWeb button
        modify_btn.setVisibility(View.GONE);
        edit_word.setVisibility(View.GONE);
        delete_word.setVisibility(View.GONE);
        getWeb_word.setVisibility(View.GONE);
        word_input.setFocusableInTouchMode(true);
        word_type.setFocusable(false);
        word_definition.setFocusable(false);
        word_example.setFocusable(false);

    }

    ////////

    //I AM ONLY MISSING THE GET FROM WEB BUTTON AND ALSO CHECK FIRST IN THE DATABASE IF THE WORD THAT IS BEING LOOKED FOR ALREADY EXISTS IN DB

//////////////

    public class wsAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String[] strUrl) {
            return requestWebService(strUrl[0]);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading ...");
            dialog.setMessage("Obtaining information about the word...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String result){

            Log.v("RESULTS: ", result);

            data_result = result;
            try {
                data_array = new JSONArray(data_result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            fillOutResults();

            dialog.dismiss();

        }

        public String requestWebService(String serviceURL){
            HttpURLConnection urlConnection = null;

            try{
                URL urlToRequest = new URL(serviceURL);
                urlConnection = (HttpURLConnection)urlToRequest.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                //Get JSON data
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                Scanner scanner = new Scanner(in);
                String strJSON = scanner.useDelimiter("\\A").next();
                scanner.close();
                return strJSON;
            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch (SocketTimeoutException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }

            return null;
        }


    }

}
