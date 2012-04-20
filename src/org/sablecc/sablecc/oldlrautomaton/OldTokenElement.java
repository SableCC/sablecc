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

package org.sablecc.sablecc.oldlrautomaton;

import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.grammar.*;

public class OldTokenElement
        extends OldElement {

    private final OldToken oldToken;

    private final Element.TokenElement origin;

    private final String typeName;

    OldTokenElement(
            Grammar grammar,
            OldAlternative oldAlternative,
            int position,
            String shortName,
            OldToken oldToken,
            Element.TokenElement element) {

        super(oldAlternative, position, shortName);
        this.oldToken = oldToken;
        this.origin = element;

        if (!getToken().getName().equals("$end")) {
            LexerExpression lexerExpression = grammar
                    .getLexerExpression(getToken().getName());
            if (lexerExpression instanceof LexerExpression.NamedExpression) {
                LexerExpression.NamedExpression namedExpression = (LexerExpression.NamedExpression) lexerExpression;
                this.typeName = namedExpression.getName_CamelCase();
            }
            else {
                LexerExpression.InlineExpression inlineExpression = (LexerExpression.InlineExpression) lexerExpression;

                this.typeName = ""
                        + inlineExpression.getInternalName_CamelCase();
            }
        }
        else {
            this.typeName = "";
        }
    }

    public OldToken getToken() {

        return this.oldToken;
    }

    @Override
    public String getTypeName() {

        return this.typeName;
    }

    @Override
    public String toString() {

        return "[" + getName() + ":]" + this.oldToken.getName();
    }

    @Override
    public Element.TokenElement getOrigin() {

        return this.origin;
    }
}
