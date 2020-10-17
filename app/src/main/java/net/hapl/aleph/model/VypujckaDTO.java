package net.hapl.aleph.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="vypujcka")
public class VypujckaDTO {

    @Element
    private String docNumber;
    @Element
    private String itemSequence;
    @Element
    private String barcode;
    @Element
    private String material;
    @Element
    private String subLibrary;
    @Element
    private String status;
    @Element
    private String loanDate;
    @Element
    private String loanHour;
    @Element
    private String dueDate;
    @Element
    private String dueHour;
    @Element
    private String autor;
    @Element
    private String nazev;
    @Element
    private String imprint;
    @Element
    private String vydani;
    @Element
    private String isbn;
    @Element
    private String predmet;

    public VypujckaDTO() {
        super();
    }

    public VypujckaDTO(String docNumber, String itemSequence, String barcode, String material,
                       String subLibrary, String status, String loanDate, String loanHour, String dueDate,
                       String dueHour, String autor, String nazev, String imprint, String vydani, String isbn, String predmet) {

        this.docNumber = docNumber;
        this.itemSequence = itemSequence;
        this.barcode = barcode;
        this.material = material;
        this.subLibrary = subLibrary;
        this.status = status;
        this.loanDate = loanDate;
        this.loanHour = loanHour;
        this.dueDate = dueDate;
        this.dueHour = dueHour;
        this.autor = autor;
        this.nazev = nazev;
        this.imprint = imprint;
        this.vydani = vydani;
        this.isbn = isbn;
        this.predmet = predmet;

    }

    public String getDocNumber() {
        return docNumber;
    }

    public String getItemSequence() {
        return itemSequence;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getMaterial() {
        return material;
    }

    public String getSubLibrary() {
        return subLibrary;
    }

    public String getStatus() {
        return status;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public String getLoanHour() {
        return loanHour;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getDueHour() {
        return dueHour;
    }

    public String getAutor() {
        return autor;
    }

    public String getNazev() {
        return nazev;
    }

    public String getImprint() {
        return imprint;
    }

    public String getVydani() {
        return vydani;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPredmet() {
        return predmet;
    }

    @Override
    public String toString() {
        return "Cislo dokumentu: " + this.docNumber + " Datum vypujcky: " + this.loanDate +  " Datum splatnosti: " +this.dueDate;
    }
}
