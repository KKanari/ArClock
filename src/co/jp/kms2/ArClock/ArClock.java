package co.jp.kms2.ArClock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Region.Op;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

public class ArClock extends Activity {
	public static String packageName;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // パッケージ名を取得
        packageName = this.getPackageName();
        
        tView v = new tView(getApplication());
        setContentView(v);
    }
    
    // viewでパッケージ名が取得できなかったためこっちで取得して文字列としてそれを渡している
	public static String packageName(){
		return packageName;
	}
}
/** 画面再描画の仕組みを提供するクラス */
class RedrawHandler extends Handler {
    private View view;
    private int delayTime;
    private int frameRate;
    public RedrawHandler(View view, int frameRate) {
        this.view = view;
        this.frameRate = frameRate;
    }
    public void start() {
        this.delayTime = 1000 / frameRate;
        this.sendMessageDelayed(obtainMessage(0), delayTime);
    }
    public void stop() {
        delayTime = 0;
    }
    @Override
    public void handleMessage(Message msg) {
        view.invalidate();
        if (delayTime == 0) return; // stop
        sendMessageDelayed(obtainMessage(0), delayTime);
    }
}
/** 描画用のView */
class tView extends View {
	public int winSizeW;			// ディスプレイの横幅
	public int winSizeH;			// ディスプレイの縦幅
	public boolean winVertical;	// 縦画面かどうか - trueなら縦画面、falseなら横画面
    
    // 時計、ストップウォッチ、カウントダウン切り替えパネル用
    public static final int TYPE_CLOCK = 1;						// 時計モードを表す
    public static final int TYPE_STOPWATCH = 2;					// ストップウォッチモードを表す
    public static final int TYPE_COUNTDOWN = 3;					// カウントダウンモードを表す
    public int typeSwitchNum = TYPE_CLOCK;						// 現在のモード
    public boolean typePnlShowFlg = false;						// 切替パネルの状態 - 開 or 閉
    public static final int TYPE_PNL_DEFAULT_NUM = -120;		// 切替パネルの初期位置
	public int typePnlSlideNum = TYPE_PNL_DEFAULT_NUM;			// 切替パネルの位置
	public static final int TYPE_SUB_VERTI_DEFAULT_X = -160;	// 縦画面時の切替サブパネルの横方向の初期位置
	public static final int TYPE_SUB_VERTI_DEFAULT_Y = 140;	// 縦画面時の切替サブパネルの縦方向の初期位置
	public static final int TYPE_SUB_HORI_DEFAULT_X = 160;	// 横画面時の切替サブパネルの横方向の初期位置
	public static final int TYPE_SUB_HORI_DEFAULT_Y = -140;	// 横画面時の切替サブパネルの縦方向の初期位置
	public int typeSubPnlSlideX = TYPE_SUB_VERTI_DEFAULT_X;		// 切替サブパネルの横方向の位置
	public int typeSubPnlSlideY = TYPE_SUB_VERTI_DEFAULT_Y;		// 切替サブパネルの縦方向の位置
	public int subPnlTitleX = 20;									// サブパネルのタイトル横方向の位置
	public int subPnlTitleY = 20;									// サブパネルのタイトル縦方向の位置
		// 切替パネル描画用 - 切替パネルの範囲
		public int typePanelL = 1;
		public int typePanelT = -10;
		public int typePanelR = 319;
		public int typePanelB = 130;
    	// 切替サブパネル範囲
	    public int typeSubPanelL = -10;
	    public int typeSubPanelT = 290;
	    public int typeSubPanelR = 160;
	    public int typeSubPanelB = 440;
    	// 切替パネル開閉用ボタン
    	public int typePnlShowBtnL = 198;
    	public int typePnlShowBtnT = 130;
    	public int typePnlShowBtnR = 248;
    	public int typePnlShowBtnB = 155;
    	// 時計切り替え用
	    public int clockBtnL = 10;
	    public int clockBtnT = 20;
	    public int clockBtnR = 40;
	    public int clockBtnB = 50;
	    // ストップウォッチ切り替え用
	    public int stopWatchBtnL = 10;
	    public int stopWatchBtnT = 55;
	    public int stopWatchBtnR = 40;
	    public int stopWatchBtnB = 85;
	    // カウントダウン切り替え用
	    public int countDownBtnL = 10;
	    public int countDownBtnT = 90;
	    public int countDownBtnR = 40;
	    public int countDownBtnB = 120;
	    // 切替パネル、(時)表示用
	    public int typePnlHourL = 50;
	    public int typePnlHourT = 45;
	    public int typePnlHourR = 100;
	    public int typePnlHourB = 95;
	    // 切替パネル、(分)表示用
	    public int typePnlMinuteL = 110;
	    public int typePnlMinuteT = 45;
	    public int typePnlMinuteR = 160;
	    public int typePnlMinuteB = 95;
	    // 切替パネル、(秒)表示用
	    public int typePnlSecondL = 170;
	    public int typePnlSecondT = 45;
	    public int typePnlSecondR = 220;
	    public int typePnlSecondB = 95;
	    // 切替パネル、(ミリ秒)表示用
	    public int typePnlMilSecL = 230;
	    public int typePnlMilSecT = 45;
	    public int typePnlMilSecR = 258;
	    public int typePnlMilSecB = 65;
    
	// 時計モード用
	public int nextAlarmTxtX = 50;
	public int nextAlarmTxtY = 80;
	
    // 指定時間用
    public boolean showSpecificFlg = false;					// 指定時間設定画面か否か
    public Calendar specificTime = Calendar.getInstance();		// 指定時間初期化用
    public long[] specificTimes = new long[9];				// 指定時間保持用
    public boolean[] specificTimesOnFlg = new boolean[specificTimes.length];	// 指定時間が有効か無効かを保持
    public int choiceSpecific = 1;								// 選択中の指定時間
    public boolean specificFlg = false;						// 指定時間が来たか否か
    public boolean[][] specificWeekdayOnFlg = new boolean[specificTimes.length][7];	// 指定時間ごとの曜日が有効か無効化を保持
	    // 指定時間設定表示用ボタン
	    public int specificTimeBtnL = 240;
	    public int specificTimeBtnT = 75;
	    public int specificTimeBtnR = 300;
	    public int specificTimeBtnB = 115;
	    // 指定時間ボタン１
	    public int specificBtn1L = 30;
	    public int specificBtn1T = 30;
	    public int specificBtn1R = 60;
	    public int specificBtn1B = 60;
	    // 指定時間ボタン２
	    public int specificBtn2L = 75;
	    public int specificBtn2T = 30;
	    public int specificBtn2R = 105;
	    public int specificBtn2B = 60;
	    // 指定時間ボタン３
	    public int specificBtn3L = 120;
	    public int specificBtn3T = 30;
	    public int specificBtn3R = 150;
	    public int specificBtn3B = 60;
	    // 指定時間ボタン４
	    public int specificBtn4L = 30;
	    public int specificBtn4T = 65;
	    public int specificBtn4R = 60;
	    public int specificBtn4B = 95;
	    // 指定時間ボタン５
	    public int specificBtn5L = 75;
	    public int specificBtn5T = 65;
	    public int specificBtn5R = 105;
	    public int specificBtn5B = 95;
	    // 指定時間ボタン６
	    public int specificBtn6L = 120;
	    public int specificBtn6T = 65;
	    public int specificBtn6R = 150;
	    public int specificBtn6B = 95;
	    // 指定時間ボタン７
	    public int specificBtn7L = 30;
	    public int specificBtn7T = 100;
	    public int specificBtn7R = 60;
	    public int specificBtn7B = 130;
	    // 指定時間ボタン８
	    public int specificBtn8L = 75;
	    public int specificBtn8T = 100;
	    public int specificBtn8R = 105;
	    public int specificBtn8B = 130;
	    // 指定時間ボタン９
	    public int specificBtn9L = 120;
	    public int specificBtn9T = 100;
	    public int specificBtn9R = 150;
	    public int specificBtn9B = 130;
	    // 指定時間ボタン１０
//	    public int specificBtn10L = 280;
//	    public int specificBtn10T = 90;
//	    public int specificBtn10R = 310;
//	    public int specificBtn10B = 120;
	    // 曜日設定ボタン１ - 日
	    public int specificWeekdayBtn1L = 175;
	    public int specificWeekdayBtn1T = 50;
	    public int specificWeekdayBtn1R = 205;
	    public int specificWeekdayBtn1B = 80;
	    // 曜日設定ボタン２ - 月
	    public int specificWeekdayBtn2L = 210;
	    public int specificWeekdayBtn2T = 50;
	    public int specificWeekdayBtn2R = 240;
	    public int specificWeekdayBtn2B = 80;
	    // 曜日設定ボタン３ - 火
	    public int specificWeekdayBtn3L = 245;
	    public int specificWeekdayBtn3T = 50;
	    public int specificWeekdayBtn3R = 275;
	    public int specificWeekdayBtn3B = 80;
	    // 曜日設定ボタン４ - 水
	    public int specificWeekdayBtn4L = 175;
	    public int specificWeekdayBtn4T = 85;
	    public int specificWeekdayBtn4R = 205;
	    public int specificWeekdayBtn4B = 115;
	    // 曜日設定ボタン５ - 木
	    public int specificWeekdayBtn5L = 210;
	    public int specificWeekdayBtn5T = 85;
	    public int specificWeekdayBtn5R = 240;
	    public int specificWeekdayBtn5B = 115;
	    // 曜日設定ボタン６ - 金
	    public int specificWeekdayBtn6L = 245;
	    public int specificWeekdayBtn6T = 85;
	    public int specificWeekdayBtn6R = 275;
	    public int specificWeekdayBtn6B = 115;
	    // 曜日設定ボタン７ - 土
	    public int specificWeekdayBtn7L = 280;
	    public int specificWeekdayBtn7T = 85;
	    public int specificWeekdayBtn7R = 310;
	    public int specificWeekdayBtn7B = 115;
	    
    // ストップウォッチ用
    public Calendar stopWatchStartTime = Calendar.getInstance();		// ストップウォッチを開始した時間
    public Calendar stopWatchStopTime = Calendar.getInstance();			// ストップウォッチを停止した時間
    public boolean stopWatchStartFlg = false;							// ストップウォッチが開始しているか否か
    public ArrayList<Long> stopWatchRecordTime = new ArrayList<Long>();// 記録時間を保持しておく配列
    public int stopWatchRecTMaxNum = 60;								// 記録時間の最大数
    public int stopWatchRecordPage = 1;								// 現在の記録時間のページ
    public int stopWatchPageRecNum = 6;								// 1ページ中の記録時間の数
    public boolean stopWatchSplitFlg = true;							// split timeかどうかのフラグ - trueならsplit、falseならrap
    public int stopWatchRecordX = 40;									// 記録時間表示の横位置
    public int[] stopWatchRecordY = {40, 58, 76, 94, 112, 130};		// 記録時間表示の縦位置
    public int stopWatchPageX = 155;									// ページ数表示位置横
    public int stopWatchPageY = 20;									// ページ数表示位置縦
    	// ストップウォッチ開始終了ボタン
    	public int stopWStartStopBtnL = 240;
    	public int stopWStartStopBtnT = 75;
    	public int stopWStartStopBtnR = 300;
    	public int stopWStartStopBtnB = 115;
    

    // カウントダウン用
    public boolean pnlHouUpFlg = false;							// 増加ボタン(時)が押されているかどうか - 指定時間と共用
    public boolean pnlHouDownFlg = false;							// 減少ボタン(時)が押されているかどうか - 指定時間と共用
    public boolean pnlMinUpFlg = false;							// 増加ボタン(分)が押されているかどうか - 指定時間と共用
    public boolean pnlMinDownFlg = false;							// 減少ボタン(分)が押されているかどうか - 指定時間と共用
    public boolean pnlSecUpFlg = false;							// 増加ボタン(秒)が押されているかどうか
    public boolean pnlSecDownFlg = false;							// 減少ボタン(秒)が押されているかどうか
    public Calendar countDownTime = Calendar.getInstance();			// 時間の設定、カウントダウンに使用する日付
    public Calendar countDownStartTime = Calendar.getInstance();	// カウントダウン開始時刻
    public long countDDatumTime = 0;								// カウントダウンの初期値を格納する
    public static final int PNLT_TARGET_HOU = 1;				// 時間増減時の対象（時） - 指定時間と共用
    public static final int PNLT_TARGET_MIN = 2;				// 時間増減時の対象（分） - 指定時間と共用
    public static final int PNLT_TARGET_SEC = 3;				// 時間増減時の対象（秒）
    public static final int COUNTD_UP = 1;						// 時間増減時に増やす - 指定時間と共用
    public static final int COUNTD_DOWN = -1;					// 時間増減時に減らす - 指定時間と共用
    public boolean countDownStartFlg =false;						// カウントダウンが開始しているか否か
    public boolean countEndFlg = false;							// カウント終了時処理のフラグ
    public int upDownwait = 0;										// 増減ボタンをずっと押していた時に少し待ってから処理をする
    public long[] countDSetTimes = new long[3];					// カウントダウン保存時間を入れる配列
    public int countDSetTimeX = 20;								// カウントダウン保存時間の横位置
    public int[] countDSetTimeY = {50, 85, 120};					// カウントダウン保存時間の縦位置
    	// パネル（時）増加ボタン
    	public int pnlHouUpBtnL = 50;
    	public int pnlHouUpBtnT = 17;
    	public int pnlHouUpBtnR = 100;
    	public int pnlHouUpBtnB = 42;
    	// パネル（時）減少ボタン
    	public int pnlHouDownBtnL = 50;
    	public int pnlHouDownBtnT = 98;
    	public int pnlHouDownBtnR = 100;
    	public int pnlHouDownBtnB = 123;
    	// パネル（分）増加ボタン
    	public int pnlMinUpBtnL = 110;
    	public int pnlMinUpBtnT = 17;
    	public int pnlMinUpBtnR = 160;
    	public int pnlMinUpBtnB = 42;
    	// パネル（分）減少ボタン
    	public int pnlMinDownBtnL = 110;
    	public int pnlMinDownBtnT = 98;
    	public int pnlMinDownBtnR = 160;
    	public int pnlMinDownBtnB = 123;
    	// パネル（秒）増加ボタン
    	public int pnlSecUpBtnL = 170;
    	public int pnlSecUpBtnT = 17;
    	public int pnlSecUpBtnR = 220;
    	public int pnlSecUpBtnB = 42;
    	// パネル（秒）減少ボタン
    	public int pnlSecDownBtnL = 170;
    	public int pnlSecDownBtnT = 98;
    	public int pnlSecDownBtnR = 220;
    	public int pnlSecDownBtnB = 123;
    	// カウントダウン開始停止ボタン
    	public int countDStartStopBtnL = 240;
    	public int countDStartStopBtnT = 75;
    	public int countDStartStopBtnR = 300;
    	public int countDStartStopBtnB = 115;
    	// カウントダウン保存ボタン1
    	public int countDSaveBtn1L = 80;
    	public int countDSaveBtn1T = 30;
    	public int countDSaveBtn1R = 115;
    	public int countDSaveBtn1B = 60;
    	// カウントダウン保存ボタン2
    	public int countDSaveBtn2L = 80;
    	public int countDSaveBtn2T = 65;
    	public int countDSaveBtn2R = 115;
    	public int countDSaveBtn2B = 95;
    	// カウントダウン保存ボタン3
    	public int countDSaveBtn3L = 80;
    	public int countDSaveBtn3T = 100;
    	public int countDSaveBtn3R = 115;
    	public int countDSaveBtn3B = 130;
    	// カウントダウンセットボタン1
    	public int countDSetBtn1L = 120;
    	public int countDSetBtn1T = 30;
    	public int countDSetBtn1R = 155;
    	public int countDSetBtn1B = 60;
    	// カウントダウンセットボタン2
    	public int countDSetBtn2L = 120;
    	public int countDSetBtn2T = 65;
    	public int countDSetBtn2R = 155;
    	public int countDSetBtn2B = 95;
    	// カウントダウンセットボタン3
    	public int countDSetBtn3L = 120;
    	public int countDSetBtn3T = 100;
    	public int countDSetBtn3R = 155;
    	public int countDSetBtn3B = 130;
	    
	    
	    
	    
	//// 設定パネル用設定
	public boolean cnfModeFlg = false;							// 設定パネルの状態 - 開 or 閉
	public static final int CNF_PNL_DEFAULT_NUM = -120;		// パネル初期位置
	public int cnfPnlSlideNum = CNF_PNL_DEFAULT_NUM;			// パネルの位置
	public static final int CNF_SUB_VERTI_DEFAULT_X = -160;	// 縦画面時の設定サブパネルの横方向の初期位置
	public static final int CNF_SUB_VERTI_DEFAULT_Y = 140;	// 縦画面時の設定サブパネルの縦方向の初期位置
	public static final int CNF_SUB_HORI_DEFAULT_X = 160;		// 横画面時の設定サブパネルの横方向の初期位置
	public static final int CNF_SUB_HORI_DEFAULT_Y = -140;	// 横画面時の設定サブパネルの縦方向の初期位置
	public int cnfSubPnlSlideX = CNF_SUB_VERTI_DEFAULT_X;		// 設定サブパネルの横方向の位置
	public int cnfSubPnlSlideY = CNF_SUB_VERTI_DEFAULT_Y;		// 設定サブパネルの縦方向の位置
	public static final int MODE_CLOCK_DISPLAY = 1;			// 設定サブパネルのモード - 時計（秒）切替モード
	public static final int MODE_NUMBER_FORMAT = 2;			// 設定サブパネルのモード - 数字表示切替モード
	public static final int MODE_ALARM_VIBRATE = 3;			// 設定サブパネルのモード - 音声・バイブ切替モード
	public static final int MODE_CLOCK_SHAPE = 4;				// 設定サブパネルのモード - 時計形変更モード
	public static final int MODE_CLOCK_COLOR_1 = 5;			// 設定サブパネルのモード - 色選択モード 時計色１
	public static final int MODE_CLOCK_COLOR_2 = 6;			// 設定サブパネルのモード - 色選択モード 時計色２
	public static final int MODE_BACKGROUND_COLOR = 7;			// 設定サブパネルのモード - 色選択モード 背景色
	public int cnfSubMode = MODE_CLOCK_DISPLAY;					// 現在の設定サブパネルのモード
		// 設定パネル描画用 - 設定パネルの範囲
		public int cnfPanelL = 1;
		public int cnfPanelT = -10;
		public int cnfPanelR = 319;
		public int cnfPanelB = 127;
	    // 設定パネル開閉用ボタン
		public int cnfPnlShowBtnL = 250;
		public int cnfPnlShowBtnT = 122;
		public int cnfPnlShowBtnR = 300;
		public int cnfPnlShowBtnB = 155;
		// 切替サブパネル範囲
	    public int cnfSubPanelL = -10;
	    public int cnfSubPanelT = 290;
	    public int cnfSubPanelR = 160;
	    public int cnfSubPanelB = 440;
	    
	
    // 時分のみ表示設定用
    public boolean onlyHouMinF = false;			// 時分のみ表示のフラグ
    public static final String HMS_TEXT = "HMS";	// 秒表示時の文字列
    public static final String HM_TEXT = "HM";	// 秒非表示時の文字列
    public String onlyHouMinBtnTxt = HMS_TEXT;	// 時計（秒）切替ボタンに表示される文字列
    	// 時計（秒）切替ボタン
	    public int onlyHouMinBtnL = 10;
	    public int onlyHouMinBtnT = 10;
	    public int onlyHouMinBtnR = 80;
	    public int onlyHouMinBtnB = 60;
	    // 時分秒ボタン
	    public int hMSBtnL = 25;
	    public int hMSBtnT = 45;
	    public int hMSBtnR = 85;
	    public int hMSBtnB = 105;
	    // 時分ボタン
	    public int hMBtnL = 95;
	    public int hMBtnT = 45;
	    public int hMBtnR = 155;
	    public int hMBtnB = 105;
    
    // 時計表示文字切替用 - 数字、漢数字
    public boolean timeTxtNumFlg = true;					// 数字か漢数字かのフラグ
    public static final String ARABIC_TEXT = "Arabic";	// アラビア数字時の文字列
    public static final String KANSUUJI_TEXT = "漢数字";// 漢数字時の文字列
    public String timeTxtBtnTxt = ARABIC_TEXT;			// 数字表示切替ボタンに表示される文字列
    	// 数字表示切替ボタン
	    public int timeTxtBtnL = 90;
	    public int timeTxtBtnT = 10;
	    public int timeTxtBtnR = 160;
	    public int timeTxtBtnB = 60;
	    // アラビア数字ボタン
	    public int arabicBtnL = 25;
	    public int arabicBtnT = 45;
	    public int arabicBtnR = 85;
	    public int arabicBtnB = 105;
	    // 漢数字ボタン
	    public int kansuujiBtnL = 95;
	    public int kansuujiBtnT = 45;
	    public int kansuujiBtnR = 155;
	    public int kansuujiBtnB = 105;
    
