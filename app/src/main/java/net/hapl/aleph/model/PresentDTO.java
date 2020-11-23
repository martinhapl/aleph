package net.hapl.aleph.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(name="present")
public class PresentDTO {

    @Attribute
    private String doc_number;
    @Element(required = false)
    private String obalka;
    @Element(required = false)
    private String isbn;
    @Element(required = false)
    private String autor;
    @Element(required = false)
    private String nazev;
    @Element(required = false)
    private String podNazev;
    @Element(required = false)
    private String imprint;
    @Element(required = false)
    private String vydani;
    @Element(required = false)
    private String popis;
    @ElementList(inline=true, required = false, entry="poznamka")
    private List<String> poznamka;
    @ElementList(inline=true, required = false, entry="predmet")
    private List<String> predmet;
    @Element(required = false)
    private String imprintRok;
    @Element(required = false)
    private String jazyk;

    public PresentDTO() {
        super();
        predmet = new ArrayList<>();
        poznamka = new ArrayList<>();
    }


    public PresentDTO(String doc_number, String obalka, String isbn, String autor, String nazev,
                      String podNazev, String imprint, String vydani, String popis, List<String> poznamky, List<String> predmety,
                      String imprintRok, String jazyk) {
        this.doc_number = doc_number;
        this.obalka = obalka;
        this.isbn = isbn;
        this.autor = autor;
        this.nazev = nazev;
        this.podNazev = podNazev;
        this.imprint = imprint;
        this.vydani = vydani;
        this.popis = popis;
        this.poznamka = poznamky;
        this.predmet = predmety;
        this.imprintRok = imprintRok;
        this.jazyk = jazyk;
        predmet = new ArrayList<>();
        poznamka = new ArrayList<>();
    }

    public String getDoc_number() {
        return doc_number;
    }
    public void setDoc_number(String doc_number) {
        this.doc_number = doc_number;
    }
    public String getObalka() {
        return obalka;
    }
    public void setObalka(String obalka) {
        this.obalka = obalka;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getNazev() {
        return nazev;
    }
    public void setNazev(String nazev) {
        this.nazev = nazev;
    }
    public String getPodNazev() {
        return podNazev;
    }
    public void setPodNazev(String podNazev) {
        this.podNazev = podNazev;
    }
    public String getImprint() {
        return imprint;
    }
    public void setImprint(String imprint) {
        this.imprint = imprint;
    }
    public String getVydani() {
        return vydani;
    }
    public void setVydani(String vydani) {
        this.vydani = vydani;
    }
    public String getPopis() {
        return popis;
    }
    public void setPopis(String popis) {
        this.popis = popis;
    }
    public List<String> getPoznamka() {
        return poznamka;
    }
    public void setPoznamka(List<String> poznamka) {
        this.poznamka = poznamka;
    }
    public List<String> getPredmet() {
        return predmet;
    }
    public void setPredmet(List<String> predmet) {
        this.predmet = predmet;
    }
    public String getImprintRok() {
        return imprintRok;
    }
    public void setImprintRok(String imprintRok) {
        this.imprintRok = imprintRok;
    }
    public String getJazyk() {
        return jazyk;
    }
    public void setJazyk(String jazyk) {
        this.jazyk = jazyk;
    }

    @Override
    public String toString() {
        return "\nČíslo dokumentu: " + this.doc_number + "\nAutor: " + this.autor + "\nNázev: " + this.nazev +  "\nPodnázev: " +this.podNazev +  "\nImprint: " +this.imprint +
                "\nVydání: " +this.vydani + "\nPopis: " + this.popis +  "\nPoznámka: " +this.poznamka +  "\nPředmět: " +this.predmet + "\nJazyk: " +this.jazyk + "\n";
    }
}
