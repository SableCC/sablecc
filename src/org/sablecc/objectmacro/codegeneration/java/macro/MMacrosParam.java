/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.codegeneration.java.macro;

import java.util.*;

public class MMacrosParam
        extends Macro {

    private Map<Context, StringValue> list_Name = new LinkedHashMap<>();

    MMacrosParam(
            Macros macros) {

        setMacros(macros);
        this.list_Name = new LinkedHashMap<>();
    }

    void setName(
            Context context,
            StringValue value) {

        if (value == null) {
            throw new RuntimeException("value cannot be null here");
        }

        this.list_Name.put(context, value);
    }

    private String buildName(
            Context context) {

        StringValue stringValue = this.list_Name.get(context);
        return stringValue.build();
    }

    private StringValue getName(
            Context context) {

        return this.list_Name.get(context);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer) {

        internalsInitializer.setMacrosParam(this);
    }

    @Override
    String build(
            Context context) {

        CacheBuilder cache_builder = this.cacheBuilders.get(context);

        if (cache_builder == null) {
            cache_builder = new CacheBuilder();
        }
        else if (cache_builder.getExpansion() == null) {
            throw new InternalException("Cycle detection detected lately");
        }
        else {
            return cache_builder.getExpansion();
        }

        this.cacheBuilders.put(context, cache_builder);
        List<String> indentations = new LinkedList<>();

        StringBuilder sb0 = new StringBuilder();

        sb0.append("Macros ");
        sb0.append(buildName(context));
        cache_builder.setExpansion(sb0.toString());
        return sb0.toString();
    }

    private void setMacros(
            Macros macros) {

        if (macros == null) {
            throw new InternalException("macros cannot be null");
        }

        this.macros = macros;
    }
}
