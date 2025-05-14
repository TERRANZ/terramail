package ru.terra.mail.config;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.Getter;

/**
 * Created by terranz on 19.10.16.
 */
@Data
public class StartUpParameters {
    @Getter
    private static StartUpParameters instance = new StartUpParameters();
    @Parameter(names = {"-u", "--user"}, description = "Mail user")
    private String user = "test";
    @Parameter(names = {"-p", "--pass"}, description = "Mail pass")
    private String pass = "test";
    @Parameter(names = {"-s", "--serv"}, description = "Mail server address")
    private String serv = "test";
    @Parameter(names = {"-a"}, description = "Attachments folder")
    private String attachments = "attachments";
    @Parameter(names = {"-t"}, description = "Protocol, imap/pop")
    private String protocol = "imap";

    private StartUpParameters() {
    }
}
