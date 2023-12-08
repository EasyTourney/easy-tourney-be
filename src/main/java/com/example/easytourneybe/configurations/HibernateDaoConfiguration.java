package com.example.easytourneybe.configurations;

import com.example.easytourneybe.user.repository.UserDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateDaoConfiguration {
    private final SessionFactory sessionFactory;

    public HibernateDaoConfiguration(ObjectProvider<SessionFactory> sessionFactory) {
        this.sessionFactory = sessionFactory.getIfAvailable();
    }

    @Bean
    public UserDao UserDao() {
        return new UserDao(sessionFactory);
    }
}
