package com.gcubos.android.interfaz;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import DataBase.DataDB;
import JSONParser.AsyncResponse;
import JSONParser.GetAsync;
import eMail.Mail;
import layout.CroquisDrawFragment;
import layout.Croquis_fragment;

public class Croquis extends AppCompatActivity implements CroquisDrawFragment.OnFragmentInteractionListener {
    Mail SeMail;
    DataDB DB;
    private String usuario = "";
    private boolean bDesdeTexto = false;
    class Holder<T> {
        //Holder de clases anonimas en respuestas asincronas
        private T value;
        Holder(T value) {
            setValue(value);
        }
        T getValue() {
            return value;
        }
        void setValue(T value) {
            this.value = value;
        }
    }
    final Croquis.Holder<String> mutableMessage = new Croquis.Holder<String>("");

    private void habilitaBotones(boolean Estado){
        //Habilita o deshabilita bortones
        ImageButton bscan = (ImageButton) findViewById(R.id.ScanCroq);
        bscan.setEnabled(Estado);
        if (!Estado){
            fadeOut(bscan);
        }
        else{
            fadeIn(bscan);
        }
    }

    private void fadeIn(View bView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bView, "alpha", 0f, 1f);
        objectAnimator.setDuration(500L);
        objectAnimator.addListener(new AnimatorListenerAdapter() {

        });            /*
            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOut();
            }*/
        objectAnimator.start();
    }

    private void fadeOut(View bView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bView, "alpha", 1f, 0f);
        objectAnimator.setDuration(500L);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            /*@Override
            public void onAnimationEnd(Animator animation) {
                fadeIn();
            }*/
        });
        objectAnimator.start();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            //Inicia integrador con Barcode scanner y captura el resultado
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            EditText contentTxt = (EditText)findViewById(R.id.ScanWCP);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                RecuperaDatos(scanContent);
            }
            else{
                //Habilita botones al terminar actividad asincrona
                habilitaBotones(true);
                //Error no hay datos escaneados
                //Toast.makeText(getApplicationContext(), R.string.NoData, Toast.LENGTH_SHORT).show();
                contentTxt.setError(getText(R.string.NoData));
            }
        }catch (Exception e){
            SendError(e, "Error. onActivityResult Croquis");
        }
    }

    private void RecuperaDatos(String scanContent){
        EditText contentTxt = (EditText)findViewById(R.id.ScanWCP);
        //Hashmap paramentros para el WebService
        HashMap<String, String> HP = new HashMap<>();
        //Asigna WCP escaneada a TextView en pantalla
        contentTxt.setText(scanContent);
        HP.put("URL",DB.getURL() + DB.getCROQ());
        HP.put("WCP",contentTxt.getText().toString());
        HP.put("Usuario",usuario);
        //Inicia actividad asincrona web service
        GetAsync async = (GetAsync) new GetAsync(new AsyncResponse() {
            @Override
            public void processFinish(JSONObject json) {
                //Limpia Holder para rescatar dato de clase anonima
                mutableMessage.setValue("");
                EditText contentTxt = (EditText)findViewById(R.id.ScanWCP);
                if (!json.toString().contains("Vacio")) {
                    //Asigna dato de a clase anonima
                    mutableMessage.setValue(json.toString());
                    contentTxt.setError(null);
                    Despliega_Datos();
                }else {
                    //Toast.makeText(getApplicationContext(), R.string.NoData, Toast.LENGTH_SHORT).show();
                    //Muestra error en WCP llego vacia o no existe
                    contentTxt.setError(getText(R.string.NoData));
                    //Limpia campo WCP llego vacia o no existe
                    contentTxt.setText("");
                }
                //Habilita botones al terminar actividad asincrona
                habilitaBotones(true);
            }
        },getApplicationContext()).execute(HP);
    }

    public void Despliega_Datos(){
        try{
            EditText contentTxt = (EditText) findViewById(R.id.ScanWCP);
            if (contentTxt.getText().length()>0) {
                FragmentTransaction fragmentTransaction;
                //Instancia Fragment y despliega los croquis
                Croquis_fragment fragment = Croquis_fragment.newInstance(mutableMessage.getValue());
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //Remplaza Fragment principal con lista de croquis
                fragmentTransaction.replace(R.id.fragment_Croquis, fragment);
                fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //Agrega BackStack
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }else{
                Toast.makeText(getApplicationContext(), R.string.NoWCP, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            SendError(e, "Error. Despliega_Datos Croquis");
        }
    }

    public void onClickCroq(View view) {
        //Backstack todos los fragmentos
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) { fm.popBackStack(); }
        habilitaBotones(false);
        if (!bDesdeTexto) {
            EditText contentTxt = (EditText) findViewById(R.id.ScanWCP);
            contentTxt.setText("");
            //Integra Scanner
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        bDesdeTexto=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB = new DataDB(getApplicationContext());
        SeMail = new Mail(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croquis);
    }

    private void SendError(Exception e, String Subject){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        SeMail.setSubject(Subject);
        SeMail.setBody(errors.toString());
        try {
            SeMail.send();
        }catch (Exception e2){
            e2.printStackTrace();
        }
    }

    public void onFragmentInteractionCF(Uri uri){

    }

    private void BackStackcall(){
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //Ultimo elemento en el BackStack pregunta antes de salir
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            //AlertDialog para validar la salida
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.ExitTlt))
                    .setMessage(getString(R.string.ExitCon))
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        //Clase anonima para validar la respuesta positiva
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BackStackcall();
                        }
                    })
                    //Si es respuesta negativa cierra
                    .setNegativeButton("No", null)
                    .show();
        }else{
            //Si no es el ultimo en el BackStack, para a super con BackPressed
            BackStackcall();
        }
    }

}
