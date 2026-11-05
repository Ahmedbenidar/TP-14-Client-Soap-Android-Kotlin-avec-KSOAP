package ma.ahmedbenidar.tp14.entites;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Force la date au format yyyy-MM-dd dans le XML SOAP.
 * Sans cet adaptateur, JAXB produit un xsd:dateTime avec fuseau horaire
 * que ksoap2 relit difficilement cote Android.
 */
public class AdaptateurDate extends XmlAdapter<String, Date> {

    private static final String FORMAT = "yyyy-MM-dd";

    @Override
    public Date unmarshal(String valeur) throws Exception {
        if (valeur == null || valeur.isBlank()) {
            return null;
        }
        return new SimpleDateFormat(FORMAT).parse(valeur);
    }

    @Override
    public String marshal(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORMAT).format(date);
    }

}
