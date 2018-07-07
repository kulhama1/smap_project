package com.example.martin.smapy.Database;

public class Place {

    private int id;
    private int id_z_webu;
    private String name;
    private String description;
    private double gps_x;
    private double gps_y;
    private String adresa_obrazku;

    public Place(int id, int id_z_webu, String name, String description, double gps_x, double gps_y, String adresa_obrazku){
        this.id = id;
        this.id_z_webu = id_z_webu;
        this.name = name;
        this.description = description;
        this.gps_x = gps_x;
        this.gps_y = gps_y;
        this.adresa_obrazku = adresa_obrazku;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId_z_webu(int id_z_webu) {
        this.id_z_webu = id_z_webu;
    }

    public int getId_z_webu() {
        return id_z_webu;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setGps_x(double gps_x) {
        this.gps_x = gps_x;
    }

    public double getGps_x() {
        return gps_x;
    }

    public void setGps_y(double gps_y) {
        this.gps_y = gps_y;
    }

    public double getGps_y() {
        return gps_y;
    }

    public void setAdresa_obrazku(String adresa_obrazku) {
        this.adresa_obrazku = adresa_obrazku;
    }

    public String getAdresa_obrazku() {
        return adresa_obrazku;
    }
}
