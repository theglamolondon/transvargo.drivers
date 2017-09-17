package com.transvargo.transvargo.processing;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.transvargo.transvargo.R;
import com.transvargo.transvargo.model.Offre;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by BW.KOFFI on 11/08/2017.
 */

public class ListeProcessing extends ArrayAdapter<Offre> {

    private Context mContext;

    public ListeProcessing(@NonNull Context context, @NonNull List<Offre> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.offre_template, parent, false);
        }

        OffreViewHolder viewHolder = (OffreViewHolder) convertView.getTag();

        if(viewHolder == null)
        {
            viewHolder = new OffreViewHolder();
            viewHolder.img_offre_end = (ImageView) convertView.findViewById(R.id.img_offre_end);
            viewHolder.img_offre_start = (ImageView) convertView.findViewById(R.id.img_offre_start);

            viewHolder.txt_offre_datechargement = (TextView) convertView.findViewById(R.id.txt_offre_datechargement);
            viewHolder.txt_offre_dateexpiration = (TextView) convertView.findViewById(R.id.txt_offre_dateexpiration);
            viewHolder.txt_offre_delais = (TextView) convertView.findViewById(R.id.txt_offre_delais);
            viewHolder.txt_offre_distance = (TextView) convertView.findViewById(R.id.txt_offre_distance);
            viewHolder.txt_offre_lieuarrivee = (TextView) convertView.findViewById(R.id.txt_offre_lieuarrivee);
            viewHolder.txt_offre_lieudepart = (TextView) convertView.findViewById(R.id.txt_offre_lieudepart);
            viewHolder.txt_offre_prix = (TextView) convertView.findViewById(R.id.txt_offre_prix);
            viewHolder.txt_offre_masse = (TextView) convertView.findViewById(R.id.txt_offre_masse);
            viewHolder.txt_offre_fragile = (TextView) convertView.findViewById(R.id.txt_offre_fragile);

            convertView.setTag(viewHolder);
        }

        Offre offre = getItem(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);

        viewHolder.txt_offre_distance.setText(String.format("%s km", offre.distance ));
        viewHolder.txt_offre_prix.setText(String.format("%s FCFA",offre.prix) );
        viewHolder.txt_offre_datechargement.setText(sdf.format(offre.datechargement));
        viewHolder.txt_offre_dateexpiration.setText(sdf.format(offre.dateexpiration));
        viewHolder.txt_offre_lieuarrivee.setText(offre.lieuarrivee);
        viewHolder.txt_offre_lieudepart.setText(offre.lieudepart);
        viewHolder.txt_offre_delais.setText( String.format("%s jour(s)", (offre.dateexpiration.getTime() - (new Date().getTime()))/(1000*60*60*24)));
        viewHolder.txt_offre_fragile.setText(offre.fragile ? "Oui" : "Non");

        if(offre.masse <= 10000)
        {
            viewHolder.txt_offre_masse.setText((offre.masse/1000) + " T");
        }else {
            viewHolder.txt_offre_masse.setText(offre.masse + " kg");
        }

        return convertView;
    }


    private class OffreViewHolder {
        public TextView txt_offre_distance;
        public TextView txt_offre_prix;
        public TextView txt_offre_datechargement;
        public TextView txt_offre_dateexpiration;
        public TextView txt_offre_lieuarrivee;
        public TextView txt_offre_lieudepart;
        public TextView txt_offre_delais;
        public TextView txt_offre_masse;
        public TextView txt_offre_fragile;

        public ImageView img_offre_start;
        public ImageView img_offre_end;
    }
}