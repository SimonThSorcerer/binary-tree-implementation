package com.simon.simontree.model;

import com.simon.simontree.enums.TypeOfModification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ModificationTest {
    private Modification mod1, mod2, mod3, mod4, mod5, mod6, mod7, mod8, mod9, mod10,
            mod11, mod12, mod13, mod14, mod15;

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
    }

    @Test
    void resolved_correctlyRemovesModificationFromParent_returnsWithBoolean() {
        Set<Modification> listOfModifications1 = Set.of(mod1, mod2, mod3, mod4);

        ModificationGroup mg1 = new ModificationGroup(listOfModifications1, "Global modifications");

        Object[] objects1 = mg1.getModifications().toArray();
        boolean resolved = mod2.resolved();
        Object[] objects2 = mg1.getModifications().toArray();
        Assertions.assertNotEquals(objects1, objects2);
        Assertions.assertEquals(true, resolved);
    }

    @Test
    void calculateTotalModificationCost_returnsWithCorrectLong_AndCalculatedAutomaticallyByConstructor() {
        Assertions.assertEquals((mod1.getCost() * mod1.getTypeOfModification().getValue()), mod1.getTotalModificationCost());
        Assertions.assertNotEquals((mod1.getCost() * Integer.MAX_VALUE), mod1.getTotalModificationCost());
    }
}
