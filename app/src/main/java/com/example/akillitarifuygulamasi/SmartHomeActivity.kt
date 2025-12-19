package com.example.akillitarifuygulamasi

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.akillitarifuygulamasi.data.ai.HealthTag
import com.example.akillitarifuygulamasi.data.ai.TextNormalizer
import com.example.akillitarifuygulamasi.data.entity.RecipeEntity
import com.example.akillitarifuygulamasi.databinding.ActivitySmartHomeBinding
import com.example.akillitarifuygulamasi.ui.RecipeAdapter
import com.example.akillitarifuygulamasi.ui.viewmodel.RatingViewModel
import com.example.akillitarifuygulamasi.ui.viewmodel.RecipeViewModel
import kotlin.random.Random
import com.example.akillitarifuygulamasi.data.ai.HealthGate


class SmartHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmartHomeBinding

    private val recipeVm: RecipeViewModel by viewModels()
    private val ratingVm: RatingViewModel by viewModels()

    private lateinit var session: SessionManager
    private var userId: Int = -1
    private var userHealth: HealthTag? = null


    private lateinit var personalAdapter: RecipeAdapter
    private lateinit var healthAdapter: RecipeAdapter
    private lateinit var searchAdapter: RecipeAdapter

    // ⭐ نخزن القوائم الأصلية (للبحث الذكي)
    private var originalPersonalList: List<RecipeEntity> = emptyList()
    private var originalHealthList: List<RecipeEntity> = emptyList()

    private fun readUserHealth(session: SessionManager): HealthTag? {
        return HealthTag.from(session.getHealthStatus())
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ لازم أول سطر
        super.onCreate(savedInstanceState)

        // ----------------------------
        // ViewBinding
        // ----------------------------
        binding = ActivitySmartHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        // ----------------------------
        // Session
        // ----------------------------
        session = SessionManager(this)

        Log.e(
            "FLOW_CHECK",
            "SmartHome onCreate userId=${session.getUserId()}"
        )

        // ----------------------------
        // 🔒 حماية: إذا مو مسجل دخول لا تفتح الصفحة
        // ----------------------------
        if (!session.isLoggedIn() || session.getUserId() == -1) {
            goLoginAndClearStack()
            return
        }

        // ----------------------------
        // قراءة بيانات المستخدم (بعد الحماية فقط)
        // ----------------------------
        userId = session.getUserId()
        userHealth = HealthTag.from(session.getHealthStatus())

        Log.e(
            "HEALTH_CHECK",
            "session health = ${session.getHealthStatus()} | parsed = $userHealth"
        )

        // Debug إضافي
        Log.d(
            "AI_DEBUG",
            "SESSION healthRaw=${session.getHealthStatus()} -> userHealth=$userHealth"
        )

        // ----------------------------
        // RecyclerViews
        // ----------------------------
        binding.recyclerPersonal.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerHealth.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerSearch.layoutManager =
            LinearLayoutManager(this)

        personalAdapter = RecipeAdapter(mutableListOf(), this, ratingVm, { openDetail(it) }, null)
        healthAdapter = RecipeAdapter(mutableListOf(), this, ratingVm, { openDetail(it) }, null)
        searchAdapter = RecipeAdapter(mutableListOf(), this, ratingVm, { openDetail(it) }, null)

        binding.recyclerPersonal.adapter = personalAdapter
        binding.recyclerHealth.adapter = healthAdapter
        binding.recyclerSearch.adapter = searchAdapter

        // ----------------------------
        // 🍽️ Meal buttons
        // ----------------------------
        binding.btnKahvalti.setOnClickListener { openMeal("kahvalti") }
        binding.btnOgle.setOnClickListener { openMeal("ogle") }
        binding.btnAksam.setOnClickListener { openMeal("aksam") }
        binding.btnTatli.setOnClickListener { openMeal("tatli") }

        // ----------------------------
        // 🔎 Smart Search
        // ----------------------------
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()

                if (query.isEmpty()) {
                    showSearchMode(false)
                    searchAdapter.updateData(emptyList())
                    return
                }

                showSearchMode(true)

                val combined = (originalPersonalList + originalHealthList)
                    .distinctBy { it.id }
                    .filter { smartMatch(query, it) }

                if (combined.isEmpty()) {
                    binding.searchResultsTitle.visibility = View.GONE
                    binding.recyclerSearch.visibility = View.GONE
                } else {
                    binding.searchResultsTitle.visibility = View.VISIBLE
                    binding.recyclerSearch.visibility = View.VISIBLE
                    searchAdapter.updateData(combined)
                }
            }
        })

        // ----------------------------
        // ⭐ Bottom Navigation
        // ----------------------------
        binding.bottomNav.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavorilerActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.nav_logout -> {
                    session.logout()
                    session.clearHealthStatus()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

        // ----------------------------
        // 🔄 Swipe refresh
        // ----------------------------
        binding.swipeRefresh.setOnRefreshListener {
            reloadSmartHome()
        }

        // ----------------------------
        // أول تحميل
        // ----------------------------
        reloadSmartHome()
    }


    override fun onStart() {
        super.onStart()

        if (!session.isLoggedIn() || session.getUserId() == -1) {
            goLoginAndClearStack()
            return
        }




        // ✅ إعادة تحميل عند الرجوع للشاشة (بعد تفاصيل/مفضلة/بروفايل/إلخ)
        // بشرط ما يكون المستخدم يكتب في البحث الآن
        val currentQuery = binding.searchBar.text?.toString()?.trim().orEmpty()
        if (currentQuery.isEmpty()) {
            reloadSmartHome()
        }
    }

    private fun goLoginAndClearStack() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        // ما نحتاج finish لأن FLAGS تنظف الستاك
    }

    // ======================================================
    // 🔎 Smart text match
    // ======================================================
    private fun smartMatch(query: String, recipe: RecipeEntity): Boolean {
        val q = TextNormalizer.normalize(query)
        if (q.isBlank()) return true

        val words = q.split(" ").filter { it.isNotBlank() }
        val haystack = TextNormalizer.normalize(recipe.title + " " + recipe.description)

        return words.any { haystack.contains(it) }
    }

    // ----------------------------
    // 📄 Open detail
    // ----------------------------
    private fun openDetail(recipe: RecipeEntity) {
        val intent = Intent(this, RecipeDetailActivity::class.java).apply {
            putExtra("recipe_id", recipe.id)
        }
        startActivity(intent)
    }

    // ----------------------------
    // 🍽️ Meal page
    // ----------------------------
    private fun openMeal(meal: String) {
        val intent = Intent(this, RecommendedRecipesActivity::class.java)
        intent.putExtra("MEAL_LIST", arrayListOf(meal))
        startActivity(intent)
    }

    // ----------------------------
    // 🔄 Show/Hide sections while searching
    // ----------------------------
    private fun showSearchMode(isSearching: Boolean) {
        if (isSearching) {
            binding.titlePersonal.visibility = View.GONE
            binding.recyclerPersonal.visibility = View.GONE
            binding.titleHealth.visibility = View.GONE
            binding.recyclerHealth.visibility = View.GONE

            binding.searchResultsTitle.visibility = View.VISIBLE
            binding.recyclerSearch.visibility = View.VISIBLE
        } else {
            binding.titlePersonal.visibility = View.VISIBLE
            binding.recyclerPersonal.visibility = View.VISIBLE
            binding.titleHealth.visibility = View.VISIBLE
            binding.recyclerHealth.visibility = View.VISIBLE

            binding.searchResultsTitle.visibility = View.GONE
            binding.recyclerSearch.visibility = View.GONE
        }
    }

    private fun reloadSmartHome() {

        HealthGate.clearDebug()

        val userId = session.getUserId()


        if (!session.isLoggedIn() || userId == -1) {
            goLoginAndClearStack()
            return
        }

        val userHealth = readUserHealth(session)

        // إذا المستخدم يبحث الآن، لا تخرب عليه وتغيّر الواجهة
        val currentQuery = binding.searchBar.text?.toString()?.trim().orEmpty()
        if (currentQuery.isNotEmpty()) {
            binding.swipeRefresh.isRefreshing = false
            return
        }


        // ابدأ الريفريش
        binding.swipeRefresh.isRefreshing = true

        // 🔥 توصيات شخصية
        recipeVm.getSmartRecommendations(
            userId = userId,
            userHealth = null,
            mealFilter = null
        ) { list ->
            runOnUiThread {
                val filtered = list.filter { recipe ->
                    val result = HealthGate.check(
                        textRaw = recipe.title + " " + recipe.description,
                        userHealthTags = userHealth?.let { setOf(it) } ?: emptySet()
                    )

                    if (!result.allowed) {
                        android.util.Log.d(
                            "AI_FILTER",
                            "REJECT ${recipe.title} | ${result.reason}"
                        )
                    }

                    result.allowed
                }

                originalPersonalList = filtered

                val showList = filtered
                    .shuffled(Random(System.currentTimeMillis()))
                    .take(4)

                personalAdapter.updateData(showList)

            }
        }

        // 💚 توصيات صحية
        // 💚 توصيات صحية
        recipeVm.getSmartRecommendations(
            userId = userId,
            userHealth = userHealth, // ✅
            mealFilter = null
        ) { list ->

        runOnUiThread {

                // 🔴 FIX أساسي:
                // إذا التصفية الصحية رجعت فاضية (بعد logout/login)
                // نرجع نعرض الوصفات العامة بدل شاشة فاضية
            val filtered = list.filter { recipe ->
                val result = HealthGate.check(
                    textRaw = recipe.title + " " + recipe.description,
                    userHealthTags = userHealth?.let { setOf(it) } ?: emptySet()
                )

                if (!result.allowed) {
                    android.util.Log.d(
                        "AI_FILTER",
                        "REJECT ${recipe.title} | ${result.reason}"
                    )
                }

                result.allowed
            }

            val finalList = if (filtered.isEmpty()) {
                android.util.Log.e(
                    "FLOW_CHECK",
                    "Health filter empty → fallback to personal list"
                )
                originalPersonalList
            } else {
                filtered
            }

            // نخزن القائمة النهائية للبحث الذكي
                originalHealthList = finalList

                // نعرض فقط 4 بشكل عشوائي
                val showList = finalList
                    .shuffled(Random(System.currentTimeMillis() + 999))
                    .take(4)

                // تحديث الواجهة
                healthAdapter.updateData(showList)

                HealthGate.logSummary("SMART_HOME")

                // إيقاف السحب
                binding.swipeRefresh.isRefreshing = false
            }
        }

    }
    }

