package net.hapl.aleph.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="serverConfig")
public class ServerConfigDTO {

    @Element
    private String user;

    @Element
    private String userPassword;

    @Element
    private String xServerURL;

    @Element
    private String baseBIB;

    @Element
    private String baseADM;

    public ServerConfigDTO() {
        super();
    }

    public ServerConfigDTO(String user, String userPassword, String xServerURL, String baseBIB, String baseADM) {

        this.user = user;
        this.userPassword = userPassword;
        this.xServerURL = xServerURL;
        this.baseBIB = baseBIB;
        this.baseADM = baseADM;
    }

    public String getUser() {
        return user;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getXServerURL() {
        return xServerURL;
    }

    public String getBaseBIB() {
        return baseBIB;
    }

    public String getBaseADM() {
        return baseADM;
    }

    @Override
    public String toString() {
        return "User: " + this.user + " Pass: " + this.userPassword + " XServerURL: " + this.xServerURL +  " Base bib: " +this.baseBIB +  " Base adm: " +this.baseADM;
    }
}
