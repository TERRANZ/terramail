package com.terramail.ui.model;

import com.terramail.model.Message;
import com.terramail.model.SortOrder;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessageTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Subject", "From", "Date", "To", "Cc", "Seen", "Flagged"};
    private static final Class<?>[] COLUMN_TYPES = {String.class, String.class, String.class, String.class, String.class, String.class, String.class};

    private final List<Message> messages = new ArrayList<>();
    private SortOrder currentSort;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MessageTableModel() {
        this.currentSort = new SortOrder(SortOrder.Field.DATE, SortOrder.Direction.DESC);
    }

    public void setMessages(List<Message> messages) {
        this.messages.clear();
        if (messages != null) {
            this.messages.addAll(messages);
        }
        sortMessages();
        fireTableDataChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        sortMessages();
        fireTableRowsInserted(messages.size() - 1, messages.size() - 1);
    }

    public void removeMessage(Message message) {
        int idx = messages.indexOf(message);
        if (idx >= 0) {
            messages.remove(idx);
            fireTableRowsDeleted(idx, idx);
        }
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.currentSort = Objects.requireNonNull(sortOrder);
        sortMessages();
        fireTableDataChanged();
    }

    public SortOrder getSortOrder() {
        return currentSort;
    }

    public Message getMessageAt(int row) {
        if (row >= 0 && row < messages.size()) {
            return messages.get(row);
        }
        return null;
    }

    public int getMessageCount() {
        return messages.size();
    }

    private void sortMessages() {
        Comparator<Message> comparator = (m1, m2) -> {
            int cmp;
            switch (currentSort.getField()) {
                case SUBJECT:
                    cmp = compareStrings(m1.getSubject(), m2.getSubject());
                    break;
                case FROM:
                    cmp = compareStrings(m1.getFrom(), m2.getFrom());
                    break;
                case DATE:
                    cmp = compareDates(m1.getDate(), m2.getDate());
                    break;
                case TO:
                    cmp = compareStrings(m1.getTo(), m2.getTo());
                    break;
                case CC:
                    cmp = compareStrings(m1.getCc(), m2.getCc());
                    break;
                default:
                    cmp = compareDates(m1.getDate(), m2.getDate());
            }
            return currentSort.getDirection() == SortOrder.Direction.DESC ? -cmp : cmp;
        };
        messages.sort(comparator);
    }

    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    private int compareDates(java.time.Instant d1, java.time.Instant d2) {
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return 1;
        if (d2 == null) return -1;
        return d1.compareTo(d2);
    }

    @Override
    public int getRowCount() {
        return messages.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return COLUMN_TYPES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Message msg = messages.get(row);
        switch (column) {
            case 0: return msg.getSubject() != null ? msg.getSubject() : "";
            case 1: return msg.getFrom() != null ? msg.getFrom() : "";
            case 2: return msg.getDate() != null ? dateFormatter.format(msg.getDate()) : "";
            case 3: return msg.getTo() != null ? msg.getTo() : "";
            case 4: return msg.getCc() != null ? msg.getCc() : "";
            case 5: return msg.isSeen() ? "Yes" : "No";
            case 6: return msg.isFlagged() ? "Yes" : "No";
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public int findRowIndex(Message message) {
        return messages.indexOf(message);
    }
}
