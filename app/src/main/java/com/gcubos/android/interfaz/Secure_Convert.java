package com.gcubos.android.interfaz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
            //Log.v(DB.geteDest(), Secure.encrypt(DB.geteDest(),getString(R.string.HD)));
            //Log.v(DB.getEtiTip(), Secure.encrypt(DB.getEtiTip(),getString(R.string.HD)));
            //Log.v(DB.getLogIn(), Secure.encrypt(DB.getLogIn(),getString(R.string.HD)));
            //Log.v(DB.getPASS(), Secure.encrypt(DB.getPASS(),getString(R.string.HD)));
            //Log.v(DB.getPORT(), Secure.encrypt(DB.getPORT(),getString(R.string.HD)));
            //Log.v(DB.getSMTP(), Secure.encrypt(DB.getSMTP(),getString(R.string.HD)));
            //Log.v(DB.getTipos(), Secure.encrypt(DB.getTipos(),getString(R.string.HD)));
            //Log.v(DB.getURL(), Secure.encrypt(DB.getURL(),getString(R.string.HD)));
            //Log.v(DB.getUSER(), Secure.encrypt(DB.getUSER(),getString(R.string.HD)));
            //Log.v(DB.geteDest(), Secure.decrypt(DB.geteDest(),getString(R.string.HD)));
            //Log.v(DB.getEtiTip(), Secure.decrypt(DB.getEtiTip(),getString(R.string.HD)));
            //Log.v(DB.getLogIn(), Secure.decrypt(DB.getLogIn(),getString(R.string.HD)));
            //Log.v(DB.getPASS(), Secure.decrypt(DB.getPASS(),getString(R.string.HD)));
            //Log.v(DB.getPORT(), Secure.decrypt(DB.getPORT(),getString(R.string.HD)));
            //Log.v(DB.getSMTP(), Secure.decrypt(DB.getSMTP(),getString(R.string.HD)));
            //Log.v(DB.getTipos(), Secure.decrypt(DB.getTipos(),getString(R.string.HD)));
            //Log.v(DB.getURL(), Secure.decrypt(DB.getURL(),getString(R.string.HD)));
            Log.v("URL", Secure.encrypt("Croquis",getString(R.string.HD)));
            eTexto.append("TERMINO");
            //DB.Actualiza("MESG",Secure.encrypt(DB.getRoot(),getString(R.string.HD)));
        } catch (Exception e2){
            e2.printStackTrace();
        }
    }
}
