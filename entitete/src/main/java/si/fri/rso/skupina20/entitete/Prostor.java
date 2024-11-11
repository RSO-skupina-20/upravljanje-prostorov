package si.fri.rso.skupina20.entitete;

import javax.persistence.*;

@Entity(name = "prostor")
@NamedQueries(value = {
        @NamedQuery(name = "Prostor.getAll", query = "SELECT p FROM prostor p"),
        @NamedQuery(name = "Prostor.getProstorLastnik", query = "SELECT p FROM prostor p WHERE p.lastnik = :lastnik"),
        @NamedQuery(name = "Prostor.getProstor", query = "SELECT p FROM prostor p WHERE p.id = :id"),
        @NamedQuery(name = "Prostor.updateProstor", query = "UPDATE prostor p SET p.ime = :ime, p.lokacija = :lokacija, p.cena = :cena, p.velikost = :velikost, p.opis = :opis WHERE p.id = :id"),
        @NamedQuery(name = "Prostor.deleteProstor", query = "DELETE FROM prostor p WHERE p.id = :id")
})
public class Prostor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ime", nullable = false)
    private String ime;

    @Column(name = "lokacija", nullable = false)
    private String lokacija;

    @Column(name = "cena", nullable = false)
    private Double cena;

    @Column(name = "velikost")
    private Integer velikost;

    @Column(name = "opis")
    private String opis;

    @Column(name = "lastnik")
    private Integer lastnik;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public Double getCena() {
        return cena;
    }

    public void setCena(Double cena) {
        this.cena = cena;
    }

    public Integer getVelikost() {
        return velikost;
    }

    public void setVelikost(Integer velikost) {
        this.velikost = velikost;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Integer getLastnik() {
        return lastnik;
    }

    public void setLastnik(Integer lastnik) {
        this.lastnik = lastnik;
    }
}
