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

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.sarl.lang.sarl.SarlEnumLiteral;
import io.sarl.lang.sarl.SarlFactory;
import java.util.function.Predicate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlEnumLiteral.
 */
@SuppressWarnings("all")
public class SarlEnumLiteralBuilderImpl extends AbstractBuilder implements ISarlEnumLiteralBuilder {

	@Inject
	private Provider<IFormalParameterBuilder> parameterProvider;
	@Inject
	private Provider<IBlockExpressionBuilder> blockExpressionProvider;
	@Inject
	private Provider<IExpressionBuilder> expressionProvider;
	private EObject container;

	private SarlEnumLiteral sarlEnumLiteral;

	/** Initialize the Ecore element.
	 * @param container the container of the SarlEnumLiteral.
	 * @param name the name of the SarlEnumLiteral.
	 */
	public void eInit(XtendTypeDeclaration container, String name, IJvmTypeProvider context) {
		setTypeResolutionContext(context);
		if (this.sarlEnumLiteral == null) {
			this.container = container;
			this.sarlEnumLiteral = SarlFactory.eINSTANCE.createSarlEnumLiteral();
			this.sarlEnumLiteral.setName(name);
			container.getMembers().add(this.sarlEnumLiteral);
		}
	}

	/** Replies the generated element.
	 */
	@Pure
	public SarlEnumLiteral getSarlEnumLiteral() {
		return this.sarlEnumLiteral;
	}

	/** Replies the resource.
	 */
	@Pure
	public Resource eResource() {
		return getSarlEnumLiteral().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlEnumLiteral().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlEnumLiteral(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlEnumLiteral().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	@Override
	@Pure
	public String toString() {
		return EmfFormatter.objToStr(getSarlEnumLiteral());
	}

}

