package ch.epfl.rechor;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

/**
 * Test class for IcalBuilder
 */
public class MyIcalBuilderTest {

    @Test
    public void testEmptyBuild() {
        IcalBuilder builder = new IcalBuilder();
        assertEquals("", builder.build(), "Empty builder should return empty string");
    }

    @Test
    public void testBasicAddString() {
        IcalBuilder builder = new IcalBuilder();
        builder
                .begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .end();

        String expected = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nEND:VCALENDAR\r\n";
        assertEquals(expected, builder.build(), "Basic calendar with version should match");
    }

    @Test
    public void testNestedComponents() {
        IcalBuilder builder = new IcalBuilder();
        builder
                .begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .add(IcalBuilder.Name.PRODID, "ReCHor")
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.SUMMARY, "Test Event")
                .end() // End VEVENT
                .end(); // End VCALENDAR

        String expected =
                "BEGIN:VCALENDAR\r\n" +
                        "VERSION:2.0\r\n" +
                        "PRODID:ReCHor\r\n" +
                        "BEGIN:VEVENT\r\n" +
                        "SUMMARY:Test Event\r\n" +
                        "END:VEVENT\r\n" +
                        "END:VCALENDAR\r\n";

        assertEquals(expected, builder.build(), "Nested components should be properly formatted");
    }

    @Test
    public void testAddDateTime() {
        IcalBuilder builder = new IcalBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2025, 2, 18, 16, 13, 0);

        builder
                .begin(IcalBuilder.Component.VCALENDAR)
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.DTSTART, dateTime)
                .end()
                .end();

        String expected =
                "BEGIN:VCALENDAR\r\n" +
                        "BEGIN:VEVENT\r\n" +
                        "DTSTART:20250218T161300\r\n" +
                        "END:VEVENT\r\n" +
                        "END:VCALENDAR\r\n";

        assertEquals(expected, builder.build(), "DateTime should be properly formatted");
    }

    @Test
    public void testLongLineFolding() {
        IcalBuilder builder = new IcalBuilder();
        String longDescription = "This is a very long description that should be folded according to the iCalendar " +
                "specification because it exceeds 75 characters in length and needs to be properly " +
                "formatted in the output";

        builder
                .begin(IcalBuilder.Component.VCALENDAR)
                .begin(IcalBuilder.Component.VEVENT)
                .add(IcalBuilder.Name.DESCRIPTION, longDescription)
                .end()
                .end();

        String result = builder.build();

        // Check that no line exceeds 75 characters (excluding CRLF)
        String[] lines = result.split("\r\n");
        for (String line : lines) {
            assertTrue(line.length() <= 75, "Each line should be at most 75 characters: " + line);
        }

        // Check that the content is preserved
        assertTrue(result.contains("DESCRIPTION:"), "Result should contain DESCRIPTION:");

        // Unfold the lines to check if we get back the original content
        StringBuilder unfolded = new StringBuilder();
        boolean inDescription = false;

        for (String line : lines) {
            if (line.startsWith("DESCRIPTION:")) {
                inDescription = true;
                unfolded.append(line.substring("DESCRIPTION:".length()));
            } else if (inDescription && line.startsWith(" ")) {
                unfolded.append(line.substring(1));
            } else {
                inDescription = false;
            }
        }

        assertEquals(longDescription, unfolded.toString(), "Unfolded content should match original");
    }

    @Test
    public void testEndWithoutBegin() {
        IcalBuilder builder = new IcalBuilder();
        assertThrows(IllegalArgumentException.class, () -> {
            builder.end();
        }, "Exception should be thrown when calling end() without a matching begin()");
    }

    @Test
    public void testBuildWithoutEnd() {
        IcalBuilder builder = new IcalBuilder();
        builder.begin(IcalBuilder.Component.VCALENDAR);

        assertThrows(IllegalArgumentException.class, () -> {
            builder.build();
        }, "Exception should be thrown when calling build() with unbalanced begin/end");
    }

    @Test
    public void testMultipleBuilds() {
        IcalBuilder builder = new IcalBuilder();
        builder
                .begin(IcalBuilder.Component.VCALENDAR)
                .add(IcalBuilder.Name.VERSION, "2.0")
                .end();

        String first = builder.build();
        String second = builder.build();

        assertEquals(first, second, "Multiple calls to build() should return the same result");
    }

    @Test
    public void testFluentInterface() {
        IcalBuilder builder = new IcalBuilder();
        IcalBuilder returnedBuilder = builder.begin(IcalBuilder.Component.VCALENDAR);

        assertSame(builder, returnedBuilder, "Methods should return the builder instance for fluent chaining");
    }
}