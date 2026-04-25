package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.ArrayDeque;
import java.util.Deque;

// TODO: 实验三: 实现语义分析
public class SemanticAnalyzer implements ActionObserver {
    private SymbolTable symbolTable;
    private final Deque<Attribute> stack = new ArrayDeque<>();

    @Override
    public void whenAccept(Status currentStatus) {
        // TODO: 该过程在遇到 Accept 时要采取的代码动作
        stack.clear();
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO: 该过程在遇到 reduce production 时要采取的代码动作
        switch (production.index()) {
            case 4 -> {
                final var id = stack.pop();
                final var type = stack.pop();
                symbolTable.get(id.token.getText()).setType(type.type);
                stack.push(Attribute.empty());
            }
            case 5 -> {
                stack.pop();
                stack.push(Attribute.type(SourceCodeType.Int));
            }
            default -> {
                for (int i = 0; i < production.body().size(); i++) {
                    stack.pop();
                }
                stack.push(Attribute.empty());
            }
        }
    }

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO: 该过程在遇到 shift 时要采取的代码动作
        stack.push(Attribute.token(currentToken));
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO: 设计你可能需要的符号表存储结构
        // 如果需要使用符号表的话, 可以将它或者它的一部分信息存起来, 比如使用一个成员变量存储
        this.symbolTable = table;
    }

    private record Attribute(Token token, SourceCodeType type) {
        static Attribute empty() {
            return new Attribute(null, null);
        }

        static Attribute token(Token token) {
            return new Attribute(token, null);
        }

        static Attribute type(SourceCodeType type) {
            return new Attribute(null, type);
        }
    }
}
