package com.dieend.adin.aphrodite.processor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.dieend.adin.aphroditegenerator.AspectFileGenerator;
import com.dieend.adin.hermes.annotations.ParameterSplitTest;
import com.dieend.adin.hermes.annotations.RecordAfterWith;
import com.dieend.adin.hermes.annotations.RecordBeforeWith;
import com.dieend.adin.hermes.annotations.SimpleSplitTest;
import com.dieend.adin.hermes.annotations.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


@SupportedAnnotationTypes({
	"com.dieend.adin.hermes.annotations.SimpleSplitTest",
	"com.dieend.adin.hermes.annotations.ParameterSplitTest",
	"com.dieend.adin.hermes.annotations.RecordBeforeWith",
	"com.dieend.adin.hermes.annotations.RecordAfterWith"}
)
public class AnnotationProcessor extends AbstractProcessor{
	private static final String SIMPLE_SPLIT_TEST = "SimpleSplitTest";
	private static final String PARAMETER_SPLIT_TEST = "ParameterSplitTest";
	private static final String RECORD_BEFORE_TRACKER = "RecordBeforeTracker";
	private static final String RECORD_AFTER_TRACKER = "RecordAfterTracker";
	private static final String ASPECT_PACKAGE = "com.dieend.adin.aspect.generated";
	private static final String CONFIGURATION_FILE_NAME = "SplitTestConfiguration.json";
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, "Processing with ADINProcessor");
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		ArrayNode configurationJson = om.createArrayNode();
		try {
			Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SimpleSplitTest.class);
			boolean any = false;			
	        
			ObjectNode node = configurationJson.addObject();
			node.put("name", SimpleSplitTest.class.getName());
	        if (elements.size() > 0) {
		        // TODO validation
	        	generatesSimpleSplitTest(elements, node.putArray("content"));
	        	any = true;
	        }


	        elements = roundEnv.getElementsAnnotatedWith(ParameterSplitTest.class);
	        node = configurationJson.addObject();
	        node.put("name", ParameterSplitTest.class.getName());
	        if (elements.size() > 0) {
	        	// TODO validation link: 
	        	generatesParameterSplitTest(elements, node.putArray("content"));
	        	any = true;
	        }
	        
	        elements = roundEnv.getElementsAnnotatedWith(RecordBeforeWith.class);
	        if (elements.size() > 0) {
	        	generatesBeforeTracker(elements);
	        	any = true;
	        }
	        
	        elements = roundEnv.getElementsAnnotatedWith(RecordAfterWith.class);
	        if (elements.size() > 0) {
	        	generatesAfterTracker(elements);
	        	any = true;
	        }
	        
	        if (!any) return false;
	        // TODO put configuration file name in gradle properties, see file:///C:/Program1/gradle-1.12/docs/userguide/tutorial_this_and_that.html
	        File file = new File(System.getProperty("user.dir")+ File.separatorChar + CONFIGURATION_FILE_NAME);
	        
	        om.writeValue(file, configurationJson);
	        FileObject jfo = processingEnv.getFiler().createResource(
	        		StandardLocation.SOURCE_OUTPUT, 
	        		ASPECT_PACKAGE, 
	        		SIMPLE_SPLIT_TEST + "Aspect.java" , 
	        		(Element)null
	        );
	        Writer writer = jfo.openWriter();
	        processingEnv.getMessager().printMessage(
	                Diagnostic.Kind.NOTE,
	                "creating source file: " + jfo.toUri());	        
	        new AspectFileGenerator(file.getAbsolutePath()).generate(writer);
	        writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return true;
	}
	 
	private void generatesSimpleSplitTest(Set<? extends Element> elements, ArrayNode configuration) throws IOException {
        for (Element element : elements) {
	        SimpleSplitTest annotation = element.getAnnotation(SimpleSplitTest.class);
	        ExecutableElement method = (ExecutableElement) element;
	        TypeElement clazz = (TypeElement) method.getEnclosingElement();
	        PackageElement pkg = (PackageElement) clazz.getEnclosingElement();
	        
	        ObjectNode node = configuration.addObject();
	        node.put("experimentName", annotation.experimentName());
	        node.put("package",pkg.getQualifiedName().toString());
	        node.put("class", clazz.getSimpleName().toString());
	        ObjectNode methodPair = node.putObject("methodPair");
	        ObjectNode mainMethod = methodPair.putObject("mainMethod");
	        mainMethod.put("name", method.getSimpleName().toString());
	        mainMethod.put("returnType", method.getReturnType().toString());
	        ArrayNode methodParams = mainMethod.putArray("params");
	        for (VariableElement param : method.getParameters()) {
	        	ObjectNode data = methodParams.addObject();
	        	data.put("type", param.asType().toString());
	        	data.put("name", param.getSimpleName().toString());
	        	if (Arrays.asList(annotation.variableIdentifier()).contains(param.getSimpleName().toString())) {
	        		data.put("modifiable", true);
	        		data.putNull("value");
	        	} else {
	        		data.put("modifiable", false);
	        	}
	        }
	        ArrayNode alternates = methodPair.putArray("alternateMethods");
	        int id = 1;
	        for (String alternate: annotation.method()) {
	        	ObjectNode data = alternates.addObject();
		        data.put("name", alternate);
		        data.put("bucket", id++);
		        ArrayNode methodValue = data.putArray("params");
		        for (String param : annotation.variableIdentifier()) {
		        	ObjectNode pr = methodValue.addObject();
		        	pr.put("name", param);
		        	pr.putNull("value");
		        }
	        }
	    }
	}
	private void generatesParameterSplitTest(Set<? extends Element> elements, ArrayNode configuration) throws IOException {
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
	        ExecutableElement method =(ExecutableElement) element;
	        TypeElement clazz = (TypeElement) method.getEnclosingElement();
	        PackageElement pkg = (PackageElement) clazz.getEnclosingElement();
	        concernClasses.add(clazz);
	        concernMethods.add(method);
	        returnTypes.add(method.getReturnType().toString());
	        parameterIdentifiers.add(annotation.variableIdentifier());
	        
	        
	        ObjectNode node = configuration.addObject();
	        node.put("experimentName", annotation.experimentName());
	        node.put("package",pkg.getQualifiedName().toString());
	        node.put("class", clazz.getSimpleName().toString());
	        ObjectNode mainMethod = node.putObject("method");
	        mainMethod.put("name", method.getSimpleName().toString());
	        mainMethod.put("returnType", method.getReturnType().toString());
	        ArrayNode methodParams = mainMethod.putArray("params");
	        for (VariableElement param : method.getParameters()) {
	        	ObjectNode data = methodParams.addObject();
	        	data.put("type", param.asType().toString());
	        	data.put("name", param.getSimpleName().toString());
	        	if (Arrays.asList(annotation.variableIdentifier()).contains(param.getSimpleName().toString())) {
	        		data.put("modifiable", true);
	        		data.putNull("value");
	        	} else {
	        		data.put("modifiable", false);
	        	}
	        }
	        ArrayNode alternates = mainMethod.putArray("parameterVarian");
	        for (int i = 0; i<annotation.variantSize(); i++) {
	        	ObjectNode data = alternates.addObject();
		        ArrayNode methodValue = data.putArray("params");
		        for (String param : annotation.variableIdentifier()) {
		        	ObjectNode pr = methodValue.addObject();
		        	pr.put("name", param);
		        	pr.putNull("value");
		        }
	        }
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
			e.printStackTrace();
		}
	    return null; // can this ever happen ??
	}
	private TypeElement asTypeElement(TypeMirror typeMirror) {
	    Types TypeUtils = this.processingEnv.getTypeUtils();
	    return (TypeElement)TypeUtils.asElement(typeMirror);
	}

}
