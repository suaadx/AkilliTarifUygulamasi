package com.example.akillitarifuygulamasi.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.akillitarifuygulamasi.data.dao.*
import com.example.akillitarifuygulamasi.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        RecipeEntity::class,
        IngredientEntity::class,
        CommentEntity::class,
        FavoriteEntity::class,
        RatingEntity::class,
        UserActivityEntity::class,
        HealthStatusEntity::class
    ],
    version = 24,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun commentDao(): CommentDao
    abstract fun ratingDao(): RatingDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userActivityDao(): UserActivityDao
    abstract fun healthStatusDao(): HealthStatusDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "akilli_tarif_db"
                )
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { db ->
                        INSTANCE = db

                        CoroutineScope(Dispatchers.IO).launch {
                            seedHealthStatuses(db)
                            seedDefaultRecipes(db)
                        }
                    }
            }
        }

        // ---------------------------------------------------------
        // ⭐ 1) SEED HEALTH STATUSES
        // ---------------------------------------------------------
        private suspend fun seedHealthStatuses(db: AppDatabase) {
            val dao = db.healthStatusDao()
            val existing = dao.getAll()

            val defaults = listOf(
                "diyabet",
                "kolesterol",
                "kalp",
                "tansiyon",
                "gluten",
                "Herhangi bir rahatsızlığım yok"
            )

            defaults.forEach { name ->
                if (existing.none { it.name == name }) {
                    dao.insert(HealthStatusEntity(name = name))
                    Log.d("HEALTH_DB_LOG", "Inserted default: $name")
                }
            }
        }

        // ---------------------------------------------------------
        // ⭐ 2) SEED RECIPES FROM RecipeRepository
        // ---------------------------------------------------------
        private suspend fun seedDefaultRecipes(db: AppDatabase) {
            try {
                val recipeDao = db.recipeDao()
                val ingredientDao = db.ingredientDao()

                val countRecipes = recipeDao.count()
                Log.d("RECIPE_DB_LOG", "Existing recipe count = $countRecipes")

                if (countRecipes > 0) {
                    Log.d("RECIPE_DB_LOG", "Skipping seeding; recipes already exist.")
                    return
                }

                Log.d("RECIPE_DB_LOG", "Inserting default recipes...")

                for (model in com.example.akillitarifuygulamasi.RecipeRepository.allRecipes) {

                    // ⭐ تفكيك RecipeModel حسب ترتيبه الحقيقي
                    // ⭐ تفكيك RecipeModel حسب ترتيبه الحقيقي (7 عناصر فقط)
                    val (name, imageResId, rating, ingredientsText, instructions, calories, meal) = model

                    val recipeId = recipeDao.insert(
                        RecipeEntity(
                            title = name,
                            description = instructions,
                            imageResId = imageResId,
                            calories = calories,
                            meal = meal,
                            healthTag = "none" // قيمة افتراضية، تُستنتج لاحقًا
                        )
                    ).toInt()


                    // ⭐ تجهيز المكونات من النص
                    val ingredients = ingredientsText
                        .split("\n")
                        .map { it.replace("•", "").trim() }
                        .filter { it.isNotEmpty() }

                    ingredients.forEach { ing ->
                        ingredientDao.insertOne(
                            IngredientEntity(
                                recipeId = recipeId,
                                name = ing,
                                quantity = ""
                            )
                        )
                    }

                    Log.d("RECIPE_DB_LOG", "Inserted recipe: $name")
                }

                Log.d("RECIPE_DB_LOG", "Default recipes inserted successfully.")

            } catch (e: Exception) {
                Log.e("RECIPE_DB_LOG", "Error seeding recipes", e)
            }
        }
    }
}
