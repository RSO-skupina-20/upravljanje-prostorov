package si.fri.rso.skupina20.health;


import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import si.fri.rso.skupina20.zrna.ProstorZrno;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Logger;

@Readiness
@ApplicationScoped
// Preverjanje ali pridobivanje podatkov o prostorih deluje
public class PridobiProstoreHealthCheckBean implements HealthCheck {
    private static final Logger LOG = Logger.getLogger(PridobiProstoreHealthCheckBean.class.getName());

    @Inject
    private ProstorZrno prostorZrno;

    @Override
    public HealthCheckResponse call() {
        String description = "Preverjanje ali pridobivanje podatkov o prostorih deluje";
        try {
            prostorZrno.getProstori();
        } catch (Exception e) {
            LOG.severe("Napaka pri preverjanju zdravja: " + e.getMessage());
            return HealthCheckResponse.named(PridobiProstoreHealthCheckBean.class.getSimpleName())
                    .down()
                    .withData("description", description)
                    .withData("error", e.getMessage())
                    .build();
        }
        return HealthCheckResponse.named(PridobiProstoreHealthCheckBean.class.getSimpleName())
                .up()
                .withData("description", description)
                .build();
    }

}
