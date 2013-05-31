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

import dk.nsi.sdm4.bemyndigelse.config.BemyndigelseApplicationConfig;
import dk.nsi.sdm4.bemyndigelse.config.BemyndigelseInfrastructureConfig;
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelse;
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelser;
import dk.nsi.sdm4.bemyndigelse.recordspecs.BemyndigelseRecordSpecs;
import dk.nsi.sdm4.core.parser.ParserException;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordFetcher;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import dk.nsi.sdm4.core.persistence.recordpersister.UpdateExistingRecordPersister;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {BemyndigelseApplicationConfig.class, TestDbConfiguration.class, BemyndigelseParserTest.TestConfiguration.class})
public class BemyndigelseParserTest {

    @Configuration
    static public class TestConfiguration {
        @Bean
        public RecordPersister recordPersister() {
            //return new RecordPersister(Instant.now());
            return new UpdateExistingRecordPersister(Instant.now());
        }

        @Bean
        public RecordFetcher recordFetcher() {
            return new RecordFetcher(Instant.now());
        }

    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BemyndigelseParser parser;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parseXML() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Bemyndigelser.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Bemyndigelser bemyndigelser = (Bemyndigelser) jaxbUnmarshaller.unmarshal(file);

            assertEquals("v00001", bemyndigelser.getVersion());
            assertEquals("075052201", bemyndigelser.getTimestamp());

        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Test
    public void testParseFile() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));

        parser.process(file, "");
        
        assertEquals("2 bemyndigelser expected",2, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

    @Test
    public void testParseFiles() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/"));

        parser.process(file, "");

        assertEquals("3 bemyndigelser expected",3, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }



    @Test
    public void testNSPSupport150() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/nspsupport150/20121130_140213497_v00001.bemyndigelse.xml"));

        parser.process(file, "");
        
        assertEquals("10 bemyndigelser expected",10, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
        
        List<Bemyndigelse> bemyndigelser = jdbcTemplate.query("select * from Bemyndigelse where kode = 'eca4d648-a7d6-41b7-bc8e-0ad01c42c780'", new RowMapper<Bemyndigelse>() {

			@Override
			public Bemyndigelse mapRow(ResultSet rs, int rowNumber)
					throws SQLException {
				
				Bemyndigelse b = new Bemyndigelse();
				
				b.setKode(rs.getString("kode"));
				b.setBemyndigedeCPR(rs.getString("bemyndigede_cpr"));
				
				return b;
			}});
        
        assertEquals(1, bemyndigelser.size());
        assertEquals("eca4d648-a7d6-41b7-bc8e-0ad01c42c780", bemyndigelser.get(0).getKode());
        assertEquals("1003244314", bemyndigelser.get(0).getBemyndigedeCPR());
    }

    @Test
    @Rollback(value = false)
    public void testNSP1674() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/nsp1674/20120329_102310000_v1.bemyndigelse.xml"));

        parser.process(file, "");
        assertEquals("2 bemyndigelser expected",2, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));

        File update = FileUtils.toFile(getClass().getClassLoader().getResource("data/nsp1674/20120329_102310000_opdatering_v1.bemyndigelse.xml"));
        parser.process(update, "");
        assertEquals(3, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

    @Test
    public void testParseDataWithInvalidKode() {
    	
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/invalidKode/"));
    	try {
            parser.process(file, "");
    	} catch(ParserException e) {
    		NullPointerException ne = (NullPointerException)e.getCause();
    		assertEquals("Bemyndigelse.kode cannot be null", ne.getMessage());
    	}
    }

    @Test
    public void testParseDataWithInvalidArbejdsfunktion() {
    	
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/invalidArbejdsfunktion/"));
    	try {
            parser.process(file, "");
    	} catch(ParserException e) {
    		NullPointerException ne = (NullPointerException)e.getCause();
    		assertEquals("Bemyndigelse.arbejdsfunktion cannot be null where kode = 1", ne.getMessage());
    	}
    }
}
