#include "DHT.h"
#define DHT_PIN 7
#define RATE 115200

// DHT sensor
DHT dht(DHT_PIN, DHT22);
// Holds the command
char buffer[4];
// Buffer index
uint8_t buffInd = 0;

void setup(void) {
  dht.begin();
  Serial.begin(RATE);
}

void process(void) {
  float reading;
  uint8_t err = 0;
  switch(buffer[0]) {
  case 'R':
    // Read from sensor
    switch(buffer[1]) {
    case 'A': case 'L':
      // Read an analog sensor
      reading = analogRead((buffer[2] - 48) * 10 + (buffer[3] - 48));
      break;
    case 'T':
      // Requests temperature
      reading = dht.readTemperature();
      break;
    case 'H':
      // Requests humidity
      reading = dht.readHumidity();
      break;
    default:
      err = 1;
    }
  case 'W':
    // Launch actuator
    uint8_t value = 0;
    uint8_t indx = 4;
    while (buffer[indx] != 'X') {
      value = value * 10 + (buffer[indx] -48);
      indx++;
    }
    switch(buffer[1]) {
    case 'A':
      for (uint8_t i = 0; i<256) {
	analogWrite((buffer[2] - 48) * 10 + (buffer[3] - 48), value);
	delay(10);
      }
      break;
    case 'D':
      break;
    }
  }
  // Echo the command  
  for(uint8_t i = 0; i<4; i++)
    Serial.print(buffer[i]);
  // Mark
  Serial.print('X');
  // Data
  if(buffer[0] == 'R' && !err && !isnan(reading))
    Serial.print(reading);
  Serial.println();
}

void loop(void) {
  if(Serial.available()) {
    char command = Serial.read();
    if(command == 'X') {
      buffInd = 0;
      process();
    } else {
      buffer[buffInd] = command;
      buffInd++;
    }
  }
}
