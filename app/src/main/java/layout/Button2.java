package layout;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gcubos.android.interfaz.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import eMail.Mail;

public class Button2 extends Fragment {
    Mail SeMail;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "cadena";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public Button2() {
        // Required empty public constructor
    }

    public static Button2 newInstance(String param1) {
        //Recupera paramentros enviados
        Button2 fragment = new Button2();
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
        JSONArray JORenglones;
        JSONObject JOE;
        String strCadena = getArguments().getString("cadena");
        int textSize = R.style.TextosE;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_button2, container, false);
        //Recorre JsonArray con los renglones y genera los TextView dinamicos
        try {
            JSONObject Json = new JSONObject(strCadena);
            JORenglones = Json.getJSONArray("renglones");
            //Por cada renglon genera un TextView
            for (int i = 0; i < JORenglones.length(); i++) {
                JOE = JORenglones.getJSONObject(i);
                View LayOutE = view.findViewById(R.id.LayOutDetalle);
                TextView Paper = new TextView(LayOutE.getContext());
                Paper.setTextSize(15);
                Paper.setTextColor(Color.BLACK);
                Paper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                Paper.setText(JOE.getString("@cadena"));
                ((LinearLayout)LayOutE).addView(Paper);
            }
        }catch (JSONException e){
            SendError(e, "Error. JSONException onCreateView Button2");
        }catch (Exception e){
            SendError(e, "Error. onCreateView Button2");
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteractionB2(uri);
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
        void onFragmentInteractionB2(Uri uri);
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
