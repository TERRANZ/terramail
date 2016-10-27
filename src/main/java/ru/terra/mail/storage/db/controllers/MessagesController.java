package ru.terra.mail.storage.db.controllers;

import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.server.db.controllers.AbstractJpaController;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesController extends AbstractJpaController<MessageEntity> {
    private EntityManager em = getEntityManager();

    public MessagesController() {
        super(MessageEntity.class);
    }

    @Override
    public void create(MessageEntity arg0) throws Exception {
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
        MessageEntity entity = get(id);
        if (entity != null) {
            em = getEntityManager();
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
        }
    }

    @Override
    public void update(MessageEntity arg0) throws Exception {
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

    public List<MessageEntity> findByFolderId(Integer folderId) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("MessageEntity.findByFolderId", MessageEntity.class).setParameter("folderId",
                    folderId);
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        } finally {

        }
    }

    public boolean isExists(Date createDate) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("MessageEntity.findByCreateDate", MessageEntity.class)
                    .setParameter("createDate", createDate);
            return q.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        } finally {

        }
    }

}
