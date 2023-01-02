package ru.practicum.explorewithme.util.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDate {
    String message() default "Event date and time has to be at least 2 hours after now";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
