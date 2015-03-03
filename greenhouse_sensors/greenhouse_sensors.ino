#include "DHT.h"
#define DHT_PIN 7
#define RATE 115200

// DHT sensor
DHT dht(DHT_PIN, DHT22);
// Holds the command
char buffer[7];
// Buffer index
uint8_t buffInd = 0;

void setup(void) {
  dht.begin();
  Serial.begin(RATE);
}

void process(void) {
  float reading;
  uint8_t err = 0;
  // [0] - R|W
  switch(buffer[0]) {
  case 'R':
    // Read from sensor 
    // [1] - Type
    // [2, 3] - Pin
    switch(buffer[1]) {
    case 'T':
      // Requests temperature
      reading = dht.readTemperature();
      break;
    case 'H':
      // Requests humidity
      reading = dht.readHumidity();
      break;
    case 'L':
      // Fall through, 'light sensor' is a photoresistor
    case 'A':
      // Read a general analog sensor
      reading = analogRead((buffer[2] - 48) * 10 + (buffer[3] - 48));
      break;
    default:
      err = 1;
    }
  case 'W':
    // Launch actuator
    // [1] - Type
    // [2 - 3] - Pin
    // [4 - 6] - Value (max 255)
    uint8_t value = 0;
    uint8_t indx = 4;
    while (buffer[indx] != 'X') {
      value = value * 10 + (buffer[indx] - 48);
      indx++;
    }
    switch(buffer[1]) {
    case 'A':
      // Launch an analog actuator
      if (value < 256) { // TODO: Check negative ?? -> reverse??
	for (uint8_t i = 0; i < 1000; i+=100) {
	  analogWrite((buffer[2] - 48) * 10 + (buffer[3] - 48), value);
	  delay(10);
	}
      } else
	err = 1;
      break;
    case 'D':
      // Launch a digital actuator. Possible values 1 or 0
      if (value != 1 || value != 0)
	err = 1;
      else
	digitalWrite((buffer[2] - 48) * 10 + (buffer[3] - 48), value);
      break;
    default:
      err = 1;
    }
  }
  // Echo the command  
  for (uint8_t i = 0; i<7; i++)
    Serial.print(buffer[i]);
  // Mark
  Serial.print('X');
  if (err | (buffer[0] == 'R' && isnan(reading)))
    Serial.print("ERROR");
  else
     Serial.print(reading);
  Serial.println();
}

void loop(void) {
  if (Serial.available()) {
    char command = Serial.read();
    if (command == 'X') {
      buffInd = 0;
      process();
    } else {
      if (buffInd < 7) {
	buffer[buffInd] = command;
	buffInd++;
      } else {
	// generate error
	buffInd = 0;
	for (uint8_t i = 0; i<7; i++)
	  Serial.print(buffer[i]);
	Serial.println("XERROR");
      }
    }
  }
}
