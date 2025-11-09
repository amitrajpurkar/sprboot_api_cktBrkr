package com.anr.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * ArchUnit tests to enforce architectural rules and best practices.
 * These tests validate:
 * - Layer architecture (controller → service → repository)
 * - Naming conventions
 * - Package dependencies
 * - Annotation usage rules
 */
@DisplayName("Architecture Tests")
class ArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.anr");
    }

    @Test
    @DisplayName("Layered architecture should be respected")
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")
                .layer("Config").definedBy("..config..")
                .layer("Logging").definedBy("..logging..")
                
                .whereLayer("Controller").mayOnlyBeAccessedByLayers("Logging")
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Config", "Logging")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Config");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should be named with Controller suffix")
    void controllersShouldBeNamedCorrectly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controller..")
                .and().areAnnotatedWith(RestController.class)
                .or().areAnnotatedWith(Controller.class)
                .should().haveSimpleNameEndingWith("Controller");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Service classes ending with Service should be annotated with @Service")
    void servicesShouldBeNamedCorrectly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..service..")
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(Service.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repository classes ending with Repository should be annotated with @Repository")
    void repositoriesShouldBeNamedCorrectly() {
        ArchRule rule = classes()
                .that().resideInAPackage("..repository..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith(Repository.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should only depend on services and models")
    void controllersShouldOnlyDependOnServicesAndModels() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controller..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage(
                        "..controller..",
                        "..service..",
                        "..model..",
                        "..exception..",
                        "..common..",
                        "java..",
                        "org.springframework..",
                        "org.slf4j..",
                        "io.swagger..",
                        "jakarta..",
                        "lombok.."
                );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Services should not depend on controllers")
    void servicesShouldNotDependOnControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..service..")
                .should().dependOnClassesThat()
                .resideInAPackage("..controller..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories should not depend on services or controllers")
    void repositoriesShouldNotDependOnServicesOrControllers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..repository..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..service..", "..controller..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controllers should be annotated with @RestController or @Controller")
    void controllersShouldHaveControllerAnnotation() {
        ArchRule rule = classes()
                .that().resideInAPackage("..controller..")
                .and().haveSimpleNameEndingWith("Controller")
                .should().beAnnotatedWith(RestController.class)
                .orShould().beAnnotatedWith(Controller.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Services should be annotated with @Service")
    void servicesShouldHaveServiceAnnotation() {
        ArchRule rule = classes()
                .that().resideInAPackage("..service..")
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(Service.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repositories should be annotated with @Repository")
    void repositoriesShouldHaveRepositoryAnnotation() {
        ArchRule rule = classes()
                .that().resideInAPackage("..repository..")
                .and().haveSimpleNameEndingWith("Repository")
                .should().beAnnotatedWith(Repository.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Configuration classes should reside in config package")
    void configurationClassesShouldResideInConfigPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Config")
                .or().haveSimpleNameEndingWith("Configuration")
                .should().resideInAPackage("..config..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Exception classes should reside in exception package")
    void exceptionClassesShouldResideInExceptionPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Exception")
                .should().resideInAPackage("..exception..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Model classes should not have Spring annotations")
    void modelClassesShouldNotHaveSpringAnnotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..model..")
                .should().beAnnotatedWith(Service.class)
                .orShould().beAnnotatedWith(Repository.class)
                .orShould().beAnnotatedWith(Controller.class)
                .orShould().beAnnotatedWith(RestController.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("No classes should use deprecated APIs")
    void noClassesShouldUseDeprecatedAPIs() {
        ArchRule rule = noClasses()
                .should().dependOnClassesThat()
                .areAnnotatedWith(Deprecated.class);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Logging classes should reside in logging package")
    void loggingClassesShouldResideInLoggingPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Logging")
                .or().haveSimpleNameContaining("Log")
                .and().resideInAPackage("com.anr..")
                .should().resideInAPackage("..logging..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Aspect classes should reside in logging or config package")
    void aspectClassesShouldResideInCorrectPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Aspect")
                .should().resideInAnyPackage("..logging..", "..config..");

        rule.check(importedClasses);
    }
}
