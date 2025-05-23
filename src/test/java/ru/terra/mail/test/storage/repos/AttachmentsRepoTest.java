package ru.terra.mail.test.storage.repos;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.storage.db.entity.AttachmentEntity;
import ru.terra.mail.storage.db.repos.AttachmentsRepo;

import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AttachmentsRepoTest {
    @Autowired
    public AttachmentsRepo repo;
    private UUID msgId = UUID.randomUUID();

    @Before
    public void setUp() {
        AttachmentEntity ae = new AttachmentEntity();
        ae.setGuid(UUID.randomUUID());
        ae.setMessageId(msgId);
        repo.save(ae);
    }

    @After
    public void cleanUp() {
        repo.deleteAll();
    }

    @Test
    public void findByMessageIdTest() {
        Assert.assertNotNull(repo.findByMessageId(msgId));
    }
}
