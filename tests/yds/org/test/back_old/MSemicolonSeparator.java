/* This file was generated by SableCC's ObjectMacro. */

package org.test.back_old;

import java.util.*;

public class MSemicolonSeparator extends Macro{

    private Map<Context, Macro[]> list_X = new LinkedHashMap<>();

    private final Context XContext = new Context();

    public MSemicolonSeparator(){
    }

    void setX(
            Context context,
            Macro macros[]) {

        if(macros == null){
            throw new RuntimeException("macros cannot be null here");
        }

        Macro[] tempMacros = new Macro[macros.length];
        int i = 0;

        for(Macro macro : macros){

            if(macro == null){
                throw ObjectMacroException.macroNull(i, "X");
            }

            macro.apply(new InternalsInitializer("X"){
@Override
void setEmptyMacro(MEmptyMacro mEmptyMacro){

        }
});

            tempMacros[i++] = macro;
        }

        this.list_X.put(context, tempMacros);
    }

    private String buildX(Context context){

        StringBuilder sb0 = new StringBuilder();
        Context local_context = XContext;
        Macro macros[] = this.list_X.get(context);
                boolean first = true;
        int i = 0;

        for(Macro macro : macros){
                        if(first) {
  first = false;
}
else {
           sb0.append("; ");
}

            sb0.append(macro.build(local_context));
            i++;

                    }

        return sb0.toString();
    }

    private Macro[] getX(Context context){

        return this.list_X.get(context);
    }

    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setSemicolonSeparator(this);
    }

    @Override
     String build(Context context){

        String local_expansion = this.expansions.get(context);

        if(local_expansion != null){
            return local_expansion;
        }

        StringBuilder sb0 = new StringBuilder();

        sb0.append("Le corps de C : ");
        sb0.append(buildX(context));

        local_expansion = sb0.toString();
        this.expansions.put(context, local_expansion);
        return local_expansion;
    }
}
