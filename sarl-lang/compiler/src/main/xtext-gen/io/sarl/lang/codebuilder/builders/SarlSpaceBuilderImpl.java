/*
 * $Id$
 *
 * File is automatically generated by the Xtext language generator.
 * Do not change it.
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2023 SARL.io, the Original Authors and Main Authors.
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
package io.sarl.lang.codebuilder.builders;

import io.sarl.lang.sarl.SarlFactory;
import io.sarl.lang.sarl.SarlScript;
import io.sarl.lang.sarl.SarlSpace;
import java.util.function.Predicate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.core.xtend.XtendFactory;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlSpace.
 */
@SuppressWarnings("all")
public class SarlSpaceBuilderImpl extends AbstractBuilder implements ISarlSpaceBuilder {

	private SarlSpace sarlSpace;

	@Override
	@Pure
	public String toString() {
		return EmfFormatter.objToStr(getSarlSpace());
	}

	/** Initialize the Ecore element when inside a script.
	 */
	public void eInit(SarlScript script, String name, IJvmTypeProvider context) {
		setTypeResolutionContext(context);
		if (this.sarlSpace == null) {
			this.sarlSpace = SarlFactory.eINSTANCE.createSarlSpace();
			script.getXtendTypes().add(this.sarlSpace);
			this.sarlSpace.setAnnotationInfo(XtendFactory.eINSTANCE.createXtendTypeDeclaration());
			if (!Strings.isEmpty(name)) {
				this.sarlSpace.setName(name);
			}
		}
	}

	/** Replies the generated SarlSpace.
	 */
	@Pure
	public SarlSpace getSarlSpace() {
		return this.sarlSpace;
	}

	/** Replies the resource to which the SarlSpace is attached.
	 */
	@Pure
	public Resource eResource() {
		return getSarlSpace().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlSpace().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlSpace(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlSpace().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Add a modifier.
	 * @param modifier the modifier to add.
	 */
	public void addModifier(String modifier) {
		if (!Strings.isEmpty(modifier)) {
			this.sarlSpace.getModifiers().add(modifier);
		}
	}

}

