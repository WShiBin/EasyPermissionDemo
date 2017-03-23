# EasyPermissionDemo

> 方便开发者申请App权限

项目地址:https://github.com/googlesamples/easypermissions

##情况是这样的
试想一下,一个App在未经允许的情况下,能获取你的联系人资料,用相机给你拍照上传,这是多么危险的一件事情.支付宝就有这么一个操作,用前置相机悄悄的给你拍照上传.
Android在6.0更新时,加入了这样一个功能,对于手机的某些敏感资源,如相机,麦克风,联系人资料,电话,短信,相册...需要用户同意,App才能拥有使用相关资源的能力

###哪些是权限是需要申请的?
官方文档:https://developer.android.google.cn/guide/topics/security/permissions.html#normal-dangerous

| 权限组 | 权限 |
| --- | --- |
| CALENDAR(日历) | READ_CALENDAR / WRITE_CALENDAR |
| CAMERA(相机) | CAMERA |
| CONTACTS(联系人) | READ_CONTACTS / WRITE_CONTACTS / GET_ACCOUNTS |
| LOCATION(位置) | ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION |
| MICROPHONE(音频录制) | RECORD_AUDIO |
| PHONE(手机相关) | / READ_PHONE_STATE / CALL_PHONE / READ_CALL_LOG / WRITE_CALL_LOG / ADD_VOICEMAIL / USE_SIP / PROCESS_OUTGOING_CALLS |
| SENSORS(传感器) | BODY_SENSORS |
| SMS(短信) | SEND_SMS / RECEIVE_SMS / READ_SMS / RECEIVE_WAP_PUSH / RECEIVE_MMS |
| STORAGE(存储) | READ_EXTERNAL_STORAGE / WRITE_EXTERNAL_STORAGE |

###Note:
Android 6.0以前在Manifest.xml中去加权限,说明这个App有这个权限了,而现在,对于这些危险权限来说,在Manifest中只能算是声明,去告诉系统,这个App需要这个权限,但有没有这个权限,用户来决定.

###在Android 6.0以上未加入权限申请,去接使用功能会怎样?
会导致App直接崩溃,所以,时常会在App评论中看到:"太流氓了,不给权限就崩溃",类似的评论

##怎么用?
###1. 引入EasyPermission
```
dependencies {
compile 'pub.devrel:easypermissions:0.3.0'
}
```

###2. 重写Activity或Fragment中的onRequestPermissionsResult方法
并在其中调用EasyPermissions.onRequestPermissionsResult来请求回调,必需加
```
public class UseEasyPermissionMainActivity extends AppCompatActivity {

private static final String TAG = "UseEasyPermission";
private static final int RC_CAMERA_AND_RECORD_AUDIO = 10000;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
// Forward results to EasyPermissions
EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
}
}
```

###3. 实现EasyPermissions.PermissionCallbacks接口,实现两个方法:
* onPermissionsGranted权限申请成功的回调
* onPermissionsDenied权限申请拒绝的回调

实际情况是这样,如果我要用相机,首先要去判断有没有相机权限,有就直接开启相机,没有就去申请权限


```
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);
findViewById(R.id.btn_requst).setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
requestPermissions();
}
});
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
super.onRequestPermissionsResult(requestCode, permissions, grantResults);
// Forward results to EasyPermissions
EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
}

/**
* 去申请权限
*/
private void requestPermissions() {
String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

//判断有没有权限
if (EasyPermissions.hasPermissions(this, perms)) {
// 如果有权限了, 就做你该做的事情
openCamera();
} else {
// 如果没有权限, 就去申请权限
// this: 上下文
// Dialog显示的正文
// RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
// perms 就是你要申请的权限
EasyPermissions.requestPermissions(this, "写上你需要用权限的理由, 是给用户看的", RC_CAMERA_AND_RECORD_AUDIO, perms);
}
}
/**
* 权限申请成功的回调
*
* @param requestCode 申请权限时的请求码
* @param perms 申请成功的权限集合
*/
@Override
public void onPermissionsGranted(int requestCode, List<String> perms) {
Log.i(TAG, "onPermissionsGranted: ");
openCamera();
}

/**
* 权限申请拒绝的回调
*
* @param requestCode 申请权限时的请求码
* @param perms 申请拒绝的权限集合
*/
@Override
public void onPermissionsDenied(int requestCode, List<String> perms) {
Log.i(TAG, "onPermissionsDenied: ");
}
```
这时候可以把打开相机调用的代码放在onPermissionsGranted中,如果你同时申请了多个权限,也可以在回调中判断做相应的操作:

