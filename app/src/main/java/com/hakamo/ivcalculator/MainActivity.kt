package com.hakamo.ivcalculator

import android.content.Intent
import android.net.Uri
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.util.concurrent.ConcurrentHashMap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class MainActivity: AppCompatActivity(){
 private lateinit var body:LinearLayout; private var mode=0; private var page="home"; private var parentPage="home"
 private val accent=Color.rgb(70,226,194); private val blue=Color.rgb(103,156,255)
 private fun dark()=(resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES
 private fun bg()=if(dark())Color.rgb(5,8,14) else Color.rgb(246,248,252)
 private fun card()=if(dark())Color.rgb(14,20,31) else Color.WHITE
 private fun fg()=if(dark())Color.rgb(244,247,251) else Color.rgb(25,31,42)
 private fun muted()=if(dark())Color.rgb(158,171,191) else Color.rgb(101,113,130)
 private fun dp(n:Int)= (n*resources.displayMetrics.density).roundToInt()
 private fun glass(c:Int=card(),s:Int=if(dark())Color.rgb(43,56,76) else Color.rgb(218,225,236),r:Int=22)=GradientDrawable().apply{setColor(if(dark())Color.argb(205,Color.red(c),Color.green(c),Color.blue(c)) else Color.argb(225,Color.red(c),Color.green(c),Color.blue(c)));cornerRadius=dp(r).toFloat();setStroke(dp(1),s)}
 private fun tv(s:String,z:Float,col:Int=fg(),bold:Boolean=false)=TextView(this).apply{text=s;textSize=z;setTextColor(col);if(bold)setTypeface(typeface,1)}
 override fun onCreate(b:Bundle?){
  super.onCreate(b)
  mode=getPreferences(0).getInt("mode",0)
  applyMode()
  shell()
  home()
}

@Suppress("DEPRECATION")
override fun onBackPressed(){
  if(page=="home"){
    finish()
    return
  }
  when(parentPage){
    "calculators"->calculators()
    "medicines"->medicines()
    "assessment"->assessment()
    "reference"->reference()
    else->home()
  }
}
 private fun applyMode(){AppCompatDelegate.setDefaultNightMode(when(mode){1->AppCompatDelegate.MODE_NIGHT_NO;2->AppCompatDelegate.MODE_NIGHT_YES;else->AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM})}
 private fun shell(){val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(bg())};val h=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(18),dp(16),dp(18),dp(10))};val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutParams=LinearLayout.LayoutParams(0,-2,1f)};b.addView(tv("HAKAMO",12f,accent,true));b.addView(tv("مساعد التمريض",25f,fg(),true));b.addView(tv("أدوات تمريض سريعة • يعمل محليًا مع تحديثات اختيارية",11f,muted()));h.addView(b);val m=tv(themeName(),12f);m.gravity=Gravity.CENTER;m.background=glass(if(dark())Color.rgb(23,30,44) else Color.rgb(238,242,248),if(dark())Color.rgb(58,72,94) else Color.rgb(211,219,231),16);m.setPadding(dp(10),0,dp(10),0);m.setOnClickListener{mode=(mode+1)%3;getPreferences(0).edit().putInt("mode",mode).apply();recreate()};h.addView(m,LinearLayout.LayoutParams(dp(106),dp(44)));root.addView(h);body=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),0,dp(18),dp(24))};val scroll=ScrollView(this).apply{isFillViewport=true;addView(body)};root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));val nav=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;setPadding(dp(8),dp(8),dp(8),dp(10));setBackgroundColor(if(dark())Color.rgb(9,13,21) else Color.WHITE)};arrayOf("⌂" to "الرئيسية","🧮" to "الحاسبات","💊" to "الأدوية","🩺" to "التقييم","📚" to "المراجع").forEachIndexed{i,pair->val item=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setOnClickListener{when(i){0->home();1->calculators();2->medicines();3->assessment();4->reference()}}};item.addView(tv(pair.first,20f,if(i==0)accent else muted()).apply{gravity=Gravity.CENTER});item.addView(tv(pair.second,9f,if(i==0)accent else muted(),i==0).apply{gravity=Gravity.CENTER});nav.addView(item,LinearLayout.LayoutParams(0,dp(58),1f))};root.addView(nav);setContentView(root)}
 private fun themeName()=when(mode){0->"◐ تلقائي";1->"☀ نهاري";else->"☾ ليلي"}
 private fun clear(title:String,sub:String){
  body.removeAllViews()
  if(page!="home"){
    val back=tv("‹  رجوع",14f,accent,true)
    back.gravity=Gravity.CENTER
    back.background=glass(if(dark())Color.rgb(13,29,36) else Color.rgb(237,250,247),accent,15)
    back.setPadding(dp(12),0,dp(12),0)
    back.setOnClickListener{
      when(parentPage){
        "calculators"->calculators()
        "medicines"->medicines()
        "assessment"->assessment()
        "reference"->reference()
        else->home()
      }
    }
    body.addView(back,LinearLayout.LayoutParams(-1,dp(48)).apply{bottomMargin=dp(12)})
  }
  body.addView(tv(title,27f,fg(),true))
  body.addView(tv(sub,12f,muted()).apply{setPadding(0,dp(4),0,dp(14))})
}
 private fun menu(icon:String,name:String,desc:String,go:()->Unit){val c=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(15),dp(13),dp(15),dp(13));background=glass();setOnClickListener{go()}};c.addView(tv(icon,27f),LinearLayout.LayoutParams(dp(52),dp(58)));val col=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutParams=LinearLayout.LayoutParams(0,-2,1f)};col.addView(tv(name,16f,fg(),true));col.addView(tv(desc,11f,muted()));c.addView(col);c.addView(tv("›",28f,muted()));body.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})}
 private fun home(){page="home";parentPage="home";
  clear("مساعد Hakamo للتمريض","دليل تمريضي عربي شامل وسريع")
  val hero=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),dp(18),dp(18),dp(18));background=glass(if(dark())Color.argb(190,9,38,42) else Color.argb(220,235,250,247),Color.rgb(66,174,158),24)}
  hero.addView(tv("💚 HAKAMO",13f,accent,true))
  hero.addView(tv("كل أدوات التمريض في مكان واحد",22f,fg(),true))
  hero.addView(tv("أدوية • حاسبات • تقييم • تحاليل • طوارئ • مراجع",12f,muted()))
  body.addView(hero,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(14)})
  menu("💊","دليل الأدوية","صور العبوات • الأسعار • المواد الفعالة • البدائل"){medicines()}
  menu("🧮","حاسبات التمريض","المحاليل • الجرعات • التخفيف • الوزن • الوحدات"){calculators()}
  menu("🩺","تقييم المريض","GCS • NEWS2 • BMI • BSA • السوائل • البول"){assessment()}
  menu("🧪","التحاليل الطبية","القيم المرجعية • CBC • الكلى • الكبد • الأملاح • ABG"){info("🧪 التحاليل الطبية","قسم مرجعي للتحاليل الطبية: CBC، وظائف الكلى، وظائف الكبد، الأملاح، التجلط، غازات الدم، تحليل البول، الغدة الدرقية والمؤشرات القلبية. استخدم المجال المرجعي المطبوع في تقرير المختبر كمرجع أساسي، لأن القيم تختلف حسب العمر والسياق وطريقة القياس.")}
  menu("🚨","الطوارئ والإسعافات","ABCDE • الإنعاش • الصدمة • الحساسية • نقص السكر • النزيف"){info("🚨 الطوارئ والإسعافات","مرجع تعليمي سريع للطوارئ: التقييم الأولي ABCDE، الإنعاش القلبي الرئوي، الصدمة، الحساسية المفرطة، نقص السكر، التشنجات، الاختناق، الحروق والنزيف. اتبع بروتوكول المنشأة والإرشادات المعتمدة عند التعامل مع حالة حقيقية.")}
  menu("🫁","العناية الحرجة","الأكسجين • ABG • جهاز التنفس • الشفط • الأنابيب"){info("🫁 العناية الحرجة","مرجع تعليمي للعناية الحرجة يشمل العلاج بالأكسجين، غازات الدم، مبادئ جهاز التنفس الصناعي، الشفط، أنبوب الرغامى، أنبوب الصدر، ومتابعة المؤشرات الحيوية. لا تستخدمه كبديل لبروتوكول العناية المركزة.")}
  menu("🩹","الجروح والضمادات","تقييم الجرح • الأنسجة • الإفرازات • متابعة الغيار"){info("🩹 الجروح والضمادات","مرجع لتقييم الجروح: المكان، الطول والعرض والعمق، نوع الأنسجة، الإفرازات، الجلد المحيط، الألم، ونوع الغيار. توثيق الجرح يجب أن يتبع سياسة المنشأة، وأي صور للمريض تحتاج حماية الخصوصية والموافقة المناسبة.")}
  menu("🦠","مكافحة العدوى","غسل اليدين • معدات الوقاية • العزل • الأدوات الحادة"){info("🦠 مكافحة العدوى","مرجع تعليمي لمكافحة العدوى: نظافة اليدين، معدات الوقاية الشخصية، الاحتياطات القياسية، أنواع العزل، التعامل مع الأدوات الحادة وإصابات الوخز. اتبع سياسة مكافحة العدوى في منشأتك.")}
  menu("📋","التوثيق وتسليم الشيفت","ملاحظات التمريض • خطة الرعاية • SBAR • تعليم المريض"){info("📋 التوثيق وتسليم الشيفت","نماذج تعليمية للتوثيق التمريضي، تقييم المريض، خطة الرعاية، التدخلات، تعليم المريض، وتسليم الحالة بطريقة SBAR: الحالة، الخلفية، التقييم، والتوصية.")}
  menu("👶","تمريض الأطفال","العلامات الحيوية • APGAR • الجرعات حسب الوزن • السوائل"){info("👶 تمريض الأطفال","مرجع تعليمي للأطفال وحديثي الولادة: تقييم العلامات الحيوية حسب العمر، APGAR، حسابات الوزن والسوائل، وملاحظات السلامة. جرعات الأطفال تحتاج تحققًا مستقلًا من مرجع دوائي وبروتوكول المنشأة قبل إعطاء الدواء.")}
  menu("🤰","النساء والتوليد","الحمل • موعد الولادة • الولادة • نبض الجنين • APGAR"){info("🤰 النساء والتوليد","مرجع تعليمي لمتابعة الحمل والولادة وما بعد الولادة، حساب عمر الحمل وموعد الولادة، نبض الجنين ومقياس APGAR. القرارات السريرية يجب أن تعتمد على الفريق المعالج والبروتوكول المحلي.")}
  menu("🧰","إجراءات التمريض","شرح عربي خطوة بخطوة للإجراءات التمريضية"){procedures()}
  menu("📚","المراجع والنطاقات","العلامات الحيوية • التحاليل • الاختصارات • السلامة"){reference()}
  menu("📝","المذاكرة والمراجعة","أسئلة تعليمية • مصطلحات طبية • مراجعة سريعة"){info("📝 المذاكرة والمراجعة","قسم مخصص للمراجعة التعليمية والمصطلحات الطبية. يمكن إضافة بنك أسئلة واختبارات تدريجية في الإصدارات التالية.")}
  menu("🛡️","سلامة إعطاء الدواء","حقوق الدواء • الهوية • الحساسية • الطريق • التركيز"){safety()}
  menu("📲","تواصل مع Hakamo","تيليجرام • تيك توك • فيسبوك • يوتيوب"){contact()}
  val n=tv("⚠ مهم: المعلومات تعليمية ولا تستبدل وصف الطبيب أو الصيدلي أو بروتوكول المنشأة. لا تعتمد على التطبيق وحده لاتخاذ قرار علاجي.",11f,muted());n.setPadding(dp(12),dp(14),dp(12),dp(14));n.background=glass(if(dark())Color.argb(180,25,22,17) else Color.argb(220,255,249,236),if(dark())Color.rgb(82,70,49) else Color.rgb(235,207,150),16);body.addView(n)
 }
 private fun sectionInfo(title:String,text:String){page="sectionInfo";parentPage="home";clear(title,"مرجع تعليمي مختصر");val c=tv(text,15f);c.setPadding(dp(16),dp(18),dp(16),dp(18));c.background=glass();body.addView(c)}
 private fun calculators(){page="calculators";parentPage="home";clear("🧮 الحاسبات","حسابات تمريضية سريعة وواضحة");menu("💧","معدل المحلول","مل/ساعة + قطرة/دقيقة + زمن الانتهاء"){iv()};menu("💊","حساب الجرعة","حساب رياضي من التركيز المتاح"){dose()};menu("🧪","التخفيف","C1 × V1 = C2 × V2"){dilute()};menu("⚖","الحساب حسب الوزن","ملغم/كجم أو ميكروغرام/كجم • حساب رياضي فقط"){weight()};menu("🔁","تحويل الوحدات","ملغم • جم • ميكروغرام • مل • لتر • كجم"){units()}}
 private fun inp(h:String)=EditText(this).apply{hint=h;textSize=16f;setSingleLine();setTextColor(fg());setHintTextColor(muted());setPadding(dp(14),0,dp(14),0);background=glass(if(dark())Color.rgb(9,14,23) else Color.rgb(250,252,255),if(dark())Color.rgb(43,57,77) else Color.rgb(214,222,233),15)}
 private fun btn(s:String,go:()->Unit)=tv(s,15f,Color.rgb(3,20,19),true).apply{gravity=Gravity.CENTER;background=glass(accent,accent,16);setOnClickListener{go()};setPadding(dp(8),0,dp(8),0)}
 private fun out(s:String){val r=tv(s,18f,fg(),true);r.gravity=Gravity.CENTER;r.setPadding(dp(14),dp(18),dp(14),dp(18));r.background=glass(if(dark())Color.rgb(8,39,42) else Color.rgb(236,251,247),Color.rgb(63,181,164),20);body.addView(r,LinearLayout.LayoutParams(-1,-2).apply{topMargin=dp(14)});r.alpha=0f;r.animate().alpha(1f).setDuration(350).start()}
 private fun add(v:android.view.View){body.addView(v,LinearLayout.LayoutParams(-1,dp(56)).apply{bottomMargin=dp(9)})}
 private fun num(e:EditText)=e.text.toString().toDoubleOrNull() ?: -1.0
 private fun fmt(x:Double)=if(x.isFinite()&&abs(x-round(x))<1e-8)round(x).toInt().toString() else String.format("%.2f",x)
 private fun iv(){page="iv";parentPage="calculators";clear("💧 معدل المحلول","حساب مل/ساعة وقطرة/دقيقة وزمن الانتهاء • IV Rate");val v=inp("الكمية (مل) • الكمية (مل)");val h=inp("الساعات • الساعات");val m=inp("الدقائق • الدقائق");add(v);add(h);add(m);val sp=Spinner(this);sp.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("بدون قطرات","10 قطرة/مل","15 قطرة/مل","20 قطرة/مل","60 قطرة/مل"));body.addView(sp,LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(10)});body.addView(btn("احسب"){val a=num(v);val t=num(h)+num(m)/60;if(a>0&&t>0){var s="معدل المحلول = ${fmt(a/t)} مل/ساعة\n ${fmt(a/t)} mL/hr";val f=when(sp.selectedItemPosition){1->10;2->15;3->20;4->60;else->0};if(f>0)s+="\n\nمعدل القطرات = ${round(a*f/(t*60)).toInt()} قطرة/دقيقة\n ${round(a*f/(t*60)).toInt()} gtt/min";s+="\n\nمدة المحلول = ${fmt(t)} ساعة\nمدة المحلول = ${fmt(t)} hr";out(s)}else toast("أدخل القيم الصحيحة")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun dose(){page="dose";parentPage="calculators";clear("💊 حساب الجرعة","حساب رياضي فقط");val d=inp("الجرعة المطلوبة (ملغم)");val a=inp("الجرعة المتاحة (ملغم)");val v=inp("الحجم المتاح (مل) • الحجم المتاح");add(d);add(a);add(v);body.addView(btn("احسب"){val x=num(d);val y=num(a);val z=num(v);if(x>0&&y>0&&z>0)out("الحجم المطلوب = ${fmt(x/y*z)} مل\nVolume = ${fmt(x/y*z)} mL")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun dilute(){page="dilute";parentPage="calculators";clear("🧪 حساب التخفيف","C1 × V1 = C2 × V2");val c1=inp("التركيز الأصلي C1 • الأصلي");val c2=inp("التركيز المطلوب C2 • المطلوب");val v2=inp("الحجم النهائي V2 (مل) • الحجم النهائي");add(c1);add(c2);add(v2);body.addView(btn("احسب V1\nCalculate V1"){val a=num(c1);val b=num(c2);val c=num(v2);if(a>0&&b>0&&c>0)out("الحجم المطلوب V1 = ${fmt(b*c/a)} مل\nV1 = ${fmt(b*c/a)} mL")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun weight(){page="weight";parentPage="calculators";clear("⚖ الحساب حسب الوزن","ملغم/كجم أو ميكروغرام/كجم • الوزن-based\nحساب رياضي فقط");val d=inp("الجرعة لكل كجم");val w=inp("الوزن (كجم) • الوزن");add(d);add(w);body.addView(btn("احسب"){val a=num(d);val b=num(w);if(a>0&&b>0)out("الجرعة المحسوبة = ${fmt(a*b)}\nCalculated dose = ${fmt(a*b)}")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun units(){page="units";parentPage="calculators";clear("🔁 تحويل الوحدات","تحويلات شائعة");val x=inp("القيمة");val sp=Spinner(this);sp.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("mg → mcg","g → mg","mcg → mg","L → mL","mL → L","kg → g","g → kg"));add(x);body.addView(sp,LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(10)});body.addView(btn("حوّل"){val a=num(x);if(a>=0){val y=when(sp.selectedItemPosition){0->a*1000;1->a*1000;2->a/1000;3->a*1000;4->a/1000;5->a*1000;else->a/1000};out(fmt(y))}},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun assessment(){page="assessment";parentPage="home";clear("🩺 التقييم","تقييم ومتابعة");menu("🧠","مقياس غلاسكو","Eyes • Verbal • Motor\nالعين • الكلام • الحركة"){gcs()};menu("📈","التقييم المبكر NEWS2","RR • SpO₂ • BP • Pulse • Temp"){news()};menu("⚖","BMI / BSA\nمؤشر كتلة الجسم • مساحة سطح الجسم","Body measurements"){bmi()};menu("💧","السوائل الداخلة والخارجة","Balance mL"){io()};menu("🚽","إخراج البول","mL/kg/hr\nمل/كجم/ساعة"){urine()}}
 private fun gcs(){page="gcs";parentPage="assessment";clear("🧠 GCS","اختر أفضل استجابة مُلاحظة");val e=Spinner(this);val v=Spinner(this);val m=Spinner(this);e.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("E4 استجابة تلقائية","E3 استجابة للصوت","E2 استجابة للضغط","E1 لا استجابة"));v.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("V5 متوجه ومدرك","V4 مشوش","V3 كلمات","V2 أصوات","V1 لا استجابة"));m.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("M6 ينفذ الأوامر","M5 يحدد مكان الألم","M4 انثناء طبيعي","M3 انثناء غير طبيعي","M2 بسط","M1 لا استجابة"));listOf(e,v,m).forEach{body.addView(it,LinearLayout.LayoutParams(-1,dp(56)).apply{bottomMargin=dp(9)})};body.addView(btn("احسب مقياس غلاسكو"){val es=4-e.selectedItemPosition;val vs=5-v.selectedItemPosition;val ms=6-m.selectedItemPosition;out("GCS = ${es+vs+ms}\nE${es} V${vs} M${ms}")},LinearLayout.LayoutParams(-1,dp(54)));body.addView(tv("توثيق E/V/M منفصلًا أفضل من الاعتماد على المجموع وحده.",11f,muted()).apply{setPadding(0,dp(12),0,0)})}
 private fun news(){page="news";parentPage="assessment";clear("📈 NEWS2","حساب الدرجة — راجع البروتوكول المحلي");val rr=inp("RR /min");val sp=inp("SpO₂ %");val bp=inp("SBP mmHg");val pu=inp("Pulse /min");val te=inp("Temp °C");listOf(rr,sp,bp,pu,te).forEach{add(it)};val o=Spinner(this);o.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("هواء الغرفة","أكسجين إضافي"));val c=Spinner(this);c.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("يقظ ومستجيب","تغير في الوعي"));body.addView(o,LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(8)});body.addView(c,LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(10)});body.addView(btn("Calculate NEWS2\nاحسب NEWS2"){val a=num(rr);val b=num(sp);val d=num(bp);val p=num(pu);val t=num(te);if(a>0&&b>0&&d>0&&p>0&&t>0){val s=rs(a)+ss(b)+bs(d)+ps(p)+ts(t)+(if(o.selectedItemPosition==1)2 else 0)+(if(c.selectedItemPosition==1)3 else 0);out("NEWS2 = $s\nراجع الإجراء المناسب حسب سياسة المنشأة.")}else toast("أدخل العلامات الحيوية")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun rs(x:Double)=when{ x<=8->3;x<=11->1;x<=20->0;x<=24->2;else->3};private fun ss(x:Double)=when{x>=96->0;x>=94->1;x>=92->2;else->3};private fun bs(x:Double)=when{x<=90->3;x<=100->2;x<=110->1;x<=219->0;else->3};private fun ps(x:Double)=when{x<=40->3;x<=50->1;x<=90->0;x<=110->1;x<=130->2;else->3};private fun ts(x:Double)=when{x<=35->3;x<=36->1;x<=38->0;x<=39->1;else->2}
 private fun bmi(){page="bmi";parentPage="assessment";clear("⚖ BMI / BSA","حسابات جسمية");val w=inp("الوزن (كجم)");val h=inp("الطول (سم)");add(w);add(h);body.addView(btn("احسب"){val a=num(w);val b=num(h);if(a>0&&b>0)out("BMI = ${fmt(a/(b/100).pow(2))}\nBSA ≈ ${fmt(sqrt(a*b/3600))} m²")else toast("أدخل الوزن والطول")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun io(){page="io";parentPage="assessment";clear("💧 Intake & Output","حساب الميزان");val i=inp("السوائل الداخلة (مل)");val o=inp("السوائل الخارجة (مل)");add(i);add(o);body.addView(btn("احسب"){val a=num(i);val b=num(o);if(a>=0&&b>=0)out("Balance = ${fmt(a-b)} mL")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun urine(){page="urine";parentPage="assessment";clear("🚽 Urine Output","mL/kg/hr\nمل/كجم/ساعة");val v=inp("Urine mL");val h=inp("الساعات");val w=inp("الوزن (كجم)");listOf(v,h,w).forEach{add(it)};body.addView(btn("احسب"){val a=num(v);val b=num(h);val c=num(w);if(a>=0&&b>0&&c>0)out("${fmt(a/(b*c))} mL/kg/hr")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}

 private data class Drug(val generic:String,val ar:String,val category:String,val uses:String,val warnings:String,val effects:String)

 private val drugs=listOf(
  Drug("Paracetamol / Acetaminophen","باراسيتامول / أسيتامينوفين","مسكنات وخافضات حرارة\nمسكن وخافض حرارة","تخفيف الألم والحمى.","الحذر مع أمراض الكبد ومع المنتجات الأخرى التي تحتوي على نفس المادة.","غثيان، طفح، ونادرًا أذى كبدي خاصة مع الجرعات الزائدة."),
  Drug("Ibuprofen","إيبوبروفين","NSAID\nمضاد التهاب غير ستيرويدي","الألم والالتهاب والحمى في حالات مناسبة.","الحذر مع قرحة/نزيف المعدة، أمراض الكلى، وبعض حالات القلب والحمل.","ألم أو حموضة المعدة، غثيان، احتباس سوائل، وقد يزيد خطر النزيف."),
  Drug("Aspirin","أسبرين / حمض أسيتيل الساليسيليك","Antiplatelet / Analgesic","يُستخدم حسب الوصفة في حالات معينة لتسكين الألم أو كمضاد صفائح.","يزيد خطر النزيف؛ لا يُعطى للأطفال/المراهقين لبعض الأمراض الفيروسية دون توجيه طبي.","تهيج المعدة، نزيف، حساسية أو تشنج قصبي لدى بعض الأشخاص."),
  Drug("Amoxicillin","أموكسيسيلين","Antibiotic\nمضاد حيوي","علاج بعض العدوى البكتيرية الحساسة له.","لا يفيد في العدوى الفيروسية؛ تحقق من حساسية البنسلين والتداخلات والوصفة.","إسهال، غثيان، طفح، وتفاعلات تحسسية قد تكون خطيرة."),
  Drug("Ceftriaxone","سيفترياكسون","Cephalosporin\nسيفالوسبورين","علاج عدوى بكتيرية معينة حسب التشخيص والبروتوكول.","تحقق من الحساسية والتوافق وطريقة الإعطاء؛ يستخدم بوصفة وتحت إشراف.","إسهال، ألم موضع الحقن، طفح أو حساسية."),
  Drug("Metronidazole","ميترونيدازول","Antimicrobial\nمضاد ميكروبي","يُستخدم ضد بعض العدوى البكتيرية والطفيلية.","تجنب الكحول أثناء العلاج ولفترة مناسبة حسب النشرة/التوجيه الطبي؛ راجع التداخلات.","غثيان، طعم معدني، إسهال، صداع."),
  Drug("Ondansetron","أوندانسيترون","Antiemetic\nمضاد قيء","الوقاية أو علاج الغثيان والقيء في حالات محددة.","الحذر مع اضطرابات نظم القلب وبعض الأدوية التي تطيل QT.","صداع، إمساك، وقد يحدث اضطراب نظم نادرًا."),
  Drug("Omeprazole","أوميبرازول","PPI\nمثبط مضخة البروتون","تقليل حمض المعدة في حالات مثل الارتجاع والقرحة.","راجع الاستخدام طويل المدى والتداخلات والحالات التي تحتاج تقييمًا طبيًا.","صداع، ألم بطني، إسهال أو إمساك."),
  Drug("Furosemide","فوروسيميد","Loop Diuretic\nمدر بول","تقليل احتباس السوائل في حالات معينة وعلاج بعض حالات ارتفاع الضغط.","يحتاج متابعة الضغط، السوائل، وظائف الكلى والأملاح.","جفاف، انخفاض ضغط، واضطراب الصوديوم أو البوتاسيوم."),
  Drug("Enoxaparin","إنوكسابارين","Anticoagulant\nمضاد تخثر","الوقاية أو العلاج من الجلطات حسب الحالة والوصفة.","خطر النزيف؛ راجع وظائف الكلى والأدوية الأخرى وإجراءات التخدير/الإبر حسب البروتوكول.","كدمات أو نزيف، أنيميا، وتفاعلات موضعية."),
  Drug("Heparin","هيبارين","Anticoagulant\nمضاد تخثر","الوقاية أو العلاج من الجلطات حسب الوصفة.","خطر النزيف وHIT؛ يحتاج متابعة مخبرية حسب نوع الاستخدام والبروتوكول.","نزيف، كدمات، ونادرًا نقص صفائح مناعي."),
  Drug("Insulin Regular","إنسولين Regular","Antidiabetic\nخافض سكر","خفض سكر الدم في حالات محددة حسب الخطة العلاجية.","أهم خطر هو نقص سكر الدم؛ تحقق من التركيز والوحدة والطريق والوجبة والبروتوكول قبل الإعطاء.","نقص سكر، زيادة وزن، وتفاعلات موضعية."),
  Drug("Salbutamol / Albuterol","سالبوتامول / ألبوتيرول","Bronchodilator\nموسع شعب","تخفيف تشنج الشعب الهوائية في حالات مثل الربو وCOPD.","استخدم حسب الخطة؛ الحذر مع بعض أمراض القلب واضطرابات النظم.","رعشة، خفقان، صداع."),
  Drug("Dexamethasone","ديكساميثازون","Corticosteroid\nكورتيكوستيرويد","تقليل الالتهاب أو التورم في حالات محددة حسب التشخيص.","قد يرفع السكر ويزيد خطر العدوى؛ لا يوقف العلاج طويل المدى فجأة دون توجيه.","ارتفاع السكر، أرق، تغيرات مزاجية، وزيادة خطر العدوى."),
  Drug("Hydrocortisone","هيدروكورتيزون","Corticosteroid\nكورتيكوستيرويد","يُستخدم في حالات التهابية أو نقص الكورتيزول حسب التشخيص.","الحذر من العدوى وارتفاع السكر؛ الاستخدام والجرعة حسب الحالة والبروتوكول.","ارتفاع السكر، احتباس سوائل، اضطراب مزاجي."),
  Drug("Morphine","مورفين","Opioid Analgesic\nمسكن أفيوني","تسكين الألم الشديد تحت إشراف طبي.","خطر تثبيط التنفس، انخفاض الوعي، والإدمان؛ يحتاج مراقبة دقيقة.","نعاس، غثيان، إمساك، وتثبيط التنفس."),
  Drug("Tramadol","ترامادول","Opioid Analgesic\nمسكن أفيوني","علاج بعض أنواع الألم عندما يكون مناسبًا وبوصفة.","خطر النعاس، التشنجات ومتلازمة السيروتونين مع بعض الأدوية؛ راجع التداخلات.","دوخة، غثيان، نعاس، إمساك."),
  Drug("Diazepam","ديازيبام","Benzodiazepine\nبنزوديازيبين","يُستخدم في حالات محددة مثل بعض التشنجات أو القلق أو التشنج العضلي.","قد يسبب تثبيط التنفس والاعتماد؛ خطر أعلى مع الأفيونات والكحول.","نعاس، دوخة، ضعف التنسيق."),
  Drug("Adrenaline / Epinephrine","أدرينالين / إبينفرين","طوارئ\nطوارئ","دواء أساسي في حالات طارئة محددة مثل التأق، ويستخدم حسب البروتوكول.","دواء عالي الخطورة؛ الطريق والتركيز مهمان جدًا، واتبع بروتوكول الطوارئ.","خفقان، رعشة، قلق، صداع."),
  Drug("Nitroglycerin","نيتروغليسرين","Nitrate\nنترات","تخفيف ألم الذبحة في سياقات محددة حسب الوصفة.","قد يسبب انخفاض ضغط شديد؛ لا يُجمع مع بعض أدوية ضعف الانتصاب.","صداع، دوخة، انخفاض ضغط."),
  Drug("Amlodipine","أملوديبين","Calcium Channel Blocker","علاج ارتفاع الضغط وبعض حالات الذبحة.","راقب الضغط والوذمة؛ الاستخدام حسب الوصفة.","تورم القدمين، دوخة، احمرار."),
  Drug("Metoprolol","ميتوبرولول","Beta Blocker\nحاصر بيتا","ارتفاع الضغط وبعض أمراض القلب واضطرابات النظم حسب الحالة.","قد يبطئ النبض ويخفض الضغط؛ الحذر في بعض حالات الربو والسكري.","بطء النبض، دوخة، تعب."),
  Drug("Atorvastatin","أتورفاستاتين","Statin\nستاتين","خفض الكوليسترول وتقليل خطر أمراض القلب حسب الخطة العلاجية.","الحذر مع أمراض الكبد وبعض التداخلات؛ أبلغ عن ألم عضلي شديد.","ألم عضلي، اضطراب هضمي، ارتفاع إنزيمات الكبد."),
  Drug("Warfarin","وارفارين","Anticoagulant\nمضاد تخثر","الوقاية أو العلاج من الجلطات في حالات محددة.","تداخلات دوائية وغذائية كثيرة؛ يحتاج متابعة INR ولا يُستخدم دون وصفة.","نزيف، كدمات."),
  Drug("Loratadine","لوراتادين","Antihistamine\nمضاد حساسية","أعراض الحساسية والرشح التحسسي.","راجع أمراض الكبد والتداخلات حسب المنتج.","صداع، جفاف الفم، نعاس أقل من بعض مضادات الحساسية القديمة."),
  Drug("Cetirizine","سيتريزين","Antihistamine\nمضاد حساسية","أعراض الحساسية والشرى.","قد يسبب نعاسًا؛ الحذر مع المهدئات وبعض حالات الكلى.","نعاس، جفاف الفم، تعب."),
  Drug("Ferrous Sulfate","كبريتات الحديد","Iron Supplement\nمكمل حديد","علاج أو وقاية من نقص الحديد عند وصفه.","يُستخدم عند وجود حاجة؛ قد تتداخل امتصاصاته مع بعض الأدوية والطعام.","إمساك، غثيان، براز داكن."),
  Drug("Potassium Chloride","كلوريد البوتاسيوم","Electrolyte\nإلكتروليت","تصحيح نقص البوتاسيوم عندما يكون موصوفًا.","دواء عالي الخطورة؛ التركيز والطريق ومعدل الإعطاء مهمون جدًا ويجب اتباع بروتوكول المنشأة.","غثيان، ألم موضعي، واضطراب نظم خطير عند ارتفاع البوتاسيوم."),
  Drug("Normal Saline 0.9%","محلول ملحي 0.9%","IV Fluid\nمحلول وريدي","تعويض سوائل أو كحامل/مذيب في استخدامات محددة حسب الحالة.","راقب حالة السوائل والصوديوم خاصة في مرضى القلب والكلى.","احتباس سوائل أو اضطراب أملاح عند الإفراط."),
  Drug("Dextrose 5%","جلوكوز 5% / ديكستروز","IV Fluid\nمحلول وريدي","مصدر ماء وجلوكوز في استخدامات محددة حسب الحالة.","قد يرفع سكر الدم ويؤثر على توازن السوائل؛ حسب الحالة والبروتوكول.","ارتفاع سكر أو اضطراب سوائل/أملاح.")
,
  Drug("Metformin","ميتفورمين","Antidiabetic\nخافض سكر","علاج السكري من النوع الثاني ضمن خطة علاجية.","يحتاج تقييم وظائف الكلى والحالة العامة؛ توجد حالات يمنع أو يوقف فيها حسب الطبيب.","غثيان، إسهال، اضطراب معدة."),
  Drug("Insulin Glargine","إنسولين جلارجين","Antidiabetic\nخافض سكر","إنسولين طويل المفعول ضمن خطط علاج السكري.","خطر نقص سكر الدم؛ تحقق من نوع الإنسولين والتركيز والوصفة قبل الإعطاء.","نقص سكر، زيادة وزن، تفاعل موضعي."),
  Drug("Levothyroxine","ليفوثيروكسين","Thyroid\nالغدة الدرقية","تعويض هرمون الغدة الدرقية عند وجود قصور.","الجرعة فردية ويُعتمد فيها على التحاليل والوصفة؛ التوقيت والتداخلات مهمان.","خفقان، رعشة، تعرق عند زيادة الجرعة."),
  Drug("Azithromycin","أزيثروميسين","Antibiotic\nمضاد حيوي","علاج بعض العدوى البكتيرية الحساسة.","لا يفيد للعدوى الفيروسية؛ الحذر مع اضطرابات نظم القلب والتداخلات.","إسهال، غثيان، ألم بطني."),
  Drug("Doxycycline","دوكسيسيكلين","Antibiotic\nمضاد حيوي","علاج بعض العدوى البكتيرية وحالات جلدية محددة.","تجنب التعرض الشديد للشمس واتبع تعليمات الاستخدام؛ توجد قيود في بعض الأعمار والحمل.","غثيان، حساسية للشمس، تهيج المريء."),
  Drug("Ciprofloxacin","سيبروفلوكساسين","Antibiotic\nمضاد حيوي","علاج بعض العدوى البكتيرية وفق الحساسية والوصفة.","قد يسبب مشاكل بالأوتار وتداخلات دوائية؛ لا يستخدم عشوائيًا.","غثيان، إسهال، دوخة، ألم أو التهاب أوتار نادر."),
  Drug("Piperacillin/Tazobactam","بيبراسيلين/تازوباكتام","Antibiotic\nمضاد حيوي","علاج عدوى بكتيرية خطيرة معينة داخل المستشفى حسب البروتوكول.","تحقق من حساسية البنسلين ووظائف الكلى والتوافق الوريدي.","إسهال، طفح، تغيرات في وظائف الكبد أو الدم."),
  Drug("Meropenem","ميروبينيم","Antibiotic\nمضاد حيوي","علاج عدوى بكتيرية شديدة محددة تحت إشراف متخصص.","استخدامه يجب أن يكون موجهًا ومقيدًا حسب بروتوكول مكافحة مقاومة الميكروبات؛ راجع وظائف الكلى.","إسهال، طفح، غثيان، وتشنجات نادرة."),
  Drug("Vancomycin","فانكوميسين","Antibiotic\nمضاد حيوي","علاج عدوى بكتيرية خطيرة معينة حسب التشخيص والمزرعة.","يحتاج متابعة وظائف الكلى ومستويات الدواء في بعض الاستخدامات؛ سرعة التسريب مهمة.","احمرار أثناء التسريب، تهيج وريدي، وتأثير كلوي."),
  Drug("Fluconazole","فلوكونازول","Antifungal\nمضاد فطريات","علاج بعض العدوى الفطرية.","راجع وظائف الكبد والتداخلات وإطالة QT عند المرضى المعرضين.","غثيان، ألم بطني، صداع، وارتفاع إنزيمات الكبد."),
  Drug("Acyclovir","أسيكلوفير","Antiviral\nمضاد فيروسات","علاج بعض عدوى الهربس والفيروسات القريبة منها.","تعديل الاستخدام حسب وظائف الكلى وشرب السوائل حسب الحالة.","غثيان، صداع، واضطراب كلوي خصوصًا مع بعض طرق الإعطاء."),
  Drug("Ipratropium","إبراتروبيوم","Bronchodilator\nموسع شعب","تخفيف تشنج الشعب في حالات تنفسية محددة.","الحذر في بعض حالات الزَرَق واحتباس البول.","جفاف الفم، صداع، تهيج الحلق."),
  Drug("Acetylcysteine","أسيتيل سيستئين","Mucolytic / Antidote\nمذيب للبلغم/ترياق","يستخدم كمذيب للبلغم وفي استخدامات طبية محددة كتسمم الباراسيتامول تحت بروتوكول.","طريق الاستخدام يختلف حسب الحالة؛ لا تعتمد على الجرعات من التطبيق.","غثيان، قيء، طفح أو تفاعل تحسسي."),
  Drug("Dopamine","دوبامين","Vasopressor\nداعم للدورة الدموية","استخدام وريدي متخصص في حالات محددة من انخفاض الضغط/الدورة الدموية.","دواء عالي الخطورة يحتاج مضخة ومراقبة مستمرة وبروتوكول متخصص.","اضطراب نظم، تغير ضغط، وتلف نسيجي عند التسرب الوريدي."),
  Drug("Norepinephrine","نورإبينفرين","Vasopressor\nرافِع ضغط","دعم الضغط في حالات صدمة محددة داخل بيئة مراقبة.","دواء عالي الخطورة؛ يحتاج مضخة ومراقبة مستمرة وطريق إعطاء مناسب.","ارتفاع ضغط، اضطراب نظم، ونقص تروية عند الجرعات المرتفعة."),
  Drug("Amiodarone","أميودارون","Antiarrhythmic\nمضاد اضطراب نظم","علاج بعض اضطرابات نظم القلب تحت إشراف متخصص.","تداخلات كثيرة ومخاطر قلبية ورئوية وكبدية؛ المتابعة مهمة.","بطء النبض، انخفاض ضغط، واضطرابات الغدة أو الرئة مع الاستخدام الطويل."),
  Drug("Spironolactone","سبيرونولاكتون","Diuretic\nمدر بول","مدر يحافظ على البوتاسيوم ويستخدم في حالات قلبية أو ضغطية محددة.","خطر ارتفاع البوتاسيوم؛ راجع وظائف الكلى والأدوية المصاحبة.","ارتفاع البوتاسيوم، دوخة، وتغيرات هرمونية."),
  Drug("Lidocaine","ليدوكايين","Local Anesthetic\nمخدر موضعي","تخدير موضعي وفي استخدامات قلبية محددة حسب البروتوكول.","السمية العصبية والقلبية ممكنة عند التعرض الزائد؛ التركيز والطريق مهمان.","تنميل، دوخة، تشنجات أو اضطراب نظم عند السمية."),
  Drug("Fentanyl","فنتانيل","Opioid\nمسكن أفيوني","مسكن أفيوني قوي في بيئات طبية محددة وتحت مراقبة.","خطر تثبيط التنفس والجرعة الزائدة مرتفع؛ يحتاج مراقبة وتجهيز إنعاش.","تثبيط التنفس، نعاس، غثيان، بطء نبض."),
  Drug("Pantoprazole","بانتوبرازول","PPI\nمثبط مضخة البروتون","تقليل حمض المعدة في حالات محددة.","راجع الاستخدام طويل المدى والتداخلات والحالة السريرية.","صداع، إسهال أو إمساك، ألم بطني."),
  Drug("Loperamide","لوبيراميد","Antidiarrheal\nمضاد إسهال","تخفيف بعض حالات الإسهال الحاد لدى البالغين عندما لا توجد علامات خطر.","لا يستخدم عند وجود دم بالبراز أو حرارة شديدة أو اشتباه عدوى معينة دون تقييم طبي.","إمساك، مغص، ونادرًا اضطراب نظم عند إساءة الاستخدام."),
  Drug("Saline 0.9%","محلول كلوريد الصوديوم 0.9%","IV Fluid\nمحلول وريدي","تعويض سوائل أو استخدامات وريدية محددة حسب الحالة.","راقب السوائل والصوديوم خاصة في قصور القلب والكلى.","زيادة السوائل أو اضطراب الأملاح عند الإفراط."),
  Drug("Potassium Phosphate","فوسفات البوتاسيوم","Electrolyte\nإلكتروليت","تصحيح نقص الفوسفات في حالات محددة وبروتوكول المستشفى.","دواء عالي الخطورة؛ يحتاج تحققًا من البوتاسيوم ووظائف الكلى وطريق الإعطاء.","اضطراب الأملاح أو نظم القلب عند الخلل."),
  Drug("Calcium Gluconate","جلوكونات الكالسيوم","Electrolyte / Emergency\nإلكتروليت/طوارئ","استخدامات طارئة ومختبرية محددة مثل بعض اضطرابات الكالسيوم.","الطريق والتركيز مهمان؛ بعض الاستخدامات تحتاج مراقبة قلبية.","تهيج وريدي، انخفاض ضغط أو اضطراب نظم عند سوء الاستخدام."),
  Drug("Magnesium Sulfate","كبريتات الماغنيسيوم","Electrolyte / Emergency\nإلكتروليت/طوارئ","يستخدم في حالات محددة مثل نقص المغنيسيوم وبعض بروتوكولات الطوارئ والتوليد.","يحتاج مراقبة التنفس والانعكاسات ووظائف الكلى حسب الاستخدام.","احمرار، انخفاض ضغط، وضعف تنفس عند السمية."),
  Drug("Naloxone","نالوكسون","Antidote\nترياق","عكس تأثير الأفيونات في حالات الاشتباه بتسمم أفيوني مع دعم مجرى الهواء.","قد يعود تثبيط التنفس بعد انتهاء تأثيره؛ يحتاج تقييم ومراقبة مستمرة.","انسحاب حاد، غثيان، تسرع نبض."),
  Drug("Atropine","أتروبين","Emergency\nطوارئ","استخدامات طارئة محددة مثل بعض حالات بطء القلب أو التسممات.","يستخدم وفق بروتوكول الطوارئ؛ قد يسبب تسرع القلب واحتباس البول.","جفاف الفم، تسرع القلب، تشوش الرؤية."),
  Drug("Promethazine","بروميثازين","Antiemetic / Antihistamine\nمضاد قيء/حساسية","تخفيف بعض حالات الغثيان والحساسية حسب الوصفة.","بعض طرق الحقن تحمل مخاطر نسيجية كبيرة؛ اتبع تعليمات المنتج والبروتوكول بدقة.","نعاس، جفاف الفم، انخفاض ضغط."),
  Drug("Diclofenac","ديكلوفيناك","NSAID\nمضاد التهاب غير ستيرويدي","تخفيف الألم والالتهاب في حالات مناسبة.","الحذر من قرحة ونزيف المعدة والكلى والقلب؛ لا يُجمع عشوائيًا مع NSAIDs أخرى.","ألم معدة، غثيان، احتباس سوائل، وزيادة خطر النزيف."),
  Drug("Ketorolac","كيتورولاك","NSAID\nمسكن مضاد التهاب","مسكن للألم الحاد لفترة محدودة في حالات مختارة.","خطر نزيف المعدة وتأثير الكلى؛ لا يستخدم لفترات طويلة أو مع بعض NSAIDs.","ألم معدة، غثيان، نزيف، وتأثير كلوي."),
  Drug("Clopidogrel","كلوبيدوجريل","Antiplatelet\nمضاد صفائح","تقليل خطر الجلطات في حالات قلبية ووعائية محددة.","يزيد خطر النزيف وله تداخلات دوائية؛ يحتاج وصفة ومتابعة.","كدمات، نزيف، اضطراب معدة."),
  Drug("Losartan","لوسارتان","ARB\nحاصر مستقبل الأنجيوتنسين","علاج ارتفاع الضغط وبعض أمراض القلب والكلى حسب الحالة.","الحذر مع الحمل وارتفاع البوتاسيوم ووظائف الكلى.","دوخة، ارتفاع البوتاسيوم، تغير وظائف الكلى."),
  Drug("Enalapril","إنالابريل","ACE Inhibitor\nمثبط ACE","علاج ارتفاع الضغط وبعض حالات فشل القلب.","قد يسبب ارتفاع البوتاسيوم وتغير وظائف الكلى؛ يُمنع في الحمل.","سعال جاف، دوخة، ارتفاع البوتاسيوم."),
  Drug("Atenolol","أتينولول","Beta Blocker\nحاصر بيتا","استخدامات قلبية وضغطية محددة.","قد يبطئ النبض؛ الحذر في بعض أمراض التنفس والسكري.","بطء النبض، دوخة، تعب."),
  Drug("Simvastatin","سيمفاستاتين","Statin\nستاتين","خفض الكوليسترول وتقليل مخاطر قلبية حسب الخطة.","تداخلات دوائية متعددة؛ أبلغ عن ألم عضلي شديد.","ألم عضلي، اضطراب هضمي، ارتفاع إنزيمات الكبد.") ,
)

 private data class MarketDrug(val en:String,val ar:String,val scientific:String,val manufacturer:String,val route:String,val category:String,val price:Double?)
 private var marketDrugs:List<MarketDrug> = emptyList()
 private var marketLoaded=false
  private var marketLoading=false
  private val renderExecutor=Executors.newSingleThreadExecutor()
  private val renderGeneration=AtomicInteger(0)
 private val marketUrl="https://raw.githubusercontent.com/karem505/egyptian-drug-database/main/data/egyptian-drugs.json"
 private val imageExecutor=Executors.newFixedThreadPool(3)
 private val imageCache=object: LruCache<String,Bitmap>(12 * 1024 * 1024){override fun sizeOf(key:String,value:Bitmap)=value.byteCount}
 private val imageMissCache=ConcurrentHashMap<String,Boolean>()
 private fun dawaagateSlug(name:String):String = name.trim().lowercase(java.util.Locale.ROOT).replace("+","-plus-").replace(Regex("[^a-z0-9\\s-]"),"").replace(Regex("\\s+"),"-").replace(Regex("-+"),"-").trim('-')
 private fun loadDrugImage(d:MarketDrug,target:ImageView,keySuffix:String="") {
  val key=d.en.trim().ifBlank{d.ar.trim()}+keySuffix
  target.setImageResource(android.R.drawable.ic_menu_gallery)
  imageCache.get(key)?.let{target.setImageBitmap(it);return}
  if(imageMissCache[key]==true)return
  imageExecutor.execute{try{
   val pageUrl="https://www.dawaagate.com/medicine/${dawaagateSlug(d.en)}"
   val pc=(java.net.URL(pageUrl).openConnection() as java.net.HttpURLConnection).apply{connectTimeout=7000;readTimeout=9000;requestMethod="GET";setRequestProperty("User-Agent","Mozilla/5.0 Hakamo Nursing Assistant")}
   val html=pc.inputStream.bufferedReader(Charsets.UTF_8).use{it.readText()};pc.disconnect()
   val imageUrl=Regex("""https://cdn\.dawaagate\.com/images/medicines/[^"'<> ]+""").find(html)?.value?.replace("&amp;","&")
   if(imageUrl.isNullOrBlank()){imageMissCache[key]=true;return@execute}
   val ic=(java.net.URL(imageUrl).openConnection() as java.net.HttpURLConnection).apply{connectTimeout=7000;readTimeout=9000;setRequestProperty("User-Agent","Mozilla/5.0 Hakamo Nursing Assistant")}
   val bitmap=ic.inputStream.use{BitmapFactory.decodeStream(it)};ic.disconnect()
   if(bitmap!=null){imageCache.put(key,bitmap);runOnUiThread{target.setImageBitmap(bitmap)}} else imageMissCache[key]=true
  }catch(_:Exception){imageMissCache[key]=true}}
 }


 private fun medicines(){
  page="medicines";parentPage="home"
  clear("💊 دليل الأدوية في مصر","ابحث عن الدواء أو المادة الفعالة واستعرض السعر والتفاصيل والبدائل")

  val info=LinearLayout(this).apply{
    orientation=LinearLayout.VERTICAL
    setPadding(dp(16),dp(14),dp(16),dp(14))
    background=glass(if(dark())Color.rgb(12,29,28) else Color.rgb(235,250,246),accent,20)
  }
  info.addView(tv("دليل أسعار الأدوية في مصر",18f,fg(),true))
  info.addView(tv("الأسعار استرشادية وتتغير مع تحديثات السوق. راجع الصيدلي أو المصدر الرسمي قبل الاعتماد عليها.",11f,muted()).apply{setPadding(0,dp(5),0,0)})
  body.addView(info,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})

  val stats=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  fun stat(title:String,value:String){
    val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(dp(8),dp(10),dp(8),dp(10));background=glass()}
    c.addView(tv(value,17f,accent,true).apply{gravity=Gravity.CENTER})
    c.addView(tv(title,10f,muted()).apply{gravity=Gravity.CENTER})
    stats.addView(c,LinearLayout.LayoutParams(0,dp(70),1f).apply{leftMargin=dp(3);rightMargin=dp(3)})
  }
  stat("دواء",if(marketLoaded)"${marketDrugs.size}" else "—")
  stat("التصنيفات",if(marketLoaded)"${marketDrugs.map{it.category}.filter{it.isNotBlank()}.distinct().size}" else "—")
  stat("حالة البيانات",if(marketLoaded)"محدّثة" else "سريعة")
  body.addView(stats,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})

  val status=tv(if(marketLoaded)"تم تحميل قاعدة الأدوية" else "يمكنك التصفح فورًا، واضغط تحديث لتحميل قاعدة السوق",11f,muted())
  body.addView(status,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(7)})

  val search=inp("ابحث باسم الدواء أو المادة الفعالة أو الشركة")
  add(search)

  val actions=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  val refreshBtn=btn("↻ تحديث قاعدة الأدوية"){loadMarketDrugs(status)}
  actions.addView(refreshBtn,LinearLayout.LayoutParams(0,dp(50),1f).apply{rightMargin=dp(4)})
  val liveBtn=btn("🌐 الأسعار الحالية"){try{startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.dawaagate.com/medicines")))}catch(_:Exception){toast("تعذر فتح مصدر الأسعار")}}
  actions.addView(liveBtn,LinearLayout.LayoutParams(0,dp(50),1f).apply{leftMargin=dp(4)})
  body.addView(actions,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})

  val filters=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(12),dp(12),dp(12),dp(12));background=glass()}
  filters.addView(tv("تصفية وترتيب النتائج",13f,accent,true))

  val category=Spinner(this)
  val company=Spinner(this)
  val sort=Spinner(this)
  fun spinnerAdapter(items:List<String>):ArrayAdapter<String>{
    return ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,items)
  }
  category.adapter=spinnerAdapter(listOf("كل التصنيفات"))
  company.adapter=spinnerAdapter(listOf("كل الشركات"))
  sort.adapter=spinnerAdapter(listOf("الترتيب الافتراضي","الاسم من أ إلى ي","السعر من الأقل للأعلى","السعر من الأعلى للأقل"))
  fun row(label:String,sp:Spinner){
    val r=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
    r.addView(tv(label,10f,muted()).apply{setPadding(dp(2),dp(6),dp(2),dp(3))})
    r.addView(sp,LinearLayout.LayoutParams(-1,dp(48)))
    filters.addView(r)
  }
  row("التصنيف",category);row("الشركة",company);row("الترتيب",sort)
  body.addView(filters,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})

  val tabs=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  val tabNames=listOf("كل الأدوية","الأرخص","الأغلى","أبجدي")
  var selectedTab=0
  val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}
  val shownCount=tv("",11f,muted()).apply{setPadding(dp(3),dp(4),dp(3),dp(7))}
  body.addView(tabs)
  body.addView(shownCount)
  body.addView(list)

  var limit=40
  var current:List<MarketDrug> = emptyList()

  fun updateTabs(){
    tabs.removeAllViews()
    tabNames.forEachIndexed{idx,name->
      val t=tv(name,11f,if(idx==selectedTab)fg() else muted(),idx==selectedTab)
      t.gravity=Gravity.CENTER
      t.background=glass(if(idx==selectedTab)Color.rgb(20,52,47) else card(),if(idx==selectedTab)accent:if(dark())Color.rgb(43,56,76) else Color.rgb(218,225,236),16)
      t.setPadding(dp(8),0,dp(8),0)
      t.setOnClickListener{selectedTab=idx;limit=40;renderNowImpl()}
      tabs.addView(t,LinearLayout.LayoutParams(0,dp(42),1f).apply{leftMargin=dp(2);rightMargin=dp(2)})
    }
  }

  fun addCard(d:MarketDrug){
    val c=LinearLayout(this).apply{
      orientation=LinearLayout.VERTICAL
      setPadding(dp(15),dp(13),dp(15),dp(13))
      background=glass()
      setOnClickListener{marketDrugDetail(d)}
    }
    val image=ImageView(this).apply{scaleType=ImageView.ScaleType.CENTER_INSIDE;setPadding(dp(4),dp(4),dp(4),dp(4));layoutParams=LinearLayout.LayoutParams(dp(92),dp(92)).apply{rightMargin=dp(10)}}
    val top=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL}
    top.addView(image)
    loadDrugImage(d,image)
    val badge=tv("💊",28f,accent,true).apply{gravity=Gravity.CENTER}
    top.addView(badge,LinearLayout.LayoutParams(dp(46),dp(52)))
    val names=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutParams=LinearLayout.LayoutParams(0,-2,1f)}
    names.addView(tv(if(d.ar.isBlank())d.en else d.ar,17f,fg(),true))
    names.addView(tv(d.en,11f,accent,true).apply{maxLines=2})
    top.addView(names)
    c.addView(top)
    c.addView(tv(if(d.category.isBlank())"تصنيف غير متاح" else d.category,10f,muted()).apply{setPadding(0,dp(7),0,dp(2))})
    c.addView(tv(if(d.scientific.isBlank())"المادة الفعالة: غير متاحة" else "المادة الفعالة: ${d.scientific}",11f,fg()))
    c.addView(tv(if(d.route.isBlank())"الشكل/الطريق: غير متاح" else "الشكل/الطريق: ${d.route}",10f,muted()).apply{setPadding(0,dp(3),0,dp(3))})
    c.addView(tv(if(d.manufacturer.isBlank())"الشركة: غير متاحة" else "الشركة: ${d.manufacturer}",10f,muted()))
    val priceRow=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(0,dp(9),0,0)}
    priceRow.addView(tv(if(d.price!=null)"${fmt(d.price!!)} جنيه" else "السعر غير متاح",18f,accent,true),LinearLayout.LayoutParams(0,-2,1f))
    priceRow.addView(tv("عرض التفاصيل  ›",11f,muted(),true))
    c.addView(priceRow)
    list.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(9)})
  }

  fun computeFiltered(query:String,cat:String,comp:String,sortPos:Int,tab:Int):List<MarketDrug>{
    val data=if(marketDrugs.isNotEmpty())marketDrugs else drugs.map{MarketDrug(it.generic,it.ar,it.generic,"—",it.category,it.category,null)}
    var out=data.asSequence().filter{
      query.isEmpty() || (it.en+" "+it.ar+" "+it.scientific+" "+it.manufacturer+" "+it.category).lowercase(java.util.Locale.ROOT).contains(query)
    }.filter{cat=="كل التصنيفات"||it.category==cat}.filter{comp=="كل الشركات"||it.manufacturer==comp}.toList()
    out=when(tab){
      1->out.sortedWith(compareBy<MarketDrug>{it.price==null}.thenBy{it.price?:Double.MAX_VALUE})
      2->out.sortedByDescending{it.price?:-1.0}
      3->out.sortedBy{it.ar.ifBlank{it.en}.lowercase(java.util.Locale.ROOT)}
      else->out
    }
    out=when(sortPos){
      1->out.sortedBy{it.ar.ifBlank{it.en}.lowercase(java.util.Locale.ROOT)}
      2->out.sortedWith(compareBy<MarketDrug>{it.price==null}.thenBy{it.price?:Double.MAX_VALUE})
      3->out.sortedByDescending{it.price?:-1.0}
      else->out
    }
    return out
  }

  fun renderNowImpl(dataOverride:List<MarketDrug>?=null){
    list.removeAllViews()
    val data=dataOverride ?: computeFiltered(
      search.text?.toString()?.trim()?.lowercase(java.util.Locale.ROOT).orEmpty(),
      category.selectedItem?.toString().orEmpty(),
      company.selectedItem?.toString().orEmpty(),
      sort.selectedItemPosition,
      selectedTab
    )
    current=data
    val shown=data.take(limit)
    shown.forEach{addCard(it)}
    shownCount.text="عرض ${shown.size} من ${data.size} نتيجة"
    status.text=if(marketLoaded)"قاعدة السوق: ${marketDrugs.size} دواء • البحث والتصفية في الخلفية" else "الوضع السريع: ${drugs.size} دواء تعليمي مدمج • اضغط تحديث لتحميل قاعدة السوق"
    if(data.isEmpty())list.addView(tv("لا توجد نتائج مطابقة للبحث أو الفلاتر.",14f,muted()).apply{setPadding(dp(12),dp(25),dp(12),dp(25))})
    else if(data.size>shown.size){
      list.addView(btn("عرض المزيد من الأدوية"){limit+=40;renderNowImpl(data)},LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(10)})
    }
  }

  updateTabs()

  fun refreshFilters(){
    val data=marketDrugs
    val cats=listOf("كل التصنيفات")+data.map{it.category}.filter{it.isNotBlank()}.distinct().sorted()
    val comps=listOf("كل الشركات")+data.map{it.manufacturer}.filter{it.isNotBlank()&&it!="—"}.distinct().sorted().take(200)
    category.adapter=spinnerAdapter(cats);company.adapter=spinnerAdapter(comps)
  }

  val watcher=object:android.text.TextWatcher{
    override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){}
    override fun onTextChanged(s:CharSequence?,st:Int,b:Int,c:Int){
      limit=40
      val generation=renderGeneration.incrementAndGet()
      val query=s?.toString()?.trim()?.lowercase(java.util.Locale.ROOT).orEmpty()
      val cat=category.selectedItem?.toString().orEmpty()
      val comp=company.selectedItem?.toString().orEmpty()
      val sortPos=sort.selectedItemPosition
      val tab=selectedTab
      status.text="جاري البحث…"
      renderExecutor.execute{
        val matches=computeFiltered(query,cat,comp,sortPos,tab)
        runOnUiThread{if(generation==renderGeneration.get()&&page=="medicines")renderNowImpl(matches)}
      }
    }
    override fun afterTextChanged(e:android.text.Editable?){}
  }
  search.addTextChangedListener(watcher)
  refreshFilters()
  category.onItemSelectedListener=object:AdapterView.OnItemSelectedListener{override fun onNothingSelected(p:AdapterView<*>?){};override fun onItemSelected(p:AdapterView<*>?,v:android.view.View?,pos:Int,id:Long){limit=40;renderNowImpl()}}
  company.onItemSelectedListener=category.onItemSelectedListener
  sort.onItemSelectedListener=category.onItemSelectedListener
  renderNowImpl()
 }

  private fun loadMarketDrugs(status:TextView){if(marketLoading){toast("جاري تحديث قاعدة الأدوية بالفعل…");return};marketLoading=true;status.text="جاري تحديث قاعدة الأدوية في الخلفية…";Thread{try{val conn=(java.net.URL(marketUrl).openConnection() as java.net.HttpURLConnection).apply{connectTimeout=10000;readTimeout=20000;requestMethod="GET";setRequestProperty("User-Agent","Hakamo-IV-Calculator/6.2.2")};val json=conn.inputStream.bufferedReader(Charsets.UTF_8).use{it.readText()};conn.disconnect();val arr=org.json.JSONArray(json);val out=ArrayList<MarketDrug>(arr.length());for(i in 0 until arr.length()){val o=arr.getJSONObject(i);val price=if(o.isNull("price_egp"))null else o.optDouble("price_egp",Double.NaN).takeIf{!it.isNaN()};out.add(MarketDrug(o.optString("commercial_name_en"),o.optString("commercial_name_ar"),o.optString("scientific_name"),o.optString("manufacturer"),o.optString("route"),o.optString("drug_class"),price))};marketDrugs=out.sortedBy{it.ar.ifBlank{it.en}.lowercase(java.util.Locale.ROOT)};marketLoaded=true;getPreferences(0).edit().putLong("market_update",System.currentTimeMillis()).apply();runOnUiThread{marketLoading=false;medicines()}}catch(e:Exception){runOnUiThread{marketLoading=false;status.text="تعذر التحديث الآن — التطبيق يعمل بالوضع السريع. ${e.javaClass.simpleName}"}}}.start()}

  override fun onDestroy(){imageExecutor.shutdownNow();renderExecutor.shutdownNow();super.onDestroy()}

  private fun marketDrugDetail(d:MarketDrug){page="marketDrug";parentPage="medicines";clear("💊 ${if(d.ar.isBlank())d.en else d.ar}",d.en)
   val hero=ImageView(this).apply{scaleType=ImageView.ScaleType.CENTER_INSIDE;setPadding(dp(10),dp(10),dp(10),dp(10));background=glass()}
   body.addView(hero,LinearLayout.LayoutParams(-1,dp(220)).apply{bottomMargin=dp(8)})
   loadDrugImage(d,hero,"-detail")
   body.addView(tv("🖼️ صورة العبوة من دواء جيت — تُحمّل عند توفر الإنترنت",10f,muted()).apply{setPadding(dp(4),0,dp(4),dp(10))})
  fun section(title:String,text:String){val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(15),dp(13),dp(15),dp(13));background=glass()};c.addView(tv(title,13f,accent,true));c.addView(tv(text,14f,fg()));body.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})}
  section("السعر المعلن في قاعدة البيانات",if(d.price!=null)"${fmt(d.price!!)} جنيه مصري" else "غير متاح")
  section("المادة الفعالة / التركيب",if(d.scientific.isBlank())"غير متاح في السجل" else d.scientific)
  section("الشركة المصنعة",d.manufacturer.ifBlank{"غير متاحة"})
  section("الشكل / طريق الإعطاء",d.route.ifBlank{"غير متاح"})
  section("التصنيف",d.category.ifBlank{"غير متاح"})
  section("تنبيه السعر","السعر قد يتغير مع قرارات التسعير وتحديثات السوق، وقد يختلف عن سعر صيدلية بعينها. تحقق من المصدر الرسمي أو الصيدلي قبل الاعتماد عليه.")
  body.addView(btn("🌐 فتح قاعدة الأسعار للتحقق الآن"){try{startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.dawaagate.com/medicines")))}catch(_:Exception){toast("تعذر فتح الرابط")}},LinearLayout.LayoutParams(-1,dp(54)))
 }

 private fun safety(){
  page="safety"; parentPage="home"
  clear("🛡️ سلامة إعطاء الدواء","قائمة فحص تمريضية قبل إعطاء الدواء")
  val items=arrayOf("1. المريض الصحيح","2. الدواء الصحيح","3. الجرعة الصحيحة","4. الطريق الصحيح","5. الوقت الصحيح","6. التحقق من الحساسية","7. التحقق من الصلاحية والتركيز","8. مراجعة التداخلات والتنبيهات","9. التوثيق بعد الإعطاء","10. مراقبة الاستجابة والآثار الجانبية")
  items.forEachIndexed{idx,x->
    val c=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(14),dp(13),dp(14),dp(13));background=glass()}
    c.addView(tv("✓",22f,accent,true),LinearLayout.LayoutParams(dp(42),-2))
    c.addView(tv(x,15f,fg(),true))
    body.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(8)})
  }
  body.addView(tv("تنبيه: هذه قائمة تحقق تعليمية وليست بديلًا عن سياسة المنشأة أو وصف الطبيب/الصيدلي.",12f,muted()).apply{setPadding(dp(4),dp(12),dp(4),dp(20))})
 }
 private fun contact(){
  page="contact"; parentPage="home"
  clear("📲 تواصل مع Hakamo","روابط التواصل الرسمية")
  fun link(label:String,url:String){
    val c=LinearLayout(this).apply{
      orientation=LinearLayout.HORIZONTAL
      gravity=Gravity.CENTER_VERTICAL
      setPadding(dp(15),dp(12),dp(15),dp(12))
      background=glass()
      setOnClickListener{
        try{startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(url)))}catch(_:Exception){toast("تعذر فتح الرابط")}
      }
    }
    c.addView(tv(label,15f,fg(),true),LinearLayout.LayoutParams(0,-2,1f))
    c.addView(tv("›",28f,accent))
    body.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})
  }
  link("📢 Telegram • قناة Hakamo","https://t.me/Hakamo99")
  link("🎵 TikTok • @hakamo99","https://www.tiktok.com/@hakamo99?_r=1&_t=ZS-91x1jX1y20z")
  link("📘 Facebook • Hakamo","https://www.facebook.com/share/19QwF7nvvf/")
  link("▶️ YouTube • @hakamo99","https://youtube.com/@hakamo99")
  link("📚 كل اللي هتحتاجه في القناة","https://t.me/Hakamo99/1978")
  body.addView(tv("للتواصل والاستفسارات استخدم روابط Hakamo الرسمية بالأعلى.",12f,muted()).apply{setPadding(dp(4),dp(10),dp(4),dp(10))})
 }

 private fun procedures(){
  page="procedures"; parentPage="home"
  clear("🧰 إجراءات التمريض","شرح عربي تعليمي للإجراءات التمريضية الأساسية")
  val note=tv("⚠️ هذا القسم تعليمي. التطبيق العملي يجب أن يتم بعد التدريب وتحت إشراف المختص ووفق سياسة المنشأة ومكافحة العدوى.",12f,muted())
  note.setPadding(dp(4),dp(4),dp(4),dp(14)); body.addView(note)
  data class P(val title:String,val icon:String,val purpose:String,val steps:String,val safety:String)
  val items=listOf(
   P("قياس العلامات الحيوية","🩺","قياس الحرارة والنبض والتنفس وضغط الدم وتشبع الأكسجين لتقييم حالة المريض.","1) عرّف نفسك وتحقق من هوية المريض.\n2) اشرح الإجراء واحصل على التعاون المناسب.\n3) اجعل المريض في وضع مناسب وهادئ.\n4) قِس الحرارة بالطريقة المناسبة.\n5) قِس النبض وسجّل المعدل والانتظام.\n6) احسب معدل التنفس دون تغيير نمط تنفس المريض قدر الإمكان.\n7) قِس ضغط الدم بكفة مناسبة للذراع.\n8) قِس SpO₂ عند الحاجة وسجّل طريقة إعطاء الأكسجين إن وُجدت.\n9) سجّل النتائج وبلّغ عن القيم غير الطبيعية وفق بروتوكول المنشأة.","تأكد من حجم الكفة الصحيح، وأعد القياس عند وجود نتيجة غير متوقعة وفق سياسة المنشأة."),
   P("غسل اليدين","🧼","تقليل انتقال الميكروبات بين المريض ومقدم الرعاية والبيئة.","1) انزع ما يمكن أن يعيق التنظيف.\n2) استخدم الماء والصابون عند الحاجة أو مطهر اليدين الكحولي عندما يكون مناسبًا.\n3) افرك جميع أسطح اليدين وبين الأصابع وحول الأظافر.\n4) التزم بالمدة والطريقة المعتمدة في المنشأة.\n5) جفف اليدين جيدًا عند استخدام الماء والصابون.\n6) تجنب لمس الأسطح الملوثة بعد التنظيف.","نظافة اليدين جزء أساسي من سلامة المريض، واستخدم القفازات كحاجز إضافي وليس كبديل لنظافة اليدين."),
   P("ارتداء وخلع معدات الوقاية الشخصية","🥼","حماية المريض ومقدم الرعاية من التعرض للسوائل أو العوامل المعدية.","1) قيّم نوع التعرض وحدد معدات الوقاية المطلوبة.\n2) جهّز المعدات قبل الدخول.\n3) ارتدِ معدات الوقاية بالترتيب الذي تعتمده المنشأة مع التأكد من التغطية المناسبة.\n4) نفّذ الرعاية مع تجنب لمس الوجه والأسطح غير الضرورية.\n5) انزع المعدات بحذر لتجنب تلويث الجلد أو الملابس.\n6) تخلّص من المعدات حسب نوع النفايات ونظّف اليدين بعد الخلع.","ترتيب ارتداء وخلع معدات الوقاية قد يختلف حسب نوع العزل وسياسة المنشأة."),
   P("تركيب الكانيولا الوريدية","💉","إنشاء مدخل وريدي لإعطاء السوائل أو الأدوية أو سحب عينات وفق الصلاحيات.","1) تحقق من أمر العلاج وهوية المريض والحساسية.\n2) جهّز الكانيولا والمطهر والضماد والأدوات اللازمة.\n3) نظّف اليدين وارتدِ القفازات المناسبة.\n4) اختر الوريد والمقاس المناسبين وفق حالة المريض والبروتوكول.\n5) طبّق المطهر واتركه يجف حسب تعليمات المنتج.\n6) أدخل الكانيولا بالطريقة المعتمدة، ثم ثبّتها ووصلها بالنظام المناسب.\n7) افحص مكان الإدخال وراقب الألم أو التورم أو التسرب.\n8) ثبّت الكانيولا وسجّل المكان والمقاس والوقت حسب سياسة المنشأة.","لا تستخدم موقعًا به علامات التهاب أو إصابة. أوقف الاستخدام وقيّم المريض عند ظهور تسرب أو تورم أو ألم أو علامات عدوى."),
   P("إعطاء الحقن العضلي","💉","إعطاء دواء في النسيج العضلي عندما يكون هذا الطريق موصوفًا ومناسبًا.","1) تحقق من الدواء والجرعة والطريق والوقت وهوية المريض والحساسية.\n2) جهّز الدواء والأدوات بطريقة آمنة.\n3) نظّف اليدين وارتدِ القفازات عند الحاجة.\n4) اختر الموقع المناسب حسب العمر والدواء وحالة المريض.\n5) نظّف الجلد واترك المطهر يجف.\n6) نفّذ الحقن بالتقنية المعتمدة في المنشأة.\n7) تخلّص من الإبرة فورًا في حاوية الأدوات الحادة دون إعادة تغطيتها إلا إذا نص بروتوكول محدد على غير ذلك.\n8) راقب المريض وسجّل الإعطاء.","اختيار الموقع والمقاس والطريقة يعتمد على الدواء وعمر المريض وكتلة العضلة وسياسة المنشأة."),
   P("إعطاء حقن تحت الجلد","💉","إعطاء بعض الأدوية في النسيج تحت الجلد وفق الوصفة.","1) تحقق من الدواء والجرعة والطريق وهوية المريض.\n2) اختر موقعًا مناسبًا وافحص الجلد.\n3) نظّف اليدين وجهّز الأدوات.\n4) نظّف الموقع عند الحاجة واتركه يجف.\n5) استخدم التقنية والزاوية المناسبتين لنوع الدواء والإبرة.\n6) أعطِ الجرعة ثم تخلّص من الإبرة بأمان.\n7) راقب الموقع والمريض وسجّل الإجراء.","لا تدلك موضع بعض الأدوية بعد الحقن إلا إذا نصت التعليمات على ذلك."),
   P("تركيب القسطرة البولية","🧴","تصريف البول أو الحصول على قياس دقيق للإخراج عند وجود سبب سريري مناسب.","1) تحقق من وجود داعٍ سريري وأمر مناسب حسب النظام المحلي.\n2) اشرح الإجراء واحفظ خصوصية المريض.\n3) جهّز مجموعة القسطرة المعقمة والأدوات المطلوبة.\n4) نظّف اليدين واستخدم معدات الوقاية.\n5) حافظ على تقنية معقمة ونظّف المنطقة بالطريقة المعتمدة.\n6) أدخل القسطرة بلطف وفق التدريب والبروتوكول.\n7) ثبّت القسطرة ووصلها بنظام تصريف مغلق.\n8) حافظ على كيس التصريف أسفل مستوى المثانة وتجنب التواء الأنبوب.\n9) وثّق الإجراء وراقب كمية البول وأي علامات عدوى أو إصابة.","قسطرة البول إجراء عالي الحساسية لمكافحة العدوى؛ لا تُركّب دون حاجة، واتبع بروتوكول المنشأة حرفيًا."),
   P("تركيب أنبوب أنفي معدي","👃","إدخال أنبوب عبر الأنف إلى المعدة لأغراض محددة مثل التغذية أو التصريف حسب الحالة.","1) تحقق من السبب السريري وموانع الإجراء حسب البروتوكول.\n2) اشرح الإجراء وضع المريض في الوضع المناسب.\n3) جهّز الأنبوب والمزلّق والمعدات المطلوبة.\n4) قِس الطول بالطريقة المعتمدة وحدد العلامة الخارجية.\n5) أدخل الأنبوب بلطف مع مراقبة المريض.\n6) لا تجبر الأنبوب عند وجود مقاومة.\n7) تحقّق من موضع الأنبوب بالطريقة المعتمدة قبل استخدامه للتغذية أو الدواء.\n8) ثبّت الأنبوب وسجّل العلامة الخارجية والنتائج.","التأكد من موضع الأنبوب قبل الاستخدام أمر أساسي؛ لا تعتمد على طريقة غير معتمدة للتحقق من الموضع."),
   P("إعطاء الأكسجين","🫁","رفع إمداد الأكسجين للمريض عند وجود حاجة سريرية وأمر أو بروتوكول مناسب.","1) قيّم التنفس وSpO₂ وحالة المريض.\n2) اختر وسيلة الأكسجين المناسبة وفق الحالة والهدف المحدد.\n3) تأكد من مصدر الأكسجين وسلامة التوصيلات.\n4) اضبط التدفق أو الإعداد حسب الأمر أو البروتوكول.\n5) ضع الجهاز بطريقة صحيحة ومريحة.\n6) أعد تقييم التنفس وSpO₂ واستجابة المريض.\n7) سجّل الوسيلة والإعداد والنتيجة.","الأكسجين علاج دوائي في كثير من السياقات؛ لا تغيّر الهدف أو التدفق عشوائيًا، وانتبه لسلامة الحريق."),
   P("شفط الإفرازات","🫁","إزالة الإفرازات عندما تؤثر في مجرى الهواء أو التهوية وفق تقييم المريض.","1) قيّم الحاجة ونمط التنفس والأصوات التنفسية وSpO₂.\n2) جهّز جهاز الشفط والمعدات الواقية.\n3) اشرح الإجراء قدر الإمكان.\n4) استخدم التقنية المعقمة أو النظيفة حسب نوع الإجراء والبروتوكول.\n5) نفّذ الشفط بأقل مدة وفعالية مناسبة وتابع استجابة المريض.\n6) أوقف الإجراء عند تدهور الحالة أو ظهور علامات عدم تحمل.\n7) أعد تقييم المريض وسجّل الإفرازات والاستجابة.","تجنب الشفط الروتيني دون حاجة؛ راقب نقص الأكسجة واضطراب النبض أو التشنج القصبي."),
   P("تغيير الغيار على الجرح","🩹","تنظيف الجرح وحمايته ومتابعة علامات الالتئام أو العدوى.","1) قيّم الجرح وراجع نوع الغيار الموصوف.\n2) جهّز الأدوات وحافظ على الخصوصية.\n3) نظّف اليدين وارتدِ معدات الوقاية المناسبة.\n4) أزل الغيار القديم بحذر وافحص الإفرازات والرائحة والألم.\n5) نظّف الجرح بالطريقة والمحلول الموصوفين.\n6) قيّم اللون والأنسجة والحواف والجلد المحيط.\n7) ضع الغيار الجديد وفق الخطة.\n8) تخلّص من النفايات بأمان وسجّل القياسات والملاحظات.","ازدياد الألم أو الاحمرار أو الحرارة أو الإفرازات أو الرائحة أو تدهور الجرح يستلزم التقييم والإبلاغ وفق البروتوكول."),
   P("قياس سكر الدم بجهاز القياس","🩸","الحصول على قراءة سريعة لسكر الدم للمساعدة في التقييم والمتابعة.","1) تحقق من هوية المريض وسبب القياس.\n2) اغسل اليدين وجهّز الجهاز وشريط الاختبار.\n3) تحقق من صلاحية الشريط وإعداد الجهاز حسب التعليمات.\n4) اختر موقع الوخز المناسب ونظّفه حسب تعليمات الجهاز.\n5) نفّذ الوخز وخذ العينة بالطريقة الصحيحة.\n6) اقرأ النتيجة وسجّل الوقت والنتيجة.\n7) تخلّص من أداة الوخز فورًا في حاوية الأدوات الحادة.\n8) قيّم الأعراض وبلّغ عن القيم الحرجة حسب البروتوكول.","القيم الحرجة والإجراءات التالية تعتمد على بروتوكول المنشأة وحالة المريض."),
   P("جمع عينة بول","🧪","الحصول على عينة بول مناسبة للتحليل أو الزراعة حسب المطلوب.","1) تحقق من نوع التحليل وتعليمات المختبر.\n2) اشرح للمريض طريقة الجمع المطلوبة.\n3) جهّز العبوة المناسبة والمُلصق.\n4) اطلب العينة بالطريقة المحددة، مثل العينة النظيفة من منتصف المجرى عند طلبها.\n5) أغلق العبوة دون تلويث داخلها.\n6) اكتب بيانات العينة في المكان الصحيح.\n7) أرسلها للمختبر ضمن الزمن وظروف النقل المطلوبة.","لا تضع العينة في وعاء غير مخصص ولا تتركها دون حفظ أو نقل وفق تعليمات المختبر."),
   P("نقل الدم ومراقبته","🩸","إعطاء مكونات الدم بأمان مع اكتشاف تفاعلات النقل مبكرًا.","1) تحقق من الأمر والموافقة والهوية والفصيلة والمطابقة وفق نظام المنشأة.\n2) افحص كيس الدم وتاريخ الصلاحية وسلامته.\n3) قِس العلامات الحيوية الأساسية.\n4) ابدأ النقل وفق البروتوكول وراقب المريض عن قرب في البداية.\n5) راقب الحرارة والنبض والضغط والتنفس والأعراض.\n6) عند الاشتباه في تفاعل: أوقف النقل واتبع بروتوكول المنشأة فورًا واطلب المساعدة.\n7) وثّق المكوّن والوقت والكمية والاستجابة.","نقل الدم إجراء عالي الخطورة؛ اتبع سياسة بنك الدم والمنشأة ولا تعتمد على هذا الملخص بدل البروتوكول."),
   P("التعامل مع إصابة وخز الإبرة","🩸","تقليل خطر التعرض المهني للدم أو سوائل الجسم بعد إصابة عرضية.","1) أوقف العمل بأمان.\n2) اغسل موضع الإصابة بالماء والصابون دون عصر الجرح بقوة.\n3) إذا تعرضت العين أو الأغشية لسائل، اغسلها بالماء أو المحلول المناسب.\n4) أبلغ المسؤول فورًا.\n5) اطلب تقييمًا طبيًا مهنيًا عاجلًا حسب نظام المنشأة.\n6) وثّق الحادث واتبع إجراءات تقييم التعرض والمتابعة.","لا تؤخر الإبلاغ والتقييم؛ بعض تدخلات ما بعد التعرض تكون مرتبطة بالوقت."),
   P("نقل المريض من السرير إلى الكرسي","🧑‍🦽","نقل المريض بأمان مع تقليل خطر السقوط وإصابات المريض أو مقدم الرعاية.","1) قيّم قدرة المريض على الحركة وخطر السقوط.\n2) اشرح الخطوات وتأكد من ارتداء حذاء أو وسيلة مناسبة.\n3) ثبّت السرير والكرسي وأبعد العوائق.\n4) استخدم وسيلة المساعدة المناسبة واطلب دعمًا إضافيًا عند الحاجة.\n5) ساعد المريض على الجلوس والوقوف تدريجيًا.\n6) انقل المريض ببطء مع حماية الأنابيب والقساطر.\n7) ثبّت المريض في الكرسي وتأكد من الراحة والأمان.","لا تنقل مريضًا غير مستقر أو غير قادر على تحمل الوزن دون وسيلة ومساعدة مناسبة وفق سياسة المنشأة."),
   P("إعطاء الدواء عن طريق الفم","💊","إعطاء الدواء بالفم بأمان بعد التحقق من الوصفة وهوية المريض وقدرته على البلع.","1) تحقّق من اسم الدواء والجرعة والطريق والوقت وهوية المريض والحساسية.\n2) اغسل اليدين وجهّز الدواء وفق سياسة المنشأة.\n3) قيّم قدرة المريض على البلع وحالته الوعي.\n4) أعطِ الدواء بالطريقة الموصوفة وتأكد من ابتلاعه عندما يلزم.\n5) لا تسحق أو تفتح الدواء ممتد المفعول أو المغلف معويًا إلا بتوجيه صيدلي.\n6) وثّق الإعطاء وراقب الاستجابة.","لا تعطِ دواءً فمويًا لمريض لديه خطر شفط أو اضطراب وعي دون تقييم وتوجيه مناسب."),
   P("إعطاء الدواء تحت اللسان","💊","إعطاء أدوية محددة تحت اللسان للسماح بالامتصاص عبر الغشاء المخاطي.","1) تحقق من الدواء والجرعة وهوية المريض والحساسية.\n2) اشرح أن القرص يوضع تحت اللسان ولا يُبتلع أو يُمضغ إذا كانت التعليمات تنص على ذلك.\n3) ضع الدواء بالطريقة الصحيحة.\n4) اطلب من المريض تركه حتى يذوب وفق تعليمات الدواء.\n5) راقب التأثيرات والآثار الجانبية وسجّل الإعطاء.","اتبع تعليمات المنتج لأن طريقة الاستخدام تختلف بين الأدوية."),
   P("إعطاء الدواء عن طريق الاستنشاق","🌬️","إعطاء الأدوية المستنشقة باستخدام الجهاز الموصوف لتحسين وصول الدواء إلى الجهاز التنفسي.","1) تحقق من الدواء والجرعة والطريق.\n2) اشرح طريقة استخدام البخاخ أو جهاز الاستنشاق.\n3) ساعد المريض على اتخاذ وضع مناسب.\n4) طبّق خطوات الجهاز حسب تعليماته، وقد يُستخدم spacer عند وصفه.\n5) قيّم التنفس والاستجابة وسجّل الجرعة.","التقنية الخاطئة تقلل وصول الدواء؛ راجع تعليمات الجهاز والتدريب العملي."),
   P("إعطاء المحاليل الوريدية","💧","إعطاء سوائل وريدية حسب الخطة العلاجية مع مراقبة المريض ومكان الوصول الوريدي.","1) تحقق من نوع المحلول ومعدل التسريب وهوية المريض.\n2) افحص العبوة والوصلة وتاريخ الصلاحية وسلامة الخط.\n3) تأكد من سلامة الوصول الوريدي.\n4) اضبط معدل التسريب وفق الأمر أو الجهاز الموصوف.\n5) راقب موقع القسطرة والمريض والعلامات الحيوية عند الحاجة.\n6) سجّل نوع السائل والكمية والمعدل والاستجابة.","راقب التسرب والارتشاح والالتهاب وزيادة السوائل حسب حالة المريض."),
   P("حساب معدل المحلول بالتنقيط","🧮","حساب معدل التدفق اليدوي عند استخدام مجموعة تنقيط ذات معامل معروف.","1) حدّد الحجم المطلوب بالمليلتر والوقت بالدقائق.\n2) راجع معامل التنقيط المكتوب على مجموعة المحلول.\n3) استخدم المعادلة: عدد القطرات/دقيقة = الحجم × معامل التنقيط ÷ الزمن بالدقائق.\n4) قرّب النتيجة إلى عدد صحيح عند الحاجة.\n5) تحقق من الحساب مرة أخرى وفق سياسة المنشأة.","هذا حساب تعليمي؛ يجب التحقق المستقل من جرعات ومعدلات السوائل قبل إعطائها."),
   P("تركيب جهاز قياس البول ومتابعة الإخراج","🧴","قياس إخراج البول ومتابعته بصورة دقيقة ضمن متابعة توازن السوائل.","1) تأكد من أن نظام التصريف مغلق وسليم.\n2) حافظ على كيس البول أسفل مستوى المثانة.\n3) تجنب ثني الأنبوب أو وضع الكيس على الأرض.\n4) قِس الكمية بالطريقة المعتمدة وسجّل الوقت.\n5) راقب اللون والمظهر وأي تغيرات غير معتادة.","قلة البول أو تغيره بشكل واضح قد يحتاج إلى تقييم سريع حسب حالة المريض."),
   P("العناية بالقسطرة البولية","🧴","تقليل مضاعفات القسطرة والمحافظة على نظام تصريف سليم.","1) نظّف اليدين واستخدم معدات الوقاية المناسبة.\n2) افحص مكان الدخول والأنبوب والكيس.\n3) حافظ على النظام المغلق ما لم توجد ضرورة سريرية.\n4) نظّف المنطقة حسب سياسة المنشأة دون إجراءات غير لازمة.\n5) تأكد من حرية تدفق البول وتثبيت الأنبوب.\n6) وثّق الملاحظات وأبلغ عن علامات العدوى أو الانسداد.","تجنب فصل النظام دون داعٍ، وقيّم الحاجة لاستمرار القسطرة يوميًا وفق سياسة المنشأة."),
   P("العناية بأنبوب الأنف المعدي","👃","الحفاظ على سلامة الأنبوب والأنف والجلد ومراقبة المريض.","1) افحص العلامة الخارجية للأنبوب وثباته.\n2) قيّم الأنف والجلد المحيط.\n3) حافظ على نظافة الفم والأنف حسب الخطة.\n4) تحقّق من موضع الأنبوب بالطريقة المعتمدة قبل استخدامه.\n5) راقب الغثيان والقيء والانتفاخ والسعال أو صعوبة التنفس.\n6) وثّق الملاحظات.","أي شك في تغير موضع الأنبوب يستلزم إيقاف استخدامه والتحقق وفق البروتوكول."),
   P("تغذية المريض عبر الأنبوب","🥣","إعطاء التغذية الأنبوبية وفق الخطة الموصوفة مع تقليل خطر الاستنشاق.","1) تحقق من نوع التغذية والكمية والطريقة.\n2) تأكد من موضع الأنبوب وفق البروتوكول قبل الاستخدام.\n3) ضع المريض في الوضع الموصى به.\n4) أعطِ التغذية بالمعدل والطريقة المحددين.\n5) راقب التحمل والسعال والقيء والانتفاخ وأي تغير تنفسي.\n6) اتبع تعليمات غسل الأنبوب والتوثيق.","لا تستخدم الأنبوب عند الشك في موضعه، واتبع خطة التغذية وتعليمات الفريق المختص."),
   P("العناية بفتحة القصبة الهوائية","🫁","المحافظة على مجرى الهواء والجلد المحيط بالـTracheostomy ومراقبة المضاعفات.","1) قيّم التنفس وSpO₂ ومظهر الإفرازات.\n2) جهّز معدات الطوارئ والشفط المناسبة حسب الوحدة.\n3) نظّف اليدين واستخدم معدات الوقاية.\n4) افحص الجلد والفتحة والتثبيت.\n5) نفّذ تنظيف أو تغيير الضماد وفق البروتوكول والتدريب.\n6) راقب الانسداد أو النزيف أو صعوبة التنفس وأبلغ فورًا عند حدوثها.","إجراء عالي الخطورة؛ يجب أن يتم وفق تدريب متخصص وبروتوكول المنشأة."),
   P("العناية بالخط الوريدي المركزي","💉","تقليل العدوى والمضاعفات المرتبطة بالقسطرة الوريدية المركزية.","1) تحقّق من الحاجة للخط وحالة الموقع.\n2) نظّف اليدين واستخدم معدات الوقاية.\n3) افحص الضماد والموقع والوصلة.\n4) حافظ على التعقيم أثناء أي وصول للخط.\n5) غيّر الضماد أو الوصلات وفق سياسة المنشأة.\n6) راقب الألم والاحمرار والإفرازات والحمى أو مشاكل التدفق.","التعامل مع الخط المركزي يجب أن يطابق حزمة الوقاية وسياسة مكافحة العدوى في المنشأة."),
   P("العناية بالدرنقة الجراحية","🩹","متابعة كمية وطبيعة الإفرازات والحفاظ على سلامة نظام التصريف.","1) افحص مكان خروج الدرنقة والجلد المحيط.\n2) تأكد من تثبيت الأنبوب وسلامة نظام التصريف.\n3) قِس الإفرازات وسجّل اللون والقوام والكمية والوقت.\n4) حافظ على الضماد وفق الخطة.\n5) راقب الألم والحمى والتسرب أو الانفصال.\n6) أبلغ عن أي تغير مفاجئ أو مقلق.","لا تفصل أو تضغط أو تغلق نظام التصريف إلا حسب تعليمات الفريق الجراحي."),
   P("الوقاية من قرح الضغط","🛏️","تقليل خطر تلف الجلد والأنسجة لدى المرضى محدودي الحركة.","1) قيّم خطر قرحة الضغط وحالة الجلد.\n2) غيّر وضعية المريض وفق الخطة وحالته.\n3) استخدم أسطح الدعم المناسبة عند الحاجة.\n4) حافظ على الجلد نظيفًا وجافًا وتعامل مع الرطوبة.\n5) راقب مناطق الضغط والعجز والكعبين.\n6) اهتم بالتغذية والترطيب حسب الخطة وأبلغ عن أي تلف جلدي.","لا تعتمد على التدليك فوق مناطق احمرار مستمر؛ اتبع سياسة الوقاية والعلاج في المنشأة."),
   P("العناية بالفم للمريض غير القادر على العناية بنفسه","🪥","الحفاظ على نظافة الفم وتقليل الجفاف والمضاعفات.","1) قيّم مستوى الوعي وخطر الاختناق.\n2) جهّز الأدوات وارتدِ معدات الوقاية.\n3) ضع المريض في وضع آمن.\n4) نظّف الأسنان واللسان واللثة بلطف حسب حالته.\n5) استخدم الشفط عند الحاجة وبحسب التدريب.\n6) رطّب الفم حسب الخطة وسجّل الملاحظات.","في المرضى ذوي خطر الاستنشاق، يجب اختيار الوضع والطريقة المناسبة بعناية."),
   P("العناية بالعين","👁️","حماية العين والمحافظة على نظافتها وترطيبها عند الحاجة.","1) قيّم العين والجفن والإفرازات.\n2) اغسل اليدين وارتدِ القفازات عند الحاجة.\n3) نظّف من الجزء الأنظف إلى الأقل نظافة باستخدام أداة مناسبة.\n4) استخدم قطرات أو مرهمًا إذا كان موصوفًا وبالطريقة الصحيحة.\n5) لا تلمس طرف العبوة العين أو الجلد.\n6) وثّق أي تغيرات غير طبيعية.","الألم الشديد أو تغير الرؤية أو إصابة العين يحتاج إلى تقييم طبي مناسب."),
   P("جمع عينة دم للتحاليل","🩸","جمع عينة دم صحيحة ووضع الملصق عليها وإرسالها للمختبر.","1) تحقق من التحاليل المطلوبة وهوية المريض.\n2) جهّز الأنابيب والأدوات حسب نوع التحليل.\n3) اغسل اليدين وارتدِ معدات الوقاية.\n4) اختر موقع السحب المناسب ونفّذ السحب بالتقنية المعتمدة.\n5) ضع العينات في الأنابيب الصحيحة وامزجها حسب تعليمات المختبر عند الحاجة.\n6) ضع بيانات المريض على العينة وفق النظام وأرسلها بسرعة.","استخدم أنبوب التحليل المناسب وتعامل مع العينات وفق تعليمات المختبر لتجنب رفضها."),
   P("جمع مزرعة من الجرح","🧪","جمع عينة مناسبة من الجرح عند طلب مزرعة ميكروبية.","1) تحقق من نوع العينة وتعليمات المختبر.\n2) نظّف اليدين وارتدِ معدات الوقاية.\n3) أزل الإفرازات السطحية حسب البروتوكول إذا كان مطلوبًا.\n4) اجمع العينة من المكان المحدد وبالأداة المعتمدة.\n5) ضعها في الوعاء المناسب دون تلوث.\n6) ضع البيانات وأرسلها للمختبر في الوقت المطلوب.","طريقة أخذ العينة تؤثر على دقة النتيجة؛ اتبع تعليمات المختبر والمنشأة."),
   P("إعطاء الحقن الوريدي","💉","إعطاء دواء عبر خط وريدي عندما يكون هذا الطريق موصوفًا ومناسبًا.","1) تحقّق من الدواء والجرعة والطريق والتوافق والحساسية.\n2) افحص الخط الوريدي وموقعه.\n3) استخدم تقنية التعقيم المناسبة.\n4) تحقق من سلامة الخط وتدفقه وفق البروتوكول.\n5) أعطِ الدواء بالسرعة المحددة للدواء.\n6) راقب المريض وموقع الخط.\n7) وثّق الدواء والاستجابة.","أدوية وريدية كثيرة لها معدلات إعطاء وتوافقات خاصة؛ لا تعتمد على معدل عام."),
   P("إعطاء الدواء عبر المضخة الوريدية","💉","إعطاء محلول أو دواء بمعدل دقيق باستخدام مضخة التسريب.","1) تحقق من الدواء والتركيز والجرعة والمعدل.\n2) افحص إعدادات المضخة والخط.\n3) برمج المعدل والحجم وفق الأمر.\n4) تحقق من الإعدادات قبل البدء وفق نظام التحقق المزدوج عند الحاجة.\n5) راقب المريض والمضخة وموقع الوصول الوريدي.\n6) وثّق الإعطاء وأي إنذارات أو تغييرات.","الأخطاء في برمجة المضخة قد تكون خطيرة؛ استخدم التحقق المزدوج والبروتوكول المحلي."),
   P("مراقبة المريض بعد إعطاء الدواء","👀","اكتشاف الاستجابة العلاجية والآثار الجانبية أو الحساسية مبكرًا.","1) اعرف التأثير المتوقع والآثار الجانبية المهمة للدواء.\n2) راقب العلامات الحيوية أو مستوى الوعي أو الأعراض المطلوبة.\n3) اسأل عن الألم أو الأعراض الجديدة.\n4) وثّق الاستجابة.\n5) عند ظهور أعراض خطيرة، أوقف الإجراء أو الدواء حسب الصلاحية والبروتوكول واطلب المساعدة.","الاستجابة المطلوبة ووقت المراقبة يختلفان حسب الدواء وحالة المريض."),
   P("تقييم مستوى الوعي GCS","🧠","تقييم الاستجابة العصبية باستخدام مكونات فتح العين والاستجابة اللفظية والحركية.","1) قيّم فتح العين.\n2) قيّم الاستجابة اللفظية مع مراعاة اللغة والسمع والأنبوب الرغامي.\n3) قيّم الاستجابة الحركية.\n4) سجّل كل مكوّن منفصلًا ثم المجموع عند استخدامه.\n5) قارن بالقراءات السابقة وبلّغ عن أي تدهور.","التقييم يجب أن يراعي العوامل التي قد تؤثر على النتيجة مثل التخدير أو الإصابات أو صعوبات التواصل."),
   P("تقييم NEWS2","📊","تجميع مؤشرات حيوية محددة للمساعدة في التعرف المبكر على تدهور المريض.","1) اجمع البيانات المطلوبة وفق نظام NEWS2 المعتمد في المنشأة.\n2) قِس معدل التنفس وSpO₂ والضغط والنبض والحرارة ومستوى الوعي حسب النموذج.\n3) أدخل القيم الصحيحة في جدول التقييم.\n4) احسب الدرجة.\n5) اتبع مسار الاستجابة الخاص بالدرجة في المنشأة.","NEWS2 أداة إنذار مبكر وليست بديلًا عن التقييم السريري أو الحكم الطبي."),
   P("قياس الألم وتوثيقه","❤️","تقييم شدة الألم وتأثيره ومتابعة الاستجابة للتدخل.","1) اسأل المريض عن مكان الألم وطبيعته ووقته.\n2) استخدم مقياس الألم المناسب لقدرة المريض.\n3) قيّم العوامل المصاحبة والأعراض التحذيرية.\n4) وثّق الدرجة والتدخل المقدم.\n5) أعد التقييم بعد التدخل في الوقت المناسب.","الألم عرض مهم؛ الألم الجديد الشديد أو المصحوب بعلامات خطورة يحتاج إلى تقييم سريع."),
   P("تقييم خطر السقوط","⚠️","تحديد عوامل خطر السقوط ووضع احتياطات مناسبة.","1) راجع العمر والحركة والتوازن والتاريخ السابق للسقوط.\n2) قيّم الأدوية والحالة العقلية والضغط عند الوقوف حسب النظام.\n3) افحص البيئة المحيطة.\n4) طبّق احتياطات السقوط المناسبة.\n5) علّم المريض وذويه متى وكيف يطلبون المساعدة.\n6) وثّق مستوى الخطر والتدخلات.","استخدم أداة تقييم السقوط المعتمدة في منشأتك ولا تعتمد على مقياس غير معتمد."),
   P("التعامل الأولي مع الإغماء","🚑","التعرف على عدم الاستقرار وتأمين المريض وطلب المساعدة.","1) تأكد من سلامة المكان.\n2) افحص الاستجابة والتنفس بسرعة.\n3) اطلب المساعدة وفعّل الاستجابة للطوارئ حسب الحالة.\n4) إذا لم يكن هناك تنفس طبيعي، ابدأ إجراءات الإنعاش وفق التدريب والبروتوكول.\n5) إذا كان يتنفس، حافظ على سلامة مجرى الهواء وقيّم العلامات الحيوية.\n6) لا تسمح للمريض بالوقوف سريعًا بعد الإفاقة.","الإغماء قد يكون علامة على سبب خطير؛ يحتاج إلى تقييم مناسب خاصة عند تكراره أو وجود أعراض تحذيرية."),
   P("الإنعاش القلبي الرئوي CPR للبالغ","❤️","بدء الاستجابة الأساسية عند الاشتباه في توقف القلب والتنفس.","1) تأكد من أمان المكان.\n2) افحص الاستجابة والتنفس واطلب المساعدة.\n3) اتبع إرشادات نظام الطوارئ المحلي.\n4) ابدأ ضغطات الصدر واستخدم جهاز AED عند توفره والتدريب عليه.\n5) استمر وفق خوارزمية الإنعاش المعتمدة حتى وصول فريق متقدم أو عودة العلامات المناسبة.","يجب تعلم CPR عمليًا من جهة تدريب معتمدة؛ هذا الملخص لا يغني عن التدريب أو خوارزمية المنشأة."),
   P("التعامل الأولي مع الاختناق","🚨","التعرف على انسداد مجرى الهواء والتصرف حسب شدة الحالة.","1) قيّم قدرة الشخص على السعال والكلام والتنفس.\n2) إذا كان الانسداد شديدًا، فعّل الطوارئ ونفّذ المناورات الموصى بها للبالغ أو الطفل حسب العمر والتدريب.\n3) إذا أصبح غير مستجيب، ابدأ خوارزمية الإنعاش المناسبة.\n4) استخدم AED عند الحاجة وفق البروتوكول.","الطريقة تختلف حسب العمر والحمل وحجم الجسم؛ التدريب العملي ضروري."),
   P("احتياطات العزل","🧤","منع انتقال العدوى حسب طريقة انتقالها وحالة المريض.","1) حدّد نوع الاحتياطات المطلوبة حسب التشخيص أو الاشتباه.\n2) اختر معدات الوقاية المناسبة.\n3) جهّز الأدوات داخل الغرفة عند الحاجة.\n4) طبّق نظافة اليدين قبل وبعد الرعاية.\n5) حدّ من نقل المريض والأدوات غير الضرورية.\n6) تخلّص من النفايات ونظّف المعدات وفق سياسة المنشأة.","نوع العزل وتسلسل معدات الوقاية يختلفان حسب الحالة؛ اتبع سياسة مكافحة العدوى المحلية."),
   P("التعامل مع الأدوات الحادة","🗑️","منع إصابات وخز الإبر والتعرض للدم وسوائل الجسم.","1) استخدم الأدوات بطريقة آمنة.\n2) لا تمرر الأدوات الحادة من شخص لآخر دون وسيلة آمنة.\n3) لا تعِد تغطية الإبر إلا في ظروف محددة وبطريقة معتمدة.\n4) تخلّص من الأداة فورًا في حاوية الأدوات الحادة.\n5) لا تملأ الحاوية فوق الحد المحدد.\n6) أبلغ عن أي إصابة فورًا.","التعامل الآمن مع الأدوات الحادة جزء من الوقاية المهنية ومكافحة العدوى."),
   P("تغيير وضعية المريض في السرير","🛏️","تحسين الراحة وتقليل مضاعفات قلة الحركة والضغط.","1) قيّم قدرة المريض والألم والأنابيب المتصلة به.\n2) اشرح الحركة واطلب المساعدة عند الحاجة.\n3) استخدم أسلوب تحريك يقلل إجهاد الظهر.\n4) غيّر الوضعية وفق الخطة مع حماية مناطق الضغط.\n5) تأكد من محاذاة الجسم والراحة.\n6) وثّق الوضعية وأي تغيرات جلدية.","استخدم وسائل النقل والمساعدة المناسبة للمرضى ذوي الحركة المحدودة."),
   P("قياس الوزن والطول وحساب BMI","⚖️","الحصول على قياسات أساسية تساعد في تقييم الحالة الغذائية والجرعات عند الحاجة.","1) استخدم ميزانًا مناسبًا ومُعايرًا قدر الإمكان.\n2) قِس الوزن في ظروف متقاربة عند المتابعة.\n3) قِس الطول بالطريقة الصحيحة.\n4) احسب BMI = الوزن بالكيلوغرام ÷ مربع الطول بالمتر.\n5) سجّل القياسات والتاريخ.","BMI أداة فحص عامة ولا يصف وحده الحالة الغذائية أو التشخيص."),
   P("تسجيل I&O","📋","متابعة السوائل الداخلة والخارجة للمساعدة في تقييم توازن السوائل.","1) سجّل كل مصادر السوائل حسب سياسة الوحدة.\n2) قِس البول والإفرازات القابلة للقياس.\n3) سجّل القياسات في الوقت الصحيح.\n4) احسب الإجمالي خلال الفترة المحددة.\n5) قارن النتائج بالحالة السريرية وبلّغ عن التغيرات المهمة.","تعريف السوائل الداخلة والخارجة يختلف حسب بروتوكول الوحدة؛ اتبع النموذج المعتمد."),
   P("التوثيق التمريضي","📝","تسجيل الرعاية والتقييم والاستجابة بصورة دقيقة وفي الوقت المناسب.","1) وثّق التقييم والملاحظات الموضوعية.\n2) سجّل الإجراءات والأدوية والاستجابة.\n3) استخدم الوقت والتاريخ الصحيحين.\n4) تجنب العبارات غير المحددة أو التخمين.\n5) صحح الأخطاء بالطريقة النظامية ولا تحذف السجل دون إجراء معتمد.\n6) حافظ على سرية معلومات المريض.","التوثيق جزء من سلامة المريض والتواصل بين الفريق ويجب أن يتبع نظام المنشأة."),
   P("تسليم الحالة SBAR","📞","نقل معلومات المريض بشكل منظم عند التواصل بين أفراد الفريق.","1) S — عرّف الموقف الحالي.\n2) B — اذكر الخلفية المهمة.\n3) A — اذكر تقييمك الحالي والبيانات المهمة.\n4) R — وضّح ما تحتاجه أو التوصية أو الخطة المطلوبة.\n5) تأكد من فهم الطرف الآخر عند المعلومات الحرجة.","استخدم SBAR كهيكل للتواصل ولا تستبدل به التقييم السريري أو التواصل المباشر عند الطوارئ."),
   P("التحضير لنقل المريض لقسم آخر","🚑","ضمان انتقال آمن ومتكامل للمريض بين الأقسام أو المنشآت.","1) تحقق من أمر النقل والهوية والحالة الحالية.\n2) جهّز الملف والمستندات والنتائج المطلوبة.\n3) تأكد من الأدوية والخطوط والقساطر والأنابيب.\n4) استخدم وسيلة النقل المناسبة.\n5) سلّم المعلومات الأساسية للفريق المستلم.\n6) وثّق وقت النقل وحالة المريض.","المرضى غير المستقرين يحتاجون خطة نقل ودعم مناسبين وفق سياسة المنشأة."),
   P("قياس حرارة المريض","🌡️","الحصول على قراءة دقيقة لدرجة الحرارة باستخدام الطريقة المناسبة.","1) اختر موقع القياس والطريقة المناسبة للمريض.\n2) جهّز الجهاز وتحقق من صلاحيته.\n3) نفّذ القياس حسب تعليمات الجهاز.\n4) سجّل القراءة والموقع والطريقة عند الحاجة.\n5) أعد القياس أو أبلغ عن النتائج غير المتوقعة وفق البروتوكول.","النتائج تختلف حسب موقع القياس والعمر والحالة؛ سجّل الطريقة عند أهمية ذلك."),
   P("قياس ضغط الدم يدويًا","🩺","قياس ضغط الدم باستخدام السماعة والكفة بالطريقة الصحيحة.","1) اجعل المريض هادئًا وفي وضع مناسب.\n2) اختر كفة بالحجم المناسب.\n3) ضع الذراع في مستوى مناسب ومدعوم.\n4) حدّد النبض وطبّق الكفة بالطريقة الصحيحة.\n5) انفخ وفرّغ الهواء بالمعدل المناسب واستمع للأصوات.\n6) سجّل الضغط والذراع والوضعية عند الحاجة.","الكفة غير المناسبة ووضعية الذراع قد تؤثران على القراءة؛ أعد القياس عند نتيجة غير متوقعة."),
   P("قياس SpO₂","🫁","قياس تشبع الأكسجين بالنبض باستخدام جهاز قياس التأكسج.","1) تأكد من نظافة وجفاف موضع القياس.\n2) ضع الحساس بشكل صحيح.\n3) انتظر قراءة مستقرة مع التأكد من وجود نبض مناسب.\n4) سجّل SpO₂ ومعدل النبض وحالة الأكسجين عند الحاجة.\n5) اربط القراءة بالحالة السريرية وأعد القياس عند عدم توافقها مع حالة المريض.","الحركة وبرودة الأطراف وضعف التروية وبعض العوامل الأخرى قد تؤثر في دقة القراءة."),
   P("تقييم التنفس","🫁","تقييم معدل التنفس وجهده ونمطه والأعراض المصاحبة.","1) راقب حركة الصدر دون التأثير على نمط المريض قدر الإمكان.\n2) احسب معدل التنفس لمدة مناسبة.\n3) لاحظ العمق والنمط واستخدام العضلات الإضافية.\n4) اسأل عن ضيق النفس عند قدرة المريض.\n5) اربط ذلك بـSpO₂ والأصوات التنفسية عند الحاجة.\n6) أبلغ عن التدهور أو العلامات الخطرة.","صعوبة التنفس أو تغير الوعي أو الزرقة تستدعي تقييمًا عاجلًا."),
   P("تقييم النبض","❤️","تقييم معدل النبض وانتظامه وقوته حسب الموقع.","1) اختر موقع النبض المناسب.\n2) احسب المعدل بالطريقة المناسبة.\n3) قيّم الانتظام والقوة عند الحاجة.\n4) قارن بين الجانبين إذا كان ذلك مطلوبًا.\n5) سجّل النتائج واربطها بأعراض المريض.","النبض غير المنتظم أو المصحوب بأعراض يحتاج إلى تقييم مناسب وفق الحالة."),
   P("تطبيق كمادة باردة أو دافئة","🌡️","استخدام الحرارة أو البرودة كإجراء مساعد عندما يكون مناسبًا.","1) تحقق من وجود أمر أو سبب مناسب.\n2) افحص الجلد والإحساس والدورة الدموية.\n3) استخدم حاجزًا مناسبًا بين الجلد والمصدر.\n4) راقب الجلد والمريض خلال التطبيق.\n5) حدّد المدة حسب البروتوكول.\n6) أزل المصدر وأعد تقييم الجلد والأعراض.","تجنب الحرارة أو البرودة الشديدة أو المباشرة، خصوصًا عند ضعف الإحساس أو الدورة الدموية."),
   P("تقييم الجلد الكامل","🧴","اكتشاف التغيرات الجلدية والجروح ومناطق الضغط مبكرًا.","1) احترم خصوصية المريض واشرح الفحص.\n2) افحص الجلد بشكل منهجي من الرأس إلى القدمين.\n3) لاحظ اللون والحرارة والرطوبة والجروح والطفح والتورم.\n4) افحص مناطق الضغط بعناية.\n5) قِس الجروح عند الحاجة ووثّق وصفًا موضوعيًا.\n6) أبلغ عن التغيرات الجديدة أو المتدهورة.","استخدم المصطلحات الموضوعية وتجنب وصف غير دقيق أو غير قابل للقياس."),
   P("تقييم الوذمة","🦵","تقييم وجود التورم ومتابعته وملاحظة التغيرات.","1) افحص مكان التورم ومقداره.\n2) قارن الجانبين عند الحاجة.\n3) قيّم الجلد والألم والتنفس والأعراض المصاحبة.\n4) إذا كان مناسبًا، قيّم الانطباع بالضغط وفق نظام الوحدة.\n5) سجّل مكان الوذمة ودرجتها وأي تغير.","الوذمة المصحوبة بضيق نفس أو ألم صدري أو تدهور مفاجئ تحتاج إلى تقييم عاجل."),
   P("التحضير لإعطاء الدم","🩸","التأكد من جاهزية المريض ومكوّن الدم والمستلزمات قبل بدء النقل.","1) تحقق من الأمر والموافقة والهوية.\n2) راجع نتائج المطابقة وفق النظام.\n3) افحص كيس الدم من حيث النوع والرقم والصلاحية وسلامته.\n4) قِس العلامات الحيوية الأساسية.\n5) جهّز خط النقل والمعدات المناسبة.\n6) نفّذ فحص المطابقة وفق سياسة بنك الدم والمنشأة.","لا تبدأ النقل عند وجود اختلاف في الهوية أو بيانات المطابقة؛ أوقف العملية واطلب المراجعة."),
   P("الاستجابة لتفاعل نقل الدم","🩸","التصرف السريع عند ظهور علامات قد تشير إلى تفاعل أثناء نقل الدم.","1) أوقف نقل الدم وفق البروتوكول عند الاشتباه في تفاعل.\n2) حافظ على سلامة الوصول الوريدي بالطريقة المعتمدة.\n3) قيّم العلامات الحيوية والحالة.\n4) اطلب المساعدة وأبلغ الطبيب/الفريق وبنك الدم حسب النظام.\n5) احتفظ بالمستلزمات والعينة وفق البروتوكول إذا طُلب.\n6) وثّق الحدث والتدخلات.","تفاعلات نقل الدم قد تكون خطيرة؛ اتبع بروتوكول بنك الدم حرفيًا ولا تعاود النقل دون توجيه."),
   P("التعامل مع مريض في حالة تدهور مفاجئ","🚨","التعرف المبكر على التدهور وتفعيل الاستجابة المناسبة.","1) قيّم ABC بسرعة: مجرى الهواء والتنفس والدورة الدموية.\n2) اطلب المساعدة وفعّل نظام الطوارئ عند الحاجة.\n3) قِس العلامات الحيوية وSpO₂ ومستوى الوعي.\n4) راجع السبب المحتمل والبيانات الحديثة.\n5) نفّذ التدخلات المسموح بها حسب البروتوكول.\n6) سلّم الحالة بشكل منظم للفريق.","التدهور المفاجئ حالة طارئة محتملة؛ لا تؤخر طلب المساعدة انتظارًا لإكمال التقييم."),
   P("تجهيز عربة الطوارئ","🚨","التأكد من جاهزية معدات وأدوية الطوارئ وفق نظام المنشأة.","1) افحص وجود العربة في المكان المحدد.\n2) تحقق من سلامة الختم أو نظام الفحص المستخدم.\n3) راجع الأدوية والمستلزمات وتواريخ الصلاحية حسب قائمة المنشأة.\n4) افحص جهاز مزيل الرجفان ومصدر الأكسجين والمستلزمات.\n5) وثّق الفحص وأبلغ عن أي نقص فورًا.","محتويات العربة تختلف حسب المنشأة والوحدة؛ استخدم قائمة الفحص الرسمية فقط."),
   P("إعداد المريض قبل إجراء تشخيصي","📋","تهيئة المريض والمعلومات والمستلزمات قبل إجراء تشخيصي.","1) تحقق من هوية المريض والإجراء المطلوب.\n2) راجع التحضير المطلوب مثل الصيام أو التحاليل عند وجوده.\n3) اشرح الإجراء وحدود دور التمريض فيه.\n4) تحقق من الحساسية والاحتياطات ذات الصلة.\n5) جهّز المستندات والمستلزمات.\n6) انقل المريض بأمان وسلّم المعلومات المهمة.","متطلبات التحضير تختلف باختلاف الفحص؛ المرجع الأساسي هو تعليمات القسم والطبيب والمختبر."),
   P("تعليم المريض عن الدواء","📚","مساعدة المريض على استخدام الدواء بأمان وفهم التعليمات الأساسية.","1) اشرح اسم الدواء والغرض منه بالطريقة المناسبة.\n2) وضّح الجرعة والطريق والجدول حسب الوصفة.\n3) اذكر أهم الاحتياطات والآثار الجانبية التي تستدعي طلب المساعدة.\n4) تحقق من فهم المريض بطريقة إعادة الشرح.\n5) وثّق التعليم عند الحاجة.","التعليم يجب أن يتوافق مع وصف الطبيب ومعلومات الدواء المعتمدة، ولا تستبدل تعليمات الصيدلي أو الطبيب."),
   P("التحقق من هوية المريض","🪪","منع أخطاء الدواء والإجراءات والتشخيص باستخدام معرفات مناسبة.","1) اطلب من المريض ذكر بياناته بدلًا من الاعتماد على رقم السرير.\n2) استخدم معرفين مناسبين وفق سياسة المنشأة.\n3) قارن البيانات مع السوار والملف والأمر.\n4) لا تبدأ الإجراء عند وجود اختلاف حتى يتم التحقق.","استخدام معرفين مستقلين جزء أساسي من سلامة المريض، وسياسة المنشأة تحدد المعرفات المقبولة."),
   P("التخلص من النفايات الطبية","🗑️","تقليل خطر العدوى والإصابة من خلال فرز النفايات والتخلص منها بشكل صحيح.","1) حدّد نوع النفاية قبل التخلص منها.\n2) استخدم الحاوية المناسبة.\n3) ضع الأدوات الحادة في الحاوية المخصصة مباشرة.\n4) لا تضغط النفايات بيدك ولا تخلط الأنواع.\n5) أغلق الحاويات عند بلوغ الحد المحدد.\n6) اتبع نظام نقل النفايات في المنشأة.","تصنيف النفايات وألوان الحاويات تختلف حسب البلد والمنشأة؛ اتبع النظام المحلي."),
   P("نقل المريض باستخدام الكرسي المتحرك","🧑‍🦽","نقل المريض بالكرسي المتحرك بطريقة آمنة.","1) قيّم قدرة المريض على الجلوس والوقوف.\n2) افحص الكرسي والمكابح ومسند القدمين.\n3) قرّب الكرسي وثبّته قبل النقل.\n4) ساعد المريض باستخدام التقنية المناسبة أو وسيلة النقل.\n5) ثبّت القدمين وتأكّد من الراحة أثناء الحركة.\n6) استخدم حزام نقل عند الحاجة وفق سياسة المنشأة.","لا تستخدم الكرسي المتحرك إذا كان المريض يحتاج وسيلة نقل أكثر دعمًا."),
   P("قياس محيط الرأس للطفل","👶","متابعة نمو الرأس عند الرضع والأطفال حسب العمر.","1) استخدم شريط قياس غير قابل للتمدد.\n2) مرر الشريط حول أكبر محيط للرأس بالطريقة الصحيحة.\n3) سجّل القياس بالسنتيمتر.\n4) قارن القياسات بمنحنى النمو المناسب للعمر والجنس.\n5) تابع الاتجاه بمرور الوقت بدل الاعتماد على قراءة واحدة.","استخدم مخطط النمو المعتمد في منشأتك، وتجنب تفسير القياس منفردًا دون السياق السريري."),
   P("تقييم حديث الولادة بشكل أولي","👶","تقييم الحالة العامة للحديث الولادة وملاحظة العلامات التي تحتاج إلى تدخل.","1) قيّم التنفس واللون والنشاط والحرارة.\n2) راقب علامات الضيق التنفسي.\n3) تحقق من العلامات الحيوية المناسبة للعمر.\n4) حافظ على دفء الطفل.\n5) أبلغ فورًا عن أي تدهور أو علامات خطورة حسب بروتوكول حديثي الولادة.","رعاية حديثي الولادة تتطلب تدريبًا متخصصًا وخوارزميات خاصة بالعمر والحالة."),
   P("تقييم المريض قبل العملية","🏥","التأكد من جاهزية المريض والتوثيق الأساسي قبل الإجراء الجراحي.","1) تحقق من الهوية والإجراء ومكانه وفق نظام المنشأة.\n2) راجع الحساسية والأدوية والتحاليل المطلوبة.\n3) تأكد من الصيام أو التحضير المطلوب عند وجوده.\n4) قيّم العلامات الحيوية والحالة العامة.\n5) تأكد من إزالة الأشياء المطلوبة مثل المجوهرات حسب البروتوكول.\n6) أكمل قائمة التحقق الجراحية المعتمدة.","استخدم قائمة التحقق الجراحية الرسمية ولا تستبدلها بقائمة عامة."),
   P("تقييم المريض بعد العملية","🏥","مراقبة المريض بعد الإجراء واكتشاف النزيف أو الألم أو مشاكل التنفس أو غيرها مبكرًا.","1) قيّم مجرى الهواء والتنفس والدورة الدموية والوعي.\n2) راقب العلامات الحيوية والألم.\n3) افحص الجرح والضماد والدرنقات عند الحاجة.\n4) راقب البول والغثيان والقيء حسب الحالة.\n5) أبلغ عن أي تدهور أو نزيف أو تغير مفاجئ.","تعتمد المراقبة على نوع العملية وحالة المريض وتعليمات الفريق الجراحي."),
   P("إعداد المريض للفحص بالأشعة","🩻","تجهيز المريض للفحص والتأكد من المتطلبات والاحتياطات.","1) تحقق من نوع الفحص وتعليمات القسم.\n2) راجع الحمل المحتمل عند من يلزم.\n3) راجع الحساسية والتاريخ المناسب عند استخدام مادة تباين.\n4) تأكد من إزالة المعادن أو الأشياء المطلوبة.\n5) حضّر المستندات وانقل المريض بأمان.\n6) اتبع تعليمات قسم الأشعة.","متطلبات التحضير والتباين تختلف حسب الفحص؛ قسم الأشعة هو المرجع المباشر."),
   P("مراقبة المريض أثناء إعطاء الأكسجين","🫁","متابعة فعالية الأكسجين وسلامة المريض أثناء العلاج.","1) سجّل وسيلة الأكسجين والإعداد.\n2) راقب SpO₂ ومعدل التنفس وجهده.\n3) راقب مستوى الوعي والراحة.\n4) افحص الجلد حول وسيلة الأكسجين.\n5) أعد التقييم بعد أي تغيير في الإعداد حسب الخطة.","لا تغيّر إعدادات الأكسجين دون أمر أو بروتوكول مناسب، مع مراعاة الفئات الخاصة مثل مرضى احتباس ثاني أكسيد الكربون."),
   P("تطبيق احتياطات السقوط","⚠️","تقليل فرص سقوط المريض داخل المنشأة.","1) ضع السرير على الارتفاع الآمن.\n2) أبقِ جرس النداء في متناول المريض.\n3) وفّر إضاءة مناسبة وأزل العوائق.\n4) ساعد المريض عند الوقوف أو المشي حسب الحاجة.\n5) استخدم وسائل الحماية المعتمدة وفق التقييم.\n6) أعد تقييم الخطر عند تغير الحالة.","لا تستخدم وسائل تقييد الحركة لمجرد الوقاية من السقوط دون تقييم وأمر وسياسة معتمدة."),
   P("العناية بالضمادات الجافة والرطبة","🩹","اختيار طريقة التعامل مع الغيار وفق نوع الجرح والخطة العلاجية.","1) راجع نوع الغيار المطلوب.\n2) جهّز الأدوات وحافظ على النظافة أو التعقيم المطلوب.\n3) أزل الغيار القديم وافحص الجرح.\n4) نظّف أو رطّب حسب الخطة وليس بشكل روتيني.\n5) ضع الغيار الجديد وثبّته.\n6) وثّق حالة الجرح والمواد المستخدمة.","نوع الغيار وطريقة الترطيب تعتمد على الجرح والخطة؛ لا تستخدم مواد غير موصوفة."),
   P("قياس سكر الدم والتعامل مع نقص السكر","🩸","التعرف على انخفاض سكر الدم واتباع بروتوكول الوحدة.","1) قِس السكر عند وجود أعراض أو حسب الخطة.\n2) قيّم الوعي وقدرة المريض على البلع.\n3) إذا كان واعيًا وقادرًا على البلع، اتبع بروتوكول علاج نقص السكر المعتمد.\n4) أعد القياس في الوقت المحدد.\n5) إذا كان فاقد الوعي أو غير قادر على البلع، فعّل الاستجابة الطبية ولا تعطِ شيئًا بالفم.\n6) وثّق السبب والتدخل والنتيجة.","علاج نقص السكر يختلف حسب البروتوكول والعمر والحالة؛ اتبع خوارزمية المنشأة."),
   P("التعامل الأولي مع ارتفاع السكر","🩸","تقييم المريض الذي لديه ارتفاع سكر وملاحظة علامات الخطورة.","1) قِس سكر الدم حسب الخطة.\n2) قيّم العطش والتبول والقيء والوعي والتنفس.\n3) راجع الأدوية والسوائل والبيانات المتاحة.\n4) أبلغ عن القيم المرتفعة جدًا أو الأعراض المقلقة.\n5) اتبع بروتوكول المنشأة للفحوص والتدخلات.","ارتفاع السكر المصحوب باضطراب وعي أو قيء أو تنفس غير طبيعي قد يكون حالة طارئة."),
   P("تقييم خطر الجفاف","💧","التعرف على علامات نقص السوائل ومتابعتها.","1) راقب العطش وجفاف الفم والجلد.\n2) تابع ضغط الدم والنبض والبول والوزن عند الحاجة.\n3) راقب المدخول والإخراج.\n4) قيّم القيء أو الإسهال أو فقد السوائل.\n5) أبلغ عن التدهور أو قلة البول أو اضطراب الوعي.","التقييم يعتمد على العمر والأمراض المصاحبة؛ لا تعتمد على علامة واحدة."),
   P("تقييم خطر العدوى","🦠","اكتشاف العلامات التي قد تشير إلى عدوى ومتابعتها.","1) راقب الحرارة والعلامات الحيوية.\n2) افحص الجروح والقساطر ومواقع الدخول.\n3) لاحظ الإفرازات أو الاحمرار أو التورم.\n4) اسأل عن الأعراض المناسبة.\n5) طبّق احتياطات مكافحة العدوى.\n6) أبلغ الفريق عن العلامات الجديدة أو المتفاقمة.","لا تُشخّص العدوى من علامة واحدة؛ اربط النتائج بالتقييم السريري والفحوص المطلوبة."),
   P("التعامل مع عينة البلغم","🧪","جمع عينة تنفسية مناسبة للفحص أو الزراعة عند طلبها.","1) تحقق من نوع العينة وتعليمات المختبر.\n2) اشرح للمريض طريقة جمع البلغم وليس اللعاب.\n3) جهّز الوعاء المناسب ووسائل الوقاية.\n4) اطلب العينة بالطريقة المحددة.\n5) أغلق الوعاء وضع البيانات وأرسله في الوقت المطلوب.","اتبع تعليمات المختبر الخاصة بالكمية والحفظ والنقل ونوع العينة."),
   P("إعطاء الأدوية عبر الأنبوب المعدي","💊","إعطاء دواء عبر أنبوب تغذية عند وصف ذلك وبعد التأكد من صلاحية الدواء للطريق.","1) تحقق من الدواء والطريق والتوافق.\n2) تأكد من موضع الأنبوب وفق البروتوكول.\n3) تحقق من إمكانية إعطاء الدواء عبر الأنبوب، واستشر الصيدلي عند الشك.\n4) أعطِ الدواء بالطريقة الموصوفة مع الغسل وفق سياسة المنشأة.\n5) راقب المريض وسجّل الإعطاء.","لا تسحق الأدوية ممتدة المفعول أو المغلفة معويًا أو تخلط أدوية متعددة دون توجيه مناسب."),
   P("العناية بالجلد حول فتحة التغذية","🩹","المحافظة على سلامة الجلد حول أنبوب التغذية وملاحظة المضاعفات.","1) افحص الجلد حول الفتحة يوميًا حسب الخطة.\n2) لاحظ الاحمرار والألم والتسرب والإفرازات.\n3) نظّف المنطقة بالطريقة الموصوفة وجففها بلطف.\n4) تأكد من تثبيت الأنبوب دون شد زائد.\n5) أبلغ عن علامات العدوى أو تغير موضع الأنبوب.","العناية تختلف حسب نوع أنبوب التغذية وتعليمات الفريق المختص."),
   P("التعامل مع القيء","🤢","حماية المريض من الاستنشاق وتقييم سبب القيء ومضاعفاته.","1) ساعد المريض إلى وضع آمن حسب مستوى الوعي.\n2) جهّز وعاء القيء ومعدات الوقاية.\n3) راقب مجرى الهواء والتنفس.\n4) سجّل كمية ومظهر القيء عند الحاجة.\n5) راقب علامات الجفاف أو الدم أو الألم الشديد.\n6) اتبع خطة العلاج وأبلغ عن العلامات التحذيرية.","وجود دم أو قيء متكرر أو اضطراب وعي أو ألم شديد يحتاج إلى تقييم سريع."),
   P("تقييم خطر النزيف","🩸","اكتشاف علامات النزيف ومتابعة المرضى المعرضين له.","1) راقب العلامات الحيوية والجلد والوعي.\n2) افحص الجروح والضمادات ومواقع الحقن.\n3) راقب البول والبراز والقيء عند الحاجة.\n4) راجع الأدوية التي قد تزيد النزيف.\n5) أبلغ فورًا عن النزيف المستمر أو التدهور.","النزيف الشديد أو المصحوب بتغير في الوعي أو العلامات الحيوية حالة طارئة."),
   P("التعامل مع الحمى","🌡️","تقييم ارتفاع الحرارة ومراقبة المريض واتباع الخطة العلاجية.","1) قِس الحرارة بالطريقة المناسبة.\n2) قيّم الأعراض المصاحبة والعلامات الحيوية.\n3) راقب السوائل والحالة العامة.\n4) نفّذ التدخلات الموصوفة مثل خافض الحرارة عند وجود أمر.\n5) أعد التقييم وسجّل الاستجابة.","العمر والحالة المناعية والسبب المحتمل تؤثر على أهمية الحمى ودرجة الاستجابة المطلوبة."),
   P("تقييم الحالة النفسية والسلوكية","🧠","ملاحظة التغيرات في المزاج والسلوك والوعي والتواصل.","1) تحدث مع المريض بطريقة هادئة ومحترمة.\n2) قيّم التوجه والوعي والسلوك حسب الحاجة.\n3) لاحظ التغيرات المفاجئة.\n4) اسأل عن عوامل قد تؤثر مثل الألم أو الأدوية أو نقص النوم عند الحاجة.\n5) وثّق ملاحظاتك الموضوعية وأبلغ عن التغيرات المهمة.","التغير المفاجئ في الوعي أو السلوك قد يكون علامة على مشكلة طبية تحتاج تقييمًا عاجلًا."),
   P("تسليم المناوبة التمريضية","📋","نقل معلومات المريض المهمة بين أفراد التمريض عند تغيير المناوبة.","1) راجع الحالة والملف قبل التسليم.\n2) اذكر الهوية والتشخيص والمخاطر الحالية.\n3) اذكر العلامات الحيوية والتغيرات المهمة.\n4) وضّح الأدوية والإجراءات والأنابيب والقساطر.\n5) اذكر ما تم وما هو مطلوب في المناوبة التالية.\n6) اترك فرصة للأسئلة والتأكد من المعلومات.","تجنب نقل معلومات غير مؤكدة أو غير ضرورية، وحافظ على سرية المريض."),
   P("التحقق من قائمة الأدوية","💊","تقليل أخطاء الأدوية من خلال مطابقة قائمة الأدوية الحالية.","1) اجمع قائمة الأدوية الحالية من المصادر المناسبة.\n2) قارنها بالأوامر الجديدة أو القائمة السابقة.\n3) تحقق من الجرعة والطريق والتكرار.\n4) اسأل عن الحساسية والتفاعلات المعروفة.\n5) أبلغ عن الاختلافات غير المفسرة للفريق المختص.\n6) وثّق عملية المطابقة وفق النظام.","مطابقة الأدوية مهمة خصوصًا عند الدخول والنقل والخروج من المستشفى."),
   P("تجهيز المريض للخروج من المستشفى","🏠","تهيئة المريض والأسرة لخطة الخروج والمتابعة بأمان.","1) راجع تعليمات الخروج والخطة.\n2) اشرح الأدوية ومواعيد المتابعة والتعليمات الأساسية.\n3) تأكد من فهم المريض أو مقدم الرعاية.\n4) راجع علامات الخطر التي تستوجب طلب المساعدة.\n5) تأكد من المستندات والمستلزمات المطلوبة.\n6) وثّق التعليمات والتسليم.","التعليمات النهائية يجب أن تتطابق مع خطة الطبيب والصيدلي وباقي الفريق."),
   P("مراجعة سلامة الدواء قبل الإعطاء","💊","تقليل أخطاء الدواء من خلال التحقق المنظم قبل الإعطاء.","1) المريض الصحيح.\n2) الدواء الصحيح.\n3) الجرعة الصحيحة.\n4) الطريق الصحيح.\n5) الوقت الصحيح.\n6) راجع الحساسية والتداخلات والتعليمات الخاصة والتوثيق وفق سياسة المنشأة.","استخدم نظام التحقق الرسمي في منشأتك، وقد تتطلب الأدوية عالية الخطورة تحققًا مزدوجًا."),
   P("التعامل مع الحساسية الدوائية المشتبه بها","🚨","التعرف على أعراض الحساسية بعد الدواء وطلب التدخل المناسب.","1) أوقف الدواء المشتبه به وفق الصلاحية والبروتوكول.\n2) قيّم مجرى الهواء والتنفس والدورة الدموية.\n3) اطلب المساعدة فورًا عند الأعراض الشديدة.\n4) راقب العلامات الحيوية.\n5) نفّذ العلاج الطارئ وفق البروتوكول والأوامر.\n6) وثّق الحدث وأبلغ عن الحساسية.","صعوبة التنفس أو تورم الوجه/اللسان أو انخفاض الضغط قد تشير إلى تفاعل شديد وتحتاج استجابة طارئة."),
   P("تقييم خطر الانسلاخ أو إصابة الجلد","🩹","تقليل إصابات الجلد عند المرضى المعرضين للتمزق أو التلف.","1) افحص الجلد والهشاشة وعوامل الخطر.\n2) استخدم لاصقات ووسائل تثبيت مناسبة للبشرة الهشة.\n3) تجنب الشد أو السحب المباشر للجلد.\n4) استخدم وسائل تحريك مناسبة.\n5) وثّق أي إصابة وطبّق خطة العناية.","البشرة الهشة تحتاج تقنيات لطيفة ومنتجات مناسبة حسب سياسة المنشأة."),
   P("العناية بالقدم لمريض السكري","🦶","تقليل خطر إصابات القدم وملاحظة المشكلات مبكرًا.","1) افحص الجلد والأظافر ومناطق الضغط.\n2) لاحظ الجروح أو الاحمرار أو التورم.\n3) اسأل عن الألم أو التنميل.\n4) حافظ على نظافة وجفاف القدمين.\n5) علّم المريض فحص القدمين وعدم المشي حافيًا.\n6) أبلغ عن أي جرح أو تغير مهم.","مرضى السكري قد يعانون من ضعف الإحساس؛ أي جرح جديد يستحق تقييمًا مناسبًا."),
   P("تقييم حالة التغذية","🥗","تحديد مؤشرات سوء التغذية أو خطرها ومتابعتها.","1) راجع الوزن وتغيره والشهية.\n2) قيّم القدرة على المضغ والبلع.\n3) راقب المدخول الغذائي والسوائل.\n4) لاحظ علامات فقدان الكتلة العضلية أو الضعف.\n5) استخدم أداة الفحص المعتمدة عند توفرها.\n6) أبلغ الفريق المختص عند وجود خطر.","تقييم التغذية يحتاج إلى أدوات وسياق سريري وقد يتطلب تدخل أخصائي تغذية."),
   P("التعامل مع المريض المصاب بضيق التنفس","🫁","التعرف على شدة ضيق التنفس ودعم المريض وتفعيل الاستجابة المناسبة.","1) قيّم مجرى الهواء والتنفس وSpO₂ والوعي.\n2) ساعد المريض إلى وضع مريح مناسب.\n3) اطلب المساعدة عند العلامات الخطرة.\n4) أعطِ الأكسجين إذا كان موصوفًا أو ضمن البروتوكول.\n5) راقب الاستجابة وأعد التقييم.\n6) جهّز للتصعيد إذا تدهورت الحالة.","ضيق التنفس الشديد أو الزرقة أو اضطراب الوعي أو عدم الاستقرار يستدعي استجابة عاجلة."),
   P("تقييم ألم الصدر","❤️","التعرف على خصائص ألم الصدر وعلامات الخطورة.","1) قيّم بداية الألم ومكانه وطبيعته وانتشاره.\n2) راقب العلامات الحيوية وSpO₂.\n3) اسأل عن ضيق النفس والتعرق والغثيان أو الإغماء.\n4) فعّل مسار ألم الصدر في المنشأة عند الاشتباه.\n5) لا تؤخر التقييم أو طلب المساعدة عند وجود علامات خطرة.","ألم الصدر قد يكون حالة طارئة؛ اتبع مسار الطوارئ المعتمد في المنشأة."),
   P("تقييم السكتة الدماغية بشكل أولي","🧠","التعرف المبكر على علامات السكتة الدماغية وتفعيل الاستجابة السريعة.","1) لاحظ بداية الأعراض أو آخر وقت كان فيه المريض طبيعيًا.\n2) افحص الوجه والذراع والكلام وفق أداة المنشأة.\n3) قيّم الوعي والعلامات الحيوية وسكر الدم عند الحاجة.\n4) فعّل مسار السكتة الدماغية فور الاشتباه.\n5) لا تعطِ الطعام أو الشراب قبل تقييم البلع عند وجود اشتباه مناسب.","الوقت مهم في السكتة الدماغية؛ اتبع بروتوكول المنشأة ولا تؤخر النقل أو التصوير."),
   P("تقييم المريض بعد السقوط","⚠️","اكتشاف الإصابات والمضاعفات بعد سقوط المريض وتوثيق الحدث.","1) لا تحرك المريض تلقائيًا إذا كان هناك اشتباه إصابة خطيرة.\n2) قيّم الوعي والتنفس والدورة الدموية والألم.\n3) افحص الإصابات الظاهرة ومجال الحركة حسب الأمان.\n4) اطلب التقييم الطبي عند الحاجة.\n5) راقب العلامات الحيوية.\n6) وثّق الحدث وأبلغ حسب نظام المنشأة.","يجب اتباع بروتوكول السقوط الرسمي، خاصة عند إصابة الرأس أو استخدام مميعات الدم."),
   P("العناية بالمريض قليل الحركة","🛏️","تقليل مضاعفات عدم الحركة وتحسين الراحة والسلامة.","1) قيّم الحركة وخطر الجلطات وقرح الضغط حسب الحالة.\n2) شجّع الحركة الآمنة إذا كانت مسموحة.\n3) غيّر الوضعية واستخدم وسائل الدعم.\n4) اهتم بالنظافة والتغذية والسوائل.\n5) نفّذ تمارين الحركة المسموح بها.\n6) راقب الجلد والتنفس والألم والإخراج.","خطة الحركة تختلف حسب التشخيص والعملية وخطر السقوط؛ اتبع أوامر الفريق."),
   P("مراقبة المريض بعد إعطاء مسكن","💊","متابعة فعالية المسكن واكتشاف النعاس المفرط أو تثبيط التنفس.","1) قيّم الألم قبل الدواء.\n2) أعطِ الدواء حسب الوصفة.\n3) أعد تقييم الألم في الوقت المناسب.\n4) راقب مستوى الوعي والتنفس وضغط الدم حسب الدواء.\n5) وثّق التأثير والآثار الجانبية.\n6) أبلغ عن تثبيط التنفس أو التدهور.","الأفيونات خصوصًا قد تسبب تثبيط التنفس؛ المراقبة تتبع الدواء وحالة المريض والبروتوكول."),
   P("تقييم فعالية العلاج بالأكسجين","🫁","معرفة ما إذا كان العلاج يحقق الهدف المحدد للمريض.","1) راجع الهدف المحدد لـSpO₂ أو الأكسجة.\n2) قِس SpO₂ وقيّم التنفس والوعي.\n3) تأكد من وصول الأكسجين فعليًا وعدم وجود مشكلة في الجهاز.\n4) أبلغ عن عدم تحقيق الهدف أو التدهور.\n5) وثّق الإعداد والاستجابة.","الهدف يختلف حسب الحالة؛ لا تستخدم هدفًا موحدًا لكل المرضى."),
   P("مراجعة خطة الرعاية التمريضية","📋","تحديث خطة الرعاية بناءً على تقييم المريض وتغير حالته.","1) راجع التقييمات والمشكلات الحالية.\n2) حدّد الأولويات.\n3) راجع التدخلات والاستجابة.\n4) حدّث الأهداف والتدخلات وفق احتياجات المريض.\n5) شارك التغييرات مع الفريق.\n6) وثّق التحديثات.","خطة الرعاية يجب أن تعكس الحالة الحالية وتلتزم بنظام التوثيق في المنشأة.")
  )
  items.forEach { p ->
   val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(16),dp(14),dp(16),dp(14));background=glass();setOnClickListener{procedureDetails(p.title,p.icon,p.purpose,p.steps,p.safety)}}
   c.addView(tv("${p.icon}  ${p.title}",17f,fg(),true))
   c.addView(tv(p.purpose,12f,muted()).apply{setPadding(0,dp(7),0,0)})
   body.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(10)})
  }
 }
 private fun procedureDetails(title:String,icon:String,purpose:String,steps:String,safety:String){
  page="procedure_detail"; parentPage="procedures"; clear("$icon  $title","إجراء تمريضي — شرح عربي")
  body.addView(tv("ما الهدف؟",18f,accent,true)); body.addView(tv(purpose,14f,fg()).apply{setPadding(0,dp(6),0,dp(16))})
  body.addView(tv("الخطوات التعليمية",18f,accent,true)); body.addView(tv(steps,14f,fg()).apply{setPadding(0,dp(8),0,dp(16))})
  val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(14),dp(14),dp(14),dp(14));background=glass(Color.argb(35,220,170,40),Color.rgb(180,145,60),18)}
  box.addView(tv("⚠️ نقاط السلامة",16f,fg(),true)); box.addView(tv(safety,13f,fg()).apply{setPadding(0,dp(7),0,0)})
  body.addView(box,LinearLayout.LayoutParams(-1,-2).apply{topMargin=dp(6);bottomMargin=dp(14)})
  body.addView(tv("المحتوى تعليمي ولا يغني عن التدريب العملي أو بروتوكول المنشأة أو توجيهات الفريق المعالج.",11f,muted()).apply{setPadding(0,dp(6),0,dp(20))})
 }
 private fun reference(){page="reference";parentPage="home";clear("📚 المراجع","معلومات سريعة — لا تغني عن مرجع المنشأة");menu("📏","النطاقات الطبيعية","علامات حيوية • تحاليل • أطفال • نمو"){normalRanges()};menu("❤️","العلامات الحيوية","النطاقات تعتمد على العمر والسياق"){info("العلامات الحيوية","استخدم مرجع الفئة العمرية والسياق السريري. منظمة الصحة العالمية في Basic Emergency Care تذكر للبالغين: النبض 60–100/دقيقة، التنفس 10–20/دقيقة، والضغط الانقباضي أكبر من 90 مم زئبق. للأطفال تختلف القيم حسب العمر.")};menu("🧪","قيم التحاليل","CBC • Electrolytes • Renal • Glucose"){info("قيم التحاليل","النطاق المرجعي المطبوع في تقرير المختبر هو المرجع الأساسي؛ منظمة الصحة العالمية توضح أن الفواصل المرجعية البيولوجية قد تختلف حسب السكان والعمر والجنس والسياق.")};menu("🔤","الاختصارات","PRN • STAT • NPO • I&O • IV"){info("الاختصارات","PRN عند الحاجة • STAT فورًا • NPO ممنوع فمويًا • I&O الداخل والخارج • IV وريدي")};menu("🧤","السلامة","Medication • Injection • Sharps • IV"){info("السلامة","اتبع سياسة المنشأة وممارسات مكافحة العدوى وسلامة الدواء.")}}
 private fun normalRanges(){page="normalRanges";parentPage="home";clear("📏 النطاقات والقيم المرجعية","مرجع تمريضي واسع • القيم تختلف حسب العمر والجنس والحمل وطريقة القياس والمختبر");
  val q=inp("ابحث باسم التحليل أو الفئة: CBC، صوديوم، كرياتينين...");add(q)
  val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};body.addView(list)
  data class R(val cat:String,val name:String,val value:String,val note:String,val source:String)
  val ranges=listOf(
   // Vital signs
   R("العلامات الحيوية","النبض للبالغين","60–100 ضربة/دقيقة","مرجع بالغ مستقر؛ يتأثر بالعمر والنشاط والأدوية","WHO/ICRC Basic Emergency Care"),
   R("العلامات الحيوية","معدل التنفس للبالغين","10–20 نفس/دقيقة","مرجع بالغ مستقر","WHO/ICRC Basic Emergency Care"),
   R("العلامات الحيوية","تشبع الأكسجين SpO₂","عادةً 95–100%","قد يكون الهدف مختلفًا في بعض أمراض الرئة؛ اتبع الخطة السريرية","مرجع سريري شائع"),
   R("العلامات الحيوية","درجة الحرارة الفموية","حوالي 36.0–37.5 °م","تختلف حسب مكان القياس والوقت","مرجع سريري شائع"),
   R("العلامات الحيوية","الضغط الانقباضي","يتغير حسب العمر والحالة","لا تستخدم رقمًا واحدًا كهدف علاجي لجميع المرضى","WHO/ICRC Basic Emergency Care"),
   R("العلامات الحيوية","الضغط الانبساطي","يتغير حسب العمر والحالة","التفسير يكون مع السياق السريري","مرجع سريري شائع"),
   // CBC
   R("صورة الدم CBC","WBC كريات الدم البيضاء","حوالي 4–11 ×10⁹/L","يختلف حسب المختبر والعمر","مرجع مختبري شائع"),
   R("صورة الدم CBC","RBC كريات الدم الحمراء - رجال","حوالي 4.5–5.9 ×10¹²/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","RBC كريات الدم الحمراء - نساء","حوالي 4.1–5.1 ×10¹²/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","الهيموجلوبين - رجال","حوالي 13.5–17.5 جم/ديسيلتر","مرجع مختبري تقريبي؛ WHO يضع حدودًا لتشخيص الأنيميا وليس نطاقًا كاملًا","WHO 2024 + مرجع مختبري شائع"),
   R("صورة الدم CBC","الهيموجلوبين - نساء غير حوامل","حوالي 12.0–15.5 جم/ديسيلتر","يختلف حسب المختبر والحمل","WHO 2024 + مرجع مختبري شائع"),
   R("صورة الدم CBC","Hematocrit - رجال","حوالي 41–53%","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","Hematocrit - نساء","حوالي 36–46%","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","MCV","80–100 fL","حجم كرية الدم الحمراء","مرجع مختبري شائع"),
   R("صورة الدم CBC","MCH","27–33 pg","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","MCHC","32–36 جم/ديسيلتر","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","RDW","حوالي 11.5–14.5%","يختلف حسب الجهاز والمختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","Platelets الصفائح","150–400 ×10⁹/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","Neutrophils","حوالي 40–70%","التفسير يعتمد أيضًا على ANC","مرجع مختبري شائع"),
   R("صورة الدم CBC","Lymphocytes","حوالي 20–40%","يختلف مع العمر والحالة","مرجع مختبري شائع"),
   R("صورة الدم CBC","Monocytes","حوالي 2–8%","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("صورة الدم CBC","Eosinophils","حوالي 1–4%","قد ترتفع في الحساسية وبعض الطفيليات","مرجع مختبري شائع"),
   R("صورة الدم CBC","Basophils","حوالي 0–1%","يختلف حسب المختبر","مرجع مختبري شائع"),
   // Electrolytes / renal
   R("الأملاح والكلى","Sodium الصوديوم","135–145 mmol/L","الفاصل المرجعي يختلف حسب المختبر","WHO LQSI example / ISO reference"),
   R("الأملاح والكلى","Potassium البوتاسيوم","3.5–5.0 mmol/L","يختلف حسب المختبر وحالة العينة","مرجع مختبري شائع"),
   R("الأملاح والكلى","Chloride الكلوريد","98–106 mmol/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الأملاح والكلى","Bicarbonate / CO₂","22–29 mmol/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الأملاح والكلى","Calcium الكالسيوم الكلي","8.5–10.5 mg/dL","يتأثر بالألبومين؛ قد يلزم تصحيح/قياس المتأين","مرجع مختبري شائع"),
   R("الأملاح والكلى","Magnesium الماغنيسيوم","1.7–2.2 mg/dL","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الأملاح والكلى","Phosphate الفوسفات","2.5–4.5 mg/dL للبالغين","الأطفال لهم نطاقات مختلفة","مرجع مختبري شائع"),
   R("وظائف الكلى","Urea / BUN","BUN حوالي 7–20 mg/dL","القيم تختلف باختلاف الوحدة والمختبر؛ استخدم مرجع التقرير","مرجع مختبري شائع"),
   R("وظائف الكلى","Creatinine الكرياتينين - بالغين","حوالي 0.6–1.3 mg/dL","يتأثر بالعمر والعضلات والجنس؛ eGFR أهم للتقييم في سياقات كثيرة","مرجع مختبري شائع"),
   R("وظائف الكلى","eGFR","≥90 mL/min/1.73m² غالبًا طبيعي إذا لم توجد دلائل مرض كلوي","لا يُفسر منفردًا؛ العمر والسياق مهمان","KDIGO/مرجع كلوي"),
   R("وظائف الكلى","Uric acid حمض اليوريك","رجال حوالي 3.4–7.0 mg/dL؛ نساء 2.4–6.0 mg/dL","قد تختلف الحدود حسب المختبر","مرجع مختبري شائع"),
   // Glucose / lipids
   R("السكر والدهون","سكر صائم","70–99 mg/dL","التشخيص له معايير مختلفة عن النطاق المرجعي","مرجع سريري شائع"),
   R("السكر والدهون","سكر عشوائي","لا يوجد نطاق طبيعي واحد صالح لكل وقت","التفسير يعتمد على وقت الأكل والأعراض والسياق","مرجع سريري"),
   R("السكر والدهون","HbA1c","أقل من 5.7% عادةً ضمن النطاق غير المصاب بالسكري","حدود التشخيص/ما قبل السكري مختلفة عن النطاق المرجعي","ADA/مرجع سريري"),
   R("السكر والدهون","Total Cholesterol الكوليسترول الكلي","أقل من 200 mg/dL مرغوب للبالغين","التقييم القلبي يعتمد على الخطورة الكلية","مرجع سريري شائع"),
   R("السكر والدهون","LDL-C","أقل من 100 mg/dL هدف شائع للأشخاص منخفضي الخطورة","الأهداف العلاجية تختلف حسب الخطورة القلبية","مرجع دهون سريري"),
   R("السكر والدهون","HDL-C","≥40 mg/dL رجال و≥50 mg/dL نساء","كلما ارتفع كان أفضل عادةً، مع مراعاة السياق","مرجع دهون سريري"),
   R("السكر والدهون","Triglycerides","أقل من 150 mg/dL","يفضل تفسيره مع الصيام والسياق","مرجع دهون سريري"),
   // Liver
   R("وظائف الكبد","ALT","حوالي 7–56 U/L","النطاق يختلف حسب المختبر والعمر والجنس","مرجع مختبري شائع"),
   R("وظائف الكبد","AST","حوالي 10–40 U/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("وظائف الكبد","ALP","حوالي 40–130 U/L للبالغين","الأطفال والحمل لهم قيم أعلى طبيعيًا في بعض الحالات","مرجع مختبري شائع"),
   R("وظائف الكبد","GGT","حوالي 9–48 U/L","يختلف كثيرًا حسب الجنس والمختبر","مرجع مختبري شائع"),
   R("وظائف الكبد","Total Bilirubin","حوالي 0.2–1.2 mg/dL","حديثو الولادة لهم نطاقات مختلفة","مرجع مختبري شائع"),
   R("وظائف الكبد","Direct Bilirubin","حوالي 0–0.3 mg/dL","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("وظائف الكبد","Albumin","3.5–5.0 g/dL","يتأثر بالحالة الغذائية والكبد والكلى والالتهاب","مرجع مختبري شائع"),
   R("وظائف الكبد","Total Protein","6.0–8.3 g/dL","يختلف حسب المختبر","مرجع مختبري شائع"),
   // Coagulation
   R("التجلط","PT","حوالي 11–13.5 ثانية","يختلف حسب الكاشف والجهاز","مرجع مختبري شائع"),
   R("التجلط","INR","حوالي 0.8–1.2 لغير المستخدمين للوارفارين","الهدف العلاجي يختلف حسب الاستطباب ولا يمثل الطبيعي","مرجع مختبري شائع"),
   R("التجلط","aPTT","حوالي 25–35 ثانية","يختلف حسب المختبر والكاشف","مرجع مختبري شائع"),
   R("التجلط","Fibrinogen","حوالي 200–400 mg/dL","يختلف حسب المختبر والحالة الالتهابية","مرجع مختبري شائع"),
   // ABG
   R("غازات الدم الشرياني ABG","pH","7.35–7.45","يُفسر مع PaCO₂ وHCO₃⁻ وPaO₂","مرجع غازات الدم شائع"),
   R("غازات الدم الشرياني ABG","PaCO₂","35–45 mmHg","يختلف حسب الحالة والارتفاع","مرجع غازات الدم شائع"),
   R("غازات الدم الشرياني ABG","PaO₂","80–100 mmHg عند سطح البحر تقريبًا","يتأثر بالعمر والارتفاع والأكسجين المعطى","مرجع غازات الدم شائع"),
   R("غازات الدم الشرياني ABG","HCO₃⁻","22–26 mmol/L","يُفسر مع الحالة الحمضية القاعدية","مرجع غازات الدم شائع"),
   R("غازات الدم الشرياني ABG","SaO₂","95–100% تقريبًا","تختلف الأهداف في بعض الأمراض التنفسية","مرجع غازات الدم شائع"),
   R("غازات الدم الشرياني ABG","Lactate","حوالي 0.5–2.2 mmol/L","يتأثر بالصدمة والتمرين والأدوية والمرض","مرجع مختبري شائع"),
   // Cardiac
   R("القلب","Troponin","يعتمد على نوع الاختبار وحد الكشف؛ لا يوجد رقم موحد","استخدم 99th percentile الخاص بالاختبار والمختبر؛ التغير الزمني مهم","إرشادات قلبية/المختبر"),
   R("القلب","BNP","غالبًا <100 pg/mL في سياقات تشخيص فشل القلب الحاد","العمر والكلى والسمنة تؤثر على التفسير","مرجع سريري"),
   R("القلب","NT-proBNP","يعتمد بشدة على العمر والسياق","لا تستخدم حدًا واحدًا لجميع الأعمار","مرجع سريري"),
   // Thyroid
   R("الغدة الدرقية","TSH","حوالي 0.4–4.0 mIU/L","الحمل والعمر والأدوية تغير المرجع","مرجع مختبري شائع"),
   R("الغدة الدرقية","Free T4","حوالي 0.8–1.8 ng/dL","يختلف حسب الاختبار والمختبر","مرجع مختبري شائع"),
   R("الغدة الدرقية","Free T3","حوالي 2.3–4.2 pg/mL","يختلف حسب الاختبار والمختبر","مرجع مختبري شائع"),
   // Iron / vitamins
   R("الحديد والفيتامينات","Ferritin - رجال","حوالي 30–400 ng/mL","يتأثر بالالتهاب؛ ليس مؤشرًا منفردًا لنقص الحديد","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","Ferritin - نساء","حوالي 13–150 ng/mL","يختلف حسب العمر والحمل والمختبر","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","Serum Iron","حوالي 60–170 µg/dL","يتغير خلال اليوم ولا يفسر منفردًا","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","TIBC","حوالي 240–450 µg/dL","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","Vitamin B12","حوالي 200–900 pg/mL","قد تختلف الحدود حسب المختبر","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","Folate","حوالي 2–20 ng/mL","يختلف حسب طريقة القياس","مرجع مختبري شائع"),
   R("الحديد والفيتامينات","25-OH Vitamin D","30–100 ng/mL كمدى كافٍ شائع الاستخدام","تختلف تعريفات الكفاية بين الإرشادات؛ لا تنسب للـWHO كمدى موحد","مرجع غدد/مختبر"),
   // Urinalysis
   R("تحليل البول","اللون","أصفر فاتح إلى كهرماني","يتأثر بالترطيب والغذاء والأدوية","مرجع سريري"),
   R("تحليل البول","Specific Gravity","1.005–1.030 تقريبًا","يتأثر بالترطيب ووظيفة الكلى","مرجع مختبري شائع"),
   R("تحليل البول","pH البول","4.5–8.0","يتأثر بالغذاء والعدوى والحالة الاستقلابية","مرجع مختبري شائع"),
   R("تحليل البول","Protein","سلبي أو أثر بسيط","الإيجابية المستمرة تحتاج تقييمًا","مرجع مختبري شائع"),
   R("تحليل البول","Glucose","سلبي","تظهر في حالات مثل ارتفاع سكر الدم أو بعض اضطرابات الأنابيب الكلوية","مرجع مختبري شائع"),
   R("تحليل البول","Ketones","سلبي","قد تظهر مع الصيام أو القيء أو السكري أو حالات أخرى","مرجع مختبري شائع"),
   R("تحليل البول","Blood","سلبي","النتيجة الإيجابية تحتاج تفسيرًا وفحصًا مجهريًا عند الحاجة","مرجع مختبري شائع"),
   R("تحليل البول","Nitrite","سلبي","قد يدعم وجود بعض البكتيريا لكنه ليس اختبارًا نافيًا للعدوى","مرجع مختبري شائع"),
   R("تحليل البول","Leukocyte esterase","سلبي","قد يدل على كريات بيضاء/التهاب؛ يفسر مع الأعراض والمزرعة","مرجع مختبري شائع"),
   R("تحليل البول","WBC في الميكروسكوب","0–5 /HPF تقريبًا","يختلف حسب المختبر وطريقة الفحص","مرجع مختبري شائع"),
   R("تحليل البول","RBC في الميكروسكوب","0–2 /HPF تقريبًا","يختلف حسب المختبر","مرجع مختبري شائع"),
   // CSF
   R("السائل النخاعي CSF","WBC","0–5 خلايا/µL","يختلف حسب نوع الخلايا والعمر والسياق","مرجع مختبري شائع"),
   R("السائل النخاعي CSF","Protein","حوالي 15–45 mg/dL","حديثو الولادة لهم قيم مختلفة","مرجع مختبري شائع"),
   R("السائل النخاعي CSF","Glucose","حوالي 40–70 mg/dL","يفسر عادةً مقارنة بجلوكوز الدم","مرجع مختبري شائع"),
   // Inflammation
   R("الالتهاب","CRP","غالبًا <5 mg/L","يختلف حسب الاختبار؛ لا يشخص سبب الالتهاب وحده","مرجع مختبري شائع"),
   R("الالتهاب","ESR","يختلف بشدة حسب العمر والجنس؛ لا يوجد رقم واحد","يستخدم كاختبار مساعد وليس تشخيصًا منفردًا","مرجع مختبري شائع"),
   // Pregnancy / reproductive
   R("الحمل والهرمونات","β-hCG","غير الحامل عادةً <5 mIU/mL","القيم في الحمل ترتفع بسرعة وتُفسر حسب عمر الحمل","مرجع مختبري شائع"),
   R("الحمل والهرمونات","Progesterone","يتغير حسب مرحلة الدورة والحمل","لا يوجد نطاق واحد صالح لكل النساء","مرجع مختبري شائع"),
   R("الحمل والهرمونات","Estradiol","يتغير حسب الدورة والعمر والحمل","استخدم مرجع المختبر ومرحلة الدورة","مرجع مختبري شائع"),
   R("الحمل والهرمونات","Testosterone - رجال","حوالي 300–1000 ng/dL","يختلف حسب العمر ووقت سحب العينة والمختبر","مرجع مختبري شائع"),
   // Pediatrics / growth
   R("نمو الأطفال","الطول/العمر","يُفسر بـ Z-score حسب العمر والجنس","لا يوجد رقم واحد طبيعي لكل الأطفال","WHO Child Growth Standards"),
   R("نمو الأطفال","الوزن/العمر","يُفسر بـ Z-score حسب العمر والجنس","يستخدم مخططات WHO","WHO Child Growth Standards"),
   R("نمو الأطفال","الوزن/الطول","يُفسر بـ Z-score حسب الطول والجنس","مهم لتقييم الهزال/الزيادة","WHO Child Growth Standards"),
   R("نمو الأطفال","BMI/العمر","يُفسر بـ Z-score أو percentile حسب العمر والجنس","WHO 0–5 سنوات وWHO 5–19 سنة لهما مراجع محددة","WHO Child Growth Standards / WHO 2007"),
   R("نمو الأطفال","محيط الرأس/العمر","يُفسر بمخططات WHO حسب العمر والجنس","خاصة في السنوات الأولى","WHO Child Growth Standards"),
   // Urine output
   R("إخراج البول","البالغ","≥0.5 مل/كجم/ساعة كقيمة سريرية شائعة لمراقبة قلة البول","ليست قيمة مخبرية؛ استخدم الاتجاه والحالة السريرية","مرجع سريري"),
   R("إخراج البول","الأطفال","تقريبًا ≥1 مل/كجم/ساعة في كثير من سياقات الرعاية الحادة","تختلف حسب العمر والحالة والبروتوكول","مرجع سريري"),
   // Other common
   R("الإنزيمات والعضلات","CK Creatine Kinase","حوالي 20–200 U/L","يختلف بشدة حسب الجنس والكتلة العضلية والنشاط","مرجع مختبري شائع"),
   R("الإنزيمات والعضلات","LDH","حوالي 140–280 U/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الإنزيمات والعضلات","Amylase","حوالي 30–110 U/L","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("الإنزيمات والعضلات","Lipase","حوالي 0–160 U/L","يختلف حسب المختبر؛ حدود التشخيص مختلفة","مرجع مختبري شائع"),
   R("المناعة","IgG","حوالي 700–1600 mg/dL للبالغين","الأطفال لهم نطاقات حسب العمر","مرجع مختبري شائع"),
   R("المناعة","IgA","حوالي 70–400 mg/dL للبالغين","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("المناعة","IgM","حوالي 40–230 mg/dL للبالغين","يختلف حسب المختبر","مرجع مختبري شائع"),
   R("السموم والأدوية","Acetaminophen level","ليس له نطاق طبيعي ثابت؛ يفسر حسب وقت الابتلاع وخط العلاج","يجب استخدام nomogram والبروتوكول عند الاشتباه بالتسمم الحاد","بروتوكولات السموم"),
   R("السموم والأدوية","Vancomycin / Aminoglycosides","يُحدد حسب الدواء والجرعة ونظام المراقبة","TDM يحتاج أهدافًا خاصة بالدواء والمختبر","بروتوكول المنشأة"),
   R("المزرعة والميكروبيولوجي","Blood culture","لا توجد قيمة طبيعية رقمية؛ الطبيعي سلبي","النتيجة تفسر مع عدد العينات والتلوث والأعراض","مرجع ميكروبيولوجي"),
   R("المزرعة والميكروبيولوجي","Urine culture","لا توجد قيمة طبيعية رقمية واحدة؛ التفسير يعتمد على CFU والسياق","الأعراض وطريقة جمع العينة مهمان","مرجع ميكروبيولوجي"),
   R("الأشعة والفحوصات","ECG","لا يوجد رقم طبيعي واحد؛ يعتمد على معدل القلب والفواصل والمحور والموجات","يحتاج قراءة ECG كاملة وليس رقمًا منفردًا","مرجع قلبي"),
   R("الأشعة والفحوصات","SpO₂/قياس التأكسج","عادةً 95–100% عند الشخص السليم على مستوى سطح البحر","الأهداف تختلف في حالات معينة","مرجع سريري")
  )
  fun refresh(){list.removeAllViews();val query=q.text.toString().trim().lowercase(java.util.Locale.ROOT);ranges.filter{query.isEmpty()||("${it.cat} ${it.name} ${it.value} ${it.note}").lowercase(java.util.Locale.ROOT).contains(query)}.forEach{r->val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(15),dp(13),dp(15),dp(13));background=glass()};c.addView(tv(r.cat,10f,muted(),true));c.addView(tv(r.name,16f,fg(),true));c.addView(tv(r.value,17f,accent,true));c.addView(tv(r.note,11f,muted()));c.addView(tv("المصدر/المرجع: ${r.source}",10f,muted()));list.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(9)})};if(list.childCount==0)list.addView(tv("لا توجد نتيجة مطابقة",14f,muted()))}
  q.addTextChangedListener(object:android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,b:Int,c:Int,a:Int){refresh()};override fun afterTextChanged(e:android.text.Editable?){} });refresh()
  val note=tv("⚠ مهم: لا يوجد مرجع عالمي واحد يحدد «الطبيعي» لكل التحاليل. منظمة الصحة العالمية تؤكد أن المختبر مسؤول عن تحديد الفاصل المرجعي المناسب لطريقة القياس والسكان، مع مراعاة العمر والجنس وعوامل أخرى. بعض القيم أعلاه فواصل مختبرية شائعة وليست حدود WHO، وبعضها قيم قرار/تشخيص وليست «مدى طبيعي». استخدم دائمًا مرجع المختبر في تقرير المريض وبروتوكول المنشأة.",11f,muted());note.setPadding(dp(8),dp(14),dp(8),dp(20));body.addView(note)
 }
 private fun info(a:String,b:String){page="info";parentPage="reference";clear(a,"مرجع سريع");val c=tv(b,15f);c.setPadding(dp(16),dp(18),dp(16),dp(18));c.background=glass();body.addView(c)}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()

}
