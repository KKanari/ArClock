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
        
        // �p�b�P�[�W�����擾
        packageName = this.getPackageName();
        
        tView v = new tView(getApplication());
        setContentView(v);
    }
    
    // view�Ńp�b�P�[�W�����擾�ł��Ȃ��������߂������Ŏ擾���ĕ�����Ƃ��Ă����n���Ă���
	public static String packageName(){
		return packageName;
	}
}
/** ��ʍĕ`��̎d�g�݂�񋟂���N���X */
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
/** �`��p��View */
class tView extends View {
	public int winSizeW;			// �f�B�X�v���C�̉���
	public int winSizeH;			// �f�B�X�v���C�̏c��
	public boolean winVertical;	// �c��ʂ��ǂ��� - true�Ȃ�c��ʁAfalse�Ȃ牡���
    
    // ���v�A�X�g�b�v�E�H�b�`�A�J�E���g�_�E���؂�ւ��p�l���p
    public static final int TYPE_CLOCK = 1;						// ���v���[�h��\��
    public static final int TYPE_STOPWATCH = 2;					// �X�g�b�v�E�H�b�`���[�h��\��
    public static final int TYPE_COUNTDOWN = 3;					// �J�E���g�_�E�����[�h��\��
    public int typeSwitchNum = TYPE_CLOCK;						// ���݂̃��[�h
    public boolean typePnlShowFlg = false;						// �ؑփp�l���̏�� - �J or ��
    public static final int TYPE_PNL_DEFAULT_NUM = -120;		// �ؑփp�l���̏����ʒu
	public int typePnlSlideNum = TYPE_PNL_DEFAULT_NUM;			// �ؑփp�l���̈ʒu
	public static final int TYPE_SUB_VERTI_DEFAULT_X = -160;	// �c��ʎ��̐ؑփT�u�p�l���̉������̏����ʒu
	public static final int TYPE_SUB_VERTI_DEFAULT_Y = 140;	// �c��ʎ��̐ؑփT�u�p�l���̏c�����̏����ʒu
	public static final int TYPE_SUB_HORI_DEFAULT_X = 160;	// ����ʎ��̐ؑփT�u�p�l���̉������̏����ʒu
	public static final int TYPE_SUB_HORI_DEFAULT_Y = -140;	// ����ʎ��̐ؑփT�u�p�l���̏c�����̏����ʒu
	public int typeSubPnlSlideX = TYPE_SUB_VERTI_DEFAULT_X;		// �ؑփT�u�p�l���̉������̈ʒu
	public int typeSubPnlSlideY = TYPE_SUB_VERTI_DEFAULT_Y;		// �ؑփT�u�p�l���̏c�����̈ʒu
	public int subPnlTitleX = 20;									// �T�u�p�l���̃^�C�g���������̈ʒu
	public int subPnlTitleY = 20;									// �T�u�p�l���̃^�C�g���c�����̈ʒu
		// �ؑփp�l���`��p - �ؑփp�l���͈̔�
		public int typePanelL = 1;
		public int typePanelT = -10;
		public int typePanelR = 319;
		public int typePanelB = 130;
    	// �ؑփT�u�p�l���͈�
	    public int typeSubPanelL = -10;
	    public int typeSubPanelT = 290;
	    public int typeSubPanelR = 160;
	    public int typeSubPanelB = 440;
    	// �ؑփp�l���J�p�{�^��
    	public int typePnlShowBtnL = 198;
    	public int typePnlShowBtnT = 130;
    	public int typePnlShowBtnR = 248;
    	public int typePnlShowBtnB = 155;
    	// ���v�؂�ւ��p
	    public int clockBtnL = 10;
	    public int clockBtnT = 20;
	    public int clockBtnR = 40;
	    public int clockBtnB = 50;
	    // �X�g�b�v�E�H�b�`�؂�ւ��p
	    public int stopWatchBtnL = 10;
	    public int stopWatchBtnT = 55;
	    public int stopWatchBtnR = 40;
	    public int stopWatchBtnB = 85;
	    // �J�E���g�_�E���؂�ւ��p
	    public int countDownBtnL = 10;
	    public int countDownBtnT = 90;
	    public int countDownBtnR = 40;
	    public int countDownBtnB = 120;
	    // �ؑփp�l���A(��)�\���p
	    public int typePnlHourL = 50;
	    public int typePnlHourT = 45;
	    public int typePnlHourR = 100;
	    public int typePnlHourB = 95;
	    // �ؑփp�l���A(��)�\���p
	    public int typePnlMinuteL = 110;
	    public int typePnlMinuteT = 45;
	    public int typePnlMinuteR = 160;
	    public int typePnlMinuteB = 95;
	    // �ؑփp�l���A(�b)�\���p
	    public int typePnlSecondL = 170;
	    public int typePnlSecondT = 45;
	    public int typePnlSecondR = 220;
	    public int typePnlSecondB = 95;
	    // �ؑփp�l���A(�~���b)�\���p
	    public int typePnlMilSecL = 230;
	    public int typePnlMilSecT = 45;
	    public int typePnlMilSecR = 258;
	    public int typePnlMilSecB = 65;
    
	// ���v���[�h�p
	public int nextAlarmTxtX = 50;
	public int nextAlarmTxtY = 80;
	
    // �w�莞�ԗp
    public boolean showSpecificFlg = false;					// �w�莞�Ԑݒ��ʂ��ۂ�
    public Calendar specificTime = Calendar.getInstance();		// �w�莞�ԏ������p
    public long[] specificTimes = new long[9];				// �w�莞�ԕێ��p
    public boolean[] specificTimesOnFlg = new boolean[specificTimes.length];	// �w�莞�Ԃ��L������������ێ�
    public int choiceSpecific = 1;								// �I�𒆂̎w�莞��
    public boolean specificFlg = false;						// �w�莞�Ԃ��������ۂ�
    public boolean[][] specificWeekdayOnFlg = new boolean[specificTimes.length][7];	// �w�莞�Ԃ��Ƃ̗j�����L������������ێ�
	    // �w�莞�Ԑݒ�\���p�{�^��
	    public int specificTimeBtnL = 240;
	    public int specificTimeBtnT = 75;
	    public int specificTimeBtnR = 300;
	    public int specificTimeBtnB = 115;
	    // �w�莞�ԃ{�^���P
	    public int specificBtn1L = 30;
	    public int specificBtn1T = 30;
	    public int specificBtn1R = 60;
	    public int specificBtn1B = 60;
	    // �w�莞�ԃ{�^���Q
	    public int specificBtn2L = 75;
	    public int specificBtn2T = 30;
	    public int specificBtn2R = 105;
	    public int specificBtn2B = 60;
	    // �w�莞�ԃ{�^���R
	    public int specificBtn3L = 120;
	    public int specificBtn3T = 30;
	    public int specificBtn3R = 150;
	    public int specificBtn3B = 60;
	    // �w�莞�ԃ{�^���S
	    public int specificBtn4L = 30;
	    public int specificBtn4T = 65;
	    public int specificBtn4R = 60;
	    public int specificBtn4B = 95;
	    // �w�莞�ԃ{�^���T
	    public int specificBtn5L = 75;
	    public int specificBtn5T = 65;
	    public int specificBtn5R = 105;
	    public int specificBtn5B = 95;
	    // �w�莞�ԃ{�^���U
	    public int specificBtn6L = 120;
	    public int specificBtn6T = 65;
	    public int specificBtn6R = 150;
	    public int specificBtn6B = 95;
	    // �w�莞�ԃ{�^���V
	    public int specificBtn7L = 30;
	    public int specificBtn7T = 100;
	    public int specificBtn7R = 60;
	    public int specificBtn7B = 130;
	    // �w�莞�ԃ{�^���W
	    public int specificBtn8L = 75;
	    public int specificBtn8T = 100;
	    public int specificBtn8R = 105;
	    public int specificBtn8B = 130;
	    // �w�莞�ԃ{�^���X
	    public int specificBtn9L = 120;
	    public int specificBtn9T = 100;
	    public int specificBtn9R = 150;
	    public int specificBtn9B = 130;
	    // �w�莞�ԃ{�^���P�O
//	    public int specificBtn10L = 280;
//	    public int specificBtn10T = 90;
//	    public int specificBtn10R = 310;
//	    public int specificBtn10B = 120;
	    // �j���ݒ�{�^���P - ��
	    public int specificWeekdayBtn1L = 175;
	    public int specificWeekdayBtn1T = 50;
	    public int specificWeekdayBtn1R = 205;
	    public int specificWeekdayBtn1B = 80;
	    // �j���ݒ�{�^���Q - ��
	    public int specificWeekdayBtn2L = 210;
	    public int specificWeekdayBtn2T = 50;
	    public int specificWeekdayBtn2R = 240;
	    public int specificWeekdayBtn2B = 80;
	    // �j���ݒ�{�^���R - ��
	    public int specificWeekdayBtn3L = 245;
	    public int specificWeekdayBtn3T = 50;
	    public int specificWeekdayBtn3R = 275;
	    public int specificWeekdayBtn3B = 80;
	    // �j���ݒ�{�^���S - ��
	    public int specificWeekdayBtn4L = 175;
	    public int specificWeekdayBtn4T = 85;
	    public int specificWeekdayBtn4R = 205;
	    public int specificWeekdayBtn4B = 115;
	    // �j���ݒ�{�^���T - ��
	    public int specificWeekdayBtn5L = 210;
	    public int specificWeekdayBtn5T = 85;
	    public int specificWeekdayBtn5R = 240;
	    public int specificWeekdayBtn5B = 115;
	    // �j���ݒ�{�^���U - ��
	    public int specificWeekdayBtn6L = 245;
	    public int specificWeekdayBtn6T = 85;
	    public int specificWeekdayBtn6R = 275;
	    public int specificWeekdayBtn6B = 115;
	    // �j���ݒ�{�^���V - �y
	    public int specificWeekdayBtn7L = 280;
	    public int specificWeekdayBtn7T = 85;
	    public int specificWeekdayBtn7R = 310;
	    public int specificWeekdayBtn7B = 115;
	    
    // �X�g�b�v�E�H�b�`�p
    public Calendar stopWatchStartTime = Calendar.getInstance();		// �X�g�b�v�E�H�b�`���J�n��������
    public Calendar stopWatchStopTime = Calendar.getInstance();			// �X�g�b�v�E�H�b�`���~��������
    public boolean stopWatchStartFlg = false;							// �X�g�b�v�E�H�b�`���J�n���Ă��邩�ۂ�
    public ArrayList<Long> stopWatchRecordTime = new ArrayList<Long>();// �L�^���Ԃ�ێ����Ă����z��
    public int stopWatchRecTMaxNum = 60;								// �L�^���Ԃ̍ő吔
    public int stopWatchRecordPage = 1;								// ���݂̋L�^���Ԃ̃y�[�W
    public int stopWatchPageRecNum = 6;								// 1�y�[�W���̋L�^���Ԃ̐�
    public boolean stopWatchSplitFlg = true;							// split time���ǂ����̃t���O - true�Ȃ�split�Afalse�Ȃ�rap
    public int stopWatchRecordX = 40;									// �L�^���ԕ\���̉��ʒu
    public int[] stopWatchRecordY = {40, 58, 76, 94, 112, 130};		// �L�^���ԕ\���̏c�ʒu
    public int stopWatchPageX = 155;									// �y�[�W���\���ʒu��
    public int stopWatchPageY = 20;									// �y�[�W���\���ʒu�c
    	// �X�g�b�v�E�H�b�`�J�n�I���{�^��
    	public int stopWStartStopBtnL = 240;
    	public int stopWStartStopBtnT = 75;
    	public int stopWStartStopBtnR = 300;
    	public int stopWStartStopBtnB = 115;
    

    // �J�E���g�_�E���p
    public boolean pnlHouUpFlg = false;							// �����{�^��(��)��������Ă��邩�ǂ��� - �w�莞�ԂƋ��p
    public boolean pnlHouDownFlg = false;							// �����{�^��(��)��������Ă��邩�ǂ��� - �w�莞�ԂƋ��p
    public boolean pnlMinUpFlg = false;							// �����{�^��(��)��������Ă��邩�ǂ��� - �w�莞�ԂƋ��p
    public boolean pnlMinDownFlg = false;							// �����{�^��(��)��������Ă��邩�ǂ��� - �w�莞�ԂƋ��p
    public boolean pnlSecUpFlg = false;							// �����{�^��(�b)��������Ă��邩�ǂ���
    public boolean pnlSecDownFlg = false;							// �����{�^��(�b)��������Ă��邩�ǂ���
    public Calendar countDownTime = Calendar.getInstance();			// ���Ԃ̐ݒ�A�J�E���g�_�E���Ɏg�p������t
    public Calendar countDownStartTime = Calendar.getInstance();	// �J�E���g�_�E���J�n����
    public long countDDatumTime = 0;								// �J�E���g�_�E���̏����l���i�[����
    public static final int PNLT_TARGET_HOU = 1;				// ���ԑ������̑Ώہi���j - �w�莞�ԂƋ��p
    public static final int PNLT_TARGET_MIN = 2;				// ���ԑ������̑Ώہi���j - �w�莞�ԂƋ��p
    public static final int PNLT_TARGET_SEC = 3;				// ���ԑ������̑Ώہi�b�j
    public static final int COUNTD_UP = 1;						// ���ԑ������ɑ��₷ - �w�莞�ԂƋ��p
    public static final int COUNTD_DOWN = -1;					// ���ԑ������Ɍ��炷 - �w�莞�ԂƋ��p
    public boolean countDownStartFlg =false;						// �J�E���g�_�E�����J�n���Ă��邩�ۂ�
    public boolean countEndFlg = false;							// �J�E���g�I���������̃t���O
    public int upDownwait = 0;										// �����{�^���������Ɖ����Ă������ɏ����҂��Ă��珈��������
    public long[] countDSetTimes = new long[3];					// �J�E���g�_�E���ۑ����Ԃ�����z��
    public int countDSetTimeX = 20;								// �J�E���g�_�E���ۑ����Ԃ̉��ʒu
    public int[] countDSetTimeY = {50, 85, 120};					// �J�E���g�_�E���ۑ����Ԃ̏c�ʒu
    	// �p�l���i���j�����{�^��
    	public int pnlHouUpBtnL = 50;
    	public int pnlHouUpBtnT = 17;
    	public int pnlHouUpBtnR = 100;
    	public int pnlHouUpBtnB = 42;
    	// �p�l���i���j�����{�^��
    	public int pnlHouDownBtnL = 50;
    	public int pnlHouDownBtnT = 98;
    	public int pnlHouDownBtnR = 100;
    	public int pnlHouDownBtnB = 123;
    	// �p�l���i���j�����{�^��
    	public int pnlMinUpBtnL = 110;
    	public int pnlMinUpBtnT = 17;
    	public int pnlMinUpBtnR = 160;
    	public int pnlMinUpBtnB = 42;
    	// �p�l���i���j�����{�^��
    	public int pnlMinDownBtnL = 110;
    	public int pnlMinDownBtnT = 98;
    	public int pnlMinDownBtnR = 160;
    	public int pnlMinDownBtnB = 123;
    	// �p�l���i�b�j�����{�^��
    	public int pnlSecUpBtnL = 170;
    	public int pnlSecUpBtnT = 17;
    	public int pnlSecUpBtnR = 220;
    	public int pnlSecUpBtnB = 42;
    	// �p�l���i�b�j�����{�^��
    	public int pnlSecDownBtnL = 170;
    	public int pnlSecDownBtnT = 98;
    	public int pnlSecDownBtnR = 220;
    	public int pnlSecDownBtnB = 123;
    	// �J�E���g�_�E���J�n��~�{�^��
    	public int countDStartStopBtnL = 240;
    	public int countDStartStopBtnT = 75;
    	public int countDStartStopBtnR = 300;
    	public int countDStartStopBtnB = 115;
    	// �J�E���g�_�E���ۑ��{�^��1
    	public int countDSaveBtn1L = 80;
    	public int countDSaveBtn1T = 30;
    	public int countDSaveBtn1R = 115;
    	public int countDSaveBtn1B = 60;
    	// �J�E���g�_�E���ۑ��{�^��2
    	public int countDSaveBtn2L = 80;
    	public int countDSaveBtn2T = 65;
    	public int countDSaveBtn2R = 115;
    	public int countDSaveBtn2B = 95;
    	// �J�E���g�_�E���ۑ��{�^��3
    	public int countDSaveBtn3L = 80;
    	public int countDSaveBtn3T = 100;
    	public int countDSaveBtn3R = 115;
    	public int countDSaveBtn3B = 130;
    	// �J�E���g�_�E���Z�b�g�{�^��1
    	public int countDSetBtn1L = 120;
    	public int countDSetBtn1T = 30;
    	public int countDSetBtn1R = 155;
    	public int countDSetBtn1B = 60;
    	// �J�E���g�_�E���Z�b�g�{�^��2
    	public int countDSetBtn2L = 120;
    	public int countDSetBtn2T = 65;
    	public int countDSetBtn2R = 155;
    	public int countDSetBtn2B = 95;
    	// �J�E���g�_�E���Z�b�g�{�^��3
    	public int countDSetBtn3L = 120;
    	public int countDSetBtn3T = 100;
    	public int countDSetBtn3R = 155;
    	public int countDSetBtn3B = 130;
	    
	    
	    
	    
	//// �ݒ�p�l���p�ݒ�
	public boolean cnfModeFlg = false;							// �ݒ�p�l���̏�� - �J or ��
	public static final int CNF_PNL_DEFAULT_NUM = -120;		// �p�l�������ʒu
	public int cnfPnlSlideNum = CNF_PNL_DEFAULT_NUM;			// �p�l���̈ʒu
	public static final int CNF_SUB_VERTI_DEFAULT_X = -160;	// �c��ʎ��̐ݒ�T�u�p�l���̉������̏����ʒu
	public static final int CNF_SUB_VERTI_DEFAULT_Y = 140;	// �c��ʎ��̐ݒ�T�u�p�l���̏c�����̏����ʒu
	public static final int CNF_SUB_HORI_DEFAULT_X = 160;		// ����ʎ��̐ݒ�T�u�p�l���̉������̏����ʒu
	public static final int CNF_SUB_HORI_DEFAULT_Y = -140;	// ����ʎ��̐ݒ�T�u�p�l���̏c�����̏����ʒu
	public int cnfSubPnlSlideX = CNF_SUB_VERTI_DEFAULT_X;		// �ݒ�T�u�p�l���̉������̈ʒu
	public int cnfSubPnlSlideY = CNF_SUB_VERTI_DEFAULT_Y;		// �ݒ�T�u�p�l���̏c�����̈ʒu
	public static final int MODE_CLOCK_DISPLAY = 1;			// �ݒ�T�u�p�l���̃��[�h - ���v�i�b�j�ؑփ��[�h
	public static final int MODE_NUMBER_FORMAT = 2;			// �ݒ�T�u�p�l���̃��[�h - �����\���ؑփ��[�h
	public static final int MODE_ALARM_VIBRATE = 3;			// �ݒ�T�u�p�l���̃��[�h - �����E�o�C�u�ؑփ��[�h
	public static final int MODE_CLOCK_SHAPE = 4;				// �ݒ�T�u�p�l���̃��[�h - ���v�`�ύX���[�h
	public static final int MODE_CLOCK_COLOR_1 = 5;			// �ݒ�T�u�p�l���̃��[�h - �F�I�����[�h ���v�F�P
	public static final int MODE_CLOCK_COLOR_2 = 6;			// �ݒ�T�u�p�l���̃��[�h - �F�I�����[�h ���v�F�Q
	public static final int MODE_BACKGROUND_COLOR = 7;			// �ݒ�T�u�p�l���̃��[�h - �F�I�����[�h �w�i�F
	public int cnfSubMode = MODE_CLOCK_DISPLAY;					// ���݂̐ݒ�T�u�p�l���̃��[�h
		// �ݒ�p�l���`��p - �ݒ�p�l���͈̔�
		public int cnfPanelL = 1;
		public int cnfPanelT = -10;
		public int cnfPanelR = 319;
		public int cnfPanelB = 127;
	    // �ݒ�p�l���J�p�{�^��
		public int cnfPnlShowBtnL = 250;
		public int cnfPnlShowBtnT = 122;
		public int cnfPnlShowBtnR = 300;
		public int cnfPnlShowBtnB = 155;
		// �ؑփT�u�p�l���͈�
	    public int cnfSubPanelL = -10;
	    public int cnfSubPanelT = 290;
	    public int cnfSubPanelR = 160;
	    public int cnfSubPanelB = 440;
	    
	
    // �����̂ݕ\���ݒ�p
    public boolean onlyHouMinF = false;			// �����̂ݕ\���̃t���O
    public static final String HMS_TEXT = "HMS";	// �b�\�����̕�����
    public static final String HM_TEXT = "HM";	// �b��\�����̕�����
    public String onlyHouMinBtnTxt = HMS_TEXT;	// ���v�i�b�j�ؑփ{�^���ɕ\������镶����
    	// ���v�i�b�j�ؑփ{�^��
	    public int onlyHouMinBtnL = 10;
	    public int onlyHouMinBtnT = 10;
	    public int onlyHouMinBtnR = 80;
	    public int onlyHouMinBtnB = 60;
	    // �����b�{�^��
	    public int hMSBtnL = 25;
	    public int hMSBtnT = 45;
	    public int hMSBtnR = 85;
	    public int hMSBtnB = 105;
	    // �����{�^��
	    public int hMBtnL = 95;
	    public int hMBtnT = 45;
	    public int hMBtnR = 155;
	    public int hMBtnB = 105;
    
