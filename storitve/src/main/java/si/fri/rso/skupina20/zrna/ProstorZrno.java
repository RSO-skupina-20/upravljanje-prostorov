package si.fri.rso.skupina20.zrna;

import si.fri.rso.skupina20.entitete.Prostor;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ProstorZrno {

    @PersistenceContext
    private EntityManager em;

    public List<Prostor> getProstori() {
        return em.createNamedQuery("Prostor.getAll").getResultList();
    }

}
