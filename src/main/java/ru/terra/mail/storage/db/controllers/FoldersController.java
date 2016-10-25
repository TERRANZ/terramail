package ru.terra.mail.storage.db.controllers;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.server.db.controllers.AbstractJpaController;

public class FoldersController extends AbstractJpaController<FolderEntity> {
	private EntityManager em = getEntityManager();

	public FoldersController() {
		super(FolderEntity.class);
	}

	@Override
	public void create(FolderEntity arg0) throws Exception {
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
		FolderEntity entity = get(id);
		if (entity != null) {
			entity.setDeleted(true);
			update(entity);
		}
	}

	@Override
	public void update(FolderEntity arg0) throws Exception {
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
