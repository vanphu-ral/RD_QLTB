package rd.project.qltb.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("rd.project.qltb.domain")
@EnableJpaRepositories("rd.project.qltb.repos")
@EnableTransactionManagement
public class DomainConfig {
}
