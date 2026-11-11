package sinfonia;

/** Evento vindo dos botões físicos do Arduino. */
public class ButtonEvent {
    public enum Type { NOTE_DO, NOTE_RE, NOTE_MI, CTRL_SHORT, CTRL_LONG }

    public final Type type;
    public final long nanos; // instante (System.nanoTime) em que a linha chegou na porta serial

    public ButtonEvent(Type type, long nanos) {
        this.type = type;
        this.nanos = nanos;
    }

    /** 0 = DÓ, 1 = RÉ, 2 = MI, -1 = botão de controle. */
    public int noteIndex() {
        switch (type) {
            case NOTE_DO: return 0;
            case NOTE_RE: return 1;
            case NOTE_MI: return 2;
            default: return -1;
        }
    }
}
