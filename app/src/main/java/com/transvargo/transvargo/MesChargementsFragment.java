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

public class MesChargementsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chargement, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Mes chargements");
        return rootView;
    }
}
