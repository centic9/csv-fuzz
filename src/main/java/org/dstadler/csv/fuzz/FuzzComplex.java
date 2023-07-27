package org.dstadler.csv.fuzz;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.output.NullAppendable;

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
			builder.setDuplicateHeaderMode(
					DuplicateHeaderMode.values()[data.consumeInt(0, DuplicateHeaderMode.values().length-1)]);
			builder.setAllowMissingColumnNames(data.consumeBoolean());
			builder.setAutoFlush(data.consumeBoolean());
			builder.setCommentMarker(data.consumeChar());
			builder.setDelimiter(data.consumeChar());
			if (data.consumeBoolean()) {
				builder.setDelimiter(data.consumeString(10));
			}
			if (data.consumeBoolean()) {
				builder.setEscape(data.consumeChar());
			} else {
				builder.setEscape(null);
			}
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
			} else {
				builder.setQuote(null);
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

		// get fuzz-data for printing afterwards before consuming the remaining bytes
		boolean newRecord = data.consumeBoolean();
		String string = data.consumeString(100);

		byte[] inputData = data.consumeRemainingAsBytes();
		FuzzBase.checkCSV(inputData, format);

		try {
			format.print(new StringReader(string), NullAppendable.INSTANCE, newRecord);
		} catch (IOException e) {
			// expected here
		}

		format.format();
		format.format("a");
	}
}
