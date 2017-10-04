package com.transvargo.transvargo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.transvargo.transvargo.http.ApiTransvargo;
import com.transvargo.transvargo.http.ResponseHandler;
import com.transvargo.transvargo.http.behavior.ListeExpeditionsAction;
import com.transvargo.transvargo.model.Chargement;
import com.transvargo.transvargo.model.Client;
import com.transvargo.transvargo.model.Offre;
import com.transvargo.transvargo.model.Transporteur;
import com.transvargo.transvargo.model.Vehicule;
import com.transvargo.transvargo.processing.StoreCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    Gson gson ;

    private ResponseHandler handler = new ResponseHandler() {
        @Override
        public <T> void doSomething(List<T> data) {
            ((Chargements)getActivity()).setListeChargement((ArrayList<Chargement>) data);
            ((Chargements)getActivity()).notifyFragmentEncours();
            fillView();
        }

        @Override
        public void error(int httpCode, VolleyError error) {
            super.error(httpCode, error);

            String json = StoreCache.getString(getActivity(), StoreCache.TRANSVARGO_MY_CHARGEMENTS);

            //JsonArray jp = new JsonParser().parse(json).getAsJsonArray();

            if(json != null && false){
                /*Log.i("###API-Cache", jp.getAsString());
                List<Chargement> chargements = processFromCache(jp.getAsString());

                ((Chargements)getActivity()).setListeChargement(chargements);


                fillView(); */
                Toast.makeText(getActivity(),"Affichage à partir du cache.", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getActivity(),"Impossible de se connecter au serveur. Veuillez ressayer dans un instant.", Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
        }
    };

    public MesChargementsFragment() {
        this.gson = new GsonBuilder().disableHtmlEscaping().create();
    }

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

    public ArrayList<Chargement> processFromCache(String json)
    {
        ArrayList<Chargement> list = new ArrayList<>();

        try{
        JSONArray response = new JSONArray(json);
        JSONObject rChargement;
        Chargement chargement;


        for (int i = 0; i <= response.length() - 1; i++)
        {
            chargement = new Chargement();

            try {
                rChargement = response.getJSONObject(i);
                chargement.id = rChargement.getInt("id");

                if(rChargement.getString("dateheurechargement") != null)
                {
                    try {
                        chargement.dateheurechargement = dateFormat.parse(rChargement.getString("dateheurechargement"));
                    } catch (ParseException e) {
                        chargement.dateheurechargement = new Date();
                        e.printStackTrace();
                    }
                }else {
                    chargement.dateheurechargement = new Date();
                }

                chargement.adressechargement = rChargement.getString("adressechargement");
                chargement.societechargement = rChargement.getString("societechargement");
                chargement.contactchargement = rChargement.getString("contactchargement");
                chargement.telephonechargement = rChargement.getString("telephonechargement");
                chargement.adresselivraison = rChargement.getString("adresselivraison");
                chargement.societelivraison = rChargement.getString("societelivraison");
                chargement.contactlivraison = rChargement.getString("contactlivraison");
                chargement.telephonelivraison = rChargement.getString("telephonelivraison");

                chargement.vehicule = getVehiculeFromJSON(rChargement.getJSONObject("vehicule"));
                chargement.expedition = getExpeditionFromJSON(rChargement.getJSONObject("expedition"));

                list.add(chargement);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return list;
    }

    private Vehicule getVehiculeFromJSON(JSONObject rVehicule)
    {
        Vehicule vehicule = new Vehicule();

        try{
            vehicule.id = rVehicule.getInt("id");
            vehicule.immatriculation = rVehicule.getString("immatriculation");
            vehicule.telephone = rVehicule.getString("telephone");
            vehicule.chauffeur = rVehicule.getString("chauffeur");

        }catch (JSONException e){
            e.printStackTrace();
        }

        return  vehicule;
    }

    private Offre getExpeditionFromJSON(JSONObject rExpedition)
    {
        Offre offre = new Offre();

        try {
            if (rExpedition.getString("datechargement") != null) {
                try {
                    offre.datechargement = dateFormat.parse(rExpedition.getString("datechargement"));
                } catch (ParseException e) {
                    offre.datechargement = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateexpiration") != null) {
                try {
                    offre.dateexpiration = dateFormat.parse(rExpedition.getString("dateexpiration"));
                } catch (ParseException e) {
                    offre.dateexpiration = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateheurelivraison") != null) {
                try {
                    offre.dateheurelivraison = dateFormat.parse(rExpedition.getString("dateheurelivraison"));
                } catch (ParseException e) {
                    offre.dateheurelivraison = new Date();
                    e.printStackTrace();
                }
            }
            if (rExpedition.getString("dateheureacceptation") != null) {
                try {
                    offre.dateheureacceptation = dateFormat.parse(rExpedition.getString("dateheureacceptation"));
                } catch (ParseException e) {
                    offre.dateheureacceptation = new Date();
                    e.printStackTrace();
                }
            }

            offre.id = rExpedition.getInt("id");
            offre.reference = rExpedition.getString("reference");
            offre.coordarrivee = rExpedition.getString("coordarrivee");
            offre.coorddepart = rExpedition.getString("coorddepart");
            offre.masse = rExpedition.getLong("masse");
            offre.fragile = rExpedition.getBoolean("fragile");

            Double rPrix = (Transporteur.pourcentage * rExpedition.getInt("prix"));
            offre.prix = rPrix.intValue();

            offre.distance = rExpedition.getInt("distance");
            offre.lieudepart = rExpedition.getString("lieudepart");
            offre.lieuarrivee = rExpedition.getString("lieuarrivee");
            offre.statut = rExpedition.getString("statut");

            //Client
            JSONObject rClient = rExpedition.getJSONObject("client");
            Client client = new Client();
            client.nom = rClient.getString("nom");
            client.prenoms = rClient.getString("prenoms");
            client.contact = rClient.getString("contact");
            client.raisonsociale = rClient.getString("raisonsociale");

            offre.client = client;

        }catch (JSONException e){
            e.printStackTrace();
        }

        return offre;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_chargement, container, false);
        this.listView = (ListView) rootView.findViewById(R.id.fragment_liste_chargement);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chargement chargement = ((Chargements)getActivity()).getListeChargement(Chargement.STATE_PROGRAMME).get(position);

                Gson gson = new Gson();
                Intent intent = new Intent(getActivity(),DetailsChargement.class);
                intent.putExtra("chargement",gson.toJson(chargement));
                startActivity(intent);
            }
        });


        this.progressDialog = ProgressDialog.show(getActivity(), "","Récupération de vos chargements ...", true);

        if(((Chargements) getActivity()).getListeChargement(Chargement.STATE_PROGRAMME) != null)
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
        this.adapter = new ChargementAdapter(getActivity(), ((Chargements)getActivity()).getListeChargement(Chargement.STATE_PROGRAMME) );
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
                /*
                viewHolder.txt_fragile  = (TextView) convertView.findViewById(R.id.txt_chrgmt_fragile);
                viewHolder.txt_distance  = (TextView) convertView.findViewById(R.id.txt_chrgmt_distance);
                viewHolder.txt_masse  = (TextView) convertView.findViewById(R.id.txt_chrgmt_masse);
                */
                convertView.setTag(viewHolder);
            }

            Chargement chargement = (Chargement)getItem(position);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

            viewHolder.txt_depart.setText(chargement.expedition.lieudepart);
            viewHolder.txt_arrivee.setText(chargement.expedition.lieuarrivee);

            viewHolder.txt_datechargement.setText(sdf.format(chargement.expedition.datechargement));
            viewHolder.txt_dateexpiration.setText(sdf.format(chargement.expedition.dateexpiration));

            /*
            viewHolder.txt_fragile.setText(chargement.expedition.fragile ? "Oui" : "Non");
            viewHolder.txt_distance.setText(String.format("%s km", chargement.expedition.distance));

            if(chargement.expedition.masse <= 10000)
            {
                viewHolder.txt_masse.setText((chargement.expedition.masse/1000) + " T");
            }else {
                viewHolder.txt_masse.setText(chargement.expedition.masse + " kg");
            }
            */
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
