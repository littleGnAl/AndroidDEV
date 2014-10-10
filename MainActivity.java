package com.yunmai.scale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umeng.analytics.MobclickAgent;
import com.xtremeprog.sdk.ble.BleGattCharacteristic;
import com.xtremeprog.sdk.ble.BleGattService;
import com.xtremeprog.sdk.ble.BleService;
import com.xtremeprog.sdk.ble.IBle;
import com.yunmai.scale.activity.FamilyManager;
import com.yunmai.scale.activity.MainListActivity;
import com.yunmai.scale.activity.SettingActivity;
import com.yunmai.scale.component.CustomGallery;
import com.yunmai.scale.component.CustomerCircle;
import com.yunmai.scale.component.CutomerTextView;
import com.yunmai.scale.component.CutomerTextViewDesc;
import com.yunmai.scale.component.CutomerTextViewMain;
import com.yunmai.scale.component.DisplayCircleView;
import com.yunmai.scale.component.FlipImageView;
import com.yunmai.scale.component.ReverseAnimation;
import com.yunmai.scale.component.SpinArcView;
import com.yunmai.scale.component.ReverseAnimation.InterpolatedTimeListener;
import com.yunmai.scale.component.SegmentedRadioGroup;
import com.yunmai.scale.component.UserChartView;
import com.yunmai.scale.component.YmDialogYesNo;
import com.yunmai.scale.config.ClientConfig;
import com.yunmai.scale.config.CompareChartDataModels;
import com.yunmai.scale.config.MainDataUtil;
import com.yunmai.scale.entity.BledeviceVo;
import com.yunmai.scale.entity.Data;
import com.yunmai.scale.entity.DataEntity;
import com.yunmai.scale.entity.ScalesSetTarget;
import com.yunmai.scale.entity.ScoreVo;
import com.yunmai.scale.entity.UserBase;
import com.yunmai.scale.entity.WeightBle;
import com.yunmai.scale.entity.WeightChart;
import com.yunmai.scale.entity.WeightInfo;
import com.yunmai.scale.entity.WeightOthers;
import com.yunmai.scale.service.AccountService;
import com.yunmai.scale.service.ScalesTargetService;
import com.yunmai.scale.service.ScoreService;
import com.yunmai.scale.service.WeightBaseService;
import com.yunmai.scale.service.WeightInfoService;
import com.yunmai.scale.utils.ByteUtils;
import com.yunmai.scale.utils.ClickUtils;
import com.yunmai.scale.utils.DataUtil;
import com.yunmai.scale.utils.DateHelper;
import com.yunmai.scale.utils.EnumBleDataType;
import com.yunmai.scale.utils.EnumBleOpt;
import com.yunmai.scale.utils.EnumBleUserOpt;
import com.yunmai.scale.utils.EnumChartType;
import com.yunmai.scale.utils.EnumSwitchDMQY;
import com.yunmai.scale.utils.FormulaUitl;
import com.yunmai.scale.utils.MathUtils;
import com.yunmai.scale.utils.StringHelper;
import com.yunmai.scale.utils.Tools;
import com.yunmai.scale.utils.UmengUtil;
import com.yunmai.scale.utils.WhatMainUtil;
import com.yunmai.scale.utils.YmComUtil;

