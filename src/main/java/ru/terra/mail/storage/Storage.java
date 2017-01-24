package ru.terra.mail.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by terranz on 19.10.16.
 */
@Component
@Scope("singleton")
public class Storage {
    @Autowired
    private ElasticSearchStorage storage;

    public AbstractStorage getStorage() {
        return storage;
    }
}
