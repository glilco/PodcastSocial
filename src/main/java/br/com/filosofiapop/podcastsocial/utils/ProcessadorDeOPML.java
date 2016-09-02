/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.filosofiapop.podcastsocial.utils;

import br.com.filosofiapop.podcastsocial.dominio.Podcast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author murilo
 */
public class ProcessadorDeOPML {

    private static SAXParserFactory factory = null;

    public static List<Podcast> getPodcastsFromOPML(File file) throws  MalformedURLException, IOException {
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
        if (factory == null) {
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

        List<URL> urls = handler.getUrls();

        for (URL url : urls) {
            Podcast podcast = getPodcastFromFeed(url);
            if (podcast != null) {
                podcasts.add(podcast);
            }
        }

        return podcasts;
    }

    private static Podcast getPodcastFromFeed(URL url) throws MalformedURLException, IOException {
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
        }

        URL resourceUrl, base, next;
        HttpURLConnection conn;
        String location;
        String urlString;

        resourceUrl = url;
        urlString = url.toString();
        while (true) {

            conn = (HttpURLConnection) resourceUrl.openConnection();

            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
            conn.setRequestProperty("User-Agent", "Mozilla/5.0...");

            try {
                switch (conn.getResponseCode()) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        location = conn.getHeaderField("Location");
                        base = new URL(urlString);
                        next = new URL(base, location);  // Deal with relative URLs
                        urlString = next.toExternalForm();
                        resourceUrl = new URL(urlString);
                        continue;
                }
            } catch(ConnectException | SocketTimeoutException e) {
                e.printStackTrace();
                System.out.println("url: " + resourceUrl.toString());
                return null;
            }

            break;
        }
        InputStream is = null;
        try {
            is = conn.getInputStream();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        
        BufferedInputStream bio = new BufferedInputStream(is);
        
        bio.mark(0);
        byte[] bytesInicio = new byte[5];
        
        do {
            bio.read(bytesInicio, 0, 1);
        } while(bytesInicio[0] != '<');
        
        bio.read(bytesInicio, 1, 4);
        bio.reset();
        
        String inicio = new String(bytesInicio);
        if(inicio != null && (!inicio.equals("<?xml") && !inicio.equals("<rss "))) {
            System.out.println("url: " + urlString + " inicio: " + inicio);
            return null;
        }

        SAXParser saxParser;
        PodcastRssHandler rssHandler = new PodcastRssHandler();
        try {
            saxParser = factory.newSAXParser();
            // Passo 2: comanda o início do parsing
            

            saxParser.parse(bio, rssHandler); // o "this" indica que a própria
            // classe "DevmediaSAX" atuará como
            // gerenciadora de eventos SAX.

            // Passo 3: tratamento de exceções.
        } catch (ParserConfigurationException | SAXException | IOException e) {
            /*StringBuffer msg = new StringBuffer();
            msg.append("Erro:\n");
            msg.append(e.getMessage() + "\n");
            msg.append(e.toString());
            System.out.println(msg);*/
        }

        Podcast podcast = rssHandler.getPodcast();
        podcast.setFeedUrl(url.toString());

        if ((podcast.getNome() == null || podcast.getNome().isEmpty())
                && (podcast.getDescricao() == null || podcast.getDescricao().isEmpty())
                && (podcast.getImgUrl() == null || podcast.getImgUrl().isEmpty())) {
            return null;
        }
        return podcast;
    }

    public static void main(String args[]) throws Exception {
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
