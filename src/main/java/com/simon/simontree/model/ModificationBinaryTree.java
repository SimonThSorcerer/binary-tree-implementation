package com.simon.simontree.model;

import com.simon.simontree.api.ModificationBinaryTreeInterface;
import com.simon.simontree.enums.TreeLogic;
import com.simon.simontree.enums.TypeOfModification;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread-safe custom binary tree that stores {@link ModificationGroup} objects
 * with customizable sorting logic. The tree supports multiple tree logic (name, priority, total cost), various traversal methods
 * and level-based operations. Uses Breadth-first search for many operations.
 * Capable of adding and removing {@link ModificationGroup}.
 * Auto-increments nodeCounter, totalNumberOfElements, totalNumberOfLevels.
 * Implements the interface {@link ModificationBinaryTreeInterface}.
 * This is the most important part of the library, and does most of the main calculations.
 *
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>auto-incremented nodeCounter, totalNumberOfElements, totalNumberOfLevels</li>
 *   <li>base cost and {@link TypeOfModification} based totalModificationCost</li>
 *   <li>reference to parent ModificationGroup</li>
 *   <li>most methods in this class are called in the {@link com.simon.simontree.api.ModificationBinaryTreeOperations} public-facing control class</li>
 * </ul>
 *
 * @see ModificationBinaryTreeInterface
 * @see ModificationGroup
 * @see TypeOfModification
 */
public class ModificationBinaryTree implements ModificationBinaryTreeInterface {
    /**
     * Auto-incremented total number of elements counter.
     */
    private final AtomicInteger totalNumberOfElements = new AtomicInteger();
    /**
     * Auto-incremented node number counter.
     */
    private final AtomicInteger nodeCounter = new AtomicInteger();
    /**
     * Simple lock Object for tree related operations.
     */
    private final Object treeLock = new Object();
    /**
     * Enum, which sets the sorting logic of the binary tree (name, priority, total cost).
     */
    private final TreeLogic treeLogic;
    /**
     * Thread-safe {@link Node} object, required for Node creation.
     */
    private volatile Node root;
    /**
     * Number of levels, calculated by calculateTotalNumberOfLevels(), automatically set during initialization.
     */
    private int totalNumberOfLevels = 0;
    /**
     * Total number of ModificationGroups, calculated in insert().
     */
    private Queue<ModificationGroup> allModificationGroups = new ConcurrentLinkedQueue<>();


    /**
     * Constructs a new ModificationBinaryTree instance with a root Node, resets number of elements counter and with treelogic enum
     * Automatically calculates totalNumberOfLevels.
     *
     * @param treeLogic enum, which defines the sorting logic of the binary tree, 3 options to sort {@link ModificationGroup} objects (name, priority, total cost))
     */
    public ModificationBinaryTree(TreeLogic treeLogic) {
        this.root = null;
        this.totalNumberOfElements.set(0);
        this.treeLogic = treeLogic;
    }

    public Queue<ModificationGroup> getAllModificationGroups() {
        return allModificationGroups;
    }

    /**
     * Returns the hierarchical path of a ModificationGroup in the tree structure.
     * The path is constructed using group names separated by " / " (e.g., "Root / Europe / Hungary").
     *
     * @param modificationGroup The group to locate (non-null)
     * @return The full path in the tree, or an empty string if not found
     * @throws NullPointerException if modificationGroup is null
     */
    @Override
    public String getTreePath(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        List<String> path = new ArrayList<>();
        boolean found = buildPath(root, modificationGroup, path);

        if (!found) {
            return "";
        }

        Collections.reverse(path);
        return String.join(" / ", path);
    }


    /**
     * Helper method: Recursively builds the path from root to target group.
     *
     * @return true if the group was found in the subtree
     */
    private boolean buildPath(Node node, ModificationGroup modificationGroup, List<String> path) {
        if (node == null) {
            return false;
        }

        if (modificationGroup.equals(node.modificationGroup)) {
            path.add(node.modificationGroup.getName());
            return true;
        }

        if (buildPath(node.left, modificationGroup, path) || buildPath(node.right, modificationGroup, path)) {
            path.add(node.modificationGroup.getName());
            return true;
        }

        return false;
    }

