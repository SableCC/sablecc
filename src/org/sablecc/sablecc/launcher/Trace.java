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

import org.sablecc.util.*;

public class Trace {

    private final Verbosity verbosity;

    Trace(
            Verbosity verbosity) {

        this.verbosity = verbosity;
    }

    public void verbose(
            String message) {

        switch (this.verbosity) {
        case VERBOSE:
            System.out.print(message);
        }
    }

    public void verboseln(
            String message) {

        switch (this.verbosity) {
        case VERBOSE:
            System.out.println(message);
        }
    }

    public void verboseln() {

        switch (this.verbosity) {
        case VERBOSE:
            System.out.println();
        }
    }

    public void informative(
            String message) {

        switch (this.verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.print(message);
        }
    }

    public void informativeln(
            String message) {

        switch (this.verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println(message);
        }
    }

    public void informativeln() {

        switch (this.verbosity) {
        case INFORMATIVE:
        case VERBOSE:
            System.out.println();
        }
    }
}
