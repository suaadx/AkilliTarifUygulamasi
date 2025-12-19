package com.example.akillitarifuygulamasi
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.akillitarifuygulamasi.R
import com.example.akillitarifuygulamasi.ui.RecipeAdapter
import com.example.akillitarifuygulamasi.RecipeModel
import com.example.akillitarifuygulamasi.RecipeRepository
import com.example.akillitarifuygulamasi.RecipeDetailActivity

object RecipeRepository {
    val allRecipes = listOf(
        RecipeModel("Yulaf Ezmesi", R.drawable.yulaf_ezmesi, 4.6f, "• Yulaf\n• Süt\n• Tarçın",
            "Yulafı sütle birlikte karıştır. Kısık ateşte karıştırarak pişir. Üzerine tarçın ekle.", 298, "kahvalti"),

        RecipeModel("Omlet", R.drawable.omlet, 4.7f, "• Yumurta\n• Süt\n• Baharat",
            "Yumurtaları süt ile çırp. Tavada pişirirken baharat ekle.", 151, "kahvalti"),

        RecipeModel("Sebzeli Menemen", R.drawable.sebzeli_menemen, 4.9f, "• Domates\n• Biber\n• Yumurta",
            "Doğranmış sebzeleri tavada pişir. Yumurtayı ekleyip karıştırarak pişirmeye devam et.", 248, "kahvalti"),

        RecipeModel("Tam Buğday Tost", R.drawable.tam_bugday_tost, 4.8f, "• Tam buğday ekmeği\n• Peynir\n• Domates",
            "Ekmeğin arasına peynir ve dilimlenmiş domates koy. Tost makinesinde ısıt.", 264, "aksam"),

        RecipeModel("Mercimek Çorbası", R.drawable.mercimek_corbasi, 4.8f, "• Mercimek\n• Soğan\n• Havuç\n• Zeytinyağı",
            "Malzemeleri haşla ve blenderdan geçir. Zeytinyağını ekleyip bir taşım kaynat.", 280, "aksam"),

        RecipeModel("Sebzeli Bulgur", R.drawable.sebzeli_bulgur, 4.3f, "• Bulgur\n• Biber\n• Domates\n• Soğan",
            "Sebzeleri kavur. Bulguru ekleyip üzerini geçecek kadar su koy. Pişene kadar kapağı kapalı tut.", 223, "aksam"),

        RecipeModel("Izgara Tavuk", R.drawable.izgara_tavuk, 4.6f, "• Tavuk\n• Sebzeler\n• Zeytinyağı",
            "Tavukları zeytinyağı ve baharatlarla marine et. Izgarada veya fırında pişir. Yanına sebzeleri ekle.", 196, "aksam"),

        RecipeModel("Zeytinyağlı Taze Fasulye", R.drawable.zeytinyagli_taze_fasulye, 4.7f, "• Taze fasulye\n• Soğan\n• Zeytinyağı\n• Domates",
            "Fasulyeleri zeytinyağında soğan ve domatesle birlikte kısık ateşte pişir.", 261, "ogle"),

        RecipeModel("Fırında Balık", R.drawable.firinda_balik, 4.2f, "• Balık\n• Zeytinyağı\n• Limon\n• Baharat",
            "Balığı zeytinyağı, limon ve baharatlarla fırında pişir.", 289, "ogle"),

        RecipeModel("Şekersiz İrmik Tatlısı", R.drawable.sekersiz_irmik_tatlisi, 4.6f, "• İrmik\n• Süt\n• Stevia",
            "İrmik ve sütü kaynat. Stevia ekleyip karıştır. Kalıba dök ve soğumaya bırak.", 184, "tatli"),

        RecipeModel("Fırında Elma", R.drawable.firinda_elma, 4.4f, "• Elma\n• Tarçın\n• Stevia",
            "Elmaları ortadan kes. Üzerine tarçın ve Stevia serpip fırında pişir.", 270, "tatli"),

        RecipeModel("Yoğurtlu Meyve", R.drawable.yogurtlu_meyve, 4.5f, "• Yoğurt\n• Mevsim meyveleri\n• Stevia",
            "Meyveleri doğra. Yoğurtla karıştır. Üzerine Stevia serpiştir.", 287, "tatli"),


        // kalp

        RecipeModel("Sütlü Yulaf ve Kuruyemiş", R.drawable.oat_milk_nuts, 4.8f,
            "• Yulaf\n• Badem veya yulaf sütü\n• Chia tohumu\n• Çiğ kuruyemiş (ceviz, badem)\n• Muz veya yaban mersini",
            "Yulafı sütle pişir. Üzerine meyve ve kuruyemiş ekle.", 260, "kahvalti"),

        RecipeModel("Avokadolu Tam Tahıllı Tost", R.drawable.avocado_toast, 4.7f,
            "• Tam buğday ekmeği\n• Yarım avokado (ezilmiş)\n• Limon suyu\n• Zeytinyağı",
            "Ezilmiş avokadoyu ekmeğin üzerine sür, limon ve zeytinyağı ekle.", 235, "kahvalti"),

        RecipeModel("Yeşil Detoks Suyu ve Ekmek", R.drawable.green_juice_bread, 4.6f,
            "• Ispanak\n• Salatalık\n• Yeşil elma\n• Zencefil\n• Limon suyu\n• Tam tahıllı ekmek",
            "Malzemeleri karıştır, ekmekle birlikte servis et.", 220, "kahvalti"),

        RecipeModel("Izgara Balık ve Sebze", R.drawable.grilled_fish_veggies, 4.9f,
            "• Fileto balık\n• Zeytinyağı\n• Limon\n• Sarımsak\n• Karabiber\n• Izgara sebzeler (kabak, havuç, brokoli)",
            "Balığı marine edip ızgarada sebzelerle birlikte pişir.", 310, "ogle"),

        RecipeModel("Mercimek Salatası", R.drawable.lentil_salad, 4.7f,
            "• Haşlanmış mercimek\n• Domates\n• Taze soğan\n• Salatalık\n• Limon suyu\n• Zeytinyağı",
            "Malzemeleri karıştır, soğuk servis et.", 240, "ogle"),

        RecipeModel("Izgara Tavuk ve Esmer Pirinç", R.drawable.grilled_chicken_brownrice, 4.8f,
            "• Derisiz tavuk göğsü\n• Hafif baharat\n• Esmer pirinç\n• Yan salata",
            "Tavuğu ızgara yap, pilav ve salata ile servis et.", 295, "ogle"),

        RecipeModel("Kremasız Sebze Çorbası", R.drawable.vegetable_soup, 4.6f,
            "• Kabak\n• Havuç\n• Tatlı patates\n• Mercimek\n• Soğan\n• Sarımsak",
            "Tüm malzemeleri haşla ve blenderdan geçir.", 210, "aksam"),

        RecipeModel("Haşlanmış Yumurta ve Sebzeler", R.drawable.boiled_egg_veggies, 4.5f,
            "• Haşlanmış yumurta\n• Domates\n• Salatalık\n• Tam buğday ekmeği\n• Zeytinyağı\n• Kekik",
            "Malzemeleri birlikte servis et, üzerine kekik ve zeytinyağı ekle.", 235, "aksam"),

        RecipeModel("Sağlıklı Ton Balıklı Salata", R.drawable.healthy_tuna_salad, 4.6f,
            "• Su içinde ton balığı\n• Marul\n• Domates\n• Salatalık\n• Zeytinyağı\n• Limon",
            "Tüm malzemeleri karıştır ve servis et.", 250, "aksam"),

        RecipeModel("Meyveli Chia Puding", R.drawable.chia_pudding, 4.8f,
            "• Bitkisel süt\n• Chia tohumu\n• Taze meyve (çilek, yaban mersini)",
            "Chia tohumlarını sütle karıştır ve bir gece beklet. Meyve ile servis et.", 180, "tatli"),

        RecipeModel("Tarçınlı Fırın Elma", R.drawable.firinda_elma, 4.7f,
            "• Elma\n• Tarçın\n• (isteğe bağlı) bal",
            "Elmaları ikiye böl, üzerine tarçın serpip fırında pişir.", 170, "tatli"),

        RecipeModel("Yoğurtlu Kuruyemişli Tatlı", R.drawable.yogurt_nuts, 4.6f,
            "• Yağsız yoğurt\n• Ceviz veya badem\n• Tarçın",
            "Malzemeleri karıştır ve soğuk servis et.", 190, "tatli"),

        // نفس الوصفات لكن للحالة "tansiyon"
        RecipeModel("Sebzeli Tam Tahıllı Omlet", R.drawable.vegetable_omelet, 4.6f,
            "• Yumurta beyazı\n• Ispanak\n• Domates\n• Tam buğday ekmeği",
            "Sebzeleri az zeytinyağında sotele, yumurta beyazını ekleyerek omlet yap. Yanında ekmekle servis et.", 245, "kahvalti"),

        RecipeModel("Lor Peynirli Sandviç", R.drawable.lor_sandvic, 4.4f,
            "• Tam buğday ekmeği\n• Lor peyniri (tuzsuz)\n• Domates\n• Salatalık",
            "Ekmeğin arasına lor ve yeşillikleri yerleştir. Hafif bir kahvaltı olarak servis et.", 230, "kahvalti"),

        RecipeModel("Muzlu Keten Tohumlu Smoothie", R.drawable.banana_flax_smoothie, 4.5f,
            "• Muz\n• Keten tohumu\n• Yoğurt (yağsız)\n• Su",
            "Tüm malzemeleri blenderdan geçirip servis et.", 210, "kahvalti"),
        RecipeModel("Fırın Sebzeli Kinoa", R.drawable.baked_veggie_quinoa, 4.7f,
            "• Kinoa\n• Kabak\n• Patlıcan\n• Havuç\n• Zeytinyağı",
            "Sebzeleri fırında pişir, haşlanmış kinoayla karıştır.", 280, "ogle"),

        RecipeModel("Nohutlu Tahıllı Salata", R.drawable.chickpea_grain_salad, 4.8f,
            "• Haşlanmış nohut\n• Tam buğday bulguru\n• Domates\n• Limon suyu\n• Zeytinyağı\n• Salatalık",
            "Malzemeleri karıştırıp soğuk servis et.", 265, "ogle"),

        RecipeModel("Zeytinyağlı Kabak Dolması", R.drawable.zeytinyagli_kabak_dolma, 4.5f,
            "• Kabak\n• Esmer pirinç\n• Dereotu\n• Soğan\n• Zeytinyağı",
            "Kabakları doldurup zeytinyağında kısık ateşte pişir.", 290, "ogle"),

        RecipeModel("Buharda Somon ve Kuşkonmaz", R.drawable.steamed_salmon_asparagus, 4.9f,
            "• Somon\n• Kuşkonmaz\n• Limon\n• Zeytinyağı",
            "Somon ve kuşkonmazı buharda pişir, limon ve zeytinyağı ile servis et.", 275, "aksam"),

        RecipeModel("Fırın Mücver", R.drawable.baked_mucver, 4.6f,
            "• Kabak\n• Havuç\n• Yumurta\n• Tam buğday unu\n• Dereotu",
            "Malzemeleri karıştır, yağsız şekilde fırında pişir.", 240, "aksam"),

        RecipeModel("Kırmızı Mercimek Köftesi", R.drawable.lentil_kofte, 4.7f,
            "• Kırmızı mercimek\n• İnce bulgur\n• Soğan\n• Maydanoz\n• Baharat",
            "Mercimeği haşla, diğer malzemelerle yoğur. Şekil verip servis et.", 260, "aksam"),
        RecipeModel("Yulaflı Hurmalı Toplar", R.drawable.oat_date_balls, 4.8f,
            "• Yulaf\n• Hurma\n• Ceviz\n• Tarçın",
            "Malzemeleri rondodan geçir, yuvarlayıp buzdolabında dinlendir.", 190, "tatli"),

        RecipeModel("Kakaolu Avokado Mus", R.drawable.avocado_cocoa_mousse, 4.6f,
            "• Avokado\n• Kakao\n• Muz\n• Stevia (isteğe bağlı)",
            "Malzemeleri blenderdan geçir, soğuk servis et.", 200, "tatli"),

        RecipeModel("Tarçınlı Armut Kompostosu", R.drawable.pear_compote, 4.5f,
            "• Armut\n• Tarçın çubuğu\n• Limon suyu\n• Su",
            "Armutları haşla, tarçın ve limonla tatlandır.", 170, "tatli"),


        // وصفات الحالة "kolesterol"
        RecipeModel("Zeytinyağlı Enginar Ezmesi", R.drawable.enginar_ezmesi, 4.6f,
            "• Haşlanmış enginar\n• Limon suyu\n• Zeytinyağı\n• Dereotu",
            "Enginarı ezip limon ve zeytinyağıyla karıştır, üstüne dereotu serpip servis et.", 210, "kahvalti"),

        RecipeModel("Domatesli Menemen (Az Yağlı)", R.drawable.sebzeli_menemen, 4.5f,
            "• Domates\n• Biber\n• Yumurta beyazı\n• Zeytinyağı (çok az)",
            "Sebzeleri sotele, yumurta beyazı ekleyip karıştırarak pişir.", 220, "kahvalti"),

        RecipeModel("Tahinli Pekmezli Tam Buğday Ekmek", R.drawable.tahin_pekmez_ekmek, 4.7f,
            "• Tam buğday ekmeği\n• Tahin\n• Doğal üzüm pekmezi",
            "Ekmek üzerine tahin ve pekmez sür, doğal enerji kaynağı olarak tüket.", 250, "kahvalti"),
        RecipeModel("Zeytinyağlı Barbunya", R.drawable.zeytinyagli_barbunya, 4.8f,
            "• Barbunya\n• Havuç\n• Soğan\n• Zeytinyağı\n• Domates salçası",
            "Tüm malzemeleri birlikte kısık ateşte pişir. Soğuk servis et.", 280, "ogle"),

        RecipeModel("Sebzeli Bulgur Pilavı", R.drawable.sebzeli_bulgur_pilavi, 4.7f,
            "• Esmer bulgur\n• Domates\n• Biber\n• Soğan\n• Zeytinyağı",
            "Sebzeleri sotele, bulguru ekle ve üzerini geçecek kadar suyla pişir.", 275, "ogle"),

        RecipeModel("Taze Fasulye Yemeği", R.drawable.taze_fasulye, 4.6f,
            "• Taze fasulye\n• Domates\n• Soğan\n• Zeytinyağı",
            "Malzemeleri birlikte pişir, yanında yoğurt ile servis et.", 265, "ogle"),
        RecipeModel("Zeytinyağlı Ispanak", R.drawable.zeytinyagli_ispanak, 4.6f,
            "• Ispanak\n• Soğan\n• Zeytinyağı\n• Limon suyu",
            "Ispanakları soğanla kavur, zeytinyağı ve limonla tatlandırarak soğuk servis et.", 240, "aksam"),

        RecipeModel("Kabak Kalye", R.drawable.kabak_kalye, 4.5f,
            "• Kabak\n• Havuç\n• Sarımsak\n• Zeytinyağı\n• Dereotu",
            "Kabakları ve havuçları haşla, üzerine sarımsaklı sos ve dereotu ile servis et.", 230, "aksam"),

        RecipeModel("Mercimekli Pazı Sarma", R.drawable.mercimekli_pazi_sarma, 4.8f,
            "• Pazı yaprağı\n• Yeşil mercimek\n• Soğan\n• Baharatlar\n• Zeytinyağı",
            "İç harcı hazırlayıp pazılara sar, tencerede az suyla pişir.", 290, "aksam"),
        RecipeModel("İncir Uyutması", R.drawable.incir_uyutmasi, 4.7f,
            "• Kuru incir\n• Süt (yağsız)\n• Ceviz içi",
            "İncirleri küçük doğrayıp sütle kaynat, cevizle süsle.", 180, "tatli"),

        RecipeModel("Ayva Tatlısı (Şekersiz)", R.drawable.sekersiz_ayva_tatlisi, 4.6f,
            "• Ayva\n• Tarçın\n• Karanfil\n• Stevia (isteğe bağlı)",
            "Ayvaları fırınla, tarçın ve karanfil ile doğal aromalı tatlı yap.", 190, "tatli"),

        RecipeModel("Fırın Balkabağı", R.drawable.firin_balkabagi, 4.8f,
            "• Balkabağı\n• Tarçın\n• Ceviz\n• Stevia",
            "Balkabağını fırınla, üzerine tarçın ve ceviz ekle.", 200, "tatli"),

        RecipeModel("Kremalıii Tereyağlı Kızartma", R.drawable.firinda_elma, 4.5f,
            "• Kızartma\n• Derin yağ\n• Tereyağı\n• Margarin\n• tuz\n• Krema\n• Kaymak",
            "Kızartmaları derin yağda hazırla. Tereyağı, margarin ve kremayı ısıtıp kızartmalarla karıştır.", 650, "aksam"),

        )

    val favoriteRecipes = mutableListOf<RecipeModel>() // لإضافة مفضلات المستخدم
}
