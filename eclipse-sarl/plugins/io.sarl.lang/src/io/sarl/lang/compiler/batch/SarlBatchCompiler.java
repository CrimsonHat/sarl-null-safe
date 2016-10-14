/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2016 the original authors or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sarl.lang.compiler.batch;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.inject.Provider;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.xtend.core.macro.ProcessorInstanceForJvmTypeProvider;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.common.types.access.impl.ClasspathTypeProvider;
import org.eclipse.xtext.common.types.access.impl.IndexedJvmTypeAccess;
import org.eclipse.xtext.common.types.descriptions.IStubGenerator;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.GeneratorDelegate;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.xtext.generator.OutputConfigurationAdapter;
import org.eclipse.xtext.mwe.NameBasedFilter;
import org.eclipse.xtext.mwe.PathTraverser;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.resource.CompilerPhases;
import org.eclipse.xtext.resource.FileExtensionProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.persistence.StorageAwareResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.Files;
import org.eclipse.xtext.util.JavaVersion;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.util.UriUtil;
import org.eclipse.xtext.util.internal.AlternateJdkLoader;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.workspace.FileProjectConfig;
import org.eclipse.xtext.workspace.ProjectConfigAdapter;
import org.eclipse.xtext.xbase.compiler.GeneratorConfig;
import org.eclipse.xtext.xbase.compiler.GeneratorConfigProvider;
import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.resource.BatchLinkableResource;

import io.sarl.lang.SARLConfig;
import io.sarl.lang.generator.GeneratorConfig2;
import io.sarl.lang.generator.GeneratorConfigProvider2;

