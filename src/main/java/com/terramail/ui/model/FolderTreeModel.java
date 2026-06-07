package com.terramail.ui.model;

import com.terramail.model.Folder;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;

public class FolderTreeModel {

    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public FolderTreeModel() {
        root = new DefaultMutableTreeNode("Terramail");
        treeModel = new DefaultTreeModel(root);
    }

    public void setFolders(List<Folder> folders) {
        root.removeAllChildren();

        // Build hierarchical tree structure based on IMAP path
        Map<String, DefaultMutableTreeNode> pathToNode = new HashMap<>();

        // Sort folders by IMAP path depth (parents first)
        List<Folder> sorted = new ArrayList<>(folders);
        sorted.sort(Comparator.comparing(Folder::getImapPath).thenComparing(Folder::getName));

        for (Folder folder : sorted) {
            String imapPath = folder.getImapPath();
            if (imapPath == null || imapPath.isEmpty()) {
                imapPath = folder.getName();
            }

            // Find parent path
            String parentPath = findParentPath(imapPath);
            DefaultMutableTreeNode parentNode;

            if (parentPath == null || !pathToNode.containsKey(parentPath)) {
                // Root level folder - add under "Inbox"
                if (getRoot().getChildCount() == 0) {
                    parentNode = new DefaultMutableTreeNode("Inbox");
                    getRoot().add(parentNode);
                }
                TreeNode child = getRoot().getChildAt(0);
                if (child instanceof DefaultMutableTreeNode existingNode) {
                    parentNode = existingNode;
                } else {
                    parentNode = new DefaultMutableTreeNode("Inbox");
                    getRoot().add(parentNode);
                }
            } else {
                parentNode = pathToNode.get(parentPath);
            }

            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder);
            parentNode.add(folderNode);
            pathToNode.put(imapPath, folderNode);
        }

        treeModel.nodeStructureChanged(root);
    }

    private String findParentPath(String imapPath) {
        int lastSeparatorIndex = imapPath.lastIndexOf('/');
        if (lastSeparatorIndex > 0) {
            return imapPath.substring(0, lastSeparatorIndex);
        }
        // Try other separators
        lastSeparatorIndex = imapPath.lastIndexOf('.');
        if (lastSeparatorIndex > 0) {
            return imapPath.substring(0, lastSeparatorIndex);
        }
        return null;
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public Folder getSelectedFolder(DefaultMutableTreeNode node) {
        if (node == null) return null;
        Object userObj = node.getUserObject();
        if (userObj instanceof Folder folder) {
            return folder;
        }
        return null;
    }

    public boolean hasFolder(DefaultMutableTreeNode node) {
        return node != null && node.getUserObject() instanceof Folder;
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }
}
