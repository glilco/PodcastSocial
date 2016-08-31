/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author murilo
 */
public class OPMLHandler extends DefaultHandler {

    private List<String> urls = new ArrayList();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("outline")) {
            urls.add(attributes.getValue("xmlUrl"));
        }
    }
    
    public List<String> getUrls() {
        return urls;
    }

}
