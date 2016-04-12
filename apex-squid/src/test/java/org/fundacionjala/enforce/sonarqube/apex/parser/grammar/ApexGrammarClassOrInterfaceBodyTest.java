/*
 * The MIT License
 *
 * Copyright 2016 Fundacion Jala.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fundacionjala.enforce.sonarqube.apex.parser.grammar;

import org.fundacionjala.enforce.sonarqube.apex.parser.ApexRuleTest;
import org.junit.Before;
import org.junit.Test;
import static org.fundacionjala.enforce.sonarqube.apex.api.grammar.ApexGrammarRuleKey.CLASS_OR_INTERFACE_BODY;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class ApexGrammarClassOrInterfaceBodyTest extends ApexRuleTest {

    @Before
    public void setUp() {
        setRootRule(CLASS_OR_INTERFACE_BODY);
    }

    @Test
    public void testValidClassOrInterfaceBody() {
        assertThat(parser)
                .matches("")
                .matches("public static int addition();"
                        + "public int multiplication();")
                .matches("with sharing class MyClass{}"
                        + "with sharing class MyClass{}")
                .matches("static {int suma = 0;}")
                .matches("private string myString;")
                .matches("private int myIntVariable;"
                        + "public MyClass() {"
                        + "int myIntVariable = 6;"
                        + "}"
                        + "public int additition() {}")
                .matches("public int Addition{get; set;}")
                .matches("int prop;"
                        + "public int Addition{get { return 3;}"
                        + "                    set{int prop = 1;}}")
                .matches("public int firstProperty { get; set;}"
                        + "public int secondProperty { get; set;}")
                .matches("public static int propertyWithParameters{"
                        + "get { return variable;}"
                        + "set { int variable = 1;}}");
    }

    @Test
    public void testInValidClassOrInterfaceBody() {
        assertThat(parser)
                .notMatches("static public Class invalid")
                .notMatches("private int noSemicolon")
                .notMatches("public int wrongProperty {get set}")
                .notMatches("private WrongConstructor int { }");
    }

}
