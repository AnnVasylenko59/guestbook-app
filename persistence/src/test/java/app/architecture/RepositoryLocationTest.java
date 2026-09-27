package app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class RepositoryLocationTest {
    @Test
    void repositoriesShouldResideInPersistence() {
        JavaClasses imported = new ClassFileImporter().importPackages("app");
        classes()
                .that().haveNameMatching(".*Repository")
                .should().resideInAPackage("..persistence..")
                .check(imported);
    }
}