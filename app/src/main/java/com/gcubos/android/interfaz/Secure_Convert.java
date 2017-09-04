package com.gcubos.android.interfaz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import DataBase.DataDB;
import Secure.AESHelper;

public class Secure_Convert extends AppCompatActivity {

    AESHelper Secure;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure__convert);
        Secure = new AESHelper();
    }

    

    public void onClickB1(View view){
        try {
            DataDB DB = new DataDB(getApplicationContext());
            EditText eTexto = (EditText) findViewById(R.id.editText);
            //eTexto.append(Secure.asciiToHex(getString(R.string.HD)) + "\n");
            //eTexto.append(Secure.hexToASCII(Secure.asciiToHex(getString(R.string.HD)))  + "\n");
            eTexto.append(DB.getRoot() + " / " + Secure.encrypt(DB.getRoot(),getString(R.string.HD)) + "\n");
            DB.Actualiza("MESG",Secure.encrypt(DB.getRoot(),getString(R.string.HD)));
            eTexto.append(DB.getDRoot() + " / " + Secure.decrypt(DB.getDRoot(), getString(R.string.HD)) + "\n");
        } catch (Exception e2){
            e2.printStackTrace();
        }
    }
}
