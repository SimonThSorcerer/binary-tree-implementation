package com.simon.simontree.model;

import com.simon.simontree.enums.TreeLogic;
import com.simon.simontree.enums.TypeOfModification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ModificationGroupTest {
    @Mock
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
    }

    @Test
    void removeModificationGroup_InCaseOfNull_ThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> mg1.removeChildModificationGroup(null));
        mg1.addChildModificationGroup(mg2);
        Assertions.assertDoesNotThrow(() -> mg1.removeChildModificationGroup(mg2));
    }

    @Test
    void addModificationGroup_InCaseOfNull_ThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> mg1.addChildModificationGroup(null));
        mg1.addChildModificationGroup(mg2);
        List<ModificationGroup> childModificationGroups = mg1.getChildModificationGroups();
        Assertions.assertDoesNotThrow(() -> mg1.addChildModificationGroup(childModificationGroups.get(0)));
    }


    @Test
    void removeModification_InCaseOfNull_ThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> mg1.removeModification(null));
    }

    @Test
    void addModifications_InCaseOfNull_ThrowsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> mg1.addModifications(null));
    }


    @Test
    void addModifications_CorrectlyRemovesModification_SetsParentOfModificationTuNull() {
        mod7.setParent(null);
        ModificationGroup parentModificationGroupBefore = mod7.getParent();
        mg1.addModifications(Collections.singleton(mod7));
        ModificationGroup parentModificationGroupAfter = mod7.getParent();

        Assertions.assertNotEquals(parentModificationGroupBefore, parentModificationGroupAfter);
        Assertions.assertNotEquals(null, parentModificationGroupAfter);
    }

    @Test
    void removeModification_CorrectlyRemovesModification_SetsParentOfModificationTuNull() {
        ModificationGroup parentModificationGroupBefore = mod1.getParent();
        mg1.removeModification(mod1);
        ModificationGroup parentModificationGroup2After = mod1.getParent();

        Assertions.assertNotEquals(parentModificationGroupBefore, parentModificationGroup2After);
        Assertions.assertEquals(null, parentModificationGroup2After);
    }

    @Test
    void updateCalculatedValues_CorrectlyUpdates_AfterRunningRemoveModifications() {
        Long costPrior = mg1.getCost();
        mg1.removeModification(mod1);
        Long costAfter = mg1.getCost();

        Assertions.assertNotEquals(costPrior, costAfter);
    }

    @Test
    void updateCalculatedValues_CorrectlyUpdates_AfterRunningAddModifications() {
        Long costPrior = mg1.getCost();
        mg1.addModifications(listOfModifications2);
        Long costAfter = mg1.getCost();

        Assertions.assertNotEquals(costPrior, costAfter);
    }


    @Test
    void calculateTotalCostOfEveryModificationUnderThisGroup_returnsWithCorrectInt_CalculatesItAutomatically() {
        Assertions.assertEquals(mg1.getCost(), (mod1.getTotalModificationCost() + mod2.getTotalModificationCost() + mod3.getTotalModificationCost() + mod4.getTotalModificationCost()));
    }

    @Test
    void calculateTotalPriorityValueOfEveryModificationUnderThisGroup_returnsWithCorrectInt_CalculatesItAutomatically() {
        Assertions.assertEquals(mg1.getPriorityValue(), (mod1.getPriorityValue() + mod2.getPriorityValue() + mod3.getPriorityValue() + mod4.getPriorityValue()));
    }

    @Test
    void setLevel_shouldNotChangeIfCurrentLevelEqualsNewLevel_ReturnsBoolean() {
        int level = mg1.getLevel();
        boolean b = mg1.setLevel(level);

        assertEquals(false, b);
    }

    @Test
    void setLevel_shouldCorrectlyIncrementChildrensLevelAsWell() {
        mg1.setLevel(-1);
        mg2.setLevel(-1);
        mg3.setLevel(-1);
        mg4.setLevel(-1);

        mg1.addChildModificationGroup(mg2);
        mg1.addChildModificationGroup(mg3);
        mg1.addChildModificationGroup(mg4);

        assertEquals(-1, mg1.getLevel());
        assertEquals(0, mg2.getLevel());
        assertEquals(0, mg3.getLevel());
        assertEquals(0, mg4.getLevel());

        mg1.setLevel(5);

        assertEquals(5, mg1.getLevel());
        assertEquals(6, mg2.getLevel());
        assertEquals(6, mg3.getLevel());
        assertEquals(6, mg4.getLevel());
    }

}
