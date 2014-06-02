package com.dieend.adin.aphrodite.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

class GenerateAspectSourceTask extends DefaultTask {
	@InputFile
	def File confSource

	@TaskAction
	def execute(IncrementalTaskInputs inputs) {
		if (confSource == null) {
			
		}
	}
}