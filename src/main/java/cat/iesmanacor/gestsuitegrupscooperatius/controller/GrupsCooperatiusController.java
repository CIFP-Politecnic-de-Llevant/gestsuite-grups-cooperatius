package cat.iesmanacor.gestsuitegrupscooperatius.controller;

import cat.iesmanacor.gestsuite.core.model.Notificacio;
import cat.iesmanacor.gestsuite.core.model.NotificacioTipus;
import cat.iesmanacor.gestsuite.core.model.gestib.Usuari;
import cat.iesmanacor.gestsuite.core.service.MathService;
import cat.iesmanacor.gestsuite.core.service.TokenManager;
import cat.iesmanacor.gestsuite.core.service.UsuariService;
import cat.iesmanacor.gestsuite.grupscooperatius.model.*;
import cat.iesmanacor.gestsuite.grupscooperatius.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GrupsCooperatiusController {

    @Autowired
    private UsuariService usuariService;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ValorItemService valorItemService;

    @Autowired
    private ValorItemMembreService valorItemMembreService;

    @Autowired
    private GrupCooperatiuService grupCooperatiuService;

    @Autowired
    private ItemGrupCooperatiuService itemGrupCooperatiuService;

    @Autowired
    private MembreService membreService;

    @Autowired
    private AgrupamentService agrupamentService;

    @Autowired
    private MathService mathService;

    @Autowired
    private Gson gson;

    /*-- GRUPS COOPERATIUS --*/
    @PostMapping("/apps/grupscooperatius/aleatori")
    public ResponseEntity<?> getMesclaGrupsAleatoria(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();
        String nom = "Sense nom";

        if (jsonGrupCooperatiu.get("nom") != null && !jsonGrupCooperatiu.get("nom").isJsonNull()) {
            jsonGrupCooperatiu.get("nom").getAsString();
        }

        //Usuaris i valors dels ítems
        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();
        List<Membre> membres = new ArrayList<>();
        for (JsonElement membreJSON : membresJSON) {

            Membre membre = new Membre();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());
            if (membreJSON.getAsJsonObject().get("agrupamentFixe") != null && !membreJSON.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                membre.setAgrupamentFixe(membreJSON.getAsJsonObject().get("agrupamentFixe").getAsString());
            }

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembre> valorsItemMembre = new ArrayList<>();
            for (JsonElement itemUsuari : itemsUsuari) {
                ValorItemMembre valorItemMembre = new ValorItemMembre();
                ValorItem valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }
            membre.setValorsItemMembre(new HashSet<>(valorsItemMembre));
            membres.add(membre);
        }

        int numGrups = jsonObject.get("numGrups").getAsInt();

        Collections.shuffle(membres);


        List<Membre>[] grups = new ArrayList[numGrups];
        for (int j = 0; j < grups.length; j++) {
            grups[j] = new ArrayList<>();
        }

        //Inserim membres fixes
        int maxMembresFixes = 0;
        for (Membre m : membres) {
            if (m.getAgrupamentFixe() != null) {
                //Elegim un grup a l'atzar també
                String agrupamentFixeRaw = m.getAgrupamentFixe();
                agrupamentFixeRaw = agrupamentFixeRaw.replaceAll("\\s+", "");
                String[] agrupamentsFixes = agrupamentFixeRaw.split(",");
                int rnd = new Random().nextInt(agrupamentsFixes.length);
                int agrupament = Integer.parseInt(agrupamentsFixes[rnd]);

                //Error en la introducció de dades. P. ex: hi ha 3 grups i posen 1,2,4, el 4 és un error, no existeix
                if (agrupament <= 0 || agrupament > grups.length) {
                    Notificacio notificacio = new Notificacio();
                    notificacio.setNotifyType(NotificacioTipus.ERROR);
                    notificacio.setNotifyMessage("Hi ha " + grups.length + " grups i s'ha introduit el nombre " + agrupament + " com a grup.");
                    return new ResponseEntity<>(notificacio, HttpStatus.OK);
                }

                grups[agrupament - 1].add(m);
                if (grups[agrupament - 1].size() > maxMembresFixes) {
                    maxMembresFixes = grups[agrupament - 1].size();
                }
            }
        }
        System.out.println("Max membres fixes: " + maxMembresFixes);

        //Acabem d'inserir la resta de membres
        int i = 0;
        for (Membre m : membres) {
            if (m.getAgrupamentFixe() == null) {
                int j = 0;
                while (grups[i % numGrups].size() >= maxMembresFixes && j < numGrups) {
                    i++;
                    j++;
                }
                grups[i % numGrups].add(m);
                i++;
            }
        }


        //Pintem els resultats
        List<Agrupament> agrupaments = new ArrayList<>();

        for (int j = 0; j < grups.length; j++) {
            Agrupament agrupament = new Agrupament();
            Set membresSet = new HashSet(grups[j]);
            agrupament.setMembres(membresSet);
            agrupament.setNumero(String.valueOf(j + 1));

            agrupaments.add(agrupament);
        }

        return new ResponseEntity<>(agrupaments, HttpStatus.OK);
    }


    @PostMapping("/apps/grupscooperatius/genetica")
    public ResponseEntity<?> getMesclaGrupsGenetica(@RequestBody String json) {
        //int numIteracions = 10;

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        int numIteracions = jsonObject.get("iteracions").getAsInt();
        int percentatgeAmics = jsonObject.get("percentatgeAmics").getAsInt();
        int percentatgeEnemics = jsonObject.get("percentatgeEnemics").getAsInt();
        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();

        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();


        JsonArray itemsGrupCooperatiuJSON = jsonGrupCooperatiu.get("itemsGrupCooperatiu").getAsJsonArray();
        List<ItemGrupCooperatiu> itemsGrupCooperatiu = new ArrayList<>();
        for (JsonElement itemGrupCooperatiuJSON : itemsGrupCooperatiuJSON) {

            Item it = itemService.getItemById(itemGrupCooperatiuJSON.getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong());
            Integer ponderacio = itemGrupCooperatiuJSON.getAsJsonObject().get("percentatge").getAsInt();

            ItemGrupCooperatiu itemGrupCooperatiu = new ItemGrupCooperatiu();
            itemGrupCooperatiu.setItem(it);
            itemGrupCooperatiu.setPercentatge(ponderacio);

            itemsGrupCooperatiu.add(itemGrupCooperatiu);
        }

        //Usuaris i valors dels ítems
        //JsonArray membresJSON = jsonMembers.get("members").getAsJsonArray();
        List<Membre> membres = new ArrayList<>();
        for (JsonElement membreJSON : membresJSON) {

            Membre membre = new Membre();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());
            if (membreJSON.getAsJsonObject().get("agrupamentFixe") != null && !membreJSON.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                membre.setAgrupamentFixe(membreJSON.getAsJsonObject().get("agrupamentFixe").getAsString());
            }

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembre> valorsItemMembre = new ArrayList<>();
            for (JsonElement itemUsuari : itemsUsuari) {
                ValorItemMembre valorItemMembre = new ValorItemMembre();
                ValorItem valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }

            membre.setValorsItemMembre(new TreeSet<>(valorsItemMembre));


            if (membreJSON.getAsJsonObject().get("amics") != null && !membreJSON.getAsJsonObject().get("amics").isJsonNull()) {
                JsonArray amicsJson = membreJSON.getAsJsonObject().get("amics").getAsJsonArray();
                List<Membre> amics = new ArrayList<>();
                for (JsonElement amic : amicsJson) {
                    Membre membreAmic = new Membre();
                    membreAmic.setNom(amic.getAsString());

                    amics.add(membreAmic);
                }
                membre.setAmics(new HashSet<>(amics));
            }

            if (membreJSON.getAsJsonObject().get("enemics") != null && !membreJSON.getAsJsonObject().get("enemics").isJsonNull()) {
                JsonArray enemicsJson = membreJSON.getAsJsonObject().get("enemics").getAsJsonArray();
                List<Membre> enemics = new ArrayList<>();
                for (JsonElement enemic : enemicsJson) {
                    Membre membreEnemic = new Membre();
                    membreEnemic.setNom(enemic.getAsString());

                    enemics.add(membreEnemic);
                }
                membre.setEnemics(new HashSet<>(enemics));
            }

            membres.add(membre);
        }


        int numGrups = jsonObject.get("numGrups").getAsInt();

        if (numGrups <= 1) {
            Notificacio notificacio = new Notificacio();
            notificacio.setNotifyType(NotificacioTipus.ERROR);
            notificacio.setNotifyMessage("El nombre de grups ha de ser més gran que 1");
            return new ResponseEntity<>(notificacio, HttpStatus.OK);
        }

        List<Agrupament> agrupaments = mesclaMembres(membres, numGrups, numIteracions, percentatgeAmics, percentatgeEnemics);
        return new ResponseEntity<>(agrupaments, HttpStatus.OK);
    }

    /* TODO: Acabar la segona manera de fer la mescla per aproximació */
    private List<Agrupament> mesclaMembres2(List<Membre> membres, int numGrups, int numIteracions, int percentatgeAmics, int percentatgeEnemics) {
        List<Membre>[] millorsAgrupacions = new ArrayList[numGrups];
        Double millorPuntuacio = null;

        Collections.shuffle(membres);

        List<Membre>[] grups = new ArrayList[numGrups];
        for (int j = 0; j < grups.length; j++) {
            grups[j] = new ArrayList<>();
        }

        //Inserim membres fixes
        for (Membre m : membres) {
            if (m.getAgrupamentFixe() != null && !m.getAgrupamentFixe().isEmpty()) {
                //Elegim un grup a l'atzar també
                String agrupamentFixeRaw = m.getAgrupamentFixe().replaceAll("\\s+", "");
                String[] agrupamentsFixes = agrupamentFixeRaw.split(",");

                int rnd = new Random().nextInt(agrupamentsFixes.length);
                int agrupament = Integer.parseInt(agrupamentsFixes[rnd]);

                grups[agrupament - 1].add(m);
            }
        }
        //System.out.println("Max membres fixes: "+maxMembresFixes);

        //Acabem d'inserir la resta de membres
        for (Membre m : membres) {
            if (m.getAgrupamentFixe() == null || m.getAgrupamentFixe().isEmpty()) {
                Integer minGrup = null;
                int index = 0;
                int i = 0;
                for (List<Membre> grup : grups) {
                    if (minGrup == null || grup.size() <= minGrup) {
                        minGrup = grup.size();
                        index = i;
                    }
                    i++;
                }
                grups[index].add(m);
            }
        }

        int idx = 0;
        double puntuacio = 0;
        while (idx < numIteracions || millorPuntuacio==0.0){
            for (List<Membre> grup : grups) {
                for(Membre membre: grup){
                    if (membre.getAmics() != null && membre.getAmics().size() > 0) {
                        double numAmics = 0;
                        for (Membre amic : membre.getAmics()) {
                            for (Membre membreGrup : grup) {
                                if (amic.getNom().equals(membreGrup.getNom())) {
                                    numAmics++;
                                }
                            }
                        }

                        if(numAmics==0){

                        }


                    }
                }
            }
            idx++;
        }
        return null;
    }

    private List<Agrupament> mesclaMembres(List<Membre> membres, int numGrups, int numIteracions, int percentatgeAmics, int percentatgeEnemics) {
        List<Membre>[] millorsAgrupacions = new ArrayList[numGrups];
        Double millorPuntuacio = null;
        Double millorPuntuacioTamanyGrup = null;

        int infoIteracio = numIteracions / 100;
        for (int k = 0; k < numIteracions; k++) {

            if (k % infoIteracio == 0) {
                log.info("Iteració " + k);
            }

            Collections.shuffle(membres);

            List<Membre>[] grups = new ArrayList[numGrups];
            for (int j = 0; j < grups.length; j++) {
                grups[j] = new ArrayList<>();
            }

            //Inserim membres fixes
            for (Membre m : membres) {
                if (m.getAgrupamentFixe() != null && !m.getAgrupamentFixe().isEmpty()) {
                    //Elegim un grup a l'atzar també
                    String agrupamentFixeRaw = m.getAgrupamentFixe().replaceAll("\\s+", "");
                    String[] agrupamentsFixes = agrupamentFixeRaw.split(",");

                    int rnd = new Random().nextInt(agrupamentsFixes.length);
                    int agrupament = Integer.parseInt(agrupamentsFixes[rnd]);

                    //Error en la introducció de dades. P. ex: hi ha 3 grups i posen 1,2,4, el 4 és un error, no existeix
                    /*if (agrupament <= 0 || agrupament > grups.length) {
                        Notificacio notificacio = new Notificacio();
                        notificacio.setNotifyType(NotificacioTipus.ERROR);
                        notificacio.setNotifyMessage("Hi ha " + grups.length + " grups i s'ha introduit el nombre " + agrupament + " com a grup.");
                        return new ResponseEntity<>(notificacio, HttpStatus.OK);
                    }*/
                    grups[agrupament - 1].add(m);
                }
            }
            //System.out.println("Max membres fixes: "+maxMembresFixes);

            //Acabem d'inserir la resta de membres
            for (Membre m : membres) {
                if (m.getAgrupamentFixe() == null || m.getAgrupamentFixe().isEmpty()) {
                    Integer minGrup = null;
                    int index = 0;
                    int i = 0;
                    for (List<Membre> grup : grups) {
                        if (minGrup == null || grup.size() <= minGrup) {
                            minGrup = grup.size();
                            index = i;
                        }
                        i++;
                    }
                    grups[index].add(m);
                }
            }


            double puntuacio = 0;

           /* double[] tamanyGrups = new double[grups.length];
            for (int j = 0; j < grups.length; j++) {
                tamanyGrups[j] = grups[j].size();
            }
            double puntuacioTamanyGrups = mathService.standardDeviation(tamanyGrups);
            puntuacio += puntuacioTamanyGrups * 100000;

            //Optimització per podar els grups no homogenis
            if (millorPuntuacioTamanyGrup == null || puntuacioTamanyGrups <= millorPuntuacioTamanyGrup) {
                millorPuntuacioTamanyGrup = puntuacioTamanyGrups;
            } else {
                continue;
            }*/

            /*for(ItemGrupCooperatiu itemGrupCooperatiu: itemsGrupCooperatiu){
                for(ValorItem valorItem: itemGrupCooperatiu.getItem().getValorItems()){
                    List<Double> valorsItemsComptador = new ArrayList<>();

                    for(List<Membre> grup: grups) {
                        Double count = 0.0;
                        for (Membre membre : grup) {
                            for (ValorItemMembre vim : membre.getValorsItemMembre()) {
                                if (vim.getValorItem().getIdvalorItem().equals(valorItem.getIdvalorItem())) {
                                    count++;
                                }
                            }
                        }
                        valorsItemsComptador.add( count * valorItem.getPes());
                    }

                    double[] valorsItemsCountPrimitive = new double[valorsItemsComptador.size()];
                    int idx = 0;
                    for(Double d: valorsItemsComptador){
                        valorsItemsCountPrimitive[idx] = d;
                        idx++;
                    }

                    //System.out.println("Puntuació abans item "+k+": "+ puntuacio);
                    puntuacio += mathService.standardDeviation(valorsItemsCountPrimitive)*itemGrupCooperatiu.getPercentatge()*0.01;

                    //System.out.println("Puntuació despres item"+ puntuacio);
                }
            }*/


            //Amistats i enemistats
            double[] amics = new double[grups.length];
            double[] teAmics = new double[grups.length];
            double[] enemics = new double[grups.length];

            for (int i = 0; i < grups.length; i++) {
                amics[i] = 0;
                teAmics[i] = 0;
                enemics[i] = 0;
            }

            int idx = 0;
            for (List<Membre> grup : grups) {
                double[] grupAmics = new double[grup.size()];
                double[] grupTeAmics = new double[grup.size()];
                double[] grupEnemics = new double[grup.size()];

                int idxgrup = 0;

                for (int i = 0; i < grup.size(); i++) {
                    grupAmics[i] = 0;
                    grupTeAmics[i] = 0;
                    grupEnemics[i] = 0;
                }
                for (Membre membre : grup) {
                    if (membre.getAmics() != null && membre.getAmics().size() > 0) {
                        boolean teAmic = false;
                        double numAmics = 0;
                        for (Membre amic : membre.getAmics()) {
                            for (Membre membreGrup : grup) {
                                if (amic.getNom().equals(membreGrup.getNom())) {
                                    numAmics++;
                                    teAmic = true;
                                }
                            }
                        }

                        grupAmics[idxgrup] = (numAmics * 100) / membre.getAmics().size();
                        if (teAmic) {
                            grupTeAmics[idxgrup] = 100;
                        }
                    } else {
                        grupAmics[idxgrup] = 100;
                        grupTeAmics[idxgrup] = 100;
                    }

                    if (membre.getEnemics() != null && membre.getEnemics().size() > 0) {
                        double numEnemics = 0;
                        for (Membre enemic : membre.getEnemics()) {
                            boolean enemicTrobat = false;
                            for (Membre membreGrup : grup) {
                                if (enemic.getNom().equals(membreGrup.getNom())) {
                                    enemicTrobat = true;
                                    break;
                                }
                            }

                            //En aquest cas, l'èxit es medeix si NO el trobem
                            if (!enemicTrobat) {
                                numEnemics++;
                            }
                        }

                        grupEnemics[idxgrup] = (numEnemics * 100) / membre.getEnemics().size();
                    } else {
                        grupEnemics[idxgrup] = 100;
                    }

                    idxgrup++;
                }

                amics[idx] = mathService.mean(grupAmics);
                teAmics[idx] = mathService.mean(grupTeAmics);
                enemics[idx] = mathService.mean(grupEnemics);

                idx++;
            }

            // System.out.println("Puntuació abans"+ puntuacio);
            if (mathService.mean(amics) > 0) {
                //puntuacio += ( (puntuacio/percentatgeAmics) /(mathService.mean(amics)*0.01))*0.1;
                //puntuacio -= mathService.mean(amics)*(itemsGrupCooperatiu.size()+grups.length)*percentatgeAmics*0.01*0.1;
                //puntuacio -= mathService.mean(teAmics)*(itemsGrupCooperatiu.size()+grups.length)*percentatgeAmics*0.01*0.9;
                puntuacio -= mathService.mean(teAmics) * percentatgeAmics;
            } else {
                //puntuacio *= percentatgeAmics;
            }
            //System.out.println("Puntuació despres"+ puntuacio);

            if (mathService.mean(enemics) > 0) {
                //puntuacio -= mathService.mean(enemics)*(itemsGrupCooperatiu.size()+grups.length)*percentatgeEnemics*0.01;
                puntuacio -= mathService.mean(enemics) * percentatgeEnemics;
            } else {
                //puntuacio *= percentatgeEnemics;
            }


            if (millorPuntuacio == null || puntuacio < millorPuntuacio) {
                millorsAgrupacions = grups;
                millorPuntuacio = puntuacio;


                //Pintem els resultats
                int membresSenseAmics = 0;
                int membresAmbEnemics = 0;
                for (int j = 0; j < millorsAgrupacions.length; j++) {
                    Agrupament agrupament = new Agrupament();
                    agrupament.setNumero(String.valueOf(j + 1));
                    Set membresSet = new HashSet(millorsAgrupacions[j]);
                    agrupament.setMembres(membresSet);

                    log.info("Grup " + (j + 1));
                    for (Membre membre : agrupament.getMembres()) {
                        String result = membre.getNom();

                        if (membre.getAmics().size() > 0) {
                            int numAmics = 0;
                            for (Membre amic : membre.getAmics()) {
                                for (Membre membre2 : agrupament.getMembres()) {
                                    if (membre2.getNom().equals(amic.getNom())) {
                                        numAmics++;
                                    }
                                }
                            }
                            if (numAmics == 0) {
                                membresSenseAmics++;
                            }
                            result += " Amics: " + numAmics + "/" + membre.getAmics().size();
                        }

                        if (membre.getEnemics().size() > 0) {
                            int numEnemics = 0;
                            for (Membre enemic : membre.getEnemics()) {
                                for (Membre membre2 : agrupament.getMembres()) {
                                    if (membre2.getNom().equals(enemic.getNom())) {
                                        numEnemics++;
                                    }
                                }
                            }
                            if (numEnemics > 0) {
                                membresAmbEnemics++;
                            }
                            result += " Enemics: " + numEnemics + "/" + membre.getEnemics().size();
                        }

                        log.info(result);
                    }
                    log.info("Membres sense cap amic: " + membresSenseAmics);
                    log.info("Membres amb algun enemic" + membresAmbEnemics);
                    log.info("---------------------------");
                    log.info("");
                    log.info("");
                }
            }
        }

        System.out.println("La millor puntuació ha estat" + millorPuntuacio);

        //Pintem els resultats
        int membresSenseAmics = 0;
        int membresAmbEnemics = 0;
        List<Agrupament> agrupaments = new ArrayList<>();

        for (int j = 0; j < millorsAgrupacions.length; j++) {
            Agrupament agrupament = new Agrupament();
            agrupament.setNumero(String.valueOf(j + 1));
            Set membresSet = new HashSet(millorsAgrupacions[j]);
            agrupament.setMembres(membresSet);

            agrupaments.add(agrupament);

            log.info("Grup " + (j + 1));
            for (Membre membre : agrupament.getMembres()) {
                String result = membre.getNom();

                if (membre.getAmics().size() > 0) {
                    int numAmics = 0;
                    for (Membre amic : membre.getAmics()) {
                        for (Membre membre2 : agrupament.getMembres()) {
                            if (membre2.getNom().equals(amic.getNom())) {
                                numAmics++;
                            }
                        }
                    }
                    if (numAmics == 0) {
                        membresSenseAmics++;
                    }
                    result += " Amics: " + numAmics + "/" + membre.getAmics().size();
                }

                if (membre.getEnemics().size() > 0) {
                    int numEnemics = 0;
                    for (Membre enemic : membre.getEnemics()) {
                        for (Membre membre2 : agrupament.getMembres()) {
                            if (membre2.getNom().equals(enemic.getNom())) {
                                numEnemics++;
                            }
                        }
                    }
                    if (numEnemics > 0) {
                        membresAmbEnemics++;
                    }
                    result += " Enemics: " + numEnemics + "/" + membre.getEnemics().size();
                }

                log.info(result);
            }
            log.info("Membres sense cap amic: " + membresSenseAmics);
            log.info("Membres amb algun enemic" + membresAmbEnemics);
            log.info("---------------------------");
            log.info("");
            log.info("");
            log.info("");
        }

        log.info("FINAL DE LA MESCLA");

        return agrupaments;
    }

    @PostMapping("/apps/grupscooperatius/mescla/desar")
    public ResponseEntity<Notificacio> saveGrupCooperatiu(@RequestBody String json, HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        //PARSE JSON
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);


        //Grup Cooperatiu
        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();

        GrupCooperatiu grupCooperatiu = new GrupCooperatiu();

        //Si ja existeix ho esborrem tot
        if (jsonGrupCooperatiu.get("id") != null && !jsonGrupCooperatiu.get("id").isJsonNull()) {
            grupCooperatiu.setIdgrupCooperatiu(jsonGrupCooperatiu.get("id").getAsLong());
        }

        String nom = "Sense nom";

        if (jsonGrupCooperatiu.get("nom") != null && !jsonGrupCooperatiu.get("nom").isJsonNull()) {
            nom = jsonGrupCooperatiu.get("nom").getAsString();
        }

        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        //LocalDateTime now = LocalDateTime.now();
        //nom += " - "+dtf.format(now);

        grupCooperatiu.setNom(nom);
        grupCooperatiu.setUsuari(myUser);

        GrupCooperatiu grupCooperatiuSaved = grupCooperatiuService.save(grupCooperatiu);


        //Items grup cooperatiu
        List<ItemGrupCooperatiu> itemsGrupCooperatiu = new ArrayList<>();
        JsonArray jsonItemsGrupCooperatiu = jsonGrupCooperatiu.get("itemsGrupCooperatiu").getAsJsonArray();
        for (JsonElement jsonItemGrupCooperatiu : jsonItemsGrupCooperatiu) {
            Item item = itemService.getItemById(jsonItemGrupCooperatiu.getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong());
            Integer percentatge = jsonItemGrupCooperatiu.getAsJsonObject().get("percentatge").getAsInt();

            ItemGrupCooperatiu itemGrupCooperatiu = new ItemGrupCooperatiu();
            itemGrupCooperatiu.setGrupCooperatiu(grupCooperatiuSaved);
            itemGrupCooperatiu.setItem(item);
            itemGrupCooperatiu.setPercentatge(percentatge);

            itemGrupCooperatiuService.save(itemGrupCooperatiu);
        }

        //Members
        grupCooperatiuSaved.getMembres().clear();
        List<Membre> membres = new ArrayList<>();
        if (jsonObject.get("members") != null && !jsonObject.get("members").isJsonNull()) {
            JsonArray jsonMembers = jsonObject.get("members").getAsJsonArray();
            for (JsonElement jsonMember : jsonMembers) {
                String nomMembre = jsonMember.getAsJsonObject().get("nom").getAsString();
                String agrupamentFixeMembre = null;
                if (jsonMember.getAsJsonObject().get("agrupamentFixe") != null && !jsonMember.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                    agrupamentFixeMembre = jsonMember.getAsJsonObject().get("agrupamentFixe").getAsString();
                }

                Membre membre = new Membre();
                membre.setNom(nomMembre);
                membre.setAgrupamentFixe(agrupamentFixeMembre);
                membre.setGrupCooperatiu(grupCooperatiuSaved);

                Membre membreSaved = membreService.save(membre);

                membres.add(membreSaved);
            }


            //Com que els membres amics i enemics són membres també, tornem a recorrer l'array i l'adjuntem dins els membres
            //Amb els "Valors item membre" passa el mateix, desem primer el membre.
            for (JsonElement jsonMember : jsonMembers) {
                Membre membreSaved = membres.stream().filter(m -> m.getNom().equals(jsonMember.getAsJsonObject().get("nom").getAsString())).collect(Collectors.toList()).get(0);

                //Amics i enemics
                List<Membre> membresAmics = new ArrayList<>();
                JsonArray amics = jsonMember.getAsJsonObject().get("amics").getAsJsonArray();
                for (JsonElement amic : amics) {
                    if (membres.stream().filter(m -> m.getNom().equals(amic.getAsString())) != null && membres.stream().filter(m -> m.getNom().equals(amic.getAsString())).collect(Collectors.toList()).size() > 0) {
                        Membre membreAmic = membres.stream().filter(m -> m.getNom().equals(amic.getAsString())).collect(Collectors.toList()).get(0);
                        membresAmics.add(membreAmic);
                    }
                }

                List<Membre> membresEnemics = new ArrayList<>();
                JsonArray enemics = jsonMember.getAsJsonObject().get("enemics").getAsJsonArray();
                for (JsonElement enemic : enemics) {
                    if (membres.stream().filter(m -> m.getNom().equals(enemic.getAsString())) != null && membres.stream().filter(m -> m.getNom().equals(enemic.getAsString())).collect(Collectors.toList()).size() > 0) {
                        Membre membreEnemic = membres.stream().filter(m -> m.getNom().equals(enemic.getAsString())).collect(Collectors.toList()).get(0);
                        membresEnemics.add(membreEnemic);
                    }
                }

                membreSaved.setAmics(new HashSet<>(membresAmics));
                membreSaved.setEnemics(new HashSet<>(membresEnemics));


                //Valors Item
                List<ValorItem> valorsItem = new ArrayList<>();
                JsonArray jsonValorsItems = jsonMember.getAsJsonObject().get("valorsItemMembre").getAsJsonArray();
                for (JsonElement jsonValorItem : jsonValorsItems) {
                    Long idValorItem = jsonValorItem.getAsJsonObject().get("valorItem").getAsJsonObject().get("id").getAsLong();
                    ValorItem valorItem = valorItemService.findById(idValorItem);
                    valorsItem.add(valorItem);
                }

                List<ValorItemMembre> valorsItemsMembres = new ArrayList<>();
                for (ValorItem vi : valorsItem) {
                    ValorItemMembre valorItemMembre = new ValorItemMembre();
                    valorItemMembre.setMembre(membreSaved);
                    valorItemMembre.setValorItem(vi);

                    ValorItemMembre valorItemMembreSaved = valorItemMembreService.save(valorItemMembre);

                    valorsItemsMembres.add(valorItemMembreSaved);
                }
                //membreSaved.setValorsItemMembre(new HashSet<>(valorsItemsMembres));

                membreService.save(membreSaved);
            }
        }


        //Resultat (Agrupaments)
        grupCooperatiuSaved.getAgrupaments().clear();
        List<Agrupament> agrupaments = new ArrayList<>();
        if (jsonObject.get("resultat") != null && !jsonObject.get("resultat").isJsonNull()) {
            JsonArray jsonAgrupaments = jsonObject.get("resultat").getAsJsonArray();
            for (JsonElement jsonAgrupament : jsonAgrupaments) {
                String numero = jsonAgrupament.getAsJsonObject().get("numero").getAsString();

                Agrupament agrupament = new Agrupament();
                agrupament.setNumero(numero);
                agrupament.setGrupCooperatiu(grupCooperatiuSaved);

                Agrupament agrupamentSaved = agrupamentService.save(agrupament);

                JsonArray jsonMembres = jsonAgrupament.getAsJsonObject().get("membres").getAsJsonArray();
                for (JsonElement jsonMember : jsonMembres) {
                    Membre membreSaved = membres.stream().filter(m -> m.getNom().equals(jsonMember.getAsJsonObject().get("nom").getAsString())).collect(Collectors.toList()).get(0);
                    membreSaved.setAgrupament(agrupamentSaved);

                    membreService.save(membreSaved);
                }


            }
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Resultat de la mescla desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }


    @GetMapping("/apps/grupscooperatius/grupscooperatiususuari")
    public ResponseEntity<List<GrupCooperatiu>> getGrupsCooperatius(HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        List<GrupCooperatiu> grupsCooperatiusUsuari = grupCooperatiuService.findAllByUsuari(myUser);

        return new ResponseEntity<>(grupsCooperatiusUsuari, HttpStatus.OK);
    }

    @GetMapping("/apps/grupscooperatius/grupcooperatiu/{id}")
    public ResponseEntity<GrupCooperatiu> getGrupCooperatiu(@PathVariable("id") String idGrupCooperatiu, HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        List<GrupCooperatiu> grupsCooperatiusUsuari = grupCooperatiuService.findAllByUsuari(myUser);

        //Dels grups cooperatius de l'usuari agafem el que tingui la ID passada per paràmtre
        GrupCooperatiu grupCooperatiu = grupsCooperatiusUsuari.stream().filter(gc -> gc.getIdgrupCooperatiu().equals(Long.valueOf(idGrupCooperatiu))).collect(Collectors.toList()).get(0);
        List<ItemGrupCooperatiu> itemsGrupCooperatiu = itemGrupCooperatiuService.findAllByGrupCooperatiu(grupCooperatiu);
        itemsGrupCooperatiu.sort(Comparator.comparing(a -> a.getItem().getIditem()));
        grupCooperatiu.setItemsGrupsCooperatius(new TreeSet<>(itemsGrupCooperatiu));

        for (Membre membre : grupCooperatiu.getMembres()) {
            membre.setValorsItemMembre(new TreeSet<>(membre.getValorsItemMembre()));
        }

        /*for(Membre membre:grupCooperatiu.getMembres()){
                ArrayList<ValorItemMembre> valorsItemMembre = new ArrayList<>(membre.getValorsItemMembre());
                valorsItemMembre.sort(Comparator.comparing(a -> a.getValorItem().getItem().getIditem()));

            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("------------------------------");
            System.out.println(membre.getNom());
                for(ValorItemMembre valorItemMembre: valorsItemMembre){
                    System.out.println("valor item membre"+valorItemMembre.getValorItem().getItem().getIditem()+" - "+valorItemMembre.getValorItem().getItem().getNom());
                }

                membre.setValorsItemMembre(new TreeSet<>(valorsItemMembre));
        }*/

        /*
        for(Membre membre:grupCooperatiu.getMembres()){
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("------------------------------");
            System.out.println(membre.getNom());
            for(ValorItemMembre valorItemMembre: membre.getValorsItemMembre()){
                System.out.println("valor item membre"+valorItemMembre.getValorItem().getItem().getIditem()+" - "+valorItemMembre.getValorItem().getItem().getNom());
            }
        }*/

        List<Agrupament> agrupaments = agrupamentService.findAllByGrupCooperatiu(grupCooperatiu);
        grupCooperatiu.setAgrupaments(new HashSet<>(agrupaments));

        return new ResponseEntity<>(grupCooperatiu, HttpStatus.OK);
    }

    /*-- ITEMS --*/
    @GetMapping("/apps/grupscooperatius/items")
    public ResponseEntity<List<Item>> getItems(HttpServletRequest request) {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        List<Item> items = itemService.findAllByUsuari(myUser);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/apps/grupscooperatius/item/{id}")
    public ResponseEntity<Item> getItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Item item = itemService.getItemById(Long.valueOf(iditem));

        if (item != null && item.getUsuari().getIdusuari().equals(myUser.getIdusuari())) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/apps/grupscooperatius/item/valors/{id}")
    public ResponseEntity<List<ValorItem>> getValorsItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        Item item = itemService.getItemById(Long.valueOf(iditem));
        List<ValorItem> valors = valorItemService.findAllValorsByItem(item);

        //Seguretat
        boolean usuariCorrecte = valors.stream().allMatch(valorItem -> valorItem.getItem().getUsuari().getIdusuari().equals(myUser.getIdusuari()));

        if (valors != null && usuariCorrecte) {
            return new ResponseEntity<>(valors, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/apps/grupscooperatius/item/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarItemGrupCooperatiu(@RequestBody String json, HttpServletRequest request) throws GeneralSecurityException, IOException {
        Claims claims = tokenManager.getClaims(request);
        String myEmail = (String) claims.get("email");

        Usuari myUser = usuariService.findByEmail(myEmail);

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idItem = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()) {
            idItem = jsonObject.get("id").getAsLong();
        }

        String nomItem = jsonObject.get("nom").getAsString();

        JsonArray valors = jsonObject.get("valorsItem").getAsJsonArray();

        Item item;

        if (idItem != null) {
            item = itemService.getItemById(idItem);
        } else {
            item = new Item();
        }

        item.setUsuari(myUser);
        item.setNom(nomItem);

        Item i = itemService.save(item);

        //Valors
        List<ValorItem> valorsItem = new ArrayList<>();
        for (JsonElement valor : valors) {
            ValorItem vi = new ValorItem();
            vi.setValor(valor.getAsJsonObject().get("valor").getAsString());
            vi.setPes(valor.getAsJsonObject().get("pes").getAsInt());
            vi.setItem(i);

            valorsItem.add(vi);
        }

        //Esborrem antics valors
        itemService.deleteAllValorsByItem(i);

        //Creem els nous
        for (ValorItem valorItem : valorsItem) {
            valorItemService.save(valorItem);
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Ítem desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

}