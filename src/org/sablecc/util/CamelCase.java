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

import org.sablecc.sablecc.syntax3.node.*;

public class CamelCase {

    public static String to_CamelCase(
            TIdentifier identifier) {

        String text = identifier.getText();
        return to_CamelCase(text);
    }

    public static String to_CamelCase(
            String text) {

        if (text.length() > 0 && text.charAt(0) == '<') {
            int length = text.length();
            return "_" + text.substring(1, length - 1);
        }

        StringBuilder sb = new StringBuilder();
        boolean upcase = true;
        for (char c : text.toCharArray()) {
            if (upcase) {
                upcase = false;
                if (c >= 'a' && c <= 'z') {
                    sb.append((char) (c + ('A' - 'a')));
                }
                else {
                    sb.append(c);
                }
            }
            else if (c == '_') {
                upcase = true;
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String to_camelCase(
            TIdentifier identifier) {

        String text = identifier.getText();
        return to_camelCase(text);
    }

    public static String to_camelCase(
            String text) {

        if (text.length() > 0 && text.charAt(0) == '<') {
            int length = text.length();
            return "_" + text.substring(1, length - 1);
        }

        StringBuilder sb = new StringBuilder();
        boolean upcase = false;
        for (char c : text.toCharArray()) {
            if (upcase) {
                upcase = false;
                if (c >= 'a' && c <= 'z') {
                    sb.append((char) (c + ('A' - 'a')));
                }
                else {
                    sb.append(c);
                }
            }
            else if (c == '_') {
                upcase = true;
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
