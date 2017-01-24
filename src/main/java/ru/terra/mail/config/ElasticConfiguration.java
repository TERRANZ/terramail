package ru.terra.mail.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Created by Vadim_Korostelev on 1/24/2017.
 */

@Configuration
@EnableElasticsearchRepositories(basePackages = "ru/terra/mail/storage/db")
public class ElasticConfiguration {
}
