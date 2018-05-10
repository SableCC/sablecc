/* This file was generated by SableCC's ObjectMacro. */

package org.test.back;

import java.util.*;

public class MSemicolonSeparator extends Macro{

    private Map<Context, InternalValue> list_X = new LinkedHashMap<>();


    public MSemicolonSeparator(){



    }

    void setX(
                Context context,
                InternalValue internal_value) {

            if(internal_value == null){
                throw new RuntimeException("macros cannot be null");
            }

            this.list_X.put(context, internal_value);
        }

    private String buildX(){
        StringBuilder sb = new StringBuilder();
        Context local_context = context;
        List<Macro> macros = this.list_X;

        int i = 0;
        int nb_macros = macros.size();
        String expansion = null;

        if(this.XNone != null){
            sb.append(this.XNone.apply(i, "", nb_macros));
        }

        for(Macro macro : macros){
            expansion = macro.build(local_context);

            if(this.XBeforeFirst != null){
                expansion = this.XBeforeFirst.apply(i, expansion, nb_macros);
            }

            if(this.XAfterLast != null){
                expansion = this.XAfterLast.apply(i, expansion, nb_macros);
            }

            if(this.XSeparator != null){
                expansion = this.XSeparator.apply(i, expansion, nb_macros);
            }

            sb.append(expansion);
            i++;
        }

        return sb.toString();
    }

    private InternalValue getX(Context context){
        return this.list_X.get(context);
    }


    @Override
    void apply(
            InternalsInitializer internalsInitializer){

        internalsInitializer.setSemicolonSeparator(this);
    }

   @Override
    public String build(Context context){

        BuildState buildState = this.build_states.get(context);

        if(buildState == null){
            buildState = new BuildState();
        }
        else if(buildState.getExpansion() == null){
            throw ObjectMacroException.cyclicReference("SemicolonSeparator");
        }
        else{
            return buildState.getExpansion();
        }
        this.build_states.put(context, buildState);

        

        

        StringBuilder sb0 = new StringBuilder();

        sb0.append("Le corps de C : ");
        sb0.append(buildX(context));

        buildState.setExpansion(sb0.toString());
        return sb0.toString();
    }

}