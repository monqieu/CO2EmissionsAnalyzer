package com.example.co2emissionsanalyzer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.co2emissionsanalyzer.R;
import com.example.co2emissionsanalyzer.models.CountryEmission;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    private List<CountryEmission> countries;
    private OnCountryClickListener listener;
    private int displayYear; // Add this to track which year to display

    public interface OnCountryClickListener {
        void onCountryClick(CountryEmission country);
    }

    // Updated constructor to accept the year
    public CountryAdapter(List<CountryEmission> countries, OnCountryClickListener listener, int displayYear) {
        this.countries = countries;
        this.listener = listener;
        this.displayYear = displayYear;
    }

    // Keep the old constructor for compatibility, defaulting to 2022
    public CountryAdapter(List<CountryEmission> countries, OnCountryClickListener listener) {
        this(countries, listener, 2022);
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        CountryEmission country = countries.get(position);
        holder.bind(country, listener, displayYear);
    }

    @Override
    public int getItemCount() {
        return countries != null ? countries.size() : 0;
    }

    static class CountryViewHolder extends RecyclerView.ViewHolder {
        private TextView textCountryName;
        private TextView textEmissions2022;
        private TextView textRanking;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCountryName = itemView.findViewById(R.id.textCountryName);
            textEmissions2022 = itemView.findViewById(R.id.textEmissions2022);
            textRanking = itemView.findViewById(R.id.textRanking);
        }

        public void bind(CountryEmission country, OnCountryClickListener listener, int displayYear) {
            textCountryName.setText(country.getCountryName());

            // Use the actual display year instead of hardcoded 2022
            int emissions = country.getEmissionsForYear(displayYear);
            textEmissions2022.setText(String.format("%,d tons", emissions));

            textRanking.setText("#" + (getAdapterPosition() + 1));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCountryClick(country);
                }
            });
        }
    }
}