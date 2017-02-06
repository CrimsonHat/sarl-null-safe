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
package io.sarl.lang.tests.general.compilation.aop;

import static org.junit.Assert.assertEquals;

import com.google.inject.Inject;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper;
import org.eclipse.xtext.xbase.compiler.CompilationTestHelper.Result;
import org.junit.Test;

import io.sarl.lang.SARLVersion;
import io.sarl.tests.api.AbstractSarlTest;

/**
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class BehaviorCompilerTest extends AbstractSarlTest {

	@Inject
	private CompilationTestHelper compiler;

	@Test
	public void basicBehaviorCompile() throws Exception {
		String source = "behavior B1 { }";
		String expected = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
				);
		this.compiler.assertCompilesTo(source, expected);
	}

	@Test
	public void trueGuardBehaviorUnit() throws Exception {
		final String expectedE1 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Address;",
				"import io.sarl.lang.core.Event;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class E1 extends Event {",
				"  /**",
				"   * Construct an event. The source of the event is unknown.",
				"   */",
				"  @SyntheticMember",
				"  public E1() {",
				"    super();",
				"  }",
				"  ",
				"  /**",
				"   * Construct an event.",
				"   * @param source - address of the agent that is emitting this event.",
				"   */",
				"  @SyntheticMember",
				"  public E1(final Address source) {",
				"    super(source);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  private final static long serialVersionUID = 588368462L;",
				"}",
				""
				);
		final String expectedB1 = multilineString(
				"import io.sarl.lang.annotation.PerceptGuardEvaluator;",
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"import java.util.Collection;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  @SyntheticMember",
				"  private void $behaviorUnit$E1$0(final E1 occurrence) {",
				"    System.out.println(occurrence);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @PerceptGuardEvaluator",
				"  private void $guardEvaluator$E1(final E1 occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {",
				"    assert occurrence != null;",
				"    assert ___SARLlocal_runnableCollection != null;",
				"    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$E1$0(occurrence));",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
				);
		String source = multilineString(
				"event E1",
				"behavior B1 {",
				"  on E1 [ true ] {",
				"    System.out.println(occurrence)",
				"  }",
				"}"
				);
		this.compiler.compile(source, (r) -> {
				assertEquals(expectedE1,r.getGeneratedCode("E1"));
				assertEquals(expectedB1,r.getGeneratedCode("B1"));
			});
	}

	@Test
	public void falseGuardBehaviorUnit() throws Exception {
		final String expectedE1 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Address;",
				"import io.sarl.lang.core.Event;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class E1 extends Event {",
				"  /**",
				"   * Construct an event. The source of the event is unknown.",
				"   */",
				"  @SyntheticMember",
				"  public E1() {",
				"    super();",
				"  }",
				"  ",
				"  /**",
				"   * Construct an event.",
				"   * @param source - address of the agent that is emitting this event.",
				"   */",
				"  @SyntheticMember",
				"  public E1(final Address source) {",
				"    super(source);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  private final static long serialVersionUID = 588368462L;",
				"}",
				""
				);
		final String expectedB1 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
				);
		String source = multilineString(
				"event E1",
				"behavior B1 {",
				"  on E1 [ false ] {",
				"    System.out.println(occurrence)",
				"  }",
				"}"
				);
		this.compiler.compile(source, (r) -> {
				assertEquals(expectedE1, r.getGeneratedCode("E1"));
				assertEquals(expectedB1, r.getGeneratedCode("B1"));
			});
	}

	@Test
	public void generalGuardBehaviorUnit() throws Exception {
		final String expectedE1 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Address;",
				"import io.sarl.lang.core.Event;",
				"import org.eclipse.xtext.xbase.lib.Pure;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class E1 extends Event {",
				"  public int i;",
				"  ",
				"  /**",
				"   * Construct an event. The source of the event is unknown.",
				"   */",
				"  @SyntheticMember",
				"  public E1() {",
				"    super();",
				"  }",
				"  ",
				"  /**",
				"   * Construct an event.",
				"   * @param source - address of the agent that is emitting this event.",
				"   */",
				"  @SyntheticMember",
				"  public E1(final Address source) {",
				"    super(source);",
				"  }",
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
				"    E1 other = (E1) obj;",
				"    if (other.i != this.i)",
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
				"    result = prime * result + this.i;",
				"    return result;",
				"  }",
				"  ",
				"  /**",
				"   * Returns a String representation of the E1 event's attributes only.",
				"   */",
				"  @SyntheticMember",
				"  @Pure",
				"  protected String attributesToString() {",
				"    StringBuilder result = new StringBuilder(super.attributesToString());",
				"    result.append(\"i  = \").append(this.i);",
				"    return result.toString();",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  private final static long serialVersionUID = 588472998L;",
				"}",
				""
				);
		final String expectedB1 = multilineString(
				"import io.sarl.lang.annotation.PerceptGuardEvaluator;",
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"import java.util.Collection;",
				"import org.eclipse.xtext.xbase.lib.Pure;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  @SyntheticMember",
				"  private void $behaviorUnit$E1$0(final E1 occurrence) {",
				"    System.out.println(occurrence);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @Pure",
				"  private boolean $behaviorUnitGuard$E1$0(final E1 it, final E1 occurrence) {",
				"    return (occurrence.i == 1);",
				"  }",
				"  ",
				"  @SyntheticMember",
				"  @PerceptGuardEvaluator",
				"  private void $guardEvaluator$E1(final E1 occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {",
				"    assert occurrence != null;",
				"    assert ___SARLlocal_runnableCollection != null;",
				"    if ($behaviorUnitGuard$E1$0(occurrence, occurrence)) {",
				"      ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$E1$0(occurrence));",
				"    }",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
				);
		String source = multilineString(
				"event E1 { var i : int }",
				"behavior B1 {",
				"  on E1 [ occurrence.i === 1 ] {",
				"    System.out.println(occurrence)",
				"  }",
				"}"
				);
		this.compiler.compile(source, (r) -> {
				assertEquals(expectedE1,r.getGeneratedCode("E1"));
				assertEquals(expectedB1,r.getGeneratedCode("B1"));
			});
	}

	@Test
	public void valueVisibility_0() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" val myval = 1",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected final int myval = 1;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void variableVisibility_0() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" var myval = 1",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected int myval = 1;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionVisibility_0() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" def myfct { }",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected void myfct() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void valueVisibility_1() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" private val myval = 1",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  private final int myval = 1;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void variableVisibility_1() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" private var myval = 1",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  private int myval = 1;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionVisibility_1() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				" private def myfct { }",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  private void myfct() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_none() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 { }"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_public() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"public behavior B1 { }"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_package() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"package behavior B1 { }"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_abstract() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"abstract behavior B1 { }"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public abstract class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_abstract_member() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	def fct",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public abstract class B1 extends Behavior {",
				"  protected abstract void fct();",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void behaviormodifier_final() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"final behavior B1 { }"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public final class B1 extends Behavior {",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void fieldmodifier_none() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	var field : int",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected int field;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void fieldmodifier_package() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	package var field : int",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  int field;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void fieldmodifier_protected() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	protected var field : int",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected int field;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void fieldmodifier_private() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	private var field : int",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  private int field;",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_override() throws Exception {
		String source = multilineString(
				"behavior B1 {",
				"	def name",
				"}",
				"behavior B2 extends B1 {",
				"	override name {}",
				"}"
			);
		final String expectedB1 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public abstract class B1 extends Behavior {",
				"  protected abstract void name();",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			);
		final String expectedB2 = multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B2 extends B1 {",
				"  @Override",
				"  protected void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B2(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			);
		this.compiler.compile(source, (r) -> {
				assertEquals(expectedB1, r.getGeneratedCode("B1"));
				assertEquals(expectedB2, r.getGeneratedCode("B2"));
			});
	}

	@Test
	public void actionmodifier_none() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_private() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	private def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  private void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_package() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	package def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_protected() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	protected def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_abstract() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	def name",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public abstract class B1 extends Behavior {",
				"  protected abstract void name();",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_static() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	static def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected static void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_dispatch() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	dispatch def name(a : Integer) {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected void _name(final Integer a) {",
				"  }",
				"  ",
				"  protected void name(final Integer a) {",
				"    _name(a);",
				"    return;",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_final() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	final def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected final void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

	@Test
	public void actionmodifier_synchronized() throws Exception {
		this.compiler.assertCompilesTo(
			multilineString(
				"behavior B1 {",
				"	synchronized def name {}",
				"}"
			),
			multilineString(
				"import io.sarl.lang.annotation.SarlSpecification;",
				"import io.sarl.lang.annotation.SyntheticMember;",
				"import io.sarl.lang.core.Agent;",
				"import io.sarl.lang.core.Behavior;",
				"",
				"@SarlSpecification(\"" + SARLVersion.SPECIFICATION_RELEASE_VERSION_STRING + "\")",
				"@SuppressWarnings(\"all\")",
				"public class B1 extends Behavior {",
				"  protected synchronized void name() {",
				"  }",
				"  ",
				"  /**",
				"   * Construct a behavior.",
				"   * @param owner - reference to the agent that is owning this behavior.",
				"   */",
				"  @SyntheticMember",
				"  public B1(final Agent owner) {",
				"    super(owner);",
				"  }",
				"}",
				""
			));
	}

}
