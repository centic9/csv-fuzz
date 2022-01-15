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
import org.apache.commons.csv.QuoteMode;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

/**
 * This class provides a simple target for fuzzing Apache Commons CSV with Jazzer.
 *
 * It uses the fuzzed input data to try to parse CSV files.
 *
 * It catches all exceptions that are currently expected.
 */
public class FuzzComplex {
	public static void fuzzerTestOneInput(FuzzedDataProvider data) {
		CSVFormat.Builder builder = CSVFormat.Builder.create();

		final CSVFormat format;
		try {
			builder.setAllowDuplicateHeaderNames(data.consumeBoolean());
			builder.setAllowMissingColumnNames(data.consumeBoolean());
			builder.setAutoFlush(data.consumeBoolean());
			builder.setCommentMarker(data.consumeChar());
			builder.setDelimiter(data.consumeChar());
			if (data.consumeBoolean()) {
				builder.setDelimiter(data.consumeString(10));
			}
			builder.setEscape(data.consumeChar());
			if (data.consumeBoolean()) {
				builder.setHeader(data.consumeString(10));
				builder.setHeaderComments(data.consumeString(10));
			}
			builder.setIgnoreEmptyLines(data.consumeBoolean());
			builder.setIgnoreHeaderCase(data.consumeBoolean());
			builder.setIgnoreSurroundingSpaces(data.consumeBoolean());
			if (data.consumeBoolean()) {
				builder.setNullString(data.consumeString(10));
			}
			if (data.consumeBoolean()) {
				builder.setQuote(data.consumeChar());
				int index = data.consumeInt(0, QuoteMode.values().length) - 1;

				// jazzer sometimes provides -1 here !?
				if (index >= 0) {
					builder.setQuoteMode(QuoteMode.values()[index]);
				}
			}
			builder.setRecordSeparator(data.consumeChar());
			builder.setSkipHeaderRecord(data.consumeBoolean());
			builder.setTrailingDelimiter(data.consumeBoolean());
			builder.setTrim(data.consumeBoolean());

			format = builder.build();
		} catch (IllegalArgumentException e) {
			// input does not produce a valid format, so we cannot continue
			return;
		}

		byte[] inputData = data.consumeRemainingAsBytes();
		checkCSV(inputData, format);
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
		} catch (IOException | IllegalStateException | NegativeArraySizeException | IllegalArgumentException e) {
			// expected here
		}
	}
}
