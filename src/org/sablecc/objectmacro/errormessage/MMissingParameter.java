/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.errormessage;

import java.util.*;

public class MMissingParameter
        extends Macro {

    private DSeparator LineSeparator;

    private DBeforeFirst LineBeforeFirst;

    private DAfterLast LineAfterLast;

    private DNone LineNone;

    final List<String> list_Line;

    final Context LineContext = new Context();

    final StringValue LineValue;

    private DSeparator CharSeparator;

    private DBeforeFirst CharBeforeFirst;

    private DAfterLast CharAfterLast;

    private DNone CharNone;

    final List<String> list_Char;

    final Context CharContext = new Context();

    final StringValue CharValue;

    private DSeparator MacroNameSeparator;

    private DBeforeFirst MacroNameBeforeFirst;

    private DAfterLast MacroNameAfterLast;

    private DNone MacroNameNone;

    final List<String> list_MacroName;

    final Context MacroNameContext = new Context();

    final StringValue MacroNameValue;

    private DSeparator VersionSeparator;

    private DBeforeFirst VersionBeforeFirst;

    private DAfterLast VersionAfterLast;

    private DNone VersionNone;

    final List<String> list_Version;

    final Context VersionContext = new Context();

    final StringValue VersionValue;

    private DSeparator ParameterNameSeparator;

    private DBeforeFirst ParameterNameBeforeFirst;

    private DAfterLast ParameterNameAfterLast;

    private DNone ParameterNameNone;

    final List<String> list_ParameterName;

    final Context ParameterNameContext = new Context();

    final StringValue ParameterNameValue;

    private DSeparator TypeSeparator;

    private DBeforeFirst TypeBeforeFirst;

    private DAfterLast TypeAfterLast;

    private DNone TypeNone;

    final List<String> list_Type;

    final Context TypeContext = new Context();

    final StringValue TypeValue;

    MMissingParameter(
            Macros macros) {

        setMacros(macros);
        this.list_Line = new LinkedList<>();
        this.list_Char = new LinkedList<>();
        this.list_MacroName = new LinkedList<>();
        this.list_Version = new LinkedList<>();
        this.list_ParameterName = new LinkedList<>();
        this.list_Type = new LinkedList<>();

        this.LineValue = new StringValue(this.list_Line, this.LineContext);
        this.CharValue = new StringValue(this.list_Char, this.CharContext);
        this.MacroNameValue
                = new StringValue(this.list_MacroName, this.MacroNameContext);
        this.VersionValue
                = new StringValue(this.list_Version, this.VersionContext);
        this.ParameterNameValue = new StringValue(this.list_ParameterName,
                this.ParameterNameContext);
        this.TypeValue = new StringValue(this.list_Type, this.TypeContext);
    }

    public void addAllLine(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("Line");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("Line");
            }

            this.list_Line.add(string);
        }
    }

    public void addLine(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("Line");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_Line.add(string);
    }

    public void addAllChar(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("Char");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("Char");
            }

            this.list_Char.add(string);
        }
    }

    public void addChar(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("Char");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_Char.add(string);
    }

    public void addAllMacroName(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("MacroName");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("MacroName");
            }

            this.list_MacroName.add(string);
        }
    }

    public void addMacroName(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("MacroName");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_MacroName.add(string);
    }

    public void addAllVersion(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("Version");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("Version");
            }

            this.list_Version.add(string);
        }
    }

    public void addVersion(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("Version");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_Version.add(string);
    }

    public void addAllParameterName(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("ParameterName");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("ParameterName");
            }

            this.list_ParameterName.add(string);
        }
    }

    public void addParameterName(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("ParameterName");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_ParameterName.add(string);
    }

    public void addAllType(
            List<String> strings) {

        if (this.macros == null) {
            throw ObjectMacroException.parameterNull("Type");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }
        for (String string : strings) {
            if (string == null) {
                throw ObjectMacroException.parameterNull("Type");
            }

            this.list_Type.add(string);
        }
    }

    public void addType(
            String string) {

        if (string == null) {
            throw ObjectMacroException.parameterNull("Type");
        }
        if (this.cacheBuilder != null) {
            throw ObjectMacroException
                    .cannotModify(this.getClass().getSimpleName());
        }

        this.list_Type.add(string);
    }

    private String buildLine() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Line;

        int i = 0;
        int nb_strings = strings.size();

        if (this.LineNone != null) {
            sb.append(this.LineNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.LineBeforeFirst != null) {
                string = this.LineBeforeFirst.apply(i, string, nb_strings);
            }

            if (this.LineAfterLast != null) {
                string = this.LineAfterLast.apply(i, string, nb_strings);
            }

            if (this.LineSeparator != null) {
                string = this.LineSeparator.apply(i, string, nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    private String buildChar() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Char;

        int i = 0;
        int nb_strings = strings.size();

        if (this.CharNone != null) {
            sb.append(this.CharNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.CharBeforeFirst != null) {
                string = this.CharBeforeFirst.apply(i, string, nb_strings);
            }

            if (this.CharAfterLast != null) {
                string = this.CharAfterLast.apply(i, string, nb_strings);
            }

            if (this.CharSeparator != null) {
                string = this.CharSeparator.apply(i, string, nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    private String buildMacroName() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_MacroName;

        int i = 0;
        int nb_strings = strings.size();

        if (this.MacroNameNone != null) {
            sb.append(this.MacroNameNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.MacroNameBeforeFirst != null) {
                string = this.MacroNameBeforeFirst.apply(i, string, nb_strings);
            }

            if (this.MacroNameAfterLast != null) {
                string = this.MacroNameAfterLast.apply(i, string, nb_strings);
            }

            if (this.MacroNameSeparator != null) {
                string = this.MacroNameSeparator.apply(i, string, nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    private String buildVersion() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Version;

        int i = 0;
        int nb_strings = strings.size();

        if (this.VersionNone != null) {
            sb.append(this.VersionNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.VersionBeforeFirst != null) {
                string = this.VersionBeforeFirst.apply(i, string, nb_strings);
            }

            if (this.VersionAfterLast != null) {
                string = this.VersionAfterLast.apply(i, string, nb_strings);
            }

            if (this.VersionSeparator != null) {
                string = this.VersionSeparator.apply(i, string, nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    private String buildParameterName() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_ParameterName;

        int i = 0;
        int nb_strings = strings.size();

        if (this.ParameterNameNone != null) {
            sb.append(this.ParameterNameNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.ParameterNameBeforeFirst != null) {
                string = this.ParameterNameBeforeFirst.apply(i, string,
                        nb_strings);
            }

            if (this.ParameterNameAfterLast != null) {
                string = this.ParameterNameAfterLast.apply(i, string,
                        nb_strings);
            }

            if (this.ParameterNameSeparator != null) {
                string = this.ParameterNameSeparator.apply(i, string,
                        nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    private String buildType() {

        StringBuilder sb = new StringBuilder();
        List<String> strings = this.list_Type;

        int i = 0;
        int nb_strings = strings.size();

        if (this.TypeNone != null) {
            sb.append(this.TypeNone.apply(i, "", nb_strings));
        }

        for (String string : strings) {

            if (this.TypeBeforeFirst != null) {
                string = this.TypeBeforeFirst.apply(i, string, nb_strings);
            }

            if (this.TypeAfterLast != null) {
                string = this.TypeAfterLast.apply(i, string, nb_strings);
            }

            if (this.TypeSeparator != null) {
                string = this.TypeSeparator.apply(i, string, nb_strings);
            }

            sb.append(string);
            i++;
        }

        return sb.toString();
    }

    StringValue getLine() {

        return this.LineValue;
    }

    StringValue getChar() {

        return this.CharValue;
    }

    StringValue getMacroName() {

        return this.MacroNameValue;
    }

    StringValue getVersion() {

        return this.VersionValue;
    }

    StringValue getParameterName() {

        return this.ParameterNameValue;
    }

    StringValue getType() {

        return this.TypeValue;
    }

    private void initLineDirectives() {

    }

    private void initCharDirectives() {

    }

    private void initMacroNameDirectives() {

    }

    private void initVersionDirectives() {

    }

    private void initParameterNameDirectives() {

    }

    private void initTypeDirectives() {

        StringBuilder sb1 = new StringBuilder();
        sb1.append(", ");
        this.TypeSeparator = new DSeparator(sb1.toString());
        this.TypeValue.setSeparator(this.TypeSeparator);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setMissingParameter(this);
    }

    public String build() {

        CacheBuilder cache_builder = this.cacheBuilder;

        if (cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if (cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }
        this.cacheBuilder = cache_builder;
        List<String> indentations = new LinkedList<>();

        initLineDirectives();
        initCharDirectives();
        initMacroNameDirectives();
        initVersionDirectives();
        initParameterNameDirectives();
        initTypeDirectives();

        StringBuilder sb0 = new StringBuilder();

        MSemanticErrorHead m1 = getMacros().newSemanticErrorHead();

        sb0.append(m1.build(null));
        sb0.append(LINE_SEPARATOR);
        sb0.append(LINE_SEPARATOR);
        sb0.append("Line : ");
        sb0.append(buildLine());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Char : ");
        sb0.append(buildChar());
        sb0.append(LINE_SEPARATOR);
        sb0.append("Macro '");
        sb0.append(buildMacroName());
        sb0.append("' version '");
        sb0.append(buildVersion());
        sb0.append("' must have a parameter named '");
        sb0.append(buildParameterName());
        sb0.append("' of type '");
        sb0.append(buildType());
        sb0.append("'.");

        cache_builder.setExpansion(sb0.toString());
        return sb0.toString();
    }

    @Override
    String build(
            Context context) {

        return build();
    }

    private void setMacros(
            Macros macros) {

        if (macros == null) {
            throw new InternalException("macros cannot be null");
        }

        this.macros = macros;
    }
}