    // 音、バイブレータ用
    public MediaPlayer mp;									// 音声再生用
    public Vibrator vibrator;								// バイブレータ用
    public static final int ALARM_VIBRAT_ALARM = 1;	// アラームのみ
    public static final int ALARM_VIBRAT_VIBRAT = 2;	// バイブレータのみ
    public static final int ALARM_VIBRAT_BOTH = 3;		// アラーム・バイブレータ両方
    public int alarmVibratFlg = ALARM_VIBRAT_BOTH;		// 選択している音、バイブレータの設定
    public static final String BOTH_TEXT = "Both";		// 両方時の文字列
    public static final String ALARM_TEXT = "Alarm";		// アラーム時の文字列
    public static final String VIBRATE_TEXT = "Vibrate";// バイブレータ時の文字列
    public String alarmVibratBtnTxt = BOTH_TEXT;			// 音声・バイブ切替ボタンに表示される文字列
    	// 音声・バイブ切替ボタン
	    public int alarmVibratBtnL = 10;
	    public int alarmVibratBtnT = 70;
	    public int alarmVibratBtnR = 80;
	    public int alarmVibratBtnB = 120;
	    // 両方ボタン
	    public int bothBtnL = 15;
	    public int bothBtnT = 45;
	    public int bothBtnR = 55;
	    public int bothBtnB = 105;
	    // アラームボタン
	    public int alarmBtnL = 60;
	    public int alarmBtnT = 45;
	    public int alarmBtnR = 110;
	    public int alarmBtnB = 105;
	    // バイブレータボタン
	    public int vibrateBtnL = 115;
	    public int vibrateBtnT = 45;
	    public int vibrateBtnR = 165;
	    public int vibrateBtnB = 105;
    
    
    // 時計形変更
	public static final int TIMEFACE_CIRCLE = 1;	// 円
	public static final int TIMEFACE_TRIANGLE = 2;	// 三角
	public static final int TIMEFACE_SQUARE = 3;	// 四角
	public static final int TIMEFACE_PENTAGON = 4;	// 五角
	public static final int TIMEFACE_HEXAGON = 5;	// 六角
	public static final int TIMEFACE_OCTAGON = 6;	// 八角
	public int timeFace = TIMEFACE_CIRCLE;			// 選択中の時計形
	public static final String CIRCLE_TEXT = "O";	// 円の文字列
	public static final String TRIANGLE_TEXT = "3";	// 三角の文字列
	public static final String SQUARE_TEXT = "4";	// 四角の文字列
	public static final String PENTAGON_TEXT = "5";	// 五角の文字列
	public static final String HEXAGON_TEXT = "6";	// 六角の文字列
	public static final String OCTAGON_TEXT = "8";	// 八角の文字列
	public String timeFaceBtnText = CIRCLE_TEXT;		// 時計形変更ボタンに表示される文字列
	
		// 時計形変更用ボタン設定
		public int timeFaceBtnL = 90;
		public int timeFaceBtnT = 70;
		public int timeFaceBtnR = 160;
		public int timeFaceBtnB = 120;
		// 円用ボタン
		public int timeFaceBtnCirL = 20;
		public int timeFaceBtnCirT = 35;
		public int timeFaceBtnCirR = 60;
		public int timeFaceBtnCirB = 75;
		// 三角用ボタン
		public int timeFaceBtnTriL = 70;
		public int timeFaceBtnTriT = 35;
		public int timeFaceBtnTriR = 110;
		public int timeFaceBtnTriB = 75;
		// 四画用ボタン
		public int timeFaceBtnSquL = 120;
		public int timeFaceBtnSquT = 35;
		public int timeFaceBtnSquR = 160;
		public int timeFaceBtnSquB = 75;
		// 五画用ボタン
		public int timeFaceBtnPenL = 20;
		public int timeFaceBtnPenT = 85;
		public int timeFaceBtnPenR = 60;
		public int timeFaceBtnPenB = 125;
		// 六角用ボタン
		public int timeFaceBtnHexL = 70;
		public int timeFaceBtnHexT = 85;
		public int timeFaceBtnHexR = 110;
		public int timeFaceBtnHexB = 125;
		// 八画用ボタン
		public int timeFaceBtnOctL = 120;
		public int timeFaceBtnOctT = 85;
		public int timeFaceBtnOctR = 160;
		public int timeFaceBtnOctB = 125;
    
	
	// 時計色１変更ボタン用
    	// 時計色１変更ボタン配置
	    public int timeCol1BtnL = 175;
	    public int timeCol1BtnT = 15;
	    public int timeCol1BtnR = 215;
	    public int timeCol1BtnB = 115;
    	
    // 時計色２変更ボタン用
    	// ボタン１配置
	    public int timeCol2BtnL = 220;
	    public int timeCol2BtnT = 15;
	    public int timeCol2BtnR = 260;
	    public int timeCol2BtnB = 115;

    // 背景色変更ボタン用
    	// ボタン１配置
	    public int backColBtnL = 265;
	    public int backColBtnT = 15;
	    public int backColBtnR = 305;
	    public int backColBtnB = 115;
	
	// 時計色グラデーション
	public static final int GRAD_RED_NUM = 0;	// 赤色をあらわす値
	public static final int GRAD_GREEN_NUM = 1;	// 緑色をあらわす値
	public static final int GRAD_BLUE_NUM = 2;	// 青色をあらわす値
	public static final int GRAD_NO_COLOR = 3;	// 色が選択されていない
	public int[] timeCol1 = {120, 120, 120};		// 時計色１の値
	public int[] timeCol2 = {108, 108, 108};		// 時計色２の値
	public int[] bgCol = {120, 120, 120};			// 背景色の値
	public int gradSettingColor = GRAD_NO_COLOR;	// 設定中の色 0=赤、1=緑、2=青、3=なし
	public int gradMinNum = 0;						// グラデーションの値の最小値
	public int gradMaxNum = 120;					// グラデーションの値の最大値
	public int gradBarAdjust = 35;					// 下のグラデーションの位置 - 基本位置からずらす位置
		// グラデーション設定用（赤）場所
		public int gradRedL = 30;
		public int gradRedT = 25;
		public int gradRedR = 150;
		public int gradRedB = 55;
		// グラデーション設定用（緑）場所
		public int gradGreenL = 30;
		public int gradGreenT = 60;
		public int gradGreenR = 150;
		public int gradGreenB = 90;
		// グラデーション設定用（青）場所
		public int gradBlueL = 30;
		public int gradBlueT = 95;
		public int gradBlueR = 150;
		public int gradBlueB = 125;
		// グラデーションバー基本位置
		public int gradPoint1L = 30;
		public int gradPoint1T = 40;
		public int gradPoint2L = 32;
		public int gradPoint2T = 38;
		public int gradPoint3L = 148;
		public int gradPoint3T = 25;
		public int gradPoint4L = 150;
		public int gradPoint4T = 27;
		public int gradPoint5L = 150;
		public int gradPoint5T = 53;
		public int gradPoint6L = 148;
		public int gradPoint6T = 55;
		public int gradPoint7L = 32;
		public int gradPoint7T = 42;
		public int gradPoint8L = 30;
		public int gradPoint8T = 40;
	
	
    
	
    //// 時計表示用変数
    public static final int HOUNUM = 1;				// 時、分、秒区別用（時）
    public static final int MINNUM = 2;				// 時、分、秒区別用（分）
    public static final int SECNUM = 3;				// 時、分、秒区別用（秒）
    public int houArc = 0;								// 時計（時）の値
    public static final int HOU_L_INITIAL = 5;		// 時計（時）の初期位置（左）
    public static final int HOU_T_INITIAL = 160;	// 時計（時）の初期位置（上）
    public int houL = HOU_L_INITIAL;					// 時計（時）の位置（左）
    public int houT = HOU_T_INITIAL;					// 時計（時）の位置（上）
    public int houR = 50;								// 時計（時）の範囲の半分の値 - 半径
    public int minArc = 0;								// 時計（分）の値
    public static final int MIN_L_INITIAL = 110;	// 時計（分）の初期位置（左）
    public static final int MIN_T_INITIAL = 160;	// 時計（分）の初期位置（上）
    public int minL = MIN_L_INITIAL;					// 時計（分）の位置（左）
    public int minT = MIN_T_INITIAL;					// 時計（分）の位置（上）
    public int minR = 50;								// 時計（分）の範囲の半分の値 - 半径
    public int secArc = 0;								// 時計（秒）の値
    public static final int SEC_L_INITIAL = 215;	// 時計（秒）の初期位置（左）
    public static final int SEC_T_INITIAL = 160;	// 時計（秒）の初期位置（上）
    public int secL = SEC_L_INITIAL;					// 時計（秒）の位置（左）
    public int secT = SEC_T_INITIAL;					// 時計（秒）の位置（上）
    public int secR = 50;								// 時計（秒）の範囲の半分の値 - 半径
    public int secPArc = 0;							// ミリ秒を6等分した値 - secArcに足すことでスムーズに動かす
    
    // タッチ時用変数
    public int houTouch = 0;			// 時計（時）の文字フェード用 - 経過フレーム数
    public int houTAlfa = 0;			// 時計（時）の文字フェード用 - アルファ値
    public boolean houMove = false;	// 時計（時）が移動中か否か
    public int minTouch = 0;			// 時計（分）の文字フェード用 - 経過フレーム数
    public int minTAlfa = 0;			// 時計（分）の文字フェード用 - アルファ値
    public boolean minMove = false;	// 時計（分）が移動中か否か
    public int secTouch = 0;			// 時計（秒）の文字フェード用 - 経過フレーム数
    public int secTAlfa = 0;			// 時計（秒）の文字フェード用 - アルファ値
    public boolean secMove = false;	// 時計（秒）が移動中か否か
    public final static int TOUCHMAXNUM = 50;	//時計の文字フェード用 - 初期値
    
    // 吹き出し用
    public boolean justTimeFlg = false;				// 1時ちょうど、2時ちょうど…の場合のフラグ
    public static final int BALCNT_INIT = 1;		// 吹き出し用変数の初期値
    public int balCnt = BALCNT_INIT;					// 吹き出し用変数 - カウント用変数
    public static final int BALL_VERTI_INIT = 108;	// 吹き出し表示初期位置（左)(縦時）
    public static final int BALT_VERTI_INIT = 310;	// 吹き出し表示初期位置（上)(縦時）
    public static final int BALR_VERTI_INIT = 112;	// 吹き出し表示初期位置（右)(縦時）
    public static final int BALB_VERTI_INIT = 310;	// 吹き出し表示初期位置（下)(縦時）
    public static final int BALL_HORI_INIT = 398;	// 吹き出し表示初期位置（左)(横時）
    public static final int BALT_HORI_INIT = 120;	// 吹き出し表示初期位置（上)(横時）
    public static final int BALR_HORI_INIT = 402;	// 吹き出し表示初期位置（右)(横時）
    public static final int BALB_HORI_INIT = 120;	// 吹き出し表示初期位置（下)(横時）
    public int balL = BALL_VERTI_INIT;				// 吹き出し表示位置（左)
    public int balT = BALT_VERTI_INIT;				// 吹き出し表示位置（上)
    public int balR = BALR_VERTI_INIT;				// 吹き出し表示位置（右)
    public int balB = BALB_VERTI_INIT;				// 吹き出し表示位置（下)
    
    
    // マスコット表示用
    public static final int DROIDADJUST_L_VERTI_INIT = 184;	// マスコット表示初期位置(左)(縦時)
    public static final int DROIDADJUST_T_VERTI_INIT = 290;	// マスコット表示初期位置(上)(縦時)
    public static final int DROIDADJUST_L_HORI_INIT = 348;	// マスコット表示初期位置(左)(横時)
    public static final int DROIDADJUST_T_HORI_INIT = 140;	// マスコット表示初期位置(上)(横時)
    public int droidAdjustL = DROIDADJUST_L_VERTI_INIT;			// マスコット表示位置(左)
    public int droidAdjustT = DROIDADJUST_T_VERTI_INIT;			// マスコット表示位置(上)
    public Paint droidColor = new Paint();							// マスコット色設定保持用
    public int droidArmBWidthPlusL = 5;							// マスコット腕ボタンのタッチ領域増加用
		// アンドロイドの右目
		public int droidRightEyeL = 37;
		public int droidRightEyeT = 26;
		public int droidRightEyeR = 3;
    	// アンドロイドの左目
    	public int droidLeftEyeL = 67;
    	public int droidLeftEyeT = 26;
		public int droidLeftEyeR = 3;
		// アンドロイドの右腕 - ストップウォッチ時
	    public int droidRArmP1L = 0;
	    public int droidRArmP1T = 65;
	    public int droidRArmP2L = 15;
	    public int droidRArmP2T = 42;
	    public int droidRArmP3L = 15;
	    public int droidRArmP3T = 88;
		// アンドロイドの右腕
	    public int droidRightArmL = 0;
	    public int droidRightArmT = 42;
	    public int droidRightArmR = 15;
	    public int droidRightArmB = 88;
		// アンドロイドの左腕 - ストップウォッチ時
	    public int droidLArmP1L = 89;
	    public int droidLArmP1T = 42;
	    public int droidLArmP2L = 104;
	    public int droidLArmP2T = 65;
	    public int droidLArmP3L = 89;
	    public int droidLArmP3T = 88;
		// アンドロイドの左腕
	    public int droidLeftArmL = 89;
	    public int droidLeftArmT = 42;
	    public int droidLeftArmR = 104;
	    public int droidLeftArmB = 88;
		// アンドロイドの上体
	    public int droidTopBodyL = 18;
	    public int droidTopBodyT = 44;
	    public int droidTopBodyR = 86;
	    public int droidTopBodyB = 71;
		// アンドロイドの下体
	    public int droidBottomBodyL = 18;
	    public int droidBottomBodyT = 61;
	    public int droidBottomBodyR = 86;
	    public int droidBottomBodyB = 101;
		// アンドロイドの右足
	    public int droidRightFootL = 32;
	    public int droidRightFootT = 89;
	    public int droidRightFootR = 47;
	    public int droidRightFootB = 124;
		// アンドロイドの左足
	    public int droidLeftFootL = 57;
	    public int droidLeftFootT = 89;
	    public int droidLeftFootR = 72;
	    public int droidLeftFootB = 124;
		// アンドロイドの頭 - ストップウォッチ時
	    public int droidHeadSL = 18;
	    public int droidHeadST = 10;
	    public int droidHeadSR = 86;
	    public int droidHeadSB = 41;
		// アンドロイドの頭
	    public int droidHeadL = 18;
	    public int droidHeadT = 10;
	    public int droidHeadR = 86;
	    public int droidHeadB = 73;
		// アンドロイドの右アンテナ
	    public int droidRAntennaP1L = 38;
	    public int droidRAntennaP1T = 16;
	    public int droidRAntennaP2L = 28;
	    public int droidRAntennaP2T = 1;
	    public int droidRAntennaP3L = 29;
	    public int droidRAntennaP3T = 0;
	    public int droidRAntennaP4L = 39;
	    public int droidRAntennaP4T = 15;
		// アンドロイドの左アンテナ
	    public int droidLAntennaP1L = 65;
	    public int droidLAntennaP1T = 15;
	    public int droidLAntennaP2L = 75;
	    public int droidLAntennaP2T = 0;
	    public int droidLAntennaP3L = 76;
	    public int droidLAntennaP3T = 1;
	    public int droidLAntennaP4L = 66;
	    public int droidLAntennaP4T = 16;
    
    // 時、分、秒自動整列用
    public Boolean autoLineBtnFlg = false;							// タッチされた場所が「時、分、秒自動整列ボタン」の範囲か否か
    public boolean autoLineFlg = false;							// 開始・停止判定
    public boolean autoMoveFlg = false;							// 動いた時計があったかを判別
	    

	
	// ファイル入出力用
    public String settingFilePath = "/data/data/" + ArClock.packageName() + "/setting.txt"; // 設定ファイルのパス
    public String timeFilePath = "/data/data/" + ArClock.packageName() + "/time.txt"; // 設定時間ファイルのパス
    public String positionFilePath = "/data/data/" + ArClock.packageName() + "/position.txt"; // 時計位置ファイルのパス
    public int settingNum = 13; // 設定ファイルに書き出される設定の数
    public int timeNum = 84; // 時計位置ファイルに書き出される設定の数
    public int positionNum = 6; // 時計位置ファイルに書き出される設定の数
    public String spliter = ",";
    
    
    
	
	
	
	
	
	
	
    public tView(Context c) {
        super(c);
        setFocusable(true);
        RedrawHandler handler = new RedrawHandler(this, 20);
        handler.start();

        
        specificTime.setTimeInMillis(0);
        specificTime.set(1970,0,1,0,0,0);
        long time = specificTime.getTimeInMillis();
        for(int iii=0; iii < specificTimes.length; iii++){
        	// 指定時間の時間初期化
        	specificTimes[iii] = time;
        	
        	// 指定時間用フラグの初期化
        	specificTimesOnFlg[iii] = false;
        	
        	// 指定時間曜日用フラグの初期化
        	for(int lll=0; lll < specificWeekdayOnFlg[iii].length; lll++){
        		specificWeekdayOnFlg[iii][lll] = false;
        	}
        }
        
        // ストップウォッチの終了時間を初期化
        stopWatchStopTime.setTimeInMillis(stopWatchStartTime.getTimeInMillis());
        
        // カウントダウンの時間の初期化
        countDownTime.setTimeInMillis(0);
        countDownTime.set(1970,0,1,0,0,0);
        countDDatumTime = countDownTime.getTimeInMillis();
        
        // カウントダウン保存時間の初期化
        for(int iii=0; iii < countDSetTimes.length; iii++){
        	countDSetTimes[iii] = countDownTime.getTimeInMillis();
        }
        
        // マスコットPaint設定用
        setRandomColor(droidColor);
        droidColor.setAntiAlias(true);
        
        // アラーム音セット
        mp = MediaPlayer.create(c, R.raw.alarm);
        try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        // バイブレーション
        vibrator = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
        
        // 設定ファイル読み込み
        settingReader();
        // 設定時間ファイル読み込み
        timeReader();
        // 時計位置ファイル読み込み
        positionReader();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	if(hasFocus) {
    		winSizeW = getWidth();
    		winSizeH = getHeight();
    		
    		if(winSizeW < winSizeH){
    			winVertical = true;
    		}else{
    			winVertical = false;
    		}
    		
    		
    		if(winVertical){
    			if(typePnlShowFlg == false){ // このif文がないとホーム押して戻ったときにサブパネルがなくなってしまう
    				// 切替サブパネルの範囲を指定
		    	    typeSubPanelL = -10;
		    	    typeSubPanelT = 290;
		    	    typeSubPanelR = 160;
		    	    typeSubPanelB = 440;
		    	    
		    	    // 切替サブパネルの位置を指定
		    	    typeSubPnlSlideX = TYPE_SUB_VERTI_DEFAULT_X;
		    	    typeSubPnlSlideY = TYPE_SUB_VERTI_DEFAULT_Y;
    			}
    			
    			if(cnfModeFlg == false){
    				// 設定サブパネルの範囲を指定
    			    cnfSubPanelL = -10;
    			    cnfSubPanelT = 290;
    			    cnfSubPanelR = 160;
    			    cnfSubPanelB = 440;
		    	    
		    	    // 設定サブパネルの位置を指定
		    	    cnfSubPnlSlideX = CNF_SUB_VERTI_DEFAULT_X;
		    	    cnfSubPnlSlideY = CNF_SUB_VERTI_DEFAULT_Y;
    			}
	    	    
	    	    // マスコットキャラクターの表示位置を指定
	    	    droidAdjustL = DROIDADJUST_L_VERTI_INIT;
				droidAdjustT = DROIDADJUST_T_VERTI_INIT;
    		}else{
    			if(typePnlShowFlg == false){
	    			// 切替サブパネルの範囲を指定
			    	typeSubPanelL = 320;
			        typeSubPanelT = 0;
			        typeSubPanelR = 480;
			        typeSubPanelB = 140;
			        
		    	    // 切替サブパネルの位置を指定
		    	    typeSubPnlSlideX = TYPE_SUB_HORI_DEFAULT_X;
		    	    typeSubPnlSlideY = TYPE_SUB_HORI_DEFAULT_Y;
    			}
    			
    			if(cnfModeFlg == false){
    				// 設定サブパネルの範囲を指定
    			    cnfSubPanelL = 320;
    			    cnfSubPanelT = 0;
    			    cnfSubPanelR = 480;
    			    cnfSubPanelB = 140;
		    	    
		    	    // 設定サブパネルの位置を指定
		    	    cnfSubPnlSlideX = CNF_SUB_HORI_DEFAULT_X;
		    	    cnfSubPnlSlideY = CNF_SUB_HORI_DEFAULT_Y;
    			}
    			
		        // マスコットキャラクターの表示位置を指定
		        droidAdjustL = DROIDADJUST_L_HORI_INIT;
				droidAdjustT = DROIDADJUST_T_HORI_INIT;
    		}
    	}
    }
    
