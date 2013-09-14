pjax-tiles
==========

Sample web application project that demonstrates how-to use [pjax](https://github.com/defunkt/jquery-pjax) with 
[Apache Tiles](http://tiles.apache.org/).

## Intro

Back in the days it was common practice for web applications to be developed based upon the so-called [MVC paradigm](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller).
All of this used to happen on the server side and often [JSPs](http://en.wikipedia.org/wiki/Java_Server_Pages) were used to render HTML pages, which were sent back 
to the browser. 

Fast forward to today. Given the evolution of modern web browsers, the rise of mobile applications and 
the vast adoption of new web standards (HTML5, CSS3 and JavaScript) it became popular to implement the MVC pattern on 
the clients itself and to communicate with the server only to send or receive data. So-called 
[Single Page Applications](http://en.wikipedia.org/wiki/Single-page_application) are now considered to be state-of-the-art.

Usually [AJAX](http://en.wikipedia.org/wiki/AJAX) is used to send/retrieve the necessary data from the server and 
then a [DOM](http://en.wikipedia.org/wiki/Document_Object_Model) manipulation is done to update the view accordingly. 
While that approach certainly conserves bandwith and results in a flicker-free page update, there are downsides as well. 
One of them is that the browser history gets out of sync. This is the problem space that [pjax](https://github.com/defunkt/jquery-pjax)
is addressing. 

There are more things to consider when developing an application using MVC on the client side. They require a 
modern browser and without JavaScript the app won't work at all. This is why some people promote 
[ROCA - Resource-oriented Client Architecture](http://roca-style.org/) and there are lots of valid arguments for the 
recommendations provided. 

Ultimately, it may be best to embrace the philosophy of [graceful degradation](http://en.wikipedia.org/wiki/Graceful_degradation) 
and develop web applications that use [progressive enhancements](http://en.wikipedia.org/wiki/Progressive_enhancement) to allow 
"everyone to access the basic content and functionality of a web page, using any browser or Internet connection, while also 
providing an enhanced version of the page to those with more advanced browser software or greater bandwidth." 
This is exactly the philosophy behind [pjax](https://github.com/defunkt/jquery-pjax).


## Apache Tiles and pjax

[Apache Tiles](http://tiles.apache.org/) is a templating framework that provides the means "to define page fragments which can be 
assembled into a complete page at runtime." This goes hand-in-hand with the idea of [pjax](https://github.com/defunkt/jquery-pjax). 
If the browser/user-agent supports JavaScript only the part has changed is updated, otherwise the fallback mechanism would load 
the entire page. For more information please refer to the corresponding documentation: [Composite View Pattern](http://tiles.apache.org/framework/tutorial/pattern.html)


## Example

I've pulled together a small Maven-based sample project using the [Spring framework](http://www.springsource.org/spring-framework)
and [Apache Tiles](http://tiles.apache.org/) as a proof of concept. The standard [template](https://github.com/steinermatt/pjax-tiles/blob/master/src/main/webapp/WEB-INF/layouts/default.jsp)
looks as follows: 
``` jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<title><tiles:getAsString name="title"/></title>
	
	<tiles:insertAttribute name="meta"/>
	<tiles:insertAttribute name="stylesheets"/>
</head>
<body>
	<tiles:insertAttribute name="header"/>
	<div id="pjax-container">
		<tiles:insertAttribute name="content"/>
	</div>
	<tiles:insertAttribute name="footer"/>
	<tiles:insertAttribute name="js"/>
</body>
</html>
```

Nothing too fancy really and it resembles a basic web page layout as commonly seen in [Bootstrap](http://getbootstrap.com/)-based 
web applications. In a nutshell, all we would need to do on the server-side to support [pjax](https://github.com/defunkt/jquery-pjax)
would be to overwrite this definition and only serve the "content" fragment/tile in case a pjax-request is executed.

For this purpose we simply create another [definition](https://github.com/steinermatt/pjax-tiles/blob/master/src/main/webapp/WEB-INF/layouts/pjax.jsp) that contains only the "content" tile:
``` jsp
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertAttribute name="content" />
``` 

So, how-to replace the definition at runtime? I figured the most easy way to do this is by creating a so-called [ViewPreparer](http://tiles.apache.org/framework/tutorial/advanced/preparer.html).

Here's the corresponding [PJAXViewPreparer](https://github.com/steinermatt/pjax-tiles/blob/master/src/main/java/net/inscope/sandbox/pjax/web/PJAXViewPreparer.java):
``` java
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
``` 

The last remaining step is to register this ViewPreparer. This is done in the [tiles-defs.xml](https://github.com/steinermatt/pjax-tiles/blob/master/src/main/webapp/WEB-INF/tiles-defs.xml):
``` xml
<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE tiles-definitions PUBLIC
       "-//Apache Software Foundation//DTD Tiles Configuration 2.1//EN"
       "http://tiles.apache.org/dtds/tiles-config_2_1.dtd">
<tiles-definitions>
	<definition name="default.definition" template="/WEB-INF/layouts/default.jsp">
		<put-attribute name="meta" value="/WEB-INF/views/tiles/meta.jsp" />
		<put-attribute name="js" value="/WEB-INF/views/tiles/js.jsp" />
		<put-attribute name="stylesheets" value="/WEB-INF/views/tiles/stylesheets.jsp" />
		<put-attribute name="header" value="/WEB-INF/views/tiles/header.jsp" />
		<put-attribute name="content" value="" />
		<put-attribute name="footer" value="/WEB-INF/views/tiles/footer.jsp" />
	</definition>

	<definition name="home" extends="default.definition" preparer="net.inscope.sandbox.pjax.web.PJAXViewPreparer">
		<put-attribute name="title" value="pjax-tiles demo" type="string" cascade="true"/>
		<put-attribute name="content" value="/WEB-INF/views/home.jsp" />
	</definition>
	
	<definition name="lorem_ipsum" extends="default.definition" preparer="net.inscope.sandbox.pjax.web.PJAXViewPreparer">
		<put-attribute name="title" value="pjax-tiles demo" type="string" cascade="true"/>
		<put-attribute name="content" value="/WEB-INF/views/lorem_ipsum.jsp" />
	</definition>
	
</tiles-definitions>
```

**NOTE:** Unfortunately, there seems no way to register a global ViewPreparer. I stumbled upon a corresponding 
[development request](https://issues.apache.org/jira/browse/TILES-363) on JIRA, but it was never implemented.

## Misc

### Submit handling

It seems that [pjax](https://github.com/defunkt/jquery-pjax) cannot handle forms with multiple submit buttons out-of-the-box.
The problem is that the "handleSubmit" method only serializes the form elements, but the value of the submit button/event is not
included. A workaround could be to do something as described in this blog: [Using Forms with multiple Submit Buttons and jQuery Events](http://www.webmuse.co.uk/blog/using-forms-with-multiple-submit-buttons-and-jquery-events/).
