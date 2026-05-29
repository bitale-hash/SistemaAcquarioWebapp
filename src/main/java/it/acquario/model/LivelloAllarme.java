package it.acquario.model;

public enum LivelloAllarme {
    CRITICAL,
    WARNING,
    INFO;

    // Metodo di utilità per convertire in modo sicuro le stringhe del DB in Enum
    public static LivelloAllarme safeValueOf(String livello) {
        if (livello == null) return INFO; // Valore di fallback predefinito
        try {
            return LivelloAllarme.valueOf(livello.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return INFO; // Se sul DB c'è qualcosa di imprevisto, evita il crash
        }
    }
}