package com.example.usuario.geomapcr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Actividad_Ayuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__ayuda);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // alambramos el FloatingActionButton
        FloatingActionButton MiFloatingActionButton = (FloatingActionButton) findViewById(R.id.link_tutorial);
        //Programamos el evento onclick
        MiFloatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Uri uri = Uri.parse("https://www.youtube.com/watch?v=Pbo7_Q5nS9Y");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

        });


    }

}
