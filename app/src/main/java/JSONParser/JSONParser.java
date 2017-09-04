package JSONParser;

import eMail.Mail;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class JSONParser {
    Mail SeMail;
    private HttpURLConnection conn;
    private StringBuilder result;
    private JSONObject jObj = null;
    private int cConTimeOUT = 15000;
    private int cReaTimeOUT = 10000;

    public JSONParser(Context context){
        SeMail= new Mail(context);
    }

    public JSONObject makeHttpRequest(String url, String method, HashMap<String, String> params) {
        StringBuilder sbParams;

        sbParams = new StringBuilder();
        int i = 0;
        String charset = "UTF-8";
        for (String key : params.keySet()) {
            try {
                if (i != 0){
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                SendError(e, "Error. JSONParser makeHttpRequest");
            }
            i++;
        }
        URL urlObj;
        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setReadTimeout(cReaTimeOUT);
                conn.setConnectTimeout(cConTimeOUT);
                conn.connect();
                String paramsString = sbParams.toString();
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                SendError(e, "Error. IOException POST JSONParser makeHttpRequest");
            }
        }
        else if(method.equals("GET")){
            // request method is GET
            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }
            try {
                urlObj = new URL(url);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setConnectTimeout(cConTimeOUT);
                conn.connect();
            } catch (IOException e) {
                SendError(e, "Error. IOException GET JSONParser makeHttpRequest");
            }
        }
        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("JSON Parser", "result: " + result.toString());
        } catch (IOException e) {
            SendError(e, "Error. IOException JSONParser makeHttpRequest");
        }
        conn.disconnect();
        // try parse the string to a JSON object
        try {
            if (!result.toString().isEmpty())
                jObj = new JSONObject(result.toString());
            else
                jObj = new JSONObject("{\"Vacio\":\"Vacio\"}");
        } catch (JSONException e) {
            SendError(e, "Error. IOException JSONParser makeHttpRequest");
        }
        // return JSON Object
        return jObj;
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
}