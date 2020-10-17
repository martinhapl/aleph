package net.hapl.aleph.model;

public class FindDTO {

    private String set_number;
    private String no_record;
    private String no_entries;
    private String session_id;

    public String getSet_number() {
        return set_number;
    }
    public void setSet_number(String set_number) {
        this.set_number = set_number;
    }
    public String getNo_record() {
        return no_record;
    }
    public void setNo_record(String no_record) {
        this.no_record = no_record;
    }
    public String getNo_entries() {
        return no_entries;
    }
    public void setNo_entries(String no_entries) {
        this.no_entries = no_entries;
    }
    public String getSession_id() {
        return session_id;
    }
    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @Override
    public String toString() {
        return this.set_number + ":" + this.no_record +  ":" +this.no_entries +  ":" +this.session_id;
    }
}
