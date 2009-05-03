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

public class Pair<L, R> {

    private final L left;

    private final R right;

    private Integer hashCode;

    private String toString;

    public Pair(
            L left,
            R right) {

        this.left = left;
        this.right = right;
    }

    public L getLeft() {

        return this.left;
    }

    public R getRight() {

        return this.right;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(
            Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Pair<L, R> pair = (Pair<L, R>) obj;

        if (this.left == null || pair.left == null) {
            if (this.left != pair.left) {
                return false;
            }
        }
        else {
            if (!this.left.equals(pair.left)) {
                return false;
            }
        }

        if (this.right == null || pair.right == null) {
            if (this.right != pair.right) {
                return false;
            }
        }
        else {
            if (!this.right.equals(pair.right)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        if (this.hashCode == null) {
            int hashCode = 0;

            if (this.left != null) {
                hashCode += this.left.hashCode();
            }

            hashCode *= 307;

            if (this.right != null) {
                hashCode += this.right.hashCode();
            }

            this.hashCode = hashCode;
        }

        return this.hashCode;
    }

    @Override
    public String toString() {

        if (this.toString == null) {
            this.toString = "(" + this.left + "," + this.right + ")";
        }

        return this.toString;
    }
}
