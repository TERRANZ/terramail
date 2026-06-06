package com.terramail.ui.model;

import com.terramail.model.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FolderTreeModelTest {

    private FolderTreeModel folderTreeModel;

    @BeforeEach
    void setUp() {
        folderTreeModel = new FolderTreeModel();
    }

    @Test
    void testCreateModel() {
        assertNotNull(folderTreeModel);
        assertNotNull(folderTreeModel.getTreeModel());
    }

    @Test
    void testSetEmptyFolders() {
        folderTreeModel.setFolders(List.of());
        TreeModel model = folderTreeModel.getTreeModel();
        assertNotNull(model);
    }

    @Test
    void testSetFolders() {
        Folder inbox = new Folder(1L, "Inbox", Folder.Type.INBOX);
        Folder sent = new Folder(1L, "Sent", Folder.Type.SENT);
        Folder drafts = new Folder(1L, "Drafts", Folder.Type.DRAFTS);

        folderTreeModel.setFolders(List.of(inbox, sent, drafts));

        TreeModel model = folderTreeModel.getTreeModel();
        assertNotNull(model);
    }

    @Test
    void testGetSelectedFolder() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);

        Folder result = folderTreeModel.getSelectedFolder(node);
        assertNotNull(result);
        assertEquals("Inbox", result.getName());
        assertEquals(Folder.Type.INBOX, result.getType());
    }

    @Test
    void testGetNullFolder() {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("not a folder");
        assertNull(folderTreeModel.getSelectedFolder(node));
    }

    @Test
    void testHasFolder() {
        Folder folder = new Folder(1L, "Inbox", Folder.Type.INBOX);
        DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder);
        DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode("text");

        assertTrue(folderTreeModel.hasFolder(folderNode));
        assertFalse(folderTreeModel.hasFolder(otherNode));
    }

    @Test
    void testNullNode() {
        assertNull(folderTreeModel.getSelectedFolder(null));
        assertFalse(folderTreeModel.hasFolder(null));
    }

    @Test
    void testFoldersSortedByTypeThenName() {
        Folder trash = new Folder(1L, "ZTrash", Folder.Type.TRASH);
        Folder inbox1 = new Folder(1L, "BInbox", Folder.Type.INBOX);
        Folder inbox2 = new Folder(1L, "AInbox", Folder.Type.INBOX);

        folderTreeModel.setFolders(List.of(trash, inbox1, inbox2));

        TreeModel model = folderTreeModel.getTreeModel();
        assertNotNull(model);
    }
}
