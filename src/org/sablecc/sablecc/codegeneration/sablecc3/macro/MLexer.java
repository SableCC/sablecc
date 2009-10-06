/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.sablecc.codegeneration.sablecc3.macro;

import java.util.LinkedList;
import java.util.List;

public class MLexer {

    private final List<Object> ePackage = new LinkedList<Object>();

    private final List<Object> eMarkerDeclaration = new LinkedList<Object>();

    private final List<Object> eSetMarkerDeclaration = new LinkedList<Object>();

    private final List<Object> eAcceptMarkerDeclaration = new LinkedList<Object>();

    public MLexer() {

    }

    public MMarkerDeclaration newMarkerDeclaration(
            String pName) {

        MMarkerDeclaration lMarkerDeclaration = new MMarkerDeclaration(pName);
        this.eMarkerDeclaration.add(lMarkerDeclaration);
        return lMarkerDeclaration;
    }

    public MSetMarkerDeclaration newSetMarkerDeclaration(
            String pName) {

        MSetMarkerDeclaration lSetMarkerDeclaration = new MSetMarkerDeclaration(
                pName);
        this.eSetMarkerDeclaration.add(lSetMarkerDeclaration);
        return lSetMarkerDeclaration;
    }

    public MAcceptMarkerDeclaration newAcceptMarkerDeclaration(
            String pName) {

        MAcceptMarkerDeclaration lAcceptMarkerDeclaration = new MAcceptMarkerDeclaration(
                pName);
        this.eAcceptMarkerDeclaration.add(lAcceptMarkerDeclaration);
        return lAcceptMarkerDeclaration;
    }

