package si.fri.rso.skupina20.zrna;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.skupina20.entitete.Prostor;
import si.fri.rso.skupina20.izjeme.ProstorNeObstajaIzjema;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
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

    public List<Prostor> getProstori(QueryParameters query) {
        return JPAUtils.queryEntities(em, Prostor.class, query);
    }

    public Long getProstoriCount(QueryParameters query) {
        return JPAUtils.queryEntitiesCount(em, Prostor.class, query);
    }

    @Transactional
    public Prostor addProstor(Prostor prostor) {
        // tu je potrebno klicati API mikrostoritve za avketikacijo
        // da se preveri, če lastnik sploh obstaja
        try{
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
    public Prostor updateProstor(int id, Prostor prostor){
        Prostor p = em.find(Prostor.class, id);

        // Če prostor ne obstaja, vrni izjemo
        if(p == null) {
            log.info("Prostor ne obstaja");
            throw new ProstorNeObstajaIzjema("Prostor z id-jem " + id + " ne obstaja");
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
        if(p != null) {
            em.remove(p);
            return true;
        }
        log.info("Prostor z id-jem " + id + " ne obstaja");
        return false;
    }

    public List<Prostor> getProstoriByLastnik(int lastnik) {
        Query q = em.createNamedQuery("Prostor.getProstorLastnik", Prostor.class);
        q.setParameter("lastnik", lastnik);
        List<Prostor> prostori = q.getResultList();

        if (prostori.isEmpty()) {
            log.info("Lastnik z id-jem " + lastnik + " nima prostorov");
            return null;
        }
        return prostori;
    }


}
