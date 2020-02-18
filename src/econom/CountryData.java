/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package econom;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kmt
 */
@Entity
@Table(name = "COUNTRY_DATA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CountryData.findAll", query = "SELECT c FROM CountryData c")
    , @NamedQuery(name = "CountryData.findById", query = "SELECT c FROM CountryData c WHERE c.id = :id")
    , @NamedQuery(name = "CountryData.findByDataYear", query = "SELECT c FROM CountryData c WHERE c.dataYear = :dataYear")
    , @NamedQuery(name = "CountryData.findByValue", query = "SELECT c FROM CountryData c WHERE c.value = :value")})
public class CountryData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "DATA_YEAR")
    private String dataYear;
    @Basic(optional = false)
    @Column(name = "VALUE")
    private String value;
    @JoinColumn(name = "DATASET", referencedColumnName = "DATASET_ID")
    @ManyToOne
    private CountryDataset dataset;

    public CountryData() {
    }

    public CountryData(Integer id) {
        this.id = id;
    }

    public CountryData(Integer id, String dataYear, String value) {
        this.id = id;
        this.dataYear = dataYear;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataYear() {
        return dataYear;
    }

    public void setDataYear(String dataYear) {
        this.dataYear = dataYear;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CountryDataset getDataset() {
        return dataset;
    }

    public void setDataset(CountryDataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CountryData)) {
            return false;
        }
        CountryData other = (CountryData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "econom.CountryData[ id=" + id + " ]";
    }
    
}
