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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MessagesRepoTest {
    @Autowired
    public MessagesRepo repo;
    public static final Long createTime = new Date().getTime();
    private UUID folderId = UUID.randomUUID();
    @Before
    public void setUp() {
        MessageEntity me = new MessageEntity();
        me.setGuid(UUID.randomUUID());
        me.setFolderId(folderId);
        me.setCreateDate(createTime);
        repo.save(me);
    }

    @After
    public void cleanUp() {
        repo.deleteAll();
    }

    @Test
    public void findByFolderIdTest() {
        Assert.assertNotNull(repo.findByFolderId(folderId));
    }

    @Test
    public void findByCreateDateTest() {
        Assert.assertNotNull(repo.findByCreateDate(createTime));
    }

    @Test
    public void countByFolderIdTest() {
        Assert.assertEquals(Integer.valueOf(1), repo.countByFolderId(folderId));
    }

    @Test
    public void findByDatesInListTest() {
        Set<Long> dates = new HashSet<>();
        dates.add(123L);

        Assert.assertEquals(0, repo.findByDatesInList(dates).size());

        dates.clear();
        dates.add(createTime);
        Assert.assertEquals(1, repo.findByDatesInList(dates).size());
    }
}
