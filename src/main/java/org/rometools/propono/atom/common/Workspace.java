/*
* Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  The ASF licenses this file to You
* under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.  For additional information regarding
* copyright in this work, please see the NOTICE file in the top level
* directory of this distribution.
*/ 
package org.rometools.propono.atom.common;

import org.rometools.propono.utils.ProponoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom2.Element;


/**
 * Models an Atom workspace.
 */
public class Workspace {
    private String title = null;
    private String titleType = null; // may be TEXT, HTML, XHTML
    private List collections = new ArrayList(); 
    
    /**
     * Collection MUST have title.
     * @param title    Title for collection
     * @param titleType Content type of title (null for plain text)
     */
    public Workspace(String title, String titleType) {
        this.title = title;
        this.titleType = titleType;
    }
    
    public Workspace(Element elem) throws ProponoException {
        parseWorkspaceElement(elem);
    }
    
    /** Iterate over collections in workspace */
    public List getCollections() {
        return collections;
    }
    
    /** Add new collection to workspace */
    public void addCollection(Collection col) {
        collections.add(col);
    }
    
    /**
     * DefaultWorkspace must have a human readable title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Set title of workspace.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get title type ("text", "html", "xhtml" or MIME content-type)
     */
    public String getTitleType() {
        return titleType;
    }

    /**
     * Set title type ("text", "html", "xhtml" or MIME content-type)
     */
    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }
    
    /**
     * Find collection by title and/or content-type.
     * @param title Title or null to match all titles.
     * @param contentType Content-type or null to match all content-types.
     * @return First Collection that matches title and/or content-type.
     */
    public Collection findCollection(String title, String contentType) {
        for (Iterator it = collections.iterator(); it.hasNext();) {
            Collection col = (Collection) it.next();
            if (title != null && col.accepts(contentType)) {
                return col;
            } else if (col.accepts(contentType)) {
                return col;
            }
        }
        return null;
    }
        
    /** Deserialize a Atom workspace XML element into an object */
    public static Workspace elementToWorkspace(Element element) throws ProponoException {  
        return new Workspace(element);
    }
    
    /**
     * Serialize an AtomService.DefaultWorkspace object into an XML element
     */
    public Element workspaceToElement() {
        Workspace space = this;
        
        Element element = new Element("workspace", AtomService.ATOM_PROTOCOL);
        
        Element titleElem = new Element("title", AtomService.ATOM_FORMAT);
        titleElem.setText(space.getTitle());
        if (space.getTitleType() != null && !space.getTitleType().equals("TEXT")) {
            titleElem.setAttribute("type", space.getTitleType(), AtomService.ATOM_FORMAT);
        }
        element.addContent(titleElem);
        
        Iterator iter = space.getCollections().iterator();
        while (iter.hasNext()) {
            Collection col = (Collection) iter.next();
            element.addContent(col.collectionToElement());
        }
        return element;
    }

    /** Deserialize a Atom workspace XML element into an object */
    protected void parseWorkspaceElement(Element element) throws ProponoException {       
        Element titleElem = element.getChild("title", AtomService.ATOM_FORMAT);
        setTitle(titleElem.getText());
        if (titleElem.getAttribute("type", AtomService.ATOM_FORMAT) != null) {
            setTitleType(titleElem.getAttribute("type", AtomService.ATOM_FORMAT).getValue());
        }       
        List collections = element.getChildren("collection", AtomService.ATOM_PROTOCOL);
        Iterator iter = collections.iterator();
        while (iter.hasNext()) {
            Element e = (Element) iter.next();
            addCollection(new Collection(e));
        }
    }
}
