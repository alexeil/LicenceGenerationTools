package com.boschat.licenceGenerationTool.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;

public class SpreadSheetUtils {

    private final static String P12FILE = "API-project-Spreadsheet-39f77a0dc547.p12";

    private final static String CLIENT_ID = "240460901799-compute@developer.gserviceaccount.com";

    private static final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile", "https://docs.google.com/feeds", "https://spreadsheets.google.com/feeds",
            "http://spreadsheets.google.com/feeds/spreadsheets/", "https://spreadsheets.google.com/feeds/spreadsheets/private/full");


    private static Drive driveService;
    private static SpreadsheetService spreadsheetService;

    public static SpreadsheetService initService()
            throws GeneralSecurityException, IOException {

        if (spreadsheetService == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            GoogleCredential credential = null;

            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport).setJsonFactory(jsonFactory)
                    .setServiceAccountId(CLIENT_ID)
                    .setServiceAccountScopes(SCOPES)
                    .setServiceAccountPrivateKeyFromP12File(getP12File())
                    .build();

            credential.refreshToken();

            spreadsheetService = new SpreadsheetService("Test");

            spreadsheetService.setOAuth2Credentials(credential);

        }
        return spreadsheetService;
    }

    private static File getP12File() {
        File p12 = new File(ClassLoader.getSystemResource(P12FILE).getFile());
        return p12;
    }

    public static Drive initDriveService() throws GeneralSecurityException,
            IOException {

        if (driveService == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport).setJsonFactory(jsonFactory)
                    .setServiceAccountId(CLIENT_ID)
                    .setServiceAccountScopes(SCOPES)
                    .setServiceAccountPrivateKeyFromP12File(getP12File())
                    .build();

            driveService = new Drive.Builder(httpTransport, jsonFactory,
                    credential).setApplicationName("Test").build();

        }
        return driveService;
    }

    public static SpreadsheetEntry initSpreadsheetEntry(SpreadsheetService service, URL url) throws IOException, ServiceException {

        // Make a request to the API and get all spreadsheets.
        SpreadsheetEntry spreadsheet = service.getEntry(url, SpreadsheetEntry.class);

        return spreadsheet;
    }

}
