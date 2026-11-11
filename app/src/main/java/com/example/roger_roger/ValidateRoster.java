package com.example.roger_roger;
public class ValidateRoster{

    boolean valid;
    String message;

    static ValidationResult validate(int[] strengths, int expectedSize, int budget){
        if(strengths.length != expectedSize) {
            return new ValidationResult(false, "Amount does not reflect allocated size");
        }

        for (int strength : strengths) {
            if (strength < BattleConfig.MIN_STRENGTH || strength > BattleConfig.MAX_STRENGTH) {
                return new ValidationResult(false, "Strength out of range");
            }
        }

        int totalStrength = 0;

        for(int strength : strengths){
            totalStrength += strength;
        }

        if(totalStrength != budget){
            return new ValidationResult(false, "Strength has not been allocated amongst the soldiers properly");
        }

        return new ValidationResult(true, "OK");
    }
}

class ValidationResult{
    public final boolean valid;
    public final String message;
    public ValidationResult(boolean valid, String message){
        this.valid = valid;
        this.message = message;
    }
}