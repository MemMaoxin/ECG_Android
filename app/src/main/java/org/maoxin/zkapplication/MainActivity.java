package org.maoxin.zkapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Queue;
import java.util.Random;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public ArrayList<BluetoothDevice> deviceArrayList;
    public ArrayList<Boolean> deviceConnectedArray;
    public BluetoothDevice connectedDevice;
    // 用于检查重复设备
    public Map<String ,BluetoothDevice> deviceMap;
    public DeviceListAdapter mDeviceListAdapter;
    BluetoothAdapter mBluetoothAdapter;
    ListView lvNewDevices;
    BluetoothGatt BG;
    Button discover_button;

    TextView valueView;
    TextView intervalView;
    Context context;
    TextView HR;
    private ArrayBlockingQueue<Integer> ECGdatas;
    private ArrayBlockingQueue<Integer> ECGdatasSave;
    int[] buffer_x={70000, 70000, 70000, 70000, 70000, 70000};
    int[] buffer_y={70000, 70000, 70000, 70000, 70000, 70000};
    int buffer_control=0;



    private Random random = new Random();

    int R_detect_flag=0;
    int []R_detect_array;
    int []R_interval;
    int detect_length=1600;
    int plot_control_mul=2;
    double[] hf_coff={4.97842E-05,-1.6073E-05,-4.38342E-05,-9.0004E-05,-0.000153298,-0.000230318,-0.000314971,-0.000398489,-0.000469973,-0.000517472,-0.000529423,-0.000496331,-0.000412407,-0.000276935,-9.50954E-05,0.000121915,0.000357604,0.000591691,0.000802295,0.000968525,0.00107317,0.00110508,0.00106089,0.000945738,0.000772936,0.000562427,0.000338361,0.000125981,-5.16857E-05,-0.000176926,-0.00023969,-0.000238881,-0.000182311,-8.54013E-05,3.12379E-05,0.000144963,0.00023462,0.000283763,0.000283099,0.000231661,0.00013688,1.33839E-05,-0.000120031,-0.000242959,-0.000337238,-0.000389688,-0.000394337,-0.000353272,-0.000276203,-0.000178694,-7.94472E-05,2.9522E-06,5.32201E-05,6.19903E-05,2.73595E-05,-4.48451E-05,-0.000142171,-0.000247778,-0.000343455,-0.000412854,-0.000444393,-0.000433287,-0.000382386,-0.000301629,-0.000206258,-0.000114059,-4.21593E-05,-3.92132E-06,-6.54742E-06,-4.97674E-05,-0.000125889,-0.00022117,-0.00031833,-0.000399692,-0.000450426,-0.000461245,-0.000430149,-0.000362831,-0.000271676,-0.000173472,-8.64117E-05,-2.67399E-05,-5.66692E-06,-2.74003E-05,-8.81832E-05,-0.000177021,-0.0002776,-0.000371292,-0.000440564,-0.000472258,-0.000460059,-0.000405741,-0.000318915,-0.000215329,-0.000114006,-3.37416E-05,1.03968E-05,9.91564E-06,-3.54469E-05,-0.000117579,-0.0002214,-0.000327588,-0.000416124,-0.000470027,-0.000478549,-0.000439269,-0.000358643,-0.000250928,-0.000135622,-3.38883E-05,3.53741E-05,5.90895E-05,3.25324E-05,-3.96701E-05,-0.000144263,-0.000261735,-0.000369865,-0.000447836,-0.000480153,-0.000459636,-0.000388857,-0.000279818,-0.000151863,-2.81216E-05,6.87087E-05,0.000120668,0.000117933,6.08078E-05,-4.01972E-05,-0.000166163,-0.000293185,-0.000396764,-0.000456395,-0.000459465,-0.000403747,-0.000297973,-0.000160372,-1.54059E-05,0.000110719,0.000194937,0.000221695,0.000185993,9.44788E-05,-3.5604E-05,-0.000179484,-0.000309383,-0.000399686,-0.000431767,-0.000397602,-0.00030144,-0.000159206,4.30025E-06,0.000159918,0.000279517,0.000341293,0.000333965,0.000259055,0.000130806,-2.62808E-05,-0.000181874,-0.000305429,-0.00037189,-0.000366495,-0.000287715,-0.000147738,2.95778E-05,0.000213015,0.000369718,0.000471346,0.000499459,0.000449139,0.000330132,0.000165249,-1.37043E-05,-0.000171755,-0.000277278,-0.000307988,-0.000255277,-0.000126043,5.8352E-05,0.000265863,0.000459606,0.000604713,0.000674904,0.000657563,0.000556324,0.000390649,0.00019242,1.50962E-07,-0.000148142,-0.00022209,-0.000204796,-9.62312E-05,8.64968E-05,0.000312086,0.000540686,0.000731258,0.000849261,0.000873244,0.000799081,0.000641031,0.000429332,0.000204786,1.12534E-05,-0.000112549,-0.000140389,-6.36667E-05,0.000106707,0.000342042,0.000600983,0.000837082,0.00100745,0.00108085,0.00104381,0.000903343,0.00068604,0.000433322,0.000193907,1.47609E-05,-6.77566E-05,-3.48957E-05,0.000110684,0.000345075,0.000627282,0.000906666,0.00113231,0.00126258,0.00127311,0.00116164,0.000948794,0.00067454,0.000390999,0.00015282,6.93962E-06,-1.63582E-05,9.06015E-05,0.000311087,0.000606724,0.000924384,0.00120594,0.00139911,0.00146741,0.00139722,0.00120072,0.000914022,0.00059069,0.000291984,7.54329E-05,-1.60388E-05,3.75807E-05,0.000229277,0.000525903,0.000874017,0.00120961,0.00147,0.00160562,0.00158958,0.00142303,0.00113543,0.000779412,0.000421231,0.00012853,-4.23778E-05,-5.72837E-05,8.91401E-05,0.000371887,0.00073985,0.00112504,0.0014551,0.00166687,0.00171843,0.00159726,0.00132311,0.00094477,0.000531317,0.000159446,-0.000100848,-0.000199552,-0.00011636,0.000135044,0.000509035,0.000936128,0.0013357,0.00163096,0.00176352,0.00170461,0.00146099,0.00107421,0.000613365,0.00016248,-0.000195056,-0.000392206,-0.000391161,-0.000190913,0.00017197,0.000629648,0.00109528,0.0014791,0.00170522,0.00172621,0.00153243,0.00115445,0.000657917,0.000131795,-0.000328076,-0.000636886,-0.000737016,-0.000609315,-0.000277325,0.000196479,0.000721825,0.00119721,0.00152882,0.00164837,0.00152629,0.00117802,0.000661752,6.8175E-05,-0.000496062,-0.0009282,-0.00114878,-0.00111701,-0.000839032,-0.000367587,0.000207202,0.000774182,0.00122177,0.00145895,0.00143281,0.00113906,0.00062371,-2.47923E-05,-0.000691529,-0.00125632,-0.00161627,-0.00170545,-0.00150797,-0.00106174,-0.0004523,0.000202544,0.000774355,0.0011481,0.00124405,0.00103341,0.000544639,-0.000140981,-0.000903717,-0.00160731,-0.00212435,-0.0023605,-0.00227276,-0.00187856,-0.00125346,-0.000517976,0.000184397,0.000713794,0.000960428,0.000865822,0.000434742,-0.00026456,-0.00111209,-0.00195777,-0.00264925,-0.00306051,-0.00311619,-0.00280663,-0.00219097,-0.00138724,-0.000551165,0.000152457,0.000580703,0.000639875,0.000304097,-0.000378518,-0.00129354,-0.00228053,-0.00316244,-0.00377866,-0.00401565,-0.00382924,-0.00325438,-0.00240002,-0.00143006,-0.00053352,0.00011063,0.000367443,0.000172336,-0.000455299,-0.00141404,-0.00253787,-0.00362621,-0.00448102,-0.00494422,-0.00492835,-0.0044346,-0.00355442,-0.00245424,-0.00134562,-0.000446322,6.06444E-05,6.23512E-05,-0.000461193,-0.0014312,-0.00268275,-0.00399399,-0.00512671,-0.00587093,-0.0060852,-0.00572491,-0.00485295,-0.0036304,-0.00228845,-0.0010864,-0.000263448,7.05746E-06,-0.000348085,-0.00128559,-0.00265096,-0.0042048,-0.00566592,-0.00676332,-0.00728765,-0.00713238,-0.00631662,-0.00498492,-0.00338331,-0.00181561,-0.000587673,5.03948E-05,-4.69781E-05,-0.000890329,-0.00234927,-0.00417302,-0.00603419,-0.00758839,-0.00853877,-0.00869352,-0.00800543,-0.00658532,-0.00468611,-0.00265964,-0.000893409,0.000261468,0.000557815,-9.7344E-05,-0.0016199,-0.00375832,-0.00613523,-0.00831414,-0.00987974,-0.0105172,-0.0100752,-0.00860142,-0.00634057,-0.00369663,-0.00116275,0.000768088,0.0016962,0.00139464,-0.000140363,-0.00267874,-0.00578933,-0.00891179,-0.0114562,-0.0129129,-0.0129523,-0.0114965,-0.00874675,-0.00516073,-0.00138027,0.00187893,0.00395757,0.00437993,0.00295426,-0.000173413,-0.00452491,-0.00935723,-0.0137831,-0.0169265,-0.018086,-0.0168787,-0.0133363,-0.0079323,-0.00153294,0.00472761,0.00962952,0.0120805,0.011325,0.00711448,-0.000194145,-0.00963208,-0.0197124,-0.0286155,-0.0344426,-0.0355015,-0.0305815,-0.0191755,-0.00161144,0.0209306,0.0465231,0.0727066,0.0967898,0.116191,0.128774,0.133132,0.128774,0.116191,0.0967898,0.0727066,0.0465231,0.0209306,-0.00161144,-0.0191755,-0.0305815,-0.0355015,-0.0344426,-0.0286155,-0.0197124,-0.00963208,-0.000194145,0.00711448,0.011325,0.0120805,0.00962952,0.00472761,-0.00153294,-0.0079323,-0.0133363,-0.0168787,-0.018086,-0.0169265,-0.0137831,-0.00935723,-0.00452491,-0.000173413,0.00295426,0.00437993,0.00395757,0.00187893,-0.00138027,-0.00516073,-0.00874675,-0.0114965,-0.0129523,-0.0129129,-0.0114562,-0.00891179,-0.00578933,-0.00267874,-0.000140363,0.00139464,0.0016962,0.000768088,-0.00116275,-0.00369663,-0.00634057,-0.00860142,-0.0100752,-0.0105172,-0.00987974,-0.00831414,-0.00613523,-0.00375832,-0.0016199,-9.7344E-05,0.000557815,0.000261468,-0.000893409,-0.00265964,-0.00468611,-0.00658532,-0.00800543,-0.00869352,-0.00853877,-0.00758839,-0.00603419,-0.00417302,-0.00234927,-0.000890329,-4.69781E-05,5.03948E-05,-0.000587673,-0.00181561,-0.00338331,-0.00498492,-0.00631662,-0.00713238,-0.00728765,-0.00676332,-0.00566592,-0.0042048,-0.00265096,-0.00128559,-0.000348085,7.05746E-06,-0.000263448,-0.0010864,-0.00228845,-0.0036304,-0.00485295,-0.00572491,-0.0060852,-0.00587093,-0.00512671,-0.00399399,-0.00268275,-0.0014312,-0.000461193,6.23512E-05,6.06444E-05,-0.000446322,-0.00134562,-0.00245424,-0.00355442,-0.0044346,-0.00492835,-0.00494422,-0.00448102,-0.00362621,-0.00253787,-0.00141404,-0.000455299,0.000172336,0.000367443,0.00011063,-0.00053352,-0.00143006,-0.00240002,-0.00325438,-0.00382924,-0.00401565,-0.00377866,-0.00316244,-0.00228053,-0.00129354,-0.000378518,0.000304097,0.000639875,0.000580703,0.000152457,-0.000551165,-0.00138724,-0.00219097,-0.00280663,-0.00311619,-0.00306051,-0.00264925,-0.00195777,-0.00111209,-0.00026456,0.000434742,0.000865822,0.000960428,0.000713794,0.000184397,-0.000517976,-0.00125346,-0.00187856,-0.00227276,-0.0023605,-0.00212435,-0.00160731,-0.000903717,-0.000140981,0.000544639,0.00103341,0.00124405,0.0011481,0.000774355,0.000202544,-0.0004523,-0.00106174,-0.00150797,-0.00170545,-0.00161627,-0.00125632,-0.000691529,-2.47923E-05,0.00062371,0.00113906,0.00143281,0.00145895,0.00122177,0.000774182,0.000207202,-0.000367587,-0.000839032,-0.00111701,-0.00114878,-0.0009282,-0.000496062,6.8175E-05,0.000661752,0.00117802,0.00152629,0.00164837,0.00152882,0.00119721,0.000721825,0.000196479,-0.000277325,-0.000609315,-0.000737016,-0.000636886,-0.000328076,0.000131795,0.000657917,0.00115445,0.00153243,0.00172621,0.00170522,0.0014791,0.00109528,0.000629648,0.00017197,-0.000190913,-0.000391161,-0.000392206,-0.000195056,0.00016248,0.000613365,0.00107421,0.00146099,0.00170461,0.00176352,0.00163096,0.0013357,0.000936128,0.000509035,0.000135044,-0.00011636,-0.000199552,-0.000100848,0.000159446,0.000531317,0.00094477,0.00132311,0.00159726,0.00171843,0.00166687,0.0014551,0.00112504,0.00073985,0.000371887,8.91401E-05,-5.72837E-05,-4.23778E-05,0.00012853,0.000421231,0.000779412,0.00113543,0.00142303,0.00158958,0.00160562,0.00147,0.00120961,0.000874017,0.000525903,0.000229277,3.75807E-05,-1.60388E-05,7.54329E-05,0.000291984,0.00059069,0.000914022,0.00120072,0.00139722,0.00146741,0.00139911,0.00120594,0.000924384,0.000606724,0.000311087,9.06015E-05,-1.63582E-05,6.93962E-06,0.00015282,0.000390999,0.00067454,0.000948794,0.00116164,0.00127311,0.00126258,0.00113231,0.000906666,0.000627282,0.000345075,0.000110684,-3.48957E-05,-6.77566E-05,1.47609E-05,0.000193907,0.000433322,0.00068604,0.000903343,0.00104381,0.00108085,0.00100745,0.000837082,0.000600983,0.000342042,0.000106707,-6.36667E-05,-0.000140389,-0.000112549,1.12534E-05,0.000204786,0.000429332,0.000641031,0.000799081,0.000873244,0.000849261,0.000731258,0.000540686,0.000312086,8.64968E-05,-9.62312E-05,-0.000204796,-0.00022209,-0.000148142,1.50962E-07,0.00019242,0.000390649,0.000556324,0.000657563,0.000674904,0.000604713,0.000459606,0.000265863,5.8352E-05,-0.000126043,-0.000255277,-0.000307988,-0.000277278,-0.000171755,-1.37043E-05,0.000165249,0.000330132,0.000449139,0.000499459,0.000471346,0.000369718,0.000213015,2.95778E-05,-0.000147738,-0.000287715,-0.000366495,-0.00037189,-0.000305429,-0.000181874,-2.62808E-05,0.000130806,0.000259055,0.000333965,0.000341293,0.000279517,0.000159918,4.30025E-06,-0.000159206,-0.00030144,-0.000397602,-0.000431767,-0.000399686,-0.000309383,-0.000179484,-3.5604E-05,9.44788E-05,0.000185993,0.000221695,0.000194937,0.000110719,-1.54059E-05,-0.000160372,-0.000297973,-0.000403747,-0.000459465,-0.000456395,-0.000396764,-0.000293185,-0.000166163,-4.01972E-05,6.08078E-05,0.000117933,0.000120668,6.87087E-05,-2.81216E-05,-0.000151863,-0.000279818,-0.000388857,-0.000459636,-0.000480153,-0.000447836,-0.000369865,-0.000261735,-0.000144263,-3.96701E-05,3.25324E-05,5.90895E-05,3.53741E-05,-3.38883E-05,-0.000135622,-0.000250928,-0.000358643,-0.000439269,-0.000478549,-0.000470027,-0.000416124,-0.000327588,-0.0002214,-0.000117579,-3.54469E-05,9.91564E-06,1.03968E-05,-3.37416E-05,-0.000114006,-0.000215329,-0.000318915,-0.000405741,-0.000460059,-0.000472258,-0.000440564,-0.000371292,-0.0002776,-0.000177021,-8.81832E-05,-2.74003E-05,-5.66692E-06,-2.67399E-05,-8.64117E-05,-0.000173472,-0.000271676,-0.000362831,-0.000430149,-0.000461245,-0.000450426,-0.000399692,-0.00031833,-0.00022117,-0.000125889,-4.97674E-05,-6.54742E-06,-3.92132E-06,-4.21593E-05,-0.000114059,-0.000206258,-0.000301629,-0.000382386,-0.000433287,-0.000444393,-0.000412854,-0.000343455,-0.000247778,-0.000142171,-4.48451E-05,2.73595E-05,6.19903E-05,5.32201E-05,2.9522E-06,-7.94472E-05,-0.000178694,-0.000276203,-0.000353272,-0.000394337,-0.000389688,-0.000337238,-0.000242959,-0.000120031,1.33839E-05,0.00013688,0.000231661,0.000283099,0.000283763,0.00023462,0.000144963,3.12379E-05,-8.54013E-05,-0.000182311,-0.000238881,-0.00023969,-0.000176926,-5.16857E-05,0.000125981,0.000338361,0.000562427,0.000772936,0.000945738,0.00106089,0.00110508,0.00107317,0.000968525,0.000802295,0.000591691,0.000357604,0.000121915,-9.50954E-05,-0.000276935,-0.000412407,-0.000496331,-0.000529423,-0.000517472,-0.000469973,-0.000398489,-0.000314971,-0.000230318,-0.000153298,-9.0004E-05,-4.38342E-05,-1.6073E-05,4.97842E-05};
    int[] ECG_buffer;
    int[] ECG_T;
    int buffer_x1[];
    int last_time_index=0;
    Button HRV_timer;
    int HRV_timer_cancel=0;
    TextView cd_timer;
    Button HRV_show;
    CreateUserDialog createUserDialog;
    int time_5_control=0;
    boolean sig_ok=false;


    String set_once=null;

    Button savetxt;
    public int record_enable=0;
    String filePath = "/sdcard/ECG/";
    String fileName = "data.txt";
    File file;
    RandomAccessFile raf=null;

    private int[] HRV;

    private int[] HRV_temp;
    int HRV_temp_control=0;
    int HRV_control=0;
    private int []HRV5;
    int HRV_5m_control=0;

    private ArrayList<String> HRV_timestamp;
    private ArrayList<Integer> mean;
    private ArrayList<Integer> StandardDiviation;
    private ArrayList<Integer> rMSSD;

    ImageButton user_info;

    protected void onDestroy() {
        super.onDestroy();
    }

    public int max(int[] Detect_array, int start,int end){
        int temp1=Detect_array[start];
        int temp2=Detect_array[start];
        int data_length=end-start;
        for(int i=1;i<data_length;i++){
            if(Detect_array[start+i]>temp1){
                temp2=temp1;
                temp1=Detect_array[start+i];
            }
            else if(Detect_array[start+i]<temp1&&Detect_array[start+i]>temp2){
                temp2=Detect_array[start+i];
            }
        }
        int temp[]={temp1,temp2};
        return  temp1;
    }
    public int[] max_pos(int[] Detect_array, int start,int end){
        int temp1=Detect_array[start];
        int pos1=start;
        int data_length=end-start;
        for(int i=1;i<data_length;i++){
            if(Detect_array[start+i]>temp1){
                temp1=Detect_array[start+i];
                pos1=start+i;
            }

        }
        int temp[]={temp1,pos1};
        return  temp;
    }

    public int mean_comp(int[] mean,int count){
        int sum=0;
        for(int i=0;i<count;i++){
            sum+=mean[i];
        }
        return sum;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HRV_timer_cancel=0;
        HRV_temp_control=0;

        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_main);
        this.context = this;

        valueView = findViewById(R.id.valueView);
        intervalView = findViewById(R.id.interval);
        lvNewDevices = findViewById(R.id.list);
        deviceArrayList = new ArrayList<>(128);
        deviceConnectedArray = new ArrayList<>(128);
        deviceMap = new ConcurrentHashMap<>();
        lvNewDevices.setOnItemClickListener(this);
        mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, deviceArrayList,deviceConnectedArray);
        lvNewDevices.setAdapter(mDeviceListAdapter);

        savetxt=findViewById(R.id.Record);
        discover_button=findViewById(R.id.scanButton);
        ECGdatas=new ArrayBlockingQueue<Integer>(1024);
        ECGdatasSave=new ArrayBlockingQueue<Integer>(1024);

        HR = findViewById(R.id.HR);
        R_detect_array=new int[detect_length];
        R_interval=new int[5];
        buffer_x1=new int[1000];
        for(int i=0;i<1000;i++){
            buffer_x1[i]=70000;
        }
        ECG_buffer=new int[1009];
        for(int i=0;i<1009;i++){
            ECG_buffer[i]=0;
        }
        ECG_T=new int[1009];
        for(int i=0;i<1009;i++){
            ECG_T[i]=0;
        }
        HRV=new int[600];
        for(int i=0;i<600;i++){
            HRV[i]=0;
        }
        HRV_temp=new int[600];
        for(int i=0;i<600;i++){
            HRV_temp[i]=0;
        }
        HRV5=new int[800];
        for(int i=0;i<800;i++){
            HRV5[i]=0;
        }
        mean= new ArrayList<>(200);
        HRV_timestamp= new ArrayList<>(200);

        StandardDiviation= new ArrayList<>(40);

        rMSSD= new ArrayList<>(40);

        cd_timer = findViewById(R.id.cd_timer);
        HRV_show = findViewById(R.id.HRV_report);
        HRV_show.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, hrv_analysis.class);
                //intent.putExtra("HRV",HRV);
                //intent.putExtra("HRV_control",HRV_control);
                intent.putExtra("HR",mean);
                intent.putExtra("HRV_timestamp",HRV_timestamp);
                intent.putExtra("SD",StandardDiviation);
                intent.putExtra("rMSSD",rMSSD);
                startActivityForResult(intent, 1);
            }
        });
        HRV_timer= findViewById(R.id.hrv_skip);
        HRV_timer.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View view) {
                  if(connectedDevice!=null){
                  if(HRV_timer_cancel==0)
                  {
                      if(sig_ok==true)
                      {
                          cdTimer.start();
                          cd_timer.setVisibility(View.VISIBLE);
                          HRV_timer.setText("Cancel");
                          HRV_timer_cancel=1;
                      }else{
                          Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                          toast.setText("请在心电质量较好时进行HRV监测及记录");
                          toast.setGravity(Gravity.CENTER,0,-10);
                          toast.show();
                      }

                  }
                  else{
                      cdTimer.cancel();
                      for(int i=0;i<600;i++){
                          HRV_temp[i]=0;
                      }
                      HRV_temp_control=0;
                      cd_timer.setVisibility(View.INVISIBLE);
                      HRV_timer_cancel=0;
                      {
                          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                          Date date = new Date(System.currentTimeMillis());
                          String time = sdf.format(date);

                          final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(context).builder()
                                  .setTitle("是否要保存本次HRV数据？\r\n若要保存，请输入文件名：")
                                  .setEditText(time)
                                  .setCancelable(false);

                          myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {

                                  String filePath = "/sdcard/ECG/HRV/";
                                  String fileName = myAlertInputDialog.getResult()+".txt";

                                  makeFilePath(filePath, fileName);

                                  String strFilePath = filePath + fileName;
                                  // 每次写入时，都换行写
                                  String strContent = "HRV\r\n";
                                  File file;
                                  try {
                                      file = new File(strFilePath);
                                      if (!file.exists()) {
                                          Log.d("TestFile", "Create the file:" + strFilePath);
                                          file.getParentFile().mkdirs();
                                          file.createNewFile();
                                      }
                                      RandomAccessFile raf=null;
                                      raf = new RandomAccessFile(file, "rw");
                                      raf.seek(file.length());
                                      raf.write(strContent.getBytes());
                                      for(int i=0;i<mean.size();i++){
                                          strContent = HRV_timestamp.get(i)+"  平均心率：  "+ mean.get(i).toString();
                                          if(i%5==4)
                                              strContent +="  SDNN：  "+(float)((StandardDiviation.get((int)(i/5)))/10)+"  rMSSD：  "+(float)(rMSSD.get((int)(i/5))/10);
                                          strContent +="\r\n";
                                          raf.write(strContent.getBytes());
                                      }
                                      raf.close();

                                  } catch (Exception e) {
                                      Log.e("TestFile", "Error on write File:" + e);
                                  }




                                  Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                                  toast.setText(myAlertInputDialog.getResult());
                                  toast.setGravity(Gravity.CENTER,0,-10);
                                  toast.show();
                                  myAlertInputDialog.dismiss();
                              }
                          }).setNegativeButton("取消", new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  myAlertInputDialog.dismiss();
                              }
                          });
                          myAlertInputDialog.show();
                      }
                      HRV_timer.setText("开始HRV监测");

                  }}
              }
          });
        user_info= findViewById(R.id.user_info);
        user_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showEditDialog(this);
            }
        });


        /*
        开启蓝牙，检查权限
         */
        boolean isGranted=true;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {


            if (this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
                isGranted=false;
            }else {
                Log.e("Tip", "checkPermission: 已经授权1！");
            }
            if (this.checkSelfPermission("Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS") != PERMISSION_GRANTED) {
                isGranted=false;
            }else {

                Log.e("Tip", "checkPermission: 已经授权2！");
            }
            if (this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION") != PERMISSION_GRANTED) {
                isGranted=false;
            }else {
                Log.e("Tip", "checkPermission: 已经授权3！");
            }
            if (this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION") != PERMISSION_GRANTED) {
                isGranted=false;
            }else {
                Log.e("Tip", "checkPermission: 已经授权4！");
            }

            if (isGranted != true) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},1001); //Any number
            }
        } else {
            Log.d("Tip", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int ECGdataT=0;
                Electrocardiogram electrocardiogram = findViewById(R.id.electrocardiogram1);
                int data_numbers=0;
                int x_axis=0;
               // electrocardiogram.electrocardDatas.clear();
                electrocardiogram.electrocarPath.reset();
                int redraw=0;

                int count=0;
                Date curDate;
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String dateStr ;
                String strContent=null ;
                int R_interval_index=0;

               while(true) {
                   try {

                       ECGdataT = ECGdatas.take();
                       curDate=new Date();
                       dateStr = sdf.format(curDate);
                       strContent = strContent + dateStr + "  " + ECGdataT + "\r\n";
                       count++;
                       if(count==20){
                           if(record_enable==1)
                           {
                               try{
                                   if (file.exists()&&raf!=null) {
                                       raf.write(strContent.getBytes());
                                   }

                               }catch(Exception e) {
                                   Log.e("TestFile", "Error on write File of ECG:" + e);
                               }

                           }
                           count=0;
                           strContent=null;
                       }

                        if(plot_control_mul>=1) {
                            float ECGdata = (float) (ECGdataT * 3.6 / 128);
                            data_numbers++;
                            x_axis = data_numbers % 816;
                            redraw++;
                            electrocardiogram.electrocardDatas.set(x_axis, ECGdata);
                            electrocardiogram.show_index = x_axis - 10;
                            if (redraw >= 8) {
                                electrocardiogram.invalidate();
                                redraw = 0;
                            }
                            plot_control_mul=0;
                        }
                        else{
                            plot_control_mul++;
                        }
                       if(R_detect_flag<detect_length){
                           R_detect_array[R_detect_flag]=ECGdataT;
                           R_detect_flag++;
                       }
                       else{
                           int []startR=new int [detect_length];
                           int []endR=new int [detect_length];
                           int []maxR=new int [detect_length];
                           int []posR=new int [detect_length];
                           int []interval=new int [detect_length];

                           int threshold=max(R_detect_array,0,detect_length-1);
                           threshold=threshold-Math.abs((int)(threshold*0.25));
                           int R_flag=0;
                           int k=0;

                           for(int i=0;i<detect_length;i++){
                               if(R_detect_array[i]>threshold&&R_flag==0){
                                   startR[k]=i;
                                   R_flag=1;
                               }else if (R_detect_array[i]<threshold&&R_flag==1){
                                   endR[k]=i;
                                   R_flag=0;
                                   int[] temp=max_pos(R_detect_array,startR[k],endR[k]);
                                   maxR[k]=temp[0];
                                   posR[k]=temp[1];

                                   k++;
                               }
                           }
                           int q=0;
                           int posPast=posR[0];
                           if(last_time_index!=0&&posR[0]+(detect_length-last_time_index)>200&&(posR[0]+(detect_length-last_time_index)<600)){
                               interval[q]=posR[0]+(detect_length-last_time_index);
                               q++;
                           }
                            if (k>0){
                                last_time_index=posR[k-1];
                            }
                           if(k<=8&&k>=2){
                               int R_number=k;
                               int i=0;
                               while (i<k-1){
                                   if(posR[i+1]-posPast<200||posR[i+1]-posPast>600) {
                                       posR[i+1]=-1;
                                       i++;
                                       R_number=R_number-1;
                                   }
                                   else{
                                       interval[q]=posR[i+1]-posPast;
                                       posPast=posR[i+1];
                                       q++;
                                   }
                                   i++;
                               }
                               if(R_number<7 &&R_number>=2)
                               {
                                   for(int p=0;p<q;p++) {
                                           R_interval[R_interval_index] = interval[p];
                                           int hr=(30720/R_interval[R_interval_index]);
                                           if(R_interval[4]!=0){
                                               HR.setText(hr+" bpm");
                                               sig_ok=true;
                                               if(HRV_timer_cancel==1)
                                                {
                                                    HRV_temp[HRV_temp_control]=interval[p];
                                                    HRV_temp_control++;
                                                }
                                               }
                                           else{
                                               HR.setText("no signal");
                                               sig_ok=false;
                                           }
                                           R_interval_index=(R_interval_index+1)%5;
                                   }
                                   //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                               }else{
                                   for(int p=0;p<5;p++){
                                       R_interval[p] = 0;
                                       HR.setText("no signal");
                                       sig_ok=false;
                                   }
                                   R_interval_index=0;
                                   //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                               }
                           }
                           if(k>8||k<2){
                               for(int p=0;p<5;p++){
                                   R_interval[p] = 0;
                                   HR.setText("no signal");
                                   sig_ok=false;
                               }
                               R_interval_index=0;
                               //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                           }


                           R_detect_flag=0;
                       }



                   }catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                }
                //electrocardiogram.startDraw();
            }
        }).start();
        new Thread(new Runnable(){
            @Override
            public void run(){
                int ECGdataSave=0;
                int count=0;
                Date curDate= new Date();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String dateStr ;
                String strContent=null ;
                while(true){
                    try{
                        ECGdataSave=ECGdatasSave.take();
                        dateStr = sdf.format(curDate);
                        strContent = strContent + dateStr + "  " + ECGdataSave + "\r\n";
                        count++;
                        if(count==20){
                            if(record_enable==1)
                            {
                                try{
                                    if (file.exists()&&raf!=null) {
                                        raf.write(strContent.getBytes());
                                    }

                                }catch(Exception e) {
                                    Log.e("TestFile", "Error on write File of ECG:" + e);
                                }

                            }
                            count=0;
                            strContent=null;
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }
    public void showEditDialog(View.OnClickListener view) {
        createUserDialog = new CreateUserDialog(this,21,onClickListener);
        createUserDialog.show();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_save_pop:

                    String name = createUserDialog.text_name.getText().toString().trim();
                    String age = createUserDialog.text_age.getText().toString().trim();
                    RadioButton rb = createUserDialog.findViewById(createUserDialog.text_sex.getCheckedRadioButtonId());
                    String sex="未填写";
                    if(rb!=null)
                        sex = rb.getText().toString();
                    if((rb==null)||(name.equals(""))||(age.equals("")))
                    {
                        Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                        toast.setText("信息填写不完整，请重填");
                        toast.setGravity(Gravity.CENTER,0,-10);
                        toast.show();
                    }
                    else{
                        String fileName1="user.txt";
                        Date curDate=new Date();
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String dateStr = sdf.format(curDate);
                        String strContent = "\r\n"+dateStr +"\r\n"+ "  姓名：" + name + "  年龄：" + age +"  性别："+ sex ;
                        writeTxtToFile(strContent, filePath, fileName1);
                    }
                    //intervalView.setText(name + "——" + mobile + "——" + sex);
                    try
                    {
                        createUserDialog.dismiss();
                    } catch(Exception e) {
                    }
                    break;
            }
        }
    };


    private CountDownTimer cdTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            cd_timer.setVisibility(View.VISIBLE);
            cd_timer.setText((millisUntilFinished / 1000) + " s");
        }

        @Override
        public void onFinish() {
            cd_timer.setText("0 s");
            cd_timer.setVisibility(View.INVISIBLE);
            HRV_show.setVisibility(View.VISIBLE);
            //HRV_timer.setText("开始HRV监测");
            //HRV_timer_cancel=0;
            for(int i=0;i<HRV_temp_control;i++){
                HRV[i]=HRV_temp[i];

                HRV5[HRV_5m_control++]=HRV_temp[i];
                HRV_temp[i]=0;
            }
            HRV_control=HRV_temp_control;
            HRV_temp_control=0;
            int mean1=mean(HRV,HRV_control);
            mean.add(mean1);

            Date curDate;
            curDate=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateStr = sdf.format(curDate);
            HRV_timestamp.add(dateStr);

            if(time_5_control<5)
            {
                time_5_control++;
            }
            else
            {
                time_5_control=0;
                double StandardDiviation1=10*StandardDiviation(HRV5,HRV_5m_control);
                StandardDiviation.add((int)StandardDiviation1);
                double rMSSD1=10*rMSSD_com(HRV5,HRV_5m_control);
                rMSSD.add((int)rMSSD1);

                HRV_5m_control=0;
            }
            cdTimer.start();
        }


    };
    boolean discover_enable=false;
    public void btnDiscover(View view) {
        if(discover_enable==false) {
            discover_enable = true;
            discover_button.setText("STOPSCAN");
            deviceArrayList.clear();
            deviceConnectedArray.clear();
            if (connectedDevice != null) {
                deviceArrayList.add(connectedDevice);
                deviceConnectedArray.add(true);
            }
            mDeviceListAdapter.notifyDataSetChanged();
            Log.d("Tip", "btnDiscover: Looking for unpaired devices.");

            Log.d("Tip", "enableDisableBT:disabling BT");
            ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.d("Tip", "Scan Complete");
                    super.onScanResult(callbackType, result);
                    BluetoothDevice bluetoothDevice = result.getDevice();
                    //if (deviceMap.containsKey(bluetoothDevice.getAddress())){
                    //    return;
                    //}
                    if (deviceArrayList.contains(bluetoothDevice)) {
                        return;
                    }
                    deviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
                    deviceArrayList.add(bluetoothDevice);
                    deviceConnectedArray.add(false);
                    mDeviceListAdapter.notifyDataSetChanged();

                }
            };
            Log.d("Tip", "Start scan");
            // 设置搜索条件
            List<ScanFilter> scanFilters = new LinkedList<ScanFilter>() {{
                add(new ScanFilter.Builder().setDeviceName("ECG").build());
            }};
            mBluetoothAdapter.getBluetoothLeScanner()
                    .startScan(scanFilters,
                            new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build(),
                            scanCallback);
            // 5
            // s 后停止扫描

            new Handler().postDelayed(() -> {
                Log.d("Tip", "Stop scan");
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                discover_enable = false;
                discover_button.setText("SCANBT");
            }, 5_000);
        }
        else{
            discover_enable = false;
            mBluetoothAdapter.cancelDiscovery();
            discover_button.setText("SCANBT");

        }
    }

    // 将字符串写入到文本文件中
    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    private void writeECGToFlie(int f){
        Date curDate= new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(curDate);
        String strContent = dateStr+"  "+f + "\r\n";
        //String strContent = f + "\r\n";
        try{
            if (file.exists()&&raf!=null) {
                raf.write(strContent.getBytes());
            }
        }catch(Exception e) {
            Log.e("TestFile", "Error on write File of ECG:" + e);
        }
    }
