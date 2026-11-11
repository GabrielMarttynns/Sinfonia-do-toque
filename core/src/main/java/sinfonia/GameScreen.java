package sinfonia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameScreen extends ScreenAdapter {
    private enum State { INTRO, PREPARE, WAIT, ROUND_END, PAUSED }

    private static final Color[] COLORS = {
        new Color(0.90f, 0.30f, 0.30f, 1), new Color(0.95f, 0.80f, 0.25f, 1), new Color(0.30f, 0.60f, 0.95f, 1)
    };
    private static final String[] NAMES = {"DÓ", "RÉ", "MI"};
    private static final String[] FINGERS = {"indicador", "médio", "anelar"};
    private static final float[][] BG = {{0.08f, 0.12f, 0.28f}, {0.17f, 0.09f, 0.27f}, {0.25f, 0.09f, 0.14f}};
    private static final float BEAT_PERIOD = 1.2f; // metrônomo da fase 3

    private final SinfoniaGame g;
    private final int levelIdx;
    private final LevelConfig cfg;
    private final Random rnd = new Random();
    private final List<Float> rts = new ArrayList<>();

    private State state = State.INTRO, stateBeforePause = State.INTRO;
    private float stateTime, beat, noteTimer, moodTimer, flashTime, anim;
    private int mood, round, idx, hits, errors;
    private int[] seq = new int[0];
    private long shownNanos, pauseStart;
    private boolean leaving;

    public GameScreen(SinfoniaGame g, int levelIdx) {
        this.g = g;
        this.levelIdx = levelIdx;
        this.cfg = LevelConfig.ALL[levelIdx];
    }

    // ---------------------------------------------------------------- lógica
    private void onEvent(ButtonEvent e) {
        switch (e.type) {
            case CTRL_LONG:  // reinicia a fase
                leaving = true;
                g.setScreen(new GameScreen(g, levelIdx));
                return;
            case CTRL_SHORT: // inicia / pausa / retoma
                if (state == State.INTRO) startRound(); else togglePause();
                return;
            default:
                if (state == State.WAIT) onNote(e.noteIndex(), e.nanos);
        }
    }

    private void togglePause() {
        if (state == State.PAUSED) {
            state = stateBeforePause;
            shownNanos += System.nanoTime() - pauseStart; // não conta o tempo pausado
        } else {
            stateBeforePause = state;
            state = State.PAUSED;
            pauseStart = System.nanoTime();
        }
    }

    private void startRound() {
        seq = new int[cfg.seqLen];
        int prev = -1;
        for (int i = 0; i < seq.length; i++) {
            int n;
            do { n = rnd.nextInt(3); } while (seq.length > 1 && n == prev); // força alternar os dedos
            seq[i] = n;
            prev = n;
        }
        state = State.PREPARE;
        stateTime = 0;
    }

    private void beginNote() {
        shownNanos = System.nanoTime();
        noteTimer = 0;
    }

    private void onNote(int n, long nanos) {
        if (n == seq[idx]) {
            rts.add(Math.max(0f, (nanos - shownNanos) / 1_000_000f));
            hits++;
            g.notes[n].play();
            g.serial.send("L" + (n + 1));
            setMood(1);
            advance();
        } else {
            errors++;
            g.errorSound.play();
            setMood(2);
            flashTime = 0.3f;
        }
    }

    private void advance() {
        idx++;
        if (idx >= seq.length) { state = State.ROUND_END; stateTime = 0; }
        else beginNote();
    }

    private void setMood(int m) { mood = m; moodTimer = 0.6f; }

    private void finish() {
        LevelResult r = LevelResult.compute(levelIdx, hits, errors, rts);
        LevelResult prev = SessionLog.lastFor(levelIdx);
        SessionLog.save(r);
        leaving = true;
        g.setScreen(new ResultScreen(g, levelIdx, r, prev));
    }

    private void update(float delta) {
        anim += delta;
        if (moodTimer > 0 && (moodTimer -= delta) <= 0) mood = 0;
        if (flashTime > 0) flashTime -= delta;
        if (state == State.PAUSED || state == State.INTRO) return;
        stateTime += delta;
        beat += delta;
        switch (state) {
            case PREPARE:
                if (stateTime >= 1f) { state = State.WAIT; idx = 0; beginNote(); }
                break;
            case WAIT:
                noteTimer += delta;
                if (noteTimer >= cfg.timeLimit) { // tempo esgotado conta como erro
                    errors++;
                    g.errorSound.play();
                    setMood(2);
                    advance();
                }
                break;
            case ROUND_END:
                if (stateTime >= 0.8f) {
                    round++;
                    if (round >= cfg.rounds) finish(); else startRound();
                }
                break;
            default:
        }
    }

    // ---------------------------------------------------------------- desenho
    @Override
    public void render(float delta) {
        ButtonEvent e;
        while (!leaving && (e = g.serial.poll()) != null) onEvent(e);
        if (leaving) return;
        update(delta);
        if (leaving) return;
        draw();
    }

    private void draw() {
        float[] bg = BG[levelIdx];
        g.beginFrame(bg[0], bg[1], bg[2]);
        boolean overlay = state == State.PAUSED || state == State.INTRO;
        int n = seq.length;
        float box = 90, gap = 15, total = n * box + (n - 1) * gap, startX = 200 + (580 - total) / 2, by = 230;

        g.shapes.begin(ShapeType.Filled);
        Lumi.draw(g.shapes, 110, 200, 1.4f, mood, anim);

        if (state != State.INTRO) {
            for (int i = 0; i < n; i++) {
                float bx = startX + i * (box + gap);
                boolean done = state == State.ROUND_END || (state == State.WAIT && i < idx);
                boolean cur = state == State.WAIT && i == idx;
                Color c = COLORS[seq[i]];
                if (cur) {
                    if (flashTime > 0) g.shapes.setColor(1, 0.1f, 0.1f, 1); else g.shapes.setColor(1, 1, 1, 1);
                    g.shapes.rect(bx - 5, by - 5, box + 10, box + 10);
                }
                g.shapes.setColor(c.r, c.g, c.b, cur ? 1f : done ? 0.25f : 0.55f);
                g.shapes.rect(bx, by, box, box);
            }
        }
        if (state == State.WAIT) { // barra de tempo
            float frac = Math.max(0, 1 - noteTimer / cfg.timeLimit);
            g.shapes.setColor(0.2f, 0.2f, 0.25f, 1);
            g.shapes.rect(200, 150, 580, 10);
            g.shapes.setColor(1 - frac, frac, 0.2f, 1);
            g.shapes.rect(200, 150, 580 * frac, 10);
        }
        if (levelIdx == 2 && !overlay) { // metrônomo (apenas guia visual)
            float phase = (beat % BEAT_PERIOD) / BEAT_PERIOD;
            g.shapes.setColor(1, 1, 1, 0.9f);
            g.shapes.circle(490, 85, 14 + 12 * Math.max(0, 1 - phase * 3));
        }
        if (overlay) {
            g.shapes.setColor(0, 0, 0, 0.75f);
            g.shapes.rect(0, 0, 800, 480);
        }
        g.shapes.end();

        g.batch.begin();
        g.text(cfg.name, 15, 465, 1.2f, Color.WHITE, false);
        if (state != State.INTRO) {
            g.text("Rodada " + Math.min(round + 1, cfg.rounds) + "/" + cfg.rounds, 785, 465, 1.2f, Color.WHITE, false);
            g.text("Acertos: " + hits + "   Erros: " + errors, 15, 435, 1.1f, Color.LIGHT_GRAY, false);
            if (!overlay) {
                for (int i = 0; i < n; i++) {
                    float cx = startX + i * (box + gap) + box / 2;
                    g.text(NAMES[seq[i]], cx, by + box / 2 + 12, 1.8f, Color.BLACK, true);
                    if (state == State.WAIT && i == idx)
                        g.text(FINGERS[seq[i]], cx, by - 15, 1.1f, Color.WHITE, true);
                }
                if (state == State.PREPARE) g.text("Prepare-se...", 490, 360, 1.5f, Color.YELLOW, true);
                if (levelIdx == 2) g.text("Siga o ritmo", 540, 90, 1.0f, Color.LIGHT_GRAY, false);
                g.text("Controle: toque = pausar | segurar = reiniciar fase", 400, 25, 0.9f, Color.GRAY, true);
            }
        }
        if (state == State.INTRO) {
            g.text(cfg.name, 400, 300, 1.6f, Color.WHITE, true);
            g.text(cfg.desc, 400, 250, 1.1f, Color.LIGHT_GRAY, true);
            g.text("Aperte o botão de CONTROLE para começar", 400, 180, 1.3f, Color.YELLOW, true);
        }
        if (state == State.PAUSED) {
            g.text("PAUSADO", 400, 280, 2.4f, Color.WHITE, true);
            g.text("Toque no botão de controle para continuar", 400, 220, 1.2f, Color.YELLOW, true);
            g.text("Segure para reiniciar a fase", 400, 190, 1.1f, Color.LIGHT_GRAY, true);
        }
        g.batch.end();
    }
}
