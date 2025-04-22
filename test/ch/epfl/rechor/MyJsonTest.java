package ch.epfl.rechor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for the Json interface and its implementations.
 */
public class MyJsonTest {

    @Test
    public void testJString() {
        Json.JString jString = new Json.JString("test");
        assertEquals("\"test\"", jString.toString());

        Json.JString emptyString = new Json.JString("");
        assertEquals("\"\"", emptyString.toString());
    }

    @Test
    public void testJNumber() {
        Json.JNumber jNumber = new Json.JNumber(42);
        assertEquals("42.0", jNumber.toString());

        Json.JNumber jNumberWithDecimal = new Json.JNumber(3.14);
        assertEquals("3.14", jNumberWithDecimal.toString());

        Json.JNumber zero = new Json.JNumber(0);
        assertEquals("0.0", zero.toString());

        Json.JNumber negative = new Json.JNumber(-123.45);
        assertEquals("-123.45", negative.toString());
    }

    @Test
    public void testJArray() {
        List<Json> elements = new ArrayList<>();
        elements.add(new Json.JString("element1"));
        elements.add(new Json.JNumber(42));

        Json.JArray jArray = new Json.JArray(elements);
        assertEquals("[\"element1\",42.0]", jArray.toString());

        // Test empty array
        Json.JArray emptyArray = new Json.JArray(List.of());
        assertEquals("[]", emptyArray.toString());

        // Test nested array
        List<Json> nestedElements = new ArrayList<>();
        nestedElements.add(new Json.JString("nested"));
        Json.JArray nestedArray = new Json.JArray(nestedElements);

        List<Json> outerElements = new ArrayList<>();
        outerElements.add(nestedArray);
        outerElements.add(new Json.JNumber(99));

        Json.JArray outerArray = new Json.JArray(outerElements);
        assertEquals("[[\"nested\"],99.0]", outerArray.toString());
    }

    @Test
    public void testJObject() {
        Map<String, Json> properties = new HashMap<>();
        properties.put("name", new Json.JString("test"));
        properties.put("value", new Json.JNumber(123));

        Json.JObject jObject = new Json.JObject(properties);
        String result = jObject.toString();

        // Since the order of properties in a Map is not guaranteed,
        // we need to check both possible outcomes
        assertTrue(
                result.equals("{\"name\":\"test\",\"value\":123.0}") ||
                        result.equals("{\"value\":123.0,\"name\":\"test\"}")
        );

        // Test empty object
        Json.JObject emptyObject = new Json.JObject(Map.of());
        assertEquals("{}", emptyObject.toString());

        // Test nested object
        Map<String, Json> nestedProperties = new HashMap<>();
        nestedProperties.put("nestedKey", new Json.JString("nestedValue"));
        Json.JObject nestedObject = new Json.JObject(nestedProperties);

        Map<String, Json> outerProperties = new HashMap<>();
        outerProperties.put("obj", nestedObject);
        outerProperties.put("num", new Json.JNumber(42));

        Json.JObject outerObject = new Json.JObject(outerProperties);
        result = outerObject.toString();

        assertTrue(
                result.equals("{\"obj\":{\"nestedKey\":\"nestedValue\"},\"num\":42.0}") ||
                        result.equals("{\"num\":42.0,\"obj\":{\"nestedKey\":\"nestedValue\"}}")
        );
    }

    @Test
    public void testComplexJson() {
        // Create a complex JSON structure that might be used for GeoJSON
        Map<String, Json> geoJson = new HashMap<>();
        geoJson.put("type", new Json.JString("LineString"));

        List<Json> coordinates = new ArrayList<>();

        List<Json> point1 = new ArrayList<>();
        point1.add(new Json.JNumber(6.62909));
        point1.add(new Json.JNumber(46.51679));
        coordinates.add(new Json.JArray(point1));

        List<Json> point2 = new ArrayList<>();
        point2.add(new Json.JNumber(6.83787));
        point2.add(new Json.JNumber(46.54276));
        coordinates.add(new Json.JArray(point2));

        geoJson.put("coordinates", new Json.JArray(coordinates));

        Json.JObject geoJsonObject = new Json.JObject(geoJson);
        String result = geoJsonObject.toString();

        // Check that the result contains all the expected parts (order may vary)
        assertTrue(result.contains("\"type\":\"LineString\""));
        assertTrue(result.contains("\"coordinates\":"));
        assertTrue(result.contains("[6.62909,46.51679]"));
        assertTrue(result.contains("[6.83787,46.54276]"));
    }

    @Test
    public void testNestedStructures() {
        // Create a JSON structure with multiple nesting levels
        Map<String, Json> user = new HashMap<>();
        user.put("name", new Json.JString("John Doe"));
        user.put("age", new Json.JNumber(30));

        Map<String, Json> address = new HashMap<>();
        address.put("street", new Json.JString("123 Main St"));
        address.put("city", new Json.JString("Anytown"));

        Map<String, Json> coordinates = new HashMap<>();
        coordinates.put("lat", new Json.JNumber(40.7128));
        coordinates.put("lng", new Json.JNumber(-74.006));
        address.put("coordinates", new Json.JObject(coordinates));

        user.put("address", new Json.JObject(address));

        List<Json> phoneNumbers = new ArrayList<>();

        Map<String, Json> homePhone = new HashMap<>();
        homePhone.put("type", new Json.JString("home"));
        homePhone.put("number", new Json.JString("555-1234"));
        phoneNumbers.add(new Json.JObject(homePhone));

        Map<String, Json> workPhone = new HashMap<>();
        workPhone.put("type", new Json.JString("work"));
        workPhone.put("number", new Json.JString("555-5678"));
        phoneNumbers.add(new Json.JObject(workPhone));

        user.put("phoneNumbers", new Json.JArray(phoneNumbers));

        Json.JObject userObject = new Json.JObject(user);
        String result = userObject.toString();

        // Check that the result contains all the expected parts
        assertTrue(result.contains("\"name\":\"John Doe\""));
        assertTrue(result.contains("\"age\":30.0"));
        assertTrue(result.contains("\"street\":\"123 Main St\""));
        assertTrue(result.contains("\"city\":\"Anytown\""));
        assertTrue(result.contains("\"coordinates\":{"));
        assertTrue(result.contains("\"lat\":40.7128"));
        assertTrue(result.contains("\"lng\":-74.006"));
        assertTrue(result.contains("\"phoneNumbers\":["));
        assertTrue(result.contains("\"type\":\"home\""));
        assertTrue(result.contains("\"number\":\"555-1234\""));
        assertTrue(result.contains("\"type\":\"work\""));
        assertTrue(result.contains("\"number\":\"555-5678\""));
    }
}