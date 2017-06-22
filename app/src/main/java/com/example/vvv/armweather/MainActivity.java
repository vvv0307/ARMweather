package com.example.vvv.armweather;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;

import android.view.Menu;

import android.view.MenuItem;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter blueToothAdapter;

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        /*filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);*/
        filter.addAction(BluetoothDevice.ACTION_FOUND);

        registerReceiver(bluetoothreciever,filter);
    }
     /*private BroadcastReceiver foundReciever = new BroadcastReceiver() {
        private List<String> bluetoothName = new ArrayList<>();
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = null;
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothName.add(device.getName());
                ListView lv =(ListView)findViewById(R.id.list_item);
                lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,R.layout.namelist,bluetoothName));
                TextView t = (TextView)findViewById(R.id.arm);
                t.setText(s + device.getName());
            }
        }
    };*/
    private BroadcastReceiver bluetoothreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s = null;
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_ON){
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    TextView t = new TextView(MainActivity.this);
                    t.setText("蓝牙已开启，请重新扫描设备");
                    t.setGravity(Gravity.CENTER_HORIZONTAL);
                    t.setPadding(10,130,10,10);
                    ad.setView(t);
                    ad.setPositiveButton("×", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog a = ad.create();
                    a.show();
                }
            }else if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage(device.getName());
                AlertDialog a = ad.create();
                a.show();
            }/*else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage("开始了");
                AlertDialog a = ad.create();
                a.show();
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.scan:
                blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
                //扫描蓝牙方法
                if(item.getTitle().toString()=="Scan" ){
                    if(blueToothAdapter.isEnabled()) {
                        item.setTitle("Stop");
                    }
                }else{
                    item.setTitle("Stop");
                    blueToothAdapter.cancelDiscovery();
                    break;
                }
                scanBlue();
                break;
            case R.id.about:
                //关于
                AlertDialog.Builder bd = new AlertDialog.Builder(this);
                bd.setTitle("关于");
                bd.setMessage("ARM气象站 App v1.0 \n 小组成员：\n李恺杰\n吴帆\n唐浩然\n张昊天");
                bd.setPositiveButton("×", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog a = bd.create();
                a.show();
                break;
            case R.id.date:
                //日期
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("日期");
                Date date = new Date();
                builder.setMessage(date.toString());
                builder.setPositiveButton("×", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog b = builder.create();
                b.show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // 如果请求被取消，则结果数组为空。
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setMessage("已经获取权限");
                    AlertDialog a = ad.create();
                    a.show();
                } else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setMessage("未获取到权限");
                    AlertDialog a = ad.create();
                    a.show();
                }
                return;
            }
        }
    }


    private void scanBlue(){
        //获取蓝牙适配器
        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否有蓝牙功能
        if(blueToothAdapter != null){
            //判断蓝牙是否开启
            if(!blueToothAdapter.isEnabled()) {
                TextView t = new TextView(this);
                t.setText("蓝牙未开启，是否允许开启蓝牙？");
                t.setGravity(Gravity.CENTER);
                t.setPadding(10,120,10,10);
                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setView(t);
                ab.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blueToothAdapter.enable();
                    }
                });
                ab.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog b = ab.create();
                b.show();


            }else {
                blueToothAdapter.startDiscovery();
                checkBlePermission();
            }
        }else{

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setMessage("该设备不支持蓝牙功能！");
        }

    }
    public void checkBlePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        } else {
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setMessage("正在申请权限");
        }
    }

    public void onDestroy(){

        super.onDestroy();
        unregisterReceiver(bluetoothreciever);
        /*unregisterReceiver(foundReciever);*/

    }

}
