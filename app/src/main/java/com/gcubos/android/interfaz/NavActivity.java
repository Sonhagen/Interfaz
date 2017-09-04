package com.gcubos.android.interfaz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String usuario = "gcubos";

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("Usuario");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                //AlertDialog para validar la salida
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getString(R.string.ExitTlt))
                        .setMessage(getString(R.string.ExitMsg))
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            //Clase anonima para validar la respuesta positiva
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        //Si es respuesta negativa cierra
                        .setNegativeButton("No", null)
                        .show();
            }else{
                //Si no es el ultimo en el BackStack, para a super con BaclPressed
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Bundle bundle = new Bundle();
        bundle.putString("Usuario", usuario);

        if (id == R.id.nav_Escaneo) {
            Intent actIntent = new Intent(getApplicationContext(), MainActivity.class);
            actIntent.putExtras(bundle);
            startActivityForResult(actIntent,0);
        } else if (id == R.id.nav_WCPAct) {
            bundle.putString("Todo", "NO");
            Intent actIntent = new Intent(getApplicationContext(), FlujoActivity.class);
            actIntent.putExtras(bundle);
            startActivityForResult(actIntent,0);
        } else if (id == R.id.nav_WCPTodo) {
            bundle.putString("Todo", "SI");
            Intent actIntent = new Intent(getApplicationContext(), FlujoActivity.class);
            actIntent.putExtras(bundle);
            startActivityForResult(actIntent,0);
        } else if (id == R.id.nav_Croquis) {
            Intent actIntent = new Intent(getApplicationContext(), Croquis.class);
            actIntent.putExtras(bundle);
            startActivityForResult(actIntent,0);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
