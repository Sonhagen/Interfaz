package JSONParser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import eMail.Mail;

public class GetAsync extends AsyncTask<Object, String, JSONObject> {

    JSONParser jsonParser;
    Mail SeMail;
    //private static final String LOGIN_URL = "http://10.19.4.93:8080/webservice/index.php";
    //http://192.168.1.73/WebServiceJson.asmx/GetJSON?Tipo=3&WCP=WCP733140
    private String LOGIN_URL = "";

    public AsyncResponse delegate = null;

    public GetAsync(AsyncResponse asyncResponse, Context context) {
        delegate = asyncResponse;
        SeMail = new Mail(context);
        jsonParser = new JSONParser(context);
    }

    @Override
    protected JSONObject doInBackground(Object... map) {
        JSONObject json = null;
        try {
            HashMap<String, String> params = (HashMap<String, String>) map[0];
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, String> entry = iterator.next();
                if (entry.getKey().equals("URL")){
                    LOGIN_URL = entry.getValue();
                    iterator.remove();
                }
            }
            Log.d("request", "starting");
            json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);
            if (json != null) {
                Log.d("JSON result", json.toString());
            }
        } catch (Exception e) {
            SendError(e, "Error. doInBackground GetAsync");
        }
        return json;
    }

    protected void onPostExecute(JSONObject json) {
        if (json != null) {
            Log.d("Success!", json.toString());
            delegate.processFinish(json);
        } else {
            Log.d("Failure", json.toString());
        }
    }

    public void SendError(Exception e, String Subject){
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
}