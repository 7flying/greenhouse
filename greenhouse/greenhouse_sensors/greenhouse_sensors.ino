#include "DHT.h"
#define DHT_PIN 7
#define PHOCELL_PIN 0
#define RATE 115200

// DHT sensor
DHT dht(DHT_PIN, DHT22);

void setup(void) {
  dht.begin();
  Serial.begin(RATE);
}

void loop(void) {
  if(Serial.available()) {
    byte command = Serial.read();
    float response = -1.0; // default response 
    switch(command) {
      case 'T': // Requests temperature
        response = dht.readTemperature();
        break;
      case 'H': // Requests humidity
        response = dht.readHumidity();
        break;
      case 'L': // Requests light
        response = analogRead(PHOCELL_PIN);
        break;
      default:
        response = 400; 
    }
    if(isnan(response))
      response = 444; // failure
    Serial.println(response);
  }
  delay(2100); // At least 2s
}

