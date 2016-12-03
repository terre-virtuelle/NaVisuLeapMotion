/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2016 Di Falco Nicola
 *
 * NaVisuLeapMotion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NaVisuLeapMotion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bzh.terrevirtuelle.navisuleapmotion.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author JP M
 */
public class ParserXML {

    Map<String, List<List<String>>> entitiesMap;
    private String xmlFile;

    public ParserXML() {
    }

    public ParserXML(String xmlFile) {
        entitiesMap = new HashMap<>();
        entitiesMap.put("bcncar", new ArrayList<List<String>>());
        entitiesMap.put("bcnisd", new ArrayList<List<String>>());
        entitiesMap.put("bcnlat", new ArrayList<List<String>>());
        entitiesMap.put("bcnsaw", new ArrayList<List<String>>());
        entitiesMap.put("bcnspp", new ArrayList<List<String>>());
        entitiesMap.put("buoycar", new ArrayList<List<String>>());
        entitiesMap.put("buoyinb", new ArrayList<List<String>>());
        entitiesMap.put("buoyisd", new ArrayList<List<String>>());
        entitiesMap.put("buoylat", new ArrayList<List<String>>());
        entitiesMap.put("buoysaw", new ArrayList<List<String>>());
        entitiesMap.put("buoyssp", new ArrayList<List<String>>());
        entitiesMap.put("daymark", new ArrayList<List<String>>());
        entitiesMap.put("lndmrk", new ArrayList<List<String>>());
        entitiesMap.put("ship", new ArrayList<List<String>>());
        entitiesMap.put("highway", new ArrayList<List<String>>());
        entitiesMap.put("morfac", new ArrayList<List<String>>());
        this.xmlFile = xmlFile;
    }

    public List<String> findInnerTagText(String tag, String str2SearchIn){
        List<String> tagValues = new ArrayList<>();
        Matcher matcher = Pattern.compile("<"+tag+">(.+?)</"+tag+">").matcher(str2SearchIn);
        while (matcher.find()) {
            //System.out.println("Pattern<"+tag+"/>: "+matcher.group(1));
            tagValues.add(matcher.group(1) );
        }
        return tagValues;
    }
    
    public Map<String, List<List<String>>> process() {

        for (String type : entitiesMap.keySet()) {
            
            //System.out.println("TYPE =  "+type);
            List<String> tagValues = findInnerTagText(type, xmlFile);
            entitiesMap.get(type).add(tagValues);
        }
        return entitiesMap;
    }

    private double getLat(String xmlContainer) {
        double lat = 0.0;
        List<String> tagValue = findInnerTagText("lat",xmlContainer);
        //System.out.println("---lat= "+tagValue.get(0));
        lat = Double.valueOf(tagValue.get(0));
        return lat;
    }

    private double getLon(String xmlContainer) {
        double lon = 0.0;
        List<String> tagValue = findInnerTagText("lon",xmlContainer);
        //System.out.println("---lon= "+tagValue.get(0));
        lon = Double.valueOf(tagValue.get(0));
        return lon;
    }

    private String getImageAddress(String xmlContainer) {
        String imageAddress = "";
        List<String> tagValue = findInnerTagText("imageAddress",xmlContainer);
        if (tagValue.size()>0){
            //System.out.println("---imageAddress= "+tagValue.get(0));
            imageAddress = tagValue.get(0);
            return imageAddress;
        }
        return "no";
    }
    
      private String getName(String xmlContainer) {
        String name = "";
        List<String> tagValue = findInnerTagText("objectName",xmlContainer);
        if (tagValue.size()>0){
            //System.out.println("---name= "+tagValue.get(0));
            name = tagValue.get(0);
            return name;
        }
          if (name.equals("")){
             tagValue = findInnerTagText("name",xmlContainer);
              if (tagValue.size()>0){
                  name = tagValue.get(0);
                  return name;
              }
          }
        return null;
    }

    /*<lat>0.0</lat>
    <lon>0.0</lon>
    <imageAddress>img/buoyage_1/BOYLAT_4_1_3_0_5_1.png</imageAddress>
     */
    /**
     * Get the value of xmlFile
     *
     * @return the value of xmlFile
     */
    public String getXmlFile() {
        return xmlFile;
    }

    /**
     * Set the value of xmlFile
     *
     * @param xmlFile new value of xmlFile
     */
    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public List<ARgeoData> getARgeoDatas() {
        List<ARgeoData> argDatas = new ArrayList<>();

        for (String type : entitiesMap.keySet()) {
            //System.out.println("***getARgeoDatas(type= "+type+" )");
            for (List<String> listE : entitiesMap.get(type)) {
                //System.out.println("***getARgeoDatas(listE.size= "+listE.size()+" )");
                int cpt=1;
                for (String entity : listE) {
                    //System.out.println("***getARgeoDatas(listE.get["+cpt+"]= "+entity+" )");
                    if (entity!=null && listE.size()>0){
                        double lat = getLat(entity);
                        double lon = getLon(entity);
                        String imageAddress = getImageAddress(entity);
                        String name = getName(entity);
                        ARgeoData argeoData = new ARgeoData(lat, lon, imageAddress,name, type);
                        argDatas.add(argeoData);
                        cpt++;
                    }
                }
            }
        }

        return argDatas;
    }

}
