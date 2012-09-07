package dk.nsi.sdm4.bemyndigelse.config;

import dk.nsi.sdm4.bemyndigelse.parser.BemyndigelseParser;
import dk.nsi.sdm4.core.parser.Parser;
import dk.nsi.sdm4.core.persistence.recordpersister.RecordPersister;
import org.joda.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BemyndigelseApplicationConfig {
	@Bean
	public Parser parser() {
		return new BemyndigelseParser();
	}
}
