package com.simon.simontree.util;

import com.simon.simontree.model.Modification;
import com.simon.simontree.model.ModificationGroup;

import java.util.*;
import java.util.stream.Collectors;

public class HelperMethods {
    private HelperMethods() {}

    private static class SingletonHelper {
        private static final HelperMethods HELPER_METHODS_SINGLETON_INSTANCE = new HelperMethods();
    }

    public static HelperMethods getInstance() {
        return SingletonHelper.HELPER_METHODS_SINGLETON_INSTANCE;
    }

    public List<Modification> sortByNameAscending(Set<Modification> setOfAllModificationsWithinTheTree) {
        if (shouldUseParallel(setOfAllModificationsWithinTheTree)) {
            return setOfAllModificationsWithinTheTree.parallelStream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toUnmodifiableList());
        } else {
            return setOfAllModificationsWithinTheTree.stream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<ModificationGroup> sortByNameAscending(Queue<ModificationGroup> listOfModificationGroups) {
        if (shouldUseParallel(listOfModificationGroups)) {
            return listOfModificationGroups.parallelStream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toUnmodifiableList());
        } else {
            return listOfModificationGroups.stream().sorted(Comparator.comparing(element -> element.getName())).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<Modification> sortByNameDescending(Set<Modification> setOfAllModificationsWithinTheTree) {
        if (shouldUseParallel(setOfAllModificationsWithinTheTree)) {
            return setOfAllModificationsWithinTheTree.parallelStream().sorted(Comparator.comparing((Modification mod) -> mod.getName()).reversed()).collect(Collectors.toUnmodifiableList());
        } else {
            return setOfAllModificationsWithinTheTree.parallelStream().sorted(Comparator.comparing((Modification mod) -> mod.getName()).reversed()).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<ModificationGroup> sortByNameDescending(Queue<ModificationGroup> listOfModificationGroups) {
        if (shouldUseParallel(listOfModificationGroups)) {
            return listOfModificationGroups.parallelStream().sorted(Comparator.comparing((ModificationGroup mg) -> mg.getName()).reversed()).collect(Collectors.toUnmodifiableList());
        } else {
            return listOfModificationGroups.stream().sorted(Comparator.comparing((ModificationGroup mg) -> mg.getName()).reversed()).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<Modification> sortByPriorityValueAscending(Set<Modification> setOfAllElementsWithinTheTree) {
        if (shouldUseParallel(setOfAllElementsWithinTheTree)) {
            return setOfAllElementsWithinTheTree.parallelStream().sorted(Comparator.comparing(element -> element.getPriorityValue())).collect(Collectors.toUnmodifiableList());
        } else {
            return setOfAllElementsWithinTheTree.stream().sorted(Comparator.comparing(element -> element.getPriorityValue())).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<ModificationGroup> sortByPriorityValueAscending(Queue<ModificationGroup> listOfModificationGroups) {
        if (shouldUseParallel(listOfModificationGroups)) {
            return listOfModificationGroups.parallelStream().sorted(Comparator.comparing(element -> element.getPriorityValue())).collect(Collectors.toUnmodifiableList());
        } else {
            return listOfModificationGroups.stream().sorted(Comparator.comparing(element -> element.getPriorityValue())).collect(Collectors.toUnmodifiableList());
        }
    }

    public List<Modification> sortByPriorityValueDescending(Set<Modification> setOfAllElementsWithinTheTree) {
        if (shouldUseParallel(setOfAllElementsWithinTheTree)) {
            return setOfAllElementsWithinTheTree.parallelStream().sorted(Comparator.comparing((Modification mod) -> mod.getPriorityValue()).reversed()).collect(Collectors.toUnmodifiableList());
        } else {
            return setOfAllElementsWithinTheTree.stream().sorted(Comparator.comparing((Modification mod) -> mod.getPriorityValue()).reversed()).collect(Collectors.toUnmodifiableList());
        }
        }

    public List<ModificationGroup> sortByPriorityValueDescending(Queue<ModificationGroup> listOfModificationGroups) {
        if (shouldUseParallel(listOfModificationGroups)) {
            return listOfModificationGroups.parallelStream().sorted(Comparator.comparing((ModificationGroup mg) -> mg.getPriorityValue()).reversed()).collect(Collectors.toUnmodifiableList());
        } else {
            return listOfModificationGroups.stream().sorted(Comparator.comparing((ModificationGroup mg) -> mg.getPriorityValue()).reversed()).collect(Collectors.toUnmodifiableList());
        }
    }

    public boolean shouldUseParallel(Collection<?> collection) {
        final int PARALLEL_THRESHOLD = 1000;
        return collection.size() > PARALLEL_THRESHOLD;
    }
}