    /**
     * Inserts a new element into the tree while maintaining thread safety. Current level must always be 0 for auto-incrementation to work.
     * Auto-increments totalNumberOfElements. Establishes parent-child relationship.
     *
     * @param modificationGroup The ModificationGroup to insert (cannot be null)
     * @throws NullPointerException if modificationGroup is null
     */
    @Override
    public void insert(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        synchronized (treeLock) {
            if (root == null) {
                root = modificationGroup.createNode(nodeCounter.incrementAndGet(), 0);
                modificationGroup.setLevel(0);
                totalNumberOfElements.incrementAndGet();
                allModificationGroups.add(modificationGroup);
                return;
            }

            if (containsNode(root, modificationGroup)) {
                throw new IllegalStateException("ModificationGroup already exists in tree");
            }

            Node parentNode = findParentForInsertion(root, modificationGroup);
            Node newNode = modificationGroup.createNode(
                    nodeCounter.incrementAndGet(),
                    parentNode.getLevel() + 1
            );

            int comparison = compareGroupsDRYComponent(parentNode.modificationGroup, modificationGroup);
            if (comparison < 0) {
                parentNode.right = newNode;
            } else {
                parentNode.left = newNode;
            }


            try {
                parentNode.getModificationGroup().addChildModificationGroup(modificationGroup);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Failed to establish parent-child relationship: " + e.getMessage());
            }

            allModificationGroups.add(modificationGroup);
            totalNumberOfElements.incrementAndGet();
            totalNumberOfLevels = calculateTotalNumberOfLevels();
        }
    }

    /**
     * Helper method: Recursively checks whether the object you are inserting already in the tree.
     * Uses equals, which is overridden in ModificationGroup (uses the unique ID).
     *
     * @return true if the group was found in the subtree
     */
    private boolean containsNode(Node current, ModificationGroup group) {
        if (current == null) {
            return false;
        }
        int comparisonResult = compareGroupsDRYComponent(current.modificationGroup, group);
        if (comparisonResult == 0) {
            return true;
        }
        return 0 < comparisonResult ? containsNode(current.left, group) : containsNode(current.right, group);
    }

    /**
     * Helper method. Finds the parent of the given ModificationGroup in the tree.Specifically used by the insert method.
     * Can not be substituted with findParentDRYComponent.
     *
     * @param modificationGroup The ModificationGroup, of which the parent we are searching for
     * @return The ModificationGroup parent, or null if no parent exists
     */
    private Node findParentForInsertion(Node current, ModificationGroup modificationGroup) {
        if (current == null) return null;

        int comparison = compareGroupsDRYComponent(current.modificationGroup, modificationGroup);

        if (comparison < 0) {
            if (current.right == null) {
                return current;
            }
            return findParentForInsertion(current.right, modificationGroup);
        } else {
            if (current.left == null) {
                return current;
            }
            return findParentForInsertion(current.left, modificationGroup);
        }
    }

    /**
     * Removes a ModificationGroup from the tree while maintaining the sorting order.
     *
     * @param modificationGroup The group to remove (cannot be null)
     * @throws NullPointerException if modificationGroup is null
     */
    public boolean removeModificationGroup(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        synchronized (treeLock) {
            Node nodeToRemove = searchNodesForModificationGroup(modificationGroup);
            if (nodeToRemove == null) {
                return false; // Group not found in tree
            }

            allModificationGroups.remove(modificationGroup);

            Node parent = findParentDRYComponent(root, modificationGroup);
            if (parent != null) {
                parent.getModificationGroup().removeChildModificationGroup(modificationGroup);
            }

            root = removeNode(root, modificationGroup);
            totalNumberOfElements.decrementAndGet();
            totalNumberOfLevels = calculateTotalNumberOfLevels();
            modificationGroup.setLevel(-1);
            return true;
        }
    }

