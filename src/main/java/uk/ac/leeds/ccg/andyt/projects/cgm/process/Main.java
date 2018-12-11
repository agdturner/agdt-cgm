/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.cgm.process;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mil.nga.sf.Geometry;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Point;
import mil.nga.sf.geojson.Position;
//import mil.nga.sf.geojson.Geometry;
import uk.ac.leeds.ccg.andyt.data.format.Generic_ReadCSV;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_IO;
import uk.ac.leeds.ccg.andyt.projects.cgm.core.Strings;
import uk.ac.leeds.ccg.andyt.projects.cgm.io.Files;
import uk.ac.leeds.ccg.andyt.vector.projection.Vector_OSGBtoLatLon;

/**
 *
 * @author geoagdt
 */
public class Main {

    Files files;
    Strings strings;

    File outdir;
    File infile;
    String name;
    File outfile;
    PrintWriter pw;

    public Main() {
    }

    public static void main(String[] args) {
        new Main().run();
    }

    protected void run() {
        try {
            files = new Files();
            files.setDataDirectory(new File(System.getProperty("user.dir"), "data"));
            strings = new Strings();
            outdir = files.getOutputDataDir(strings);

//        // Process pollution
//        outfile = new File(outdir, name + ".geojson");
//        pw = Generic_IO.getPrintWriter(outfile, false);
//        name = "pollution";
//        infile = new File(files.getInputDataDir(strings), name + ".csv");
//        processLoadPollutionData();
            // Process calderdale-public-wifi-spots
            name = "calderdale-public-wifi-spots";
            infile = new File(files.getInputDataDir(strings), name + ".csv");
            outfile = new File(outdir, name + ".geojson");
            pw = Generic_IO.getPrintWriter(outfile, false);
            processLoadCalderdalePublicWifiSpotsData();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void processLoadCalderdalePublicWifiSpotsData() throws Exception {
        // Read the input file into memory.
        ArrayList<String> lines;
        lines = Generic_ReadCSV.read(infile, outdir, 1);
        System.out.println(lines.size());
        // Construct points
        String line;
        String[] split;
        // GeoJSON
        Position position;
        Point point;
        Iterator<String> ite;
        ite = lines.iterator();
        // Header
        String header;
        header = ite.next();
        System.out.println(header);
        String[] fieldnames;
        fieldnames = header.split(",");
        // Initialise feature collection
        FeatureCollection fc = new FeatureCollection();
        Map<String, Object> properties;
        while (ite.hasNext()) {
            line = ite.next();
            System.out.println(line);
            properties = new HashMap();
            //p = new Position(longitude, latitude, null);
            split = line.split("\"");
            if (split.length == 1) {
                split = line.split(",");
            } else {
                if (split.length == 3) {
                    String part1;
                    part1 = split[0].substring(0, split[0].length() - 1);
                    String[] splitPart1;
                    splitPart1 = part1.split(",");
                    String part2;
                    part2 = split[1];
                    String part3;
                    part3 = split[2].substring(1, split[2].length() - 1);
                    String[] splitPart3;
                    splitPart3 = part3.split(",");
                    split = new String[splitPart1.length + 3 + splitPart3.length];
                    System.arraycopy(splitPart1, 0, split, 0, splitPart1.length);
                    split[splitPart1.length] = part2;
                    System.arraycopy(splitPart3, 0, split, splitPart1.length + 1, splitPart3.length);
//For checking
//                    System.out.println(line);
//                    for (int i = 0; i < split.length - 1; i ++) {
//                        System.out.print(split[i] + ",");
//                    }
//                    System.out.print(split[split.length - 1]);

                } else {
                    throw new Exception("Need dev!");
                }
            }
            int n;
            n = fieldnames.length;
            for (int i = 0; i < n; i++) {
                properties.put(fieldnames[i], split[i]);
            }
            // Get latitude and longitude
            position = new Position(
                    Double.valueOf(split[n - 1]),
                    Double.valueOf(split[n - 2]));
            point = new Point(position);
            // Geometry geometry = null;
            String content = FeatureConverter.toStringValue(point);
            System.out.println(content);
            Geometry geometry = point.getGeometry();
            Feature feature = FeatureConverter.toFeature(geometry);
            feature.getFeature().setProperties(properties);
            String featureContent = FeatureConverter.toStringValue(feature);
            System.out.println(featureContent);
            fc.addFeature(feature);
        }
        String featureCollectionContent = FeatureConverter.toStringValue(fc);
        System.out.println(featureCollectionContent);
        pw.println(featureCollectionContent);
        pw.close();
    }

    protected void processLoadPollutionData() {
        // Read the input file into memory.
        ArrayList<String> lines;
        lines = Generic_ReadCSV.read(infile, outdir, 1);
        System.out.println(lines.size());
        // Construct points
        double[] latlon;
        String line;
        String SITE_ID;
        String ADDRESS;
        String SITE_TYPE;
        String DIST_TO_KERB;
        String HEIGHT;
        String DISTANCE_TO_RELEVANT_EXP;
        String ANNUAL_MEAN_2015_BIAS_ADJUSTED;
        String EASTING;
        String NORTHING;
        String[] split;
        // GeoJSON
        Position position;
        Point point;
        Iterator<String> ite;
        ite = lines.iterator();
        // Header
        String header;
        header = ite.next();
        System.out.println(header);
        String[] fieldnames;
        fieldnames = header.split(",");
        // Initialise feature collection
        FeatureCollection fc = new FeatureCollection();
        Map<String, Object> properties;
        while (ite.hasNext()) {
            line = ite.next();
            System.out.println(line);
            properties = new HashMap();
            //p = new Position(longitude, latitude, null);
            split = line.split(",");
            SITE_ID = split[0];
            properties.put(fieldnames[0], SITE_ID);
            SITE_TYPE = split[split.length - 7];
            properties.put(fieldnames[2], SITE_TYPE);
            DIST_TO_KERB = split[split.length - 6];
            properties.put(fieldnames[3], DIST_TO_KERB);
            HEIGHT = split[split.length - 5];
            properties.put(fieldnames[4], HEIGHT);
            DISTANCE_TO_RELEVANT_EXP = split[split.length - 4];
            properties.put(fieldnames[5], DISTANCE_TO_RELEVANT_EXP);
            ANNUAL_MEAN_2015_BIAS_ADJUSTED = split[split.length - 3];
            properties.put(fieldnames[6], ANNUAL_MEAN_2015_BIAS_ADJUSTED);
            EASTING = split[split.length - 2];
            properties.put(fieldnames[7], EASTING);
            NORTHING = split[split.length - 1];
            properties.put(fieldnames[8], NORTHING);
            ADDRESS = "";
            for (int i = 1; i < split.length - 7; i++) {
                if (i > 1 && i < split.length - 6) {
                    ADDRESS += ",";
                }
                ADDRESS += split[i];
            }
            properties.put(fieldnames[2], ADDRESS);
            // Get latitude and longitude
            latlon = Vector_OSGBtoLatLon.osgb2latlon(
                    new Double(EASTING),
                    new Double(NORTHING));
            System.out.println(SITE_ID + "," + ADDRESS + "," + SITE_TYPE + ","
                    + DIST_TO_KERB + "," + HEIGHT + ","
                    + DISTANCE_TO_RELEVANT_EXP + ","
                    + ANNUAL_MEAN_2015_BIAS_ADJUSTED + ","
                    + EASTING + "," + NORTHING + ","
                    + latlon[0] + "," + latlon[1]);
            position = new Position(latlon[1], latlon[0]);
            point = new Point(position);
            // Geometry geometry = null;
            String content = FeatureConverter.toStringValue(point);
            System.out.println(content);
            Geometry geometry = point.getGeometry();
            Feature feature = FeatureConverter.toFeature(geometry);
            feature.getFeature().setProperties(properties);
            String featureContent = FeatureConverter.toStringValue(feature);
            System.out.println(featureContent);
            fc.addFeature(feature);
        }
        String featureCollectionContent = FeatureConverter.toStringValue(fc);
        System.out.println(featureCollectionContent);
        pw.println(featureCollectionContent);
        pw.close();
    }
}
