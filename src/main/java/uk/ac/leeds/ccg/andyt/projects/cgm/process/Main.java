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
import mil.nga.sf.Geometry;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.FeatureConverter;
import mil.nga.sf.geojson.Point;
import mil.nga.sf.geojson.Position;
//import mil.nga.sf.geojson.Geometry;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_ReadCSV;
import uk.ac.leeds.ccg.andyt.generic.io.Generic_StaticIO;
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

    public Main() {
    }

    public static void main(String[] args) {
        new Main().run();
    }

    protected void run() {
        files = new Files();
        files.setDataDirectory(new File(System.getProperty("user.dir"), "data"));
        strings = new Strings();
        File infile;
        infile = new File(files.getInputDataDir(strings), "pollution.csv");
        File outdir;
        outdir = files.getOutputDataDir(strings);
        File outfile;
        outfile = new File(outdir, "pollution.geojson");
        PrintWriter pw;
        pw = Generic_StaticIO.getPrintWriter(outfile, false);
        // Read the input file into memory.
        ArrayList<String> lines;
        lines = Generic_ReadCSV.read(infile, outdir, 1);
        System.out.println(lines.size());
        // Construct points
        double[] latlon;
        //BigDecimal[] latlon;
        //Double longitude;
        //Double latitude;

        Vector_OSGBtoLatLon projector;
        projector = new Vector_OSGBtoLatLon();
        String line;
        String s_SITE_ID;
        String s_ADDRESS;
        String s_SITE_TYPE;
        String s_DIST_TO_KERB;
        String s_HEIGHT;
        String s_DISTANCE_TO_RELEVANT_EXP;
        String s_ANNUAL_MEAN_2015_BIAS_ADJUSTED;
        String s_EASTING;
        String s_NORTHING;

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

        String header;
        header = ite.next();
        System.out.println(header);
        String[] fieldnames;
        fieldnames = header.split(",");

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
        // Write out result
//        //Geometry geometry = ...
//        Geometry geometry = null;
//        
//
//        
//
//        FeatureCollection featureCollection = FeatureConverter.toFeatureCollection(geometry);
//        String featureCollectionContent = FeatureConverter.toStringValue(featureCollection);
//
//        Map<String, Object> contentMap = FeatureConverter.toMap(geometry);
    }
}
