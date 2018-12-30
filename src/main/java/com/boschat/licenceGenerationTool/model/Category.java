package com.boschat.licenceGenerationTool.model;

public enum Category {
    JUNIOR("Junior"),
    SENIOR("Sénior");

    private String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
