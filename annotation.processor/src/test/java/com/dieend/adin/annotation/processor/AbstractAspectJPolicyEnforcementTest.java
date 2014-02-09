package com.dieend.adin.annotation.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

import org.aspectj.tools.ajc.Main;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public abstract class AbstractAspectJPolicyEnforcementTest {
	protected Matcher<File> producesCompilationErrorWith(final File aspectFile) {
	    return new AspectJCompilationMatcher(aspectFile, Result.ERROR);
	}

	private class AspectJCompilationMatcher extends TypeSafeMatcher<File> {
	    private final File aspectFile;
	    private final Result expectedResult;
	    private Result result;

	    public AspectJCompilationMatcher(final File aspectFile, final Result expectedResult) {
	        this.aspectFile = aspectFile;
	        this.expectedResult = expectedResult;
	    }

	    @Override
		public boolean matchesSafely(final File javaSourceFile) {
	    	List<String> info = new ArrayList<String>();
	        result = compile(javaSourceFile, aspectFile, info);
	        return result == expectedResult;
	    }

	    @Override
	    public void describeTo(final Description description) {
	        description.appendText("compilation result: ").appendValue(result);
	    }
	}

	enum Result {
	    ERROR,
	    WARNING,
	    SUCCESS
	}
	public void assertExists(File java) {
		if (!java.exists()) throw new AssertionFailedError(java.getAbsolutePath() + " doesn't exist");
	}
	protected Result compile(final File javaFileName, final File aspectFile, List<String> infos) {

	    assertExists(javaFileName);
	    assertExists(aspectFile);

	    List<String> argList = new ArrayList<String>();

	    // java 7 compatibility
	    argList.add("-source");
	    argList.add("1.7");
	    argList.add("-target");
	    argList.add("1.7");

	    // set class path
	    argList.add("-cp");
	    argList.add(System.getProperty("java.class.path"));

	    // add java file
	    argList.add(javaFileName.getAbsolutePath());

	    // add aspect files
	    argList.add(aspectFile.getAbsolutePath());
//	    for (File additionalAspectFile : requiredAspects) {
//	        assertExists(additionalAspectFile);
//	        argList.add(additionalAspectFile.getAbsolutePath());
//	    }

	    String[] args = argList.toArray(new String[argList.size()]);
	    List<String> fails = new ArrayList<String>();
	    List<String> errors = new ArrayList<String>();
	    List<String> warnings = new ArrayList<String>();

	    // org.aspectj.tools.ajc.Main;
	    Main.bareMain(args, false, fails, errors, warnings, infos);
	    infos.addAll(fails);
	    infos.addAll(errors);
	    infos.addAll(warnings);
	    if (!fails.isEmpty() || !errors.isEmpty()) {
	        return Result.ERROR;
	    } else {
	        return Result.SUCCESS;
	    }
	}
}
