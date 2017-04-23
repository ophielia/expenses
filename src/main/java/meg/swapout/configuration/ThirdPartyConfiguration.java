package meg.swapout.configuration;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Created by margaretmartin on 22/04/2017.
 */
@Configuration
@Profile("alex")
public class ThirdPartyConfiguration {

    @Bean
    public DataSource dataSource(){
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/alexbank")
                .username("bank")
                .password("940620")
                .build();
    }


}