/*
 * $Id$
 *
 * File is automatically generated by the Xtext language generator.
 * Do not change it.
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright 2014-2016 the original authors and authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sarl.lang.codebuilder.builders;

import io.sarl.lang.sarl.SarlAnnotationType;
import io.sarl.lang.sarl.SarlFactory;
import io.sarl.lang.sarl.SarlScript;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotationsFactory;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlAnnotationType.
 */
@SuppressWarnings("all")
public class AnnotationTypeBuilderImpl extends AbstractBuilder implements IAnnotationTypeBuilder {

	private SarlAnnotationType sarlAnnotationType;

	/** Initialize the Ecore element.
	 */
	public void eInit(SarlScript script, String name) {
		if (this.sarlAnnotationType == null) {
			this.sarlAnnotationType = SarlFactory.eINSTANCE.createSarlAnnotationType();
			script.getXtendTypes().add(this.sarlAnnotationType);
			if (!Strings.isEmpty(name)) {
				this.sarlAnnotationType.setName(name);
			}
		}
	}

	/** Replies the generated SarlAnnotationType.
	 */
	@Pure
	public SarlAnnotationType getSarlAnnotationType() {
		return this.sarlAnnotationType;
	}

	/** Replies the resource to which the SarlAnnotationType is attached.
	 */
	@Pure
	public Resource eResource() {
		return getSarlAnnotationType().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlAnnotationType().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlAnnotationType(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlAnnotationType().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Add an annotation.
	 * @param type - the qualified name of the annotation.
	 */
	public void addAnnotation(String type) {
		if (!Strings.isEmpty(type)) {
			XAnnotation annotation = XAnnotationsFactory.eINSTANCE.createXAnnotation();
			annotation.setAnnotationType(newTypeRef(sarlAnnotationType, type).getType());
			this.sarlAnnotationType.getAnnotations().add(annotation);
		}
	}

	/** Add a modifier.
	 * @param modifier - the modifier to add.
	 */
	public void addModifier(String modifier) {
		if (!Strings.isEmpty(modifier)) {
			this.sarlAnnotationType.getModifiers().add(modifier);
		}
	}

	@Inject
	private Provider<IAnnotationFieldBuilder> iAnnotationFieldBuilderProvider;

	/** Create an AnnotationField.
	 * @param name - the name of the AnnotationField.
	 * @return the builder.
	 */
	public IAnnotationFieldBuilder addVarAnnotationField(String name) {
		IAnnotationFieldBuilder builder = this.iAnnotationFieldBuilderProvider.get();
		builder.eInit(getSarlAnnotationType(), name, "var");
		return builder;
	}

	/** Create an AnnotationField.
	 * @param name - the name of the AnnotationField.
	 * @return the builder.
	 */
	public IAnnotationFieldBuilder addValAnnotationField(String name) {
		IAnnotationFieldBuilder builder = this.iAnnotationFieldBuilderProvider.get();
		builder.eInit(getSarlAnnotationType(), name, "val");
		return builder;
	}

	/** Create an AnnotationField.	 *
	 * <p>This function is equivalent to {@link #addVarAnnotationField}.
	 * @param name - the name of the AnnotationField.
	 * @return the builder.
	 */
	public IAnnotationFieldBuilder addAnnotationField(String name) {
		return this.addVarAnnotationField(name);
	}

}
