package si.fri.rso.skupina20.api.v1;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(
        title = "Upravljanje prostorov API",
        version = "v1",
        description = "Upravljanje prostorov API omogoča upravljanje s prostori"),
        servers = @Server(url = "http://localhost:8080"))
@ApplicationPath("v1")
public class upravljanjeProstorovApplication extends Application {
}