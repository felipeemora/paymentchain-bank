@echo off
set SPRING_CLOUD_CONFIG_NAME=application
set SPRING_CLOUD_CONFIG_PROFILE=dev
set CONFIG_SERVER=optional:configserver:http://localhost:8888
java -jar "C:\Users\julim\Documents\Java\payment-chain\paymentchainparent\businessdomain\customer\target\customer-0.0.1-SNAPSHOT.jar"