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
import java.util.*;

import org.sablecc.exception.*;

public abstract class Type {

    public Type() {

    }

    public abstract boolean isAssignableTo(
            Type aType);

    public abstract Type add(
            Type aType);

    public abstract static class SimpleType
            extends Type {

        public SimpleType() {

        }

        @Override
        public boolean isAssignableTo(
                Type aType) {

            if (aType instanceof CompositeType) {
                throw new InternalException(
                        "Composite type shouldn't be assigned to simple type");
            }

            return true;
        }

        public abstract CardinalityInterval getCardinality();

        public static class HomogeneousType
                extends SimpleType {

            private final String name;

            private final CardinalityInterval cardinality;

            public HomogeneousType(
                    String name,
                    CardinalityInterval cardinality) {

                if (name == null) {
                    throw new InternalException("name shouldn't be null");
                }

                if (cardinality == null) {
                    throw new InternalException("cardinality shouldn't be null");
                }

                this.name = name;
                this.cardinality = cardinality;
            }

            @Override
            public boolean isAssignableTo(
                    Type aType) {

                super.isAssignableTo(aType);

                if (!(aType instanceof HomogeneousType)) {
                    return false;
                }

                HomogeneousType anHomogeneousType = (HomogeneousType) aType;

                if (!this.name.equals(anHomogeneousType.getName())) {
                    return false;
                }

                if (!this.cardinality.isIncludedIn(anHomogeneousType
                        .getCardinality())) {
                    return false;
                }

                return true;
            }

            public String getName() {

                return this.name;
            }

            @Override
            public CardinalityInterval getCardinality() {

                return this.cardinality;
            }

            @Override
            public Type add(
                    Type aType) {

                if (this.cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
                    return aType;
                }

                if (aType instanceof Type.SimpleType) {
                    Type.SimpleType aSimpleType = (Type.SimpleType) aType;

                    if (aSimpleType.getCardinality().equals(
                            CardinalityInterval.ZERO_ZERO)) {
                        return this;
                    }
                }

                if (aType instanceof SimpleType.AlternatedType) {
                    SimpleType.AlternatedType anAlternated = (SimpleType.AlternatedType) aType;

                    if (this.name.equals(anAlternated.getLeftElementName())
                            && this.name.equals(anAlternated
                                    .getRightElementName())) {
                        // x^n..m + (x x)^o..p =
                        // x^[(n+o*2)..(m+p*2)]

                        Bound lowerBound = anAlternated.getCardinality()
                                .getLowerBound()
                                .multiply(BigInteger.valueOf(2L));
                        lowerBound.add(this.cardinality.getLowerBound());

                        Bound upperBound = anAlternated.getCardinality()
                                .getUpperBound()
                                .multiply(BigInteger.valueOf(2L));
                        upperBound.add(this.cardinality.getUpperBound());

                        return new Type.SimpleType.HomogeneousType(this.name,
                                new CardinalityInterval(lowerBound, upperBound));
                    }
                }
                else if (aType instanceof SimpleType.SeparatedType) {
                    SimpleType.SeparatedType aSeparated = (SimpleType.SeparatedType) aType;

                    if (this.name.equals(aSeparated.getRightElementName())
                            && (aSeparated.getCardinality().isANumber() || aSeparated
                                    .getCardinality().equals(
                                            CardinalityInterval.ONE_OR_MORE))) {
                        // x ^n..m + (x Sep x)^p =
                        // (x y)^[(n+p)..(m+p)]

                        return new Type.SimpleType.HomogeneousType(this.name,
                                this.cardinality.add(aSeparated
                                        .getCardinality()));
                    }

                }

                if (aType instanceof SimpleType.HomogeneousType) {

                    SimpleType.HomogeneousType anHomogeneous = (SimpleType.HomogeneousType) aType;

                    if (this.name.equals(anHomogeneous.getName())) {
                        // x^[n..m] + x^[p..q]
                        // x^[(n+p)..(m+q)]

                        return new Type.SimpleType.HomogeneousType(this.name,
                                this.cardinality.add(anHomogeneous
                                        .getCardinality()));
                    }
                }

                return null;
            }
        }

        public static class SeparatedType
                extends SimpleType {

            private final String leftElementName;

            private final String rightElementName;

            private final CardinalityInterval cardinality;

            public SeparatedType(
                    String leftElementName,
                    String rightElementName,
                    CardinalityInterval cardinality) {

                if (leftElementName == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                if (rightElementName == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                if (cardinality == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                this.leftElementName = leftElementName;
                this.rightElementName = rightElementName;
                this.cardinality = cardinality;
            }

            public SeparatedType(
                    String elementName,
                    CardinalityInterval cardinality) {

                if (elementName == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                if (cardinality == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                this.leftElementName = elementName;
                this.rightElementName = elementName;
                this.cardinality = cardinality;
            }

            @Override
            public boolean isAssignableTo(
                    Type aType) {

                super.isAssignableTo(aType);

                if (aType instanceof SimpleType.AlternatedType) {
                    return false;
                }

                if (aType instanceof SimpleType.SeparatedType) {
                    SimpleType.SeparatedType separatedType = (SimpleType.SeparatedType) aType;

                    if (separatedType.getCardinality().equals(
                            CardinalityInterval.ONE_ONE)
                            || separatedType.getCardinality().equals(
                                    CardinalityInterval.ZERO_ONE)) {

                        if (!separatedType.getLeftElementName().equals(
                                this.leftElementName)) {
                            return false;
                        }

                    }
                    else {
                        if (!separatedType.getLeftElementName().equals(
                                this.leftElementName)
                                || !separatedType.getRightElementName().equals(
                                        this.rightElementName)) {

                            return false;
                        }
                    }

                    if (!this.cardinality.isIncludedIn(separatedType
                            .getCardinality())) {
                        return false;
                    }

                }

                if (aType instanceof SimpleType.HomogeneousType) {
                    SimpleType.HomogeneousType homogeneousType = (SimpleType.HomogeneousType) aType;

                    if (!this.leftElementName.equals(this.rightElementName)) {
                        return false;
                    }

                    if (!homogeneousType.getName().equals(this.leftElementName)) {
                        return false;
                    }

                    if (!this.cardinality.isIncludedIn(homogeneousType
                            .getCardinality())) {
                        return false;
                    }
                }

                if (aType instanceof EmptyListType || aType instanceof NullType) {
                    if (this.cardinality.getLowerBound().compareTo(
                            new Bound(BigInteger.ZERO)) != 0) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public CardinalityInterval getCardinality() {

                return this.cardinality;
            }

            public String getLeftElementName() {

                return this.leftElementName;
            }

            public String getRightElementName() {

                return this.rightElementName;
            }

            @Override
            public Type add(
                    Type aType) {

                if (this.cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
                    return aType;
                }

                Type.SimpleType aSimpleType;

                if (aType instanceof Type.SimpleType) {
                    aSimpleType = (Type.SimpleType) aType;

                    if (aSimpleType.getCardinality().equals(
                            CardinalityInterval.ZERO_ZERO)) {
                        return this;
                    }
                }
                else {
                    return null;
                }

                if (this.cardinality.equals(CardinalityInterval.ONE_ONE)) {

                    // x + (x Sep ..)^1 = (x x)^1
                    // x + (y Sep ..)^1 = (x y)^1
                    // x + y = (x y)
                    if (aSimpleType instanceof Type.SimpleType.SeparatedType
                            && aSimpleType.getCardinality().equals(
                                    CardinalityInterval.ONE_ONE)) {

                        Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                        return new Type.SimpleType.AlternatedType(
                                this.leftElementName,
                                separatedType.getLeftElementName(),
                                CardinalityInterval.ONE_ONE);

                    }
                    // x + (x Sep x)^n = (x x)^n
                    // x + (y Sep x)^n = (x y)^n
                    // x + (y Sep x)^(n..m) = (x y)^(n..m)
                    // x + (x Sep x)^(n..m) = (x x)^(n..m)
                    else if (aSimpleType instanceof Type.SimpleType.SeparatedType) {

                        Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                        if (this.leftElementName.equals(separatedType
                                .getRightElementName())) {
                            return new Type.SimpleType.AlternatedType(
                                    separatedType.getLeftElementName(),
                                    separatedType.getRightElementName(),
                                    separatedType.getCardinality());
                        }

                    }
                    // x + (x x)^n = (x Sep x)^(n+1)
                    // x + (y x)^n = (x Sep y)^(n+1)
                    // x + (y x)^(n..m) = (x Sep y)^(n+1..m+1)
                    // x + (x x)^(n..m) = (x Sep x)^(n+1..m+1)
                    else if (aSimpleType instanceof Type.SimpleType.AlternatedType) {
                        Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                        if (this.leftElementName.equals(alternatedType
                                .getRightElementName())) {
                            return new Type.SimpleType.SeparatedType(
                                    alternatedType.getRightElementName(),
                                    alternatedType.getLeftElementName(),
                                    alternatedType.getCardinality().add(
                                            CardinalityInterval.ONE_ONE));
                        }
                    }
                    // x + x^n = x^(n+1)
                    else if (aSimpleType instanceof Type.SimpleType.HomogeneousType) {

                        Type.SimpleType.HomogeneousType homogeneousType = (Type.SimpleType.HomogeneousType) aSimpleType;

                        if (this.leftElementName.equals(homogeneousType
                                .getName())) {
                            return new Type.SimpleType.HomogeneousType(
                                    this.leftElementName, homogeneousType
                                            .getCardinality()
                                            .add(CardinalityInterval.ONE_ONE));
                        }

                    }

                }
                else if (this.cardinality.equals(CardinalityInterval.ZERO_ONE)) {

                    if (aSimpleType instanceof Type.SimpleType.SeparatedType) {
                        Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                        if (this.leftElementName.equals(separatedType
                                .getLeftElementName())) {
                            // x? + (x Sep ..)^1 = x^(1,2)
                            if (separatedType.cardinality
                                    .equals(CardinalityInterval.ONE_ONE)) {
                                return new Type.SimpleType.HomogeneousType(
                                        this.leftElementName,
                                        this.cardinality
                                                .add(CardinalityInterval.ONE_ONE));
                            }
                            // x? + (x Sep ..)^[0..1] = x^(0,2)
                            else if (separatedType.cardinality
                                    .equals(CardinalityInterval.ZERO_ONE)) {
                                return new Type.SimpleType.HomogeneousType(
                                        this.leftElementName,
                                        this.cardinality.add(separatedType
                                                .getCardinality()));

                            }
                            // x? + (x Sep x)^(n..m) = x^[2*n-1, 2*m]
                            else if (this.leftElementName.equals(separatedType
                                    .getRightElementName())) {

                                Bound lowerBound = aSimpleType.getCardinality()
                                        .getLowerBound()
                                        .multiply(BigInteger.valueOf(2L));
                                Bound upperBound = aSimpleType.getCardinality()
                                        .getUpperBound()
                                        .multiply(BigInteger.valueOf(2L));
                                upperBound = upperBound
                                        .subtract(BigInteger.ONE);

                                return new Type.SimpleType.HomogeneousType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));

                            }
                        }
                    }
                    else if (aSimpleType instanceof Type.SimpleType.AlternatedType) {
                        Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                        // x? + (x x)^(n..m) = x^(2n..2m+1)
                        if (this.leftElementName.equals(alternatedType
                                .getLeftElementName())
                                && alternatedType.getLeftElementName().equals(
                                        alternatedType.getRightElementName())) {
                            Bound lowerBound = aSimpleType.getCardinality()
                                    .getLowerBound()
                                    .multiply(BigInteger.valueOf(2L));
                            Bound upperBound = aSimpleType.getCardinality()
                                    .getUpperBound()
                                    .multiply(BigInteger.valueOf(2L));
                            upperBound = upperBound.add(BigInteger.ONE);

                            return new Type.SimpleType.HomogeneousType(
                                    this.leftElementName,
                                    new CardinalityInterval(lowerBound,
                                            upperBound));
                        }
                    }
                    else if (aSimpleType instanceof Type.SimpleType.HomogeneousType) {
                        Type.SimpleType.HomogeneousType homogeneousType = (Type.SimpleType.HomogeneousType) aSimpleType;

                        // x? + x^n = x^(n..n+1)
                        if (this.leftElementName.equals(homogeneousType
                                .getName())) {
                            return new Type.SimpleType.HomogeneousType(
                                    this.leftElementName, homogeneousType
                                            .getCardinality()
                                            .add(CardinalityInterval.ZERO_ONE));
                        }

                    }
                }
                else {
                    if (this.leftElementName.equals(this.rightElementName)) {

                        if (aSimpleType instanceof Type.SimpleType.SeparatedType) {
                            Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                            // (x Sep x)^(n..m) + (x Sep x)^(p..q)
                            // = (x x)^(n+p-1..m+q-1)
                            if (this.leftElementName.equals(separatedType
                                    .getLeftElementName())
                                    && separatedType.getLeftElementName()
                                            .equals(separatedType
                                                    .getRightElementName())) {

                                Bound lowerBound = this.cardinality
                                        .getLowerBound().add(
                                                separatedType.getCardinality()
                                                        .getLowerBound());
                                lowerBound = lowerBound
                                        .subtract(BigInteger.ONE);
                                Bound upperBound = this.cardinality
                                        .getUpperBound().add(
                                                separatedType.getCardinality()
                                                        .getUpperBound());
                                upperBound = upperBound
                                        .subtract(BigInteger.ONE);

                                return new Type.SimpleType.AlternatedType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));

                            }
                            // (x Sep x)^(n..m) + (x Sep..)^1
                            // = (x x)^(n..m)
                            else if (this.leftElementName.equals(separatedType
                                    .getLeftElementName())
                                    && separatedType.getCardinality().equals(
                                            CardinalityInterval.ONE_ONE)) {

                                return new Type.SimpleType.AlternatedType(
                                        this.leftElementName,
                                        separatedType.getCardinality());

                            }

                        }
                        else if (aSimpleType instanceof Type.SimpleType.AlternatedType
                                && !(this.cardinality.getLowerBound()
                                        .compareTo(Bound.ZERO) == 0)) {
                            Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                            // (x Sep x)^(n..m) + (x x)^(p..q)
                            // = (x Sep x)^(n+p..m+q)
                            // n > 0
                            if (this.leftElementName.equals(alternatedType
                                    .getLeftElementName())
                                    && alternatedType.getLeftElementName()
                                            .equals(alternatedType
                                                    .getRightElementName())) {

                                Bound lowerBound = this.cardinality
                                        .getLowerBound().add(
                                                alternatedType.getCardinality()
                                                        .getLowerBound());
                                Bound upperBound = this.cardinality
                                        .getUpperBound().add(
                                                alternatedType.getCardinality()
                                                        .getUpperBound());

                                return new Type.SimpleType.SeparatedType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));

                            }
                        }
                        else if (aSimpleType instanceof Type.SimpleType.HomogeneousType) {
                            Type.SimpleType.HomogeneousType homogeneousType = (Type.SimpleType.HomogeneousType) aSimpleType;

                            // (x Sep x)^n + x^(p..q)
                            // = x^(2*n-1+p..2*n-1+q)

                            if (this.leftElementName.equals(homogeneousType
                                    .getName())) {
                                Bound lowerBound = this.cardinality
                                        .getLowerBound().multiply(
                                                BigInteger.valueOf(2L));
                                lowerBound = lowerBound
                                        .subtract(BigInteger.ONE);
                                lowerBound
                                        .add(this.cardinality.getLowerBound());

                                Bound upperBound = this.cardinality
                                        .getUpperBound().multiply(
                                                BigInteger.valueOf(2L));
                                upperBound = upperBound
                                        .subtract(BigInteger.ONE);
                                upperBound = upperBound.add(this.cardinality
                                        .getUpperBound());

                                return new Type.SimpleType.HomogeneousType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));
                            }
                        }

                    }
                    else {

                        if (aSimpleType instanceof Type.SimpleType.SeparatedType) {
                            Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                            // (x Sep y)^(n..m) + (y Sep x)^(p..q)
                            // = (x y)^(n+p-1...m+q-1)
                            if (this.leftElementName.equals(separatedType
                                    .getRightElementName())
                                    && this.rightElementName
                                            .equals(separatedType
                                                    .getLeftElementName())) {
                                Bound lowerBound = this.cardinality
                                        .getLowerBound().add(
                                                separatedType.getCardinality()
                                                        .getLowerBound());
                                lowerBound = lowerBound
                                        .subtract(BigInteger.ONE);
                                Bound upperBound = this.cardinality
                                        .getUpperBound().add(
                                                separatedType.getCardinality()
                                                        .getUpperBound());
                                upperBound = upperBound
                                        .subtract(BigInteger.ONE);

                                return new Type.SimpleType.AlternatedType(
                                        this.leftElementName,
                                        this.rightElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));

                            }
                            // (x Sep y)^(n..m) + (y Sep..)^1
                            // = (x y)^(n..m)
                            else if (this.rightElementName.equals(separatedType
                                    .getLeftElementName())
                                    && separatedType.getCardinality().equals(
                                            CardinalityInterval.ONE_ONE)) {
                                return new Type.SimpleType.AlternatedType(
                                        this.leftElementName,
                                        this.rightElementName, this.cardinality);
                            }

                        }
                        else if (aSimpleType instanceof Type.SimpleType.AlternatedType
                                && !(this.cardinality.getLowerBound()
                                        .compareTo(Bound.ZERO) == 0)) {
                            Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                            // (x Sep y)^(n..m) + (y x)^(p..q)
                            // = (x Sep y)^(n+p..m+q)
                            // n > 0
                            if (this.leftElementName.equals(alternatedType
                                    .getRightElementName())
                                    && this.rightElementName
                                            .equals(alternatedType
                                                    .getLeftElementName())) {
                                Bound lowerBound = this.cardinality
                                        .getLowerBound().add(
                                                alternatedType.getCardinality()
                                                        .getLowerBound());
                                Bound upperBound = this.cardinality
                                        .getUpperBound().add(
                                                alternatedType.getCardinality()
                                                        .getUpperBound());

                                return new Type.SimpleType.SeparatedType(
                                        this.leftElementName,
                                        this.rightElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));
                            }
                        }
                    }
                }