    // ���v�\�������ؑ֗p - �����A������
    public boolean timeTxtNumFlg = true;					// ���������������̃t���O
    public static final String ARABIC_TEXT = "Arabic";	// �A���r�A�������̕�����
    public static final String KANSUUJI_TEXT = "������";// ���������̕�����
    public String timeTxtBtnTxt = ARABIC_TEXT;			// �����\���ؑփ{�^���ɕ\������镶����
    	// �����\���ؑփ{�^��
	    public int timeTxtBtnL = 90;
	    public int timeTxtBtnT = 10;
	    public int timeTxtBtnR = 160;
	    public int timeTxtBtnB = 60;
	    // �A���r�A�����{�^��
	    public int arabicBtnL = 25;
	    public int arabicBtnT = 45;
	    public int arabicBtnR = 85;
	    public int arabicBtnB = 105;
	    // �������{�^��
	    public int kansuujiBtnL = 95;
	    public int kansuujiBtnT = 45;
	    public int kansuujiBtnR = 155;
	    public int kansuujiBtnB = 105;
    
    // ���A�o�C�u���[�^�p
    public MediaPlayer mp;									// �����Đ��p
    public Vibrator vibrator;								// �o�C�u���[�^�p
    public static final int ALARM_VIBRAT_ALARM = 1;	// �A���[���̂�
    public static final int ALARM_VIBRAT_VIBRAT = 2;	// �o�C�u���[�^�̂�
    public static final int ALARM_VIBRAT_BOTH = 3;		// �A���[���E�o�C�u���[�^����
    public int alarmVibratFlg = ALARM_VIBRAT_BOTH;		// �I�����Ă��鉹�A�o�C�u���[�^�̐ݒ�
    public static final String BOTH_TEXT = "Both";		// �������̕�����
    public static final String ALARM_TEXT = "Alarm";		// �A���[�����̕�����
    public static final String VIBRATE_TEXT = "Vibrate";// �o�C�u���[�^���̕�����
    public String alarmVibratBtnTxt = BOTH_TEXT;			// �����E�o�C�u�ؑփ{�^���ɕ\������镶����
    	// �����E�o�C�u�ؑփ{�^��
	    public int alarmVibratBtnL = 10;
	    public int alarmVibratBtnT = 70;
	    public int alarmVibratBtnR = 80;
	    public int alarmVibratBtnB = 120;
	    // �����{�^��
	    public int bothBtnL = 15;
	    public int bothBtnT = 45;
	    public int bothBtnR = 55;
	    public int bothBtnB = 105;
	    // �A���[���{�^��
	    public int alarmBtnL = 60;
	    public int alarmBtnT = 45;
	    public int alarmBtnR = 110;
	    public int alarmBtnB = 105;
	    // �o�C�u���[�^�{�^��
	    public int vibrateBtnL = 115;
	    public int vibrateBtnT = 45;
	    public int vibrateBtnR = 165;
	    public int vibrateBtnB = 105;
    
    
    // ���v�`�ύX
	public static final int TIMEFACE_CIRCLE = 1;	// �~
	public static final int TIMEFACE_TRIANGLE = 2;	// �O�p
	public static final int TIMEFACE_SQUARE = 3;	// �l�p
	public static final int TIMEFACE_PENTAGON = 4;	// �܊p
	public static final int TIMEFACE_HEXAGON = 5;	// �Z�p
	public static final int TIMEFACE_OCTAGON = 6;	// ���p
	public int timeFace = TIMEFACE_CIRCLE;			// �I�𒆂̎��v�`
	public static final String CIRCLE_TEXT = "O";	// �~�̕�����
	public static final String TRIANGLE_TEXT = "3";	// �O�p�̕�����
	public static final String SQUARE_TEXT = "4";	// �l�p�̕�����
	public static final String PENTAGON_TEXT = "5";	// �܊p�̕�����
	public static final String HEXAGON_TEXT = "6";	// �Z�p�̕�����
	public static final String OCTAGON_TEXT = "8";	// ���p�̕�����
	public String timeFaceBtnText = CIRCLE_TEXT;		// ���v�`�ύX�{�^���ɕ\������镶����
	
		// ���v�`�ύX�p�{�^���ݒ�
		public int timeFaceBtnL = 90;
		public int timeFaceBtnT = 70;
		public int timeFaceBtnR = 160;
		public int timeFaceBtnB = 120;
		// �~�p�{�^��
		public int timeFaceBtnCirL = 20;
		public int timeFaceBtnCirT = 35;
		public int timeFaceBtnCirR = 60;
		public int timeFaceBtnCirB = 75;
		// �O�p�p�{�^��
		public int timeFaceBtnTriL = 70;
		public int timeFaceBtnTriT = 35;
		public int timeFaceBtnTriR = 110;
		public int timeFaceBtnTriB = 75;
		// �l��p�{�^��
		public int timeFaceBtnSquL = 120;
		public int timeFaceBtnSquT = 35;
		public int timeFaceBtnSquR = 160;
		public int timeFaceBtnSquB = 75;
		// �܉�p�{�^��
		public int timeFaceBtnPenL = 20;
		public int timeFaceBtnPenT = 85;
		public int timeFaceBtnPenR = 60;
		public int timeFaceBtnPenB = 125;
		// �Z�p�p�{�^��
		public int timeFaceBtnHexL = 70;
		public int timeFaceBtnHexT = 85;
		public int timeFaceBtnHexR = 110;
		public int timeFaceBtnHexB = 125;
		// ����p�{�^��
		public int timeFaceBtnOctL = 120;
		public int timeFaceBtnOctT = 85;
		public int timeFaceBtnOctR = 160;
		public int timeFaceBtnOctB = 125;
    
	
	// ���v�F�P�ύX�{�^���p
    	// ���v�F�P�ύX�{�^���z�u
	    public int timeCol1BtnL = 175;
	    public int timeCol1BtnT = 15;
	    public int timeCol1BtnR = 215;
	    public int timeCol1BtnB = 115;
    	
    // ���v�F�Q�ύX�{�^���p
    	// �{�^���P�z�u
	    public int timeCol2BtnL = 220;
	    public int timeCol2BtnT = 15;
	    public int timeCol2BtnR = 260;
	    public int timeCol2BtnB = 115;

    // �w�i�F�ύX�{�^���p
    	// �{�^���P�z�u
	    public int backColBtnL = 265;
	    public int backColBtnT = 15;
	    public int backColBtnR = 305;
	    public int backColBtnB = 115;
	
	// ���v�F�O���f�[�V����
	public static final int GRAD_RED_NUM = 0;	// �ԐF������킷�l
	public static final int GRAD_GREEN_NUM = 1;	// �ΐF������킷�l
	public static final int GRAD_BLUE_NUM = 2;	// �F������킷�l
	public static final int GRAD_NO_COLOR = 3;	// �F���I������Ă��Ȃ�
	public int[] timeCol1 = {120, 120, 120};		// ���v�F�P�̒l
	public int[] timeCol2 = {108, 108, 108};		// ���v�F�Q�̒l
	public int[] bgCol = {120, 120, 120};			// �w�i�F�̒l
	public int gradSettingColor = GRAD_NO_COLOR;	// �ݒ蒆�̐F 0=�ԁA1=�΁A2=�A3=�Ȃ�
	public int gradMinNum = 0;						// �O���f�[�V�����̒l�̍ŏ��l
	public int gradMaxNum = 120;					// �O���f�[�V�����̒l�̍ő�l
	public int gradBarAdjust = 35;					// ���̃O���f�[�V�����̈ʒu - ��{�ʒu���炸�炷�ʒu
		// �O���f�[�V�����ݒ�p�i�ԁj�ꏊ
		public int gradRedL = 30;
		public int gradRedT = 25;
		public int gradRedR = 150;
		public int gradRedB = 55;
		// �O���f�[�V�����ݒ�p�i�΁j�ꏊ
		public int gradGreenL = 30;
		public int gradGreenT = 60;
		public int gradGreenR = 150;
		public int gradGreenB = 90;
		// �O���f�[�V�����ݒ�p�i�j�ꏊ
		public int gradBlueL = 30;
		public int gradBlueT = 95;
		public int gradBlueR = 150;
		public int gradBlueB = 125;
		// �O���f�[�V�����o�[��{�ʒu
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
	
	
    
	
    //// ���v�\���p�ϐ�
    public static final int HOUNUM = 1;				// ���A���A�b��ʗp�i���j
    public static final int MINNUM = 2;				// ���A���A�b��ʗp�i���j
    public static final int SECNUM = 3;				// ���A���A�b��ʗp�i�b�j
    public int houArc = 0;								// ���v�i���j�̒l
    public static final int HOU_L_INITIAL = 5;		// ���v�i���j�̏����ʒu�i���j
    public static final int HOU_T_INITIAL = 160;	// ���v�i���j�̏����ʒu�i��j
    public int houL = HOU_L_INITIAL;					// ���v�i���j�̈ʒu�i���j
    public int houT = HOU_T_INITIAL;					// ���v�i���j�̈ʒu�i��j
    public int houR = 50;								// ���v�i���j�͈̔͂̔����̒l - ���a
    public int minArc = 0;								// ���v�i���j�̒l
    public static final int MIN_L_INITIAL = 110;	// ���v�i���j�̏����ʒu�i���j
    public static final int MIN_T_INITIAL = 160;	// ���v�i���j�̏����ʒu�i��j
    public int minL = MIN_L_INITIAL;					// ���v�i���j�̈ʒu�i���j
    public int minT = MIN_T_INITIAL;					// ���v�i���j�̈ʒu�i��j
    public int minR = 50;								// ���v�i���j�͈̔͂̔����̒l - ���a
    public int secArc = 0;								// ���v�i�b�j�̒l
    public static final int SEC_L_INITIAL = 215;	// ���v�i�b�j�̏����ʒu�i���j
    public static final int SEC_T_INITIAL = 160;	// ���v�i�b�j�̏����ʒu�i��j
    public int secL = SEC_L_INITIAL;					// ���v�i�b�j�̈ʒu�i���j
    public int secT = SEC_T_INITIAL;					// ���v�i�b�j�̈ʒu�i��j
    public int secR = 50;								// ���v�i�b�j�͈̔͂̔����̒l - ���a
    public int secPArc = 0;							// �~���b��6���������l - secArc�ɑ������ƂŃX���[�Y�ɓ�����
    
    // �^�b�`���p�ϐ�
    public int houTouch = 0;			// ���v�i���j�̕����t�F�[�h�p - �o�߃t���[����
    public int houTAlfa = 0;			// ���v�i���j�̕����t�F�[�h�p - �A���t�@�l
    public boolean houMove = false;	// ���v�i���j���ړ������ۂ�
    public int minTouch = 0;			// ���v�i���j�̕����t�F�[�h�p - �o�߃t���[����
    public int minTAlfa = 0;			// ���v�i���j�̕����t�F�[�h�p - �A���t�@�l
    public boolean minMove = false;	// ���v�i���j���ړ������ۂ�
    public int secTouch = 0;			// ���v�i�b�j�̕����t�F�[�h�p - �o�߃t���[����
    public int secTAlfa = 0;			// ���v�i�b�j�̕����t�F�[�h�p - �A���t�@�l
    public boolean secMove = false;	// ���v�i�b�j���ړ������ۂ�
    public final static int TOUCHMAXNUM = 50;	//���v�̕����t�F�[�h�p - �����l
    
    // �����o���p
    public boolean justTimeFlg = false;				// 1�����傤�ǁA2�����傤�ǁc�̏ꍇ�̃t���O
    public static final int BALCNT_INIT = 1;		// �����o���p�ϐ��̏����l
    public int balCnt = BALCNT_INIT;					// �����o���p�ϐ� - �J�E���g�p�ϐ�
    public static final int BALL_VERTI_INIT = 108;	// �����o���\�������ʒu�i��)(�c���j
    public static final int BALT_VERTI_INIT = 310;	// �����o���\�������ʒu�i��)(�c���j
    public static final int BALR_VERTI_INIT = 112;	// �����o���\�������ʒu�i�E)(�c���j
    public static final int BALB_VERTI_INIT = 310;	// �����o���\�������ʒu�i��)(�c���j
    public static final int BALL_HORI_INIT = 398;	// �����o���\�������ʒu�i��)(�����j
    public static final int BALT_HORI_INIT = 120;	// �����o���\�������ʒu�i��)(�����j
    public static final int BALR_HORI_INIT = 402;	// �����o���\�������ʒu�i�E)(�����j
    public static final int BALB_HORI_INIT = 120;	// �����o���\�������ʒu�i��)(�����j
    public int balL = BALL_VERTI_INIT;				// �����o���\���ʒu�i��)
    public int balT = BALT_VERTI_INIT;				// �����o���\���ʒu�i��)
    public int balR = BALR_VERTI_INIT;				// �����o���\���ʒu�i�E)
    public int balB = BALB_VERTI_INIT;				// �����o���\���ʒu�i��)
    
    
    // �}�X�R�b�g�\���p
    public static final int DROIDADJUST_L_VERTI_INIT = 184;	// �}�X�R�b�g�\�������ʒu(��)(�c��)
    public static final int DROIDADJUST_T_VERTI_INIT = 290;	// �}�X�R�b�g�\�������ʒu(��)(�c��)
    public static final int DROIDADJUST_L_HORI_INIT = 348;	// �}�X�R�b�g�\�������ʒu(��)(����)
    public static final int DROIDADJUST_T_HORI_INIT = 140;	// �}�X�R�b�g�\�������ʒu(��)(����)
    public int droidAdjustL = DROIDADJUST_L_VERTI_INIT;			// �}�X�R�b�g�\���ʒu(��)
    public int droidAdjustT = DROIDADJUST_T_VERTI_INIT;			// �}�X�R�b�g�\���ʒu(��)
    public Paint droidColor = new Paint();							// �}�X�R�b�g�F�ݒ�ێ��p
    public int droidArmBWidthPlusL = 5;							// �}�X�R�b�g�r�{�^���̃^�b�`�̈摝���p
		// �A���h���C�h�̉E��
		public int droidRightEyeL = 37;
		public int droidRightEyeT = 26;
		public int droidRightEyeR = 3;
    	// �A���h���C�h�̍���
    	public int droidLeftEyeL = 67;
    	public int droidLeftEyeT = 26;
		public int droidLeftEyeR = 3;
		// �A���h���C�h�̉E�r - �X�g�b�v�E�H�b�`��
	    public int droidRArmP1L = 0;
	    public int droidRArmP1T = 65;
	    public int droidRArmP2L = 15;
	    public int droidRArmP2T = 42;
	    public int droidRArmP3L = 15;
	    public int droidRArmP3T = 88;
		// �A���h���C�h�̉E�r
	    public int droidRightArmL = 0;
	    public int droidRightArmT = 42;
	    public int droidRightArmR = 15;
	    public int droidRightArmB = 88;
		// �A���h���C�h�̍��r - �X�g�b�v�E�H�b�`��
	    public int droidLArmP1L = 89;
	    public int droidLArmP1T = 42;
	    public int droidLArmP2L = 104;
	    public int droidLArmP2T = 65;
	    public int droidLArmP3L = 89;
	    public int droidLArmP3T = 88;
		// �A���h���C�h�̍��r
	    public int droidLeftArmL = 89;
	    public int droidLeftArmT = 42;
	    public int droidLeftArmR = 104;
	    public int droidLeftArmB = 88;
		// �A���h���C�h�̏��
	    public int droidTopBodyL = 18;
	    public int droidTopBodyT = 44;
	    public int droidTopBodyR = 86;
	    public int droidTopBodyB = 71;
		// �A���h���C�h�̉���
	    public int droidBottomBodyL = 18;
	    public int droidBottomBodyT = 61;
	    public int droidBottomBodyR = 86;
	    public int droidBottomBodyB = 101;
		// �A���h���C�h�̉E��
	    public int droidRightFootL = 32;
	    public int droidRightFootT = 89;
	    public int droidRightFootR = 47;
	    public int droidRightFootB = 124;
		// �A���h���C�h�̍���
	    public int droidLeftFootL = 57;
	    public int droidLeftFootT = 89;
	    public int droidLeftFootR = 72;
	    public int droidLeftFootB = 124;
		// �A���h���C�h�̓� - �X�g�b�v�E�H�b�`��
	    public int droidHeadSL = 18;
	    public int droidHeadST = 10;
	    public int droidHeadSR = 86;
	    public int droidHeadSB = 41;
		// �A���h���C�h�̓�
	    public int droidHeadL = 18;
	    public int droidHeadT = 10;
	    public int droidHeadR = 86;
	    public int droidHeadB = 73;
		// �A���h���C�h�̉E�A���e�i
	    public int droidRAntennaP1L = 38;
	    public int droidRAntennaP1T = 16;
	    public int droidRAntennaP2L = 28;
	    public int droidRAntennaP2T = 1;
	    public int droidRAntennaP3L = 29;
	    public int droidRAntennaP3T = 0;
	    public int droidRAntennaP4L = 39;
	    public int droidRAntennaP4T = 15;
		// �A���h���C�h�̍��A���e�i
	    public int droidLAntennaP1L = 65;
	    public int droidLAntennaP1T = 15;
	    public int droidLAntennaP2L = 75;
	    public int droidLAntennaP2T = 0;
	    public int droidLAntennaP3L = 76;
	    public int droidLAntennaP3T = 1;
	    public int droidLAntennaP4L = 66;
	    public int droidLAntennaP4T = 16;
    
