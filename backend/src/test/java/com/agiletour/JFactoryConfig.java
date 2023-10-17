package com.agiletour;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.jfactory.repo.JPADataRepository;
import com.github.leeonky.util.Classes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Configuration
public class JFactoryConfig {
    @PersistenceUnit
    private EntityManagerFactory factory;

    @Bean
    @Qualifier("jFactoryEntityManager")
    public EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    @Bean
    public JFactory jFactory() {
        JFactory jFactory = new JFactory(new JPADataRepository(getEntityManager()));
        registerSpecs(jFactory);
        return jFactory;
    }

    private void registerSpecs(JFactory jFactory) {
        Classes.assignableTypesOf(Spec.class, "com.agiletour.spec").forEach(jFactory::register);
        jFactory.ignoreDefaultValue(p -> p.getName().equals("id"));
    }
}
