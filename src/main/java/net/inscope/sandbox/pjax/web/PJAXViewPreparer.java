package net.inscope.sandbox.pjax.web;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.PreparerException;
import org.apache.tiles.preparer.ViewPreparer;

public class PJAXViewPreparer  implements ViewPreparer 
{

    public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) throws PreparerException 
    {
    	if (tilesContext.getHeader().containsKey("x-pjax"))
    	{
    		Attribute template = attributeContext.getTemplateAttribute();
        	
        	String templatePath = (String) template.getValue();
        	templatePath = templatePath.replace("default", "pjax");
        	
        	template.setValue(templatePath);
        	
        	attributeContext.setTemplateAttribute(template);
    	}
    }
}