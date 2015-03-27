package uk.ac.sanger.mig.aker.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import uk.ac.sanger.mig.aker.domain.requests.LabwareRequest;

/**
 * @author pi1
 * @since March 2015
 */
@Component("labwareRequestValidator")
public class LabwareRequestValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return LabwareRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

	}
}
