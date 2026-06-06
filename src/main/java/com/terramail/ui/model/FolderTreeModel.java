package com.terramail.ui.model;

import com.terramail.model.Folder;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FolderTreeModel {

    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public FolderTreeModel() {
        root = new DefaultMutableTreeNode("Terramail");
        treeModel = new DefaultTreeModel(root);
    }

    public void setFolders(List<Folder> folders) {
        root.removeAllChildren();

        List<Folder> sorted = new ArrayList<>(folders);
        sorted.sort(Comparator.comparing(Folder::getType).thenComparing(Folder::getName));

        DefaultMutableTreeNode accountNode = new DefaultMutableTreeNode("Inbox");
        for (Folder folder : sorted) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder);
            accountNode.add(folderNode);
        }
        root.add(accountNode);

        treeModel.nodeStructureChanged(root);
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
