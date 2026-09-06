package ortus.boxlang.bxsites.core;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mirrors bx-sites' own {@code SourceDirResolver}: a project's content lives
 * in {@code docs/}, falling back to {@code src/}, defaulting to {@code docs/}
 * when neither exists yet (e.g. before {@code new} has scaffolded anything).
 * Both plugins use this so their up-to-date/staleness checks watch whichever
 * directory bx-sites itself would actually use, instead of hardcoding
 * {@code docs/}.
 */
public final class ContentDirResolver {

    private ContentDirResolver() {
    }

    public static Path resolve(Path projectRoot) {
        Path docs = projectRoot.resolve("docs");
        if (Files.isDirectory(docs)) {
            return docs;
        }
        Path src = projectRoot.resolve("src");
        if (Files.isDirectory(src)) {
            return src;
        }
        return docs;
    }
}
