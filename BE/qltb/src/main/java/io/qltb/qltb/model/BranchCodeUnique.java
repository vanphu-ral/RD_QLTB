package io.qltb.qltb.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.qltb.qltb.service.BranchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;


/**
 * Validate that the code value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = BranchCodeUnique.BranchCodeUniqueValidator.class
)
public @interface BranchCodeUnique {

    String message() default "{Exists.branch.code}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class BranchCodeUniqueValidator implements ConstraintValidator<BranchCodeUnique, String> {

        private final BranchService branchService;
        private final HttpServletRequest request;

        public BranchCodeUniqueValidator(final BranchService branchService,
                final HttpServletRequest request) {
            this.branchService = branchService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(branchService.get(Long.parseLong(currentId)).getCode())) {
                // value hasn't changed
                return true;
            }
            return !branchService.codeExists(value);
        }

    }

}
