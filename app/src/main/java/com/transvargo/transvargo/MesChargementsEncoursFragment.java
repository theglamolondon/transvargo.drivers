package com.transvargo.transvargo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by BW.KOFFI on 17/09/2017.
 */

public class MesChargementsEncoursFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public MesChargementsEncoursFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MesChargementsEncoursFragment newInstance(int sectionNumber) {
        MesChargementsEncoursFragment fragment = new MesChargementsEncoursFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chargement_en_cours, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        return rootView;
    }
}