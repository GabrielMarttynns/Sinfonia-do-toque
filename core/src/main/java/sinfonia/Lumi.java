package sinfonia;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Lumi, o pequeno maestro (desenhado com formas; o ShapeRenderer deve estar em modo Filled). */
public class Lumi {
    /** mood: 0 = normal, 1 = feliz, 2 = triste. */
    public static void draw(ShapeRenderer s, float cx, float cy, float k, int mood, float time) {
        cy += (float) Math.sin(time * 4) * 4 * k;
        s.setColor(0.12f, 0.12f, 0.30f, 1);                 // fraque
        s.rect(cx - 25 * k, cy - 70 * k, 50 * k, 55 * k);
        s.setColor(1, 1, 1, 1);                             // camisa
        s.triangle(cx - 8 * k, cy - 15 * k, cx + 8 * k, cy - 15 * k, cx, cy - 45 * k);
        s.setColor(1f, 0.88f, 0.70f, 1);                    // cabeça
        s.circle(cx, cy + 10 * k, 32 * k);
        s.setColor(0.08f, 0.08f, 0.08f, 1);                 // cartola
        s.rect(cx - 38 * k, cy + 36 * k, 76 * k, 8 * k);
        s.rect(cx - 24 * k, cy + 42 * k, 48 * k, 34 * k);
        s.circle(cx - 12 * k, cy + 16 * k, 4 * k);          // olhos
        s.circle(cx + 12 * k, cy + 16 * k, 4 * k);
        if (mood == 1) s.arc(cx, cy + 4 * k, 12 * k, 180, 180);       // sorriso
        else if (mood == 2) s.arc(cx, cy - 8 * k, 10 * k, 0, 180);    // tristeza
        else s.rect(cx - 8 * k, cy - 4 * k, 16 * k, 3 * k);
        float wave = (float) Math.sin(time * (mood == 1 ? 12 : 3)) * 15 * k; // batuta
        s.setColor(1f, 0.88f, 0.70f, 1);
        s.rectLine(cx + 25 * k, cy - 35 * k, cx + 55 * k, cy + 5 * k + wave, 6 * k);
        s.setColor(0.95f, 0.95f, 0.95f, 1);
        s.rectLine(cx + 55 * k, cy + 5 * k + wave, cx + 85 * k, cy + 40 * k + wave * 1.5f, 3 * k);
    }
}
