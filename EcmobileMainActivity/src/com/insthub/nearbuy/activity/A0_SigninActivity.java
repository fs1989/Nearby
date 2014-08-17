package com.insthub.nearbuy.activity;

//
//                       __
//                      /\ \   _
//    ____    ____   ___\ \ \_/ \           _____    ___     ___
//   / _  \  / __ \ / __ \ \    <     __   /\__  \  / __ \  / __ \
//  /\ \_\ \/\  __//\  __/\ \ \\ \   /\_\  \/_/  / /\ \_\ \/\ \_\ \
//  \ \____ \ \____\ \____\\ \_\\_\  \/_/   /\____\\ \____/\ \____/
//   \/____\ \/____/\/____/ \/_//_/         \/____/ \/___/  \/___/
//     /\____/
//     \/___/
//
//  Powered by BeeFramework
//

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.external.androidquery.callback.AjaxStatus;
import com.insthub.BeeFramework.activity.BaseActivity;
import com.insthub.BeeFramework.model.BusinessResponse;
import com.insthub.BeeFramework.view.ToastView;
import com.insthub.nearbuy.R;
import com.insthub.nearbuy.fragment.E0_ProfileFragment;
import com.insthub.nearbuy.model.LoginModel;
import com.insthub.nearbuy.model.ProtocolConst;
import com.insthub.nearbuy.protocol.ApiInterface;

import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;

public class A0_SigninActivity extends BaseActivity implements OnClickListener,
		BusinessResponse {

	private CheckBox checkBox1;
	static String YES = "yes";
	static String NO = "no";
	static String nm, passwd;

	private ImageView back;
	private Button login;

	private EditText userName;
	private EditText password;
	private TextView register;

	private String name;
	private String psd;

	private LoginModel loginModel;
	private final static int REQUEST_SIGN_UP = 1;

	private String isMemory = "";// isMemory变量用来判断SharedPreferences有没有数据，包括上面的YES和NO
	private String FILE = "saveUserNamePwd";// 用于保存SharedPreferences的文件
	private SharedPreferences sp = null;// 声明一个SharedPreferences

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a0_signin);

		back = (ImageView) findViewById(R.id.login_back);
		login = (Button) findViewById(R.id.login_login);
		userName = (EditText) findViewById(R.id.login_name);
		password = (EditText) findViewById(R.id.login_password);
		register = (TextView) findViewById(R.id.login_register);
		register.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		back.setOnClickListener(this);
		login.setOnClickListener(this);
		register.setOnClickListener(this);

		checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
		sp = getSharedPreferences(FILE, MODE_PRIVATE);
		isMemory = sp.getString("isMemory", NO);
		// 进入界面时，这个if用来判断SharedPreferences里面name和password有没有数据，有的话则直接打在EditText上面
		if (isMemory.equals(YES)) {
			name = sp.getString("name", "");
			psd = sp.getString("psd", "");
			userName.setText(name);
			password.setText(psd);
		}
		Editor editor = sp.edit();
		editor.putString(name, userName.toString());
		editor.putString(psd, password.toString());
		editor.commit();

	}

	@Override
	public void onClick(View v) {
		Resources resource = (Resources) getBaseContext().getResources();
		String usern = resource.getString(R.string.user_name_cannot_be_empty);
		String pass = resource.getString(R.string.password_cannot_be_empty);
		Intent intent;
		switch (v.getId()) {
		case R.id.login_back:
			finish();
			CloseKeyBoard();
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;
		case R.id.login_login:
			name = userName.getText().toString();
			psd = password.getText().toString();
			if (name.length() < 2) {
				ToastView toast = new ToastView(this,
						resource.getString(R.string.username_too_short));
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			if (name.length() > 20) {
				ToastView toast = new ToastView(this,
						resource.getString(R.string.username_too_long));
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			if (psd.length() < 6) {
				ToastView toast = new ToastView(this,
						resource.getString(R.string.password_too_short));
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			if (psd.length() > 20) {
				ToastView toast = new ToastView(this,
						resource.getString(R.string.password_too_long));
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			if ("".equals(name)) {
				ToastView toast = new ToastView(this, usern);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else if ("".equals(psd)) {
				ToastView toast = new ToastView(this, pass);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else {
				loginModel = new LoginModel(A0_SigninActivity.this);
				loginModel.addResponseListener(this);
				loginModel.login(name, psd);
				remenber();
				CloseKeyBoard();

			}
			break;
		case R.id.login_register:
			intent = new Intent(this, A1_SignupActivity.class);
			startActivityForResult(intent, REQUEST_SIGN_UP);
			break;
		}

	}

	@Override
	public void OnMessageResponse(String url, JSONObject jo, AjaxStatus status)
			throws JSONException {
		if (loginModel.responseStatus.succeed == 1) {
			if (url.endsWith(ApiInterface.USER_SIGNIN)) {
				Intent intent = new Intent();
				intent.putExtra("login", true);
				setResult(Activity.RESULT_OK, intent);
				finish();
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_SIGN_UP) {
			if (data != null) {
				Intent intent = new Intent();
				intent.putExtra("login", true);
				setResult(Activity.RESULT_OK, intent);
				finish();
				E0_ProfileFragment.isRefresh = true;
				overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			}
		}

	}

	// remenber方法用于判断是否记住密码，checkBox1选中时，提取出EditText里面的内容，放到SharedPreferences里面的name和password中
	public void remenber() {
		if (checkBox1.isChecked()) {

			if (sp == null) {
				sp = getSharedPreferences(FILE, MODE_PRIVATE);
			}

			Editor edit = sp.edit();
			edit.putString("name", userName.getText().toString());
			edit.putString("psd", password.getText().toString());
			edit.putString("isMemory", YES);
			edit.commit();
		}

		else if (!checkBox1.isChecked()) {

			if (sp == null) {
				sp = getSharedPreferences(FILE, MODE_PRIVATE);
			}
			Editor edit = sp.edit();
			edit.putString("isMemory", NO);
			edit.commit();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}
		return true;
	}

	// 关闭键盘
	public void CloseKeyBoard() {
		userName.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(userName.getWindowToken(), 0);
	}
}
