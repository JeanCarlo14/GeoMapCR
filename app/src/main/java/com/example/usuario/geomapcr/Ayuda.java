package com.example.usuario.geomapcr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Ayuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);
        Mensaje("Ayuda");

    }//fin oncreate

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};

}//fin