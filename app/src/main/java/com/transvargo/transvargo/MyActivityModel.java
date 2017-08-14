package com.transvargo.transvargo;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showProfile() {
        Toast.makeText(this,"Profile du transporteur",Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        Toast.makeText(this, "Déconnnexion", Toast.LENGTH_SHORT).show();
    }
}
