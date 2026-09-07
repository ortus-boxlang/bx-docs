package ortus.boxlang.bxsites.core;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mirrors bx-sites' own {@code ConfigLoader.bx} resolution order: the site
 * config file is {@code bxsites.yaml}, falling back in turn to {@code .yml},
 * {@code .toml}, then {@code .json} - the first one found on disk wins.
 * Defaults to the {@code bxsites.yaml} path when none exist yet (e.g. before
 * {@code new} has scaffolded a project), matching {@code ConfigLoader.bx}'s
 * own "no config found" fallback path.
 */
public final class ConfigFileResolver {

    private static final String[] CANDIDATE_NAMES = {
            "bxsites.yaml", "bxsites.yml", "bxsites.toml", "bxsites.json"
    };

    private ConfigFileResolver() {
    }

    public static Path resolve(Path projectRoot) {
        for (String name : CANDIDATE_NAMES) {
            Path candidate = projectRoot.resolve(name);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }
        return projectRoot.resolve(CANDIDATE_NAMES[0]);
    }
}
