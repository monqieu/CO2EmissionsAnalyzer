package com.example.co2emissionsanalyzer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.co2emissionsanalyzer.adapters.CountryAdapter;
import com.example.co2emissionsanalyzer.models.CountryEmission;
import com.example.co2emissionsanalyzer.utils.CSVParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTopPolluters;
    private CountryAdapter adapter;
    private List<CountryEmission> allCountries;
    private List<CountryEmission> topPolluters;
    private EditText editTextSearch;
    private Button buttonSearch, buttonFilter, buttonSelectedCountry;
    private TextView textTopPollutersTitle;
    private int currentDisplayYear = 2022; // Track which year we're showing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("MainActivity", "onCreate started");

        try {
            setContentView(R.layout.activity_main);
            Log.d("MainActivity", "Layout set successfully");

            initializeViews();
            Log.d("MainActivity", "Views initialized");

            loadData();
            Log.d("MainActivity", "Data loaded");

            setupRecyclerView();
            Log.d("MainActivity", "RecyclerView setup");

            setupClickListeners();
            Log.d("MainActivity", "Click listeners setup");

            Log.d("MainActivity", "onCreate completed successfully");

        } catch (Exception e) {
            Log.e("MainActivity", "Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error starting app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeViews() {
        recyclerViewTopPolluters = findViewById(R.id.recyclerViewTopPolluters);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonSelectedCountry = findViewById(R.id.buttonSelectedCountry);
        textTopPollutersTitle = findViewById(R.id.textTopPollutersTitle);
    }

    private void loadData() {
        try {
            Log.d("MainActivity", "Starting data loading...");

            // Parse CSV file and load data
            CSVParser parser = new CSVParser(this);
            allCountries = parser.parseCSVFile("co2_emission_by_countries.csv");
            Log.d("MainActivity", "CSV parsed, found " + allCountries.size() + " countries");

            // Debug: Check what years we have data for
            if (!allCountries.isEmpty()) {
                CountryEmission sampleCountry = allCountries.get(0);
                Log.d("MainActivity", "Sample country: " + sampleCountry.getCountryName());

                // Check what years this country has data for
                HashMap<Integer, Integer> emissions = sampleCountry.getCo2Emissions();
                Log.d("MainActivity", "Sample country has data for " + emissions.size() + " years");

                // Log some specific years to see what data we have
                for (int year = 2018; year <= 2022; year++) {
                    int yearEmissions = sampleCountry.getEmissionsForYear(year);
                    Log.d("MainActivity", "Sample - Year " + year + ": " + yearEmissions + " tons");
                }
            }

            // Find the most recent year with data
            currentDisplayYear = 2022;
            topPolluters = CSVParser.getTopPolluters(allCountries, currentDisplayYear, 10);
            Log.d("MainActivity", "Year " + currentDisplayYear + " has " + topPolluters.size() + " countries with data");

            // If 2022 has no data, try previous years
            if (topPolluters.isEmpty()) {
                for (int year = 2021; year >= 2010; year--) {
                    topPolluters = CSVParser.getTopPolluters(allCountries, year, 10);
                    Log.d("MainActivity", "Year " + year + " has " + topPolluters.size() + " countries with data");

                    if (!topPolluters.isEmpty()) {
                        currentDisplayYear = year;
                        Log.d("MainActivity", "Using year " + year + " instead of 2022");

                        // Log the top emitters for debugging
                        for (int i = 0; i < Math.min(3, topPolluters.size()); i++) {
                            CountryEmission country = topPolluters.get(i);
                            Log.d("MainActivity", "Top " + (i+1) + ": " + country.getCountryName() +
                                    " (" + country.getEmissionsForYear(year) + " tons)");
                        }
                        break;
                    }
                }
            } else {
                // Log the top emitters for 2022
                for (int i = 0; i < Math.min(3, topPolluters.size()); i++) {
                    CountryEmission country = topPolluters.get(i);
                    Log.d("MainActivity", "Top " + (i+1) + " for 2022: " + country.getCountryName() +
                            " (" + country.getEmissionsForYear(2022) + " tons)");
                }
            }

            // Update the title with the actual year being displayed
            if (textTopPollutersTitle != null) {
                textTopPollutersTitle.setText("Top 10 CO2 Polluters (" + currentDisplayYear + ")");
            }

            // Show appropriate message (without year since it's now on screen)
            if (allCountries.isEmpty()) {
                Toast.makeText(this, "Failed to load data - no countries found", Toast.LENGTH_LONG).show();
                Log.w("MainActivity", "No countries loaded from CSV");
            } else if (topPolluters.isEmpty()) {
                Toast.makeText(this, "Loaded " + allCountries.size() + " countries, but no emission data found", Toast.LENGTH_LONG).show();
                Log.w("MainActivity", "Countries loaded but no top polluters found");
            } else {
                // Simple message without year since it's shown on screen
                Toast.makeText(this, "Loaded " + allCountries.size() + " countries successfully", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Data loading successful - using year " + currentDisplayYear + " with " + topPolluters.size() + " polluters");
            }

        } catch (Exception e) {
            Log.e("MainActivity", "Error loading data: " + e.getMessage());
            e.printStackTrace();

            // Create empty lists as fallback
            allCountries = new ArrayList<>();
            topPolluters = new ArrayList<>();

            Toast.makeText(this, "Error loading CSV data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        try {
            // Pass the correct year to the adapter
            adapter = new CountryAdapter(topPolluters, this::onCountryClick, currentDisplayYear);
            recyclerViewTopPolluters.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTopPolluters.setAdapter(adapter);
            Log.d("MainActivity", "RecyclerView setup successfully with " + topPolluters.size() + " items for year " + currentDisplayYear);
        } catch (Exception e) {
            Log.e("MainActivity", "Error setting up RecyclerView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        buttonSearch.setOnClickListener(v -> performSearch());

        buttonFilter.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            intent.putExtra("countries_data", "countries_loaded");
            startActivity(intent);
        });

        buttonSelectedCountry.setOnClickListener(v -> {
            if (!allCountries.isEmpty()) {
                CountryEmission selectedCountry = allCountries.get(0);
                navigateToCountryDetails(selectedCountry);
            } else {
                Toast.makeText(this, "No countries available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch() {
        String searchQuery = editTextSearch.getText().toString().trim();

        if (searchQuery.isEmpty()) {
            Toast.makeText(this, "Please enter a country name", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("MainActivity", "Searching for: " + searchQuery);
        CountryEmission foundCountry = CSVParser.findCountryByName(allCountries, searchQuery);

        if (foundCountry != null) {
            navigateToCountryDetails(foundCountry);
        } else {
            Toast.makeText(this, "Country not found: " + searchQuery, Toast.LENGTH_SHORT).show();
        }
    }

    private void onCountryClick(CountryEmission country) {
        navigateToCountryDetails(country);
    }

    private void navigateToCountryDetails(CountryEmission country) {
        Intent intent = new Intent(MainActivity.this, CountryDetailsActivity.class);
        intent.putExtra("country_name", country.getCountryName());
        intent.putExtra("population", country.getPopulation());
        intent.putExtra("percentage_world", country.getPercentageOfWorld());
        intent.putExtra("density", country.getDensity());
        intent.putExtra("total_emissions", country.getTotalEmissions());
        intent.putExtra("avg_per_capita", country.getAverageEmissionsPerCapita());
        intent.putExtra("highest_year", country.getYearWithHighestEmissions());
        startActivity(intent);
    }
}