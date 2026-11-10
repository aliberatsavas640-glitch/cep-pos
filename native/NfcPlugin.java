package com.ceppos.oyun;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Nfc")
public class NfcPlugin extends Plugin {
  private NfcAdapter adapter;

  @PluginMethod
  public void start(final PluginCall call) {
    final Activity act = getActivity();
    adapter = NfcAdapter.getDefaultAdapter(act);
    if (adapter == null) { call.reject("NFC_YOK"); return; }
    if (!adapter.isEnabled()) { call.reject("NFC_KAPALI"); return; }
    final int flags = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B
        | NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V
        | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    act.runOnUiThread(() -> {
      try {
        adapter.enableReaderMode(act, tag -> onTag(tag), flags, null);
        call.resolve();
      } catch (Exception e) {
        call.reject("NFC_BASLATILAMADI");
      }
    });
  }

  @PluginMethod
  public void stop(final PluginCall call) {
    final Activity act = getActivity();
    act.runOnUiThread(() -> {
      try { if (adapter != null) adapter.disableReaderMode(act); } catch (Exception e) { }
      call.resolve();
    });
  }

  // Her NFC kartının seri numarasını (UID) uygulamaya gönderir. Karta yazma yapılmaz.
  private void onTag(Tag tag) {
    byte[] id = tag.getId();
    StringBuilder sb = new StringBuilder();
    if (id != null) {
      for (byte b : id) sb.append(String.format("%02X", b));
    }
    JSObject r = new JSObject();
    r.put("id", sb.toString());
    notifyListeners("tag", r);
  }
}
