/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://jsftemplating.dev.java.net/public/CDDLv1.0.html or
 * jsftemplating/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at jsftemplating/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

////////////////////////////////////////////////////////////////////////////
//  The following functions facilitate XMLHttpRequest calls
////////////////////////////////////////////////////////////////////////////

/**
 *  <p>	This submits an AJAX request.  The response is expected to be HTML
 *	which replaces "targetNode".  "extraInfo" may be used to provide
 *	additional QUERY_STRING-type parameters to the request.  It should
 *	start with a '&', but not end with one (i.e.  &name=value).</p>
 *
 *  <p>	Alternately, you may provide your own JS function to handle the AJAX
 *	response.  In this case, targetNode may be null (will process the
 *	whole page normally), or any valid UIComponent clientId (processing
 *	will only occur at that level and its children, if any).</p>
 */
function submitAjaxRequest(targetNode, extraInfo, func) {
    // FIXME: clean the following 'if' up, should allow string id or object
    if (targetNode && targetNode.toLowerCase) {
	// String
	targetNode = document.getElementById(targetNode);
    }

    // Get content to submit
    var form = null;
    if (targetNode) {
	form = findParentNodeByTypeAndProp(targetNode, 'form', null, null);
    } else {
	form = findNode(document, 'form', null, null);
    }
    var content = getFormValues(form);

    // Make XMLHttpRequest
    var req = new XMLHttpRequest();
    req.open('POST', form.action, true);
    req.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    if (func) {
	req.onreadystatechange = func;
    } else {
	req.onreadystatechange = 
	    function() {
		// NOTE: This function assumes the parentNode *ONLY* contains
		// NOTE: the targetNode, otherwise the other HTML will be lost.
		// NOTE: Use a custom function if you want anything different.
		if (req.readyState == 4) {
//alert('Got response: ' + req.responseText);
//alert(printThis(targetNode.attributes.getNamedItem("id")));
		    targetNode.parentNode.innerHTML = req.responseText;
		}
	    };
    }
    content += "&ajaxReq=" + encodeURIComponent(targetNode.id);
    if (extraInfo) {
	content += '&' + extraInfo;
    }
    req.send(content);

    return req;
}

////////////////////////////////////////////////////////////////////////////
//  The following functions assist with Editor related functionality
////////////////////////////////////////////////////////////////////////////

/**
 *  <p>	This function enables and disables editing features.</p>
 */
function toggleEditMode(event) {
    var elt = getEditArea(event);
    if (!elt) {
	return;
    }
    if (elt.className == 'lfEditArea') {
	elt.className = 'lfEditAreaOFF';
    } else {
	elt.className = 'lfEditArea';
    }

    // Make sure to hide the menu (if applicable)
    var menu = getMenu(event);
    if (menu) {
	hideElement(menu);
    }
}

function getEditArea(event) {
    var elt = getEventSource(event);
    while (elt) {
	if (elt.className) {
	    if (elt.className.indexOf('lfEditArea') > -1) {
		return elt;
	    } else if (elt.className.indexOf('popupArea') > -1) {
		return findNode(
		    elt, null, "className", "lfEditArea");
	    }
	}
	elt = elt.parentNode;
    }
    return null;
}

function getEditorContent(editorNode) {
    var content = findNode(editorNode, null, "className", "lfEditorContent");
    if (content) {
	// Found span around content, get to content itself...
	var nodeList = content.childNodes;
	if (!nodeList || (nodeList.length == 0)) {
	    return null;
	}

	// Return the first one...
	return nodeList[0];
    }
    return null;
}

////////////////////////////////////////////////////////////////////////////
//  The following functions enable the custom popup menu
////////////////////////////////////////////////////////////////////////////

/**
 *  <p>	This function initializes the popup menu.</p>
 */
function initPopupMenu() {
    // FIXME: Add the following listener only when the popup menu is
    // FIXME: visible (use removeEventListener("mousedown", hide, true))
    document.addEventListener("mousedown", hidePopupMenu, true);
//    document.addEventListener("contextmenu", showPopupMenu, true);
    document.oncontextmenu = showPopupMenu;
}

/**
 *  <p>	This function hides the popup menu.</p>
 */
function hidePopupMenu(event) {
    // First make sure we should hide it
    var src = getEventSource(event);
    if ((src.nodeName == 'A') || (src.nodeName == 'a')) {
	return;
    }

    // Get the menu Node (div)
    var menu = getMenu(event);
    if (menu) {
	hideElement(menu);
    }
}

function hideElement(elt) {
    elt.style.visibility = "hidden";
}

/**
 *  <p>	This function displays the popup menu.</p>
 */
function showPopupMenu(event) {
    // Get the menu Node (div)
    var menu = getMenu(event);
    if (!menu) {
	return;
    }

    // First ensure it is invisible while we prepare to show it
    hideElement(menu);

    // FIXME: Fix getElementById() to be generalized
    // Set the x,y coords
    menu.style.left = event.pageX + "px";
    menu.style.top = event.pageY + "px";

    menu.style.visibility = "visible";
    return false;
}

function getMenu(event) {
    // Find the popup menu
    var src = getEventSource(event); // popupMenu node
    var menuId = null;		     // Actual Menu Id
    while (src) {
	if (src.attributes) {
	    if (src.attributes['popupMenuId']) {
		menuId = src.attributes['popupMenuId'].value;
		break;
	    }
	}
	src = src.parentNode;
    }

    // Not inside a menu area, look for any menu on the page
    if (!menuId) {
	src = findNode(document, null, "attributes", "popupMenuId");
	if (src) {
	    menuId = src.attributes['popupMenuId'].value;
	}
    }

    return menuId ? document.getElementById(menuId) : null;
}


