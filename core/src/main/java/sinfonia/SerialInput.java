package sinfonia;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Thread de comunicação serial com o Arduino (jSerialComm).
 * Protocolo (9600 baud, uma mensagem por linha):
 *   Arduino -> jogo: "N1" (DÓ), "N2" (RÉ), "N3" (MI), "S" (controle curto), "R" (controle longo)
 *   jogo -> Arduino: "L1", "L2", "L3" (acende o LED da nota por um instante)
 */
public class SerialInput extends Thread {
    private final Queue<ButtonEvent> queue = new ConcurrentLinkedQueue<>();
    private final String portName;
    private volatile boolean running = true;
    private volatile boolean connected = false;
    private SerialPort port;

    public SerialInput(String portName) {
        super("serial-reader");
        setDaemon(true);
        this.portName = portName;
    }

    @Override
    public void run() {
        port = findPort();
        if (port == null) {
            System.err.println("[Serial] Nenhuma porta do Arduino encontrada.");
            return;
        }
        port.setBaudRate(9600);
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        if (!port.openPort()) {
            System.err.println("[Serial] Não foi possível abrir " + port.getSystemPortName());
            return;
        }
        connected = true;
        System.out.println("[Serial] Conectado em " + port.getSystemPortName());

        StringBuilder line = new StringBuilder();
        byte[] buf = new byte[64];
        while (running) {
            int avail = port.bytesAvailable();
            if (avail < 0) break; // dispositivo desconectado
            if (avail == 0) {
                try { Thread.sleep(2); } catch (InterruptedException e) { break; }
                continue;
            }
            int n = port.readBytes(buf, Math.min(avail, buf.length));
            if (n < 0) break;
            long now = System.nanoTime();
            for (int i = 0; i < n; i++) {
                char c = (char) buf[i];
                if (c == '\n' || c == '\r') {
                    if (line.length() > 0) {
                        handle(line.toString().trim(), now);
                        line.setLength(0);
                    }
                } else if (line.length() < 64) {
                    line.append(c);
                } else {
                    line.setLength(0); // ignora mensagens inesperadamente longas
                }
            }
        }
        connected = false;
        port.closePort();
    }

    private SerialPort findPort() {
        if (portName != null && !portName.isEmpty()) return SerialPort.getCommPort(portName);
        for (SerialPort p : SerialPort.getCommPorts()) {
            String d = (p.getDescriptivePortName() + " " + p.getSystemPortName()).toLowerCase();
            if (d.contains("arduino") || d.contains("ch340") || d.contains("usb") || d.contains("acm")) return p;
        }
        return null;
    }

    private void handle(String msg, long now) {
        ButtonEvent.Type t;
        switch (msg) {
            case "N1": t = ButtonEvent.Type.NOTE_DO; break;
            case "N2": t = ButtonEvent.Type.NOTE_RE; break;
            case "N3": t = ButtonEvent.Type.NOTE_MI; break;
            case "S":  t = ButtonEvent.Type.CTRL_SHORT; break;
            case "R":  t = ButtonEvent.Type.CTRL_LONG; break;
            default: return;
        }
        queue.add(new ButtonEvent(t, now));
    }

    public ButtonEvent poll() { return queue.poll(); }

    /** Usado apenas pelo modo de teste por teclado. */
    public void inject(ButtonEvent e) { queue.add(e); }

    public boolean isConnected() { return connected; }

    public void send(String msg) {
        if (connected && port != null) {
            byte[] b = (msg + "\n").getBytes();
            port.writeBytes(b, b.length);
        }
    }

    /** Para a thread e libera a porta serial. */
    public void stopReading() {
        running = false;
        interrupt();
    }
}
