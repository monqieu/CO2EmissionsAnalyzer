package com.example.co2emissionsanalyzer.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.example.co2emissionsanalyzer.models.CountryEmission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    private Context context;

    public CSVParser(Context context) {
        this.context = context;
    }

    public List<CountryEmission> parseCSVFile(String fileName) {
        Map<String, CountryEmission> countryMap = new HashMap<>();
        AssetManager assetManager = context.getAssets();

        try {
            Log.d("CSVParser", "Attempting to open file: " + fileName);

            // List all files in assets to debug
            String[] assetFiles = assetManager.list("");
            Log.d("CSVParser", "Files in assets folder: " + java.util.Arrays.toString(assetFiles));

            InputStream inputStream = assetManager.open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            boolean isFirstLine = true;
            int lineCount = 0;
            int processedLines = 0;

            while ((line = reader.readLine()) != null) {
                lineCount++;

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (isFirstLine) {
                    // Skip header line
                    Log.d("CSVParser", "Header: " + line);
                    isFirstLine = false;
                    continue;
                }

                try {
                    // Parse CSV line: Country,Code,Calling Code,Year,CO2 emission (Tons),Population(2022),Area,% of World,Density(km2)
                    String[] values = line.split(",");

                    if (values.length >= 9) {
                        String countryName = values[0].trim();
                        String code = values[1].trim();
                        int year = Integer.parseInt(values[3].trim());

                        // Parse CO2 emissions (handle decimal values)
                        double co2Emissions = 0;
                        try {
                            co2Emissions = Double.parseDouble(values[4].trim());
                        } catch (NumberFormatException e) {
                            co2Emissions = 0;
                        }

                        // Parse population
                        long population = 0;
                        try {
                            population = (long) Double.parseDouble(values[5].trim());
                        } catch (NumberFormatException e) {
                            population = 0;
                        }

                        // Parse area
                        double area = 0;
                        try {
                            area = Double.parseDouble(values[6].trim());
                        } catch (NumberFormatException e) {
                            area = 0;
                        }

                        String percentageOfWorld = values[7].trim();
                        String density = values[8].trim();

                        // Get or create country object
                        CountryEmission country = countryMap.get(countryName);
                        if (country == null) {
                            country = new CountryEmission();
                            country.setCountryName(countryName);
                            country.setPopulation((int) population);
                            country.setPercentageOfWorld(percentageOfWorld);
                            country.setDensity(density);
                            countryMap.put(countryName, country);
                        }

                        // Add emissions data for this year (convert to int tons)
                        if (co2Emissions > 0) {
                            country.addEmissionData(year, (int) co2Emissions);
                        }

                        processedLines++;

                        // Log progress every 1000 lines
                        if (processedLines % 1000 == 0) {
                            Log.d("CSVParser", "Processed " + processedLines + " lines, found " + countryMap.size() + " countries");
                        }

                    } else {
                        Log.w("CSVParser", "Line " + lineCount + " has insufficient columns: " + values.length);
                    }

                } catch (Exception e) {
                    Log.w("CSVParser", "Error parsing line " + lineCount + ": " + e.getMessage());
                }
            }

            reader.close();
            inputStream.close();

            // Convert map to list
            List<CountryEmission> countries = new ArrayList<>(countryMap.values());

            Log.d("CSVParser", "Successfully parsed " + countries.size() + " countries from " + processedLines + " data lines");

            // Log some sample countries for debugging
            for (int i = 0; i < Math.min(5, countries.size()); i++) {
                CountryEmission country = countries.get(i);
                Log.d("CSVParser", "Sample country: " + country.getCountryName() +
                        ", Population: " + country.getPopulation() +
                        ", 2022 Emissions: " + country.getEmissionsForYear(2022));
            }

            return countries;

        } catch (IOException e) {
            Log.e("CSVParser", "Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<CountryEmission> getTopPolluters(List<CountryEmission> countries, int year, int limit) {
        List<CountryEmission> sortedCountries = new ArrayList<>();

        // Filter countries that have data for the specified year
        for (CountryEmission country : countries) {
            if (country.getEmissionsForYear(year) > 0) {
                sortedCountries.add(country);
            }
        }

        // Sort by emissions for the specified year (descending)
        sortedCountries.sort((c1, c2) -> {
            int emissions1 = c1.getEmissionsForYear(year);
            int emissions2 = c2.getEmissionsForYear(year);
            return Integer.compare(emissions2, emissions1);
        });

        // Return top 'limit' countries
        int resultSize = Math.min(limit, sortedCountries.size());
        Log.d("CSVParser", "getTopPolluters: Found " + sortedCountries.size() + " countries with data for " + year + ", returning top " + resultSize);

        return sortedCountries.subList(0, resultSize);
    }

    public static CountryEmission getHighestEmitterForYear(List<CountryEmission> countries, int year) {
        CountryEmission highest = null;
        int maxEmissions = 0;

        for (CountryEmission country : countries) {
            int emissions = country.getEmissionsForYear(year);
            if (emissions > maxEmissions) {
                maxEmissions = emissions;
                highest = country;
            }
        }

        Log.d("CSVParser", "getHighestEmitterForYear " + year + ": " +
                (highest != null ? highest.getCountryName() + " (" + maxEmissions + " tons)" : "None"));

        return highest;
    }

    public static CountryEmission findCountryByName(List<CountryEmission> countries, String name) {
        Log.d("CSVParser", "Searching for: '" + name + "' in " + countries.size() + " countries");

        for (CountryEmission country : countries) {
            if (country.getCountryName().toLowerCase().contains(name.toLowerCase())) {
                Log.d("CSVParser", "Found match: " + country.getCountryName());
                return country;
            }
        }

        // Log available countries for debugging
        Log.d("CSVParser", "No match found. Available countries include:");
        for (int i = 0; i < Math.min(10, countries.size()); i++) {
            Log.d("CSVParser", "  - " + countries.get(i).getCountryName());
        }

        return null;
    }
}