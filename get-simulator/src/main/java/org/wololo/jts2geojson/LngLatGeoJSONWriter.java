package org.wololo.jts2geojson;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Writer for GeoJSON Files. Extends the {@link GeoJSONWriter} where the coordinates are returned in the wrong format (according to the GeoJSON specification). This
 * writer returns them in the right format: [longitude, latitude, elevation].
 *
 * (The package is due the package-private method that is overwritten to correct the coordinate format
 * {@link GeoJSONWriter#convert(com.vividsolutions.jts.geom.Coordinate)}).
 *
 * @author Thomas Preisler
 */
public class LngLatGeoJSONWriter extends GeoJSONWriter {

    @Override
    protected double[] convert(Coordinate coordinate) {
        if (Double.isNaN(coordinate.z)) {
            return new double[]{coordinate.y, coordinate.x};
        } else {
            return new double[]{coordinate.y, coordinate.x, coordinate.z};
        }
    }
}
