package com.boschat.licenceGenerationTool.model;


public enum TypeLicence {

    COMPETITION(1, "Compétition", "Joueur", "FKBF_CartoucheJoueur.png"),
    REFEREE(2, "Arbitre", "Arbitre", "FKBF_CartoucheArbitre.png"),
    LEISURE(1, "Loisir", "Joueur", "FKBF_CartoucheJoueur.png"),
    COACH(1, "Coach", "Coach", "FKBF_CartoucheCoach.png"),
    ADMINISTRATOR(1, "Dirigeant", "Dirigeant", "FKBF_CartoucheDirigeant.png");

    private Integer id;
    private String name;
    private String carrier;
    private String imageName;


    TypeLicence(Integer id, String name, String carrier, String imageName) {
        this.id = id;
        this.name = name;
        this.carrier = carrier;
        this.imageName = imageName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
