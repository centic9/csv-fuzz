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
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.output.NullAppendable;

public class FuzzBase {
	@SuppressWarnings({ "ResultOfMethodCallIgnored", "EqualsBetweenInconvertibleTypes", "EqualsWithItself" })
	public static void checkCSV(byte[] inputData, CSVFormat format) {
		// trigger some methods of the format which should never throw an exception
		format.hashCode();
		format.toString();
		format.equals(null);
		format.equals("bla");
		format.equals(format);
		format.equals(CSVFormat.DEFAULT);

		try (InputStream stream = new ByteArrayInputStream(inputData);
				Reader in = new BufferedReader(new InputStreamReader(stream), 100*1024)) {
			try (CSVParser records = format.parse(in)) {

				Iterator<CSVRecord> it = records.iterator();
				//noinspection WhileLoopReplaceableByForEach
				while (it.hasNext()) {
					CSVRecord record = it.next();
					record.getComment();
					record.toString();
					record.toList();
					record.values();
					record.getRecordNumber();
					record.getParser();
					record.getCharacterPosition();
					record.size();
					try {
						record.get("head");
					} catch (IllegalStateException | IllegalArgumentException e) {
						// expected here
					}
					try {
						record.get(0);
					} catch (ArrayIndexOutOfBoundsException e) {
						// expected here
					}

					records.getFirstEndOfLine();
					records.getCurrentLineNumber();
					records.getRecordNumber();
				}

				records.stream();
				records.hasHeaderComment();
				records.hasTrailerComment();
				records.getHeaderComment();
				records.getTrailerComment();
				records.getRecords();
				records.getHeaderNames();
				records.getHeaderMap();

				try (CSVPrinter printer = format.print(NullAppendable.INSTANCE)) {
					printer.printRecords(records);
				}
			}
		} catch (IOException | UncheckedIOException | IllegalStateException | NegativeArraySizeException | IllegalArgumentException e) {
			// expected here
		}
	}
}
