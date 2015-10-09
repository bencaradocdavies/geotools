package org.geotools.data.wfs;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataAccess;
import org.geotools.data.DataAccessFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;

public class ComplexFeatureWfsClientExample {

    public static void main(String[] args) throws Exception {
        // 1. Configure:
        String getCapabilitiesURL = "http://geossdi.dmp.wa.gov.au/services/wfs?service=WFS&version=1.1.0&request=GetCapabilities";
        Map<String, Serializable> connectionParameters = new HashMap<String, Serializable>();
        connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilitiesURL);
        connectionParameters.put("WFSDataStoreFactory:TIMEOUT", 120000);
        connectionParameters.put("WFSDataStoreFactory:PROTOCOL", false);
        /** #A: Require a Factory with this level of compliance. **/
        // connectionParameters.put("WFSDataStoreFactory:GML_COMPLIANCE_LEVEL", 1);
        connectionParameters.put("WFSDataStoreFactory:MAXFEATURES", 2);
        /** #B: Specify the location of the folder to be used by schema-resolver. **/
        connectionParameters.put("WFSDataStoreFactory:SCHEMA_CACHE_LOCATION",
                (new File(System.getProperty("java.io.tmpdir"), "schema-cache")).getPath());

        // 2. Find suitable DataAccess:
        /** #C: Notice that these classes are the non-Simple forms. **/
        DataAccess<FeatureType, Feature> dataAccess = DataAccessFinder
                .getDataStore(connectionParameters);

        // 3. Declare the type you're interested in (this could be done by iterating through the typeNames in dataStore.getTypeNames()).
        Name nameToRetrieve = new NameImpl("urn:cgi:xmlns:CGI:GeoSciML:2.0", "Borehole");

        // 4. Get the FeatureSource (WFSContentComplexFeatureSource):
        FeatureSource<FeatureType, Feature> featureSource = dataAccess
                .getFeatureSource(nameToRetrieve);

        // 5. Get the FeatureType (AKA schema):
        FeatureType schema = dataAccess.getSchema(nameToRetrieve);

        // 6. Create Query using the schema.
        Query query = new Query("gsml:Borehole");
        query.setMaxFeatures(2);
        query.setCoordinateSystem(schema.getGeometryDescriptor().getCoordinateReferenceSystem());

        // 7. Get the features and their corresponding types:
        FeatureCollection<FeatureType, Feature> features = featureSource.getFeatures(query);

        // 8. Iterate over the features and display them:
        FeatureIterator<Feature> iterator = features.features();

        try {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                for (Property property : feature.getProperties()) {
                    System.out.println(property);
                }
            }
        } finally {
            iterator.close();
        }
    }

}
