package com.example.roger_roger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class EnemyGenerator {

    static LinkedList<Soldier> generateEnemyRoster(int size, int budget, Random random){
        if(budget > size * BattleConfig.MAX_STRENGTH || budget < size * BattleConfig.MIN_STRENGTH){
            throw new IllegalArgumentException("Invalid Enemy allocation budget");
        }

        int[] s = new int[size];
        Arrays.fill(s, BattleConfig.MIN_STRENGTH);
        int remaining = budget - (size * BattleConfig.MIN_STRENGTH);

        while(remaining > 0) {
            int rand = random.nextInt(size);
            if (s[rand] < BattleConfig.MAX_STRENGTH) {
                s[rand]++;
                remaining--;
            }
        }

        LinkedList<Soldier> deck  = new LinkedList<>();
        for(int i = 0; i < size; i++) {
            deck.add(new Soldier(i, s[i]));
            }
        Collections.shuffle(deck, random);
        return deck;
    }
}
