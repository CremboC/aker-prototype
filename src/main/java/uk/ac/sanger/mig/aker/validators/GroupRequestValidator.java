package uk.ac.sanger.mig.aker.validators;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.services.SampleService;

/**
 * @author pi1
 * @since March 2015
 */
@Component("groupRequestValidator")
public class GroupRequestValidator implements Validator {

	@Autowired
	private SampleService sampleService;

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "Name must be entered");
		ValidationUtils.rejectIfEmpty(errors, "samples", "samples.empty", "At least one sample must be selected");

		GroupRequest groupRequest = (GroupRequest) obj;

		final Collection<String> samples = groupRequest.getSamples();

		if (!samples.isEmpty()) {
			final Integer differentTypes = sampleService.getRepository().countDifferentTypes(samples);
			if (differentTypes != 1) {
				errors.rejectValue("samples", "samples.mismatch", "Samples should be of a single type");
			}
		}
	}
}
