package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Common code for property renderer that generates a List as
 * its underlying data structure.
 * 
 * <p>
 * For performance reaons, the actual list object used to store
 * data is lazily created.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
abstract class AbstractListField extends AbstractField {
    /** The field that stores the list. */
    protected JFieldVar field;
    
    /**
     * a method that lazily initializes a List.
     * Lazily created.
     *
     * [RESULT]
     * List _getFoo() {
     *   if(field==null)
     *     field = create new list;
     *   return field;
     * }
     */
    private JMethod internalGetter;

    /**
     * If this collection property is a collection of a primitive type,
     * this variable refers to that primitive type.
     * Otherwise null.
     */
    protected final JPrimitiveType primitiveType;

    protected final JClass listT = codeModel.ref(List.class).narrow(exposedType.boxify());

    /**
     * True to create a new instance of List eagerly in the constructor.
     * False otherwise.
     *
     * <p>
     * Setting it to true makes the generated code slower (as more list instances need to be
     * allocated), but it works correctly if the user specifies the custom type of a list.
     */
    private final boolean eagerInstanciation;

    /**
     * Call {@link #generate()} method right after this.
     */
    protected AbstractListField(ClassOutlineImpl outline, CPropertyInfo prop, boolean eagerInstanciation) {
        super(outline,prop);
        this.eagerInstanciation = eagerInstanciation;

        if( implType instanceof JPrimitiveType ) {
            // primitive types don't have this tricky distinction
            assert implType==exposedType;
            primitiveType = (JPrimitiveType)implType;
        } else
            primitiveType = null;
    }
    
    protected final void generate() {

        // for the collectionType customization to take effect, the field needs to be strongly typed,
        // not just List<Foo>.
        field = outline.implClass.field( JMod.PROTECTED, listT, prop.getName(false) );
        if(eagerInstanciation)
            field.init(newCoreList());

        annotate(field);

        // generate the rest of accessors
        generateAccessors();
    }

    private void generateInternalGetter() {
        internalGetter = outline.implClass.method(JMod.PROTECTED,listT,"_get"+prop.getName(true));
        if(!eagerInstanciation) {
            // if eagerly instanciated, the field can't be null
            fixNullRef(internalGetter.body());
        }
        internalGetter.body()._return(field);
    }

    /**
     * Generates statement(s) so that the successive {@link Accessor#ref(boolean)} with
     * true will always return a non-null list.
     *
     * This is useful to avoid generating redundant internal getter.
     */
    protected final void fixNullRef(JBlock block) {
        block._if(field.eq(JExpr._null()))._then()
            .assign(field,newCoreList());
    }

    @Override
	public final JType getRawType() {
        return codeModel.ref(List.class).narrow(exposedType.boxify());
    }
    
    private JExpression newCoreList() {
        return JExpr._new(getCoreListType());
    }
    
    /**
     * Concrete class that implements the List interface.
     * Used as the actual data storage.
     */
    protected abstract JClass getCoreListType();
    
    
    /** Generates accessor methods. */
    protected abstract void generateAccessors();
    
    
    
    /**
     * 
     * 
     * @author
     *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
     */
    protected abstract class Accessor extends AbstractField.Accessor {
        
        /**
         * Reference to the {@link AbstractListField#field}
         * of the target object.
         */
        protected final JFieldRef field;
        
        protected Accessor( JExpression $target ) {
            super($target);
            field = $target.ref(AbstractListField.this.field);
        }
        
        
        protected final JExpression unbox( JExpression exp ) {
            if(primitiveType==null) return exp;
            else                    return primitiveType.unwrap(exp);
        }
        protected final JExpression box( JExpression exp ) {
            if(primitiveType==null) return exp;
            else                    return primitiveType.wrap(exp);
        }
        
        /**
         * Returns a reference to the List field that stores the data.
         * <p>
         * Using this method hides the fact that the list is lazily
         * created.
         * 
         * @param canBeNull
         *      if true, the returned expression may be null (this is
         *      when the list is still not constructed.) This could be
         *      useful when the caller can deal with null more efficiently.
         *      When the list is null, it should be treated as if the list
         *      is empty.
         * 
         *      if false, the returned expression will never be null.
         *      This is the behavior users would see.
         */
        protected final JExpression ref(boolean canBeNull) {
            if(canBeNull)
                return field;
            if(internalGetter==null)
                generateInternalGetter();
            return $target.invoke(internalGetter);
        }

        public JExpression count() {
            return JOp.cond( field.eq(JExpr._null()), JExpr.lit(0), field.invoke("size") );
        }
        
        @Override
		public void unsetValues( JBlock body ) {
            body.assign(field,JExpr._null());
        }
        @Override
		public JExpression hasSetValue() {
            return field.ne(JExpr._null()).cand(field.invoke("isEmpty").not());
        }
    }
    
}
