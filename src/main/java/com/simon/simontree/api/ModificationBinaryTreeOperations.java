package com.simon.simontree.api;

import com.simon.simontree.model.Modification;
import com.simon.simontree.model.ModificationBinaryTree;
import com.simon.simontree.model.ModificationGroup;
import com.simon.simontree.util.HelperMethods;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Provides all the needed operations for working with the binary tree {@link ModificationBinaryTree}, which consist Modifications and Modificationgroups.
 * Some-methods use paralellstream for better performance.
 * Uses the helper methods from {@link HelperMethods},
 * This is the control panel for the ModificationBinaryTree with tree traversal, search, and various sorting functionalities.
 * Control the binary tree {@link ModificationBinaryTree} via this class.
 * <p>
 * Implements the interface {@link ModificationBinaryTreeOperationsInterface} and {@link ModificationBinaryTreeInterface}
 * Usage:
 * 1., First create Set<Modification>, accepts any type of Collection, to insert single elements use Collections.singleton()
 * 2., add to {@link ModificationGroup}, via the constructor
 * 3., instantiate {@link ModificationBinaryTree} with preferred sorting logic (based on name, based on priority value, based on total cost in Modification, use the enum TreeLogic)
 * 4., add {@link ModificationGroup}s to ModificationBinaryTree
 * 5., Instantiate {@link ModificationBinaryTreeOperations}, pass ModificationBinaryTree to constructor
 * 6., use insert() method of ModificationBinaryTreeOperations instance to add a ModificationGroups
 *
 * <p>Null checks:
 * <ul>
 *   <li>null-checked queuOfModificationGroups</li>
 *   <li>null-checked setOfAllModifications</li>
 * </ul>
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>Level-based element retrieval</li>
 *   <li>Cost and priority calculations</li>
 *   <li>Sorting operations for modifications and groups</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 *
 * @see ModificationBinaryTreeInterface
 * @see ModificationBinaryTreeOperationsInterface
 * @see ModificationGroup
 * @see Modification
 */
public class ModificationBinaryTreeOperations implements ModificationBinaryTreeOperationsInterface, ModificationBinaryTreeInterface {
    /**
     * The binary tree instance this operations class works with
     */
    private final ModificationBinaryTree modificationBinaryTree;
    /**
     * Thread-safe queue of modification groups
     */
    private final Queue<ModificationGroup> queueOfModificationGroups = new ConcurrentLinkedQueue<>();
    /**
     * AtomicInteger for calculating total number of levels in the tree, but increment in ModificationBinaryTree
     */
    private final AtomicInteger totalNumberOfLevels = new AtomicInteger(0);
    /**
     * Instance of the HelperMethod class, which implements singleton.
     */
    private final HelperMethods helperMethods = HelperMethods.getInstance();
    /**
     * Set containing all Modifications in the tree, calcualted freshly, upon sue, in the relevant method, from values from ModificationBinaryTree
     */
    private Set<Modification> setOfAllModifications = new HashSet<>();

    /**
     * Constructs a new ModificationBinaryTreeOperations object with a set of modifications and a name.
     * Auto-increments totalNumberOfLevels.
     *
     * @param modificationBinaryTree ModificationBinaryTree instance, that you need for background calculations
     * @throws NullPointerException if modificationBinaryTree is null
     */
    public ModificationBinaryTreeOperations(ModificationBinaryTree modificationBinaryTree) {
        Objects.requireNonNull(modificationBinaryTree, "ModificationBinaryTree cannot be null");
        this.modificationBinaryTree = modificationBinaryTree;
        this.totalNumberOfLevels.set(modificationBinaryTree.getTotalNumberOfLevels());
    }

