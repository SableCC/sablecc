/* This file is part of SableCC ( http://sablecc.org ).
 *
 * See the NOTICE file distributed with this work for copyright information.
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

package org.sablecc.objectmacro.launcher;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import org.sablecc.exception.InternalException;
import org.sablecc.objectmacro.syntax3.lexer.Lexer;
import org.sablecc.objectmacro.syntax3.lexer.LexerException;
import org.sablecc.objectmacro.syntax3.node.TBody;
import org.sablecc.objectmacro.syntax3.node.TDquote;
import org.sablecc.objectmacro.syntax3.node.TEnd;
import org.sablecc.objectmacro.syntax3.node.TIndentCommand;
import org.sablecc.objectmacro.syntax3.node.TInsertCommand;
import org.sablecc.objectmacro.syntax3.node.TRBrace;
import org.sablecc.objectmacro.syntax3.node.Token;

public class CustomLexer extends
        Lexer {

    public CustomLexer(
            PushbackReader in) {

        super(in);
    }

    private List<State> states = new LinkedList<>();

    private List<Token> tokens_command = new LinkedList<>();

    @Override
    protected void filter()
            throws LexerException,
                IOException {

        if (this.token instanceof TDquote) {
            if (this.state != State.STRING) {
                this.states.add(this.state);
                this.state = State.STRING;
            }
            else {
                this.state = getLastState();
            }
        }
        else if (this.token instanceof TInsertCommand) {
            this.states.add(this.state);
            this.state = State.COMMAND;
        }
        else if (this.token instanceof TIndentCommand) {
            this.states.add(this.state);
            this.state = State.COMMAND;
            this.tokens_command.add(this.token);
        }
        else if (this.token instanceof TRBrace) {
            if (this.states.size() == 0) {
                throw new InternalException(
                        "There must be at least one state.");
            }
            else {
                this.state = getLastState();
            }
        }
        else if (this.token instanceof TEnd) {
            if (this.tokens_command.size() > 0 && this.state == State.MACRO_BODY
                    && getLastToken() instanceof TBody) {

                this.state = State.TOP_LEVEL;
            }
        }
        else if (this.token instanceof TBody) {
            this.tokens_command.add(this.token);
        }
    }

    private State getLastState() {

        int lastStateIndex = this.states.size() - 1;
        return this.states.remove(lastStateIndex);
    }

    private Token getLastToken() {

        int lastTokenIndex = this.tokens_command.size() - 1;
        return this.tokens_command.remove(lastTokenIndex);
    }
}
