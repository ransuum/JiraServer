package org.parent.jira.security.registrationchooser;

import org.parent.jira.models.dto.request.SignUpRequest;
import org.parent.jira.models.entity.User;
import org.parent.jira.models.enums.Provider;

public sealed interface RegistrationChooser permits EmailRegistration, GoogleRegistration {
    User registration(SignUpRequest signUpRequest);

    Provider getProvider();
}
