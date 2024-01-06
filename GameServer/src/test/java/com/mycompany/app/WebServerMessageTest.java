package com.mycompany.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import org.junit.Test;

import com.mycompany.app.WebServer.Packet;

public class WebServerMessageTest {

    // @Test 
    // public void basicParseJsonTest() {
    //     String jsonString = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\",\"nested\":{\"key\":\"value\"},\"array\":[1,2,3]}";

    //     HashMap<String, Object> jsonMap = Message.parseJson(jsonString);
    //     assertNotNull(jsonMap);

    //     assertTrue(jsonMap.keySet().size() == 5); // first depth has five elements
    //     assertTrue(jsonMap.containsKey("name"));
    //     assertTrue(jsonMap.containsKey("age"));
    //     assertTrue(jsonMap.containsKey("city"));
    //     assertTrue(jsonMap.containsKey("nested"));
    //     assertTrue(jsonMap.containsKey("array"));

    //     Object nestedMapObject = jsonMap.get("nested");
    //     assertEquals(HashMap.class, nestedMapObject.getClass());
    //     HashMap<String, Object> nestedMap = (HashMap<String, Object>)nestedMapObject;
    //     assertTrue(nestedMap.keySet().size() == 1); 
    //     assertTrue(nestedMap.containsKey("key"));

    //     Object nestedArrayObject = jsonMap.get("array");
    //     Object[] nestedArray = (Object[])nestedArrayObject;
    //     assertTrue(nestedArray.length == 3); 
    // }

    // @Test
    // public void testParsePrimitiveBooleanTrue() {
    //     Object result = Message.parsePrimitive("true");
    //     assertTrue(result instanceof Boolean);
    //     assertEquals(true, result);
    // }

    // @Test
    // public void testParsePrimitiveBooleanFalse() {
    //     Object result = Message.parsePrimitive("false");
    //     assertTrue(result instanceof Boolean);
    //     assertEquals(false, result);
    // }

    // @Test
    // public void testParsePrimitiveDouble() {
    //     Object result = Message.parsePrimitive("123.45");
    //     assertTrue(result instanceof Double);
    //     assertEquals(123.45, (Double) result, 0.0001);
    // }

    // @Test
    // public void testParsePrimitiveLong() {
    //     Object result = Message.parsePrimitive("42");
    //     assertTrue(result instanceof Long);
    //     assertEquals(42L, result);
    // }

    // @Test
    // public void testParsePrimitiveInvalidInput() {
    //     // Test with invalid input that cannot be parsed
    //     try {
    //         Message.parsePrimitive("invalid");
    //         fail("Expected NumberFormatException was not thrown");
    //     } catch (NumberFormatException e) {
    //         // Expected exception
    //     }
    // }

    // @Test
    // public void testParsePrimitiveNullInput() {
    //     // Test with null input
    //     try {
    //         Message.parsePrimitive(null);
    //         fail("Expected IllegalArgumentException was not thrown");
    //     } catch (IllegalArgumentException e) {
    //         // Expected exception
    //         assertEquals("Input value cannot be null", e.getMessage());
    //     }
    // }
    // @Test 
    // public void minimalMessageTest() {
    //     String rawMessage = "{ \"posx\": }"

    //     Message message = new Message()
    // }
}
