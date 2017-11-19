package com.transvargo.transvargo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.LoginAction;
import com.transvargo.transvargo.service.Tracking;

import java.util.List;

public class Login extends AppCompatActivity {
    EditText txtlogin;
    EditText txtpassword;
    Button btnconnexion;
    ProgressDialog progressDialog;

    ResponseHandler handler = new ResponseHandler() {
        @Override
        public void doSomething(Object liste) {
            Log.e("##TESTS##","execute login");
            Intent openApp = new Intent(Login.this,Principal.class);
            startActivity(openApp);

            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

        @Override
        public <T> void doSomething(List<T> data) {
            Log.e("##TESTS##","execute login");
            Intent openApp = new Intent(Login.this,Principal.class);
            startActivity(openApp);

            //Démarrage du service de géolocalisation
            startService(new Intent(Login.this, Tracking.class));

            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            error.printStackTrace();
            Toast.makeText(Login.this,"Oups ! Erreur de connexion.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnconnexion = (Button) findViewById(R.id.btnconnexion);
        txtlogin = (EditText) findViewById(R.id.txtlogin);
        txtpassword = (EditText) findViewById(R.id.txtpassword);

        btnconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(Login.this,"Authentification du transporteur","Vérification de vos informations");
                ApiTransvargo api = new ApiTransvargo(getBaseContext());
                LoginAction login = new LoginAction(txtlogin.getText().toString(), txtpassword.getText().toString());
                login.action = handler;
                api.executeHttpRequest(login);
            }
        });
    }
}
