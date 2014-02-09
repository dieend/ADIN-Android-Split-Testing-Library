package com.dieend.adin.annotation.processor;
import java.io.IOException;
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

import com.dieend.adin.annotation.ParameterSplitTest;
import com.dieend.adin.annotation.RecordAfterWith;
import com.dieend.adin.annotation.RecordBeforeWith;
import com.dieend.adin.annotation.SimpleSplitTest;
import com.dieend.adin.annotation.Type;


@SupportedAnnotationTypes({
	"com.dieend.adin.annotation.SimpleSplitTest",
	"com.dieend.adin.annotation.ParameterSplitTest",
	"com.dieend.adin.annotation.RecordBeforeWith",
	"com.dieend.adin.annotation.RecordAfterWith"}
)
public class ADINAnnotationProcessor extends AbstractProcessor{
	private static final String SIMPLE_SPLIT_TEST = "SimpleSplitTest";
	private static final String PARAMETER_SPLIT_TEST = "ParameterSplitTest";
	private static final String RECORD_BEFORE_TRACKER = "RecordBeforeTracker";
	private static final String RECORD_AFTER_TRACKER = "RecordAfterTracker";
	private static final String ASPECT_PACKAGE = "com.dieend.adin.generated.aspect";
	
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, "Processing with ADINProcessor");
		try {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SimpleSplitTest.class);
			boolean any = false;
	        if (elements.size() > 0) {
	        	any = true;
	        	generatesSimpleSplitTest(elements);
	        }
	        
	        elements = roundEnv.getElementsAnnotatedWith(ParameterSplitTest.class);
	        if (elements.size() > 0) {
	        	any = true;
	        	generatesParameterSplitTest(elements);
	        }
	        
	        elements = roundEnv.getElementsAnnotatedWith(RecordBeforeWith.class);
	        if (elements.size() > 0) {
	        	any = true;
	        	generatesBeforeTracker(elements);
	        }
	        
	        elements = roundEnv.getElementsAnnotatedWith(RecordAfterWith.class);
	        if (elements.size() > 0) {
	        	any = true;
	        	generatesAfterTracker(elements);
	        }
	        
	        if (!any) return false;
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}
	 
	private void generatesSimpleSplitTest(Set<? extends Element> elements) throws IOException {
		Properties props = new Properties();
		URL url = this.getClass().getClassLoader().getResource("velocity.properties");
		props.load(url.openStream());
		Velocity.init(props);
        VelocityContext vc = new VelocityContext();
        Template template = Velocity.getTemplate(SIMPLE_SPLIT_TEST + "Template");
        
        List<TypeElement> concernClasses = new ArrayList<TypeElement>();
        List<ExecutableElement> concernMethods = new ArrayList<ExecutableElement>();
        List<String> alternateMethods = new ArrayList<String>();
        List<String> returnTypes = new ArrayList<String>();
        List<String> experimentNames = new ArrayList<String>();

        for (Element element : elements) {
	        SimpleSplitTest annotation = element.getAnnotation(SimpleSplitTest.class);
	        experimentNames.add(annotation.experimentName());
	        concernClasses.add(((TypeElement)((ExecutableElement)element).getEnclosingElement()));
	        concernMethods.add((ExecutableElement) element);
	        alternateMethods.add(annotation.method());
	        returnTypes.add(((ExecutableElement) element).getReturnType().toString());
	    }
        
        
        vc.put("concerns", concernClasses);
        vc.put("methods", concernMethods );
        vc.put("alternateMethods", alternateMethods);
        vc.put("returnTypes", returnTypes);
        vc.put("experimentNames", experimentNames);
        
        FileObject jfo = processingEnv.getFiler().createResource(
        		StandardLocation.SOURCE_OUTPUT, 
        		ASPECT_PACKAGE, 
        		SIMPLE_SPLIT_TEST + "Aspect.java" , 
        		concernClasses.toArray(new Element[0])
        );
        Writer writer = jfo.openWriter();
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "applying velocity template: " + template.getName());
        template.merge(vc, writer);
        writer.close();
	}
	private void generatesParameterSplitTest(Set<? extends Element> elements) throws IOException {
		Properties props = new Properties();
		URL url = this.getClass().getClassLoader().getResource("velocity.properties");
		props.load(url.openStream());
		Velocity.init(props);
        VelocityContext vc = new VelocityContext();
        Template template = Velocity.getTemplate(PARAMETER_SPLIT_TEST + "Template");
        
        List<TypeElement> concernClasses = new ArrayList<TypeElement>();
        List<ExecutableElement> concernMethods = new ArrayList<ExecutableElement>();
        List<String> returnTypes = new ArrayList<String>();
        List<String> experimentNames = new ArrayList<String>();
        List<String[]> parameterIdentifiers = new ArrayList<String[]>();

        for (Element element : elements) {
        	ParameterSplitTest annotation = element.getAnnotation(ParameterSplitTest.class);
	        experimentNames.add(annotation.experimentName());
	        concernClasses.add(((TypeElement)((ExecutableElement)element).getEnclosingElement()));
	        concernMethods.add((ExecutableElement) element);
	        returnTypes.add(((ExecutableElement) element).getReturnType().toString());
	        parameterIdentifiers.add(annotation.variableIdentifier());
	    }
        
        
        vc.put("concerns", concernClasses);
        vc.put("methods", concernMethods );
        vc.put("parameterIdentifiers", parameterIdentifiers);
        vc.put("returnTypes", returnTypes);
        vc.put("experimentNames", experimentNames);
        
        FileObject jfo = processingEnv.getFiler().createResource(
        		StandardLocation.SOURCE_OUTPUT, 
        		ASPECT_PACKAGE, 
        		PARAMETER_SPLIT_TEST + "Aspect.java" , 
        		concernClasses.toArray(new Element[0])
        );
        Writer writer = jfo.openWriter();
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "applying velocity template: " + template.getName());
        template.merge(vc, writer);
        writer.close();
	}
	
	private void generatesBeforeTracker(Set<? extends Element> elements) throws IOException{
		Properties props = new Properties();
		URL url = this.getClass().getClassLoader().getResource("velocity.properties");
		props.load(url.openStream());
		Velocity.init(props);
        VelocityContext vc = new VelocityContext();
        Template template = Velocity.getTemplate(RECORD_BEFORE_TRACKER + "Template");
        List<TypeElement> concernClasses = new ArrayList<TypeElement>();
        List<ExecutableElement> concernMethods = new ArrayList<ExecutableElement>();
        List<String> returnTypes = new ArrayList<String>();
        List<String> eventNames = new ArrayList<String>();
        List<Type> types = new ArrayList<Type>();

        for (Element element : elements) {
        	RecordBeforeWith annotation = element.getAnnotation(RecordBeforeWith.class);
	        eventNames.add(annotation.eventName());
	        concernClasses.add(((TypeElement)((ExecutableElement)element).getEnclosingElement()));
	        concernMethods.add((ExecutableElement) element);
	        returnTypes.add(((ExecutableElement) element).getReturnType().toString());
	        types.add(annotation.type());
	    }


        vc.put("concerns", concernClasses);
        vc.put("methods", concernMethods );
        vc.put("returnTypes", returnTypes);
        vc.put("eventNames", eventNames);
        vc.put("timeds", types);
        FileObject jfo = processingEnv.getFiler().createResource(
        		StandardLocation.SOURCE_OUTPUT, 
        		ASPECT_PACKAGE, 
        		RECORD_BEFORE_TRACKER  + "Aspect.java" , 
        		concernClasses.toArray(new Element[0])
        );
        Writer writer = jfo.openWriter();
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "applying velocity template: " + template.getName());
        template.merge(vc, writer);
        writer.close();
	}
	private void generatesAfterTracker(Set<? extends Element> elements) throws IOException{
		Properties props = new Properties();
		URL url = this.getClass().getClassLoader().getResource("velocity.properties");
		props.load(url.openStream());
		Velocity.init(props);
        VelocityContext vc = new VelocityContext();
        Template template = Velocity.getTemplate(RECORD_AFTER_TRACKER + "Template");
        List<TypeElement> concernClasses = new ArrayList<TypeElement>();
        List<ExecutableElement> concernMethods = new ArrayList<ExecutableElement>();
        List<String> returnTypes = new ArrayList<String>();
        List<String> eventNames = new ArrayList<String>();
        List<Type> types = new ArrayList<Type>();

        for (Element element : elements) {
        	RecordAfterWith annotation = element.getAnnotation(RecordAfterWith.class);
	        eventNames.add(annotation.eventName());
	        concernClasses.add(((TypeElement)((ExecutableElement)element).getEnclosingElement()));
	        concernMethods.add((ExecutableElement) element);
	        returnTypes.add(((ExecutableElement) element).getReturnType().toString());
	        types.add(annotation.type());
	    }


        vc.put("concerns", concernClasses);
        vc.put("methods", concernMethods );
        vc.put("returnTypes", returnTypes);
        vc.put("eventNames", eventNames);
        vc.put("timeds", types);
        FileObject jfo = processingEnv.getFiler().createResource(
        		StandardLocation.SOURCE_OUTPUT, 
        		ASPECT_PACKAGE, 
        		RECORD_AFTER_TRACKER  + "Aspect.java" , 
        		concernClasses.toArray(new Element[0])
        );
        Writer writer = jfo.openWriter();
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "creating source file: " + jfo.toUri());
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.NOTE,
            "applying velocity template: " + template.getName());
        template.merge(vc, writer);
        writer.close();
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
