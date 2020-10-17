package net.hapl.aleph.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class ItemDataDTO {
    @Attribute
    private String doc_number;
    @Element(required = false)
    private String barcode;
    @Element(required = false)
    private String dueDate;
    @Element(required = false)
    private String iStatus;
    @Element(required = false)
    private String onHold;
    @Element(required = false)
    private String requested;
    @Element(required = false)
    private String expected;
    @Element(required = false)
    private String subLibrary;
    @Element(required = false)
    private String collection;
    @Element(required = false)
    private String location;
    @Element(required = false)
    private String location2;
    @Element(required = false)
    private String description;

    public ItemDataDTO() {
        super();
    }

    public ItemDataDTO(String doc_number, String dueDate, String barcode, String iStatus, String onHold, String requested, String expected,
                       String subLibrary, String collection, String location, String location2, String description) {
        this.doc_number = doc_number;
        this.dueDate = dueDate;
        this.barcode = barcode;
        this.iStatus = iStatus;
        this.onHold = onHold;
        this.requested = requested;
        this.expected = expected;
        this.subLibrary = subLibrary;
        this.collection = collection;
        this.location = location;
        this.location2 = location2;
        this.description = description;
    }

    public String getDoc_number() {
        return doc_number;
    }
    public String getBarcode() {
        return barcode;
    }
    public String getDueDate() { return dueDate; }
    public String getiStatus() {
        return iStatus;
    }
    public String getOnHold() {
        return onHold;
    }
    public String getRequested() {
        return requested;
    }
    public String getExpected() {
        return expected;
    }
    public String getSubLibrary() {
        return subLibrary;
    }
    public String getCollection() {
        return collection;
    }
    public String getLocation() {
        return location;
    }
    public String getLocation2() {
        return location2;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "\nDue-date: " + this.dueDate + "\nbarCode: " + this.barcode + "\nItem status: " + this.iStatus + "\nCollection: " + this.collection + "\nSub library: " + this.subLibrary;
    }
}