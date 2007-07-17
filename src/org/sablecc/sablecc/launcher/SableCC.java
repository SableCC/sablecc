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

package org.sablecc.sablecc.launcher;

import org.sablecc.sablecc.exception.InternalException;
import org.sablecc.sablecc.exception.InvalidArgumentException;

public class SableCC {

    public static void main(
            String[] args) {

        try {
            Arguments arguments = new Arguments(args);

            // handle options
            for (OptionArgument optionArgument : arguments.getOptionArguments()) {

                switch (optionArgument.getOption()) {

                case DESTINATION:
                    System.err.println("ERROR: unimplemented option "
                            + optionArgument.getOption());
                    System.exit(1);
                    break;

                case VERSION:
                    System.out.println("SableCC version " + Version.VERSION);
                    System.exit(0);
                    break;

                case HELP:
                    System.out.println("usage: sablecc "
                            + Option.getShortHelpMessage()
                            + " specification.sablecc ...");
                    System.out.println("options:");
                    System.out.println(Option.getLongHelpMessage());
                    System.exit(0);
                    break;

                default:
                    throw new InternalException("unhandled option "
                            + optionArgument.getOption());
                }
            }

            if (arguments.getTextArguments().size() == 0) {
                System.err.println("usage: sablecc "
                        + Option.getShortHelpMessage()
                        + " specification.sablecc ...");
                System.err.println("type 'sablecc -h' for more information.");
                System.exit(1);
            }

            for (TextArgument textArgument : arguments.getTextArguments()) {
                if (!textArgument.getText().endsWith(".sablecc")) {
                    System.err
                            .println("ERROR: specification file name does not end with .sablecc: "
                                    + textArgument.getText());
                    System.exit(1);
                }
            }
            System.err.println("ERROR: unimplemented");
            System.exit(1);
        }
        catch (InvalidArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }
}
