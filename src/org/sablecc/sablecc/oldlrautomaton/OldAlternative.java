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

import java.util.*;

import org.sablecc.exception.*;
import org.sablecc.sablecc.core.*;
import org.sablecc.sablecc.grammar.*;

public class OldAlternative {

    private final OldProduction oldProduction;

    private final String shortName;

    private String name;

    private final ArrayList<OldElement> oldElements = new ArrayList<OldElement>();

    private boolean isStable;

    private Integer shortestLength;

    private ArrayList<Item> items;

    private final Alternative origin;

/*    private ParserPriorityLevel priorityLevel;
*/
    OldAlternative(
            OldProduction oldProduction,
            String shortName,
            Alternative alternative) {

        this.oldProduction = oldProduction;
        this.shortName = shortName;
        this.origin = alternative;
    }

    public OldElement addProductionElement(
            String shortName,
            OldProduction oldProduction,
            org.sablecc.sablecc.grammar.Element.ProductionElement element) {

        if (this.isStable) {
            throw new InternalException("alternative is stable");
        }
        int position = this.oldElements.size();
        OldElement oldElement = new OldProductionElement(this, position,
                shortName, oldProduction, element);
        this.oldElements.add(oldElement);
        return oldElement;
    }

    public OldElement addTokenElement(
            Grammar grammar,
            String shortName,
            OldToken oldToken,
            Element.TokenElement element) {

        if (this.isStable) {
            throw new InternalException("alternative is stable");
        }
        int position = this.oldElements.size();
        OldElement oldElement = new OldTokenElement(grammar, this, position,
                shortName, oldToken, element);
        this.oldElements.add(oldElement);
        return oldElement;
    }

    public Alternative getOrigin() {

        return this.origin;
    }

    public String getShortName() {

        return this.shortName;
    }

    void setName(
            String name) {

        if (this.isStable) {
            throw new InternalException("alternative is stable");
        }
        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    void stabilize() {

        if (this.isStable) {
            throw new InternalException("alternative is already stable");
        }

        Map<String, List<OldElement>> nameToElementListMap = new LinkedHashMap<String, List<OldElement>>();
        for (OldElement oldElement : this.oldElements) {
            String shortName = oldElement.getShortName();
            List<OldElement> elementList = nameToElementListMap.get(shortName);
            if (elementList == null) {
                elementList = new LinkedList<OldElement>();
                nameToElementListMap.put(shortName, elementList);
            }
            elementList.add(oldElement);
        }
        for (List<OldElement> elementList : nameToElementListMap.values()) {
            if (elementList.size() == 1) {
                OldElement oldElement = elementList.get(0);
                if (oldElement.getShortName() != "") {
                    oldElement.setName(oldElement.getShortName());
                }
                else {
                    oldElement.setName("$1");
                }

            }
            else {
                int index = 1;
                for (OldElement oldElement : elementList) {
                    oldElement.setName(oldElement.getShortName() + "$"
                            + index++);
                }
            }
        }
        for (OldElement oldElement : this.oldElements) {
            oldElement.stabilize();
        }

        this.items = new ArrayList<Item>(this.oldElements.size() + 1);
        int position = 0;
        for (OldElement oldElement : this.oldElements) {
            if (oldElement instanceof OldTokenElement) {
                this.items.add(new Item(this, position, ItemType.BEFORE_TOKEN));
            }
            else {
                this.items.add(new Item(this, position,
                        ItemType.BEFORE_PRODUCTION));
            }
            position++;
        }
        this.items.add(new Item(this, position, ItemType.END));
    }

    public String getFullName() {

        return this.oldProduction.getName() + "." + getName();
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(getName());
        sb.append(":}");
        for (OldElement oldElement : this.oldElements) {
            sb.append(" ");
            sb.append(oldElement);
        }
        return sb.toString();
    }

    public Integer getShortestLength() {

        return this.shortestLength;
    }

    boolean computeShortestLength() {

        Integer length = 0;

        for (OldElement oldElement : this.oldElements) {
            if (oldElement instanceof OldTokenElement) {
                length++;
            }
            else {
                OldProductionElement oldProductionElement = (OldProductionElement) oldElement;
                Integer elementLength = oldProductionElement.getProduction()
                        .getShortestLength();
                if (elementLength == null) {
                    return false;
                }
                else {
                    length += elementLength;
                }
            }
        }

        if (length != null) {
            if (this.shortestLength == null
                    || length.compareTo(this.shortestLength) < 0) {
                this.shortestLength = length;
                return true;
            }
        }

        return false;
    }

    public Item getItem(
            int position) {

        return this.items.get(position);
    }

    public OldElement getElement(
            int position) {

        return this.oldElements.get(position);
    }

    public OldElement getElement(
            Element sElement) {

        Iterator<OldElement> i = this.oldElements.iterator();
        while (i.hasNext()) {
            OldElement e = i.next();
            if (e.getOrigin() == sElement) {
                return e;
            }
        }

        return null;
    }

    public ArrayList<OldElement> getElements() {

        return this.oldElements;
    }

    Set<Ahead> tryLook(
            int distance) {

        return this.items.get(0).tryLook(distance);
    }

    public OldProduction getProduction() {

        return this.oldProduction;
    }

/*    public void setPriorityLevel(
            ParserPriorityLevel priorityLevel,
            TIdentifier identifier) {

        if (this.priorityLevel != null) {
            throw CompilerException.parserSpuriousPriority(identifier);
        }

        this.priorityLevel = priorityLevel;
    }
*/
/*    public ParserPriorityLevel getPriorityLevel() {

        return this.priorityLevel;
    }
*/
/*    public boolean hasPriorityOver(
            Alternative alternative) {

        if (this.priorityLevel == null) {
            return false;
        }

        if (alternative.priorityLevel == null) {
            return false;
        }

        if (this.priorityLevel == alternative.priorityLevel) {
            throw new InternalException(
                    "cannot decide within a single priority level");
        }

        ParserPriorityLevel currentPriorityLevel = this.priorityLevel;
        while (currentPriorityLevel != null) {

            if (currentPriorityLevel == alternative.priorityLevel) {
                return true;
            }

            currentPriorityLevel = currentPriorityLevel
                    .getNextLowerPriorityLevel();
        }

        return false;
    }
*/
/*    public boolean isLeftAssociative() {

        if (this.priorityLevel == null) {
            return false;
        }

        return this.priorityLevel.getType() == PriorityType.LEFT;
    }
*/
/*    public boolean isRightAssociative() {

        if (this.priorityLevel == null) {
            return false;
        }

        return this.priorityLevel.getType() == PriorityType.RIGHT;
    }
*/
}
