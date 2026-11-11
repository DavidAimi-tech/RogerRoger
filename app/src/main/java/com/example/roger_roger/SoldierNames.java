package com.example.roger_roger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoldierNames {

    private static final String[] NAMES = {
            "Aldric", "Brenner", "Cedric", "Dagna", "Edric", "Fenwick", "Gareth", "Hilda",
            "Ivor", "Jorund", "Kael", "Lorcan", "Mira", "Nolan", "Odalys", "Percy",
            "Quill", "Rowan", "Sable", "Tobin", "Ulric", "Vesna", "Wynn", "Xander",
            "Yara", "Zephyr", "Anselm", "Beatrix", "Corvin", "Dorian", "Elowen", "Finn",
            "Greta", "Hugo", "Isolde", "Jasper", "Kestrel", "Lucan", "Maren", "Nikolai",
            "Orrin", "Petra", "Roland", "Sigrid", "Tamsin", "Ansel", "Bram", "Cora",
            "Dmitri", "Esme", "Fyodor", "Gideon", "Helga", "Ilya", "Jorah", "Katya",
            "Leif", "Magnus", "Nadia", "Osric", "Pavel", "Rurik", "Soren", "Talia"
    };

    private final Map<Soldier, String> assigned = new IdentityHashMap<>();
    private final List<String> bag = new ArrayList<>();
    private final Random random;

    public SoldierNames(Random random) {
        this.random = random;
    }

    public String nameOf(Soldier soldier) {
        if (soldier != null && soldier.getBaseStrength() == 1) {
            return "Joe";
        }
        String name = assigned.get(soldier);
        if (name == null) {
            if (bag.isEmpty()) {
                Collections.addAll(bag, NAMES);
                Collections.shuffle(bag, random);
            }
            name = bag.remove(bag.size() - 1);
            assigned.put(soldier, name);
        }
        return name;
    }
}
