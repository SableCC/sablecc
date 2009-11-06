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

package org.sablecc.sablecc.lrautomaton;

import java.util.*;

import org.sablecc.exception.*;

public class Item
        implements Ahead {

    private final Alternative alternative;

    private final int position;

    private final ItemType itemType;

    Item(
            Alternative alternative,
            int position,
            ItemType itemType) {

        this.alternative = alternative;
        this.position = position;
        this.itemType = itemType;
    }

    public ItemType getType() {

        return this.itemType;
    }

    public ProductionElement getProductionElement() {

        if (this.itemType != ItemType.BEFORE_PRODUCTION) {
            throw new InternalException("invalid call");
        }

        return (ProductionElement) this.alternative.getElement(this.position);
    }

    public TokenElement getTokenElement() {

        if (this.itemType != ItemType.BEFORE_TOKEN) {
            throw new InternalException("invalid call");
        }

        return (TokenElement) this.alternative.getElement(this.position);
    }

    public Item next() {

        return this.alternative.getItem(this.position + 1);
    }

    @Override
    public String toString() {

        return this.alternative.getFullName() + ":" + this.position;
    }

    public Alternative getAlternative() {

        return this.alternative;
    }

    public int getPosition() {

        return this.position;
    }

    public Set<Ahead> look(
            int distance) {

        if (distance < 1) {
            throw new InternalException("invalid distance");
        }

        if (this.position == this.alternative.getElements().size()) {
            Set<Ahead> result = new LinkedHashSet<Ahead>();
            result.add(Farther.get(distance));
            return result;
        }

        Element element = this.alternative.getElement(this.position);
        if (element instanceof TokenElement) {
            if (distance == 1) {
                Set<Ahead> result = new LinkedHashSet<Ahead>();
                result.add(this);
                return result;
            }

            return next().look(distance - 1);
        }

        ProductionElement productionElement = (ProductionElement) element;
        Production production = productionElement.getProduction();
        Set<Ahead> result = new LinkedHashSet<Ahead>();
        for (Ahead ahead : production.look(distance)) {
            if (ahead instanceof Item) {
                result.add(ahead);
            }
            else {
                Farther farther = (Farther) ahead;
                result.addAll(next().look(farther.getDistance()));
            }
        }
        return result;
    }

    public Set<Ahead> tryLook(
            int distance) {

        if (distance < 1) {
            throw new InternalException("invalid distance");
        }

        if (this.position == this.alternative.getElements().size()) {
            Set<Ahead> result = new LinkedHashSet<Ahead>();
            result.add(Farther.get(distance));
            return result;
        }

        Element element = this.alternative.getElement(this.position);
        if (element instanceof TokenElement) {
            if (distance == 1) {
                Set<Ahead> result = new LinkedHashSet<Ahead>();
                result.add(this);
                return result;
            }

            return next().tryLook(distance - 1);
        }

        ProductionElement productionElement = (ProductionElement) element;
        Production production = productionElement.getProduction();
        Set<Ahead> result = new LinkedHashSet<Ahead>();
        for (Ahead ahead : production.tryLook(distance)) {
            if (ahead instanceof Item) {
                result.add(ahead);
            }
            else {
                Farther farther = (Farther) ahead;
                result.addAll(next().tryLook(farther.getDistance()));
            }
        }
        return result;
    }

    public boolean hasPriorityOver(
            Item rightItem) {

        if (this.alternative.getPriorityLevel() != rightItem.alternative
                .getPriorityLevel()) {
            return this.alternative.hasPriorityOver(rightItem.alternative);
        }

        if (this.itemType == ItemType.END) {
            return this.alternative.isLeftAssociative();
        }

        return this.alternative.isRightAssociative();
    }
}
