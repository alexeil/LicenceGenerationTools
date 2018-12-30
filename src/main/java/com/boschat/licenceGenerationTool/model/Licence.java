package com.boschat.licenceGenerationTool.model;

import java.util.ArrayList;
import java.util.List;


public class Licence {

    public static final String HEADER_COLUMN_PRENOM = "prénom";
    public static final String HEADER_COLUMN_NOM = "name";
    public static final String HEADER_COLUMN_DATE_DE_NAISSANCE = "datedenaissance";
    public static final String HEADER_COLUMN_CLUB = "club";
    public static final String HEADER_COLUMN_PHOTO = "photo";
    public static final String HEADER_COLUMN_HOMMME = "homme";
    public static final String HEADER_COLUMN_NOM_EQUIPE = "nomdéquipe";
    public static final String HEADER_COLUMN_FEMME = "femme";
    public static final String HEADER_COLUMN_JUNIOR = "junior";
    public static final String HEADER_COLUMN_SENIOR = "sénior";
    public static final String HEADER_COLUMN_TYPE_LICENCE_COMPETITION = "compétition";
    public static final String HEADER_COLUMN_TYPE_LICENCE_DIRIGEANT = "dirigeant";
    public static final String HEADER_COLUMN_TYPE_LICENCE_COACH = "coach";
    public static final String HEADER_COLUMN_TYPE_LICENCE_ARBITRE = "arbitre";
    public static final String HEADER_COLUMN_TYPE_LICENCE_LOISIR = "loisir";
    public static final String HEADER_COLUMN_NUMERO_LICENCE = "numerodelicence";
    public static final String HEADER_COLUMN_A_GENERER = "agénérer";

    public static final String MALE_TOKEN = "M";
    public static final String FEMALE_TOKEN = "F";

    private String gender;
    private String birthDate;
    private String category;
    private List<TypeLicence> types = new ArrayList<>();
    private String licenceLeisureCompetition;
    private String seriesNumber;
    private String firstName;
    private String name;
    private String club;
    private String photo;
    private String team;

    public Licence() {

    }

    public Licence(String gender, String birthDate, String category, List<TypeLicence> types, String seriesNumber, String firstName, String name, String club, String photo) {
        this.gender = gender;
        this.birthDate = birthDate;
        this.category = category;
        this.types = types;
        this.seriesNumber = seriesNumber;
        this.firstName = firstName;
        this.name = name;
        this.club = club;
        this.photo = photo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<TypeLicence> getTypes() {
        return types;
    }


    public String getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public String getLicenceFileName() {
        return getName().toUpperCase() + "_" + getFirstName().toUpperCase() + "_" + getSeriesNumber() + ".PDF";
    }

    public String getBirthDateAsString() {
        if (MALE_TOKEN.equals(getGender())) {
            return "Né le " + getBirthDate();
        } else if (FEMALE_TOKEN.equals(getGender())) {
            return "Née le " + getBirthDate();
        } else {
            return "";
        }
    }

    public String getLicenceLeisureCompetition() {
        return licenceLeisureCompetition;
    }

    public void setLicenceLeisureCompetition(String licenceLeisureCompetition) {
        this.licenceLeisureCompetition = licenceLeisureCompetition;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "Licence{" +
                "gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", category='" + category + '\'' +
                ", types=" + types +
                ", licenceLeisureCompetition='" + licenceLeisureCompetition + '\'' +
                ", seriesNumber='" + seriesNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", name='" + name + '\'' +
                ", club='" + club + '\'' +
                ", photo='" + photo + '\'' +
                ", team='" + team + '\'' +
                '}';
    }
}
