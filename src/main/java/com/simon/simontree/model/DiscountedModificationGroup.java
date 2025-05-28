package com.simon.simontree.model;

import java.util.Set;

public class DiscountedModificationGroup extends ModificationGroup{
    /**
     * Constructs a new ModificationGroup with a set of modifications and a name. This inherits from the {@link ModificationGroup} and overrides
     * the method calculateTotalCostOfEveryModificationUnderThisGroup.
     *
     * @param modifications the set of modifications to be included in this group
     * @param name          the name of the ModificationGroup
     * @throws NullPointerException if either modifications or name is null, comes from parent class's constructor
     */
    public DiscountedModificationGroup(Set<Modification> modifications, String name) {
        super(modifications, name);
    }

    /**
     * Calculates total cost of every Modification, which belong to the parent ModificationGroup.
     * @return total cost of every Modification under this group.
     */
    @Override
    public long calculateTotalCostOfEveryModificationUnderThisGroup() {
        return (long) (super.calculateTotalCostOfEveryModificationUnderThisGroup()*0.9);
    }
}
