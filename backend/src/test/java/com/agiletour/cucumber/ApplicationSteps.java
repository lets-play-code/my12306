package com.agiletour.cucumber;

import com.github.leeonky.cucumber.restful.RestfulStep;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class ApplicationSteps {

    @Autowired
    private RestfulStep restfulStep;

    @Value("${server.port}")
    private int port;

    @Before
    public void initRestfulStep() {
         restfulStep.setBaseUrl("http://localhost:%d/api".formatted(port));
    }

    @Before
    public void cleanDb() {
        asList("train", "ticket", "seat").forEach(this::clearTable);
    }

    @Transactional
    public void clearTable(String tableName) {
        executeDB(entityManager -> {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            entityManager.createNativeQuery("delete from " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        });
    }

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    public void executeDB(Consumer<EntityManager> consumer) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        transaction.begin();
        consumer.accept(manager);
        transaction.commit();
        manager.close();
    }

}