////////////////////////////////////////////////////////////////////////////
//  The following are utility functions
////////////////////////////////////////////////////////////////////////////

/**
 *  <p>	This function finds a node of the given type w/ matching property name
 *	and value by looking at the parents of the given node.  The type is the
 *	type of node to find (i.e. "IMG").  The propName is the name of the
 *	property to match (i.e. "src" on an "IMG" node).  The propVal is the
 *	value that must be contained in propName; this value does not have to
 *	match exactly, it only needs to exist within the property.</p>
 */
function findParentNodeByTypeAndProp(node, type, propName, propVal) {
    // First check to see if node is what we are looking for...
    if (node.nodeName && (node.nodeName.toLowerCase() == type)) {
	if (!propName) {
	    return node;
	}
	if (node[propName]) {
	    var val = ('' + node[propName]).toLowerCase();
	    if (val.indexOf(propVal) > -1) {
		return node;
	    }
	}
    }

    // Not what we want, look at the parent
    node = node.parentNode;
    if (!node) {
	return null;
    }
    return findParentNodeByTypeAndProp(node, type, propName, propVal);
}

/**
 *  <p>	This function finds a node of the given type w/ matching property name
 *	and value by looking recursively deep at the children of the given
 *	node.  The type is the type of node to find (i.e. "IMG").  The propName
 *	is the name of the property to match (i.e. "src" on an "IMG" node).
 *	The propVal is the value that must be contained in propName; this value
 *	does not have to match exactly, it only needs to exist within the
 *	property.</p>
 */
function findNode(node, type, propName, propVal) {
    // First check to see if node is what we are looking for...
    if (!type || (node.nodeName == type)) {
	if (!propName) {
	    return node;
	}
	if (node[propName]) {
	    if (node[propName].indexOf) {
		if (node[propName].indexOf(propVal) > -1) {
		    return node;
		}
	    } else if ((node[propName].getNamedItem) && node[propName].getNamedItem(propVal)) {
		return node;
	    }
	}
    }

    // Not what we want, walk its children if any
    var nodeList = node.childNodes;
    if (!nodeList || (nodeList.length == 0)) {
	return null;
    }
    var result;
    for (var count = 0; count<nodeList.length; count++) {
	// Recurse
	result = findNode(nodeList[count], type, propName, propVal);
	if (result) {
	    // Propagate the result
	    return result;
	}
    }

    // Not found
    return null;
}

/**
 *  <p>	This is a helper method for obtaining the source of the event.</p>
 */
function getEventSource(event) {
    // Get the event source
    if (!event) {
	event = window.event;
    }
    var elt = null;
    if (event) {
	elt = (event.target) ? event.target : event.srcElement;
    }
    return elt;
}

/**
 *  <p> This function retrieves all the form values from the given form
 *	and returns a urlencoded QUERY_STRING.</p>
 */
function getFormValues(form) {
    if (!form) {
	return "";
    }
    var nodeName = '';
    var type = '';
    var value = '';
    var id = '';

    // Iterate through the form fields
    for (var idx = 0; idx < form.elements.length; idx++) {
	// Skip buttons...
	nodeName = form.elements[idx].nodeName;
	type = form.elements[idx].type;
	if ((type == 'button') || (nodeName == 'BUTTON')) {
	    continue;
	}

	// Find the name (or id), give precedence to 'name' as browser does
	id = form.elements[idx].name;
	if (!id) {
	    id = form.elements[idx].id;
	}

	// Handle special cases
	if (nodeName == 'INPUT') {
	    if (((type == 'checkbox') || (type == 'radio'))
		    && !(form.elements[idx].checked))  {
		// Skip radio / checkboxes that aren't checked
		continue;
	    } else {
	    }
	    if (value != '') {
		value += '&';
	    }
	    value +=  id + '=' + encodeURIComponent(form.elements[idx].value);
	} else if (nodeName == 'SELECT') {
	    if (!form.elements[idx].value) {
		// No values selected, skip
		continue;
	    }
	    if (form.elements[idx].multiple) {
		var options = form.elements[idx].options;
		for (var cnt = 0; cnt < options.length; cnt++) {
		    if (options[cnt].selected) {
			if (value != '') {
			    value += '&';
			}
			value +=  id + '=' + encodeURIComponent(options[cnt].value);
		    }
		}
	    }
	} else {
	    // "TEXTAREA" and unknown stuff comes through here
	    if (value != '') {
		value += '&';
	    }
	    value +=  id + '=' + encodeURIComponent(form.elements[idx].value);
	}
    }

    // Return the urlencoded String
    return value;
}

function setCookieValue(cookieName, val) {
    document.cookie = cookieName + "=" + val;
}

function getCookieValue(cookieName) {
    docCookie = document.cookie;
    pos= docCookie.indexOf(cookieName+"=");
    if (pos == -1) {
	return null;
    }
    start= pos+cookieName.length+1;
    end= docCookie.indexOf(";", start );
    if ( end == -1 ) {
	end= docCookie.length;
    }
    return docCookie.substring(start, end);
}

function printThis(obj) {
    var content = '';
    for (var idx in obj) {
	try {
	    content += idx + '=(' + obj[idx] + ');  ';
	} catch (ex) {
	    content += idx + '=[NA];  ';
	}
    }
    return content;
}
