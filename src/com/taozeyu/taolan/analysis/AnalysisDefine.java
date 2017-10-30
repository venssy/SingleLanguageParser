package com.taozeyu.taolan.analysis;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.taozeyu.taolan.analysis.NonTerminalSymbol.Exp;
import com.taozeyu.taolan.analysis.Token.Type;
import com.taozeyu.taolan.analysis.node.ArrayNode;
import com.taozeyu.taolan.analysis.node.ChunkNode;
import com.taozeyu.taolan.analysis.node.CommandNode;
import com.taozeyu.taolan.analysis.node.DefineFunctionNode;
import com.taozeyu.taolan.analysis.node.DefineVariableNode;
import com.taozeyu.taolan.analysis.node.ElementNode;
import com.taozeyu.taolan.analysis.node.ElementNode.ElementType;
import com.taozeyu.taolan.analysis.node.OperateNode;
import com.taozeyu.taolan.analysis.node.StartChunkNode;
import com.taozeyu.taolan.analysis.node.WhenNode;
import com.taozeyu.taolan.analysis.node.ExpressionNode;
import com.taozeyu.taolan.analysis.node.ForEachLoopNode;
import com.taozeyu.taolan.analysis.node.IfElseNode;
import com.taozeyu.taolan.analysis.node.InvokerNode;
import com.taozeyu.taolan.analysis.node.LambdaNode;
import com.taozeyu.taolan.analysis.node.LoopChunkNode;
import com.taozeyu.taolan.analysis.node.TryCatchNode;

class AnalysisDefine {
    private static final HashMap<Exp, ElementNodeSupplier> containerMap = new HashMap<Exp, ElementNodeSupplier>();

    private static abstract class ElementNodeSupplier {
        public AnalysisNode get() {
            return null;
        }
    }

    public static class containerNodeSupplier extends ElementNodeSupplier {
        public AnalysisNode get() {
            return new DefaultContainerNode();
        }
    }

    /**
     * 仅可用于双目运算符
     */
    public static class expressionDefaultNodeSupplier extends ElementNodeSupplier {
        public AnalysisNode get() {
            return new ExpressionDefaultNode();
        }
    }

    ;
    private static final Pattern IntegerPattern = Pattern.compile("\\d+");

