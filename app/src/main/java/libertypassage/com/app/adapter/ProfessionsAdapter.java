package libertypassage.com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import libertypassage.com.app.R;
import libertypassage.com.app.models.DetailIndustryProf;


public class ProfessionsAdapter extends BaseAdapter {
    private Context context;
    private List<DetailIndustryProf> options;

    public ProfessionsAdapter(Context context, List<DetailIndustryProf> options) {
        this.context = context;
        this.options = options;
    }
    @Override
    public int getCount() {
        return options.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= (inflater).inflate(R.layout.adapter_custom_spinner,viewGroup,false);
        }
        TextView tvName=view.findViewById(R.id.tv_name);
        DetailIndustryProf model=options.get(i);
        tvName.setText(model.getTitle());
        return view;
    }
}