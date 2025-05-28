package com.simon.simontree.model;

import com.simon.simontree.api.TreeElement;
import com.simon.simontree.enums.TypeOfModification;

import java.util.Objects;

/**
 * Represents a modification in the tree structure with associated costs and priority.
 * Each modification belongs to a {@link ModificationGroup}.
 * The class is extensible, you can extend it with specialized subclasses for custom functionality.
 *
 * <p><b>Key extension points:</b>
 *  <ul>
 *     <li>Override {@link #calculateTotalModificationCost(long param cost, TypeOfModification typeOfModification)} to implement custom cost calculation logic</li>
 *   </ul>
 *
 *   <p><b>Example subclass:</b>
 *   <pre>{@code
 * public class DiscountedModification extends Modification{
 *
 * public DiscountedModification(String name, int priorityValue, long cost, TypeOfModification typeOfModification) {
 *       super(name, priorityValue, cost, typeOfModification);
 *  }
 * @Override
 * protected Long calculateTotalModificationCost(long cost, TypeOfModification typeOfModification) {
 * return (long) (super.calculateTotalModificationCost(cost, typeOfModification)*0.9);
 *  }
 * }</pre>
 *
 * <p>Extends the abstract class {@link TreeElement}, which consist of:
 * <ul>
 * <li> protected static AtomicInteger id = new AtomicInteger(0);</li>
 * <li> protected String name;</li>
 * <li> protected int priorityValue;</li>
 * <li> protected long cost</li>
 * <li> protected int level</li>
 * </ul>
 *
 * <p>Null checks:
 * <ul>
 *   <li>nullcheked String name</li>
 *   <li>nullchecked TypeOfModification param typeOfModification</li>
 * </ul>
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>unique modificationID (auto-generated)</li>
 *   <li>base cost and {@link TypeOfModification} based totalModificationCost</li>
 *   <li>Reference to parent ModificationGroup</li>
 * </ul>
 *
 * @see TreeElement
 * @see ModificationGroup
 * @see TypeOfModification
 */
public class Modification extends TreeElement {
    /**
     * {@link TypeOfModification} affecting cost calculation, enum with an int value
     */
    protected final TypeOfModification typeOfModification;
    /**
     * The total calculated cost (base cost × type multiplier (type multiplier is from TypeOfModification enum))
     */
    protected final long totalModificationCost;
    /**
     * Unique identifier for this Modification, auto-generated using AtomicInteger from {@link TreeElement}
     */
    protected final int modificationId;
    /**
     * The ModificationGroup, which is the parent of this Modification, needed for parent-child hierarchy implementation
     */
    protected ModificationGroup parent;

    /**
     * Constructs a new ModificationGroup with a set of modifications and a name.
     *
     * @param typeOfModification the set of modifications to be included in this group
     * @param name               the name of the ModificationGroup
     * @throws NullPointerException if either modifications or name is null
     */
    public Modification(String name, int priorityValue, long cost, TypeOfModification typeOfModification) {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(typeOfModification, "TypeOfModification cannot be null");

        modificationId = id.incrementAndGet();
        this.name = name;
        this.priorityValue = priorityValue;
        this.cost = cost;
        this.typeOfModification = typeOfModification;
        this.totalModificationCost = calculateTotalModificationCost(cost, typeOfModification);
    }


    /**
     * Removes this Modification from parent ModificationGroup.
     *
     * @return boolean, true if the Modification was successfully removed from under its' parent ModificationGroup
     * @throws NullPointerException if Modification does not have a parent.
     */
    public boolean resolved() throws NullPointerException {
        if (parent == null) {
            throw new NullPointerException("This Modification was already removed or never had a parent");
        }
        if (parent.removeModification(this)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the total modification cost (base cost × typeOfModification enum value), automatically, used in constructor
     *
     * @return the calculated total cost
     */
    protected Long calculateTotalModificationCost(long cost, TypeOfModification typeOfModification) {
        return cost * typeOfModification.getValue();
    }

    public ModificationGroup getParent() {
        return parent;
    }

    public void setParent(ModificationGroup parent) {
        this.parent = parent;
    }

    public int getModificationId() {
        return modificationId;
    }

    public Long getTotalModificationCost() {
        return totalModificationCost;
    }

    public String getName() {
        return name;
    }

    public Integer getPriorityValue() {
        return priorityValue;
    }

    public Long getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private void calculateLevel() {
        this.level = parent.getLevel();
    }

    public TypeOfModification getTypeOfModification() {
        return typeOfModification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modification that = (Modification) o;
        return modificationId == that.modificationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modificationId);
    }

    @Override
    public String toString() {
        return "Modification{" +
                "cost=" + cost +
                ", typeOfModification=" + typeOfModification +
                ", totalModificationCost=" + totalModificationCost +
                ", modificationId=" + modificationId +
                ", name='" + name + '\'' +
                ", priorityValue=" + priorityValue +
                '}';
    }
}