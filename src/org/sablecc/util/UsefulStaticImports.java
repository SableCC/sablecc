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

package org.sablecc.util;

import java.math.*;

import org.sablecc.exception.*;

/**
 * This class provides commonly used constants for static import.
 */
public class UsefulStaticImports {

    /** The system-specific line separator. */
    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /** BigInteger constant 32. */
    public static final BigInteger BI_32 = new BigInteger("32");

    /** BigInteger constant 126. */
    public static final BigInteger BI_126 = new BigInteger("126");

    /** Prevents instantiation of this class. */
    private UsefulStaticImports() {

        throw new InternalException("no instance allowed");
    }
}
