package com.example.roger_roger;

import java.util.List;
import java.util.Random;

public class CommanderDialogue {

    // [event kind][0 = happened to the ENEMY's soldier, 1 = happened to YOURS] -> lines to pick from
    // kind order matches BattleEvent.Kind: COMBUSTION, DEATH, CAPTURE, LOYALTY
    private static final String[][][] LEONE = {
            /* COMBUSTION */ {{"Their ranks burn by The sun's flames."}, {"Even the sun scorches its own. keep composure"}},
            /* DEATH      */ {{"One less enemy beneath the sun."}, {"There is a time to live and a time to die."}},
            /* CAPTURE    */ {{"The sun welcomes you. Fight well"}, {"They dare steal from the sun? they must burn!."}},
            /* LOYALTY    */ {{"Slippery blackhead. the sun never forgets."}, {"Loyalty to the sun. your sole purpose."}}
    };

    private static final String[][][] FORSYTHE = {
            /* COMBUSTION */ {{"fire and ash. Strengthen the cause."}, {"Lost to the fire. Weak!."}},
            /* DEATH      */ {{"The moon leads another victory."}, {"May the moonshine strengthen our resolve."}},
            /* CAPTURE    */ {{"The moon shines on you. Be strong."}, {"Loyalty is paramount. The weak are not of us!."}},
            /* LOYALTY    */ {{"He lives. Surely this shall be his last night."}, {"Loyalty to the moon. only the strongest!."}}
    };

    private static final String[][][] BACCUS = {
            /* COMBUSTION */ {{"Their inner wine ran hot. Hurrah!"}, {"Ha! bubbly character, that one!"}},
            /* DEATH      */ {{"One less fork at the feast. More for us!"}, {"Wine for our brethren. Drink well."}},
            /* CAPTURE    */ {{"Our banquet gains another attendant"}, {"How Rude it is to leave a soiree with no notice!"}},
            /* LOYALTY    */ {{"We go again! Wine for the road?."}, {"Wine drinkers stay loyal! Welcome back."}}
    };

    public static String name(Clan clan) {
        switch (clan) {
            case LEONE:    return "FYODOR";
            case FORSYTHE: return "LORDE";      // give him a name when you have one
            default:       return "GIRARD";
        }
    }

    public static int portrait(Clan clan) {
        switch (clan) {
            case LEONE:    return R.drawable.fyodor_leone;
            case FORSYTHE: return R.drawable.layorde;
            default:       return R.drawable.girard;
        }
    }

    public static String opening(Clan clan) {
        switch (clan) {
            case LEONE:    return "\u201cThe sun is our divine compass, our enemies cower\u201d";
            case FORSYTHE: return "\u201c We fight with ire for Mistress Luna.\u201d";
            default:       return "\u201cRaise your cups, brothers. Let us paint the field wine-red!\u201d";
        }
    }

    // Picks the most dramatic event of the round and returns the commander's line, or null if there was none
    public static String lineFor(Clan clan, List<BattleEvent> events, Random random) {
        BattleEvent chosen = null;
        for (BattleEvent e : events) {
            if (chosen == null || rank(e.kind) < rank(chosen.kind)) chosen = e;
        }
        if (chosen == null) return null;

        String[][][] set;
        switch (clan) {
            case LEONE:    set = LEONE; break;
            case FORSYTHE: set = FORSYTHE; break;
            default:       set = BACCUS; break;
        }
        String[] options = set[chosen.kind.ordinal()][chosen.ofPlayer ? 1 : 0];
        return "\u201c" + options[random.nextInt(options.length)] + "\u201d";
    }

    private static int rank(BattleEvent.Kind kind) {
        switch (kind) {
            case COMBUSTION: return 0;      // rarest, so it wins
            case CAPTURE:    return 1;
            case DEATH:      return 2;
            default:         return 3;      // LOYALTY
        }
    }
}
