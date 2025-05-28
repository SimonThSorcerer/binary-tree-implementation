package com.simon.simontree.model;

import com.simon.simontree.enums.TreeLogic;
import com.simon.simontree.enums.TypeOfModification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ModificationBinaryTreeTest {
    ModificationBinaryTree modificationBinaryTree;

    private Modification mod1, mod2, mod3, mod4, mod5, mod6, mod7, mod8, mod9, mod10,
            mod11, mod12, mod13, mod14, mod15;
    private Set<Modification> listOfModifications1, listOfModifications2, listOfModifications3, listOfModifications4,
            listOfModifications5, listOfModifications6;
    private ModificationGroup mg1, mg2, mg3, mg4, mg5, mg6, mg7;

    @BeforeEach
    void setup() {
        mod1 = new Modification("Test1", 10, 2324, TypeOfModification.ADD);
        mod2 = new Modification("Something", 20, 33253, TypeOfModification.MODIFY);
        mod3 = new Modification("Hiring", 30, 1, TypeOfModification.REMOVE);
        mod4 = new Modification("Firing", 10, 67, TypeOfModification.ADD);
        mod5 = new Modification("Bonuses", 20, -123, TypeOfModification.MODIFY);
        mod6 = new Modification("Hardware", 30, 33333333, TypeOfModification.REMOVE);
        mod7 = new Modification("Marketing", 11, 0, TypeOfModification.ADD);
        mod8 = new Modification("OverTime", 22, 1, TypeOfModification.MODIFY);
        mod9 = new Modification("Test9", 33, 5, TypeOfModification.REMOVE);
        mod10 = new Modification("Investment", 15, 34, TypeOfModification.ADD);
        mod11 = new Modification("Insurance", 25, 23, TypeOfModification.MODIFY);
        mod12 = new Modification("Vis Major", 37, 23, TypeOfModification.REMOVE);
        mod13 = new Modification("Extra", 3432, 1, TypeOfModification.ADD);
        mod14 = new Modification("Surprise", 7, 33, TypeOfModification.MODIFY);
        mod15 = new Modification("Name", 3, 87, TypeOfModification.REMOVE);

        listOfModifications1 = Set.of(mod1, mod2, mod3, mod4);
        listOfModifications2 = Set.of(mod5, mod6, mod7, mod8);
        listOfModifications3 = Set.of(mod9, mod10, mod11, mod12);
        listOfModifications4 = Set.of(mod13, mod14, mod15);
        listOfModifications5 = Set.of(mod5, mod6, mod7, mod8);
        listOfModifications6 = Set.of(mod13, mod14, mod15);

        mg1 = new ModificationGroup(listOfModifications1, "Global modifications");
        mg2 = new ModificationGroup(listOfModifications2, "Europe");
        mg3 = new ModificationGroup(listOfModifications3, "District I.");
        mg4 = new ModificationGroup(listOfModifications4, "Hungary");
        mg5 = new ModificationGroup(listOfModifications5, "Budapest");
        mg6 = new ModificationGroup(listOfModifications6, "Office");
        mg7 = new ModificationGroup(Collections.singleton(mod15), "Test");

        modificationBinaryTree = new ModificationBinaryTree(TreeLogic.NAME);
        modificationBinaryTree.insert(mg1);
        modificationBinaryTree.insert(mg2);
        modificationBinaryTree.insert(mg3);
        modificationBinaryTree.insert(mg4);
        modificationBinaryTree.insert(mg5);
        modificationBinaryTree.insert(mg6);
        modificationBinaryTree.insert(mg7);
    }

    @Test
    void getTreePath_shouldThrowNullPointerException_InCaseOfNull() {
        Assertions.assertThrows(NullPointerException.class, () -> modificationBinaryTree.getTreePath(null));
    }

    @Test
    void getTreePath_returnsCorrectValue_AfterInsertMethodInsertion() {
        System.out.println(modificationBinaryTree.getTreePath(mg3));
        modificationBinaryTree.printTreeStructure();
        Assertions.assertEquals("Global modifications / Europe / District I.", modificationBinaryTree.getTreePath(mg3));
    }

    @Test
    void getTreePath_shouldReturnEmptyString_InCaseOfNonExistingModificationGroup() {
        ModificationGroup nonExistent = new ModificationGroup(Collections.emptySet(), "Empty");
        Assertions.assertEquals("", modificationBinaryTree.getTreePath(nonExistent));
    }

    @Test
    void insert_shouldSetRoot_whenTreeIsEmpty() {
        ModificationBinaryTree newTree1 = new ModificationBinaryTree(TreeLogic.PRIORITY);

        ModificationGroup test1 = new ModificationGroup(Collections.singleton(mod1), "TEST");
        newTree1.insert(test1);

        Assertions.assertNotNull(newTree1.getRoot());
        Assertions.assertEquals(test1, newTree1.getRoot().getModificationGroup());
        Assertions.assertEquals(0, test1.getLevel());
        Assertions.assertEquals(1, newTree1.getTotalNumberOfElements().get());
    }

    @Test
    void insert_shouldAddChildNodesCorrectly() {
        modificationBinaryTree.printTreeStructure();
        System.out.println(mg1.getLevel() + mg1.getName());
        System.out.println(mg2.getLevel() + mg2.getName());
        System.out.println(mg3.getLevel() + mg3.getName());
        System.out.println(mg4.getLevel() + mg4.getName());
        System.out.println(mg5.getLevel() + mg5.getName());
        System.out.println(mg6.getLevel());
        System.out.println(mg7.getLevel());
        Assertions.assertEquals(7, modificationBinaryTree.getTotalNumberOfElements().get());
        Assertions.assertEquals(3, mg5.getLevel());
        Assertions.assertEquals(1, mg4.getLevel());
        Assertions.assertTrue(mg4.getChildModificationGroups().contains(mg6));
    }

    @Test
    void insert_throwsNullPointerException_whenInsertingNull() {
        Assertions.assertThrows(NullPointerException.class, () -> modificationBinaryTree.insert(null));
    }

    @Test
    void insert_shouldThrowException_whenInsertingDuplicates() {
        Assertions.assertThrows(IllegalStateException.class, () -> modificationBinaryTree.insert(mg3));
    }


    @Test
    void insert_shouldCalculateLevelsCorrectly() {
        ModificationBinaryTree newTree1 = new ModificationBinaryTree(TreeLogic.PRIORITY);

        ModificationGroup test1 = new ModificationGroup(Collections.singleton(mod1), "TEST");
        ModificationGroup test2 = new ModificationGroup(Collections.singleton(mod2), "TEST2");
        newTree1.insert(test1);
        newTree1.insert(test2);

        ModificationGroup grandChild = new ModificationGroup(Collections.singleton(mod3), "TEST3");
        newTree1.insert(grandChild);

        Assertions.assertEquals(0, test1.getLevel());
        Assertions.assertEquals(1, test2.getLevel());
        Assertions.assertEquals(2, grandChild.getLevel());
        Assertions.assertEquals(3, newTree1.getTotalNumberOfLevels());

        ModificationBinaryTree testTree = new ModificationBinaryTree(TreeLogic.NAME);
        testTree.insert(mg1);
        testTree.insert(mg2);
        testTree.insert(mg3);
        testTree.printTreeStructure();
        testTree.getTotalNumberOfLevels();
        Assertions.assertEquals(3, testTree.getTotalNumberOfElements().get());
    }

    @Test
    void insert_shouldUpdateTotalNumberOfLevels() {
        modificationBinaryTree.printTreeStructure();
        Assertions.assertEquals(4, modificationBinaryTree.getTotalNumberOfLevels());
        ModificationGroup grandChild = new ModificationGroup(Collections.singleton(mod3), "TEST3");
        ModificationGroup grandChild3 = new ModificationGroup(Collections.singleton(mod4), "TEST5");
        ModificationGroup grandChild4 = new ModificationGroup(Collections.singleton(mod7), "TEST7");
        modificationBinaryTree.insert(grandChild);
        modificationBinaryTree.insert(grandChild3);
        modificationBinaryTree.insert(grandChild4);
        modificationBinaryTree.printTreeStructure();
        Assertions.assertEquals(7, modificationBinaryTree.getTotalNumberOfLevels());

        ModificationBinaryTree testTree = new ModificationBinaryTree(TreeLogic.NAME);
        testTree.insert(mg1);
        testTree.insert(mg2);
        testTree.insert(mg3);
        testTree.printTreeStructure();
        testTree.getTotalNumberOfLevels();
        Assertions.assertEquals(3, testTree.getTotalNumberOfLevels());
    }

    @Test
    void removeModificationGroup_shouldUpdateTotalNumberOfElements() {
        ModificationBinaryTree testTree = new ModificationBinaryTree(TreeLogic.NAME);
        testTree.insert(mg1);
        Assertions.assertEquals(1, testTree.getTotalNumberOfElements().get());
        Assertions.assertEquals(0, testTree.getTotalNumberOfLevels()); // Single node tree has 0 levels (root at level 0)
        Assertions.assertTrue(testTree.removeModificationGroup(mg1));
        Assertions.assertEquals(-1, testTree.getTotalNumberOfLevels()); // Empty tree should return -1 for levels
        Assertions.assertEquals(0, testTree.getTotalNumberOfElements().get()); // Element count should be 0
        Assertions.assertFalse(testTree.getAllModificationGroups().contains(mg1));
    }

    @Test
    void removeModificationGroup_shouldRemoveElement() {
        Assertions.assertTrue(modificationBinaryTree.removeModificationGroup(mg7));
        Assertions.assertFalse(modificationBinaryTree.getAllModificationGroups().contains(mg7));
    }

        @Test
    void removeModificationGroup_shouldUpdateParentChildRelationships() {
        Assertions.assertTrue(modificationBinaryTree.removeModificationGroup(mg2));
        Assertions.assertFalse(mg1.getChildModificationGroups().contains(mg2));
        Assertions.assertTrue(mg1.getChildModificationGroups().contains(mg4)); // mg4 should still exist
        Assertions.assertEquals(4, modificationBinaryTree.getTotalNumberOfLevels());
    }



    @Test
    void addModificationGroup_shouldAddlement() {
        Assertions.assertTrue(modificationBinaryTree.removeModificationGroup(mg7));
        Assertions.assertFalse(modificationBinaryTree.getAllModificationGroups().contains(mg7));
    }

    @Test
    void addModificationGroup_shouldUpdateParentChildRelationships() {
        Assertions.assertTrue(modificationBinaryTree.removeModificationGroup(mg2));
        Assertions.assertFalse(mg1.getChildModificationGroups().contains(mg2));
        Assertions.assertTrue(mg1.getChildModificationGroups().contains(mg4)); // mg4 should still exist
        Assertions.assertEquals(4, modificationBinaryTree.getTotalNumberOfLevels());
    }

}

