package app.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ControllerLocationTest {
    @Test
    void servletsShouldResideOnlyInWeb() {
        JavaClasses imported = new ClassFileImporter().importPackages("app");
        classes()
                .that().haveSimpleNameEndingWith("Servlet")
                .should().resideInAPackage("..web..")
                .check(imported);
    }
}