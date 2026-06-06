package com.terramail.ui.main;

import com.terramail.model.SortOrder;
import com.terramail.ui.model.MessageTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MessageTablePanel extends JPanel {

    private final JTable messageTable;
    private final MessageTableModel messageTableModel;
    private java.util.function.Consumer<com.terramail.model.Message> messageClickListener;
    private java.util.function.Consumer<SortOrder> sortChangeListener;
    private final Map<Integer, SortOrder.Field> columnToField;

    public MessageTablePanel(MessageTableModel messageTableModel) {
        this.messageTableModel = messageTableModel;
        this.columnToField = new HashMap<>();
        this.columnToField.put(0, SortOrder.Field.SUBJECT);
        this.columnToField.put(1, SortOrder.Field.FROM);
        this.columnToField.put(2, SortOrder.Field.DATE);
        this.columnToField.put(3, SortOrder.Field.TO);
        this.columnToField.put(4, SortOrder.Field.CC);
        this.messageTable = new JTable(messageTableModel);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Messages"));
        setPreferredSize(new Dimension(0, 200));

        messageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messageTable.setRowHeight(25);
        messageTable.setGridColor(Color.LIGHT_GRAY);
        messageTable.setIntercellSpacing(new Dimension(1, 0));
        messageTable.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 7; i++) {
            messageTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        messageTable.getColumnModel().getColumn(0).setCellRenderer(new LeftAlignedRenderer());
        messageTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        messageTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        messageTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        messageTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        messageTable.getColumnModel().getColumn(4).setPreferredWidth(180);

        JTableHeader header = messageTable.getTableHeader();
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = messageTable.columnAtPoint(e.getPoint());
                if (col >= 0 && col < columnToField.size()) {
                    SortOrder.Field field = columnToField.get(col);
                    SortOrder currentSort = messageTableModel.getSortOrder();
                    SortOrder newSort;
                    if (currentSort.getField() == field) {
                        newSort = currentSort.reversed();
                    } else {
                        newSort = new SortOrder(field, SortOrder.Direction.DESCENDING);
                    }
                    messageTableModel.setSortOrder(newSort);
                    if (sortChangeListener != null) {
                        sortChangeListener.accept(newSort);
                    }
                }
            }
        });

        messageTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = messageTable.getSelectedRow();
                    if (row >= 0) {
                        com.terramail.model.Message msg = messageTableModel.getMessageAt(row);
                        if (msg != null && messageClickListener != null) {
                            messageClickListener.accept(msg);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(messageTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setMessageClickListener(java.util.function.Consumer<com.terramail.model.Message> listener) {
        this.messageClickListener = listener;
    }

    public void setSortChangeListener(java.util.function.Consumer<SortOrder> listener) {
        this.sortChangeListener = listener;
    }

    private static class LeftAlignedRenderer extends DefaultTableCellRenderer {
        {
            setHorizontalAlignment(LEFT);
        }
    }
}
