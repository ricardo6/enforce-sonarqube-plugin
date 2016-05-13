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
package org.fundacionjala.enforce.sonarqube.apex.checks;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import java.util.List;
import org.fundacionjala.enforce.sonarqube.apex.api.grammar.ApexGrammarRuleKey;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;

/**
 * Checks that no "loop" block of code calls an async method, which have the
 * "@future" annotation.
 */
@Rule(
        key = AsyncMethodsCheck.CHECK_KEY,
        priority = Priority.INFO,
        name = "An async method should not be invoked within a loop.",
        description = "An async method, which is declared with an \"@future\" annotation, should not"
        + "be called or invoked within a block of code inside a loop statement (for, while, do while).",
        tags = Tags.OBSOLETE
)
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.SYNCHRONIZATION_RELIABILITY)
@SqaleConstantRemediation("5min")
@ActivatedByDefault
public class AsyncMethodsCheck extends SquidCheck<Grammar> {

    /**
     * It is the code of the rule for the plugin.
     */
    public static final String CHECK_KEY = "A1009";

    /**
     * Value of the annotation future, which this check looks for.
     */
    private final String FUTURE = "future";

    private List<AstNode> methods;

    /**
     * The variables are initialized and subscribe the base rule.
     */
    @Override
    public void init() {
        subscribeTo(ApexGrammarRuleKey.WHILE_STATEMENT,
                ApexGrammarRuleKey.FOR_STATEMENT,
                ApexGrammarRuleKey.DO_STATEMENT);
    }

    /**
     * It is responsible for verifying whether the rule is met in the rule base.
     * In the event that the rule is not correct, create message error.
     *
     * @param astNode It is the node that stores all the rules.
     */
    @Override
    public void visitNode(AstNode astNode) {
        if (astNode.hasDescendant(ApexGrammarRuleKey.STATEMENT_EXPRESSION)) {
            List<AstNode> expressions = astNode.getDescendants(ApexGrammarRuleKey.STATEMENT_EXPRESSION);
            for (AstNode expression : expressions) {
                if (expression.hasDescendant(ApexGrammarRuleKey.PRIMARY_SUFFIX)
                        && expression.hasDescendant(ApexGrammarRuleKey.ARGUMENTS)) {
                    List<AstNode> arguments = expression.getDescendants(ApexGrammarRuleKey.ARGUMENTS);
                    for (AstNode argument : arguments) {
                        AstNode prefix = argument.getPreviousAstNode();
                        AstNode method = prefix.getFirstDescendant(ApexGrammarRuleKey.NAME,
                                ApexGrammarRuleKey.ALLOWED_KEYWORDS_AS_IDENTIFIER_FOR_METHODS);
                        AstNode methodIdentifier = method.hasDescendant(ApexGrammarRuleKey.METHOD_IDENTIFIER)
                                ? method.getLastChild(ApexGrammarRuleKey.METHOD_IDENTIFIER) : method;
                        String methodName = methodIdentifier.getTokenValue();
                        if (methodIsAsync(astNode, methodName)) {
                            getContext().createLineViolation(this,
                                    "Method \"{0}\" is Async, should not be called withing a loop.",
                                    method, methodIdentifier.getTokenOriginalValue());
                        }
                    }
                }
            }
        }
    }

    private boolean methodIsAsync(AstNode astNode, String methodName) {
        AstNode firstAncestor = astNode.getFirstAncestor(ApexGrammarRuleKey.TYPE_DECLARATION);
        loadMethods(firstAncestor);
        if (!methods.isEmpty()) {
            for (AstNode method : methods) {
                String name = method.getFirstChild(ApexGrammarRuleKey.METHOD_IDENTIFIER).getTokenValue();
                if (name.equals(methodName)) {
                    AstNode member = method.getFirstAncestor(ApexGrammarRuleKey.CLASS_OR_INTERFACE_MEMBER);
                    AstNode modifiers = member.getFirstChild(ApexGrammarRuleKey.MODIFIERS);
                    if (modifiers.hasDescendant(ApexGrammarRuleKey.ANNOTATION)) {
                        List<AstNode> annotations = modifiers.getChildren(ApexGrammarRuleKey.ANNOTATION);
                        for (AstNode annotation : annotations) {
                            String annotationValue = annotation.getFirstChild(ApexGrammarRuleKey.NAME).getTokenValue();
                            if (annotationValue.equalsIgnoreCase(FUTURE)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void loadMethods(AstNode ancestor) {
        if(methods == null) {
            methods = ancestor.getDescendants(ApexGrammarRuleKey.METHOD_DECLARATION);
        }
    }
}
