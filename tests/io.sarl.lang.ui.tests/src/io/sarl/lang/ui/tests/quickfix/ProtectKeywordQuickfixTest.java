/*
 * Copyright (C) 2014-2016 the original authors or authors.
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
package io.sarl.lang.ui.tests.quickfix;

import org.junit.Test;

import io.sarl.lang.parser.SyntaxIssueCodes;

@SuppressWarnings("all")
public class ProtectKeywordQuickfixTest extends AbstractSARLQuickfixTest {

	@Test
	public void fix() {
		assertQuickFixWithErrors(
				SyntaxIssueCodes.USED_RESERVED_KEYWORD,
				//
				// Code to fix:
				//
				"package io.sarl.lang.tests.behavior.mypackage",
				//
				// Label and description:
				//
				"Change to '^behavior'",
				//
				// Expected fixed code:
				//
				"package io.sarl.lang.tests.^behavior.mypackage");
	}

}