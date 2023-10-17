package com.agiletour.cucumber;

import com.github.leeonky.cucumber.restful.RestfulStep;
import com.github.leeonky.jfactory.JFactory;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class ApplicationSteps {

    @Autowired
    private RestfulStep restfulStep;

    @Value("${server.port}")
    private int port;

    @Autowired
    private JFactory jFactory;

    @Autowired
    @Qualifier("jFactoryEntityManager")
    private EntityManager entityManager;

    @Before
    public void initRestfulStep() {
        restfulStep.setBaseUrl("http://localhost:%d/api".formatted(port));
    }

    @Before
    public void cleanDb() {
        asList("ticket", "train", "seat", "stop").forEach(this::clearTable);
        jFactory.getDataRepository().clear();
    }

    private EntityTransaction getCleanTransaction() {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) transaction.rollback();
        return transaction;
    }

    public void clearTable(String tableName) {
        executeDB(entityManager -> {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            entityManager.createNativeQuery("delete from " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1").executeUpdate();
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
        });
    }

    public void executeDB(Consumer<EntityManager> consumer) {
        EntityTransaction transaction = entityManager.getTransaction();
        if (transaction.isActive()) transaction.rollback();
        transaction.begin();
        consumer.accept(entityManager);
        transaction.commit();
    }

}
