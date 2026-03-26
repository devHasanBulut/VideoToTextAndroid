package com.duhan.videototext.Data.LocalDataSource

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [SummaryTextModel::class,
    SearchHistoryModel::class, CategoriesModel::class,
    SummaryForCategories::class],
    version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun summaryTextModelDao(): SummaryTextModelDao
    abstract fun searchHistoryDao(): SearchHistoryModelDao
    abstract fun categoriesDao(): CategoriesDao
}

//arama geçmişinden herhangi bir geçmişi sildiğimde diğer geçmişteki verilerin id'si güncellenmiyor
//tabloları birleştir


//url 'yi al. Youtube'dan vidoyu bul. Ekranda video başlağı ve kapak fotosunu göster
//kapak fotosunun altında bunu mu indirmek istiyon gibisinden sor
//kulanıcı download'a basarsa indirmeye başla
//indirilen metni downloads ekranında göster
//uygulama açıldığı gibi url bar'a focus olması lazım(yani direkt klavye açılacak)
//downloads yerine kaydedilenler kullan
//download tusu yerine özetle olacak
//arama geçmişi ekranı ful kaplasın(youtube gibi)
//kategori butonlarını card ile değiştir bunları straggered grid yap
//theming in compose material 3
//m3 material io
//favrites ve downloads ekranlarındaki verileri liste şeklinde göster card değil
//(youtube geçmişi gibi)
//renkleri değiştir gradient'i kaldır
//account settings'i kaldır gereksiz
//drawe menuyu ucur. Ordaki ayarlar kısmını al ekranın sağ üstünde ıcon button altında göster
//kategori eklemeyi son karta ekle
//text speech ekle