package DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.gcubos.android.interfaz.R;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import Secure.AESHelper;

public class DataDB {
    private Context _context;
    private String _URL;
    private String _SMTP;
    private String _USER;
    private String _PASS;
    private String _PORT;
    private String _EtiTip;
    private String _LogIn;
    private String _Tipos;
    private String _eDest;
    private String _FLWCP;
    private String _FWACT;
    private String _CROQ;
    AESHelper Secure;

    public DataDB(Context context) {
        _context = context;
        Secure = new AESHelper();
        CargaDatos(_context);
    }

    private void setFWACT(String _sFWACT) {
        this._FWACT = _sFWACT;
    }

    private void setCROQ(String _sCROQ) {
        this._CROQ = _sCROQ;
    }

    private void setFLWCP(String _sFLWCP) {
        this._FLWCP = _sFLWCP;
    }

    private void setURL(String _sURL) {
        this._URL = _sURL;
    }

    private void setEtiTip(String _sEtiTip) {
        this._EtiTip = _sEtiTip;
    }

    private void setLogIn(String _sLogIn) {
        this._LogIn = _sLogIn;
    }

    private void setTipos(String _sTipos) {
        this._Tipos = _sTipos;
    }

    private void setSMTP(String _sSMTP) {
        this._SMTP = _sSMTP;
    }

    private void setUSER(String _sUSER) {
        this._USER = _sUSER;
    }

    private void setPASS(String _sPASS) {
        this._PASS = _sPASS;
    }

    private void setPORT(String _sPORT) {
        this._PORT = _sPORT;
    }

    private void seteDest(String _seDest) {
        this._eDest = _seDest;
    }

    public String getFWACT(){
        return Secure.decrypt(_FWACT,_context.getString(R.string.HD));
    }

    public String getCROQ(){
        return Secure.decrypt(_CROQ,_context.getString(R.string.HD));
    }

    public String getFLWCP(){
        return Secure.decrypt(_FLWCP,_context.getString(R.string.HD));
    }

    public String getURL(){
        return Secure.decrypt(_URL,_context.getString(R.string.HD));
    }

    public String geteDest(){
        return Secure.decrypt(_eDest,_context.getString(R.string.HD));
    }

    public String getLogIn(){
        return Secure.decrypt(_LogIn,_context.getString(R.string.HD));
    }

    public String getTipos(){
        return Secure.decrypt(_Tipos,_context.getString(R.string.HD));
    }

    public String getEtiTip(){
        return Secure.decrypt(_EtiTip,_context.getString(R.string.HD));
    }

    public String getSMTP(){
        return Secure.decrypt(_SMTP,_context.getString(R.string.HD));
    }

    public String getUSER(){
        return Secure.decrypt(_USER,_context.getString(R.string.HD));
    }

    public String getPASS(){
        return Secure.decrypt(_PASS,_context.getString(R.string.HD));
    }

    public String getPORT(){
        return Secure.decrypt(_PORT,_context.getString(R.string.HD));
    }

    private void CargaDatos(Context context){
        DatabaseHandler Conn;

        Conn = new DatabaseHandler(context);
        try{
            Conn.createDataBase();
        }catch (IOException e){
            SendError(e, "DataDB createDataBase CargaDatos");
        }
        try {
            if (Conn.checkDataBase()) {
                Conn.openDataBase();
                SQLiteDatabase db = Conn.getReadableDatabase();
                Cursor cur = db.rawQuery("select CAMPO, VALOR from Params", null);
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    String strCampo = cur.getString(cur.getColumnIndex("CAMPO"));
                    switch (strCampo) {
                        case "URL":
                            this.setURL(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "ETITIP":
                            this.setEtiTip(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "LOGIN":
                            this.setLogIn(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "TIPOS":
                            this.setTipos(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "SMTP":
                            this.setSMTP(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "PORT":
                            this.setPORT(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "USER":
                            this.setUSER(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "PASS":
                            this.setPASS(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "eDEST":
                            this.seteDest(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "FLWCP":
                            this.setFLWCP(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "FWACT":
                            this.setFWACT(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "CROQ":
                            this.setCROQ(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                    }
                    cur.moveToNext();
                }
                cur.close();
                db.close();
                Conn.close();
            } else {
                Conn.close();
            }
        }catch(Exception e){
            SendError(e, "DataDB CargaDatos");
        }
    }

    public void Actualiza(String Campo, String Valor){
        DatabaseHandler Conn;

        Conn = new DatabaseHandler(_context);
        try{
            Conn.createDataBase();
        }catch (IOException e){
            SendError(e, "DataDB createDataBase CargaDatos");
        }
        try {
            if (Conn.checkDataBase()) {
                Conn.openDataBase();
                SQLiteDatabase db = Conn.getWritableDatabase();
                String strQuery = "UPDATE Params SET VALORE = '" + Valor + "' WHERE CAMPO='" + Campo + "'";
                db.execSQL(strQuery);
                db.close();
                Conn.close();
            } else {
                Conn.close();
            }
        }catch(Exception e){
            Conn.close();
            SendError(e, "DataDB CargaDatos");
        }
    }

    private void SendError(Exception e, String Subject){
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        Log.e(Subject, errors.toString());
    }
}
