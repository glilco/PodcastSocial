/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import java.net.MalformedURLException;
import java.net.URL;
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

    private List<URL> urls = new ArrayList();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("outline")) {
            String urlString = attributes.getValue("xmlUrl");
            if(urlString != null && !urlString.isEmpty()) {
                try {
                urls.add(new URL(urlString));
                } catch (MalformedURLException e) {
                    System.err.println("URL Mal Formada: " + urlString);
                }
            }
        }
    }
    
    public List<URL> getUrls() {
        return urls;
    }

}
