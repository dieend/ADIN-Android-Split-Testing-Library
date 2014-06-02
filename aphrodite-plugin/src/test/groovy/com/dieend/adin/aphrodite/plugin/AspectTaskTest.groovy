package com.dieend.adin.aphrodite.plugin

import static org.junit.Assert.*;

import org.junit.Test;
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project

class AspectTaskTest {
	@Test
	void testName() {
		Project project = ProjectBuilder.builder().withProjectDir(new File("C:\\Users\\dieend\\workspace\\ADIN\\aphrodite-plugin\\src\\test\\resource\\sample-project")).build()
		project.ext.aspectjVersion = "1.7.4"
		
		//assertTrue(project.tasks.findByPath('assemble') != null)
		project.apply plugin: 'aspectj-android'
	}
	
}
