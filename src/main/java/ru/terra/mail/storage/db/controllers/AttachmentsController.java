package ru.terra.mail.storage.db.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ru.terra.mail.storage.db.entity.AttachmentEntity;
import ru.terra.mail.storage.db.entity.MessageEntity;

public class AttachmentsController extends AbstractController<AttachmentEntity> {

	public AttachmentsController() {
		super(AttachmentEntity.class);
	}

	public List<AttachmentEntity> findByFolderId(Integer parentId) {
		EntityManager em = getEntityManager();
		try {
			Query q = em.createNamedQuery("AttachmentEntity.findByParentId", AttachmentEntity.class)
					.setParameter("parentId", parentId);
			return q.getResultList();
		} catch (NoResultException e) {
			return new ArrayList<>();
		} finally {

		}
	}
}
