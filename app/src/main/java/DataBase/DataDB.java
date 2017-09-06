package DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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
    private String _root;
    private String _Droot;

    public DataDB(Context context) {
        _context = context;
        CargaDatos(_context);
    }

    private void setURL(String _sURL) {
        this._URL = _sURL;
    }

    private void setRoot(String _sroot) {
        this._root = _sroot;
    }

    private void setDRoot(String _sDroot) {
        this._Droot = _sDroot;
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

    public String getURL(){
        return _URL;
    }

    public String getRoot(){
        return _root;
    }

    public String getDRoot(){
        return _Droot;
    }

    public String geteDest(){
        return _eDest;
    }

    public String getLogIn(){
        return _LogIn;
    }

    public String getTipos(){
        return _Tipos;
    }

    public String getEtiTip(){
        return _EtiTip;
    }

    public String getSMTP(){
        return _SMTP;
    }

    public String getUSER(){
        return _USER;
    }

    public String getPASS(){
        return _PASS;
    }

    public String getPORT(){
        return _PORT;
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
                Cursor cur = db.rawQuery("select CAMPO, VALOR, VALORE from Params", null);
                cur.moveToFirst();
                while (!cur.isAfterLast()) {
                    String strCampo = cur.getString(cur.getColumnIndex("CAMPO"));
                    switch (strCampo) {
                        case "URL":
                            this.setURL(cur.getString(cur.getColumnIndex("VALOR")));
                            break;
                        case "MESG":
                            this.setRoot(cur.getString(cur.getColumnIndex("VALOR")));
                            this.setDRoot(cur.getString(cur.getColumnIndex("VALORE")));
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
