package com.dieend.adin.annotation.processor;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.dieend.adin.annotation.ADINSimpleAlternateWith;


@SupportedAnnotationTypes({"com.dieend.adin.annotation.ADINSimpleAlternateWith"})
public class ADINAnnotationProcessor extends AbstractProcessor{
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, "Processing with ADINProcessor");
		try {
			Properties props = new Properties();
			// TODO check validity
			URL url = this.getClass().getClassLoader().getResource("velocity.properties");
			props.load(url.openStream());
			Velocity.init(props);
	        VelocityContext vc = new VelocityContext();
	        Template template = Velocity.getTemplate("SimpleAlternateMethod");
	        
	        List<TypeElement> concernClasses = new ArrayList<TypeElement>();
	        List<ExecutableElement> concernMethods = new ArrayList<ExecutableElement>();
	        List<String> alternateMethods = new ArrayList<String>();
	        List<String> returnTypes = new ArrayList<String>();
	        List<String> experimentNames = new ArrayList<String>();
	        boolean any = false;
	        
	        for (Element element : roundEnv.getElementsAnnotatedWith(ADINSimpleAlternateWith.class)) {
	        	any = true;
		        ADINSimpleAlternateWith annotation = element.getAnnotation(ADINSimpleAlternateWith.class);
		        experimentNames.add(annotation.experimentName());
		        concernClasses.add(((TypeElement)((ExecutableElement)element).getEnclosingElement()));
		        concernMethods.add((ExecutableElement) element);
		        alternateMethods.add(annotation.method());
		        returnTypes.add(((ExecutableElement) element).getReturnType().toString());
		    }
	        if (!any) return false;
	        vc.put("concerns", concernClasses);
	        vc.put("methods", concernMethods );
	        vc.put("alternateMethods", alternateMethods);
	        vc.put("returnTypes", returnTypes);
	        vc.put("experimentNames", experimentNames);
	        
	        FileObject jfo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "com.dieend.adin.generated.aspect", "SplittingAspect.java" , concernClasses.toArray(new Element[0]));
	        Writer writer = jfo.openWriter();
	        processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "creating source file: " + jfo.toUri());
            processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "applying velocity template: " + template.getName());
            template.merge(vc, writer);
            writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
	private static <T> TypeMirror getTypeMirror(T annotation, String value) {
	    try
	    {
	        annotation.getClass().getMethod(value).invoke(annotation);
	    }
	    catch( MirroredTypeException mte )
	    {
	        return mte.getTypeMirror();
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null; // can this ever happen ??
	}
	private TypeElement asTypeElement(TypeMirror typeMirror) {
	    Types TypeUtils = this.processingEnv.getTypeUtils();
	    return (TypeElement)TypeUtils.asElement(typeMirror);
	}
 
}
