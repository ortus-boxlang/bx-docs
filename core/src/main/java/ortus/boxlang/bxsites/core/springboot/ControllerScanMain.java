package ortus.boxlang.bxsites.core.springboot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Forked-JVM entry point for {@link ControllerScanGenerator} - both
 * plugins launch this as a plain {@code java -cp <this jar>:<target
 * project's own runtime classpath> ...} subprocess, since the scan needs
 * Spring and the target project's own compiled classes on its classpath,
 * neither of which this plugin's own JVM has (or should have - this
 * module stays Spring-agnostic).
 *
 * <p>Args: {@code <classesDir> <contentDir> <pagePathPrefix> [tag,tag,...]}
 */
public final class ControllerScanMain {

    private ControllerScanMain() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: ControllerScanMain <classesDir> <contentDir> <pagePathPrefix> [tag,tag,...]");
            System.exit(2);
            return;
        }

        Path classesDir = Path.of(args[0]);
        Path contentDir = Path.of(args[1]);
        String pagePathPrefix = args[2];
        List<String> tags = args.length > 3 && !args[3].isBlank() ? List.of(args[3].split(",")) : List.of();

        ControllerScanGenerator.Result result = ControllerScanGenerator.scan(
                new ControllerScanGenerator.Request(classesDir, contentDir, pagePathPrefix, tags),
                ControllerScanMain.class.getClassLoader());

        for (String warning : result.warnings()) {
            System.err.println("WARN: " + warning);
        }
        System.out.println("Wrote " + result.pageFiles().size() + " controller page(s)");
    }
}
