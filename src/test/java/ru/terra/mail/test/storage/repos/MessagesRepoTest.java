package ru.terra.mail.test.storage.repos;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.storage.db.entity.MessageEntity;
import ru.terra.mail.storage.db.repos.MessagesRepo;

import java.util.Date;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MessagesRepoTest {
    @Autowired
    public MessagesRepo repo;
    public static final Long createTime = new Date().getTime();

    @Before
    public void setUp() {
        MessageEntity me = new MessageEntity();
        me.setFolderId("folder_id");
        me.setCreateDate(createTime);
        repo.save(me);
    }

    @After
    public void cleanUp() {
        repo.deleteAll();
    }

    @Test
    public void findByFolderIdTest() {
        Assert.assertNotNull(repo.findByFolderId("folder_id"));
    }

    @Test
    public void findByCreateDateTest() {
        Assert.assertNotNull(repo.findByCreateDate(createTime));
    }

    @Test
    public void countByFolderIdTest() {
        Assert.assertEquals(Integer.valueOf(1), repo.countByFolderId("folder_id"));
    }
}
