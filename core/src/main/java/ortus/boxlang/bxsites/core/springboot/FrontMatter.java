package ortus.boxlang.bxsites.core.springboot;

import java.util.List;

/**
 * Renders bx-sites' standard page frontmatter block - shared by every
 * Spring Boot doc generator (OpenAPI, Javadoc, controller-scan) so all
 * three, regardless of which build tool invoked them, produce
 * byte-identical frontmatter for the same inputs.
 */
public final class FrontMatter {

    private FrontMatter() {
    }

    public record Fields(String title, Double order, String icon, String summary, List<String> tags) {
        public Fields {
            tags = tags == null ? List.of() : List.copyOf(tags);
        }
    }

    public static String render(Fields fields) {
        StringBuilder sb = new StringBuilder("---\n");
        sb.append("title: ").append(quote(fields.title())).append('\n');
        if (fields.order() != null) {
            sb.append("order: ").append(formatOrder(fields.order())).append('\n');
        }
        if (notBlank(fields.icon())) {
            sb.append("icon: ").append(fields.icon()).append('\n');
        }
        if (notBlank(fields.summary())) {
            sb.append("summary: ").append(quote(fields.summary())).append('\n');
        }
        if (!fields.tags().isEmpty()) {
            sb.append("tags: [").append(String.join(", ", fields.tags())).append("]\n");
        }
        sb.append("---\n");
        return sb.toString();
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private static String quote(String value) {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    private static String formatOrder(double order) {
        return order == Math.floor(order) ? String.valueOf((long) order) : String.valueOf(order);
    }
}
