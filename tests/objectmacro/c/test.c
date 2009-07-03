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

#include <stdlib.h>
#include <stdio.h>

#include "test_c/MTextNormalId.h"
#include "test_c/M_teXTRIChid.h"
#include "test_c/MTextParams.h"
#include "test_c/M_textparAMs.h"
#include "test_c/MMacroNormalId.h"
#include "test_c/M_mACrOrIChId.h"
#include "test_c/MMacroParams.h"
#include "test_c/M_mACROpaRAms.h"
#include "test_c/MMacroInMacro.h"
#include "test_c/MMacroHeritageOfParams.h"
#include "test_c/MMacroExpand.h"
#include "test_c/MMacroInsert.h"
#include "test_c/M_MacroInsert.h"
#include "test_c/MMacroNoEol.h"
#include "test_c/MExpandHelpers.h"
#include "test_c/M_expandHelpers.h"


int main() {

  printf( "------- test text : normal identifier -------\n" );
  {
    MTextNormalId* m = MTextNormalId_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test text : rich identifier -------\n" );
  {
    M_teXTRIChid* m = M_teXTRIChid_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test text : params -------\n" );
  {
    MTextParams* m =  MTextParams_init("ObjectMacro is a simple", " language.");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }
  {
    M_textparAMs* m =  M_textparAMs_init("Do you want learn ObjectMacro", "OK.");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test macro : normal identifier -------\n" );
  {
    MMacroNormalId* m =  MMacroNormalId_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test macro : rich identifier -------\n" );
  {
    M_mACrOrIChId* m =  M_mACrOrIChId_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test macro : params -------\n" );
  {
    MMacroParams* m =  MMacroParams_init("Arnaud", "Julien");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }
  {
    M_mACROpaRAms* m =  M_mACROpaRAms_init("Arnaud", "Julien");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test macro : macro in macro -------\n" );
  {
    MMacroInMacro* m =  MMacroInMacro_init("GRESIL");
    MMacroInMacroLv1* s1 = MMacroInMacro_newMacroInMacroLv1(m, "Profs :");
    MMacroInMacroLv1* s2 = MMacroInMacro_newMacroInMacroLv1(m, "Students :");
    MMacroInMacroLv1_newMacroInMacroLv2(s1, "Jean");
    MMacroInMacroLv1_newMacroInMacroLv2(s1, "Etienne");
    MMacroInMacroLv1_newMacroInMacroLv2(s2, "Arnaud");
    MMacroInMacroLv1_newMacroInMacroLv2(s2, "Julien");
    MMacroInMacroLv1_newMacroInMacroLv2(s2, "Alexis");
    MMacroInMacroLv1_newMacroInMacroLv2(s2, "Jean-Sebastien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }

  printf( "------- test macro : heritage of params -------\n" );
  {
    MMacroHeritageOfParams* m =  MMacroHeritageOfParams_init("GRESIL");
    MMacroHeritageOfParamsLv1* s1 = MMacroHeritageOfParams_newMacroHeritageOfParamsLv1(m, "(Prof)");
    MMacroHeritageOfParamsLv1* s2 = MMacroHeritageOfParams_newMacroHeritageOfParamsLv1(m, "(Student)");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s1, "Jean");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s1, "Etienne");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s2, "Arnaud");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s2, "Julien");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s2, "Alexis");
    MMacroHeritageOfParamsLv1_newMacroHeritageOfParamsLv2(s2, "Jean-Sebastien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }

  printf( "------- test macro : expand -------\n" );
  {
    MMacroExpand* m =  MMacroExpand_init();
    MMacroExpand_newMacroNormalId(m);
    MMacroExpand_newMacroExpandHello(m);
    MMacroExpand_new_mACrOrIChId(m);
    MMacroExpand_newMacroParams(m, "Arnaud", "Julien");
    MMacroExpand_newMacroExpandBye(m);
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }

  printf( "------- test macro : insert -------\n" );
  {
    MMacroInsert* m =  MMacroInsert_init("ObjectMacro is a simple language", "Yes !");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }
  {
    M_MacroInsert* m =  M_MacroInsert_init("Arnaud");
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }

  printf( "------- test macro : no_eol -------\n" );
  {
    MMacroNoEol* m =  MMacroNoEol_init();
    MMacroNoEol_newMacroNoEolHello(m);
    MMacroNoEol_new_mACrOrIChId(m);
    MMacroNoEol_newMacroNormalId(m);
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }

  printf( "------- test expand : helpers -------\n" );
  {
    MExpandHelpers* m =  MExpandHelpers_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }
  {
    MExpandHelpers* m =  MExpandHelpers_init();
    MExpandHelpers_newMacroParams(m, "Arnaud", "Julien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }
  {
    MExpandHelpers* m =  MExpandHelpers_init();
    MExpandHelpers_newMacroParams(m, "Arnaud", "Julien");
    MExpandHelpers_newMacroParams(m, "Jean", "Alexis");
    MExpandHelpers_newMacroParams(m, "Jean", "Sebastien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }
  printf( "\n ------- \n" );
  {
    M_expandHelpers* m =  M_expandHelpers_init();
    char* str = m->toString(m);
    printf( "%s\n", str );
    free(str);
    m->free(m);
  }
  {
    M_expandHelpers* m =  M_expandHelpers_init();
    M_expandHelpers_newMacroParams(m, "Arnaud", "Julien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }
  {
    M_expandHelpers* m =  M_expandHelpers_init();
    M_expandHelpers_newMacroParams(m, "Arnaud", "Julien");
    M_expandHelpers_newMacroParams(m, "Jean", "Alexis");
    M_expandHelpers_newMacroParams(m, "Jean", "Sebastien");
    {
      char* str = m->toString(m);
      printf( "%s\n", str );
      free(str);
    }
    m->free(m);
  }

  return 0;
}
