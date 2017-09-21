package com.transvargo.transvargo.model;

import java.util.Date;

/**
 * Created by BW.KOFFI on 11/08/2017.
 */

public class Chargement
{
    public int id;
    public Date dateheurechargement;
    public String adressechargement;
    public String societechargement;
    public String contactchargement;
    public String adresselivraison;
    public String societelivraison;
    public String contactlivraison;

    public Offre expedition;
    public Vehicule vehicule;
}
