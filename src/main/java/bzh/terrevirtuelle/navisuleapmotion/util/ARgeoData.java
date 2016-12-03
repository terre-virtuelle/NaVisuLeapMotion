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


/**
 *
 * @author JP M
 */
public class ARgeoData {
     
    private double lat;

    private double lon;
    
    private String imageAddress;
    
    private String name;

    private String type;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }

    public ARgeoData(double lat, double lon, String imageAddress) {
        this.lat = lat;
        this.lon = lon;
        this.imageAddress = imageAddress;
    }

    public ARgeoData(double lat, double lon, String imageAddress, String name) {
        this.lat = lat;
        this.lon = lon;
        this.imageAddress = imageAddress;
        this.name = name;
    }

    public ARgeoData(double lat, double lon, String imageAddress, String name, String type) {
        this.lat = lat;
        this.lon = lon;
        this.imageAddress = imageAddress;
        this.name = name;
        this.type = type;
    }

    public ARgeoData() {
    }    
    

    /**
     * Get the value of imageAddress
     *
     * @return the value of imageAddress
     */
    public String getImageAddress() {
        return imageAddress;
    }

    /**
     * Set the value of imageAddress
     *
     * @param imageAddress new value of imageAddress
     */
    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the value of lon
     *
     * @return the value of lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * Set the value of lon
     *
     * @param lon new value of lon
     */
    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Get the value of lat
     *
     * @return the value of lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * Set the value of lat
     *
     * @param lat new value of lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "ARgeoData{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", imageAddress='" + imageAddress + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

