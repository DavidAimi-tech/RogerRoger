package com.example.roger_roger;

public class BattleEvent {
    public enum Kind { COMBUSTION, DEATH, CAPTURE, LOYALTY }

    public final Kind kind;
    public final boolean ofPlayer;   // true = it happened to YOUR soldier

    public BattleEvent(Kind kind, boolean ofPlayer) {
        this.kind = kind;
        this.ofPlayer = ofPlayer;
    }
}
