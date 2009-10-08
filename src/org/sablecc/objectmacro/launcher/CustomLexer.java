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

import java.io.*;

import org.sablecc.objectmacro.syntax3.lexer.*;
import org.sablecc.objectmacro.syntax3.node.*;

public class CustomLexer
        extends Lexer {

    private State previousState = null;

    private int longCommentDepth = 0;

    private int commandDepth = 0;

    public CustomLexer(
            PushbackReader in) {

        super(in);
    }

    @Override
    protected void filter()
            throws LexerException, IOException {

        if (this.token instanceof TMacroCommand
                || this.token instanceof TTextBlockCommand) {

            this.state = State.COMMAND;

            this.commandDepth++;
        }
        else if (this.token instanceof TExpandCommand
                || this.token instanceof TInsertCommand) {

            this.state = State.COMMAND;
        }
        else if (this.token instanceof TEndCommand) {

            this.state = State.COMMAND;

            this.commandDepth--;
        }
        else if (this.token instanceof TShortCommentCommand) {

            this.state = State.SHORT_COMMENT;
        }
        else if (this.token instanceof TCommandTail) {

            if (this.commandDepth == 0) {
                this.state = State.TOP_LEVEL;
            }
            else {
                this.state = State.TEXT;
            }
        }
        else if (this.token instanceof TLongCommentStart) {

            if (this.longCommentDepth == 0) {
                this.previousState = this.state;
                this.state = State.LONG_COMMENT;
            }

            this.longCommentDepth++;
        }
        else if (this.token instanceof TLongCommentEnd) {

            this.longCommentDepth--;

            if (this.longCommentDepth == 0) {
                this.state = this.previousState;
                this.previousState = null;
            }
        }
    }
}
