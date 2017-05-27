package com.example.usuario.geomapcr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
   // private int con = 0;
    private int conta = 0; // Para que el spinner no se seleccione
    private String correcta="";
    private String opcion="";
    private  int cant=1;
    private int preg_rand[];
    private int pos = 0;
    private int puntosA=0;
    private int puntosB=0;
    private int sonido=0;



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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nuevo:
                if(sonido==0){
                    PararReproducirAudio();
                    item.setIcon(R.drawable.apagado);
                sonido=1;}

                else
                {
                    ReproducirAudio();
                    item.setIcon(R.drawable.volumen);
                    sonido=0;

                }
                return true;
                       default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void guardarPrefSonido(int tipo){
        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PreGeoMap", MODE_PRIVATE).edit();
        editor.putInt("sonido", sonido);
        editor.commit();
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
                switch (v.getId()) {

                    case btn_siguiente:
                       funcionalidad_Btn_Siguiente();

                        break;


                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton


    private void funcionalidad_Btn_Siguiente(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Button Mi_button = (Button) findViewById(R.id.btn_siguiente);
                Mi_button.setEnabled(true);
                if(cant<=preg_rand.length) { //VER
                    // con++;
                    if((cant-1)==pos) {
                        preRandom();
                    }

                    cargarDatos();

                }
            }
        }, 2000);

        cambiarNombreBoton();

        if(cant>preg_rand.length) {
             handler = new Handler();
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
        sumarRestarPuntos();


    }


    private void cambiarNombreBoton(){
        Button Mi_button = (Button) findViewById(R.id.btn_siguiente);
       // if((cant-1) == preg_rand.length-1){
            //btn_atras_Siguiente(true,false);
            if(cant >= preg_rand.length)
                Mi_button.setText("Finalizar");
            else
                Mi_button.setText("Siguiente");
        Mi_button.setEnabled(false);
    //    }
    }

private void sumarRestarPuntos(){
    // cant++;
    if(correcta.equals(opcion)){
        esCorrecta();

        puntosA++;
        //  esCorrecta();
    }
    else {
        esIncorrecta();
        puntosB++;
    }

}
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
                        llenarVectorPregInt();
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


    private void llenarVectorPregInt(){
        for(int i=0;i<preg_rand.length;i++){
            preg_rand[i]=-1;
        }
    }
    public void MensajeOK(String msg){
        View v1 = getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder( v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {} });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        ;};

    public void Mensaje2(String msg){Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};

    private int preRandom(){
        int rnd=  0;
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

            JSONObject c = preguntas.getJSONObject(preg_rand[(cant-1)]);

            TextView Mi_textview = (TextView) findViewById(R.id.txt_pregunta);

            Mi_textview.setText(cant+". "+c.getString("pregunta") + "");

            cambioImagen(c.getString("imagen"));

            CargarSpinner(c.getString("opciones"));
            this.correcta=c.getString("respuesta");
            cant++;
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

        Toast toast3 = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.cust_toast_layout,
                (ViewGroup) findViewById(R.id.lytLayout));

        toast3.setGravity(Gravity.CENTER | 0 , 0, 0);
        toast3.setDuration(Toast.LENGTH_SHORT);
        toast3.setView(layout);
        toast3.show();
    }

    public void esIncorrecta(){

        Toast toast3 = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();

        View layout = inflater.inflate(R.layout.icust_toast_layout,
                (ViewGroup) findViewById(R.id.lytLayout));

        toast3.setGravity(Gravity.CENTER | 0 , 0, 0);
        toast3.setDuration(Toast.LENGTH_SHORT);
        toast3.setView(layout);
        toast3.show();
    }



    @Override
    protected void onStart(){
        super.onStart();
    };
    @Override
    protected void onRestart(){
        super.onRestart();
    };
    @Override
    protected void onResume(){
        super.onResume();
    };
    @Override
    protected void onPause(){
        super.onPause();
        PararReproducirAudio();
    };
    @Override
    protected void onStop(){
        super.onStop();
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { // Regresar boton atras
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // Esto es lo que hace mi botón de atras
            Intent intento = new Intent(getApplicationContext(), MenuPrincipal.class);
            startActivity(intento);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



} // Fin clase Jugar
