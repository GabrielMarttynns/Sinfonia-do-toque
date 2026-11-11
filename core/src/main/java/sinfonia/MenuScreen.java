package sinfonia;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;

public class MenuScreen extends ScreenAdapter {
    private final SinfoniaGame g;
    private float t;

    public MenuScreen(SinfoniaGame g) { this.g = g; }

    @Override
    public void render(float delta) {
        t += delta;
        ButtonEvent e;
        while ((e = g.serial.poll()) != null) {
            if (e.type == ButtonEvent.Type.CTRL_SHORT) { g.setScreen(new GameScreen(g, 0)); return; }
        }

        g.beginFrame(0.08f, 0.10f, 0.25f);
        g.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        Lumi.draw(g.shapes, 200, 220, 2.2f, 1, t);
        float[][] cols = {{0.9f, 0.3f, 0.3f}, {0.95f, 0.8f, 0.25f}, {0.3f, 0.6f, 0.95f}};
        for (int i = 0; i < 3; i++) {
            g.shapes.setColor(cols[i][0], cols[i][1], cols[i][2], 1);
            g.shapes.circle(450 + i * 90, 210 + (float) Math.sin(t * 3 + i) * 12, 30);
        }
        g.shapes.end();

        g.batch.begin();
        g.text("Sinfonia do Toque", 400, 440, 2.4f, Color.WHITE, true);
        g.text("Ajude Lumi a recuperar as melodias do seu mundo!", 400, 400, 1.1f, Color.LIGHT_GRAY, true);
        g.text("DÓ = indicador   RÉ = médio   MI = anelar", 400, 130, 1.2f, Color.WHITE, true);
        g.text("Aperte o botão de CONTROLE para começar", 400, 90, 1.4f, Color.YELLOW, true);
        boolean ok = g.serial.isConnected();
        String status = ok ? "Arduino conectado"
            : SinfoniaGame.ALLOW_KEYBOARD_DEBUG ? "Arduino não encontrado - modo teste: Z X C / ESPAÇO / R"
            : "Arduino nao encontrado - conecte e reinicie";
        g.text(status, 400, 30, 1.0f, ok ? Color.GREEN : Color.ORANGE, true);
        g.batch.end();
    }
}
