package net.hapl.aleph.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(name="ctenar")
public class CtenarDTO {
    @Element
    private String name;

    @Element
    private String email;

    @Element
    private String ctenarID;

    @Element
    private String date_from;

    @Element
    private String date_to;

    private List<RezervaceDTO> rezervaceDTOs;

    private List<VypujckaDTO> vypujckaDTOs;

    public CtenarDTO() {
        super();
        rezervaceDTOs = new ArrayList<RezervaceDTO>();
        vypujckaDTOs = new ArrayList<VypujckaDTO>();
    }

    public CtenarDTO(String name, String email, String ctenarID, String date_from, String date_to, List<RezervaceDTO> rezervaceDTOs, List<VypujckaDTO> vypujckaDTOs) {
        this.name = name;
        this.email = email;
        this.ctenarID = ctenarID;
        this.date_from = date_from;
        this.date_to = date_to;
        this.rezervaceDTOs = new ArrayList<RezervaceDTO>();
        this.rezervaceDTOs = rezervaceDTOs;
        this.vypujckaDTOs = new ArrayList<VypujckaDTO>();
        this.vypujckaDTOs = vypujckaDTOs;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCtenarID() {
        return ctenarID;
    }

    public String getDate_from() {
        return date_from;
    }

    public String getDate_to() {
        return date_to;
    }

    public List<RezervaceDTO> getRezervaceDTOs() {
        return rezervaceDTOs;
    }

    public List<VypujckaDTO> getVypujckaDTOs() {
        return vypujckaDTOs;
    }

    @Override
    public String toString() {
        return "Jméno: " + this.name + " E-mail: " + this.email +  " ID: " +this.ctenarID +  " Datum od: " +this.date_from +  " Datum do: " +this.date_to;
    }
}
