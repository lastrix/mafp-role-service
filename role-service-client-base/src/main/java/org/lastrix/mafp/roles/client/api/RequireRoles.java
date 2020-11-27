package org.lastrix.mafp.roles.client.api;

import org.lastrix.jwt.UserType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireRoles {
    String[] value();

    UserType[] userTypes() default {UserType.PERSON};
}
