/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MInsertMacroPart extends Macro {
    
    private DSeparator ReferencedMacroNameSeparator;
    
    private DBeforeFirst ReferencedMacroNameBeforeFirst;
    
    private DAfterLast ReferencedMacroNameAfterLast;
    
    private DNone ReferencedMacroNameNone;
    
    final List<String> list_ReferencedMacroName;
    
    final Context ReferencedMacroNameContext = new Context();
    
    final StringValue ReferencedMacroNameValue;
    
    private DSeparator EnclosingClassNameSeparator;
    
    private DBeforeFirst EnclosingClassNameBeforeFirst;
    
    private DAfterLast EnclosingClassNameAfterLast;
    
    private DNone EnclosingClassNameNone;
    
    final List<String> list_EnclosingClassName;
    
    final Context EnclosingClassNameContext = new Context();
    
    final StringValue EnclosingClassNameValue;
    
    private DSeparator IndexBuilderSeparator;
    
    private DBeforeFirst IndexBuilderBeforeFirst;
    
    private DAfterLast IndexBuilderAfterLast;
    
    private DNone IndexBuilderNone;
    
    final List<String> list_IndexBuilder;
    
    final Context IndexBuilderContext = new Context();
    
    final StringValue IndexBuilderValue;
    
    private DSeparator IndexInsertSeparator;
    
    private DBeforeFirst IndexInsertBeforeFirst;
    
    private DAfterLast IndexInsertAfterLast;
    
    private DNone IndexInsertNone;
    
    final List<String> list_IndexInsert;
    
    final Context IndexInsertContext = new Context();
    
    final StringValue IndexInsertValue;
    
    private DSeparator MacroBodyPartsSeparator;
    
    private DBeforeFirst MacroBodyPartsBeforeFirst;
    
    private DAfterLast MacroBodyPartsAfterLast;
    
    private DNone MacroBodyPartsNone;
    
    final List<Macro> list_MacroBodyParts;
    
    final Context MacroBodyPartsContext = new Context();
    
    final MacroValue MacroBodyPartsValue;
    
    private DSeparator SetInternalsSeparator;
    
    private DBeforeFirst SetInternalsBeforeFirst;
    
    private DAfterLast SetInternalsAfterLast;
    
    private DNone SetInternalsNone;
    
    final List<Macro> list_SetInternals;
    
    final Context SetInternalsContext = new Context();
    
    final MacroValue SetInternalsValue;
    
    private DSeparator SingleElementListsSeparator;
    
    private DBeforeFirst SingleElementListsBeforeFirst;
    
    private DAfterLast SingleElementListsAfterLast;
    
    private DNone SingleElementListsNone;
    
    final List<Macro> list_SingleElementLists;
    
    final Context SingleElementListsContext = new Context();
    
    final MacroValue SingleElementListsValue;
    
    MInsertMacroPart(Macros macros){
        
        
        this.setMacros(macros);
        this.list_ReferencedMacroName = new LinkedList<>();
        this.list_EnclosingClassName = new LinkedList<>();
        this.list_IndexBuilder = new LinkedList<>();
        this.list_IndexInsert = new LinkedList<>();
        this.list_MacroBodyParts = new LinkedList<>();
        this.list_SetInternals = new LinkedList<>();
        this.list_SingleElementLists = new LinkedList<>();
        
        this.ReferencedMacroNameValue = new StringValue(this.list_ReferencedMacroName, this.ReferencedMacroNameContext);
        this.EnclosingClassNameValue = new StringValue(this.list_EnclosingClassName, this.EnclosingClassNameContext);
        this.IndexBuilderValue = new StringValue(this.list_IndexBuilder, this.IndexBuilderContext);
        this.IndexInsertValue = new StringValue(this.list_IndexInsert, this.IndexInsertContext);
        this.MacroBodyPartsValue = new MacroValue(this.list_MacroBodyParts, this.MacroBodyPartsContext);
        this.SetInternalsValue = new MacroValue(this.list_SetInternals, this.SetInternalsContext);
        this.SingleElementListsValue = new MacroValue(this.list_SingleElementLists, this.SingleElementListsContext);
    }
    
    public void addAllReferencedMacroName(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("ReferencedMacroName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("ReferencedMacroName");
            }
    
            this.list_ReferencedMacroName.add(string);
        }
    }
    
    public void addReferencedMacroName(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("ReferencedMacroName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_ReferencedMacroName.add(string);
    }
    
    public void addAllEnclosingClassName(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("EnclosingClassName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("EnclosingClassName");
            }
    
            this.list_EnclosingClassName.add(string);
        }
    }
    
    public void addEnclosingClassName(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("EnclosingClassName");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_EnclosingClassName.add(string);
    }
    
    public void addAllIndexBuilder(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("IndexBuilder");
            }
    
            this.list_IndexBuilder.add(string);
        }
    }
    
    public void addIndexBuilder(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("IndexBuilder");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_IndexBuilder.add(string);
    }
    
    public void addAllIndexInsert(
                    List<String> strings){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("IndexInsert");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        for(String string : strings) {
            if(string == null) {
                throw ObjectMacroException.parameterNull("IndexInsert");
            }
    
            this.list_IndexInsert.add(string);
        }
    }
    
    public void addIndexInsert(String string){
        if(string == null){
            throw ObjectMacroException.parameterNull("IndexInsert");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
    
        this.list_IndexInsert.add(string);
    }
    
    public void addAllMacroBodyParts(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "MacroBodyParts");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeMacroBodyParts(macro);
            this.list_MacroBodyParts.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeMacroBodyParts (Macro macro) {
        macro.apply(new InternalsInitializer("MacroBodyParts"){
            @Override
            void setInitStringBuilder(MInitStringBuilder mInitStringBuilder){
                
            
            
            }
            
            @Override
            void setStringPart(MStringPart mStringPart){
                
            
            
            }
            
            @Override
            void setParamInsertPart(MParamInsertPart mParamInsertPart){
                
            
            
            }
            
            @Override
            void setEolPart(MEolPart mEolPart){
                
            
            
            }
            
            @Override
            void setInsertMacroPart(MInsertMacroPart mInsertMacroPart){
                
            
            
            }
        });
    }
    
    public void addMacroBodyParts(MInitStringBuilder macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_MacroBodyParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addMacroBodyParts(MStringPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_MacroBodyParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addMacroBodyParts(MParamInsertPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_MacroBodyParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addMacroBodyParts(MEolPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_MacroBodyParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addMacroBodyParts(MInsertMacroPart macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("MacroBodyParts");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_MacroBodyParts.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllSetInternals(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("SetInternals");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "SetInternals");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeSetInternals(macro);
            this.list_SetInternals.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeSetInternals (Macro macro) {
        macro.apply(new InternalsInitializer("SetInternals"){
            @Override
            void setSetInternal(MSetInternal mSetInternal){
                
            
            
            }
        });
    }
    
    public void addSetInternals(MSetInternal macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("SetInternals");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_SetInternals.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    public void addAllSingleElementLists(
                    List<Macro> macros){
    
        if(macros == null){
            throw ObjectMacroException.parameterNull("SingleElementLists");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        int i = 0;
        
        for(Macro macro : macros) {
            if(macro == null) {
                throw ObjectMacroException.macroNull(i, "SingleElementLists");
            }
        
            if(this.getMacros() != macro.getMacros()) {
                throw ObjectMacroException.diffMacros();
            }
        
            this.verifyTypeSingleElementLists(macro);
            this.list_SingleElementLists.add(macro);
            this.children.add(macro);
            Macro.cycleDetector.detectCycle(this, macro);
        
            i++;
        }
    }
    
    
    void verifyTypeSingleElementLists (Macro macro) {
        macro.apply(new InternalsInitializer("SingleElementLists"){
            @Override
            void setNewStringValue(MNewStringValue mNewStringValue){
                
            
            
            }
        });
    }
    
    public void addSingleElementLists(MNewStringValue macro){
        if(macro == null){
            throw ObjectMacroException.parameterNull("SingleElementLists");
        }
        if(this.cacheBuilder != null) {
            throw ObjectMacroException.cannotModify(this.getClass().getSimpleName());
        }
        
        if(this.getMacros() != macro.getMacros()) {
            throw ObjectMacroException.diffMacros();
        }
    
        this.list_SingleElementLists.add(macro);
        this.children.add(macro);
        Macro.cycleDetector.detectCycle(this, macro);
    }
    
    private String buildReferencedMacroName() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_ReferencedMacroName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.ReferencedMacroNameNone != null) {
            sb.append(this.ReferencedMacroNameNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.ReferencedMacroNameBeforeFirst != null) {
                string = this.ReferencedMacroNameBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.ReferencedMacroNameAfterLast != null) {
                string = this.ReferencedMacroNameAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.ReferencedMacroNameSeparator != null) {
                string = this.ReferencedMacroNameSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildEnclosingClassName() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_EnclosingClassName;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.EnclosingClassNameNone != null) {
            sb.append(this.EnclosingClassNameNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.EnclosingClassNameBeforeFirst != null) {
                string = this.EnclosingClassNameBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.EnclosingClassNameAfterLast != null) {
                string = this.EnclosingClassNameAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.EnclosingClassNameSeparator != null) {
                string = this.EnclosingClassNameSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildIndexBuilder() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_IndexBuilder;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.IndexBuilderNone != null) {
            sb.append(this.IndexBuilderNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.IndexBuilderBeforeFirst != null) {
                string = this.IndexBuilderBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.IndexBuilderAfterLast != null) {
                string = this.IndexBuilderAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.IndexBuilderSeparator != null) {
                string = this.IndexBuilderSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildIndexInsert() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_IndexInsert;
    
        int i = 0;
        int nb_strings = strings.size();
    
        if(this.IndexInsertNone != null) {
            sb.append(this.IndexInsertNone.apply(i, "", nb_strings));
        }
    
        for(String string : strings) {
    
            if(this.IndexInsertBeforeFirst != null) {
                string = this.IndexInsertBeforeFirst.apply(i, string, nb_strings);
            }
    
            if(this.IndexInsertAfterLast != null) {
                string = this.IndexInsertAfterLast.apply(i, string, nb_strings);
            }
    
            if(this.IndexInsertSeparator != null) {
                string = this.IndexInsertSeparator.apply(i, string, nb_strings);
            }
    
            sb.append(string);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildMacroBodyParts() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.MacroBodyPartsContext;
        List<Macro> macros = this.list_MacroBodyParts;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.MacroBodyPartsNone != null) {
            sb.append(this.MacroBodyPartsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.MacroBodyPartsBeforeFirst != null) {
                expansion = this.MacroBodyPartsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.MacroBodyPartsAfterLast != null) {
                expansion = this.MacroBodyPartsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.MacroBodyPartsSeparator != null) {
                expansion = this.MacroBodyPartsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildSetInternals() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.SetInternalsContext;
        List<Macro> macros = this.list_SetInternals;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.SetInternalsNone != null) {
            sb.append(this.SetInternalsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.SetInternalsBeforeFirst != null) {
                expansion = this.SetInternalsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.SetInternalsAfterLast != null) {
                expansion = this.SetInternalsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.SetInternalsSeparator != null) {
                expansion = this.SetInternalsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    private String buildSingleElementLists() {
        StringBuilder sb = new StringBuilder();
        Context local_context = this.SingleElementListsContext;
        List<Macro> macros = this.list_SingleElementLists;
    
        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;
    
        if(this.SingleElementListsNone != null) {
            sb.append(this.SingleElementListsNone.apply(i, "", nb_macros));
        }
    
        for(Macro macro : macros) {
            expansion = macro.build(local_context);
    
            if(this.SingleElementListsBeforeFirst != null) {
                expansion = this.SingleElementListsBeforeFirst.apply(i, expansion, nb_macros);
            }
    
            if(this.SingleElementListsAfterLast != null) {
                expansion = this.SingleElementListsAfterLast.apply(i, expansion, nb_macros);
            }
    
            if(this.SingleElementListsSeparator != null) {
                expansion = this.SingleElementListsSeparator.apply(i, expansion, nb_macros);
            }
    
            sb.append(expansion);
            i++;
        }
    
        return sb.toString();
    }
    
    StringValue getReferencedMacroName() {
        return this.ReferencedMacroNameValue;
    }
    
    StringValue getEnclosingClassName() {
        return this.EnclosingClassNameValue;
    }
    
    StringValue getIndexBuilder() {
        return this.IndexBuilderValue;
    }
    
    StringValue getIndexInsert() {
        return this.IndexInsertValue;
    }
    
    MacroValue getMacroBodyParts() {
        return this.MacroBodyPartsValue;
    }
    
    MacroValue getSetInternals() {
        return this.SetInternalsValue;
    }
    
    MacroValue getSingleElementLists() {
        return this.SingleElementListsValue;
    }
    private void initMacroBodyPartsInternals(Context context) {
        for(Macro macro : this.list_MacroBodyParts) {
            macro.apply(new InternalsInitializer("MacroBodyParts"){
                @Override
                void setInitStringBuilder(MInitStringBuilder mInitStringBuilder){
                    
                
                
                }
                
                @Override
                void setStringPart(MStringPart mStringPart){
                    
                
                
                }
                
                @Override
                void setParamInsertPart(MParamInsertPart mParamInsertPart){
                    
                
                
                }
                
                @Override
                void setEolPart(MEolPart mEolPart){
                    
                
                
                }
                
                @Override
                void setInsertMacroPart(MInsertMacroPart mInsertMacroPart){
                    
                
                
                }
            });
        }
    }
    
    private void initSetInternalsInternals(Context context) {
        for(Macro macro : this.list_SetInternals) {
            macro.apply(new InternalsInitializer("SetInternals"){
                @Override
                void setSetInternal(MSetInternal mSetInternal){
                    
                    
                    mSetInternal.setVarName(SetInternalsContext, getIndexInsert());
                }
            });
        }
    }
    
    private void initSingleElementListsInternals(Context context) {
        for(Macro macro : this.list_SingleElementLists) {
            macro.apply(new InternalsInitializer("SingleElementLists"){
                @Override
                void setNewStringValue(MNewStringValue mNewStringValue){
                    
                
                
                }
            });
        }
    }
    
    private void initReferencedMacroNameDirectives() {
        
    }
    
    private void initEnclosingClassNameDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(".");
        this.EnclosingClassNameAfterLast = new DAfterLast(sb1.toString());
        this.EnclosingClassNameValue.setAfterLast(this.EnclosingClassNameAfterLast);StringBuilder sb2 = new StringBuilder();
        sb2.append("M");
        this.EnclosingClassNameBeforeFirst = new DBeforeFirst(sb2.toString());
        this.EnclosingClassNameValue.setBeforeFirst(this.EnclosingClassNameBeforeFirst);
    }
    
    private void initIndexBuilderDirectives() {
        
    }
    
    private void initIndexInsertDirectives() {
        
    }
    
    private void initMacroBodyPartsDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.MacroBodyPartsSeparator = new DSeparator(sb1.toString());
        this.MacroBodyPartsValue.setSeparator(this.MacroBodyPartsSeparator);
    }
    
    private void initSetInternalsDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.SetInternalsSeparator = new DSeparator(sb1.toString());
        this.SetInternalsValue.setSeparator(this.SetInternalsSeparator);
    }
    
    private void initSingleElementListsDirectives() {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(LINE_SEPARATOR);
        this.SingleElementListsSeparator = new DSeparator(sb1.toString());
        this.SingleElementListsValue.setSeparator(this.SingleElementListsSeparator);
    }
    @Override
    void apply(
            InternalsInitializer internalsInitializer) {
    
        internalsInitializer.setInsertMacroPart(this);
    }
    
    
    public String build() {
    
        CacheBuilder cache_builder = this.cacheBuilder;
    
        if(cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if(cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
        List<String> indentations = new LinkedList<>();
    
        initReferencedMacroNameDirectives();
        initEnclosingClassNameDirectives();
        initIndexBuilderDirectives();
        initIndexInsertDirectives();
        initMacroBodyPartsDirectives();
        initSetInternalsDirectives();
        initSingleElementListsDirectives();
        
        initMacroBodyPartsInternals(null);
        initSetInternalsInternals(null);
        initSingleElementListsInternals(null);
    
        StringBuilder sb0 = new StringBuilder();
    
        sb0.append("M");
        sb0.append(buildReferencedMacroName());
        sb0.append(" m");
        sb0.append(buildIndexInsert());
        sb0.append(" = ");
        sb0.append(buildEnclosingClassName());
        sb0.append("this.getMacros().new");
        sb0.append(buildReferencedMacroName());
        sb0.append("();");
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildMacroBodyParts());
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildSingleElementLists());
        sb0.append(LINE_SEPARATOR);
        sb0.append(buildSetInternals());
        sb0.append(LINE_SEPARATOR);
        sb0.append("sb");
        sb0.append(buildIndexBuilder());
        sb0.append(".append(m");
        sb0.append(buildIndexInsert());
        sb0.append(".build(null));");
    
        cache_builder.setExpansion(sb0.toString());
        return sb0.toString();
    }
    
    @Override
    String build(Context context) {
        return build();
    }
    
    private void setMacros(Macros macros){
        if(macros == null){
            throw new InternalException("macros cannot be null");
        }
    
        this.macros = macros;
    }
}