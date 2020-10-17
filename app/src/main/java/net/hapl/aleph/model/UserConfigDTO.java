package net.hapl.aleph.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(name="config")
public class UserConfigDTO {

    @Element(required = false)
    private String name;

    @Element(required = false)
    private String email;

    @Element(required = false)
    private String login;

    @Element(required = false)
    private String passwd;

    public UserConfigDTO() {
        super();
    }

    public UserConfigDTO(String name, String email, String login, String passwd) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswd() {
        return passwd;
    }

    @Override
    public String toString() {
        return "Jméno: " + this.name + " E-mail: " + this.email +  " Login: " +this.login +  " Heslo: " +this.passwd;
    }
}