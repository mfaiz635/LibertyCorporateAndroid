package libertypassage.com.app.other;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import libertypassage.com.app.R;



public class QRScanResultActivity extends AppCompatActivity {
    TextView txtScanResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_scan_result_activity);

        txtScanResult = (TextView) findViewById(R.id.txtScanResult); /* Find TextView and initialize it to object */

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String scanResult = extras.getString("ResultText"); /* Retrieving text of QR Code */
            txtScanResult.setText(scanResult);
        }
    }
}
