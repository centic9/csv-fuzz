package org.dstadler.csv.fuzz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * This class provides a simple target for fuzzing Apache Commons CSV with Jazzer.
 *
 * It uses the fuzzed input data to try to parse CSV files.
 *
 * It catches all exceptions that are currently expected.
 */
public class Fuzz {
	private static final CSVFormat[] FORMATS = new CSVFormat[] {
			CSVFormat.DEFAULT,
			CSVFormat.EXCEL,
			CSVFormat.INFORMIX_UNLOAD,
			CSVFormat.MONGODB_CSV,
			CSVFormat.MONGODB_TSV,
			CSVFormat.MYSQL,
			CSVFormat.ORACLE,
			CSVFormat.POSTGRESQL_CSV,
			CSVFormat.POSTGRESQL_TEXT,
			CSVFormat.RFC4180,
			CSVFormat.TDF,
			CSVFormat.Builder.create().build(),
	};

	public static void fuzzerTestOneInput(byte[] inputData) {
		for (CSVFormat format : FORMATS) {
			try (InputStream stream = new ByteArrayInputStream(inputData);
					Reader in = new BufferedReader(new InputStreamReader(stream), 100*1024)) {
				Iterable<CSVRecord> records = format.parse(in);
				records.iterator();
			} catch (IOException e) {
				// expected here
			}
		}
	}
}
