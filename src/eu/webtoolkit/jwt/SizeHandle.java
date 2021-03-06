/*
 * Copyright (C) 2009 Emweb bvba, Leuven, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
package eu.webtoolkit.jwt;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.lang.ref.*;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.*;
import javax.servlet.*;
import eu.webtoolkit.jwt.*;
import eu.webtoolkit.jwt.chart.*;
import eu.webtoolkit.jwt.utils.*;
import eu.webtoolkit.jwt.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SizeHandle {
	private static Logger logger = LoggerFactory.getLogger(SizeHandle.class);

	public static void loadJavaScript(WApplication app) {
		app.loadJavaScript("js/SizeHandle.js", wtjs1());
	}

	static WJavaScriptPreamble wtjs1() {
		return new WJavaScriptPreamble(
				JavaScriptScope.WtClassScope,
				JavaScriptObjectType.JavaScriptConstructor,
				"SizeHandle",
				"function(c,k,e,r,v,w,x,y,h,f,l,n,o){function s(b){b=c.pageCoordinates(b);return Math.min(Math.max(k==\"h\"?b.x-g.x-d.x:b.y-g.y-d.y,v),w)}function i(b){var t=s(b);if(k==\"h\")a.style.left=d.x+t+\"px\";else a.style.top=d.y+t+\"px\";c.cancelEvent(b)}function u(b){if(a.parentNode!=null){a.parentNode.removeChild(a);y(s(b))}}var a=document.createElement(\"div\");a.style.position=\"absolute\";a.style.zIndex=\"100\";if(k==\"v\"){a.style.width=r+\"px\";a.style.height= e+\"px\"}else{a.style.height=r+\"px\";a.style.width=e+\"px\"}var g,d=c.widgetPageCoordinates(h);e=c.widgetPageCoordinates(f);if(l.touches)g=c.widgetCoordinates(h,l.touches[0]);else{g=c.widgetCoordinates(h,l);c.capture(null);c.capture(a)}n-=c.px(h,\"marginLeft\");o-=c.px(h,\"marginTop\");d.x+=n-e.x;d.y+=o-e.y;g.x-=n-e.x;g.y-=o-e.y;a.style.left=d.x+\"px\";a.style.top=d.y+\"px\";a.className=x;f.appendChild(a);c.cancelEvent(l);if(document.addEventListener){var j=$(\".Wt-domRoot\")[0];j||(j=f);var p=j.style[\"pointer-events\"]; p||(p=\"all\");j.style[\"pointer-events\"]=\"none\";var q=document.body.style.cursor;q||(q=\"auto\");document.body.style.cursor=k==\"h\"?\"ew-resize\":\"ns-resize\";function m(b){j.style[\"pointer-events\"]=p;document.body.style.cursor=q;document.removeEventListener(\"mousemove\",i);document.removeEventListener(\"mouseup\",m);document.removeEventListener(\"touchmove\",i);document.removeEventListener(\"touchend\",m);u(b)}document.addEventListener(\"mousemove\",i,{capture:true});document.addEventListener(\"mouseup\",m,{capture:true}); document.addEventListener(\"touchmove\",i,{capture:true});document.addEventListener(\"touchend\",m,{capture:true})}else{a.onmousemove=f.ontouchmove=i;a.onmouseup=f.ontouchend=function(b){f.ontouchmove=null;f.ontouchend=null;u(b)}}}");
	}
}
