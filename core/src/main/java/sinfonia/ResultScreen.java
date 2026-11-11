package sinfonia;

import java.util.Locale;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ResultScreen extends ScreenAdapter {
    private final SinfoniaGame g;
    private final int levelIdx;
    private final LevelResult r, prev;
    private float t;

    public ResultScreen(SinfoniaGame g, int levelIdx, LevelResult r, LevelResult prev) {
        this.g = g; this.levelIdx = levelIdx; this.r = r; this.prev = prev;
    }

    @Override
    public void render(float delta) {
        t += delta;
        ButtonEvent e;
        while ((e = g.serial.poll()) != null) {
            if (e.type == ButtonEvent.Type.CTRL_SHORT) {
                if (levelIdx + 1 < LevelConfig.ALL.length) g.setScreen(new GameScreen(g, levelIdx + 1));
                else g.setScreen(new MenuScreen(g));
                return;
            }
            if (e.type == ButtonEvent.Type.CTRL_LONG) { g.setScreen(new MenuScreen(g)); return; }
        }

        g.beginFrame(0.08f, 0.14f, 0.22f);
        g.shapes.begin(ShapeType.Filled);
        Lumi.draw(g.shapes, 130, 220, 1.8f, r.precision >= 70 ? 1 : 0, t);
        g.shapes.end();

        g.batch.begin();
        g.text("Fase concluída!", 480, 440, 2.0f, Color.WHITE, true);
        g.text(LevelConfig.ALL[levelIdx].name, 480, 405, 1.0f, Color.LIGHT_GRAY, true);
        g.text("Pontuação: " + r.score, 480, 360, 1.8f, Color.YELLOW, true);
        g.text("Acertos: " + r.hits + "   Erros: " + r.errors, 300, 310, 1.2f, Color.WHITE, false);
        g.text(String.format(Locale.US, "Precisão: %.0f%%", r.precision), 300, 280, 1.2f, Color.WHITE, false);
        g.text(String.format(Locale.US, "Tempo de reação médio: %.0f ms", r.avgReactionMs), 300, 250, 1.2f, Color.WHITE, false);
        g.text(String.format(Locale.US, "Consistência: %.0f%%", r.consistency), 300, 220, 1.2f, Color.WHITE, false);
        if (prev != null) {
            g.text(String.format(Locale.US, "Evolução vs. sessão anterior: precisão %+.1f%% | reação %+.0f ms | consistência %+.1f%%",
                r.precision - prev.precision, r.avgReactionMs - prev.avgReactionMs, r.consistency - prev.consistency),
                480, 160, 1.0f, Color.CYAN, true);
        } else {
            g.text("Primeira sessão nesta fase: resultados salvos para comparação futura.", 480, 160, 1.0f, Color.CYAN, true);
        }
        boolean last = levelIdx + 1 >= LevelConfig.ALL.length;
        g.text(last ? "Toque no controle: voltar ao menu" : "Toque no controle: próxima fase | segure: menu",
            480, 80, 1.2f, Color.YELLOW, true);
        g.batch.end();
    }
}
