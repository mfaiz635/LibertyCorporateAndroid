package libertypassage.com.app.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.collection.LongSparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;

import java.util.ArrayList;
import java.util.List;

import libertypassage.com.app.R;
import libertypassage.com.app.models.MyItem;



public class MarkerClusterRenderer extends DefaultClusterRenderer<MyItem> {
    private Context context;
    private final GoogleMap map;
    ClusterManager<MyItem> clusterManager;
    private List<Circle> circleList = new ArrayList<Circle>();
    IconGenerator mIconGenerator;
    float mDensity;


    public MarkerClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        this.map = map;
        this.clusterManager = clusterManager;

        mDensity = context.getResources().getDisplayMetrics().density;
        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setContentView(makeSquareTextView(context));
        // comment this line to prevent text style being applied and overwriting makeSquareTextView settings
        mIconGenerator.setTextAppearance(R.style.amu_Bubble_TextAppearance_Dark);
    //   mIconGenerator.setBackground(makeClusterBackground());



    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        markerOptions.flat(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(195));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.trasparent_image));
        drawRadius(item);
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    @Override
    protected void onClusterRendered(Cluster<MyItem> cluster, Marker marker) {
        super.onClusterRendered(cluster, marker);
        for (Circle circle : circleList) {
            circle.remove();
        }
        circleList.clear();
    }

    private void drawRadius(MyItem point) {
        CircleOptions circleOptions = new CircleOptions()
                .center(point.getPosition())
                .strokeWidth(4)
                .strokeColor(Color.RED)
                // .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 97, 30, 48))
                .radius(200);
        Circle circle = map.addCircle(circleOptions);
        circleList.add(circle);
    }

    private SquareTextView makeSquareTextView(Context context) {
        SquareTextView squareTextView = new SquareTextView(context);
        // added the following 3 lines to change text size, color and bold
        // I make the text smaller, color and bold is required since we removed R.style.amu_ClusterIcon_TextAppearance
       // squareTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        squareTextView.setTextSize(1);
        squareTextView.setTextColor(Color.RED);
     //   squareTextView.setTypeface(null, Typeface.BOLD);
    //    squareTextView.setTypeface(null, Typeface.NORMAL);
        squareTextView.setText("");

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(R.id.amu_text);
        // initial is 12dp, I resize the round shape padding to 6dp
        int twelveDpi = (int) (6 * mDensity);
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi);
        return squareTextView;
    }
    
    protected int getColor(int clusterSize) {
        /*
        final float hueRange = 220;
        final float sizeRange = 300;
        final float size = Math.min(clusterSize, sizeRange);
        final float hue = (sizeRange - size) * (sizeRange - size) / (sizeRange * sizeRange) * hueRange;
        return Color.HSVToColor(new float[]{
                hue, 1f, .6f
        });
         */

        // I use Cyan color palette: https://material.io/guidelines/style/color.html#color-color-palette
        // refer to variable BUCKETS for available cluster size
        String color = "#C62828";
        switch(clusterSize) {
            case 1:
                color = "#C62828";
                break;
            case 10:
                color = "#C62828";
                break;
            case 20:
                color = "#C62828";
                break;
            case 40:
                color = "#C62828";
                break;
            case 50:
                color = "#C62828";
                break;
            case 100:
                color = "#C62828";
                break;
            case 200:
                color = "#C62828";
                break;
            case 500:
                color = "#C62828";
                break;
            case 1000:
                color = "#C62828";
                break;
            case 10000000:
                color = "#C62828";
                break;
        }
        return Color.parseColor(color);
    }

    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
//        int bucket = getBucket(cluster);
//        BitmapDescriptor descriptor = null;
//        //= mIcons.get(bucket);
//        if (descriptor == null) {
//           // mColoredCircleBackground.getPaint().setColor(getColor(bucket));
//            // you can edit/replace getClusterText to change text of cluster icon
//         //   descriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon(getClusterText(bucket)));
//          //  mIcons.put(bucket, descriptor);
//        }

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(195));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.trasparent_image));
     //   drawRadius(item);

        // TODO: consider adding anchor(.5, .5) (Individual markers will overlap more often)
     //   markerOptions.icon(descriptor);
    }


}


