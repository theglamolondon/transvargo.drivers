package com.transvargo.transvargo;

import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeExpeditionsAction;
import com.transvargo.transvargo.http.behavior.ListeOffreAction;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.processing.StoreCache;

import java.util.ArrayList;
import java.util.List;

public class Chargements extends MyActivityModel {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ProgressDialog progressDialog;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<Chargement> chargements = new ArrayList<>();
    private ResponseHandler handler = new ResponseHandler() {
        @Override
        public <T> void doSomething(List<T> data) {
            setListeChargement((ArrayList<Chargement>) data);
            notifyFragmentMesChargements();
            notifyFragmentEncours();
        }

        @Override
        public void error(int httpCode, VolleyError error)
        {
            super.error(httpCode, error);

            String json = StoreCache.getString(getBaseContext(), StoreCache.TRANSVARGO_MY_CHARGEMENTS);
            //JsonArray jp = new JsonParser().parse(json).getAsJsonArray();

            if(json != null && false){
                /*Log.i("###API-Cache", jp.getAsString());
                List<Chargement> chargements = processFromCache(jp.getAsString());
                ((Chargements)getActivity()).setListeChargement(chargements);

                fillView(); */
                Toast.makeText(getBaseContext(),"Affichage à partir du cache.", Toast.LENGTH_LONG).show();
            }else {
                Log.e("###API-Chargement",error.getMessage());
                Toast.makeText(getBaseContext(),"Impossible de se connecter au serveur. Veuillez ressayer dans un instant.", Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chargement);

        TabLayout tab = (TabLayout) findViewById(R.id.tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tab.setupWithViewPager(mViewPager);
        this.loadData();
    }

    public void loadData()
    {
        this.progressDialog = ProgressDialog.show(this, "", "Récupération de vos chargements ...", true);
        new Runnable(){
            @Override
            public void run() {
                if(getListeChargement(Chargement.STATE_PROGRAMME) != null)
                {
                    ApiTransvargo api = new ApiTransvargo(Chargements.this.getApplicationContext());
                    ListeExpeditionsAction liste = new ListeExpeditionsAction(handler);
                    api.executeHttpRequest(liste);
                }else{
                    notifyFragmentMesChargements();
                    notifyFragmentEncours();
                }
            }
        }.run();
    }

    public void notifyFragmentMesChargements()
    {
        Fragment fragment = MesChargementsFragment.newInstance(0);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.linear_fragment_chargements, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        this.progressDialog.dismiss();
    }

    public void notifyFragmentEncours()
    {
        Fragment fragment = MesChargementsEncoursFragment.newInstance(1);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.relativ_frgmnt_encours, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        this.progressDialog.dismiss();
    }

    public List<Chargement> getListeChargement(int statut)
    {
        List<Chargement> liste = new ArrayList<>();
        for( Chargement chargement: this.chargements )
        {
            Log.e("###Liste","Statut actuel :"+chargement.expedition.statut+" | Statut recherché :"+statut);
            if( chargement.expedition.statut == statut )
            {
                Log.e("###Liste","Chargement ajouté");
                liste.add(chargement);
            }
        }
        Log.e("###Liste",liste.toString());
        //return liste;
        return this.chargements;
    }

    public void setListeChargement(List<Chargement> liste)
    {
        this.chargements = liste;
        Log.e("###Liste-Chargement",liste.size()+" élément(s)");
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public final List<Fragment> mFragmentList = new ArrayList<>();
        private String[] titles = {"Programmés", "En cours"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList.add(new MesChargementsFragment());
            mFragmentList.add(new MesChargementsEncoursFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}