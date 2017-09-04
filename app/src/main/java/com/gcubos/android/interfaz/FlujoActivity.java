package com.gcubos.android.interfaz;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import DataBase.DataDB;
import JSONParser.AsyncResponse;
import JSONParser.GetAsync;
import eMail.Mail;

public class FlujoActivity extends AppCompatActivity {
    DataDB DB;
    Mail SeMail;
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
    final FlujoActivity.Holder<String> mutableMessage = new FlujoActivity.Holder<String>("");
    private String usuario = "", Todo="";
    private boolean bDesdeTexto = false;

    private void habilitaBotones(boolean Estado){
        //Habilita o deshabilita bortones
        ImageButton bscan = (ImageButton) findViewById(R.id.ScanBtn);
        bscan.setEnabled(Estado);
        if (!Estado)
            fadeOut(bscan);
        else
            fadeIn(bscan);
    }

    private void fadeIn(View bView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bView, "alpha", 0f, 1f);
        objectAnimator.setDuration(500L);
        objectAnimator.addListener(new AnimatorListenerAdapter() { });
        objectAnimator.start();
    }

    private void fadeOut(View bView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bView, "alpha", 1f, 0f);
        objectAnimator.setDuration(500L);
        objectAnimator.addListener(new AnimatorListenerAdapter() { });
        objectAnimator.start();
    }

    public void onClickScan(View view) {
        TableLayout gridview = (TableLayout) findViewById(R.id.Grid);
        gridview.removeAllViews();
        habilitaBotones(false);
        if (!bDesdeTexto) {
            EditText contentTxt = (EditText) findViewById(R.id.ScanFlujo);
            contentTxt.setText("");
            //Integra Scanner
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        bDesdeTexto=false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try{
            //Inicia integrador con Barcode scanner y captura el resultado
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            EditText contentTxt = (EditText)findViewById(R.id.ScanFlujo);
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
            SendError(e, "Error. onActivityResult FlujoActivity");
        }
    }

    private void RecuperaDatos(String scanContent){
        EditText contentTxt = (EditText)findViewById(R.id.ScanFlujo);
        //Hashmap paramentros para el WebService
        HashMap<String, String> HP = new HashMap<>();
        //Asigna WCP escaneada a TextView en pantalla
        contentTxt.setText(scanContent);
        if (Todo.equals("SI"))
            HP.put("URL",DB.getURL() + DB.getFLWCP());
        else
            HP.put("URL",DB.getURL() + DB.getFWACT());

        HP.put("WCP",contentTxt.getText().toString());
        HP.put("Usuario",usuario);
        //Inicia actividad asincrona web service
        GetAsync async = (GetAsync) new GetAsync(new AsyncResponse() {
            @Override
            public void processFinish(JSONObject json) {
                //Limpia Holder para rescatar dato de clase anonima
                mutableMessage.setValue("");
                EditText contentTxt = (EditText)findViewById(R.id.ScanFlujo);
                if (!json.toString().contains("Vacio")) {
                    //Asigna dato de a clase anonima
                    mutableMessage.setValue(json.toString());
                    contentTxt.setError(null);
                    Despliega_Datos(json);
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

    private void Datos_Textos(JSONObject JOE1, TableLayout gridview){
        TextView TxtFEnt = (TextView)findViewById(R.id.FechaEnt);
        TextView TxtFCap = (TextView)findViewById(R.id.FechaCap);
        TextView TxtLot = (TextView)findViewById(R.id.Lote);
        TextView TxtPed = (TextView)findViewById(R.id.Pedido);
        TextView TxtOP = (TextView)findViewById(R.id.OP);
        TextView TxtWCP = (TextView)findViewById(R.id.WCP);
        TextView TxtAncho = (TextView)findViewById(R.id.Ancho);
        TextView TxtAlto = (TextView)findViewById(R.id.Alto);
        TxtFEnt.setText("Fecha Entrega:");
        TxtFCap.setText("Fecha Captura:");
        TxtLot.setText("Lote:");
        TxtPed.setText("Pedido:");
        TxtOP.setText("OP:");
        TxtWCP.setText("WCP:");
        TxtAncho.setText("Ancho:");
        TxtAlto.setText("Alto:");
        try {
            TxtFEnt.setText(TxtFEnt.getText().toString() + ' ' + JOE1.getString("@FechaEntrega").substring(0, 10));
            TxtFCap.setText(TxtFCap.getText().toString() + ' ' + JOE1.getString("@FechaCapt").substring(0, 10));
            TxtLot.setText(TxtLot.getText().toString() + ' ' + JOE1.getString("@LoteExe"));
            TxtPed.setText(TxtPed.getText().toString() + ' ' + JOE1.getString("@Pedido"));
            TxtOP.setText(TxtOP.getText().toString() + ' ' + JOE1.getString("@OP"));
            TxtWCP.setText(TxtWCP.getText().toString() + ' ' + JOE1.getString("@WCP"));
            TxtAncho.setText(TxtAncho.getText().toString() + ' ' + JOE1.getString("@Ancho"));
            TxtAlto.setText(TxtAlto.getText().toString() + ' ' + JOE1.getString("@Alto"));
            EncabezadoGrid(gridview);
        }catch (JSONException e){
            SendError(e, "Error. JSONException Datos_Textos FlujoActivity");
        }catch (Exception e) {
            SendError(e, "Error. Datos_Textos FlujoActivity");
        }
    }

    private void Despliega_Datos(JSONObject json){
        JSONObject JOE0, JOE1;
        JSONArray JOE;

        TableLayout gridview = (TableLayout) findViewById(R.id.Grid);
        try {
            JOE0 = json.getJSONObject("Flujo");
            Object JObjArr = JOE0.get("Proceso");
            gridview.removeAllViews();
            if (JObjArr instanceof JSONArray) {
                JOE = (JSONArray) JObjArr;
                for (int i = 0; i < JOE.length(); i++) {
                    JOE1 = JOE.getJSONObject(i);
                    if (i==0) {
                        Datos_Textos(JOE1, gridview);
                    }
                    Hashtable<Integer, String> Textos = new Hashtable<>();
                    Textos.put(0,JOE1.getString("@Proceso"));
                    Textos.put(1,JOE1.getString("@Fecha_Inicio"));
                    Textos.put(2,JOE1.getString("@Fecha_Fin"));
                    AddGridRow(gridview, Textos);
                }
            } else if (JObjArr instanceof JSONObject){
                JOE1 =  (JSONObject) JObjArr;
                Datos_Textos(JOE1, gridview);
                Hashtable<Integer, String> Textos = new Hashtable<>();
                Textos.put(0,JOE1.getString("@Proceso"));
                Textos.put(1,JOE1.getString("@Fecha_Inicio"));
                Textos.put(2,JOE1.getString("@Fecha_Fin"));
                AddGridRow(gridview, Textos);
            }
        }catch (JSONException e){
            SendError(e, "Error. JSONException Despliega_Datos FlujoActivity");
        }catch (Exception e) {
            SendError(e, "Error. Despliega_Datos FlujoActivity");
        }
    }

    private void AddGridRow(TableLayout gridview, Hashtable<Integer, String> Textos){
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(1, 1, 1, 1);//2px right-margin
        row.setLayoutParams(lp);
        for (int i = 0; i<=Textos.size(); i++) {
            LinearLayout cell = new LinearLayout(this);
            cell.setBackgroundColor(Color.LTGRAY);
            cell.setLayoutParams(lp);//2px border on the right for the cell
            TextView tv = new TextView(this);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(20, 0, 20, 10);
            tv.setText(Textos.get(i));
            cell.addView(tv);
            row.addView(cell);
        }
        gridview.addView(row);
    }

    private void EncabezadoGrid(TableLayout gridview){
        Hashtable<Integer, String> Textos = new Hashtable<>();
        Textos.put(0,"Proceso");
        Textos.put(1,"Fecha Inicio");
        Textos.put(2,"Fecha Fin");
        AddGridRow(gridview, Textos);
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
            setContentView(R.layout.activity_flujo);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Todo = extras.getString("Todo");
            }
            //Si la WCP es digitada
            EditText contentTxt = (EditText) findViewById(R.id.ScanFlujo);
            contentTxt.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        EditText contentTxt = (EditText) findViewById(R.id.ScanFlujo);
                        bDesdeTexto=true;
                        onClickScan(v);
                        RecuperaDatos(contentTxt.getText().toString());
                        bDesdeTexto=false;
                        return true;
                    }
                    return false;
                }
            });
        }catch (Exception e){
            SendError(e, "Error. onCreate FlujoActivity");
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

