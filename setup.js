// "npx cap add android" sonrası çalışır: NFC eklentisini ve izinleri Android projesine ekler.
const fs = require('fs');
const path = require('path');
const pkg = 'com.ceppos.oyun';
const main = path.join('android', 'app', 'src', 'main');
if (!fs.existsSync(main)) {
  console.error('HATA: android klasörü yok. Önce "npx cap add android" çalıştır.');
  process.exit(1);
}
const jdir = path.join(main, 'java', ...pkg.split('.'));
fs.mkdirSync(jdir, { recursive: true });
['NfcPlugin.java', 'MainActivity.java'].forEach(f =>
  fs.copyFileSync(path.join('native', f), path.join(jdir, f)));
const mf = path.join(main, 'AndroidManifest.xml');
let x = fs.readFileSync(mf, 'utf8');
if (!x.includes('android.permission.NFC')) {
  x = x.replace('<application',
    '<uses-permission android:name="android.permission.NFC" />\n    <uses-permission android:name="android.permission.VIBRATE" />\n    <uses-feature android:name="android.hardware.nfc" android:required="false" />\n\n    <application');
  if (!x.includes('screenOrientation')) x = x.replace('<activity', '<activity\n            android:screenOrientation="portrait"');
  fs.writeFileSync(mf, x);
}
console.log('Tamam: NFC eklentisi ve izinler eklendi.');
