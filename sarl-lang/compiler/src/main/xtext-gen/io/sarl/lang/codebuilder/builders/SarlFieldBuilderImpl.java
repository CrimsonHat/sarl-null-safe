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
import io.sarl.lang.sarl.SarlFactory;
import io.sarl.lang.sarl.SarlField;
import java.util.function.Predicate;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.core.xtend.XtendFactory;
import org.eclipse.xtend.core.xtend.XtendTypeDeclaration;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.util.EmfFormatter;
import org.eclipse.xtext.util.Strings;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.DocumentationAdapter;
import org.eclipse.xtext.xbase.lib.Procedures;
import org.eclipse.xtext.xbase.lib.Pure;

/** Builder of a Sarl SarlField.
 */
@SuppressWarnings("all")
public class SarlFieldBuilderImpl extends AbstractBuilder implements ISarlFieldBuilder {

	@Inject
	private Provider<IFormalParameterBuilder> parameterProvider;
	@Inject
	private Provider<IBlockExpressionBuilder> blockExpressionProvider;
	@Inject
	private Provider<IExpressionBuilder> expressionProvider;
	private EObject container;

	private SarlField sarlField;

	/** Initialize the Ecore element.
	 * @param container the container of the SarlField.
	 * @param name the name of the SarlField.
	 */
	public void eInit(XtendTypeDeclaration container, String name, String modifier, IJvmTypeProvider context) {
		setTypeResolutionContext(context);
		if (this.sarlField == null) {
			this.container = container;
			this.sarlField = SarlFactory.eINSTANCE.createSarlField();
			this.sarlField.setAnnotationInfo(XtendFactory.eINSTANCE.createXtendMember());
			this.sarlField.setName(name);
			if (Strings.equal(modifier, "var")
				|| Strings.equal(modifier, "val")) {
				this.sarlField.getModifiers().add(modifier);
			} else {
				throw new IllegalStateException("Invalid modifier");
			}
			container.getMembers().add(this.sarlField);
		}
	}

	/** Replies the generated element.
	 */
	@Pure
	public SarlField getSarlField() {
		return this.sarlField;
	}

	/** Replies the resource.
	 */
	@Pure
	public Resource eResource() {
		return getSarlField().eResource();
	}

	/** Change the documentation of the element.
	 *
	 * <p>The documentation will be displayed just before the element.
	 *
	 * @param doc the documentation.
	 */
	public void setDocumentation(String doc) {
		if (Strings.isEmpty(doc)) {
			getSarlField().eAdapters().removeIf(new Predicate<Adapter>() {
				public boolean test(Adapter adapter) {
					return adapter.isAdapterForType(DocumentationAdapter.class);
				}
			});
		} else {
			DocumentationAdapter adapter = (DocumentationAdapter) EcoreUtil.getExistingAdapter(
					getSarlField(), DocumentationAdapter.class);
			if (adapter == null) {
				adapter = new DocumentationAdapter();
				getSarlField().eAdapters().add(adapter);
			}
			adapter.setDocumentation(doc);
		}
	}

	/** Change the type.
	 * @param type the type of the member.
	 */
	public void setType(String type) {
		this.sarlField.setType(newTypeRef(this.container, type));
	}

	/** Replies the initialValue.
	 * @return the value of the initialValue. It may be {@code null}.
	 */
	@Pure
	public IExpressionBuilder getInitialValue() {
		IExpressionBuilder exprBuilder = this.expressionProvider.get();
		exprBuilder.eInit(getSarlField(), new Procedures.Procedure1<XExpression>() {
				public void apply(XExpression expr) {
					getSarlField().setInitialValue(expr);
				}
			}, getTypeResolutionContext());
		return exprBuilder;
	}

	/** Add a modifier.
	 * @param modifier the modifier to add.
	 */
	public void addModifier(String modifier) {
		if (!Strings.isEmpty(modifier)) {
			getSarlField().getModifiers().add(modifier);
		}
	}

	@Override
	@Pure
	public String toString() {
		return EmfFormatter.objToStr(getSarlField());
	}

}

