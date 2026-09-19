package com.hakamo.ivcalculator

import android.content.Intent
import android.net.Uri
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlin.math.*

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
 private fun shell(){val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(bg())};val h=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setPadding(dp(18),dp(16),dp(18),dp(10))};val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;layoutParams=LinearLayout.LayoutParams(0,-2,1f)};b.addView(tv("HAKAMO",12f,accent,true));b.addView(tv("مساعد التمريض",25f,fg(),true));b.addView(tv("أدوات تمريض سريعة • بدون إنترنت",11f,muted()));h.addView(b);val m=tv(themeName(),12f);m.gravity=Gravity.CENTER;m.background=glass(if(dark())Color.rgb(23,30,44) else Color.rgb(238,242,248),if(dark())Color.rgb(58,72,94) else Color.rgb(211,219,231),16);m.setPadding(dp(10),0,dp(10),0);m.setOnClickListener{mode=(mode+1)%3;getPreferences(0).edit().putInt("mode",mode).apply();recreate()};h.addView(m,LinearLayout.LayoutParams(dp(106),dp(44)));root.addView(h);body=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(18),0,dp(18),dp(24))};val scroll=ScrollView(this).apply{isFillViewport=true;addView(body)};root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f));val nav=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;setPadding(dp(8),dp(8),dp(8),dp(10));setBackgroundColor(if(dark())Color.rgb(9,13,21) else Color.WHITE)};arrayOf("⌂" to "الرئيسية","🧮" to "الحاسبات","💊" to "الأدوية","🩺" to "التقييم","📚" to "المراجع").forEachIndexed{i,pair->val item=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setOnClickListener{when(i){0->home();1->calculators();2->medicines();3->assessment();4->reference()}}};item.addView(tv(pair.first,20f,if(i==0)accent else muted()).apply{gravity=Gravity.CENTER});item.addView(tv(pair.second,9f,if(i==0)accent else muted(),i==0).apply{gravity=Gravity.CENTER});nav.addView(item,LinearLayout.LayoutParams(0,dp(58),1f))};root.addView(nav);setContentView(root)}
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
  clear("مساعد Hakamo للتمريض","Hakamo Nursing Assistant • بسيط، سريع، عربي أولًا")
  val hero=LinearLayout(this).apply{
    orientation=LinearLayout.VERTICAL
    setPadding(dp(18),dp(18),dp(18),dp(18))
    background=glass(if(dark())Color.argb(190,9,38,42) else Color.argb(220,235,250,247),Color.rgb(66,174,158),24)
  }
  hero.addView(tv("💧 HAKAMO",13f,accent,true))
  hero.addView(tv("كل أدوات التمريض في مكان واحد",22f,fg(),true))
  hero.addView(tv("حسابات • تقييم • أدوية • مراجع\nالحاسبات • التقييم • الأدوية • المراجعs",12f,muted()))
  body.addView(hero,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(14)})
  menu("🧮","الحاسبات • الحاسبات","معدل المحلول • الجرعات • التخفيف • الوحدات"){calculators()}
  menu("💊","دليل الأدوية","اسم الدواء • الاستخدامات • التحذيرات • الآثار الجانبية"){medicines()}
  menu("🩺","التقييم","GCS • NEWS2 • BMI • BSA • I&O • إخراج البول"){assessment()}
  menu("📚","المراجع","العلامات الحيوية • التحاليل • الاختصارات • السلامة"){reference()}
  menu("📏","النطاقات الطبيعية","علامات حيوية • تحاليل • أطفال • نمو • بول • غازات الدم"){normalRanges()}
  menu("🛡️","سلامة إعطاء الدواء","حقوق الدواء • التحقق من الهوية • الحساسية • الطريق • التركيز"){safety()};menu("📲","تواصل مع Hakamo","تيليجرام • تيك توك • فيسبوك • يوتيوب"){contact()}
  val n=tv("⚠ مهم: المعلومات التعليمية لا تستبدل وصف الطبيب أو الصيدلي أو بروتوكول المنشأة. لا تعتمد على التطبيق وحده لاتخاذ قرار علاجي.",11f,muted())
  n.setPadding(dp(12),dp(14),dp(12),dp(14))
  n.background=glass(if(dark())Color.argb(180,25,22,17) else Color.argb(220,255,249,236),if(dark())Color.rgb(82,70,49) else Color.rgb(235,207,150),16)
  body.addView(n)
}
 private fun calculators(){page="calculators";parentPage="home";clear("🧮 الحاسبات","حسابات سريعة وواضحة");menu("💧","معدل المحلول","مل/ساعة + قطرة/دقيقة + زمن الانتهاء"){iv()};menu("💊","حساب الجرعة","حساب الجرعة من التركيز المتاح"){dose()};menu("🧪","التخفيف","C1 × V1 = C2 × V2"){dilute()};menu("⚖","الحساب حسب الوزن","ملغم/كجم أو ميكروغرام/كجم • حساب رياضي فقط"){weight()};menu("🔁","تحويل الوحدات","ملغم • جم • ميكروغرام • مل • لتر • كجم"){units()}}
 private fun inp(h:String)=EditText(this).apply{hint=h;textSize=16f;setSingleLine();setTextColor(fg());setHintTextColor(muted());setPadding(dp(14),0,dp(14),0);background=glass(if(dark())Color.rgb(9,14,23) else Color.rgb(250,252,255),if(dark())Color.rgb(43,57,77) else Color.rgb(214,222,233),15)}
 private fun btn(s:String,go:()->Unit)=tv(s,15f,Color.rgb(3,20,19),true).apply{gravity=Gravity.CENTER;background=glass(accent,accent,16);setOnClickListener{go()};setPadding(dp(8),0,dp(8),0)}
 private fun out(s:String){val r=tv(s,18f,fg(),true);r.gravity=Gravity.CENTER;r.setPadding(dp(14),dp(18),dp(14),dp(18));r.background=glass(if(dark())Color.rgb(8,39,42) else Color.rgb(236,251,247),Color.rgb(63,181,164),20);body.addView(r,LinearLayout.LayoutParams(-1,-2).apply{topMargin=dp(14)});r.alpha=0f;r.animate().alpha(1f).setDuration(350).start()}
 private fun add(v:android.view.View){body.addView(v,LinearLayout.LayoutParams(-1,dp(56)).apply{bottomMargin=dp(9)})}
 private fun num(e:EditText)=e.text.toString().toDoubleOrNull() ?: -1.0
 private fun fmt(x:Double)=if(x.isFinite()&&abs(x-round(x))<1e-8)round(x).toInt().toString() else String.format("%.2f",x)
 private fun iv(){page="iv";parentPage="calculators";clear("💧 معدل المحلول","حساب مل/ساعة وقطرة/دقيقة وزمن الانتهاء • IV Rate");val v=inp("الكمية (مل) • الكمية (مل)");val h=inp("الساعات • الساعات");val m=inp("الدقائق • الدقائق");add(v);add(h);add(m);val sp=Spinner(this);sp.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,arrayOf("بدون قطرات","10 قطرة/مل","15 قطرة/مل","20 قطرة/مل","60 قطرة/مل"));body.addView(sp,LinearLayout.LayoutParams(-1,dp(52)).apply{bottomMargin=dp(10)});body.addView(btn("احسب"){val a=num(v);val t=num(h)+num(m)/60;if(a>0&&t>0){var s="معدل المحلول = ${fmt(a/t)} مل/ساعة\n ${fmt(a/t)} mL/hr";val f=when(sp.selectedItemPosition){1->10;2->15;3->20;4->60;else->0};if(f>0)s+="\n\nمعدل القطرات = ${round(a*f/(t*60)).toInt()} قطرة/دقيقة\n ${round(a*f/(t*60)).toInt()} gtt/min";s+="\n\nمدة المحلول = ${fmt(t)} ساعة\nمدة المحلول = ${fmt(t)} hr";out(s)}else toast("أدخل القيم الصحيحة")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun dose(){page="dose";parentPage="calculators";clear("💊 حساب الجرعة","حساب رياضي فقط");val d=inp("الجرعة المطلوبة (ملغم)");val a=inp("الجرعة المتاحة (ملغم)");val v=inp("الحجم المتاح (مل) • Available volume");add(d);add(a);add(v);body.addView(btn("احسب"){val x=num(d);val y=num(a);val z=num(v);if(x>0&&y>0&&z>0)out("الحجم المطلوب = ${fmt(x/y*z)} مل\nVolume = ${fmt(x/y*z)} mL")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
 private fun dilute(){page="dilute";parentPage="calculators";clear("🧪 حساب التخفيف","C1 × V1 = C2 × V2");val c1=inp("التركيز الأصلي C1 • Original");val c2=inp("التركيز المطلوب C2 • Desired");val v2=inp("الحجم النهائي V2 (مل) • Final volume");add(c1);add(c2);add(v2);body.addView(btn("احسب V1\nCalculate V1"){val a=num(c1);val b=num(c2);val c=num(v2);if(a>0&&b>0&&c>0)out("الحجم المطلوب V1 = ${fmt(b*c/a)} مل\nV1 = ${fmt(b*c/a)} mL")else toast("أدخل القيم")},LinearLayout.LayoutParams(-1,dp(54)))}
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
 private val marketUrl="https://raw.githubusercontent.com/karem505/egyptian-drug-database/main/data/egyptian-drugs.json"

 private fun medicines(){page="medicines";parentPage="home";
  clear("💊 دليل أدوية السوق المصري","قاعدة بيانات موسعة • الاسم التجاري • المادة الفعالة • الشركة • السعر");
  val status=tv("جاري تجهيز قاعدة الأدوية…",12f,muted()); body.addView(status,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(8)})
  val search=inp("ابحث باسم الدواء أو المادة الفعالة أو الشركة"); add(search)
  val actions=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL}
  val refreshBtn=btn("↻ تحديث قاعدة الأدوية") { loadMarketDrugs(status,search) }
  actions.addView(refreshBtn,LinearLayout.LayoutParams(0,dp(52),1f).apply{rightMargin=dp(6)})
  val liveBtn=btn("🌐 تحقق من الأسعار") { try{startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.dawaagate.com/medicines")))}catch(_:Exception){toast("تعذر فتح المصدر")} }
  actions.addView(liveBtn,LinearLayout.LayoutParams(0,dp(52),1f).apply{leftMargin=dp(6)})
  body.addView(actions)
  val list=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL}; body.addView(list)
  fun render(){
    list.removeAllViews(); val q=search.text.toString().trim().lowercase(java.util.Locale.ROOT)
    val data=if(marketDrugs.isNotEmpty()) marketDrugs else drugs.map{MarketDrug(it.generic,it.ar,it.generic,"—",it.category,it.category,null)}
    var shown=0
    data.forEach{d->
      val text=(d.en+" "+d.ar+" "+d.scientific+" "+d.manufacturer+" "+d.category).lowercase(java.util.Locale.ROOT)
      if(q.isEmpty()||text.contains(q)){
        shown++
        val c=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(dp(15),dp(13),dp(15),dp(13));background=glass();setOnClickListener{marketDrugDetail(d)}}
        c.addView(tv(if(d.ar.isBlank()) d.en else d.ar,17f,fg(),true));
        c.addView(tv(d.en,12f,accent,true));
        c.addView(tv(if(d.scientific.isBlank())"المادة الفعالة: غير متاحة" else "المادة الفعالة: ${d.scientific}",11f,muted()));
        val price=if(d.price!=null) "${fmt(d.price!!)} جنيه" else "السعر غير متاح";
        c.addView(tv("💰 $price   •   ${d.manufacturer.ifBlank{"الشركة غير متاحة"}}",12f,fg(),true));
        list.addView(c,LinearLayout.LayoutParams(-1,-2).apply{bottomMargin=dp(8)})
        if(shown>=120) return@forEach
      }
    }
    if(shown==0) list.addView(tv("لا توجد نتائج مطابقة.",14f,muted()).apply{setPadding(dp(12),dp(20),dp(12),dp(20))})
    status.text=if(marketLoaded) "تم تحميل ${marketDrugs.size} صنفًا • البيانات المفتوحة آخر تحديث معلن: يونيو 2026" else "وضع احتياطي: ${drugs.size} دواء تعليمي مدمج"
  }
  search.addTextChangedListener(object:android.text.TextWatcher{override fun beforeTextChanged(s:CharSequence?,st:Int,c:Int,a:Int){};override fun onTextChanged(s:CharSequence?,b:Int,c:Int,a:Int){render()};override fun afterTextChanged(e:android.text.Editable?){} })
  render()
  if(!marketLoaded) loadMarketDrugs(status,search)
 }

 private fun loadMarketDrugs(status:TextView,search:EditText){
  status.text="جاري تحديث قاعدة الأدوية من الإنترنت…"
  Thread{
   try{
    val conn=(java.net.URL(marketUrl).openConnection() as java.net.HttpURLConnection).apply{connectTimeout=15000;readTimeout=30000;requestMethod="GET";setRequestProperty("User-Agent","Hakamo-IV-Calculator/6.2")}
    val json=conn.inputStream.bufferedReader(Charsets.UTF_8).use{it.readText()}; conn.disconnect()
    val arr=org.json.JSONArray(json); val out=ArrayList<MarketDrug>(arr.length())
    for(i in 0 until arr.length()){
      val o=arr.getJSONObject(i); val price=if(o.isNull("price_egp"))null else o.optDouble("price_egp",Double.NaN).takeIf{!it.isNaN()}
      out.add(MarketDrug(o.optString("commercial_name_en"),o.optString("commercial_name_ar"),o.optString("scientific_name"),o.optString("manufacturer"),o.optString("route"),o.optString("drug_class"),price))
    }
    marketDrugs=out.sortedBy{it.ar.ifBlank{it.en}.lowercase(java.util.Locale.ROOT)}; marketLoaded=true
    getPreferences(0).edit().putLong("market_update",System.currentTimeMillis()).apply()
    runOnUiThread{medicines()}
   }catch(e:Exception){runOnUiThread{status.text="تعذر التحديث الآن — استخدم زر تحديث لاحقًا. ${e.javaClass.simpleName}"}}
  }.start()
 }

 private fun marketDrugDetail(d:MarketDrug){page="marketDrug";parentPage="medicines";clear("💊 ${if(d.ar.isBlank())d.en else d.ar}",d.en)
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
