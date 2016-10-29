package ru.terra.mail.storage.db.controllers;

import ru.terra.mail.storage.db.entity.FolderEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class FoldersController extends AbstractController<FolderEntity> {

    public FoldersController() {
        super(FolderEntity.class);
    }

    public FolderEntity findByFullName(String fullName) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("FolderEntity.findByFullName").setParameter("fullName", fullName)
                    .setMaxResults(1);
            return (FolderEntity) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {

        }
    }
}
