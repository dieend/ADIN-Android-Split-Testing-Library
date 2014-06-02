package com.dieend.adin.aphrodite.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.tooling.BuildException
import org.gradle.api.file.FileTree

/**
 *
 * @author Luke Taylor
 * @author Mike Noordermeer
 */
class AspectJAndroidPlugin implements Plugin<Project> {
	private static final String[] GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS = [
		'0.7.0',
		'0.7.1',
		'0.7.2',
		'0.7.3',
		'0.8.0',
		'0.8.1',
		'0.8.2',
		'0.8.3',
		'0.9.0',
		'0.9.1',
		'0.9.2',
		'0.10.0'
	]
	void apply(Project project) {

		if (!project.hasProperty('aspectjVersion')) {
			throw new GradleException("You must set the property 'aspectjVersion' before applying the aspectj plugin")
		}
		if (project.configurations.findByName('ajtools') == null) {
			project.configurations.create('ajtools')
			if (project.configurations.findByName('compile') == null) {
				project.configurations.create('compile')
			}
			project.dependencies {
				ajtools "org.aspectj:aspectjtools:${project.aspectjVersion}"
				compile "org.aspectj:aspectjrt:${project.aspectjVersion}"
			}
			
		}

		for(configuration in [
			'aspects',
			'ajinpath',
			'otherAspect'
		]) {
			if (project.configurations.findByName(configuration) == null) {
				project.configurations.create(configuration)
			}
		}
		project.afterEvaluate {
			if (isAndroidProject(project)) {
				applyToAndroidProject(project)
			} else  {
				throw new BuildException('The project isn\'t an android project', null)
			}
		}
	}

	def applyToAndroidProject(Project project) {
		checkGradleAndroidPlugin(project)
		println '---------AAAAAAAAAAAAAAAAAAAAAAA--------------------'
		def androidExtension
		def variants
		if (project.plugins.hasPlugin('android')) {
			androidExtension = project.plugins.getPlugin('android').extension
			variants = androidExtension.applicationVariants
		} else if (project.plugins.hasPlugin('android-library')) {
			androidExtension = project.plugins.getPlugin('android-library').extension
			variants = androidExtension.libraryVariants
		}

		println "main source:"
		def androidSdk = androidExtension.adbExe.parent + "/../platforms/" + androidExtension.compileSdkVersion + "/android.jar"
		
		variants.all { variant ->
			project.tasks.create(name: 'compileAspect' + variant.name.capitalize(), overwrite: true, description: 'Compiles AspectJ Source', type: Ajc,
				dependsOn:'compile'+variant.name.capitalize() + 'Java') {
				androidSdkPath = androidSdk
				inputs.files(javaCompile.source)
				outputs.dir(javaCompile.destinationDir.path)
				sourceRoot = javaCompile.source
				destDir= javaCompile.destinationDir
			}
				
			project.tasks['preDex' + variant.name.capitalize()].dependsOn(project.tasks['compileAspect' + variant.name.capitalize()])
			println name
			println 'compile' + variant.name.capitalize()
			println 'destDir: '+ javaCompile.destinationDir.path
			println 'inpath:' + project.configurations.ajinpath.asPath
			println 'aspects: ' + project.configurations.aspects.asPath
		}
	}
	def sourceSetName(variant) {
		variant.dirName.split('/').last()
	}

	def isAndroidProject(project) {
		hasAndroidPlugin(project) || hasAndroidLibraryPlugin(project)
	}

	def hasAndroidPlugin(project) {
		project.plugins.hasPlugin('android')
	}

	def hasAndroidLibraryPlugin(project) {
		project.plugins.hasPlugin('android-library')
	}
	def checkGradleAndroidPlugin(project) {
		// as in: http://stackoverflow.com/a/18119304/389262
		def gradleAndroidPluginVersion = project.buildscript.configurations.classpath.resolvedConfiguration.firstLevelModuleDependencies.find { plugin ->
			plugin.moduleGroup == 'com.android.tools.build'
		}.moduleVersion
		if (!(gradleAndroidPluginVersion in GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS)) {
			throw new BuildException("Android Gradle plugin version for the current project is not supported [" + gradleAndroidPluginVersion + "]. Supported versions are: "
			+ GRADLE_ANDROID_PLUGIN_SUPPORTED_VERSIONS.join(' ').trim(), null);
		}
	}
	def getAptOutputDir(project) {
		def aptOutputDirName = project.apt.outputDirName
		if (!aptOutputDirName) {
			aptOutputDirName = 'build/source/apt'
		}
		project.file aptOutputDirName
	}
}

class Ajc extends DefaultTask {

	FileCollection sourceRoot
	def destDir
	def androidSdkPath
	String iajcClassPath

	// ignore or warning
	String xlint = 'ignore'

	String maxmem

	Ajc() {
		logging.captureStandardOutput(LogLevel.INFO)
	}

	@TaskAction
	def compile() {
		logger.info("="*30)
		logger.info("="*30)
		logger.info("Running ajc ...")
		//		logger.info("classpath: ${sourceSet.compileClasspath.asPath}")
		//		logger.info("srcDirs $sourceSet.java.srcDirs")

		iajcClassPath = project.configurations.compile.asPath + ";" + androidSdkPath
		def tree = project.fileTree(dir: "${project.buildDir}/exploded-aar", include: '**/classes.jar')
		tree.each { jarFile ->
			iajcClassPath += ";" + jarFile
		}
		
		println 'searching Path: ' + iajcClassPath
		if (maxmem == null) {
			maxmem = "512m"
		}
		def inpath = project.configurations.ajinpath.asPath;
		ant.taskdef(resource: "org/aspectj/tools/ant/taskdefs/aspectjTaskdefs.properties", classpath: project.configurations.ajtools.asPath)
		try {
			ant.iajc(
				 source:project.convention.plugins.java.sourceCompatibility,,
	             target:project.convention.plugins.java.targetCompatibility,
	             destDir:destDir,
	             maxmem:maxmem,
	             aspectPath:project.configurations.aspects.asPath,
	             inpath:inpath,
	             sourceRootCopyFilter:"**/.svn/*,**/*.java",
	             classpath:iajcClassPath,
				 showWeaveInfo: true,
				 verbose: true,
				 failonerror:true,
				 log:"log_aspect.txt") {
				sourceroots {
					sourceRoot.files.each  {
						pathelement(location: it.parent)
					}
					project.configurations.otherAspect.each {
						pathelement(location: it.parent)
					}
				}
			}
		} catch (org.apache.tools.ant.BuildException e) {
			throw new TaskExecutionException(this, e)
		}
	}
}