```
/**
* 权限申请成功的回调
*
* @param requestCode 申请权限时的请求码
* @param perms 申请成功的权限集合
*/
@Override
public void onPermissionsGranted(int requestCode, List<String> perms) {
Log.i(TAG, "onPermissionsGranted: ");
if (requestCode != RC_CAMERA_AND_RECORD_AUDIO) {
return;
}
for (int i = 0; i < perms.size(); i++) {
if (perms.get(i).equals(Manifest.permission.CAMERA)) {
Log.i(TAG, "onPermissionsGranted: " + "相机权限成功");
openCamera();

} else if (perms.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
Log.i(TAG, "onPermissionsGranted: " + "录制音频权限成功");
}
}
}

/**
* 权限申请拒绝的回调
*
* @param requestCode 申请权限时的请求码
* @param perms 申请拒绝的权限集合
*/
@Override
public void onPermissionsDenied(int requestCode, List<String> perms) {
Log.i(TAG, "onPermissionsDenied: ");

if (requestCode != RC_CAMERA_AND_RECORD_AUDIO) {
return;
}

for (int i = 0; i < perms.size(); i++) {
if (perms.get(i).equals(Manifest.permission.CAMERA)) {
Log.i(TAG, "onPermissionsDenied: " + "相机权限拒绝");
} else if (perms.get(i).equals(Manifest.permission.RECORD_AUDIO)) {
Log.i(TAG, "onPermissionsDenied: " + "录制音频权限拒绝");
}
}
}
}
```

还有一个问题是:如果权限申请对话一直弹,用户也觉得烦,这时候Android在对话框上加了一个不在询问的勾选框,这时候在怎么requestPermission都不会弹出那个让用户选择允许或者拒绝的对话框了.App还需要使用相机的权限只能去:设置->应用->当前应用->权限,里面去开启权限,这时候可以在申请拒绝的回调onPermissionsDenied中做相应的引导操作:转跳到设置页面去手动开启权限.


```
/**
* 权限申请拒绝的回调
*
* @param requestCode 申请权限时的请求码
* @param perms 申请拒绝的权限集合
*/
@Override
public void onPermissionsDenied(int requestCode, List<String> perms) {
Log.i(TAG, "onPermissionsDenied: ");
//如果有一些权限被永久的拒绝, 就需要转跳到 设置-->应用-->对应的App下去开启权限
if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
new AppSettingsDialog.Builder(this)
.setTitle("权限已经被您拒绝")
.setRationale("如果不打开权限则无法使用该功能,点击确定去打开权限")
.setRequestCode(10001)//用于onActivityResult回调做其它对应相关的操作
.build()
.show();
}
}
```

最后,从设置页面转跳回来也可以做一些相应的操作
```
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
super.onActivityResult(requestCode, resultCode, data);
if (requestCode == 10001) {
Toast.makeText(this, " 从开启权限的页面转跳回来 ", Toast.LENGTH_SHORT).show();
}
}
```

###所有流程图大致如下
![](/assets/EasyPermission.png)

###最后EasyPermission还提供了一个可供选择的注解:AfterPermissionGranted
方法注解,注解中参数是申请权限的请求码.
被@AfterPermissionGranted注解的方法会在请求码中的所有权限申请成功之后被调用

```
/**
* 去申请权限
*/
@AfterPermissionGranted(RC_CAMERA_AND_RECORD_AUDIO)
private void requestPermissions() {
String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

//判断有没有权限
if (EasyPermissions.hasPermissions(this, perms)) {
// 如果有权限了, 就做你该做的事情
// doing something
openCamera();
} else {
// 如果没有权限, 就去申请权限
// this: 上下文
// Dialog显示的正文
// RC_CAMERA_AND_RECORD_AUDIO 请求码, 用于回调的时候判断是哪次申请
// perms 就是你要申请的权限
EasyPermissions.requestPermissions(this, "写上你需要用权限的理由, 是给用户看的", RC_CAMERA_AND_RECORD_AUDIO, perms);
}
}
```
