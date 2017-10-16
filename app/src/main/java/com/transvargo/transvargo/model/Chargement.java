package com.transvargo.transvargo.model;

import java.util.Date;

/**
 * Created by BW.KOFFI on 11/08/2017.
 */

public class Chargement
{
    public static int STATE_EN_COURS = 252;
    public static int STATE_PROGRAMME = 242;

    public int id;
    public Date dateheurechargement;
    public String adressechargement;
    public String societechargement;
    public String contactchargement;
    public String telephonechargement;
    public String adresselivraison;
    public String societelivraison;
    public String contactlivraison;
    public String telephonelivraison;

    public Offre expedition;
    public Vehicule vehicule;
}
