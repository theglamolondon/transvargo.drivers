package com.transvargo.transvargo.model;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public class Transporteur {

    public static double pourcentage = 1;
    public static final int CHAFFEUR_PATRON = 1;
    public static final int PROPRIETAIRE_FLOTTE = 2;
    public static final int CHAUFFEUR_FLOTTE = 3;

    public Identite identite;

    public String nom;
    public String prenoms;
    public String raisonsociale;
    public String contact;
    public String comptecontribuable;
    public String ville;
    public String nationalite;
    public String datenaissance;
    public String lieunaissance;
    public String rib;
    public String datecreation;
    public int typetransporteur_id;

    public String jwt;

    public Vehicule vehicule;

}
