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
import dk.nsi.sdm4.bemyndigelse.model.Bemyndigelser;
import dk.nsi.sdm4.bemyndigelse.recordspecs.BemyndigelseRecordSpecs;
import dk.nsi.sdm4.testutils.TestDbConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {BemyndigelseApplicationConfig.class, TestDbConfiguration.class})
public class BemyndigelseParserTest {
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

        parser.process(file);
        
        assertEquals("2 bemyndigelser expected",2, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

    @Test
    public void testParseFiles() {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/"));

        parser.process(file);

        assertEquals("3 bemyndigelser expected",3, jdbcTemplate.queryForInt("SELECT Count(*) FROM " + BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC.getTable()));
    }

}
