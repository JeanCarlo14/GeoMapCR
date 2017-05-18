package com.example.hilla.geomapcr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Resultados extends AppCompatActivity {
private int puntos1=0;
private int puntos2=0;
private int puntosTotal=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados);

        Intent callingIntent = getIntent();
        puntos1 = callingIntent.getIntExtra("puntosA", 1);
        puntos2 = callingIntent.getIntExtra("puntosB", 1);

        TextView buenas = (TextView) findViewById(R.id.textView5);
        TextView malas = (TextView) findViewById(R.id.textView6);
        TextView nota = (TextView) findViewById(R.id.textView3);
        buenas.setText(String.valueOf(puntos1));
        malas.setText(String.valueOf(puntos2));
        if(puntos1!=0)
            puntosTotal=(puntos1*100)/(puntos1+puntos2);

       nota.setText(String.valueOf(puntosTotal));

        OnclickDelButton(R.id.again);
        OnclickDelButton(R.id.inicio);



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
                        startActivity(intento1);
                        break;

                    case R.id.inicio:
                        Intent intento = new Intent(getApplicationContext(), MainMenu.class);
                        startActivity(intento);
                        break;



                    default:break; }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton


}
