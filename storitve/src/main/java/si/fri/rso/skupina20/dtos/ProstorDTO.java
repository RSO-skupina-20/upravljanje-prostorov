package si.fri.rso.skupina20.dtos;


import javax.persistence.criteria.CriteriaBuilder;

public class ProstorDTO {

    private String ime;
    private String opis;
    private String lokacija;
    private Double cena;
    private Integer velikost;

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
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
}
