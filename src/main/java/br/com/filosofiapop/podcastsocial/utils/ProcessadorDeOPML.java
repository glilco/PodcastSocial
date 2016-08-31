/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import br.com.filosofiapop.podcastsocial.dominio.Podcast;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author murilo
 */
public class ProcessadorDeOPML {
 
    
    public static List<Podcast> getPodcastsFromOPML(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        
        List<Podcast> podcasts = new ArrayList<>();
        
        List list = document.selectNodes( "//outline/@xmlUrl" );

        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();
            Podcast podcast = getPodcastFromFeed(url);
            if(podcast != null) {
                podcasts.add(podcast);
            }
            
        }
        
        return podcasts;
    }
    
    private static Podcast getPodcastFromFeed(String url) throws DocumentException {
        SAXReader reader = new SAXReader();
        try {Document document = reader.read(url);
        
        Node channel = document.selectSingleNode("//channel" );

        Podcast podcast = new Podcast();
        
        Node nome = channel.selectSingleNode("//title");
        podcast.setNome(nome.getText());
        podcast.setFeedUrl(url);
        
        Node imgUrl = channel.selectSingleNode("//image/url");
        podcast.setImgUrl(imgUrl.getText());
        
        Node desc = channel.selectSingleNode("//description");
        podcast.setDescricao(desc.getText());
        
        return podcast;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } 
        /*for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Attribute attribute = (Attribute) iter.next();
            String url = attribute.getValue();*/
            
            
            
    }
    
    public static void main(String args[]) throws DocumentException{
        File file = new File("/home/murilo/Dropbox/podkicker_backup.opml");
        List<Podcast> podcasts = ProcessadorDeOPML.getPodcastsFromOPML(file);
        
        for(Podcast p : podcasts) {
            System.out.println("Nome: " + p.getNome());
            System.out.println("--- Descricao: " + p.getDescricao());
            System.out.println("--- ImgUrl: " + p.getImgUrl());
            System.out.println("--- Feed: " + p.getFeedUrl());
            
        }
        
        System.out.println("Número de podcasts: " + podcasts.size());
    }
}
