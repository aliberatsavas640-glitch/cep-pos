package com.ceppos.oyun;

import android.app.Activity;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@CapacitorPlugin(name = "Nfc")
public class NfcPlugin extends Plugin {
  private NfcAdapter adapter;
  private volatile Tag lastTag;

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
      lastTag = null;
      call.resolve();
    });
  }

  @PluginMethod
  public void write(final PluginCall call) {
    final Integer bal = call.getInt("balance");
    final Tag tag = lastTag;
    if (bal == null || tag == null) { call.reject("KART_YOK"); return; }
    new Thread(() -> {
      try {
        NdefMessage msg = new NdefMessage(textRecord("CEPPOS:" + bal));
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
          ndef.connect();
          try {
            if (!ndef.isWritable()) throw new Exception("YAZILAMAZ");
            ndef.writeNdefMessage(msg);
          } finally { try { ndef.close(); } catch (Exception ignore) { } }
        } else {
          NdefFormatable f = NdefFormatable.get(tag);
          if (f == null) throw new Exception("DESTEKLENMIYOR");
          f.connect();
          try { f.format(msg); } finally { try { f.close(); } catch (Exception ignore) { } }
        }
        call.resolve();
      } catch (Exception e) {
        call.reject(e.getMessage() == null ? "YAZMA_HATASI" : e.getMessage());
      }
    }).start();
  }

  private void onTag(Tag tag) {
    lastTag = tag;
    boolean found = false;
    int bal = 0;
    Ndef ndef = Ndef.get(tag);
    if (ndef != null) {
      try {
        ndef.connect();
        NdefMessage m = ndef.getNdefMessage();
        if (m != null) {
          for (NdefRecord rec : m.getRecords()) {
            if (rec.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(rec.getType(), NdefRecord.RTD_TEXT)) {
              byte[] p = rec.getPayload();
              if (p.length > 0) {
                int off = 1 + (p[0] & 0x3F);
                if (off <= p.length) {
                  String txt = new String(p, off, p.length - off, StandardCharsets.UTF_8);
                  if (txt.startsWith("CEPPOS:")) {
                    try { bal = Integer.parseInt(txt.substring(7).trim()); found = true; } catch (Exception ignore) { }
                  }
                }
              }
            }
          }
        }
      } catch (Exception ignore) {
      } finally {
        try { ndef.close(); } catch (Exception ignore) { }
      }
    }
    JSObject r = new JSObject();
    r.put("found", found);
    r.put("balance", bal);
    notifyListeners("tag", r);
  }

  private NdefRecord textRecord(String text) {
    byte[] lang = "tr".getBytes(StandardCharsets.US_ASCII);
    byte[] t = text.getBytes(StandardCharsets.UTF_8);
    byte[] p = new byte[1 + lang.length + t.length];
    p[0] = (byte) lang.length;
    System.arraycopy(lang, 0, p, 1, lang.length);
    System.arraycopy(t, 0, p, 1 + lang.length, t.length);
    return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], p);
  }
}