    /**
     * Calls the insert method of ModificationBinaryTree, you can find the documentation in the {@link ModificationBinaryTree} class
     */
    public void insert(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        try {
            if (queueOfModificationGroups.contains(modificationGroup)) {
                System.err.println("Warning: ModificationGroup already in queue: " + modificationGroup.getName());
                return;
            }

            modificationBinaryTree.insert(modificationGroup);
            queueOfModificationGroups.add(modificationGroup);

        } catch (IllegalStateException e) {
            System.err.println("Insert failed: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModificationBinaryTree.Node searchNodesForModificationGroup(ModificationGroup modificationGroup) {
        return modificationBinaryTree.searchNodesForModificationGroup(modificationGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModificationBinaryTree.Node searchNodesForModifications(Modification modification) {
        return modificationBinaryTree.searchNodesForModifications(modification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevelNumberOfModificationGroup(ModificationGroup modificationGroup) {
        return modificationBinaryTree.getLevelNumberOfModificationGroup(modificationGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ModificationGroup> getSetOfModificationGroupsUpUntilGivenLevel(ModificationGroup modificationGroup, int upUntilThisLevel) {
        return modificationBinaryTree.getSetOfModificationGroupsUpUntilGivenLevel(modificationGroup, upUntilThisLevel);
    }

    /**
     * Calls the remove method of ModificationBinaryTree, you can find the documentation in the {@link ModificationBinaryTree} class
     */
    public boolean removeModificationGroup(ModificationGroup modificationGroup) {
        boolean removed = modificationBinaryTree.removeModificationGroup(modificationGroup);
        if (removed) {
            queueOfModificationGroups.remove(modificationGroup);
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ModificationGroup> getModificationGroupsUpToLevel(int level) {
        try {
            validateLevelDRYComponent(level);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return getCustomModificationBinaryTree().getSetOfModificationGroupsUpUntilGivenLevel(getCustomModificationBinaryTree().getRoot().getModificationGroup(), level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ModificationGroup> getModificationGroupsAtLevel(int level) {
        try {
            validateLevelDRYComponent(level);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return getCustomModificationBinaryTree().getListOfModificationGroupsOnGivenLevel(getCustomModificationBinaryTree().getRoot().getModificationGroup(), level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModificationGroupLevel(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");
        return getCustomModificationBinaryTree().getLevelNumberOfModificationGroup(modificationGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long calculateModificationGroupCostOnGivenLevel(ModificationGroup modificationGroup, int level) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");
        try {
            validateLevelDRYComponent(level);
        } catch (IllegalArgumentException e) {
            return null;
        }
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");
        Set<ModificationGroup> listOfModificationGroupsOnGivenLevel = modificationBinaryTree.getListOfModificationGroupsOnGivenLevel(modificationGroup, level);
        if (helperMethods.shouldUseParallel(listOfModificationGroupsOnGivenLevel)) {
            return listOfModificationGroupsOnGivenLevel.parallelStream().mapToLong(mg -> mg.getCost()).sum();
        } else {
            return listOfModificationGroupsOnGivenLevel.parallelStream().mapToLong(mg -> mg.getCost()).sum();
        }
    }

    /**
     * Returns a Set<Modification> of all Modification of the given ModificationGroup and its children. It uses BFS.
     * This only calls the method in the ModificationBinaryTree class.
     *
     * @param modificationGroup The ModificationGroup, which will be investigated
     * @return Set<Modification> of all Modification of this ModificationGroup and its' children
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if the group is not found in the tree
     */
    public Set<Modification> getModificationsOfGivenModificationGroupAndItsChildren(ModificationGroup modificationGroup) throws NoSuchElementException {
        return modificationBinaryTree.getModificationsOfGivenModificationGroupAndItsChildren(modificationGroup);
    }

    /**
     * Returns the sum of the total cost of all Modification of the given ModificationGroup and its children. It uses BFS.
     * This only calls the method in the ModificationBinaryTree class.
     *
     * @param modificationGroup The ModificationGroup, which will be investigated
     * @return Set<Modification> of all Modification of this ModificationGroup and its' children
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if the group is not found in the tree
     */
    public Long getTotalCostOfModificationsOfGivenModificationGroupAndItsChildren(ModificationGroup modificationGroup) throws NoSuchElementException {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");
        try {
            return modificationBinaryTree.getModificationsOfGivenModificationGroupAndItsChildren(modificationGroup).stream().mapToLong(mg -> mg.getTotalModificationCost()).sum();
        } catch (NoSuchElementException e) {
            System.err.println("ModificationGroup not found: " + modificationGroup.getName());
            throw e;
        }
    }

    public String getTreePath(ModificationGroup modificationGroup) {
        return modificationBinaryTree.getTreePath(modificationGroup);
    }

    /**
     * Finds a modification by ID at a specific level in the tree
     *
     * @param id    The ID of the modification to find
     * @param level The tree level to search
     * @return The found Modification
     * @throws IllegalArgumentException if level is invalid
     * @throws NoSuchElementException   if modification is not found
     * @throws NullPointerException     if the tree is empty
     */
    @Override
    public Modification findModificationOnGivenLevelById(int id, int level)
            throws IllegalArgumentException, NoSuchElementException {

        if (id <= 0) {
            throw new IllegalArgumentException("Modification ID must be positive");
        }
        validateLevelDRYComponent(level);

        try {
            Modification modification = modificationBinaryTree.findModificationOnGivenLevelById(id, level);
            if (modification == null) {
                throw new NoSuchElementException(
                        String.format("No modification with ID %d found at level %d", id, level)
                );
            }
            return modification;
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Tree is empty", e);
        }
    }

    @Override
    public Set<ModificationGroup> getListOfModificationGroupsOnGivenLevel(ModificationGroup modificationGroup, int onThisLevel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Modification findBiggestPriorityValueModificationOnAGivenLevel(int level) {
        try {
            validateLevelDRYComponent(level);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Set<ModificationGroup> groupsAtLevel = modificationBinaryTree
                .getListOfModificationGroupsOnGivenLevel(modificationBinaryTree.getRoot().getModificationGroup(), level);

        return groupsAtLevel.stream()
                .flatMap(mg -> mg.getModifications().stream())
                .max(Comparator.comparingInt(Modification::getPriorityValue))
                .orElse(null);
    }

    /**
     * Performs an in-order traversal by delegating to the underlying tree
     */
    public void inOrder() {
        modificationBinaryTree.inOrder();
    }

    /**
     * Performs a reverse-order traversal by delegating to the underlying tree
     */
    public void reverseOrder() {
        modificationBinaryTree.reverseOrder();
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalNumberOfLevels() {
        return modificationBinaryTree.getTotalNumberOfLevels(); // Always fresh
    }

    private void validateLevelDRYComponent(int level) throws IllegalArgumentException {
        if (level < 0 || getTotalNumberOfLevels() <= level) {
            throw new IllegalArgumentException(
                    "Level must be between 0 and " + (getTotalNumberOfLevels() - 1)
            );
        }
    }

    public ModificationBinaryTree getCustomModificationBinaryTree() {
        return modificationBinaryTree;
    }

    public Queue<ModificationGroup> getQueueOfModificationGroups() {
        return queueOfModificationGroups;
    }

    public Set<Modification> getSetOfAllModifications() {
        return modificationBinaryTree.getAllModificationGroups().stream().flatMap(mg -> mg.getModifications().stream()).collect(Collectors.toSet());
    }

    /**
     * Helper method, from the HelperMethods calls, sorts Modifications by name in ascending order.
     */
    public List<Modification> sortModificationsByNameAscending() {
        setOfAllModifications = getSetOfAllModifications();
        Objects.requireNonNull(setOfAllModifications, "Set<Modification> cannot be null");
        return helperMethods.sortByNameAscending(setOfAllModifications);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts Modifications by name in descending order.
     */
    public List<Modification> sortModificationByNameDescending() {
        Objects.requireNonNull(setOfAllModifications, "Set<Modification> cannot be null");
        return helperMethods.sortByNameDescending(setOfAllModifications);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts ModificationGroups by name in ascending order.
     */
    public List<ModificationGroup> sortModificationGroupByNameAscending() {
        Objects.requireNonNull(setOfAllModifications, "Queue<ModificationGroup> cannot be null");
        return helperMethods.sortByNameAscending(queueOfModificationGroups);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts ModificationGroups by name in descending order.
     */
    public List<ModificationGroup> sortModificationGroupByNameDescending() {
        Objects.requireNonNull(queueOfModificationGroups, "Queue<ModificationGroup> cannot be null");
        return helperMethods.sortByNameDescending(queueOfModificationGroups);
    }

    /**
     * Prints the tree structure by delegating to the underlying ModificationBinaryTree
     * The display format depends on the current treeLogic setting (NAME, PRIORITY, or TOTAL_COST)
     */
    public void printTreeStructure() {
        modificationBinaryTree.printTreeStructure();
    }

    /**
     * Helper method, from the HelperMethods calls, sorts Modifications by priority value in ascending order.
     */
    public List<Modification> sortModificationByPriorityValueAscending() {
        Objects.requireNonNull(setOfAllModifications, "Set<Modification> cannot be null");
        return helperMethods.sortByPriorityValueAscending(setOfAllModifications);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts Modifications by priority value in descending order.
     */
    public List<Modification> sortModificationByPriorityValueDescending() {
        Objects.requireNonNull(setOfAllModifications, "Set<Modification> cannot be null");
        return helperMethods.sortByPriorityValueDescending(setOfAllModifications);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts ModificationGroups by priority value in ascending order.
     */
    public List<ModificationGroup> sortModificationGroupByPriorityValueAscending() {
        Objects.requireNonNull(queueOfModificationGroups, "Queue<ModificationGroup> cannot be null");
        return helperMethods.sortByPriorityValueAscending(queueOfModificationGroups);
    }

    /**
     * Helper method, from the HelperMethods calls, sorts ModificationGroups by priority value in descending order.
     */
    public List<ModificationGroup> sortModificationGroupByPriorityValueDescending() {
        Objects.requireNonNull(queueOfModificationGroups, "Queue<ModificationGroup> cannot be null");
        return helperMethods.sortByPriorityValueDescending(queueOfModificationGroups);
    }
}
