package com.simon.simontree.model;

import com.simon.simontree.enums.TypeOfModification;

public class DiscountedModification extends Modification{
    /**
     * Constructs a new Modification.Automatically increments {@param modificationId} and calculates {@param totalModificationCost}.
     * Extend the parent {@ Modification}.
     *
     * @param name               the name of the modification
     * @param priorityValue      the priority value (higher = more important)
     * @param cost               the base cost of the modification
     * @param typeOfModification the type of modification, enum + int value (cannot be null)
     * @throws NullPointerException if name or typeOfModification is null, from the parent's constructor
     */
    public DiscountedModification(String name, int priorityValue, long cost, TypeOfModification typeOfModification) {
        super(name, priorityValue, cost, typeOfModification);
    }

    /**
     * Calculates total cost of every Modification, which belong to the parent ModificationGroup.
     * @return total cost of every Modification under this group.
     */
    @Override
    protected Long calculateTotalModificationCost(long cost, TypeOfModification typeOfModification) {
        return (long) (super.calculateTotalModificationCost(cost, typeOfModification)*0.9);
    }
}
