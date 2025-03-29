package com.code.truck.finances.app;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.EvaluationResult;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class HexagonalArchitectureTest {

    private final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.code.truck.finances.app");

    private final DescribedPredicate<JavaClass> areStandard = resideInAnyPackage(
            "",
            "java..",
            "javax..",
            "org.slf4j..",
            "lombok..");

    private final DescribedPredicate<JavaClass> areCore = resideInAnyPackage("com.code.truck.finances.app.core..");

    private final DescribedPredicate<JavaClass> areInfrastructure = resideInAnyPackage("com.code.truck.finances.app.infrastructure..");

    @Test
    void core_should_not_depend_on_infrastructure() {
        ArchRule rule = noClasses()
                .that(areCore)
                .should()
                .dependOnClassesThat(areInfrastructure);

        EvaluationResult result = rule.evaluate(classes);

        if (result.hasViolation()) {
            System.out.println("Violations found in the core_should_not_depend_on_infrastructure:");
            result.getFailureReport().getDetails().forEach(System.out::println);
        }

        rule.check(classes);
    }

    @Test
    void core_should_only_depend_on_itself_and_standard_classes() {
        ArchRule rule = classes()
                .that(areCore)
                .should()
                .onlyDependOnClassesThat(areCore.or(areStandard));

        EvaluationResult result = rule.evaluate(classes);

        if (result.hasViolation()) {
            System.out.println("Violations found in core_should_only_depend_on_itself_and_standard_classes");
            result.getFailureReport().getDetails().forEach(System.out::println);
        }

        rule.check(classes);
    }

}
