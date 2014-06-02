package com.dieend.adin.aphrodite.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class SplitTestAspectGeneratorPlugin implements Plugin<Project>{
	@Override
	def void apply(Project project) {
		project.task('generate', type: GenerateAspectSourceTask)
		
	}
}
