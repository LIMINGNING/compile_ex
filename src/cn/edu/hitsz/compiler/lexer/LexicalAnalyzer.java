package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String fileContent;
    private final List<Token> tokens = new ArrayList<>();

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        this.fileContent = FileUtils.readFile(path);
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        int length = fileContent.length();
        int i = 0;
        while (i < length) {
            char c = fileContent.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < length && Character.isDigit(fileContent.charAt(i))) {
                    sb.append(fileContent.charAt(i));
                    i++;
                }
                tokens.add(Token.normal("IntConst", sb.toString()));
            } else if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < length && (Character.isLetterOrDigit(fileContent.charAt(i)))) {
                    sb.append(fileContent.charAt(i));
                    i++;
                }
                String text = sb.toString();
                if (text.equals("int")) {
                    tokens.add(Token.simple("int"));
                } else if (text.equals("return")) {
                    tokens.add(Token.simple("return"));
                } else {
                    tokens.add(Token.normal("id", text));
                    if (!symbolTable.has(text)) {
                        symbolTable.add(text);
                    }
                }
            } else {
                switch (c) {
                    case '=':
                        tokens.add(Token.simple("="));
                        i++;
                        break;
                    case ',':
                        tokens.add(Token.simple(","));
                        i++;
                        break;
                    case ';':
                        tokens.add(Token.simple("Semicolon"));
                        i++;
                        break;
                    case '+':
                        tokens.add(Token.simple("+"));
                        i++;
                        break;
                    case '-':
                        tokens.add(Token.simple("-"));
                        i++;
                        break;
                    case '*':
                        tokens.add(Token.simple("*"));
                        i++;
                        break;
                    case '/':
                        tokens.add(Token.simple("/"));
                        i++;
                        break;
                    case '(':
                        tokens.add(Token.simple("("));
                        i++;
                        break;
                    case ')':
                        tokens.add(Token.simple(")"));
                        i++;
                        break;
                    default:
                        i++;
                        break;
                }
            }
        }
        tokens.add(Token.eof());
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
                path,
                StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList());
    }

}
