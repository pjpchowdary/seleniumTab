import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceNowIncidentDownloader {

    private static final String INSTANCE_URL = "https://your-instance.service-now.com";
    private static final String API_ENDPOINT = "/api/now/table/incident";
    private static final String USERNAME = "your-username";
    private static final String PASSWORD = "your-password";

    public static void main(String[] args) {
        try {
            String incidents = getMajorIncidents2024();
            if (incidents != null) {
                saveIncidentsToFile(incidents);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMajorIncidents2024() throws Exception {
        String query = "priority=1^opened_atONThis year@javascript:gs.beginningOfThisYear()@javascript:gs.endOfThisYear()";
        String fields = "number,short_description,priority,opened_at,closed_at,state";
        String urlStr = INSTANCE_URL + API_ENDPOINT + "?sysparm_query=" + query + "&sysparm_fields=" + fields + "&sysparm_display_value=true";
        
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        // Basic Authentication
        String auth = USERNAME + ":" + PASSWORD;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

        // TODO: Implement SSO and MFA authentication logic here
        // This might involve opening a browser for SSO login and prompting for MFA code

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = br.readLine()) != null) {
            response.append(output);
        }

        conn.disconnect();

        return response.toString();
    }

    private static void saveIncidentsToFile(String incidents) throws Exception {
        String fileName = "major_incidents_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
        Files.write(Paths.get(fileName), incidents.getBytes());
        System.out.println("Incidents saved to " + fileName);
    }
}
