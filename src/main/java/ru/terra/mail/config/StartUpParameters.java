package ru.terra.mail.config;

import com.beust.jcommander.Parameter;

/**
 * Created by terranz on 19.10.16.
 */
public class StartUpParameters {
    private static StartUpParameters instance = new StartUpParameters();
    @Parameter(names = {"-u", "--user"}, description = "Mail user")
    private String user = "test";
    @Parameter(names = {"-p", "--pass"}, description = "Mail pass")
    private String pass = "test";
    @Parameter(names = {"-s", "--serv"}, description = "Mail server address")
    private String serv = "test";
    @Parameter(names = {"-a"}, description = "Attachments folder")
    private String attachments = "attachments";

    private StartUpParameters() {
    }

    public static StartUpParameters getInstance() {
        return instance;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getServ() {
        return serv;
    }

    public void setServ(String serv) {
        this.serv = serv;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
}
