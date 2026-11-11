package com.example.roger_roger;
import java.util.LinkedList;
import java.util.Random;

public class Army {
    private LinkedList<Soldier> deck;
    private final String label;

    public Army(String label, LinkedList<Soldier> Startdeck) {
        this.label = label;
        this.deck = Startdeck;
    }

    public Soldier FindfirstSoldier() {
        if (deck.isEmpty()) return null;
        return deck.getFirst();
    }

    public Soldier findNextSoldier(){
        if (deck.size() < 2) return null;
        return deck.get(1);
    }
    public Soldier getStrongestSoldier() {
        if (deck.isEmpty()) return null;
        Soldier best = deck.getFirst();
        for (Soldier soldier : deck) {
            if(soldier.getBaseStrength() >= best.getBaseStrength()){
                best = soldier;
            }
        }
        return best;
    }

    int size() {
        return deck.size();
    }

    boolean isEmpty() {
        return deck.isEmpty();
    }

    int totalStrength() {
        int total = 0;
        for (Soldier soldier : deck) {
            total += soldier.getBaseStrength();
        }
        return total;
    }

    LinkedList<Soldier> getCurrentSoldiers() {
        return deck;
    }

    public Soldier CurrentSoldier() {
        if (deck.isEmpty()) throw new IllegalStateException("Nothing here");
        return deck.removeFirst();
    }

    public void StealSoldier(Soldier Soldier) {
        deck.addLast(Soldier);
    }

    public void reshuffleArmy() {
        StealSoldier(CurrentSoldier());
    }

    public Soldier Combust(Random random) {
        if (deck.isEmpty()) throw new IllegalStateException();
        int index = random.nextInt(deck.size());
        return deck.remove(index);
    }

    public static  Army CreateArmy(String label, int[] strengths) {
        LinkedList<Soldier> deck = new LinkedList<>();
        for (int i = 0; i < strengths.length; i++) {
            deck.add(new Soldier(i, strengths[i]));
        }
        return new Army(label, deck);
    }

    public Soldier removeStrongest(){
        if (deck.isEmpty()) throw new IllegalStateException("Nothing here");
        int bestIndex = 0;
        for(int i = 0; i < deck.size(); i++){
            if (deck.get(i).getBaseStrength() >= deck.get(bestIndex).getBaseStrength()){
                bestIndex = i;
            }
        }
        return deck.remove(bestIndex);
    }
}

