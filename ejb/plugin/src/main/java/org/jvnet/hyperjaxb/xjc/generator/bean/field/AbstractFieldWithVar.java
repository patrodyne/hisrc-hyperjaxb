package org.jvnet.hyperjaxb.xjc.generator.bean.field;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class AbstractFieldWithVar extends AbstractField {
    
    /**
     * Field declaration of the actual list object that we use
     * to store data.
     */
    protected JFieldVar field;
    
    /**
     * Invoke {@link #createField()} after calling the
     * constructor.
     */
    AbstractFieldWithVar( ClassOutlineImpl outline, CPropertyInfo prop ) {
        super(outline,prop);
    }
    
    protected final void createField() {
        field = outline.implClass.field( JMod.PROTECTED,
            getFieldType(), prop.getName(false) );

        annotate(field);
    }

    /**
     * Gets the name of the getter method.
     *
     * <p>
     * This encapsulation is necessary because sometimes we use
     * {@code isXXXX} as the method name.
     */
    protected String getGetterMethod() {
        return (getFieldType().boxify().getPrimitiveType()==codeModel.BOOLEAN?"is":"get")+prop.getName(true);
    }

    /**
     * Returns the type used to store the value of the field in memory.
     */
    protected abstract JType getFieldType();

    protected JFieldVar ref() { return field; }

    @Override
	public final JType getRawType() {
        return exposedType;
    }
    
    protected abstract class Accessor extends AbstractField.Accessor {
    
        protected Accessor(JExpression $target) {
            super($target);
            this.$ref = $target.ref(AbstractFieldWithVar.this.ref());
        }
        
        /**
         * Reference to the field bound by the target object.
         */
        protected final JFieldRef $ref;

        @Override
		public final void toRawValue(JBlock block, JVar $var) {
            block.assign($var,$target.invoke(getGetterMethod()));
        }

        @Override
		public final void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
            block.invoke($target,("set"+prop.getName(true))).arg($var);
        }
    }
}
