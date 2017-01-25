package ru.terra.mail.storage;

import javafx.collections.ObservableSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.terra.mail.storage.domain.MailFolder;
import ru.terra.mail.storage.domain.MailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Scope("singleton")
public class ModificationObserver {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<ScheduledFuture<?>> checks = new ArrayList<>();
    @Autowired
    private Storage storage;

    public Integer startObserve(ObservableSet<MailMessage> messages, MailFolder mailFolder) {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new ScheduledChecking(messages, mailFolder), 0, 1,
                TimeUnit.SECONDS);
        checks.forEach(sf -> sf.cancel(true));//TODO: hmm
        checks.clear();
        checks.add(future);
        return checks.indexOf(future);
    }

    private class ScheduledChecking implements Runnable {
        private ObservableSet<MailMessage> messages;
        private MailFolder mailFolder;

        public ScheduledChecking(ObservableSet<MailMessage> messages, MailFolder mailFolder) {
            super();
            this.messages = messages;
            this.mailFolder = mailFolder;
        }

        @Override
        public void run() {
//            logger.info("Checking folder " + mailFolder.getFullName() + " for modifications");
            Integer messagesInDb = storage.getStorage().countMessages(mailFolder);
            if (messagesInDb != messages.size()) {
//                logger.info("Modifications: in db: " + messagesInDb + " <> " + messages.size());
                messages.clear();
                messages.addAll(storage.getStorage().getFolderMessages(mailFolder));
            } else {
//                logger.info("No modifications");
            }
        }
    }
}
