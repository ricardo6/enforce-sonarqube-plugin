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

import org.junit.Before;
import org.junit.Test;

import org.fundacionjala.enforce.sonarqube.apex.parser.ApexRuleTest;

import static org.fundacionjala.enforce.sonarqube.apex.api.grammar.ApexGrammarRuleKey.FOR_STATEMENT;
import static org.sonar.sslr.tests.Assertions.assertThat;

public class ApexGrammarForStatementTest extends ApexRuleTest {

    @Before
    public void init() {
        setRootRule(FOR_STATEMENT);
    }

    @Test
    public void RulesForStament() {
        assertThat(parser)
                .matches("for(Object MyObject : listIntegers){ }")
                .matches("for(int MyObject:listIntegers){}")
                .matches("for(double MyObject:listDoubles)intnumber;")
                .matches("for(boolean MyObject:listBooleans){int number=i;}");
    }

    @Test
    public void RulesForStamentCaseError() {
        assertThat(parser)
                .notMatches("for(ObjectMyObject:listIntegers){ }")
                .notMatches("for(intMyObject:listIntegers){}")
                .notMatches("for(doubleMyObject:listDoubles)intnumber;")
                .notMatches("for(booleanMyObject:listBooleans){int number=i;}");
    }
}
