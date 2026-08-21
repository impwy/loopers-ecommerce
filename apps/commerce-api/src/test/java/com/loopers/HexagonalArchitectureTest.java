package com.loopers;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.library.Architectures;

@AnalyzeClasses(packages = "com.loopers", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {
    @ArchTest
    void hexagonalArchitecture(JavaClasses classes) {
        Architectures.layeredArchitecture()
                     .consideringAllDependencies()
                     .layer("domain").definedBy("com.loopers.domain..")
                     .layer("application").definedBy("com.loopers.application..")
                     .layer("adapter").definedBy("com.loopers.adapter..")
                     .whereLayer("domain").mayOnlyBeAccessedByLayers("application", "adapter")
                     .whereLayer("application").mayOnlyBeAccessedByLayers("adapter")
                     .whereLayer("adapter").mayNotBeAccessedByAnyLayer()
                     .check(classes);
    }
}
