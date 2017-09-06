package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gcubos.android.interfaz.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import eMail.Mail;

public class fragment_datos_generales extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "cadena";
    Mail SeMail;
    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public fragment_datos_generales() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static fragment_datos_generales newInstance(String param1) {
        fragment_datos_generales fragment = new fragment_datos_generales();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        SeMail = new Mail(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        JSONObject JOE;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_datos_generales, container, false);
        //Despliga datos generales
        try {
            JSONObject Json = new JSONObject(getArguments().getString("cadena"));
            TextView TxVOP = (TextView) view.findViewById(R.id.TV_OP);
            TextView TxVLote = (TextView) view.findViewById(R.id.TV_Lote);
            TextView TxVPedido = (TextView) view.findViewById(R.id.TV_Pedido);
            TextView TxVFC = (TextView) view.findViewById(R.id.TV_FCap);
            TextView TxVFL = (TextView) view.findViewById(R.id.TV_Flle);
            TextView TxVMarca = (TextView) view.findViewById(R.id.TV_Marca);
            TextView TxVFamilia = (TextView) view.findViewById(R.id.TV_fam);
            TextView TxVLinea = (TextView) view.findViewById(R.id.TV_Line);
            TextView TxVCol = (TextView) view.findViewById(R.id.TV_Col);
            TextView TxVAcc = (TextView) view.findViewById(R.id.TV_Acc);
            TextView TxVCon = (TextView) view.findViewById(R.id.TV_Cont);
            TextView TxVColor = (TextView) view.findViewById(R.id.TV_Color);
            TextView TxVAncho = (TextView) view.findViewById(R.id.TV_Ancho);
            TextView TxVAlto = (TextView) view.findViewById(R.id.TV_Alto);
            TxVOP.setText(Json.getString("@OP"));
            TxVLote.setText(Json.getString("@LoteExe"));
            TxVPedido.setText(Json.getString("@SalesID"));
            TxVFC.setText(Json.getString("@FechaCapt").substring(0,10));
            TxVFL.setText(Json.getString("@FechaEntrega").substring(0,10));
            TxVMarca.setText(Json.getString("@Marca") + " - " + Json.getString("@DMarca"));
            TxVFamilia.setText(Json.getString("@Familia") + " - " + Json.getString("@DFamilia"));
            TxVLinea.setText(Json.getString("@Linea") + " - " + Json.getString("@DLinea"));
            TxVCol.setText(Json.getString("@Coleccion") + " - " + Json.getString("@DColeccion"));
            TxVAcc.setText(Json.getString("@Accionamiento") + " - " + Json.getString("@DAccionamiento"));
            TxVCon.setText(Json.getString("@Control") + " - " + Json.getString("@DControl"));
            TxVColor.setText(Json.getString("@ColorID") + " - " + Json.getString("@Name"));
            TxVAncho.setText(Json.getString("@Ancho"));
            TxVAlto.setText(Json.getString("@Alto"));
        }catch (JSONException e){
            SendError(e, "Error. JSONException onCreateView fragment_datos_generales");
        }catch (Exception e){
            SendError(e, "Error. onCreateView fragment_datos_generales");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionDG(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteractionDG(Uri uri);
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
