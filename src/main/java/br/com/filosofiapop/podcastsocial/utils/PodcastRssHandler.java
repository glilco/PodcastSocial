/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import br.com.filosofiapop.podcastsocial.dominio.Podcast;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author murilo
 */
public class PodcastRssHandler extends DefaultHandler {
    private Podcast podcast = new Podcast();
    private StringBuffer buffer = new StringBuffer();
    //private AtributoPod atributo;
    private boolean gravarElemento = false;

    
    /*public enum AtributoPod {
        NOME,DESC,IMG,URLIMG
    }*/
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String node = qName.toLowerCase();
        buffer = new StringBuffer();
        
        if(node.equals("title") || node.equals("description") || node.equals("url")) {
            gravarElemento = true;
        }
        
        if(node.equals("itunes:image")) {
            podcast.setImgUrl(attributes.getValue("href"));
        }
        
        if(node.equals("item")) {
            throw new SAXException("Todos os dados carregados");
        }
    }
    

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(gravarElemento) {
            buffer.append(ch,start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String node = qName.toLowerCase();
        
        switch(node) {
            case "title":
                if(podcast.getNome() == null || podcast.getNome().equals("")) {
                    podcast.setNome(buffer.toString());
                }
                break;
            case "description":
                if(podcast.getDescricao() == null || podcast.getDescricao().equals("")) {
                    podcast.setDescricao(buffer.toString());
                }
                break;
            case "url":
                podcast.setImgUrl(buffer.toString());
                break;
        }
        
        buffer = new StringBuffer();
        gravarElemento = false;
    }
    
    public Podcast getPodcast() {
        return podcast;
    }
    
}
