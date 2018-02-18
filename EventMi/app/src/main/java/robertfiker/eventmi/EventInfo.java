package robertfiker.eventmi;

/**
 * Created by robfi on 2018-02-17.
 */

public class EventInfo {

    private String title;
    private int proximity;
    private double latitude;
    private double longitude;
    private String discord;
    private String website;
    private String details;

    public EventInfo(String tit, int prox, double lat, double lon, String dis, String web, String det){
        title = tit;
        proximity = prox;
        latitude = lat;
        longitude = lon;
        discord = dis;
        website = web;
        details = det;
    }

    public EventInfo(){
        title = "empty";
        proximity = 0;
        latitude = 0;
        longitude = 0;
        discord = "";
        website = "";
        details = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProximity() {
        return proximity;
    }

    public void setProximity(int proximity) {
        this.proximity = proximity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDiscord() {
        return discord;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
