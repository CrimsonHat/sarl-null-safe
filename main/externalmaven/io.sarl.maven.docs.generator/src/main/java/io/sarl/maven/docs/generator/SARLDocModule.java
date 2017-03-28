/*
 * $Id$
 *
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 *
 * Copyright (C) 2014-2017 the original authors or authors.
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

package io.sarl.maven.docs.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Binder;
import com.google.inject.Module;
import org.jnario.doc.AbstractDocGenerator;
import org.jnario.doc.HtmlAssets;

/** Injection module that permits to inject the documentation generator
 * for the SARL project.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class SARLDocModule implements Module {

	/** Construct a module.
	 */
	SARLDocModule() {
		//
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(AbstractDocGenerator.class).to(SARLDocGenerator.class);

		try {
			final HtmlAssets assets = new HtmlAssets();
			final List<String> clone = new ArrayList<>(assets.getJsFiles());
			clone.add("js/lang-sarl.js"); //$NON-NLS-1$
			final Field field = HtmlAssets.class.getDeclaredField("_jsFiles"); //$NON-NLS-1$
			final boolean accessible = field.isAccessible();
			try {
				field.setAccessible(true);
				field.set(assets, clone);
			} finally {
				field.setAccessible(accessible);
			}
			binder.bind(HtmlAssets.class).toInstance(assets);
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

}
