package cat.iesmanacor.gestsuitegrupscooperatius.controller;

import cat.iesmanacor.common.model.Notificacio;
import cat.iesmanacor.common.model.NotificacioTipus;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.*;
import cat.iesmanacor.gestsuitegrupscooperatius.dto.gestib.UsuariDto;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ItemGrupCooperatiu;
import cat.iesmanacor.gestsuitegrupscooperatius.model.Membre;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItem;
import cat.iesmanacor.gestsuitegrupscooperatius.model.ValorItemMembre;
import cat.iesmanacor.gestsuitegrupscooperatius.restclient.CoreRestClient;
import cat.iesmanacor.gestsuitegrupscooperatius.service.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GrupsCooperatiusController {

    private final CoreRestClient coreRestClient;
    private final ItemService itemService;
    private final ValorItemService valorItemService;
    private final ValorItemMembreService valorItemMembreService;
    private final GrupCooperatiuService grupCooperatiuService;
    private final ItemGrupCooperatiuService itemGrupCooperatiuService;
    private final MembreService membreService;
    private final AgrupamentService agrupamentService;
    private final MathService mathService;
    private final Gson gson;

    @Autowired
    public GrupsCooperatiusController(CoreRestClient coreRestClient, ItemService itemService, ValorItemService valorItemService, ValorItemMembreService valorItemMembreService, GrupCooperatiuService grupCooperatiuService, ItemGrupCooperatiuService itemGrupCooperatiuService, MembreService membreService, AgrupamentService agrupamentService, MathService mathService, Gson gson) {
        this.coreRestClient = coreRestClient;
        this.itemService = itemService;
        this.valorItemService = valorItemService;
        this.valorItemMembreService = valorItemMembreService;
        this.grupCooperatiuService = grupCooperatiuService;
        this.itemGrupCooperatiuService = itemGrupCooperatiuService;
        this.membreService = membreService;
        this.agrupamentService = agrupamentService;
        this.mathService = mathService;
        this.gson = gson;
    }

    /*-- GRUPS COOPERATIUS --*/
    /* TODO - Hi ha un error de recursivitat infinita als membres. Copiar la solució de la mescla genètica */
    @PostMapping("/aleatori")
    public ResponseEntity<?> getMesclaGrupsAleatoria(@RequestBody String json) {
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();
        String nom = "Sense nom";

        if (jsonGrupCooperatiu.get("nom") != null && !jsonGrupCooperatiu.get("nom").isJsonNull()) {
            jsonGrupCooperatiu.get("nom").getAsString();
        }

        //Usuaris i valors dels ítems
        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();
        List<MembreDto> membres = new ArrayList<>();
        for (JsonElement membreJSON : membresJSON) {

            MembreDto membre = new MembreDto();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());
            if (membreJSON.getAsJsonObject().get("agrupamentFixe") != null && !membreJSON.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                membre.setAgrupamentFixe(membreJSON.getAsJsonObject().get("agrupamentFixe").getAsString());
            }

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembreDto> valorsItemMembre = new ArrayList<>();
            for (JsonElement itemUsuari : itemsUsuari) {
                ValorItemMembreDto valorItemMembre = new ValorItemMembreDto();
                ValorItemDto valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }
            membre.setValorsItemMembre(new HashSet<>(valorsItemMembre));
            membres.add(membre);
        }

        int numGrups = jsonObject.get("numGrups").getAsInt();

        Collections.shuffle(membres);


        List<MembreDto>[] grups = new ArrayList[numGrups];
        for (int j = 0; j < grups.length; j++) {
            grups[j] = new ArrayList<>();
        }

        //Inserim membres fixes
        int maxMembresFixes = 0;
        for (MembreDto m : membres) {
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
        for (MembreDto m : membres) {
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
        List<AgrupamentDto> agrupaments = new ArrayList<>();

        for (int j = 0; j < grups.length; j++) {
            AgrupamentDto agrupament = new AgrupamentDto();
            Set membresSet = new HashSet(grups[j]);
            agrupament.setMembres(membresSet);
            agrupament.setNumero(String.valueOf(j + 1));

            agrupaments.add(agrupament);
        }

        return new ResponseEntity<>(agrupaments, HttpStatus.OK);
    }


    @PostMapping("/genetica")
    public ResponseEntity<?> getMesclaGrupsGenetica(@RequestBody String json) {
        //int numIteracions = 10;

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        int numIteracions = jsonObject.get("iteracions").getAsInt();
        int percentatgeAmics = jsonObject.get("percentatgeAmics").getAsInt();
        int percentatgeEnemics = jsonObject.get("percentatgeEnemics").getAsInt();
        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();

        JsonArray membresJSON = jsonObject.get("members").getAsJsonArray();


        JsonArray itemsGrupCooperatiuJSON = jsonGrupCooperatiu.get("itemsGrupCooperatiu").getAsJsonArray();
        List<ItemGrupCooperatiuDto> itemsGrupCooperatiu = new ArrayList<>();
        for (JsonElement itemGrupCooperatiuJSON : itemsGrupCooperatiuJSON) {

            ItemDto it = itemService.getItemById(itemGrupCooperatiuJSON.getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong());
            Integer ponderacio = itemGrupCooperatiuJSON.getAsJsonObject().get("percentatge").getAsInt();
            List<ValorItemDto> valorsItemDto = valorItemService.findAllValorsByItem(it);

            ItemGrupCooperatiuDto itemGrupCooperatiu = new ItemGrupCooperatiuDto();
            itemGrupCooperatiu.setItem(it);
            itemGrupCooperatiu.setPercentatge(ponderacio);
            itemGrupCooperatiu.getItem().setValorItems(new HashSet<>(valorsItemDto));

            itemsGrupCooperatiu.add(itemGrupCooperatiu);
        }

        //Usuaris i valors dels ítems
        //JsonArray membresJSON = jsonMembers.get("members").getAsJsonArray();
        List<MembreDto> membres = new ArrayList<>();
        for (JsonElement membreJSON : membresJSON) {

            MembreDto membre = new MembreDto();
            membre.setNom(membreJSON.getAsJsonObject().get("nom").getAsString());
            if (membreJSON.getAsJsonObject().get("agrupamentFixe") != null && !membreJSON.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                membre.setAgrupamentFixe(membreJSON.getAsJsonObject().get("agrupamentFixe").getAsString());
            }

            JsonArray itemsUsuari = membreJSON.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
            List<ValorItemMembreDto> valorsItemMembre = new ArrayList<>();
            for (JsonElement itemUsuari : itemsUsuari) {
                ValorItemMembreDto valorItemMembre = new ValorItemMembreDto();
                ValorItemDto valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                valorItemMembre.setMembre(membre);
                valorItemMembre.setValorItem(valorItem);

                valorsItemMembre.add(valorItemMembre);
            }

            membre.setValorsItemMembre(new TreeSet<>(valorsItemMembre));

            if (membreJSON.getAsJsonObject().get("amics") != null && !membreJSON.getAsJsonObject().get("amics").isJsonNull()) {
                JsonArray amicsJson = membreJSON.getAsJsonObject().get("amics").getAsJsonArray();
                List<MembreDto> amics = new ArrayList<>();
                for (JsonElement amic : amicsJson) {
                    MembreDto membreAmic = new MembreDto();
                    membreAmic.setNom(amic.getAsString());

                    amics.add(membreAmic);
                }
                membre.setAmics(new HashSet<>(amics));
            }

            if (membreJSON.getAsJsonObject().get("enemics") != null && !membreJSON.getAsJsonObject().get("enemics").isJsonNull()) {
                JsonArray enemicsJson = membreJSON.getAsJsonObject().get("enemics").getAsJsonArray();
                List<MembreDto> enemics = new ArrayList<>();
                for (JsonElement enemic : enemicsJson) {
                    MembreDto membreEnemic = new MembreDto();
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

        List<AgrupamentDto> agrupaments = mesclaMembres(itemsGrupCooperatiu,membres, numGrups, numIteracions, percentatgeAmics, percentatgeEnemics);
        List<AgrupamentDto> result = new ArrayList<>();
        agrupaments.forEach(a->{
            AgrupamentDto agrupament = new AgrupamentDto();
            agrupament.setNumero(a.getNumero());
            agrupament.setMembres(a.getMembres().stream().map(m->{
                MembreDto membre = new MembreDto();
                membre.setNom(m.getNom());

                List<ValorItemMembreDto> valorsItemMembre = new ArrayList<>();
                m.getValorsItemMembre().forEach(v->{
                    //Important. No afegir membre perquè membre.setValorsItemMembre de després seria recursiu
                    ValorItemMembreDto valorItemMembreDto = new ValorItemMembreDto();
                    valorItemMembreDto.setIdvalorItemMembre(v.getIdvalorItemMembre());
                    valorItemMembreDto.setValorItem(v.getValorItem());

                    valorsItemMembre.add(valorItemMembreDto);
                });
                membre.setValorsItemMembre(new TreeSet<>(valorsItemMembre));
                membre.setAmics(m.getAmics());
                membre.setEnemics(m.getEnemics());
                membre.setAgrupamentFixe(m.getAgrupamentFixe());

                return membre;
            }).collect(Collectors.toSet()));
            result.add(agrupament);
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private List<AgrupamentDto> mesclaMembres(List<ItemGrupCooperatiuDto> itemsGrupCooperatiuDto, List<MembreDto> membres, int numGrups, int numIteracions, int percentatgeAmics, int percentatgeEnemics) {
        List<MembreDto>[] millorsAgrupacions = new ArrayList[numGrups];
        Double millorPuntuacio = null;

        int infoIteracio = numIteracions / 100;
        for (int k = 0; k < numIteracions; k++) {

            if (k % infoIteracio == 0) {
                log.info("Iteració " + k);
            }

            Collections.shuffle(membres);

            List<MembreDto>[] grups = new ArrayList[numGrups];
            for (int j = 0; j < grups.length; j++) {
                grups[j] = new ArrayList<>();
            }

            //Inserim membres fixes
            for (MembreDto m : membres) {
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
            for (MembreDto m : membres) {
                if (m.getAgrupamentFixe() == null || m.getAgrupamentFixe().isEmpty()) {
                    Integer minGrup = null;
                    int index = 0;
                    int i = 0;
                    for (List<MembreDto> grup : grups) {
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

            for(ItemGrupCooperatiuDto itemGrupCooperatiu: itemsGrupCooperatiuDto){
                for(ValorItemDto valorItem: itemGrupCooperatiu.getItem().getValorItems()){
                    List<Double> valorsItemsComptador = new ArrayList<>();

                    for(List<MembreDto> grup: grups) {
                        Double count = 0.0;
                        for (MembreDto membre : grup) {
                            for (ValorItemMembreDto vim : membre.getValorsItemMembre()) {
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

                    //log.info("Desviació estàndard "+k+": "+ mathService.standardDeviation(valorsItemsCountPrimitive));
                    double desviacio = mathService.standardDeviation(valorsItemsCountPrimitive);
                    double mitjana = mathService.mean(valorsItemsCountPrimitive);
                    double percentatgeDesviacio = (desviacio/mitjana)*100;
                    //Només comptem el percentatge de l'ítem. Si té 3 valors, cada valor valdrà un 33% de la puntuació
                    puntuacio += (percentatgeDesviacio * (itemGrupCooperatiu.getPercentatge()*0.01))/itemGrupCooperatiu.getItem().getValorItems().size();

                    //System.out.println("Puntuació despres item"+ puntuacio);
                }
            }


            //Amistats i enemistats
            /*double[] amics = new double[grups.length];
            double[] teAmics = new double[grups.length];
            double[] enemics = new double[grups.length];

            for (int i = 0; i < grups.length; i++) {
                amics[i] = 0;
                teAmics[i] = 0;
                enemics[i] = 0;
            }*/
            List<Double> amics = new ArrayList<>();
            List<Double> teAmics = new ArrayList<>();
            List<Double> enemics = new ArrayList<>();

            int idx = 0;
            for (List<MembreDto> grup : grups) {
                double[] grupAmics = new double[grup.size()];
                double[] grupTeAmics = new double[grup.size()];
                double[] grupEnemics = new double[grup.size()];

                int idxgrup = 0;

                for (int i = 0; i < grup.size(); i++) {
                    grupAmics[i] = 0;
                    grupTeAmics[i] = 0;
                    grupEnemics[i] = 0;
                }
                for (MembreDto membre : grup) {
                    if (membre.getAmics() != null && membre.getAmics().size() > 0) {
                        boolean teAmic = false;
                        double numAmics = 0;
                        for (MembreDto amic : membre.getAmics()) {
                            for (MembreDto membreGrup : grup) {
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
                        for (MembreDto enemic : membre.getEnemics()) {
                            boolean enemicTrobat = false;
                            for (MembreDto membreGrup : grup) {
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

                amics.addAll(Arrays.stream(grupAmics).boxed().toList());
                teAmics.addAll(Arrays.stream(grupTeAmics).boxed().toList());
                enemics.addAll(Arrays.stream(grupEnemics).boxed().toList());

                idx++;
            }

            double[] amicsPrimitive = new double[amics.size()];
            double[] teAmicsPrimitive = new double[teAmics.size()];
            double[] enemicsPrimitive = new double[enemics.size()];
            idx = 0;
            for(Double d: amics){
                amicsPrimitive[idx] = d;
                idx++;
            }
            idx = 0;
            for(Double d: teAmics){
                teAmicsPrimitive[idx] = d;
                idx++;
            }
            idx = 0;
            for(Double d: enemics){
                enemicsPrimitive[idx] = d;
                idx++;
            }

            if (mathService.mean(teAmicsPrimitive) > 0) {
                double desviacio = mathService.standardDeviation(teAmicsPrimitive);
                double mitjana = mathService.mean(teAmicsPrimitive);
                double percentatgeDesviacio = (desviacio/mitjana)*100;
                puntuacio += percentatgeDesviacio * (percentatgeAmics*0.01);
            }

            if (mathService.mean(enemicsPrimitive) > 0) {
                double desviacio = mathService.standardDeviation(enemicsPrimitive);
                double mitjana = mathService.mean(enemicsPrimitive);
                double percentatgeDesviacio = (desviacio/mitjana)*100;
                puntuacio += percentatgeDesviacio * (percentatgeEnemics*0.01);
            }


            if (millorPuntuacio == null || puntuacio < millorPuntuacio) {
                millorsAgrupacions = grups;
                millorPuntuacio = puntuacio;


                //Pintem els resultats
                int membresSenseAmics = 0;
                int membresAmbEnemics = 0;
                for (int j = 0; j < millorsAgrupacions.length; j++) {
                    AgrupamentDto agrupament = new AgrupamentDto();
                    agrupament.setNumero(String.valueOf(j + 1));
                    Set membresSet = new HashSet(millorsAgrupacions[j]);
                    agrupament.setMembres(membresSet);

                    //log.info("Grup " + (j + 1));
                    for (MembreDto membre : agrupament.getMembres()) {
                        String result = membre.getNom();

                        if (membre.getAmics().size() > 0) {
                            int numAmics = 0;
                            for (MembreDto amic : membre.getAmics()) {
                                for (MembreDto membre2 : agrupament.getMembres()) {
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
                            for (MembreDto enemic : membre.getEnemics()) {
                                for (MembreDto membre2 : agrupament.getMembres()) {
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

                        //log.info(result);
                    }
                }
                log.info("Membres sense cap amic: " + membresSenseAmics);
                log.info("Membres amb algun enemic: " + membresAmbEnemics);
                log.info("Puntuació provisional: "+ puntuacio);
                log.info("---------------------------");
                log.info("");
            }
        }

        System.out.println("La millor puntuació ha estat" + millorPuntuacio);

        //Pintem els resultats
        int membresSenseAmics = 0;
        int membresAmbEnemics = 0;
        List<AgrupamentDto> agrupaments = new ArrayList<>();

        for (int j = 0; j < millorsAgrupacions.length; j++) {
            AgrupamentDto agrupament = new AgrupamentDto();
            agrupament.setNumero(String.valueOf(j + 1));
            Set membresSet = new HashSet(millorsAgrupacions[j]);
            agrupament.setMembres(membresSet);

            agrupaments.add(agrupament);

            log.info("Grup " + (j + 1));
            for (MembreDto membre : agrupament.getMembres()) {
                String result = membre.getNom();

                if (membre.getAmics().size() > 0) {
                    int numAmics = 0;
                    for (MembreDto amic : membre.getAmics()) {
                        for (MembreDto membre2 : agrupament.getMembres()) {
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
                    for (MembreDto enemic : membre.getEnemics()) {
                        for (MembreDto membre2 : agrupament.getMembres()) {
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

    @PostMapping("/mescla/desar")
    public ResponseEntity<Notificacio> saveGrupCooperatiu(@RequestBody String json, HttpServletRequest request) throws Exception {
        //ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        //UsuariDto myUser = myUserResponse.getBody();

        //PARSE JSON
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        //Grup Cooperatiu
        JsonObject jsonGrupCooperatiu = jsonObject.get("grupCooperatiu").getAsJsonObject();

        GrupCooperatiuDto grupCooperatiu = new GrupCooperatiuDto();

        //Si ja existeix ho esborrem tot
        if (jsonGrupCooperatiu.get("id") != null && !jsonGrupCooperatiu.get("id").isJsonNull()) {
            Long idGrupCooperatiu = jsonGrupCooperatiu.get("id").getAsLong();
            grupCooperatiu.setIdgrupCooperatiu(idGrupCooperatiu);
        }

        String nom = "Sense nom";

        if (jsonGrupCooperatiu.get("nom") != null && !jsonGrupCooperatiu.get("nom").isJsonNull()) {
            nom = jsonGrupCooperatiu.get("nom").getAsString();
        }

        grupCooperatiu.setNom(nom);
        //grupCooperatiu.setUsuari(myUser);

        GrupCooperatiuDto grupCooperatiuSaved = grupCooperatiuService.save(grupCooperatiu);


        //Items grup cooperatiu
        JsonArray jsonItemsGrupCooperatiu = jsonGrupCooperatiu.get("itemsGrupCooperatiu").getAsJsonArray();
        List<MembreDto> membres = new ArrayList<>();
        for (JsonElement jsonItemGrupCooperatiu : jsonItemsGrupCooperatiu) {
            ItemDto item = itemService.getItemById(jsonItemGrupCooperatiu.getAsJsonObject().get("item").getAsJsonObject().get("id").getAsLong());
            Integer percentatge = jsonItemGrupCooperatiu.getAsJsonObject().get("percentatge").getAsInt();

            ItemGrupCooperatiuDto itemGrupCooperatiu = new ItemGrupCooperatiuDto();
            itemGrupCooperatiu.setGrupCooperatiu(grupCooperatiuSaved);
            itemGrupCooperatiu.setItem(item);
            itemGrupCooperatiu.setPercentatge(percentatge);

            itemGrupCooperatiuService.save(itemGrupCooperatiu);
        }

        //Members
        grupCooperatiuSaved.getMembres().clear();
        if (jsonObject.get("members") != null && !jsonObject.get("members").isJsonNull()) {
            JsonArray jsonMembers = jsonObject.get("members").getAsJsonArray();
            for (JsonElement jsonMember : jsonMembers) {
                Long idMembre = null;
                if(jsonMember.getAsJsonObject().get("idmembre") != null && !jsonMember.getAsJsonObject().get("idmembre").isJsonNull()) {
                    idMembre = jsonMember.getAsJsonObject().get("idmembre").getAsLong();
                }

                String nomMembre = jsonMember.getAsJsonObject().get("nom").getAsString();
                String agrupamentFixeMembre = null;
                if (jsonMember.getAsJsonObject().get("agrupamentFixe") != null && !jsonMember.getAsJsonObject().get("agrupamentFixe").isJsonNull()) {
                    agrupamentFixeMembre = jsonMember.getAsJsonObject().get("agrupamentFixe").getAsString();
                }


                MembreDto membre;
                if(idMembre != null) {
                    membre = membreService.getMembreById(idMembre);
                } else {
                    membre = new MembreDto();
                }
                membre.setNom(nomMembre);
                membre.setAgrupamentFixe(agrupamentFixeMembre);
                membre.setGrupCooperatiu(grupCooperatiuSaved);
                membre.setAmics(new HashSet<>());
                membre.setEnemics(new HashSet<>());

                MembreDto membreSaved = membreService.save(membre);
                membres.add(membreSaved);
            }


            //Com que els membres amics i enemics són membres també, tornem a recorrer l'array i l'adjuntem dins els membres
            //Amb els "Valors item membre" passa el mateix, desem primer el membre.
            for (JsonElement jsonMember : jsonMembers) {
                MembreDto membreSaved = membres.stream().filter(m -> m.getNom().equals(jsonMember.getAsJsonObject().get("nom").getAsString())).toList().get(0);
                //MembreDto membreSaved = membreService.getMembreByNom(jsonMember.getAsJsonObject().get("nom").getAsString());

                //Amics i enemics
                membreSaved.getAmics().clear();
                membreSaved.getEnemics().clear();
                JsonArray amics = jsonMember.getAsJsonObject().get("amics").getAsJsonArray();
                for (JsonElement amic : amics) {
                    MembreDto membreAmic = membres.stream().filter(m -> m.getNom().equals(amic.getAsString())).toList().get(0);
                    membreSaved.getAmics().add(membreAmic);
                }

                JsonArray enemics = jsonMember.getAsJsonObject().get("enemics").getAsJsonArray();
                for (JsonElement enemic : enemics) {
                    MembreDto membreEnemic = membres.stream().filter(m -> m.getNom().equals(enemic.getAsString())).toList().get(0);
                    membreSaved.getEnemics().add(membreEnemic);
                }

                membreService.save(membreSaved);

                JsonArray itemsUsuari = jsonMember.getAsJsonObject().get("valorsItemMapped").getAsJsonArray();
                for (JsonElement itemUsuari : itemsUsuari) {
                    if(
                            itemsUsuari.isJsonNull() ||
                            itemUsuari.getAsJsonObject()==null ||
                            itemUsuari.getAsJsonObject().isJsonNull() ||
                            itemUsuari.getAsJsonObject().get("value") == null ||
                            itemUsuari.getAsJsonObject().get("value").isJsonNull()
                    ) {
                        System.out.println("itemUsuari.getAsJsonObject().get(\"value\") is null");
                        continue;
                    }
                    ValorItemDto valorItem = valorItemService.findById(itemUsuari.getAsJsonObject().get("value").getAsLong());

                    //Esborrem els anteriors si existeixen i coincideixen amb l'ítem cercat
                    List<ValorItemMembreDto> valorItemMembreOld = valorItemMembreService.findAllByMembre(membreSaved);
                    for (ValorItemMembreDto valorItemMembreDto : valorItemMembreOld) {
                        if(valorItem.getItem().getIdItem().equals(valorItemMembreDto.getValorItem().getItem().getIdItem())) {
                            valorItemMembreService.deleteById(valorItemMembreDto.getIdvalorItemMembre());
                        }
                    }

                    ValorItemMembreDto valorItemMembre = new ValorItemMembreDto();

                    valorItemMembre.setMembre(membreSaved);
                    valorItemMembre.setValorItem(valorItem);

                    valorItemMembreService.save(valorItemMembre);
                }
            }
        }


        //Resultat (Agrupaments)
        /*List<AgrupamentDto> agrupaments = new ArrayList<>();
        List<MembreDto> membresGrupCooperatiu = membreService.findAllByGrupCooperatiu(grupCooperatiuSaved);
        if (jsonObject.get("resultat") != null && !jsonObject.get("resultat").isJsonNull()) {
            JsonArray jsonAgrupaments = jsonObject.get("resultat").getAsJsonArray();
            for (JsonElement jsonAgrupament : jsonAgrupaments) {
                String numero = jsonAgrupament.getAsJsonObject().get("numero").getAsString();

                AgrupamentDto agrupament = new AgrupamentDto();
                agrupament.setNumero(numero);
                agrupament.setGrupCooperatiu(grupCooperatiuSaved);

                AgrupamentDto agrupamentSaved = agrupamentService.save(agrupament);

                HashSet<MembreDto> membresAgrupament = new HashSet<>();
                JsonArray jsonMembres = jsonAgrupament.getAsJsonObject().get("membres").getAsJsonArray();
                for (JsonElement jsonMember : jsonMembres) {
                    List<MembreDto> membreSaved = membres.stream().filter(m -> m.getNom().equals(jsonMember.getAsJsonObject().get("nom").getAsString())).toList();
                    membresAgrupament.addAll(membreSaved);
                }

                agrupamentSaved.setMembres(membresAgrupament);
                agrupamentService.save(agrupament);
            }
        }*/


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Resultat de la mescla desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }


    @GetMapping("/grupscooperatiususuari")
    public ResponseEntity<List<GrupCooperatiuDto>> getGrupsCooperatius(HttpServletRequest request) throws Exception {
        ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        UsuariDto myUser = myUserResponse.getBody();

        //List<GrupCooperatiuDto> grupsCooperatiusUsuari = grupCooperatiuService.findAllByUsuari(myUser);
        List<GrupCooperatiuDto> grupsCooperatiusUsuari = grupCooperatiuService.findAll();

        return new ResponseEntity<>(grupsCooperatiusUsuari, HttpStatus.OK);
    }

    @GetMapping("/grupcooperatiu/{id}")
    public ResponseEntity<GrupCooperatiuDto> getGrupCooperatiu(@PathVariable("id") String idGrupCooperatiu, HttpServletRequest request) throws Exception {
        //ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        //UsuariDto myUser = myUserResponse.getBody();

        //List<GrupCooperatiuDto> grupsCooperatiusUsuari = grupCooperatiuService.findAllByUsuari(myUser);
        List<GrupCooperatiuDto> grupsCooperatiusUsuari = grupCooperatiuService.findAll();

        //Dels grups cooperatius de l'usuari agafem el que tingui la ID passada per paràmtre
        GrupCooperatiuDto grupCooperatiu = grupsCooperatiusUsuari.stream().filter(gc -> gc.getIdgrupCooperatiu().equals(Long.valueOf(idGrupCooperatiu))).collect(Collectors.toList()).get(0);

        List<ItemGrupCooperatiuDto> itemsGrupCooperatiu = itemGrupCooperatiuService.findAllByGrupCooperatiu(grupCooperatiu);
        itemsGrupCooperatiu.sort(Comparator.comparing(a -> a.getItem().getIdItem()));
        grupCooperatiu.setItemsGrupsCooperatius(new TreeSet<>(itemsGrupCooperatiu));

        List<MembreDto> membresGrupCooperatiu = membreService.findAllByGrupCooperatiu(grupCooperatiu);
        membresGrupCooperatiu.sort(Comparator.comparing(a -> a.getNom()));
        grupCooperatiu.setMembres(new TreeSet<>(membresGrupCooperatiu));

        grupCooperatiu.getMembres().forEach(m->{
            m.setValorsItemMembre(new TreeSet<>(valorItemMembreService.findAllByMembre(m)));
        });

        System.out.println("Grup cooperatiu membres: "+grupCooperatiu.getMembres().size());

        List<AgrupamentDto> agrupaments = agrupamentService.findAllByGrupCooperatiu(grupCooperatiu);
        agrupaments.forEach(a -> {
            List<MembreDto> membresAgrupament = membreService.findAllByAgrupament(a);

            membresAgrupament.forEach(m->{
                m.setValorsItemMembre(new TreeSet<>(valorItemMembreService.findAllByMembre(m)));
            });
            a.setMembres(new TreeSet<>(membresAgrupament));
        });
        grupCooperatiu.setAgrupaments(new HashSet<>(agrupaments));

        return new ResponseEntity<>(grupCooperatiu, HttpStatus.OK);
    }

    /*-- ITEMS --*/
    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getItems(HttpServletRequest request) throws Exception {
        ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        UsuariDto myUser = myUserResponse.getBody();

        List<ItemDto> items = itemService.findAllByUsuari(myUser);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<ItemDto> getItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws Exception {
        ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        UsuariDto myUser = myUserResponse.getBody();

        ItemDto item = itemService.getItemById(Long.valueOf(iditem));

        //if (item != null && item.getUsuari().getIdusuari().equals(myUser.getIdusuari())) {
        if (item != null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/item/valors/{id}")
    public ResponseEntity<List<ValorItemDto>> getValorsItemGrupCooperatiu(@PathVariable("id") String iditem, HttpServletRequest request) throws Exception {
        ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        UsuariDto myUser = myUserResponse.getBody();

        ItemDto item = itemService.getItemById(Long.valueOf(iditem));
        List<ValorItemDto> valors = valorItemService.findAllValorsByItem(item);

        //Seguretat
        //boolean usuariCorrecte = item.getUsuari().getIdusuari().equals(myUser.getIdusuari());

        //if (valors != null && usuariCorrecte) {
        if (valors != null) {
            return new ResponseEntity<>(valors, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/item/desar")
    @Transactional
    public ResponseEntity<Notificacio> desarItemGrupCooperatiu(@RequestBody String json, HttpServletRequest request) throws Exception {
        //ResponseEntity<UsuariDto> myUserResponse = coreRestClient.getProfile();
        //UsuariDto myUser = myUserResponse.getBody();

        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

        Long idItem = null;
        if (jsonObject.get("id") != null && !jsonObject.get("id").isJsonNull()) {
            idItem = jsonObject.get("id").getAsLong();
        }

        String nomItem = jsonObject.get("nom").getAsString();

        JsonArray valors = jsonObject.get("valorsItem").getAsJsonArray();

        ItemDto item;

        if (idItem != null) {
            item = itemService.getItemById(idItem);
        } else {
            item = new ItemDto();
        }

        //item.setUsuari(myUser);
        item.setNom(nomItem);

        ItemDto i = itemService.save(item);

        //Valors
        List<ValorItemDto> valorsItem = new ArrayList<>();
        for (JsonElement valor : valors) {
            ValorItemDto vi = new ValorItemDto();
            vi.setValor(valor.getAsJsonObject().get("valor").getAsString());
            vi.setPes(valor.getAsJsonObject().get("pes").getAsInt());
            vi.setItem(i);

            valorsItem.add(vi);
        }

        //Esborrem antics valors
        itemService.deleteAllValorsByItem(i);

        //Creem els nous
        for (ValorItemDto valorItem : valorsItem) {
            valorItemService.save(valorItem);
        }


        Notificacio notificacio = new Notificacio();
        notificacio.setNotifyMessage("Ítem desat correctament");
        notificacio.setNotifyType(NotificacioTipus.SUCCESS);
        return new ResponseEntity<>(notificacio, HttpStatus.OK);
    }

    @PostMapping("/parser")
    public void parserFile(HttpServletRequest request){
        try {
            Part filePart = request.getPart("arxiu");

            InputStream is = filePart.getInputStream();

            // Reads the file into memory
            /*
             * Path path = Paths.get(audioPath); byte[] data = Files.readAllBytes(path);
             * ByteString audioBytes = ByteString.copyFrom(data);
             */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] readBuf = new byte[4096];
            while (is.available() > 0) {
                int bytesRead = is.read(readBuf);
                os.write(readBuf, 0, bytesRead);
            }

            // Passam l'arxiu a dins una carpeta
            String fileName = "/tmp/arxiu.csv";

            OutputStream outputStream = new FileOutputStream(fileName);
            os.writeTo(outputStream);

            File f = new File(fileName);

            //Parser csv
            //El caràcter "|" s'ha d'escapar, sinó ho agafa malament com expressió regular
            final String DELIMITER = Pattern.quote("\t");

            List<List<String>> records = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(DELIMITER);
                records.add(Arrays.asList(values));
            }


            ValorItemDto valorAtencioDiversitatOrd = valorItemService.findById(6L);
            ValorItemDto valorAtencioDiversitatNese = valorItemService.findById(7L);
            ValorItemDto valorAtencioDiversitatNee = valorItemService.findById(8L);

            ValorItemDto valorSexeHome = valorItemService.findById(1L);
            ValorItemDto valorRendimentMoltBe = valorItemService.findById(5L);
            ValorItemDto valorMatesA = valorItemService.findById(9L);

            GrupCooperatiuDto grupCooperatiu1 = grupCooperatiuService.getGrupCooperatiuById(1L);
            GrupCooperatiuDto grupCooperatiu2 = grupCooperatiuService.getGrupCooperatiuById(2L);
            GrupCooperatiuDto grupCooperatiu3 = grupCooperatiuService.getGrupCooperatiuById(3L);
            GrupCooperatiuDto grupCooperatiu4 = grupCooperatiuService.getGrupCooperatiuById(4L);


            //Linia = 0003|15/01/18|13:13:17|000|01;
            for(List<String> linia: records){
                if(linia.size()>=7) {
                    // Columna A - Nom alumne
                    String nomAlumne = linia.get(0).trim();
                    // Columna B - ORD - NESE - NEE (si és blanc serà ORD)
                    String atencioDiversitat = linia.get(1).trim();
                    // Columne F - Curs (si és blanc no es té en compte)
                    String curs = linia.get(5).trim();
                    //Columna G - Si té grup posar-lo al grup que toca
                    String grup = linia.get(6).trim();

                    //Afegir: sexe, rendiment (malament, bé, molt bé), amics, enemics
                    System.out.println("Nom alumne: " + nomAlumne + " - Atenció diversitat: " + atencioDiversitat + " - Curs: " + curs + " - Grup: " + grup);

                    if (curs.equals("1") || curs.equals("2") || curs.equals("3") || curs.equals("4")) {
                        MembreDto membre = new MembreDto();
                        membre.setNom(nomAlumne);

                        if(grup.equals("A")){
                            membre.setAgrupamentFixe("1");
                        }

                        if(curs.equals("1")) {
                            membre.setGrupCooperatiu(grupCooperatiu1);
                        } else if(curs.equals("2")){
                            membre.setGrupCooperatiu(grupCooperatiu2);
                        } else if(curs.equals("3")){
                            membre.setGrupCooperatiu(grupCooperatiu3);
                        } else if(curs.equals("4")){
                            membre.setGrupCooperatiu(grupCooperatiu4);
                        }

                        MembreDto membreSaved = membreService.save(membre);

                        ValorItemMembreDto vimAtencioDiversitat = new ValorItemMembreDto();
                        vimAtencioDiversitat.setMembre(membreSaved);

                        if(atencioDiversitat.equals("ORD")){
                            vimAtencioDiversitat.setValorItem(valorAtencioDiversitatOrd);
                        } else if(atencioDiversitat.equals("NESE")){
                            vimAtencioDiversitat.setValorItem(valorAtencioDiversitatNese);
                        } else if(atencioDiversitat.equals("NEE")){
                            vimAtencioDiversitat.setValorItem(valorAtencioDiversitatNee);
                        } else {
                            vimAtencioDiversitat.setValorItem(valorAtencioDiversitatOrd);
                        }
                        valorItemMembreService.save(vimAtencioDiversitat);

                        ValorItemMembreDto vimSexe = new ValorItemMembreDto();
                        vimSexe.setMembre(membreSaved);
                        vimSexe.setValorItem(valorSexeHome);
                        valorItemMembreService.save(vimSexe);

                        ValorItemMembreDto vimRendiment = new ValorItemMembreDto();
                        vimRendiment.setMembre(membreSaved);
                        vimRendiment.setValorItem(valorRendimentMoltBe);
                        valorItemMembreService.save(vimRendiment);

                        if(curs.equals("4")){
                            ValorItemMembreDto vimMates = new ValorItemMembreDto();
                            vimMates.setMembre(membreSaved);
                            vimMates.setValorItem(valorMatesA);
                            valorItemMembreService.save(vimMates);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}