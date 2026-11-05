package ma.ahmedbenidar.tp14.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Compte bancaire expose au client Android.
 * L ordre des elements JAXB est fige pour que ksoap2 les lise toujours
 * dans le meme sens : id, titulaire, solde, dateCreation, type.
 */
@Entity
@Table(name = "compte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "compte")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "titulaire", "solde", "dateCreation", "type"})
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    @XmlElement(name = "titulaire")
    private String titulaire;

    private double solde;

    @Temporal(TemporalType.DATE)
    @XmlJavaTypeAdapter(AdaptateurDate.class)
    private Date dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeCompte type;

}
