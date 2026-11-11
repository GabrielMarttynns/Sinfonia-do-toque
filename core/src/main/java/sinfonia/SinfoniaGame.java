package sinfonia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class SinfoniaGame extends Game {
    /**
     * Somente demonstração local, sem Arduino, quando habilitado por -PdebugKeyboard=true.
     * Na execução normal, o jogo é controlado exclusivamente pelos botões físicos.
     */
    public static final boolean ALLOW_KEYBOARD_DEBUG = Boolean.getBoolean("sinfonia.debugKeyboard");
    /** Use a propriedade -Dsinfonia.port=COM3 ou deixe em branco para autodetecção. */
    public static final String PORT_NAME = System.getProperty("sinfonia.port", "");

    public OrthographicCamera camera;
    public FitViewport viewport;
    public ShapeRenderer shapes;
    public SpriteBatch batch;
    public BitmapFont font;
    public SerialInput serial;
    public Sound[] notes;
    public Sound errorSound;
    private final GlyphLayout layout = new GlyphLayout();

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 480, camera);
        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        notes = ToneFactory.createNotes();
        errorSound = ToneFactory.create("erro", 130f, 0.25f, true);

        serial = new SerialInput(PORT_NAME);
        serial.start();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        if (ALLOW_KEYBOARD_DEBUG && !serial.isConnected()) debugKeys();
        super.render();
    }

    private void debugKeys() {
        long t = System.nanoTime();
        if (Gdx.input.isKeyJustPressed(Keys.Z)) serial.inject(new ButtonEvent(ButtonEvent.Type.NOTE_DO, t));
        if (Gdx.input.isKeyJustPressed(Keys.X)) serial.inject(new ButtonEvent(ButtonEvent.Type.NOTE_RE, t));
        if (Gdx.input.isKeyJustPressed(Keys.C)) serial.inject(new ButtonEvent(ButtonEvent.Type.NOTE_MI, t));
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) serial.inject(new ButtonEvent(ButtonEvent.Type.CTRL_SHORT, t));
        if (Gdx.input.isKeyJustPressed(Keys.R)) serial.inject(new ButtonEvent(ButtonEvent.Type.CTRL_LONG, t));
    }

    /** Desenha texto (chamar entre batch.begin() e batch.end()). */
    public void text(String s, float x, float y, float scale, Color c, boolean center) {
        font.getData().setScale(scale);
        font.setColor(c);
        layout.setText(font, s);
        font.draw(batch, s, center ? x - layout.width / 2 : x, y);
    }

    /** Prepara a tela (limpa, aplica viewport e matrizes de projeção). */
    public void beginFrame(float r, float g, float b) {
        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int w, int h) {
        viewport.update(w, h, true);
        super.resize(w, h);
    }

    @Override
    public void dispose() {
        // Garante que a thread pare e a porta serial seja liberada.
        serial.stopReading();
        try { serial.join(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        for (Sound s : notes) s.dispose();
        errorSound.dispose();
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
