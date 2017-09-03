package com.farifam.ayomelangkah;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.SmotionPedometer;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements PedometerCallback  {

    PedometerHelper pedometerHelper;
    LinearLayout res;
    TextView res_title;
    TextView res_result;
    TextView res_residu;
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pedometerHelper = new PedometerHelper(this);

        try {
            pedometerHelper.initialize();
            pedometerHelper.setPedometerCallback(this);

        }catch (IllegalArgumentException e){
            showErrorDialog("Something went wrong",e.getMessage());
            return;
        }catch (SsdkUnsupportedException e){
            showErrorDialog("SDK Not Supported",e.getMessage());
            return;
        }

        pedometerHelper.setModePedometer(PedometerHelper.MODE_PEDOMETER_REALTIME);

        res = (LinearLayout)findViewById(R.id.res);
        res_title=(TextView)findViewById(R.id.res_title);
        res_result=(TextView)findViewById(R.id.res_result);
        res_residu=(TextView)findViewById(R.id.res_residu);
        btnStart = (Button)findViewById(R.id.btn_start);

        spinMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case PedometerHelper.MODE_PEDOMETER_REALTIME:
                        pedometerHelper.setModePedometer(PedometerHelper.MODE_PEDOMETER_REALTIME);
                        break;
                    case PedometerHelper.MODE_PEDOMETER_PERIODIC:
                        pedometerHelper.setModePedometer(PedometerHelper.MODE_PEDOMETER_PERIODIC);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pedometerHelper.isStarted() == false){
                    pedometerHelper.start();
                }
                else {
                    pedometerHelper.stop();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop pedometer
        pedometerHelper.stop();
    }

    private String getStatus(int status) {
        String str = null;
        switch (status) {
            case SmotionPedometer.Info.STATUS_WALK_UP:
                str = "Walk Up";
                break;
            case SmotionPedometer.Info.STATUS_WALK_DOWN:
                str = "Walk Down";
                break;
            case SmotionPedometer.Info.STATUS_WALK_FLAT:
                str = "Walk";
                break;
            case SmotionPedometer.Info.STATUS_RUN_DOWN:
                str = "Run Down";
                break;
            case SmotionPedometer.Info.STATUS_RUN_UP:
                str = "Run Up";
                break;
            case SmotionPedometer.Info.STATUS_RUN_FLAT:
                str = "Run";
                break;
            case SmotionPedometer.Info.STATUS_STOP:
                str = "Stop";
                break;
            case SmotionPedometer.Info.STATUS_UNKNOWN:
                str = "Unknown";
                break;
            default:
                break;
        }
        return str;
    }

    @Override
    public void motionStarted() {
        btnStart.setText(R.string.stop);
        enableSpinner(false);
    }

    @Override
    public void motionStopped() {
        btnStart.setText(R.string.start);
        enableSpinner(true);
    }

    @Override
    public void updateInfo(SmotionPedometer.Info info) {
        SmotionPedometer.Info pedometerInfo = info;
        System.out.println("HelloMotion PedometerHelper");
        double calorie = info.getCalorie();
        double distance = info.getDistance();
        double speed = info.getSpeed();
        long count = info.getCount(SmotionPedometer.Info.COUNT_TOTAL);
        int status = info.getStatus();

        textCalorie.setText(String.valueOf(calorie));
        textDistance.setText(String.valueOf(distance));
        textSpeed.setText(String.valueOf(speed));
        textCount.setText(String.valueOf(count));
        textStatus.setText(getStatus(status));

        Log.d("Burn", "onChanged: "+ " calorie "+calorie+
                " distance"+distance+
                " speed"+speed+
                " count"+count+
                " status"+status);
    }

    void showErrorDialog(String title,String message){

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    void enableSpinner(boolean enabled){
        spinMode.setEnabled(enabled);
    }
}
