package sinfonia;

import java.util.List;

/** Métricas de uma fase: acertos, erros, precisão, tempo de reação, consistência e pontuação. */
public class LevelResult {
    public int level, hits, errors, score;
    public float precision;     // % de respostas corretas
    public float avgReactionMs; // tempo de reação médio
    public float consistency;   // 0-100 (100 = movimentos perfeitamente regulares)

    public static LevelResult compute(int level, int hits, int errors, List<Float> rts) {
        LevelResult r = new LevelResult();
        r.level = level; r.hits = hits; r.errors = errors;
        r.precision = (hits + errors) > 0 ? 100f * hits / (hits + errors) : 0;

        float sum = 0;
        for (float v : rts) sum += v;
        r.avgReactionMs = rts.isEmpty() ? 0 : sum / rts.size();

        float var = 0;
        for (float v : rts) var += (v - r.avgReactionMs) * (v - r.avgReactionMs);
        float sd = rts.size() > 1 ? (float) Math.sqrt(var / rts.size()) : 0;
        // Sem pelo menos duas respostas nao existe regularidade mensuravel.
        float cv = r.avgReactionMs > 0 ? sd / r.avgReactionMs : 1;
        r.consistency = rts.size() < 2 ? 0 : Math.max(0, Math.min(100, 100f * (1 - cv)));

        // Prioriza acerto e regularidade; velocidade tem bonus pequeno, sem forcar rapidez extrema.
        float speedBonus = 0;
        for (float v : rts) speedBonus += 5f * Math.max(0, Math.min(1, 1 - (v - 1000f) / 1000f));
        r.score = Math.max(0, Math.round(hits * 100 + speedBonus - errors * 25 + r.consistency * 2));
        return r;
    }
}
