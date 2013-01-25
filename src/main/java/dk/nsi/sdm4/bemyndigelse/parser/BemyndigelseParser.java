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
package dk.nsi.sdm4.bemyndigelse.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import dk.nsi.sdm4.core.persistence.recordpersister.*;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelse;
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelser;
import dk.nsi.sdm4.bemyndigelse.recordspecs.BemyndigelseRecordSpecs;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import dk.sdsd.nsp.slalog.api.SLALogger;

public class BemyndigelseParser implements Parser {

    private final RecordSpecification recordSpecification;
    private static Logger logger = Logger.getLogger(BemyndigelseParser.class); 

    @Autowired
    private SLALogger slaLogger;
    
    @Autowired
    private RecordPersister persister;

    @Autowired
    private RecordFetcher fetcher;
    
    
    public BemyndigelseParser() {
        this.recordSpecification = BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC;
    }
    
    @Override
    public void process(File dataSet) throws ParserException {

        SLALogItem slaLogItem = slaLogger.createLogItem("BemyndigelseParser", "dataSet");
        try {
            logger.debug("Starting Bemyndigelse parser");
            File files = checkRequiredFiles(dataSet);
            
            List<Bemyndigelser> bemyndigelsesList = unmarshallFile(files);
            for (Bemyndigelser bemyndigelser : bemyndigelsesList) {
                for(Bemyndigelse bemyndigelse: bemyndigelser.getBemyndigelseList()) {
                	validateBemyndigelse(bemyndigelse);
                    Record record = buildRecord(bemyndigelse);

                    updateCurrentRecordIfExists(bemyndigelse.getKode(), recordSpecification);
                    persister.persist(record, recordSpecification);
                }
            }
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("BemyndigelseParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw new ParserException(e);
        } finally {
            logger.debug("Ending Bemyndigelse parser");
        }
        
    }

    private void updateCurrentRecordIfExists(String key, RecordSpecification specification) {
        RecordWithMetadata recordWithMetadata = fetcher.fetchCurrentWithMeta(key, specification);
        if (recordWithMetadata != null) {
            logger.debug("Existing record found for key '"+key+"' - setting ValidTo");
            recordWithMetadata.setValidTo(persister.getTransactionTime());
            persister.update(recordWithMetadata, specification);
        }
    }

    private void validateBemyndigelse(Bemyndigelse bemyndigelse) {
        Preconditions.checkNotNull(bemyndigelse.getKode(), "Bemyndigelse.kode cannot be null");
        Preconditions.checkNotNull(bemyndigelse.getBemyndigedeCPR(), "Bemyndigelse.bemyndigede_cpr cannot be null where kode = " + bemyndigelse.getKode());
        Preconditions.checkNotNull(bemyndigelse.getBemyndigendeCPR(), "Bemyndigelse.bemyndigende_cpr cannot be null where kode = " + bemyndigelse.getKode());
        Preconditions.checkNotNull(bemyndigelse.getSystem(), "Bemyndigelse.system cannot be null where kode = " + bemyndigelse.getKode());
        Preconditions.checkNotNull(bemyndigelse.getArbejdsfunktion(), "Bemyndigelse.arbejdsfunktion cannot be null where kode = " + bemyndigelse.getKode());
        Preconditions.checkNotNull(bemyndigelse.getRettighed(), "Bemyndigelse.rettighed cannot be null where kode = " + bemyndigelse.getKode());
        Preconditions.checkNotNull(bemyndigelse.getStatus(), "Bemyndigelse.status cannot be null where kode = " + bemyndigelse.getKode());
	}

	private Record buildRecord(Bemyndigelse bemyndigelse) {
        RecordBuilder builder = new RecordBuilder(recordSpecification);

        builder.field("kode", bemyndigelse.getKode());
        builder.field("bemyndigende_cpr", bemyndigelse.getBemyndigendeCPR());
        builder.field("bemyndigede_cpr", bemyndigelse.getBemyndigedeCPR());
        builder.field("bemyndigede_cvr", bemyndigelse.getBemyndigedeCVR());
        builder.field("system", bemyndigelse.getSystem());
        builder.field("arbejdsfunktion", bemyndigelse.getArbejdsfunktion());
        builder.field("rettighed", bemyndigelse.getRettighed());
        builder.field("status", bemyndigelse.getStatus());
        builder.field("godkendelses_dato", bemyndigelse.getGodkendelsesDato());
        builder.field("oprettelses_dato", bemyndigelse.getOprettelsesDato());
        builder.field("modificeret_dato", bemyndigelse.getModificeretDato());
        builder.field("gyldig_fra_dato", bemyndigelse.getGyldigFraDato());
        builder.field("gyldig_til_dato", bemyndigelse.getGyldigTilDato());
        
        return builder.build();
    }

    private List<Bemyndigelser> unmarshallFile(File dataSet) {
        List<Bemyndigelser> bemyndigelsesList = new ArrayList<Bemyndigelser>();
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Bemyndigelser.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File[] input = null;
            if(dataSet.isDirectory()) {
                 input = dataSet.listFiles();
            } else {
                input = new File[] {dataSet};
            }

            for (int i = 0; i < input.length; i++) {
                Bemyndigelser bemyndigelser = (Bemyndigelser) jaxbUnmarshaller.unmarshal(input[i]);
                bemyndigelsesList.add(bemyndigelser);
            }
        } catch (JAXBException e) {
            logger.error("", e);
        }
        return bemyndigelsesList;
    }

    private File checkRequiredFiles(File dataSet) {
        
       Preconditions.checkNotNull(dataSet);
       
       File[] input = null;
       if(dataSet.isDirectory()) {
            input = dataSet.listFiles();
       } else {
           input = new File[] {dataSet};
       }

       for (int i = 0; i < input.length; i++) {
           String fileName = input[i].getName();
           MDC.put("filename", fileName);
       }
       
       return dataSet;
    }

    @Override
    public String getHome() {
        return "bemyndigelseimporter";
    }

}
