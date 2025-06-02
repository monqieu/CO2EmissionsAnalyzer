package com.example.co2emissionsanalyzer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.co2emissionsanalyzer.models.CountryEmission;
import com.example.co2emissionsanalyzer.utils.CSVParser;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private EditText editTextYear;
    private Button buttonFilter, buttonBackToHome;
    private TextView textResult;
    private List<CountryEmission> allCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initializeViews();
        loadData();
        setupClickListeners();
    }

    private void initializeViews() {
        editTextYear = findViewById(R.id.editTextYear);
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonBackToHome = findViewById(R.id.buttonBackToHome);
        textResult = findViewById(R.id.textResult);
    }

    private void loadData() {
        CSVParser parser = new CSVParser(this);
        allCountries = parser.parseCSVFile("co2_emission_by_countries.csv"); // Updated filename
    }

    private void setupClickListeners() {
        buttonFilter.setOnClickListener(v -> performFilter());

        buttonBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(FilterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void performFilter() {
        String yearStr = editTextYear.getText().toString().trim();

        if (yearStr.isEmpty()) {
            Toast.makeText(this, "Please enter a year", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);

            // Validate year range
            if (year < 1750 || year > 2022) {
                Toast.makeText(this, "Year must be between 1750 and 2022", Toast.LENGTH_SHORT).show();
                return;
            }

            // Find country with highest emissions for that year
            CountryEmission highestEmitter = CSVParser.getHighestEmitterForYear(allCountries, year);

            if (highestEmitter != null) {
                int emissions = highestEmitter.getEmissionsForYear(year);
                String result = "Highest CO2 Emitter in " + year + ":\n\n" +
                        "Country: " + highestEmitter.getCountryName() + "\n" +
                        "CO2 Emissions: " + String.format("%,d", emissions) + " tons";
                textResult.setText(result);
                textResult.setVisibility(View.VISIBLE);
            } else {
                textResult.setText("No data available for " + year);
                textResult.setVisibility(View.VISIBLE);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid year", Toast.LENGTH_SHORT).show();
        }
    }
}