package ru.terra.mail.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */

@Configuration
@EnableElasticsearchRepositories(basePackages = "ru/terra/mail/storage/db")
public class ElasticConfiguration {
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() throws UnknownHostException {
        Settings settings = Settings.settingsBuilder().build();
        TransportClient client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        return new ElasticsearchTemplate(client);
    }
}
