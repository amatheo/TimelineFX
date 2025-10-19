package com.github.amatheo.timelinefx.annotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Validates {@link AnimatedProperty} usage at compile-time.
 * Ensures the annotation is not applied to final fields.
 */
@SupportedAnnotationTypes("com.github.amatheo.timelinefx.annotation.AnimatedProperty")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class AnimatedPropertyProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AnimatedProperty.class)) {
            if (element.getKind() != ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@AnimatedProperty can only be applied to fields",
                    element
                );
                continue;
            }
            
            if (element.getModifiers().contains(Modifier.FINAL)) {
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "@AnimatedProperty cannot be applied to final fields. " +
                    "Remove 'final' or the annotation.",
                    element
                );
            }
        }
        
        return false;
    }
}