                return null;
            }
        }

        public static class AlternatedType
                extends SimpleType {

            private final String leftElementName;

            private final String rightElementName;

            private final CardinalityInterval cardinality;

            public AlternatedType(
                    String leftElementName,
                    String rightElementName,
                    CardinalityInterval cardinality) {

                if (leftElementName == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                if (rightElementName == null) {
                    throw new InternalException(
                            "rightElementName shouldn't be null");
                }

                if (cardinality == null) {
                    throw new InternalException("cardinality shouldn't be null");
                }

                this.leftElementName = leftElementName;
                this.rightElementName = rightElementName;
                this.cardinality = cardinality;
            }

            public AlternatedType(
                    String elementName,
                    CardinalityInterval cardinality) {

                if (elementName == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                if (cardinality == null) {
                    throw new InternalException(
                            "leftElementName shouldn't be null");
                }

                this.leftElementName = elementName;
                this.rightElementName = elementName;
                this.cardinality = cardinality;
            }

            @Override
            public boolean isAssignableTo(
                    Type aType) {

                super.isAssignableTo(aType);

                if (aType instanceof SimpleType.SeparatedType) {
                    return false;
                }

                if (aType instanceof SimpleType.AlternatedType) {
                    SimpleType.AlternatedType alternatedType = (SimpleType.AlternatedType) aType;

                    if (!alternatedType.getLeftElementName().equals(
                            this.leftElementName)
                            || !alternatedType.getRightElementName().equals(
                                    this.rightElementName)) {
                        return false;
                    }

                    if (!this.cardinality.isIncludedIn(alternatedType
                            .getCardinality())) {
                        return false;
                    }
                }

                if (aType instanceof SimpleType.HomogeneousType) {
                    SimpleType.HomogeneousType homogeneousType = (SimpleType.HomogeneousType) aType;

                    if (!this.leftElementName.equals(this.rightElementName)) {
                        return false;
                    }

                    if (!homogeneousType.getName().equals(this.leftElementName)) {
                        return false;
                    }

                    if (!homogeneousType.getCardinality().isIncludedIn(
                            this.cardinality)) {
                        return false;
                    }
                }

                if (aType instanceof EmptyListType || aType instanceof NullType) {
                    if (this.cardinality.getLowerBound().compareTo(Bound.ZERO) != 0) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public CardinalityInterval getCardinality() {

                return this.cardinality;
            }

            public String getLeftElementName() {

                return this.leftElementName;
            }

            public String getRightElementName() {

                return this.rightElementName;
            }

            @Override
            public Type add(
                    Type aType) {

                if (this.cardinality.equals(CardinalityInterval.ZERO_ZERO)) {
                    return aType;
                }

                Type.SimpleType aSimpleType;

                if (aType instanceof Type.SimpleType) {

                    aSimpleType = (Type.SimpleType) aType;

                    if (aSimpleType.getCardinality().equals(
                            CardinalityInterval.ZERO_ZERO)) {
                        return this;
                    }
                }
                else {
                    return null;
                }

                if (this.leftElementName.equals(this.rightElementName)) {

                    if (aSimpleType instanceof Type.SimpleType.SeparatedType
                            && !(aSimpleType.getCardinality().getLowerBound()
                                    .compareTo(Bound.ZERO) == 0)) {
                        Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                        // (x x)^(n..m) + (x Sep..)^1
                        // = (x Sep x)^(n+1..m+1)
                        if (this.leftElementName.equals(separatedType
                                .getLeftElementName())
                                && separatedType.getCardinality().equals(
                                        CardinalityInterval.ONE_ONE)) {

                            return new SeparatedType(this.leftElementName,
                                    this.cardinality
                                            .add(CardinalityInterval.ONE_ONE));

                        }
                        // (x x)^(n..m) + (x Sep x)^(p..q)
                        // = (x Sep x)^(n+p..m+q)
                        // p > 0
                        else if (this.leftElementName.equals(separatedType
                                .getLeftElementName())
                                && separatedType.getLeftElementName().equals(
                                        separatedType.getRightElementName())) {
                            Bound lowerBound = this.cardinality.getLowerBound()
                                    .add(separatedType.getCardinality()
                                            .getLowerBound());
                            Bound upperBound = this.cardinality.getUpperBound()
                                    .add(separatedType.getCardinality()
                                            .getUpperBound());

                            return new SeparatedType(this.leftElementName,
                                    new CardinalityInterval(lowerBound,
                                            upperBound));

                        }

                    }
                    else if (aSimpleType instanceof Type.SimpleType.AlternatedType) {
                        Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                        // (x x)^(n..m) + (x x)^(p..q)
                        // = (x x)^(n+p..m+q)
                        if (this.leftElementName.equals(alternatedType
                                .getLeftElementName())
                                && alternatedType.getLeftElementName().equals(
                                        alternatedType.getRightElementName())) {
                            Bound lowerBound = this.cardinality.getLowerBound()
                                    .add(alternatedType.getCardinality()
                                            .getLowerBound());
                            Bound upperBound = this.cardinality.getUpperBound()
                                    .add(alternatedType.getCardinality()
                                            .getUpperBound());

                            return new Type.SimpleType.AlternatedType(
                                    this.leftElementName,
                                    new CardinalityInterval(lowerBound,
                                            upperBound));
                        }
                    }
                    else if (aSimpleType instanceof Type.SimpleType.HomogeneousType
                            && aSimpleType.getCardinality().isANumber()) {
                        Type.SimpleType.HomogeneousType homogeneousType = (Type.SimpleType.HomogeneousType) aSimpleType;

                        if (this.leftElementName.equals(homogeneousType
                                .getName())) {
                            BigInteger cardinalityValue = aSimpleType
                                    .getCardinality().getLowerBound()
                                    .getValue();

                            Bound lowerBound = aSimpleType.getCardinality()
                                    .getLowerBound()
                                    .divide(BigInteger.valueOf(2L));
                            lowerBound = lowerBound.add(this.cardinality
                                    .getLowerBound());
                            Bound upperBound = aSimpleType.getCardinality()
                                    .getUpperBound()
                                    .divide(BigInteger.valueOf(2L));
                            upperBound = upperBound.add(this.cardinality
                                    .getUpperBound());

                            // (x x)^(n..m) + x^2p
                            // = (x x)^(n+p..m+p)
                            if (cardinalityValue.mod(BigInteger.valueOf(2L))
                                    .compareTo(BigInteger.ZERO) == 0) {
                                return new Type.SimpleType.AlternatedType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));
                            }
                            // (x x)^(n..m) + x^(2p+1)
                            // = (x Sep x)^(n+p..m=P)
                            else {
                                return new Type.SimpleType.SeparatedType(
                                        this.leftElementName,
                                        new CardinalityInterval(lowerBound,
                                                upperBound));
                            }
                        }

                    }
                }
                else {

                    if (aSimpleType instanceof Type.SimpleType.SeparatedType
                            && !(aSimpleType.getCardinality().getLowerBound()
                                    .compareTo(Bound.ZERO) == 0)) {

                        Type.SimpleType.SeparatedType separatedType = (Type.SimpleType.SeparatedType) aSimpleType;

                        // (x..y)^(n..m) + (x Sep..)^1
                        // = (x Sep y)^(n+1..m+1)
                        if (this.leftElementName
                                .equals(separatedType.leftElementName)
                                && separatedType.cardinality
                                        .equals(CardinalityInterval.ONE_ONE)) {
                            return new Type.SimpleType.SeparatedType(
                                    this.leftElementName,
                                    this.rightElementName,
                                    this.cardinality
                                            .add(CardinalityInterval.ONE_ONE));
                        }
                        // (x..y)^(n..m) + (y Sep x)^(p..q)
                        // = (x Sep y)^(n+p..m+q)
                        // p > 0
                        else if (this.leftElementName.equals(separatedType
                                .getRightElementName())
                                && this.rightElementName.equals(separatedType
                                        .getLeftElementName())) {

                            Bound lowerBound = this.cardinality.getLowerBound()
                                    .add(separatedType.getCardinality()
                                            .getLowerBound());
                            Bound upperBound = this.cardinality.getUpperBound()
                                    .add(separatedType.getCardinality()
                                            .getUpperBound());

                            return new Type.SimpleType.SeparatedType(
                                    this.leftElementName,
                                    this.rightElementName,
                                    new CardinalityInterval(lowerBound,
                                            upperBound));

                        }
                    }
                    else if (aSimpleType instanceof Type.SimpleType.AlternatedType) {
                        Type.SimpleType.AlternatedType alternatedType = (Type.SimpleType.AlternatedType) aSimpleType;

                        // (x y)^(n..m) + (x y)^(p..q)
                        // = (x y)^(n+p..m+q)
                        if (this.leftElementName.equals(alternatedType
                                .getLeftElementName())
                                && this.rightElementName.equals(alternatedType
                                        .getRightElementName())) {
                            Bound lowerBound = this.cardinality.getLowerBound()
                                    .add(alternatedType.getCardinality()
                                            .getLowerBound());
                            Bound upperBound = this.cardinality.getUpperBound()
                                    .add(alternatedType.getCardinality()
                                            .getUpperBound());

                            return new Type.SimpleType.AlternatedType(
                                    this.leftElementName,
                                    this.rightElementName,
                                    new CardinalityInterval(lowerBound,
                                            upperBound));
                        }
                    }

                }
                return null;
            }

        }

        public static class EmptyListType
                extends SimpleType {

            public EmptyListType() {

            }

            @Override
            public boolean isAssignableTo(
                    Type aType) {

                throw new InternalException("An empty list shouldn't be assign");
            }

            @Override
            public Type add(
                    Type aType) {

                return aType;
            }

            @Override
            public CardinalityInterval getCardinality() {

                return CardinalityInterval.ZERO_ZERO;
            }
        }

        public static class NullType
                extends SimpleType {

            public NullType() {

            }

            @Override
            public boolean isAssignableTo(
                    Type aType) {

                throw new InternalException(
                        "A Null element shouldn't be assign");
            }

            @Override
            public Type add(
                    Type aType) {

                throw new InternalException("addType not implemented for Null");
            }

            @Override
            public CardinalityInterval getCardinality() {

                return CardinalityInterval.ZERO_ZERO;
            }

        }
    }

    public static class CompositeType
            extends Type {

        private final LinkedList<Type> elements;

        public CompositeType(
                LinkedList<Type> elements) {

            if (elements == null) {
                throw new InternalException("elements shouldn't be null");
            }

            this.elements = elements;
        }

        @Override
        public boolean isAssignableTo(
                Type aType) {

            if (aType instanceof SimpleType) {
                throw new InternalException(
                        "Composed type and simple type shouldn't be mixed");
            }

            CompositeType aCompositeType = (CompositeType) aType;

            if (this.elements.size() != aCompositeType.getElements().size()) {
                throw new InternalException(
                        "List element size should be check before calling this method");
            }

            for (int i = 0; i < this.elements.size(); i++) {
                if (!this.elements.get(i).isAssignableTo(
                        aCompositeType.getElements().get(i))) {
                    return false;
                }
            }

            return true;
        }

        public LinkedList<Type> getElements() {

            return this.elements;
        }

        @Override
        public Type add(
                Type aType) {

            throw new InternalException("addType not implemented for Composed");
        }
    }
}
