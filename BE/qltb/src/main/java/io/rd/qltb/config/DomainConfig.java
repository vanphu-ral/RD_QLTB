package io.rd.qltb.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("io.rd.qltb.domain")
@EnableJpaRepositories("io.rd.qltb.repos")
@EnableTransactionManagement
public class DomainConfig {
}
