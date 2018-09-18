package com.irisking.scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.irisking.ia.w.DnSmpDetect;
import com.irisking.irisalgo.bean.IKEnrIdenStatus;
import com.irisking.irisalgo.util.Config;
import com.irisking.irisalgo.util.EnrFeatrueStruct;
import com.irisking.irisalgo.util.EnumDeviceType;
import com.irisking.irisalgo.util.EnumEyeType;
import com.irisking.irisalgo.util.FeatureList;
import com.irisking.irisalgo.util.IKALGConstant;
import com.irisking.irisalgo.util.Person;
import com.irisking.irisapp.R;
import com.irisking.scanner.bean.IrisUserInfo;
import com.irisking.scanner.callback.CameraPreviewCallback;
import com.irisking.scanner.callback.IrisProcessCallback;
import com.irisking.scanner.database.SqliteDataBase;
import com.irisking.scanner.model.EyePosition;
import com.irisking.scanner.presenter.IrisConfig;
import com.irisking.scanner.presenter.IrisPresenter;
import com.irisking.scanner.util.ImageUtil;
import com.irisking.scanner.util.TimeArray;
import com.irisking.scanner.utils.DBHelper;
import com.irisking.scanner.utils.LogUtil;
import com.irisking.scanner.view.adapter.ShowUserInfoRecyclerViewAdapter;
import com.irisking.scanner.view.adapter.SpacesItemDecoration;
import com.irisking.scanner.view.custom.CustomBottomGuideView;
import com.irisking.scanner.view.custom.CustomLeftThermometerView;
import com.irisking.scanner.view.custom.CustomRightGuideView;
import com.irisking.scanner.view.custom.CustomRightThermometerView;
import com.irisking.scanner.view.custom.CustomTopGuideView;
import com.irisking.scanner.view.custom.EyeScannerView;
import com.irisking.scanner.view.custom.RoundProgressBar;
import com.irisking.scanner.view.custom.dialog.CustomPromptDialog;
import com.irisking.scanner.view.custom.dialog.CustomTextViewDialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 主文件，完成界面显示，UI控件控制等逻辑
public class MainActivity extends Activity implements OnClickListener {

    private TimeArray uvcTimeArray = new TimeArray();

    private String curName = "default";

    public boolean previewParaUpdated = false;

    // ============声音播放器=============
    private MediaPlayer soundEnrollSuccess = null;
    private MediaPlayer soundIdentifySuccess = null;
    private MediaPlayer soundCloser = null;
    private MediaPlayer soundFarther = null;
    private MediaPlayer soundLeft = null;
    private MediaPlayer soundRight = null;
    private MediaPlayer soundDown = null;
    //===================================


    //===============控件================
//	private EyeView mEyeView; // 显示提示框的view界面
    private RadioButton mIrisRegisterRBtn;
    private RadioButton mIrisRecognizeRBtn;
    private RadioButton mIrisContinueRBtn;
    private TextView mResultTextViewEnrRecFinal;
    private EditText mUserNameEditText; // 显示用户名
    private TextView mFrameRateTextView; // 帧率显示文本
    private IrisPresenter mIrisPresenter;
    private SurfaceView svCamera;

    private int mCurDist;
    private TextView mCurrentEyeDistance;

    private List<Map<String, String>> userMap;
    private List<IrisUserInfo> userInfo;

    private LinearLayout mRecyclerViewLayout;

    private RecyclerView mRecyclerView;
    private ShowUserInfoRecyclerViewAdapter recyclerViewAdapter;
    //===================================

    //=========画IR图像=========
    private SurfaceHolder holder;
    private Bitmap uBitmap;
    private Matrix matrix;

    public int eyeViewWidth = 0;
    public int eyeViewHeight = 0;
    //==========================

    // ======load feature list==========

    FeatureList irisLeftData = new FeatureList();
    FeatureList irisRightData = new FeatureList();


    private SqliteDataBase sqliteDataBase;

    ArrayList<IrisUserInfo> leftEyeList = new ArrayList<>();//从数据库中获取的所有用户左眼特征的集合
    ArrayList<IrisUserInfo> rightEyeList = new ArrayList<>();//从数据库中获取的所有用户右眼特征的集合

    private int maxFeatureCount = 900;
    //==================================
    //屏幕中双眼的坐标位置
    private float eyeX1;
    private float eyeX2;
    private float eyeHeight;
    private RoundProgressBar mRoundProgressBar;
    private ProgressBar custom_ProgressBar;

    private CustomLeftThermometerView mLeftThermometerView;
    private CustomRightThermometerView mRightThermometerView;
    private int mMinProgress = 13;
    private EyeScannerView mLeftEyeView;
    private EyeScannerView mRightEyeView;
    private EyeScannerView mEyeScannerView;

    private IrisConfig.EnrollConfig mEnrollConfig;
    private IrisConfig.IdentifyConfig mIdentifyConfig;

    private CountDownTimer mCountDownTimer;
    private CustomTextViewDialog customTextViewDialog;
    private CustomPromptDialog customPromptDialog;
    private CustomTopGuideView mTopGuideView;
    private CustomBottomGuideView mBottomGuideView;
    private CustomRightGuideView mRightGuideView;

    private CheckBox cbDetectFakeEye1;
    private CheckBox cbDetectFakeEye2;

    private int state;
    private static final int STATE_REGISTER_FLAG = 1;
    private static final int STATE_IDENTIFY_FLAG = 2;
    private static final int STATE_CONTINUE_FLAG = 3;

    private int lastIndex;

    //以分辨率1920*1080为基准
    int defaultWidth = 1080;
    int defaultHeight = 1920;
    //当前屏幕分辨率与基准分辨率1920*1080的比值
    float optWidth = 0.0f;
    float optHeight = 0.0f;

    private DnSmpDetect dnSmpDetect;

    Configuration mConfiguration;
    int orientation;

    private static String logPath = "/sdcard/IrisTest/";
    private long startTime;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenUiAdjust();

        Intent intent = getIntent();
        int cameraId = intent.getIntExtra("cameraid", 1);
        int previewImW = intent.getIntExtra("width", 1);
        int previewImH = intent.getIntExtra("height", 1);

