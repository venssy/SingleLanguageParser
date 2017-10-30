package com.taozeyu.taolan.analysis;

import java.util.HashMap;

import com.taozeyu.taolan.analysis.NonTerminalSymbol.Exp;
import com.taozeyu.taolan.analysis.Token.Type;

class SyntacticDefine {

    private final static HashMap<Exp, NonTerminalSymbol> expContainer = new HashMap<>();

    static {
        NonTerminalSymbol[] defineNodes = new NonTerminalSymbol[] {
                //基本单元
                node(Exp.Number).or(token(Type.Number), node().or(token(Type.Sign, "."), token(Type.Number)).sign('?')),
                /*node(Exp.Variable).or(
                        node().or(token(Type.Sign, "@")).or(token(Type.Sign, "@@")).sign('?'),
                        token(Type.Identifier)),*/
				node(Exp.Variable).or(token(Type.Identifier)),
                node(Exp.String).or(token(Type.String)),

                node(Exp.SpaceOrEnter).or(token(Type.NewLine)).or(token(Type.Space)).sign('*'),
                node(Exp.Space).or(token(Type.Space)).sign('*'),
                node(Exp.SplitSpaceSign).or(token(Type.Space)).sign('+'),

                node(Exp.Enter).or(node().or(token(Type.Space)).sign('?'), token(Type.NewLine)),

                node(Exp.Null).or(token(Type.Keyword, "null")),

                node(Exp.Boolean).or(token(Type.Keyword, "true"))
                        .or(token(Type.Keyword, "false")),

                node(Exp.Element).or(Exp.Number)
                        .or(Exp.Variable)
                        .or(Exp.String)
                        .or(Exp.Array)
                        .or(Exp.Map)
                        .or(Exp.Container)
                        .or(Exp.Null)
                        .or(Exp.Boolean),

            //表达式相关
                //赋值表达式
            //node(Exp.L0Expression).or(Exp.L1Expression, node().or(Exp.L0Sign, Exp.L1Expression).sign('*')),

            /*node(Exp.L0Sign).or(token(Type.Sign, "="))
                            .or(token(Type.Sign, "+=")).or(token(Type.Sign, "-="))
                            .or(token(Type.Sign, "*=")).or(token(Type.Sign, "/="))
                            .or(token(Type.Sign, "&&=")).or(token(Type.Sign, "=~"))
                            .or(token(Type.Sign, "||=")).or(token(Type.Sign, "&="))
                            .or(token(Type.Sign, "<<")),*/


            // 二元判断
            node(Exp.L1Expression).or(Exp.L2Expression, node().or(
                    token(Type.Sign, "?"), Exp.L2Expression, token(Type.Sign, ":"), Exp.L2Expression
            ).sign('?')),

            node(Exp.L2Expression).or(Exp.L3Expression, node().or(Exp.L2Sign, Exp.L3Expression).sign('*')),

            node(Exp.L2Sign).or(token(Type.Sign, "||")),

            node(Exp.L3Expression).or(Exp.L4Expression, node().or(Exp.L3Sign, Exp.L4Expression).sign('*')),

            node(Exp.L3Sign).or(token(Type.Sign, "&&")),

            node(Exp.L4Expression).or(Exp.L5Expression, node().or(Exp.L4Sign, Exp.L5Expression).sign('*')),

            node(Exp.L4Sign).or(token(Type.Sign, "^")),

            node(Exp.L5Expression).or(Exp.L6Expression, node().or(Exp.L5Sign, Exp.L6Expression).sign('*')),

            node(Exp.L5Sign).or(token(Type.Sign, "=="))
                            .or(token(Type.Sign, "!=")),

            node(Exp.L6Expression).or(Exp.L7Expression, node().or(Exp.L6Sign, Exp.L7Expression).sign('*')),

            node(Exp.L6Sign).or(token(Type.Sign, ">")).or(token(Type.Sign, "<"))
                            .or(token(Type.Sign, ">=")).or(token(Type.Sign, "<=")),
                            //.or(token(Type.Keyword, "instanceof"))
                            //.or(token(Type.Keyword, "is")),

            node(Exp.L6_Expression).or(Exp.L7Expression, node().or(token(Type.Sign, "&"), Exp.L7Expression).sign('*')),

            node(Exp.L7Expression).or(Exp.L8Expression, node().or(Exp.L7Sign, Exp.L8Expression).sign('*')),

            node(Exp.L7Sign).or(token(Type.Sign, "+"))
                            .or(token(Type.Sign, "-")),

            node(Exp.L8Expression).or(Exp.L9Expression, node().or(Exp.L8Sign, Exp.L9Expression).sign('*')),

            node(Exp.L8Sign).or(token(Type.Sign, "*"))
                            .or(token(Type.Sign, "/"))
                            .or(token(Type.Sign, "%")),

            node(Exp.L9Expression).or(node().or(Exp.L9Sign).sign('?'), Exp.L10Expression),

            node(Exp.L9Sign).or(token(Type.Sign, "+")).or(token(Type.Sign, "-")).or(token(Type.Sign, "!")),

            node(Exp.L10Expression).or(
                    Exp.Space,
                    Exp.L11Expression,
                    Exp.L10Tail,
                    node().or(Exp.Invoker, Exp.Space, Exp.L10Tail)
                          .or(Exp.SplitSpaceSign, node().or(Exp.InvokerBraceless).sign('?'))
                    .sign('?'),
                    Exp.Space),

            node(Exp.L10Tail).or(Exp.L10TailOperation).sign('*'),


            node(Exp.L10TailOperation).or(token(Type.Sign, "."), Exp.SpaceOrEnter, token(Type.Identifier))
                                      .or(token(Type.Sign, "["), Exp.L1Expression, token(Type.Sign, "]")),
                                      //.or(token(Type.Sign, ".."), Exp.L10Expression)
                                      //.or(token(Type.Sign, "<<"), Exp.L10Expression),

                // 单元，或为一个有返回值的表达式->单元
            node(Exp.L11Expression).or(
                    node().or(token(Type.Sign, "("), Exp.L0Expression, token(Type.Sign, ")"))
                          .or(Exp.Element)),

            //控制流语法
            node(Exp.StartChunk).or(Exp.Chunk),

            node(Exp.Chunk).or(
                    Exp.SpaceOrEnter,
                    node().or(Exp.Line, Exp.Space,
                              node().or(token(Type.NewLine), Exp.SpaceOrEnter)
                                    .or(token(Type.EndSymbol))).sign('?')
            ).sign('*'),

            node(Exp.Line).or(Exp.Operate)
                          .or(Exp.DefineVariable),

            //node(Exp.Operate).or(Exp.L0Expression, Exp.When),

            node(Exp.ParamsList).or(token(Type.Identifier), Exp.Space,
                          node().or(token(Type.Sign, ","), Exp.SpaceOrEnter, token(Type.Identifier), Exp.Space)
                          .sign('*'))
                .sign('?'),

            //语法糖
            node(Exp.List).or(Exp.L0Expression, node().or(Exp.Space, token(Type.Sign, ","), Exp.SpaceOrEnter, Exp.L0Expression).sign('*')),
            node(Exp.Map).or(Exp.MapEntry, node().or(Exp.Space, token(Type.Sign, ","), Exp.SpaceOrEnter, Exp.MapEntry).sign('*')),
            node(Exp.MapEntry).or(
                    node().or(Exp.String).or(token(Type.Identifier)),
                    Exp.SpaceOrEnter, Exp.SpaceOrEnter, token(Type.Sign, ":"), Exp.SpaceOrEnter, Exp.SpaceOrEnter,
                    Exp.L0Expression),

            node(Exp.Invoker).or(node()
                    .or(token(Type.Sign, "("),
                              node().or(Exp.SpaceOrEnter, Exp.ParamList, Exp.SpaceOrEnter).sign('?'),
                              token(Type.Sign, ")")),
                    Exp.Space,
                    node().or(Exp.Lambda).sign('?')),

            node(Exp.InvokerBraceless).or(
                    node().or(Exp.ParamListBanTokens, Exp.Space).sign('?'),
                    node().or(Exp.Lambda).sign('?')),

            node(Exp.InvokerBanLambda).or(node()
                    .or(token(Type.Sign, "("), Exp.SpaceOrEnter,
                        Exp.ParamList, Exp.SpaceOrEnter,
                        token(Type.Sign, ")"))),

            node(Exp.InvokerBracelessBanLambda).or(Exp.ParamListBanTokens, Exp.Space).sign('?'),

            node(Exp.ParamListBanTokens).or(
                    node().or(Exp.L0ParamExpression).ban(
                            token(Type.Sign, "+"), token(Type.Sign, "-"), token(Type.Sign, "[")
                    ),
                    node().or(Exp.Space, token(Type.Sign, ","), Exp.SpaceOrEnter, Exp.L0ParamExpression).sign('*')),

            node(Exp.ParamList).or(
                    node().or(Exp.L0ParamExpression),
                    node().or(Exp.Space, token(Type.Sign, ","), Exp.SpaceOrEnter, Exp.L0ParamExpression).sign('*')),

            node(Exp.Array).or(token(Type.Sign, "["), Exp.SpaceOrEnter, Exp.List, Exp.SpaceOrEnter, token(Type.Sign, "]")),
            node(Exp.Container).or(token(Type.Sign, "{"), Exp.SpaceOrEnter, Exp.Map, Exp.SpaceOrEnter, token(Type.Sign, "}")),

        };
        for(NonTerminalSymbol node:defineNodes)
        {
            expContainer.put(node.exp, node);
        }
        FirstSetConstructor firstSetConstructor = new FirstSetConstructor(expContainer);
        firstSetConstructor.build();
    }

    static NonTerminalSymbol getNonTerminalSymbol(Exp exp) {
        return expContainer.get(exp);
    }

    private static NonTerminalSymbol node(Exp exp) {
        return new NonTerminalSymbol(exp);
    }

    private static NonTerminalSymbol node() {
        return new NonTerminalSymbol(null);
    }

    private static TerminalSymbol token(Type type, String value) {
        return new TerminalSymbol(type, value);
    }

    private static TerminalSymbol token(Type type) {
        return new TerminalSymbol(type, null);
    }
}
