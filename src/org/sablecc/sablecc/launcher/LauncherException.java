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

import org.sablecc.sablecc.exception.*;
import org.sablecc.sablecc.launcher.errormessage.*;

public class LauncherException
        extends CompilerException {

    private LauncherException(
            String message) {

        super(message);
    }

    public static CompilerException unknownTarget(
            String targetLanguage) {

        return new LauncherException(
                new MUnknownTarget(targetLanguage).toString());
    }

}
