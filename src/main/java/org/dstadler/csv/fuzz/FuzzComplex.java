package org.dstadler.csv.fuzz;

import org.apache.commons.csv.CSVFormat;
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
		FuzzBase.checkCSV(inputData, format);
	}
}