/** The compiler from SARL that could be used for batch tasks (Maven, CLI).
 *
 * <p>This compiler is inspired by the Xtend batch compiler.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
@SuppressWarnings({"checkstyle:classfanoutcomplexity", "checkstyle:methodcount", "checkstyle:classdataabstractioncoupling"})
public class SarlBatchCompiler {

	private static final String BINCLASS_FOLDER_PREFIX = "classes"; //$NON-NLS-1$

	private static final String STUB_FOLDER_PREFIX = "stubs"; //$NON-NLS-1$

	private static final FileFilter ACCEPT_ALL_FILTER = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return true;
		}
	};

	/** The provider of resource sets.
	 */
	protected Provider<ResourceSet> resourceSetProvider;

	private File outputPath;

	private File classOutputPath;

	private File tempPath;

	private boolean deleteTempPath = true;

	private List<File> bootClasspath;

	private List<File> classpath;

	private String encoding;

	private boolean writeTraceFiles = true;

	private boolean writeStorageFiles;

	private boolean verbose;

	private boolean enableJavaPostCompilation;

	private List<File> sourcePath;

	private boolean useCurrentClassLoaderAsParent;

	private org.eclipse.emf.common.util.URI baseUri;

	private FileProjectConfig projectConfig;

	private OutputConfiguration outputConfiguration;

	private ClassLoader currentClassLoader;

	private ClassLoader jvmTypesClassLoader;

	private ClassLoader annotationProcessingClassLoader;

	@Inject
	private GeneratorConfigProvider generatorConfigProvider;

	@Inject
	private GeneratorConfigProvider2 generatorConfigProvider2;

	@Inject
	private IOutputConfigurationProvider outputConfigurationProvider;

	@Inject
	private CompilerPhases compilerPhases;

	@Inject
	private Provider<JavaIoFileSystemAccess> javaIoFileSystemAccessProvider;

	@Inject
	private IndexedJvmTypeAccess indexedJvmTypeAccess;

	@Inject
	private IEncodingProvider.Runtime encodingProvider;

	@Inject
	private FileExtensionProvider fileExtensionProvider;

	@Inject
	private IResourceDescription.Manager resourceDescriptionManager;

	@Inject
	private IStubGenerator stubGenerator;

	@Inject
	private GeneratorDelegate generator;

	@Inject
	@Named(Constants.LANGUAGE_NAME)
	private String languageName;

	private final GeneratorConfig generatorConfig = new GeneratorConfig();

	private final GeneratorConfig2 generatorConfig2 = new GeneratorConfig2();

	private Logger log;

	private IssueMessageFormatter messageFormatter;

	private final List<File> tempFolders = new ArrayList<>();

	private Comparator<Issue> issueComparator = new DefaultIssueComparator();

	/** Constructor the batch compiler.
	 */
	public SarlBatchCompiler() {
		this.log = Logger.getLogger(getClass().getName());
	}

	/** Set the comparator of issues that is used for sorting the issues before they are logged.
	 *
	 * @param comparator the comparator; never <code>null</code>.
	 */
	public void setIssueComparator(Comparator<Issue> comparator) {
		if (comparator != null) {
			this.issueComparator = comparator;
		}
	}

	/** Replies the comparator of issues that is used for sorting the issues before they are logged.
	 *
	 * @return the comparator; never <code>null</code>.
	 */
	public Comparator<Issue> getIssueComparator() {
		return this.issueComparator;
	}

	/** Replies if the Java compiler should be invoked after the SARL compiler is invoked.
	 *
	 * @return <code>true</code> if the Java compiler is invoked after the SARL compiler.
	 */
	public boolean isJavaPostCompilationEnable() {
		return this.enableJavaPostCompilation;
	}

	/** Set if the Java compiler should be invoked after the SARL compiler is invoked.
	 *
	 * @param enable <code>true</code> if the Java compiler is invoked after the SARL compiler.
	 */
	public void setJavaPostCompilationEnable(boolean enable) {
		this.enableJavaPostCompilation = enable;
	}

	/** Replies the formatter of the issue messages.
	 *
	 * @return the formatter.
	 */
	public IssueMessageFormatter getIssueMessageFormatter() {
		return this.messageFormatter;
	}

	/** Set the formatter of the issue messages.
	 *
	 * @param formatter the formatter.
	 */
	public void setIssueMessageFormatter(IssueMessageFormatter formatter) {
		this.messageFormatter = formatter;
	}

	/** Replies the logger.
	 *
	 * @return the logger.
	 */
	public Logger getLogger() {
		return this.log;
	}

	/** Set the logger.
	 *
	 * @param logger the logger.
	 */
	public void setLogger(Logger logger) {
		this.log = logger == null ? Logger.getLogger(getClass().getName()) : logger;
	}

	/** Set the provider of resource sets.
	 *
	 * @param resourceSetProvider the provider.
	 */
	@Inject
	public void setResourceSetProvider(Provider<ResourceSet> resourceSetProvider) {
		this.resourceSetProvider = resourceSetProvider;
	}

	private static File normalizeFile(String file) {
		return new File(new File(file).getAbsoluteFile().toURI().normalize());
	}

	/** Replies if the trace files must be generated.
	 *
	 * @return <code>true</code> for generation.
	 */
	public boolean isWriteTraceFiles() {
		return this.writeTraceFiles;
	}

	/** Set if the trace files must be generated.
	 *
	 * @param writeTraceFiles <code>true</code> for generation.
	 */
	public void setWriteTraceFiles(boolean writeTraceFiles) {
		this.writeTraceFiles = writeTraceFiles;
	}

	/** Replies if the storage files must be generated.
	 *
	 * @return <code>true</code> for generation.
	 */
	@Pure
	public boolean isWriteStorageFiles() {
		return this.writeStorageFiles;
	}

	/** Set if the storage files must be generated.
	 *
	 * @param writeStorageFiles <code>true</code> for generation.
	 */
	public void setWriteStorageFiles(boolean writeStorageFiles) {
		this.writeStorageFiles = writeStorageFiles;
	}

	/** Replies if the compiler is verbose.
	 *
	 * @return <code>true</code> if the compiler is verbose.
	 */
	@Pure
	public boolean isJavaCompilerVerbose() {
		return this.verbose;
	}

	/** Set the underlying Java compiler verbosity.
	 *
	 * @param verbose <code>true</code> if the Java compiler is verbose.
	 */
	public void setJavaCompilerVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** Replies the current class loader.
	 *
	 * @return the class loader.
	 */
	@Pure
	public ClassLoader getCurrentClassLoader() {
		if (this.currentClassLoader == null) {
			this.currentClassLoader = getClass().getClassLoader();
		}
		return this.currentClassLoader;
	}

	/** Set the current class loader.
	 *
	 * @param loader the new current class loader.
	 */
	public void setCurrentClassLoader(ClassLoader loader) {
		this.currentClassLoader = null;
	}

	/** Set if the class loaderr of this batch compiler must be used as sthe parent class loader.
	 *
	 * @param useCurrentClassLoaderAsParent <code>true</code> for using the class loader of this batch compiler.
	 */
	public void setUseCurrentClassLoaderAsParent(boolean useCurrentClassLoaderAsParent) {
		this.useCurrentClassLoaderAsParent = useCurrentClassLoaderAsParent;
	}

	/** Replies if the class loaderr of this batch compiler must be used as sthe parent class loader.
	 *
	 * @return <code>true</code> for using the class loader of this batch compiler.
	 */
	@Pure
	public boolean isUseCurrentClassLoaderAsParent() {
		return this.useCurrentClassLoaderAsParent;
	}

	/** Change the base path.
	 *
	 * @param basePath the base path.
	 */
	public void setBasePath(String basePath) {
		setBaseURI(UriUtil.createFolderURI(normalizeFile(basePath)));
	}

	/** Change the base URI.
	 *
	 * @param basePath the base path.
	 */
	public void setBaseURI(org.eclipse.emf.common.util.URI basePath) {
		this.baseUri = basePath;
	}

	/** Change the path where the Java files are generated.
	 *
	 * @param path the path.
	 */
	public void setOutputPath(File path) {
		this.outputPath = path;
	}

	/** Change the path where the Java files are generated.
	 *
	 * @param path the path.
	 */
	public void setOutputPath(String path) {
		setOutputPath(normalizeFile(path));
	}

	/** Replies the path where the Java files are generated.
	 *
	 * @return the path; or <code>null</code> for using the default path in {@link SARLConfig#FOLDER_SOURCE_GENERATED}.
	 */
	@Pure
	public File getOutputPath() {
		return this.outputPath;
	}

	/** Replies the path where the class files are generated.
	 *
	 * @return the path; or <code>null</code> for ignoring the class generation.
	 */
	@Pure
	public File getClassOutputPath() {
		return this.classOutputPath;
	}

	/** Set the path where the class files are generated.
	 *
	 * @param path the path; or <code>null</code> for ignoring the class generation.
	 */
	@Pure
	public void setClassOutputPath(File path) {
		this.classOutputPath = path;
	}

	/** Change the boot classpath.
	 *
	 * <p>The boot classpath is a list the names of folders or jar files that are separated by {@link File#pathSeparator}.
	 *
	 * @param bootClasspath the new boot classpath.
	 */
	public void setBootClassPath(String bootClasspath) {
		this.bootClasspath = new ArrayList<>();
		for (final String path : Strings.split(bootClasspath, Pattern.quote(File.pathSeparator))) {
			this.bootClasspath.add(normalizeFile(path));
		}
	}

	/** Change the boot classpath.
	 *
	 * @param bootClasspath the new boot classpath.
	 */
	public void setBootClassPath(Collection<File> bootClasspath) {
		this.bootClasspath = new ArrayList<>(bootClasspath);
	}

	/** Replies the boot classpath.
	 *@Inject
	private IResourceDescription.Manager resourceDescriptionManager;
	 * @return the boot classpath.
	 */
	@Pure
	public List<File> getBootClassPath() {
		if (this.bootClasspath == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(this.bootClasspath);
	}

	/** Change the classpath.
	 *
	 * <p>The classpath is a list the names of folders or jar files that are separated by {@link File#pathSeparator}.
	 *
	 * @param classpath the new classpath.
	 */
	public void setClassPath(String classpath) {
		this.classpath = new ArrayList<>();
		for (final String path : Strings.split(classpath, Pattern.quote(File.pathSeparator))) {
			this.classpath.add(normalizeFile(path));
		}
	}

	/** Change the classpath.
	 *
	 * @param classpath the new classpath.
	 */
	public void setClassPath(Collection<File> classpath) {
		this.classpath = new ArrayList<>(classpath);
	}

	/** Replies the classpath.
	 *
	 * @return the classpath.
	 */
	@Pure
	public List<File> getClassPath() {
		if (this.classpath == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(this.classpath);
	}

	/** Change the path where the Xtext stubs are generated.
	 *
	 * @param path the path.
	 */
	public void setTempDirectory(File path) {
		this.tempPath = path;
	}

	/** Change the path where the Xtext stubs are generated.
	 *
	 * @param path the path.
	 */
	public void setTempDirectory(String path) {
		setTempDirectory(normalizeFile(path));
	}

	/** Replies the path where the Xtext stubs are generated.
	 *
	 * @return the path; or <code>null</code> for using the default path.
	 */
	@Pure
	public File getTempDirectory() {
		if (this.tempPath == null) {
			return new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		}
		return this.tempPath;
	}

	/** Replies if the temp folder must be deleted at the end of the compilation.
	 *
	 * @return <code>true</code> if the temp folder is deleted.
	 */
	@Pure
	public boolean isDeleteTempDirectory() {
		return this.deleteTempPath;
	}

	/** Set if the temp folder must be deleted at the end of the compilation.
	 *
	 * @param delete <code>true</code> if the temp folder is deleted.
	 */
	public void setDeleteTempDirectory(boolean delete) {
		this.deleteTempPath = delete;
	}

	/** Change the file encoding.
	 *
	 * @param encoding the encoding, usually <code>UTF-8</code>.
	 */
	public void setFileEncoding(String encoding) {
		this.encoding = encoding;
	}

	/** Change the file encoding.
	 *
	 * @return the file encoding, or <code>null</code> if the default encoding must be used.
	 */
	@Pure
	public String getFileEncoding() {
		return this.encoding;
	}

	/** Change the version of the Java source to be used for the generated Java files.
	 *
	 * @param version the Java version.
	 */
	public void setJavaSourceVersion(String version) {
		final JavaVersion javaVersion = JavaVersion.fromQualifier(version);
		if (javaVersion == null) {
			final List<String> qualifiers = new ArrayList<>();
			for (final JavaVersion vers : JavaVersion.values()) {
				qualifiers.add(vers.getQualifier());
			}

			throw new RuntimeException(MessageFormat.format(
					Messages.SarlBatchCompiler_0, version, Joiner.on(Messages.SarlBatchCompiler_1).join(qualifiers)));
		}
		this.generatorConfig.setJavaSourceVersion(javaVersion);
	}

	/** Replies the version of the Java source to be used for the generated Java files.
	 *
	 * @return the Java version.
	 */
	@Pure
	public String getJavaSourceVersion() {
		return this.generatorConfig.getJavaSourceVersion().getQualifier();
	}

	/** Replies the compiler generate the Xbase expressions.
	 *
	 * @return <code>true</code> if the compiler generates the expressions
	 */
	@Pure
	public boolean isGenerateExpressions() {
		return this.generatorConfig.isGenerateExpressions();
	}

	/** Set if the compiler generate the Xbase expressions.
	 *
	 * @param generateExpressions <code>true</code> if the compiler generates the expressions
	 */
	public void setGenerateExpressions(boolean generateExpressions) {
		this.generatorConfig.setGenerateExpressions(generateExpressions);
	}

	/** Replies the <code>@SuppressWarnings</code> is generated.
	 *
	 * @return <code>true</code> if the compiler generates the warning supression annotations.
	 */
	@Pure
	public boolean isGenerateSyntheticSuppressWarnings() {
		return this.generatorConfig.isGenerateSyntheticSuppressWarnings();
	}

	/** Set if the <code>@SuppressWarnings</code> is generated.
	 *
	 * @param generateAnnotations <code>true</code> if the compiler generates the warning supression annotations.
	 */
	public void setGenerateSyntheticSuppressWarnings(boolean generateAnnotations) {
		this.generatorConfig.setGenerateSyntheticSuppressWarnings(generateAnnotations);
	}

	/** Replies the <code>@Generated</code> is generated.
	 *
	 * @return <code>true</code> if the compiler generates the generated annotations.
	 */
	@Pure
	public boolean isGenerateGeneratedAnnotation() {
		return this.generatorConfig.isGenerateGeneratedAnnotation();
	}

	/** Set if the <code>@Generated</code> is generated.
	 *
	 * @param generateAnnotations <code>true</code> if the compiler generates the generated annotations.
	 */
	public void setGenerateGeneratedAnnotation(boolean generateAnnotations) {
		this.generatorConfig.setGenerateGeneratedAnnotation(generateAnnotations);
	}

	/** Replies if the generation date is included in the <code>@Generated</code> annotations.
	 *
	 * @return <code>true</code> if the generation date is added.
	 */
	@Pure
	public boolean isIncludeDateInGeneratedAnnotation() {
		return this.generatorConfig.isIncludeDateInGeneratedAnnotation();
	}

	/** Set if the generation date is included in the <code>@Generated</code> annotations.
	 *
	 * @param includeDateInGeneratedAnnotation <code>true</code> if the generation date is added.
	 */
	public void setIncludeDateInGeneratedAnnotation(boolean includeDateInGeneratedAnnotation) {
		this.generatorConfig.setIncludeDateInGeneratedAnnotation(includeDateInGeneratedAnnotation);
	}

	/** Replies the comment in the <code>@Generated</code> annnotations.
	 *
	 * @return the comment.
	 */
	@Pure
	public String getGeneratedAnnotationComment() {
		return this.generatorConfig.getGeneratedAnnotationComment();
	}

	/** Set the comment in the <code>@Generated</code> annnotations.
	 *
	 * @param comment the comment.
	 */
	public void setGeneratedAnnotationComment(String comment) {
		this.generatorConfig.setGeneratedAnnotationComment(comment);
	}

	/** Replies if the <code>@Inline</code> shall be generated.
	 *
	 * @return <code>true</code> if annotation shall be generated.
	 */
	@Pure
	public boolean isGenerateInlineAnnotation() {
		return this.generatorConfig2.isGenerateInlineAnnotation();
	}

	/** Set if the <code>@Inline</code> shall be generated.
	 *
	 * @param generateInlineAnnotation <code>true</code> if annotation shall be generated.
	 */
	public void setGenerateInlineAnnotation(final boolean generateInlineAnnotation) {
		this.generatorConfig2.setGenerateInlineAnnotation(generateInlineAnnotation);
	}

	/** Replies if constant expression interpreter shall be called for generated <code>@Inline</code>.
	 *
	 * @return <code>true</code> if annotation shall be generated.
	 */
	@Pure
	public boolean isUseExpressionInterpreterForInlineAnnotation() {
		return this.generatorConfig2.isUseExpressionInterpreterForInlineAnnotation();
	}

	/** Set if the constant expression interpreter shall be called for generated <code>@Inline</code>.
	 *
	 * @param generateInlineAnnotation <code>true</code> if annotation shall be generated.
	 */
	public void setUseExpressionInterpreterForInlineAnnotation(final boolean generateInlineAnnotation) {
		this.generatorConfig2.setUseExpressionInterpreterForInlineAnnotation(generateInlineAnnotation);
	}

	/** Change the source path.
	 *
	 * <p>The source path is a list the names of folders that are separated by {@link File#pathSeparator}.
	 *
	 * @param sourcePath the new source path.
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = new ArrayList<>();
		for (final String path : Strings.split(sourcePath, Pattern.quote(File.pathSeparator))) {
			this.sourcePath.add(normalizeFile(path));
		}
	}

	/** Change the source path.
	 *
	 * @param sourcePath the new source path.
	 */
	public void setSourcePath(Collection<File> sourcePath) {
		this.sourcePath = new ArrayList<>(sourcePath);
	}

	/** Add a folder to the source path.
	 *
	 * @param sourcePath the new source path.
	 */
	public void addSourcePath(String sourcePath) {
		if (!Strings.isEmpty(sourcePath)) {
			addSourcePath(normalizeFile(sourcePath));
		}
	}

	/** Add a folder to the source path.
	 *
	 * @param sourcePath the new source path.
	 */
	public void addSourcePath(File sourcePath) {
		if (this.sourcePath == null) {
			this.sourcePath = new ArrayList<>();
		}
		this.sourcePath.add(sourcePath);
	}

	/** Replies the source path.
	 *
	 * @return the source path.
	 */
	@Pure
	public List<File> getSourcePaths() {
		if (this.sourcePath == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(this.sourcePath);
	}

	private List<String> getSourcePathStrings() {
		if (this.sourcePath == null) {
			return Collections.emptyList();
		}
		final List<String> list = new ArrayList<>(this.sourcePath.size());
		for (final File input : this.sourcePath) {
			list.add(input.getAbsolutePath());
		}
		return list;
	}

	/** Run the compilation.
	 *
	 * @return success status.
	 */
	@SuppressWarnings({"checkstyle:npathcomplexity", "checkstyle:cyclomaticcomplexity"})
	@Inline("compile(null)")
	public boolean compile() {
		return compile(null);
	}

	/** Run the compilation.
	 *
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return success status.
	 */
	@SuppressWarnings({"checkstyle:npathcomplexity", "checkstyle:cyclomaticcomplexity", "checkstyle:returncount"})
	public boolean compile(CancelIndicator cancelIndicator) {
		final CancelIndicator cancel = cancelIndicator == null ? CancelIndicator.NullImpl : cancelIndicator;
		try {
			if (!checkConfiguration(cancel)) {
				return false;
			}
			final ResourceSet resourceSet = this.resourceSetProvider.get();
			if (!configureWorkspace(resourceSet, cancel)) {
				return false;
			}
			this.generatorConfigProvider.install(resourceSet, this.generatorConfig);
			if (cancel.isCanceled()) {
				return false;
			}
			this.generatorConfigProvider2.install(resourceSet, this.generatorConfig2);
			if (cancel.isCanceled()) {
				return false;
			}
			final File stubClassDirectory = createTempDir(BINCLASS_FOLDER_PREFIX);
			if (cancel.isCanceled()) {
				return false;
			}
			try {
				this.compilerPhases.setIndexing(resourceSet, true);
				if (cancel.isCanceled()) {
					return false;
				}
				// install a type provider without index lookup for the first phase
				installJvmTypeProvider(resourceSet, stubClassDirectory, true, cancel);
				if (cancel.isCanceled()) {
					return false;
				}
				loadSARLFiles(resourceSet, cancel);
				if (cancel.isCanceled()) {
					return false;
				}
				final File stubSourceDirectory = createStubs(resourceSet, cancel);
				if (cancel.isCanceled()) {
					return false;
				}
				if (!preCompileStubs(stubSourceDirectory, stubClassDirectory, cancel)) {
					if (cancel.isCanceled()) {
						return false;
					}
					this.log.warn(Messages.SarlBatchCompiler_2);
				}
				if (!preCompileJava(stubSourceDirectory, stubClassDirectory, cancel)) {
					if (cancel.isCanceled()) {
						return false;
					}
					this.log.debug(Messages.SarlBatchCompiler_3);
				}
			} finally {
				this.compilerPhases.setIndexing(resourceSet, false);
				if (cancel.isCanceled()) {
					return false;
				}
			}
			// install a fresh type provider for the second phase, so we clear all previously cached classes and misses.
			installJvmTypeProvider(resourceSet, stubClassDirectory, false, cancel);
			if (cancel.isCanceled()) {
				return false;
			}
			generateJvmElements(resourceSet, cancel);
			if (cancel.isCanceled()) {
				return false;
			}
			final List<Resource> validatedResources = new ArrayList<>();
			final boolean hasError = validate(resourceSet, validatedResources, cancel);
			if (hasError || cancel.isCanceled()) {
				return false;
			}
			generateJavaFiles(validatedResources, cancel);
			if (cancel.isCanceled()) {
				return false;
			}
			if (isJavaPostCompilationEnable()) {
				postCompileJava(cancel);
				if (cancel.isCanceled()) {
					return false;
				}
			}
		} finally {
			destroyClassLoader(this.jvmTypesClassLoader);
			destroyClassLoader(this.annotationProcessingClassLoader);
			if (isDeleteTempDirectory()) {
				for (final File file : this.tempFolders) {
					cleanFolder(file, ACCEPT_ALL_FILTER, true, true);
				}
			}
		}
		return true;
	}

	/** Create a message for the issue.
	 *
	 * @param issue the issue.
	 * @return the message.
	 */
	protected String createIssueMessage(Issue issue) {
		final IssueMessageFormatter formatter = getIssueMessageFormatter();
		final org.eclipse.emf.common.util.URI uriToProblem = issue.getUriToProblem();
		if (formatter != null) {
			return formatter.format(issue, uriToProblem);
		}
		if (uriToProblem != null) {
			final org.eclipse.emf.common.util.URI resourceUri = uriToProblem.trimFragment();
			return MessageFormat.format(Messages.SarlBatchCompiler_4,
					issue.getSeverity(), resourceUri.lastSegment(),
					resourceUri.isFile() ? resourceUri.toFileString() : "", //$NON-NLS-1$
							issue.getLineNumber(), issue.getMessage());
		}
		return MessageFormat.format(Messages.SarlBatchCompiler_5,
				issue.getSeverity(), issue.getLineNumber(), issue.getMessage());
	}

	/** Output the given issues.
	 *
	 * @param issues the issues to report.
	 */
	protected void reportIssues(Iterable<Issue> issues) {
		for (final Issue issue : issues) {
			final String issueBuilder = createIssueMessage(issue);
			if (Severity.ERROR == issue.getSeverity()) {
				this.log.error(issueBuilder);
			} else if (Severity.WARNING == issue.getSeverity()) {
				this.log.warn(issueBuilder);
			}
		}
	}

	/** Generate the Java files from the SARL scripts.
	 *
	 * @param validatedResources the validatedResources for which the Java files could be generated.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 */
	protected void generateJavaFiles(Iterable<Resource> validatedResources, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		this.log.info(MessageFormat.format(Messages.SarlBatchCompiler_28, getOutputPath()));
		final JavaIoFileSystemAccess javaIoFileSystemAccess = this.javaIoFileSystemAccessProvider.get();
		javaIoFileSystemAccess.setOutputPath(getOutputPath().getAbsolutePath());
		javaIoFileSystemAccess.setWriteTrace(isWriteTraceFiles());
		if (cancelIndicator.isCanceled()) {
			return;
		}

		final GeneratorContext context = new GeneratorContext();
		context.setCancelIndicator(cancelIndicator);
		for (final Resource resource : validatedResources) {
			if (cancelIndicator.isCanceled()) {
				return;
			}
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_23, resource.getURI().lastSegment()));
			if (isWriteStorageFiles() && resource instanceof StorageAwareResource) {
				final StorageAwareResource storageAwareResource = (StorageAwareResource) resource;
				storageAwareResource.getResourceStorageFacade().saveResource(storageAwareResource, javaIoFileSystemAccess);
			}
			if (cancelIndicator.isCanceled()) {
				return;
			}
			this.generator.generate(resource, javaIoFileSystemAccess, context);
		}
	}

	/** Generate the JVM model elements.
	 *
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @param resourceSet the container of the scripts.
	 */
	protected void generateJvmElements(ResourceSet resourceSet, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		this.log.info(Messages.SarlBatchCompiler_21);
		final List<Resource> resources = new LinkedList<>(resourceSet.getResources());
		for (final Resource resource : resources) {
			if (cancelIndicator.isCanceled()) {
				return;
			}
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_26, resource.getURI().lastSegment()));
			resource.getContents();
		}
		for (final Resource resource : resources) {
			if (cancelIndicator.isCanceled()) {
				return;
			}
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_27, resource.getURI().lastSegment()));
			EcoreUtil2.resolveLazyCrossReferences(resource, CancelIndicator.NullImpl);
		}
	}

	/** Generate the JVM model elements, and validate generated elements.
	 *
	 * @param resourceSet the container of the scripts.
	 * @param validResources will be filled by this function with the collection of resources that was successfully validated.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return <code>true</code> if an error exists in the issues. Replies <code>false</code> if the activity is cancelled.
	 */
	@SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:npathcomplexity"})
	protected boolean validate(ResourceSet resourceSet, Collection<Resource> validResources, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		boolean hasError = false;
		final List<Resource> resources = new LinkedList<>(resourceSet.getResources());
		for (final Resource resource : resources) {
			if (cancelIndicator.isCanceled()) {
				return false;
			}
			if (isSourceFile(resource)) {
				this.log.info(MessageFormat.format(Messages.SarlBatchCompiler_22, resource.getURI().lastSegment()));
				final IResourceServiceProvider resourceServiceProvider = IResourceServiceProvider.Registry.INSTANCE
						.getResourceServiceProvider(resource.getURI());
				if (resourceServiceProvider != null) {
					final IResourceValidator resourceValidator = resourceServiceProvider.getResourceValidator();
					final List<Issue> result = resourceValidator.validate(resource, CheckMode.ALL, null);
					if (cancelIndicator.isCanceled()) {
						return false;
					}
					final SortedSet<Issue> issues = new TreeSet<>(getIssueComparator());
					boolean hasValidationError = false;
					for (final Issue issue : result) {
						if (cancelIndicator.isCanceled()) {
							return false;
						}
						if (issue.isSyntaxError() || issue.getSeverity() == Severity.ERROR) {
							hasValidationError = true;
						}
						issues.add(issue);
					}
					hasError |= hasValidationError;
					if (!hasValidationError) {
						if (!issues.isEmpty()) {
							reportIssues(issues);
						}
						validResources.add(resource);
					} else {
						reportIssues(issues);
					}
				}
			}
		}
		return hasError;
	}

	/** Replies if the given resource is a script.
	 *
	 * @param resource the resource to test.
	 * @return <code>true</code> if the given resource is a script.
	 */
	@SuppressWarnings("static-method")
	protected boolean isSourceFile(Resource resource) {
		if (resource instanceof BatchLinkableResource) {
			return !((BatchLinkableResource) resource).isLoadedFromStorage();
		}
		return false;
	}

	/** Compile the stub files before the compilation of the project's files.
	 *
	 * @param sourceDirectory the source directory where stubs are stored.
	 * @param classDirectory the output directory, where stub binary files should be generated.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return the success status. Replies <code>false</code> if the activity is cancelled.
	 */
	protected boolean preCompileStubs(File sourceDirectory, File classDirectory, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		return runJavaCompiler(classDirectory, Collections.singletonList(sourceDirectory), getClassPath(), false,
				cancelIndicator);
	}

	/** Compile the java files before the compilation of the project's files.
	 *
	 * @param sourceDirectory the source directory where java files are stored.
	 * @param classDirectory the output directory, where binary files should be generated.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return the success status. Replies <code>false</code> if the activity is cancelled.
	 */
	protected boolean preCompileJava(File sourceDirectory, File classDirectory, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		return runJavaCompiler(classDirectory, getSourcePaths(),
				Iterables.concat(Collections.singleton(sourceDirectory), getClassPath()),
				false, cancelIndicator);
	}

	/** Compile the java files after the compilation of the project's files.
	 *
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return the success status. Replies <code>false</code> if the activity is cancelled.
	 */
	protected boolean postCompileJava(CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final File classOutputPath = getClassOutputPath();
		if (classOutputPath == null) {
			this.log.info(Messages.SarlBatchCompiler_24);
			return true;
		}
		this.log.info(Messages.SarlBatchCompiler_25);
		final Iterable<File> sources = Iterables.concat(getSourcePaths(), Collections.singleton(getOutputPath()));
		if (this.log.isDebugEnabled()) {
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_29, toPathString(sources)));
		}
		final List<File> classpath = getClassPath();
		if (this.log.isDebugEnabled()) {
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_30, toPathString(classpath)));
		}
		return runJavaCompiler(classOutputPath, sources, classpath, true, cancelIndicator);
	}

	private static String toPathString(Iterable<File> files) {
		final StringBuilder result = new StringBuilder();
		for (final File file : files) {
			if (result.length() > 0) {
				result.append(File.pathSeparator);
			}
			result.append(file.toString());
		}
		return result.toString();
	}

	/** Run the Java compiler.
	 *
	 * @param classDirectory the output directory.
	 * @param sourcePathDirectories the source directories.
	 * @param classPathEntries classpath entries.
	 * @param enableCompilerOutput indicates if the Java compiler output is displayed.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return the success status. Replies <code>false</code> if the activity is cancelled.
	 */
	@SuppressWarnings({ "checkstyle:npathcomplexity", "checkstyle:cyclomaticcomplexity", "resource" })
	protected boolean runJavaCompiler(File classDirectory, Iterable<File> sourcePathDirectories,
			Iterable<File> classPathEntries, boolean enableCompilerOutput, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final List<String> commandLine = Lists.newArrayList();
		// todo args
		if (isJavaCompilerVerbose()) {
			commandLine.add("-verbose"); //$NON-NLS-1$
		}
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		final List<File> bootClassPathEntries = getBootClassPath();
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		if (!bootClassPathEntries.isEmpty()) {
			final StringBuilder cmd = new StringBuilder("-bootclasspath \""); //$NON-NLS-1$
			boolean first = true;
			for (final File entry : bootClassPathEntries) {
				if (cancelIndicator.isCanceled()) {
					return false;
				}
				if (first) {
					first = false;
				} else {
					cmd.append(File.pathSeparator);
				}
				cmd.append(entry.getAbsolutePath());
			}
			cmd.append("\""); //$NON-NLS-1$
			commandLine.add(cmd.toString());
		}
		final Iterator<File> classPathIterator = classPathEntries.iterator();
		if (classPathIterator.hasNext()) {
			final StringBuilder cmd = new StringBuilder("-cp \""); //$NON-NLS-1$
			boolean first = true;
			while (classPathIterator.hasNext()) {
				if (cancelIndicator.isCanceled()) {
					return false;
				}
				if (first) {
					first = false;
				} else {
					cmd.append(File.pathSeparator);
				}
				cmd.append(classPathIterator.next().getAbsolutePath());
			}
			cmd.append("\""); //$NON-NLS-1$
			commandLine.add(cmd.toString());
		}
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		commandLine.add("-d \"" + classDirectory.getAbsolutePath() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		commandLine.add("-" + getJavaSourceVersion()); //$NON-NLS-1$
		commandLine.add("-proceedOnError"); //$NON-NLS-1$
		if (this.encodingProvider.getDefaultEncoding() != null) {
			commandLine.add("-encoding \"" + this.encodingProvider.getDefaultEncoding() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (cancelIndicator.isCanceled()) {
			return false;
		}

		final StringBuilder cmd = new StringBuilder();
		boolean first = true;
		for (final File sourceFolder : sourcePathDirectories) {
			if (cancelIndicator.isCanceled()) {
				return false;
			}
			if (first) {
				first = false;
			} else {
				cmd.append(" "); //$NON-NLS-1$
			}
			cmd.append("\""); //$NON-NLS-1$
			cmd.append(sourceFolder.getAbsolutePath().replaceAll(Pattern.quote("\""), "\\\""));  //$NON-NLS-1$//$NON-NLS-2$
			cmd.append("\""); //$NON-NLS-1$
		}
		commandLine.add(cmd.toString());

		if (this.log.isDebugEnabled()) {
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_6, Strings.concat(" ", commandLine))); //$NON-NLS-1$
		}

		if (cancelIndicator.isCanceled()) {
			return false;
		}

		final PrintWriter outWriter = getStubCompilerOutputWriter();
		final PrintWriter errWriter;
		if (enableCompilerOutput) {
			errWriter = getErrorCompilerOutputWriter();
		} else {
			errWriter = getStubCompilerOutputWriter();
		}
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		return BatchCompiler.compile(Strings.concat(" ", commandLine), outWriter, errWriter, //$NON-NLS-1$
				new CancelIndicatorWrapper(cancelIndicator));
	}

	private PrintWriter getStubCompilerOutputWriter() {
		final Writer debugWriter = new Writer() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void write(char[] data, int offset, int count) throws IOException {
				final String message = String.copyValueOf(data, offset, count);
				SarlBatchCompiler.this.log.debug(message);
			}

			@Override
			public void flush() throws IOException {
				//
			}

			@Override
			public void close() throws IOException {
				//
			}
		};
		return new PrintWriter(debugWriter);
	}

	private PrintWriter getErrorCompilerOutputWriter() {
		final Writer debugWriter = new Writer() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void write(char[] data, int offset, int count) throws IOException {
				final String message = String.copyValueOf(data, offset, count);
				SarlBatchCompiler.this.log.error(message);
			}

			@Override
			public void flush() throws IOException {
				//
			}

			@Override
			public void close() throws IOException {
				//
			}
		};
		return new PrintWriter(debugWriter);
	}

	/** Create the stubs.
	 *
	 * @param resourceSet the input resource set.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return the folder in which the stubs are located. Replies <code>null</code> if the activity is cancelled.
	 */
	protected File createStubs(ResourceSet resourceSet, CancelIndicator cancelIndicator) {
		final File outputDirectory = createTempDir(STUB_FOLDER_PREFIX);
		if (cancelIndicator.isCanceled()) {
			return null;
		}
		this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_19, outputDirectory));
		final JavaIoFileSystemAccess fileSystemAccess = this.javaIoFileSystemAccessProvider.get();
		if (cancelIndicator.isCanceled()) {
			return null;
		}
		fileSystemAccess.setOutputPath(outputDirectory.toString());
		final List<Resource> resources = new ArrayList<>(resourceSet.getResources());
		for (final Resource resource : resources) {
			if (cancelIndicator.isCanceled()) {
				return null;
			}
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_20, resource.getURI()));
			final IResourceDescription description = this.resourceDescriptionManager.getResourceDescription(resource);
			this.stubGenerator.doGenerateStubs(fileSystemAccess, description);
		}
		return outputDirectory;
	}

	/** Load the SARL files in the given resource set.
	 *
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @param resourceSet the resource set to load from.
	 */
	protected void loadSARLFiles(ResourceSet resourceSet, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		this.encodingProvider.setDefaultEncoding(getFileEncoding());
		final NameBasedFilter nameBasedFilter = new NameBasedFilter();
		nameBasedFilter.setExtension(this.fileExtensionProvider.getPrimaryFileExtension());
		final PathTraverser pathTraverser = new PathTraverser();
		final List<String> sourcePathDirectories = getSourcePathStrings();
		if (cancelIndicator.isCanceled()) {
			return;
		}
		final Multimap<String, org.eclipse.emf.common.util.URI> pathes = pathTraverser.resolvePathes(sourcePathDirectories,
				(input) -> nameBasedFilter.matches(input));
		if (cancelIndicator.isCanceled()) {
			return;
		}
		for (final String source : pathes.keySet()) {
			for (final org.eclipse.emf.common.util.URI uri : pathes.get(source)) {
				if (cancelIndicator.isCanceled()) {
					return;
				}
				if (this.log.isDebugEnabled()) {
					this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_7, uri));
				}
				resourceSet.getResource(uri, true);
			}
		}
	}

	/** Create a temporary subdirectory inside the root temp directory.
	 *
	 * @param namePrefix the prefix for the folder name.
	 * @return the temp directory.
	 * @see #getTempDirectory()
	 */
	protected File createTempDir(String namePrefix) {
		final File tempDir = new File(getTempDirectory(), namePrefix);
		cleanFolder(tempDir, ACCEPT_ALL_FILTER, true, true);
		if (!tempDir.mkdirs()) {
			throw new RuntimeException(MessageFormat.format(Messages.SarlBatchCompiler_8, tempDir.getAbsolutePath()));
		}
		this.tempFolders.add(tempDir);
		return tempDir;
	}

	/** Clean the folders.
	 *
	 * @param parentFolder the parent folder.
	 * @param filter the file filter for the file to remove..
	 * @param continueOnError indicates if the cleaning should continue on error.
	 * @param deleteParentFolder indicates if the parent folder should be removed.
	 * @return the success status.
	 */
	protected boolean cleanFolder(File parentFolder, FileFilter filter, boolean continueOnError,
			boolean deleteParentFolder) {
		try {
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_9, parentFolder.toString()));
			return Files.cleanFolder(parentFolder, null, continueOnError, deleteParentFolder);
		} catch (FileNotFoundException e) {
			return true;
		}
	}

	/** Check the compiler configuration; and logs errors.
	 *
	 * @param cancelIndicator monitor for cancelling the compilation.
	 * @return success status. Replies <code>false</code> if the operation is cancelled.
	 */
	protected boolean checkConfiguration(CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final File output = getOutputPath();
		this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_35, output));
		if (output == null) {
			this.log.error(Messages.SarlBatchCompiler_36);
			return false;
		}
		for (final File sourcePath : getSourcePaths()) {
			if (cancelIndicator.isCanceled()) {
				return false;
			}
			try {
				this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_37, sourcePath));
				if (isContainedIn(output.getCanonicalFile(), sourcePath.getCanonicalFile())) {
					this.log.error(MessageFormat.format(
							Messages.SarlBatchCompiler_10,
							output, sourcePath));
					return false;
				}
			} catch (IOException e) {
				this.log.error(Messages.SarlBatchCompiler_11, e);
			}
		}
		return true;
	}

	private static boolean isContainedIn(File child, File possibleParent) {
		File parent = child;
		while (parent != null) {
			if (parent.equals(possibleParent)) {
				return true;
			}
			parent = parent.getParentFile();
		}
		return false;
	}

	private static LinkedList<String> splitFile(File file, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final LinkedList<String> elements = new LinkedList<>();
		File current = file;
		do {
			if (cancelIndicator.isCanceled()) {
				return null;
			}
			elements.addFirst(current.getName());
			current = current.getParentFile();
		} while (current != null);
		return elements;
	}

	@SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:npathcomplexity"})
	private File determineCommonRoot(File outputFile, List<File> sourceFileList, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;

		if (this.baseUri != null) {
			if (this.baseUri.isFile()) {
				this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_32, this.baseUri));
				return new File(this.baseUri.toFileString());
			}
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_33, this.baseUri));
		}

		LinkedList<String> longuestPrefix = null;

		for (final File file : Iterables.concat(sourceFileList, Collections.singleton(outputFile))) {
			if (cancelIndicator.isCanceled()) {
				return null;
			}
			final LinkedList<String> components = splitFile(file, cancelIndicator);
			if (longuestPrefix == null) {
				longuestPrefix = components;
			} else {
				int i = 0;
				while (i < longuestPrefix.size() && i < components.size()
						&& Strings.equal(longuestPrefix.get(i), components.get(i))) {
					if (cancelIndicator.isCanceled()) {
						return null;
					}
					++i;
				}
				while (i < longuestPrefix.size()) {
					if (cancelIndicator.isCanceled()) {
						return null;
					}
					longuestPrefix.removeLast();
				}
				if (longuestPrefix.isEmpty()) {
					return null;
				}
			}
		}

		if (longuestPrefix == null || cancelIndicator.isCanceled()) {
			return null;
		}

		File prefix = null;
		for (final String component : longuestPrefix) {
			if (cancelIndicator.isCanceled()) {
				return null;
			}
			if (prefix == null) {
				prefix = new File(component);
			} else {
				prefix = new File(prefix, component);
			}
		}

		return prefix;
	}

	@SuppressWarnings({"checkstyle:cyclomaticcomplexity", "checkstyle:npathcomplexity"})
	private boolean configureWorkspace(ResourceSet resourceSet, CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final List<File> sourceFolders = getSourcePaths();
		final File outputFile = getOutputPath();
		if (sourceFolders == null || sourceFolders.isEmpty() || outputFile == null || cancelIndicator.isCanceled()) {
			return false;
		}

		this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_31, this.baseUri));

		final File commonRoot = determineCommonRoot(outputFile, sourceFolders, cancelIndicator);
		if (cancelIndicator.isCanceled()) {
			return false;
		}

		this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_34, commonRoot));
		if (commonRoot == null) {
			this.log.error(Messages.SarlBatchCompiler_12);
			for (final File sourceFile : sourceFolders) {
				this.log.error(MessageFormat.format(Messages.SarlBatchCompiler_13, sourceFile));
			}
			this.log.error(MessageFormat.format(Messages.SarlBatchCompiler_14, outputFile));
			return false;
		}
		this.projectConfig = new FileProjectConfig(commonRoot, commonRoot.getName());
		if (cancelIndicator.isCanceled()) {
			return false;
		}

		final URI commonURI = commonRoot.toURI();
		final URI relativizedTarget = commonURI.relativize(outputFile.toURI());
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		if (relativizedTarget.isAbsolute()) {
			this.log.error(MessageFormat.format(Messages.SarlBatchCompiler_15, outputFile, commonRoot));
			return false;
		}
		final CharMatcher slash = CharMatcher.is('/');
		final String relativeTargetFolder = slash.trimTrailingFrom(relativizedTarget.getPath());
		this.outputConfiguration = this.outputConfigurationProvider.getOutputConfigurations().iterator().next();
		this.outputConfiguration.setOutputDirectory(relativeTargetFolder);
		for (final File source : sourceFolders) {
			if (cancelIndicator.isCanceled()) {
				return false;
			}
			final URI relSource = commonURI.relativize(source.toURI());
			if (relSource.isAbsolute()) {
				this.log.error(MessageFormat.format(Messages.SarlBatchCompiler_16, source, commonRoot));
				return false;
			}
			this.projectConfig.addSourceFolder(slash.trimTrailingFrom(relSource.getPath()));
		}
		final Map<String, Set<OutputConfiguration>> outputConfigurations = new HashMap<>();
		outputConfigurations.put(this.languageName, Collections.singleton(this.outputConfiguration));
		ProjectConfigAdapter.install(resourceSet, this.projectConfig);
		resourceSet.eAdapters().add(new OutputConfigurationAdapter(outputConfigurations));
		if (cancelIndicator.isCanceled()) {
			return false;
		}
		return true;
	}

	/**
	 * Installs the JvmTypeProvider optionally including index access into the {@link ResourceSet}. The lookup classpath
	 * is enhanced with the given tmp directory.
	 *
	 * @param resourceSet the resource set that will be compiled.
	 * @param temporaryClassDirectory the directory where the class files of the stubs are generated.
	 * @param skipIndexLookup indicates if the index should be used for looking up types.
	 * @param cancelIndicator monitor for cancelling the compilation.
	 */
	@SuppressWarnings({ "resource", "unused", "checkstyle:npathcomplexity" })
	private void installJvmTypeProvider(ResourceSet resourceSet, File temporaryClassDirectory, boolean skipIndexLookup,
			CancelIndicator cancelIndicator) {
		assert cancelIndicator != null;
		final Iterable<File> classpath;
		if (temporaryClassDirectory != null) {
			classpath = Iterables.concat(getClassPath(), getSourcePaths(),
					Collections.singletonList(temporaryClassDirectory));
		} else {
			classpath = Iterables.concat(getClassPath(), getSourcePaths());
		}
		if (this.log.isDebugEnabled()) {
			this.log.debug(MessageFormat.format(Messages.SarlBatchCompiler_17, classpath));
		}
		if (cancelIndicator.isCanceled()) {
			return;
		}
		final ClassLoader parentClassLoader;
		if (isUseCurrentClassLoaderAsParent()) {
			parentClassLoader = getClass().getClassLoader();
		} else if (getBootClassPath().isEmpty()) {
			parentClassLoader = getCurrentClassLoader();
		} else {
			parentClassLoader = new AlternateJdkLoader(getBootClassPath());
		}
		if (cancelIndicator.isCanceled()) {
			return;
		}
		this.jvmTypesClassLoader = createClassLoader(classpath, parentClassLoader);
		if (cancelIndicator.isCanceled()) {
			return;
		}
		new ClasspathTypeProvider(this.jvmTypesClassLoader, resourceSet, skipIndexLookup ? null : this.indexedJvmTypeAccess, null);
		if (cancelIndicator.isCanceled()) {
			return;
		}
		((XtextResourceSet) resourceSet).setClasspathURIContext(this.jvmTypesClassLoader);
		if (cancelIndicator.isCanceled()) {
			return;
		}

		// for annotation processing we need to have the compiler's classpath as a parent.
		this.annotationProcessingClassLoader = createClassLoader(classpath, getCurrentClassLoader());
		if (cancelIndicator.isCanceled()) {
			return;
		}
		resourceSet.eAdapters().add(new ProcessorInstanceForJvmTypeProvider.ProcessorClassloaderAdapter(this.annotationProcessingClassLoader));
	}

	/** Create the project class loader.
	 *
	 * @param jarsAndFolders the project class path.
	 * @param parentClassLoader the parent class loader.
	 * @return the class loader for the project.
	 */
	@SuppressWarnings("static-method")
	protected ClassLoader createClassLoader(Iterable<File> jarsAndFolders, ClassLoader parentClassLoader) {
		return new URLClassLoader(Iterables.toArray(Iterables.transform(jarsAndFolders, (from) -> {
			try {
				return from.toURI().toURL();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}), URL.class), parentClassLoader);
	}

	/** Null-safe destruction of the given class loaders.
	 *
	 * @param classLoader the class loader to destroy.
	 */
	protected void destroyClassLoader(ClassLoader classLoader) {
		if (classLoader instanceof Closeable) {
			try {
				((Closeable) classLoader).close();
			} catch (Exception e) {
				this.log.warn(Messages.SarlBatchCompiler_18, e);
			}
		}
	}

	/** Filtering the severities.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@FunctionalInterface
	public interface IssueMessageFormatter {

		/** Format the message for the given issue.
		 *
		 * @param issue the issue.
		 * @param uri URI to the problem.
		 * @return the message.
		 */
		String format(Issue issue, org.eclipse.emf.common.util.URI uri);

	}

	/** Comparator of issues.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static class DefaultIssueComparator implements Comparator<Issue> {

		@Override
		@SuppressWarnings("checkstyle:npathcomplexity")
		public int compare(Issue issue1, Issue issue2) {
			if (issue1 == issue2) {
				return 0;
			}
			if (issue1 == null) {
				return -1;
			}
			if (issue2 == null) {
				return 1;
			}
			final org.eclipse.emf.common.util.URI u1 = issue1.getUriToProblem();
			final org.eclipse.emf.common.util.URI u2 = issue2.getUriToProblem();
			int cmp = 0;
			if (u1 != u2 && u1 != null && u2 != null) {
				cmp = u1.toFileString().compareTo(u2.toFileString());
			}
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(issue1.getLineNumber(), issue2.getLineNumber());
			if (cmp != 0) {
				return cmp;
			}
			cmp = Integer.compare(issue1.getColumn(), issue2.getColumn());
			if (cmp != 0) {
				return cmp;
			}
			cmp = issue1.getSeverity().compareTo(issue2.getSeverity());
			if (cmp != 0) {
				return cmp;
			}
			cmp = issue1.getMessage().compareTo(issue2.getMessage());
			if (cmp != 0) {
				return cmp;
			}
			return Integer.compare(System.identityHashCode(issue1), System.identityHashCode(issue2));
		}

	}

	/** Wrap a cancel indicator into a compilation progress.
	 *
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class CancelIndicatorWrapper extends CompilationProgress {

		private final CancelIndicator cancelIndicator;

		/**
		 * @param cancelIndicator the wrapped cancel indicator.
		 */
		CancelIndicatorWrapper(CancelIndicator cancelIndicator) {
			assert cancelIndicator != null;
			this.cancelIndicator = cancelIndicator;
		}

		@Override
		public void begin(int remainingWork) {
			//
		}

		@Override
		public void done() {
			//
		}

		@Override
		public boolean isCanceled() {
			return this.cancelIndicator.isCanceled();
		}

		@Override
		public void setTaskName(String name) {
			//
		}

		@Override
		public void worked(int workIncrement, int remainingWork) {
			//
		}

	}

}
