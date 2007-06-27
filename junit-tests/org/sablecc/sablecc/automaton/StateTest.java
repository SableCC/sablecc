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

package org.sablecc.sablecc.automaton;

/*
 * import static org.junit.Assert.assertEquals; import static
 * org.junit.Assert.assertFalse; import static org.junit.Assert.assertTrue;
 * import static org.junit.Assert.fail;
 * 
 * import org.junit.Before;
 */
import org.junit.Test;

/*
 * import org.sablecc.sablecc.exception.InternalException;
 */
public class StateTest {

    /*
     * private NfaTransition<Integer> transition;
     * 
     * private NfaState<Integer> firstState;
     * 
     * private NfaState<Integer> secondState;
     * 
     * @Before public void setUp() throws Exception {
     * 
     * this.firstState = new NfaState<Integer>("firstStage");
     * 
     * this.secondState = new NfaState<Integer>("secondState");
     * 
     * this.transition = new NfaTransition<Integer>(this.firstState,
     * this.secondState, null);
     * 
     * this.firstState.addForwardTransition(this.transition); }
     * 
     * @SuppressWarnings("unused") @Test public void testState() { // Case with
     * null name try { NfaState nullNameState = new NfaState(null); fail("name
     * may not be null"); } catch (InternalException e) { // expected } // Case
     * with an expected name String expectedName = "expectedName"; NfaState
     * newState = new NfaState(expectedName);
     * 
     * assertEquals("The state should have the expected name.", expectedName,
     * newState.getName()); }
     * 
     * @Test public void testGetForwardTransitions() { // Case with non-stable
     * State try { this.firstState.getForwardTransitions(); fail("the state is
     * not stable yet"); } catch (InternalException e) { // Expected } }
     * 
     * @Test public void testGetBackwardTransitions() { // Case with non-stable
     * State try { this.firstState.getBackwardTransitions(); fail("the state is
     * not stable yet"); } catch (InternalException e) { // Expected } }
     * 
     * @SuppressWarnings("unchecked") @Test public void
     * testAddForwardTransition() { // Case with null transition NfaTransition<Integer>
     * nullTransition = null; try {
     * this.firstState.addForwardTransition(nullTransition); fail("transition
     * may not be null"); } catch (RuntimeException e) { // expected } //
     * Typical case this.firstState.addForwardTransition(this.transition);
     * this.firstState.stabilize(); assertTrue( "the forward transition should
     * contain at least transition.",
     * this.firstState.getForwardTransitions().contains( this.transition)); //
     * Case with already stable State try {
     * this.firstState.addForwardTransition(this.transition); fail("a stable
     * state may not be modified"); } catch (InternalException e) { // Expected } }
     * 
     * @Test public void testAddBackwardTransition() { // Case with null
     * transition NfaTransition<Integer> nullTransition = null; try {
     * this.secondState.addBackwardTransition(nullTransition); fail("transition
     * may not be null"); } catch (RuntimeException e) { // expected } //
     * Typical case this.secondState.addBackwardTransition(this.transition);
     * this.secondState.stabilize(); assertTrue( "the forward transition should
     * contain at least transition.",
     * this.secondState.getBackwardTransitions().contains( this.transition)); //
     * Case with already stable State try {
     * this.secondState.addBackwardTransition(this.transition); fail("a stable
     * state may not be modified"); } catch (InternalException e) { // Expected } }
     * 
     * @Test public void testRemoveForwardTransition() { // Case with null
     * Transition NfaTransition<Integer> nullTransition = null; try {
     * this.firstState.removeForwardTransition(nullTransition); fail("transition
     * may not be null"); } catch (InternalException e) { // Excepted } //
     * Typical case this.firstState.addForwardTransition(this.transition);
     * 
     * this.firstState.removeForwardTransition(this.transition);
     * this.firstState.stabilize(); assertFalse("The transition should have been
     * removed", this.firstState
     * .getForwardTransitions().contains(this.transition)); // Case with already
     * stable State try {
     * this.firstState.removeForwardTransition(this.transition); fail("a stable
     * state may not be modified"); } catch (InternalException e) { // Expected } }
     * 
     * @Test public void testRemoveBackwardTransition() { // Case with null
     * Transition NfaTransition<Integer> nullTransition = null; try {
     * this.secondState.removeBackwardTransition(nullTransition);
     * fail("transition may not be null"); } catch (InternalException e) { //
     * Excepted } // Typical case
     * this.secondState.addBackwardTransition(this.transition);
     * 
     * this.secondState.removeBackwardTransition(this.transition);
     * this.secondState.stabilize(); assertFalse("The transition should have
     * been removed", this.secondState
     * .getBackwardTransitions().contains(this.transition)); // Case with
     * already stable State try {
     * this.secondState.removeBackwardTransition(this.transition); fail("a
     * stable state may not be modified"); } catch (InternalException e) { //
     * Expected } }
     * 
     * @Test public void testStabilize() { // Case with already stabilized State
     * this.firstState.stabilize();
     * 
     * try { this.firstState.stabilize(); fail("state is already stable"); }
     * catch (InternalException e) { // Expected } }
     */

    @Test
    public void dummy() {

    }
}
