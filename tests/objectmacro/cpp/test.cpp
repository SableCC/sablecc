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

#include <iostream>

#include "test_cpp/MTextNormalId.h"
#include "test_cpp/M_teXTRIChid.h"
#include "test_cpp/MTextParams.h"
#include "test_cpp/M_textparAMs.h"
#include "test_cpp/MMacroNormalId.h"
#include "test_cpp/M_mACrOrIChId.h"
#include "test_cpp/MMacroParams.h"
#include "test_cpp/M_mACROpaRAms.h"
#include "test_cpp/MMacroInMacro.h"
#include "test_cpp/MMacroHeritageOfParams.h"
#include "test_cpp/MMacroExpand.h"
#include "test_cpp/MMacroInsert.h"
#include "test_cpp/M_MacroInsert.h"
#include "test_cpp/MMacroNoEol.h"
#include "test_cpp/MExpandHelpers.h"
#include "test_cpp/M_expandHelpers.h"

using namespace std;
using namespace test_cpp;

int main() {

  cout << "------- test text : normal identifier -------" << endl;
  MTextNormalId* m = new MTextNormalId();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test text : rich identifier -------" << endl;
  M_teXTRIChid* m = new M_teXTRIChid();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test text : params -------" << endl;
  MTextParams* m = new MTextParams("ObjectMacro is a simple", " language.");
  cout << m->toString() << endl;
  delete m;

  M_textparAMs* m = new M_textparAMs("Do you want learn ObjectMacro", "OK.");
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : normal identifier -------" << endl;
  MMacroNormalId* m = new MMacroNormalId();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : rich identifier -------" << endl;
  M_mACrOrIChId* m = new M_mACrOrIChId();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : params -------" << endl;
  MMacroParams* m = new MMacroParams("Arnaud", "Julien");
  cout << m->toString() << endl;
  delete m;

  M_mACROpaRAms* m = new M_mACROpaRAms("Arnaud", "Julien");
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : macro in macro -------" << endl;
  MMacroInMacro* m = new MMacroInMacro("GRESIL");
  MMacroInMacroLv1* s1 = m->newMacroInMacroLv1("Profs :");
  MMacroInMacroLv1* s2 = m->newMacroInMacroLv1("Students :");
  s1->newMacroInMacroLv2("Jean");
  s1->newMacroInMacroLv2("Etienne");
  s2->newMacroInMacroLv2("Arnaud");
  s2->newMacroInMacroLv2("Julien");
  s2->newMacroInMacroLv2("Alexis");
  s2->newMacroInMacroLv2("Jean-Sebastien");
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : heritage of params -------" << endl;
  MMacroHeritageOfParams* m = new MMacroHeritageOfParams("GRESIL");
  MMacroHeritageOfParamsLv1* s1 = m->newMacroHeritageOfParamsLv1("(Prof)");
  MMacroHeritageOfParamsLv1* s2 = m->newMacroHeritageOfParamsLv1("(Student)");
  s1->newMacroHeritageOfParamsLv2("Jean");
  s1->newMacroHeritageOfParamsLv2("Etienne");
  s2->newMacroHeritageOfParamsLv2("Arnaud");
  s2->newMacroHeritageOfParamsLv2("Julien");
  s2->newMacroHeritageOfParamsLv2("Alexis");
  s2->newMacroHeritageOfParamsLv2("Jean-Sebastien");
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : expand -------" << endl;
  MMacroExpand* m = new MMacroExpand();
  m->newMacroNormalId();
  m->newMacroExpandHello();
  m->new_mACrOrIChId();
  m->newMacroParams("Arnaud", "Julien");
  m->newMacroExpandBye();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : insert -------" << endl;
  MMacroInsert* m = new MMacroInsert("ObjectMacro is a simple language", "Yes !");
  cout << m->toString() << endl;
  delete m;

  M_MacroInsert* m = new M_MacroInsert("Arnaud");
  cout << m->toString() << endl;
  delete m;

  cout << "------- test macro : no_eol -------" << endl;
  MMacroNoEol* m = new MMacroNoEol();
  m->newMacroNoEolHello();
  m->new_mACrOrIChId();
  m->newMacroNormalId();
  cout << m->toString() << endl;
  delete m;

  cout << "------- test expand : helpers -------" << endl;
  MExpandHelpers* m = new MExpandHelpers();
  cout << m->toString()<< endl;
  delete m;

  MExpandHelpers* m = new MExpandHelpers();
  m->newMacroParams("Arnaud", "Julien");
  cout << m->toString()<< endl;
  delete m;

  MExpandHelpers* m = new MExpandHelpers();
  m->newMacroParams("Arnaud", "Julien");
  m->newMacroParams("Jean", "Alexis");
  m->newMacroParams("Jean", "Sebastien");
  cout << m->toString() << endl;
  delete m;

  cout << endl << " ------- " << endl;

  M_expandHelpers* m = new M_expandHelpers();
  cout << m->toString()<< endl;
  delete m;

  M_expandHelpers* m = new M_expandHelpers();
  m->newMacroParams("Arnaud", "Julien");
  cout << m->toString()<< endl;
  delete m;

  M_expandHelpers* m = new M_expandHelpers();
  m->newMacroParams("Arnaud", "Julien");
  m->newMacroParams("Jean", "Alexis");
  m->newMacroParams("Jean", "Sebastien");
  cout << m->toString()<< endl;
  delete m;

  return 0;
}
