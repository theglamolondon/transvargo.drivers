package com.transvargo.transvargo.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by BW.KOFFI on 09/08/2017.
 */

public class Offre {

    public int id;
    public String reference;
    public Date dateheureacceptation;
    public Date dateheurelivraison;
    public Date datechargement;
    public Date dateexpiration;
    public String lieudepart;
    public String coorddepart;
    public String lieuarrivee;
    public String coordarrivee;
    public double masse;
    public String statut;
    public Boolean fragile;
    public int prix;
    public int distance;

    public Client client;
    public Chargement chargement;
    public TypeCamion typeCamion;
}
