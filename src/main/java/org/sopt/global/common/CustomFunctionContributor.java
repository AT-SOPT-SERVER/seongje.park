package org.sopt.global.common;

import static org.hibernate.type.StandardBasicTypes.*;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class CustomFunctionContributor implements FunctionContributor {

	private static final String FUNCTION_NAME = "match_against";
	private static final String FUNCTION_PATTERN = "match (?1) against (?2 in boolean mode)";

	@Override
	public void contributeFunctions(final FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry()
			.registerPattern(FUNCTION_NAME, FUNCTION_PATTERN,
				functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(DOUBLE));
	}
}
