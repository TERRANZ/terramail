package com.terramail.ui.main;

import com.terramail.model.Folder;
import com.terramail.ui.model.FolderTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class FolderTreePanel extends JPanel {

    private final JTree folderTree;
    private final FolderTreeModel folderTreeModel;
    private Folder selectedFolder;
    private java.util.function.Consumer<Folder> selectionListener;

    public FolderTreePanel(FolderTreeModel folderTreeModel) {
        this.folderTreeModel = folderTreeModel;
        this.folderTree = new JTree(folderTreeModel.getTreeModel());
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createTitledBorder("Folders"));

        folderTree.setRootVisible(true);
        folderTree.setShowsRootHandles(false);
        folderTree.setCellRenderer(new FolderTreeCellRenderer());
        folderTree.getSelectionModel().setSelectionMode(
            javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
        folderTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleSelection();
                }
            }
        });
        folderTree.addTreeSelectionListener(e -> {
            if (folderTree.getSelectionCount() == 1) {
                handleSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(folderTree);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleSelection() {
        javax.swing.tree.TreePath path = folderTree.getSelectionPath();
        if (path == null) return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Folder folder = folderTreeModel.getSelectedFolder(node);
        if (folder != null) {
            selectedFolder = folder;
            if (selectionListener != null) {
                selectionListener.accept(folder);
            }
        }
    }

    public void setSelectionListener(java.util.function.Consumer<Folder> listener) {
        this.selectionListener = listener;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    private static class FolderTreeCellRenderer extends JPanel implements TreeCellRenderer {

        private final JLabel iconLabel;
        private final JLabel textLabel;

        public FolderTreeCellRenderer() {
            setLayout(new BorderLayout(5, 0));
            iconLabel = new JLabel();
            iconLabel.setPreferredSize(new Dimension(20, 20));
            textLabel = new JLabel();
            textLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            add(iconLabel, BorderLayout.WEST);
            add(textLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObj = node.getUserObject();

            if (userObj instanceof Folder folder) {
                textLabel.setText(folder.getName());
                textLabel.setFont(tree.getFont());
                iconLabel.setIcon(getFolderIcon(folder.getType()));
            } else {
                textLabel.setText(node.toString());
                textLabel.setFont(tree.getFont().deriveFont(Font.BOLD, (float) tree.getFont().getSize()));
                iconLabel.setIcon(null);
            }

            if (selected) {
                setBackground((Color) tree.getClientProperty("JTree.selectionBackground"));
                textLabel.setForeground((Color) tree.getClientProperty("JTree.selectionForeground"));
                iconLabel.setForeground((Color) tree.getClientProperty("JTree.selectionForeground"));
            } else {
                setBackground(tree.getBackground());
                textLabel.setForeground(tree.getForeground());
                iconLabel.setForeground(tree.getForeground());
            }

            return this;
        }

        private Icon getFolderIcon(Folder.Type type) {
            return switch (type) {
                case INBOX -> UIManager.getIcon("FileView.directoryIcon");
                case SENT -> UIManager.getIcon("FileView.floppyDriveIcon");
                case DRAFTS -> UIManager.getIcon("FileView.fileIcon");
                case TRASH -> UIManager.getIcon("FileView.trashIcon");
                case CUSTOM -> UIManager.getIcon("FileView.directoryIcon");
            };
        }
    }
}
