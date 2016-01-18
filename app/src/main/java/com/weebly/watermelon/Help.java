package com.weebly.watermelon;

/**
 * Created by gaby on 5/27/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.weebly.watermelon.R;


public class Help extends Activity {

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.help);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.help).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Toast toast = Toast.makeText(getApplicationContext(),"Option 1", Toast.LENGTH_SHORT);


        switch(item.getItemId()){
            case R.id.scan:
                toast.setText("Scan");
                toast.show();
                changeActivityToScan();
                return true;
      //      case R.id.maps:
         //       toast.setText("Map");
         //       toast.show();
        //        changeActivityToMap();
         //       return true;
            case R.id.help:
                toast.setText("Help");
                toast.show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void changeActivityToMap(){
        Intent intent = new Intent(Help.this, MapActivity.class);
        startActivity(intent);
    }
    private void changeActivityToScan(){
        Intent intent = new Intent(Help.this, MainActivity.class);
        startActivity(intent);

    }
}