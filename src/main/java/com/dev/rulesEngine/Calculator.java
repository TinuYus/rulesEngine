package com.dev.rulesEngine;

import org.eclipse.paho.client.mqttv3.*;
import com.google.gson.*;

public class Calculator {
	
	//instantiate the broker address and port
    private static final String BROKER = "tcp://test.mosquitto.org:1883";
    //Define the topic for incoming messages
    private static final String INPUT_TOPIC = "BRE/calculateWinterSupplementInput/a3db6375-ee19-456e-a087-72d8a0d6990f";
    //Define the topic to publish
    private static final String OUTPUT_TOPIC_BASE = "BRE/calculateWinterSupplementOutput/a3db6375-ee19-456e-a087-72d8a0d6990f";

    public static void main(String[] args) throws MqttException {
    	//connect the client to mqtt broker which generates an ID for the client
        MqttClient client = new MqttClient(BROKER, MqttClient.generateClientId());
        client.setCallback(new SimpleMqttCallback(client));
        //establish a connection to the broker
        client.connect();
        //subscribe to the topic and monitor for messages
        client.subscribe(INPUT_TOPIC);
        //print a message to indicate a successful connection
        System.out.println("Connected to the broker");
    }

    static class SimpleMqttCallback implements MqttCallback {

        private final MqttClient mqttClient;
        //handle mqtt events like callbacks and loss of connection
        SimpleMqttCallback(MqttClient mqttClient) {
            this.mqttClient = mqttClient;
        }
        
        //Print a message in the event of a connection loss
        @Override
        public void connectionLost(Throwable e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
        
        //When a message arrives, the messageArrived method is triggered
        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        	//convert into a JsonObject
            JsonObject obj1 = JsonParser.parseString(new String(mqttMessage.getPayload())).getAsJsonObject();
            //determines eligibility and amount
            JsonObject obj2 = processInput(obj1);
            String output = OUTPUT_TOPIC_BASE + obj1.get("id").getAsString();
            mqttClient.publish(output, new MqttMessage(obj2.toString().getBytes()));
            System.out.println("result published to: " + output);
        }

        @Override
     //To log delivery status
        public void deliveryComplete(IMqttDeliveryToken deliveryToken) {
            
        }
    }

    public static JsonObject processInput(JsonObject pay) {
    	//determines eligibilty based on user input
        boolean isEligible = pay.get("familyUnitInPayForDecember").getAsBoolean();
        //gets the composition of the family
        String composition = pay.get("familyComposition").getAsString();
        //gets the number of children
        int numOfChildren = pay.get("numberOfChildren").getAsInt();
        //calculates the amount if eligible
        double benefitAmount = isEligible ? (composition.equals("single") ? 60.0 : 120.0) : 0.0;
        //calculates the amount based on the number of children
        double childBenefit = isEligible ? numOfChildren * 20.0 : 0.0;
        
        //returns the output in JSON format
        JsonObject obj3 = new JsonObject();
        obj3.addProperty("id", pay.get("id").getAsString());
        obj3.addProperty("isEligible", isEligible);
        obj3.addProperty("baseAmount", benefitAmount);
        obj3.addProperty("childrenAmount", childBenefit);
        obj3.addProperty("supplementAmount", benefitAmount + childBenefit);
        return obj3;
    }
}
