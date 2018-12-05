package de.hawhh.gewiss.get.core.util;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.postgis.PGgeometry;

/**
 * Helper class for handling the conversion of different geometry formats.
 *
 * @author Thomas Preisler
 */
public class GeometryHelper {

    /**
     * Converts the given {@link PGgeometry} (geometry format of the postgis db) to a JTS {@link Polygon} by parsing its 'well known text' (WKT) representation.
     *
     * @param pgGeom the given {@link PGgeometry}
     * @return the converted {@link Polygon}
     * @throws ParseException if the WKT representation couldn't been parsed
     */
    public static Polygon convertToJTSPolygon(PGgeometry pgGeom) throws ParseException {
        return (Polygon) convertMultiPolygonToPolygon(convertToJTSGeometry(pgGeom));
    }

    /**
     * Converts the given WKT String represenation of a JTS {@link Polygon}.
     *
     * @param geomWkt the WKT String
     * @return the converted {@link Polygon}
     * @throws ParseException if the WKT representation couldn't been parsed
     */
    public static Polygon convertToJTSPolygon(String geomWkt) throws ParseException {
        WKTReader wktReader = new WKTReader();
        Geometry geom = wktReader.read(geomWkt);

        return (Polygon) geom;
    }

    /**
     * Converts the given {@link PGgeometry} (geometry format of the postgis db) to the {@link Geometry} used by the simulator by parsing its 'well known text' (WKT)
     * representation
     *
     * @param pgGeom the given {@link PGgeometry}
     * @return the converted {@link Geometry}
     * @throws ParseException if the WKT representation couldn't been parsed
     */
    public static Geometry convertToJTSGeometry(PGgeometry pgGeom) throws ParseException {
        StringBuffer sb = new StringBuffer();
        pgGeom.getGeometry().outerWKT(sb);
        WKTReader wktReader = new WKTReader();
        Geometry geometry = wktReader.read(sb.toString());

        return geometry;
    }

    /**
     * Converts the given {@link Geometry} if its an instance of the {@link MultiPolygon} subclass to the {@link Polygon} subclass.
     *
     * @param geom the given {@link Geometry}
     * @return the converted {@link Geometry}
     */
    public static Geometry convertMultiPolygonToPolygon(Geometry geom) {
        if (geom instanceof MultiPolygon) {
            MultiPolygon mp = (MultiPolygon) geom;
            geom = mp.getGeometryN(0);
        }

        return geom;
    }

}
