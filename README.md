# Ezan Vakti

Namaz vakitlerini gösteren, Diyanet hesaplama yöntemine yakın (Aladhan API, method=13)
bir Android uygulaması. Kotlin + MVVM mimarisiyle yazılmıştır.

## Özellikler (bu sürümde)
- ✅ Tam vaktinde ezan bildirimi (AlarmManager)
- ✅ Bildirim & titreşim ayarları
- ✅ İnternetsiz çalışma (ilk senkronizasyondan sonra ay boyunca önbellekten okunur)
- ✅ Otomatik / manuel konum (FusedLocationProviderClient + Geocoder)
- ✅ Karanlık yeşil-altın tema
- ✅ Kıble pusulası (gerçek sensör verisiyle)
- ✅ Ramazan sayacı
- ✅ Aylık vakitler tablosu
- ❌ Widget desteği (bu sürümde yok)
- ❌ Wear OS / Akıllı saat desteği (bu sürümde yok)

## GitHub Actions ile APK üretme
Bu repoda `.github/workflows/build-apk.yml` dosyası hazır. Depoyu GitHub'a
yükleyip `main` dalına push yaptığınızda (ya da Actions sekmesinden elle
tetiklediğinizde) otomatik olarak `assembleDebug` çalışır ve üretilen
**app-debug.apk** dosyasını Actions çalıştırmasının "Artifacts" bölümünden
indirebilirsiniz.

Adımlar:
1. Bu klasördeki tüm dosyaları yeni bir GitHub reposuna yükleyin (push edin).
2. Repo sayfasında **Actions** sekmesine gidin.
3. "Build APK" workflow'unun tamamlanmasını bekleyin (birkaç dakika sürer).
4. Çalıştırma sonucunun altındaki **Artifacts** bölümünden
   `ezan-vakti-debug-apk` dosyasını indirin, içinden `app-debug.apk` çıkar.
5. Bu APK'yı Android cihazınıza kopyalayıp kurun (bilinmeyen kaynaklardan
   yükleme izni gerekebilir).

> Not: Bu workflow **debug** APK üretir (imzasız/test amaçlı). Play Store'a
> yüklemek için release imzalı bir APK/AAB gerekir; isterseniz bunun için
> ayrıca bir keystore oluşturup workflow'u genişletebiliriz.

## Yerelde Android Studio ile açma
1. Android Studio (Hedgehog veya üstü) ile bu klasörü **Open** edin.
2. Gradle sync otomatik başlar (gradle wrapper eksikse Studio sizden
   "gradle wrapper oluştur" onayı isteyebilir, kabul edin).
3. `Run 'app'` ile bir emülatör veya gerçek cihazda çalıştırın.

## Kullanılan teknolojiler
- Kotlin, ViewBinding, Navigation Component
- Retrofit + Gson (Aladhan API)
- Coroutines
- FusedLocationProviderClient (Google Play Services)
- SharedPreferences tabanlı önbellek/ayarlar
- AlarmManager + BroadcastReceiver (ezan bildirimleri)
- SensorManager (kıble pusulası)

## Paket adı
`com.ezanvakti.app` — yayınlamadan önce kendi paket adınızla değiştirmeniz
önerilir (`app/build.gradle.kts` içindeki `namespace` ve `applicationId`,
ayrıca `AndroidManifest.xml`).
