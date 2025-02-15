package edu.kingston.agriconnect.model.enums;

public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");

    private String templateName;

    EmailTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
