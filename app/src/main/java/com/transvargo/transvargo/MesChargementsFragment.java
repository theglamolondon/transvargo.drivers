package com.transvargo.transvargo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeExpeditionsAction;
import com.transvargo.transvargo.model.Chargement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by BW.KOFFI on 17/09/2017.
 */

public class MesChargementsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listView;
    private ProgressDialog progressDialog;
    private ChargementAdapter adapter;

    private ResponseHandler handler = new ResponseHandler() {
        @Override
        public <T> void doSomething(List<T> data) {
            ((Chargements)getActivity()).setListeChargement((ArrayList<Chargement>) data);
            fillView();
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            super.error(httpCode, error);
            progressDialog.dismiss();
            Toast.makeText(getActivity(),"Impossible de se connecter au serveur. Veuillez ressayer dans un instant.", Toast.LENGTH_LONG).show();
        }
    };

    public MesChargementsFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MesChargementsFragment newInstance(int sectionNumber) {
        MesChargementsFragment fragment = new MesChargementsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_chargement, container, false);
        this.listView = (ListView) rootView.findViewById(R.id.fragment_liste_chargement);


        this.progressDialog = ProgressDialog.show(getActivity(), "","Récupération de vos chargements ...", true);

        if(((Chargements) getActivity()).getListeChargement() != null)
        {
            new Runnable(){
                @Override
                public void run() {
                    ApiTransvargo api = new ApiTransvargo(getActivity());
                    ListeExpeditionsAction liste = new ListeExpeditionsAction(handler);

                    api.executeHttpRequest(liste);
                }
            }.run();
        }else{
            fillView();
        }

        return rootView;
    }

    public void fillView(){
        this.adapter = new ChargementAdapter(getActivity(), ((Chargements)getActivity()).getListeChargement() );
        this.listView.setAdapter(this.adapter);

        this.progressDialog.dismiss();
        this.adapter.notifyDataSetChanged();
    }

    private class ChargementAdapter extends BaseAdapter {

        private Context mContext;
        private List<Chargement> mesChargements;

        public ChargementAdapter(@NonNull Context context,@NonNull List<Chargement> objects) {
            this.mContext = context;
            this.mesChargements = objects;
        }

        @Override
        public int getCount() {
            return this.mesChargements.size();
        }

        @Override
        public Chargement getItem(int position) {
            return this.mesChargements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return this.mesChargements.get(position).id;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chargement_template, parent, false);
            }

            ChargmentViewHolder viewHolder = (ChargmentViewHolder) convertView.getTag();

            if(viewHolder == null){
                viewHolder = new ChargmentViewHolder(){};
                viewHolder.txt_depart  = (TextView) convertView.findViewById(R.id.txt_chrgmt_depart);
                viewHolder.txt_arrivee  = (TextView) convertView.findViewById(R.id.txt_chrgmt_arrivee);
                viewHolder.txt_datechargement  = (TextView) convertView.findViewById(R.id.txt_chrgmt_datechargement);
                viewHolder.txt_dateexpiration  = (TextView) convertView.findViewById(R.id.txt_chrgmt_dateexpiration);
                viewHolder.txt_fragile  = (TextView) convertView.findViewById(R.id.txt_chrgmt_fragile);
                viewHolder.txt_distance  = (TextView) convertView.findViewById(R.id.txt_chrgmt_distance);
                viewHolder.txt_masse  = (TextView) convertView.findViewById(R.id.txt_chrgmt_masse);

                convertView.setTag(viewHolder);
            }

            Chargement chargement = (Chargement)getItem(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

            viewHolder.txt_depart.setText(chargement.expedition.lieudepart);
            viewHolder.txt_arrivee.setText(chargement.expedition.lieuarrivee);

            viewHolder.txt_datechargement.setText(sdf.format(chargement.expedition.datechargement));
            viewHolder.txt_dateexpiration.setText(sdf.format(chargement.expedition.dateexpiration));

            viewHolder.txt_fragile.setText(chargement.expedition.fragile ? "Oui" : "Non");
            viewHolder.txt_distance.setText(String.format("%s km", chargement.expedition.distance));

            if(chargement.expedition.masse <= 10000)
            {
                viewHolder.txt_masse.setText((chargement.expedition.masse/1000) + " T");
            }else {
                viewHolder.txt_masse.setText(chargement.expedition.masse + " kg");
            }

            return convertView;
        }

        public class ChargmentViewHolder{
            public TextView txt_depart ;
            public TextView txt_datechargement ;
            public TextView txt_arrivee ;
            public TextView txt_dateexpiration ;

            public TextView txt_fragile ;
            public TextView txt_distance ;
            public TextView txt_masse ;
        }
    }
}
