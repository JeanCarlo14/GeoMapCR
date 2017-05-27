package com.example.usuario.geomapcr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Resultados extends AppCompatActivity {
    private int puntos1=0;
    private int puntos2=0;
    private int tipo = 0;
    private float puntosTotal=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

         Intent callingIntent = getIntent();
        puntos1 = callingIntent.getIntExtra("puntosA", 1);
        puntos2 = callingIntent.getIntExtra("puntosB", 1);
        tipo = callingIntent.getIntExtra("tipo", 1);
        SharedPreferences pref = getSharedPreferences("PreGeoMap", MODE_PRIVATE);
        TextView nombre = (TextView) findViewById(R.id.txt_Nombre);
        nombre.setText(pref.getString("nombre","Usuario")); // REVISAR
        TextView buenas = (TextView) findViewById(R.id.textView5);
        TextView malas = (TextView) findViewById(R.id.textView6);
        TextView nota = (TextView) findViewById(R.id.textView3);
        buenas.setText(String.valueOf(puntos1));
        malas.setText(String.valueOf(puntos2));
        if(puntos1!=0)
            puntosTotal =(float)(puntos1*100)/(puntos1+puntos2);

        nota.setText(String.valueOf(puntosTotal).substring(0,4));

        OnclickDelButton(R.id.again);
        OnclickDelButton(R.id.inicio);

        sonidoPuntoTotal();


    }

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
                    case R.id.again:
                        Intent intento1 = new Intent(getApplicationContext(), Jugar.class);
                        intento1.putExtra("tipo", tipo);
                        startActivity(intento1);
                        break;

                    case R.id.inicio:
                        Intent intento = new Intent(getApplicationContext(), MenuPrincipal.class);
                        startActivity(intento);
                        break;

                    default: break;
                }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton

    private void sonidoPuntoTotal(){
        if(puntosTotal>70)
            ReproducirAudioAplausos();
        else
            ReproducirAudioWawa();
    }

    MediaPlayer misonido;

    public void ReproducirAudioAplausos(){
        misonido = MediaPlayer.create(this, R.raw.aplauso);
        misonido.start();

    }
    public void ReproducirAudioWawa(){
        misonido = MediaPlayer.create(this, R.raw.wawa);
        misonido.start();

    }
    public void PararReproducirAudio(){
        misonido.stop();

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


}
