package org.dstadler.csv.fuzz;

import java.io.UncheckedIOException;

import org.apache.commons.csv.CSVFormat;

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
			try {
				FuzzBase.checkCSV(inputData, format.getFormat());
			} catch (Throwable e) {
				throw new IllegalStateException("While using format: " + format, e);
			}
		}

		FuzzBase.checkCSV(inputData, CSVFormat.Builder.create().build());
	}
}
