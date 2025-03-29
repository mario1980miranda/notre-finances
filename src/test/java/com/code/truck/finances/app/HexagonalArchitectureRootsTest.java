package com.code.truck.finances.app;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class HexagonalArchitectureRootsTest {
    @Test
    void core_should_not_depend_on_infrastructure() throws IOException {
        List<String> violations = new ArrayList<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);

        Resource[] resources = resolver.getResources("classpath:com/code/truck/finances/app/core/**/*.class");

        for (Resource resource : resources) {
            MetadataReader reader = factory.getMetadataReader(resource);
            ClassMetadata metadata = reader.getClassMetadata();
            String[] dependencies = metadata.getInterfaceNames();

            // Check class dependencies
            for (String dependency : dependencies) {
                if (dependency.startsWith("com.code.truck.finances.app.infrastructure")) {
                    violations.add(metadata.getClassName() + " depends on " + dependency);
                }
            }

            // Also check superclass
            String superclass = metadata.getSuperClassName();
            if (superclass != null && superclass.startsWith("com.code.truck.finances.app.infrastructure")) {
                violations.add(metadata.getClassName() + " extends " + superclass);
            }
        }

        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Core classes should not depend on infrastructure:\n");
            violations.forEach(v -> message.append("- ").append(v).append("\n"));
            fail(message.toString());
        }
    }

    @Test
    void core_should_only_depend_on_itself_and_standard_classes() throws IOException {
        List<String> violations = new ArrayList<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory(resolver);

        Resource[] resources = resolver.getResources("classpath:com/code/truck/finances/app/core/**/*.class");

        for (Resource resource : resources) {
            MetadataReader reader = factory.getMetadataReader(resource);
            String[] dependencies = reader.getClassMetadata().getInterfaceNames();

            // Check dependencies
            for (String dependency : dependencies) {
                if (!dependency.startsWith("com.code.truck.finances.app.core") &&
                        !dependency.startsWith("java.") &&
                        !dependency.startsWith("javax.") &&
                        !dependency.startsWith("org.slf4j.") &&
                        !dependency.startsWith("lombok.")) {
                    violations.add(reader.getClassMetadata().getClassName() +
                            " depends on non-core class " + dependency);
                }
            }
        }

        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder("Core classes should only depend on core and standard classes:\n");
            violations.forEach(v -> message.append("- ").append(v).append("\n"));
            fail(message.toString());
        }
    }
}
