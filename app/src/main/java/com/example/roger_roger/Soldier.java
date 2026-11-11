package com.example.roger_roger;
public final class Soldier {
    
    private final int id;
    private final int baseStrength;

    public Soldier(int id, int baseStrength) {
        if (baseStrength <BattleConfig.MIN_STRENGTH || baseStrength > BattleConfig.MAX_STRENGTH){
            throw new IllegalArgumentException("Invalid base strength: " + baseStrength);
        }
        this.id = id;
        this.baseStrength = baseStrength;
    }

    public int getId() {
        return id;
    }
    public int getBaseStrength() {
        return baseStrength;
    }

    @Override
    public String toString() {
        return "Soldier{" + "id=" + id + ", baseStrength=" + baseStrength + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return  id == ((Soldier) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