    // ���A���A�b��������p
    public Boolean autoLineBtnFlg = false;							// �^�b�`���ꂽ�ꏊ���u���A���A�b��������{�^���v�͈̔͂��ۂ�
    public boolean autoLineFlg = false;							// �J�n�E��~����
    public boolean autoMoveFlg = false;							// ���������v�����������𔻕�
	    

	
	// �t�@�C�����o�͗p
    public String settingFilePath = "/data/data/" + ArClock.packageName() + "/setting.txt"; // �ݒ�t�@�C���̃p�X
    public String timeFilePath = "/data/data/" + ArClock.packageName() + "/time.txt"; // �ݒ莞�ԃt�@�C���̃p�X
    public String positionFilePath = "/data/data/" + ArClock.packageName() + "/position.txt"; // ���v�ʒu�t�@�C���̃p�X
    public int settingNum = 13; // �ݒ�t�@�C���ɏ����o�����ݒ�̐�
    public int timeNum = 84; // ���v�ʒu�t�@�C���ɏ����o�����ݒ�̐�
    public int positionNum = 6; // ���v�ʒu�t�@�C���ɏ����o�����ݒ�̐�
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
        	// �w�莞�Ԃ̎��ԏ�����
        	specificTimes[iii] = time;
        	
        	// �w�莞�ԗp�t���O�̏�����
        	specificTimesOnFlg[iii] = false;
        	
