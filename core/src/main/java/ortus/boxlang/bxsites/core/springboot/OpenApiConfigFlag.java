package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads/patches bx-sites' single top-level {@code openapi} boolean config
 * key (confirmed against {@code ConfigLoader.bx} and {@code configuration.md}
 * - always an un-nested key: {@code openapi: true} in YAML/TOML,
 * {@code "openapi": true} in JSON).
 *
 * <p>Deliberately not a full YAML/TOML/JSON parser - bx-sites' own config
 * schema has ~30 top-level keys and pulling in a parsing library just to
 * read/flip one boolean would be a heavier dependency than this single
 * check justifies. A regex-based scan for this one specific, always
 * top-level key is a safe, narrow scope - it does not attempt to
 * understand the rest of the document.
 */
public final class OpenApiConfigFlag {

    private static final Pattern YAML_OR_TOML_KEY = Pattern.compile("(?m)^\\s*openapi\\s*[:=]\\s*(true|false)\\s*$");
    private static final Pattern JSON_KEY = Pattern.compile("\"openapi\"\\s*:\\s*(true|false)");

    private OpenApiConfigFlag() {
    }

    public static boolean isEnabled(Path configFile) throws IOException {
        if (!Files.isRegularFile(configFile)) {
            return false;
        }
        String content = Files.readString(configFile);
        Matcher matcher = keyPattern(configFile).matcher(content);
        return matcher.find() && Boolean.parseBoolean(matcher.group(1));
    }

    /**
     * Flips (or adds) {@code openapi: true} in place. JSON is deliberately
     * unsupported - safely inserting a new top-level key into arbitrary
     * JSON without a real parser risks producing invalid JSON (trailing
     * commas, nesting), unlike YAML/TOML where a bare {@code key: value}
     * line can always be appended.
     */
    public static void enable(Path configFile) throws IOException {
        if (isJson(configFile)) {
            throw new UnsupportedOperationException(
                    "Cannot auto-patch a JSON config file safely - add \"openapi\": true to "
                            + configFile + " manually.");
        }
        if (!Files.isRegularFile(configFile)) {
            throw new IllegalStateException(
                    "No config file exists yet at " + configFile + " - run the `new` verb first.");
        }

        String content = Files.readString(configFile);
        Matcher matcher = YAML_OR_TOML_KEY.matcher(content);
        String enabledLine = isToml(configFile) ? "openapi = true" : "openapi: true";
        String updated = matcher.find()
                ? matcher.replaceFirst(Matcher.quoteReplacement(enabledLine))
                : appendLine(content, enabledLine);
        Files.writeString(configFile, updated);
    }

    private static String appendLine(String content, String line) {
        return (content.isEmpty() || content.endsWith("\n") ? content : content + "\n") + line + "\n";
    }

    private static Pattern keyPattern(Path configFile) {
        return isJson(configFile) ? JSON_KEY : YAML_OR_TOML_KEY;
    }

    private static boolean isJson(Path configFile) {
        return configFile.getFileName().toString().endsWith(".json");
    }

    private static boolean isToml(Path configFile) {
        return configFile.getFileName().toString().endsWith(".toml");
    }
}
