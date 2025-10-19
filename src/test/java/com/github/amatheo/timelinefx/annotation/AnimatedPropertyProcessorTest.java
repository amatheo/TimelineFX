package com.github.amatheo.timelinefx.annotation;

import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link AnimatedPropertyProcessor} to verify it correctly
 * validates that {@code @AnimatedProperty} cannot be applied to final fields.
 */
class AnimatedPropertyProcessorTest {

    /**
     * Test that the processor correctly detects and reports an error when
     * {@code @AnimatedProperty} is applied to a final field.
     */
    @Test
    void processorDetectsFinalFieldError() throws IOException {
        // Create a temporary Java source file with a final field annotated with @AnimatedProperty
        String sourceCode = """
            package test;
            import com.github.amatheo.timelinefx.annotation.AnimatedProperty;
            
            public class TestEffect {
                @AnimatedProperty
                private final String testField = "test";
            }
            """;

        // Get the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "Java compiler must be available");

        // Create a diagnostic collector to capture compilation errors
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);

        // Create a temporary directory for the source file
        Path tempDir = Files.createTempDirectory("annotation-processor-test");
        Path sourceFile = tempDir.resolve("TestEffect.java");
        Files.writeString(sourceFile, sourceCode);

        // Get the source file object
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());

        // Set up compiler options - include the processor on the classpath
        List<String> options = Arrays.asList(
            "-proc:only",  // Only run annotation processing
            "-processor", "com.github.amatheo.timelinefx.annotation.AnimatedPropertyProcessor",
            "-classpath", System.getProperty("java.class.path")
        );

        // Compile the source file
        JavaCompiler.CompilationTask task = compiler.getTask(
            null,
            fileManager,
            diagnostics,
            options,
            null,
            compilationUnits
        );

        // Execute the compilation
        Boolean success = task.call();

        // Verify that compilation failed
        assertFalse(success, "Compilation should fail due to annotation processor error");

        // Verify that the error message is about final fields
        boolean foundFinalFieldError = false;
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                String message = diagnostic.getMessage(null);
                if (message.contains("@AnimatedProperty cannot be applied to final fields")) {
                    foundFinalFieldError = true;
                    break;
                }
            }
        }

        assertTrue(foundFinalFieldError, 
            "Expected to find error about @AnimatedProperty on final fields");

        // Clean up
        fileManager.close();
        Files.deleteIfExists(sourceFile);
        Files.deleteIfExists(tempDir);
    }

    /**
     * Test that the processor allows {@code @AnimatedProperty} on non-final fields.
     */
    @Test
    void processorAllowsNonFinalField() throws IOException {
        // Create a temporary Java source file with a non-final field annotated with @AnimatedProperty
        String sourceCode = """
            package test;
            import com.github.amatheo.timelinefx.annotation.AnimatedProperty;
            
            public class TestEffect {
                @AnimatedProperty
                private String testField = "test";
            }
            """;

        // Get the Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "Java compiler must be available");

        // Create a diagnostic collector to capture compilation errors
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);

        // Create a temporary directory for the source file
        Path tempDir = Files.createTempDirectory("annotation-processor-test");
        Path sourceFile = tempDir.resolve("TestEffect.java");
        Files.writeString(sourceFile, sourceCode);

        // Get the source file object
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());

        // Set up compiler options - include the processor on the classpath
        List<String> options = Arrays.asList(
            "-proc:only",  // Only run annotation processing
            "-processor", "com.github.amatheo.timelinefx.annotation.AnimatedPropertyProcessor",
            "-classpath", System.getProperty("java.class.path")
        );

        // Compile the source file
        JavaCompiler.CompilationTask task = compiler.getTask(
            null,
            fileManager,
            diagnostics,
            options,
            null,
            compilationUnits
        );

        // Execute the compilation
        Boolean success = task.call();

        // Verify that annotation processing succeeded (no errors from our processor)
        // Note: There might be other compilation errors, but we only care about processor errors
        boolean foundFinalFieldError = false;
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                String message = diagnostic.getMessage(null);
                if (message.contains("@AnimatedProperty cannot be applied to final fields")) {
                    foundFinalFieldError = true;
                    break;
                }
            }
        }

        assertFalse(foundFinalFieldError, 
            "Should not find error about @AnimatedProperty on non-final fields");

        // Clean up
        fileManager.close();
        Files.deleteIfExists(sourceFile);
        Files.deleteIfExists(tempDir);
    }
}
