#include <TinyWireM.h>
#include "TinyDHT.h"
#include <SoftwareSerial.h>

#define DHT_TYPE DHT22
#define TEMP_TYPE 0 // Celsius
#define DHT_PIN 3
#define BLUE_PIN 0

SoftwareSerial Serial(2,0);
DHT dht(DHT_PIN, DHT_TYPE);

void setup() {
 dht.begin(); 
 Serial.begin(9600);
 Serial.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
 Serial.println(" Greenhouse System active and waiting ");
 Serial.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
}

void loop() {
  int8_t humi = dht.readHumidity();
  int16_t temp = dht.readTemperature(TEMP_TYPE);
  Serial.println(humi + " " + temp);
  delay(5000);
}
