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

package org.sablecc.sablecc.exception;

import org.sablecc.sablecc.errormessages.M_destination_is_not_directory;
import org.sablecc.sablecc.errormessages.M_duplicate_global_name_declaration;
import org.sablecc.sablecc.errormessages.M_grammar_not_file;
import org.sablecc.sablecc.errormessages.M_input_error;
import org.sablecc.sablecc.errormessages.M_invalid_argument;
import org.sablecc.sablecc.errormessages.M_invalid_argument_count;
import org.sablecc.sablecc.errormessages.M_invalid_long_option;
import org.sablecc.sablecc.errormessages.M_invalid_short_option;
import org.sablecc.sablecc.errormessages.M_invalid_suffix;
import org.sablecc.sablecc.errormessages.M_missing_destination_directory;
import org.sablecc.sablecc.errormessages.M_missing_grammar_file;
import org.sablecc.sablecc.errormessages.M_missing_long_option_operand;
import org.sablecc.sablecc.errormessages.M_missing_short_option_operand;
import org.sablecc.sablecc.errormessages.M_spurious_long_option_operand;
import org.sablecc.sablecc.errormessages.M_spurious_short_option_operand;
import org.sablecc.sablecc.structures.Name;
import org.sablecc.sablecc.syntax3.node.Token;

@SuppressWarnings("serial")
public class CompilerException
        extends RuntimeException {

    private CompilerException(
            String message) {

        super(message);

        if (message == null) {
            throw new InternalException("message may not be null");
        }
    }

    private CompilerException(
            String message,
            Throwable cause) {

        super(message, cause);

        if (message == null) {
            throw new InternalException("message may not be null");
        }

        if (cause == null) {
            throw new InternalException("cause may not be null");
        }
    }

    public static CompilerException invalid_argument(
            String argument_text,
            Throwable cause) {

        return new CompilerException(new M_invalid_argument(argument_text)
                .toString(), cause);
    }

    public static CompilerException missing_long_option_operand(
            String option_name,
            String operand_name) {

        return new CompilerException(new M_missing_long_option_operand(
                option_name, operand_name).toString());
    }

    public static CompilerException missing_short_option_operand(
            String option_name,
            String operand_name) {

        return new CompilerException(new M_missing_short_option_operand(
                option_name, operand_name).toString());
    }

    public static CompilerException invalid_long_option(
            String option_name) {

        return new CompilerException(new M_invalid_long_option(option_name)
                .toString());
    }

    public static CompilerException invalid_short_option(
            String option_name) {

        return new CompilerException(new M_invalid_short_option(option_name)
                .toString());
    }

    public static CompilerException spurious_long_option_operand(
            String option_name,
            String operand_text) {

        return new CompilerException(new M_spurious_long_option_operand(
                option_name, operand_text).toString());
    }

    public static CompilerException spurious_short_option_operand(
            String option_name,
            String operand_text) {

        return new CompilerException(new M_spurious_short_option_operand(
                option_name, operand_text).toString());
    }

    public static CompilerException input_error(
            String file_name,
            Throwable cause) {

        return new CompilerException(new M_input_error(file_name, cause
                .getMessage()).toString(), cause);
    }

    public static CompilerException missing_destination_directory(
            String location) {

        return new CompilerException(new M_missing_destination_directory(
                location).toString());
    }

    public static CompilerException destination_is_not_directory(
            String location) {

        return new CompilerException(new M_destination_is_not_directory(
                location).toString());
    }

    public static CompilerException invalid_argument_count() {

        return new CompilerException(new M_invalid_argument_count().toString());
    }

    public static CompilerException invalid_suffix(
            String file_name) {

        return new CompilerException(new M_invalid_suffix(file_name).toString());
    }

    public static CompilerException missing_grammar_file(
            String file_name) {

        return new CompilerException(new M_missing_grammar_file(file_name)
                .toString());
    }

    public static CompilerException grammar_not_file(
            String file_name) {

        return new CompilerException(new M_grammar_not_file(file_name)
                .toString());
    }

    public static CompilerException duplicate_global_name_declaration(
            Name duplicateGlobalName,
            Name firstGlobalName) {

        String name = duplicateGlobalName.getNameString();
        if (!name.equals(firstGlobalName.getNameString())) {
            throw new InternalException("name must be identical");
        }

        Token duplicateToken = duplicateGlobalName.getNameToken();
        Token firstToken = firstGlobalName.getNameToken();

        return new CompilerException(new M_duplicate_global_name_declaration(
                name, duplicateToken.getLine() + "", duplicateToken.getPos()
                        + "", firstToken.getLine() + "", firstToken.getPos()
                        + "").toString());
    }
}
