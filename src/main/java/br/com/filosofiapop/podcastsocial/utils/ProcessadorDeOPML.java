/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import br.com.filosofiapop.podcastsocial.dominio.Podcast;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.EntityResolver;


/**
 *
 * @author murilo
 */
public class ProcessadorDeOPML {
    private static SAXParserFactory factory = null;
    
    public static List<Podcast> getPodcastsFromOPML(File file) throws DocumentException {
        List<Podcast> podcasts = new ArrayList<>();
        
        /*SAXReader reader = new SAXReader();
        reader.setValidation(false);
        reader.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                        return new InputSource(new StringReader(""));
                }
            });
        
        Document document = reader.read(file);
        
        
        
        List list = document.selectNodes("//outline/@xmlUrl");
        
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();
            Podcast podcast = getPodcastFromFeed(url);
            if (podcast != null) {
                podcasts.add(podcast);
            }
            
        }*/
        if(factory == null) {
            factory = SAXParserFactory.newInstance();
        }
        SAXParser saxParser;
        
        OPMLHandler handler = new OPMLHandler();

		try {
			saxParser = factory.newSAXParser();

			// Passo 2: comanda o início do parsing
			saxParser.parse(file, handler); // o "this" indica que a própria
								// classe "DevmediaSAX" atuará como
								// gerenciadora de eventos SAX.

			// Passo 3: tratamento de exceções.
		} catch (ParserConfigurationException | SAXException | IOException e) {
			StringBuffer msg = new StringBuffer();
			msg.append("Erro:\n");
			msg.append(e.getMessage() + "\n");
			msg.append(e.toString());
			System.out.println(msg);
		}
                
        List<String> urls = handler.getUrls();
        
        for(String url: urls) {
            Podcast podcast = getPodcastFromFeed(url);
            if(podcast != null) {
                podcasts.add(podcast);
            }
        }
        
        
        
        return podcasts;
    }
    
    private static Podcast getPodcastFromFeed(String url) throws DocumentException {
        if(factory == null) {
            factory = SAXParserFactory.newInstance();
        }
        SAXParser saxParser;
        PodcastRssHandler rssHandler = new PodcastRssHandler();
            try {
			saxParser = factory.newSAXParser();
			// Passo 2: comanda o início do parsing
                        
			saxParser.parse(url, rssHandler); // o "this" indica que a própria
								// classe "DevmediaSAX" atuará como
								// gerenciadora de eventos SAX.

			// Passo 3: tratamento de exceções.
		} catch (ParserConfigurationException | SAXException | IOException e) {
			StringBuffer msg = new StringBuffer();
			msg.append("Erro:\n");
			msg.append(e.getMessage() + "\n");
			msg.append(e.toString());
			System.out.println(msg);
		}
            
            Podcast podcast = rssHandler.getPodcast();
            podcast.setFeedUrl(url);
            
            if((podcast.getNome() == null || podcast.getNome().isEmpty())
                    && (podcast.getDescricao() == null  || podcast.getDescricao().isEmpty())
                    && (podcast.getImgUrl() == null || podcast.getImgUrl().isEmpty())) {
                return null;
            }
            return podcast;
    }
    
    public static void main(String args[]) throws DocumentException {
        File file = new File("/home/murilo/Dropbox/podkicker_backup.opml");
        List<Podcast> podcasts = ProcessadorDeOPML.getPodcastsFromOPML(file);
        
        for (Podcast p : podcasts) {
            System.out.println("----------------------------------------");
            System.out.println("Nome: " + p.getNome());
            System.out.println("--- Descricao: " + p.getDescricao());
            System.out.println("--- ImgUrl: " + p.getImgUrl());
            System.out.println("--- Feed: " + p.getFeedUrl());
            
        }
        
        System.out.println("Número de podcasts: " + podcasts.size());
    }
}
