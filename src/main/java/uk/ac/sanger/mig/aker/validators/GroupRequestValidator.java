package uk.ac.sanger.mig.aker.validators;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import uk.ac.sanger.mig.aker.domain.requests.GroupRequest;
import uk.ac.sanger.mig.aker.repositories.SampleRepository;
import uk.ac.sanger.mig.aker.utils.SampleHelper;

/**
 * @author pi1
 * @since March 2015
 */
@Component("groupRequestValidator")
public class GroupRequestValidator implements Validator {

	@Autowired
	private SampleRepository sampleRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return GroupRequest.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "name", "name.empty", "Name must be entered");
		ValidationUtils.rejectIfEmpty(errors, "samples", "samples.empty", "At least one sample must be selected");

		GroupRequest groupRequest = (GroupRequest) obj;

		final Collection<Long> samples = groupRequest.getSamples()
				.stream()
				.map(SampleHelper::idFromBarcode)
				.collect(Collectors.toList());

		if (!samples.isEmpty()) {
			final Integer differentTypes = sampleRepository.countDifferentTypes(samples);
			if (differentTypes != 1) {
				errors.rejectValue("samples", "samples.mismatch", "Samples should be of a single type");
			}
		}
	}
}
