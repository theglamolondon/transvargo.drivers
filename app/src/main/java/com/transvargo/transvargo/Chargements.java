package com.transvargo.transvargo;

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

import com.android.volley.VolleyError;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeExpeditionsAction;
import com.transvargo.transvargo.http.behavior.ListeOffreAction;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Offre;

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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private List<Chargement> chargements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    public void notifyFragmentEncours()
    {
        MesChargementsEncoursFragment fragment = (MesChargementsEncoursFragment) this.mSectionsPagerAdapter.getItem(1);
        fragment.updateListe();
    }

    public List<Chargement> getListeChargement(String statut)
    {
        List<Chargement> liste = new ArrayList<>();

        for( Chargement chargement: this.chargements ) {

            if( chargement.expedition.statut.equals(statut) ){
                liste.add(chargement);
            }
        }
        return liste;
    }

    public void setListeChargement(List<Chargement> liste){
        this.chargements = liste;
        Log.e("###Liste-Chargement",liste.toString());
        MesChargementsEncoursFragment fragment = (MesChargementsEncoursFragment) this.mSectionsPagerAdapter.getItem(1);
        Log.e("###Fragment",fragment.toString());
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public final List<Fragment> mFragmentList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList.add(new MesChargementsFragment());
            mFragmentList.add(new MesChargementsEncoursFragment());
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Programmés";
                case 1:
                    return "En cours";
            }
            return null;
        }
    }
}