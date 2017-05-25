package com.example.usuario.geomapcr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

import static com.example.usuario.geomapcr.R.id.btn_siguiente;

public class Jugar extends AppCompatActivity {

    private static String url_preguntas = "http://geomapcr.atwebpages.com/get_patient_details.php";
    private static String url_all_preguntas = "http://geomapcr.atwebpages.com/get_all_data.php";
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    JSONArray preguntas = null;
    private ProgressDialog pDialog;
    private int tipo = 0;
    private int con = 0;
    private int conta = 0;
    private String correcta="";
    private String opcion="";
    private  int cant=1;
    private int preg_rand[];
    private int pos = 0;
    private int puntosA=0;
    private int puntosB=0;
    private int intentos=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugar);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        preguntas = new JSONArray();
        Intent callingIntent = getIntent();
        tipo = callingIntent.getIntExtra("tipo", 1);
        ReproducirAudio();
        OnclickDelButton(btn_siguiente);

        new LoadAllProducts().execute(); // ver en que momento usar
    }



    MediaPlayer misonido;

    public void ReproducirAudio(){
        misonido = MediaPlayer.create(this, R.raw.dos);
        misonido.start();

    }

    public void PararReproducirAudio(){
        misonido.stop();

    }

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};



    public void OnclickDelButton(int ref) {

        // Ejemplo  OnclickDelButton(R.id.MiButton);
        // 1 Doy referencia al Button
        View view =findViewById(ref);
        Button miButton = (Button) view;
        //  final String msg = miButton.getText().toString();
        // 2.  Programar el evento onclick


        miButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                switch (v.getId()) {

                    case btn_siguiente:
                        intentos++;
                        if(cant>preg_rand.length-1) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //sleep....

                                    Intent intento1 = new Intent(getApplicationContext(), Resultados.class);
                                    intento1.putExtra("puntosA", puntosA);
                                    intento1.putExtra("puntosB", puntosB);
                                    startActivity(intento1);
                                }
                            }, 2500);



                        }
                        cant++;
                        if(correcta.equals(opcion)){
                            esCorrecta();

                            puntosA++;
                            //  esCorrecta();
                        }
                        else {
                            esIncorrecta();

                            puntosB++;

                        }


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                Button Mi_button = (Button) findViewById(R.id.btn_siguiente);
                                Mi_button.setEnabled(true);
                                if(con<preg_rand.length-1) {
                                    con++;
                                    if(con==pos) {
                                        preRandom();
                                    }

                                    cargarDatos();

                                }
                            }
                        }, 2000);
                        Button Mi_button = (Button) findViewById(R.id.btn_siguiente);
                        Mi_button.setEnabled(false);
                      /*if(con<preguntas.length()-1) {
                            btn_atras_Siguiente(true,true);
                            con++;

                            cargarDatos();

                        }*/
                        if(con == preg_rand.length-1){
                            //btn_atras_Siguiente(true,false);
                            if(cant+1>preg_rand.length-1)
                                Mi_button.setText("Finalizar");
                            else
                                Mi_button.setText("Siguiente");
                        }

                        break;


                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton




    /*
         * Background Async Task to Load all product by making HTTP Request
         * */
    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Jugar.this);
            pDialog.setMessage("Cargando preguntas. Por favor espere...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        int success = 0;

        /**
         * getting un usuario from url
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tipo", tipo + ""));



            // getting JSON string from URL
            JSONObject json = null;
            if(tipo!=7) {
                json = jParser.makeHttpRequest(url_preguntas, "GET", params);
            }
            else{
                json = jParser.makeHttpRequest(url_all_preguntas,"GET",params);
            }
            try {
                // Checking for SUCCESS TAG
                success = json.getInt("success");

                if (success == 1) {
                    try {
                        // Preguntas found
                        // Getting Array of preguntas
                        preguntas = json.getJSONArray("preguntas");

                        if (preguntas.length() < 7 || tipo== 7) {
                            preg_rand = new int[preguntas.length()];
                        } else {
                            preg_rand = new int[7];
                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                    if (success == 1) {
                        // Mensaje("Entro en success");

                        preRandom();
                        cargarDatos();


                        // Colocar si trabajo algo
                    } else {
                        // Colocar si no
                    }


                }
            });

        }


    } // Fin LoadallProducts


    public void Mensaje2(String msg){Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    private int preRandom(){
        int rnd= (int) (Math.random() * preguntas.length());
        //Mensaje2("Valor random "+rnd);
        while(existe_numPre(rnd)){
            rnd= (int) (Math.random() * preguntas.length());
        }
        preg_rand[pos]=rnd;
        pos++;
        return rnd;
    }



    boolean existe_numPre(int num_rnd){
        for(int i= 0; i<preg_rand.length;i++){
            if(preg_rand[i] == num_rnd){
                return true;
            }
        }
        return false;
    }

    public void cargarDatos(){
        try {
           /* Mensaje2("LENGTH Preguntas"+preguntas.length());
            Mensaje2("LENGTH PREGUN_RAND"+preg_rand.length);
            Mensaje2("NUM PREGUNTA   "+preg_rand[con]);
             Mensaje2("CON "+con);*/
            JSONObject c = preguntas.getJSONObject(preg_rand[con]);

            TextView Mi_textview = (TextView) findViewById(R.id.txt_pregunta);

            Mi_textview.setText(c.getString("pregunta") + "");

            cambioImagen(c.getString("imagen"));

            CargarSpinner(c.getString("opciones"));
            this.correcta=c.getString("respuesta");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void cambioImagen(String img) {

        ImageView midib = (ImageView) findViewById(R.id.img_pregunta);
        PhotoViewAttacher photoView = new PhotoViewAttacher(midib);
        photoView.update();
        switch (img) {
            case "uno.png":
                midib.setImageResource(R.drawable.uno);
                break;
            case "dos.png":
                midib.setImageResource(R.drawable.dos);
                break;
            case "tres.png":
                midib.setImageResource(R.drawable.tres);
                break;
            case "cuatro.png":
                midib.setImageResource(R.drawable.cuatro);
                break;
            case "cinco.png":
                midib.setImageResource(R.drawable.cinco);
                break;
            case "seis.png":
                midib.setImageResource(R.drawable.seis);
                break;
            case "siete.png":
                midib.setImageResource(R.drawable.siete);
                break;
            case "nueve.png":
                midib.setImageResource(R.drawable.nueve);
                break;
            case "diez.png":
                midib.setImageResource(R.drawable.diez);
                break;
            case "once.png":
                midib.setImageResource(R.drawable.once);
                break;
            default:
                midib.setImageResource(R.drawable.mapa);
                break;

        }
    } // Fin cargar imagen


    String[] Opciones(String datos){
        int cantidad = 1;
        for(int i=0; i< datos.length(); i++){
            if(datos.charAt(i) == '&' ) {
                cantidad++;
            }
        }
        String[] opciones = new String[cantidad];
        String  aux = " ";
        int j = 0;

        for(int i=0; i< datos.length(); i++){
            if(datos.charAt(i) == '&' ) {
                aux = aux.substring(1,aux.length()-1);
                opciones[j]= aux;
                j++;
                aux = "";
            }
            else{
                aux = aux+datos.charAt(i);
            }
        }
        opciones[j]= aux.substring(1,aux.length());
        return opciones;
    }
    private void CargarSpinner(String datos) {

        Spinner s1;
        final String[] presidents = Opciones(datos);

        //---Spinner View---
        s1 = (Spinner) findViewById(R.id.spr_opciones);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, presidents);


        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (conta==0){conta++;
                    return;}

                opcion=presidents[position];

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        s1.setAdapter(adapter);


    }// fin de CargarSpinner

    public void esCorrecta(){

        //  Mensaje("correctaaaaaaa");
        Toast toast3 = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.cust_toast_layout,
                (ViewGroup) findViewById(R.id.lytLayout));

        // TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
        //  txtMsg.setText("Toast Personalizado");
        toast3.setGravity(Gravity.CENTER | 0 , 0, 0);
        toast3.setDuration(Toast.LENGTH_SHORT);
        toast3.setView(layout);
        toast3.show();
    }

    public void esIncorrecta(){

        //  Mensaje("correctaaaaaaa");
        Toast toast3 = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.icust_toast_layout,
                (ViewGroup) findViewById(R.id.lytLayout));

        // TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
        //  txtMsg.setText("Toast Personalizado");
        toast3.setGravity(Gravity.CENTER | 0 , 0, 0);
        toast3.setDuration(Toast.LENGTH_SHORT);
        toast3.setView(layout);
        toast3.show();
    }



    @Override
    protected void onStart(){
        super.onStart();
        // Mensaje2("Pase por OnStart");
    };
    @Override
    protected void onRestart(){
        super.onRestart();
        //  Mensaje2("Pase por onRestart");
    };
    @Override
    protected void onResume(){
        super.onResume();
        // Mensaje2("Pase por onResume");
    };
    @Override
    protected void onPause(){
        super.onPause();
        //  Mensaje2("Pase por onPause");
        PararReproducirAudio();
    };
    @Override
    protected void onStop(){
        super.onStop();
        // Mensaje2("Pase por onStop");
    };
    //estoy tratado e



} // Fin clase Jugar