//生成文件

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

//生成文件夹

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }
    public void btnRecord(View view) throws FileNotFoundException {
        if(record_enable==0)
        {
            record_enable=1;
            savetxt.setText("StopRecord");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            fileName = time+".txt";

            Scanner sc=new Scanner(new FileReader("/sdcard/ECG/user.txt"));
            String line;
            while((sc.hasNextLine()&&(line=sc.nextLine())!=null)) {
                if (!sc.hasNextLine())
                    writeTxtToFile(line+"\r\n", filePath, fileName);
            }
            writeTxtToFile("ECG start", filePath, fileName);
        }else{
            record_enable=0;
            savetxt.setText("Record");
            file=null;
            try{
                raf.close();
            }
            catch(Exception e){}
        }
    }
    public int lastClick=0;

    int index;
    boolean index_flag;
    private class addViewsToList extends AsyncTask<Void, Void, Boolean> {
        protected void onPostExecute(Boolean result) {

            deviceConnectedArray.set(index, index_flag);
            mDeviceListAdapter.notifyDataSetChanged();
            if(index_flag==false)
                HRV_timer.setText("开始HRV监测");
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        mBluetoothAdapter.cancelDiscovery();
        {
            Log.d("Tip", "onItemClick: You Clicked on a device.");
            String deviceName = deviceArrayList.get(i).getName();
            String deviceAddress = deviceArrayList.get(i).getAddress();

            Log.d("Tip", "onItemClick: deviceName = " + deviceName);
            Log.d("Tip", "onItemClick: deviceAddress = " + deviceAddress);
            lastClick=i;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Log.d("Tip", "Trying to pair with " + deviceName);
                if(set_once == null) {
                    BG=deviceArrayList.get(i).connectGatt(this, true, new BluetoothGattCallback() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            Log.d("Tip", gatt.getDevice().getName() + ":" + status + " -> " + newState);
                            index = deviceArrayList.indexOf(deviceMap.get(gatt.getDevice().getAddress()));
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                Log.i("Tip", "Attempting to start service discovery:" +
                                        gatt.discoverServices());
                                connectedDevice = deviceArrayList.get(index);
                                set_once = deviceArrayList.get(index).getAddress();
                                valueView.setText("Connected successfully");
                                //deviceConnectedArray.remove(index);
                                //deviceConnectedArray.add(index,true);
                                index_flag=true;
                                //runOnUiThread(() -> mDeviceListAdapter.notifyDataSetChanged());
                                new addViewsToList().execute();

                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                Log.i("Tip", "Disconnected from GATT server.");
                                BG.close();
                                set_once = null;
                                connectedDevice = null;
                                //deviceConnectedArray.set(index, false);
                                //deviceConnectedArray.remove(index);
                                //deviceConnectedArray.add(index,false);
                                //runOnUiThread(() -> mDeviceListAdapter.notifyDataSetChanged());
                                index_flag=false;
                                new addViewsToList().execute();
                                valueView.setText("Disconnected");

                                cd_timer.setText("0 s");
                                cdTimer.cancel();
                                for(int i=0;i<600;i++){
                                    HRV_temp[i]=0;
                                }
                                HRV_temp_control=0;

                                cd_timer.setVisibility(View.INVISIBLE);
                                HRV_timer_cancel=0;
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            super.onServicesDiscovered(gatt, status);
                            if (status == BluetoothGatt.GATT_SUCCESS) {
                                for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                                    Log.d("Tip", bluetoothGattService.getUuid().toString());
                                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                                        Log.d("Tip", "   " + bluetoothGattCharacteristic.getDescriptors()
                                                + ":" + bluetoothGattCharacteristic.getUuid() + ":" + bluetoothGattCharacteristic.getWriteType());

                                        if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString("6e400004-b5a3-f393-e0a9-e50e24dcca9e"))) {
                                            Log.d("Tip", "find bluetoothGattDescriptor");
                                            for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                                                Log.d("Tip", "      " + bluetoothGattDescriptor.getUuid().toString());
                                            }
                                            Log.d("Tip", "Enable notify:" + gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true));


                                            BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                            gatt.writeDescriptor(descriptor);

                                        }
                                    }
                                }
                            } else {
                                Log.w("Tip", "onServicesDiscovered received: " + status);
                            }

                        }

                        int check = 0, i = 0, mark = 0;
                        byte[] payload = new byte[4];
                        byte paylength;
                        byte cs;
                        int k = 0;
                        int rawdata = 0;

                        @Override

                        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                            super.onCharacteristicChanged(gatt, characteristic);
                            int bytes_number = 0;
                            bytes_number = characteristic.getValue().length;
                            byte[] buffer = new byte[bytes_number];
                            buffer = characteristic.getValue();

                            int j = 0;

                            while (bytes_number > 0) {
                                bytes_number--;
                                // textBox1.AppendText(bytes_number.ToString() + Environment.NewLine);
                                byte temp = buffer[j++];

                                if (check == 0 && temp == -86) {
                                    check = 1;
                                    continue;
                                }
                                if (check == 1) {
                                    if (temp == -86) {
                                        check = 2;
                                        continue;
                                    } else {
                                        check = 0;
                                        continue;
                                    }
                                }
                                if (check == 2) {
                                    paylength = temp;
                                    if (paylength == 4) {
                                        check = 3;
                                        continue;
                                    } else {
                                        check = 0;
                                        continue;
                                    }
                                }
                                if (check >= 3) {
                                    if (i < 4) {
                                        payload[i++] = temp;
                                    } else {
                                        cs = temp;
                                        i = 0;
                                        check = 0;
                                        mark = 1;
                                    }
                                }

                                if (i == 0 && check == 0 && mark == 1) {
                                    int bytecount = 0;
                                    byte checksum = 0x00;
                                    byte code, length;

                                    for (int k = 0; k < 4; k++)
                                        checksum += payload[k];

                                    checksum = (byte) (~(int) (checksum & 0xFF));
                                    if (checksum != cs)
                                        continue;
                                    else {
                                        code = payload[bytecount++];
                                        if (((int) code & 0x80) != 0) length = payload[bytecount++];
                                        else length = 1;

                                        rawdata = payload[bytecount] * 256 + payload[bytecount + 1];
                                        if (rawdata >= 32768) rawdata = rawdata - 65536;

                                        System.arraycopy(ECG_buffer, 1, ECG_T, 0, 1008);
                                        ECG_T[1008] = rawdata;
                                        System.arraycopy(ECG_T, 0, ECG_buffer, 0, 1009);
                                        if (ECG_buffer[1008] != 0) {
                                            int filtered = 0;
                                            for (int i = 0; i < 1009; i++) {
                                                filtered += hf_coff[i] * ECG_buffer[i];
                                            }
                                            try {
                                                ECGdatas.put(filtered);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    mark = 0;
                                }
                            }

                        }
                    });
                }else if(set_once == deviceArrayList.get(i).getAddress()){
                    BG.disconnect();
                }
                else
                    {
                        Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                        toast.setText("请断开现有连接后开始新连接");
                        toast.setGravity(Gravity.CENTER,0,-10);
                        toast.show();
                    }
            }
        }
    }
    public int mean(int[] x,int m) {
        int hr_sum=0;
        for(int i=0;i<m;i++)
        {
            hr_sum+=x[i];
        }
        int hr=0;
        if(hr_sum!=0)
            hr=60*512*m/hr_sum;
        return hr;
    }
    public double StandardDiviation(int[] x,int m) {

        double sum = 0;
        for (int i = 0; i < m; i++) {//求和
            sum += (float)x[i]*1000/512;
        }
        double dAve = sum / m;//求平均值

        double dVar = 0;
        for (int i = 0; i < m; i++) {//求方差
            dVar += ((float)x[i]*1000/512 - dAve) * ((float)x[i]*1000/512 - dAve);
        }
        return Math.sqrt(dVar / m);
    }
    public double rMSSD_com(int[] x,int m) {

        double sum = 0;
        if(m>=2) {
            for (int i = 0; i < m - 1; i++) {
                sum += (float) (x[i + 1] - x[i]) * (x[i + 1] - x[i]);
            }

            sum = (float) sum * 15625 / 4096;
            return Math.sqrt(sum / (m - 1));
        }
            return 0;
    }
}