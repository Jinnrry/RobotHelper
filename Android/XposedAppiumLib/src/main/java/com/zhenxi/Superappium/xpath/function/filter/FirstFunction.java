/*    */
package com.zhenxi.Superappium.xpath.function.filter;
/*    */
/*    */

import com.zhenxi.Superappium.ViewImage;
import com.zhenxi.Superappium.xpath.parser.expression.SyntaxNode;

import java.util.List;

/*    */
/*    */

/*    */
/*    */ public class FirstFunction implements FilterFunction
        /*    */ {
    /*    */
    public Object call(ViewImage element, List<SyntaxNode> params)
    /*    */ {
        /* 11 */
        return Boolean.valueOf(element.index().intValue() == 0);
        /*    */
    }

    /*    */
    /*    */
    public String getName()
    /*    */ {
        /* 16 */
        return "first";
        /*    */
    }
    /*    */
}


/* Location:              /Users/alienhe/.gradle/caches/modules-2/files-2.1/com.virjar/ratel-extersion/1.0.4/c3247d9a6d2e125b04726c3b311ee721ef979ad2/ratel-extersion-1.0.4.jar!/com/virjar/ratel/api/extension/superappium/xpath/function/filter/FirstFunction.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */