package com.example.shana.myweather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2016/12/14.xml文件解析
 */

public class SaxHandler extends DefaultHandler {
    private Map<String,List<String>> city=new HashMap<String,List<String>>();

    String provinceName="";
    String cityName="";
    public  Map<String,List<String>> getCityMap(){
        return city;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if("Province".equals(qName)){
            provinceName=attributes.getValue("name");
            city.put(provinceName,new ArrayList<String>());
        }else if("City".equals(qName)){
            cityName=attributes.getValue("name");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("City".equals(qName)){
            city.get(provinceName).add(cityName);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
