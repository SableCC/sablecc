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

package test_java;

class test {
   public static void main (
           String[] args) {

    System.out.println( "------- test text : normal identifier ------- \n");
    MTextNormalId m = new MTextNormalId();
    System.out.println(m.toString());

    System.out.println( "------- test text : rich identifier -------\n");
    M_teXTRIChid m = new M_teXTRIChid();
    System.out.println( m.toString() );

    System.out.println( "------- test text : params -------\n");
    MTextParams m = new MTextParams("ObjectMacro is a simple", " language.");
    System.out.println( m.toString() );

    M_textparAMs m = new M_textparAMs("Do you want learn ObjectMacro", "OK.");
    System.out.println( m.toString() );

    System.out.println( "------- test macro : normal identifier -------\n");
    MMacroNormalId m = new MMacroNormalId();
    System.out.println( m.toString() );

    System.out.println( "------- test macro : rich identifier -------\n");
    M_mACrOrIChId m = new M_mACrOrIChId();
    System.out.println( m.toString() );

    System.out.println( "------- test macro : params -------\n");
    MMacroParams m = new MMacroParams("Arnaud", "Julien");
    System.out.println( m.toString() );

    M_mACROpaRAms m = new M_mACROpaRAms("Arnaud", "Julien");
    System.out.println( m.toString() );

    System.out.println( "------- test macro : macro in macro -------\n");
    MMacroInMacro m = new MMacroInMacro("GRESIL");
    MMacroInMacroLv1 s1 = m.newMacroInMacroLv1("Profs :");
    MMacroInMacroLv1 s2 = m.newMacroInMacroLv1("Students :");
    s1.newMacroInMacroLv2("Jean");
    s1.newMacroInMacroLv2("Etienne");
    s2.newMacroInMacroLv2("Arnaud");
    s2.newMacroInMacroLv2("Julien");
    s2.newMacroInMacroLv2("Alexis");
    s2.newMacroInMacroLv2("Jean-Sebastien");
    System.out.println( m.toString() );

    System.out.println( "------- test macro : heritage of params -------\n");
    MMacroHeritageOfParams m = new MMacroHeritageOfParams("GRESIL");
    MMacroHeritageOfParamsLv1 s1 = m.newMacroHeritageOfParamsLv1("(Prof)");
    MMacroHeritageOfParamsLv1 s2 = m.newMacroHeritageOfParamsLv1("(Student)");
    s1.newMacroHeritageOfParamsLv2("Jean");
    s1.newMacroHeritageOfParamsLv2("Etienne");
    s2.newMacroHeritageOfParamsLv2("Arnaud");
    s2.newMacroHeritageOfParamsLv2("Julien");
    s2.newMacroHeritageOfParamsLv2("Alexis");
    s2.newMacroHeritageOfParamsLv2("Jean-Sebastien");
    System.out.println( m.toString() );

    System.out.println( "------- test macro : expand -------\n");
    MMacroExpand m = new MMacroExpand();
    m.newMacroNormalId();
    m.newMacroExpandHello();
    m.new_mACrOrIChId();
    m.newMacroParams("Arnaud", "Julien");
    m.newMacroExpandBye();
    System.out.println( m.toString() );

    System.out.println( "------- test macro : insert -------\n");
    MMacroInsert m = new MMacroInsert("ObjectMacro is a simple language", "Yes !");
    System.out.println( m.toString() );

    M_MacroInsert m = new M_MacroInsert("Arnaud");
    System.out.println( m.toString() );

    System.out.println( "------- test macro : no_eol -------\n");
    MMacroNoEol m = new MMacroNoEol();
    m.newMacroNoEolHello();
    m.new_mACrOrIChId();
    m.newMacroNormalId();
    System.out.println( m.toString() );

    System.out.println( "------- test expand : helpers -------\n");
    MExpandHelpers m = new MExpandHelpers();
    System.out.println( m.toString());

    MExpandHelpers m = new MExpandHelpers();
    m.newMacroParams("Arnaud", "Julien");
    System.out.println( m.toString());

    MExpandHelpers m = new MExpandHelpers();
    m.newMacroParams("Arnaud", "Julien");
    m.newMacroParams("Jean", "Alexis");
    m.newMacroParams("Jean", "Sebastien");
    System.out.println( m.toString() );

    System.out.println( "\n ------- ");

    M_expandHelpers m = new M_expandHelpers();
    System.out.println( m.toString());

    M_expandHelpers m = new M_expandHelpers();
    m.newMacroParams("Arnaud", "Julien");
    System.out.println( m.toString());

    M_expandHelpers m = new M_expandHelpers();
    m.newMacroParams("Arnaud", "Julien");
    m.newMacroParams("Jean", "Alexis");
    m.newMacroParams("Jean", "Sebastien");
    System.out.println( m.toString());
  }
}
