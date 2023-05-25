package com.example.happywed.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationModel {

    public static HashMap<String,ArrayList<String>> getLocation(){

        HashMap<String,ArrayList<String>> locations = new HashMap<String,ArrayList<String>>();


        ArrayList<String> colombo = new ArrayList<String>();
        colombo.add("Colombo");
        colombo.add("Piliyandala");
        colombo.add("Maharagama");
        colombo.add("Boralesgamuwa");
        colombo.add("Kaduwela");
        colombo.add("Nugegoda");

        ArrayList<String> gampaha = new ArrayList<String>();
        gampaha.add("Gampaha");
        gampaha.add("Biyagama");
        gampaha.add("Delgoda");
        gampaha.add("Weliveriya");

        ArrayList<String> kalutara = new ArrayList<String>();
        kalutara.add("Kalutara");
        kalutara.add("Piliyandala");
        kalutara.add("Horana");
        kalutara.add("Bandaragama");
        kalutara.add("Mathugama");

        ArrayList<String> galle = new ArrayList<String>();
        galle.add("Galle");
        galle.add("Ambalangoda");
        galle.add("Baddegama");
        galle.add("Hikkaduwa");
        galle.add("Ihalagama");

        ArrayList<String> matara = new ArrayList<String>();
        matara.add("Matara");
        matara.add("Weligama");
        matara.add("Hakmana");
        matara.add("Akuressa");
        matara.add("Kaburupitiya");

        ArrayList<String> hambantota = new ArrayList<String>();
        hambantota.add("Hambantota");
        hambantota.add("Tangalle");
        hambantota.add("Ambalantota");
        hambantota.add("Tissamaharama");
        hambantota.add("Beliatta");

        ArrayList<String> jaffna = new ArrayList<String>();
        jaffna.add("Jaffna");
        jaffna.add("Nallur");
        jaffna.add("Chavakachcheri");

        ArrayList<String> batticalo = new ArrayList<String>();
        batticalo.add("Baticaloa");

        ArrayList<String> trincomalee = new ArrayList<String>();
        trincomalee.add("Trincomallee");
        trincomalee.add("Kinniya");

        ArrayList<String> anuradhapura = new ArrayList<String>();
        anuradhapura.add("Anuradhapura");
        anuradhapura.add("Kekirawa");
        anuradhapura.add("Madawachchiya");
        anuradhapura.add("Tissamaharama");
        anuradhapura.add("Eppawala");

        ArrayList<String> puttalam = new ArrayList<String>();
        puttalam.add("Puttalam");
        puttalam.add("Dankotuwa");
        puttalam.add("Nattandiya");
        puttalam.add("Chilaw");
        puttalam.add("Wennappuwa");

        ArrayList<String> nuwaraeliya = new ArrayList<String>();
        nuwaraeliya.add("Nuwaraeliya");
        nuwaraeliya.add("Hatton");
        nuwaraeliya.add("Ginigathhena");
        nuwaraeliya.add("Madulla");

        ArrayList<String> matale = new ArrayList<String>();
        matale.add("Matale");
        matale.add("Dabulla");
        matale.add("Galewela");
        matale.add("Sigiriya");
        matale.add("Ukuwela");

        ArrayList<String> mannar = new ArrayList<String>();
        mannar.add("Mannar");

        ArrayList<String> polonnaruwa = new ArrayList<String>();
        polonnaruwa.add("Polonnaruwa");
        polonnaruwa.add("Kaduruwela");
        polonnaruwa.add("Higurangoda");
        polonnaruwa.add("Medirigiriya");

        ArrayList<String> ampara = new ArrayList<String>();
        ampara.add("Ampara");
        ampara.add("Akkarapattu");
        ampara.add("Kalmunai");
        ampara.add("Sainthamaruthu");

        ArrayList<String> badulla = new ArrayList<String>();
        badulla.add("Badulla");
        badulla.add("Bandarawela");
        badulla.add("Welimada");
        badulla.add("Mahiyanganaya");
        badulla.add("Ella");

        ArrayList<String> kandy = new ArrayList<String>();
        kandy.add("Kandy");
        kandy.add("Katugastota");
        kandy.add("Gampola");
        kandy.add("Peradeniya");
        kandy.add("Kundasale");

        ArrayList<String> kegalle = new ArrayList<String>();
        kegalle.add("Kegalle");
        kegalle.add("Mawanella");
        kegalle.add("Warakapola");
        kegalle.add("Rabukkana");
        kegalle.add("Ruwanwella");

        ArrayList<String> kilinochchi = new ArrayList<String>();
        kilinochchi.add("Kilinochchi");

        ArrayList<String> kurunegala = new ArrayList<String>();
        kurunegala.add("Kurunegala");
        kurunegala.add("Kuliyapitiya");
        kurunegala.add("Narammala");
        kurunegala.add("Pannala");
        kurunegala.add("Mawathagama");

        ArrayList<String> monaragala = new ArrayList<String>();
        monaragala.add("Monaragala");
        monaragala.add("Wellawaya");
        monaragala.add("Bibile");
        monaragala.add("Kataragama");
        monaragala.add("Buttala");

        ArrayList<String> mulativ = new ArrayList<String>();
        mulativ.add("Mulativ");

        ArrayList<String> ratnapura = new ArrayList<String>();
        ratnapura.add("Ratnapura");
        ratnapura.add("Pelmadulla");
        ratnapura.add("Balangoda");
        ratnapura.add("Embilipitiya");
        ratnapura.add("Eheliyagoda");

        ArrayList<String> vavuniya = new ArrayList<String>();
        vavuniya.add("Vavuniya");

        locations.put("Colombo", colombo);
        locations.put("Kalutara", kalutara);
        locations.put("Kandy", kandy);
        locations.put("Gampaha", gampaha);
        locations.put("Anuradhapura", anuradhapura);
        locations.put("Ampara", ampara);
        locations.put("Badulla", badulla);
        locations.put("Batticalo", batticalo);
        locations.put("Galle", galle);
        locations.put("Hambantota", hambantota);
        locations.put("Jaffna", jaffna);
        locations.put("Kegalle", kegalle);
        locations.put("Kilinochchi", kilinochchi);
        locations.put("Kurunegala", kurunegala);
        locations.put("Mannar", mannar);
        locations.put("Matale", matale);
        locations.put("Matara", matara);
        locations.put("Monaragala", monaragala);
        locations.put("Mulativ", mulativ);
        locations.put("Nuwaraeliya", nuwaraeliya);
        locations.put("Polonnaruwa", polonnaruwa);
        locations.put("Puttalam", puttalam);
        locations.put("Ratnapura", ratnapura);
        locations.put("Trincomalee", trincomalee);
        locations.put("Vavuniya", vavuniya);

        return locations;

    }
}
