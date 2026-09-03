package ngz.markov.sparkmodel.registry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import ngz.markov.sparkmodel.model.SparkModel;

public class ModelRegistryFactory {

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".yaml", ".yml", ".json");

    private ModelRegistryFactory() {}

    public static ModelRegistry createDefault(
            String registryDirectoryPath, List<Class<? extends SparkModel>> modelz) {
        List<Path> filePaths = listFiles(registryDirectoryPath);

        ModelRegistry registry = new ModelRegistry();

        filePaths.stream()
                // from file map to a spark registry spec
                .map(Path::toString)
                .map(SparkModelSpecReader::readModelRegistry)
                // extract the spark models spec from the whole registry specified
                .flatMap(spec -> spec.getModels().stream())
                // instantiate spark model
                .map(specModel -> SparkModelSpecReader.instantiateModel(specModel, modelz))
                // validate unicity
                .forEach(model -> registerWithUniquenessCheck(registry, model));

        // validate all dependencies are properly declared
        registry.validateAll();

        return registry;
    }

    private static void registerWithUniquenessCheck(ModelRegistry registry, SparkModel model) {
        Class<? extends SparkModel> modelClass = model.getClass();

        if (registry.contains(modelClass)) {
            throw new InvalidRegistryException(
                    "Duplicate model detected: %s is already registered."
                            .formatted(modelClass.getName()));
        }

        registry.register(model);
    }

    private static List<Path> listFiles(String registryDirectoryPath) {

        Path rootPath = Paths.get(registryDirectoryPath);

        if (Files.exists(rootPath) && Files.isRegularFile(rootPath)) {
            return List.of(rootPath);
        }
        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException(
                    "Specified registry directory path does not exist or is not a directory: "
                            + registryDirectoryPath);
        }

        // Utilisation du try-with-resources car Files.walk / Files.list ouvre un I/O Stream
        try (Stream<Path> stream = Files.walk(rootPath, 1)) {
            return stream.filter(Files::isRegularFile)
                    .filter(path -> hasValidExtension(path, ALLOWED_EXTENSIONS))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to list files in directory: " + registryDirectoryPath, e);
        }
    }

    private static boolean hasValidExtension(Path path, List<String> extensions) {
        String fileName = path.getFileName().toString().toLowerCase();
        return extensions.stream().anyMatch(fileName::endsWith);
    }
}
