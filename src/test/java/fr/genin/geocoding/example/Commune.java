package fr.genin.geocoding.example;

import fr.ybonnel.csvengine.annotation.CsvColumn;
import fr.ybonnel.csvengine.annotation.CsvFile;

/**
 * Commune datas.
 */
@CsvFile
public class Commune {

    // The "name" field is mapped to a column in CSV named "name"
    @CsvColumn("nom_commune")
    private String name;

    @CsvColumn("latitude")
    private Double latitude;

    @CsvColumn("longitude")
    private Double longitude;

    @CsvColumn("codes_postaux")
    private String codes_postaux;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCodes_postaux() {
        return codes_postaux;
    }

    public void setCodes_postaux(String codes_postaux) {
        this.codes_postaux = codes_postaux;
    }

    @Override
    public String toString() {
        return name + '/' + codes_postaux;
    }
}
