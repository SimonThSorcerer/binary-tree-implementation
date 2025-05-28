package com.simon.simontree;

import com.simon.simontree.api.ModificationBinaryTreeOperations;
import com.simon.simontree.enums.TreeLogic;
import com.simon.simontree.enums.TypeOfModification;
import com.simon.simontree.model.*;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("The following demonstrate the requested functionalities, features:");
        System.out.println();
        System.out.println("Task to be implemented:" +
                " \"A konkrét módosításokat leíró elemek rendelkeznek egy egyedi azonosítóval, névvel, költség értékkel és egy prioritás értékkel\".");

        Modification mod1 = new Modification("Test1", 10, 2324, TypeOfModification.ADD);
        System.out.println("mod1.getModificationId() = " + mod1.getModificationId());
        System.out.println("mod1.getName() = " + mod1.getName());
        System.out.println("mod1.getCost() = " + mod1.getCost());
        System.out.println("mod1.getPriorityValue() =" + mod1.getPriorityValue());
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"Az elemek tárolnak egy módosítási típust is, melyek a következők lehetnek: add, remove, modify. Minden típushoz tartozik egy fix költség szorzó is, melyek rendre a következőek: 1, -1, 3.\".");
        System.out.println();
        System.out.println("mod1.getTypeOfModification().getValue() = " + mod1.getTypeOfModification() + " " + mod1.getTypeOfModification().getValue());
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"Ha egy konkrét módosítás költségét kérdezzük, akkor az eredmény a \"tárolt költség érték\" * \"típus költség szorzója\".");
        System.out.println();
        System.out.println("tárolt költség érték  * típus költség szorzója = " + mod1.getTotalModificationCost());
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A konkrét módosításokat több szintű módosítás csoportokba lehet rendezni - ezek építik fel a fa struktúrát. Egy módosítás csoport ugyanolyan tulajdonságokkal rendelkezik," +
                " mint maga a konkrét módosítás, annyi különbséggel, hogy nem tartozik hozzá módosítási típus és fix költség sem.\".");
        System.out.println();
        ModificationBinaryTree modificationBinaryTree = new ModificationBinaryTree(TreeLogic.NAME);

        Modification mod2 = new Modification("Something", 20, 33253, TypeOfModification.MODIFY);
        Modification mod3 = new Modification("Hiring", 30, 1, TypeOfModification.REMOVE);
        Modification mod4 = new Modification("Firing", 10, 67, TypeOfModification.ADD);
        Modification mod5 = new Modification("Bonuses", 20, -123, TypeOfModification.MODIFY);
        Modification mod6 = new Modification("Hardware", 30, 33333333, TypeOfModification.REMOVE);
        Modification mod7 = new Modification("Marketing", 11, 0, TypeOfModification.ADD);
        Modification mod8 = new Modification("OverTime", 22, 1, TypeOfModification.MODIFY);
        Modification mod9 = new Modification("Test9", 33, 5, TypeOfModification.REMOVE);
        Modification mod10 = new Modification("Investment", 15, 34, TypeOfModification.ADD);
        Modification mod11 = new Modification("Insurance", 25, 23, TypeOfModification.MODIFY);
        Modification mod12 = new Modification("Vis Major", 37, 23, TypeOfModification.REMOVE);
        Modification mod13 = new Modification("Extra", 3432, 1, TypeOfModification.ADD);
        Modification mod14 = new Modification("Surprise", 7, 33, TypeOfModification.MODIFY);
        Modification mod15 = new Modification("Name", 3, 87, TypeOfModification.REMOVE);

        Set<Modification> listOfModifications1 = Set.of(mod1, mod2, mod3, mod4);
        Set<Modification> listOfModifications2 = Set.of(mod5, mod6, mod7, mod8);
        Set<Modification> listOfModifications3 = Set.of(mod9, mod10, mod11, mod12);
        Set<Modification> listOfModifications4 = Set.of(mod13, mod14, mod15);
        Set<Modification> listOfModifications5 = Set.of(mod5, mod6, mod7, mod8);
        Set<Modification> listOfModifications6 = Set.of(mod13, mod14, mod15);

        ModificationGroup mg1 = new ModificationGroup(listOfModifications1, "Global modifications");
        ModificationGroup mg2 = new ModificationGroup(listOfModifications2, "Europe");
        ModificationGroup mg3 = new ModificationGroup(listOfModifications3, "District I.");
        ModificationGroup mg4 = new ModificationGroup(listOfModifications4, "Hungary");
        ModificationGroup mg5 = new ModificationGroup(listOfModifications5, "Budapest");
        ModificationGroup mg6 = new ModificationGroup(listOfModifications6, "Office");
        ModificationGroup mg7 = new ModificationGroup(Collections.singleton(mod15), "Test");

        List<ModificationGroup> listOfModificationGroups = List.of(mg1, mg2, mg3, mg4, mg5, mg6, mg7);

        ModificationBinaryTreeOperations modificationBinaryTreeOperations = new ModificationBinaryTreeOperations(modificationBinaryTree);

        for (ModificationGroup m : listOfModificationGroups) {
            modificationBinaryTreeOperations.insert(m);
        }
        System.out.println("Tree structure:");
        modificationBinaryTreeOperations.printTreeStructure();
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa bármely szintjén lévő módosítási csoport költsége lekérdezhető az alatta elhelyezkedő struktúrában található módosítások költségének összegeként.\".");
        System.out.println();
        System.out.println("mg6.getCost() = " + mg6.getCost());
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa bármely szintjén lévő elemről lekérdezhető legyen a fa struktúrabéli pozíciója szöveges formában a nevek felhasználásával, elválasztással az alábbi formában: " +
                "például \"Budapest modifications\" eredménye: \"Global modifications / Europe / Hungary / Budapest modifications\".");
        System.out.println();
        String path = modificationBinaryTreeOperations.getTreePath(mg5);
        System.out.println(path);
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa bármely szintjén lévő módosítási csoporttól lekérhető legyen a prioritási értékek alapján legnagyobb prioritású konkrét módosítás\".");
        System.out.println();
        try {
            Modification biggestPriorityMod = modificationBinaryTreeOperations.findBiggestPriorityValueModificationOnAGivenLevel(1);
            System.out.println("Biggest priority mod: " + biggestPriorityMod.getName());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid level: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.err.println("No modifications found: " + e.getMessage());
        }

        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa elemei rendezhetőek legyenek prioritás szerint és név szerint is növekvő sorrendbe\".");
        System.out.println("I have implemented this in multiple ways:");
        System.out.println("1., You can set the logic of the binary tree, during creation, then traverse the nodes in order. This will give you an alphabetical order (TreeLogic.NAME is being used here)");
        modificationBinaryTreeOperations.inOrder();
        System.out.println();
        modificationBinaryTreeOperations.reverseOrder();
        System.out.println("This works, but duplicates will cause issues, that is the limitation of binary trees.");
        System.out.println();
        System.out.println("2., You can just directly order the elements via the included helper class:");
        List<ModificationGroup> modificationGroupsNameAscending = modificationBinaryTreeOperations.sortModificationGroupByNameAscending();
        List<ModificationGroup> modificationGroupsNameDescending = modificationBinaryTreeOperations.sortModificationGroupByNameDescending();

        for (ModificationGroup mg : modificationGroupsNameAscending) {
            System.out.print(mg.getName() + " ");
        }
        System.out.println();
        for (ModificationGroup mg : modificationGroupsNameDescending) {
            System.out.print(mg.getName() + " ");
        }
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa megfelelő elemei rendelkezzenek alapvető szerkesztési műveletekkel, mint add, remove. A konkrét módosításoknak pedig legyen egy \"resolved\" metódusa, ami nem tesz egyebet, mint eltávolítja a konkrét módosítást az őt tartalmazó módosítási csoportból\".");
        modificationBinaryTreeOperations.printTreeStructure();
        System.out.println("Removing ModificationGroup Europe:");
        modificationBinaryTreeOperations.removeModificationGroup(mg2);
        modificationBinaryTreeOperations.printTreeStructure();
        System.out.println("Adding ModificationGroup Europe back and adding ModificationGroup THIS:");
        modificationBinaryTreeOperations.insert(mg2);
        modificationBinaryTreeOperations.printTreeStructure();
        System.out.println("It adds it back at a new position correctly.");
        Modification mod16 = new Modification("Alpha", 1, 1, TypeOfModification.REMOVE);
        Modification mod17 = new Modification("Omega", 2, 2, TypeOfModification.ADD); //1
        Modification mod18 = new Modification("Beta", 3, 3, TypeOfModification.MODIFY); //99
        Modification mod19 = new Modification("Theta", 4, 4, TypeOfModification.REMOVE); //-87

        Set<Modification> listOfModifications8 = Set.of(mod16, mod17, mod18, mod19);
        ModificationGroup mg8 = new ModificationGroup(listOfModifications8, "THIS");
        modificationBinaryTreeOperations.insert(mg8);
        System.out.println();

        System.out.println("Removing a Modification, true if it is successful, throws NullPointerException if already removed: ");
        mod3.setParent(mg1);
        try {
            System.out.println(mod3.resolved());
        } catch (NullPointerException e) {
            System.out.println("NullPointer Exception");
        }
        System.out.println("Removing a Modification, which was removed already: ");
        try {
            System.out.println(mod3.resolved());
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            System.out.println("NullPointer Exception was thrown, but was successfully caught.");
        }
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A fa bármely szintjén lehessen keresni az egyedi azonosító alapján, mely visszaadja a keresett azonosítóval rendelkező módosítást, ha az megtalálható az adott szint alatt, ahol a keresést indítottuk. " +
                "Amennyiben nem található a keresett azonosító, akkor az ilyen keresési hibára legyen felkészülve az API, hogy az azt használó programozó majd kezelni tudja\".");
        System.out.println();
        System.out.println("Retrieving ID = " + mod1.getModificationId() + " on level 1");
        try {
            Modification modificationOnGivenLevelById = modificationBinaryTreeOperations.findModificationOnGivenLevelById(mod1.getModificationId(), 1);
            System.out.println("modificationOnGivenLevelById.getName() =" + modificationOnGivenLevelById.getName() + " " + "modificationOnGivenLevelById.getModificationId() =" + modificationOnGivenLevelById.getModificationId());
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException caught");
        } catch (NoSuchElementException e) {
            System.out.println("No Modification with ID = " + mod1.getModificationId() + " exist on level " + 1 + ". Try a different level.");
        }
        System.out.println("But this Modification exist:");
        try {
            Modification modificationOnGivenLevelById = modificationBinaryTreeOperations.findModificationOnGivenLevelById(mod1.getModificationId(), mod1.getLevel());
            System.out.println("modificationOnGivenLevelById.getName() = " + modificationOnGivenLevelById.getName());
            System.out.println("modificationOnGivenLevelById.getModificationId() = " + modificationOnGivenLevelById.getModificationId());
            System.out.println("modificationOnGivenLevelById.getLevel() = " + modificationOnGivenLevelById.getLevel());
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException caught");
        } catch (NoSuchElementException e) {
            System.out.println("NoSuchElementException caught");
        }
        System.out.println();
        System.out.println();
        System.out.println("Task to be implemented:" +
                "\"A library-t használó projekteknek legyen lehetősége a funkcionalitás kibővítésére azáltal, hogy akár a \"konkrét módosítást\", " +
                "akár a \"módosítási csoportot\" valamilyen specializált alosztállyal helyettesíteni lehessen a library módosítása nélkül is.\".");
        Modification testModification1 = new Modification("Test1", 12, 10, TypeOfModification.ADD);
        System.out.println("Regular Modification values : " + testModification1.getTotalModificationCost());
        DiscountedModification discountedModificationTest1 = new DiscountedModification("DiscountTest1", 12, 10, TypeOfModification.ADD);
        System.out.println("DiscountedModification values : " + discountedModificationTest1.getTotalModificationCost());
        System.out.println("It is lower by 10% as it should be.");
        System.out.println();
        ModificationGroup testModificationGroup1 = new ModificationGroup(Set.of(testModification1), "testModificationGroup1");
        System.out.println("Regular ModificationGroup values : " + testModificationGroup1.calculateTotalCostOfEveryModificationUnderThisGroup());
        DiscountedModificationGroup discountedModificationGroupTest1 = new DiscountedModificationGroup(Set.of(testModification1), "discountedModificationGroupTest1");
        System.out.println("DiscountedModification values : " + discountedModificationGroupTest1.calculateTotalCostOfEveryModificationUnderThisGroup());
        System.out.println("It is lower by 10% as it should be.");
    }
}
