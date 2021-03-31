#include <FastLED.h>

#define NUM_LEDS 150 // Number of leds in strip
#define DATA_PIN 6 // Pin on arduino for data

CRGB leds[NUM_LEDS]; // Sets the leds up, with the number of leds

void setup() {
  FastLED.addLeds<WS2812, DATA_PIN>(leds, NUM_LEDS); // 
  Serial.begin(115200); // opens serial port, sets baud rate to 115200 bps
  Serial.println("Serial open:");
  setBlack();
}

void setBlack() {
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(0,0,0);
    FastLED.show();
  }
}

void loop() {
  
  int i, r, g, b, counter;
  i = r = g = b = counter = 0;
  while(Serial.available() > 0)
  {
    if(Serial.peek() && counter < 150){
      int i = Serial.parseInt();
      int g = Serial.parseInt();// IDK WHY BUT IT WORKS, it didnt before
      int r = Serial.parseInt();
      int b = Serial.parseInt();
      Serial.println(String(counter) + ": [" + String(i) + "] = (" + String(g) + ", " + String(r) + ", " + String(b) + ")");
      leds[i] = CRGB(r, g, b);
      counter++;
    }
    if(Serial.peek() == -1 || counter > 150) {
      FastLED.show();
      delay(15);
      Serial.println(String(counter) + " leds set, ready:");
      counter = 0;
    }
    // note, no checks for data being false (ie: i neg, or greater than NUM_LED; r,g,b being neg or greater than 255
  }
}