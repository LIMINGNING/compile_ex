package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.ir.InstructionKind;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

// TODO: 实验三: 实现 IR 生成

/**
 *
 */
public class IRGenerator implements ActionObserver {
    private final Deque<Attribute> stack = new ArrayDeque<>();
    private final List<Instruction> instructions = new ArrayList<>();

    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // TODO
        stack.push(Attribute.token(currentToken));
    }

    @Override
    public void whenReduce(Status currentStatus, Production production) {
        // TODO
        switch (production.index()) {
            case 6 -> reduceAssign();
            case 7 -> reduceReturn();
            case 8 -> reduceBinary(InstructionKind.ADD);
            case 9 -> reduceBinary(InstructionKind.SUB);
            case 10, 12 -> reduceSingle();
            case 11 -> reduceBinary(InstructionKind.MUL);
            case 13 -> reduceParentheses();
            case 14 -> reduceIdentifier();
            case 15 -> reduceImmediate();
            default -> reduceIgnored(production);
        }
    }


    @Override
    public void whenAccept(Status currentStatus) {
        // TODO
        stack.clear();
    }

    @Override
    public void setSymbolTable(SymbolTable table) {
        // TODO
    }

    public List<Instruction> getIR() {
        // TODO
        return List.copyOf(instructions);
    }

    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }

    private void reduceAssign() {
        final var source = stack.pop();
        stack.pop();
        final var target = stack.pop();
        instructions.add(Instruction.createMov(IRVariable.named(target.token.getText()), source.value));
        stack.push(Attribute.empty());
    }

    private void reduceReturn() {
        final var returnValue = stack.pop();
        stack.pop();
        instructions.add(Instruction.createRet(returnValue.value));
        stack.push(Attribute.empty());
    }

    private void reduceBinary(InstructionKind kind) {
        final var rhs = stack.pop();
        stack.pop();
        final var lhs = stack.pop();
        final var result = IRVariable.temp();

        switch (kind) {
            case ADD -> instructions.add(Instruction.createAdd(result, lhs.value, rhs.value));
            case SUB -> instructions.add(Instruction.createSub(result, lhs.value, rhs.value));
            case MUL -> instructions.add(Instruction.createMul(result, lhs.value, rhs.value));
            default -> throw new RuntimeException("Unsupported binary instruction kind: " + kind);
        }

        stack.push(Attribute.value(result));
    }

    private void reduceSingle() {
        stack.push(stack.pop());
    }

    private void reduceParentheses() {
        stack.pop();
        final var value = stack.pop();
        stack.pop();
        stack.push(value);
    }

    private void reduceIdentifier() {
        final var id = stack.pop();
        stack.push(Attribute.value(IRVariable.named(id.token.getText())));
    }

    private void reduceImmediate() {
        final var immediate = stack.pop();
        stack.push(Attribute.value(IRImmediate.of(Integer.parseInt(immediate.token.getText()))));
    }

    private void reduceIgnored(Production production) {
        for (int i = 0; i < production.body().size(); i++) {
            stack.pop();
        }
        stack.push(Attribute.empty());
    }

    private record Attribute(Token token, IRValue value) {
        static Attribute empty() {
            return new Attribute(null, null);
        }

        static Attribute token(Token token) {
            return new Attribute(token, null);
        }

        static Attribute value(IRValue value) {
            return new Attribute(null, value);
        }
    }
}