    /**
     * Helper method to remove a node while maintain binary tree structure
     */
    private Node removeNode(Node root, ModificationGroup group) {
        if (root == null) {
            return null;
        }

        int comparison = compareGroupsDRYComponent(root.modificationGroup, group);
        if (comparison < 0) {
            root.right = removeNode(root.right, group);
        } else if (comparison > 0) {
            root.left = removeNode(root.left, group);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            } else {
                Node minNode = findMinNode(root.right);
                root.modificationGroup = minNode.modificationGroup;
                root.right = removeNode(root.right, minNode.modificationGroup);
            }
        }
        return root;
    }

    /**
     * Helper method, finds the minimum node in a subtree
     */
    private Node findMinNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Helper method, compares two ModificationGroups based on current tree logic.
     */
    private int compareGroupsDRYComponent(ModificationGroup a, ModificationGroup b) {
        switch (treeLogic) {
            case NAME:
                return a.getName().compareTo(b.getName());
            case PRIORITY:
                return Integer.compare(a.getPriorityValue(), b.getPriorityValue());
            case TOTAL_COST:
                return Long.compare(a.getCost(), b.getCost());
            default:
                throw new IllegalStateException("Unknown tree logic: " + treeLogic);
        }
    }

    /**
     * Finds the parent of the given ModificationGroup in the tree. Uses compareGroupsDRYComponent helper method.
     * A component in-line with the DRY Don1t Repeat Yourself principle, it can not substitute findParent in the current setup.
     *
     * @param modificationGroup The ModificationGroup, of which the parent we are searching for
     * @return The parents Node, or null if no parent exists
     */
    public Node findParentDRYComponent(Node current, ModificationGroup modificationGroup) {
        if (current == null || current.modificationGroup.equals(modificationGroup)) {
            return null;
        }

        int cmp = compareGroupsDRYComponent(current.modificationGroup, modificationGroup);
        if (0 < cmp) {
            if (current.left != null && current.left.modificationGroup.equals(modificationGroup)) {
                return current;
            }
            return findParentDRYComponent(current.left, modificationGroup);
        } else {
            if (current.right != null && current.right.modificationGroup.equals(modificationGroup)) {
                return current;
            }
            return findParentDRYComponent(current.right, modificationGroup);
        }
    }


    /**
     * Finds the node containing the specified modification group using BFS(Breadth-first Search).
     * Public method, therefore nullPointerException can still occur here, despite the Objects.requireNonNull further up the chain.
     *
     * @param modificationGroup The modification group to find (non-null)
     * @return The Node in which the modificationGroup is found
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if modificationGroup is not found or tree is empty
     */
    @Override
    public Node searchNodesForModificationGroup(ModificationGroup modificationGroup) {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        Queue<Node> nodeQueue = new LinkedList<>();
        final Node currentRoot = this.root;
        if (currentRoot == null) {
            throw new NoSuchElementException("Tree is empty");
        }
        nodeQueue.add(currentRoot);
        while (!nodeQueue.isEmpty()) {
            Node selected = nodeQueue.poll();
            if (selected.modificationGroup.equals(modificationGroup)) {
                return selected;
            }
            if (selected.left != null) {
                nodeQueue.add(selected.left);
            }
            if (selected.right != null) {
                nodeQueue.add(selected.right);
            }
        }
        throw new NoSuchElementException("ModificationGroup not found in tree");
    }

    /**
     * Finds the node containing the specified modification using BFS (Breadth-first Search).
     * Public method, therefore nullPointerException can still occur here, despite the Objects.requireNonNull further up the chain.
     *
     * @param modification The modification to find
     * @return The parent ModificationGroup of the modification the method searches for
     * @throws NullPointerException   if modification is null
     * @throws NoSuchElementException if tree is empty or modification not found
     */
    @Override
    public Node searchNodesForModifications(Modification modification) {
        Objects.requireNonNull(modification, "Modification cannot be null");

        Queue<Node> nodeQueue = new LinkedList<>();
        final Node currentRoot = this.root;
        if (currentRoot == null) {
            throw new NoSuchElementException("Tree is empty");
        }
        nodeQueue.add(currentRoot);
        while (!nodeQueue.isEmpty()) {
            Node selected = nodeQueue.poll();
            Set<Modification> modifications = selected.getModificationGroup().getModifications();

            if (modifications.contains(modification)) {
                return selected;
            }
            if (selected.left != null) {
                nodeQueue.add(selected.left);
            }
            if (selected.right != null) {
                nodeQueue.add(selected.right);
            }
        }
        throw new NoSuchElementException("Modification not found in tree");
    }

    /**
     * Finds the tree level of the specified ModificationGroup. If not successful uses BFS.
     *
     * @param modificationGroup The group to locate (can be null)
     * @return The level (root = 0), or -1 if not found
     */
    @Override
    public int getLevelNumberOfModificationGroup(ModificationGroup modificationGroup) {
        if (modificationGroup == null) {
            return -1;
        }

        if (modificationGroup.getLevel() >= 0 && searchNodesForModificationGroup(modificationGroup) != null) {
            return modificationGroup.getLevel();
        }
        return getLevelNumberOfModificationGroupBFS(modificationGroup);
    }

    /**
     * Finds the tree level of the specified ModificationGroup suing breadth-first search.
     *
     * @param modificationGroup The group to locate (non-null)
     * @return The level (root = 0), or -1 if not found
     */
    private int getLevelNumberOfModificationGroupBFS(ModificationGroup modificationGroup) {
        final Node currentRoot = this.root;
        if (currentRoot == null) {
            return -1;
        }

        Queue<Node> queueOfElementsCurrentLevel = new LinkedList<>();
        Queue<Node> queueOfElementsOnNextLevel = new LinkedList<>();
        queueOfElementsCurrentLevel.add(currentRoot);
        int levelNumber = 0;

        while (!queueOfElementsCurrentLevel.isEmpty()) {
            Node currentNode = queueOfElementsCurrentLevel.poll();

            if (modificationGroup.equals(currentNode.modificationGroup)) {
                return levelNumber;
            }
            if (currentNode.left != null) {
                queueOfElementsOnNextLevel.add(currentNode.left);
            }
            if (currentNode.right != null) {
                queueOfElementsOnNextLevel.add(currentNode.right);
            }
            if (queueOfElementsCurrentLevel.isEmpty()) {
                queueOfElementsCurrentLevel = queueOfElementsOnNextLevel;
                queueOfElementsOnNextLevel = new LinkedList<>();
                levelNumber++;
            }
        }
        return -1;
    }

    /**
     * Returns a Set<ModificationGroup> of all ModificationGroup on this level and below it, using BFS.
     *
     * @param upUntilThisLevel the level up until to count the ModificationGroups
     * @return Set<ModificationGroup> the set of ModificationGroups up until the give level
     */
    @Override
    public Set<ModificationGroup> getSetOfModificationGroupsUpUntilGivenLevel(ModificationGroup modificationGroup, int upUntilThisLevel) {
        Set<ModificationGroup> modificationsUntilThisLevel = new HashSet<>();

        if (root == null) {
            return modificationsUntilThisLevel;
        }

        Queue<Node> queueOfElements = new LinkedList<>();
        Queue<Node> queueOfElementsOnNextLevel = new LinkedList<>();
        queueOfElements.add(root);
        int levelNumber = 0;

        while (!queueOfElements.isEmpty()) {
            Node currentNode = queueOfElements.poll();

            modificationsUntilThisLevel.add(currentNode.modificationGroup);

            if (levelNumber == upUntilThisLevel) {
                return modificationsUntilThisLevel;
            }
            if (currentNode.left != null) {
                queueOfElementsOnNextLevel.add(currentNode.left);
            }
            if (currentNode.right != null) {
                queueOfElementsOnNextLevel.add(currentNode.right);
            }
            if (queueOfElements.isEmpty()) {
                queueOfElements = queueOfElementsOnNextLevel;
                queueOfElementsOnNextLevel = new LinkedList<>();
                levelNumber++;
            }
        }
        return modificationsUntilThisLevel;
    }

    /**
     * Returns a Set<Modification> of all Modification of the given ModificationGroup and its children. It uses BFS.
     *
     * @param modificationGroup The ModificationGroup, which will be investigated
     * @return Set<Modification> of all Modification of this ModificationGroup and its' children
     * @throws NullPointerException   if modificationGroup is null
     * @throws NoSuchElementException if the group is not found in the tree
     */
    @Override
    public Set<Modification> getModificationsOfGivenModificationGroupAndItsChildren(ModificationGroup modificationGroup) throws NoSuchElementException {
        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");

        Set<Modification> allModifications = new HashSet<>();
        collectModificationsRecursively(modificationGroup, allModifications);
        return allModifications;
    }

    /**
     * Helper method to find the Node containing a specific ModificationGroup (BFS).
     */
    private Node findNodeByModificationGroup(ModificationGroup group) throws NoSuchElementException {
        if (root == null) {
            throw new NoSuchElementException("Tree is empty");
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (group.equals(current.modificationGroup)) {
                return current;
            }
            if (current.left != null) {
                queue.add(current.left);
            }
            if (current.right != null) {
                queue.add(current.right);
            }
        }

        throw new NoSuchElementException("ModificationGroup not found in tree");
    }

    /**
     * Collects all modifications from a given node and its children into a set.
     *
     * @param group       ModificationGroup
     * @param accumulator The set in which you collect modifications, needed to be recursive
     */
    private void collectModificationsRecursively(ModificationGroup group, Set<Modification> accumulator) {
        synchronized (group.modificationsLock) {
            accumulator.addAll(group.getModifications());
        }

        synchronized (group.childModificationsLock) {
            for (ModificationGroup child : group.getChildModificationGroups()) {
                collectModificationsRecursively(child, accumulator);
            }
        }
    }

    /**
     * Finds a modification with the specified ID at the given tree level.
     *
     * @param id    The ID of the modification to find (must be positive)
     * @param level The tree level to search in (0-based)
     * @return The found Modification
     * @throws NoSuchElementException   if no modification with the given ID exists at the specified level
     * @throws IllegalArgumentException if level is invalid (negative or exceeds tree depth)
     * @throws NullPointerException     if the tree is empty
     */
    @Override
    public Modification findModificationOnGivenLevelById(int id, int level) throws NoSuchElementException {
        if (id <= 0) {
            throw new IllegalArgumentException("Modification ID must be positive");
        }
        if (level < 0 || getTotalNumberOfLevels() <= level) {
            throw new IllegalArgumentException("Level must be between 0 and " + (getTotalNumberOfLevels() - 1));
        }
        if (root == null) {
            throw new NullPointerException("Tree is empty");
        }

        return getListOfModificationGroupsOnGivenLevel(root.getModificationGroup(), level).stream()
                .flatMap(group -> group.getModifications().stream())
                .filter(mod -> mod.getModificationId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No modification found with ID = " + id + " and level = " + level)
                );
    }

    /**
     * Retrieves all ModificationGroups at a given level of the binary tree.
     * The method is public, therefore it check for NullPointerException and has ModificationGroup as an input value.
     *
     * @param modificationGroup The ModificationGroup which you want to check
     * @param targetLevel       The target level to search for (0-based)
     * @return Set<ModificationGroup> found at the specified level, empty set if level doesn't exist
     */
    @Override
    public Set<ModificationGroup> getListOfModificationGroupsOnGivenLevel(
            ModificationGroup modificationGroup, int targetLevel) {

        Objects.requireNonNull(modificationGroup, "ModificationGroup cannot be null");
        Set<ModificationGroup> result = new HashSet<>();

        if (root == null) {
            return result;
        }

        if (!modificationGroup.equals(root.modificationGroup)) {
            throw new IllegalArgumentException("ModificationGroup must match tree root");
        }

        if (targetLevel == 0) {
            result.add(root.modificationGroup);
            return result;
        }

        Queue<Node> currentLevel = new LinkedList<>();
        Queue<Node> nextLevel = new LinkedList<>();
        currentLevel.add(root);
        int currentDepth = 0;

        while (!currentLevel.isEmpty() && currentDepth <= targetLevel) {
            Node node = currentLevel.poll();

            if (node.left != null) nextLevel.add(node.left);
            if (node.right != null) nextLevel.add(node.right);

            if (currentLevel.isEmpty()) {
                if (++currentDepth == targetLevel) {
                    nextLevel.forEach(n -> result.add(n.modificationGroup));
                    break;
                }
                currentLevel = nextLevel;
                nextLevel = new LinkedList<>();
            }
        }

        return result;
    }

    /**
     * Calculates the total number of levels in the tree. In the special case that only root node exists it return 0 correctly.
     * Returns -1 in case of empty tree.
     * @return The number of levels (0 for empty tree), returns -1 if the tree is empty
     */
    public int calculateTotalNumberOfLevels() {
        if (root == null) {
            return -1;
        }
        if (root.left == null && root.right == null) {
            return 0;
        }
        totalNumberOfLevels = 0;
        Queue<Node> queueOfElements = new LinkedList<>();
        Queue<Node> queueOfElementsOnNextLevel = new LinkedList<>();
        queueOfElements.add(root);


        while (!queueOfElements.isEmpty()) {
            Node currentNode = queueOfElements.poll();

            if (currentNode.left != null) {
                queueOfElementsOnNextLevel.add(currentNode.left);
            }
            if (currentNode.right != null) {
                queueOfElementsOnNextLevel.add(currentNode.right);
            }
            if (queueOfElements.isEmpty()) {
                queueOfElements = queueOfElementsOnNextLevel;
                queueOfElementsOnNextLevel = new LinkedList<>();
                totalNumberOfLevels++;
            }
        }

        return totalNumberOfLevels;
    }


    /**
     * Prints the tree structure to standard output.
     * The display format depends on the current treeLogic setting (NAME, PRIORITY, or TOTAL_COST), uses the printTreeSttructureLogic(Node root, String prefix) method below.
     */
    @Override
    public void printTreeStructure() {
        final Node currentFinalRoot = this.root;
        if (currentFinalRoot == null) {
            System.out.println("Empty tree.");
            return;
        }
        printTreeStructureLogic(currentFinalRoot, "");
    }

    public void printTreeLevels() {
        if (root == null) {
            System.out.println("Tree is empty");
            return;
        }

        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            System.out.println("Level " + current.getLevel() + ": " +
                    current.getModificationGroup().getName());

            if (current.left != null) queue.add(current.left);
            if (current.right != null) queue.add(current.right);
        }
    }

    /**
     * Helper method for recursively printing the tree structure. Uses the dryModificationDisplayElement(root.modificationGroup, prefix) helper method.
     *
     * @param root   The current node being processed
     * @param prefix The indentation prefix for proper tree visualization, needed because it is recursive
     */
    private void printTreeStructureLogic(Node root, String prefix) {
        if (root == null) {
            return;
        }

        dryModificationDisplayElement(root.getModificationGroup(), prefix);

        printTreeStructureLogic(root.left, prefix + "    ");
        printTreeStructureLogic(root.right, prefix + "    ");
    }

    /**
     * Performs an in-order traversal of the tree (left -> root -> right) and displays each element. This is the equivalent of sort() by natural ordering,
     * which means from smallest to biggest.
     */
    @Override
    public void inOrder() {
        inOrderTraversal(root);
    }

    public void inOrderTraversal(Node root) {

        if (root == null) {
            return;
        }

        final Node currentRoot = root;

        inOrderTraversal(root.left);
        dryModificationDisplayElement(currentRoot.modificationGroup, " ");
        inOrderTraversal(root.right);
    }

    /**
     * Performs a reverse-order traversal of the tree (right -> root -> left) and displays each element. This is the equivalent of sort(Collections.reverseOrder()),
     * which means from biggest to smallest,
     */
    @Override
    public void reverseOrder() {
        reverseOrderTraversal(root);
    }


    public void reverseOrderTraversal(Node root) {
        if (root == null) {
            return;
        }

        final Node currentRoot = root;

        reverseOrderTraversal(root.right);
        dryModificationDisplayElement(currentRoot.modificationGroup, " ");
        reverseOrderTraversal(root.left);
    }

    /**
     * Displays a modification group with proper formatting based on treeLogic setting. This is a helper method that is used by reverseOrder(),
     * inOrder(), printTreeStructureLogic(), DRY is short for Don't Repeat Yourself, a coding paradigm like KISS.
     *
     * @param modificationGroup The modification group to display
     * @param prefix            The indentation prefix for alignment
     */
    private void dryModificationDisplayElement(ModificationGroup modificationGroup, String prefix) {
        switch (treeLogic) {
            case NAME:
                System.out.println(prefix + "--- " + modificationGroup.getName() +
                        " (ID: " + modificationGroup.getModificationGroupId() + ") ");
                break;
            case PRIORITY:
                System.out.println(prefix + "--- " + modificationGroup.getPriorityValue() +
                        " (ID: " + modificationGroup.getModificationGroupId() + ")");
                break;
            case TOTAL_COST:
                System.out.println(prefix + "--- " + modificationGroup.getCost() +
                        " (ID: " + modificationGroup.getModificationGroupId() + ")");
                break;
        }
    }

    public Node getRoot() {
        return root;
    }

    public AtomicInteger getTotalNumberOfElements() {
        return totalNumberOfElements;
    }

    public int getTotalNumberOfLevels() {
        return totalNumberOfLevels;
    }

    /**
     * A static nested class, this represents a Node in the binary tree, the basis of the tree structure. Only ModificationGroups are added as nodes, Modifications are not.
     * It implements ModificationBinaryTreeInterface.Node nested interface.
     */
    public static final class Node implements ModificationBinaryTreeInterface.Node {
        private final int nodeNumber;
        private ModificationGroup modificationGroup;
        private int level;
        private Node right, left;

        Node(ModificationGroup modificationGroup, int nodeNumber, int level) {
            this.modificationGroup = Objects.requireNonNull(modificationGroup);
            this.nodeNumber = nodeNumber;
            this.level = level;
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public int getNodeNumber() {
            return nodeNumber;
        }

        @Override
        public ModificationGroup getModificationGroup() {
            return modificationGroup;
        }
    }
}
