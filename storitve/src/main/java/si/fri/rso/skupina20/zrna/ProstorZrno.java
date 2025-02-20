package si.fri.rso.skupina20.zrna;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.skupina20.auth.PreverjanjeZetonov;
import si.fri.rso.skupina20.dtos.ProstorDTO;
import si.fri.rso.skupina20.entitete.Prostor;
import si.fri.rso.skupina20.izjeme.ProstorNeObstajaIzjema;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ProstorZrno {

    @PersistenceContext(unitName = "upravljanje-prostorov-jpa")
    private EntityManager em;
    private Logger log = Logger.getLogger(ProstorZrno.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + ProstorZrno.class.getSimpleName());
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + ProstorZrno.class.getSimpleName());
    }

    public List<Prostor> getProstori() {
        Query q = em.createNamedQuery("Prostor.getAll", Prostor.class);
        return q.getResultList();
    }

    public Prostor getProstor(int id) {
        return em.find(Prostor.class, id);
    }

    // Za pridobivanje vremena
    public JsonObject pridobiVremeZaLokacijo(String lokacija) {
        try {
            String apiKey = System.getenv("WEATHER_API_KEY");
            String urlString = "http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + lokacija + "&days=3&aqi=no&alerts=no";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                try (JsonReader jsonReader = Json.createReader(new StringReader(response.toString()))) {
                    return jsonReader.readObject();
                }
            } else {
                log.info("Napaka pri pridobivanju vremena: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Prostor> getProstori(QueryParameters query) {
        return JPAUtils.queryEntities(em, Prostor.class, query);
    }

    public Long getProstoriCount(QueryParameters query) {
        return JPAUtils.queryEntitiesCount(em, Prostor.class, query);
    }

    @Transactional
    public Prostor addProstor(ProstorDTO prostorDTO, String token) {

        Integer uporabnik_id = PreverjanjeZetonov.verifyToken(token);
        if (uporabnik_id == -1) {
            return null;
        }
        Prostor prostor = new Prostor();
        prostor.setIme(prostorDTO.getIme());
        prostor.setLokacija(prostorDTO.getLokacija());
        prostor.setCena(prostorDTO.getCena());
        prostor.setVelikost(prostorDTO.getVelikost());
        prostor.setOpis(prostorDTO.getOpis());
        prostor.setLastnik(uporabnik_id);

        try {
            em.persist(prostor);
            return prostor;
        } catch (Exception e) {
            log.info("Napaka pri dodajanju prostora: " + e.getMessage());
            return null;
        }
    }

    // Metoda, ki preveri, če je vrednost null ali prazna in vrne privzeto vrednost
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return (value != null && !(value instanceof String && ((String) value).isEmpty())) ? value : defaultValue;
    }

    @Transactional
    public Prostor updateProstor(Prostor prostor) {
        Prostor p = em.find(Prostor.class, prostor.getId());

        if (p.getLastnik() != prostor.getLastnik()) {
            log.info("Uporabnik nima pravic za spreminjanje prostora");
            return null;
        }
        // Če prostor ne obstaja, vrni izjemo
        if (p == null) {
            log.info("Prostor ne obstaja");
            throw new ProstorNeObstajaIzjema("Prostor z id-jem " + prostor.getId() + " ne obstaja");
        }
        // Če je vnesen nov podatek, ga uporabi, sicer uporabi obstoječega
        prostor.setIme(defaultIfNull(prostor.getIme(), p.getIme()));
        prostor.setLokacija(defaultIfNull(prostor.getLokacija(), p.getLokacija()));
        prostor.setCena(defaultIfNull(prostor.getCena(), p.getCena()));
        prostor.setVelikost(defaultIfNull(prostor.getVelikost(), p.getVelikost()));
        prostor.setOpis(defaultIfNull(prostor.getOpis(), p.getOpis()));
        prostor.setLastnik(p.getLastnik()); // Lastnika ne moremo spreminjati


        prostor.setId(p.getId());
        em.merge(prostor);

        return prostor;
    }

    @Transactional
    public boolean deleteProstor(int id) {
        Prostor p = em.find(Prostor.class, id);
        if (p != null) {
            em.remove(p);
            return true;
        }
        log.info("Prostor z id-jem " + id + " ne obstaja");
        return false;
    }

    public List<Prostor> getProstoriByLastnik(int lastnik) {
        // preveri če obstaja uporabnik s tem id-jem in če je lastnik
        Query q = em.createNamedQuery("Prostor.getProstorLastnik", Prostor.class);
        q.setParameter("lastnik", lastnik);
        try {
            List<Prostor> prostori = q.getResultList();
            return prostori;
        } catch (Exception e) {
            log.info("Napaka pri pridobivanju prostorov lastnika: " + e.getMessage());
            return null;
        }
    }


}
