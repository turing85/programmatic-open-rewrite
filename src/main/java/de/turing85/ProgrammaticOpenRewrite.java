package de.turing85;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Result;
import org.openrewrite.SourceFile;
import org.openrewrite.config.Environment;
import org.openrewrite.internal.InMemoryLargeSourceSet;
import org.openrewrite.java.JavaParser;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgrammaticOpenRewrite {
  public static void main(String... args) {
    List<SourceFile> sourceFiles = JavaParser
        .fromJavaVersion()
        .classpath(Collections.emptyList())
        .build()
        .parse(
              """
              package foo.bar;
              
              import javax.enterprise.context.ApplicationScoped;
              
              @ApplicationScoped
              class Foo {
              }
              """)
        .toList();
    ExecutionContext context = new InMemoryExecutionContext(Throwable::printStackTrace);
    Environment environment = Environment.builder()
        .scanRuntimeClasspath()
        .build();
    Recipe recipe = environment
        .activateRecipes("org.openrewrite.java.migrate.jakarta.JavaxMigrationToJakarta");
    recipe
        .run(new InMemoryLargeSourceSet(sourceFiles), context)
        .getChangeset()
        .getAllResults()
        .stream()
        .map(Result::getAfter)
        .filter(Objects::nonNull)
        .map(SourceFile::printAll)
        .forEach(file ->
            Logger.getAnonymousLogger().log(
                Level.INFO,
                "File after rewrite:%n%s".formatted(file)));
  }
}
