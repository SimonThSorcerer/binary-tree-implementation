package com.simon.simontree.api;

import com.simon.simontree.model.Modification;
import com.simon.simontree.model.ModificationBinaryTree;
import com.simon.simontree.model.ModificationGroup;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * Defines operations for working with a binary tree structure containing modifications and modification groups.
 * Provides methods for tree traversal, searching, and cost calculations.
 * Full documentation can be found in the implementation{@link ModificationBinaryTreeOperations}.
 *
 */
public interface ModificationBinaryTreeOperationsInterface {

    /**
     * Finds a modification by ID at given tree level. Uses ModificationBinaryTree findModificationOnGivenLevelById method, which throws
     * NoSuchElementException if no modification exists with the given ID at the specified level, it is handled via a try-catch block, in the method.
     * IllegalArgumentException
     *
     * @param id the modification ID to search for
     * @param level the tree level to search in (0-based)
     * @return the found Modification
     * @throws IllegalArgumentException if level is invalid
     * @throws NoSuchElementException if id is invalid
     */
    Modification findModificationOnGivenLevelById(int id, int level);

    /**
     * Returns the total number of ModificationGroup in the tree up to the given level (level you have selected and the levels below)
     *
     * @throws IllegalArgumentException if level is invalid
     * @return immutable Set<ModificationGroup>
     * @param level the tree level to search in (0-based)
     */
    Set<ModificationGroup> getModificationGroupsUpToLevel(int level);

    /**
     * Returns the total number of ModificationGroup in the tree on a given level
     *
     * @throws IllegalArgumentException if level is invalid
     * @return immutable Set<ModificationGroup>
     * @param level the tree level to search in (0-based)
     */
    Set<ModificationGroup> getModificationGroupsAtLevel(int level);

    /**
     * Returns the total number of ModificationGroup in the tree on a given level
     *
     * @param modificationGroup a ModificationGroup object you wish to know the level of
     * @throws IllegalArgumentException if level is invalid
     * @return int value of the level (0-based)
     */
    int getModificationGroupLevel(ModificationGroup modificationGroup);

    /**
     * Calculates the total cost of all modifications under a given group (including its level and all below)
     * @param modificationGroup the group to calculate the cost for
     * @return the total calculated cost
     */
    Long calculateModificationGroupCostOnGivenLevel(ModificationGroup modificationGroup, int level);

    /**
     * Finds the modification with the highest priority value on a given level
     * @param level the level to search in
     * @throws IllegalArgumentException if level is invalid
     * @return the modification with the highest priority, or null if none found
     */
    Modification findBiggestPriorityValueModificationOnAGivenLevel(int level);

}