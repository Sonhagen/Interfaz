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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import DataBase.DataDB;
import JSONParser.AsyncResponse;
import JSONParser.GetAsync;
import layout.Button1;
import layout.Button2;
import layout.fragment_datos_generales;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import eMail.Mail;

public class MainActivity extends AppCompatActivity implements Button2.OnFragmentInteractionListener, fragment_datos_generales.OnFragmentInteractionListener {

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
    final Holder<String> mutableMessage = new Holder<String>("");
    DataDB DB;
    Mail SeMail;
    private boolean userIsInteracting;
    private String usuario = "";
    private boolean bDesdeTexto = false;

    private void habilitaBotones(boolean Estado){
        //Habilita o deshabilita bortones
        ImageButton bscan = (ImageButton) findViewById(R.id.button1);
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DB = new DataDB(getApplicationContext());
        SeMail = new Mail(getApplicationContext());
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //Si la WCP es digitada
            EditText contentTxt = (EditText) findViewById(R.id.EditTScan);
            contentTxt.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        EditText contentTxt = (EditText) findViewById(R.id.EditTScan);
                        bDesdeTexto=true;
                        onClickButton1(v);
                        RecuperaDatos(contentTxt.getText().toString());
                        bDesdeTexto=false;
                        return true;
                    }
                    return false;
                }
            });

            //Recupera usuario de actividad LogIn
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                usuario = extras.getString("Usuario");
            }
            //Deshabilita botones hasta que termine actividad asincrona
            habilitaBotones(false);
            //Inicializa Hashmap de paramentros
            HashMap<String, String> HP = new HashMap<>();
            HP.put("URL", DB.getURL() + DB.getTipos());
            HP.put("Tipo", "3");
            //Inicia servicio asincrono con HP paramentros el primero siempre es URL WebService
            GetAsync async = (GetAsync) new GetAsync(new AsyncResponse() {
                //Sobrecarga de clase anonima para individualizar la salida del proceso asincrono
                @Override
                public void processFinish(JSONObject json) {
                    JSONObject JTipo;
                    JSONArray JRenglon;
                    //Lista para el spinner de tipos de proceso
                    List<String> list = new ArrayList<String>();
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    //Desglose de JSON con tipos de proceso
                    try {
                        JTipo = json.getJSONObject("Tipos");
                        JRenglon = JTipo.getJSONArray("renglones");
                        list.add("");
                        for (int i = 0; i < JRenglon.length(); i++) {
                            JSONObject JOE = JRenglon.getJSONObject(i);
                            list.add(JOE.getString("@Tipo") + " - " + JOE.getString("@Proceso"));
                        }
                        //Inicia adaptador con layout de spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, list);
                        spinner.setAdapter(dataAdapter);
                        //habilita botones al terminar actividad asincrona
                        habilitaBotones(true);
                    } catch (JSONException e) {
                        Log.e("Error JSON -> ", e.getMessage());
                        e.printStackTrace();
                    }
                }
            },getApplicationContext()).execute(HP);

            //Limpiar campos formulario al cambiar la seleccion del spinner
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //Limpia solo si hay interaccion de usuario
                    if (userIsInteracting) {
                        //Limpia Campo WCP
                        EditText contentTxt = (EditText) findViewById(R.id.EditTScan);
                        contentTxt.setText("");
                        //Limpia Estado
                        TextView TVEstado = (TextView) findViewById(R.id.TVEstado);
                        TVEstado.setText("");
                        //Backstack todos los fragmentos
                        FragmentManager fm = getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });
        }catch (Exception e){
            SendError(e, "Error. onCreate MainActivity");
        }
    }

    public void onClickButton1(View view){
        try{
            if (!bDesdeTexto) {
                //Limpia Campo WCP
                EditText contentTxt = (EditText) findViewById(R.id.EditTScan);
                contentTxt.setText("");
            }
            //Limpia Estado
            TextView TVEstado = (TextView) findViewById(R.id.TVEstado);
            TVEstado.setText("");
            //Backstack todos los fragmentos
            FragmentManager fm = getSupportFragmentManager();
            for(int i = 0; i < fm.getBackStackEntryCount(); ++i) { fm.popBackStack(); }
            //Valida Spinner
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            if(!spinner.getSelectedItem().toString().trim().equals("") ) {
                //Deshabilita botones antes de actividad asincrona
                habilitaBotones(false);
                if (!bDesdeTexto) {
                    //Integra Scanner
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();
                }
            }else {
                //No hay proceso
                Toast.makeText(getApplicationContext(),R.string.SelProc, Toast.LENGTH_SHORT).show();
            }
            bDesdeTexto=false;
        }catch (Exception e){
            SendError(e, "Error. onClickButton1 MainActivity");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            //Inicia integrador con Barcode scanner y captura el resultado
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            EditText contentTxt = (EditText)findViewById(R.id.EditTScan);
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
            SendError(e, "Error. onActivityResult MainActivity");
        }
    }

    private void RecuperaDatos(String scanContent){
        EditText contentTxt = (EditText)findViewById(R.id.EditTScan);
        //Hashmap paramentros para el WebService
        HashMap<String, String> HP = new HashMap<>();
        //Deshabilita botones antes de actividad asincrona
        //habilitaBotones(false);
        //spinner y editview para los paramentros en HP
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        //Asigna WCP escaneada a TextView en pantalla
        contentTxt.setText(scanContent);
        HP.put("URL",DB.getURL() + DB.getEtiTip());
        HP.put("WCP",contentTxt.getText().toString());
        HP.put("Tipo",spinner.getSelectedItem().toString().substring(0,1));
        HP.put("Usuario",usuario);
        //Inicia actividad asincrona web service
        GetAsync async = (GetAsync) new GetAsync( new AsyncResponse() {
            @Override
            public void processFinish(JSONObject json) {
                //Limpia Holder para rescatar dato de clase anonima
                mutableMessage.setValue("");
                EditText contentTxt = (EditText)findViewById(R.id.EditTScan);
                if (!json.toString().contains("Vacio")) {
                    //Asigna dato de a clase anonima
                    mutableMessage.setValue(json.toString());
                    contentTxt.setError(null);
                    ImageButton IBtn = (ImageButton)findViewById(R.id.button2);
                    onClickButton2(IBtn);
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

    public void onClickButton2(View view){
        JSONObject JOEtiquetas;
        try{
            TextView TVEstado = (TextView) findViewById(R.id.TVEstado);
            EditText contentTxt = (EditText) findViewById(R.id.EditTScan);
            if (contentTxt.getText().length()>0) {
                try {
                    JSONObject Json = new JSONObject(mutableMessage.getValue());
                    JOEtiquetas = Json.getJSONObject("Etiquetas");
                    //Valida banderas de Flujo correcto
                    if (JOEtiquetas.getString("@estado").equals("OK")) {
                        TVEstado.setText(JOEtiquetas.getString("@mensaje"));
                        FragmentTransaction fragmentTransaction;
                        //Instancia Fragment y despliega las etiquetas
                        Button1 fragment = Button1.newInstance(mutableMessage.getValue());
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        //Remplaza Fragment principal con lista de etiquetas
                        fragmentTransaction.replace(R.id.sample_content_fragment, fragment);
                        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        //Agrega BackStack
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }else{
                        //Muestra error de flujo
                        Toast.makeText(getApplicationContext(), JOEtiquetas.getString("@mensaje"), Toast.LENGTH_LONG).show();
                        TVEstado.setText("");
                    }
                } catch (JSONException e){
                    Log.e("Error JSON -> ",e.getMessage());
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.NoWCP, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            SendError(e, "Error. onClickButton2 MainActivity");
        }
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

    public void onFragmentInteractionB2(Uri uri){

    }

    public void onFragmentInteractionDG(Uri uri){

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
                    .setMessage(getString(R.string.ExitEsc))
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
