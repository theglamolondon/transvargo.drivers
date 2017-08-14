package com.transvargo.transvargo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.LoginAction;

import java.util.List;

public class Login extends AppCompatActivity {

    EditText txtlogin;
    EditText txtpassword;
    Button btnconnexion;

    ResponseHandler handler = new ResponseHandler() {
        @Override
        public void doSomething(Object liste) {
            Intent openApp = new Intent(Login.this,Principal.class);
            startActivity(openApp);
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
                new Runnable(){
                    @Override
                    public void run() {

                        ApiTransvargo api = new ApiTransvargo(getBaseContext());
                        LoginAction login = new LoginAction(txtlogin.getText().toString(), txtpassword.getText().toString());
                        login.action = handler;
                        api.executeHttpRequest(login);

                    }
                }.run();
            }
        });
    }
}
