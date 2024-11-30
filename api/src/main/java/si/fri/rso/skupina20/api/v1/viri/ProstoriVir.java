package si.fri.rso.skupina20.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import com.kumuluz.ee.rest.beans.QueryParameters;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.fri.rso.skupina20.auth.PreverjanjeZetonov;
import si.fri.rso.skupina20.dtos.ProstorDTO;
import si.fri.rso.skupina20.entitete.Prostor;
import si.fri.rso.skupina20.zrna.ProstorZrno;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/prostori")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, DELETE, PUT, HEAD, OPTIONS")
@Tag(name = "Prostori", description = "Upravljanje s prostori")
public class ProstoriVir {
    @Context
    protected UriInfo uriInfo;

    @Inject
    private ProstorZrno prostorZrno;


    // Pridobi vse prostore
    @GET
    @Operation(summary = "Pridobi seznam vseh prostorov", description = "Vrne seznam vseh prostorov")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Seznam prostorov",
                    content = @Content(
                            schema = @Schema(implementation = Prostor.class)
                    ),
                    headers = @Header(
                            name = "X-Total-Count",
                            description = "Število vrnjenih prostorov",
                            schema = @Schema(type = SchemaType.INTEGER)
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Prostori ne obstajajo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Prostorov ni mogoče najti\"}"
                            )
                    )
            )
    })
    public Response vrniProstore(){
        QueryParameters query = QueryParameters.query(uriInfo.getRequestUri().getQuery()).build();
        List<Prostor> prostori = prostorZrno.getProstori(query);

        // uporabnik_id naj bo null
        for (Prostor prostor : prostori) {
            prostor.setLastnik(null);
        }
        if(prostori == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Prostorov ni mogoče najti\"}").build();
        }
        Long count = prostorZrno.getProstoriCount(query);

        return Response.ok(prostori).header("X-Total-Count", count).build();
    }







    // Pridobi prostor glede na id
    @GET
    @Operation(summary = "Pridobi prostor glede na id", description = "Vrne prostor glede na id")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Prostor", content = @Content(schema = @Schema(implementation = Prostor.class, example = "{\"id\": 1, \"ime\": \"Ime\", \"lokacija\": \"Lokacija\", \"cena\": 100.0, \"velikost\": 100, \"opis\": \"Opis\", \"lastnik\": 1}"))),
            @APIResponse(responseCode = "404", description = "Prostor ne obstaja", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class, example = "{\"napaka\": \"Prostor z id 1 ne obstaja\"}"))),
    })
    @Path("{id}")
    public Response vrniProstor(@PathParam("id") Integer id){
        Prostor prostor = prostorZrno.getProstor(id);
        if(prostor == null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Prostor z id " + id + " ne obstaja\"}").build();
        }
        return Response.ok(prostor).build();
    }





    // Dodajanje prostora - lahko samo tip uporabnika LASTNIK
    @POST
    @Operation(summary = "Dodaj prostor", description = "Doda nov prostor")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Prostor uspešno dodan",
                    content = @Content(
                            schema = @Schema(implementation = Prostor.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Manjkajo obvezni podatki",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Manjkajo obvezni podatki\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Uporabnik ne obstaja",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Uporabnik ne obstaja\"}"
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public Response dodajProstor(@RequestBody(description = "Prostor za dodajanje", required = true, content = @Content(schema =
        @Schema(implementation = ProstorDTO.class, example = "{\"ime\": \"Ime\", \"lokacija\": \"Lokacija\", \"cena\": 100.0, \"velikost\": 100, \"opis\": \"Opis\"}"))) ProstorDTO prostor, @HeaderParam("authorization") String authorization){

        if(prostor.getIme() == null || prostor.getLokacija() == null || prostor.getCena() == null || prostor.getOpis() == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Manjkajo obvezni podatki\"}").build();
        }

        Prostor prostor_new = prostorZrno.addProstor(prostor, authorization);
        if(prostor_new == null){
           // neveljavna avtorizacija
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Uporabnik ne obstaja\"}").build();
        }
        return Response.status(Response.Status.CREATED).entity(prostor_new).build();
    }





    // Izbris prostora - lahko le lastnik
    @DELETE
    @Path("{id}")
    @Operation(summary = "Izbriši prostor", description = "Izbriše prostor glede na id")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Prostor uspešno izbrisan"
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Uporabnik nima pravic za brisanje tega prostora",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Nimate pravic za brisanje tega prostora\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Prostor z določenim ID-jem ne obstaja",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Prostor z id 123 ne obstaja\"}"
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public Response izbrisProstora(@PathParam("id") Integer id, @HeaderParam("authorization") String authorization) {
        Prostor prostor = prostorZrno.getProstor(id);
        if (prostor == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Prostor z id " + id + " ne obstaja\"}").build();
        } else if (!PreverjanjeZetonov.verifyOwner(authorization, prostor.getLastnik())) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"napaka\": \"Nimate pravic za brisanje tega prostora\"}").build();
        }
        boolean deleted = prostorZrno.deleteProstor(id);
        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"napaka\": \"Prostor z id " + id + " ne obstaja\"}").build();
        }
    }






    // Posodabljanje prostora - lahko le lastnik prostora
    @PUT
    @Path("{id}")
    @Operation(summary = "Posodobi prostor", description = "Posodobi prostor glede na id")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Prostor uspešno posodobljen",
                    content = @Content(
                            schema = @Schema(implementation = Prostor.class)
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Manjkajo podatki za posodobitev",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Manjkajo podatki za posodobitev\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Uporabnik nima pravic za posodabljanje tega prostora",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "{\"napaka\": \"Nimate pravic za posodabljanje tega prostora\"}"
                            )
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public Response posodobiProstor(@RequestBody(description = "Prostor za posodabljanje", required = true, content = @Content(schema =
        @Schema(implementation = ProstorDTO.class, example = "{\"id\": 1, \"ime\": \"Ime\", \"lokacija\": \"Lokacija\", \"cena\": 100.0, \"velikost\": 100, \"opis\": \"Opis\"}"))) ProstorDTO prostor, @HeaderParam("authorization") String authorization,
                                    @PathParam("id") Integer id){

        if(prostor.getIme() == null && prostor.getLokacija() == null && prostor.getCena() == null && prostor.getVelikost() == null && prostor.getOpis() == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"napaka\": \"Manjkajo podatki za posodobitev\"}").build();
        }

        Integer uporabnik_id = PreverjanjeZetonov.verifyToken(authorization);
        if(uporabnik_id == -1){
            // Uporabnik ni LASTNIk in ne sme spreminjati prostora
            return Response.status(Response.Status.FORBIDDEN).entity("{\"napaka\": \"Nimate pravic za posodabljanje tega prostora\"}").build();
        }

        Prostor prostor_new = new Prostor();
        prostor_new.setIme(prostor.getIme());
        prostor_new.setLokacija(prostor.getLokacija());
        prostor_new.setCena(prostor.getCena());
        prostor_new.setVelikost(prostor.getVelikost());
        prostor_new.setOpis(prostor.getOpis());
        prostor_new.setId(id);
        prostor_new.setLastnik(uporabnik_id);

        Prostor prostor_updated = prostorZrno.updateProstor(prostor_new);

        // Če je prostor_updated null, uporabnik nima pravic za posodabljanje prostora
        if(prostor_updated == null){
            return Response.status(Response.Status.FORBIDDEN).entity("{\"napaka\": \"Nimate pravic za posodabljanje tega prostora\"}").build();
        }

        return Response.ok(prostor_updated).build();
    }

}
