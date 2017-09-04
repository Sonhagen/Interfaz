package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gcubos.android.interfaz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import eMail.Mail;

public class Button1 extends ListFragment  implements OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    Mail SeMail;
    private static final String ARG_PARAM1 = "cadena";
    ListaAdapt adapt;
    // TODO: Rename and change types of parameters
    private String mParam1;

    public static ArrayList<String> Etiquetas = new ArrayList<String>();

    public Button1() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Button1 newInstance(String param1) {
        //Recupera paramentros enviados
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        Button1 fragment = new Button1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SeMail = new Mail(getActivity().getApplicationContext());

        //Limpia arreglo de etiquetas
        Etiquetas.clear();
        String strCadena = getArguments().getString("cadena");
        try {
            //Recorre Json para obtener los nombres de las etiquetas y anexarlos a la lista Etiquetas
            JSONArray JOEtiqueta;
            JSONObject JOEtiquetas, JOE;
            String strNombre = "";
            JSONObject Json = new JSONObject(strCadena);
            JOEtiquetas = Json.getJSONObject("Etiquetas");
            Object JObjArr = JOEtiquetas.get("Etiqueta");
            Etiquetas.add("Datos Generales");
            if (JObjArr instanceof JSONArray) {
                JOEtiqueta =  (JSONArray) JObjArr;
                for (int i = 0; i < JOEtiqueta.length(); i++) {
                    JOE = JOEtiqueta.getJSONObject(i);
                    strNombre = JOE.getString("@nombre");
                    Etiquetas.add(strNombre);
                }
            } else if (JObjArr instanceof JSONObject){
                JOE =  (JSONObject) JObjArr;
                strNombre = JOE.getString("@nombre");
                Etiquetas.add(strNombre);
            }
            //Agrega lista Etiquetas al adaptador de listas
            adapt = new ListaAdapt(getActivity(), Etiquetas);
            //Agrega ListView
            setListAdapter(adapt);
            getListView().setOnItemClickListener(this);
        }catch (JSONException e){
            SendError(e, "Error. JSONException onActivityCreated Button1");
        }catch (Exception e) {
            SendError(e, "Error. onActivityCreated Button1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_button1, container, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction fragmentTransaction;
        JSONArray JOEtiqueta;
        JSONObject JOEtiquetas, JOE = null;
        String strJson = "";
        Fragment fragment;

        //Al seleccionar un item de la lista
        String strCadena = getArguments().getString("cadena");
        //Muestra Toast con item seleccionado
        Toast.makeText(getActivity(), Etiquetas.get(position), Toast.LENGTH_SHORT).show();
        //Recorre Json para encontrar los renglones de la etiqueta seleccionada
        try {
            JSONObject Json = new JSONObject(strCadena);
            JOEtiquetas = Json.getJSONObject("Etiquetas");
            Object JObjArr = JOEtiquetas.get("Etiqueta");
            if (JObjArr instanceof JSONArray) {
                JOEtiqueta = JOEtiquetas.getJSONArray("Etiqueta");
                for (int i = 0; i < JOEtiqueta.length(); i++) {
                    JOE = JOEtiqueta.getJSONObject(i);
                    if (Etiquetas.get(position).equals(JOE.getString("@nombre"))){
                        strJson = JOE.toString();
                        break;
                    }
                }
            } else if (JObjArr instanceof JSONObject){
                JOE =  (JSONObject) JObjArr;
                if (Etiquetas.get(position).equals(JOE.getString("@nombre"))){
                    strJson = JOE.toString();
                }
            }

            if (Etiquetas.get(position).equals("Datos Generales")) {
                //Instancia Fragmento con datos generales de la WCP
                fragment = fragment_datos_generales.newInstance(JOEtiquetas.toString());
            }else{
                //Instancia Fragmento con detalle para las etiquetas
                fragment = Button2.newInstance(strJson);
            }
            fragmentTransaction = getFragmentManager().beginTransaction();
            //fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            //Remplaza Fragment con detalle
            fragmentTransaction.replace(R.id.sample_content_fragment, fragment);
            //Agrega BackStack
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (JSONException e) {
            SendError(e, "Error. JSONException onItemClick Button1");
        } catch (Exception e) {
            SendError(e, "Error. onItemClick Button1");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
