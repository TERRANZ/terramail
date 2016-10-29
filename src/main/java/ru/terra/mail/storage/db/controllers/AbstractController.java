package ru.terra.mail.storage.db.controllers;

import ru.terra.mail.storage.db.entity.AbstractEntity;
import ru.terra.server.db.controllers.AbstractJpaController;

import javax.persistence.EntityManager;

public abstract class AbstractController<Entity extends AbstractEntity> extends AbstractJpaController<Entity> {
    protected EntityManager em = getEntityManager();

    public AbstractController(Class entityClass) {
        super(entityClass);
    }

    @Override
    public void create(Entity arg0) throws Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(arg0);
            em.getTransaction().commit();
        } finally {

        }
    }

    @Override
    public void delete(Integer id) throws Exception {
        Entity entity = get(id);
        if (entity != null) {
            entity.setDeleted(true);
            update(entity);
        }
    }

    @Override
    public void update(Entity arg0) throws Exception {
        try {
            em.getTransaction().begin();
            arg0 = em.merge(arg0);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = arg0.getId();
                if (get(id) == null) {
                }
            }
            throw ex;
        } finally {
        }
    }
}
