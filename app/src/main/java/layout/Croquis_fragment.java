package layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import com.gcubos.android.interfaz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import eMail.Mail;

public class Croquis_fragment extends ListFragment implements OnItemClickListener  {
    Mail SeMail;
    ListaAdapt adapt;
    private static final String ARG_PARAM1 = "cadena";
    private String mParam1;
    public static ArrayList<String> Etiquetas = new ArrayList<String>();

    public Croquis_fragment() {
        // Required empty public constructor
    }

    public static Croquis_fragment newInstance(String param1) {
        //Recupera paramentros enviados
        Croquis_fragment fragment = new Croquis_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        JSONArray JOArr;
        JSONObject JOCroquis, JOE;
        Object JObjArr;

        super.onActivityCreated(savedInstanceState);
        SeMail = new Mail(getActivity().getApplicationContext());

        try {
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                JSONObject Json = new JSONObject(mParam1);
                JOCroquis = Json.getJSONObject("Procesa");
                JObjArr = JOCroquis.get("Croquis");
                Etiquetas.clear();
                if (JObjArr instanceof JSONArray) {
                    JOArr = (JSONArray) JObjArr;
                    for (int i = 0; i < JOArr.length(); i++) {
                        JOE = JOArr.getJSONObject(i);
                        if (!Etiquetas.contains(JOE.getString("@Peca"))){
                            Etiquetas.add(JOE.getString("@Peca"));
                        }
                    }
                } else if (JObjArr instanceof JSONObject){
                    JOE = (JSONObject) JObjArr;
                    if (!Etiquetas.contains(JOE.getString("@Peca"))){
                        Etiquetas.add(JOE.getString("@Peca"));
                    }
                }
                adapt = new ListaAdapt(getActivity(), Etiquetas);
                //Agrega ListView
                setListAdapter(adapt);
                getListView().setOnItemClickListener(this);
            }
        }catch (JSONException e){
            SendError(e, "JSONException. onActivityCreated Croquis_fragment");
        } catch (Exception e) {
            SendError(e, "Error. onActivityCreated Croquis_fragment");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction fragmentTransaction;
        Fragment fragment;

        //Muestra Toast con item seleccionado
        Toast.makeText(getActivity(), Etiquetas.get(position), Toast.LENGTH_SHORT).show();
        //Recorre Json para encontrar los renglones de la etiqueta seleccionada
        fragment = CroquisDrawFragment.newInstance(mParam1, Etiquetas.get(position));

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        //fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //Remplaza Fragment con detalle
        fragmentTransaction.replace(R.id.fragment_Croquis, fragment);

        //Agrega BackStack
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_croquis, container, false);
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
