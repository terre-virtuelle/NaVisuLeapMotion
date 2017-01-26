/*
 * This file is a part of NaVisuLeapMotion
 * Copyright (C) 2017 Di Falco Nicola
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * NaVisu
 *
 * @param <T>
 * @date 26 oct. 2015
 * @author Serge Morvan
 */
public class ImportExportXML<T> {

    public static <T> T exports(T data, File file) throws JAXBException, FileNotFoundException {
        if (file != null) {
            FileOutputStream outputFile;
            outputFile = new FileOutputStream(file);
            JAXBContext jAXBContext;
            Marshaller marshaller;
            jAXBContext = JAXBContext.newInstance(data.getClass());
            marshaller = jAXBContext.createMarshaller();
            marshaller.marshal(data, outputFile);
        }
        return data;
    }

    public static <T> T exports(T data, String filename) throws JAXBException, FileNotFoundException {
        FileOutputStream outputFile;
        outputFile = new FileOutputStream(new File(filename));
        JAXBContext jAXBContext;
        Marshaller marshaller;
        jAXBContext = JAXBContext.newInstance(data.getClass());
        marshaller = jAXBContext.createMarshaller();
        marshaller.marshal(data, outputFile);
        return data;
    }

    public static <T> T exports(T data, StringWriter xmlString) throws JAXBException {
        JAXBContext jAXBContext;
        Marshaller marshaller;
        jAXBContext = JAXBContext.newInstance(data.getClass());
        marshaller = jAXBContext.createMarshaller();
        marshaller.marshal(data, xmlString);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static <T> T imports(T data, File file) throws FileNotFoundException, JAXBException {
        if (file != null) {
            FileInputStream inputFile = new FileInputStream(new File(file.getPath()));
            Unmarshaller unmarshaller;
            JAXBContext jAXBContext;
            jAXBContext = JAXBContext.newInstance(data.getClass());
            unmarshaller = jAXBContext.createUnmarshaller();
            data = (T) unmarshaller.unmarshal(inputFile);
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static <T> T imports(T data, String filename) throws FileNotFoundException, JAXBException {
        File file = new File(filename);
        FileInputStream inputFile = new FileInputStream(new File(file.getPath()));
        Unmarshaller unmarshaller;
        JAXBContext jAXBContext;
        jAXBContext = JAXBContext.newInstance(data.getClass());
        unmarshaller = jAXBContext.createUnmarshaller();
        data = (T) unmarshaller.unmarshal(inputFile);
        return data;
    }
@SuppressWarnings("unchecked")
    public static <T> T imports(T data, StringReader xmlString) throws JAXBException {
        Unmarshaller unmarshaller;
        JAXBContext jAXBContext;
        jAXBContext = JAXBContext.newInstance(data.getClass());
        unmarshaller = jAXBContext.createUnmarshaller();
        data = (T) unmarshaller.unmarshal(xmlString);
        return data;
    }
}
