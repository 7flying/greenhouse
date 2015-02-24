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
    case 'A': case 'L':
      // Read an analog sensor, A00 - A05
      reading = analogRead(buffer[3] - 48); // See ascii table
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
  // Echo the command  
  for(uint8_t i = 0; i<4; i++)
    Serial.print(buffer[i]);
  // Mark
  Serial.print('X');
  // Data
  if(!err && !isnan(reading))
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