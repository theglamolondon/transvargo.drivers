package com.transvargo.transvargo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.transvargo.transvargo.model.Chargement;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by BW.KOFFI on 17/09/2017.
 */

public class MesChargementsEncoursFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    ListView listView;
    private MesChargementsEncoursFragment.ChargementAdapter adapter;

    public MesChargementsEncoursFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MesChargementsEncoursFragment newInstance(int sectionNumber)
    {
        MesChargementsEncoursFragment fragment = new MesChargementsEncoursFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_chargement_en_cours, container, false);
        listView = (ListView) rootView.findViewById(R.id.fragment_liste_chargement_encours);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Chargement chargement = ((Chargements)getActivity()).getListeChargement(Chargement.STATE_EN_COURS).get(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getActivity(),LivraisonChargement.class);
                intent.putExtra("chargement",gson.toJson(chargement));
                startActivity(intent);
            }
        });

        this.fillView();
        return rootView;
    }

    public void fillView()
    {
        this.adapter = new MesChargementsEncoursFragment.ChargementAdapter(getActivity(), ((Chargements)getActivity()).getListeChargement(Chargement.STATE_EN_COURS) );
        this.listView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    private class ChargementAdapter extends BaseAdapter
    {
        Context mContext;
        private List<Chargement> mesChargements;

        public ChargementAdapter(@NonNull Context context, @NonNull List<Chargement> objects) {
            this.mContext = context;
            this.mesChargements = objects;
        }

        @Override
        public int getCount() {
            return this.mesChargements.size();
        }

        @Override
        public Object getItem(int position) {
            return this.mesChargements.get(position);
        }

        @Override
        public long getItemId(int position) {
            try {
                return this.mesChargements.get(position).id;
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(mContext,"Impossible d'obtenir le chargement demandé", Toast.LENGTH_LONG).show();
                return 0;
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chargement_template, parent, false);
            }

            MesChargementsEncoursFragment.ChargementAdapter.ChargmentViewHolder viewHolder = (MesChargementsEncoursFragment.ChargementAdapter.ChargmentViewHolder) convertView.getTag();

            if(viewHolder == null){
                viewHolder = new MesChargementsEncoursFragment.ChargementAdapter.ChargmentViewHolder(){};
                viewHolder.txt_depart  = (TextView) convertView.findViewById(R.id.txt_chrgmt_depart);
                viewHolder.txt_arrivee  = (TextView) convertView.findViewById(R.id.txt_chrgmt_arrivee);
                viewHolder.txt_datechargement  = (TextView) convertView.findViewById(R.id.txt_chrgmt_datechargement);
                viewHolder.txt_dateexpiration  = (TextView) convertView.findViewById(R.id.txt_chrgmt_dateexpiration);
                viewHolder.section_label = (TextView) convertView.findViewById(R.id.section_label);
                viewHolder.relativ_chgmnt_template = (RelativeLayout) convertView.findViewById(R.id.relativ_chgmnt_template);

                convertView.setTag(viewHolder);
            }

            Chargement chargement = (Chargement)getItem(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

            viewHolder.section_label.setText("Chargement en cours");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //viewHolder.relativ_chgmnt_template.setBackgroundColor(mContext.getColor(R.color.colorSecondaryLitgh));
                viewHolder.section_label.setTextColor(mContext.getColor(R.color.colorSecondaryLitgh));
            }else{
                //viewHolder.relativ_chgmnt_template.setBackgroundColor(mContext.getResources().getColor(R.color.colorSecondaryLitgh));
                viewHolder.section_label.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryLitgh));
            }

            viewHolder.txt_depart.setText(chargement.expedition.lieudepart);
            viewHolder.txt_arrivee.setText(chargement.expedition.lieuarrivee);

            viewHolder.txt_datechargement.setText(sdf.format(chargement.expedition.datechargement));
            viewHolder.txt_dateexpiration.setText(sdf.format(chargement.expedition.dateexpiration));

            return convertView;
        }

        public class ChargmentViewHolder{
            public TextView txt_depart ;
            public TextView txt_datechargement ;
            public TextView txt_arrivee ;
            public TextView txt_dateexpiration ;
            public TextView section_label;
            public RelativeLayout relativ_chgmnt_template;

            public TextView txt_fragile ;
            public TextView txt_distance ;
            public TextView txt_masse ;
        }
    }
}