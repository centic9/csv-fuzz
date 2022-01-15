package org.dstadler.csv.fuzz;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;

class FuzzComplexTest {
	@Test
	public void test() {
		FuzzedDataProvider provider = mock(FuzzedDataProvider.class);

		FuzzComplex.fuzzerTestOneInput(provider);

		when(provider.consumeRemainingAsBytes()).thenReturn("abc".getBytes(StandardCharsets.UTF_8));

		FuzzComplex.fuzzerTestOneInput(provider);
	}
}