@SuppressLint("DefaultLocale")
public class MainActivity extends BaseActivity implements OnViewChangeListener, OnClickListener,
		OnCheckedChangeListener, InterpolatedTimeListener {
	SettingActivity mSettingView;
	private boolean openToast = false;
	/**刷新简报延迟时间*/
	private final int showChartTime = 100;
	/**圆圈动画*/
	private boolean enableAnimate = false;
	
	/**页面一*/
	/**左右边圈字体大小Left*/
	private Typeface fontType;
	private boolean nowWeighting = false;
	private Animation desPopAni = null;
	private Display display = null;
	private HandlerThread hThread = null;
	private HandlerMsg mHandler = null;
	private int msgCount;
	private Thread ctlTthread = null;
	private boolean staticWeight = false;
	private final String TAG = MainActivity.class.getName();
	public static final int[] imgChange = new int[] { R.drawable.main_statushigh, R.drawable.main_statusnormal,
			R.drawable.main_statuslower };
	static final int NOTIFICATION_ID = 0x1123;
	private static AccountService mAccountService = null;
	private static List<Integer> userList = null;
	private static String userName = null;
	private float scoreTotal = 0.0f;
	private String scoreDesc = null;
	private boolean isShowScore;
	private WeightBle mWeightBle = null;
	//private LinearLayout llHiddenAnimi = null;
	private LinearLayout llShowAnimiRunBar = null;
	private LinearLayout linearLayoutHidden = null;
	private LinearLayout linearLayoutShow = null;
	private TextView txtViewNoWeightDesc = null;
	private float lastTargetValue;
	private boolean openTargetShow = false;
	private boolean openAnimi = true;
	
	// DisplayCircleView
	private DisplayCircleView mDisplayCircleView = null;
	private DisplayCircleView mDisplayCircleViewFront = null;
	private DisplayCircleView mDisplayCircleViewBack = null;
	private FlipImageView flipImageView = null;
	private FrameLayout mFrameLayout = null;
	private SpinArcView spinArcView = null;
	
	private boolean isRealTimeDisplay = false;
	
	private int realTimeAnimi = 1;
	public int stabilityEnter = 1;
	/**列表项底部图片*/
	private RelativeLayout rlViewTipBgMuscle = null;
	private RelativeLayout rlViewTipBgBone = null;
	private RelativeLayout rlViewTipBgMoisture = null;
	private RelativeLayout llViewTipBgBmr = null;
	private RelativeLayout rlViewTipBgBodyAge = null;
	/** TextNumber是否允许显示最新的数字。 */
	private boolean enableRefresh;
	//报文
	/**BLE 获取实时测量报文处理*/
	public static final String GET_TIME_LINE_DATA = "01";
	/**BLE 稳定测量报文处理*/
	public static final String GET_OK_WEIGHT_DATA = "02";
	/**BLE 获取称用户列表*/
	public static final String GET_USER_LIST = "03";

	private Map<String, WeightInfo> wiMap;
	//列表项
	private LinearLayout rlleftimgBtnBmiCover = null;
	private LinearLayout llimgBtnFatCover = null;
	private TableLayout tLayoutMuscle = null;
	private TableLayout tLayoutBone = null;
	private TableLayout tLayoutMoisture = null;
	private TableLayout tLayoutBmri = null;
	private TableLayout tLayoutBodyAge = null;
	//BMI>>Muscle>>
	private TextView tViewTargeInfoMuscle = null;
	private TextView tViewTargeInfoBone = null;
	private TextView tViewTargeInfoMoisture = null;
	private TextView tViewTargeInfoBmr = null;
	private TextView txtViewTargeInfoSomaAge = null;

	private TextView txtViewSomaAge = null;
	//private TextView txtViewSomaAgeExt = null;
	private TextView txtViewBmi = null;
	private TextView txtViewFat = null;
	private TextView txtViewMuscle = null;
	private TextView txtViewBone = null;
	private TextView txtViewMoisture = null;
	private TextView txtViewBmr = null;
	//
	private CustomScrollLayout mScrollLayout = null;
	private LinearLayout[] mImageViews = null;
	private int mViewCount;
	private int mCurSel;// 标识当前tab
	//自定义文本
	private CutomerTextView customerTextViewLeft = null;
	private CutomerTextView customerTextViewRight = null;
	private LinearLayout linearLayoutContent = null;
	private CutomerTextViewDesc main_tv_show = null;
	private CutomerTextViewMain weight_info = null;
	//private CutomerTextView customerTextViewTop = null;
	//private TextView main_info_middle = null;
	//private TextView main_info_unit = null;
	private RelativeLayout rlBannerShowHidden = null;
	// 标题栏添加、设置下拉列表
	private ImageView setImageView = null;
	// 标题栏添加、设置下拉列表方法
	DropDownSetPopupMenu ddspMenu = null;
	// Tab 导航文字
	private TextView tabOne = null;
	private TextView tabTwo = null;
	private TextView tabThree = null;
	// PageOne
	private ScoreService scoreService = null;//评分
	private ScoreVo mScoreVo = null;
	private ImageView addFamily;

	/**页面二*/
	private AutoCompleteTextView mAutoCompleteTextView = null;
	private ImageView btn_calendar_previous = null;
	private ImageView btn_calendar_next = null;
	private UserChartView userCharView = null;
	private SegmentedRadioGroup segmentText = null;
	private TextView txtVieWeightUnit = null;
	Button BT_Add = null;
	Timer mTimer = null;
	List<HashMap<Float, Float>> list;
	Tools tool = new Tools();
	//底部架圈数量设置
	private CustomerCircle customerCircleLeft = null;
	private CustomerCircle customerCircleCenter = null;
	private CustomerCircle customerCircleRight = null;

	private TextView txtViewSetTargetWeight = null;
	private TextView txtViewTargetWeight = null;
	private TextView txtViewTargetUnit = null;
	private TextView txtViewTargetMsg = null;
	private TextView txtViewTargetTip = null;
	//private CutomerTargetTextView txtViewTargetDesc;
	private TextView txtViewTargetDesc = null;
	private ImageView iv_reduce_weight = null;
	private ImageView iv_keep_weight = null;
	private ImageView iv_add_weight = null;

	private float weekWeight;
	private static int changeCalendar = 0;//周
	private static int changeMonth = 0; // 月
	private static int quarterDay = 0; // 季
	private static int changeYear = 0; // 年
	private EnumSwitchDMQY chartDateType = EnumSwitchDMQY.SWITCH_WEEK;
	/**页面三*/
	private float fistTarget;
	private float changeTarget;
	private float targetSetting;
	private ScalesSetTarget setTargetWeight = null;
	private ScrollDataAdapter pAdapterKg = null;
	private ScrollDataAdapter pAdapterJing = null;
	private ScrollDataAdapter pAdapterLb = null;
	private static ArrayList<Data> listDataKg = null;
	private static ArrayList<Data> listDataJing = null;
	private static ArrayList<Data> listDataLb = null;
	private CustomGallery mGallery = null;
	private static int scrollRangleKg = 150;
	private static int scrollRangleJing = 300;
	private static int scrollRangleLb = 330;

	private WeightBaseService weightBaseService = null;
	private ScalesTargetService mScalesTargetService = null;
	private ScalesSetTarget currTarget = null;
	private DataEntity<ScalesSetTarget> dataSetTarget = null;
	private ToggleButton tb_setTarget_weight = null;
	private short targetWeightType = 2;
	private float lastWeight = 0.0f;
	private int targetWeightSelectIndex;
	private float nowOffset;
	private String offsetWeight = null;
	private UserBase mUserBase = null;
	private UserBase globalUser = null;
	private UserBase firstUserBase = null;
	/**BLE 初始化部分*/
	private UserBase delSubUser = null;
	/**是否扫描 请求打开蓝牙*/
	private static final int REQUEST_ENABLE_BT = 1;
	/**是否扫描 过期时间*/
	private static final long SCAN_PERIOD_TIME = 5000;
	/**定时发送用户信息*/
	private static final long MAIN_PERIOD_TIME = 1000;

	private BaseBleActivity baseApp = null;
	/**是否扫描*/
	private boolean mScanning;
	private boolean mConnected = false;
	private ArrayList<ArrayList<BleGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BleGattCharacteristic>>();
	protected BleService mService = null;
	protected IBle mBle = null;
	private Map<String, BledeviceVo> mapBle;
	private BleGattCharacteristic sendBGCharacteristic = null;
	private BleGattCharacteristic readBGCharacteristic = null;
	private String mDeviceName = null;
	private String mDeviceAddress = null;
	byte[] WriteBytes = new byte[20];
	//要保存的数据对象
	private WeightInfo mWeightInfo = null;
	private WeightOthers weightOthers = null;
	private float baseWeight = 0.0f;
	// 体检结果列表
	//private ListView main_lv01;
	private WeightChart lastChart = null;
	DataEntity<Integer> dataVo = null;
	private List<WeightInfo> listCurrUser = null;
	private WeightInfoService wifService = null;
	private List<WeightOthers> listOther = null;
	@SuppressWarnings("unused")
	private static int currPosition = 0;
	static {
		listDataKg = Data.getList(scrollRangleKg);
		listDataJing = Data.getList(scrollRangleJing);
		listDataLb = Data.getList(scrollRangleLb);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			Log.d(TAG, "onCreate");
			super.onCreate(savedInstanceState);
			netWorkStrictMode();
			/**7、初始加载友盟统计 配置*/
			UmengUtil.initSetUMengCfg(this);
			hThread = new HandlerThread("MainThread");
			hThread.start();
			mHandler = new HandlerMsg(hThread.getLooper());
			fontType = getTypeFaceNum(this);
			/**1、装载布局*/
			setContentView(R.layout.activity_main);
			//Context mContext = getBaseContext();
			//String path = mContext.getDatabasePath(ClientConfig.DB_NAME).getPath();
			//loadFile(mContext, path);
			//Log.d("INFO", "新数据库路径："+path);
			/**全局用户*/
			globalUser = getCurrentUser();
			init();
			/**2、加载主页*/
			loadMainList();
			/**设置字体*/
			setTypeFace();
			/**3、加载简报*/
			loadPresentation();
			/**4、加载目标*/
			loadTargetPage();
			/**6、装载页面对象*/
			BaseActivityManager.getInstance().addActivity(MainActivity.class.getName(), MainActivity.this);
			BaseBleActivity.setmHandler(mHandler);//用于页面间设置单位 
			
		} catch (Exception e) {
			MobclickAgent.reportError(getApplication(), e);
		}
	}

	public static String DB_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yunmai/";

	public static void loadFile(Context context, String file) throws IOException {
		InputStream is = new FileInputStream(new File(file));
		File filex = new File(DB_DIR, ClientConfig.DB_NAME);
		FileOutputStream fos = new FileOutputStream(filex);
		Log.d("INFO", "拷贝至：" + filex.getAbsolutePath());
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = is.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
		}
		fos.close();
		is.close();
	}

	private void init() {
		mAccountService = new AccountService(MainActivity.this);
		mScalesTargetService = new ScalesTargetService(this);
		weightBaseService = new WeightBaseService(this);
		wifService = new WeightInfoService(this);
		display = getWindowManager().getDefaultDisplay();
	}

	/**
	 * //处理由发射服务的各种活动。
	 * BLE_NOT_SUPPORTED  :蓝牙设备不支持
	 * BLE_DEVICE_FOUND :发现设备
		BLE_GATT_CONNECTED ：连接到服务器的关贸总协定。
		BLE_GATT_DISCONNECTED ：从关贸总协定服务器断开连接。
		BLE_GATT_SERVICES_DISCOVERED ：发现关贸总协定的服务。
		BLE_DATA_AVAILABLE ：从该设备接收数据。这可能是一个
	 * */
	private final BroadcastReceiver mBleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
			// 4. 处理 service 发过来的广播 
			// 发现设备
			if (BleService.BLE_DEVICE_FOUND.equals(action)) {
				updateConnectionState();
				BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
				bleConnect(device);
				return;
			}

			// 设备已连接
			if (BleService.BLE_GATT_CONNECTED.equals(action)) {
				BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
				Log.i("BLE", "设备已连接  Address:" + device.getAddress() + " name:" + device.getName());
				if (device != null) {
					setThreeTime(300);
					Log.i("BLE", " 发送数据验证 ");
					Log.i("BLE", " 发送数据验证 ");
					//获取连接设备的服务和特性  第一次
					getBleConnectCharacteristics(mBle.getServices(mDeviceAddress));
					if (sendBGCharacteristic == null) {
						//如果获取特征失败，第二次在次获取
						setThreeTime(200);
						getBleConnectCharacteristics(mBle.getServices(mDeviceAddress));
					}
					if (sendBGCharacteristic == null) {
						//如果获取特征失败，第三次在次获取
						setThreeTime(200);
						getBleConnectCharacteristics(mBle.getServices(mDeviceAddress));
					}
					if (sendBGCharacteristic == null) {
						//如果获取特征失败，第四次在次获取
						setThreeTime(200);
						getBleConnectCharacteristics(mBle.getServices(mDeviceAddress));
					}
					if (sendBGCharacteristic != null) {
						bleDataProcessing(EnumBleDataType.SEND_GETDATA, EnumBleOpt.DATA_SEND);
						if (!mConnected) {
							bleDataProcessing(EnumBleDataType.SEND_GETDATA, EnumBleOpt.DATA_SEND);
						}
						if (!mConnected) {
							bleDataProcessing(EnumBleDataType.SEND_GETDATA, EnumBleOpt.DATA_SEND);
						}
						if (!mConnected) {
							bleDataProcessing(EnumBleDataType.SEND_GETDATA, EnumBleOpt.DATA_SEND);
						}
						setConnectStatus(mConnected);
						setThreeTime(150);
						bleDataProcessing(EnumBleDataType.SEND_SET_BLE_USER_INFO, EnumBleOpt.DATA_SEND);
						setThreeTime(150);
						bleDataProcessing(EnumBleDataType.SEND_UPDATETIME, EnumBleOpt.DATA_SEND);
					} else {
						mConnected = false;
					}
					setConnectStatus(true);
					updateConnectionState();
					if (mConnected && mScanning) {
						mBle.stopScan();
						mScanning = false;
					}

				}
				return;
			}

			// 发现设备服务
			if (BleService.BLE_SERVICE_DISCOVERED.equals(action)) {
				String address = extras.getParcelable(BleService.EXTRA_ADDR);
				if (StringUtils.isNotEmpty(address) && address.equals(mDeviceAddress)) {
					Log.d("BLE", " 发现设备服务：");
					ArrayList<BleGattService> services = mBle.getServices(address);
				} else {
					Log.d("BLE", " 发现设备服务B：" + address);
				}
				return;
			}
			if (BleService.BLE_GATT_DISCONNECTED.endsWith(action)) {
				setConnectStatus(false);
				onDeviceDisconnected();
				Log.i("BLE", "设备断开连接");
				return;
			}
			if (BleService.BLE_CHARACTERISTIC_READ.equals(action)
					|| BleService.BLE_CHARACTERISTIC_CHANGED.equals(action)) {
				Log.i("BLE", "读取数据  success!");
				//读取数据
				byte[] val = extras.getByteArray(BleService.EXTRA_VALUE);
				//String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				if (null != val && val.length > 0) {
					//resetDisplayCircle();
					displayData(ByteUtils.byteToStr(val));
				}
				setConnectStatus(true);
				updateConnectionState();
			} else if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
				//发送数据 
				Log.i("BLE", "发送数据  success!");
				setConnectStatus(true);
				updateConnectionState();
			} else if (BleService.BLE_CHARACTERISTIC_NOTIFICATION.equals(action)) {
				Log.i("BLE", "通知状态变了 BLE_CHARACTERISTIC_NOTIFICATION");
			} else if (BleService.BLE_CHARACTERISTIC_INDICATION.equals(action)) {
				Log.i("BLE", "通知状态变了 BLE_CHARACTERISTIC_INDICATION");
			} else if (BleService.BLE_STATUS_ABNORMAL.equals(action)) {
				//				setConnectStatus(false);
				//				onDeviceDisconnected();
				//				BluetoothDevice device = extras.getParcelable(BleService.EXTRA_DEVICE);
				//				bleConnect(device);
				Log.i("BLE", "硬件状态出现错误");
			} else if (BleService.BLE_REQUEST_FAILED.equals(action)) {
				Log.i("BLE", "硬件蓝牙设备请求出错误");
			}

		}

		private void setConnectStatus(boolean isConnect) {
			mConnected = isConnect;
			if (baseApp != null) {
				baseApp.setMConnectedBle(mConnected);
			} else {
				baseApp = (BaseBleActivity) getApplication();
				baseApp.setMConnectedBle(mConnected);
			}
		}

		private void bleConnect(BluetoothDevice device) {
			if (device != null && device.getName().contains(ClientConfig.DEVICENAME)) {
				mDeviceName = device.getName();
				mDeviceAddress = device.getAddress();
				BaseBleActivity.setmDeviceAddress(mDeviceAddress);
				mapBle.put(mDeviceAddress, new BledeviceVo(device.getName(), mDeviceAddress, null, device));
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						boolean isOk = false;
						if (ClientConfig.isDebug) {
							if (ClientConfig.debugDeviceAddress.contains(mDeviceAddress)) {
								isOk = mBle.requestConnect(mDeviceAddress);
								Log.i("BLE", "conn 开始连接成功 isDebug。" + (isOk ? "conn OK" : "conn err"));
							} else {
								Log.i("BLE", "not found Ble device。");
							}
						} else {
							isOk = mBle.requestConnect(mDeviceAddress);
							Log.i("BLE", "conn 开始连接成功。" + (isOk ? "conn OK" : "conn err"));
						}
					}
				});
			}
		}
	};

	private boolean bleDataProcessing(EnumBleDataType type, EnumBleOpt opt) {
		if (readBGCharacteristic == null && sendBGCharacteristic == null) {
			Log.i("BLE", "readBGCharacteristic or sendBGCharacteristic is null");
			readBGCharacteristic = BaseBleActivity.getReadBGCharacteristic();
			sendBGCharacteristic = BaseBleActivity.getSendBGCharacteristic();
			if (readBGCharacteristic == null && sendBGCharacteristic == null) {
				return false;
			}
		}
		try {
			byte[] resp = null;
			switch (type) {
			//时间更新
			case SEND_UPDATETIME:
				resp = MainDataUtil.bleGetSendUpdateTime();
				Log.d(TAG, "连接上蓝牙设备后，校准时间");
				break;
			//数据请求协议  通知要建立连接
			case SEND_GETDATA:
				resp = ByteUtils.getBTBData();
				break;
			//响应数据已收到
			case SEND_SET_RESPRECDATA:
				resp = ByteUtils.respBTBRecData();
				break;
			//设置单位
			case SEND_SET_USER_UNIT:
				resp = ByteUtils.setUserInfo(EnumBleUserOpt.USER_ADD_OR_UPDATE.getVal(), getCurrentUser());//0x01 更新,新增  
				Log.d(TAG, " 设置用户信息  UNIT");
				break;
			//发用户数据到蓝牙设备
			case SEND_SET_BLE_USER_INFO:
				//设置用户信息数据到称重模块
				mAccountService = new AccountService(this);
				resp = MainDataUtil.bleSetBaseWeight(getCurrentUser(), mAccountService, baseWeight);
				setEditUnit(false);
				Log.d(TAG, "连接上蓝牙设备后，设置用户信息");
				break;
			//删除秤用户 
			case SEND_SET_DEL_USER:
				resp = ByteUtils.setUserInfo(EnumBleUserOpt.USER_DELETE.getVal(), getCurrentUser());
				break;
			//删除秤子用户 
			case SEND_SUB_DEL_USER:
				if (null != delSubUser) {
					resp = ByteUtils.setUserInfo(EnumBleUserOpt.USER_DELETE.getVal(), delSubUser);
				}
				break;
			//恢复出厂设置delSubUser
			case SEND_SET_BLE_RESET_DEVCE:
				resp = ByteUtils.respBTBReset();
				break;
			//请求用户列表 
			case SEND_GET_BLE_USER_LIST:
				resp = ByteUtils.getUserList();
				break;
			//回应秤收到称量完成数据
			case SEND_BACK_BLE_GET_DATA_OK:
				resp = ByteUtils.respBTBRecFinish();
				break;
			//内屏
			case SEND_SET_CHECK_CONNECT:
				resp = ByteUtils.checkConnect();
				break;
			default:
				break;
			}
			//如果为空，从全局中获取
			if (null == mDeviceAddress) {
				mDeviceAddress = BaseBleActivity.getmDeviceAddress();
			}
			if (opt == EnumBleOpt.DATA_SEND && resp != null) {
				return sendBleData(resp, type);
			} else {
				mBle.requestReadCharacteristic(mDeviceAddress, readBGCharacteristic);
				byte[] val = readBGCharacteristic.getValue();
				mBle.requestCharacteristicNotification(mDeviceAddress, readBGCharacteristic);
			}
		} catch (Exception e) {
			MobclickAgent.reportError(getApplication(), e);
			MobclickAgent.reportError(getApplication(), type.getName() + e.getMessage());
		}
		return true;
	}

	private boolean sendBleData(byte[] resp, EnumBleDataType type) {
		sendBGCharacteristic.setValue(resp);
		mConnected = mBle.requestWriteCharacteristic(mDeviceAddress, sendBGCharacteristic, "");
		boolean isNotfi = mBle.requestCharacteristicNotification(mDeviceAddress, readBGCharacteristic);
		if (type == EnumBleDataType.SEND_GETDATA) {
			mConnected = mConnected && isNotfi;
			Log.i("BLE", mConnected ? "Connect 确认身份【成功】" : "确认身份【失败】");
		} else {
			//Log.i("BLE", mConnected ? "Connect 确认身份【成功】" : "确认身份【失败】");
		}
		return true;
	}

	/**
	 *  获取当前连接的BLE Characteristics
	 *void 
	*/
	@SuppressLint("DefaultLocale")
	private void getBleConnectCharacteristics(List<BleGattService> gattServices) {
		if (mBle == null || gattServices == null) {
			Log.d("BLE", "mBle or gattServices null ");
			return;
		}
		for (BleGattService gattService : gattServices) {
			final String serviceUUID = gattService.getUuid().toString().substring(0, 8);
			Log.d("BLE", serviceUUID);
			if (!serviceUUID.contains(ClientConfig.BLE_SEND_DEVICEUUID)) {
				continue;
			}
			List<BleGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			for (BleGattCharacteristic characteristic : gattCharacteristics) {
				final String characteristicUUID = characteristic.getUuid().toString().substring(0, 8);//0000ffe4
				Log.d("BLE", characteristicUUID);
				if (!characteristicUUID.contains(ClientConfig.BLE_SEND_CHARACTUUID)) {
					continue;
				}
				if (characteristicUUID.equalsIgnoreCase(ClientConfig.BLE_SEND_CHARACTUUID)) {
					Log.d("BLE", "serverce UUID:" + gattService.getUuid().toString());
					Log.d("BLE", "charcter UUID:" + characteristic.getUuid().toString());
					sendBGCharacteristic = characteristic;
					BaseBleActivity.setSendBGCharacteristic(sendBGCharacteristic);
					break;
				}
			}
		}

		for (BleGattService gattService : gattServices) {
			final String serviceUUID = gattService.getUuid().toString().substring(0, 8);
			if (!serviceUUID.contains(ClientConfig.BLE_READ_DEVICEUUID)) {
				continue;
			}
			List<BleGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
			for (BleGattCharacteristic characteristic : gattCharacteristics) {
				final String characteristicUUID = characteristic.getUuid().toString().substring(0, 8);//0000ffe4
				if (!characteristicUUID.contains(ClientConfig.BLE_READ_CHARACTUUID)) {
					continue;
				}

				if (characteristicUUID.equalsIgnoreCase(ClientConfig.BLE_READ_CHARACTUUID)) {
					Log.d("BLE", "serverce UUID:" + gattService.getUuid().toString());
					Log.d("BLE", "charcter UUID:" + characteristic.getUuid().toString());
					readBGCharacteristic = characteristic;
					//存入全局变量
					BaseBleActivity.setReadBGCharacteristic(readBGCharacteristic);
					break;
				}
			}
		}
	}

	public static final UUID CHARACTER_COMMON_WRITE = UUID.fromString("00008a81-0000-1000-8000-00805f9b34fb");

	//	private final ServiceConnection mServiceConnection = new ServiceConnection() {
	//		@Override
	//		public void onServiceConnected(ComponentName className, IBinder rawBinder) {
	//			// 1. 绑定 service 成功，获取 mBle 对象
	//			mService = ((BleService.LocalBinder) rawBinder).getService();
	//			mBle = mService.getBle();
	//		}
	//
	//		@Override
	//		public void onServiceDisconnected(ComponentName classname) {
	//			mService = null;
	//			mBle = null;
	//		}
	//	};

	private void setTypeFace() {
		//main_info_middle.setTypeface(fontType);
		//main_info_middle.getPaint().setFakeBoldText(true);
		tViewTargeInfoMuscle.setTypeface(fontType);
		tViewTargeInfoMuscle.getPaint().setFakeBoldText(true);
		tViewTargeInfoBone.setTypeface(fontType);
		tViewTargeInfoBone.getPaint().setFakeBoldText(true);
		tViewTargeInfoMoisture.setTypeface(fontType);
		tViewTargeInfoMoisture.getPaint().setFakeBoldText(true);
		tViewTargeInfoBmr.setTypeface(fontType);
		tViewTargeInfoBmr.getPaint().setFakeBoldText(true);
		txtViewSomaAge.setTypeface(fontType);
		txtViewSomaAge.getPaint().setFakeBoldText(true);
	}
	
	class HandlerMsg extends Handler {
		public HandlerMsg(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what == WhatMainUtil.WHAT_SAVE_MY_WDATA) {//保存自己的历史数据
					dataVo = wifService.saveBatchWeight(listCurrUser, getCurrentUId());
					listCurrUser.clear();
				} else if (msg.what == WhatMainUtil.WHAT_SAVE_OTHER_WDATA) {//保存别人的历史数据
					dataVo = wifService.saveBatchWeightOthers(listOther, getCurrentUId());
					listOther.clear();
				} else if (msg.what == WhatMainUtil.WHAT_CHART_REFRESH) {//切换页面时，查询报表数据
					if (openToast) {
						toastCenter("刷新报表数据!");
					}
					reLoadCharView(changeCalendar);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_WEEK) {//点击周按钮时，查询报表
					reLoadCharView(changeCalendar);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_MONTH) {//点击月按钮时，查询报表
					reLoadCharView(changeMonth);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_QUARTER) {//点击季按钮时，查询报表
					reLoadCharView(quarterDay);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_YEAR) {//点击年按钮时，查询报表
					reLoadCharView(changeYear);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_Left_WEEK) {//选中周，向左移动时，查询报表
					reLoadCharView(changeCalendar);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_Left_MONTH) {//选中月，向左移动时，查询报表
					reLoadCharView(changeMonth);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_Left_QUARTER) {//选中季，向左移动时，查询报表
					reLoadCharView(quarterDay);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_Left_YEAR) {//选中年，向左移动时，查询报表
					reLoadCharView(changeYear);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_MOVE_WEEK) {//选中周，向右滑动是触发
					reLoadCharView(changeCalendar);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_MOVE_MONTH) {//选中月，向右滑动是触发
					reLoadCharView(changeMonth);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_MOVE_QUARTER) {//选中季，向右滑动是触发
					reLoadCharView(quarterDay);
				} else if (msg.what == WhatMainUtil.WHAT_CHART_MOVE_YEAR) {//选中年，向右滑动是触发
					reLoadCharView(changeYear);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_DATA) {
					//BLE 发送数据给BLE  通知蓝牙建立连接
					bleDataProcessing(EnumBleDataType.SEND_GETDATA, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_USER_INFO) {
					//BLE 设置用户信息  
					bleDataProcessing(EnumBleDataType.SET_BLE_USERINFO, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_USER_UNIT) {
					//BLE 设置用户单位 
					bleDataProcessing(EnumBleDataType.SEND_SET_USER_UNIT, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_DEL_USER) {
					//BLE删除用户操作
					bleDataProcessing(EnumBleDataType.SEND_SET_DEL_USER, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SUB_DEL_USER) {
					//BLE删除用户操作2
					Bundle delUserData = msg.getData();
					delSubUser = (UserBase) delUserData.getSerializable("delUser");
					bleDataProcessing(EnumBleDataType.SEND_SUB_DEL_USER, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_UPDATETIME) {
					//连接上蓝牙设备后，校准时间
					bleDataProcessing(EnumBleDataType.SEND_UPDATETIME, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_RESPRECDATA) {
					//BLE 回复数据已收到  
					bleDataProcessing(EnumBleDataType.SEND_SET_RESPRECDATA, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_SET_CHECK_CONNECT) {
					//BLE 验证是否连接 发送内屏
					bleDataProcessing(EnumBleDataType.SEND_SET_CHECK_CONNECT, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_BLE_GET_USER_LIST) {
					//BLE 获取用户列表 
					bleDataProcessing(EnumBleDataType.SEND_GET_BLE_USER_LIST, EnumBleOpt.DATA_SEND);
				} else if (msg.what == WhatMainUtil.WHAT_SAVE_TARGET) {//保存目标值
					dataVo = mScalesTargetService.saveOrUpdate(currTarget);
					if (dataVo.getCode() != 0) {
						Log.d("TARGET_SAVE", dataVo.getMsgcn());
					} else {
						Log.d("TARGET_SAVE", dataVo.getMsgcn());
					}
				} else if (msg.what == WhatMainUtil.WHAT_SCORE_MSG) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							
							mDisplayCircleView.setScores(msgCount);
							mDisplayCircleView.invalidate();
							//cusCirclarProgress.setCenterData(msgCount + "");
							//cusCirclarProgress.invalidate();
						}
					});
				} else if (msg.what == 0x510) {//目标页，控制滑动锁定
					scrollClickBtn();
					boolean openScroll = Boolean.parseBoolean(msg.obj.toString());
					if (openScroll) {
						ClickUtils.scrollClickExit(mScrollLayout, "");
					}
				} else if (msg.what == 0x511) {
					//toastTop("Hi，Boy!");
				} else if (msg.what == WhatMainUtil.WHAT_SAVE_BASE_USER) {//保存UserBase 信息
					AccountService service = new AccountService(MainActivity.this);
					service.editUserData(firstUserBase);
					if (openToast) {
						toastCenter("保存用户信息");
					}
				}
			} catch (Exception e) {
				MobclickAgent.reportError(getApplication(), e);
			}

		}
	}

	/**发送消息*/
	public void sendMesssage(final int what) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Message msg = mHandler.obtainMessage(what);
				mHandler.sendMessage(msg);
			}
		});
	}

	/**2、加载主页*/
	private void loadMainList() {
		/**家庭列表菜单*/
		addFamily = (ImageView) findViewById(R.id.addFamily);
		addFamily.setOnClickListener(onClickListener);
		//蓝牙用户列表
		userList = new ArrayList<Integer>();
		mWeightBle = new WeightBle();
		//首页动画
		//llHiddenAnimi = (LinearLayout) findViewById(R.id.llHiddenAnimi);
		llShowAnimiRunBar = (LinearLayout) findViewById(R.id.llShowAnimiRunBar);
		llShowAnimiRunBar.setVisibility(View.INVISIBLE);
		/**实例化首页对象*/
		linearLayoutHidden = (LinearLayout) findViewById(R.id.linearLayoutHidden);
		linearLayoutShow = (LinearLayout) findViewById(R.id.linearLayoutShow);
		txtViewSetTargetWeight = (TextView) findViewById(R.id.txtViewSetTargetWeight);
		txtViewSetTargetWeight.setTypeface(fontType);
		txtViewSetTargetWeight.getPaint().setFakeBoldText(true);
		txtViewNoWeightDesc = (TextView) findViewById(R.id.txtViewNoWeightDesc);
		
		// init DisplayCircleView
		mDisplayCircleView = (DisplayCircleView) findViewById(R.id.mDisplayCircleView);
		mDisplayCircleView.setUnit(getString(R.string.setting_jin));
		mDisplayCircleView.setScoreUnit(getString(R.string.dis_score_unit));
		mDisplayCircleView.setTopText(getString(R.string.dis_top_text));
		
		// Init framelayout
		mFrameLayout = (FrameLayout) super.findViewById(R.id.frameLayout1);
		
		// Init flipImageView
		flipImageView = (FlipImageView) super.findViewById(R.id.flipImageView);
		
		// Init spinArcView
		spinArcView = (SpinArcView) super.findViewById(R.id.spinArcView1);
		
		linearLayoutHidden.setVisibility(View.INVISIBLE);
		/**底部图片实例对象*/
		rlViewTipBgMuscle = (RelativeLayout) findViewById(R.id.rlViewTipBgMuscle);
		rlViewTipBgBone = (RelativeLayout) findViewById(R.id.rlViewTipBgBone);
		rlViewTipBgMoisture = (RelativeLayout) findViewById(R.id.rlViewTipBgMoisture);
		llViewTipBgBmr = (RelativeLayout) findViewById(R.id.llViewTipBgBmr);
		rlViewTipBgBodyAge = (RelativeLayout) findViewById(R.id.rlViewTipBgBodyAge);
		/**跳转到【BMI】详细列表*/
		rlleftimgBtnBmiCover = (LinearLayout) findViewById(R.id.rlleftimgBtnBmi);
		rlleftimgBtnBmiCover.setOnClickListener(onClickListener);

		/**跳转到【脂肪】详细列表*/
		llimgBtnFatCover = (LinearLayout) findViewById(R.id.llimgBtnFat);
		llimgBtnFatCover.setOnClickListener(onClickListener);

		/**跳转到【肌肉率】详细列表*/
		tLayoutMuscle = (TableLayout) findViewById(R.id.tLayoutMuscle);
		tLayoutMuscle.setOnClickListener(onClickListener);

		/**跳转到【骨量】详细列表*/
		tLayoutBone = (TableLayout) findViewById(R.id.tLayoutBone);
		tLayoutBone.setOnClickListener(onClickListener);

		/**跳转到【水份】 详细列表*/
		tLayoutMoisture = (TableLayout) findViewById(R.id.tLayoutMoisture);
		tLayoutMoisture.setOnClickListener(onClickListener);

		/**跳转到【基础代谢率】 详细列表*/
		tLayoutBmri = (TableLayout) findViewById(R.id.tLayoutBmri);
		tLayoutBmri.setOnClickListener(onClickListener);

		/**跳转到【身体年龄】 详细列表*/
		tLayoutBodyAge = (TableLayout) findViewById(R.id.tLayoutBodyAge);
		tLayoutBodyAge.setOnClickListener(onClickListener);
		/**初始化健康评分描述对象*/
		txtViewBmi = (TextView) findViewById(R.id.txtViewBmi);
		txtViewFat = (TextView) findViewById(R.id.txtViewFat);
		txtViewMuscle = (TextView) findViewById(R.id.txtViewMuscle);
		txtViewBone = (TextView) findViewById(R.id.txtViewBone);
		txtViewMoisture = (TextView) findViewById(R.id.txtViewMoisture);
		txtViewBmr = (TextView) findViewById(R.id.txtViewBmr);
		txtViewSomaAge = (TextView) findViewById(R.id.txtViewSomaAge);
		//txtViewSomaAgeExt = (TextView) findViewById(R.id.txtViewSomaAgeExt);
		/**初始化目标信息视图对象*/
		tViewTargeInfoMuscle = (TextView) findViewById(R.id.tViewTargeInfoMuscle);
		tViewTargeInfoBone = (TextView) findViewById(R.id.tViewTargeInfoBone);
		tViewTargeInfoMoisture = (TextView) findViewById(R.id.tViewTargeInfoMoisture);
		tViewTargeInfoBmr = (TextView) findViewById(R.id.tViewTargeInfoBmr);
		txtViewTargeInfoSomaAge = (TextView) findViewById(R.id.txtViewTargeInfoSomaAge);
		userName = getCurrentRealName();
		tabOne = (TextView) findViewById(R.id.title_pageone);
		tabTwo = (TextView) findViewById(R.id.title_pagetwo);
		tabThree = (TextView) findViewById(R.id.title_pagethree);
		mScrollLayout = (CustomScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;//当前选中TAB
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
		mScrollLayout.setScrollerPass(true);
		setImageView = (ImageView) findViewById(R.id.set);
		setImageView.setOnClickListener(onClickListener);
		/**初始化左右圆圈对象*/
		customerTextViewLeft = (CutomerTextView) findViewById(R.id.customerTextViewLeft);
		customerTextViewRight = (CutomerTextView) findViewById(R.id.customerTextViewRight);
		linearLayoutContent = (LinearLayout) findViewById(R.id.linearLayoutContent);
		linearLayoutContent.setVisibility(View.INVISIBLE);
		main_tv_show = (CutomerTextViewDesc) findViewById(R.id.main_tv_show);
		main_tv_show.setContext(MainActivity.this);
		main_tv_show.setVisibility(View.INVISIBLE);
		main_tv_show.invalidate();
		weight_info = (CutomerTextViewMain) findViewById(R.id.weight_info);
		weight_info.setContext(MainActivity.this);
		/**初始化中间圆圈对象*/
		//customerTextViewTop = (CutomerTextView) findViewById(R.id.customerTextViewTop);
		//main_info_middle = (TextView) findViewById(R.id.main_info_middle);
		//main_info_unit = (TextView) findViewById(R.id.main_info_unit);
		rlBannerShowHidden = (RelativeLayout) findViewById(R.id.rlBannerShowHidden);
		rlBannerShowHidden.setVisibility(View.INVISIBLE);
	}

	//点击事件
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.rlleftimgBtnBmi) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(0);
				}
			} else if (v.getId() == R.id.llimgBtnFat) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(1);
				}
			} else if (v.getId() == R.id.tLayoutMuscle) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(2);
				}
			} else if (v.getId() == R.id.tLayoutBone) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(3);
				}
			} else if (v.getId() == R.id.tLayoutMoisture) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(4);
				}
			} else if (v.getId() == R.id.tLayoutBmri) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(5);
				}
			} else if (v.getId() == R.id.tLayoutBodyAge) {
				if (!ClickUtils.isFastDoubleClick()) {
					jumpToMainList(6);
				}
			} else if (v.getId() == R.id.set) {
				uploadImage(MainActivity.this);
			} else if (v.getId() == R.id.addFamily) {
				//家庭管理按钮
				Intent intent = new Intent(MainActivity.this, FamilyManager.class);
				startActivity(intent);
			}
		}
	};

	/**3、加载简报*/
	private void loadPresentation() {
		/**图像报表*/
		mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTv);
		btn_calendar_previous = (ImageView) findViewById(R.id.btn_calendar_previous);
		btn_calendar_next = (ImageView) findViewById(R.id.btn_calendar_next);
		btn_calendar_previous.setOnClickListener(mOnclickCalendar);
		btn_calendar_next.setOnClickListener(mOnclickCalendar);
		/**简报中的单位设置*/
		txtVieWeightUnit = (TextView) findViewById(R.id.txtVieWeightUnit);
		txtVieWeightUnit.setText("体重(" + showUnitStr() + ")");
		segmentText = (SegmentedRadioGroup) findViewById(R.id.segment_text);
		segmentText.setOnCheckedChangeListener(this);
		/**初始化左中右圆圈对象*/
		customerCircleLeft = (CustomerCircle) findViewById(R.id.customerCircleLeft);
		customerCircleLeft.setContext(MainActivity.this);
		customerCircleCenter = (CustomerCircle) findViewById(R.id.customerCircleCenter);
		customerCircleCenter.setContext(MainActivity.this);
		customerCircleRight = (CustomerCircle) findViewById(R.id.customerCircleRight);
		customerCircleRight.setContext(MainActivity.this);
		/**初始化默认左右切换控件文本对象*/
		String str[] = DateHelper.getWeekByDate(DateHelper.getCurrentDate(), changeCalendar * 7);
		mAutoCompleteTextView.setText(str[2]);
		/**初始化自定义报表对象*/
		userCharView = (UserChartView) findViewById(R.id.userChaerView);
		Date[] starAndEndDate = DateHelper.getWeekByDateToDate(DateHelper.getCurrentDate(), 0);
		userCharView.loadListData(YmComUtil.screenAppScreen(getWindowManager()), getCurrentUnit(),
				EnumChartType.CHART_WEEK, getCurrentUId(), getCurrentFirstWeight(), getCurrentFirstFat(),
				starAndEndDate[0], starAndEndDate[1]);
	}

	/**列表跳转页*/
	public void jumpToMainList(int currPage) {
		Intent intent = new Intent(MainActivity.this, MainListActivity.class);
		intent.putExtra("currPage", currPage);
		if (lastChart != null && mScoreVo != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable("weightChar", lastChart);
			bundle.putSerializable("mScoreVo", mScoreVo);
			intent.putExtras(bundle);
		} else {
			lastChart = new WeightChart(0, 0, 0, 0, 0f, 0, 0f, 0, 0f, 0f, 0f, 0, 0f, 0, 0, new Date());
			Bundle bundle = new Bundle();
			bundle.putSerializable("weightChar", lastChart);
			intent.putExtras(bundle);
		}
		startActivityForResult(intent, 0x21101);
	}

	/**4、加载目标*/
	private void loadTargetPage() throws Exception {
		// Page Three
		txtViewTargetWeight = (TextView) findViewById(R.id.txtViewTargetWeight);
		txtViewTargetWeight.setTypeface(fontType);
		txtViewTargetWeight.getPaint().setFakeBoldText(true);
		txtViewTargetUnit = (TextView) findViewById(R.id.txtViewTargetUnit);
		txtViewTargetMsg = (TextView) findViewById(R.id.txtViewTargetMsg);
		txtViewTargetTip = (TextView) findViewById(R.id.txtViewTargetTip);
		//txtViewTargetDesc = (CutomerTargetTextView) findViewById(R.id.txtViewTargetDesc);
		txtViewTargetDesc = (TextView) findViewById(R.id.txtViewTargetDesc);
		iv_reduce_weight = (ImageView) findViewById(R.id.iv_reduce_weight);
		iv_keep_weight = (ImageView) findViewById(R.id.iv_keep_weight);
		iv_add_weight = (ImageView) findViewById(R.id.iv_add_weight);
		iv_reduce_weight.setVisibility(View.INVISIBLE);
		iv_keep_weight.setVisibility(View.VISIBLE);
		iv_add_weight.setVisibility(View.INVISIBLE);
		//滑动控件
		mGallery = (CustomGallery) findViewById(R.id.galleryTarget);
		tb_setTarget_weight = (ToggleButton) findViewById(R.id.tb_setTarget_weight);
		
		//公共部分
		//PAGE ONE 首次进来，显示"现在"
		mDisplayCircleView.setScores(0);
		
		//cusCirclarProgress.setTopData("现在|");
		//customerTextViewTop.setUserData("现在|");
		customerTextViewLeft.setContext(MainActivity.this);
		customerTextViewLeft.setUserData("00|.0");
		customerTextViewRight.setContext(MainActivity.this);
		customerTextViewRight.setUserData("00|.0");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				resetDisplayCircle();
//				mDisplayCircleView.invalidate();
				customerTextViewLeft.invalidate();
				customerTextViewRight.invalidate();
			}
		});
		//默认单位
		
		//cusCirclarProgress.setCenterData("0.0");
		//cusCirclarProgress.setBottomData(showUnitStr());
		//cusCirclarProgress.invalidate();
		
		//main_info_middle.setText("0.0");
		//main_info_unit.setTypeface(fontType);
		//main_info_unit.setText(loadUnit);
		txtViewSomaAge.setText("0 岁");
		//
		if (globalUser.getAge() > 18) {
			loadInitData();
		} else {
			loadYoung();
		}
	}

	@SuppressLint("NewApi")
	private void loadYoung() {
		/**左右圈*/
		txtViewBmi.setText("未成年");
		txtViewFat.setText("未成年");
		/**主列表*/
		rlleftimgBtnBmiCover.setEnabled(false);
		llimgBtnFatCover.setEnabled(false);
		tLayoutMuscle.setEnabled(false);
		tLayoutBone.setEnabled(false);
		tLayoutMoisture.setEnabled(false);
		tLayoutBmri.setEnabled(false);
		tLayoutBodyAge.setEnabled(false);
		/**列表：肌肉、健康情况描述*/
		txtViewMuscle.setText("--");
		txtViewBone.setText("--");
		txtViewMoisture.setText("--");
		txtViewBmr.setText("--");
		txtViewTargeInfoSomaAge.setText("--");
		/**列表：底部背景图片变化*/
		rlViewTipBgMuscle.setBackground(getResources().getDrawable(checkType("标准")));
		rlViewTipBgBone.setBackground(getResources().getDrawable(checkType("标准")));
		rlViewTipBgMoisture.setBackground(getResources().getDrawable(checkType("标准")));
		llViewTipBgBmr.setBackground(getResources().getDrawable(checkType("标准")));
		rlViewTipBgBodyAge.setBackground(getResources().getDrawable(checkType("标准")));
		/**初始化健康评分描述对象*/
		tViewTargeInfoBmr.setText("---");
		txtViewSomaAge.setText("---");
		//txtViewSomaAgeExt.setText("");
		tViewTargeInfoMuscle.setText("---");
		tViewTargeInfoBone.setText("---");
		tViewTargeInfoMoisture.setText("---");
		tViewTargeInfoBmr.setText("---");
		/**简报页*/
		customerCircleCenter.setDrawTopDesc("-");
		customerCircleCenter.setDrawData("0.0");
		customerCircleCenter.setDrawUnit("%");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				customerCircleCenter.invalidate();
			}
		});
		/**目标页*/

	}

	private void loadInitData() {
		//PAGE TWO
		String loadUnit = showUnitStr();
		customerCircleLeft.setDrawTopDesc("+");
		customerCircleLeft.setDrawData("0.0");
		customerCircleLeft.setDrawUnit(loadUnit);
		customerCircleCenter.setDrawTopDesc("-");
		customerCircleCenter.setDrawData("0.0");
		customerCircleCenter.setDrawUnit("%");
		customerCircleRight.setDrawTopDesc("");
		customerCircleRight.setDrawData("0/0");
		customerCircleRight.setDrawUnit("");
		//目标页
		txtViewTargetDesc.setText("您的目标是要保持：0.0" + loadUnit);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				customerCircleLeft.invalidate();
				customerCircleCenter.invalidate();
				customerCircleRight.invalidate();
			}
		});
		//查询用户当前目标值 
		dataSetTarget = mScalesTargetService.getScalesTagret(getCurrentUId());//根据当前用户ID查询目标信息
		currTarget = dataSetTarget.getData();
		//如果目标值为空(未设置)，则取最后一次称重体重作为目标体重
		lastTargetValue = currTarget == null ? lastWeight : currTarget.getTargetWeight();
		//根据目前单位转换目标值
		changeTarget = Float.parseFloat(showWeightByUnit(lastTargetValue, 1));
		//给滑动控件转换目标值
		targetWeightSelectIndex = DataUtil.toInt(changeTarget);
		//滑动控件
		String userUnit = showUnitStr();
		if ("kg".equals(userUnit)) {
			pAdapterKg = new ScrollDataAdapter(this, listDataKg, display);// 初始化自定义的Adapter
			mGallery.setAdapter(pAdapterKg);// 设置Gallery显示的内容
		} else if ("斤".equals(userUnit)) {
			pAdapterJing = new ScrollDataAdapter(this, listDataJing, display);// 初始化自定义的Adapter
			mGallery.setAdapter(pAdapterJing);// 设置Gallery显示的内容
		} else if ("lb".equalsIgnoreCase(userUnit)) {
			pAdapterLb = new ScrollDataAdapter(this, listDataLb, display);
			mGallery.setAdapter(pAdapterLb);
		}
		mGallery.setSelection(targetWeightSelectIndex);
		mGallery.setmHandler(mHandler);
		// 通过setSelection() 可以设置当前选中的元素，这里我们将其设置在中间
		// 这里对Item项进行监听，以实现刷新显示的效果
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//设置目标值体重单位
				String scrollUnit = showUnitStr();
				txtViewTargetUnit.setText(scrollUnit);
				int weightValue = 0;
				int showMsg = R.string.TargetDeflutMsg;

				float lastWeight;
				if (lastChart != null) {//目标不为空的时候
					lastWeight = Float.parseFloat(showWeightByUnit(lastChart.getWeight(), 1));
				} else {//第一次称重的时候
					lastWeight = fistTarget;
				}
				//当前修改目标体重
				float currWeight = DataUtil.toFloat(position, 1);
				nowOffset = currWeight - lastWeight;
				offsetWeight = DataUtil.floatToString(nowOffset, 1).replace("-", "");
				//如果当前目标为空,获取控件默认目标值
				if (nowOffset > 0) {
					iv_reduce_weight.setVisibility(View.INVISIBLE);
					iv_keep_weight.setVisibility(View.INVISIBLE);
					iv_add_weight.setVisibility(View.VISIBLE);
					targetWeightType = 3;
					weightValue = (position) - DataUtil.toInt(lastWeight);
					System.err.println("ADD_weightValue=" + weightValue);
					switch (weightValue) {
					case 1:
						showMsg = R.string.targetAddOneMsg;
						break;
					case 2:
						showMsg = R.string.targetAddTwoMsg;
						break;
					case 3:
						showMsg = R.string.targetAddThreeMsg;
						break;
					default:
						showMsg = R.string.targetAddFourMsg;
						break;
					}
					txtViewTargetTip.setText(showMsg);
					//切换时的文字描述
					StringBuffer sbufer = new StringBuffer();
					sbufer.append("要达成这个目标，您还需要努力增重").append(offsetWeight).append(scrollUnit);
					txtViewTargetDesc.setText(sbufer.toString());
					//txtViewTargetDesc.setUnit(scrollUnit);
					//txtViewTargetDesc.invalidate();
				} else if (nowOffset < 0) {
					iv_reduce_weight.setVisibility(View.VISIBLE);
					iv_keep_weight.setVisibility(View.INVISIBLE);
					iv_add_weight.setVisibility(View.INVISIBLE);
					weightValue = DataUtil.toInt(lastWeight) - (position);
					targetWeightType = 1;
					System.err.println("kreduce_weightValue=" + weightValue);
					switch (weightValue) {
					case 1:
						showMsg = R.string.targetReduceOneMsg;
						break;
					case 2:
						showMsg = R.string.targetReduceTwoMsg;
						break;
					case 3:
						showMsg = R.string.targetReduceThreeMsg;
						break;
					default:
						showMsg = R.string.targetReduceFourMsg;
						break;
					}
					txtViewTargetTip.setText(showMsg);
					//切换时的文字描述
					StringBuffer sbufer = new StringBuffer();
					sbufer.append("要达成这个目标，您还需要努力减重").append(offsetWeight).append(scrollUnit);
					txtViewTargetDesc.setText(sbufer.toString());
					//txtViewTargetDesc.setUnit(scrollUnit);
					//txtViewTargetDesc.invalidate();
				} else {
					iv_reduce_weight.setVisibility(View.INVISIBLE);
					iv_keep_weight.setVisibility(View.VISIBLE);
					iv_add_weight.setVisibility(View.INVISIBLE);
					targetWeightType = 2;
					txtViewTargetTip.setText(showMsg);
					//切换时的文字描述
					StringBuffer sbufer = new StringBuffer();
					sbufer.append("您的目标是要保持：").append(offsetWeight).append(scrollUnit);
					txtViewTargetDesc.setText(sbufer.toString());
					//txtViewTargetDesc.setUnit(scrollUnit);
					//txtViewTargetDesc.invalidate();
				}
				StringBuilder sbCurr = new StringBuilder();
				sbCurr.append(position).append("");
				//在编辑状态打开时，才更新改控件的值
				if (openTargetShow) {
					txtViewTargetWeight.setText(sbCurr.toString());//大圆展示
				}
				txtViewSetTargetWeight.setText(sbCurr.toString().replace(".0", ""));//目标体重中展示
				if ("kg".equals(scrollUnit)) {
					pAdapterKg.notifyDataSetChanged(position);
				} else if ("斤".equals(scrollUnit)) {
					pAdapterJing.notifyDataSetChanged(position);
				} else if ("lb".equals(scrollUnit)) {
					pAdapterLb.notifyDataSetChanged(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				System.err.println("adapterView=" + arg0.getCount());
			}
		});
		if (currTarget == null) {
			//mGallery.setPass(true);//设置控件可滑动
			tb_setTarget_weight.setChecked(true);//设置按钮状态为保存
		}
		//保存体重
		tb_setTarget_weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//如果当前目标为空,获取控件默认目标值
				targetSetting = MathUtils.round(Float.parseFloat(txtViewTargetWeight.getText().toString()), 0);
				if (currTarget == null) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tb_setTarget_weight.setChecked(true);//设置按钮状态为保存
							defaultSaveTargetWeight(targetSetting);
						}
					});
				} else {
					//已设置目标值
					if (isChecked) {
						editTargetWeight();
						//控制是否为编辑状态
						mScrollLayout.setOrNotEdit(true);
						//控制滑动范围
						if (mGallery != null) {
							mScrollLayout.setmGallery(mGallery);
						}
						//控制倒计时
						if (mScrollLayout != null) {
							mScrollLayout.setmScrollLayout(mScrollLayout);
						}
					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								saveTargetWeight(targetSetting);
								//控制是否为编辑状态
								mScrollLayout.setOrNotEdit(false);
							}
						});
					}
				}
			}

		});
	}

	private void defaultSaveTargetWeight(float targetSetting) {
		//首次可滑动
		mScrollLayout.setScrollerPass(true);
		//mGallery.setPass(true);//设置状态为可滑动
		if (0.0 != targetSetting) {
			currTarget = new ScalesSetTarget();//创建目标对象
			currTarget.setUserId(getCurrentUId());//塞进目标数据
			currTarget.setTargetType(targetWeightType);
			currTarget.setTargetWeight(targetSetting);
			mHandler.sendEmptyMessage(WhatMainUtil.WHAT_SAVE_TARGET);
		}
	}

	@SuppressLint("NewApi")
	private void saveTargetWeight(float targetSetting) {
		Animation mAnimationd;
		Animation outAnimation;
		tb_setTarget_weight.setAlpha(1.0f);
		//设置控制不可滑动
		//mGallery.setPass(false);
		mScrollLayout.setScrollerPass(true);
		try {
			WeightChart wChart = getCurrentWeight();
			if (wChart != null) {
				openTargetShow = false;
				float lastWeight = showWeightByUnitKG(targetSetting, 1);
				//取出当前已设置的值
				setTargetWeight = mScalesTargetService.getFindOne(getCurrentUId());
				setTargetWeight.setUserId(getCurrentUId());
				setTargetWeight.setTargetType(targetWeightType);
				setTargetWeight.setTargetWeight(lastWeight);
				//保存
				currTarget = setTargetWeight;
				mHandler.sendEmptyMessage(WhatMainUtil.WHAT_SAVE_TARGET);
				//退出动画
				outAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_from_left);
				linearLayoutShow.startAnimation(outAnimation);
				linearLayoutShow.setVisibility(View.VISIBLE);
				//进入动画
				mAnimationd = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_right);
				linearLayoutHidden.startAnimation(mAnimationd);
				linearLayoutHidden.setVisibility(View.INVISIBLE);
				//点击保存后，更新UI
				txtViewNoWeightDesc.setText("目前体重");
				txtViewTargetWeight.setText(showWeightByUnit(wChart.getWeight(), 1).replace(".0", ""));
				String targetSaveVal = MathUtils.subString(showWeightByUnit(lastWeight, 1)).replace(".0", "");
				if (targetSaveVal.contains(".")) {
					String[] targetVal = targetSaveVal.split("\\.");
					txtViewSetTargetWeight.setText(targetVal[0] + "");
				} else {
					txtViewSetTargetWeight.setText(targetSaveVal);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void editTargetWeight() {
		try {
			openTargetShow = true;
			//显示页退出的动画
			Animation outAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_left);
			linearLayoutShow.startAnimation(outAnimation);
			linearLayoutShow.setVisibility(View.INVISIBLE);
			//编辑页显示的动画
			Animation mAnimationd = AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_from_right);
			linearLayoutHidden.startAnimation(mAnimationd);
			linearLayoutHidden.setVisibility(View.VISIBLE);
			tb_setTarget_weight.setAlpha(0.5f);
			//更新UI提示
			txtViewNoWeightDesc.setText("目标体重");
			//查询最近一次称重数据
			lastChart = weightBaseService.getLastWeight(getCurrentUId());
			//取出当前目标体重
			currTarget = mScalesTargetService.getFindOne(getCurrentUId());
			//将当前目标体重更新到UI
			String targetx = showWeightByUnit(currTarget.getTargetWeight(), 1);
			int targetSetting = DataUtil.toInt(Float.parseFloat(targetx));
			txtViewTargetWeight.setText(String.valueOf(targetSetting).replace(".0", "")); //设置目标体重
			txtViewSetTargetWeight.setText(MathUtils.subString(targetx).replace(".0", ""));
			//设置控件可滑动
			mScrollLayout.setScrollerPass(true);
			mGallery.setSelection(targetSetting);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 日期切换事件
	 */
	private OnClickListener mOnclickCalendar = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_calendar_previous) {
				switch (chartDateType) {
				case SWITCH_WEEK:
					//没有更多数据了噢
					changeCalendar--;
					String[] weekChanging = DateHelper.getWeekByDate(DateHelper.getCurrentDate(), changeCalendar * 7);
					mAutoCompleteTextView.setText(weekChanging[2]);//控件显示
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_Left_WEEK);
					break;
				case SWITCH_MONTH:
					changeMonth--;
					String strMonth = DateHelper.getMonthDay(changeMonth);
					mAutoCompleteTextView.setText(strMonth);
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_Left_MONTH);
					break;
				case SWITCH_QUARTER:
					//quarterDay = quarterDay - 3;
					//String strquarterDay = DateHelper.getquarterDay(quarterDay);
					//mAutoCompleteTextView.setText(strquarterDay);
					//mHandler.sendEmptyMessage(WHAT_CHART_Left_QUARTER);
					break;
				case SWITCH_YEAR:
					//changeYear--;
					//String curYear = DateHelper.getCurrentYear(changeYear);
					//mAutoCompleteTextView.setText(curYear);
					//mHandler.sendEmptyMessage(WHAT_CHART_Left_YEAR);
					break;
				}

			} else if (v.getId() == R.id.btn_calendar_next) {
				switch (chartDateType) {
				case SWITCH_WEEK:
					if (0 > changeCalendar) {
						changeCalendar++;
						String str[] = DateHelper.getWeekByDate(DateHelper.getCurrentDate(), changeCalendar * 7);
						mAutoCompleteTextView.setText(str[2]);
						mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_MOVE_WEEK);
						break;
					} else {
						doubleClickExit("要持续使用才能查看更多数据噢!");
					}
				case SWITCH_MONTH:
					if (0 > changeMonth) {
						changeMonth++;
						String strMonth = DateHelper.getMonthDay(changeMonth);
						mAutoCompleteTextView.setText(strMonth);
						mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_MOVE_MONTH);
						break;
					} else {
						doubleClickExit("要持续使用才能查看更多数据噢!");
					}
				case SWITCH_QUARTER:
					doubleClickExit("要持续使用才能查看更多数据噢!");
					/*if (0>quarterDay) {
						quarterDay = quarterDay + 3;
						String strquarterDay = DateHelper.getquarterDay(quarterDay);
						mAutoCompleteTextView.setText(strquarterDay);
						//mHandler.sendEmptyMessage(WHAT_CHART_MOVE_QUARTER);
						break;
					}*/
				case SWITCH_YEAR:
					doubleClickExit("要持续使用才能查看更多数据噢!");
					/*if (0>changeYear) {
						changeYear++;
						String curYear = DateHelper.getCurrentYear(changeYear);
						mAutoCompleteTextView.setText(curYear);
						//mHandler.sendEmptyMessage(WHAT_CHART_MOVE_YEAR);
						break;
					}*/
				}
			}
		}
	};

	/**
	 *  简报图表事件（单击切换周、月、季度、年）
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == segmentText) {
			if (checkedId == R.id.button_one) {// week
				chartDateType = EnumSwitchDMQY.SWITCH_WEEK;//选中周
				changeCalendar = 0; //复位
				String str[] = DateHelper.getWeekByDate(DateHelper.getCurrentDate(), changeCalendar * 7);
				mAutoCompleteTextView.setText(str[2]);
				mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_WEEK);
			} else if (checkedId == R.id.button_two) {// month
				changeMonth = 0;//复位
				chartDateType = EnumSwitchDMQY.SWITCH_MONTH;//选中月份
				String strMonth = DateHelper.getMonthDay(changeMonth);//获取左右滑动控件描述
				mAutoCompleteTextView.setText(strMonth);
				mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_MONTH);
			} else if (checkedId == R.id.button_three) {// quarter
				doubleClickExit("要持续使用才能查看更多数据噢!");
				/*quarterDay = 0;//复位
				chartDateType = EnumSwitchDMQY.SWITCH_QUARTER;//选中季度
				String strquarterDay = DateHelper.getquarterDay(quarterDay);
				mAutoCompleteTextView.setText(strquarterDay);
				//mHandler.sendEmptyMessage(WHAT_CHART_QUARTER);
				*/} else if (checkedId == R.id.button_four) {// year
				doubleClickExit("要持续使用才能查看更多数据噢!");
				/*changeYear = 0;//复位
				chartDateType = EnumSwitchDMQY.SWITCH_YEAR;
				String curYear = DateHelper.getCurrentYear(changeYear);
				mAutoCompleteTextView.setText(curYear);
				//mHandler.sendEmptyMessage(WHAT_CHART_YEAR);
				*/}
		}
	}

	/**
	 * 加载图表数据
	 * 
	 * @param changePosition 
	 *void
	 */
	private void reLoadCharView(int changePosition) {
		Date[] starAndEndDate = new Date[2];
		starAndEndDate[0] = DateHelper.defualtDateTime();
		starAndEndDate[1] = DateHelper.defualtDateTime();
		CompareChartDataModels compareChartDataModels = null;
		switch (chartDateType) {
		case SWITCH_WEEK:
			starAndEndDate = DateHelper.getWeekByDateToDate(DateHelper.getCurrentDate(), changePosition * 7);
			if (globalUser.getAge() > 18) {
				userCharView.loadListData(YmComUtil.screenAppScreen(getWindowManager()), getCurrentUnit(),
						EnumChartType.CHART_WEEK, getCurrentUId(), getCurrentFirstWeight(), getCurrentFirstFat(),
						starAndEndDate[0], starAndEndDate[1]);
			} else {
				userCharView.loadListData(YmComUtil.screenAppScreen(getWindowManager()), getCurrentUnit(),
						EnumChartType.CHART_WEEK, getCurrentUId(), getCurrentFirstWeight(), 0.0f, starAndEndDate[0],
						starAndEndDate[1]);
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					userCharView.invalidate();
				}
			});
			compareChartDataModels = wifService.getCompareDataMoles(chartDateType, getCurrentUId(), starAndEndDate[0],
					starAndEndDate[1], UserChartView.getList() == null ? 0 : UserChartView.getList().size());
			//显示底部圆圈信息
			weekWeight = compareChartDataModels.getDiffWeight();
			float weekFat = compareChartDataModels.getDiffFat();
			if (weekWeight < 0 || weekFat < 0) {
				customerCircleLeft.setDrawTopDesc("-");
				customerCircleCenter.setDrawTopDesc("-");
				customerCircleLeft.setDrawData(showWeightByUnit(weekWeight, 1).replace("-", "").replace(".0", ""));
				customerCircleLeft.setDrawUnit(showUnitStr());
				String centerData = DataUtil.floatToString(weekFat, 1).replace("-", "").replace(".0", "");
				if (globalUser.getAge() > 18) {
					customerCircleCenter.setDrawData(centerData);
				} else {
					customerCircleCenter.setDrawData("0.0");
				}
				customerCircleCenter.setDrawUnit("%");
				StringBuilder sbRight = new StringBuilder();
				sbRight.append(String.valueOf(compareChartDataModels.getCurrentCount()));
				sbRight.append("/");
				sbRight.append(String.valueOf(compareChartDataModels.getSumCount()));
				customerCircleRight.setDrawData(sbRight.toString());
				customerCircleRight.setDrawTopDesc("");
				customerCircleRight.setDrawUnit("");
			} else if (weekWeight > 0 || weekFat > 0) {
				customerCircleLeft.setDrawTopDesc("+");
				customerCircleCenter.setDrawTopDesc("+");
				customerCircleLeft.setDrawData(showWeightByUnit(weekWeight, 1).replace("-", "").replace(".0", ""));
				customerCircleLeft.setDrawUnit(showUnitStr());
				String centerData = DataUtil.floatToString(weekFat, 1).replace("-", "").replace(".0", "");
				if (globalUser.getAge() > 18) {
					customerCircleCenter.setDrawData(centerData);
				} else {
					customerCircleCenter.setDrawData("0.0");
				}
				customerCircleCenter.setDrawUnit("%");
				StringBuilder sbRight = new StringBuilder();
				sbRight.append(String.valueOf(compareChartDataModels.getCurrentCount()));
				sbRight.append("/");
				sbRight.append(String.valueOf(compareChartDataModels.getSumCount()));
				customerCircleRight.setDrawData(sbRight.toString());
				customerCircleRight.setDrawTopDesc("");
				customerCircleRight.setDrawUnit("");
			} else if (weekWeight == 0 || weekFat == 0) {
				//左圈
				customerCircleLeft.setDrawTopDesc("+");
				customerCircleLeft.setDrawData("0.0");
				customerCircleLeft.setDrawUnit(showUnitStr());
				//中圈
				customerCircleCenter.setDrawTopDesc("-");
				customerCircleCenter.setDrawData("0.0");
				customerCircleCenter.setDrawUnit("%");
				//右圈
				customerCircleRight.setDrawData("0/0");
				customerCircleRight.setDrawTopDesc("");
				customerCircleRight.setDrawUnit("");
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					customerCircleLeft.invalidate();
					customerCircleCenter.invalidate();
					customerCircleRight.invalidate();
				}
			});
			break;
		case SWITCH_MONTH:
			starAndEndDate = DateHelper.getMonth(changePosition);
			userCharView.loadListData(YmComUtil.screenAppScreen(getWindowManager()), getCurrentUnit(),
					EnumChartType.CHART_MONTH, getCurrentUId(), getCurrentFirstWeight(), getCurrentFirstFat(),
					starAndEndDate[0], starAndEndDate[1]);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					userCharView.invalidate();
				}
			});
			//int dataNum = DateHelper.getDayOfMonth(starAndEndDate[0]); 
			compareChartDataModels = wifService.getCompareDataMoles(chartDateType, getCurrentUId(), starAndEndDate[0],
					starAndEndDate[1], 0);
			break;
		case SWITCH_QUARTER:
			/*starAndEndDate = DateHelper.getQuarter(changePosition);
			userCharView.loadListData(getCurrentUnit(), EnumChartType.CHART_QUARTER, getCurrentUId(),
					getCurrentFirstWeight(), getCurrentFirstFat(), starAndEndDate[0], starAndEndDate[1]);
			*/break;
		case SWITCH_YEAR:
			/*starAndEndDate = DateHelper.getCurrYear(changePosition);
			userCharView.loadListData(getCurrentUnit(), EnumChartType.CHART_YEAR, getCurrentUId(),
					getCurrentFirstWeight(), getCurrentFirstFat(), starAndEndDate[0], starAndEndDate[1]);
			*/break;
		default:
			break;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				userCharView.invalidate();
			}
		});
	}

	/**更显示状态*/
	private void updateConnectionState() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mConnected) {

					if (ClientConfig.isDebug) {
						String devName = mBle.getBTAdapterMacAddr().split(":")[5];
						tabOne.setText(StringUtils.defaultIfBlank(MainDataUtil.showUserName(userName, devName),
								getResources().getString(R.string.bleConnected)));
					} else {
						tabOne.setText(StringUtils.defaultIfBlank(MainDataUtil.showUserName(userName), getResources()
								.getString(R.string.bleConnected)));
					}
				} else {
					tabOne.setText(R.string.bleConnecting);
				}
			}
		});
	}

	private void onDeviceDisconnected() {
		mConnected = false;
		mScanning = false;
		mDeviceAddress = null;
		mDeviceName = null;
		readBGCharacteristic = null;
		sendBGCharacteristic = null;
		BaseBleActivity.setmDeviceAddress(null);
		BaseBleActivity.setReadBGCharacteristic(null);
		BaseBleActivity.setSendBGCharacteristic(null);
		if (null != mapBle) {
			mapBle.clear();
		}
		updateConnectionState();
	}

	private int num = 0;

	/**BLE Scan Device */
	private void scanLeDevice(final boolean enable) {
		baseApp = (BaseBleActivity) getApplication();
		mBle = baseApp.getIBle();
		if (mBle != null && !mBle.adapterEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		mConnected = baseApp.getMConnectedBle();
		Log.i("BLE", "mConnected:" + mConnected);
		if (mBle == null) {
			return;
		}
		mapBle = new HashMap<String, BledeviceVo>();
		if (mBle == null) {
			return;
		}
		if (enable) {
			//蓝牙检测任务，每隔10秒钟检测一次
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					//mScanning = false;
					if (mBle != null) {
						if (mConnected) {//默认为false,未连接
							Log.i("BLE", "A停止扫描 !mBle  mConnected=true stopScan");
							mScanning = false;
							mBle.stopScan();
						} else {
							if (!mScanning) {
								mScanning = true;
								mBle.startScan();
								Log.i("BLE", "A停止扫描 !mBle mConnected=false startScan");
							} else {
								if (num % 2 == 0) {
									mScanning = true;
									mBle.startScan();
								} else {
									Log.i("BLE", "待连接中....");
								}
								num = num + 1;
							}
						}
					} else {
						mBle = baseApp.getIBle();
						mScanning = true;
						mBle.startScan();
						Log.i("BLE", "A停止扫描 mBle null");
					}
				}
			}, 0, SCAN_PERIOD_TIME);
			//			mHandler.postDelayed(new Runnable() {
			//				@Override
			//				public void run() {
			//					mScanning = false;
			//					if (mBle != null) {
			//						if (mConnected) {
			//							Log.i("BLE", "A停止扫描 !mBle  mConnected=true stopScan");
			//							mBle.stopScan();
			//						} else {
			//							mBle.startScan();
			//							Log.i("BLE", "A停止扫描 !mBle mConnected=false startScan");
			//						}
			//					} else {
			//						Log.i("BLE", "A停止扫描 mBle null");
			//					}
			//				}
			//			}, SCAN_PERIOD_TIME);
			mScanning = true;
			if (mBle != null) {
				mBle.startScan();
				Log.i("BLE", "开始扫描");
			}
		} else {
			mScanning = false;
			if (mBle != null) {
				mBle.stopScan();
				Log.i("BLE", "离开页面停止扫描");
			}
		}
	}

	/**返回键按下处理方法*/
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		//友盟统计
		MobclickAgent.onPageStart("主页");
		MobclickAgent.onResume(this);
		Log.d("INFO", "onResume 被调用了!");
		// 3. 接受 service 发送的广播
		registerReceiver(mBleReceiver, BleService.getIntentFilter());
		loadInitData();
		Log.i("BLE", "onResume");
		//启动扫描
		scanLeDevice(true);
		//跟新状态
		updateConnectionState();
		//装载蓝牙设备
		if (ClickUtils.isFastDoubleClick()) {
			return;
		}
		
		//首次进入页面动画
		mainRun();
	}

	/**首次进入页面动画*/
	private void mainRun() {
		//设置单位
		if (mConnected) {
			mUserBase = getCurrentUser();
			setThreeTime(30);
			mHandler.sendEmptyMessage(WhatMainUtil.WHAT_BLE_SET_USER_INFO);
			setThreeTime(100);
			mHandler.sendEmptyMessage(WhatMainUtil.WHAT_BLE_SET_UPDATETIME);
			setThreeTime(100);
			if (null != mUserBase) {
				boolean isBleSync = mUserBase.isSyncBle();
				Log.d("BLE", "isBleSync" + isBleSync);
				if (!isBleSync) {
					//更新蓝牙设备单位
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_BLE_SET_USER_UNIT);
					mUserBase.setSyncBle(true);
					mAccountService.editUserData(mUserBase);
				}
			}
		}
		//启动时创建实例对象 
		//查询最近一次称重数据
		lastChart = weightBaseService.getLastWeight(getCurrentUId());
		if (null != lastChart) {
			//数据不为空时，直接显示
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//加载数据和动画
					//查询最近一次称重数据
					if (null != lastChart) {
						//数据不为空时，直接显示
						mWeightInfo = lastChart.toWeightInfo();
						if (globalUser.getAge() > 18) {
							scoreService = new ScoreService(MainActivity.this, mWeightInfo, getCurrentUser());
							mScoreVo = scoreService.getScoreVo();
							scoreTotal = scoreService.getScoreTotal();
							scoreDesc = scoreService.getScoreDesc();
							setShowMainFirst(mWeightInfo, mScoreVo);
							txtViewTargetWeight.setText(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", ""));
							//隐藏底部描述
							linearLayoutContent.setVisibility(View.VISIBLE);
							
							//displayStaticCircle(weight, isRealTime)
							displayStaticCircle(Float.valueOf(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", "")), false);
							displayCircleToAnimate(Float.valueOf(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", "")), (int) scoreTotal);
						} else {
							//未成年人
							//顶部显示体重
							//cusCirclarProgress.setTopData("现在|");
							//cusCirclarProgress.setCenterData(nowWeight);
							//cusCirclarProgress.setBottomData(showUnitStr());
							//cusCirclarProgress.invalidate();
							//customerTextViewTop.setUserData("现在|");
							//customerTextViewTop.invalidate();
							//中间部分
							//main_info_middle.setText(nowWeight);//显示体重
							//下部分 --描述更新
							//main_info_unit.setText(showUnitStr());
							weight_info.setUserData("体重|");
							weight_info.invalidate();
							//目标页
							txtViewTargetWeight.setText(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", ""));
							//隐藏底部描述
							linearLayoutContent.setVisibility(View.VISIBLE);
							loadYoung();
							
							String nowWeight = showWeightByUnit(mWeightInfo.getWeight(), 1).trim();
							displayStaticCircle(Float.valueOf(nowWeight), false);
							displayCircleToAnimate(Float.valueOf(nowWeight), 0);
						}
					}
				}
			});
		}
	}

	/**停止键按下处理方法*/
	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("主页");
		MobclickAgent.onPause(this);//友盟统计  
		Log.i("BLE", "onPause");
		// 5. 停止接收广播
		unregisterReceiver(mBleReceiver);
		scanLeDevice(false);
		//清空数据
		//mLeDeviceListAdapter.clear();
		if (ClickUtils.isFastDoubleClick()) {
			return;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	/**销毁*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		hThread.isInterrupted();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private void displayData(String data) {
		//报文过滤
		if (data == null || data.length() < 22) {
			return;
		}
		Log.d("time", data.toString());
		//区别报文类型
		int orderType = ByteUtils.positionToInt(data);
		if (WhatMainUtil.WHAT_BLE_GET_TIME_LINE_DATA == orderType) {//实时测量报文处理
			stabilityEnter = 1;
			
			realTimeData(data);
		} else if (WhatMainUtil.WHAT_BLE_GET_OK_WEIGHT_DATA == orderType) {//稳定测量报文处理
			if (stabilityEnter == 1) {
				stabilityEnter++;
				//响应秤数据已收到
				bleDataProcessing(EnumBleDataType.SEND_SET_RESPRECDATA, EnumBleOpt.DATA_SEND);
				stabilityData(data);
				realTimeAnimi = 1;
				
				//resetDisplayCircle();
			}
		} else if (WhatMainUtil.WHAT_BLE_GET_USER_LIST == orderType) {//获取称用户列表
			int userCount = Integer.parseInt(data.substring(8, 10), 16); //用户ID
			int currUser = Integer.parseInt(data.substring(10, 12), 16); //用户ID
			int userId = Integer.parseInt(data.substring(12, 20), 16); //用户ID
			System.err.println("用户：" + userId);
			Log.d("XiaoMai", "用户：" + userId + "  当前为第：" + currUser + " 个用户，用户总数：" + userCount + " 个!");
			userList.add(userId);
		}
	}

	/**
	 * 接收稳定数据   1、实时后稳定数据 2、历史数据
	 * @param data 
	 *void 
	*/
	private void stabilityData(String data) {
		//翻转动画
		synchronized (data) {
			int orNotHistory = Integer.parseInt(data.substring(8, 10)); //记录标识位(0表示实时数据，1表示历史数据)
			int currTime = Integer.parseInt(data.substring(10, 18), 16); //实时时间
			mWeightBle = MainDataUtil.getStabilityData(data, mDeviceName, mDeviceAddress);

			//判断是历史记录还是当前记录
			if (orNotHistory == 0) {
				//0表示当前称量的稳定数据
				llShowAnimiRunBar.setVisibility(View.VISIBLE);//显示
				//cusCirclarProgress.startCartoom(2);//执行动画
				//控制是否显示评分
				//animiCheck(3000);
				//1、当前稳定称量记录
				staticWeight = true;//开启数字动画 
				stabilityinit(getCurrentUser(), mWeightBle);
				
				float sWeight = Float.valueOf(MathUtils.subString(showWeightByUnit(mWeightBle.getWeight(), 1)).replace(".0", ""));
				//new Thread(new DisplayCircleRunnable(sWeight,(int)mScoreVo.getScoreTotal(),true)).start();
				displayCircleToAnimate(sWeight, (int)mScoreVo.getScoreTotal());
				//displayCircleToAnimate(sWeight);
				
			} else if (orNotHistory == 1) {
				//1表示历史数据
				//2.1、对比系统当前时间与接收到的报文时间
				//				Long currLocalTime = System.currentTimeMillis();
				//				Long currAcceptTime = currTime * 1000L;
				//				Long sumTime = currLocalTime - currAcceptTime;
				//				//2.2、还原报文时间
				//				long trueTime = currLocalTime - sumTime;//报文时间
				//				Date saveTime = new Date(trueTime);//转换为可保存的时间
				//				mWeightBle.setCreateTime(saveTime);
				Long currLocalTime = System.currentTimeMillis();
				if (currTime < 1398787200) {
					//断电时间
					currLocalTime = currLocalTime - currTime;
					Date saveTime = new Date(currLocalTime);//转换为可保存的时间
					mWeightBle.setCreateTime(saveTime);
				} else {
					//下常时间
					Long currAcceptTime = currTime * 1000L;
					Date saveTime = new Date(currAcceptTime);//转换为可保存的时间
					mWeightBle.setCreateTime(saveTime);

				}
				
//				float sWeight = Float.valueOf(MathUtils.subString(showWeightByUnit(mWeightBle.getWeight(), 1)).replace(".0", ""));
//				displayStaticCircle(sWeight);
				/*
								Long currAcceptTime = currTime * 1000L;
								Long sumTime = currLocalTime - currAcceptTime;
								//2.2、还原报文时间
								long trueTime = currLocalTime - sumTime;//报文时间
								Date saveTime = new Date(trueTime);//转换为可保存的时间
								mWeightBle.setCreateTime(saveTime);*/
				//2.3、处理历史数据
				stablilityHistoryList(mWeightBle);
			}
		}
	}

	/*private void animiCheck(int timeSecond) {
		//2、翻转动画
		if (openAnimi) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					enableRefresh = true;
					openAnimi = false;
					float cX = llShowAnimiRunBar.getWidth() / 2.0f;
					float cY = llShowAnimiRunBar.getHeight() / 2.0f;
					final ReverseAnimation rotateAnim = new ReverseAnimation(cX, cY, ReverseAnimation.ROTATE_DECREASE);
					if (rotateAnim != null) {
						rotateAnim.setInterpolatedTimeListener(MainActivity.this);
						rotateAnim.setFillAfter(true);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								llShowAnimiRunBar.setVisibility(View.INVISIBLE); 	//隐藏
								llShowAnimiRunBar.startAnimation(rotateAnim); 		//启动动画
							}
						});
					}
				}
			}, timeSecond);
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				openAnimi = true;
			}
		}, 3000);
	}*/

	/**
	 * 1 表示历史数据
	 * @param currTime
	 * @param userId
	 * @param weights
	 * @param elecImpedance
	 * @param mUserBase
	 * @param weightBle 
	 *void 
	*/
	private void stablilityHistoryList(WeightBle weightBle) {
		if (weightBle != null) {
			listCurrUser = new ArrayList<WeightInfo>();
			listOther = new ArrayList<WeightOthers>();
			UserBase uBase = getCurrentUser();
			//1、当前用户记录
			if (uBase != null) {
				if (weightBle.getUserId() == getCurrentUId()) {//批量保存当前用户的历史数据
					mWeightInfo = FormulaUitl.fromWeightInfo(uBase, weightBle);
					listCurrUser.add(mWeightInfo);
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_SAVE_MY_WDATA);//保存实时数据
					setThreeTime(showChartTime);
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新报表数据
				} else {//非当前用户的历史数据
					//上传别人的数据
					if (weightBle.getUserId() != 0) {//userID 为0的数据，直接抛弃掉
						//Thu Aug 07 20:32:09 GMT+08:00 2014
						weightOthers = new WeightOthers(weightBle.getUserId(),
								StringHelper.macNoToDevceNo(mDeviceAddress), mDeviceName, mDeviceAddress, "",
								weightBle.getWeight(), weightBle.getResistance(), 0, DateHelper.dataToString(
										weightBle.getCreateTime(), null));
						listOther.add(weightOthers);
						mHandler.sendEmptyMessage(WhatMainUtil.WHAT_SAVE_OTHER_WDATA);//保存别人的历史数据
						setThreeTime(showChartTime);
						mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新报表数据
					}
				}
			}
		}
	}

	/**
	 * 处理稳定数据
	 * 
	 * @param mUserBase
	 * @param weightBle 
	 *void
	 */
	private void stabilityinit(UserBase mUserBase, WeightBle weightBle) {
		//UI 显示实时称量体重
		mWeightBle = weightBle;
		//如果用户对象不为空
		if (mUserBase != null) {
			mUserBase.setSyncBle(true);
			//1、计算BMI 数值 mWeightInfo
			mWeightInfo = FormulaUitl.fromWeightInfo(mUserBase, weightBle);
			mWeightInfo.setWeight(weightBle.getWeight());//保存最新体重 
			wiMap = new HashMap<String, WeightInfo>();
			wiMap.put(weightBle.getWeight() + "", mWeightInfo);
			//2、数据存储(WeightChar 一天只保留一条记录)
			if (wiMap.size() == 1) {
				//2.1 取出最后一条称重记录，与当前体重作比较，超过2公斤，将不作保存 
				WeightChart lastChar = weightBaseService.getLastWeight(getCurrentUId());
				//2.2 第一次称量的情况，没有最后一次的体重
				if (mWeightInfo != null && lastChar == null) {
					//数据存储(第一次时保存基准体重和基准脂肪率)
					mUserBase.setBasisWeight(weightBle.getWeight());
					mUserBase.setFirstWeight(weightBle.getWeight());
					mUserBase.setFirstFat(mWeightInfo.getFat());
					//保存用户信息(报表脂肪率)
					AccountService service = new AccountService(MainActivity.this);
					service.editUserData(mUserBase);
					//保存信息到weightInfo(每次称重) 
					wifService.saveOne(mWeightInfo);//mWeightInfo
					mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新报表数据 
					//第一次称重，取第一次称重作为目标值保存
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							currTarget = new ScalesSetTarget();//创建目标对象
							currTarget.setUserId(mWeightInfo.getUserId());//塞进目标数据
							currTarget.setTargetType(targetWeightType);
							currTarget.setTargetWeight(mWeightBle.getWeight());
							mScalesTargetService.saveOrUpdate(currTarget);//保存目标值
						}
					});
					//更新目标页
					txtViewSetTargetWeight.setText(MathUtils.subString(showWeightByUnit(weightBle.getWeight(), 1))
							.replace(".0", ""));
					targetWeightSelectIndex = DataUtil.toInt(showWeightByUnitFloat(weightBle.getWeight(), 1));
					fistTarget = targetWeightSelectIndex;
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							YmDialogYesNo.Builder builder = new YmDialogYesNo.Builder(MainActivity.this, "设置减肥目标吧!");
							builder.setBtnYesText(getResources().getString(R.string.btnYes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													setCurPoint(2);
													mScrollLayout.snapToScreen(2);
													editTargetWeight();
													//控制是否为编辑状态
													mScrollLayout.setOrNotEdit(true);
													//控制滑动范围
													if (mGallery != null) {
														mScrollLayout.setmGallery(mGallery);
													}
													//控制倒计时
													if (mScrollLayout != null) {
														mScrollLayout.setmScrollLayout(mScrollLayout);
													}
												}
											});
										}
									}).setBtnNoText("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							}).create().show();

						}
					}, 5000);
					showMainData(mWeightInfo, true);
				} else if (mWeightInfo != null && lastChar != null) {
					lastChart = weightBaseService.getLastWeight(getCurrentUId());
					float result = mWeightInfo.getWeight() - lastChar.getWeight();
					//超过1.5公斤的范围，不作保存
					if (result > -1.5f && result <= 1.5f) {
						//toastCenter("保存数据!");
						//4、数据存储(weightInfo 保存每次称重记录) 
						Log.d("BLE", "保存每次称重记录");
						wifService.saveOne(mWeightInfo);//mWeightInfo
						Log.d("BLE", "刷新报表数据");
						mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新报表数据
					} else {
						//我好像不认识你哦，请问你是user本人么？
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								YmDialogYesNo.Builder builder = new YmDialogYesNo.Builder(MainActivity.this, "请问你是"
										+ userName + "本人么？").setTitle("我好像不认识你哦");
								builder.setBtnYesText("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										//更新基准体重
										runOnUiThread(new Runnable() {
											@Override
											public void run() {
												try {
													UserBase mUserBase = getCurrentUser();
													mUserBase.setBasisWeight(mWeightInfo.getWeight());
													mUserBase.setSyncBle(false);
													dataVo = mAccountService.editUserData(mUserBase);
													//将基准体重更新到蓝牙设备
													if (dataVo.getCode() == 0) {
														mHandler.sendEmptyMessage(WhatMainUtil.WHAT_BLE_SET_USER_UNIT);
													}
													//数据存储(第一次时保存基准体重和基准脂肪率)
													mUserBase.setBasisWeight(mWeightBle.getWeight());
													mUserBase.setFirstWeight(mWeightBle.getWeight());
													mUserBase.setFirstFat(mWeightInfo.getFat());
													//保存用户信息(报表脂肪率)
													AccountService service = new AccountService(MainActivity.this);
													service.editUserData(mUserBase);
													//保存信息到weightInfo(每次称重) 
													wifService.saveOne(mWeightInfo);//mWeightInfo
													mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新报表数据
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										});
									}
								}).setBtnNoText("取消", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).create().show();
							}
						}, 7000);
					}
					showMainData(mWeightInfo, true);
				}
			}
		}
	}

	/**
	 * UI 显示数据
	 * 
	 * @param mWeightInfo
	 * @param isShowScore 
	 *void
	 */
	private void showMainData(final WeightInfo mWeightInfo, boolean isShowScore) {
		try {
			if (globalUser.getAge() > 18) {
				//计算评分 
				scoreService = new ScoreService(this, mWeightInfo, getCurrentUser());
				mScoreVo = scoreService.getScoreVo();
				main_tv_show.setVisibility(View.VISIBLE);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						main_tv_show.invalidate();
					}
				});
				scoreTotal = scoreService.getScoreTotal();
				scoreDesc = scoreService.getScoreDesc();
				//更新目标页的体重
				txtViewTargetWeight.setText(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", ""));
				if (isShowScore) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									nowWeighting = true;
									setShowMainItem(mWeightInfo, mScoreVo);
								}
							});
						}
					}, 3000);
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setShowMainItem(mWeightInfo, mScoreVo);
						}
					});
				}
			} else {
				//未成年人
				/**更新目标页的体重*/
				txtViewTargetWeight.setText(showWeightByUnit(mWeightInfo.getWeight(), 1).replace(".0", ""));
				loadYoung();
				/**更该目标值*/
				changeTargetValue(mWeightInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接收并处理实时 数据
	 * @param data
	 * @param sb 
	 * void 
	*/
	private void realTimeData(String data) {
		Log.d("datainfo", data.toString() + "===实时");
		if (realTimeAnimi == 1) {
			isShowScore = false;//实时称重时，将动画后面关闭
			//animiCheck(1);
			realTimeAnimi++;
			
			//resetDisplayCircle();
			
		}
		//实时体重数据
		float weight = (Integer.parseInt(data.substring(16, 20), 16)) * 0.01f;//实时体重
		//实时时间
		int currTime = Integer.parseInt(data.substring(8, 16), 16);//实时时间
		Log.d("YunMai", " 计算时间：" + DateHelper.timeString(currTime) + " currTime= " + currTime);
		//主界面显示实时体重
		if (weight <= 100) {
			//mDisplayCircleView.setWeight(Float.valueOf(showWeightByUnit(weight, 1)));
			//displayStaticCircle(Float.valueOf(showWeightByUnit(weight, 1)));
			//cusCirclarProgress.setCenterData(showWeightByUnit(weight, 1));
			//main_info_middle.setText(showWeightByUnit(weight, 1));
			//main_info_middle.setTextColor(Color.WHITE);
		}
		//提示信息修改为现在
//		mDisplayCircleView.setTopText("现在");
//		mDisplayCircleView.setScoreUnit(showUnitStr());
		//mDisplayCircleView.invalidate();
		
		displayStaticCircle(Float.valueOf(showWeightByUnit(weight, 1)), true);
		
		//cusCirclarProgress.setTopData("现在|");
		//cusCirclarProgress.setBottomData(showUnitStr());
		//cusCirclarProgress.invalidate();
		//customerTextViewTop.setUserData("现在|");
		//customerTextViewTop.invalidate();
		//单位修改
		//main_info_unit.setText(showUnitStr());
		//提示信息修改
		weight_info.setUserData("体重|");
		linearLayoutContent.setVisibility(View.INVISIBLE);//显示描述部分
		main_tv_show.setVisibility(View.INVISIBLE);
		main_tv_show.setUserData("|");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				weight_info.invalidate();
				main_tv_show.invalidate();
			}
		});
		//隐藏左侧分享按钮
		//rlBannerShowHidden.setVisibility(View.INVISIBLE);
	}

	/**
	 * TAB 页切换
	 * @param index
	 */
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			if (mGallery != null) {
				mScrollLayout.setmGallery(mGallery);
			}
			mScrollLayout.setCurrPage(0);
			tabOne.setTextColor(getResources().getColor(R.color.white));
			tabTwo.setTextColor(getResources().getColor(R.color.white));
			tabThree.setTextColor(getResources().getColor(R.color.white));
		} else if (index == 1) {
			if (mGallery != null) {
				mScrollLayout.setmGallery(mGallery);
			}
			mScrollLayout.setCurrPage(1);
			tabTwo.setTextColor(getResources().getColor(R.color.white));
			tabOne.setTextColor(getResources().getColor(R.color.white));
			tabThree.setTextColor(getResources().getColor(R.color.white));
		} else {
			if (mGallery != null) {
				mScrollLayout.setmGallery(mGallery);
			}
			if (mScrollLayout != null) {
				mScrollLayout.setmScrollLayout(mScrollLayout);
			}
			mScrollLayout.setCurrPage(2);
			tabThree.setTextColor(getResources().getColor(R.color.white));
			tabOne.setTextColor(getResources().getColor(R.color.white));
			tabTwo.setTextColor(getResources().getColor(R.color.white));
		}
	}

	/**TAB 页单击切换处理事件*/
	@Override
	public void onClick(View v) {
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	/**
	 * 下拉列
	 * @param context
	 */
	public void uploadImage(final Activity context) {
		ddspMenu = new DropDownSetPopupMenu(MainActivity.this, getCurrentUser(), new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClickUtils.isFastDoubleClick()) {
					return;
				}
				ddspMenu.dismiss();
			}
		});
		View view = MainActivity.this.findViewById(R.id.set);
		int xoffInPixels = ddspMenu.getWidth() - view.getWidth() + 10;
		ddspMenu.showAsDropDown(view, -xoffInPixels, 0);
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); //调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			toastCenter("再按一次将退出好轻");
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果1秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		} else {
			BaseActivityManager.getInstance().exit();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	@Override
	public void interpolatedTime(float interpolatedTime) {
		// 监听到翻转进度过半时，更新txtNumber显示内容。
		if (enableRefresh && interpolatedTime > 0.5f) {
			if (isShowScore) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (nowWeighting) {
							showMainData(mWeightInfo, false);
						} else {
							//查询最近一次称重数据
							if (null != lastChart) {
								//数据不为空时，直接显示
								WeightInfo wifs = lastChart.toWeightInfo();
								ScoreService interpolatedScoreService = new ScoreService(MainActivity.this, wifs,
										getCurrentUser());
								ScoreVo interpolatedScoreVo = interpolatedScoreService.getScoreVo();
								//加载动画
								loadAnimi(wifs, interpolatedScoreVo);
							}
						}
					}
				});
			}
			enableRefresh = false;
		}
	}

	private void setShowMainFirst(WeightInfo mWeightInfo, ScoreVo mScoreVo) throws NotFoundException {
		//装载主页数据
		loadMainData(mWeightInfo, mScoreVo);
		//左侧按钮弹出
		//rlBannerShowHidden.setVisibility(View.VISIBLE);
		//顶部显示体重
		String nowWeight = showWeightByUnit(mWeightInfo.getWeight(), 1).trim();
		//加载体重
		//上部分
		//displayStaticCircle(Float.valueOf(nowWeight));
		
		//cusCirclarProgress.setTopData("现在|");
		//cusCirclarProgress.setCenterData(nowWeight);
		//cusCirclarProgress.setBottomData(showUnitStr());
		//cusCirclarProgress.invalidate();
		
		//customerTextViewTop.setUserData("现在|");
		//customerTextViewTop.invalidate();
		//中间部分
		//main_info_middle.setText(nowWeight);//显示体重
		//下部分 --描述更新
		//main_info_unit.setText(showUnitStr());
		weight_info.setUserData("体重|");
		weight_info.invalidate();
		mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新简报数据
		//1、旋转动画
		llShowAnimiRunBar.setVisibility(View.VISIBLE);//显示
		//new Thread(new DisplayCircleRunnable(Float.valueOf(nowWeight),(int)mScoreVo.getScoreTotal(),true)).start();
		//displayCircleToAnimate(Float.valueOf(nowWeight),(int)mScoreVo.getScoreTotal());
		//displayCircleToAnimate(Float.valueOf(nowWeight));
		//cusCirclarProgress.startCartoom(2);//执行动画
		//控制翻转动画
		nowWeighting = false;
		enableRefresh = true;
		//控制是否显示评分
		isShowScore = true;
		//animiCheck(3000);
	}

	private void loadAnimi(WeightInfo mWeightInfo, ScoreVo mScoreVo) throws NotFoundException {
		//1、装载主页数据
		loadMainData(mWeightInfo, mScoreVo);
		//3、显示评分描述
		desPopAni = AnimationUtils.loadAnimation(MainActivity.this, R.anim.descpop);
		linearLayoutContent.setVisibility(View.VISIBLE);
		linearLayoutContent.startAnimation(desPopAni);
		//2、评分描述数据处理
		if (scoreDesc != null) {
			String[] description = scoreDesc.split(";");
			Random mRandom = new Random(4);
			String showDesc = description[mRandom.nextInt(description.length)];
			main_tv_show.setVisibility(View.VISIBLE);
			main_tv_show.setUserData(showDesc + "|");
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					main_tv_show.invalidate();
				}
			});
		}
		//左侧按钮弹出
		//rlBannerShowHidden.setVisibility(View.VISIBLE);
		//4、顶部显示体重
		String nowWeight = showWeightByUnit(mWeightInfo.getWeight(), 1).trim();
		//加载评分 
		//上部分
		//mDisplayCircleView.setTopText(nowWeight);
		mDisplayCircleView.setScores((int) scoreTotal);
		//mDisplayCircleView.setScoreUnit("分");
		mDisplayCircleView.invalidate();
		
		//cusCirclarProgress.setTopData(sb.toString());
		//cusCirclarProgress.setCenterData(String.valueOf((int) scoreTotal));
		//cusCirclarProgress.setBottomData("分");
		//cusCirclarProgress.invalidate();
		
		//customerTextViewTop.setUserData(sb.toString());
		//customerTextViewTop.invalidate();
		
		//中间部分
		//main_info_middle.setText(String.valueOf((int) scoreTotal));
		//下部分--描述更新
		//main_info_unit.setText("分");
		weight_info.setUserData("体检评分|");
		weight_info.invalidate();
		//4、开启倒计时动画
		staticWeight = true;//数字动画开启
		//评分动画
		if (staticWeight) {
			ctlTthread = new Thread(new ScoreAnimi(DataUtil.toInt(scoreTotal), true));
			ctlTthread.start();
			staticWeight = false;
		}
		mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新简报数据
	}

	@SuppressLint("NewApi")
	private void loadMainData(WeightInfo mWeightInfo, ScoreVo mScoreVo) throws NotFoundException {
		try {
			//列表：肌肉、健康情况描述
			txtViewMuscle.setText(mScoreVo.getIndexMuscleName());
			//txtViewBone.setText(mScoreVo.getIndexBoneName());
			txtViewBone.setText("标准");
			txtViewMoisture.setText(mScoreVo.getIndexWaterName());
			txtViewBmr.setText(mScoreVo.getIndexBmrName());
			txtViewTargeInfoSomaAge.setText(mScoreVo.getIndexSomaAgeName());
			txtViewBmi.setText(mScoreVo.getIndexBmiName());
			txtViewFat.setText(mScoreVo.getIndexFatName());
			//列表：底部背景图片变化
			rlViewTipBgMuscle.setBackground(getResources().getDrawable(checkType(mScoreVo.getIndexMuscleName())));
			//rlViewTipBgBone.setBackground(getResources().getDrawable(checkType(mScoreVo.getIndexBoneName())));
			rlViewTipBgBone.setBackground(getResources().getDrawable(checkType("标准")));
			rlViewTipBgMoisture.setBackground(getResources().getDrawable(checkType(mScoreVo.getIndexWaterName())));
			llViewTipBgBmr.setBackground(getResources().getDrawable(checkType(mScoreVo.getIndexBmrName())));
			rlViewTipBgBodyAge.setBackground(getResources().getDrawable(checkType(mScoreVo.getIndexSomaAgeName())));
			//BMI
			String getBmib = DataUtil.floatToString(mWeightInfo.getBmi(), 2);
			String nowBmi = MathUtils.subString(getBmib);
			String[] userBmi = nowBmi.split("\\.");
			StringBuffer sbLeft = new StringBuffer();
			MainDataUtil.setShowRoundItem(userBmi, sbLeft);
			customerTextViewLeft.setUserData(sbLeft.toString());
			customerTextViewLeft.setContext(MainActivity.this);
			//脂肪
			String getFat = DataUtil.floatToString(mWeightInfo.getFat(), 2);
			String nowFat = MathUtils.subString(getFat);
			String[] userFat = nowFat.split("\\.");
			StringBuffer sbRight = new StringBuffer();
			MainDataUtil.setShowRoundItem(userFat, sbRight);
			customerTextViewRight.setUserData(sbRight.toString());
			customerTextViewRight.setContext(MainActivity.this);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					customerTextViewLeft.invalidate();
					customerTextViewRight.invalidate();
				}
			});
			//
			tViewTargeInfoMuscle.setText(MathUtils.subString(DataUtil.floatToString(mWeightInfo.getMuscle(), 2)) + "%");
			if (mWeightInfo.getBone() != 0 && mWeightInfo.getBone() != 0) {
				float bone = mWeightInfo.getBone() / mWeightInfo.getWeight() * 100f;
				tViewTargeInfoBone.setText(DataUtil.floatToString(bone, 1) + "%");
			} else {
				tViewTargeInfoBone.setText("0%");
			}
			tViewTargeInfoMoisture.setText(MathUtils.subString(DataUtil.floatToString(mWeightInfo.getWater(), 2)) + "%");
			tViewTargeInfoBmr.setText((int) mWeightInfo.getBmr() + "");
			String showSomaAge = String.valueOf(mWeightInfo.getSomaAge());
			StringBuilder sbsame = new StringBuilder();
			sbsame.append(showSomaAge).append("岁");
			txtViewSomaAge.setText(sbsame.toString());//身体年龄，没有，现在显示的是kcal
			/**更该目标值*/
			changeTargetValue(mWeightInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changeTargetValue(WeightInfo mWeightInfo) throws SQLException, NumberFormatException {
		//查询目标体重
		currTarget = mScalesTargetService.getFindOne(getCurrentUId());
		//转换目标体重值
		int currTargetWeight = DataUtil.toInt(Float.parseFloat(showWeightByUnit(currTarget.getTargetWeight(), 1)));
		//转换当前体重
		float lastWeight;
		if (mWeightInfo != null) {//目标不为空的时候
			lastWeight = Float.parseFloat(showWeightByUnit(mWeightInfo.getWeight(), 1));
		} else {//第一次称重的时候
			lastWeight = fistTarget;
		}
		//当前修改目标体重
		nowOffset = currTargetWeight - lastWeight;
		offsetWeight = DataUtil.floatToString(nowOffset, 1).replace("-", "");
		String notUnit = showUnitStr();
		//如果当前目标为空,获取控件默认目标值
		if (nowOffset > 0) {
			//切换时的文字描述
			StringBuffer sbufer = new StringBuffer();
			sbufer.append("要达成这个目标，您还需要努力增重").append(offsetWeight).append(notUnit);
			txtViewTargetDesc.setText(sbufer.toString());
		} else if (nowOffset < 0) {
			//切换时的文字描述
			StringBuffer sbufer = new StringBuffer();
			sbufer.append("要达成这个目标，您还需要努力减重").append(offsetWeight).append(notUnit);
			txtViewTargetDesc.setText(sbufer.toString());
		} else {
			//切换时的文字描述
			StringBuffer sbufer = new StringBuffer();
			sbufer.append("您的目标是要保持：").append(offsetWeight).append(notUnit);
			txtViewTargetDesc.setText(sbufer.toString());
		}
	}

	/**根据用户健康评测结果显示不同背景图*/
	public int checkType(String bodyType) {
		String tempStr = bodyType.trim();
		if (bodyType == null || bodyType.equals("")) {
			return imgChange[1];//默认
		}
		if (tempStr.equals("偏高") || tempStr.equals("过高") || tempStr.equals("偏大")) {
			return imgChange[0];
		} else if (tempStr.equals("正常") || tempStr.equals("达标") || tempStr.equals("标准")) {
			return imgChange[1];
		} else if (tempStr.equals("偏低") || tempStr.equals("偏瘦") || tempStr.equals("未达标") || tempStr.equals("年轻")) {
			return imgChange[2];
		} else {
			return imgChange[1];//默认
		}
	}

	@SuppressLint("NewApi")
	private void setShowMainItem(WeightInfo mWeightInfo, ScoreVo mScoreVo) throws NotFoundException {
		loadMainData(mWeightInfo, mScoreVo);
		//左侧按钮弹出
		//rlBannerShowHidden.setVisibility(View.VISIBLE);
		//显示描述部分
		desPopAni = AnimationUtils.loadAnimation(this, R.anim.descpop);
		linearLayoutContent.setVisibility(View.VISIBLE);
		linearLayoutContent.startAnimation(desPopAni);
		//顶部显示体重
		String nowWeight = showWeightByUnit(mWeightInfo.getWeight(), 1).trim();
		//StringBuffer sb = new StringBuffer();
		//sb.append(nowWeight).append("|").append(showUnitStr().trim());
		//mDisplayCircleView.setTopText(nowWeight);
		mDisplayCircleView.setScores((int) scoreTotal);
		//mDisplayCircleView.setScoreUnit("分");
		mDisplayCircleView.invalidate();
		
		//cusCirclarProgress.setTopData(sb.toString());
		//cusCirclarProgress.setCenterData(String.valueOf((int) scoreTotal));
		//cusCirclarProgress.setBottomData("分");
		//cusCirclarProgress.invalidate();
		
		//customerTextViewTop.setUserData(sb.toString());
		//customerTextViewTop.invalidate();
		//评分
		//main_info_middle.setText(String.valueOf((int) scoreTotal));
		//评分描述
		if (scoreDesc != null) {
			String[] description = scoreDesc.split(";");
			Random mRandom = new Random();
			main_tv_show.setVisibility(View.VISIBLE);
			main_tv_show.setUserData(description[mRandom.nextInt(description.length)] + "|");
			main_tv_show.invalidate();
		}
		//描述更新
		//main_info_unit.setText("分");
		weight_info.setUserData("体检评分|");
		weight_info.invalidate();
		//线程启动
		if (staticWeight) {
			ctlTthread = new Thread(new ScoreAnimi(DataUtil.toInt(scoreTotal), true));
			ctlTthread.start();
		}
		mHandler.sendEmptyMessage(WhatMainUtil.WHAT_CHART_REFRESH);//刷新简报数据
	}

	/**评分动画*/
	class ScoreAnimi implements Runnable {
		private int count = 0;
		private boolean threadCheck = false;

		public ScoreAnimi(int count, boolean threadCheck) {
			this.count = count;
			this.threadCheck = threadCheck;
		}

		@Override
		public void run() {
			while (threadCheck) {
				try {
					synchronized (ACCESSIBILITY_SERVICE) {
						int countEnd = count - 8;
						for (int i = 0; i <= count; i++) {
							msgCount = i;
							if (i <= countEnd) {
								Thread.sleep(30);
							} else {
								int mod = i % 10;
								Thread.sleep(i * mod);
							}
							mHandler.sendEmptyMessage(WhatMainUtil.WHAT_SCORE_MSG);
							if (i == count) {
								threadCheck = false;
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		uploadImage(MainActivity.this);
		return false;
	}

	private static Boolean expiredTimeExit = false;

	public void scrollClickBtn() {
		Timer timeExpired = null;
		if (expiredTimeExit == false) {
			expiredTimeExit = true; // 准备退出
			runOnUiThread(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					tb_setTarget_weight.setAlpha(0.3f);
					tb_setTarget_weight.setEnabled(false);
				}
			});
			timeExpired = new Timer();
			timeExpired.schedule(new TimerTask() {
				@Override
				public void run() {
					expiredTimeExit = false; // 取消退出
					runOnUiThread(new Runnable() {
						@SuppressLint("NewApi")
						@Override
						public void run() {
							tb_setTarget_weight.setAlpha(1.0f);
							tb_setTarget_weight.setEnabled(true);
						}
					});
				}
			}, 1000); // 如果1秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
		}
	}
	
	private class DisplayCircleRunnable implements Runnable {
		private float weight = 0.0f;
		private boolean isSpin = false;
		
		public DisplayCircleRunnable(float weight, boolean isSpin){
			this.weight = weight;
			this.isSpin = isSpin;
		}
		@Override
		public void run() {
			// Counting the counts
			mDisplayCircleView.setWeight(weight);
			
			if (isSpin) {
				mDisplayCircleView.setBeginToSpin(true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for(int i = 1; i <= 100; i++) {
					mDisplayCircleView.setProgress(i);
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					 
				}
			}	
				
		}

	}
	
	private void resetDisplayCircle() {
		mDisplayCircleView.setWeight(0);
		mDisplayCircleView.setBeginToSpin(false);
		mDisplayCircleView.setSpinToEnd(false);
		mDisplayCircleView.setBeginToSlide(false);
		mDisplayCircleView.setSlideToEnd(false);
	}
	
	private void displayStaticCircle(float weight, boolean isRealTime) {		
//		if (mDisplayCircleView.getVisibility() == View.INVISIBLE) {
//			flipImageView.setVisibility(View.INVISIBLE);
//			flipImageView.setRotationReversed(true);
//			flipImageView.setDrawable(null);
//			flipImageView.setFlippedDrawable(null);
//			mDisplayCircleView.setVisibility(View.VISIBLE);
//			//flipImageView.toggleFlip();
//			flipImageView.invalidate();
//		}
		
//		if (isRealTime && isRealTimeToggled) {
//			FlipAnimateHelper.toFlipImageView(flipImageView);
//			isRealTimeToggled = false;
//		}
//		
//		FlipAnimate fa = new FlipAnimate(weight, 0, false);
//		fa.execute(10);
		
		isRealTimeDisplay = isRealTime;
		
		FlipAnimationDisplay fad = new FlipAnimationDisplay(weight, 0, false);
		fad.execute();
	}
	
	private void displayCircleToAnimate(float weight, int scores) {
//		FlipAnimate fa = new FlipAnimate(weight, scores, true);
//		fa.execute(10);	
//		
//		isRealTimeToggled = true;
		
		FlipAnimationDisplay fad = new FlipAnimationDisplay(weight, scores, true);
		fad.execute();
	}

	private void initFlipImageView(float weight, int scores) {
		flipImageView = new FlipImageView(this);
		
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mDisplayCircleView.getLayoutParams().width, mDisplayCircleView.getLayoutParams().width);
		lp.gravity = Gravity.CENTER;
		flipImageView.setLayoutParams(lp);
		//flipImageView.setLayoutParams(new LayoutParams(mDisplayCircleView.getLayoutParams().width, mDisplayCircleView.getLayoutParams().width));
		flipImageView.setScaleType(ScaleType.CENTER);
		flipImageView.setVisibility(View.INVISIBLE);
		//flipImageView.
		
		mFrameLayout.addView(flipImageView);
		
		mDisplayCircleViewBack = new DisplayCircleView(this, null);
		mDisplayCircleViewBack.setUnit(getString(R.string.setting_jin));
		mDisplayCircleViewBack.setScoreUnit(getString(R.string.dis_score_unit));
		
		mDisplayCircleViewFront = new DisplayCircleView(this, null);
		mDisplayCircleViewFront.setUnit(getString(R.string.setting_jin));
		mDisplayCircleViewFront.setScoreUnit(getString(R.string.dis_score_unit));
	
		FlipAnimateHelper.initDisplayView(mDisplayCircleViewFront, this, weight, scores, true, mDisplayCircleView.getLayoutParams().width, 0);
		FlipAnimateHelper.initDisplayView(mDisplayCircleViewBack, this, weight, scores, false, mDisplayCircleView.getLayoutParams().width, 0);
		
		Bitmap b = FlipAnimateHelper.saveViewToBitmap(mDisplayCircleViewBack);
		BitmapDrawable bb = new BitmapDrawable(getResources(), b);
		
		Bitmap b1 = FlipAnimateHelper.saveViewToBitmap(mDisplayCircleViewFront);
		BitmapDrawable bb1 = new BitmapDrawable(getResources(), b1);
		
		flipImageView.setFlippedDrawable(bb);
		flipImageView.setDrawable(bb1);
	}
	
	private void initDisplayCircleFront(float weight, int progress) {
		mDisplayCircleViewFront = new DisplayCircleView(this, null);
		mDisplayCircleViewFront.setUnit(getString(R.string.setting_jin));
		mDisplayCircleViewFront.setScoreUnit(getString(R.string.dis_score_unit));
		
		FlipAnimateHelper.initDisplayView(mDisplayCircleViewFront, this, weight, 0, true, flipImageView.getLayoutParams().width, progress);
	}
	
	private void initDisplayCircleBack(float weight, int scores) {
		mDisplayCircleViewBack = new DisplayCircleView(this, null);
		mDisplayCircleViewBack.setUnit(getString(R.string.setting_jin));
		mDisplayCircleViewBack.setScoreUnit(getString(R.string.dis_score_unit));
		
		FlipAnimateHelper.initDisplayView(mDisplayCircleViewBack, this, weight, scores, false, flipImageView.getLayoutParams().width, 0);
	}
	
	private void initFlipImageViewFront(float weight, int progress) {
		initDisplayCircleFront(weight, progress);
		
		Bitmap b1 = FlipAnimateHelper.saveViewToBitmap(mDisplayCircleViewFront);
		BitmapDrawable bb1 = new BitmapDrawable(getResources(), b1);
		
		flipImageView.setDrawable(bb1);
	}
	
	private void initFlipImageViewBack(float weight, int scores) {
		initDisplayCircleBack(weight, scores);
		
		Bitmap b = FlipAnimateHelper.saveViewToBitmap(mDisplayCircleViewBack);
		BitmapDrawable bb = new BitmapDrawable(getResources(), b);
		
		flipImageView.setFlippedDrawable(bb);
	}
	
	public class FlipAnimate extends AsyncTask<Integer, Integer, String> {

		private float counts = 0.0f;
		private boolean isSpin = false;
		private int scores = 0;
		private boolean isCounting = false;
	
		public FlipAnimate(float counts, int scores, boolean isSpin) {
			this.counts = counts;
			this.isSpin = isSpin;
			this.scores = scores;
		}

		@Override
		protected String doInBackground(Integer... params) {
			spinArcView.setProgress(0);
			
			if (isSpin) {
				// Spin the arc
				for (int i = 0; i <= 101; i++) {
					this.publishProgress(i);
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					Thread.sleep(576);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				isCounting = true;
				
				// Counting
				int countBeginScore = (scores - 20) > 0 ? scores - 20 : 0;
				
				for (int i = countBeginScore; i < scores; i++) {
					this.publishProgress(i);
					
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				this.publishProgress(0);
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (isSpin) {
				if ((progress[0] == 0) && !isCounting) {
					initFlipImageViewFront(counts, 0);
				}
				
				if (!isCounting) {
					spinArcView.setProgress(progress[0]);
					
					if (progress[0] == 101) {
						initFlipImageViewFront(counts, 100);
						initFlipImageViewBack(counts, (scores - 20) > 0 ? scores - 20 : 0);
						spinArcView.setProgress(0);
						FlipAnimateHelper.toFlipImageView(flipImageView);						
					}
				} else {
					initFlipImageViewBack(counts, progress[0]);
				}
				
			} else {
		
			}
			
		}
		
		@Override
		protected void onPostExecute(String result) {		
			if (!isSpin) {
				initFlipImageViewFront(counts, 0);
			}

		}
	}
	
	public class FlipAnimationDisplay extends AsyncTask<Integer, Integer, String> {
		
		private float counts = 0.0f;
		private boolean isSpin = false;
		private int scores = 0;
		private boolean isCounting = false;
		//AnimationSet animationSet = null;
		private boolean toFlip = false;
		private boolean isRealTimeToggled = false;
	
		public FlipAnimationDisplay(float counts, int scores, boolean isSpin) {
			this.counts = counts;
			this.isSpin = isSpin;
			this.scores = scores;
			
			if (isSpin) {
				mDisplayCircleView.setWeight(counts);
				
			}
		}

		@Override
		protected String doInBackground(Integer... arg0) {
			if (!isSpin) {
				if (isRealTimeDisplay) {
					isRealTimeToggled = false;
					this.publishProgress(0);
					
					try {
						Thread.sleep(440);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				this.publishProgress(1);
			} else {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Spin the arc
				for (int i = 0; i <= 100; i++) {
					this.publishProgress(i);
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Flip the circle
				toFlip = true;
				this.publishProgress(0);
				
				// Change the back side
				try {
					Thread.sleep(440);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				this.publishProgress(1);
				
				toFlip = false;
				
				// Counting the scores
				isCounting = true;
				for (int i = (scores - 20) > 0 ? scores - 19 : 1; i < scores; i++) {
					this.publishProgress(i);
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			return null;
		}
		
		@Override 
		protected void onProgressUpdate(Integer... progress) {
			if (!isSpin) {
				mDisplayCircleView.setBeginToSpin(false);
				mDisplayCircleView.setSlideToEnd(false);
				mDisplayCircleView.setSpinToEnd(false);
				
				if (!isRealTimeToggled && isRealTimeDisplay) {
					FlipAnimation fa = new FlipAnimation(mDisplayCircleView, mDisplayCircleView);
					mDisplayCircleView.startAnimation(fa);
					
					isRealTimeToggled = true;
				} else {
					
					mDisplayCircleView.setWeight(counts);
				}
			} else {
				
				// Spin the arc
				if (!toFlip && !isCounting) {
					spinArcView.setProgress(progress[0]);
					
					
					if (progress[0] == 100) {
						spinArcView.setProgress(0);
						mDisplayCircleView.setBeginToSpin(true);
						mDisplayCircleView.setProgress(100);
						
					}
					
				}
				
				if (toFlip && progress[0] == 0) {
					// Flip the circle
					FlipAnimation fa = new FlipAnimation(mDisplayCircleView, mDisplayCircleView);
					mDisplayCircleView.startAnimation(fa);
				}
				
				if (toFlip && progress[0] == 1) {
					mDisplayCircleView.setSpinToEnd(true);
					mDisplayCircleView.setSlideToEnd(true);
					mDisplayCircleView.setScores((scores - 20) > 0 ? scores - 20 : 0);
				}
				
				if (isCounting) {
					mDisplayCircleView.setSpinToEnd(true);
					mDisplayCircleView.setSlideToEnd(true);
					mDisplayCircleView.setScores(progress[0]);
				}
			}
		}
		
		@Override 
		protected void onPostExecute(String result) {
			
		}
		
	}
	
}
