package com.boschat.licenceGenerationTool.utils;

import com.boschat.licenceGenerationTool.model.GenerationType;
import com.boschat.licenceGenerationTool.model.Licence;
import com.boschat.licenceGenerationTool.model.TypeLicence;
import com.google.common.base.Strings;
import com.google.gdata.util.common.base.StringUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import org.apache.commons.lang.StringUtils;
import com.boschat.licenceGenerationTool.exception.SpreadSheetException;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.boschat.licenceGenerationTool.metier.LicenceWriteFromXLS.DATE_VALIDITY_END;
import static com.boschat.licenceGenerationTool.metier.LicenceWriteFromXLS.DATE_VALIDITY_START;

/**
 *
 */
public class PdfManager {

    public static final String JASPER_NAME = "nom";
    public static final String JAPPER_FIRST_NAME = "prenom";
    public static final String JASPER_SERIES_NUMBER = "licence";
    public static final String JASPER_BIRTHDATE = "dateNaissance";
    public static final String JASPER_LICENCE_TYPE = "typeLicence";
    public static final String JASPER_LICENCE_CARRIER = "typeLicenceLibelle";
    public static final String JAPSER_LEISURE_COMPETITION = "type";
    public static final String JASPER_TEAM = "equipe";
    public static final String JASPER_CATEGORY = "categorie";
    public static final String JASPER_GENDER = "sexe";
    public static final String JASPER_VALIDITY_START = "delivre";
    public static final String JASPER_VALIDITY_END = "valable";
    public static final String JASPER_PHOTO = "photo";
    public static final String JASPER_CLUB = "club";
    public static final String JASPER_LICENCE_COLOUR = "licenceColor";
    private static final String GENERATION_JOUEUR = "JOUEUR/";
    private static final String GENERATION_FOLDER = "target/generation/";
    private static Map<String, List<JasperPrint>> licencesByEquipe = new HashMap<>();
    private static Map<String, List<JasperPrint>> licencesByClub = new HashMap<>();
    private static Map<String, List<JasperPrint>> licencesByCategorie = new HashMap<>();

    public static void generateLicencePdf(List<Licence> licences) {

        System.out.println("Début de la génération des licences ");
        // generateLicences(licences, PLAYER);
        generateLicences(licences, GenerationType.TEAM);
        generateLicences(licences, GenerationType.CLUB);
        generateLicences(licences, GenerationType.CATEGORY);
    }

