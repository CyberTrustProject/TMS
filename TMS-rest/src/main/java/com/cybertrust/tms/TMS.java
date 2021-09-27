package com.cybertrust.tms;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.cybertrust.tms.cryptoutils.ConfigManagement;


@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.cybertrust.tms")
//@PropertySource({ "classpath:persistence-mysql.properties" })
@PropertySource({ "classpath:model.properties" })
//@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class TMS extends SpringBootServletInitializer {
	
    public static void main(String[] args) throws Exception {
    	
        SpringApplication.run(TMS.class, args);
    }
    
}
