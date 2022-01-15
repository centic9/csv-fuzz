package org.dstadler.csv.fuzz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

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
	public static void fuzzerTestOneInput(byte[] inputData) {
		for (CSVFormat.Predefined format : CSVFormat.Predefined.values()) {
			checkCSV(inputData, format.getFormat());
		}

		checkCSV(inputData, CSVFormat.Builder.create().build());
	}

	private static void checkCSV(byte[] inputData, CSVFormat format) {
		try (InputStream stream = new ByteArrayInputStream(inputData);
				Reader in = new BufferedReader(new InputStreamReader(stream), 100*1024)) {
			Iterable<CSVRecord> records = format.parse(in);

			Iterator<CSVRecord> it = records.iterator();
			while (it.hasNext()) {
				CSVRecord record = it.next();
				//noinspection ResultOfMethodCallIgnored
				record.getComment();
				//noinspection ResultOfMethodCallIgnored
				record.toString();
				//noinspection ResultOfMethodCallIgnored
				record.toList();
			}
		} catch (IOException | IllegalStateException e) {
			// expected here
		}
	}
}
