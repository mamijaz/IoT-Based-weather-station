#include <Wire.h>
#include <Adafruit_BMP085.h>
#include <SoftwareSerial.h>
#include "DHT.h"

// Connect VCC of the BMP085 sensor to 3.3V (NOT 5.0V!)
// Connect GND to Ground
// Connect SCL to i2c clock - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 5
// Connect SDA to i2c data - on '168/'328 Arduino Uno/Duemilanove/etc thats Analog 4

//Connect CH_PD to VCC

SoftwareSerial ESP8622(10, 11);

#define DHTPIN 2     // what pin we're connected to
#define DHTTYPE DHT21   // DHT 21 (AM2301)
DHT dht(DHTPIN, DHTTYPE);

#define SSID "GHNS"
#define PASSWORD "123456789n"
#define apiKey "C1N3HN2Y462JQU58"

Adafruit_BMP085 bmp;

void setup() {
  Serial.begin(115200);
  ESP8622.begin(115200);
  bmp.begin();
  connectWiFi();
}

void loop() {
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  float pre = bmp.readPressure();
  
  if (isnan(temp) || isnan(hum)) {
    Serial.println("Failed to read from DHT");
  }
  else if (isnan(pre)) {
    Serial.println("Failed to read from BMP");
  }
  else {
    Serial.print("Temperature: ");
    Serial.print(temp);
    Serial.print(" *C\t");
    Serial.print("Humidity: ");
    Serial.print(hum);
    Serial.print(" %\t");
    Serial.print("Pressure: ");
    Serial.print(pre);
    Serial.println(" Pa");
    updateThingSpeak(temp,hum,pre);
  }
}

boolean connectWiFi() {
  ESP8622.println("AT+CWMODE=1");
  delay(2000);
  String cmd = "AT+CWJAP=\"";
  cmd += SSID;
  cmd += "\",\"";
  cmd += PASSWORD;
  cmd += "\"";
  ESP8622.println(cmd);
  delay(5000);
//  Serial.println("Connected to Wifi Hotspot");
}

boolean updateThingSpeak(float temp,float hum,float pre){
  
  String cmd = "AT+CIPSTART=\"TCP\",\"";
  cmd += "184.106.153.149";
  cmd += "\",80";
  ESP8622.println(cmd); //AT+CIPSTART="TCP","184.106.153.149",80
 
  if(ESP8622.find("OK")){
    Serial.println("Connected to ThingSpeak");
    String getStr = "GET /update?api_key=";
    getStr += apiKey;
    getStr +="&field1=";
    getStr += String(temp);
    getStr +="&field2=";
    getStr += String(hum);
    getStr +="&field3=";
    getStr += String(pre);
    getStr +="&field4=";
    getStr += String("null");
    getStr += "\r\n\r\n";
  
    // send data length
    String cmd1= "AT+CIPSEND=";
    cmd1 += String(getStr.length());
    ESP8622.println(cmd1);
  
    if(ESP8622.find(">")){
      ESP8622.print(getStr);
      Serial.println("Data Sended");
      delay(10000);
      return true;
    }
    else{
      ESP8622.println("AT+CIPCLOSE");
      Serial.println("Data Failed");
      return false;
    }
  }
  else{
    return 0;
  }
}

