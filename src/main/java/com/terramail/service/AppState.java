package com.terramail.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class AppState {

    public enum Status {
        ONLINE, OFFLINE
    }

    private Status status = Status.OFFLINE;
    private long activeAccountId;
    private long activeFolderId;
    private boolean syncing;
    private String syncStatus;
    private final List<String> queuedMessages;
    private final List<Consumer<String>> stringStatusListeners;
    private final List<Consumer<AppState.Status>> statusListenersTyped;

    public AppState() {
        this.queuedMessages = new CopyOnWriteArrayList<>();
        this.stringStatusListeners = new CopyOnWriteArrayList<>();
        this.statusListenersTyped = new CopyOnWriteArrayList<>();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Objects.requireNonNull(status);
        notifyStatusListeners();
    }

    public boolean isOnline() {
        return status == Status.ONLINE;
    }

    public long getActiveAccountId() {
        return activeAccountId;
    }

    public void setActiveAccountId(long accountId) {
        this.activeAccountId = accountId;
    }

    public long getActiveFolderId() {
        return activeFolderId;
    }

    public void setActiveFolderId(long folderId) {
        this.activeFolderId = folderId;
    }

    public boolean isSyncing() {
        return syncing;
    }

    public void setSyncing(boolean syncing) {
        this.syncing = syncing;
        if (!syncing) {
            this.syncStatus = "Ready";
        }
    }

    public String getSyncStatus() {
        return syncStatus != null ? syncStatus : "Ready";
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
        notifyStatusListeners();
    }

    public List<String> getQueuedMessages() {
        return new ArrayList<>(queuedMessages);
    }

    public void addQueuedMessage(String message) {
        queuedMessages.add(Objects.requireNonNull(message));
    }

    public boolean removeQueuedMessage(String message) {
        return queuedMessages.remove(message);
    }

    public int getQueuedMessageCount() {
        return queuedMessages.size();
    }

    public boolean isEmpty() {
        return queuedMessages.isEmpty();
    }

    public void clearQueuedMessages() {
        queuedMessages.clear();
    }

    public void addStatusListener(Consumer<String> listener) {
        stringStatusListeners.add(Objects.requireNonNull(listener));
    }

    public void addStatusChangeListener(Consumer<Status> listener) {
        statusListenersTyped.add(Objects.requireNonNull(listener));
    }

    public void removeStatusListener(Consumer<String> listener) {
        stringStatusListeners.remove(listener);
    }

    public void removeStatusChangeListener(Consumer<Status> listener) {
        statusListenersTyped.remove(listener);
    }

    private void notifyStatusListeners() {
        for (Consumer<String> listener : stringStatusListeners) {
            listener.accept(getSyncStatus());
        }
        for (Consumer<Status> listener : statusListenersTyped) {
            listener.accept(status);
        }
    }
}
