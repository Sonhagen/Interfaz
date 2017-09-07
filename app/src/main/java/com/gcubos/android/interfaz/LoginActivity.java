package com.gcubos.android.interfaz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import DataBase.DataDB;
import JSONParser.AsyncResponse;
import JSONParser.GetAsync;
import eMail.Mail;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Mail SeMail;

    private void habilitaBotones(boolean Estado){
        //Habilita o deshabilita bortones
        Button bLog = (Button) findViewById(R.id.email_sign_in_button);
        bLog.setEnabled(Estado);
        if (!Estado){
            fadeOut(bLog);
        }
        else{
            fadeIn(bLog);
        }
    }

    private void fadeIn(View bView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bView, "alpha", 0f, 1f);
        objectAnimator.setDuration(500L);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            /*
            @Override
            public void onAnimationEnd(Animator animation) {
                fadeOut();
            }*/
        });
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
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            SeMail = new Mail(getApplicationContext());
            setContentView(R.layout.activity_login);
            // Set up the login form.
            mEmailView = (EditText) findViewById(R.id.email);
            mPasswordView = (EditText) findViewById(R.id.password);

            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }catch (Exception e){
            SendError(e, "Error. onCreate LoginActivity");
        }
    }

    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        try{
            // Check for a valid password, if the user entered one.
            if (password.isEmpty()) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (email.isEmpty()) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {
                habilitaBotones(false);
                HashMap<String, String> HP = new HashMap<>();
                //HP.put("URL",getResources().getString(R.string.sURL) + getResources().getString(R.string.sLogIn));
                DataDB DB = new DataDB(getApplicationContext());
                HP.put("URL", DB.getURL() + DB.getLogIn());
                HP.put("Usuario", mEmailView.getText().toString());
                HP.put("Pass", mPasswordView.getText().toString());
                //Progress
                showProgress(true);
                //Inicia servicio asincrono con HP paramentros el primero siempre es URL WebService
                GetAsync async = (GetAsync) new GetAsync(new AsyncResponse() {
                    //Sobrecarga de clase anonima para individualizar la salida del proceso asincrono
                    @Override
                    public void processFinish(JSONObject json) {
                        EditText contentTxt = (EditText)findViewById(R.id.email);
                        if (!json.toString().contains("Vacio")) {
                            contentTxt.setError(null);
                            showProgress(false);
                            habilitaBotones(true);
                            //Intent actIntent = new Intent(getApplicationContext(), MainActivity.class);
                            Intent actIntent = new Intent(getApplicationContext(), NavActivity.class);
                            //Bundle para enviar los parametros
                            Bundle bundle = new Bundle();
                            bundle.putString("Usuario",contentTxt.getText().toString());
                            actIntent.putExtras(bundle);
                            startActivityForResult(actIntent,0);
                        }else {
                            //Muestra error de LogIn
                            contentTxt.setError(getText(R.string.error_invalid_email));
                            habilitaBotones(true);
                        }
                        //Progress
                        showProgress(false);
                        //Habilita botones al terminar actividad asincrona
                    }
                },getApplicationContext()).execute(HP);
            }
        }catch (Exception e){
            SendError(e, "Error. attemptLogin LoginActivity");
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

