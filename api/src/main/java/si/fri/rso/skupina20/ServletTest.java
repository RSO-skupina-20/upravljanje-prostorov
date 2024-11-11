package si.fri.rso.skupina20;

import si.fri.rso.skupina20.entitete.Prostor;
import si.fri.rso.skupina20.zrna.ProstorZrno;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/servlet")
public class ServletTest extends HttpServlet {
    @Inject
    private ProstorZrno prostorZrno;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Prostor> prostori = prostorZrno.getProstori();

        PrintWriter writer = resp.getWriter();

        for(Prostor prostor : prostori) {
            writer.write("Prostor: " + prostor.getIme() + " " + prostor.getLokacija() + " " + prostor.getCena() + " " + prostor.getVelikost() + " " + prostor.getOpis() + " " + prostor.getLastnik() + "\n");
        }

    }
}