    private static void generateLicences(List<Licence> licences, GenerationType typeGeneration) {
        licencesByClub.clear();
        licencesByEquipe.clear();
        licencesByCategorie.clear();

        List<JasperPrint> list = new ArrayList<>();
        HashMap<String, Object> hm;

        try {
            System.out.println("Start " + typeGeneration.name() + " ...");
            // Get jasper report

            ClassLoader classLoader = PdfManager.class.getClassLoader();
            String jrxmlFileName = new File(classLoader.getResource("licenceFKBF.jrxml").getFile()).getAbsolutePath();
            String jasperFileName = new File(classLoader.getResource("licenceFKBF.jasper").getFile()).getAbsolutePath();

            JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);

            for (Licence licence : licences) {

                try {
                    hm = new HashMap<>();
                    hm.put(JASPER_NAME, licence.getName().toUpperCase());
                    hm.put(JAPPER_FIRST_NAME, licence.getFirstName().toUpperCase());
                    hm.put(JASPER_SERIES_NUMBER, licence.getSeriesNumber());
                    hm.put(JASPER_BIRTHDATE, licence.getBirthDateAsString());

                    int nbTypeLicence = 1;
                    for (TypeLicence typeLicence : licence.getTypes()) {
                        hm.put(JASPER_LICENCE_TYPE + (nbTypeLicence), ClassLoader.getSystemResource(typeLicence.getImageName()).openStream());
                        hm.put(JASPER_LICENCE_CARRIER + (nbTypeLicence), typeLicence.getCarrier());
                        nbTypeLicence++;
                    }

                    if (!Strings.isNullOrEmpty(licence.getLicenceLeisureCompetition())) {
                        hm.put(JAPSER_LEISURE_COMPETITION, "Type de Licence : " + licence.getLicenceLeisureCompetition());
                    }

                    hm.put(JASPER_TEAM, licence.getTeam() == null ? null : "Equipe : " + licence.getTeam());
                    hm.put(JASPER_CATEGORY, licence.getCategory());
                    hm.put(JASPER_GENDER, licence.getGender());
                    hm.put(JASPER_VALIDITY_START, DATE_VALIDITY_START);
                    hm.put(JASPER_VALIDITY_END, DATE_VALIDITY_END);
                    hm.put(JASPER_PHOTO, getPhoto(licence, true));
                    hm.put(JASPER_CLUB, licence.getClub().toUpperCase());
                    hm.put(JASPER_LICENCE_COLOUR, ClassLoader.getSystemResource("FKBF_FondGris.png").openStream());

                    exportPDF(list, hm, jasperFileName, licence, typeGeneration);
                } catch (Exception e) {
                    System.out.println("ERROR exporting reports to pdf : " + licence.getLicenceFileName() + " ===> " + e.getMessage());
                }
            }

            switch (typeGeneration) {
                case CLUB:
                    licencesByClub.forEach((key, value) -> concatenateLicences(value, key));
                case TEAM:
                    licencesByEquipe.forEach((key, value) -> concatenateLicences(value, key));
                case CATEGORY:
                    licencesByCategorie.forEach((key, value) -> concatenateLicences(value, key));
                default:
                    //nothing to do
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void exportPDF(List<JasperPrint> list, HashMap<String, Object> hm, String jasperFileName, Licence licence, GenerationType typeGeneration) throws JRException {
        // Generate jasper print
        JasperPrint jprint = JasperFillManager.fillReport(jasperFileName, hm, new JREmptyDataSource());

        String path = GENERATION_FOLDER + licence.getClub().toUpperCase() + "/";
        if (GenerationType.PLAYER == typeGeneration) {
            String destFile = path + GENERATION_JOUEUR;
            mkDir(destFile);
            JasperExportManager.exportReportToPdfFile(jprint, destFile + licence.getLicenceFileName());
        }
        list.add(jprint);

        if (!StringUtils.isEmpty(licence.getClub())) {
            addToCollection(path + licence.getClub().toUpperCase(), jprint, licencesByClub);
        }

        if (!StringUtils.isEmpty(licence.getTeam())) {
            addToCollection(path + licence.getGender() + "_" + licence.getTeam().toUpperCase(), jprint, licencesByEquipe);
        }

        if (!StringUtils.isEmpty(licence.getCategory())) {
            addToCollection(path + licence.getCategory().toUpperCase(), jprint, licencesByCategorie);
        }
    }

    private static InputStream getPhoto(Licence licence, boolean checkPhoto) throws IOException, GeneralSecurityException {

        if (!checkPhoto || StringUtil.isEmpty(licence.getPhoto())) {
            // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // SpreadSheetUtils.initDriveService().files()
            // .get("0B8jseuWfPiPyMGtDTlFlMzNvckE")
            // .executeMediaAndDownloadTo(outputStream);
            // InputStream is = new FileInputStream()
            // OutputStream outputStream = new FileOutputStream(new File(
            // "E:/dev/src/spreadSheetReader/src/main/resources/Anonyme.png"));

            // return new ByteArrayInputStream(outputStream);
            InputStream in = ClassLoader.getSystemResource("Anonyme.png").openStream();

            byte[] buff = new byte[80000];

            int bytesRead;

            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            while ((bytesRead = in.read(buff)) != -1) {
                bao.write(buff, 0, bytesRead);
            }

            byte[] data = bao.toByteArray();

            return new ByteArrayInputStream(data);
        } else {
            String id = licence.getPhoto().split("id=")[1];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            SpreadSheetUtils.initDriveService().files().get(id).executeMediaAndDownloadTo(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private static void mkDir(String Path) {
        File folderJoueur = new File(Path);
        if (!folderJoueur.exists()) {
            folderJoueur.mkdirs();
            System.out.println("Création du dossier : " + Path);
        }
    }

    private static void addToCollection(String key, JasperPrint value, Map<String, List<JasperPrint>> map) {

        List<JasperPrint> values = map.get(key);
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(value);
        map.put(key, values);

    }

    private static void concatenateLicences(List<JasperPrint> list, String destFilmName) {

        JasperPrint pageFinal = new JasperPrint();
        // 155*4 + marge
        pageFinal.setPageHeight(760);
        // 240 * 2 + marge
        pageFinal.setPageWidth(580);

        int nbLicence = 0;
        JRBasePrintPage page = null;

        int x;
        int y;
        for (JasperPrint jasperPrint : list) {

            if (nbLicence % 8 == 0) {
                page = new JRBasePrintPage();
                pageFinal.getPages().add(page);
            }

            if ((nbLicence % 2) == 0) {
                x = 25;
            } else {
                x = 300;
            }

            y = (((nbLicence % 8 / 2)) * 180) + 10;

            for (JRPrintElement jRPrintElement : jasperPrint.getPages().get(0).getElements()) {
                jRPrintElement.setX(jRPrintElement.getX() + x);
                jRPrintElement.setY(jRPrintElement.getY() + y);
                page.getElements().add(jRPrintElement);
            }
            nbLicence++;
        }

        mkDir(destFilmName.substring(0, destFilmName.lastIndexOf("/")));

        try {
            JasperExportManager.exportReportToPdfFile(pageFinal, destFilmName + ".pdf");
        } catch (JRException e) {
            throw new SpreadSheetException(e);
        }

        System.out.println("Fichier de merge : " + destFilmName + ".PDF" + " généré !");
    }

}
