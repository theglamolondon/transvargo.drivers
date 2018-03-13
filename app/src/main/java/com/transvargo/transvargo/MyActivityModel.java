package com.transvargo.transvargo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.processing.StoreCache;

/**
 * Created by BW.KOFFI on 13/08/2017.
 */

public class MyActivityModel extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fixe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout :
                logout();
                return true;

            case R.id.menu_profil:
                showProfile();
                return true;

            case R.id.menu_quit:
                quitApplication();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProfile() {
        Toast.makeText(this,"Profile transporteur",Toast.LENGTH_SHORT).show();
    }

    public void quitApplication()
    {
        Log.e("#Trans-API#","back button pressed");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Êtes-vous sur de vouloir quitter l'application ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
            }
        });

        builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void logout()
    {
        StoreCache.delete(this, StoreCache.TRANSVARGO_TRANSPORTEUR);
        Toast.makeText(this, "Déconnnexion", Toast.LENGTH_SHORT).show();

        Intent openApp = new Intent(getApplicationContext(), Login.class);
        startActivity(openApp);
    }
}
