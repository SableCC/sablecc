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

import java.util.*;

import org.sablecc.exception.*;

public class ComponentFinder<T> {

    private Map<T, T> representativeMap = new HashMap<T, T>();

    private Map<T, Set<T>> memberMap = new LinkedHashMap<T, Set<T>>();

    private Set<T> linearization = new LinkedHashSet<T>();

    private Map<T, Set<T>> reachMap = new LinkedHashMap<T, Set<T>>();

    public ComponentFinder(
            final Collection<T> nodes,
            final Progeny<T> progeny) {

        if (nodes == null) {
            throw new InternalException("nodes may not be null");
        }

        if (progeny == null) {
            throw new InternalException("progeny may not be null");
        }

        new Runnable() {

            class NodeInfo {

                int discoveryTime;

                int lowestReachableDiscoveryTime;

                boolean onStack;
            }

            Map<T, NodeInfo> infoMap = new HashMap<T, NodeInfo>();

            LinkedList<T> stack = new LinkedList<T>();

            int time = 0;

            public void run() {

                for (T node : nodes) {
                    if (this.infoMap.get(node) == null) {
                        tarjan(node);
                    }
                }

                ComponentFinder.this.linearization = Collections
                        .unmodifiableSet(ComponentFinder.this.linearization);

                for (T node : ComponentFinder.this.linearization) {
                    Set<T> members = ComponentFinder.this.memberMap.get(node);
                    Set<T> nodeReach = new LinkedHashSet<T>();
                    boolean recursive = false;

                    for (T member : members) {
                        for (T child : progeny.getChildren(member)) {
                            nodeReach.add(child);

                            T childRepresentative = ComponentFinder.this.representativeMap
                                    .get(child);
                            Set<T> childReach = ComponentFinder.this.reachMap
                                    .get(childRepresentative);

                            if (childReach != null) {
                                nodeReach.addAll(childReach);
                            }
                            else {
                                recursive = true;
                            }
                        }
                    }

                    if (recursive) {
                        nodeReach.addAll(members);
                    }

                    nodeReach = Collections.unmodifiableSet(nodeReach);
                    ComponentFinder.this.reachMap.put(node, nodeReach);
                }
            }

            private void tarjan(
                    T node) {

                if (!nodes.contains(node)) {
                    throw new InternalException("invalid node");
                }

                NodeInfo nodeInfo = new NodeInfo();
                nodeInfo.discoveryTime = this.time;
                nodeInfo.lowestReachableDiscoveryTime = this.time;
                nodeInfo.onStack = true;
                this.infoMap.put(node, nodeInfo);
                this.stack.addFirst(node);

                this.time++;

                for (T child : progeny.getChildren(node)) {

                    NodeInfo childInfo = this.infoMap.get(child);
                    if (childInfo == null || childInfo.onStack) {
                        if (childInfo == null) {
                            tarjan(child);
                            childInfo = this.infoMap.get(child);
                        }
                        nodeInfo.lowestReachableDiscoveryTime = Math.min(
                                nodeInfo.lowestReachableDiscoveryTime,
                                childInfo.lowestReachableDiscoveryTime);
                    }
                }

                if (nodeInfo.lowestReachableDiscoveryTime == nodeInfo.discoveryTime) {
                    Set<T> members = new LinkedHashSet<T>();
                    T member;
                    ComponentFinder.this.linearization.add(node);

                    do {
                        member = this.stack.removeFirst();
                        NodeInfo memberInfo = this.infoMap.get(member);
                        memberInfo.onStack = false;
                        ComponentFinder.this.representativeMap
                                .put(member, node);
                        members.add(member);
                    }
                    while (!member.equals(node));

                    members = Collections.unmodifiableSet(members);
                    ComponentFinder.this.memberMap.put(node, members);
                }
            }
        }.run();
    }

    public T getRepresentative(
            T node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        if (!this.representativeMap.containsKey(node)) {
            throw new InternalException("node is not valid");
        }

        return this.representativeMap.get(node);
    }

    public Set<T> getMembers(
            T node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        if (!this.memberMap.containsKey(node)) {
            throw new InternalException("node is not valid");
        }

        return this.memberMap.get(node);
    }

    public Set<T> getLinearization() {

        return this.linearization;
    }

    public Set<T> getReach(
            T node) {

        if (node == null) {
            throw new InternalException("node may not be null");
        }

        if (!this.memberMap.containsKey(node)) {
            throw new InternalException("node is not valid");
        }

        return this.reachMap.get(node);
    }
}
