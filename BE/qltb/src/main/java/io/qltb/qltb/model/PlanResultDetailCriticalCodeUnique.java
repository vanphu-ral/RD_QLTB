package io.qltb.qltb.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import io.qltb.qltb.service.PlanResultDetailService;
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
 * Validate that the criticalCode value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = PlanResultDetailCriticalCodeUnique.PlanResultDetailCriticalCodeUniqueValidator.class
)
public @interface PlanResultDetailCriticalCodeUnique {

    String message() default "{Exists.planResultDetail.criticalCode}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PlanResultDetailCriticalCodeUniqueValidator implements ConstraintValidator<PlanResultDetailCriticalCodeUnique, String> {

        private final PlanResultDetailService planResultDetailService;
        private final HttpServletRequest request;

        public PlanResultDetailCriticalCodeUniqueValidator(
                final PlanResultDetailService planResultDetailService,
                final HttpServletRequest request) {
            this.planResultDetailService = planResultDetailService;
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
            if (currentId != null && value.equalsIgnoreCase(planResultDetailService.get(Long.parseLong(currentId)).getCriticalCode())) {
                // value hasn't changed
                return true;
            }
            return !planResultDetailService.criticalCodeExists(value);
        }

    }

}