        	// �w�莞�ԗj���p�t���O�̏�����
        	for(int lll=0; lll < specificWeekdayOnFlg[iii].length; lll++){
        		specificWeekdayOnFlg[iii][lll] = false;
        	}
        }
        
        // �X�g�b�v�E�H�b�`�̏I�����Ԃ�������
        stopWatchStopTime.setTimeInMillis(stopWatchStartTime.getTimeInMillis());
        
        // �J�E���g�_�E���̎��Ԃ̏�����
        countDownTime.setTimeInMillis(0);
        countDownTime.set(1970,0,1,0,0,0);
        countDDatumTime = countDownTime.getTimeInMillis();
        
        // �J�E���g�_�E���ۑ����Ԃ̏�����
        for(int iii=0; iii < countDSetTimes.length; iii++){
        	countDSetTimes[iii] = countDownTime.getTimeInMillis();
        }
        
        // �}�X�R�b�gPaint�ݒ�p
        setRandomColor(droidColor);
        droidColor.setAntiAlias(true);
        
        // �A���[�����Z�b�g
        mp = MediaPlayer.create(c, R.raw.alarm);
        try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        // �o�C�u���[�V����
        vibrator = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
        
        // �ݒ�t�@�C���ǂݍ���
        settingReader();
        // �ݒ莞�ԃt�@�C���ǂݍ���
        timeReader();
        // ���v�ʒu�t�@�C���ǂݍ���
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
    			if(typePnlShowFlg == false){ // ����if�����Ȃ��ƃz�[�������Ė߂����Ƃ��ɃT�u�p�l�����Ȃ��Ȃ��Ă��܂�
    				// �ؑփT�u�p�l���͈̔͂��w��
		    	    typeSubPanelL = -10;
		    	    typeSubPanelT = 290;
		    	    typeSubPanelR = 160;
		    	    typeSubPanelB = 440;
		    	    
		    	    // �ؑփT�u�p�l���̈ʒu���w��
		    	    typeSubPnlSlideX = TYPE_SUB_VERTI_DEFAULT_X;
		    	    typeSubPnlSlideY = TYPE_SUB_VERTI_DEFAULT_Y;
    			}
    			
    			if(cnfModeFlg == false){
    				// �ݒ�T�u�p�l���͈̔͂��w��
    			    cnfSubPanelL = -10;
    			    cnfSubPanelT = 290;
    			    cnfSubPanelR = 160;
    			    cnfSubPanelB = 440;
		    	    
		    	    // �ݒ�T�u�p�l���̈ʒu���w��
		    	    cnfSubPnlSlideX = CNF_SUB_VERTI_DEFAULT_X;
		    	    cnfSubPnlSlideY = CNF_SUB_VERTI_DEFAULT_Y;
    			}
	    	    
	    	    // �}�X�R�b�g�L�����N�^�[�̕\���ʒu���w��
	    	    droidAdjustL = DROIDADJUST_L_VERTI_INIT;
				droidAdjustT = DROIDADJUST_T_VERTI_INIT;
    		}else{
    			if(typePnlShowFlg == false){
	    			// �ؑփT�u�p�l���͈̔͂��w��
			    	typeSubPanelL = 320;
			        typeSubPanelT = 0;
			        typeSubPanelR = 480;
			        typeSubPanelB = 140;
			        
		    	    // �ؑփT�u�p�l���̈ʒu���w��
		    	    typeSubPnlSlideX = TYPE_SUB_HORI_DEFAULT_X;
		    	    typeSubPnlSlideY = TYPE_SUB_HORI_DEFAULT_Y;
    			}
    			
    			if(cnfModeFlg == false){
    				// �ݒ�T�u�p�l���͈̔͂��w��
    			    cnfSubPanelL = 320;
    			    cnfSubPanelT = 0;
    			    cnfSubPanelR = 480;
    			    cnfSubPanelB = 140;
		    	    
		    	    // �ݒ�T�u�p�l���̈ʒu���w��
		    	    cnfSubPnlSlideX = CNF_SUB_HORI_DEFAULT_X;
		    	    cnfSubPnlSlideY = CNF_SUB_HORI_DEFAULT_Y;
    			}
    			
		        // �}�X�R�b�g�L�����N�^�[�̕\���ʒu���w��
		        droidAdjustL = DROIDADJUST_L_HORI_INIT;
				droidAdjustT = DROIDADJUST_T_HORI_INIT;
    		}
    	}
    }
    
    protected void onDraw(Canvas canvas) {
    	// �w�i�F
        canvas.drawColor(Color.WHITE);
        canvas.drawARGB(255, (int)(((double)bgCol[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
        
        // �����p
        Paint counterColor = new Paint();
        counterColor.setColor(Color.BLACK);
        counterColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
		counterColor.setAntiAlias(true);
		
        // �����p�i���j
        Paint houTCol = new Paint();
        houTCol.setColor(Color.GRAY);
        houTCol.setTextSize(40);
		houTCol.setAntiAlias(true);
		houTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		houTCol.setTextAlign(Paint.Align.CENTER);
		
        // �����p�i���j
        Paint minTCol = new Paint();
        minTCol.setColor(Color.GRAY);
        minTCol.setTextSize(40);
		minTCol.setAntiAlias(true);
		minTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		minTCol.setTextAlign(Paint.Align.CENTER);
		
        // �����p�i�b�j
        Paint secTCol = new Paint();
        secTCol.setColor(Color.GRAY);
        secTCol.setTextSize(40);
		secTCol.setAntiAlias(true);
		secTCol.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
		secTCol.setTextAlign(Paint.Align.CENTER);
		
        // ���v�p�i�F1�j
        Paint cColor1 = new Paint();
        cColor1.setColor(Color.WHITE);
		cColor1.setAntiAlias(true);
        
        // ���v�p�i�F2�j
        Paint cColor2 = new Paint();
		cColor2.setAntiAlias(true);
        
        // ���v�p�i�g�j
        Paint fColor = new Paint();
        fColor.setColor(Color.GRAY);
		fColor.setAntiAlias(true);
		fColor.setStyle(Paint.Style.STROKE);
		fColor.setStrokeWidth(1);
		
        
        // �����o���p
        Paint balColor = new Paint();
        balColor.setARGB(255, 189, 225, 231);
        balColor.setAntiAlias(true);
        balColor.setStyle(Paint.Style.STROKE);
        balColor.setStrokeWidth(5);
        
        // �����o���w�i�p
        Paint balBackColor = new Paint();
        balBackColor.setARGB(255, 255, 255, 255);
        balBackColor.setAntiAlias(true);
        balBackColor.setStrokeWidth(5);
        
        // �����o�������p
        Paint balTColor = new Paint();
        balTColor.setColor(Color.BLACK);
        balTColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
		balTColor.setAntiAlias(true);
		balTColor.setTextAlign(Paint.Align.CENTER);
		
		// �ؑփp�l���p
        Paint typePnlColor = new Paint();
        typePnlColor.setARGB(200, 176, 196, 222);
        typePnlColor.setAntiAlias(true);
        
        // �ؑփp�l���������w�i�F�p
        Paint typeBtnColor = new Paint();
        typeBtnColor.setARGB(255, 255, 255, 122);
        typeBtnColor.setAntiAlias(true);
        
        // �ؑփp�l���i���v�j�������w�i�F�p
        Paint clockBtnColor = new Paint();
        clockBtnColor.setARGB(255, 230, 255, 122);
        clockBtnColor.setAntiAlias(true);
        
        // �ؑփp�l���i���v - �w�莞�Ԑݒ�j�w�莞��OFF���p
        Paint specificTimeOffBtnColor = new Paint();
        specificTimeOffBtnColor.setARGB(255, 180, 222, 0);
        specificTimeOffBtnColor.setAntiAlias(true);
        
        // �ؑփp�l���i�X�g�b�v�E�H�b�`�j�������w�i�F�p
        Paint stopWBtnColor = new Paint();
        stopWBtnColor.setARGB(255, 222, 255, 211);
        stopWBtnColor.setAntiAlias(true);
        
        // �ؑփp�l���i�J�E���g�_�E���j�������w�i�F�p
        Paint countDBtnColor = new Paint();
        countDBtnColor.setARGB(255, 255, 215, 111);//255, 235, 241
        countDBtnColor.setAntiAlias(true);
        
        
        // �ؑփp�l�����ԕ����p
        Paint pnlTimeColor = new Paint();
        pnlTimeColor.setColor(Color.GRAY);
        pnlTimeColor.setTextSize(40);
		pnlTimeColor.setAntiAlias(true);
		pnlTimeColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
        pnlTimeColor.setTextAlign(Paint.Align.RIGHT);
		
		// �p�l�����ԑ����{�^���p
        Paint pnlTimeUpDownColor = new Paint();
        pnlTimeUpDownColor.setColor(Color.GRAY);
        pnlTimeUpDownColor.setTextSize(20);
		pnlTimeUpDownColor.setAntiAlias(true);
        pnlTimeUpDownColor.setTextAlign(Paint.Align.CENTER);
        pnlTimeUpDownColor.setTextScaleX(2);
		
        
        // �T�u�p�l�������p
        Paint subPnlTxtColor = new Paint();
        subPnlTxtColor.setColor(Color.BLACK);
        subPnlTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
        subPnlTxtColor.setAntiAlias(true);
        
        
        // �L�^���ԕ����p
        Paint recordTxtColor = new Paint();
        recordTxtColor.setColor(Color.BLACK);
        recordTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.ITALIC));
        recordTxtColor.setAntiAlias(true);
        recordTxtColor.setTextAlign(Paint.Align.RIGHT);
        
		
		// �ݒ�p�l���p
        Paint cnfPnlColor = new Paint();
        cnfPnlColor.setARGB(255, 238, 232, 170);
        cnfPnlColor.setAntiAlias(true);
        
        // �ݒ�p�l���\�����̈Ó]�����p
        Paint cnfCoverColor = new Paint();
        cnfCoverColor.setARGB(150, 0, 0, 0);
        cnfCoverColor.setAntiAlias(true);
        
        // �ݒ�{�^���p
        Paint cnfBtnColor = new Paint();
        cnfBtnColor.setARGB(255, 255, 255, 122);
        cnfBtnColor.setAntiAlias(true);
        
        // �ݒ�{�^���p - on��
        Paint cnfOnBtnColor = new Paint();
        cnfOnBtnColor.setARGB(255, 255, 255, 122);
        cnfOnBtnColor.setAntiAlias(true);
        
        // �ݒ�{�^���p - off��
        Paint cnfOffBtnColor = new Paint();
        cnfOffBtnColor.setARGB(255, 200, 200, 90);
        cnfOffBtnColor.setAntiAlias(true);
        
        // �ؑցA�ݒ�p�l�������p
        Paint pnlTxtColor = new Paint();
        pnlTxtColor.setColor(Color.GRAY);
        pnlTxtColor.setAntiAlias(true);
        pnlTxtColor.setTypeface(Typeface.create(Typeface.SERIF,Typeface.BOLD));
        pnlTxtColor.setTextAlign(Paint.Align.CENTER);
        
		
		// ���v�F�P�̐ݒ�ɂ��������F��ύX
        cColor1.setARGB(255, (int)(((double)timeCol1[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
		
		// ���v�F�Q�̐ݒ�ɂ��������F��ύX
        cColor2.setARGB(255, (int)(((double)timeCol2[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
		
        // �A���h���C�h�̖ڗp
        Paint androidEye = new Paint();
        androidEye.setColor(Color.WHITE);
		
		
		
        // ���v�A�p�l�����v�p�̃J�����_�[
        Calendar calendar = Calendar.getInstance();
        
        // ���݂̎��Ԏ擾�p
        Calendar nowCalendar = Calendar.getInstance();
        
        
        //// ���Ԃ̎擾
        // �X�g�b�v�E�H�b�`���̕\�����Ԑݒ�
        String stopWBtnTxt = "Stop";
        if(typeSwitchNum == TYPE_STOPWATCH){
        	if(stopWatchStartFlg){
        		calendar.setTimeInMillis(calendar.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - calendar.get(Calendar.ZONE_OFFSET));
        	}else{
        		calendar.setTimeInMillis(stopWatchStopTime.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - calendar.get(Calendar.ZONE_OFFSET));
        		stopWBtnTxt = "Start";
        	}
        }
        
        // �J�E���g�_�E�����̕\�����Ԑݒ�v
        String countDStartStopBtnTxt = "";
    	if(countDownStartFlg){
    		// �J�E���g�_�E���̎c�莞�Ԃ��擾
        	long time = countDownTime.getTimeInMillis() - (nowCalendar.getTimeInMillis() - countDownStartTime.getTimeInMillis());
        	
        	if(time <= countDDatumTime){	// �J�E���g�I�������ꍇ
        		// �e�ϐ���������
        		time = countDDatumTime;
        		countDownStartFlg = false;
        		countDownTime.setTimeInMillis(time);
        		
        		// �J�E���g�_�E�����[�h���̂ݕ\���p�̃J�����_�[�̒l�������\���ɂ���
        		if(typeSwitchNum == TYPE_COUNTDOWN){
        			calendar.setTimeInMillis(time);
        		}
        		countDStartStopBtnTxt = "Start";
        		
        		// �A���h���C�h����A�����o������
        		countEndFlg = true;
        		balloonInit();
    			
        		// �A���[���A�o�C�u���[�^����
        		alarmVibrat();
        	}else{
        		if(typeSwitchNum == TYPE_COUNTDOWN){
            		// �J�E���g�_�E���̎c�莞�Ԃ��Z�b�g
            		calendar.setTimeInMillis(time);
            		countDStartStopBtnTxt = "Stop";
        		}
        	}
    	}else{
    		if(typeSwitchNum == TYPE_COUNTDOWN){
        		// �ݒ蒆�̃J�E���g�_�E���̒l���Z�b�g
        		calendar.setTimeInMillis(countDownTime.getTimeInMillis());
        		countDStartStopBtnTxt = "Start";
    		}
    	}
    	
    	// ���v�A�p�l�����v�p�̎��Ԃ̎擾
        int ampm = calendar.get(Calendar.AM_PM);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int milSec = calendar.get(Calendar.MILLISECOND);
		
		// ���݂̎��Ԃ����ꂼ��擾
        int nowHourOfDay = nowCalendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = nowCalendar.get(Calendar.MINUTE);
		int nowSecond = nowCalendar.get(Calendar.SECOND);
		int nowWeekDay = nowCalendar.get(Calendar.DAY_OF_WEEK);
		
		// ���v�^�b�`���̐����Ɋ������g���ꍇ�̐ݒ�
		String[] kansuuji = {"", "��", "��", "�Q", "�l", "��", "�Z", "��", "��", "��", "�E", "�Z"};
		
		// �����o���ɕ\�����镶����ݒ�
		String[] hourName = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
		
		
		// �ǂ̐ݒ�
		houArc = hour * 30;
		minArc = minute * 6;
		secArc = second * 6;
		secPArc = (int)Math.floor((milSec * 6)/1000);
		//msecArc = (int)Math.round(3.6*(milSec/10));
        
		
		////  ���v������`��
		// ���̕`��
		drawTime(canvas, cColor1, cColor2, fColor, HOUNUM, ampm%2);
		
		// ���̕`��
		drawTime(canvas, cColor1, cColor2, fColor, MINNUM, hour%2);
		
		// �b�̕`��
		if(onlyHouMinF == false || typeSwitchNum != TYPE_CLOCK){
			drawTime(canvas, cColor1, cColor2, fColor, SECNUM, minute%2);
		}
		
		
		
		//// �^�b�`���A���ԕ\��
		// �ݒ�ɉ����������\���ʒu�̒���
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
		
		// ���\��
		if(houTouch > 0){
			// �t�F�[�h����
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
				canvas.drawText("��", houL + houR, houT + unitPositionAdjust, houTCol);
			}else if(hour < 10){
		        houTCol.setTextSize(25);
				canvas.drawText(kansuuji[hour], houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("��", houL + houR, houT + unitPositionAdjust, houTCol);
			}else{
		        houTCol.setTextSize(25);
				canvas.drawText(kansuuji[hour/10] + kansuuji[10] + kansuuji[hour%10], houL + houR, houT + positionAdjust, houTCol);
				houTCol.setTextSize(11);
				canvas.drawText("��", houL + houR, houT + unitPositionAdjust, houTCol);
			}
			
			houTouch--;
		}
		// ���\��
		if(minTouch > 0){
			// �t�F�[�h����
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
				canvas.drawText("��", minL + minR, minT + unitPositionAdjust, minTCol);
			}else if(minute < 10){
		        minTCol.setTextSize(25);
				canvas.drawText(kansuuji[minute], minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("��", minL + minR, minT + unitPositionAdjust, minTCol);
			}else{
		        minTCol.setTextSize(25);
				canvas.drawText(kansuuji[minute/10] + kansuuji[10] + kansuuji[minute%10], minL + minR, minT + positionAdjust, minTCol);
				minTCol.setTextSize(11);
				canvas.drawText("��", minL + minR, minT + unitPositionAdjust, minTCol);
			}
			
			minTouch--;
		}
		// �b�\��
		if(secTouch > 0){
			// �t�F�[�h����
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
				canvas.drawText("�b", secL + secR, secT + unitPositionAdjust, secTCol);
			}else if(second < 10){
		        secTCol.setTextSize(25);
				canvas.drawText(kansuuji[second], secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("�b", secL + secR, secT + unitPositionAdjust, secTCol);
			}else{
		        secTCol.setTextSize(25);
				canvas.drawText(kansuuji[second/10] + kansuuji[10] + kansuuji[second%10], secL + secR, secT + positionAdjust, secTCol);
				secTCol.setTextSize(11);
				canvas.drawText("�b", secL + secR, secT + unitPositionAdjust, secTCol);
			}
			
			secTouch--;
		}
		
		
		
		// ���A���A�b��������
		if(autoLineFlg){
			autoMoveFlg = false;	// �e���v�����������̔���
			
			// ���̍��W�𓮂���
			houL = setSmallOrBigValue(houL, HOU_L_INITIAL);
			houT = setSmallOrBigValue(houT, HOU_T_INITIAL);

			// ���̍��W�𓮂���
			minL = setSmallOrBigValue(minL, MIN_L_INITIAL);
			minT = setSmallOrBigValue(minT, MIN_T_INITIAL);

			// �b�̍��W�𓮂���
			secL = setSmallOrBigValue(secL, SEC_L_INITIAL);
			secT = setSmallOrBigValue(secT, SEC_T_INITIAL);
			
			if(autoMoveFlg == false){
				autoLineFlg = false;
				
				// ���v�ʒu�ۑ�
				positionWriter();
			}
		}
		
		
		
		
        //// �}�X�R�b�g�L�����N�^�[�摜�\��
        // �`��}�X�R�b�g
        // ��
        canvas.drawRect(new RectF(droidRightEyeL+droidAdjustL-droidLeftEyeR, droidRightEyeT+droidAdjustT-droidLeftEyeR, droidLeftEyeL+droidAdjustL+droidLeftEyeR, droidRightEyeT+droidAdjustT+droidLeftEyeR), androidEye);
        Path clipPath = new Path();
        clipPath.addCircle(droidRightEyeL+droidAdjustL, droidRightEyeT+droidAdjustT, droidRightEyeR, Path.Direction.CW);
        clipPath.addCircle(droidLeftEyeL+droidAdjustL, droidLeftEyeT+droidAdjustT, droidLeftEyeR, Path.Direction.CW);
        canvas.clipPath(clipPath, Op.DIFFERENCE);
        
        Path droidPath = new Path();
        
        if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	        // �r�i�����j- �X�g�b�v�E�H�b�`��
	        droidPath.moveTo(droidRArmP1L+droidAdjustL, droidRArmP1T+droidAdjustT);
	        droidPath.lineTo(droidRArmP2L+droidAdjustL, droidRArmP2T+droidAdjustT);
	        droidPath.lineTo(droidRArmP3L+droidAdjustL, droidRArmP3T+droidAdjustT);
	        // �r�i�E���j- �X�g�b�v�E�H�b�`��
	        droidPath.moveTo(droidLArmP1L+droidAdjustL, droidLArmP1T+droidAdjustT);
	        droidPath.lineTo(droidLArmP2L+droidAdjustL, droidLArmP2T+droidAdjustT);
	        droidPath.lineTo(droidLArmP3L+droidAdjustL, droidLArmP3T+droidAdjustT);
        }else{
	        // �r�i�����j- �ʏ펞
	        droidPath.addRoundRect(new RectF(droidRightArmL+droidAdjustL, droidRightArmT+droidAdjustT, droidRightArmR+droidAdjustL, droidRightArmB+droidAdjustT), 7, 7, Path.Direction.CW);
	        // �r�i�E���j- �ʏ펞
	        droidPath.addRoundRect(new RectF(droidLeftArmL+droidAdjustL, droidLeftArmT+droidAdjustT, droidLeftArmR+droidAdjustL, droidLeftArmB+droidAdjustT), 7, 7, Path.Direction.CW);
        }
        
        // ���́i�㕔�j
        droidPath.addRect(new RectF(droidTopBodyL+droidAdjustL, droidTopBodyT+droidAdjustT, droidTopBodyR+droidAdjustL, droidTopBodyB+droidAdjustT), Path.Direction.CW);
        // ���́i�����j
        droidPath.addRoundRect(new RectF(droidBottomBodyL+droidAdjustL, droidBottomBodyT+droidAdjustT, droidBottomBodyR+droidAdjustL, droidBottomBodyB+droidAdjustT), 10, 10, Path.Direction.CW);
        // ���i�����j
        droidPath.addRoundRect(new RectF(droidRightFootL+droidAdjustL, droidRightFootT+droidAdjustT, droidRightFootR+droidAdjustL, droidRightFootB+droidAdjustT), 7, 7, Path.Direction.CW);
        // ���i�E���j
        droidPath.addRoundRect(new RectF(droidLeftFootL+droidAdjustL, droidLeftFootT+droidAdjustT, droidLeftFootR+droidAdjustL, droidLeftFootB+droidAdjustT), 7, 7, Path.Direction.CW);
        
        if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	        // �� - �X�g�b�v�E�H�b�`��
	        droidPath.addRect(new RectF(droidHeadSL+droidAdjustL, droidHeadST+droidAdjustT, droidHeadSR+droidAdjustL, droidHeadSB+droidAdjustT), Path.Direction.CW);
        }else{
	        // �� - �ʏ펞
	        droidPath.addArc(new RectF(droidHeadL+droidAdjustL, droidHeadT+droidAdjustT, droidHeadR+droidAdjustL, droidHeadB+droidAdjustT), 180, 180);
        }
        // �A���e�i�H�i�����j
        droidPath.moveTo(droidRAntennaP1L+droidAdjustL, droidRAntennaP1T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP2L+droidAdjustL, droidRAntennaP2T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP3L+droidAdjustL, droidRAntennaP3T+droidAdjustT);
        droidPath.lineTo(droidRAntennaP4L+droidAdjustL, droidRAntennaP4T+droidAdjustT);
        // �A���e�i�H�i�E���j
        droidPath.moveTo(droidLAntennaP1L+droidAdjustL, droidLAntennaP1T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP2L+droidAdjustL, droidLAntennaP2T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP3L+droidAdjustL, droidLAntennaP3T+droidAdjustT);
        droidPath.lineTo(droidLAntennaP4L+droidAdjustL, droidLAntennaP4T+droidAdjustT);
        
        canvas.drawPath(droidPath, droidColor);
        
        
        
        
        
        
        
        //// �ؑփp�l���`��
        if(typePnlShowFlg && typePnlSlideNum < 0){
        	if(typePnlSlideNum < -25){
        		// �ؑփp�l�����J��
        		typePnlSlideNum += 17;
        		
        		// �ؑփT�u�p�l�����J��
        		if(winVertical){
	        		typeSubPnlSlideX += 22;
	        		typeSubPnlSlideY -= 19;
        		}else{
        			typeSubPnlSlideX -= 22;
	        		typeSubPnlSlideY += 19;
        		}
        	}else{
        		// �ؑփp�l�����J��
        		typePnlSlideNum += 7;
        		
        		// �ؑփT�u�p�l�����J��
        		if(winVertical){
	        		typeSubPnlSlideX += 10;
	        		typeSubPnlSlideY -= 9;
        		}else{
        			typeSubPnlSlideX -= 10;
	        		typeSubPnlSlideY += 9;
        		}
        		
        		// �w��ʒu���߂�����w��̐���������
        		if(typePnlSlideNum > 0){
        			typePnlSlideNum = 0;
            		typeSubPnlSlideX = 0;
            		typeSubPnlSlideY = 0;
        		}
        	}
        }else if(typePnlShowFlg == false && typePnlSlideNum > TYPE_PNL_DEFAULT_NUM){
        	// �ؑփp�l�������
        	typePnlSlideNum -= 20;
        	
        	// �ؑփT�u�p�l�������
        	if(winVertical){
	    		typeSubPnlSlideX -= 27;
	    		typeSubPnlSlideY += 24;
        	}else{
        		typeSubPnlSlideX += 27;
	    		typeSubPnlSlideY -= 24;
        	}
        	
        	// �w��ʒu���߂�����w��̐���������
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
        
        // �ؑփp�l���J�p�{�^��
        Path path = new Path();
        int radius = 3;
        path.moveTo(typePnlShowBtnL, typePnlShowBtnT+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR, typePnlShowBtnT+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR, typePnlShowBtnB-radius+typePnlSlideNum);
		path.lineTo(typePnlShowBtnR-radius, typePnlShowBtnB+typePnlSlideNum);
		path.lineTo(typePnlShowBtnL+radius, typePnlShowBtnB+typePnlSlideNum);
		path.lineTo(typePnlShowBtnL, typePnlShowBtnB-radius+typePnlSlideNum);
		canvas.drawPath(path, typePnlColor);
        
        // �ؑփp�l���`��
        canvas.drawRoundRect(new RectF(typePanelL, typePanelT, typePanelR, typePanelB+typePnlSlideNum), 5, 5, typePnlColor);
        
        // �ؑփT�u�p�l��
        canvas.drawRoundRect(new RectF(typeSubPanelL+typeSubPnlSlideX, typeSubPanelT+typeSubPnlSlideY, typeSubPanelR+typeSubPnlSlideX, typeSubPanelB+typeSubPnlSlideY), 5, 5, typePnlColor);
        
        // �ؑփp�l���\�����̂ݕ`�揈�����s��
        if(typePnlShowFlg == true || typePnlSlideNum > -90){
        	// �؂�ւ��{�^���i���v�j
        	if(typeSwitchNum == TYPE_CLOCK){
        		typeBtnColor = clockBtnColor;
        	}
        	String clTxt = "CL";
        	if(typeSwitchNum == TYPE_CLOCK && showSpecificFlg){
        		// �w�莞�Ԑݒ莞�͑I�𒆂̔ԍ���\��
        		clTxt = String.valueOf(choiceSpecific);
        	}
	        canvas.drawRoundRect(new RectF(clockBtnL, clockBtnT+typePnlSlideNum, clockBtnR, clockBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText(clTxt, clockBtnL+(clockBtnR-clockBtnL)/2, clockBtnT+typePnlSlideNum+(clockBtnB-clockBtnT)/4*3, pnlTxtColor);
	        
	        // �؂�ւ��{�^���i�X�g�b�v�E�H�b�`�j
	        typeBtnColor = cnfBtnColor;
	        if(typeSwitchNum == TYPE_STOPWATCH){
        		typeBtnColor = stopWBtnColor;
        	}
	        canvas.drawRoundRect(new RectF(stopWatchBtnL, stopWatchBtnT+typePnlSlideNum, stopWatchBtnR, stopWatchBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText("SW", stopWatchBtnL+(stopWatchBtnR-stopWatchBtnL)/2, stopWatchBtnT+typePnlSlideNum+(stopWatchBtnB-stopWatchBtnT)/4*3, pnlTxtColor);
	        
	        // �؂�ւ��{�^���i�J�E���g�_�E���j
	        typeBtnColor = cnfBtnColor;
	        if(typeSwitchNum == TYPE_COUNTDOWN){
        		typeBtnColor = countDBtnColor;
        	}
	        canvas.drawRoundRect(new RectF(countDownBtnL, countDownBtnT+typePnlSlideNum, countDownBtnR, countDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        canvas.drawText("CD", countDownBtnL+(countDownBtnR-countDownBtnL)/2, countDownBtnT+typePnlSlideNum+(countDownBtnB-countDownBtnT)/4*3, pnlTxtColor);
        }
        
        // ���[�h�ɂ�镶���̔w�i�F�ύX
        if(typeSwitchNum == TYPE_CLOCK){
    		typeBtnColor = clockBtnColor;
    	}else if(typeSwitchNum == TYPE_STOPWATCH){
    		typeBtnColor = stopWBtnColor;
    	}else if(typeSwitchNum == TYPE_COUNTDOWN){
    		typeBtnColor = countDBtnColor;
    	}
        
        // �ؑփp�l���\�����̂ݕ`�揈�����s��
        if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        // �w�莞�Ԑݒ�Ɋւ���`��
	        if(typeSwitchNum == TYPE_CLOCK){
	        	if(showSpecificFlg == false){
			        // �w�莞�Ԑݒ�\���{�^��
			        canvas.drawRoundRect(new RectF(specificTimeBtnL, specificTimeBtnT+typePnlSlideNum, specificTimeBtnR, specificTimeBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Alarm", specificTimeBtnL+(specificTimeBtnR-specificTimeBtnL)/2, specificTimeBtnT+typePnlSlideNum+(specificTimeBtnB-specificTimeBtnT)/9*5, pnlTxtColor);
			        canvas.drawText("setting", specificTimeBtnL+(specificTimeBtnR-specificTimeBtnL)/2, specificTimeBtnT+typePnlSlideNum+((specificTimeBtnB-specificTimeBtnT)/9)*8, pnlTxtColor);
			        
			        // �T�u�p�l���^�C�g���̕\��
			        canvas.drawText("Next alarm", typeSubPanelL+typeSubPnlSlideX+subPnlTitleX, typeSubPanelT+typeSubPnlSlideY+subPnlTitleY, subPnlTxtColor);
			        
			        // ���̃A���[���܂ł̎���
			        subPnlTxtColor.setTextSize(20);
			        canvas.drawText(nextAlarmValue(nowHourOfDay, nowMinute, nowSecond, nowWeekDay), typeSubPanelL+typeSubPnlSlideX+nextAlarmTxtX, typeSubPanelT+typeSubPnlSlideY+nextAlarmTxtY, subPnlTxtColor);
			        subPnlTxtColor.setTextSize(12);
			        
		        }else{
		        	int subPX = typeSubPanelL+typeSubPnlSlideX;
		        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		        	// �T�u�p�l���^�C�g���̕\��
			        canvas.drawText("Alarm number", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
		        	
		        	// �w�莞�ԂP�ݒ�{�^��
		        	if(specificTimesOnFlg[0]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn1L+subPX, specificBtn1T+subPY, specificBtn1R+subPX, specificBtn1B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("1", specificBtn1L+subPX+(specificBtn1R-specificBtn1L)/2, specificBtn1T+subPY+(specificBtn1B-specificBtn1T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂQ�ݒ�{�^��
			        if(specificTimesOnFlg[1]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn2L+subPX, specificBtn2T+subPY, specificBtn2R+subPX, specificBtn2B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("2", specificBtn2L+subPX+(specificBtn2R-specificBtn2L)/2, specificBtn2T+subPY+(specificBtn2B-specificBtn2T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂR�ݒ�{�^��
			        if(specificTimesOnFlg[2]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn3L+subPX, specificBtn3T+subPY, specificBtn3R+subPX, specificBtn3B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("3", specificBtn3L+subPX+(specificBtn3R-specificBtn3L)/2, specificBtn3T+subPY+(specificBtn3B-specificBtn3T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂS�ݒ�{�^��
			        if(specificTimesOnFlg[3]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn4L+subPX, specificBtn4T+subPY, specificBtn4R+subPX, specificBtn4B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("4", specificBtn4L+subPX+(specificBtn4R-specificBtn4L)/2, specificBtn4T+subPY+(specificBtn4B-specificBtn4T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂT�ݒ�{�^��
			        if(specificTimesOnFlg[4]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn5L+subPX, specificBtn5T+subPY, specificBtn5R+subPX, specificBtn5B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("5", specificBtn5L+subPX+(specificBtn5R-specificBtn5L)/2, specificBtn5T+subPY+(specificBtn5B-specificBtn5T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂU�ݒ�{�^��
			        if(specificTimesOnFlg[5]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn6L+subPX, specificBtn6T+subPY, specificBtn6R+subPX, specificBtn6B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("6", specificBtn6L+subPX+(specificBtn6R-specificBtn6L)/2, specificBtn6T+subPY+(specificBtn6B-specificBtn6T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂV�ݒ�{�^��
			        if(specificTimesOnFlg[6]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn7L+subPX, specificBtn7T+subPY, specificBtn7R+subPX, specificBtn7B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("7", specificBtn7L+subPX+(specificBtn7R-specificBtn7L)/2, specificBtn7T+subPY+(specificBtn7B-specificBtn7T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂW�ݒ�{�^��
			        if(specificTimesOnFlg[7]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn8L+subPX, specificBtn8T+subPY, specificBtn8R+subPX, specificBtn8B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("8", specificBtn8L+subPX+(specificBtn8R-specificBtn8L)/2, specificBtn8T+subPY+(specificBtn8B-specificBtn8T)/4*3, pnlTxtColor);
		        	
			        // �w�莞�ԂX�ݒ�{�^��
			        if(specificTimesOnFlg[8]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificBtn9L+subPX, specificBtn9T+subPY, specificBtn9R+subPX, specificBtn9B+subPY), 5, 5, typeBtnColor);
			        canvas.drawText("9", specificBtn9L+subPX+(specificBtn9R-specificBtn9L)/2, specificBtn9T+subPY+(specificBtn9B-specificBtn9T)/4*3, pnlTxtColor);
		        	
//			        // �w�莞�ԂP�O�ݒ�{�^��
//			        if(specificTimesOnFlg[9]){
//		        		typeBtnColor = clockBtnColor;
//		        	}else{
//		        		typeBtnColor = specificTimeOffBtnColor;
//		        	}
//			        canvas.drawRoundRect(new RectF(specificBtn10L, specificBtn10T+typePnlSlideNum, specificBtn10R, specificBtn10B+typePnlSlideNum), 5, 5, typeBtnColor);
//			        canvas.drawText("10", specificBtn10L+(specificBtn10R-specificBtn10L)/2, specificBtn10T+typePnlSlideNum+(specificBtn10B-specificBtn10T)/4*3, pnlTxtColor);
			        
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][0]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn1L, specificWeekdayBtn1T+typePnlSlideNum, specificWeekdayBtn1R, specificWeekdayBtn1B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Sun", specificWeekdayBtn1L+(specificWeekdayBtn1R-specificWeekdayBtn1L)/2, specificWeekdayBtn1T+typePnlSlideNum+(specificWeekdayBtn1B-specificWeekdayBtn1T)/4*3, pnlTxtColor);
		        	
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][1]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn2L, specificWeekdayBtn2T+typePnlSlideNum, specificWeekdayBtn2R, specificWeekdayBtn2B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Mon", specificWeekdayBtn2L+(specificWeekdayBtn2R-specificWeekdayBtn2L)/2, specificWeekdayBtn2T+typePnlSlideNum+(specificWeekdayBtn2B-specificWeekdayBtn2T)/4*3, pnlTxtColor);
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][2]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn3L, specificWeekdayBtn3T+typePnlSlideNum, specificWeekdayBtn3R, specificWeekdayBtn3B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Tue", specificWeekdayBtn3L+(specificWeekdayBtn3R-specificWeekdayBtn3L)/2, specificWeekdayBtn3T+typePnlSlideNum+(specificWeekdayBtn3B-specificWeekdayBtn3T)/4*3, pnlTxtColor);
		        	
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][3]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn4L, specificWeekdayBtn4T+typePnlSlideNum, specificWeekdayBtn4R, specificWeekdayBtn4B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Wed", specificWeekdayBtn4L+(specificWeekdayBtn4R-specificWeekdayBtn4L)/2, specificWeekdayBtn4T+typePnlSlideNum+(specificWeekdayBtn4B-specificWeekdayBtn4T)/4*3, pnlTxtColor);
		        	
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][4]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn5L, specificWeekdayBtn5T+typePnlSlideNum, specificWeekdayBtn5R, specificWeekdayBtn5B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Thu", specificWeekdayBtn5L+(specificWeekdayBtn5R-specificWeekdayBtn5L)/2, specificWeekdayBtn5T+typePnlSlideNum+(specificWeekdayBtn5B-specificWeekdayBtn5T)/4*3, pnlTxtColor);
		        	
			        // �j���ݒ�{�^���P - ��
			        if(specificWeekdayOnFlg[choiceSpecific-1][5]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn6L, specificWeekdayBtn6T+typePnlSlideNum, specificWeekdayBtn6R, specificWeekdayBtn6B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Fri", specificWeekdayBtn6L+(specificWeekdayBtn6R-specificWeekdayBtn6L)/2, specificWeekdayBtn6T+typePnlSlideNum+(specificWeekdayBtn6B-specificWeekdayBtn6T)/4*3, pnlTxtColor);
		        	
			        // �j���ݒ�{�^���P - �y
			        if(specificWeekdayOnFlg[choiceSpecific-1][6]){
		        		typeBtnColor = clockBtnColor;
		        	}else{
		        		typeBtnColor = specificTimeOffBtnColor;
		        	}
			        canvas.drawRoundRect(new RectF(specificWeekdayBtn7L, specificWeekdayBtn7T+typePnlSlideNum, specificWeekdayBtn7R, specificWeekdayBtn7B+typePnlSlideNum), 5, 5, typeBtnColor);
			        canvas.drawText("Sat", specificWeekdayBtn7L+(specificWeekdayBtn7R-specificWeekdayBtn7L)/2, specificWeekdayBtn7T+typePnlSlideNum+(specificWeekdayBtn7B-specificWeekdayBtn7T)/4*3, pnlTxtColor);
		        	
			        
			        // �w�莞�Ԑݒ�{�^���ŕύX����Ă���{�^���F�����ɖ߂�
			        typeBtnColor = clockBtnColor;
			        
			        // �����{�^����������Ă���Ԃ͒l��������������
		        	if(pnlHouUpFlg){
		        		specificUpDownPush(PNLT_TARGET_HOU, COUNTD_UP);
		        	}else if(pnlHouDownFlg){
		        		specificUpDownPush(PNLT_TARGET_HOU, COUNTD_DOWN);
		        	}else if(pnlMinUpFlg){
		        		specificUpDownPush(PNLT_TARGET_MIN, COUNTD_UP);
		        	}else if(pnlMinDownFlg){
		        		specificUpDownPush(PNLT_TARGET_MIN, COUNTD_DOWN);
		        	}
			        
			        // �p�l�����������Ɏw�莞�Ԃ�\������悤�ɐݒ�
			        Calendar cal = Calendar.getInstance();
			        cal.setTimeInMillis(specificTimes[choiceSpecific-1]);
			        hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
			        minute = cal.get(Calendar.MINUTE);
		        }
	        }
	        

	        // �p�l�������\��
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
        
        // �X�g�b�v�E�H�b�`�Ɋւ���`��
        if(typeSwitchNum == TYPE_STOPWATCH){
            // �ؑփp�l���\�����̂ݕ`�揈�����s��
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// �~���b�\��
            	DecimalFormat format3 = new DecimalFormat("000");
	        	pnlTimeColor.setTextSize(12);
	        	canvas.drawRoundRect(new RectF(typePnlMilSecL, typePnlMilSecT+typePnlSlideNum, typePnlMilSecR, typePnlMilSecB+typePnlSlideNum), 5, 5, typeBtnColor);
	            canvas.drawText(format3.format(milSec), typePnlMilSecR-3, typePnlMilSecT+typePnlSlideNum+(typePnlMilSecB-typePnlMilSecT)/4*3, pnlTimeColor);
	            
		        // �T�u�p�l���^�C�g���̕\��
	            String title;
	            if(stopWatchSplitFlg){
	            	title = "Split time";
	            }else{
	            	title = "Rap time";
	            }
		        canvas.drawText(title, typeSubPanelL+typeSubPnlSlideX+subPnlTitleX, typeSubPanelT+typeSubPnlSlideY+subPnlTitleY, subPnlTxtColor);
	            
	            // �L�^���Ԃ̃y�[�W���\��
	            canvas.drawText(stopWatchRecordPage + "�^" + ((stopWatchRecordTime.size()-1)/stopWatchPageRecNum+1) , stopWatchPageX+typeSubPanelL+typeSubPnlSlideX, stopWatchPageY+typeSubPanelT+typeSubPnlSlideY, recordTxtColor);
	            
	            // �L�^���Ԃ̕\��
	            int recNumBase = ((stopWatchRecordPage-1)*stopWatchPageRecNum)+1;	// ���݂̃y�[�W�̍ŏ��̔ԍ�
	            int recSize = stopWatchRecordTime.size();
            	DecimalFormat format2 = new DecimalFormat("00");
            	
	            for(int iii=0; iii < stopWatchPageRecNum; iii++){
	            	if(recSize < recNumBase+iii){
	            		// �y�[�W�ɕ\������L�^���Ԃ��Ȃ��Ȃ�����I��
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
	        
            // �X�g�b�v�E�H�b�`�J�n�I���{�^���`��
        	canvas.drawRoundRect(new RectF(stopWStartStopBtnL, stopWStartStopBtnT, stopWStartStopBtnR, stopWStartStopBtnB), 5, 5, typeBtnColor);
        	canvas.drawText(stopWBtnTxt, stopWStartStopBtnL+(stopWStartStopBtnR-stopWStartStopBtnL)/2, stopWStartStopBtnT+(float)(stopWStartStopBtnB-stopWStartStopBtnT)/11*7, pnlTxtColor);
        }
        
        // �p�l�����v���̑����{�^���`�揈��
        if((typeSwitchNum == TYPE_COUNTDOWN) || (typeSwitchNum == TYPE_CLOCK && showSpecificFlg)){
        	// �ؑփp�l���\�����̂ݕ`�揈�����s��
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// �������{�^��
	        	canvas.drawRoundRect(new RectF(pnlHouUpBtnL, pnlHouUpBtnT+typePnlSlideNum, pnlHouUpBtnR, pnlHouUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlHouUpBtnL+(pnlHouUpBtnR-pnlHouUpBtnL)/2, pnlHouUpBtnT+typePnlSlideNum+(pnlHouUpBtnB-pnlHouUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlHouDownBtnL, pnlHouDownBtnT+typePnlSlideNum, pnlHouDownBtnR, pnlHouDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlHouDownBtnL+(pnlHouDownBtnR-pnlHouDownBtnL)/2, pnlHouDownBtnT+typePnlSlideNum+(pnlHouDownBtnB-pnlHouDownBtnT)/5*4, pnlTimeUpDownColor);
	        	// �������{�^��
	        	canvas.drawRoundRect(new RectF(pnlMinUpBtnL, pnlMinUpBtnT+typePnlSlideNum, pnlMinUpBtnR, pnlMinUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlMinUpBtnL+(pnlMinUpBtnR-pnlMinUpBtnL)/2, pnlMinUpBtnT+typePnlSlideNum+(pnlMinUpBtnB-pnlMinUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlMinDownBtnL, pnlMinDownBtnT+typePnlSlideNum, pnlMinDownBtnR, pnlMinDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlMinDownBtnL+(pnlMinDownBtnR-pnlMinDownBtnL)/2, pnlMinDownBtnT+typePnlSlideNum+(pnlMinDownBtnB-pnlMinDownBtnT)/5*4, pnlTimeUpDownColor);
            }
        }
        
        // �J�E���g�_�E���Ɋւ���`��
        if(typeSwitchNum == TYPE_COUNTDOWN){
        	// �ؑփp�l���\�����̂ݕ`�揈�����s��
            if(typePnlShowFlg == true || typePnlSlideNum > -90){
	        	// �b�����{�^��
	        	canvas.drawRoundRect(new RectF(pnlSecUpBtnL, pnlSecUpBtnT+typePnlSlideNum, pnlSecUpBtnR, pnlSecUpBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlSecUpBtnL+(pnlSecUpBtnR-pnlSecUpBtnL)/2, pnlSecUpBtnT+typePnlSlideNum+(pnlSecUpBtnB-pnlSecUpBtnT)/5*4, pnlTimeUpDownColor);
	        	canvas.drawRoundRect(new RectF(pnlSecDownBtnL, pnlSecDownBtnT+typePnlSlideNum, pnlSecDownBtnR, pnlSecDownBtnB+typePnlSlideNum), 5, 5, typeBtnColor);
	        	canvas.drawText("��", pnlSecDownBtnL+(pnlSecDownBtnR-pnlSecDownBtnL)/2, pnlSecDownBtnT+typePnlSlideNum+(pnlSecDownBtnB-pnlSecDownBtnT)/5*4, pnlTimeUpDownColor);
	        	
	        	// �����{�^����������Ă���Ԃ͒l��������������
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
		        // �T�u�p�l���^�C�g���̕\��
		        canvas.drawText("Set time", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
		        
		        // �J�E���g�_�E���ۑ�����
		        Calendar cal = Calendar.getInstance();
		        for(int iii=0; iii < countDSetTimes.length; iii++){
			        cal.setTimeInMillis(countDSetTimes[iii]);
			        DecimalFormat format = new DecimalFormat("00");
			   	    String hh = format.format(cal.get(Calendar.HOUR_OF_DAY));
	            	String mm = format.format(cal.get(Calendar.MINUTE));
	            	String ss = format.format(cal.get(Calendar.SECOND));
	            	
		        	canvas.drawText(hh + ":" + mm + ":" + ss, countDSetTimeX+subPX, countDSetTimeY[iii]+subPY, subPnlTxtColor);
		        }
	        	
	        	// �J�E���g�_�E���ۑ��{�^���̕`��
	        	canvas.drawRoundRect(new RectF(countDSaveBtn1L+subPX, countDSaveBtn1T+subPY, countDSaveBtn1R+subPX, countDSaveBtn1B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn1L+subPX+(countDSaveBtn1R-countDSaveBtn1L)/2, countDSaveBtn1T+subPY+(countDSaveBtn1B-countDSaveBtn1T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSaveBtn2L+subPX, countDSaveBtn2T+subPY, countDSaveBtn2R+subPX, countDSaveBtn2B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn2L+subPX+(countDSaveBtn2R-countDSaveBtn2L)/2, countDSaveBtn2T+subPY+(countDSaveBtn2B-countDSaveBtn2T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSaveBtn3L+subPX, countDSaveBtn3T+subPY, countDSaveBtn3R+subPX, countDSaveBtn3B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Save", countDSaveBtn3L+subPX+(countDSaveBtn3R-countDSaveBtn3L)/2, countDSaveBtn3T+subPY+(countDSaveBtn3B-countDSaveBtn3T)/3*2, pnlTxtColor);
	        	
	        	// �J�E���g�_�E���Z�b�g���ԃ{�^���̕`��
	        	canvas.drawRoundRect(new RectF(countDSetBtn1L+subPX, countDSetBtn1T+subPY, countDSetBtn1R+subPX, countDSetBtn1B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn1L+subPX+(countDSetBtn1R-countDSetBtn1L)/2, countDSetBtn1T+subPY+(countDSetBtn1B-countDSetBtn1T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSetBtn2L+subPX, countDSetBtn2T+subPY, countDSetBtn2R+subPX, countDSetBtn2B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn2L+subPX+(countDSetBtn2R-countDSetBtn2L)/2, countDSetBtn2T+subPY+(countDSetBtn2B-countDSetBtn2T)/3*2, pnlTxtColor);
	        	canvas.drawRoundRect(new RectF(countDSetBtn3L+subPX, countDSetBtn3T+subPY, countDSetBtn3R+subPX, countDSetBtn3B+subPY), 5, 5, typeBtnColor);
	        	canvas.drawText("Set", countDSetBtn3L+subPX+(countDSetBtn3R-countDSetBtn3L)/2, countDSetBtn3T+subPY+(countDSetBtn3B-countDSetBtn3T)/3*2, pnlTxtColor);
            }
            
        	// �J�E���g�_�E���J�n��~�{�^��
        	canvas.drawRoundRect(new RectF(countDStartStopBtnL, countDStartStopBtnT, countDStartStopBtnR, countDStartStopBtnB), 5, 5, typeBtnColor);
        	canvas.drawText(countDStartStopBtnTxt, countDStartStopBtnL+(countDStartStopBtnR-countDStartStopBtnL)/2, countDStartStopBtnT+(float)(countDStartStopBtnB-countDStartStopBtnT)/11*7, pnlTxtColor);
        }
        
        
        
        
        
        
        //// �ݒ�p�l���`��
        if(cnfModeFlg && cnfPnlSlideNum < 0){
        	if(cnfPnlSlideNum < -25){
            	// �ݒ�p�l�����J��
        		cnfPnlSlideNum += 17;
        		
        		// �ݒ�T�u�p�l�����J��
        		if(winVertical){
	        		cnfSubPnlSlideX += 22;
	        		cnfSubPnlSlideY -= 19;
        		}else{
        			cnfSubPnlSlideX -= 22;
	        		cnfSubPnlSlideY += 19;
        		}
        	}else{
        		// �ݒ�p�l�����J��
        		cnfPnlSlideNum += 7;
        		
        		// �ݒ�T�u�p�l�����J��
        		if(winVertical){
	        		cnfSubPnlSlideX += 10;
	        		cnfSubPnlSlideY -= 9;
        		}else{
        			cnfSubPnlSlideX -= 12;
	        		cnfSubPnlSlideY += 9;
        		}
        		
        		// �w��ʒu���߂�����w��̐���������
        		if(cnfPnlSlideNum > 0){
        			cnfPnlSlideNum = 0;
        			cnfSubPnlSlideX = 0;
	        		cnfSubPnlSlideY = 0;
        		}
        	}
        	// ��ʂ��Â�
        	canvas.drawRect(new RectF(0, 0, winSizeW, winSizeH), cnfCoverColor);
        }else if(cnfModeFlg == false && cnfPnlSlideNum > CNF_PNL_DEFAULT_NUM){
        	// �ݒ�p�l�������
        	cnfPnlSlideNum -= 20;
        	
        	// �ݒ�T�u�p�l�������
        	if(winVertical){
	    		cnfSubPnlSlideX -= 27;
	    		cnfSubPnlSlideY += 24;
        	}else{
        		cnfSubPnlSlideX += 27;
	    		cnfSubPnlSlideY -= 24;
        	}
        	
        	// �w��ʒu���߂�����w��̐���������
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
        }else if(cnfModeFlg){	// �ݒ�p�l���\����
        	// ��ʂ��Â�
        	canvas.drawRect(new RectF(0, 0, winSizeW, winSizeH), cnfCoverColor);
        }
        
        // �ݒ�p�l���J�p�{�^��
        canvas.drawRoundRect(new RectF(cnfPnlShowBtnL, cnfPnlShowBtnT+cnfPnlSlideNum, cnfPnlShowBtnR, cnfPnlShowBtnB+cnfPnlSlideNum), 5, 5, cnfPnlColor);
        
        // �ݒ�p�l���`��
        canvas.drawRoundRect(new RectF(cnfPanelL, cnfPanelT, cnfPanelR, cnfPanelB+cnfPnlSlideNum), 5, 5, cnfPnlColor);
        
        // �ݒ�T�u�p�l��
        canvas.drawRoundRect(new RectF(cnfSubPanelL+cnfSubPnlSlideX, cnfSubPanelT+cnfSubPnlSlideY, cnfSubPanelR+cnfSubPnlSlideX, cnfSubPanelB+cnfSubPnlSlideY), 5, 5, cnfPnlColor);
        
        // �ݒ�p�l���\�����̂ݕ`�揈�����s��
        if(cnfModeFlg || cnfPnlSlideNum > CNF_PNL_DEFAULT_NUM){
        	float btnTxtPos = pnlTxtColor.getTextSize()/3;
        	
	        // �����̂ݐݒ�{�^��
			canvas.drawRoundRect(new RectF(onlyHouMinBtnL, onlyHouMinBtnT+cnfPnlSlideNum, onlyHouMinBtnR, onlyHouMinBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(onlyHouMinBtnTxt, onlyHouMinBtnL+(onlyHouMinBtnR-onlyHouMinBtnL)/2, onlyHouMinBtnT+cnfPnlSlideNum+(onlyHouMinBtnB-onlyHouMinBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// ���ԕ\������ �����E�����؂�ւ��{�^��
			canvas.drawRoundRect(new RectF(timeTxtBtnL, timeTxtBtnT+cnfPnlSlideNum, timeTxtBtnR, timeTxtBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(timeTxtBtnTxt, timeTxtBtnL+(timeTxtBtnR-timeTxtBtnL)/2, timeTxtBtnT+cnfPnlSlideNum+(timeTxtBtnB-timeTxtBtnT)/2+btnTxtPos, pnlTxtColor);
			
			canvas.drawRoundRect(new RectF(alarmVibratBtnL, alarmVibratBtnT+cnfPnlSlideNum, alarmVibratBtnR, alarmVibratBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(alarmVibratBtnTxt, alarmVibratBtnL+(alarmVibratBtnR-alarmVibratBtnL)/2, alarmVibratBtnT+cnfPnlSlideNum+(alarmVibratBtnB-alarmVibratBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// ���v�`�؂�ւ��{�^��
			canvas.drawRoundRect(new RectF(timeFaceBtnL, timeFaceBtnT+cnfPnlSlideNum, timeFaceBtnR, timeFaceBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			canvas.drawText(timeFaceBtnText, timeFaceBtnL+(timeFaceBtnR-timeFaceBtnL)/2, timeFaceBtnT+cnfPnlSlideNum+(timeFaceBtnB-timeFaceBtnT)/2+btnTxtPos, pnlTxtColor);
			
			// TODO ���v�F�P�ύX�p�{�^��
//			setTypeColor(cnfBtnColor, 1, 1);
			cnfBtnColor.setARGB(255, (int)(((double)timeCol1[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol1[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(timeCol1BtnL, timeCol1BtnT+cnfPnlSlideNum, timeCol1BtnR, timeCol1BtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
	        // ���v�F�Q�ύX�p�{�^��
			cnfBtnColor.setARGB(255, (int)(((double)timeCol2[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)timeCol2[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(timeCol2BtnL, timeCol2BtnT+cnfPnlSlideNum, timeCol2BtnR, timeCol2BtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
	        // �w�i�F�ύX�p�{�^��
			cnfBtnColor.setARGB(255, (int)(((double)bgCol[GRAD_RED_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_GREEN_NUM]/(double)gradMaxNum)*255), (int)(((double)bgCol[GRAD_BLUE_NUM]/(double)gradMaxNum)*255));
			canvas.drawRoundRect(new RectF(backColBtnL, backColBtnT+cnfPnlSlideNum, backColBtnR, backColBtnB+cnfPnlSlideNum), 5, 5, cnfBtnColor);
			
			
			
        	int subPX = cnfSubPanelL+cnfSubPnlSlideX;
        	int subPY = cnfSubPanelT+cnfSubPnlSlideY;
			
			if(cnfSubMode == MODE_CLOCK_DISPLAY){	// ���v�i�b�j�ؑփ��[�h
				// �ݒ�T�u�p�l���^�C�g���\��
				canvas.drawText("Clock display", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);

				// �ݒ�p�{�^���\�� - HMS
				if(onlyHouMinF){
					cnfBtnColor = cnfOffBtnColor;
				}else{
					cnfBtnColor = cnfOnBtnColor;
				}
				canvas.drawRoundRect(new RectF(hMSBtnL+subPX, hMSBtnT+subPY, hMSBtnR+subPX, hMSBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HMS_TEXT, hMSBtnL+subPX+(hMSBtnR-hMSBtnL)/2, hMSBtnT+subPY+(hMSBtnB-hMSBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - HM
				if(onlyHouMinF){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(hMBtnL+subPX, hMBtnT+subPY, hMBtnR+subPX, hMBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HM_TEXT, hMBtnL+subPX+(hMBtnR-hMBtnL)/2, hMBtnT+subPY+(hMBtnB-hMBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_NUMBER_FORMAT){	// �����\���ؑփ��[�h
				// �ݒ�T�u�p�l���^�C�g���\��
				canvas.drawText("Number format", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �A���r�A����
				if(timeTxtNumFlg){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(arabicBtnL+subPX, arabicBtnT+subPY, arabicBtnR+subPX, arabicBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(ARABIC_TEXT, arabicBtnL+subPX+(arabicBtnR-arabicBtnL)/2, arabicBtnT+subPY+(arabicBtnB-arabicBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - ������
				if(timeTxtNumFlg){
					cnfBtnColor = cnfOffBtnColor;
				}else{
					cnfBtnColor = cnfOnBtnColor;
				}
				canvas.drawRoundRect(new RectF(kansuujiBtnL+subPX, kansuujiBtnT+subPY, kansuujiBtnR+subPX, kansuujiBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(KANSUUJI_TEXT, kansuujiBtnL+subPX+(kansuujiBtnR-kansuujiBtnL)/2, kansuujiBtnT+subPY+(kansuujiBtnB-kansuujiBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_ALARM_VIBRATE){	// �����E�o�C�u�ؑփ��[�h
				// �ݒ�T�u�p�l���^�C�g���\��
				canvas.drawText("Alarm vibrate", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// �ݒ�p�{�^���\�� - ����
				if(alarmVibratFlg == ALARM_VIBRAT_BOTH){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(bothBtnL+subPX, bothBtnT+subPY, bothBtnR+subPX, bothBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(BOTH_TEXT, bothBtnL+subPX+(bothBtnR-bothBtnL)/2, bothBtnT+subPY+(bothBtnB-bothBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �A���[��
				if(alarmVibratFlg == ALARM_VIBRAT_ALARM){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(alarmBtnL+subPX, alarmBtnT+subPY, alarmBtnR+subPX, alarmBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(ALARM_TEXT, alarmBtnL+subPX+(alarmBtnR-alarmBtnL)/2, alarmBtnT+subPY+(alarmBtnB-alarmBtnT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �o�C�u���[�V����
				if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(vibrateBtnL+subPX, vibrateBtnT+subPY, vibrateBtnR+subPX, vibrateBtnB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(VIBRATE_TEXT, vibrateBtnL+subPX+(vibrateBtnR-vibrateBtnL)/2, vibrateBtnT+subPY+(vibrateBtnB-vibrateBtnT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_CLOCK_SHAPE){	// ���v�`�ύX���[�h
				// �ݒ�T�u�p�l���^�C�g���\��
				canvas.drawText("Clock shape", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �~
				if(timeFace == TIMEFACE_CIRCLE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnCirL+subPX, timeFaceBtnCirT+subPY, timeFaceBtnCirR+subPX, timeFaceBtnCirB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(CIRCLE_TEXT, timeFaceBtnCirL+subPX+(timeFaceBtnCirR-timeFaceBtnCirL)/2, timeFaceBtnCirT+subPY+(timeFaceBtnCirB-timeFaceBtnCirT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �O�p
				if(timeFace == TIMEFACE_TRIANGLE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnTriL+subPX, timeFaceBtnTriT+subPY, timeFaceBtnTriR+subPX, timeFaceBtnTriB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(TRIANGLE_TEXT, timeFaceBtnTriL+subPX+(timeFaceBtnTriR-timeFaceBtnTriL)/2, timeFaceBtnTriT+subPY+(timeFaceBtnTriB-timeFaceBtnTriT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �l�p
				if(timeFace == TIMEFACE_SQUARE){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnSquL+subPX, timeFaceBtnSquT+subPY, timeFaceBtnSquR+subPX, timeFaceBtnSquB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(SQUARE_TEXT, timeFaceBtnSquL+subPX+(timeFaceBtnSquR-timeFaceBtnSquL)/2, timeFaceBtnSquT+subPY+(timeFaceBtnSquB-timeFaceBtnSquT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �܊p
				if(timeFace == TIMEFACE_PENTAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnPenL+subPX, timeFaceBtnPenT+subPY, timeFaceBtnPenR+subPX, timeFaceBtnPenB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(PENTAGON_TEXT, timeFaceBtnPenL+subPX+(timeFaceBtnPenR-timeFaceBtnPenL)/2, timeFaceBtnPenT+subPY+(timeFaceBtnPenB-timeFaceBtnPenT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - �Z�p
				if(timeFace == TIMEFACE_HEXAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnHexL+subPX, timeFaceBtnHexT+subPY, timeFaceBtnHexR+subPX, timeFaceBtnHexB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(HEXAGON_TEXT, timeFaceBtnHexL+subPX+(timeFaceBtnHexR-timeFaceBtnHexL)/2, timeFaceBtnHexT+subPY+(timeFaceBtnHexB-timeFaceBtnHexT)/2+btnTxtPos, pnlTxtColor);
				
				// �ݒ�p�{�^���\�� - ���p
				if(timeFace == TIMEFACE_OCTAGON){
					cnfBtnColor = cnfOnBtnColor;
				}else{
					cnfBtnColor = cnfOffBtnColor;
				}
				canvas.drawRoundRect(new RectF(timeFaceBtnOctL+subPX, timeFaceBtnOctT+subPY, timeFaceBtnOctR+subPX, timeFaceBtnOctB+subPY), 5, 5, cnfBtnColor);
				canvas.drawText(OCTAGON_TEXT, timeFaceBtnOctL+subPX+(timeFaceBtnOctR-timeFaceBtnOctL)/2, timeFaceBtnOctT+subPY+(timeFaceBtnOctB-timeFaceBtnOctT)/2+btnTxtPos, pnlTxtColor);
				
			}else if(cnfSubMode == MODE_CLOCK_COLOR_1 || cnfSubMode == MODE_CLOCK_COLOR_2 || cnfSubMode == MODE_BACKGROUND_COLOR){	// �F�I�����[�h
				int[] gradValue = new int[3];
				
				if(cnfSubMode == MODE_CLOCK_COLOR_1){		// �F�I�����[�h - ���v�F�P
					// �ݒ�T�u�p�l���^�C�g���\��
					canvas.drawText("Clock color 1", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// �F�̒l�擾
					gradValue = timeCol1;
				}else if(cnfSubMode == MODE_CLOCK_COLOR_2){	// �F�I�����[�h - ���v�F�Q
					// �ݒ�T�u�p�l���^�C�g���\��
					canvas.drawText("Clock color 2", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// �F�̒l�擾
					gradValue = timeCol2;
				}else if(cnfSubMode == MODE_BACKGROUND_COLOR){	// �F�I�����[�h - �w�i�F
					// �ݒ�T�u�p�l���^�C�g���\��
					canvas.drawText("Background color", subPnlTitleX+subPX, subPnlTitleY+subPY, subPnlTxtColor);
					
					// �F�̒l�擾
					gradValue = bgCol;
				}
				
		        // �O���f�[�V�����o�[�p
		        Paint gradationColor = new Paint();
		        gradationColor.setAntiAlias(true);
		        
		        // �O���f�[�V�����o�[�̘g�p
		        Paint gradFrameColor = new Paint();
		        gradFrameColor.setColor(Color.GRAY);
		        gradFrameColor.setAntiAlias(true);
		        gradFrameColor.setStyle(Paint.Style.STROKE);
		        gradFrameColor.setStrokeWidth(1);
		        
		        // �ݒ�o�[�p
		        Paint settingBarColor = new Paint();
		        settingBarColor.setARGB(255, 230, 230, 230);
		        settingBarColor.setAntiAlias(true);
		        
		        // �ݒ�o�[�̘g�p
		        Paint setFrameColor = new Paint();
		        setFrameColor.setARGB(255, 230, 230, 230);
		        setFrameColor.setStyle(Paint.Style.STROKE);
		        setFrameColor.setStrokeWidth(0);
		        setFrameColor.setColor(Color.GRAY);
		        setFrameColor.setAntiAlias(true);
				
				// �O���f�[�V�����̐ݒ�
		        Shader gradationRed = new LinearGradient(gradRedL+subPX, 0, gradRedR+subPX, 0, 0xFF000000, 0xFFFF0000, Shader.TileMode.CLAMP);
		        Shader gradationGreen = new LinearGradient(gradGreenL+subPX, 0, gradGreenR+subPX, 0, 0xFF000000, 0xFF00FF00, Shader.TileMode.CLAMP);
		        Shader gradationBlue = new LinearGradient(gradBlueL+subPX, 0, gradBlueR+subPX, 0, 0xFF000000, 0xFF0000FF, Shader.TileMode.CLAMP);
		        
		        // �O���f�[�V�����o�[�`��ʒu�ݒ� - ��{�ʒu
		        Path gradBarPath = new Path();
		        gradBarPath.moveTo(gradPoint1L+subPX, gradPoint1T+subPY);
		        gradBarPath.lineTo(gradPoint2L+subPX, gradPoint2T+subPY);
		        gradBarPath.lineTo(gradPoint3L+subPX, gradPoint3T+subPY);
		        gradBarPath.lineTo(gradPoint4L+subPX, gradPoint4T+subPY);
		        gradBarPath.lineTo(gradPoint5L+subPX, gradPoint5T+subPY);
		        gradBarPath.lineTo(gradPoint6L+subPX, gradPoint6T+subPY);
		        gradBarPath.lineTo(gradPoint7L+subPX, gradPoint7T+subPY);
		        gradBarPath.lineTo(gradPoint8L+subPX, gradPoint8T+subPY);
		        
		        // �O���f�[�V�����o�[�\���i�ԁj
		        gradationColor.setShader(gradationRed);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);
		        
		        // �O���f�[�V�����o�[�\���i�΁j
		        canvas.translate(0, gradBarAdjust);
		        gradationColor.setShader(gradationGreen);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);
		        
		        // �O���f�[�V�����o�[�\���i�j
		        canvas.translate(0, gradBarAdjust);
		        gradationColor.setShader(gradationBlue);
		        canvas.drawPath(gradBarPath, gradationColor);
		        canvas.drawPath(gradBarPath, gradFrameColor);

		        // ���������L�����o�X�����ɖ߂�
		        canvas.translate(0, -(gradBarAdjust*2));
		        
		        // �ݒ�o�[�̕\���i�ԁj
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_RED_NUM]+gradRedL-5+subPX, gradRedT+subPY, gradValue[GRAD_RED_NUM]+gradRedL+5+subPX, gradRedB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_RED_NUM]+gradRedL-5+subPX, gradRedT+subPY, gradValue[GRAD_RED_NUM]+gradRedL+5+subPX, gradRedB+subPY), 5, 5, setFrameColor);
		        // �ݒ�o�[�̕\���i�΁j
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_GREEN_NUM]+gradGreenL-5+subPX, gradGreenT+subPY, gradValue[GRAD_GREEN_NUM]+gradGreenL+5+subPX, gradGreenB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_GREEN_NUM]+gradGreenL-5+subPX, gradGreenT+subPY, gradValue[GRAD_GREEN_NUM]+gradGreenL+5+subPX, gradGreenB+subPY), 5, 5, setFrameColor);
		        // �ݒ�o�[�̕\���i�j
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_BLUE_NUM]+gradBlueL-5+subPX, gradBlueT+subPY, gradValue[GRAD_BLUE_NUM]+gradBlueL+5+subPX, gradBlueB+subPY), 5, 5, settingBarColor);
		        canvas.drawRoundRect(new RectF(gradValue[GRAD_BLUE_NUM]+gradBlueL-5+subPX, gradBlueT+subPY, gradValue[GRAD_BLUE_NUM]+gradBlueL+5+subPX, gradBlueB+subPY), 5, 5, setFrameColor);

			}
        }
		
        

        
		// ���Ԃ��傤�ǃ`�F�b�N
		if(nowMinute == 0 && nowSecond == 0 && justTimeFlg == false){
			justTimeFlg = true;
			
			balloonInit();
		}
		
		// �w�莞�ԃ`�F�b�N
		for(int iii=0; iii < specificTimes.length; iii++){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(specificTimes[iii]);
			
			if(specificTimesOnFlg[iii] && specificWeekdayOnFlg[iii][nowWeekDay-1] && nowHourOfDay == cal.get(Calendar.HOUR_OF_DAY) && nowMinute == cal.get(Calendar.MINUTE) && nowSecond == cal.get(Calendar.SECOND) && showSpecificFlg == false && specificFlg == false){
				// �A���h���C�h�Ɛ����o��
				specificFlg = true;
				balloonInit();
				
				// �A���[���A�o�C�u���[�^�[����
				alarmVibrat();
			}
		}
		
		// ���Ԃ����傤�ǁA�J�E���g�_�E����A�w�莞�Ԃ� �}�X�R�b�g�����˂�A�����o�����o��
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
         * �f�o�b�O�p
         * 
         * */
/*
		// �����̃J�E���^�[ ���݉��t���[���ڂ���\��
        String msg = "Frame" + drawCount++;
        canvas.drawText(msg, 2, 30, counterColor);
        
        // ���ԓ��̕\��
        canvas.drawText("AP�F" + String.valueOf(ampm), 20, 90, counterColor);
        canvas.drawText("���F" + String.valueOf(hour), 20, 110, counterColor);
        canvas.drawText("���F" + String.valueOf(minute), 20, 130, counterColor);
        canvas.drawText("�b�F" + String.valueOf(second), 20, 150, counterColor);
        canvas.drawText("ms�F" + String.valueOf(milSec), 100, 70, counterColor);
        
        
        canvas.drawText("SP�F" + String.valueOf(secPArc), 100, 150, counterColor);
		canvas.drawText("time�F" + String.valueOf(calendar.getTimeInMillis()), 100, 130, counterColor);
		canvas.drawText("tz�F" + cnfModeFlg + " " + cnfPnlSlideNum , 100, 110, counterColor);
        

        /***********************************************************/
    }
    
    // -------------------------------------------
    // �^�b�`�C�x���g
    // -------------------------------------------
    public boolean onTouchEvent(MotionEvent event) {
        // X,Y���W�̎擾
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();
        int evAction = (int)event.getAction();

        // �^�b�`�����Ƃ�
        if(evAction == MotionEvent.ACTION_DOWN){
        	if(cnfModeFlg == false){	// �ݒ�p�l����\�����Ă��Ȃ��ꍇ�̂�
	        	// ���͈̔͂��^�b�`�����ꍇ
	            if(touchX >= houL && touchX <= houL+houR*2 && touchY >= houT && touchY <= houT+houR*2){
	            	houMove = true;
	            }
	            // ���͈̔͂��^�b�`�����ꍇ
	            if(touchX >= minL && touchX <= minL+minR*2 && touchY >= minT && touchY <= minT+minR*2){
	            	minMove = true;
	            }
	            // �b�͈̔͂��^�b�`�����ꍇ
	            if(touchX >= secL && touchX <= secL+secR*2 && touchY >= secT && touchY <= secT+secR*2){
	            	if(onlyHouMinF == false || typeSwitchNum != TYPE_CLOCK){
	            		secMove = true;
	            	}
	            }
	            
	            
	            //// �ؑփp�l���p�^�b�`�̈�
	            // �ؑփp�l���J�{�^���͈̔͂��^�b�`�����ꍇ
	            if(touchX >= typePnlShowBtnL && touchX <= typePnlShowBtnR && touchY >= typePnlShowBtnT+typePnlSlideNum && touchY <= typePnlShowBtnB+typePnlSlideNum){
	            	if(typePnlShowFlg){
	            		typePnlShowFlg = false;
	            		showSpecificFlg = false;
	            		
	            		// �ݒ莞�ԃt�@�C���ۑ�
	            		timeWriter();
	            	}else{
	            		typePnlShowFlg = true;
	            	}
	            }
	            
	            // ���v�A�X�g�b�v�E�H�b�`�A�J�E���g�_�E���؂�ւ��{�^���͈̔͂��^�b�`�����ꍇ
	            if(touchX >= clockBtnL && touchX <= clockBtnR && touchY >= clockBtnT+typePnlSlideNum && touchY <= clockBtnB+typePnlSlideNum){	// ���[�h�ؑփ{�^��(���v)�Ƀ^�b�`�����ꍇ
	            	// ���݂̃��[�h�����v���[�h�ɕύX
	            	typeSwitchNum = TYPE_CLOCK;
	            	
	            	// �w�莞�Ԑݒ��
	            	showSpecificFlg = false;
	            }
	            if(touchX >= stopWatchBtnL && touchX <= stopWatchBtnR && touchY >= stopWatchBtnT+typePnlSlideNum && touchY <= stopWatchBtnB+typePnlSlideNum){	// ���[�h�ؑփ{�^��(�X�g�b�v�E�H�b�`)�Ƀ^�b�`�����ꍇ
	            	if(typeSwitchNum == TYPE_STOPWATCH){
	            		// �X�g�b�v�E�H�b�`�I�𒆂Ƀ^�b�`���ꂽ�ꍇ�̓X�g�b�v�E�H�b�`�̒l��������
		            	stopWatchStartFlg = false;
		            	stopWatchStartTime = Calendar.getInstance();
		                stopWatchStopTime.setTimeInMillis(stopWatchStartTime.getTimeInMillis());
		                stopWatchRecordTime.clear();
		                stopWatchRecordPage = 1;
	            	}else{
	            		// ���݂̃��[�h���X�g�b�v�E�H�b�`���[�h�ɕύX
	            		typeSwitchNum = TYPE_STOPWATCH;
		            	showSpecificFlg = false;
	            	}
	            }
	            if(touchX >= countDownBtnL && touchX <= countDownBtnR && touchY >= countDownBtnT+typePnlSlideNum && touchY <= countDownBtnB+typePnlSlideNum){	// ���[�h�ؑփ{�^��(�J�E���g�_�E��)�Ƀ^�b�`�����ꍇ
	            	if(typeSwitchNum == TYPE_COUNTDOWN){
	            		// �J�E���g�_�E���I�𒆂Ƀ^�b�`���ꂽ�ꍇ�̓J�E���g�_�E���̒l��������
		            	countDownStartFlg = false;
	            		countDownTime.setTimeInMillis(countDDatumTime);
	            	}else{
	            		// ���݂̃��[�h���J�E���g�_�E�����[�h�ɕύX
	            		typeSwitchNum = TYPE_COUNTDOWN;
		            	showSpecificFlg = false;
	            	}
	            }
	            
	            
	            // �w�莞�Ԑݒ�\�����̂�
	            if(typeSwitchNum == TYPE_CLOCK && showSpecificFlg){
		            // �p�l�����v�̑����{�^���͈̔͂��^�b�`�����ꍇ
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
		            
		            // �w�莞�Ԕԍ��ؑփ{�^���͈̔͂��^�b�`�����ꍇ// TODO if�������\�b�h�ɂ�����
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
		            
		            // �j���ݒ�{�^���͈̔͂��^�b�`�����ꍇ
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
	            
	            // �w�莞�Ԑݒ� �\���p�{�^���͈̔͂��^�b�`�����ꍇ ���v���[�h�Ŏw�莞�Ԑݒ�ł͂Ȃ�
	            if(touchX >= specificTimeBtnL && touchX <= specificTimeBtnR && touchY >= specificTimeBtnT+typePnlSlideNum && touchY <= specificTimeBtnB+typePnlSlideNum && typeSwitchNum == TYPE_CLOCK && showSpecificFlg == false){
	            		showSpecificFlg = true;
	            }
	            
	            
	            // �X�g�b�v�E�H�b�`���[�h���̂�
	            if(typeSwitchNum == TYPE_STOPWATCH){
	            	// �X�g�b�v�E�H�b�`�J�n�I���{�^���͈̔͂��^�b�`�����ꍇ
		            if(touchX >= stopWStartStopBtnL && touchX <= stopWStartStopBtnR && touchY >= stopWStartStopBtnT && touchY <= stopWStartStopBtnB){
		            	if(stopWatchStartFlg){
		            		stopWatchStopTime = Calendar.getInstance();
		            		stopWatchStartFlg = false;
		            	}else{
		            		stopWatchStartTime = Calendar.getInstance();
		            		stopWatchStartFlg = true;
		            	}
		            }
		            
		            // �p�l�����J���Ă���Ƃ��ɃT�u�p�l���͈̔͂Ƀ^�b�`�����ꍇ
		            if(touchX >= typeSubPanelL && touchX <= typeSubPanelR && touchY >= typeSubPanelT && touchY <= typeSubPanelB && typePnlShowFlg && stopWatchStartFlg){
		            	Calendar cal = Calendar.getInstance();
		            	stopWatchRecordTime.add(cal.getTimeInMillis() - stopWatchStartTime.getTimeInMillis() - cal.get(Calendar.ZONE_OFFSET));
		            	
		            	if(stopWatchRecordTime.size() > stopWatchRecTMaxNum){
		            		stopWatchRecordTime.remove(0);
		            	}
		            }
	            }
	            
	            // �J�E���g�_�E�����[�h���̂�
	            if(typeSwitchNum == TYPE_COUNTDOWN){
	            	// �J�E���g�_�E���X�g�b�v���̂�
	            	if(countDownStartFlg == false){
			            // �p�l�����v�̑����{�^���͈̔͂��^�b�`�����ꍇ
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
		            
		            // �J�E���g�_�E���J�n�I���{�^���͈̔͂��^�b�`�����ꍇ
		            if(touchX >= countDStartStopBtnL && touchX <= countDStartStopBtnR && touchY >= countDStartStopBtnT && touchY <= countDStartStopBtnB){
		            	if(countDownStartFlg){	//�J�n���Ă����ꍇ
		            		Calendar cal = Calendar.getInstance();
		            		
		            		// �J�E���g�_�E���J�n������̌o�ߎ��Ԃ��擾
		            		cal.setTimeInMillis(Calendar.getInstance().getTimeInMillis() - countDownStartTime.getTimeInMillis());
		            		
		            		// �J�E���g�_�E����~���Ƀ~���b�����r���[�Ȃ��߁A��~��0�b��ݒ肵�Ă����v�����Ƀ~���b���c�� - ���̖�����������ɂ͈ȉ�2�s�̃R�����g��������
		            		//cal.set(Calendar.SECOND, cal.get(Calendar.SECOND)-1);
		            		//cal.set(Calendar.MILLISECOND,0);
		            		
		            		// �J�E���g�_�E���̎c�莞�Ԃ��Z�b�g
		            		countDownTime.setTimeInMillis(countDownTime.getTimeInMillis() - cal.getTimeInMillis());
		            		countDownStartFlg = false;
		            	}else if(countDownTime.getTimeInMillis() > countDDatumTime){
		            		countDownStartTime = Calendar.getInstance();
		            		countDownStartFlg = true;
		            	}
		            }
		            
		            // �T�u�p�l���ɕ\������ۂ̊�l
		        	int subPX = typeSubPanelL+typeSubPnlSlideX;
		        	int subPY = typeSubPanelT+typeSubPnlSlideY;
		            
		            // �J�E���g�_�E����~���̏ꍇ
		        	if(countDownStartFlg == false){
		        		// �J�E���g�_�E���ۑ��{�^���͈̔͂Ƀ^�b�`�����ꍇ
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
			            
			            // �J�E���g�_�E���Z�b�g�{�^���͈̔͂Ƀ^�b�`�����ꍇ
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
	            
	            // �X�g�b�v�E�H�b�`���[�h���Ńp�l�����J���Ă���ꍇ
	            if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){
	            	// �A���h���C�h�̍����̘r�i�E�r�j�͈̔͂��^�b�`�����ꍇ
	            	if(touchX >= droidRightArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidRightArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidRightArmT+droidAdjustT && touchY <= droidRightArmB+droidAdjustT){
	            		// �L�^���Ԃ�߂�����
	            		if(stopWatchRecordPage > 1){
	            			stopWatchRecordPage--;
	            		}
	            	}
	            	
	            	// �A���h���C�h�̓��͈̔͂��^�b�`�����ꍇ
	            	if(touchX >= droidHeadSL+droidAdjustL && touchX <= droidHeadSR+droidAdjustL && touchY >= droidHeadST+droidAdjustT && touchY <= droidHeadSB+droidAdjustT){
	            		if(stopWatchSplitFlg){
	            			stopWatchSplitFlg = false;
	            		}else{
	            			stopWatchSplitFlg = true;
	            		}
	            	}
	            }
	            
	            // �A���h���C�h�E���̘r�i���r�j�͈̔͂��^�b�`�����ꍇ
	            if(touchX >= droidLeftArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidLeftArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidLeftArmT+droidAdjustT && touchY <= droidLeftArmB+droidAdjustT){
	            	
	            	if(typeSwitchNum == TYPE_STOPWATCH && typePnlShowFlg){	// �X�g�b�v�E�H�b�`���[�h���Ńp�l�����J���Ă���ꍇ
	            		// �L�^���ԃy�[�W��i�߂鏈��
	            		if(stopWatchRecordPage < ((stopWatchRecordTime.size()-1)/stopWatchPageRecNum+1)){
	            			stopWatchRecordPage++;
	            		}
	            	}else{
	            		// �^�b�`���ꂽ�t���O - �^�b�`�A�b�v���Ɏg�p
	            		autoLineBtnFlg = true;
	            	}
	            }
        	}
        	
        	
            //// �ݒ�p�^�b�`�̈�
            // �ݒ�p�l���J�{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= cnfPnlShowBtnL && touchX <= cnfPnlShowBtnR && touchY >= cnfPnlShowBtnT+cnfPnlSlideNum && touchY <= cnfPnlShowBtnB+cnfPnlSlideNum){
            	if(cnfModeFlg){
            		cnfModeFlg = false;
            		
            		// �ݒ�t�@�C���ۑ�
            		settingWriter();
            	}else{
            		cnfModeFlg = true;
            	}
            }
            
            // �����̂݃{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= onlyHouMinBtnL && touchX <= onlyHouMinBtnR && touchY >= onlyHouMinBtnT+cnfPnlSlideNum && touchY <= onlyHouMinBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_DISPLAY;
            }
            
            // ���v�������������ؑփ{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= timeTxtBtnL && touchX <= timeTxtBtnR && touchY >= timeTxtBtnT+cnfPnlSlideNum && touchY <= timeTxtBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_NUMBER_FORMAT;
            }
            
            // �����E�o�C�u�ؑփ{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= alarmVibratBtnL && touchX <= alarmVibratBtnR && touchY >= alarmVibratBtnT+cnfPnlSlideNum && touchY <= alarmVibratBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_ALARM_VIBRATE;
            }
            
            // ���v�`�ύX�{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= timeFaceBtnL && touchX <= timeFaceBtnR && touchY >= timeFaceBtnT+cnfPnlSlideNum && touchY <= timeFaceBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_SHAPE;
            }
            
            // ���v�F�P�ύX�{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= timeCol1BtnL && touchX <= timeCol1BtnR && touchY >= timeCol1BtnT+cnfPnlSlideNum && touchY <= timeCol1BtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_COLOR_1;
            }
            
            // ���v�F�Q�ύX�{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= timeCol2BtnL && touchX <= timeCol2BtnR && touchY >= timeCol2BtnT+cnfPnlSlideNum && touchY <= timeCol2BtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_CLOCK_COLOR_2;
            }

            // ���v�F�Q�ύX�{�^���͈̔͂��^�b�`�����ꍇ
            if(touchX >= backColBtnL && touchX <= backColBtnR && touchY >= backColBtnT+cnfPnlSlideNum && touchY <= backColBtnB+cnfPnlSlideNum){
            	cnfSubMode = MODE_BACKGROUND_COLOR;
            }
            
            
            
            // �T�u�p�l���ɕ\������ۂ̊�l
        	int subPX = cnfSubPanelL+cnfSubPnlSlideX;
        	int subPY = cnfSubPanelT+cnfSubPnlSlideY;
            
        	if(cnfSubMode == MODE_CLOCK_DISPLAY){	// ���v�i�b�j�ؑփ��[�h
	            if(touchX >= hMSBtnL+subPX && touchX <= hMSBtnR+subPX && touchY >= hMSBtnT+subPY && touchY <= hMSBtnB+subPY){
	            	onlyHouMinF = false;
	            	onlyHouMinBtnTxt = HMS_TEXT;
	            }
	            if(touchX >= hMBtnL+subPX && touchX <= hMBtnR+subPX && touchY >= hMBtnT+subPY && touchY <= hMBtnB+subPY){
	            	onlyHouMinF = true;
	            	onlyHouMinBtnTxt = HM_TEXT;
	            }
			}else if(cnfSubMode == MODE_NUMBER_FORMAT){	// �����\���ؑփ��[�h
	            if(touchX >= arabicBtnL+subPX && touchX <= arabicBtnR+subPX && touchY >= arabicBtnT+subPY && touchY <= arabicBtnB+subPY){
	            	timeTxtNumFlg = true;
	            	timeTxtBtnTxt = ARABIC_TEXT;
	            }
	            if(touchX >= kansuujiBtnL+subPX && touchX <= kansuujiBtnR+subPX && touchY >= kansuujiBtnT+subPY && touchY <= kansuujiBtnB+subPY){
	            	timeTxtNumFlg = false;
	            	timeTxtBtnTxt = KANSUUJI_TEXT;
	            }
			}else if(cnfSubMode == MODE_ALARM_VIBRATE){	// �����E�o�C�u�ؑփ��[�h
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
			}else if(cnfSubMode == MODE_CLOCK_SHAPE){	// ���v�`�ύX���[�h
	            // ���v�`�ύX�{�^�����͈̔͂��^�b�`�����ꍇ
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
	            // �O���f�[�V�����i�ԁj�͈�
	            if(touchX >= gradRedL+subPX && touchX <= gradRedR+subPX && touchY >= gradRedT+subPY && touchY <= gradRedB+subPY){
	            	// �Ԃ�ݒ蒆�Ƃ���
	            	gradSettingColor = GRAD_RED_NUM;
	            	
	            	// �F�̐ݒ������
	            	setColor(touchX - (gradRedL+subPX));
	            }
	            // �O���f�[�V�����i�΁j�͈�
	            if(touchX >= gradGreenL+subPX && touchX <= gradGreenR+subPX && touchY >= gradGreenT+subPY && touchY <= gradGreenB+subPY){
	            	// �΂�ݒ蒆�Ƃ���
	            	gradSettingColor = GRAD_GREEN_NUM;
	            	
	            	// �F�̐ݒ������
	            	setColor(touchX - (gradGreenL+subPX));
	            }
	            // �O���f�[�V�����i�j�͈�
	            if(touchX >= gradBlueL+subPX && touchX <= gradBlueR+subPX && touchY >= gradBlueT+subPY && touchY <= gradBlueB+subPY){
	            	// ��ݒ蒆�Ƃ���
	            	gradSettingColor = GRAD_BLUE_NUM;
	            	
	            	// �F�̐ݒ������
	            	setColor(touchX - (gradBlueL+subPX));
	            }
			}
        }
        
        // ���������Ƃ�
        if(evAction == MotionEvent.ACTION_MOVE){
        	// ���v�̔z�u���ړ�����
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
        	
        	// �O���f�[�V�����̒l��ύX����
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
	        	// �F�̐ݒ������
	        	setColor(colNum);
        	}
        }
        
        // �w�𗣂����Ƃ�
        if(evAction == MotionEvent.ACTION_UP){
        	// ���v�𓮂����Ă����ꍇ
        	if(houMove || minMove || secMove){
        		// ���v�ʒu�̕ۑ�
        		positionWriter();
        		
        		// ���A���A�b�}�`�ړ��t���OOFF
            	houMove = false;
            	minMove = false;
            	secMove = false;
        	}

        	
        	// �p�l�����ԕ����̑����t���OOFF
        	pnlHouUpFlg = false;
        	pnlHouDownFlg = false;
        	pnlMinUpFlg = false;
        	pnlMinDownFlg = false;
        	pnlSecUpFlg = false;
        	pnlSecDownFlg = false;
        	
        	// �p�l���������̃E�F�C�g��������
        	upDownwait = 5;
        	
        	// �A���h���C�h�̍��r�N���b�N����
        	if(touchX >= droidLeftArmL+droidAdjustL-droidArmBWidthPlusL && touchX <= droidLeftArmR+droidAdjustL+droidArmBWidthPlusL && touchY >= droidLeftArmT+droidAdjustT && touchY <= droidLeftArmB+droidAdjustT && autoLineBtnFlg){
        		autoLineFlg = true;
        	}
        	autoLineBtnFlg = false;
        	
        	// �O���f�[�V�����̐ݒ蒆������Ȃ��ɕύX����
        	gradSettingColor = GRAD_NO_COLOR;
        }
        
        if(cnfModeFlg == false){
	        // ���̏��ʉ߂����ꍇ
	        if(touchX >= houL && touchX <= houL+houR*2 && touchY >= houT && touchY <= houT+houR*2 && houTouch == 0 && houMove == false){
	        	houTouch = TOUCHMAXNUM;
	        }
	        // ���̏��ʉ߂����ꍇ
	        if(touchX >= minL && touchX <= minL+minR*2 && touchY >= minT && touchY <= minT+minR*2 && minTouch == 0 && minMove == false){
	        	minTouch = TOUCHMAXNUM;
	        }
	        // �b�̏��ʉ߂����ꍇ
	        if(touchX >= secL && touchX <= secL+secR*2 && touchY >= secT && touchY <= secT+secR*2 && secTouch == 0 && secMove == false && onlyHouMinF == false){
	        	secTouch = TOUCHMAXNUM;
	        }
        }
        
        return true;
    }
    
    /*************************************************
     *  ���v�����`��
     * canvas		:�L�����o�X
     * cColor1		:���v�F�P
     * cColor2		:���v�F�Q
     * fColor		:���v�g
     * hms			:���A���A�b�̂ǂꂪ�ΏۂȂ̂�
     * switchCol	:�ǂ����̐F�ŏd�˂�^�[���Ȃ̂�
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
    	
		// ���A���A�b�ɉ����ĕ`��ꏊ�A���Ԃ�ύX����
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
    		objArc = secArc + secPArc; // �u+ secPArc�v ��ǉ��ŕb���X���[�Y�ɓ���
    	}
		
		bgSize = (int)((objR * 2) * Math.sqrt(2) - (objR * 2)) / 2;
		
		// ���v�`�ɉ������`��
		switch(timeFace){
			case TIMEFACE_TRIANGLE:
				objOval = new RectF(objL-bgSize, objT+(int)(objR-objR/Math.sqrt(3))-bgSize, objL+objR*2+bgSize, objT+objR*2+(int)(objR-objR/Math.sqrt(3))+bgSize);
	    		objT += 100;
				
	    		//// �`��͈͎w�� �`canvas.restore();�܂�
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+objR, objT-(int)(objR*Math.sqrt(3)));
				path.lineTo(objL+objR*2, objT);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// �ǂ̕`��
					drawTimeArc(canvas, objOval, cColor1, cColor2, objArc, switchCol);
				canvas.restore();
				
				canvas.drawPath(path, fColor);
				break;
			case TIMEFACE_SQUARE:
				objOval = new RectF(objL-bgSize, objT-bgSize, objL+objR*2+bgSize, objT+objR*2+bgSize);
				
				//// �`��͈͎w�� �`canvas.restore();�܂�
				canvas.save();
				canvas.clipRect(objL, objT, objL+objR*2, objT+objR*2);
					// �ǂ̕`��
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
				
				
				//// �`��͈͎w�� �`canvas.restore();�܂�
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+penPntL1, objT+penPntT1);
				path.lineTo(objL+penPntL2, objT+penPntT2);
				path.lineTo(objL+penPntL3, objT+penPntT3);
				path.lineTo(objL+penPntL4, objT+penPntT4);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// �ǂ̕`��
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
				
				//// �`��͈͎w�� �`canvas.restore();�܂�
				canvas.save();
				path.moveTo(objL, objT);
				path.lineTo(objL+hexPntL1, objT+hexPntT1);
				path.lineTo(objL+hexPntL2, objT+hexPntT2);
				path.lineTo(objL+hexPntL3, objT+hexPntT3);
				path.lineTo(objL+hexPntL4, objT+hexPntT4);
				path.lineTo(objL+hexPntL5, objT+hexPntT5);
				path.lineTo(objL, objT);
				
				canvas.clipPath(path);
					// �ǂ̕`��
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
				
				//// �`��͈͎w�� �`canvas.restore();�܂�
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
					// �ǂ̕`��
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
    // ���v�̎��Ԃ����������̕`��i�ǂ̕`��j
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
    // �t�F�[�h���� - ���v���^�b�`���̐����Ɏg�p
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
    // value�̒l��initial�̒l�ɉ����ĕύX���� - ���񏈗��Ɏg�p
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
    // Android�̏c�A���̏󋵂ɂ���Đ����o���ʒu�̕ύX
    // -------------------------------------------
    private void balloonInit(){
    	if(winVertical){
			// Android�c�������̐ݒ�
			balL = BALL_VERTI_INIT;
			balT = BALT_VERTI_INIT;
			balR = BALR_VERTI_INIT;
			balB = BALB_VERTI_INIT;
		}else{
			// Android���������̐ݒ�
			balL = BALL_HORI_INIT;
			balT = BALT_HORI_INIT;
			balR = BALR_HORI_INIT;
			balB = BALB_HORI_INIT;
		}
		
		balCnt = BALCNT_INIT;
    }
    
    // -------------------------------------------
    // �J�E���g�_�E���̎��Ԃ𑝌�������
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
    // �J�E���g�_�E���̎��ԑ������^�b�`���Ă���ԑ�������������
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
    // �w�莞�Ԑݒ�̎��Ԃ𑝌�������
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
    // �w�莞�Ԃ̎��ԑ������^�b�`���Ă���ԑ�������������
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
    // ���v�F�P or ���v�F�Q or �w�i�F�̐ݒ�
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
    // �n���ꂽPaint�Ƀ����_���ŐF��ݒ肷��
    // -------------------------------------------
    private void setRandomColor(Paint paint){
    	int rndR = (int)(Math.random()*256);
    	int rndG = (int)(Math.random()*256);
    	int rndB = (int)(Math.random()*256);
    	
    	paint.setARGB(255, rndR, rndG, rndB);
    }
    
    // -------------------------------------------
    // �A���[�����o�C�u���[�^���������܂�
    // -------------------------------------------
    private void alarmVibrat(){
    	// �����Đ�
    	if(alarmVibratFlg == ALARM_VIBRAT_ALARM || alarmVibratFlg == ALARM_VIBRAT_BOTH){
			try {
				mp.start();
			} catch (Exception e) {
				// ��O�͔������Ȃ�
			}
    	}
		
		// �o�C�u���[�V����
    	if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT || alarmVibratFlg == ALARM_VIBRAT_BOTH){
	        long[] pattern = {0, 2000, 1000, 2000, 1000, 2000}; // OFF/ON/OFF/ON...
	        vibrator.vibrate(pattern, -1);
    	}
    }
    // -------------------------------------------
    // ���̃A���[���܂ł̎��Ԃ��擾����
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
    		// iii�Ԗڂ̎w�莞�Ԃ�OFF�Ȃ玟�̎w�莞�Ԃɏ�����i�߂�
    		if(specificTimesOnFlg[iii] == false){
    			continue;
    		}
    		
    		weekDay = nWek;
    		for(int lll=0; lll < specificWeekdayOnFlg[iii].length; lll++){
    			// lll�����݌��肵�Ă���������傫���Ȃ����ꍇ�͎��̎w�莞�Ԃɐi�߂�
    			if(lll > nextAlarmDays){
    				break;
    			}
    			
    			if(specificWeekdayOnFlg[iii][weekDay]){
    				if(nowInMil > specificTimes[iii]){	// ���ݎ����̂ق��������̎���
    					int tmpNextD;
    					// ���̃A���[���܂ł̓���
    					if(lll == 0){
    						tmpNextD = 6;
    					}else{
    						tmpNextD = lll - 1;
    					}
    					
    					// ���݌��肵�Ă��鎞�Ԃ�菬�����ꍇ�̂ݏ���������
    					if(tmpNextD < nextAlarmDays || (tmpNextD == nextAlarmDays && t24InMil - nowInMil + specificTimes[iii] < nextAlarmTimes) || nextAlarmDays == 10){
	    					// ���̃A���[���܂ł̓���
	    					nextAlarmDays = tmpNextD;
	    					
	    					// ���̃A���[���܂ł̎���
	    					nextAlarmTimes = t24InMil - nowInMil + specificTimes[iii];
    					}
    				}else{
    					// ���݌��肵�Ă��鎞�Ԃ�菬�����ꍇ�̂ݏ���������
    					if(lll < nextAlarmDays || (lll == nextAlarmDays && specificTimes[iii] - nowInMil < nextAlarmTimes) || nextAlarmDays == 10){
	    					// ���̃A���[���܂ł̓���
	    					nextAlarmDays = lll;
	    					
	    					// ���̃A���[���܂ł̎���
	    					nextAlarmTimes = specificTimes[iii] - nowInMil - now.get(Calendar.ZONE_OFFSET) + 1000;
    					}
    				}
    			}
    			
    			// ���̗j���ɐi�߂�
    			if(weekDay == 6){
    				weekDay = 0;
    			}else{
    				weekDay++;
    			}
    		}
    	}
    	
    	// ���^�[���p�̕�����̍쐬 - ���̃A���[���܂ł̎���
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
    // �ݒ�t�@�C���ۑ�
    // -------------------------------------------
    private void settingWriter(){
    	int onlyHouMinF_Val;		// onlyHouMinF��boolean�l�𐮐��ɂ���ׂ̕ϐ�
    	int timeTxtNumFlg_Val;		// timeTxtNumFlg��boolean�l�𐮐��ɂ���ׂ̕ϐ�
    	String setting;				// �ݒ�t�@�C���ɕۑ����镶����
    	
    	// boolean�𐮐��ɂ���
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
    	
    	//// �ݒ�t�@�C���ɏ������ޕ�������쐬
    	// ���v(�b)�ؑ�
    	setting = String.valueOf(onlyHouMinF_Val);
    	
    	// �����\���ؑ�
    	setting += spliter + String.valueOf(timeTxtNumFlg_Val);
    	
    	// �����E�o�C�u�ؑ�
    	setting += spliter + String.valueOf(alarmVibratFlg);
    	
    	// ���v�`
    	setting += spliter + String.valueOf(timeFace);
    	
    	// ���v�F1
    	setting += spliter + String.valueOf(timeCol1[GRAD_RED_NUM]) + spliter + String.valueOf(timeCol1[GRAD_GREEN_NUM]) + spliter + String.valueOf(timeCol1[GRAD_BLUE_NUM]);
    	
    	// ���v�F2
    	setting += spliter + String.valueOf(timeCol2[GRAD_RED_NUM]) + spliter + String.valueOf(timeCol2[GRAD_GREEN_NUM]) + spliter + String.valueOf(timeCol2[GRAD_BLUE_NUM]);

    	// �w�i�F
    	setting += spliter + String.valueOf(bgCol[GRAD_RED_NUM]) + spliter + String.valueOf(bgCol[GRAD_GREEN_NUM]) + spliter + String.valueOf(bgCol[GRAD_BLUE_NUM]);
    	
    	// �ݒ�t�@�C���o��
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(settingFilePath, false)));
			bw.write(setting);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // �ݒ莞�ԃt�@�C���ۑ�
    // -------------------------------------------
    private void timeWriter(){
    	String time = "";				// �ݒ�t�@�C���ɕۑ����镶����
    	
    	// �J�E���g�_�E���ۑ�����
    	for(int iii = 0; iii < countDSetTimes.length; iii++){
    		if(iii != 0){
    			time += spliter;
    		}
    		time += countDSetTimes[iii];
    	}
    	
    	// �w�莞�ԁA�w�莞�Ԃ�ON�AOFF�A�w�莞�Ԃ̗j��
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
    	
    	// �ݒ�t�@�C���o��
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(timeFilePath, false)));
			bw.write(time);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // ���v�ʒu�̕ۑ�
    // -------------------------------------------
    private void positionWriter(){
    	String position;				// �ݒ�t�@�C���ɕۑ����镶����
    	
    	position = String.valueOf(houL) + spliter + String.valueOf(houT) + spliter + String.valueOf(minL) + spliter + String.valueOf(minT) + spliter + String.valueOf(secL) + spliter + String.valueOf(secT);
    	
    	// �ݒ�t�@�C���o��
    	try {
    		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(positionFilePath, false)));
			bw.write(position);
			bw.close();
		} catch (IOException e) {
		}
    }
    
    // -------------------------------------------
    // �ݒ�t�@�C���ǂݍ���
    // -------------------------------------------
    public void settingReader(){
    	File settingFile = new File(settingFilePath);	// �ݒ�t�@�C���̐ݒ�
    	String str = "";								// �ݒ�t�@�C���̓ǂݍ��ݗp
    	String[] settingS;								// split�ŕ�����ꂽ�ݒ�̒l���i�[����
    	long[] settingL = new long[settingNum];		// �����񂾂����ݒ�̒l��long�^�ŕێ�
    	int onlyHouMinF_Val = 0;						// onlyHouMinF�p�̒l�����Ԗڂɓ����Ă��邩
    	int timeTxtNumFlg_Val = 1;						// timeTxtNumFlg�p�̒l�����Ԗڂɓ����Ă��邩
    	int alarmVibratFlg_Val = 2;						// alarmVibratFlg�p�̒l�����Ԗڂɓ����Ă��邩
    	int timeFace_Val = 3;							// timeFace�p�̒l�����Ԗڂɓ����Ă��邩
    	int timeCol1_Val = 4;							// timeCol1�p�̒l�����Ԗڂɓ����Ă��邩
    	int timeCol2_Val = 7;							// timeCol2�p�̒l�����Ԗڂɓ����Ă��邩
    	int bgCol_Val = 10;								// bgCol�p�̒l�����Ԗڂɓ����Ă��邩
    	
        if(settingFile.exists()){
        	try {
        		// �ݒ�t�@�C���ǂݍ���
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
        
        // �ۑ�����Ă����ݒ�t�@�C���̕������؂蕪��
        settingS = str.split(spliter);
        
        // �ۑ�����Ă����ݒ�t�@�C���̒l�̐����m�F
        if(settingS.length != settingNum){
        	return;
        }
        
        // �ۑ�����Ă����ݒ�t�@�C���̒l�����l�ɕύX�ł��邩���`�F�b�N
        for(int iii=0; iii < settingS.length; iii++){
        	if(numberFormatCheck_long(settingS[iii]) == false){
        		return;
        	}
        	settingL[iii] = Long.parseLong(settingS[iii]);
        }
        
        //// �ۑ�����Ă����l���e�ϐ��ɃZ�b�g
        // ���v(�b)�ؑ�
        if(settingL[onlyHouMinF_Val] == 0){
        	onlyHouMinF = false;
        	onlyHouMinBtnTxt = HMS_TEXT;
        }else{
        	onlyHouMinF = true;
        	onlyHouMinBtnTxt = HM_TEXT;
        }
        
        // �����\���ؑ�
        if(settingL[timeTxtNumFlg_Val] == 0){
        	timeTxtNumFlg = false;
        	timeTxtBtnTxt = KANSUUJI_TEXT;
        }else{
        	timeTxtNumFlg = true;
        	timeTxtBtnTxt = ARABIC_TEXT;
        }
        
        // �����E�o�C�u�ؑ�
        alarmVibratFlg = (int)settingL[alarmVibratFlg_Val];
        if(alarmVibratFlg == ALARM_VIBRAT_ALARM){
        	alarmVibratBtnTxt = ALARM_TEXT;
        }else if(alarmVibratFlg == ALARM_VIBRAT_VIBRAT){
        	alarmVibratBtnTxt = VIBRATE_TEXT;
        }else{
        	alarmVibratBtnTxt = BOTH_TEXT;
        }
        
        // ���v�`
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
        
        // ���v�F1
        timeCol1[GRAD_RED_NUM] = (int)settingL[timeCol1_Val];
        timeCol1[GRAD_GREEN_NUM] = (int)settingL[timeCol1_Val+1];
        timeCol1[GRAD_BLUE_NUM] = (int)settingL[timeCol1_Val+2];
        
        // ���v�F2
        timeCol2[GRAD_RED_NUM] = (int)settingL[timeCol2_Val];
        timeCol2[GRAD_GREEN_NUM] = (int)settingL[timeCol2_Val+1];
        timeCol2[GRAD_BLUE_NUM] = (int)settingL[timeCol2_Val+2];

        // �w�i�F
        bgCol[GRAD_RED_NUM] = (int)settingL[bgCol_Val];
        bgCol[GRAD_GREEN_NUM] = (int)settingL[bgCol_Val+1];
        bgCol[GRAD_BLUE_NUM] = (int)settingL[bgCol_Val+2];
    }
    
    // -------------------------------------------
    // ������long�^�ɕύX�ł��邩���`�F�b�N
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
    // �ݒ莞�ԃt�@�C���ǂݍ���
    // -------------------------------------------
    public void timeReader(){
    	File settingFile = new File(timeFilePath);	// �ݒ�t�@�C���̐ݒ�
    	String str = "";								// �ݒ�t�@�C���̓ǂݍ��ݗp
    	String[] timeS;									// split�ŕ�����ꂽ�ݒ�̒l���i�[����
    	long[] settingL = new long[timeNum];			// �����񂾂����ݒ�̒l��long�^�ŕێ�
    	int countDSetTimes_Val = 0;						// countDSetTimes�p�̒l�����Ԗڂ�������Ă��邩
    	int specificTimes_Val = 3;						// specificTimes�p�̒l�����Ԗڂ�������Ă��邩
    	
        if(settingFile.exists()){
        	try {
        		// �ݒ�t�@�C���ǂݍ���
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
        
        // �ۑ�����Ă����ݒ�t�@�C���̕������؂蕪��
        timeS = str.split(spliter);
        
        // �ۑ�����Ă����ݒ�t�@�C���̒l�̐����m�F
        if(timeS.length != timeNum){
        	return;
        }
        
        // �ۑ�����Ă����ݒ�t�@�C���̒l�����l�ɕύX�ł��邩���`�F�b�N
        for(int iii=0; iii < timeS.length; iii++){
        	if(numberFormatCheck_long(timeS[iii]) == false){
        		return;
        	}
        	settingL[iii] = Long.parseLong(timeS[iii]);
        }
        
        // �J�E���g�_�E���ۑ�����
        for(int iii = 0; iii < countDSetTimes.length; iii++){
        	countDSetTimes[iii] = settingL[countDSetTimes_Val + iii];
        }
        
        for(int iii = 0; iii < specificTimes.length; iii++){
        	// �w�莞��
        	specificTimes[iii] = settingL[specificTimes_Val + iii*9];
        	
        	// �w�莞�Ԃ�ON�AOFF
        	if(settingL[specificTimes_Val + iii * 9 + 1] == 0){
        		specificTimesOnFlg[iii] = false;
        	}else{
        		specificTimesOnFlg[iii] = true;
        	}
        	
        	// �w�莞�Ԃ̗j��
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
    // ���v�ʒu�t�@�C���ǂݍ���
    // -------------------------------------------
    public void positionReader(){
    	File positionFile = new File(settingFilePath);	// ���v�ʒu�t�@�C���ݒ�
    	String str = "";						// ���v�ʒu�t�@�C���̓ǂݍ��ݗp
    	String[] positionS;								// split�ŕ�����ꂽ���v�ʒu�̒l���i�[����
    	int[] positionI = new int[settingNum];			// �����񂾂������v�ʒu�̒l��int�^�ŕێ�

        // ���v�ʒu�t�@�C���ǂݍ���
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
    	
        // �ۑ�����Ă������v�ʒu�t�@�C���̕������؂蕪��
        positionS = str.split(spliter);
        
        // �ۑ�����Ă������v�ʒu�t�@�C���̒l�̐����m�F
        if(positionS.length != positionNum){
        	return;
        }

        // �ۑ�����Ă������v�ʒu�t�@�C���̒l�����l�ɕύX�ł��邩���`�F�b�N
        for(int iii=0; iii < positionS.length; iii++){
        	if(numberFormatCheck_int(positionS[iii]) == false){
        		return;
        	}
        	positionI[iii] = Integer.parseInt(positionS[iii]);
        }
        
        // ���v�ʒu�̒l
        houL = positionI[0];
        houT = positionI[1];
        minL = positionI[2];
        minT = positionI[3];
        secL = positionI[4];
        secT = positionI[5];
    }
    
    // -------------------------------------------
    // ������int�^�ɕύX�ł��邩���`�F�b�N
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