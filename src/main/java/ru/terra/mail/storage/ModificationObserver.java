package ru.terra.mail.storage;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ModificationObserver {
    private static ModificationObserver instance = new ModificationObserver();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<ScheduledFuture<?>> checks = new ArrayList<>();
    private Storage storage = StorageSingleton.getInstance().getStorage();

    private ModificationObserver() {
    }

    public static ModificationObserver getInstance() {
        return instance;
    }

    public Integer startObserve(ObservableList<MailMessage> messages, MailFolder mailFolder) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new ScheduledChecking(messages, mailFolder), 0, 5,
                TimeUnit.SECONDS);
        checks.add(future);
        return checks.indexOf(future);
    }

    public void stopObserve(Integer observerId) {
        logger.info("Stopping observer " + observerId);
        ScheduledFuture<?> future = checks.get(observerId);
        if (future != null) {
            if (!future.isCancelled() && !future.isDone()) {
                future.cancel(true);
            }
        }
    }

    private class ScheduledChecking implements Runnable {
        private ObservableList<MailMessage> messages;
        private MailFolder mailFolder;

        public ScheduledChecking(ObservableList<MailMessage> messages, MailFolder mailFolder) {
            super();
            this.messages = messages;
            this.mailFolder = mailFolder;
        }

        @Override
        public void run() {
            logger.info("Checking folder " + mailFolder.getFullName() + " for modifications");
            Integer messagesInDb = storage.countMessages(mailFolder);
            if (messagesInDb != messages.size()) {
                logger.info("Modifications: in db: " + messagesInDb + " <> " + messages.size());
                messages.clear();
                messages.addAll(storage.getFolderMessages(mailFolder));
            } else {
                logger.info("No modifications");
            }
        }
    }
}
