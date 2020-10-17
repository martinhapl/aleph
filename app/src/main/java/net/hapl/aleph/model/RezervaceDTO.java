package net.hapl.aleph.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="rezervace")
public class RezervaceDTO {

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
    private String requestDate;
    @Element
    private String endRequestDate;
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
    @Element
    private String sequence;

    public RezervaceDTO() {
        super();
    }

    public RezervaceDTO(String docNumber, String itemSequence, String barcode, String material, String subLibrary,
                        String status, String requestDate, String endRequestDate, String autor, String nazev,
                        String imprint, String vydani, String isbn, String predmet, String sequence) {
        this.docNumber = docNumber;
        this.itemSequence = itemSequence;
        this.barcode = barcode;
        this.material = material;
        this.subLibrary = subLibrary;
        this.status = status;
        this.requestDate = requestDate;
        this.endRequestDate = endRequestDate;
        this.autor = autor;
        this.nazev = nazev;
        this.imprint = imprint;
        this.vydani = vydani;
        this.isbn = isbn;
        this.predmet = predmet;
        this.sequence = sequence;
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

    public String getRequestDate() {
        return requestDate;
    }

    public String getEndRequestDate() {
        return endRequestDate;
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

    public String getSequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "Cislo dokumentu: " + this.docNumber + " Datum vypujcky: " + this.requestDate + " Datum splatnosti: " + this.endRequestDate;
    }
}
