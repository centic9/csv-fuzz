package org.dstadler.csv.fuzz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.output.NullAppendable;

public class FuzzBase {
	public static void checkCSV(byte[] inputData, CSVFormat format) {
		try (InputStream stream = new ByteArrayInputStream(inputData);
				Reader in = new BufferedReader(new InputStreamReader(stream), 100*1024)) {
			Iterable<CSVRecord> records = format.parse(in);

			Iterator<CSVRecord> it = records.iterator();
			//noinspection WhileLoopReplaceableByForEach
			while (it.hasNext()) {
				CSVRecord record = it.next();
				//noinspection ResultOfMethodCallIgnored
				record.getComment();
				//noinspection ResultOfMethodCallIgnored
				record.toString();
				record.toList();
			}

			try (CSVPrinter printer = new CSVPrinter(NullAppendable.INSTANCE, format)) {
				printer.printRecords(records);
			}
		} catch (IOException | UncheckedIOException | IllegalStateException | NegativeArraySizeException | IllegalArgumentException e) {
			// expected here
		}
	}
}
