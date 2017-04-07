package ru.terra.mail.test.storage.repos;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.terra.mail.storage.db.entity.FolderEntity;
import ru.terra.mail.storage.db.repos.FoldersRepo;

import java.util.UUID;

/**
 * Created by Vadim_Korostelev on 4/7/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FoldersRepoTest {
    @Autowired
    public FoldersRepo repo;

    @Before
    public void setUp() {
        FolderEntity fe = new FolderEntity();
        fe.setGuid(UUID.randomUUID().toString());
        fe.setFullName("full_name");
        repo.save(fe);
    }

    @After
    public void cleanUp() {
        repo.deleteAll();
    }

    @Test
    public void findByFullNameTest() {
        Assert.assertNotNull(repo.findByFullName("full_name"));
    }
}
