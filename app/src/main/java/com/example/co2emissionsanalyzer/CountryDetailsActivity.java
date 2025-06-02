package com.example.co2emissionsanalyzer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CountryDetailsActivity extends AppCompatActivity {

    private TextView textCountryName, textPopulation, textDensity, textPercentageWorld;
    private TextView textTotalEmissions, textAvgPerCapita, textHighestYear;
    private Button buttonBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);

        initializeViews();
        displayCountryData();
        setupBackButton();
    }

    private void initializeViews() {
        textCountryName = findViewById(R.id.textCountryName);
        textPopulation = findViewById(R.id.textPopulation);
        textDensity = findViewById(R.id.textDensity);
        textPercentageWorld = findViewById(R.id.textPercentageWorld);
        textTotalEmissions = findViewById(R.id.textTotalEmissions);
        textAvgPerCapita = findViewById(R.id.textAvgPerCapita);
        textHighestYear = findViewById(R.id.textHighestYear);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);
    }

    private void displayCountryData() {
        Intent intent = getIntent();

        String countryName = intent.getStringExtra("country_name");
        int population = intent.getIntExtra("population", 0);
        String density = intent.getStringExtra("density");
        String percentageWorld = intent.getStringExtra("percentage_world");
        int totalEmissions = intent.getIntExtra("total_emissions", 0);
        double avgPerCapita = intent.getDoubleExtra("avg_per_capita", 0.0);
        int highestYear = intent.getIntExtra("highest_year", 0);

        // Display country information
        textCountryName.setText("Country: " + countryName);
        textPopulation.setText("Population: " + String.format("%,d", population));
        textDensity.setText("Density: " + density);
        textPercentageWorld.setText("% of World Landmass: " + percentageWorld);

        // Display calculated statistics
        textTotalEmissions.setText("Total CO2 Emissions (1750-2022): " + String.format("%,d", totalEmissions) + " tons");
        textAvgPerCapita.setText("Average Emissions Per Capita: " + String.format("%.2f", avgPerCapita) + " tons/person");
        textHighestYear.setText("Year with Highest Emissions: " + highestYear);
    }

    private void setupBackButton() {
        buttonBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(CountryDetailsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
