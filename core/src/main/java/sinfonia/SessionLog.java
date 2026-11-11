package sinfonia;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/** Guarda o histórico em CSV para acompanhar a evolução entre sessões. */
public class SessionLog {
    private static FileHandle file() { return Gdx.files.local("sinfonia_sessions.csv"); }

    public static LevelResult lastFor(int level) {
        FileHandle f = file();
        if (!f.exists()) return null;
        String[] lines = f.readString().split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String[] p = lines[i].trim().split(",");
            if (p.length < 8 || p[0].startsWith("timestamp")) continue;
            try {
                if (Integer.parseInt(p[1]) != level) continue;
                LevelResult r = new LevelResult();
                r.level = level;
                r.hits = Integer.parseInt(p[2]);
                r.errors = Integer.parseInt(p[3]);
                r.precision = Float.parseFloat(p[4]);
                r.avgReactionMs = Float.parseFloat(p[5]);
                r.consistency = Float.parseFloat(p[6]);
                r.score = Integer.parseInt(p[7]);
                return r;
            } catch (NumberFormatException ignored) { }
        }
        return null;
    }

    public static void save(LevelResult r) {
        FileHandle f = file();
        if (!f.exists()) f.writeString("timestamp,level,hits,errors,precision,avgReactionMs,consistency,score\n", false);
        f.writeString(String.format(Locale.US, "%d,%d,%d,%d,%.1f,%.0f,%.1f,%d\n",
            System.currentTimeMillis(), r.level, r.hits, r.errors, r.precision, r.avgReactionMs, r.consistency, r.score), true);
    }
}