        EnumDeviceType.getCurrentDevice().setCameraId(cameraId);
        EnumDeviceType.getCurrentDevice().setPreviewWidth(previewImW);
        EnumDeviceType.getCurrentDevice().setPreviewHeight(previewImH);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 全屏，不出现图标
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_iris_recognition);
        sqliteDataBase = SqliteDataBase.getInstance(this);
        // 设置语音
        initSound();
        initUI();
        dnSmpDetect = DnSmpDetect.getInstance(this);
        this.vibrator = (Vibrator) super.getSystemService(Service.VIBRATOR_SERVICE);

        if (Config.DEVICE_USBCAMERA) {
            mIrisPresenter = new IrisPresenter(this, uvcPreviewCallback);
        } else {
            mIrisPresenter = new IrisPresenter(this, irPreviewCallback);
        }

        mIrisPresenter.updateReserveInfo(0);

        mEnrollConfig = new IrisConfig.EnrollConfig();
        mIdentifyConfig = new IrisConfig.IdentifyConfig();

        initIrisData();
        updateUserInfo();
    }

    @Override
    protected void onStart() {
        mIrisPresenter.resume();
        cbDetectFakeEye1.setChecked(Config.IfDetectFakeEye);
        cbDetectFakeEye2.setChecked(Config.IfDetectFakeEye);
        super.onStart();
    }

    private void initSound() {
        this.soundEnrollSuccess = MediaPlayer.create(this, R.raw.enrsucc);
        this.soundIdentifySuccess = MediaPlayer.create(this, R.raw.bamboo);
        this.soundCloser = MediaPlayer.create(this, R.raw.closer);
        this.soundFarther = MediaPlayer.create(this, R.raw.farther);
        this.soundLeft = MediaPlayer.create(this, R.raw.moveleft);
        this.soundRight = MediaPlayer.create(this, R.raw.moveright);
        this.soundDown = MediaPlayer.create(this, R.raw.countdown);
    }

    private void initIrisData() {

        irisLeftData = new FeatureList(maxFeatureCount, "L");
        irisRightData = new FeatureList(maxFeatureCount, "R");

        // 2017.09.05 10:25修改，从数据库查询所有特征文件
        leftEyeList = (ArrayList<IrisUserInfo>) sqliteDataBase.queryLeftFeature();

        rightEyeList = (ArrayList<IrisUserInfo>) sqliteDataBase.queryRightFeature();

        if (leftEyeList.size() == 0 || leftEyeList == null && rightEyeList.size() == 0 || rightEyeList == null) {
            return;
        }

        irisLeftData.clear();
        irisRightData.clear();

        for (int i = 0; i < leftEyeList.size(); i++) {
            irisLeftData.add(new Person(leftEyeList.get(i).m_UserName, leftEyeList.get(i).m_Uid, 1), EnumEyeType.LEFT,
                    leftEyeList.get(i).m_LeftTemplate);
        }

        for (int i = 0; i < rightEyeList.size(); i++) {
            irisRightData.add(new Person(rightEyeList.get(i).m_UserName, rightEyeList.get(i).m_Uid, 1), EnumEyeType.RIGHT,
                    rightEyeList.get(i).m_RightTemplate);
        }
        mIrisPresenter.setIrisData(irisLeftData, irisRightData, null);//需要把特征传入jar包，以便识别
    }

    @Override
    protected void onStop() {
        resetUI();
        mIrisPresenter.pause();
        //显示上部引导界面
        mTopGuideView.setVisibility(View.VISIBLE);
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    private void drawImage() {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (textureBuffer == null) {
                textureBuffer = new int[width * height];
            }
            if (uBitmap == null) {
                uBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            ImageUtil.getBitmap8888(downSample, height, width, 0, 0, width - 1, height - 1, textureBuffer, rotateAngle, 1);

            uBitmap.setPixels(textureBuffer, 0, width, 0, 0, width, height);

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.scale(-1, 1, eyeViewWidth / 2.0f, eyeViewHeight / 2.0f);
                canvas.drawBitmap(uBitmap, matrix, null);
                holder.unlockCanvasAndPost(canvas);
            }
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (textureBuffer == null) {
                textureBuffer = new int[width * height];
            }
            if (uBitmap == null) {
                uBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            ImageUtil.getBitmap8888(downSample, height, width, 0, 0, width - 1, height - 1, textureBuffer, rotateAngle, 1);

            uBitmap.setPixels(textureBuffer, 0, width, 0, 0, width, height);

            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.scale(-1, 1, eyeViewWidth / 3.0f, eyeViewHeight / 3.0f);
                canvas.drawBitmap(uBitmap, matrix, null);
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 屏幕UI调整
     */
    private void screenUiAdjust() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels; // 获取屏幕的宽
        int screenHeight = metrics.heightPixels;// 获取屏幕的高
        mConfiguration = this.getResources().getConfiguration();

        orientation = mConfiguration.orientation;// 获取屏幕方向

        DecimalFormat df = new DecimalFormat("0.00");
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            eyeViewWidth = screenWidth;// 如果是竖屏，预览区域的宽为屏幕的宽
            // 由于图像是16:9的图像
            eyeViewHeight = (int) (eyeViewWidth / 1.777f);

            //当前屏幕分辨率与基准分辨率1920*1080的比值
            optWidth = (float) screenWidth / defaultWidth;
            optHeight = (float) screenHeight / defaultHeight;

            eyeX1 = Float.parseFloat(df.format(optWidth)) * 315;
            eyeX2 = Float.parseFloat(df.format(optWidth)) * 765;

            eyeHeight = Float.parseFloat(df.format(((float) eyeViewHeight / 3) * optHeight)) + 100;
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            eyeViewWidth = (int) (screenWidth * 0.8f);
            // 由于图像是16:9的图像
            eyeViewHeight = (int) (eyeViewWidth / 1.777f);
            // 在1920*1080的分辨率下，双眼的横坐标点为315，765，在不同手机分辨率下做出不同调整，手机横向分辨率 / 1080 * 315再减去预览区域距离左边的距离
            //当前屏幕分辨率与基准分辨率1920*1080的比值
            optWidth = (float) screenWidth / defaultWidth;
            optHeight = (float) screenHeight / defaultHeight;

            eyeX1 = Float.parseFloat(df.format(optWidth)) * 120;
            eyeX2 = Float.parseFloat(df.format(optWidth)) * 430;

            eyeHeight = Float.parseFloat(df.format(((float) eyeViewHeight / 3) * optHeight)) + 120;
            Log.e("tony", "screenUiAdjust eyeHeight: " + eyeHeight);
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE); // 全屏，不出现图标
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    @SuppressLint("HandlerLeak")
    Handler surfaceviewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                drawImage();
            } else if (msg.what == 2) {
            } else if (msg.what == 3) {
                mResultTextViewEnrRecFinal.setText(msg.obj.toString());

                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mLeftThermometerView.setProgress(mCurDist);
                    mLeftThermometerView.invalidate();
                } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    mRightThermometerView.setProgress(mCurDist);
                    mRightThermometerView.invalidate();
                }
            } else if (msg.what == 4) {
                resetUI();
            } else if (msg.what == 5) {
                mIdentifyConfig.irisMode = IKALGConstant.IR_IM_EYE_UNDEF;
                mIdentifyConfig.overTime = 60;
                mIdentifyConfig.reserve |= IKALGConstant.RESERVE_INFO_I_CONSTANT_LIGHT;
                mIdentifyConfig.reserve |= IKALGConstant.RESERVE_INFO_I_CONSTANT_PREVIEW;
                mIrisPresenter.startIdentify(mIdentifyConfig, processCallback);

            }
        }
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.rgb(0, 0, 0));
            holder.unlockCanvasAndPost(canvas);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    // Init view
    private void initUI() {

        svCamera = findViewById(R.id.iv_camera);
        LayoutParams svParams = svCamera.getLayoutParams();
        svParams.width = eyeViewWidth;
        svParams.height = eyeViewHeight;
        svCamera.setLayoutParams(svParams);

        holder = svCamera.getHolder();
        holder.addCallback(surfaceCallback);
        matrix = new Matrix();

        mRoundProgressBar = findViewById(R.id.roundProgress);
        mRoundProgressBar.setXAndY(eyeX1, eyeX2, eyeHeight);// 设置双眼progressbar的位置
        Log.e("tony", "initUI eyeHeight: " + eyeHeight);

        //横向ProgressBar
        custom_ProgressBar = findViewById(R.id.horizontal_progressBar);

        mRecyclerViewLayout = findViewById(R.id.ll_recycler);
        mRecyclerView = findViewById(R.id.lv_user_info);

        // Init button
        mIrisRegisterRBtn = findViewById(R.id.start_register_rb);
        mIrisRegisterRBtn.setOnClickListener(this);
        mIrisRecognizeRBtn = findViewById(R.id.start_recognize_rb);
        mIrisRecognizeRBtn.setOnClickListener(this);
        mIrisContinueRBtn = findViewById(R.id.continue_identify_rb);
        mIrisContinueRBtn.setOnClickListener(this);

        cbDetectFakeEye1 = findViewById(R.id.cb_detectfake1);
        cbDetectFakeEye1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Config.IfDetectFakeEye = isChecked;
            }
        });
        cbDetectFakeEye2 = findViewById(R.id.cb_detectfake2);
        cbDetectFakeEye2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Config.IfDetectFakeEye = isChecked;
            }
        });

        cbDetectFakeEye1.setChecked(Config.IfDetectFakeEye);
        cbDetectFakeEye2.setChecked(Config.IfDetectFakeEye);
        Config.IfDetectFakeEye = false;

        if (Config.IfReleaseVersion) {
            mFrameRateTextView.setVisibility(View.GONE);
        }

        mResultTextViewEnrRecFinal = findViewById(R.id.ie_final_result);
        mCurrentEyeDistance = findViewById(R.id.tv_current_eye_distance);


        previewParaUpdated = false;

        mLeftThermometerView = findViewById(R.id.custom_left_thermometer_view);
        mRightThermometerView = findViewById(R.id.custom_right_thermometer_view);

        mEyeScannerView = new EyeScannerView(this);
        mLeftEyeView = findViewById(R.id.leftEye);
        mRightEyeView = findViewById(R.id.rightEye);

        mTopGuideView = findViewById(R.id.top_guide_view);
        mBottomGuideView = findViewById(R.id.bottom_guide_view);
        mRightGuideView = findViewById(R.id.right_guide_view);
        mRecyclerViewLayout.setVisibility(View.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int leftWidth = mLeftEyeView.getWidth();
        int leftHeight = mLeftEyeView.getHeight();

        RelativeLayout.LayoutParams pL = (RelativeLayout.LayoutParams) mLeftEyeView.getLayoutParams();
        pL.leftMargin = (int) (eyeX1 - leftWidth / 2);
        pL.topMargin = (int) (eyeHeight - leftHeight / 2);

        mLeftEyeView.setLayoutParams(pL);

        int rightWidth = mRightEyeView.getWidth();
        int rightHeight = mRightEyeView.getHeight();

        RelativeLayout.LayoutParams pR = (RelativeLayout.LayoutParams) mRightEyeView.getLayoutParams();
        pR.leftMargin = (int) (eyeX2 - rightWidth / 2);
        pR.topMargin = (int) (eyeHeight - rightHeight / 2);

        mRightEyeView.setLayoutParams(pR);
    }

    private boolean isActive = false;

    @Override
    public void onClick(View v) {
        boolean result;

        LogUtil.saveToFile(logPath + curName + "/" + curName + ".txt");

        cbDetectFakeEye1.setChecked(Config.IfDetectFakeEye);
        cbDetectFakeEye2.setChecked(Config.IfDetectFakeEye);

        switch (v.getId()) {
            case R.id.start_register_rb: // 虹膜注册
                state = STATE_REGISTER_FLAG;
                hideGuideView();
                recyclerViewAdapter = new ShowUserInfoRecyclerViewAdapter(MainActivity.this, userMap);
                mRecyclerView.setAdapter(recyclerViewAdapter);
                List<IrisUserInfo> usersExists = sqliteDataBase.queryAll();
                if (usersExists != null && usersExists.size() >= 5) {
                    mTopGuideView.setVisibility(View.VISIBLE);
                    //显示对话框
                    customTextViewDialog = new CustomTextViewDialog(MainActivity.this);
                    customTextViewDialog.setMessage("最多可注册5个用户\n请删除某个用户后再注册");
                    customTextViewDialog.show();
                    //对话框默认三秒消失
                    delayDialogDismiss(customTextViewDialog);
                    //删除用户、修改用户名按钮设置可点击
                    recyclerViewAdapter.setActionEnabled(true);
                    return;
                } else {
                    if (isActive) {
                        resetUI();
                        //显示上部引导界面
                        mTopGuideView.setVisibility(View.VISIBLE);
                    } else {
                        isActive = true;
                        //删除用户、修改用户名按钮设置可点击
                        recyclerViewAdapter.setActionEnabled(true);
                        mIrisRegisterRBtn.setText("停止注册");
                        mIrisRecognizeRBtn.setEnabled(false);
                        mIrisContinueRBtn.setEnabled(false);

                        svCamera.setKeepScreenOn(true);

                        mEnrollConfig.irisMode = IKALGConstant.IR_IM_EYE_BOTH;
                        mEnrollConfig.irisNeedCount = 9;
                        mEnrollConfig.overTime = 60;
                        mEnrollConfig.singleUse = false;
                        mIrisPresenter.startEnroll(mEnrollConfig, processCallback);
                        LogUtil.e("iris_info", "按钮点击  开始注册");
                        startTime = System.currentTimeMillis();
                        startCountDownTimer(60000);
                        mLeftEyeView.play();
                        mRightEyeView.play();

                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            mLeftThermometerView.setProgress(mMinProgress);
                            mLeftThermometerView.invalidate();
                        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mRightThermometerView.setProgress(mMinProgress);
                            mRightThermometerView.invalidate();
                        }
                    }
                }
                //删除用户、修改用户名按钮设置不可点击
                recyclerViewAdapter.setActionEnabled(false);
                break;
            case R.id.start_recognize_rb: // 单独虹膜识别
                state = STATE_IDENTIFY_FLAG;
                hideGuideView();
                recyclerViewAdapter = new ShowUserInfoRecyclerViewAdapter(MainActivity.this, userMap);
                mRecyclerView.setAdapter(recyclerViewAdapter);
                result = sqliteDataBase.queryAllResult();
                if (result) {
                    if (isActive) {
                        resetUI();
                        //显示上部引导界面
                        mTopGuideView.setVisibility(View.VISIBLE);
                    } else {
                        isActive = true;
                        //删除用户、修改用户名按钮设置可点击
                        recyclerViewAdapter.setActionEnabled(true);
                        mIrisRecognizeRBtn.setText("停止识别");
                        mIrisRegisterRBtn.setEnabled(false);
                        mIrisContinueRBtn.setEnabled(false);

                        svCamera.setKeepScreenOn(true);
                        mIdentifyConfig.irisMode = IKALGConstant.IR_IM_EYE_UNDEF;
                        mIdentifyConfig.overTime = 30;

                        mIrisPresenter.startIdentify(mIdentifyConfig, processCallback);
                        LogUtil.e("iris_info", "按钮点击  开始识别");
                        startTime = System.currentTimeMillis();
                        startCountDownTimer(30000);
                        mLeftEyeView.play();
                        mRightEyeView.play();

                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            mLeftThermometerView.setProgress(mMinProgress);
                            mLeftThermometerView.invalidate();
                        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mRightThermometerView.setProgress(mMinProgress);
                            mRightThermometerView.invalidate();
                        }
                    }
                } else {
                    customPromptDialog = new CustomPromptDialog(MainActivity.this);
                    customPromptDialog.setMessage("没有特征,请先注册");
                    customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
                        @Override
                        public void onConfirmClick() {
                            customPromptDialog.dismiss();
                        }
                    });
                    customPromptDialog.show();
                    delayDialogDismiss(customPromptDialog);
                    //显示上部引导界面
                    mTopGuideView.setVisibility(View.VISIBLE);
                }
                //删除用户、修改用户名按钮设置不可点击
                recyclerViewAdapter.setActionEnabled(false);
                break;
            case R.id.continue_identify_rb: //连续识别
                state = STATE_CONTINUE_FLAG;
                hideGuideView();
                recyclerViewAdapter = new ShowUserInfoRecyclerViewAdapter(MainActivity.this, userMap);
                mRecyclerView.setAdapter(recyclerViewAdapter);
                result = sqliteDataBase.queryAllResult();
                if (result) {
                    if (isActive) {
                        resetUI();
                        //显示上部引导界面
                        mTopGuideView.setVisibility(View.VISIBLE);
                        if (lastIndex != -1) {
                            recyclerViewAdapter.setContinueSelectOrderColor(lastIndex);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        tipTime = System.currentTimeMillis();
                        isActive = true;
                        //删除用户、修改用户名按钮设置可点击
                        recyclerViewAdapter.setActionEnabled(true);
                        mIrisContinueRBtn.setText("停止识别");
                        mIrisRegisterRBtn.setEnabled(false);
                        mIrisRecognizeRBtn.setEnabled(false);

                        mIdentifyConfig.irisMode = IKALGConstant.IR_IM_EYE_UNDEF;
                        mIdentifyConfig.overTime = 60;
                        mIdentifyConfig.reserve |= IKALGConstant.RESERVE_INFO_I_CONSTANT_LIGHT;
                        mIdentifyConfig.reserve |= IKALGConstant.RESERVE_INFO_I_CONSTANT_PREVIEW;
                        mIrisPresenter.startIdentify(mIdentifyConfig, processCallback);

                        startCountDownTimer(30000);
                        mLeftEyeView.play();
                        mRightEyeView.play();

                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            mLeftThermometerView.setProgress(mMinProgress);
                            mLeftThermometerView.invalidate();
                        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                            mRightThermometerView.setProgress(mMinProgress);
                            mRightThermometerView.invalidate();
                        }
                    }
                } else {
                    customPromptDialog = new CustomPromptDialog(MainActivity.this);
                    customPromptDialog.setMessage("没有特征,请先注册");
                    customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
                        @Override
                        public void onConfirmClick() {
                            customPromptDialog.dismiss();
                        }
                    });
                    customPromptDialog.show();
                    delayDialogDismiss(customPromptDialog);
                    //显示上部引导界面
                    mTopGuideView.setVisibility(View.VISIBLE);
                    resetUI();
                }
                //删除用户、修改用户名按钮设置不可点击
                recyclerViewAdapter.setActionEnabled(false);
                break;
        }
    }

    public void resetUI() {
        isActive = false;
        maxLeft = 0;
        maxRight = 0;

        mResultTextViewEnrRecFinal.setText(" ");

        mIrisRegisterRBtn.setText("开始注册");
        mIrisRegisterRBtn.setEnabled(true);

        mIrisRecognizeRBtn.setText("开始识别");
        mIrisRecognizeRBtn.setEnabled(true);

        mIrisContinueRBtn.setText("连续识别");
        mIrisContinueRBtn.setEnabled(true);

        mIrisPresenter.updateReserveInfo(0);
        mIrisPresenter.stopAlgo();
        mRoundProgressBar.setLeftAndRightProgress(0, 0, 0);
        //左右眼动画暂停
        mLeftEyeView.pause();
        mRightEyeView.pause();
        //取消倒计时
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        //默认设置水平进度条为最大值
        custom_ProgressBar.setProgress(custom_ProgressBar.getMax());
        //删除用户、修改用户名按钮默认设置可点击
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.setActionEnabled(true);
        }

        //设置柱状条初始最小值，重置进度
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mLeftThermometerView.setProgress(mMinProgress);
            mLeftThermometerView.invalidate();
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRightThermometerView.setProgress(mMinProgress);
            mRightThermometerView.invalidate();
        }
    }

    long tipTime = 0;
    public static final int TIME = 10;

    public void updateUIStatus(int status) {

        mCurDist = IKEnrIdenStatus.getInstance().irisPos.dist;

        String tips = "";

        switch (status) {
            case IKALGConstant.IRIS_FRAME_STATUS_BLINK:
                tips = "请眨眼";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_MOTION_BLUR:
            case IKALGConstant.IRIS_FRAME_STATUS_FOCUS_BLUR:
                tips = "保持稳定";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_BAD_EYE_OPENNESS:
                tips = "请睁大眼睛";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_WITH_GLASS:
                if (System.currentTimeMillis() - tipTime > TIME * 1000) {
                    tips = "请摘掉眼镜";
                } else {
                    tips = "请抬高手机";
                }
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_WITH_GLASS_HEADUP:
                tips = "请抬头";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_WITH_GLASS_HEADDOWN:
                tips = "请低头";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_CLOSE:

                int suit = EnumDeviceType.getCurrentDevice().getSuitablePosDist();
                int movedist = Math.abs(mCurDist - suit);
                if (mCurDist != -1) {
                    tips = String.format("请后移%02d厘米", movedist);
                } else {
                    tips = "请后移";
                }
                soundFarther.start();
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_FAR:
                suit = EnumDeviceType.getCurrentDevice().getSuitablePosDist();
                movedist = Math.abs(mCurDist - suit);
                if (mCurDist != -1) {
                    tips = String.format("请前移%02d厘米", movedist);
                } else {
                    tips = "请前移";
                }
                soundCloser.start();
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_NOT_FOUND:
                tips = "请将双眼对准取景框";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_UNAUTHORIZED_ATTACK:
                tips = "请停止使用";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_CONTACTLENS:
                tips = "请摘掉隐形眼镜";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_ATTACK:
                tips = "请勿攻击";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_OUTDOOR:
                tips = "请室内使用";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_UP:
                tips = "眼睛太靠上，请将双眼对准取景框";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_DOWN:
                tips = "眼睛太靠下，请将双眼对准取景框";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_LEFT:
                tips = "眼睛太靠左，请将双眼对准取景框";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_EYE_TOO_RIGHT:
                tips = "眼睛太靠右，请将双眼对准取景框";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_SUITABLE:
                tips = "正在扫描，请保持稳定";
                break;
            case IKALGConstant.IRIS_FRAME_STATUS_BAD_IMAGE_QUALITY:
                tips = "请将双眼对准取景框";
                break;
            case IKALGConstant.ERR_INVALIDDATE:
                tips = "设备超过授权日期";
                break;
            case IKALGConstant.ERR_INVALIDDEVICE:
                tips = "设备未授权";
                break;
            default:
                break;
        }

        LogUtil.e("iris_info", "state (1-enroll, 2-identify):" + state + ", 当前 UI 提示：" + tips);

        Message msg = Message.obtain();
        msg.obj = tips;
        msg.what = 3;
        surfaceviewHandler.sendMessage(msg);
    }

    private int maxLeft = 0;
    private int maxRight = 0;

    private IrisProcessCallback processCallback = new IrisProcessCallback() {

        @Override
        public void onUIStatusUpdate(int status) {
            updateUIStatus(status);
        }

        @Override
        public void onEnrollProgress(int currentLeftCount, int currentRightCount, int needCount) {
            maxLeft = maxLeft > currentLeftCount ? maxLeft : currentLeftCount;
            maxRight = maxRight > currentRightCount ? maxRight : currentRightCount;
            mRoundProgressBar.setLeftAndRightProgress(maxLeft, maxRight, needCount);
        }

        @Override
        public void onEnrollComplete(int ifSuccess, EnrFeatrueStruct leftEyeFeat, EnrFeatrueStruct rightEyeFeat, EnrFeatrueStruct faceFeat) {
            resetUI();
            // 首先判断是否成功，若失败提示后返回
            if (ifSuccess != IKALGConstant.ALGSUCCESS) {
                customPromptDialog = new CustomPromptDialog(MainActivity.this);

                if (ifSuccess == IKALGConstant.ERR_OVERTIME) {
                    customPromptDialog.setMessage("超时,请重试");
                } else if (ifSuccess == IKALGConstant.ERR_ENROLL_ERRORFEATURE) {
                    customPromptDialog.setMessage("多人注册!");
                } else {
                    customPromptDialog.setMessage("ErrorCode:" + ifSuccess);
                }

                mTopGuideView.setVisibility(View.VISIBLE);
                mIrisPresenter.updateReserveInfo(0);

                customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
                    @Override
                    public void onConfirmClick() {
                        customPromptDialog.dismiss();
                    }
                });
                customPromptDialog.show();
                //对话框默认三秒消失
                delayDialogDismiss(customPromptDialog);

                LogUtil.e("iris_info", "注册完成， 注册失败，code：" + ifSuccess + ", 总耗时：" + (System.currentTimeMillis() - startTime));

                return;
            }
            LogUtil.e("iris_info", "注册完成，注册成功 " + ", 总耗时：" + (System.currentTimeMillis() - startTime));

            soundEnrollSuccess.start();

            //左右眼动画暂停
            mLeftEyeView.pause();
            mRightEyeView.pause();
            //显示引导界面
            mTopGuideView.setVisibility(View.VISIBLE);
            //显示注册成功对话框
            customTextViewDialog = new CustomTextViewDialog(MainActivity.this);
            customTextViewDialog.setMessage("注册成功！");
            customTextViewDialog.show();
            //对话框默认三秒消失
            delayDialogDismiss(customTextViewDialog);
            //取消倒计时
            mCountDownTimer.cancel();
            //默认设置水平进度条为最大值
            custom_ProgressBar.setProgress(custom_ProgressBar.getMax());
            //查询数据库中已注册的用户
            List<IrisUserInfo> existsUsers = sqliteDataBase.queryAll();
            int minDefault = DBHelper.getMinimalDefaultValue(existsUsers);
            if (minDefault == -1) {
                return;
            }

            curName = "用户" + (minDefault + 1);

            //注册成功的用户信息存入数据库
            saveIrisFile(curName, minDefault, leftEyeFeat, rightEyeFeat);

            //重新查询数据库，将已注册用户显示在用户列表界面
            userInfo = sqliteDataBase.queryAll();

            if (userMap != null) {
                userMap.clear();
            }

            userMap = new ArrayList<>();

            for (IrisUserInfo info : userInfo) {
                Map<String, String> item = new HashMap<>();
                item.put("name", info.m_UserName);
                item.put("order", info.m_UserFavicon + "");
                userMap.add(item);
            }

            recyclerViewAdapter = new ShowUserInfoRecyclerViewAdapter(MainActivity.this, userMap);
            mRecyclerView.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onEyeDetected(boolean isValid, EyePosition leftPos, EyePosition rightPos, int captureDistance) {
        }

        @Override
        public void onIdentifyComplete(int ifSuccess, int matchIndex, int eyeFlag) {
            if (state == STATE_IDENTIFY_FLAG) {
                resetUI();
                if (ifSuccess != IKALGConstant.ALGSUCCESS) {
                    customPromptDialog = new CustomPromptDialog(MainActivity.this);

                    if (ifSuccess == IKALGConstant.ERR_IDENFAILED) {
                        customPromptDialog.setMessage("识别失败,请重试");
                    } else if (ifSuccess == IKALGConstant.ERR_OVERTIME) {
                        customPromptDialog.setMessage("识别超时,请重试");
                    } else if (ifSuccess == IKALGConstant.ERR_NOFEATURE) {
                        customPromptDialog.setMessage("没有特征,请先注册");
                    } else if (ifSuccess == IKALGConstant.ERR_EXCEEDMAXMATCHCAPACITY) {
                        customPromptDialog.setMessage("特征数量过多!");
                    } else if (ifSuccess == IKALGConstant.ERR_IDEN) {
                        customPromptDialog.setMessage("特征不匹配!");
                    } else {
                        customPromptDialog.setMessage("error code:" + ifSuccess);
                    }

                    customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
                        public void onConfirmClick() {
                            customPromptDialog.dismiss();
                        }
                    });
                    customPromptDialog.show();
                    //对话框默认三秒消失
                    delayDialogDismiss(customPromptDialog);

                    mIrisPresenter.updateReserveInfo(0);

                    //取消倒计时
                    mCountDownTimer.cancel();

                    //识别完成，将水平进度条重设为最大值
                    custom_ProgressBar.setProgress(custom_ProgressBar.getMax());

                    mTopGuideView.setVisibility(View.VISIBLE);

                    if (ifSuccess == IKALGConstant.ERR_NOFEATURE) {
                        Toast.makeText(getApplicationContext(), "没有特征，请先注册！", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Toast.makeText(getApplicationContext(), "识别失败，code:" + ifSuccess, Toast.LENGTH_SHORT).show();

                    LogUtil.e("iris_info", "识别完成，识别失败，code:" + ifSuccess + ", 总耗时：" + (System.currentTimeMillis() - startTime)
                            + ", 算法耗时：" + ((DnSmpDetect.getInstance(MainActivity.this).endTime - DnSmpDetect.getInstance().startTime)));

//                    mCurrentEyeDistance.setText("识别失败, 总耗时：" + (System.currentTimeMillis() - startTime) + ", 算法耗时："
//                            + ((DnSmpDetect.getInstance(MainActivity.this).endTime - DnSmpDetect.getInstance().startTime)));

                    return;
                }

                soundIdentifySuccess.start();

                LogUtil.e("iris_info", "识别完成，识别成功" + ", 总耗时：" + (System.currentTimeMillis() - startTime)
                        + ", 算法耗时：" + ((DnSmpDetect.getInstance(MainActivity.this).endTime - DnSmpDetect.getInstance().startTime)));

//                mCurrentEyeDistance.setText("识别成功, 总耗时：" + (System.currentTimeMillis() - startTime) + ", 算法耗时："
//                        + ((DnSmpDetect.getInstance(MainActivity.this).endTime - DnSmpDetect.getInstance().startTime)));

                //左右眼动画暂停
                mLeftEyeView.pause();
                mRightEyeView.pause();

                if (state == STATE_IDENTIFY_FLAG) {
                    //显示引导界面
                    mTopGuideView.setVisibility(View.VISIBLE);
                }

                String matchName;

                if (eyeFlag == EnumEyeType.LEFT) {
                    matchName = irisLeftData.personAt(matchIndex).getName();
                } else {
                    matchName = irisRightData.personAt(matchIndex).getName();
                }

                int order = sqliteDataBase.queryFaviconByName(matchName);

                recyclerViewAdapter.setIdentifySelectOrder(order);
                recyclerViewAdapter.notifyDataSetChanged();
            } else if (state == STATE_CONTINUE_FLAG) {
                if (ifSuccess != IKALGConstant.ALGSUCCESS) {
                    resetUI();
                    customPromptDialog = new CustomPromptDialog(MainActivity.this);

                    if (ifSuccess == IKALGConstant.ERR_IDENFAILED) {
                        customPromptDialog.setMessage("识别失败,请重试");
                    } else if (ifSuccess == IKALGConstant.ERR_OVERTIME) {
//                        customPromptDialog.setMessage("识别超时,请重试");
                        mIrisPresenter.updateReserveInfo(0);

                        mIrisContinueRBtn.setText("连续识别");

                        //左右眼动画暂停
                        mLeftEyeView.pause();
                        mRightEyeView.pause();
                        mTopGuideView.setVisibility(View.VISIBLE);
                        return;
                    } else if (ifSuccess == IKALGConstant.ERR_NOFEATURE) {
                        customPromptDialog.setMessage("没有特征,请先注册");
                    } else if (ifSuccess == IKALGConstant.ERR_EXCEEDMAXMATCHCAPACITY) {
                        customPromptDialog.setMessage("特征数量过多!");
                    } else if (ifSuccess == IKALGConstant.ERR_IDEN) {
                        customPromptDialog.setMessage("特征不匹配!");
                    } /*else {
                        customPromptDialog.setMessage("error code:" + ifSuccess);
                    }*/

                    customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
                        public void onConfirmClick() {
                            customPromptDialog.dismiss();
                        }
                    });
                    customPromptDialog.show();
                    //对话框默认三秒消失
                    delayDialogDismiss(customPromptDialog);

                    mIrisPresenter.updateReserveInfo(0);

                    mIrisContinueRBtn.setText("连续识别");

                    //取消倒计时
//                    mCountDownTimer.cancel();

                    //左右眼动画暂停
                    mLeftEyeView.pause();
                    mRightEyeView.pause();

                    //识别完成，将水平进度条重设为最大值
//                    custom_ProgressBar.setProgress(custom_ProgressBar.getMax());

                    mTopGuideView.setVisibility(View.VISIBLE);

                    if (ifSuccess == IKALGConstant.ERR_NOFEATURE) {
                        Toast.makeText(getApplicationContext(), "没有特征，请先注册！", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Toast.makeText(getApplicationContext(), "识别失败，code:" + ifSuccess, Toast.LENGTH_SHORT).show();

//                    surfaceviewHandler.sendEmptyMessage(5);
                    return;
                }

                MainActivity.this.vibrator.vibrate(new long[]{0, 50}, 1);

                soundIdentifySuccess.start();

                //左右眼动画暂停
                mLeftEyeView.play();
                mRightEyeView.play();

                String matchName;

                if (eyeFlag == EnumEyeType.LEFT) {
                    matchName = irisLeftData.personAt(matchIndex).getName();
                } else {
                    matchName = irisRightData.personAt(matchIndex).getName();
                }

                int order = sqliteDataBase.queryFaviconByName(matchName);

                lastIndex = order;

                recyclerViewAdapter.setContinueSelectOrder(order);
                recyclerViewAdapter.notifyDataSetChanged();
                surfaceviewHandler.sendEmptyMessage(5);
            }
//            Toast.makeText(getApplicationContext(), "识别成功 matchIndex: " + matchIndex + ", eyeFlag:" + eyeFlag + ", name:" + matchName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAlgoExit() {
            if (state == STATE_CONTINUE_FLAG) {
                //删除用户、修改用户名按钮默认设置可点击
                if (recyclerViewAdapter != null) {
                    recyclerViewAdapter.setActionEnabled(true);
                }
                mIrisPresenter.stopAlgo();

                //左右眼动画暂停
                mLeftEyeView.pause();
                mRightEyeView.pause();
                //播放注册、识别声音
//                soundDown.pause();
            } else {
                resetUI();
            }
        }
    };
    public int[] textureBuffer;
    private byte[] downSample;
    private int rotateAngle = 0;
    private int width = 0;
    private int height = 0;

    private CameraPreviewCallback irPreviewCallback = new CameraPreviewCallback.IRPreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, int bmpWidth, int bmpHeight) {

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                uvcTimeArray.newTime();
                if (uvcTimeArray.count() % 3 == 0) {
                    Log.e("tony", "MainActivity onPreviewFrame fps:" + uvcTimeArray.toString());
                }

                width = bmpWidth;
                height = bmpHeight;
                downSample = data;

                if (previewParaUpdated == false) {
                    previewParaUpdated = true;
                    if (matrix != null) {
                        matrix.postScale(1.0f * eyeViewWidth / bmpWidth, 1.0f * eyeViewHeight / bmpHeight);
                    }
                    surfaceviewHandler.sendEmptyMessage(2);
                }
                surfaceviewHandler.sendEmptyMessage(0);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                uvcTimeArray.newTime();
                if (uvcTimeArray.count() % 3 == 0) {
                    Log.e("tony", "MainActivity onPreviewFrame fps:" + uvcTimeArray.toString());
                }

                width = bmpWidth;
                height = bmpHeight;
                downSample = data;

                if (previewParaUpdated == false) {
                    previewParaUpdated = true;
                    if (matrix != null) {
                        matrix.postScale(1.0f * eyeViewWidth / bmpWidth, 1.0f * eyeViewHeight / bmpHeight);
                    }
                    surfaceviewHandler.sendEmptyMessage(2);
                }
                surfaceviewHandler.sendEmptyMessage(0);
            }
        }
    };

    private CameraPreviewCallback uvcPreviewCallback = new CameraPreviewCallback.UVCPreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] downSample, int bmpWidth, int bmpHeight) {
        }

        @Override
        public void onCameraConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "USB设备已连接", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onCameraDisconnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "USB设备失去连接", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            });
        }

        @Override
        public void onCameraDettached() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "USB设备失去连接", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            });
        }

        @Override
        public void onDeviceFlip(boolean direction) {
            if (!direction) {
                surfaceviewHandler.sendEmptyMessage(4);
            }
        }
    };

    public void onBackPressed() {
        resetUI();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mIrisPresenter != null) {
            mIrisPresenter.release();
            mIrisPresenter = null;
        }
        super.onDestroy();
    }

    public void saveIrisFile(String name, int defaultVal, EnrFeatrueStruct leftEyeFeat, EnrFeatrueStruct rightEyeFeat) {
        deleteIrisData();
        int userCount = sqliteDataBase.getUserCount();
        for (int i = userCount; i >= IrisConfig.LimitNumber; i--) {
            sqliteDataBase.removeFirstUser();
        }

        IrisUserInfo userInfo = new IrisUserInfo();

        if (leftEyeFeat != null && rightEyeFeat != null) {
            userInfo.m_Uid = name;
            userInfo.m_UserName = name;
            userInfo.m_UserFavicon = defaultVal;

            userInfo.m_LeftTemplate = new byte[leftEyeFeat.enrCount * IKALGConstant.IKALG_Iris_Enr_CodeLen];
            for (int i = 0; i < leftEyeFeat.enrCount; i++) {
                System.arraycopy(
                        leftEyeFeat.irisInfo[i].irisEnrTemplate, 0,
                        userInfo.m_LeftTemplate, i * IKALGConstant.IKALG_Iris_Enr_CodeLen,
                        IKALGConstant.IKALG_Iris_Enr_CodeLen);
            }

            userInfo.m_RightTemplate = new byte[rightEyeFeat.enrCount * IKALGConstant.IKALG_Iris_Enr_CodeLen];
            for (int i = 0; i < rightEyeFeat.enrCount; i++) {
                System.arraycopy(
                        rightEyeFeat.irisInfo[i].irisEnrTemplate, 0,
                        userInfo.m_RightTemplate, i * IKALGConstant.IKALG_Iris_Enr_CodeLen,
                        IKALGConstant.IKALG_Iris_Enr_CodeLen);
            }

            userInfo.m_LeftTemplate_Count = leftEyeFeat.enrCount;
            userInfo.m_RightTemplate_Count = rightEyeFeat.enrCount;
            userInfo.m_EnrollTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//            Log.e("iris_info_1", "MainActivity saveIrisFile() userInfo.m_EnrollTime:" + userInfo.m_EnrollTime + ", userInfo.m_LeftTemplate:" + userInfo.m_LeftTemplate.length);
            sqliteDataBase.insertUserData(userInfo);
        }
        //重新加载数据
        initIrisData();
    }

    public void deleteIrisData() {
        if (mEnrollConfig.singleUse) {
            sqliteDataBase.removeAll();
        }
    }


    //绑定用户信息，显示在用户列表界面
    public void updateUserInfo() {
        userInfo = sqliteDataBase.queryAll();
        userMap = new ArrayList<>();
        for (IrisUserInfo info : userInfo) {
            Map<String, String> item = new HashMap<>();
            item.put("name", info.m_UserName);
            item.put("order", info.m_UserFavicon + "");
            userMap.add(item);
        }

        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacingInPixels;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //RecycleView 增加边距
            spacingInPixels = (int) (50 * optHeight);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //RecycleView 增加边距
            spacingInPixels = (int) (65 * optHeight);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        }
        // 设置固定大小
        //mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        recyclerViewAdapter = new ShowUserInfoRecyclerViewAdapter(MainActivity.this, userMap);
        // 为mRecyclerView设置适配器
        mRecyclerView.setAdapter(recyclerViewAdapter);
    }

    //设置水平进度条倒计时
    public void startCountDownTimer(final int millisTotal) {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(millisTotal, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                float progress = ((float) millisUntilFinished) / millisTotal;
                progress = progress * 100;
                custom_ProgressBar.setProgress((int) progress);

                //播放注册、识别声音
//                soundDown.start();
            }

            @Override
            public void onFinish() {
                custom_ProgressBar.setProgress(custom_ProgressBar.getMax());
                if (state == STATE_CONTINUE_FLAG) {
                    resetUI();
                    mIrisPresenter.stopAlgo();
                    mIrisPresenter.updateReserveInfo(0);
                    mTopGuideView.setVisibility(View.VISIBLE);
                    displayOverTimeDialog();
                }
            }
        }.start();
    }

    //对话框默认三秒消失
    public void delayDialogDismiss(final Dialog dialog) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 3000);
        return;
    }

    //隐藏用户引导界面
    public void hideGuideView() {
        mTopGuideView.setVisibility(View.GONE);
        cbDetectFakeEye2.setVisibility(View.GONE);
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBottomGuideView.setVisibility(View.GONE);
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {    // 如果是横屏，预览区域
            mRightGuideView.setVisibility(View.GONE);
        }
        mRecyclerViewLayout.setVisibility(View.VISIBLE);
    }

    public void displayOverTimeDialog() {
        custom_ProgressBar.setProgress(custom_ProgressBar.getMax());
        customPromptDialog = new CustomPromptDialog(MainActivity.this);

        customPromptDialog.setMessage("超时,请重试!");

        customPromptDialog.setConfirmOnclickListener("确认", new CustomPromptDialog.onConfirmOnclickListener() {
            public void onConfirmClick() {
                customPromptDialog.dismiss();
            }
        });
        customPromptDialog.show();
        //对话框默认三秒消失
        delayDialogDismiss(customPromptDialog);
    }
}
