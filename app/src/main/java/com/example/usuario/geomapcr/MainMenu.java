package com.example.usuario.geomapcr;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.os.Build.VERSION_CODES.M;

public class MainMenu extends AppCompatActivity  {

    private static final int REQUEST_PERMISSION = 0; // Id para los permisos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateAutoComplete();
        setContentView(R.layout.activity_main_menu);
        Mensaje("Bienvenidos a GeoMapCR");
        ReproducirAudio();
        OnclickDelButton(R.id.btnTest);
        OnclickDelButton(R.id.btnMapa);
        OnclickDelButton(R.id.btnRelax);
        OnclickDelButton(R.id.btnAyuda);
        OnclickDelButton(R.id.btnJugar);

        Button boton = (Button) findViewById(R.id.btnJugar);
        Button boton2 = (Button) findViewById(R.id.btnMapa);
        registerForContextMenu(boton);
        registerForContextMenu(boton2);


    }//fin oncreate
    MediaPlayer misonido;


    private void populateAutoComplete() {
        if (!mayRequestPermissions()) {
            return;
        }
    }

    private boolean mayRequestPermissions() { // Pedir permiso ubicación
        if (Build.VERSION.SDK_INT < M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void ReproducirAudio(){
        misonido = MediaPlayer.create(this, R.raw.uno);
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
                    case R.id.btnJugar:
                        openContextMenu(findViewById(R.id.btnJugar));
                        break;

                    case R.id.btnTest:
                        Intent intento = new Intent(getApplicationContext(), Jugar.class);
                        intento.putExtra("tipo", 6);
                        startActivity(intento);
                        break;

                    case R.id.btnMapa:
                        openContextMenu(findViewById(R.id.btnMapa));
                        break;

                    case R.id.btnRelax:
                        Intent intento3 = new Intent(getApplicationContext(), Jugar.class);
                        intento3.putExtra("tipo", 7);
                        startActivity(intento3);
                        break;

                    case R.id.btnAyuda:
                        Mensaje("Ayuda");

                        Intent intento2 = new Intent(getApplicationContext(), Ayuda.class);
                        startActivity(intento2);

                        break;
                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {
            case R.id.btnJugar:
                MenuInflater infla =getMenuInflater();
                infla.inflate(R.menu.mimenu, menu);
                break;
            case R.id.btnMapa:
                MenuInflater infla2 =getMenuInflater();
                infla2.inflate(R.menu.menumapa, menu);
                break;

            default: /* Mensaje("No clasificado");*/ break;
        }
        super.onCreateContextMenu(menu, v, menuInfo);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int opcionseleccionada = item.getItemId();
        Intent intento = new Intent(getApplicationContext(), Jugar.class);
        Intent intento2 = new Intent(getApplicationContext(), MapsActivity.class);

        switch (opcionseleccionada) {
            case R.id.item1:
                Mensaje("Ríos");
                intento.putExtra("tipo", 3);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item2:
                Mensaje("Cordilleras y Cerros");
                intento.putExtra("tipo", 4);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item3:
                Mensaje("Volcanes");
                intento.putExtra("tipo", 1);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item4:
                intento.putExtra("tipo", 2);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item5:
                Mensaje("Parques Nacionales");
                intento.putExtra("tipo", 5);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item6:
                intento2.putExtra("tipo", 3);
                startActivity(intento2);
                PararReproducirAudio();
                break;
            case R.id.item7:
                intento2.putExtra("tipo", 4);
                startActivity(intento2);
                PararReproducirAudio();
                break;
            case R.id.item8:
                intento2.putExtra("tipo", 1);
                startActivity(intento2);
                PararReproducirAudio();
                break;
            case R.id.item9:
                intento2.putExtra("tipo", 2);
                startActivity(intento2);
                PararReproducirAudio();
                break;
            case R.id.item10:
                intento2.putExtra("tipo", 5);
                startActivity(intento2);
                PararReproducirAudio();
                break;
            case R.id.item11:
                intento2.putExtra("tipo", 7);
                startActivity(intento2);
                PararReproducirAudio();
                break;


            default:  Mensaje("No clasificado"); break;
        }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item12:
                Intent intento3 = new Intent(getApplicationContext(), Info_Integrantes.class);
                startActivity(intento3);
                PararReproducirAudio();
                break;
            default:  /*Mensaje("No clasificado");*/ break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();
        ReproducirAudio();
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
    };
    //estoy tratando de que se haga un cambio en el git hub
}//fin





