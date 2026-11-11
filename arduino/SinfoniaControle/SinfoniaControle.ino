// Sinfonia do Toque - controle com Arduino Uno ou Nano
// Botao D2 = DO, D3 = RE, D4 = MI, D5 = MENU
// Cada botao vai do pino ao GND (INPUT_PULLUP; nenhum resistor externo necessario).
// LEDs opcionais: D8, D9, D10 -> resistor 220 ohms -> LED -> GND.
// 9600 baud; envia N1, N2, N3, S (toque curto), R (segurar >= 1s).

const byte BUTTON_PIN[4] = {2, 3, 4, 5};
const byte LED_PIN[3] = {8, 9, 10};
const unsigned long DEBOUNCE_MS = 35;
const unsigned long LONG_PRESS_MS = 1000;
const unsigned long LED_MS = 300;

bool rawPressed[4] = {false, false, false, false};
bool stablePressed[4] = {false, false, false, false};
unsigned long rawChangedAt[4] = {0, 0, 0, 0};
unsigned long controlPressedAt = 0;
unsigned long ledOffAt[3] = {0, 0, 0};
bool commandNeedsDigit = false;

void setup() {
  Serial.begin(9600);
  for (byte i = 0; i < 4; i++) pinMode(BUTTON_PIN[i], INPUT_PULLUP);
  for (byte i = 0; i < 3; i++) {
    pinMode(LED_PIN[i], OUTPUT);
    digitalWrite(LED_PIN[i], LOW);
  }
}

void loop() {
  unsigned long now = millis();

  // Debounce: somente envia o evento quando a leitura estabilizou.
  for (byte i = 0; i < 4; i++) {
    bool current = digitalRead(BUTTON_PIN[i]) == LOW;
    if (current != rawPressed[i]) {
      rawPressed[i] = current;
      rawChangedAt[i] = now;
    }
    if ((now - rawChangedAt[i] >= DEBOUNCE_MS) &&
        (stablePressed[i] != rawPressed[i])) {
      stablePressed[i] = rawPressed[i];
      if (i < 3) {
        if (stablePressed[i]) {
          Serial.print('N');
          Serial.println(i + 1);
        }
      } else {
        if (stablePressed[i]) {
          controlPressedAt = now;
        } else {
          if (now - controlPressedAt >= LONG_PRESS_MS) Serial.println("R");
          else Serial.println("S");
        }
      }
    }
  }

  // O computador acende um LED ao acertar a nota (comando L1/L2/L3).
  while (Serial.available() > 0) {
    char c = (char) Serial.read();
    if (c == 'L') {
      commandNeedsDigit = true;
    } else if (commandNeedsDigit) {
      commandNeedsDigit = false;
      byte n = c - '0';
      if (n >= 1 && n <= 3) {
        digitalWrite(LED_PIN[n - 1], HIGH);
        ledOffAt[n - 1] = now + LED_MS;
      }
    }
  }

  for (byte i = 0; i < 3; i++) {
    if (ledOffAt[i] && (long)(now - ledOffAt[i]) >= 0) {
      digitalWrite(LED_PIN[i], LOW);
      ledOffAt[i] = 0;
    }
  }
}
