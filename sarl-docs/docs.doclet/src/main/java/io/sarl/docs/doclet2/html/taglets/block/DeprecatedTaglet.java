/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2023 SARL.io, the Original Authors and Main Authors
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
 *
 *------- FORKED SOURCE CODE:
 *
 * THIS CODE IS FORKED FROM JDK.JAVADOC INTERNAL PACKAGE AND ADAPTED TO THE SARL PURPOSE.
 * THE FORK WAS NECESSARY BECAUSE IT IS IMPOSSIBLE TO SUBCLASS THE TYPES FOR THE.
 * STANDARD HTML DOCLET THAT IS PROVIDED BY JDK.JAVADOC MODULE.
 *
 * Copyright (c) 2003, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package io.sarl.docs.doclet2.html.taglets.block;

import java.text.MessageFormat;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import com.sun.source.doctree.DeprecatedTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownBlockTagTree;

import io.sarl.docs.doclet2.html.framework.CssStyles;
import io.sarl.docs.doclet2.html.framework.HtmlFactoryContentExtractor;
import io.sarl.docs.doclet2.html.taglets.AbstractSarlTaglet;

/** Taglet for {@code deprecated} tag.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.13
 */
public class DeprecatedTaglet extends AbstractSarlTaglet {

	/** Name of the tag.
	 */
	public static final String TAGLET_NAME = "deprecated"; //$NON-NLS-1$

	/** Constructor.
	 */
	public DeprecatedTaglet() {
		super(TAGLET_NAME.toLowerCase(), false, Location.values());
	}

	/** Replies the CSS style for the deprecated text.
	 *
	 * @param original the original CSS style.
	 * @return the CSS style.
	 */
	@SuppressWarnings("static-method")
	protected CssStyles getTextCssStyle(CssStyles original) {
		return CssStyles.TAG_DEPRECATED_COMMENT;
	}
	
	@Override
	public boolean appendNode(org.jsoup.nodes.Element parent, List<? extends DocTree> tags, Element element, DocTree sourceDocumentation, CssStyles style, HtmlFactoryContentExtractor referenceExtractor) {
		final Deque<Element> candidates = new LinkedList<>();
		candidates.addLast(element);
		while (!candidates.isEmpty()) {
			final Element current = candidates.removeFirst();
			final boolean deprecated = referenceExtractor.getContext().getEnvironment().getElementUtils().isDeprecated(current);
			if (deprecated) {
				final boolean inEnclosing = current != element;
				if (inEnclosing) {
					final org.jsoup.nodes.Element prefix = getHtmlFactory().createSpanTag(parent, getTextCssStyle(style));
					prefix.appendText(MessageFormat.format(Messages.DeprecatedTaglet_0, element.getSimpleName()));
					getHtmlFactory().createSecableSpace(parent);
				}
			} else {
				final Element container = current.getEnclosingElement();
				if (container != null) {
					candidates.addLast(container);
				}
			}
		}
		boolean changed = false;
		for (final DocTree tr : tags) {
			if (changed) {
				if (!parent.html().toString().endsWith(".")) { //$NON-NLS-1$
					parent.appendText("."); //$NON-NLS-1$
				}
				getHtmlFactory().createSecableSpace(parent);
			}
			if (tr instanceof DeprecatedTree) {
				final DeprecatedTree dt = (DeprecatedTree) tr;
		    	changed = appendCommentTextWithSpace(parent, dt.getBody(), element, getTextCssStyle(style), referenceExtractor.getContext())
		    			|| changed;
			} else if (tr instanceof UnknownBlockTagTree) {
				final UnknownBlockTagTree ubtt = (UnknownBlockTagTree) tr;
		    	changed = appendCommentTextWithSpace(parent, ubtt.getContent(), element, getTextCssStyle(style), referenceExtractor.getContext())
		    			|| changed;
			}
		}
		final boolean deprecated0 = referenceExtractor.getContext().getEnvironment().getElementUtils().isDeprecated(element);
		if (!deprecated0) {
			referenceExtractor.getContext().getReporter().print(Kind.WARNING, MessageFormat.format(Messages.DeprecatedTaglet_1, 
					getElementUtils().getFullyQualifiedName(element, true), Deprecated.class.getName()));
		}
		return changed;
	}

}