    protected void onDraw(Canvas canvas) {
    	// 背景色
        canvas.drawColor(Color.WHITE);
        canvas.drawARGB(255, (int)(((double)bgCol[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
        
        // 文字用
        Paint counterColor = new Paint();
        counterColor.setColor(Color.BLACK);
        counterColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
		counterColor.setAntiAlias(true);
		
        // 文字用（時）
        Paint houTCol = new Paint();
        houTCol.setColor(Color.GRAY);
        houTCol.setTextSize(40);
		houTCol.setAntiAlias(true);
		houTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		houTCol.setTextAlign(Paint.Align.CENTER);
		
        // 文字用（分）
        Paint minTCol = new Paint();
        minTCol.setColor(Color.GRAY);
        minTCol.setTextSize(40);
		minTCol.setAntiAlias(true);
		minTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		minTCol.setTextAlign(Paint.Align.CENTER);
		
        // 文字用（秒）
        Paint secTCol = new Paint();
        secTCol.setColor(Color.GRAY);
        secTCol.setTextSize(40);
		secTCol.setAntiAlias(true);
		secTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		secTCol.setTextAlign(Paint.Align.CENTER);
		
        // 時計用（色1）
        Paint cColor1 = new Paint();
        cColor1.setColor(Color.WHITE);
		cColor1.setAntiAlias(true);
        
        // 時計用（色2）
        Paint cColor2 = new Paint();
		cColor2.setAntiAlias(true);
        
        // 時計用（枠）
        Paint fColor = new Paint();
        fColor.setColor(Color.GRAY);
		fColor.setAntiAlias(true);
		fColor.setStyle(Paint.Style.STROKE);
		fColor.setStrokeWidth(1);
		
        
        // 吹き出し用
        Paint balColor = new Paint();
        balColor.setARGB(255, 189, 225, 231);
        balColor.setAntiAlias(true);
        balColor.setStyle(Paint.Style.STROKE);
        balColor.setStrokeWidth(5);
        
        // 吹き出し背景用
        Paint balBackColor = new Paint();
        balBackColor.setARGB(255, 255, 255, 255);
        balBackColor.setAntiAlias(true);
        balBackColor.setStrokeWidth(5);
        
        // 吹き出し文字用
        Paint balTColor = new Paint();
        balTColor.setColor(Color.BLACK);
        balTColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
		balTColor.setAntiAlias(true);
		balTColor.setTextAlign(Paint.Align.CENTER);
		
		// 切替パネル用
        Paint typePnlColor = new Paint();
        typePnlColor.setARGB(200, 176, 196, 222);
        typePnlColor.setAntiAlias(true);
        
        // 切替パネル文字部背景色用
        Paint typeBtnColor = new Paint();
        typeBtnColor.setARGB(255, 255, 255, 122);
        typeBtnColor.setAntiAlias(true);
        
        // 切替パネル（時計）文字部背景色用
        Paint clockBtnColor = new Paint();
        clockBtnColor.setARGB(255, 230, 255, 122);
        clockBtnColor.setAntiAlias(true);
        
        // 切替パネル（時計 - 指定時間設定）指定時間OFF時用
        Paint specificTimeOffBtnColor = new Paint();
        specificTimeOffBtnColor.setARGB(255, 180, 222, 0);
        specificTimeOffBtnColor.setAntiAlias(true);
        
        // 切替パネル（ストップウォッチ）文字部背景色用
        Paint stopWBtnColor = new Paint();
        stopWBtnColor.setARGB(255, 222, 255, 211);
        stopWBtnColor.setAntiAlias(true);
        
        // 切替パネル（カウントダウン）文字部背景色用
        Paint countDBtnColor = new Paint();
        countDBtnColor.setARGB(255, 255, 215, 111);//255, 235, 241
        countDBtnColor.setAntiAlias(true);
        
        
        // 切替パネル時間文字用
        Paint pnlTimeColor = new Paint();
        pnlTimeColor.setColor(Color.GRAY);
        pnlTimeColor.setTextSize(40);
		pnlTimeColor.setAntiAlias(true);
		pnlTimeColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
        pnlTimeColor.setTextAlign(Paint.Align.RIGHT);
		
		// パネル時間増減ボタン用
        Paint pnlTimeUpDownColor = new Paint();
        pnlTimeUpDownColor.setColor(Color.GRAY);
        pnlTimeUpDownColor.setTextSize(20);
		pnlTimeUpDownColor.setAntiAlias(true);
        pnlTimeUpDownColor.setTextAlign(Paint.Align.CENTER);
        pnlTimeUpDownColor.setTextScaleX(2);
		
        
        // サブパネル文字用
        Paint subPnlTxtColor = new Paint();
        subPnlTxtColor.setColor(Color.BLACK);
        subPnlTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
        subPnlTxtColor.setAntiAlias(true);
        
        
        // 記録時間文字用
        Paint recordTxtColor = new Paint();
        recordTxtColor.setColor(Color.BLACK);
        recordTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
        recordTxtColor.setAntiAlias(true);
        recordTxtColor.setTextAlign(Paint.Align.RIGHT);
        
		
		// 設定パネル用
        Paint cnfPnlColor = new Paint();
        cnfPnlColor.setARGB(255, 238, 232, 170);
        cnfPnlColor.setAntiAlias(true);
        
        // 設定パネル表示時の暗転処理用
        Paint cnfCoverColor = new Paint();
        cnfCoverColor.setARGB(150, 0, 0, 0);
        cnfCoverColor.setAntiAlias(true);
        
        // 設定ボタン用
        Paint cnfBtnColor = new Paint();
        cnfBtnColor.setARGB(255, 255, 255, 122);
        cnfBtnColor.setAntiAlias(true);
        
        // 設定ボタン用 - on時
        Paint cnfOnBtnColor = new Paint();
        cnfOnBtnColor.setARGB(255, 255, 255, 122);
        cnfOnBtnColor.setAntiAlias(true);
        
        // 設定ボタン用 - off時
        Paint cnfOffBtnColor = new Paint();
        cnfOffBtnColor.setARGB(255, 200, 200, 90);
        cnfOffBtnColor.setAntiAlias(true);
        
        // 切替、設定パネル文字用
        Paint pnlTxtColor = new Paint();
        pnlTxtColor.setColor(Color.GRAY);
        pnlTxtColor.setAntiAlias(true);
        pnlTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
        pnlTxtColor.setTextAlign(Paint.Align.CENTER);
        
		
		// 時計色１の設定にしたがい色を変更
        cColor1.setARGB(255, (int)(((double)timeCol1[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
		
		// 時計色２の設定にしたがい色を変更
        cColor2.setARGB(255, (int)(((double)timeCol2[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
		
        // アンドロイドの目用
        Paint androidEye = new Paint();
        androidEye.setColor(Color.WHITE);
		
		
		
        // 時計、パネル時計用のカレンダー
        Calendar calendar = Calendar.getInstance();
        
        // 現在の時間取得用
        Calendar nowCalendar = Calendar.getInstance();
        
        
        //// 時間の取得
        // ストップウォッチ時の表示時間設定
        String stopWBtnTxt = "Stop";
        if(typeSwitchNum == TYPE_STOPWATCH){
        	if(stopWatchStartFlg){
        		calendar.setTimeInMillis(calendar.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - calendar.get(Calendar.ZONE_OFFSET));
        	}else{
        		calendar.setTimeInMillis(stopWatchStopTime.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - calendar.get(Calendar.ZONE_OFFSET));
        		stopWBtnTxt = "Start";
        	}
        }
        
        // カウントダウン時の表示時間設定」
        String countDStartStopBtnTxt = "";
    	if(countDownStartFlg){
    		// カウントダウンの残り時間を取得
        	long time = countDownTime.getTimeInMillis() - (nowCalendar.getTimeInMillis() - countDownStartTime.getTimeInMillis());
        	
        	if(time <= countDDatumTime){	// カウント終了した場合
        		// 各変数を初期化
        		time = countDDatumTime;
        		countDownStartFlg = false;
        		countDownTime.setTimeInMillis(time);
        		
        		// カウントダウンモード時のみ表示用のカレンダーの値を初期表示にする
        		if(typeSwitchNum == TYPE_COUNTDOWN){
        			calendar.setTimeInMillis(time);
        		}
        		countDStartStopBtnTxt = "Start";
        		
        		// アンドロイド動作、吹き出し処理
        		countEndFlg = true;
        		balloonInit();
    			
        		// アラーム、バイブレータ処理
        		alarmVibrat();
        	}else{
        		if(typeSwitchNum == TYPE_COUNTDOWN){
            		// カウントダウンの残り時間をセット
            		calendar.setTimeInMillis(time);
            		countDStartStopBtnTxt = "Stop";
        		}
        	}
    	}else{
    		if(typeSwitchNum == TYPE_COUNTDOWN){
        		// 設定中のカウントダウンの値をセット
        		calendar.setTimeInMillis(countDownTime.getTimeInMillis());
        		countDStartStopBtnTxt = "Start";
    		}
    	}
    	
    	// 時計、パネル時計用の時間の取得
        int ampm = calendar.get(Calendar.AM_PM);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int milSec = calendar.get(Calendar.MILLISECOND);
		
		// 現在の時間をそれぞれ取得
        int nowHourOfDay = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
		int nowSecond = nowCalendar.get(Calendar.SECOND);
		int nowWeekDay = nowCalendar.get(Calendar.DAY_OF_WEEK);
		
		// 時計タッチ時の数字に漢字を使う場合の設定
		String[] kansuuji = {"", "壱", "弐", "参", "四", "五", "六", "七", "八", "九", "拾", "〇"};
		
		// 吹き出しに表示する文字を設定
		String[] hourName = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
		
		
		// 孤の設定
		houArc = hour * 30;
		minArc = minute * 6;
		secArc = second * 6;
		secPArc = (int)Math.floor((milSec * 6)/1000);
		//msecArc = (int)Math.round(3.6*(milSec/10));
        
		
		////  時計部分を描画
		// 時の描画
		drawTime(canvas, cColor1, cColor2, fColor, HOUNUM, ampm%2);
		
		// 分の描画
		drawTime(canvas, cColor1, cColor2, fColor, MINNUM, hour%2);
		
		// 秒の描画
		if(onlyHouMinF == false || typeSwitchNum != TYPE_CLOCK){
			drawTime(canvas, cColor1, cColor2, fColor, SECNUM, minute%2);
		}
		
		
		
		//// タッチ時、時間表示
		// 設定に応じた文字表示位置の調整
		int positionAdjust;
		int unitPositionAdjust = 10;
		if(timeTxtNumFlg){
			positionAdjust = 65;
		}else{
			positionAdjust = 60;
			unitPositionAdjust += 3;
		}
		if(timeFace == TIMEFACE_TRIANGLE){
			positionAdjust +=15;
		}else if(timeFace == TIMEFACE_PENTAGON){
			positionAdjust +=5;
		}
		unitPositionAdjust += positionAdjust;
		
		// 時表示
		if(houTouch > 0){
			// フェード処理
			houTAlfa = touchFadeNum(houTouch, houTAlfa);
			houTCol.setAlpha(houTAlfa);
			
			if(timeTxtNumFlg){
				canvas.drawText(String.valueOf(hour), houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("H", houL + houR, houT + unitPositionAdjust, houTCol);
			}else if(hour == 0){
				houTCol.setTextSize(25);
				canvas.drawText(kansuuji[11], houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("時", houL + houR, houT + unitPositionAdjust, houTCol);
			}else if(hour < 10){
		        houTCol.setTextSize(25);
				canvas.drawText(kansuuji[hour], houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("時", houL + houR, houT + unitPositionAdjust, houTCol);
			}else{
		        houTCol.setTextSize(25);
				canvas.drawText(kansuuji[hour/10] + kansuuji[10] + kansuuji[hour%10], houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("時", houL + houR, houT + unitPositionAdjust, houTCol);
			}
			
			houTouch--;
		}
		// 分表示
		if(minTouch > 0){
			// フェード処理
			minTAlfa = touchFadeNum(minTouch, minTAlfa);
			minTCol.setAlpha(minTAlfa);
			
			if(timeTxtNumFlg){
				canvas.drawText(String.valueOf(minute), minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("M", minL + minR, minT + unitPositionAdjust, minTCol);
			}else if(minute == 0){
				minTCol.setTextSize(25);
				canvas.drawText(kansuuji[11], minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("分", minL + minR, minT + unitPositionAdjust, minTCol);
			}else if(minute < 10){
		        minTCol.setTextSize(25);
				canvas.drawText(kansuuji[minute], minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("分", minL + minR, minT + unitPositionAdjust, minTCol);
			}else{
		        minTCol.setTextSize(25);
				canvas.drawText(kansuuji[minute/10] + kansuuji[10] + kansuuji[minute%10], minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("分", minL + minR, minT + unitPositionAdjust, minTCol);
			}
			
			minTouch--;
		}
		// 秒表示
		if(secTouch > 0){
			// フェード処理
			secTAlfa = touchFadeNum(secTouch, secTAlfa);
			secTCol.setAlpha(secTAlfa);
			
			if(timeTxtNumFlg){
				canvas.drawText(String.valueOf(second), secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("S", secL + secR, secT + unitPositionAdjust, secTCol);
			}else if(second == 0){
				secTCol.setTextSize(25);
				canvas.drawText(kansuuji[11], secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("秒", secL + secR, secT + unitPositionAdjust, secTCol);
			}else if(second < 10){
		        secTCol.setTextSize(25);
				canvas.drawText(kansuuji[second], secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("秒", secL + secR, secT + unitPositionAdjust, secTCol);
			}else{
		        secTCol.setTextSize(25);
				canvas.drawText(kansuuji[second/10] + kansuuji[10] + kansuuji[second%10], secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("秒", secL + secR, secT + unitPositionAdjust, secTCol);
			}
			
			secTouch--;
		}
		
		
		
		// 時、分、秒自動整列
		if(autoLineFlg){
			autoMoveFlg = false;	// 各時計が動いたかの判定
			
			// 時の座標を動かす
			houL = setSmallOrBigValue(houL, HOU_L_INITIAL);
			houT = setSmallOrBigValue(houT, HOU_T_INITIAL);

			// 分の座標を動かす
			minL = setSmallOrBigValue(minL, MIN_L_INITIAL);
			minT = setSmallOrBigValue(minT, MIN_T_INITIAL);

			// 秒の座標を動かす
			secL = setSmallOrBigValue(secL, SEC_L_INITIAL);
			secT = setSmallOrBigValue(secT, SEC_T_INITIAL);
			
			if(autoMoveFlg == false){
				autoLineFlg = false;
				
				// 時計位置保存
				positionWriter();
			}
		}
		
		
		
		
        //// マスコットキャラクター画像表示
        // 描画マスコット
        // 目
        canvas.drawRect(new RectF(droidRightEyeL+droidAdjustL-droidLeftEyeR, droidRightEyeT+droidAdjustT-droidLeftEyeR, droidLeftEyeL+droidAdjustL+droidLeftEyeR, droidRightEyeT+droidAdjustT+droidLeftEyeR), androidEye);
        Path clipPath = new Path();
        clipPath.addCircle(droidRightEyeL+droidAdjustL, droidRightEyeT+droidAdjustT, droidRightEyeR, Path.Direction.CW);
        clipPath.addCircle(droidLeftEyeL+droidAdjustL, droidLeftEyeT+droidAdjustT, droidLeftEyeR, Path.Direction.CW);
        canvas.clipPath(clipPath, Op.DIFFERENCE);
        
        Path droidPath = new Path();
        
        if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	        // 腕（左側）- ストップウォッチ時
	        droidPath.moveTo(droidRArmP1L+droidAdjustL, droidRArmP1T+droidAdjustT);
	        droidPath.lineTo(droidRArmP2L+droidAdjustL, droidRArmP2T+droidAdjustT);
	        droidPath.lineTo(droidRArmP3L+droidAdjustL, droidRArmP3T+droidAdjustT);
	        // 腕（右側）- ストップウォッチ時
	        droidPath.moveTo(droidLArmP1L+droidAdjustL, droidLArmP1T+droidAdjustT);
	        droidPath.lineTo(droidLArmP2L+droidAdjustL, droidLArmP2T+droidAdjustT);
	        droidPath.lineTo(droidLArmP3L+droidAdjustL, droidLArmP3T+droidAdjustT);
        }else{
	        // 腕（左側）- 通常時
	        droidPath.addRoundRect(new RectF(droidRightArmL+droidAdjustL, droidRightArmT+droidAdjustT, droidRightArmR+droidAdjustL, droidRightArmB+droidAdjustT), 7, 7, Path.Direction.CW);
	        // 腕（右側）- 通常時
	        droidPath.addRoundRect(new RectF(droidLeftArmL+droidAdjustL, droidLeftArmT+droidAdjustT, droidLeftArmR+droidAdjustL, droidLeftArmB+droidAdjustT), 7, 7, Path.Direction.CW);
        }
        
        // 胴体（上部）
        droidPath.addRect(new RectF(droidTopBodyL+droidAdjustL, droidTopBodyT+droidAdjustT, droidTopBodyR+droidAdjustL, droidTopBodyB+droidAdjustT), Path.Direction.CW);
        // 胴体（下部）
        droidPath.addRoundRect(new RectF(droidBottomBodyL+droidAdjustL, droidBottomBodyT+droidAdjustT, droidBottomBodyR+droidAdjustL, droidBottomBodyB+droidAdjustT), 10, 10, Path.Direction.CW);
        // 足（左側）
        droidPath.addRoundRect(new RectF(droidRightFootL+droidAdjustL, droidRightFootT+droidAdjustT, droidRightFootR+droidAdjustL, droidRightFootB+droidAdjustT), 7, 7, Path.Direction.CW);
        // 足（右側）
        droidPath.addRoundRect(new RectF(droidLeftFootL+droidAdjustL, droidLeftFootT+droidAdjustT, droidLeftFootR+droidAdjustL, droidLeftFootB+droidAdjustT), 7, 7, Path.Direction.CW);
        
        if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	        // 頭 - ストップウォッチ時
	        droidPath.addRect(new RectF(droidHeadSL+droidAdjustL, droidHeadST+droidAdjustT, droidHeadSR+droidAdjustL, droidHeadSB+droidAdjustT), Path.Direction.CW);
        }else{
	        // 頭 - 通常時
	        droidPath.addArc(new RectF(droidHeadL+droidAdjustL, droidHeadT+droidAdjustT, droidHeadR+droidAdjustL, droidHeadB+droidAdjustT), 180, 180);
        }
        // アンテナ？（左側）
        droidPath.moveTo(droidRAntennaP1L+droidAdjustL, droidRAntennaP1T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP2L+droidAdjustL, droidRAntennaP2T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP3L+droidAdjustL, droidRAntennaP3T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP4L+droidAdjustL, droidRAntennaP4T+droidAdjustT);
        // アンテナ？（右側）
        droidPath.moveTo(droidLAntennaP1L+droidAdjustL, droidLAntennaP1T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP2L+droidAdjustL, droidLAntennaP2T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP3L+droidAdjustL, droidLAntennaP3T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP4L+droidAdjustL, droidLAntennaP4T+droidAdjustT);
        
        canvas.drawPath(droidPath, droidColor);
        
        
        
        
        
        
        
        //// 切替パネル描画
        if(typePnlShowFlg && typePnlSlideNum < 0){
        	if(typePnlSlideNum < -25){
        		// 切替パネルを開く
        		typePnlSlideNum += 17;
        		
        		// 切替サブパネルを開く
        		if(winVertical){
	        		typeSubPnlSlideX += 22;
	        		typeSubPnlSlideY -= 19;
        		}else{
        			typeSubPnlSlideX -= 22;
	        		typeSubPnlSlideY += 19;
        		}
        	}else{
        		// 切替パネルを開く
        		typePnlSlideNum += 7;
        		
        		// 切替サブパネルを開く
        		if(winVertical){
	        		typeSubPnlSlideX += 10;
	        		typeSubPnlSlideY -= 9;
        		}else{
        			typeSubPnlSlideX -= 10;
	        		typeSubPnlSlideY += 9;
        		}
        		
        		// 指定位置を過ぎたら指定の数を代入する
        		if(typePnlSlideNum > 0){
        			typePnlSlideNum = 0;
            		typeSubPnlSlideX = 0;
            		typeSubPnlSlideY = 0;
        		}
        	}
        }else if(typePnlShowFlg == false && typePnlSlideNum > TYPE_PNL_DEFAULT_NUM){
        	// 切替パネルを閉じる
        	typePnlSlideNum -= 20;
        	
        	// 切替サブパネルを閉じる
        	if(winVertical){
	    		typeSubPnlSlideX -= 27;
	    		typeSubPnlSlideY += 24;
        	}else{
        		typeSubPnlSlideX += 27;
	    		typeSubPnlSlideY -= 24;
        	}
        	
        	// 指定位置を過ぎたら指定の数を代入する
        	if(typePnlSlideNum < TYPE_PNL_DEFAULT_NUM){
    			typePnlSlideNum = TYPE_PNL_DEFAULT_NUM;
    			if(winVertical){
	        		typeSubPnlSlideX = TYPE_SUB_VERTI_DEFAULT_X;
	        		typeSubPnlSlideY = TYPE_SUB_VERTI_DEFAULT_Y;
    			}else{
    				typeSubPnlSlideX = TYPE_SUB_HORI_DEFAULT_X;
	        		typeSubPnlSlideY = TYPE_SUB_HORI_DEFAULT_Y;
    			}
    		}
        }
        
        // 切替パネル開閉用ボタン
        Path path = new Path();
        int radius = 3;
        path.moveTo(typePnlShowBtnL, typePnlShowBtnT+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR, typePnlShowBtnT+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR, typePnlShowBtnB-radius+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR-radius, typePnlShowBtnB+typePnlSlideNum);
		path.lineTo(typePnlShowBtnL+radius, typePnlShowBtnB+typePnlSlideNum);
		path.lineTo(typePnlShowBtnL, typePnlShowBtnB-radius+typePnlSlideNum);
		canvas.drawPath(path, typePnlColor);
        
        // 切替パネル描画
        canvas.drawRoundRect(new RectF(typePanelL, typePanelT, typePanelR, typePanelB+typePnlSlideNum), 5, 5, typePnlColor);
        
        // 切替サブパネル
        canvas.drawRoundRect(new RectF(typeSubPanelL+typeSubPnlSlideX, typeSubPanelT+typeSubPnlSlideY, typeSubPanelR+typeSubPnlSlideX, typeSubPanelB+typeSubPnlSlideY), 5, 5, typePnlColor);
        
        // 切替パネル表示時のみ描画処理を行う
        if(typePnlShowFlg == true || typePnlSlideNum > -90){
        	// 切り替えボタン（時計）
        	if(typeSwitchNum == TYPE_CLOCK){
        		typeBtnColor = clockBtnColor;
        	}
        	String clTxt = "CL";
        	if(typeSwitchNum == TYPE_CLOCK && showSpecificFlg){
        		// 指定時間設定時は選択中の番号を表示
        		clTxt = String.valueOf(choiceSpecific);
        	}
	        canvas.drawRoundRect(new RectF(clockBtnL, clockBtnT+typePnlSlideNum, clockBtnR, clockBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText(clTxt, clockBtnL+(clockBtnR-clockBtnL)/2, clockBtnT+typePnlSlideNum+(clockBtnB-clockBtnT)/4*3, pnlTxtColor);
	        
	        // 切り替えボタン（ストップウォッチ）
	        typeBtnColor = cnfBtnColor;
	        if(typeSwitchNum == TYPE_STOPWATCH){
        		typeBtnColor = stopWBtnColor;
        	}
	        canvas.drawRoundRect(new RectF(stopWatchBtnL, stopWatchBtnT+typePnlSlideNum, stopWatchBtnR, stopWatchBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText("SW", stopWatchBtnL+(stopWatchBtnR-stopWatchBtnL)/2, stopWatchBtnT+typePnlSlideNum+(stopWatchBtnB-stopWatchBtnT)/4*3, pnlTxtColor);
	        
	        // 切り替えボタン（カウントダウン）
	        typeBtnColor = cnfBtnColor;
	        if(typeSwitchNum == TYPE_COUNTDOWN){
        		typeBtnColor = countDBtnColor;
        	}
	        canvas.drawRoundRect(new RectF(countDownBtnL, countDownBtnT+typePnlSlideNum, countDownBtnR, countDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText("CD", countDownBtnL+(countDownBtnR-countDownBtnL)/2, countDownBtnT+typePnlSlideNum+(countDownBtnB-countDownBtnT)/4*3, pnlTxtColor);
        }
        
        // モードによる文字の背景色変更
        if(typeSwitchNum == TYPE_CLOCK){
    		typeBtnColor = clockBtnColor;
    	}else if(typeSwitchNum == TYPE_STOPWATCH){
    		typeBtnColor = stopWBtnColor;
    	}else if(typeSwitchNum == TYPE_COUNTDOWN){
    		typeBtnColor = countDBtnColor;
    	}
        
        // 切替パネル表示時のみ描画処理を行う
        if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        // 指定時間設定に関する描画
	        if(typeSwitchNum == TYPE_CLOCK){
	        	if(showSpecificFlg == false){
			        // 指定時間設定表示ボタン
			        canvas.drawRoundRect(new RectF(specificTimeBtnL, specificTimeBtnT+typePnlSlideNum, specificTimeBtnR, specificTimeBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Alarm", specificTimeBtnL+(specificTimeBtnR-specificTimeBtnL)/2, specificTimeBtnT+typePnlSlideNum+(specificTimeBtnB-specificTimeBtnT)/9*5, pnlTxtColor);
			        canvas.drawText("setting", specificTimeBtnL+(specificTimeBtnR-specificTimeBtnL)/2, specificTimeBtnT+typePnlSlideNum+((specificTimeBtnB-specificTimeBtnT)/9)*8, pnlTxtColor);
			        
			        // サブパネルタイトルの表示
			        canvas.drawText("Next alarm", typeSubPanelL+typeSubPnlSlideX+subPnlTitleX, typeSubPanelT+typeSubPnlSlideY+subPnlTitleY, subPnlTxtColor);
			        
			        // 次のアラームまでの時間
			        subPnlTxtColor.setTextSize(20);
			        canvas.drawText(nextAlarmValue(nowHourOfDay, nowMinute, nowSecond, nowWeekDay), typeSubPanelL+typeSubPnlSlideX+nextAlarmTxtX, typeSubPanelT+typeSubPnlSlideY+nextAlarmTxtY, subPnlTxtColor);
			        subPnlTxtColor.setTextSize(12);
			        
		        }else{
		        	int subPX = typeSubPanelL+typeSubPnlSlideX;
		        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		        	// サブパネルタイトルの表示
			        canvas.drawText("Alarm number", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
		        	
		        	// 指定時間１設定ボタン
		        	if(specificTimesOnFlg[0]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn1L+subPX, specificBtn1T+subPY, specificBtn1R+subPX, specificBtn1B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("1", specificBtn1L+subPX+(specificBtn1R-specificBtn1L)/2, specificBtn1T+subPY+(specificBtn1B-specificBtn1T)/4*3, pnlTxtColor);
		        	
			        // 指定時間２設定ボタン
			        if(specificTimesOnFlg[1]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn2L+subPX, specificBtn2T+subPY, specificBtn2R+subPX, specificBtn2B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("2", specificBtn2L+subPX+(specificBtn2R-specificBtn2L)/2, specificBtn2T+subPY+(specificBtn2B-specificBtn2T)/4*3, pnlTxtColor);
		        	
			        // 指定時間３設定ボタン
			        if(specificTimesOnFlg[2]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn3L+subPX, specificBtn3T+subPY, specificBtn3R+subPX, specificBtn3B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("3", specificBtn3L+subPX+(specificBtn3R-specificBtn3L)/2, specificBtn3T+subPY+(specificBtn3B-specificBtn3T)/4*3, pnlTxtColor);
		        	
			        // 指定時間４設定ボタン
			        if(specificTimesOnFlg[3]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn4L+subPX, specificBtn4T+subPY, specificBtn4R+subPX, specificBtn4B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("4", specificBtn4L+subPX+(specificBtn4R-specificBtn4L)/2, specificBtn4T+subPY+(specificBtn4B-specificBtn4T)/4*3, pnlTxtColor);
		        	
			        // 指定時間５設定ボタン
			        if(specificTimesOnFlg[4]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn5L+subPX, specificBtn5T+subPY, specificBtn5R+subPX, specificBtn5B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("5", specificBtn5L+subPX+(specificBtn5R-specificBtn5L)/2, specificBtn5T+subPY+(specificBtn5B-specificBtn5T)/4*3, pnlTxtColor);
		        	
			        // 指定時間６設定ボタン
			        if(specificTimesOnFlg[5]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn6L+subPX, specificBtn6T+subPY, specificBtn6R+subPX, specificBtn6B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("6", specificBtn6L+subPX+(specificBtn6R-specificBtn6L)/2, specificBtn6T+subPY+(specificBtn6B-specificBtn6T)/4*3, pnlTxtColor);
		        	
			        // 指定時間７設定ボタン
			        if(specificTimesOnFlg[6]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn7L+subPX, specificBtn7T+subPY, specificBtn7R+subPX, specificBtn7B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("7", specificBtn7L+subPX+(specificBtn7R-specificBtn7L)/2, specificBtn7T+subPY+(specificBtn7B-specificBtn7T)/4*3, pnlTxtColor);
		        	
			        // 指定時間８設定ボタン
			        if(specificTimesOnFlg[7]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn8L+subPX, specificBtn8T+subPY, specificBtn8R+subPX, specificBtn8B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("8", specificBtn8L+subPX+(specificBtn8R-specificBtn8L)/2, specificBtn8T+subPY+(specificBtn8B-specificBtn8T)/4*3, pnlTxtColor);
		        	
			        // 指定時間９設定ボタン
			        if(specificTimesOnFlg[8]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn9L+subPX, specificBtn9T+subPY, specificBtn9R+subPX, specificBtn9B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("9", specificBtn9L+subPX+(specificBtn9R-specificBtn9L)/2, specificBtn9T+subPY+(specificBtn9B-specificBtn9T)/4*3, pnlTxtColor);
		        	
//			        // 指定時間１０設定ボタン
//			        if(specificTimesOnFlg[9]){
//		        		typeBtnColor = clockBtnColor;
//		        	}else{
//		        		typeBtnColor = specificTimeOffBtnColor;
//		        	}
//			        canvas.drawRoundRect(new RectF(specificBtn10L, specificBtn10T+typePnlSlideNum, specificBtn10R, specificBtn10B+typePnlSlideNum), 5, 5, typeBtnColor);
//			        canvas.drawText("10", specificBtn10L+(specificBtn10R-specificBtn10L)/2, specificBtn10T+typePnlSlideNum+(specificBtn10B-specificBtn10T)/4*3, pnlTxtColor);
			        
			        // 曜日設定ボタン１ - 日
			        if(specificWeekdayOnFlg[choiceSpecific-1][0]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn1L, specificWeekdayBtn1T+typePnlSlideNum, specificWeekdayBtn1R, specificWeekdayBtn1B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Sun", specificWeekdayBtn1L+(specificWeekdayBtn1R-specificWeekdayBtn1L)/2, specificWeekdayBtn1T+typePnlSlideNum+(specificWeekdayBtn1B-specificWeekdayBtn1T)/4*3, pnlTxtColor);
		        	
			        // 曜日設定ボタン１ - 月
			        if(specificWeekdayOnFlg[choiceSpecific-1][1]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn2L, specificWeekdayBtn2T+typePnlSlideNum, specificWeekdayBtn2R, specificWeekdayBtn2B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Mon", specificWeekdayBtn2L+(specificWeekdayBtn2R-specificWeekdayBtn2L)/2, specificWeekdayBtn2T+typePnlSlideNum+(specificWeekdayBtn2B-specificWeekdayBtn2T)/4*3, pnlTxtColor);
			        // 曜日設定ボタン１ - 火
			        if(specificWeekdayOnFlg[choiceSpecific-1][2]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn3L, specificWeekdayBtn3T+typePnlSlideNum, specificWeekdayBtn3R, specificWeekdayBtn3B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Tue", specificWeekdayBtn3L+(specificWeekdayBtn3R-specificWeekdayBtn3L)/2, specificWeekdayBtn3T+typePnlSlideNum+(specificWeekdayBtn3B-specificWeekdayBtn3T)/4*3, pnlTxtColor);
		        	
			        // 曜日設定ボタン１ - 水
			        if(specificWeekdayOnFlg[choiceSpecific-1][3]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn4L, specificWeekdayBtn4T+typePnlSlideNum, specificWeekdayBtn4R, specificWeekdayBtn4B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Wed", specificWeekdayBtn4L+(specificWeekdayBtn4R-specificWeekdayBtn4L)/2, specificWeekdayBtn4T+typePnlSlideNum+(specificWeekdayBtn4B-specificWeekdayBtn4T)/4*3, pnlTxtColor);
		        	
			        // 曜日設定ボタン１ - 木
			        if(specificWeekdayOnFlg[choiceSpecific-1][4]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn5L, specificWeekdayBtn5T+typePnlSlideNum, specificWeekdayBtn5R, specificWeekdayBtn5B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Thu", specificWeekdayBtn5L+(specificWeekdayBtn5R-specificWeekdayBtn5L)/2, specificWeekdayBtn5T+typePnlSlideNum+(specificWeekdayBtn5B-specificWeekdayBtn5T)/4*3, pnlTxtColor);
		        	
			        // 曜日設定ボタン１ - 金
			        if(specificWeekdayOnFlg[choiceSpecific-1][5]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn6L, specificWeekdayBtn6T+typePnlSlideNum, specificWeekdayBtn6R, specificWeekdayBtn6B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Fri", specificWeekdayBtn6L+(specificWeekdayBtn6R-specificWeekdayBtn6L)/2, specificWeekdayBtn6T+typePnlSlideNum+(specificWeekdayBtn6B-specificWeekdayBtn6T)/4*3, pnlTxtColor);
		        	
			        // 曜日設定ボタン１ - 土
			        if(specificWeekdayOnFlg[choiceSpecific-1][6]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn7L, specificWeekdayBtn7T+typePnlSlideNum, specificWeekdayBtn7R, specificWeekdayBtn7B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Sat", specificWeekdayBtn7L+(specificWeekdayBtn7R-specificWeekdayBtn7L)/2, specificWeekdayBtn7T+typePnlSlideNum+(specificWeekdayBtn7B-specificWeekdayBtn7T)/4*3, pnlTxtColor);
		        	
			        
			        // 指定時間設定ボタンで変更されているボタン色を元に戻す
			        typeBtnColor = clockBtnColor;
			        
			        // 増減ボタンが押されている間は値が増減し続ける
		        	if(pnlHouUpFlg){
		        		specificUpDownPush(PNLT_TARGET_HOU, COUNTD_UP);
		        	}else if(pnlHouDownFlg){
		        		specificUpDownPush(PNLT_TARGET_HOU, COUNTD_DOWN);
		        	}else if(pnlMinUpFlg){
		        		specificUpDownPush(PNLT_TARGET_MIN, COUNTD_UP);
		        	}else if(pnlMinDownFlg){
		        		specificUpDownPush(PNLT_TARGET_MIN, COUNTD_DOWN);
		        	}
			        
			        // パネル時刻部分に指定時間を表示するように設定
			        Calendar cal = Calendar.getInstance();
			        cal.setTimeInMillis(specificTimes[choiceSpecific-1]);
			        hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
			        minute = cal.get(Calendar.MINUTE);
		        }
	        }
	        

	        // パネル時刻表示
	        DecimalFormat format = new DecimalFormat("00");
	        canvas.drawRoundRect(new RectF(typePnlHourL, typePnlHourT+typePnlSlideNum, typePnlHourR, typePnlHourB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText(format.format(hourOfDay), typePnlHourR-3, typePnlHourT+typePnlSlideNum+(typePnlHourB-typePnlHourT)/5*4, pnlTimeColor);
	        canvas.drawRoundRect(new RectF(typePnlMinuteL, typePnlMinuteT+typePnlSlideNum, typePnlMinuteR, typePnlMinuteB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText(format.format(minute), typePnlMinuteR-3, typePnlMinuteT+typePnlSlideNum+(typePnlMinuteB-typePnlMinuteT)/5*4, pnlTimeColor);
	        if(showSpecificFlg == false){
		        canvas.drawRoundRect(new RectF(typePnlSecondL, typePnlSecondT+typePnlSlideNum, typePnlSecondR, typePnlSecondB+typePnlSlideNum), 5, 5, typeBtnColor);
		        canvas.drawText(format.format(second), typePnlSecondR-3, typePnlSecondT+typePnlSlideNum+(typePnlSecondB-typePnlSecondT)/5*4, pnlTimeColor);
	        }
        }
        
        // ストップウォッチに関する描画
        if(typeSwitchNum == TYPE_STOPWATCH){
            // 切替パネル表示時のみ描画処理を行う
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// ミリ秒表示
            	DecimalFormat format3 = new DecimalFormat("000");
	        	pnlTimeColor.setTextSize(12);
	        	canvas.drawRoundRect(new RectF(typePnlMilSecL, typePnlMilSecT+typePnlSlideNum, typePnlMilSecR, typePnlMilSecB+typePnlSlideNum), 5, 5, typeBtnColor);
	            canvas.drawText(format3.format(milSec), typePnlMilSecR-3, typePnlMilSecT+typePnlSlideNum+(typePnlMilSecB-typePnlMilSecT)/4*3, pnlTimeColor);
	            
		        // サブパネルタイトルの表示
	            String title;
	            if(stopWatchSplitFlg){
	            	title = "Split time";
	            }else{
	            	title = "Rap time";
	            }
		        canvas.drawText(title, typeSubPanelL+typeSubPnlSlideX+subPnlTitleX, typeSubPanelT+typeSubPnlSlideY+subPnlTitleY, subPnlTxtColor);
	            
	            // 記録時間のページ数表示
	            canvas.drawText(stopWatchRecordPage + "／" + ((stopWatchRecordTime.size()-1)/stopWatchPageRecNum+1) , stopWatchPageX+typeSubPanelL+typeSubPnlSlideX, stopWatchPageY+typeSubPanelT+typeSubPnlSlideY, recordTxtColor);
	            
	            // 記録時間の表示
	            int recNumBase = ((stopWatchRecordPage-1)*stopWatchPageRecNum)+1;	// 現在のページの最初の番号
	            int recSize = stopWatchRecordTime.size();
            	DecimalFormat format2 = new DecimalFormat("00");
            	
	            for(int iii=0; iii < stopWatchPageRecNum; iii++){
	            	if(recSize < recNumBase+iii){
	            		// ページに表示する記録時間がなくなったら終了
	            		break;
	            	}

	            	Calendar cal = Calendar.getInstance();
	            	cal.setTimeInMillis(stopWatchRecordTime.get(recSize - iii - recNumBase));
	            	
	            	if(stopWatchSplitFlg == false && recNumBase+iii != recSize){
	            		cal.setTimeInMillis(stopWatchRecordTime.get(recSize-iii-recNumBase) - stopWatchRecordTime.get(recSize-iii-recNumBase-1) - cal.get(Calendar.ZONE_OFFSET));
	            	}
	            	
	            	String hh = format2.format(cal.get(Calendar.HOUR_OF_DAY));
	            	String mm = format2.format(cal.get(Calendar.MINUTE));
	            	String ss = format2.format(cal.get(Calendar.SECOND));
	            	String fff = format3.format(cal.get(Calendar.MILLISECOND));
	            	
	            	
	            	
	            	canvas.drawText(format2.format(recNumBase+iii) + ".  " + hh + ":" + mm + ":" + ss + ":" + fff, stopWatchRecordX+typeSubPanelL+typeSubPnlSlideX, stopWatchRecordY[iii]+typeSubPanelT+typeSubPnlSlideY, subPnlTxtColor);
	            }
            }
	        
            // ストップウォッチ開始終了ボタン描画
        	canvas.drawRoundRect(new RectF(stopWStartStopBtnL, stopWStartStopBtnT, stopWStartStopBtnR, stopWStartStopBtnB), 5, 5, typeBtnColor);
        	canvas.drawText(stopWBtnTxt, stopWStartStopBtnL+(stopWStartStopBtnR-stopWStartStopBtnL)/2, stopWStartStopBtnT+(float)(stopWStartStopBtnB-stopWStartStopBtnT)/11*7, pnlTxtColor);
        }
        
        // パネル時計部の増減ボタン描画処理
        if((typeSwitchNum == TYPE_COUNTDOWN) || (typeSwitchNum == TYPE_CLOCK && showSpecificFlg)){
        	// 切替パネル表示時のみ描画処理を行う
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// 時増減ボタン
	        	canvas.drawRoundRect(new RectF(pnlHouUpBtnL, pnlHouUpBtnT+typePnlSlideNum, pnlHouUpBtnR, pnlHouUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∧", pnlHouUpBtnL+(pnlHouUpBtnR-pnlHouUpBtnL)/2, pnlHouUpBtnT+typePnlSlideNum+(pnlHouUpBtnB-pnlHouUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlHouDownBtnL, pnlHouDownBtnT+typePnlSlideNum, pnlHouDownBtnR, pnlHouDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∨", pnlHouDownBtnL+(pnlHouDownBtnR-pnlHouDownBtnL)/2, pnlHouDownBtnT+typePnlSlideNum+(pnlHouDownBtnB-pnlHouDownBtnT)/5*4, pnlTimeUpDownColor);
	        	// 分増減ボタン
	        	canvas.drawRoundRect(new RectF(pnlMinUpBtnL, pnlMinUpBtnT+typePnlSlideNum, pnlMinUpBtnR, pnlMinUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∧", pnlMinUpBtnL+(pnlMinUpBtnR-pnlMinUpBtnL)/2, pnlMinUpBtnT+typePnlSlideNum+(pnlMinUpBtnB-pnlMinUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlMinDownBtnL, pnlMinDownBtnT+typePnlSlideNum, pnlMinDownBtnR, pnlMinDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∨", pnlMinDownBtnL+(pnlMinDownBtnR-pnlMinDownBtnL)/2, pnlMinDownBtnT+typePnlSlideNum+(pnlMinDownBtnB-pnlMinDownBtnT)/5*4, pnlTimeUpDownColor);
            }
        }
        
        // カウントダウンに関する描画
        if(typeSwitchNum == TYPE_COUNTDOWN){
        	// 切替パネル表示時のみ描画処理を行う
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// 秒増減ボタン
	        	canvas.drawRoundRect(new RectF(pnlSecUpBtnL, pnlSecUpBtnT+typePnlSlideNum, pnlSecUpBtnR, pnlSecUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∧", pnlSecUpBtnL+(pnlSecUpBtnR-pnlSecUpBtnL)/2, pnlSecUpBtnT+typePnlSlideNum+(pnlSecUpBtnB-pnlSecUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlSecDownBtnL, pnlSecDownBtnT+typePnlSlideNum, pnlSecDownBtnR, pnlSecDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("∨", pnlSecDownBtnL+(pnlSecDownBtnR-pnlSecDownBtnL)/2, pnlSecDownBtnT+typePnlSlideNum+(pnlSecDownBtnB-pnlSecDownBtnT)/5*4, pnlTimeUpDownColor);
	        	
	        	// 増減ボタンが押されている間は値が増減し続ける
	        	if(pnlHouUpFlg){
	        		countDownUpDownPush(PNLT_TARGET_HOU, COUNTD_UP);
	        	}else if(pnlHouDownFlg){
	        		countDownUpDownPush(PNLT_TARGET_HOU, COUNTD_DOWN);
	        	}else if(pnlMinUpFlg){
	        		countDownUpDownPush(PNLT_TARGET_MIN, COUNTD_UP);
	        	}else if(pnlMinDownFlg){
	        		countDownUpDownPush(PNLT_TARGET_MIN, COUNTD_DOWN);
	        	}else if(pnlSecUpFlg){
	        		countDownUpDownPush(PNLT_TARGET_SEC, COUNTD_UP);
	        	}else if(pnlSecDownFlg){
	        		countDownUpDownPush(PNLT_TARGET_SEC, COUNTD_DOWN);
	        	}
	        	

	        	int subPX = typeSubPanelL+typeSubPnlSlideX;
	        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		        // サブパネルタイトルの表示
		        canvas.drawText("Set time", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
		        
		        // カウントダウン保存時間
		        Calendar cal = Calendar.getInstance();
		        for(int iii=0; iii < countDSetTimes.length; iii++){
			        cal.setTimeInMillis(countDSetTimes[iii]);
			        DecimalFormat format = new DecimalFormat("00");
			   	    String hh = format.format(cal.get(Calendar.HOUR_OF_DAY));
	            	String mm = format.format(cal.get(Calendar.MINUTE));
	            	String ss = format.format(cal.get(Calendar.SECOND));
	            	
		        	canvas.drawText(hh + ":" + mm + ":" + ss, countDSetTimeX+subPX, countDSetTimeY[iii]+subPY, subPnlTxtColor);
		        }
	        	
	        	// カウントダウン保存ボタンの描画
	        	canvas.drawRoundRect(new RectF(countDSaveBtn1L+subPX, countDSaveBtn1T+subPY, countDSaveBtn1R+subPX, countDSaveBtn1B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn1L+subPX+(countDSaveBtn1R-countDSaveBtn1L)/2, countDSaveBtn1T+subPY+(countDSaveBtn1B-countDSaveBtn1T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSaveBtn2L+subPX, countDSaveBtn2T+subPY, countDSaveBtn2R+subPX, countDSaveBtn2B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn2L+subPX+(countDSaveBtn2R-countDSaveBtn2L)/2, countDSaveBtn2T+subPY+(countDSaveBtn2B-countDSaveBtn2T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSaveBtn3L+subPX, countDSaveBtn3T+subPY, countDSaveBtn3R+subPX, countDSaveBtn3B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn3L+subPX+(countDSaveBtn3R-countDSaveBtn3L)/2, countDSaveBtn3T+subPY+(countDSaveBtn3B-countDSaveBtn3T)/3*2, pnlTxtColor);
	        	
	        	// カウントダウンセット時間ボタンの描画
	        	canvas.drawRoundRect(new RectF(countDSetBtn1L+subPX, countDSetBtn1T+subPY, countDSetBtn1R+subPX, countDSetBtn1B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn1L+subPX+(countDSetBtn1R-countDSetBtn1L)/2, countDSetBtn1T+subPY+(countDSetBtn1B-countDSetBtn1T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSetBtn2L+subPX, countDSetBtn2T+subPY, countDSetBtn2R+subPX, countDSetBtn2B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn2L+subPX+(countDSetBtn2R-countDSetBtn2L)/2, countDSetBtn2T+subPY+(countDSetBtn2B-countDSetBtn2T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSetBtn3L+subPX, countDSetBtn3T+subPY, countDSetBtn3R+subPX, countDSetBtn3B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn3L+subPX+(countDSetBtn3R-countDSetBtn3L)/2, countDSetBtn3T+subPY+(countDSetBtn3B-countDSetBtn3T)/3*2, pnlTxtColor);
            }
            
        	// カウントダウン開始停止ボタン
        	canvas.drawRoundRect(new RectF(countDStartStopBtnL, countDStartStopBtnT, countDStartStopBtnR, countDStartStopBtnB), 5, 5, typeBtnColor);
        	canvas.drawText(countDStartStopBtnTxt, countDStartStopBtnL+(countDStartStopBtnR-countDStartStopBtnL)/2, countDStartStopBtnT+(float)(countDStartStopBtnB-countDStartStopBtnT)/11*7, pnlTxtColor);
        }
        
        
        
        
        
        
        //// 設定パネル描画
        if(cnfModeFlg && cnfPnlSlideNum < 0){
        	if(cnfPnlSlideNum < -25){
            	// 設定パネルを開く
        		cnfPnlSlideNum += 17;
        		
        		// 設定サブパネルを開く
        		if(winVertical){
	        		cnfSubPnlSlideX += 22;
	        		cnfSubPnlSlideY -= 19;
        		}else{
        			cnfSubPnlSlideX -= 22;
	        		cnfSubPnlSlideY += 19;
        		}
        	}else{
        		// 設定パネルを開く
        		cnfPnlSlideNum += 7;
        		
        		// 設定サブパネルを開く
        		if(winVertical){
	        		cnfSubPnlSlideX += 10;
	        		cnfSubPnlSlideY -= 9;
        		}else{
        			cnfSubPnlSlideX -= 12;
	        		cnfSubPnlSlideY += 9;
        		}
        		
        		// 指定位置を過ぎたら指定の数を代入する
        		if(cnfPnlSlideNum > 0){
        			cnfPnlSlideNum = 0;
        			cnfSubPnlSlideX = 0;
	        		cnfSubPnlSlideY = 0;
        		}
        	}
        	// 画面を暗く
        	canvas.drawRect(new RectF(0, 0, winSizeW, winSizeH), cnfCoverColor);
        }else if(cnfModeFlg == false && cnfPnlSlideNum > CNF_PNL_DEFAULT_NUM){
        	// 設定パネルを閉じる
        	cnfPnlSlideNum -= 20;
        	
        	// 設定サブパネルを閉じる
        	if(winVertical){
	    		cnfSubPnlSlideX -= 27;
	    		cnfSubPnlSlideY += 24;
        	}else{
        		cnfSubPnlSlideX += 27;
	    		cnfSubPnlSlideY -= 24;
        	}
        	
        	// 指定位置を過ぎたら指定の数を代入する
        	if(cnfPnlSlideNum < CNF_PNL_DEFAULT_NUM){
    			cnfPnlSlideNum = CNF_PNL_DEFAULT_NUM;
    			if(winVertical){
	        		cnfSubPnlSlideX = CNF_SUB_VERTI_DEFAULT_X;
	        		cnfSubPnlSlideY = CNF_SUB_VERTI_DEFAULT_Y;
    			}else{
    				cnfSubPnlSlideX = CNF_SUB_HORI_DEFAULT_X;
	        		cnfSubPnlSlideY = CNF_SUB_HORI_DEFAULT_Y;
    			}
    		}
        }else if(cnfModeFlg){	// 設定パネル表示中
        	// 画面を暗く
        	canvas.drawRect(new RectF(0, 0, winSizeW, winSizeH), cnfCoverColor);
        }
        
        // 設定パネル開閉用ボタン
        canvas.drawRoundRect(new RectF(cnfPnlShowBtnL, cnfPnlShowBtnT+cnfPnlSlideNum, cnfPnlShowBtnR, cnfPnlShowBtnB+cnfPnlSlideNum), 5, 5, cnfPnlColor);
        
        // 設定パネル描画
        canvas.drawRoundRect(new RectF(cnfPanelL, cnfPanelT, cnfPanelR, cnfPanelB+cnfPnlSlideNum), 5, 5, cnfPnlColor);
        
        // 設定サブパネル
        canvas.drawRoundRect(new RectF(cnfSubPanelL+cnfSubPnlSlideX, cnfSubPanelT+cnfSubPnlSlideY, cnfSubPanelR+cnfSubPnlSlideX, cnfSubPanelB+cnfSubPnlSlideY), 5, 5, cnfPnlColor);
        
        // 設定パネル表示時のみ描画処理を行う
        if(cnfModeFlg || cnfPnlSlideNum > CNF_PNL_DEFAULT_NUM){
        	float btnTxtPos = pnlTxtColor.getTextSize()/3;
        	
	        // 時分のみ設定ボタン
			canvas.drawRoundRect(new RectF(onlyHouMinBtnL, onlyHouMinBtnT+cnfPnlSlideNum, onlyHouMinBtnR, onlyHouMinBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(onlyHouMinBtnTxt, onlyHouMinBtnL+(onlyHouMinBtnR-onlyHouMinBtnL)/2, onlyHouMinBtnT+cnfPnlSlideNum+(onlyHouMinBtnB-onlyHouMinBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// 時間表示文字 数字・漢字切り替えボタン
			canvas.drawRoundRect(new RectF(timeTxtBtnL, timeTxtBtnT+cnfPnlSlideNum, timeTxtBtnR, timeTxtBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(timeTxtBtnTxt, timeTxtBtnL+(timeTxtBtnR-timeTxtBtnL)/2, timeTxtBtnT+cnfPnlSlideNum+(timeTxtBtnB-timeTxtBtnT)/2+btnTxtPos, pnlTxtColor);
			
			canvas.drawRoundRect(new RectF(alarmVibratBtnL, alarmVibratBtnT+cnfPnlSlideNum, alarmVibratBtnR, alarmVibratBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(alarmVibratBtnTxt, alarmVibratBtnL+(alarmVibratBtnR-alarmVibratBtnL)/2, alarmVibratBtnT+cnfPnlSlideNum+(alarmVibratBtnB-alarmVibratBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// 時計形切り替えボタン
			canvas.drawRoundRect(new RectF(timeFaceBtnL, timeFaceBtnT+cnfPnlSlideNum, timeFaceBtnR, timeFaceBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(timeFaceBtnText, timeFaceBtnL+(timeFaceBtnR-timeFaceBtnL)/2, timeFaceBtnT+cnfPnlSlideNum+(timeFaceBtnB-timeFaceBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// TODO 時計色１変更用ボタン
//			setTypeColor(cnfBtnColor, 1, 1);
			cnfBtnColor.setARGB(255, (int)(((double)timeCol1[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(timeCol1BtnL, timeCol1BtnT+cnfPnlSlideNum, timeCol1BtnR, timeCol1BtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
	        // 時計色２変更用ボタン
			cnfBtnColor.setARGB(255, (int)(((double)timeCol2[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(timeCol2BtnL, timeCol2BtnT+cnfPnlSlideNum, timeCol2BtnR, timeCol2BtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
	        // 背景色変更用ボタン
			cnfBtnColor.setARGB(255, (int)(((double)bgCol[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(backColBtnL, backColBtnT+cnfPnlSlideNum, backColBtnR, backColBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
			
			
        	int subPX = cnfSubPanelL+cnfSubPnlSlideX;
        	int subPY = cnfSubPanelT+cnfSubPnlSlideY;
			
			if(cnfSubMode == MODE_CLOCK_DISPLAY){	// 時計（秒）切替モード
				// 設定サブパネルタイトル表示
				canvas.drawText("Clock display", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);

				// 設定用ボタン表示 - HMS
				if(onlyHouMinF){
					cnfBtnColor = cnfOffBtnColor;
				}else{
					cnfBtnColor = cnfOnBtnColor;
				}
				canvas.drawRoundRect(new RectF(hMSBtnL+subPX, hMSBtnT+subPY, hMSBtnR+subPX, hMSBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HMS_TEXT, hMSBtnL+subPX+(hMSBtnR-hMSBtnL)/2, hMSBtnT+subPY+(hMSBtnB-hMSBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - HM
				if(onlyHouMinF){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(hMBtnL+subPX, hMBtnT+subPY, hMBtnR+subPX, hMBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HM_TEXT, hMBtnL+subPX+(hMBtnR-hMBtnL)/2, hMBtnT+subPY+(hMBtnB-hMBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_NUMBER_FORMAT){	// 数字表示切替モード
				// 設定サブパネルタイトル表示
				canvas.drawText("Number format", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// 設定用ボタン表示 - アラビア数字
				if(timeTxtNumFlg){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(arabicBtnL+subPX, arabicBtnT+subPY, arabicBtnR+subPX, arabicBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(ARABIC_TEXT, arabicBtnL+subPX+(arabicBtnR-arabicBtnL)/2, arabicBtnT+subPY+(arabicBtnB-arabicBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 漢数字
				if(timeTxtNumFlg){
					cnfBtnColor = cnfOffBtnColor;
				}else{
					cnfBtnColor = cnfOnBtnColor;
				}
				canvas.drawRoundRect(new RectF(kansuujiBtnL+subPX, kansuujiBtnT+subPY, kansuujiBtnR+subPX, kansuujiBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(KANSUUJI_TEXT, kansuujiBtnL+subPX+(kansuujiBtnR-kansuujiBtnL)/2, kansuujiBtnT+subPY+(kansuujiBtnB-kansuujiBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_ALARM_VIBRATE){	// 音声・バイブ切替モード
				// 設定サブパネルタイトル表示
				canvas.drawText("Alarm vibrate", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// 設定用ボタン表示 - 両方
				if(alarmVibratFlg == ALARM_VIBRAT_BOTH){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(bothBtnL+subPX, bothBtnT+subPY, bothBtnR+subPX, bothBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(BOTH_TEXT, bothBtnL+subPX+(bothBtnR-bothBtnL)/2, bothBtnT+subPY+(bothBtnB-bothBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - アラーム
				if(alarmVibratFlg == ALARM_VIBRAT_ALARM){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(alarmBtnL+subPX, alarmBtnT+subPY, alarmBtnR+subPX, alarmBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(ALARM_TEXT, alarmBtnL+subPX+(alarmBtnR-alarmBtnL)/2, alarmBtnT+subPY+(alarmBtnB-alarmBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - バイブレーション
				if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(vibrateBtnL+subPX, vibrateBtnT+subPY, vibrateBtnR+subPX, vibrateBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(VIBRATE_TEXT, vibrateBtnL+subPX+(vibrateBtnR-vibrateBtnL)/2, vibrateBtnT+subPY+(vibrateBtnB-vibrateBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_CLOCK_SHAPE){	// 時計形変更モード
				// 設定サブパネルタイトル表示
				canvas.drawText("Clock shape", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// 設定用ボタン表示 - 円
				if(timeFace == TIMEFACE_CIRCLE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnCirL+subPX, timeFaceBtnCirT+subPY, timeFaceBtnCirR+subPX, timeFaceBtnCirB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(CIRCLE_TEXT, timeFaceBtnCirL+subPX+(timeFaceBtnCirR-timeFaceBtnCirL)/2, timeFaceBtnCirT+subPY+(timeFaceBtnCirB-timeFaceBtnCirT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 三角
				if(timeFace == TIMEFACE_TRIANGLE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnTriL+subPX, timeFaceBtnTriT+subPY, timeFaceBtnTriR+subPX, timeFaceBtnTriB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(TRIANGLE_TEXT, timeFaceBtnTriL+subPX+(timeFaceBtnTriR-timeFaceBtnTriL)/2, timeFaceBtnTriT+subPY+(timeFaceBtnTriB-timeFaceBtnTriT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 四角
				if(timeFace == TIMEFACE_SQUARE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnSquL+subPX, timeFaceBtnSquT+subPY, timeFaceBtnSquR+subPX, timeFaceBtnSquB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(SQUARE_TEXT, timeFaceBtnSquL+subPX+(timeFaceBtnSquR-timeFaceBtnSquL)/2, timeFaceBtnSquT+subPY+(timeFaceBtnSquB-timeFaceBtnSquT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 五角
				if(timeFace == TIMEFACE_PENTAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnPenL+subPX, timeFaceBtnPenT+subPY, timeFaceBtnPenR+subPX, timeFaceBtnPenB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(PENTAGON_TEXT, timeFaceBtnPenL+subPX+(timeFaceBtnPenR-timeFaceBtnPenL)/2, timeFaceBtnPenT+subPY+(timeFaceBtnPenB-timeFaceBtnPenT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 六角
				if(timeFace == TIMEFACE_HEXAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnHexL+subPX, timeFaceBtnHexT+subPY, timeFaceBtnHexR+subPX, timeFaceBtnHexB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HEXAGON_TEXT, timeFaceBtnHexL+subPX+(timeFaceBtnHexR-timeFaceBtnHexL)/2, timeFaceBtnHexT+subPY+(timeFaceBtnHexB-timeFaceBtnHexT)/2+btnTxtPos, pnlTxtColor);
				
				// 設定用ボタン表示 - 八角
				if(timeFace == TIMEFACE_OCTAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnOctL+subPX, timeFaceBtnOctT+subPY, timeFaceBtnOctR+subPX, timeFaceBtnOctB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(OCTAGON_TEXT, timeFaceBtnOctL+subPX+(timeFaceBtnOctR-timeFaceBtnOctL)/2, timeFaceBtnOctT+subPY+(timeFaceBtnOctB-timeFaceBtnOctT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_CLOCK_COLOR_1 || cnfSubMode == MODE_CLOCK_COLOR_2 || cnfSubMode == MODE_BACKGROUND_COLOR){	// 色選択モード
				int[] gradValue = new int[3];
				
				if(cnfSubMode == MODE_CLOCK_COLOR_1){		// 色選択モード - 時計色１
					// 設定サブパネルタイトル表示
					canvas.drawText("Clock color 1", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// 色の値取得
					gradValue = timeCol1;
				}else if(cnfSubMode == MODE_CLOCK_COLOR_2){	// 色選択モード - 時計色２
					// 設定サブパネルタイトル表示
					canvas.drawText("Clock color 2", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// 色の値取得
					gradValue = timeCol2;
				}else if(cnfSubMode == MODE_BACKGROUND_COLOR){	// 色選択モード - 背景色
					// 設定サブパネルタイトル表示
					canvas.drawText("Background color", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// 色の値取得
					gradValue = bgCol;
				}
				
		        // グラデーションバー用
		        Paint gradationColor = new Paint();
		        gradationColor.setAntiAlias(true);
		        
		        // グラデーションバーの枠用
		        Paint gradFrameColor = new Paint();
		        gradFrameColor.setColor(Color.GRAY);
		        gradFrameColor.setAntiAlias(true);
		        gradFrameColor.setStyle(Paint.Style.STROKE);
		        gradFrameColor.setStrokeWidth(1);
		        
		        // 設定バー用
		        Paint settingBarColor = new Paint();
		        settingBarColor.setARGB(255, 230, 230, 230);
		        settingBarColor.setAntiAlias(true);
		        
		        // 設定バーの枠用
		        Paint setFrameColor = new Paint();
		        setFrameColor.setARGB(255, 230, 230, 230);
		        setFrameColor.setStyle(Paint.Style.STROKE);
		        setFrameColor.setStrokeWidth(0);
		        setFrameColor.setColor(Color.GRAY);
		        setFrameColor.setAntiAlias(true);
				
				// グラデーションの設定
		        Shader gradationRed = new LinearGradient(gradRedL+subPX, 0, gradRedR+subPX, 0, 0xFF000000, 0xFFFF0000, Shader.TileMode.CLAMP);
		        Shader gradationGreen = new LinearGradient(gradGreenL+subPX, 0, gradGreenR+subPX, 0, 0xFF000000, 0xFF00FF00, Shader.TileMode.CLAMP);
		        Shader gradationBlue = new LinearGradient(gradBlueL+subPX, 0, gradBlueR+subPX, 0, 0xFF000000, 0xFF0000FF, Shader.TileMode.CLAMP);
		        
		        // グラデーションバー描画位置設定 - 基本位置
		        Path gradBarPath = new Path();
		        gradBarPath.moveTo(gradPoint1L+subPX, gradPoint1T+subPY);
		        gradBarPath.lineTo(gradPoint2L+subPX, gradPoint2T+subPY);
		        gradBarPath.lineTo(gradPoint3L+subPX, gradPoint3T+subPY);
		        gradBarPath.lineTo(gradPoint4L+subPX, gradPoint4T+subPY);
		        gradBarPath.lineTo(gradPoint5L+subPX, gradPoint5T+subPY);
		        gradBarPath.lineTo(gradPoint6L+subPX, gradPoint6T+subPY);
		        gradBarPath.lineTo(gradPoint7L+subPX, gradPoint7T+subPY);
		        gradBarPath.lineTo(gradPoint8L+subPX, gradPoint8T+subPY);
		        
		        // グラデーションバー表示（赤）
		        gradationColor.setShader(gradationRed);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);
		        
		        // グラデーションバー表示（緑）
		        canvas.translate(0, gradBarAdjust);
		        gradationColor.setShader(gradationGreen);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);
		        
		        // グラデーションバー表示（青）
		        canvas.translate(0, gradBarAdjust);
		        gradationColor.setShader(gradationBlue);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);

		        // 動かしたキャンバスを元に戻す
		        canvas.translate(0, -(gradBarAdjust*2));
		        
		        // 設定バーの表示（赤）
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_RED_NUM]+gradRedL-5+subPX, gradRedT+subPY, gradValue[GRAD_RED_NUM]+gradRedL+5+subPX, gradRedB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_RED_NUM]+gradRedL-5+subPX, gradRedT+subPY, gradValue[GRAD_RED_NUM]+gradRedL+5+subPX, gradRedB+subPY), 5, 5, setFrameColor);
		        // 設定バーの表示（緑）
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_GREEN_NUM]+gradGreenL-5+subPX, gradGreenT+subPY, gradValue[GRAD_GREEN_NUM]+gradGreenL+5+subPX, gradGreenB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_GREEN_NUM]+gradGreenL-5+subPX, gradGreenT+subPY, gradValue[GRAD_GREEN_NUM]+gradGreenL+5+subPX, gradGreenB+subPY), 5, 5, setFrameColor);
		        // 設定バーの表示（青）
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_BLUE_NUM]+gradBlueL-5+subPX, gradBlueT+subPY, gradValue[GRAD_BLUE_NUM]+gradBlueL+5+subPX, gradBlueB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_BLUE_NUM]+gradBlueL-5+subPX, gradBlueT+subPY, gradValue[GRAD_BLUE_NUM]+gradBlueL+5+subPX, gradBlueB+subPY), 5, 5, setFrameColor);

			}
        }
		
        

        
		// 時間ちょうどチェック
		if(nowMinute == 0 && nowSecond == 0 && justTimeFlg == false){
			justTimeFlg = true;
			
			balloonInit();
		}
		
		// 指定時間チェック
		for(int iii=0; iii < specificTimes.length; iii++){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(specificTimes[iii]);
			
			if(specificTimesOnFlg[iii] && specificWeekdayOnFlg[iii][nowWeekDay-1] && nowHourOfDay == cal.get(Calendar.HOUR_OF_DAY) && nowMinute == cal.get(Calendar.MINUTE) && nowSecond == cal.get(Calendar.SECOND) && showSpecificFlg == false && specificFlg == false){
				// アンドロイドと吹き出し
				specificFlg = true;
				balloonInit();
				
				// アラーム、バイブレーター処理
				alarmVibrat();
			}
		}
		
		// 時間がちょうど、カウントダウン後、指定時間に マスコットが跳ねる、吹き出しが出る
        if(justTimeFlg || countEndFlg || specificFlg){
    		RectF balSpace = new RectF();
    		if(balCnt < 10){
    			droidAdjustT -= 3;
        	}else if(balCnt < 11){
        		setRandomColor(droidColor);
        	}else if(balCnt < 20){
        		droidAdjustT += 3;
        	}else if(balCnt < 25){
        		balT -= 4;
        		balB += 4;
        	}else if(balCnt < 30){
        		balL -= 12;
        		balR += 12;
        	}else if(balCnt < 110){
        		balSpace.set(balL, balT, balR, balB);
                canvas.drawRoundRect(balSpace,10,10,balBackColor);
        		if(specificFlg){
        			canvas.drawText("Alarm time.", balL+(balR-balL)/2, balT+(balB-balT)/5*3, balTColor);
        		}else if(justTimeFlg){
        			canvas.drawText("It is " + hourName[hour] + " o'clock.", balL+(balR-balL)/2, balT+(balB-balT)/5*3, balTColor);
        		}else{
        			canvas.drawText("Count end.", balL+(balR-balL)/2, balT+(balB-balT)/5*3, balTColor);
        		}
        	}else if(balCnt < 115){
        		balL += 12;
        		balR -= 12;
        	}else if(balCnt < 120){
        		balT += 4;
        		balB -= 4;
        	}else{
        		specificFlg = false;
        		justTimeFlg = false;
        		countEndFlg = false;
        		if(winVertical){
        			droidAdjustT = DROIDADJUST_T_VERTI_INIT;
        		}else{
        			droidAdjustT = DROIDADJUST_T_HORI_INIT;
        		}
        	}
    		balSpace.set(balL, balT, balR, balB);
            canvas.drawRoundRect(balSpace,10,10,balColor);
            
            balCnt++;
        }

        
        /*************************************************************
         * 
         * デバッグ用
         * 
         * */
/*
		// ただのカウンター 現在何フレーム目かを表示
        String msg = "Frame" + drawCount++;
        canvas.drawText(msg, 2, 30, counterColor);
        
        // 時間等の表示
        canvas.drawText("AP：" + String.valueOf(ampm), 20, 90, counterColor);
        canvas.drawText("時：" + String.valueOf(hour), 20, 110, counterColor);
        canvas.drawText("分：" + String.valueOf(minute), 20, 130, counterColor);
        canvas.drawText("秒：" + String.valueOf(second), 20, 150, counterColor);
        canvas.drawText("ms：" + String.valueOf(milSec), 100, 70, counterColor);
        
        
        canvas.drawText("SP：" + String.valueOf(secPArc), 100, 150, counterColor);
		canvas.drawText("time：" + String.valueOf(calendar.getTimeInMillis()), 100, 130, counterColor);
		canvas.drawText("tz：" + cnfModeFlg + " " + cnfPnlSlideNum , 100, 110, counterColor);
        

        /***********************************************************/
    }
    
    // -------------------------------------------
    // タッチイベント
    // -------------------------------------------
    public boolean onTouchEvent(MotionEvent event) {
        // X,Y座標の取得
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();
        int evAction = (int)event.getAction();

        // タッチしたとき
        if(evAction == MotionEvent.ACTION_DOWN){
        	if(cnfModeFlg == false){	// 設定パネルを表示していない場合のみ
	        	// 時の範囲をタッチした場合
	            if(touchX >= houL && touchX <= houL+houR*2 && touchY >= houT && touchY <= houT+houR*2){
	            	houMove = true;
	            }
	            // 分の範囲をタッチした場合
	            if(touchX >= minL && touchX <= minL+minR*2 && touchY >= minT && touchY <= minT+minR*2){
	            	minMove = true;
	            }
	            // 秒の範囲をタッチした場合
	            if(touchX >= secL && touchX <= secL+secR*2 && touchY >= secT && touchY <= secT+secR*2){
	            	if(onlyHouMinF == false || typeSwitchNum != TYPE_CLOCK){
	            		secMove = true;
	            	}
	            }
	            
	            
	            //// 切替パネル用タッチ領域
	            // 切替パネル開閉ボタンの範囲をタッチした場合
	            if(touchX >= typePnlShowBtnL && touchX <= typePnlShowBtnR && touchY >= typePnlShowBtnT+typePnlSlideNum && touchY <= typePnlShowBtnB+typePnlSlideNum){
	            	if(typePnlShowFlg){
	            		typePnlShowFlg = false;
	            		showSpecificFlg = false;
	            		
	            		// 設定時間ファイル保存
	            		timeWriter();
	            	}else{
	            		typePnlShowFlg = true;
	            	}
	            }
	            
	            // 時計、ストップウォッチ、カウントダウン切り替えボタンの範囲をタッチした場合
	            if(touchX >= clockBtnL && touchX <= clockBtnR && touchY >= clockBtnT+typePnlSlideNum && touchY <= clockBtnB+typePnlSlideNum){	// モード切替ボタン(時計)にタッチした場合
	            	// 現在のモードを時計モードに変更
	            	typeSwitchNum = TYPE_CLOCK;
	            	
	            	// 指定時間設定の
	            	showSpecificFlg = false;
	            }
	            if(touchX >= stopWatchBtnL && touchX <= stopWatchBtnR && touchY >= stopWatchBtnT+typePnlSlideNum && touchY <= stopWatchBtnB+typePnlSlideNum){	// モード切替ボタン(ストップウォッチ)にタッチした場合
	            	if(typeSwitchNum == TYPE_STOPWATCH){
	            		// ストップウォッチ選択中にタッチされた場合はストップウォッチの値を初期化
		            	stopWatchStartFlg = false;
		            	stopWatchStartTime = Calendar.getInstance();
		                stopWatchStopTime.setTimeInMillis(stopWatchStartTime.getTimeInMillis());
		                stopWatchRecordTime.clear();
		                stopWatchRecordPage = 1;
	            	}else{
	            		// 現在のモードをストップウォッチモードに変更
	            		typeSwitchNum = TYPE_STOPWATCH;
		            	showSpecificFlg = false;
	            	}
	            }
	            if(touchX >= countDownBtnL && touchX <= countDownBtnR && touchY >= countDownBtnT+typePnlSlideNum && touchY <= countDownBtnB+typePnlSlideNum){	// モード切替ボタン(カウントダウン)にタッチした場合
	            	if(typeSwitchNum == TYPE_COUNTDOWN){
	            		// カウントダウン選択中にタッチされた場合はカウントダウンの値を初期化
		            	countDownStartFlg = false;
	            		countDownTime.setTimeInMillis(countDDatumTime);
	            	}else{
	            		// 現在のモードをカウントダウンモードに変更
	            		typeSwitchNum = TYPE_COUNTDOWN;
		            	showSpecificFlg = false;
	            	}
	            }
	            
	            
	            // 指定時間設定表示時のみ
	            if(typeSwitchNum == TYPE_CLOCK && showSpecificFlg){
		            // パネル時計の増減ボタンの範囲をタッチした場合
		            if(touchX >= pnlHouUpBtnL && touchX <= pnlHouUpBtnR && touchY >= pnlHouUpBtnT+typePnlSlideNum && touchY <= pnlHouUpBtnB+typePnlSlideNum){
		            	pnlHouUpFlg = true;
		            	specificUpDown(PNLT_TARGET_HOU, COUNTD_UP);
		            }
		            if(touchX >= pnlHouDownBtnL && touchX <= pnlHouDownBtnR && touchY >= pnlHouDownBtnT+typePnlSlideNum && touchY <= pnlHouDownBtnB+typePnlSlideNum){
		            	pnlHouDownFlg = true;
		            	specificUpDown(PNLT_TARGET_HOU, COUNTD_DOWN);
		            }
		            if(touchX >= pnlMinUpBtnL && touchX <= pnlMinUpBtnR && touchY >= pnlMinUpBtnT+typePnlSlideNum && touchY <= pnlMinUpBtnB+typePnlSlideNum){
		            	pnlMinUpFlg = true;
		            	specificUpDown(PNLT_TARGET_MIN, COUNTD_UP);
		            }
		            if(touchX >= pnlMinDownBtnL && touchX <= pnlMinDownBtnR && touchY >= pnlMinDownBtnT+typePnlSlideNum && touchY <= pnlMinDownBtnB+typePnlSlideNum){
		            	pnlMinDownFlg = true;
		            	specificUpDown(PNLT_TARGET_MIN, COUNTD_DOWN);
		            }
		            
		            // 指定時間番号切替ボタンの範囲をタッチした場合// TODO if文をメソッドにしたい
		        	int subPX = typeSubPanelL+typeSubPnlSlideX;
		        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		            int choice = choiceSpecific;
		            if(touchX >= specificBtn1L+subPX && touchX <= specificBtn1R+subPX && touchY >= specificBtn1T+subPY && touchY <= specificBtn1B+subPY){
		            	if(choice == 1){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 1;
		            	}
		            }
		            if(touchX >= specificBtn2L+subPX && touchX <= specificBtn2R+subPX && touchY >= specificBtn2T+subPY && touchY <= specificBtn2B+subPY){
		            	if(choice == 2){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 2;
		            	}
		            }
		            if(touchX >= specificBtn3L+subPX && touchX <= specificBtn3R+subPX && touchY >= specificBtn3T+subPY && touchY <= specificBtn3B+subPY){
		            	if(choice == 3){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 3;
		            	}
		            }
		            if(touchX >= specificBtn4L+subPX && touchX <= specificBtn4R+subPX && touchY >= specificBtn4T+subPY && touchY <= specificBtn4B+subPY){
		            	if(choice == 4){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 4;
		            	}
		            }
		            if(touchX >= specificBtn5L+subPX && touchX <= specificBtn5R+subPX && touchY >= specificBtn5T+subPY && touchY <= specificBtn5B+subPY){
		            	if(choice == 5){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 5;
		            	}
		            }
		            if(touchX >= specificBtn6L+subPX && touchX <= specificBtn6R+subPX && touchY >= specificBtn6T+subPY && touchY <= specificBtn6B+subPY){
		            	if(choice == 6){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 6;
		            	}
		            }
		            if(touchX >= specificBtn7L+subPX && touchX <= specificBtn7R+subPX && touchY >= specificBtn7T+subPY && touchY <= specificBtn7B+subPY){
		            	if(choice == 7){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 7;
		            	}
		            }
		            if(touchX >= specificBtn8L+subPX && touchX <= specificBtn8R+subPX && touchY >= specificBtn8T+subPY && touchY <= specificBtn8B+subPY){
		            	if(choice == 8){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 8;
		            	}
		            }
		            if(touchX >= specificBtn9L+subPX && touchX <= specificBtn9R+subPX && touchY >= specificBtn9T+subPY && touchY <= specificBtn9B+subPY){
		            	if(choice == 9){
		            		if(specificTimesOnFlg[choice-1]){
		            			specificTimesOnFlg[choice-1] = false;
		            		}else{
		            			specificTimesOnFlg[choice-1] = true;
		            		}
		            	}else{
		            		choice = 9;
		            	}
		            }
//		            if(touchX >= specificBtn10L && touchX <= specificBtn10R && touchY >= specificBtn10T+typePnlSlideNum && touchY <= specificBtn10B+typePnlSlideNum){
//		            	if(choice == 10){
//		            		if(specificTimesOnFlg[choice-1]){
//		            			specificTimesOnFlg[choice-1] = false;
//		            		}else{
//		            			specificTimesOnFlg[choice-1] = true;
//		            		}
//		            	}else{
//		            		choice = 10;
//		            	}
//		            }
		            choiceSpecific = choice;
		            
		            // 曜日設定ボタンの範囲をタッチした場合
		            if(touchX >= specificWeekdayBtn1L && touchX <= specificWeekdayBtn1R && touchY >= specificWeekdayBtn1T+typePnlSlideNum && touchY <= specificWeekdayBtn1B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][0]){
	            			specificWeekdayOnFlg[choice-1][0] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][0] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn2L && touchX <= specificWeekdayBtn2R && touchY >= specificWeekdayBtn2T+typePnlSlideNum && touchY <= specificWeekdayBtn2B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][1]){
	            			specificWeekdayOnFlg[choice-1][1] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][1] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn3L && touchX <= specificWeekdayBtn3R && touchY >= specificWeekdayBtn3T+typePnlSlideNum && touchY <= specificWeekdayBtn3B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][2]){
	            			specificWeekdayOnFlg[choice-1][2] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][2] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn4L && touchX <= specificWeekdayBtn4R && touchY >= specificWeekdayBtn4T+typePnlSlideNum && touchY <= specificWeekdayBtn4B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][3]){
	            			specificWeekdayOnFlg[choice-1][3] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][3] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn5L && touchX <= specificWeekdayBtn5R && touchY >= specificWeekdayBtn5T+typePnlSlideNum && touchY <= specificWeekdayBtn5B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][4]){
	            			specificWeekdayOnFlg[choice-1][4] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][4] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn6L && touchX <= specificWeekdayBtn6R && touchY >= specificWeekdayBtn6T+typePnlSlideNum && touchY <= specificWeekdayBtn6B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][5]){
	            			specificWeekdayOnFlg[choice-1][5] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][5] = true;
	            		}
		            }
		            if(touchX >= specificWeekdayBtn7L && touchX <= specificWeekdayBtn7R && touchY >= specificWeekdayBtn7T+typePnlSlideNum && touchY <= specificWeekdayBtn7B+typePnlSlideNum){
		            	if(specificWeekdayOnFlg[choice-1][6]){
	            			specificWeekdayOnFlg[choice-1][6] = false;
	            		}else{
	            			specificWeekdayOnFlg[choice-1][6] = true;
	            		}
		            }
	            }
	            
	            // 指定時間設定 表示用ボタンの範囲をタッチした場合 時計モードで指定時間設定ではない
	            if(touchX >= specificTimeBtnL && touchX <= specificTimeBtnR && touchY >= specificTimeBtnT+typePnlSlideNum && touchY <= specificTimeBtnB+typePnlSlideNum && typeSwitchNum == TYPE_CLOCK && showSpecificFlg == false){
	            		showSpecificFlg = true;
	            }
	            
	            
	            // ストップウォッチモード時のみ
	            if(typeSwitchNum == TYPE_STOPWATCH){
	            	// ストップウォッチ開始終了ボタンの範囲をタッチした場合
		            if(touchX >= stopWStartStopBtnL && touchX <= stopWStartStopBtnR && touchY >= stopWStartStopBtnT && touchY <= stopWStartStopBtnB){
		            	if(stopWatchStartFlg){
		            		stopWatchStopTime = Calendar.getInstance();
		            		stopWatchStartFlg = false;
		            	}else{
		            		stopWatchStartTime = Calendar.getInstance();
		            		stopWatchStartFlg = true;
		            	}
		            }
		            
		            // パネルが開いているときにサブパネルの範囲にタッチした場合
		            if(touchX >= typeSubPanelL && touchX <= typeSubPanelR && touchY >= typeSubPanelT && touchY <= typeSubPanelB && typePnlShowFlg && stopWatchStartFlg){
		            	Calendar cal = Calendar.getInstance();
		            	stopWatchRecordTime.add(cal.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - cal.get(Calendar.ZONE_OFFSET));
		            	
		            	if(stopWatchRecordTime.size() > stopWatchRecTMaxNum){
		            		stopWatchRecordTime.remove(0);
		            	}
		            }
	            }
	            
	            // カウントダウンモード時のみ
	            if(typeSwitchNum == TYPE_COUNTDOWN){
	            	// カウントダウンストップ中のみ
	            	if(countDownStartFlg == false){
			            // パネル時計の増減ボタンの範囲をタッチした場合
			            if(touchX >= pnlHouUpBtnL && touchX <= pnlHouUpBtnR && touchY >= pnlHouUpBtnT+typePnlSlideNum && touchY <= pnlHouUpBtnB+typePnlSlideNum){
			            	pnlHouUpFlg = true;
			            	countDownUpDown(PNLT_TARGET_HOU, COUNTD_UP);
			            }
			            if(touchX >= pnlHouDownBtnL && touchX <= pnlHouDownBtnR && touchY >= pnlHouDownBtnT+typePnlSlideNum && touchY <= pnlHouDownBtnB+typePnlSlideNum){
			            	pnlHouDownFlg = true;
			            	countDownUpDown(PNLT_TARGET_HOU, COUNTD_DOWN);
			            }
			            if(touchX >= pnlMinUpBtnL && touchX <= pnlMinUpBtnR && touchY >= pnlMinUpBtnT+typePnlSlideNum && touchY <= pnlMinUpBtnB+typePnlSlideNum){
			            	pnlMinUpFlg = true;
			            	countDownUpDown(PNLT_TARGET_MIN, COUNTD_UP);
			            }
			            if(touchX >= pnlMinDownBtnL && touchX <= pnlMinDownBtnR && touchY >= pnlMinDownBtnT+typePnlSlideNum && touchY <= pnlMinDownBtnB+typePnlSlideNum){
			            	pnlMinDownFlg = true;
			            	countDownUpDown(PNLT_TARGET_MIN, COUNTD_DOWN);
			            }
			            if(touchX >= pnlSecUpBtnL && touchX <= pnlSecUpBtnR && touchY >= pnlSecUpBtnT+typePnlSlideNum && touchY <= pnlSecUpBtnB+typePnlSlideNum){
			            	pnlSecUpFlg = true;
			            	countDownUpDown(PNLT_TARGET_SEC, COUNTD_UP);
			            }
			            if(touchX >= pnlSecDownBtnL && touchX <= pnlSecDownBtnR && touchY >= pnlSecDownBtnT+typePnlSlideNum && touchY <= pnlSecDownBtnB+typePnlSlideNum){
			            	pnlSecDownFlg = true;
			            	countDownUpDown(PNLT_TARGET_SEC, COUNTD_DOWN);
			            }
	            	}
		            
		            // カウントダウン開始終了ボタンの範囲をタッチした場合
		            if(touchX >= countDStartStopBtnL && touchX <= countDStartStopBtnR && touchY >= countDStartStopBtnT && touchY <= countDStartStopBtnB){
		            	if(countDownStartFlg){	//開始していた場合
		            		Calendar cal = Calendar.getInstance();
		            		
		            		// カウントダウン開始時からの経過時間を取得
		            		cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - countDownStartTime.getTimeInMillis());
		            		
		            		// カウントダウン停止時にミリ秒が中途半端なため、停止後0秒を設定しても時計部分にミリ秒分残る - この問題を解決するには以下2行のコメント化を解除
		            		//cal.set(Calendar.SECOND, cal.get(Calendar.SECOND)-1);
		            		//cal.set(Calendar.MILLISECOND,0);
		            		
		            		// カウントダウンの残り時間をセット
		            		countDownTime.setTimeInMillis(countDownTime.getTimeInMillis() - cal.getTimeInMillis());
		            		countDownStartFlg = false;
		            	}else if(countDownTime.getTimeInMillis() > countDDatumTime){
		            		countDownStartTime = Calendar.getInstance();
		            		countDownStartFlg = true;
		            	}
		            }
		            
		            // サブパネルに表示する際の基準値
		        	int subPX = typeSubPanelL+typeSubPnlSlideX;
		        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		            
		            // カウントダウン停止中の場合
		        	if(countDownStartFlg == false){
		        		// カウントダウン保存ボタンの範囲にタッチした場合
		            	Calendar cal = Calendar.getInstance();
		            	cal.setTimeInMillis(countDownTime.getTimeInMillis());
		            	int yer = 1970;
		                int mon = 0;
		                int day = 1;
		                int hou = cal.get(Calendar.HOUR_OF_DAY);
		                int min = cal.get(Calendar.MINUTE);
		                int sec = cal.get(Calendar.SECOND);
		                cal.setTimeInMillis(0);
		                cal.set(yer, mon, day, hou, min, sec);
			            if(touchX >= countDSaveBtn1L+subPX && touchX <= countDSaveBtn1R+subPX && touchY >= countDSaveBtn1T+subPY && touchY <= countDSaveBtn1B+subPY){
			            	countDSetTimes[0] = cal.getTimeInMillis();
			            }
			            if(touchX >= countDSaveBtn2L+subPX && touchX <= countDSaveBtn2R+subPX && touchY >= countDSaveBtn2T+subPY && touchY <= countDSaveBtn2B+subPY){
			            	countDSetTimes[1] = cal.getTimeInMillis();
			            }
			            if(touchX >= countDSaveBtn3L+subPX && touchX <= countDSaveBtn3R+subPX && touchY >= countDSaveBtn3T+subPY && touchY <= countDSaveBtn3B+subPY){
			            	countDSetTimes[2] = cal.getTimeInMillis();
			            }
			            
			            // カウントダウンセットボタンの範囲にタッチした場合
			            if(touchX >= countDSetBtn1L+subPX && touchX <= countDSetBtn1R+subPX && touchY >= countDSetBtn1T+subPY && touchY <= countDSetBtn1B+subPY){
			            	countDownTime.setTimeInMillis(countDSetTimes[0]);
			            }
			            if(touchX >= countDSetBtn2L+subPX && touchX <= countDSetBtn2R+subPX && touchY >= countDSetBtn2T+subPY && touchY <= countDSetBtn2B+subPY){
			            	countDownTime.setTimeInMillis(countDSetTimes[1]);
			            }
			            if(touchX >= countDSetBtn3L+subPX && touchX <= countDSetBtn3R+subPX && touchY >= countDSetBtn3T+subPY && touchY <= countDSetBtn3B+subPY){
			            	countDownTime.setTimeInMillis(countDSetTimes[2]);
			            }
			        }
	            }
	            
	            // ストップウォッチモード時でパネルを開いている場合
	            if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	            	// アンドロイドの左側の腕（右腕）の範囲をタッチした場合
	            	if(touchX >= droidRightArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidRightArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidRightArmT+droidAdjustT && touchY <= droidRightArmB+droidAdjustT){
	            		// 記録時間を戻す処理
	            		if(stopWatchRecordPage > 1){
	            			stopWatchRecordPage--;
	            		}
	            	}
	            	
	            	// アンドロイドの頭の範囲をタッチした場合
	            	if(touchX >= droidHeadSL+droidAdjustL && touchX <= droidHeadSR+droidAdjustL && touchY >= droidHeadST+droidAdjustT && touchY <= droidHeadSB+droidAdjustT){
	            		if(stopWatchSplitFlg){
	            			stopWatchSplitFlg = false;
	            		}else{
	            			stopWatchSplitFlg = true;
	            		}
	            	}
	            }
	            
	            // アンドロイド右側の腕（左腕）の範囲をタッチした場合
	            if(touchX >= droidLeftArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidLeftArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidLeftArmT+droidAdjustT && touchY <= droidLeftArmB+droidAdjustT){
	            	
	            	if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){	// ストップウォッチモード時でパネルを開いている場合
	            		// 記録時間ページを進める処理
	            		if(stopWatchRecordPage < ((stopWatchRecordTime.size()-1)/stopWatchPageRecNum+1)){
	            			stopWatchRecordPage++;
	            		}
	            	}else{
	            		// タッチされたフラグ - タッチアップ時に使用
	            		autoLineBtnFlg = true;
	            	}
	            }
        	}
        	
        	
            //// 設定用タッチ領域
            // 設定パネル開閉ボタンの範囲をタッチした場合
            if(touchX >= cnfPnlShowBtnL && touchX <= cnfPnlShowBtnR && touchY >= cnfPnlShowBtnT+cnfPnlSlideNum && touchY <= cnfPnlShowBtnB+cnfPnlSlideNum){
            	if(cnfModeFlg){
            		cnfModeFlg = false;
            		
            		// 設定ファイル保存
            		settingWriter();
            	}else{
            		cnfModeFlg = true;
            	}
            }
            
            // 時分のみボタンの範囲をタッチした場合
            if(touchX >= onlyHouMinBtnL && touchX <= onlyHouMinBtnR && touchY >= onlyHouMinBtnT+cnfPnlSlideNum && touchY <= onlyHouMinBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_DISPLAY;
            }
            
            // 時計文字数字漢字切替ボタンの範囲をタッチした場合
            if(touchX >= timeTxtBtnL && touchX <= timeTxtBtnR && touchY >= timeTxtBtnT+cnfPnlSlideNum && touchY <= timeTxtBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_NUMBER_FORMAT;
            }
            
            // 音声・バイブ切替ボタンの範囲をタッチした場合
            if(touchX >= alarmVibratBtnL && touchX <= alarmVibratBtnR && touchY >= alarmVibratBtnT+cnfPnlSlideNum && touchY <= alarmVibratBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_ALARM_VIBRATE;
            }
            
            // 時計形変更ボタンの範囲をタッチした場合
            if(touchX >= timeFaceBtnL && touchX <= timeFaceBtnR && touchY >= timeFaceBtnT+cnfPnlSlideNum && touchY <= timeFaceBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_SHAPE;
            }
            
            // 時計色１変更ボタンの範囲をタッチした場合
            if(touchX >= timeCol1BtnL && touchX <= timeCol1BtnR && touchY >= timeCol1BtnT+cnfPnlSlideNum && touchY <= timeCol1BtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_COLOR_1;
            }
            
            // 時計色２変更ボタンの範囲をタッチした場合
            if(touchX >= timeCol2BtnL && touchX <= timeCol2BtnR && touchY >= timeCol2BtnT+cnfPnlSlideNum && touchY <= timeCol2BtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_COLOR_2;
            }

            // 時計色２変更ボタンの範囲をタッチした場合
            if(touchX >= backColBtnL && touchX <= backColBtnR && touchY >= backColBtnT+cnfPnlSlideNum && touchY <= backColBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_BACKGROUND_COLOR;
            }
            
            
            
            // サブパネルに表示する際の基準値
        	int subPX = cnfSubPanelL+cnfSubPnlSlideX;
        	int subPY = cnfSubPanelT+cnfSubPnlSlideY;
            
        	if(cnfSubMode == MODE_CLOCK_DISPLAY){	// 時計（秒）切替モード
	            if(touchX >= hMSBtnL+subPX && touchX <= hMSBtnR+subPX && touchY >= hMSBtnT+subPY && touchY <= hMSBtnB+subPY){
	            	onlyHouMinF = false;
	            	onlyHouMinBtnTxt = HMS_TEXT;
	            }
	            if(touchX >= hMBtnL+subPX && touchX <= hMBtnR+subPX && touchY >= hMBtnT+subPY && touchY <= hMBtnB+subPY){
	            	onlyHouMinF = true;
	            	onlyHouMinBtnTxt = HM_TEXT;
	            }
			}else if(cnfSubMode == MODE_NUMBER_FORMAT){	// 数字表示切替モード
	            if(touchX >= arabicBtnL+subPX && touchX <= arabicBtnR+subPX && touchY >= arabicBtnT+subPY && touchY <= arabicBtnB+subPY){
	            	timeTxtNumFlg = true;
	            	timeTxtBtnTxt = ARABIC_TEXT;
	            }
	            if(touchX >= kansuujiBtnL+subPX && touchX <= kansuujiBtnR+subPX && touchY >= kansuujiBtnT+subPY && touchY <= kansuujiBtnB+subPY){
	            	timeTxtNumFlg = false;
	            	timeTxtBtnTxt = KANSUUJI_TEXT;
	            }
			}else if(cnfSubMode == MODE_ALARM_VIBRATE){	// 音声・バイブ切替モード
	            if(touchX >= bothBtnL+subPX && touchX <= bothBtnR+subPX && touchY >= bothBtnT+subPY && touchY <= bothBtnB+subPY){
	            	alarmVibratFlg = ALARM_VIBRAT_BOTH;
	            	alarmVibratBtnTxt = BOTH_TEXT;
	            }
	            if(touchX >= alarmBtnL+subPX && touchX <= alarmBtnR+subPX && touchY >= alarmBtnT+subPY && touchY <= alarmBtnB+subPY){
	            	alarmVibratFlg = ALARM_VIBRAT_ALARM;
	            	alarmVibratBtnTxt = ALARM_TEXT;
	            }
	            if(touchX >= vibrateBtnL+subPX && touchX <= vibrateBtnR+subPX && touchY >= vibrateBtnT+subPY && touchY <= vibrateBtnB+subPY){
	            	alarmVibratFlg = ALARM_VIBRAT_VIBRAT;
	            	alarmVibratBtnTxt = VIBRATE_TEXT;
	            }
			}else if(cnfSubMode == MODE_CLOCK_SHAPE){	// 時計形変更モード
	            // 時計形変更ボタンをの範囲をタッチした場合
	            if(touchX >= timeFaceBtnCirL+subPX && touchX <= timeFaceBtnCirR+subPX && touchY >= timeFaceBtnCirT+subPY && touchY <= timeFaceBtnCirB+subPY){
	            	timeFace = TIMEFACE_CIRCLE;
	            	timeFaceBtnText = CIRCLE_TEXT;
	            }
	            if(touchX >= timeFaceBtnTriL+subPX && touchX <= timeFaceBtnTriR+subPX && touchY >= timeFaceBtnTriT+subPY && touchY <= timeFaceBtnTriB+subPY){
	            	timeFace = TIMEFACE_TRIANGLE;
	            	timeFaceBtnText = TRIANGLE_TEXT;
	            }
	            if(touchX >= timeFaceBtnSquL+subPX && touchX <= timeFaceBtnSquR+subPX && touchY >= timeFaceBtnSquT+subPY && touchY <= timeFaceBtnSquB+subPY){
	            	timeFace = TIMEFACE_SQUARE;
	            	timeFaceBtnText = SQUARE_TEXT;
	            }
	            if(touchX >= timeFaceBtnPenL+subPX && touchX <= timeFaceBtnPenR+subPX && touchY >= timeFaceBtnPenT+subPY && touchY <= timeFaceBtnPenB+subPY){
	            	timeFace = TIMEFACE_PENTAGON;
	            	timeFaceBtnText = PENTAGON_TEXT;
	            }
	            if(touchX >= timeFaceBtnHexL+subPX && touchX <= timeFaceBtnHexR+subPX && touchY >= timeFaceBtnHexT+subPY && touchY <= timeFaceBtnHexB+subPY){
	            	timeFace = TIMEFACE_HEXAGON;
	            	timeFaceBtnText = HEXAGON_TEXT;
	            }
	            if(touchX >= timeFaceBtnOctL+subPX && touchX <= timeFaceBtnOctR+subPX && touchY >= timeFaceBtnOctT+subPY && touchY <= timeFaceBtnOctB+subPY){
	            	timeFace = TIMEFACE_OCTAGON;
	            	timeFaceBtnText = OCTAGON_TEXT;
	            }
			}else if((cnfSubMode == MODE_CLOCK_COLOR_1 || cnfSubMode == MODE_CLOCK_COLOR_2 || cnfSubMode == MODE_BACKGROUND_COLOR) && cnfSubPnlSlideX == 0){
	            // グラデーション（赤）範囲
	            if(touchX >= gradRedL+subPX && touchX <= gradRedR+subPX && touchY >= gradRedT+subPY && touchY <= gradRedB+subPY){
	            	// 赤を設定中とする
	            	gradSettingColor = GRAD_RED_NUM;
	            	
	            	// 色の設定をする
	            	setColor(touchX - (gradRedL+subPX));
	            }
	            // グラデーション（緑）範囲
	            if(touchX >= gradGreenL+subPX && touchX <= gradGreenR+subPX && touchY >= gradGreenT+subPY && touchY <= gradGreenB+subPY){
	            	// 緑を設定中とする
	            	gradSettingColor = GRAD_GREEN_NUM;
	            	
	            	// 色の設定をする
	            	setColor(touchX - (gradGreenL+subPX));
	            }
	            // グラデーション（青）範囲
	            if(touchX >= gradBlueL+subPX && touchX <= gradBlueR+subPX && touchY >= gradBlueT+subPY && touchY <= gradBlueB+subPY){
	            	// 青を設定中とする
	            	gradSettingColor = GRAD_BLUE_NUM;
	            	
	            	// 色の設定をする
	            	setColor(touchX - (gradBlueL+subPX));
	            }
			}
        }
        
        // 動かしたとき
        if(evAction == MotionEvent.ACTION_MOVE){
        	// 時計の配置を移動する
        	if(secMove){
        		secL = touchX - houR;
        		secT = touchY - houR;
        	}else if(minMove){
        		minL = touchX - minR;
        		minT = touchY - minR;
        	}else if(houMove){
        		houL = touchX - secR;
        		houT = touchY - secR;
        	}
        	
        	// グラデーションの値を変更する
        	if((gradSettingColor == GRAD_RED_NUM || gradSettingColor == GRAD_GREEN_NUM || gradSettingColor == GRAD_BLUE_NUM) && cnfSubPnlSlideX == 0){
        		int subPX = cnfSubPanelL+cnfSubPnlSlideX;
	        	int colNum;
	        	if(touchX < gradBlueL+subPX){
	        		colNum = gradMinNum;
	        	}else if(touchX > gradBlueL+subPX+gradMaxNum){
	        		colNum = gradMaxNum;
	        	}else{
	        		colNum = touchX - (gradBlueL+subPX);
	        	}
	        	// 色の設定をする
	        	setColor(colNum);
        	}
        }
        
        // 指を離したとき
        if(evAction == MotionEvent.ACTION_UP){
        	// 時計を動かしていた場合
        	if(houMove || minMove || secMove){
        		// 時計位置の保存
        		positionWriter();
        		
        		// 時、分、秒図形移動フラグOFF
            	houMove = false;
            	minMove = false;
            	secMove = false;
        	}

        	
        	// パネル時間部分の増減フラグOFF
        	pnlHouUpFlg = false;
        	pnlHouDownFlg = false;
        	pnlMinUpFlg = false;
        	pnlMinDownFlg = false;
        	pnlSecUpFlg = false;
        	pnlSecDownFlg = false;
        	
        	// パネル増減時のウェイトを初期化
        	upDownwait = 5;
        	
        	// アンドロイドの左腕クリック処理
        	if(touchX >= droidLeftArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidLeftArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidLeftArmT+droidAdjustT && touchY <= droidLeftArmB+droidAdjustT && autoLineBtnFlg){
        		autoLineFlg = true;
        	}
        	autoLineBtnFlg = false;
        	
        	// グラデーションの設定中判定をなしに変更する
        	gradSettingColor = GRAD_NO_COLOR;
        }
        
        if(cnfModeFlg == false){
	        // 時の上を通過した場合
	        if(touchX >= houL && touchX <= houL+houR*2 && touchY >= houT && touchY <= houT+houR*2 && houTouch == 0 && houMove == false){
	        	houTouch = TOUCHMAXNUM;
	        }
	        // 分の上を通過した場合
	        if(touchX >= minL && touchX <= minL+minR*2 && touchY >= minT && touchY <= minT+minR*2 && minTouch == 0 && minMove == false){
	        	minTouch = TOUCHMAXNUM;
	        }
	        // 秒の上を通過した場合
	        if(touchX >= secL && touchX <= secL+secR*2 && touchY >= secT && touchY <= secT+secR*2 && secTouch == 0 && secMove == false && onlyHouMinF == false){
	        	secTouch = TOUCHMAXNUM;
	        }
        }
        
        return true;
    }
    
    /*************************************************
     *  時計部分描画
     * canvas		:キャンバス
     * cColor1		:時計色１
     * cColor2		:時計色２
     * fColor		:時計枠
     * hms			:時、分、秒のどれが対象なのか
     * switchCol	:どっちの色で重ねるターンなのか
     * 
     * */
    private void drawTime(Canvas canvas, Paint cColor1, Paint cColor2, Paint fColor, int hms, int switchCol){
    	
		int objL;
		int objT;
		int objR;
		int objArc;
		RectF objOval;
		int bgSize;
		Path path = new Path();
    	
		// 時、分、秒に応じて描画場所、時間を変更する
		if(hms == HOUNUM){
    		objL = houL;
    		objT = houT;
    		objR = houR;
    		objArc = houArc;
    	}else if(hms == MINNUM){
    		objL = minL;
    		objT = minT;
    		objR = minR;
    		objArc = minArc;
    	}else{
    		objL = secL;
    		objT = secT;
    		objR = secR;
    		objArc = secArc + secPArc; // 「+ secPArc」 を追加で秒がスムーズに動く
    	}
		
		bgSize = (int)((objR * 2) * Math.sqrt(2) - (objR * 2)) / 2;
		
		// 時計形に応じた描画
		switch(timeFace){
			case TIMEFACE_TRIANGLE:
				objOval = new RectF(objL-bgSize, objT+(int)(objR-objR/Math.sqrt(3))-bgSize, objL+objR*2+bgSize, objT+objR*2+(int)(objR-objR/Math.sqrt(3))+bgSize);
	    		objT += 100;
				
	    		//// 描画範囲指定 ～canvas.restore();まで
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+objR, objT-(int)(objR*Math.sqrt(3)));
				path.lineTo(objL+objR*2, objT);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// 孤の描画
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawPath(path, fColor);
				break;
			case TIMEFACE_SQUARE:
				objOval = new RectF(objL-bgSize, objT-bgSize, objL+objR*2+bgSize, objT+objR*2+bgSize);
				
				//// 描画範囲指定 ～canvas.restore();まで
				canvas.save();
				canvas.clipRect(objL, objT, objL+objR*2, objT+objR*2);
					// 孤の描画
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawRect(objL, objT, objL+objR*2, objT+objR*2, fColor);
				break;
			case TIMEFACE_PENTAGON:
				int objShift = 8;
				objOval = new RectF(objL-bgSize, objT-bgSize+objShift, objL+objR*2+bgSize, objT+objR*2+bgSize+objShift);
				objT += 42;
				
				final int penPntL1 = 50;
				final int penPntL2 = 100;
				final int penPntL3 = 80;
				final int penPntL4 = 20;
				final int penPntT1 = -37;
				final int penPntT2 = 0;
				final int penPntT3 = 58;
				final int penPntT4 = 58;
				
				
				//// 描画範囲指定 ～canvas.restore();まで
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+penPntL1, objT+penPntT1);
				path.lineTo(objL+penPntL2, objT+penPntT2);
				path.lineTo(objL+penPntL3, objT+penPntT3);
				path.lineTo(objL+penPntL4, objT+penPntT4);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// 孤の描画
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawPath(path, fColor);
				break;
			case TIMEFACE_HEXAGON:
				objOval = new RectF(objL-bgSize, objT-bgSize, objL+objR*2+bgSize, objT+objR*2+bgSize);
				objT += 50;
				
				final int hexPntL1 = 25;
				final int hexPntL2 = 75;
				final int hexPntL3 = 100;
				final int hexPntL4 = 75;
				final int hexPntL5 = 25;
				final int hexPntT1 = -43;
				final int hexPntT2 = -43;
				final int hexPntT3 = 0;
				final int hexPntT4 = 43;
				final int hexPntT5 = 43;
				
				//// 描画範囲指定 ～canvas.restore();まで
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+hexPntL1, objT+hexPntT1);
				path.lineTo(objL+hexPntL2, objT+hexPntT2);
				path.lineTo(objL+hexPntL3, objT+hexPntT3);
				path.lineTo(objL+hexPntL4, objT+hexPntT4);
				path.lineTo(objL+hexPntL5, objT+hexPntT5);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// 孤の描画
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawPath(path, fColor);
				break;
			case TIMEFACE_OCTAGON:
				objOval = new RectF(objL-bgSize, objT-bgSize, objL+objR*2+bgSize, objT+objR*2+bgSize);
				objT += 30;
				
				final int octPntL1 = 30;
				final int octPntL2 = 70;
				final int octPntL3 = 100;
				final int octPntL4 = 100;
				final int octPntL5 = 70;
				final int octPntL6 = 30;
				final int octPntL7 = 0;
				final int octPntT1 = -30;
				final int octPntT2 = -30;
				final int octPntT3 = 0;
				final int octPntT4 = 40;
				final int octPntT5 = 70;
				final int octPntT6 = 70;
				final int octPntT7 = 40;
				
				//// 描画範囲指定 ～canvas.restore();まで
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+octPntL1, objT+octPntT1);
				path.lineTo(objL+octPntL2, objT+octPntT2);
				path.lineTo(objL+octPntL3, objT+octPntT3);
				path.lineTo(objL+octPntL4, objT+octPntT4);
				path.lineTo(objL+octPntL5, objT+octPntT5);
				path.lineTo(objL+octPntL6, objT+octPntT6);
				path.lineTo(objL+octPntL7, objT+octPntT7);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// 孤の描画
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawPath(path, fColor);
				break;
			default:
				objOval = new RectF(objL, objT, objL+objR*2, objT+objR*2);
				if(switchCol == 1){
					canvas.drawCircle(objL+objR, objT+objR, objR, cColor2);
					canvas.drawArc(objOval, 270, objArc, true, cColor1);
				}else{
					canvas.drawCircle(objL+objR, objT+objR, objR, cColor1);
					canvas.drawArc(objOval, 270, objArc, true, cColor2);
				}
				canvas.drawCircle(objL+objR, objT+objR, objR, fColor);
				break;
			
		}
    }
    
    // -------------------------------------------
    // 時計の時間を示す部分の描画（孤の描画）
    // -------------------------------------------
    private void drawTimeArc(Canvas canvas, RectF objOval, Paint cColor1, Paint cColor2,int objArc, int switchCol){
    	int startAngle = 270;
    	
    	if(switchCol == 1){
			canvas.drawRect(objOval, cColor2);
			canvas.drawArc(objOval, startAngle, objArc, true, cColor1);
		}else{
			canvas.drawRect(objOval, cColor1);
			canvas.drawArc(objOval, startAngle, objArc, true, cColor2);
		}
    }
    
    // -------------------------------------------
    // フェード処理 - 時計部タッチ時の数字に使用
    // -------------------------------------------
    private int touchFadeNum(int touchNum, int alfa){
		if(touchNum == TOUCHMAXNUM){
			alfa = 10;
		}else if(touchNum >= TOUCHMAXNUM - 10){
			alfa += 25;
			if(alfa > 255){
				alfa = 255;
			}
		}else if(touchNum < 25){
			alfa -= 10;
			if(alfa < 0){
				alfa = 0;
			}
		}else{
			alfa = 255;
		}
		
		return alfa;
    }
    
    // -------------------------------------------
    // valueの値をinitialの値に応じて変更する - 整列処理に使用
    // -------------------------------------------
    private int setSmallOrBigValue(int value,int initial){
		if(value < initial){
			value += 5;
			autoMoveFlg = true;
		}else if(value > initial){
			value -= 5;
			autoMoveFlg = true;
		}
		
		if(Math.abs(value-initial) < 5){
			value = initial;
		}
		
		return value;
    }
    
    // -------------------------------------------
    // Androidの縦、横の状況によって吹き出し位置の変更
    // -------------------------------------------
    private void balloonInit(){
    	if(winVertical){
			// Android縦向き時の設定
			balL = BALL_VERTI_INIT;
			balT = BALT_VERTI_INIT;
			balR = BALR_VERTI_INIT;
			balB = BALB_VERTI_INIT;
		}else{
			// Android横向き時の設定
			balL = BALL_HORI_INIT;
			balT = BALT_HORI_INIT;
			balR = BALR_HORI_INIT;
			balB = BALB_HORI_INIT;
		}
		
		balCnt = BALCNT_INIT;
    }
    
    // -------------------------------------------
    // カウントダウンの時間を増減させる
    // -------------------------------------------
    private void countDownUpDown(int TargetTime, int upDown){
    	int pnlYer = countDownTime.get(Calendar.YEAR);
        int pnlMon = countDownTime.get(Calendar.MONTH);
        int pnlDay = countDownTime.get(Calendar.DATE);
        int pnlHou = countDownTime.get(Calendar.HOUR_OF_DAY);
        int pnlMin = countDownTime.get(Calendar.MINUTE);
        int pnlSec = countDownTime.get(Calendar.SECOND);
        
        if(TargetTime == PNLT_TARGET_HOU){
        	if((pnlHou < 23 && upDown == COUNTD_UP) || (pnlHou > 0 && upDown == COUNTD_DOWN)){
        		pnlHou += upDown;
        	}else if(pnlHou == 23 && upDown == COUNTD_UP){
        		pnlHou = 0;
        	}else if(pnlHou == 0 && upDown == COUNTD_DOWN){
        		pnlHou = 23;
        	}
        }else if(TargetTime == PNLT_TARGET_MIN){
        	if((pnlMin < 59 && upDown == COUNTD_UP) || (pnlMin > 0 && upDown == COUNTD_DOWN)){
        		pnlMin += upDown;
        	}else if(pnlMin == 59 && upDown == COUNTD_UP){
        		pnlMin = 0;
        	}else if(pnlMin == 0 && upDown == COUNTD_DOWN){
        		pnlMin = 59;
        	}
        }else{
        	if((pnlSec < 59 && upDown == COUNTD_UP) || (pnlSec > 0 && upDown == COUNTD_DOWN)){
        		pnlSec += upDown;
        	}else if(pnlSec == 59 && upDown == COUNTD_UP){
        		pnlSec = 0;
        	}else if(pnlSec == 0 && upDown == COUNTD_DOWN){
        		pnlSec = 59;
        	}
        }
        countDownTime.set(pnlYer, pnlMon, pnlDay, pnlHou, pnlMin, pnlSec);
    }
    
    // -------------------------------------------
    // カウントダウンの時間増減をタッチしている間増減させ続ける
    // -------------------------------------------
    private void countDownUpDownPush(int TargetTime, int upDown){
    	if(upDownwait < 0){
			countDownUpDown(TargetTime, upDown);
			upDownwait = 0;
    	}else{
    		upDownwait--;
    	}
    }
    
    
    // -------------------------------------------
    // 指定時間設定の時間を増減させる
    // -------------------------------------------
    private void specificUpDown(int TargetTime, int upDown){
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(specificTimes[choiceSpecific-1]);
    	int pnlYer = 1970;
        int pnlMon = 0;
        int pnlDay = 1;
        int pnlHou = cal.get(Calendar.HOUR_OF_DAY);
        int pnlMin = cal.get(Calendar.MINUTE);
        int pnlSec = cal.get(Calendar.SECOND);
        
        if(TargetTime == PNLT_TARGET_HOU){
        	if((pnlHou < 23 && upDown == COUNTD_UP) || (pnlHou > 0 && upDown == COUNTD_DOWN)){
        		pnlHou += upDown;
        	}else if(pnlHou == 23 && upDown == COUNTD_UP){
        		pnlHou = 0;
        	}else if(pnlHou == 0 && upDown == COUNTD_DOWN){
        		pnlHou = 23;
        	}
        }else if(TargetTime == PNLT_TARGET_MIN){
        	if((pnlMin < 59 && upDown == COUNTD_UP) || (pnlMin > 0 && upDown == COUNTD_DOWN)){
        		pnlMin += upDown;
        	}else if(pnlMin == 59 && upDown == COUNTD_UP){
        		pnlMin = 0;
        	}else if(pnlMin == 0 && upDown == COUNTD_DOWN){
        		pnlMin = 59;
        	}
        }
        cal.set(pnlYer, pnlMon, pnlDay, pnlHou, pnlMin, pnlSec);
        specificTimes[choiceSpecific-1] = cal.getTimeInMillis();
    }
    
    // -------------------------------------------
    // 指定時間の時間増減をタッチしている間増減させ続ける
    // -------------------------------------------
    private void specificUpDownPush(int TargetTime, int upDown){
    	if(upDownwait < 0){
    		specificUpDown(TargetTime, upDown);
			upDownwait = 0;
    	}else{
    		upDownwait--;
    	}
    }
    
    // -------------------------------------------
    // 時計色１ or 時計色２ or 背景色の設定
    // -------------------------------------------
    private void setColor(int colNum){
		if(cnfSubMode == MODE_CLOCK_COLOR_1){
			timeCol1[gradSettingColor] = colNum;
		}else if(cnfSubMode == MODE_CLOCK_COLOR_2){
			timeCol2[gradSettingColor] = colNum;
		}else if(cnfSubMode == MODE_BACKGROUND_COLOR){
			bgCol[gradSettingColor] = colNum;
		}
    }
    
    // -------------------------------------------
    // 渡されたPaintにランダムで色を設定する
    // -------------------------------------------
    private void setRandomColor(Paint paint){
    	int rndR = (int)(Math.random()*256);
    	int rndG = (int)(Math.random()*256);
    	int rndB = (int)(Math.random()*256);
    	
    	paint.setARGB(255, rndR, rndG, rndB);
    }
    
    // -------------------------------------------
    // アラームかバイブレータ処理をします
    // -------------------------------------------
    private void alarmVibrat(){
    	// 音声再生
    	if(alarmVibratFlg == ALARM_VIBRAT_ALARM || alarmVibratFlg == ALARM_VIBRAT_BOTH){
			try {
				mp.start();
			} catch (Exception e) {
				// 例外は発生しない
			}
    	}
		
		// バイブレーション
    	if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT || alarmVibratFlg == ALARM_VIBRAT_BOTH){
	        long[] pattern = {0, 2000, 1000, 2000, 1000, 2000}; // OFF/ON/OFF/ON...
	        vibrator.vibrate(pattern, -1);
    	}
    }
    // -------------------------------------------
    // 次のアラームまでの時間を取得する
    // -------------------------------------------
    private String nextAlarmValue(int nHou, int nMin, int nSec, int nWek){
    	Calendar now = Calendar.getInstance();
    	Calendar t24 = Calendar.getInstance();
    	int weekDay;
    	long nowInMil;
    	long t24InMil;
    	int nextAlarmDays = 10;
    	long nextAlarmTimes = 0;
    	String nextAlarm = "";
    	
    	now.set(1970, 0, 1, nHou, nMin, nSec);
    	t24.set(1970, 0, 2, 0, 0, 0);
    	nowInMil = now.getTimeInMillis();
    	t24InMil = t24.getTimeInMillis();
    	nWek--;
    	
    	for(int iii=0; iii < specificWeekdayOnFlg.length; iii++){
    		// iii番目の指定時間がOFFなら次の指定時間に処理を進める
    		if(specificTimesOnFlg[iii] == false){
    			continue;
    		}
    		
    		weekDay = nWek;
    		for(int lll=0; lll < specificWeekdayOnFlg[iii].length; lll++){
    			// lllが現在決定している日数より大きくなった場合は次の指定時間に進める
    			if(lll > nextAlarmDays){
    				break;
    			}
    			
    			if(specificWeekdayOnFlg[iii][weekDay]){
    				if(nowInMil > specificTimes[iii]){	// 現在時刻のほうが未来の時間
    					int tmpNextD;
    					// 次のアラームまでの日数
    					if(lll == 0){
    						tmpNextD = 6;
    					}else{
    						tmpNextD = lll - 1;
    					}
    					
    					// 現在決定している時間より小さい場合のみ処理をする
    					if(tmpNextD < nextAlarmDays || (tmpNextD == nextAlarmDays && t24InMil - nowInMil + specificTimes[iii] < nextAlarmTimes) || nextAlarmDays == 10){
	    					// 次のアラームまでの日数
	    					nextAlarmDays = tmpNextD;
	    					
	    					// 次のアラームまでの時間
	    					nextAlarmTimes = t24InMil - nowInMil + specificTimes[iii];
    					}
    				}else{
    					// 現在決定している時間より小さい場合のみ処理をする
    					if(lll < nextAlarmDays || (lll == nextAlarmDays && specificTimes[iii] - nowInMil < nextAlarmTimes) || nextAlarmDays == 10){
	    					// 次のアラームまでの日数
	    					nextAlarmDays = lll;
	    					
	    					// 次のアラームまでの時間
	    					nextAlarmTimes = specificTimes[iii] - nowInMil - now.get(Calendar.ZONE_OFFSET) + 1000;
    					}
    				}
    			}
    			
    			// 次の曜日に進める
    			if(weekDay == 6){
    				weekDay = 0;
    			}else{
    				weekDay++;
    			}
    		}
    	}
    	
    	// リターン用の文字列の作成 - 次のアラームまでの時間
    	if(nextAlarmDays == 10){
    		nextAlarm = "Not set";
    	}else{
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(nextAlarmTimes);
			DecimalFormat format = new DecimalFormat("00");
			
    		nextAlarm = nextAlarmDays + " " + format.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + format.format(cal.get(Calendar.MINUTE)) + ":" + format.format(cal.get(Calendar.SECOND));
    	}
    	
		return nextAlarm;
    }
    
    // -------------------------------------------
    // 設定ファイル保存
    // -------------------------------------------
    private void settingWriter(){
    	int onlyHouMinF_Val;		// onlyHouMinFのboolean値を整数にする為の変数
    	int timeTxtNumFlg_Val;		// timeTxtNumFlgのboolean値を整数にする為の変数
    	String setting;				// 設定ファイルに保存する文字列
    	
    	// booleanを整数にする
    	if(onlyHouMinF){
    		onlyHouMinF_Val = 1;
    	}else{
    		onlyHouMinF_Val = 0;
    	}
    	
    	if(timeTxtNumFlg){
    		timeTxtNumFlg_Val = 1;
    	}else{
    		timeTxtNumFlg_Val = 0;
    	}
    	
    	//// 設定ファイルに書き込む文字列を作成
    	// 時計(秒)切替
    	setting = String.valueOf(onlyHouMinF_Val);
    	
    	// 数字表示切替
    	setting += spliter + String.valueOf(timeTxtNumFlg_Val);
    	
    	// 音声・バイブ切替
    	setting += spliter + String.valueOf(alarmVibratFlg);
    	
    	// 時計形
    	setting += spliter + String.valueOf(timeFace);
    	
    	// 時計色1
    	setting += spliter + String.valueOf(timeCol1[GRAD_RED_NUM]) + spliter + String.valueOf(timeCol1[GRAD_GREEN_NUM]) + spliter + String.valueOf(timeCol1[GRAD_BLUE_NUM]);
    	
    	// 時計色2
    	setting += spliter + String.valueOf(timeCol2[GRAD_RED_NUM]) + spliter + String.valueOf(timeCol2[GRAD_GREEN_NUM]) + spliter + String.valueOf(timeCol2[GRAD_BLUE_NUM]);

    	// 背景色
    	setting += spliter + String.valueOf(bgCol[GRAD_RED_NUM]) + spliter + String.valueOf(bgCol[GRAD_GREEN_NUM]) + spliter + String.valueOf(bgCol[GRAD_BLUE_NUM]);
    	
    	// 設定ファイル出力
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settingFilePath, false)));
			bw.write(setting);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // 設定時間ファイル保存
    // -------------------------------------------
    private void timeWriter(){
    	String time = "";				// 設定ファイルに保存する文字列
    	
    	// カウントダウン保存時間
    	for(int iii = 0; iii < countDSetTimes.length; iii++){
    		if(iii != 0){
    			time += spliter;
    		}
    		time += countDSetTimes[iii];
    	}
    	
    	// 指定時間、指定時間のON、OFF、指定時間の曜日
    	for(int iii = 0; iii < specificTimes.length; iii++){
    		time += spliter + specificTimes[iii];
        	
    		if(specificTimesOnFlg[iii]){
        		time += spliter + String.valueOf(1);
        	}else{
        		time += spliter + String.valueOf(0);
        	}
    		
    		for(int lll = 0; lll < specificWeekdayOnFlg[iii].length; lll++){
    			if(specificWeekdayOnFlg[iii][lll]){
    				time += spliter + String.valueOf(1);
    			}else{
            		time += spliter + String.valueOf(0);
            	}
    		}
    	}
    	
    	// 設定ファイル出力
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(timeFilePath, false)));
			bw.write(time);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // 時計位置の保存
    // -------------------------------------------
    private void positionWriter(){
    	String position;				// 設定ファイルに保存する文字列
    	
    	position = String.valueOf(houL) + spliter + String.valueOf(houT) + spliter + String.valueOf(minL) + spliter + String.valueOf(minT) + spliter + String.valueOf(secL) + spliter + String.valueOf(secT);
    	
    	// 設定ファイル出力
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(positionFilePath, false)));
			bw.write(position);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // 設定ファイル読み込み
    // -------------------------------------------
    public void settingReader(){
    	File settingFile = new File(settingFilePath);	// 設定ファイルの設定
    	String str = "";								// 設定ファイルの読み込み用
    	String[] settingS;								// splitで分けられた設定の値を格納する
    	long[] settingL = new long[settingNum];		// 文字列だった設定の値をlong型で保持
    	int onlyHouMinF_Val = 0;						// onlyHouMinF用の値が何番目に入っているか
    	int timeTxtNumFlg_Val = 1;						// timeTxtNumFlg用の値が何番目に入っているか
    	int alarmVibratFlg_Val = 2;						// alarmVibratFlg用の値が何番目に入っているか
    	int timeFace_Val = 3;							// timeFace用の値が何番目に入っているか
    	int timeCol1_Val = 4;							// timeCol1用の値が何番目に入っているか
    	int timeCol2_Val = 7;							// timeCol2用の値が何番目に入っているか
    	int bgCol_Val = 10;								// bgCol用の値が何番目に入っているか
    	
        if(settingFile.exists()){
        	try {
        		// 設定ファイル読み込み
	            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(settingFilePath)));
	            str = br.readLine();
	            br.close();
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				return;
			}
        }else{
        	return;
        }
        
        // 保存されていた設定ファイルの文字列を切り分け
        settingS = str.split(spliter);
        
        // 保存されていた設定ファイルの値の数を確認
        if(settingS.length != settingNum){
        	return;
        }
        
        // 保存されていた設定ファイルの値が数値に変更できるかをチェック
        for(int iii=0; iii < settingS.length; iii++){
        	if(numberFormatCheck_long(settingS[iii]) == false){
        		return;
        	}
        	settingL[iii] = Long.parseLong(settingS[iii]);
        }
        
        //// 保存されていた値を各変数にセット
        // 時計(秒)切替
        if(settingL[onlyHouMinF_Val] == 0){
        	onlyHouMinF = false;
        	onlyHouMinBtnTxt = HMS_TEXT;
        }else{
        	onlyHouMinF = true;
        	onlyHouMinBtnTxt = HM_TEXT;
        }
        
        // 数字表示切替
        if(settingL[timeTxtNumFlg_Val] == 0){
        	timeTxtNumFlg = false;
        	timeTxtBtnTxt = KANSUUJI_TEXT;
        }else{
        	timeTxtNumFlg = true;
        	timeTxtBtnTxt = ARABIC_TEXT;
        }
        
        // 音声・バイブ切替
        alarmVibratFlg = (int)settingL[alarmVibratFlg_Val];
        if(alarmVibratFlg == ALARM_VIBRAT_ALARM){
        	alarmVibratBtnTxt = ALARM_TEXT;
        }else if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT){
        	alarmVibratBtnTxt = VIBRATE_TEXT;
        }else{
        	alarmVibratBtnTxt = BOTH_TEXT;
        }
        
        // 時計形
        timeFace = (int)settingL[timeFace_Val];
        if(timeFace == TIMEFACE_CIRCLE){
        	timeFaceBtnText = CIRCLE_TEXT;
        }else if(timeFace == TIMEFACE_TRIANGLE){
        	timeFaceBtnText = TRIANGLE_TEXT;
        }else if(timeFace == TIMEFACE_SQUARE){
        	timeFaceBtnText = SQUARE_TEXT;
        }else if(timeFace == TIMEFACE_PENTAGON){
        	timeFaceBtnText = PENTAGON_TEXT;
        }else if(timeFace == TIMEFACE_HEXAGON){
        	timeFaceBtnText = HEXAGON_TEXT;
        }else if(timeFace == TIMEFACE_OCTAGON){
        	timeFaceBtnText = OCTAGON_TEXT;
        }
        
        // 時計色1
        timeCol1[GRAD_RED_NUM] = (int)settingL[timeCol1_Val];
        timeCol1[GRAD_GREEN_NUM] = (int)settingL[timeCol1_Val+1];
        timeCol1[GRAD_BLUE_NUM] = (int)settingL[timeCol1_Val+2];
        
        // 時計色2
        timeCol2[GRAD_RED_NUM] = (int)settingL[timeCol2_Val];
        timeCol2[GRAD_GREEN_NUM] = (int)settingL[timeCol2_Val+1];
        timeCol2[GRAD_BLUE_NUM] = (int)settingL[timeCol2_Val+2];

        // 背景色
        bgCol[GRAD_RED_NUM] = (int)settingL[bgCol_Val];
        bgCol[GRAD_GREEN_NUM] = (int)settingL[bgCol_Val+1];
        bgCol[GRAD_BLUE_NUM] = (int)settingL[bgCol_Val+2];
    }
    
    // -------------------------------------------
    // 引数がlong型に変更できるかをチェック
    // -------------------------------------------
    public boolean numberFormatCheck_long(String str){
    	try {
    		Long.parseLong(str);
    		return true;
    	} catch(NumberFormatException e) {
    		return false;
    	}
    }
    
    // -------------------------------------------
    // 設定時間ファイル読み込み
    // -------------------------------------------
    public void timeReader(){
    	File settingFile = new File(timeFilePath);	// 設定ファイルの設定
    	String str = "";								// 設定ファイルの読み込み用
    	String[] timeS;									// splitで分けられた設定の値を格納する
    	long[] settingL = new long[timeNum];			// 文字列だった設定の値をlong型で保持
    	int countDSetTimes_Val = 0;						// countDSetTimes用の値が何番目から入っているか
    	int specificTimes_Val = 3;						// specificTimes用の値が何番目から入っているか
    	
        if(settingFile.exists()){
        	try {
        		// 設定ファイル読み込み
	            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(timeFilePath)));
	            str = br.readLine();
	            br.close();
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				return;
			}
        }else{
        	return;
        }
        
        // 保存されていた設定ファイルの文字列を切り分け
        timeS = str.split(spliter);
        
        // 保存されていた設定ファイルの値の数を確認
        if(timeS.length != timeNum){
        	return;
        }
        
        // 保存されていた設定ファイルの値が数値に変更できるかをチェック
        for(int iii=0; iii < timeS.length; iii++){
        	if(numberFormatCheck_long(timeS[iii]) == false){
        		return;
        	}
        	settingL[iii] = Long.parseLong(timeS[iii]);
        }
        
        // カウントダウン保存時間
        for(int iii = 0; iii < countDSetTimes.length; iii++){
        	countDSetTimes[iii] = settingL[countDSetTimes_Val + iii];
        }
        
        for(int iii = 0; iii < specificTimes.length; iii++){
        	// 指定時間
        	specificTimes[iii] = settingL[specificTimes_Val + iii*9];
        	
        	// 指定時間のON、OFF
        	if(settingL[specificTimes_Val + iii * 9 + 1] == 0){
        		specificTimesOnFlg[iii] = false;
        	}else{
        		specificTimesOnFlg[iii] = true;
        	}
        	
        	// 指定時間の曜日
        	for(int lll=0; lll < specificWeekdayOnFlg[iii].length; lll++){
        		if(settingL[specificTimes_Val + iii * 9 + lll + 2] == 0){
        			specificWeekdayOnFlg[iii][lll] = false;
        		}else{
        			specificWeekdayOnFlg[iii][lll] = true;
        		}
        	}
        }
    }
    
    // -------------------------------------------
    // 時計位置ファイル読み込み
    // -------------------------------------------
    public void positionReader(){
    	File positionFile = new File(settingFilePath);	// 時計位置ファイル設定
    	String str = "";						// 時計位置ファイルの読み込み用
    	String[] positionS;								// splitで分けられた時計位置の値を格納する
    	int[] positionI = new int[settingNum];			// 文字列だった時計位置の値をint型で保持

        // 時計位置ファイル読み込み
        if(positionFile.exists()){
        	try {
        		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(positionFilePath)));
	            str = br.readLine();
	            br.close();
			} catch (FileNotFoundException e) {
				return;
			} catch (IOException e) {
				return;
			}
        }else{
        	return;
        }
    	
        // 保存されていた時計位置ファイルの文字列を切り分け
        positionS = str.split(spliter);
        
        // 保存されていた時計位置ファイルの値の数を確認
        if(positionS.length != positionNum){
        	return;
        }

        // 保存されていた時計位置ファイルの値が数値に変更できるかをチェック
        for(int iii=0; iii < positionS.length; iii++){
        	if(numberFormatCheck_int(positionS[iii]) == false){
        		return;
        	}
        	positionI[iii] = Integer.parseInt(positionS[iii]);
        }
        
        // 時計位置の値
        houL = positionI[0];
        houT = positionI[1];
        minL = positionI[2];
        minT = positionI[3];
        secL = positionI[4];
        secT = positionI[5];
    }
    
    // -------------------------------------------
    // 引数がint型に変更できるかをチェック
    // -------------------------------------------
    public boolean numberFormatCheck_int(String str){
    	try {
    		Integer.parseInt(str);
    		return true;
    	} catch(NumberFormatException e) {
    		return false;
    	}
    }
}