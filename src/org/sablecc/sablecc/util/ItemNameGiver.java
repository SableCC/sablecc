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

package org.sablecc.sablecc.util;

import java.util.*;
import java.util.Map.*;

import org.sablecc.exception.*;

public class ItemNameGiver {

    private final Map<String, Set<NamedItem>> publicNameToNamedItemSetMap = new LinkedHashMap<String, Set<NamedItem>>();

    private final Map<NamedItem, String> namedItemToInternalNameMap = new LinkedHashMap<NamedItem, String>();

    public ItemNameGiver(
            Set<NamedItem> namedItems) {

        for (NamedItem namedItem : namedItems) {
            String publicName = namedItem.getPublicName();
            Set<NamedItem> namedItemSet = this.publicNameToNamedItemSetMap
                    .get(publicName);

            if (namedItemSet == null) {
                namedItemSet = new LinkedHashSet<NamedItem>();
                this.publicNameToNamedItemSetMap.put(publicName, namedItemSet);
            }

            namedItemSet.add(namedItem);
        }

        for (Entry<String, Set<NamedItem>> entry : this.publicNameToNamedItemSetMap
                .entrySet()) {
            String publicName = entry.getKey();
            Set<NamedItem> namedItemSet = entry.getValue();
            int size = namedItemSet.size();
            int index = 1;

            for (NamedItem namedItem : namedItemSet) {
                String internalName;

                if (size > 1 || publicName.equals("")) {
                    internalName = publicName + "$" + index;
                }
                else {
                    internalName = publicName;
                }

                this.namedItemToInternalNameMap.put(namedItem, internalName);
                index++;
            }
        }
    }

    public String getInternalName(
            NamedItem namedItem) {

        String internalName = this.namedItemToInternalNameMap.get(namedItem);

        if (internalName == null) {
            throw new InternalException("invalid namedItem");
        }

        return internalName;
    }
}
