package libertypassage.com.corporate.others;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import libertypassage.com.corporate.R;


public class QRScanResultActivity extends AppCompatActivity {
    TextView txtScanResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcode_scan_result_activity);

        txtScanResult = findViewById(R.id.txtScanResult); /* Find TextView and initialize it to object */

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String scanResult = extras.getString("ResultText"); /* Retrieving text of QR Code */
            txtScanResult.setText(scanResult);
        }
    }
}
