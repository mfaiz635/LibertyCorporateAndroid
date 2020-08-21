package libertypassage.com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import libertypassage.com.app.R;
import libertypassage.com.app.models.CountryDto;
import libertypassage.com.app.widgets.CenturyGothicTextview;


public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ViewHolder> {
    Context context;
    ArrayList<CountryDto> countryList;

    public CountryCodeAdapter(Context context, ArrayList<CountryDto> countryList) {
        this.context = context;
        this.countryList = countryList;
    }

    @NonNull
    @Override
    public CountryCodeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.country_text, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryCodeAdapter.ViewHolder viewHolder, int i) {
        CountryDto countries = countryList.get(i);

        viewHolder.lbl_name.setText(countries.getCountryName());
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CenturyGothicTextview lbl_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lbl_name = itemView.findViewById(R.id.lbl_name);
        }
    }

    public void filterList(ArrayList<CountryDto> filterdNames) {
        countryList = new ArrayList<>();
        countryList.addAll(filterdNames);

        notifyDataSetChanged();
    }
}
