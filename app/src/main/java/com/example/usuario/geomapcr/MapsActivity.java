package com.example.usuario.geomapcr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marcador;
    double lat = 9.971157;
    double lng = -84.129138;
    private static String url_tipo_ubicacion = "http://geomapcr.atwebpages.com/get_tipo_ubicaciones.php";
    private static String url_all_ubicacion = "http://geomapcr.atwebpages.com/get_all_ubicaciones.php";
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    JSONArray ubicaciones = null;
    private ProgressDialog pDialog;
    private int tipo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        ubicaciones = new JSONArray();
        Intent callingIntent = getIntent();
        tipo = callingIntent.getIntExtra("tipo", 1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new LoadAllProducts().execute(); // ver en que momento usar

        FloatingActionButton fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        registerForContextMenu(fab4);

        OnclickDelFloatingActionButton(R.id.fab);
        OnclickDelFloatingActionButton(R.id.fab2);
        OnclickDelFloatingActionButton(R.id.fab3);
        OnclickDelFloatingActionButton(R.id.fab4);



    } // FIN ONCREATE



    public void OnclickDelFloatingActionButton(int ref) {

        // Ejemplo  OnclickDelButton(R.id.MiButton);
        // 1 Doy referencia al Button
        View view =findViewById(ref);
        FloatingActionButton miButton = (FloatingActionButton) view;
        //  final String msg = miButton.getText().toString();
        // 2.  Programar el evento onclick
        miButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.fab:
                        // acercar
                        mMap.animateCamera(CameraUpdateFactory.zoomIn());
                        break;

                    case R.id.fab2:
                        // alejar
                        mMap.animateCamera(CameraUpdateFactory.zoomOut());
                        break;

                    case R.id.fab3:
                        miUbicacion();
                        break;

                    case R.id.fab4:
                        openContextMenu(findViewById(R.id.fab4));
                        break;

                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelFloatingActionButton


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.fab4) {
            menu.setHeaderTitle("Tipos de mapa");
            menu.add(0, 1, 0, "Satelital");
            menu.add(0, 2, 0, "Terreno");
            menu.add(0, 3, 0, "Hibrido");
            menu.add(0, 4, 0, "Normal");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int opcionseleccionada = item.getItemId();
        switch (item.getItemId()) {
            case 1: mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
            case 2: mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); break;
            case 3: mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
            case 4: mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); break;
            default:  Mensaje("No clasificado"); break;
        }
        return true;
    }





    public void Mensaje(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();};


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();

    }

    public void locGim(double lat, double lon, String nombre, int tipo){
     /*   Volcanes          1
        LLanuras		  2
        Rios			  3
        Cordilleras		  4
        Parque Nacional   5*/
        LatLng posicion = new LatLng(lat, lon);
        switch (tipo) {
            case 1 :    mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(""+posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.volcanes)));
                break;
            case 2 :    mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(""+posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ullanura)));
                break;
            case 3 :    mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(""+posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.urio)));
                break;
            case 4 :    mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(""+posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.ucordillera)));
                break;
            case 5 :    mMap.addMarker(new MarkerOptions().position(posicion).title(nombre).snippet(""+posicion).icon(BitmapDescriptorFactory.fromResource(R.drawable.uparque)));
                break;
            default: break;
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

    private void cargarUbicaciones(){

        for(int i=0; i<ubicaciones.length();i++){
            try {
                JSONObject c = ubicaciones.getJSONObject(i);
                locGim(c.getDouble("latitud"),c.getDouble("longitud"),c.getString("nombre"),c.getInt("tipo"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void agregarMarcador(double lat, double lng) {
        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 9);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Mi posición")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pos)));
        mMap.animateCamera(miUbicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            agregarMarcador(lat, lng);
        }

    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarUbicacion(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100000,0,locListener);
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Cargando ubicaciones. Por favor espere...");
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
                json = jParser.makeHttpRequest(url_tipo_ubicacion, "GET", params);
            }
            else{
                json = jParser.makeHttpRequest(url_all_ubicacion,"GET",params);
            }
            try {
                // Checking for SUCCESS TAG
                success = json.getInt("success");

                if (success == 1) {
                    try {
                        // Preguntas found
                        // Getting Array of preguntas
                        ubicaciones = json.getJSONArray("ubicaciones");


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
                        cargarUbicaciones();
                        // Colocar si trabajo algo
                    } else {
                        // Colocar si no
                    }


                }
            });

        }


    } // Fin LoadallProducts




}
