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
 */

package io.sarl.lang.mwe2;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;

import io.sarl.lang.core.SARLVersion;

/**
 * Guice module dedicated to the SARL language generator.
 *
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SarlLanguageGeneratorModule extends AbstractModule {

	/** Property name for the version of the language.
	 */
	public static final String LANGUAGE_VERSION_PROPERTY = "LANGUAGE_VERSION"; //$NON-NLS-1$

	@Override
	protected void configure() {
		bind(Key.get(String.class, Names.named(LANGUAGE_VERSION_PROPERTY))).toInstance(
				SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING);
	}

}