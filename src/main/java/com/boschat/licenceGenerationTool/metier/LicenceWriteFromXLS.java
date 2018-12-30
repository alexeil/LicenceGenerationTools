package com.boschat.licenceGenerationTool.metier;

import com.boschat.licenceGenerationTool.model.Licence;
import com.boschat.licenceGenerationTool.model.TypeLicence;
import com.boschat.licenceGenerationTool.utils.PdfManager;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.poi.ss.usermodel.CellType.*;
import static com.boschat.licenceGenerationTool.model.Category.JUNIOR;
import static com.boschat.licenceGenerationTool.model.Category.SENIOR;

/**
 * Created by tboschqt on 10/10/2017.
 */
public class LicenceWriteFromXLS {

    public static final String DATE_VALIDITY_START = "01/09/2018";
    public static final String DATE_VALIDITY_END = "31/08/2019";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final String PREFIX_LICENCE = "20182019";


    private LicenceWriteFromXLS() {

    }

    public static void main(String[] args) throws IOException {
        loadFile("C:\\Users\\tboschqt\\Desktop\\Licences 2018-19.xlsx");
    }

    private static void loadFile(String fullName) throws IOException {
        List<Licence> licences;

        try (InputStream ExcelFileToRead = new FileInputStream(fullName); XSSFWorkbook wb = new XSSFWorkbook(ExcelFileToRead)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            licences = buildAndFillLicencesList(rows);
        }
        PdfManager.generateLicencePdf(licences);

        System.out.println("Generation done !");
    }

    private static List<Licence> buildAndFillLicencesList(Iterator rows) {
        XSSFRow row;
        List<Licence> licences = new ArrayList<>();
        rows.next();

        while (rows.hasNext()) {
            row = (XSSFRow) rows.next();

            Licence licence = getRowData(row);
            if (licence != null) {
                licences.add(licence);
                System.out.println(licence);
            }
        }
        return licences;
    }

    private static String getCellValue(XSSFCell cell) {
        if (cell != null) {
            if (cell.getCellType() == STRING) {
                return StringUtils.trimToEmpty(cell.getStringCellValue());
            } else if (cell.getCellType() == NUMERIC) {
                return cell.getNumericCellValue() + "";
            }
        }
        return "";
    }

    private static String getCurrentTimeStamp(long date) {
        return SIMPLE_DATE_FORMAT.format(date);
    }

    private static Licence getRowData(XSSFRow row) {
        Licence licence = new Licence();

        String firstName = getCellValue(row.getCell(0));

        // Skip empty row
        if (StringUtils.isEmpty(firstName)) {
            return null;
        }

        String name = getCellValue(row.getCell(1));
        String male = getCellValue(row.getCell(2));
        String female = getCellValue(row.getCell(3));
        // String Mail = row.getCell(4));
        //  String Arbitrage = row.getCell(5));

        String birthDate = "N/A";
        try {
            birthDate = getCurrentTimeStamp(row.getCell(6).getDateCellValue().getTime());
        } catch (Exception e) {
            if (row.getCell(6).getCellType() != BLANK) {
                System.out.println(" Format de date incorrecte " + getCellValue(row.getCell(6)) + ", ligne :" + row.getRowNum());
                birthDate = getCellValue(row.getCell(6));
            }
        }

        String team = getCellValue(row.getCell(7));
        String idClub = getCellValue(row.getCell(8));
        String club = getCellValue(row.getCell(9));
        // photo is not implemented yet
        String junior = getCellValue(row.getCell(11));
        String senior = getCellValue(row.getCell(12));
        String competition = getCellValue(row.getCell(13));
        String leisure = getCellValue(row.getCell(14));
        String referee = getCellValue(row.getCell(15));
        String coach = getCellValue(row.getCell(16));
        String administrator = getCellValue(row.getCell(17));

        licence.setSeriesNumber(PREFIX_LICENCE + idClub + row.getRowNum());
        licence.setFirstName(firstName);
        licence.setName(name);
        if (StringUtils.isNotEmpty(male)) {
            licence.setGender(Licence.MALE_TOKEN);
        } else if (StringUtils.isNotEmpty(female)) {
            licence.setGender(Licence.FEMALE_TOKEN);
        }
        licence.setBirthDate(birthDate);
        licence.setTeam(team);
        licence.setClub(club);
        if (StringUtils.isNotEmpty(junior)) {
            licence.setCategory(JUNIOR.getName());
        } else if (StringUtils.isNotEmpty(senior)) {
            licence.setCategory(SENIOR.getName());
        }


        if (StringUtils.isNotEmpty(competition)) {
            licence.getTypes().add(TypeLicence.COMPETITION);
            licence.setLicenceLeisureCompetition(TypeLicence.COMPETITION.getName());
        }

        if (StringUtils.isNotEmpty(leisure)) {
            licence.getTypes().add(TypeLicence.LEISURE);
            licence.setLicenceLeisureCompetition(TypeLicence.LEISURE.getName());
        }

        if (StringUtils.isNotEmpty(referee)) {
            licence.getTypes().add(TypeLicence.REFEREE);
        }

        if (StringUtils.isNotEmpty(coach)) {
            licence.getTypes().add(TypeLicence.COACH);
        }

        if (StringUtils.isNotEmpty(administrator)) {
            licence.getTypes().add(TypeLicence.ADMINISTRATOR);
        }
        return licence;
    }

}
