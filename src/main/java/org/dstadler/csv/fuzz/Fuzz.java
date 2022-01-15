package org.dstadler.csv.fuzz;

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
			FuzzBase.checkCSV(inputData, format.getFormat());
		}

		FuzzBase.checkCSV(inputData, CSVFormat.Builder.create().build());
	}
}