    static {
        creator(new Exp[]{Exp.Number}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) {
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        if (Type.Number != token.type) {
                            return;
                        }
                        Matcher matcher = IntegerPattern.matcher(token.value);
                        if (!matcher.matches()) {
                            throw new SyntacticAnalysisException(token);
                        }
                        if (this.value == null) {
                            this.type = ElementType.Integer;
                            this.value = token.value;

                        } else {
                            this.type = ElementType.Number;
                            this.value += "." + token.value;
                        }
                    }

                };
            }
        });
        creator(new Exp[]{Exp.Variable}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) {
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        if (Type.Identifier == token.type) {
                            this.type = ElementType.Variable;
                            this.value = token.value;

                        } else if (Type.Sign == token.type) {
                            if ("@".equals(token.value)) {
                                this.fromThis = true;
                            } else if ("@@".equals(token.value)) {
                                this.fromConstructor = true;
                            }
                        }
                    }
                };
            }
        });
        creator(new Exp[]{Exp.String}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {
                    @Override
                    public void match(AnalysisNode analysisNode) {
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        if (Type.Identifier == token.type || Type.String == token.type) {
                            this.type = ElementType.String;
                            this.value = token.value;
                        }
                    }
                };
            }
        });
        creator(new Exp[]{Exp.This, Exp.Null}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {
                    @Override
                    public void match(AnalysisNode analysisNode) {
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        if ("this".equals(token.value)) {
                            this.type = ElementType.This;
                        } else if ("null".equals(token.value)) {
                            this.type = ElementType.Null;
                        }
                    }
                };
            }
        });
        creator(new Exp[]{Exp.Boolean}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {
                    @Override
                    public void match(AnalysisNode analysisNode) {
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        this.type = ElementType.Boolean;
                        this.value = token.value;
                    }
                };
            }
        });
        /*creator(new Exp[] {Exp.RegEx}, new ElementNodeSupplier(){
            public AnalysisNode get() {
                return new ElementNode() {

                @Override
                public void match(AnalysisNode analysisNode) {}

                @Override
                public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                    if(Type.RegEx == token.type) {
                        this.type = ElementType.RegEx;
                        this.value = token.value;
                    }
                }
            
                };
            }
        });*/
        creator(new Exp[]{Exp.L11Expression}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ExpressionNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        analysisNode = tryGetSingleElement(analysisNode);

                        if (analysisNode instanceof ExpressionNode) {
                            this.expressionOperands[0] = (ExpressionNode) analysisNode;
                        } else if (analysisNode instanceof ElementNode) {
                            this.elementOperands[0] = (ElementNode) analysisNode;
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                    }

                };
            }
        });
        creator(new Exp[]{
                Exp.L10Expression, Exp.L8Expression,
                Exp.L7Expression, Exp.L6Expression,
                Exp.L5Expression, Exp.L4Expression,
                Exp.L3Expression, Exp.L2Expression,
                Exp.L0Expression,

        }, new expressionDefaultNodeSupplier());

        creator(new Exp[]{Exp.Chunk}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ChunkNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        if (analysisNode instanceof ExpressionNode) {
                            ExpressionNode expression = (ExpressionNode) analysisNode;
                            clearRedundancy(expression);
                            analysisNode = expression;
                        }
                        lineList.add(analysisNode);
                    }

                    @Override
                    public void match(TerminalSymbol token) {
                    }

                };
            }
        });
        creator(new Exp[]{Exp.StartChunk}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new StartChunkNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        if (analysisNode instanceof ChunkNode) {
                            chunk = (ChunkNode) analysisNode;
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) {
                    }

                };
            }
        });

        creator(new Exp[]{Exp.Operate}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new OperateNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {

                        if (analysisNode instanceof ExpressionNode) {
                            expression = (ExpressionNode) analysisNode;
                            clearRedundancy(expression);

                        } else if (analysisNode instanceof WhenNode) {
                            condition = (WhenNode) analysisNode;
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) {
                    }

                };
            }
        });
        creator(new Exp[]{Exp.DefineVariableElement}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new DefineVariableNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {

                        if (analysisNode instanceof ExpressionNode) {
                            initValue = (ExpressionNode) analysisNode;
                            clearRedundancy(initValue);
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) {
                        if (token.type == Type.Identifier) {
                            variableName = token.value;
                        }
                    }

                };
            }
        });

        creator(new Exp[]{
                Exp.Invoker, Exp.InvokerBraceless,
                Exp.InvokerBanLambda, Exp.InvokerBracelessBanLambda
        }, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new InvokerNode() {

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        if (analysisNode instanceof ExpressionNode) {
                            ExpressionNode expression = (ExpressionNode) analysisNode;
                            clearRedundancy(expression);
                            paramList.add(expression);

                        } else if (analysisNode instanceof LambdaNode) {
                            lambda = (LambdaNode) analysisNode;
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                    }

                };
            }
        });
        creator(new Exp[]{Exp.Array}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {
                    {
                        array = new ArrayNode();
                        type = ElementType.Array;
                    }

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        if (analysisNode instanceof ExpressionNode) {
                            ExpressionNode expression = (ExpressionNode) analysisNode;
                            clearRedundancy(expression);
                            array.content.add(expression);
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                    }

                };
            }
        });
        creator(new Exp[]{Exp.Container}, new ElementNodeSupplier() {
            public AnalysisNode get() {
                return new ElementNode() {
                    {
                        container = new com.taozeyu.taolan.analysis.node.ContainerNode();
                        type = ElementType.Container;
                    }

                    private String keyBuffered = null;

                    @Override
                    public void match(AnalysisNode analysisNode) throws SyntacticAnalysisException {
                        if (analysisNode instanceof ExpressionNode) {
                            ExpressionNode expression = (ExpressionNode) analysisNode;
                            clearRedundancy(expression);
                            container.content.put(keyBuffered, expression);
                        } else if (analysisNode instanceof ElementNode) {
                            keyBuffered = ((ElementNode) analysisNode).value;
                        }
                    }

                    @Override
                    public void match(TerminalSymbol token) throws SyntacticAnalysisException {
                        if (token.type == Type.Identifier) {
                            keyBuffered = token.value;
                        }
                    }

                };
            }
        });
    }

    static AnalysisNode createContainer(Exp exp) {
        //Supplier<AnalysisNode> supplier = containerMap.get(exp);
        ElementNodeSupplier supplier = containerMap.get(exp);
        AnalysisNode analysisNode = null;
        if (supplier != null) {
            analysisNode = supplier.get();
            analysisNode.setExp(exp);
        }
        return analysisNode;
    }

    private static void clearRedundancy(ExpressionNode node) {
        if (node.sign == null && node.invoker == null && node.isExpression(0) && !node.any(1)) {
            ExpressionNode child = node.getExpressionAt(0);
            AnalysisNode target = tryGetSingleElement(child);
            if (target instanceof ExpressionNode) {
                node.copy((ExpressionNode) target);
            } else {
                node.clear();
                node.elementOperands[0] = (ElementNode) target;
            }

        } else {
            for (int i = 0; i < 3; ++i) {
                if (node.isExpression(i)) {
                    ExpressionNode child = node.getExpressionAt(i);
                    AnalysisNode target = tryGetSingleElement(child);
                    if (target instanceof ElementNode) {
                        node.expressionOperands[i] = null;
                        node.elementOperands[i] = (ElementNode) target;
                    }
                }
            }
        }
    }

    /*private static void creator(Exp[] exps, Supplier<AnalysisNode> supplier) {
        for(Exp exp:exps) {
            containerMap.put(exp, supplier);
        }
    }*/

    private static void creator(Exp[] exps, ElementNodeSupplier supplier) {
        for (Exp exp : exps) {
            containerMap.put(exp, supplier);
        }
    }

    private static class DefaultContainerNode extends AnalysisNode implements Iterable<Object> {

        private final LinkedList<Object> containerList = new LinkedList<>();

        @Override
        public Iterator<Object> iterator() {
            return containerList.iterator();
        }

        @Override
        public void match(AnalysisNode analysisNode) {
            containerList.add(analysisNode);
        }

        @Override
        public void match(TerminalSymbol token) {
            containerList.add(token);
        }

        @Override
        public void print(int retractNum, PrintStream out) {
        }
    }

    private static class ExpressionDefaultNode extends ExpressionNode {

        private TerminalSymbol lastToken = null;

        @Override
        public void match(AnalysisNode node) throws SyntacticAnalysisException {
            if (node instanceof InvokerNode) {
                if (isSecondPositionFilled()) {
                    ExpressionNode forked = this.fork();
                    clear();
                    expressionOperands[0] = forked;
                }
                InvokerNode invokerNode = (InvokerNode) node;
                if (!invokerNode.paramList.isEmpty() || invokerNode.lambda != null) {
                    invoker = invokerNode;
                }
            } else {
                node = tryGetSingleElement(node);
                if (this.any(0)) {
                    forkIfSecondPositionFilled();
                    setAnalysisNodeAt(node, 1);

                } else {
                    setAnalysisNodeAt(node, 0);
                }
            }
        }

        @Override
        public void match(TerminalSymbol token) throws SyntacticAnalysisException {
            if (this.sign == null) {
                if (token.type == Type.Sign ||
                        (token.type == Type.Keyword && (token.value.equals("instanceof") || token.value.equals("is")))) {
                    this.sign = token.value;
                    this.lastToken = token;
                }
            } else if (token.type == Type.Identifier) {
                forkIfSecondPositionFilled();
                ElementNode element = createVariableElement(token.value);
                setAnalysisNodeAt(element, 1);
            }
        }

        private void forkIfSecondPositionFilled() {
            if (isSecondPositionFilled()) {
                ExpressionNode forked = this.fork();
                this.clear();
                this.expressionOperands[0] = forked;
                this.sign = lastToken.value;
                lastToken = null;
            }
        }

        private void setAnalysisNodeAt(AnalysisNode analysisNode, int index) {
            if (analysisNode instanceof ExpressionNode) {
                this.expressionOperands[index] = (ExpressionNode) analysisNode;
            } else {
                this.elementOperands[index] = (ElementNode) analysisNode;
            }
        }
    }

    private static AnalysisNode tryGetSingleElement(AnalysisNode node) {
        if (node instanceof ExpressionNode) {
            ExpressionNode expression = (ExpressionNode) node;
            clearRedundancy(expression);
            if (expression.sign == null && expression.invoker == null && expression.isElement(0)) {
                node = expression.getElementAt(0);
            }
        }
        return node;
    }

    private static ElementNode createVariableElement(String varName) {
        ElementNode element = new ElementNode() {
            @Override
            public void match(TerminalSymbol token) {
            }

            @Override
            public void match(AnalysisNode analysisNode) {
            }
        };
        element.type = ElementType.Variable;
        element.value = varName;
        return element;
    }
}
