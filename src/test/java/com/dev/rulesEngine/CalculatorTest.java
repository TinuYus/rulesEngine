package com.dev.rulesEngine;

import com.google.gson.JsonObject;
import org.eclipse.paho.client.mqttv3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CalculatorTest {
    
    private Calculator.SimpleMqttCallback mqttCallback;
    private MqttClient mqttClient;

    @BeforeEach
    public void setUp() throws MqttException {
        mqttClient = mock(MqttClient.class);
        mqttCallback = new Calculator.SimpleMqttCallback(mqttClient);
    }

    //Tests the logic for single eligibility without children
    @Test
    public void singleNoChildren() {
        JsonObject input = new JsonObject();
        input.addProperty("id", "1");
        input.addProperty("familyUnitInPayForDecember", true);
        input.addProperty("familyComposition", "single");
        input.addProperty("numberOfChildren", 0);

        JsonObject result = Calculator.processInput(input);

        assertEquals("1", result.get("id").getAsString());
        assertTrue(result.get("isEligible").getAsBoolean());
        assertEquals(60.0, result.get("baseAmount").getAsDouble());
        assertEquals(0.0, result.get("childrenAmount").getAsDouble());
        assertEquals(60.0, result.get("supplementAmount").getAsDouble());
    }

    //Tests the logic for families with children
    @Test
    public void familyWithChildren() {
        JsonObject input = new JsonObject();
        input.addProperty("id", "2");
        input.addProperty("familyUnitInPayForDecember", true);
        input.addProperty("familyComposition", "couple");
        input.addProperty("numberOfChildren", 2);

        JsonObject result = Calculator.processInput(input);

        assertEquals("2", result.get("id").getAsString());
        assertTrue(result.get("isEligible").getAsBoolean());
        assertEquals(120.0, result.get("baseAmount").getAsDouble());
        assertEquals(40.0, result.get("childrenAmount").getAsDouble());
        assertEquals(160.0, result.get("supplementAmount").getAsDouble());
    }

    //Tests the logic for non eligibility
    @Test
    public void notEligible() {
        JsonObject input = new JsonObject();
        input.addProperty("id", "3");
        input.addProperty("familyUnitInPayForDecember", false);
        input.addProperty("familyComposition", "single");
        input.addProperty("numberOfChildren", 1);

        JsonObject result = Calculator.processInput(input);

        assertEquals("3", result.get("id").getAsString());
        assertFalse(result.get("isEligible").getAsBoolean());
        assertEquals(0.0, result.get("baseAmount").getAsDouble());
        assertEquals(0.0, result.get("childrenAmount").getAsDouble());
        assertEquals(0.0, result.get("supplementAmount").getAsDouble());
    }
//Tests the logic that verifies messages and publishes the correct results
    @Test
    public void testMessageArrived() throws Exception {
        JsonObject input = new JsonObject();
        input.addProperty("id", "1");
        input.addProperty("familyUnitInPayForDecember", true);
        input.addProperty("familyComposition", "single");
        input.addProperty("numberOfChildren", 0);

        MqttMessage message = new MqttMessage(input.toString().getBytes());

        mqttCallback.messageArrived("BRE/calculateWinterSupplementInput/a3db6375-ee19-456e-a087-72d8a0d6990f", message);

        verify(mqttClient, times(1)).publish(anyString(), any(MqttMessage.class));
    }
    
    //Tests the method that verifies loss of connection
    @Test
    public void testConnectionLost() {
        Exception exception = new Exception("Connection lost");
        mqttCallback.connectionLost(exception);

    }
//Tests the delivery complete method
    @Test
    public void testDeliveryComplete() {
        IMqttDeliveryToken token = mock(IMqttDeliveryToken.class);
        mqttCallback.deliveryComplete(token);

    }
}
