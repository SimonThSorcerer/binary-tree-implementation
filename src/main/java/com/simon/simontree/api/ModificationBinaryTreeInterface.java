package com.simon.simontree.api;

import com.simon.simontree.model.Modification;
import com.simon.simontree.model.ModificationBinaryTree;
import com.simon.simontree.model.ModificationGroup;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * Defines a thread-safe binary tree that stores {@link ModificationGroup} objects
 * with customizable sorting logic. Implementations must support multiple tree traversal methods,
 * level-based operations, and maintain thread safety during structural modifications.
 * Full documentation can be found in the implementation {@link ModificationBinaryTree}.
 */
public interface ModificationBinaryTreeInterface {

    /**
     * Returns the hierarchical path of a ModificationGroup in the tree structure.
     * The path is constructed using group names separated by " / " (e.g., "Root / Europe / Hungary").
     *
     * @param modificationGroup The group to locate (non-null)
     * @return The full path in the tree, or an empty string if not found
     * @throws NullPointerException if modificationGroup is null
     */
    String getTreePath(ModificationGroup modificationGroup);

    /**
     * Inserts a new modification group into the tree while maintaining the sorting order.
     *
     * @param modificationGroup The group to insert (cannot be null)
     * @throws NullPointerException if modificationGroup is null
     */
    void insert(ModificationGroup modificationGroup);

    /**
     * Finds the node containing the specified modification group using BFS(Breadth-first Search).
     * Public method, therefore nullPointerException can still occur here, despite the Objects.requireNonNull further up the chain.
     *
     * @param modificationGroup The modification group to find (non-null)
     * @return The Node in which the modificationGroup is found
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if modificationGroup is not found or tree is empty
     */
    ModificationBinaryTree.Node searchNodesForModificationGroup(ModificationGroup modificationGroup);

    /**
     * Finds the node containing the specified modification using BFS (Breadth-first Search).
     * Public method, therefore nullPointerException can still occur here, despite the Objects.requireNonNull further up the chain.
     *
     * @param modification The modification to find
     * @return The parent ModificationGroup of the modification the method searches for
     * @throws NullPointerException   if modification is null
     * @throws NoSuchElementException if tree is empty or modification not found
     */
    ModificationBinaryTree.Node searchNodesForModifications(Modification modification);

    /**
     * Finds the tree level of the specified ModificationGroup. If not successful uses BFS.
     *
     * @param modificationGroup The group to locate (non-null)
     * @return The level (root = 0), or -1 if not found
     */
    int getLevelNumberOfModificationGroup(ModificationGroup modificationGroup);

    /**
     * Returns a Set<ModificationGroup> of all ModificationGroup on this level and below it, using BFS.
     *
     * @param upUntilThisLevel the level up until to count the ModificationGroups
     * @return Set<ModificationGroup> the set of ModificationGroups up until the give level
     */
    Set<ModificationGroup> getSetOfModificationGroupsUpUntilGivenLevel(ModificationGroup modificationGroup, int upUntilThisLevel);

    /**
     * Finds all modifications belonging to a group and its children.
     *
     * @param modificationGroup The starting group (cannot be null)
     * @return Set of all modifications in the subtree
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if the group is not found
     */
    Set<Modification> getModificationsOfGivenModificationGroupAndItsChildren(ModificationGroup modificationGroup);

    /**
     * Finds a modification by ID at a specific tree level.
     *
     * @param id    The modification ID to search for
     * @param level The tree level to search (0-based)
     * @return The found modification
     * @throws NoSuchElementException if no matching modification exists at the level
     */
    Modification findModificationOnGivenLevelById(int id, int level);

    /**
     * Gets all modification groups at a specific tree level.
     *
     * @param modificationGroup Unused parameter (maintained for backward compatibility)
     * @param onThisLevel       The target level (0-based)
     * @return Set of modification groups at the level, empty set if level doesn't exist
     */
    Set<ModificationGroup> getListOfModificationGroupsOnGivenLevel(ModificationGroup modificationGroup, int onThisLevel);

    /**
     * Performs an in-order traversal (left-root-right) and displays elements.
     */
    void inOrder();

    /**
     * Performs a reverse-order traversal (right-root-left) and displays elements.
     */
    void reverseOrder();

    /**
     * Prints the tree structure to standard output.
     */
    void printTreeStructure();

    /**
     * Nested interface representing a node in the binary tree structure.
     */
    interface Node {
        /**
         * Gets the depth level of this node in the tree.
         *
         * @return The 0-based level (root = 0)
         */
        int getLevel();

        /**
         * Gets thenodeNumber of this node of the tree.
         *
         * @return The 0-based level (root = 0)
         */
        int getNodeNumber();

        /**
         * Gets the modification group stored in this node.
         *
         * @return The non-null modification group
         */
        ModificationGroup getModificationGroup();
    }
}