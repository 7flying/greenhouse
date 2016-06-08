#include "DHT.h"
#define DHT_PIN 7
#define RATE 115200
#define DEBUG 0

// DHT sensor
DHT dht(DHT_PIN, DHT22);
// Holds the command
char buffer[7];
// Buffer index
uint8_t buffInd = 0;

void setup(void) {
  dht.begin();
  Serial.begin(RATE);
  for (uint8_t i = 2; i < 14; i++)
    pinMode(i, OUTPUT);
}

void process(void) {
  float reading;
  uint8_t err = 0;
  // [0] - R|W
  switch(buffer[0]) {
  case 'R':
    // Read from sensor 
    // [1] - Type: A|D
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
    case 'D':
      // Read a general digital sensor
      reading = digitalRead((buffer[2] - 48) * 10 + (buffer[3] - 48));
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
    break;
  case 'W':
    // Launch actuator
    // [1] - Type
    // [2 - 3] - Pin
    // [4 - 6] - Value (max 255)
    uint8_t value = 0;
    uint8_t multi = 1;
    uint8_t indx = buffInd;
    switch(buffer[1]) {
    case 'A':
       // from buffInd (last) to 4
      while (indx > 3) {
        value += ((buffer[indx] - 48) * multi);
        indx--;
        multi *= 10;
      }
      if (DEBUG) {
        Serial.print("A.Value: ");
        Serial.println(value);
      }
      // Launch an analog actuator
      if (value < 256 && value >= 0) {
        analogWrite((buffer[2] - 48) * 10 + (buffer[3] - 48), value);
      } else
        err = 1;
      break;
    case 'D':
      // Launch a digital actuator. Possible values 1 or 0
      value = (buffer[4] - 48);
      if (value != 1 && value != 0)
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
  if (err || (buffer[0] == 'R' && isnan(reading)))
    Serial.print("ERROR");
  else
     Serial.print(reading);
  Serial.println();
}

void loop(void) {
  if (Serial.available()) {
    char command = Serial.read();
    if (DEBUG) {
      Serial.print("Index: ");
      Serial.println(buffInd);
      Serial.print("Input: ");
      Serial.println(command);
      Serial.println("");
    }
    if (command == '\n') {
      buffInd -= 1;
      process();
      buffInd = 0;
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
