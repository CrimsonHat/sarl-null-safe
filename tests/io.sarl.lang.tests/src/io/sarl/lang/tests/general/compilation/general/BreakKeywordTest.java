/*
 * Copyright (C) 2014-2017 the original authors or authors.
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
package io.sarl.lang.tests.general.compilation.general;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import io.sarl.lang.SARLVersion;
import io.sarl.lang.sarl.SarlAction;
import io.sarl.lang.sarl.SarlAgent;
import io.sarl.lang.sarl.SarlPackage;
import io.sarl.lang.sarl.SarlScript;
import io.sarl.lang.validation.IssueCodes;
import io.sarl.tests.api.AbstractSarlTest;

import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.xbase.XbasePackage;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotationsPackage;
import org.eclipse.xtext.xbase.testing.CompilationTestHelper;
import org.eclipse.xtext.xtype.XtypePackage;
import org.junit.Test;

import com.google.common.base.Strings;
import com.google.inject.Inject;

/**
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class BreakKeywordTest extends AbstractSarlTest {

	@Inject
	private CompilationTestHelper compiler;

	@Test
	public void insideFunction() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct {",
				"    break",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct() {",
				"    break;",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideIfThen() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    if (a == 1) {",
				"      break",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    if ((a == 1)) {",
				"      break;",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideField() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  var field = [",
				"    break",
				"  ]",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;",
				"import org.eclipse.xtext.xbase.lib.Pure;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  private Procedure1<Object> field = ((Procedure1<Object>) (Object it) -> {",
				"    break;",
				"  });",
				"  ",
				"  @Override",
				"  @Pure",
				"  @SyntheticMember",
				"  public boolean equals(final Object obj) {",
				"    if (this == obj)",
				"      return true;",
				"    if (obj == null)",
				"      return false;",
				"    if (getClass() != obj.getClass())",
				"      return false;",
				"    A1 other = (A1) obj;",
				"    if (this.field == null) {",
				"      if (other.field != null)",
				"        return false;",
				"    } else if (!this.field.equals(other.field))",
				"      return false;",
				"    return super.equals(obj);",
				"  }",
				"  ",
				"  @Override",
				"  @Pure",
				"  @SyntheticMember",
				"  public int hashCode() {",
				"    final int prime = 31;",
				"    int result = super.hashCode();",
				"    result = prime * result + ((this.field== null) ? 0 : this.field.hashCode());",
				"    return result;",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideWhileWithoutBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    var b = a",
				"    while (b > 0) {",
				"      break",
				"      b--",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    int b = a;",
				"    while ((b > 0)) {",
				"      {",
				"        break;",
				"        b--;",
				"      }",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideWhileWithBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    var b = a",
				"    while (b > 0) {",
				"      if (a == 5) break",
				"      b--",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    int b = a;",
				"    while ((b > 0)) {",
				"      {",
				"        if ((a == 5)) {",
				"          break;",
				"        }",
				"        b--;",
				"      }",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideDoWhileWithoutBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    var b = a",
				"    do {",
				"      break",
				"      b--",
				"    } while (b > 0)",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    int b = a;",
				"    do {",
				"      {",
				"        break;",
				"        b--;",
				"      }",
				"    } while((b > 0));",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideDoWhileWithBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    var b = a",
				"    do {",
				"      if (a == 5) break",
				"      b--",
				"    } while (b > 0)",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    int b = a;",
				"    do {",
				"      {",
				"        if ((a == 5)) {",
				"          break;",
				"        }",
				"        b--;",
				"      }",
				"    } while((b > 0));",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideForWithoutBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    for(b : 0..a) {",
				"      break",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"import org.eclipse.xtext.xbase.lib.IntegerRange;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    IntegerRange _upTo = new IntegerRange(0, a);",
				"    for (final Integer b : _upTo) {",
				"      break;",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideForWithBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    for(b : 0..a) {",
				"      if (b == 5) break",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"import org.eclipse.xtext.xbase.lib.IntegerRange;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    IntegerRange _upTo = new IntegerRange(0, a);",
				"    for (final Integer b : _upTo) {",
				"      if (((b).intValue() == 5)) {",
				"        break;",
				"      }",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideBasicForWithoutBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    for(var b = 0; b < a; b++) {",
				"      break",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    for (int b = 0; (b < a); b++) {",
				"      break;",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void insideBasicForWithBranch() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    for(var b = 0; b < a; b++) {",
				"      if (b == 5) break",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    for (int b = 0; (b < a); b++) {",
				"      if ((b == 5)) {",
				"        break;",
				"      }",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void unreachableCode() throws Exception {
		String source = multilineString(
				"agent A1 {",
				"  def fct(a : int) {",
				"    for(b : 0..a) {",
				"      if (b == 5) {",
				"        break",
				"        println(b)",
				"      }",
				"    }",
				"  }",
				"}"
				);
		final String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.BuiltinCapacitiesProvider;",
				"import java.util.UUID;",
				"import javax.inject.Inject;",
				"import org.eclipse.xtext.xbase.lib.InputOutput;",
				"import org.eclipse.xtext.xbase.lib.IntegerRange;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class A1 extends Agent {",
				"  protected void fct(final int a) {",
				"    IntegerRange _upTo = new IntegerRange(0, a);",
				"    for (final Integer b : _upTo) {",
				"      if (((b).intValue() == 5)) {",
				"        break;",
				"        InputOutput.<Integer>println(b);",
				"      }",
				"    }",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  public A1(final UUID arg0, final UUID arg1) {",
				"    super(arg0, arg1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Inject",
				"  public A1(final BuiltinCapacitiesProvider arg0, final UUID arg1, final UUID arg2) {",
				"    super(arg0, arg1, arg2);",
				"  }",
				"}",
				"");
		this.compiler.assertCompilesTo(source, expected);
	}

}