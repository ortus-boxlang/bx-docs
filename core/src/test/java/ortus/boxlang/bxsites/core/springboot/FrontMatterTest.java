package ortus.boxlang.bxsites.core.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class FrontMatterTest {

    @Test
    void rendersTitleOnly() {
        String result = FrontMatter.render(new FrontMatter.Fields("API Reference", null, null, null, null));

        assertEquals("---\ntitle: \"API Reference\"\n---\n", result);
    }

    @Test
    void rendersEveryFieldWhenPresent() {
        String result = FrontMatter.render(new FrontMatter.Fields(
                "API Reference", 4.6, "phosphor-duotone:plug", "The full API", List.of("api", "openapi")));

        assertEquals(
                "---\ntitle: \"API Reference\"\norder: 4.6\nicon: phosphor-duotone:plug\nsummary: \"The full API\"\ntags: [api, openapi]\n---\n",
                result);
    }

    @Test
    void formatsAWholeNumberOrderWithoutADecimalPoint() {
        String result = FrontMatter.render(new FrontMatter.Fields("Title", 6.0, null, null, null));

        assertEquals("---\ntitle: \"Title\"\norder: 6\n---\n", result);
    }

    @Test
    void escapesQuotesInTitleAndSummary() {
        String result = FrontMatter.render(new FrontMatter.Fields("A \"quoted\" title", null, null, "A \"quoted\" summary", null));

        assertEquals("---\ntitle: \"A \\\"quoted\\\" title\"\nsummary: \"A \\\"quoted\\\" summary\"\n---\n", result);
    }
}
