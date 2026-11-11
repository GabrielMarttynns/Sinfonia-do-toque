package sinfonia;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/** Gera sons (WAV 16 bits mono) por código, sem precisar de arquivos de áudio. */
public class ToneFactory {
    private static final int SR = 22050;

    public static Sound[] createNotes() {
        return new Sound[] {
            create("do", 261.63f, 0.45f, false),
            create("re", 293.66f, 0.45f, false),
            create("mi", 329.63f, 0.45f, false)
        };
    }

    public static Sound create(String name, float freq, float dur, boolean harsh) {
        int n = (int) (SR * dur);
        ByteBuffer bb = ByteBuffer.allocate(44 + n * 2).order(ByteOrder.LITTLE_ENDIAN);
        bb.put("RIFF".getBytes()).putInt(36 + n * 2).put("WAVE".getBytes());
        bb.put("fmt ".getBytes()).putInt(16).putShort((short) 1).putShort((short) 1);
        bb.putInt(SR).putInt(SR * 2).putShort((short) 2).putShort((short) 16);
        bb.put("data".getBytes()).putInt(n * 2);
        for (int i = 0; i < n; i++) {
            double t = i / (double) SR;
            double env = Math.min(1.0, t / 0.01) * Math.exp(-4.0 * t / dur);
            double w = Math.sin(2 * Math.PI * freq * t) + 0.3 * Math.sin(4 * Math.PI * freq * t);
            if (harsh) w = Math.signum(w);
            bb.putShort((short) (w * env * 0.35 * Short.MAX_VALUE));
        }
        FileHandle fh = Gdx.files.local("tones/" + name + ".wav");
        fh.writeBytes(bb.array(), false);
        return Gdx.audio.newSound(fh);
    }
}
