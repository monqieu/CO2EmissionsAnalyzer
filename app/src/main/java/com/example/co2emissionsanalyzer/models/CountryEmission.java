package com.example.co2emissionsanalyzer.models;

import java.util.HashMap;

public class CountryEmission {
    private String countryName;
    private HashMap<Integer, Integer> co2Emissions;
    private int population;
    private String percentageOfWorld;
    private String density;

    // Constructor
    public CountryEmission(String countryName, int population, String percentageOfWorld, String density) {
        this.countryName = countryName;
        this.population = population;
        this.percentageOfWorld = percentageOfWorld;
        this.density = density;
        this.co2Emissions = new HashMap<>();
    }

    // Default constructor
    public CountryEmission() {
        this.co2Emissions = new HashMap<>();
    }

    // Getters
    public String getCountryName() {
        return countryName;
    }

    public HashMap<Integer, Integer> getCo2Emissions() {
        return co2Emissions;
    }

    public int getPopulation() {
        return population;
    }

    public String getPercentageOfWorld() {
        return percentageOfWorld;
    }

    public String getDensity() {
        return density;
    }

    // Setters
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCo2Emissions(HashMap<Integer, Integer> co2Emissions) {
        this.co2Emissions = co2Emissions;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setPercentageOfWorld(String percentageOfWorld) {
        this.percentageOfWorld = percentageOfWorld;
    }

    public void setDensity(String density) {
        this.density = density;
    }

    // Utility methods for calculations
    public int getTotalEmissions() {
        int total = 0;
        for (Integer emission : co2Emissions.values()) {
            if (emission != null) {
                total += emission;
            }
        }
        return total;
    }

    public double getAverageEmissionsPerCapita() {
        if (population == 0) return 0;
        return (double) getTotalEmissions() / population;
    }

    public int getYearWithHighestEmissions() {
        int maxYear = 0;
        int maxEmissions = 0;

        for (HashMap.Entry<Integer, Integer> entry : co2Emissions.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > maxEmissions) {
                maxEmissions = entry.getValue();
                maxYear = entry.getKey();
            }
        }
        return maxYear;
    }

    public int getEmissionsForYear(int year) {
        Integer emissions = co2Emissions.get(year);
        return emissions != null ? emissions : 0;
    }

    public void addEmissionData(int year, int emissions) {
        co2Emissions.put(year, emissions);
    }
}
