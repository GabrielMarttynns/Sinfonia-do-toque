package sinfonia;

public class LevelConfig {
    public final String name, desc;
    public final int seqLen, rounds;
    public final float timeLimit; // segundos por nota

    public LevelConfig(String name, String desc, int seqLen, int rounds, float timeLimit) {
        this.name = name; this.desc = desc; this.seqLen = seqLen; this.rounds = rounds; this.timeLimit = timeLimit;
    }

    public static final LevelConfig[] ALL = {
        new LevelConfig("Fase 1 - Primeiras Notas (Fácil)",
            "Uma nota por vez, com bastante tempo. Use o dedo indicado.", 1, 8, 6f),
        new LevelConfig("Fase 2 - Ritmo da Harmonia (Intermediária)",
            "Repita pequenas sequências alternando os dedos.", 3, 6, 4f),
        new LevelConfig("Fase 3 - A Grande Melodia (Avançada)",
            "Sequências longas. Acompanhe o ritmo do metrônomo.", 5, 6, 3f)
    };
}