    public MPackage newPackage(
            String pPackage) {

        MPackage lPackage = new MPackage(pPackage);
        this.ePackage.add(lPackage);
        return lPackage;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(new MHeader().toString());
        sb.append(System.getProperty("line.separator"));
        sb.append("package ");
        for (Object oPackage : this.ePackage) {
            sb.append(oPackage.toString());
        }
        sb.append("lexer;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("import ");
        for (Object oPackage : this.ePackage) {
            sb.append(oPackage.toString());
        }
        sb.append("node.*;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("import java.io.*;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("public class Lexer {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  private final BufferedReader br;");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("  private final StringBuilder buffer = new StringBuilder();");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private boolean eof;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private int line = 1;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private int pos = 1;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private boolean last_is_cr;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private StringBuilder sb;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private int acceptLine;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private int acceptPos;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private int current_sb_length;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  private boolean sb_contains_eof;");
        sb.append(System.getProperty("line.separator"));
        for (Object oMarkerDeclaration : this.eMarkerDeclaration) {
            sb.append(oMarkerDeclaration.toString());
        }
        sb.append("  private Token token;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        for (Object oSetMarkerDeclaration : this.eSetMarkerDeclaration) {
            sb.append(oSetMarkerDeclaration.toString());
        }
        sb.append(System.getProperty("line.separator"));
        sb.append("  public Lexer(Reader reader) {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.br = new BufferedReader(reader);");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  public Token peek()");
        sb.append(System.getProperty("line.separator"));
        sb.append("      throws LexerException, IOException {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    while(this.token == null) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("      this.token = internalNext();");
        sb.append(System.getProperty("line.separator"));
        sb.append("    };");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    return this.token;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  public Token next()");
        sb.append(System.getProperty("line.separator"));
        sb.append("      throws LexerException, IOException {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    Token token = peek();");
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.token = null;");
        sb.append(System.getProperty("line.separator"));
        sb.append("    return token;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  Token internalNext()");
        sb.append(System.getProperty("line.separator"));
        sb.append("      throws LexerException, IOException {");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    boolean first = true;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    StringBuilder sb = new StringBuilder();");
        sb.append(System.getProperty("line.separator"));
        sb.append("    State state = S_0.instance;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb
                .append("    while(state.getStateType() == State.StateType.TRANSITION) {");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("      TransitionState transitionState = (TransitionState) state;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      this.current_sb_length = sb.length();");
        sb.append(System.getProperty("line.separator"));
        sb.append("      transitionState.setMarker(this);");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      Symbol symbol;");
        sb.append(System.getProperty("line.separator"));
        sb.append("      if(buffer.length() > 0) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        char c = buffer.charAt(0);");
        sb.append(System.getProperty("line.separator"));
        sb.append("        buffer.deleteCharAt(0);");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("        sb.append(c);");
        sb.append(System.getProperty("line.separator"));
        sb.append("        symbol = Symbol.getSymbol(c);");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      else if(this.eof) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        if(sb.length() == 0) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          return new EOF(this.line, this.pos);");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("        this.sb_contains_eof = true;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        symbol = Symbol.Symbol_end;");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      else {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        int i = br.read();");
        sb.append(System.getProperty("line.separator"));
        sb.append("        if(i == -1) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.eof = true;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          br.close();");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("          if(sb.length() == 0) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("            return new EOF(this.line, this.pos);");
        sb.append(System.getProperty("line.separator"));
        sb.append("          }");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.sb_contains_eof = true;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          symbol = Symbol.Symbol_end;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("        else {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          char c = (char) i;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("          sb.append(c);");
        sb.append(System.getProperty("line.separator"));
        sb.append("          symbol = Symbol.getSymbol(c);");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      if(symbol == null) {");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("        throw new LexerException(sb.charAt(0), this.line, this.pos);");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      state = transitionState.getTarget(symbol);");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      if(state == null) {");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("        throw new LexerException(sb.charAt(0), this.line, this.pos);");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("    }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    FinalState finalState = (FinalState) state;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.sb = sb;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    return finalState.getToken(this);");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  int getAcceptLine() {");
        sb.append(System.getProperty("line.separator"));
        sb.append("    return this.acceptLine;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  int getAcceptPos() {");
        sb.append(System.getProperty("line.separator"));
        sb.append("    return this.acceptPos;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  private void updateLinePos(String text) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("    if(this.last_is_cr) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("      if(text.length() > 0) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        if(text.charAt(0) != 10) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.last_is_cr = false;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.line++;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.pos = 1;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      else if(this.buffer.length() > 0) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        if(this.buffer.charAt(0) != 10) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.last_is_cr = false;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.line++;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.pos = 1;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("    }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.acceptLine = line;");
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.acceptPos = pos;");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("    for(int i = 0; i < text.length(); i++) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("      char c = text.charAt(i);");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      if(c == 10) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        this.line++;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        this.pos = 1;");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      else {");
        sb.append(System.getProperty("line.separator"));
        sb.append("        if(last_is_cr) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.line++;");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.pos = 2;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("        else {");
        sb.append(System.getProperty("line.separator"));
        sb.append("          this.pos++;");
        sb.append(System.getProperty("line.separator"));
        sb.append("        }");
        sb.append(System.getProperty("line.separator"));
        sb.append("      }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("      this.last_is_cr = c == 13;");
        sb.append(System.getProperty("line.separator"));
        sb.append("    }");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        sb.append("  String accept(int backCount) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("    if(this.sb_contains_eof) {");
        sb.append(System.getProperty("line.separator"));
        sb.append("      this.sb_contains_eof = false;");
        sb.append(System.getProperty("line.separator"));
        sb.append("      backCount--;");
        sb.append(System.getProperty("line.separator"));
        sb.append("    }");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("    String text = this.sb.toString().substring(0, this.sb.length() - backCount);");
        sb.append(System.getProperty("line.separator"));
        sb
                .append("    String leftover = this.sb.toString().substring(this.sb.length() - backCount, this.sb.length());");
        sb.append(System.getProperty("line.separator"));
        sb.append("    this.buffer.insert(0, leftover);");
        sb.append(System.getProperty("line.separator"));
        sb.append("    updateLinePos(text);");
        sb.append(System.getProperty("line.separator"));
        sb.append("    return text;");
        sb.append(System.getProperty("line.separator"));
        sb.append("  }");
        sb.append(System.getProperty("line.separator"));
        sb.append(System.getProperty("line.separator"));
        for (Object oAcceptMarkerDeclaration : this.eAcceptMarkerDeclaration) {
            sb.append(oAcceptMarkerDeclaration.toString());
        }
        sb.append(System.getProperty("line.separator"));
        sb.append("}");
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

}
