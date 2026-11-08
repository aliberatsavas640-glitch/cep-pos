# Cep POS – APK yapma rehberi

Gerekenler (bir kez kurulur):
1. Node.js (LTS): https://nodejs.org
2. Android Studio: https://developer.android.com/studio (kurulumda Android SDK da gelir)

## Adımlar (bu klasörde terminal/komut satırı aç)

    npm run kur

Bu komut bağımlılıkları indirir, Android projesini oluşturur ve NFC eklentisini ekler.
Hata vermezse sonraki adıma geç:

    npm run ac

Android Studio açılır. İlk açılışta Gradle işini bitirmesini bekle (alt çubuk), sonra:

**Build → Build Bundle(s) / APK(s) → Build APK(s)**

APK burada oluşur:
`android/app/build/outputs/apk/debug/app-debug.apk`

Dosyayı telefona at, aç, "bilinmeyen kaynaklardan yükleme" iznini ver ve kur.
(Telefonu USB ile bağlayıp Android Studio'daki ▶ Run ile de doğrudan kurabilirsin.)

## Uygulamada değişiklik yaparsan
`www/index.html` dosyasını düzenle, sonra `npm run guncelle` ve Android Studio'dan tekrar APK al.

## NFC kullanımı
- Telefonda NFC açık olmalı.
- Boş, yazılabilir NFC etiketi/kartı (NTAG213/215/216) kullan. Banka ve ulaşım kartları çalışmaz.
- Önce Kart yükle → NFC kartına yükle ile bakiye yaz.
- Ödemede kartı telefonun arkasında yaklaşık 2 saniye sabit tut (bakiye karta geri yazılıyor).

Bu uygulama oyun amaçlıdır, gerçek bankacılık işlemi yapmaz.

---

# Bilgisayarın zayıfsa: GitHub ile bulutta derle (Android Studio gerekmez)

1. github.com'da ücretsiz hesap aç, sağ üstte **+ → New repository** de. İsim: `cep-pos`, **Public** seç, **Create repository**'ye bas.
2. Açılan sayfada **uploading an existing file** yazısına tıkla.
3. Bu klasörün İÇİNDEKİ her şeyi (`.github`, `www`, `native` klasörleri ve diğer dosyalar) tarayıcıya sürükle-bırak yap. Alttan **Commit changes**'e bas.
4. `.github` klasörü yüklenmediyse: **Add file → Create new file** de, isim kutusuna `.github/workflows/apk.yml` yaz, bu klasördeki `apk.yml` içeriğini yapıştır, **Commit changes**.
5. Üstteki **Actions** sekmesine gir. "APK Derle" çalışmaya başlar (5-10 dk). Yeşil tik olunca o çalışmaya tıkla.
6. Sayfanın altındaki **Artifacts** bölümünden **cep-pos-apk**'ya tıkla, zip iner. İçinden `app-debug.apk` çıkar.
7. APK'yı telefona atıp kur.

Kırmızı çarpı çıkarsa çalışmaya tıkla, hata yazısını kopyalayıp Claude'a gönder.
