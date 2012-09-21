/**
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark
 * (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dk.nsi.sdm4.bemyndigelse.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="Bemyndigelse", namespace="http://nsi.dk/bemyndigelser/2012/04/")
public class Bemyndigelse {
    
    String kode;
    String bemyndigendeCPR;
    String bemyndigedeCPR;
    String bemyndigedeCVR;
    String system;
    private String arbejdsfunktion;
    String rettighed;
    String status;
    String godkendelsesDato;
    String oprettelsesDato;
    String modificeretDato;
    String gyldigFraDato;
    String gyldigTilDato;
    
    @XmlElement(name="kode")
    public String getKode() {
        return kode;
    }
    public void setKode(String kode) {
        this.kode = kode;
    }
    @XmlElement(name="bemyndigende_cpr")
    public String getBemyndigendeCPR() {
        return bemyndigendeCPR;
    }
    public void setBemyndigendeCPR(String bemyndigendeCPR) {
        this.bemyndigendeCPR = bemyndigendeCPR;
    }
    @XmlElement(name="bemyndigede_cpr")
    public String getBemyndigedeCPR() {
        return bemyndigedeCPR;
    }
    public void setBemyndigedeCPR(String bemyndigedeCPR) {
        this.bemyndigedeCPR = bemyndigedeCPR;
    }
    @XmlElement(name="bemyndigede_cvr")
    public String getBemyndigedeCVR() {
        return bemyndigedeCVR;
    }
    public void setBemyndigedeCVR(String bemyndigedeCVR) {
        this.bemyndigedeCVR = bemyndigedeCVR;
    }
    @XmlElement(name="system")
    public String getSystem() {
        return system;
    }
    public void setSystem(String system) {
        this.system = system;
    }
    @XmlElement(name="rettighed")
    public String getRettighed() {
        return rettighed;
    }
    public void setRettighed(String rettighed) {
        this.rettighed = rettighed;
    }
    @XmlElement(name="godkendelsesdato")
    public String getGodkendelsesDato() {
        return godkendelsesDato;
    }
    public void setGodkendelsesDato(String godkendelsesDato) {
        this.godkendelsesDato = godkendelsesDato;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @XmlElement(name="status")
    public String getStatus() {
        return status;
    }
    @XmlElement(name="CreatedDate")
    public String getOprettelsesDato() {
        return oprettelsesDato;
    }
    public void setOprettelsesDato(String oprettelsesDato) {
        this.oprettelsesDato = oprettelsesDato;
    }
    @XmlElement(name="ModifiedDate")
    public String getModificeretDato() {
        return modificeretDato;
    }
    public void setModificeretDato(String modificeretDato) {
        this.modificeretDato = modificeretDato;
    }
    @XmlElement(name="ValidFrom")
    public String getGyldigFraDato() {
        return gyldigFraDato;
    }
    public void setGyldigFraDato(String gyldigFraDato) {
        this.gyldigFraDato = gyldigFraDato;
    }
    @XmlElement(name="ValidTo")
    public String getGyldigTilDato() {
        return gyldigTilDato;
    }
    public void setGyldigTilDato(String gyldigTilDato) {
        this.gyldigTilDato = gyldigTilDato;
    }
    public void setArbejdsfunktion(String arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }
    @XmlElement(name="arbejdsfunktion")
    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }

}
