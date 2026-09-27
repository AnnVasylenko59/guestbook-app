package app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CoreArchitectureTest {
    @Test
    void coreShouldNotDependOnWebOrPersistence() {
        JavaClasses imported = new ClassFileImporter().importPackages("app");
        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..web..", "..persistence..", "java.sql..", "jakarta.servlet..")
                .check(imported);
    }
}