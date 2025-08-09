package org.parent.jira;

import org.parent.jira.security.rsa.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RSAKeyRecord.class)
public class JiraApplication {

    public static void main(String[] args) {
        SpringApplication.run(JiraApplication.class, args);
    }

}
