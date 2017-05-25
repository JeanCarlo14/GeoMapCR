package com.example.usuario.geomapcr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.os.Build.VERSION_CODES.M;
import static com.example.usuario.geomapcr.R.id.nav_volumen;

public class MenuPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_PERMISSION = 0; // Id para los permisos
    static EditText texto;
    private int cs=0;
    public MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        populateAutoComplete();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Mensaje("Bienvenidos a GeoMapCR");

        OnclickDelButton(R.id.btnTest);
        OnclickDelButton(R.id.btnMapa);
        OnclickDelButton(R.id.btnRelax);
        OnclickDelButton(R.id.btnAyuda);
        OnclickDelButton(R.id.btnJugar);

        Button boton = (Button) findViewById(R.id.btnJugar);
        Button boton2 = (Button) findViewById(R.id.btnMapa);
        registerForContextMenu(boton);
        registerForContextMenu(boton2);

        View hView = navigationView.getHeaderView(0);
        SharedPreferences pref = getSharedPreferences("PreGeoMap", MODE_PRIVATE);

        TextView txt_correo_Navi = (TextView) hView.findViewById(R.id.txt_nick);
        txt_correo_Navi.setText(pref.getString("nombre","Usuario"));

         cs = pref.getInt("sonido",0);
        ReproducirAudio();

        //View hView2 = navigationView.get();
 /*       TextView txt_nombreCompleto= (TextView) hView.findViewById(R.id.txt_nombre_completo);
        txt_nombreCompleto.setText(pref.getString("nombre","Sin Nombre")+" "+pref.getString("apellidos","Sin Apellidos"));
*/


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nombre) {
            DemeTexto(this.getCurrentFocus());
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_credenciales) {
            Intent intento3 = new Intent(getApplicationContext(), Info_Integrantes.class);
            startActivity(intento3);
            PararReproducirAudio();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        else if (id == nav_volumen) {
            sonarSINO(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sonarSINO(MenuItem item){
        if(cs==0){
            item.setIcon(R.drawable.apagado);
            PararReproducirAudio();
            cs=1;}
        else{
            item.setIcon(R.drawable.volumen);
            cs=0;
            ReproducirAudio();
        }
        guardarPrefSonido(cs);
    }

    private void guardarPrefSonido(int tipo){
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PreGeoMap", MODE_PRIVATE).edit();
            editor.putInt("sonido", cs);
            editor.commit();
    }


    public void DemeTexto(View view){
        // Uso:
        texto =  new EditText(view.getContext());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());

        builder1.setMessage("Digite su nombre:");
        texto.setText("");
        texto.selectAll();
        builder1.setView(texto);

        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        guardarNombre();
                    }
                });

        builder1.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    };

    private void guardarNombre(){
        if(texto.getText().length()!=0) {
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PreGeoMap", MODE_PRIVATE).edit();
            editor.putString("nombre", texto.getText().toString());
            editor.commit();
           cambiarNombre();
        }
    }
    private void cambiarNombre(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        SharedPreferences pref = getSharedPreferences("PreGeoMap", MODE_PRIVATE);
        TextView txt_correo_Navi = (TextView) hView.findViewById(R.id.txt_nick);
        txt_correo_Navi.setText(pref.getString("nombre","Usuario"));
    }




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

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }*/

    public void ReproducirAudio(){
        if(cs==0) {
            mp = MediaPlayer.create(this, R.raw.uno);
            mp.start();
        }

    }

    public void PararReproducirAudio(){
        if(cs==0)
        mp.stop();

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
                        Intent intento2 = new Intent(getApplicationContext(), Actividad_Ayuda.class);
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
                intento.putExtra("tipo", 3);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item2:
                intento.putExtra("tipo", 4);
                startActivity(intento);
                PararReproducirAudio();
                break;
            case R.id.item3:
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


   /*public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item12:
                Intent intento3 = new Intent(getApplicationContext(), Info_Integrantes.class);
                startActivity(intento3);
                PararReproducirAudio();
                break;
            default:  /*Mensaje("No clasificado");*/ //break;
     /*   }
        return super.onOptionsItemSelected(item);
    }*/// QUITAR

    @Override
    protected void onStart(){
        super.onStart();
       // ReproducirAudio();
        // Mensaje2("Pase por OnStart");
    };
    @Override
    protected void onRestart(){
        super.onRestart();
        if(cs==0)
        ReproducirAudio();
        //  Mensaje2("Pase por onRestart");
    };
    @Override
    protected void onResume(){
        super.onResume();
        //ReproducirAudio();
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
        PararReproducirAudio();

    };
    //estoy tratando de que se haga un cambio en el git hub
}//fin





