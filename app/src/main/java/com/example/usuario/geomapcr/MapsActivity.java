package com.example.usuario.geomapcr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

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
    double lat = 0.0;
    double lng = 0.0;
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

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        miUbicacion();
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(9.9961661, -84.1196966999999);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        LatLng sydney3 = new LatLng(10.0009657, -84.11567230000000);
        mMap.addMarker(new MarkerOptions().position(sydney3).title("p"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        /*
        *  gim(int lat, int lon){
        *      LatLng sydney3 = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney3).title("p"));
        *
        *  }
        *
        * */
    }

    public void locGim(double lat, double lon, String nombre, int tipo){
     /*   Volcanes          1
        LLanuras		  2
        Rios			  3
        Cordilleras		  4
        Parque Nacional   5*/
        LatLng gimnasio = new LatLng(lat, lon);
        switch (tipo) {
            case 1 :    mMap.addMarker(new MarkerOptions().position(gimnasio).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.volcanes)));
                break;
            case 2 :    mMap.addMarker(new MarkerOptions().position(gimnasio).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.ullanura)));
                break;
            case 3 :    mMap.addMarker(new MarkerOptions().position(gimnasio).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.urio)));
                break;
            case 4 :    mMap.addMarker(new MarkerOptions().position(gimnasio).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.ucordillera)));
                break;
            case 5 :    mMap.addMarker(new MarkerOptions().position(gimnasio).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.uparque)));
                break;
            default: break;
        }


    }



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
                .title("Posición")
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locListener);
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
