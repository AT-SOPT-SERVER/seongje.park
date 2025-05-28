package org.sopt.validator;

import org.sopt.util.StringControlUtil;
import org.sopt.validator.annotation.ValidTitle;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TitleLengthValidator implements ConstraintValidator<ValidTitle, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (value == null || value.isBlank()) {
			return false;
		}
		return StringControlUtil.countCharacters(value) <= 30;
	}
}
