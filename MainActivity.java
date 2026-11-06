package com.veni.health;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.net.Uri;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.widget.*;
import android.text.InputType;
import java.util.Locale;

public class MainActivity extends Activity {
    int water = 0, calories = 0;
    boolean darkMode = false;
    SharedPreferences prefs;
    LinearLayout root, foodList;
    TextView waterText, caloriesText;
    Uri selectedFoodPhoto = null;
    String language = "English";
    final String[] languages = {"English", "తెలుగు — Telugu", "हिन्दी — Hindi", "தமிழ் — Tamil", "ಕನ್ನಡ — Kannada", "മലയാളം — Malayalam", "मराठी — Marathi", "বাংলা — Bengali", "اردو — Urdu"};
    final String[] codes = {"en", "te", "hi", "ta", "kn", "ml", "mr", "bn", "ur"};
    final int green = Color.rgb(28, 76, 58);
    final int pale = Color.rgb(232, 242, 233);

    // Each translation row follows the same order as KEYS below.
    final String[] KEYS = {
        "Your caring health companion", "Small steps matter. Build healthier habits one meal at a time.", "Settings / Language", "Choose app language", "FOOD LOGGED", "WATER LOGGED", "What did you eat?", "Log a meal photo", "Describe meal by voice", "Photo logging is part of the prototype. AI photo recognition needs to be connected before launch.", "Voice logging is planned. In this prototype, type your meal below.", "Quick food log", "Food (e.g. 2 idlis with sambar)", "Estimated calories (e.g. 300)", "Add meal", "Enter a food and calorie estimate.", "Please enter a valid calorie number.", "Water tracker", "Log a glass whenever you drink water.", "+ Add one glass", "A gentle reminder", "Food and calorie values are estimates. Eat regularly, avoid extreme diets, and choose changes that feel sustainable.", "Coming next", "AI photo recognition, voice-to-food logging, nutrition breakdown, personalized diet plans, weekly progress, and optional water reminders.", "OK", " glass", " glasses", "VENI • Prototype 0.2"
    };
    final String[][] T = {
      KEYS,
      {"మీ ఆరోగ్యానికి తోడుగా", "చిన్న అడుగులే ముఖ్యం. ప్రతి భోజనంతో ఆరోగ్యకరమైన అలవాట్లు పెంచుకోండి.", "సెట్టింగ్స్ / భాష", "యాప్ భాషను ఎంచుకోండి", "నమోదైన ఆహారం", "నమోదైన నీరు", "మీరు ఏమి తిన్నారు?", "ఆహార ఫోటో నమోదు", "వాయిస్‌తో భోజనం వివరించండి", "ఇది నమూనా యాప్‌లోని ఫీచర్. ప్రారంభానికి ముందు AI ఫోటో గుర్తింపును అనుసంధానించాలి.", "వాయిస్ నమోదు త్వరలో. ప్రస్తుతం మీ భోజనాన్ని కింద టైప్ చేయండి.", "త్వరిత ఆహార నమోదు", "ఆహారం (ఉదా. 2 ఇడ్లీలు, సాంబార్)", "అంచనా కేలరీలు (ఉదా. 300)", "భోజనం జోడించండి", "ఆహారం మరియు కేలరీల అంచనాను నమోదు చేయండి.", "సరైన కేలరీ సంఖ్యను నమోదు చేయండి.", "నీటి ట్రాకర్", "నీరు తాగినప్పుడల్లా ఒక గ్లాస్ నమోదు చేయండి.", "+ ఒక గ్లాస్ జోడించండి", "చిన్న గుర్తుచూపు", "ఆహారం, కేలరీల విలువలు అంచనాలు మాత్రమే. క్రమం తప్పకుండా తినండి; అతిగా డైట్ చేయకండి.", "తర్వాత రానున్నవి", "AI ఫోటో గుర్తింపు, వాయిస్ నమోదు, పోషక వివరాలు, వ్యక్తిగత ఆహార ప్రణాళికలు, వారపు పురోగతి, నీటి రిమైండర్లు.", "సరే", " గ్లాస్", " గ్లాసులు", "VENI • నమూనా 0.2"},
      {"आपका स्वास्थ्य साथी", "छोटे कदम मायने रखते हैं। हर भोजन के साथ स्वस्थ आदतें बनाएँ।", "सेटिंग्स / भाषा", "ऐप की भाषा चुनें", "दर्ज किया भोजन", "दर्ज किया पानी", "आपने क्या खाया?", "खाने की फोटो दर्ज करें", "आवाज़ से भोजन बताएँ", "यह प्रोटोटाइप का हिस्सा है। लॉन्च से पहले AI फोटो पहचान जोड़नी होगी।", "आवाज़ से लॉगिंग जल्द आएगी। अभी नीचे भोजन टाइप करें।", "जल्दी भोजन दर्ज करें", "खाना (जैसे 2 इडली और सांभर)", "अनुमानित कैलोरी (जैसे 300)", "भोजन जोड़ें", "खाना और कैलोरी का अनुमान दर्ज करें।", "कैलोरी की सही संख्या दर्ज करें।", "पानी ट्रैकर", "पानी पीने पर एक गिलास दर्ज करें।", "+ एक गिलास जोड़ें", "एक छोटी याद दिलाना", "भोजन और कैलोरी के मान अनुमान हैं। नियमित खाएँ और अत्यधिक डाइट से बचें।", "आगे आने वाला", "AI फोटो पहचान, आवाज़ से भोजन, पोषण विवरण, व्यक्तिगत डाइट प्लान, साप्ताहिक प्रगति और पानी रिमाइंडर।", "ठीक है", " गिलास", " गिलास", "VENI • प्रोटोटाइप 0.2"},
      {"உங்கள் ஆரோக்கியத் துணை", "சிறிய படிகள் முக்கியம். ஒவ்வொரு உணவிலும் ஆரோக்கியமான பழக்கங்களை வளர்த்திடுங்கள்.", "அமைப்புகள் / மொழி", "செயலி மொழியைத் தேர்ந்தெடுக்கவும்", "பதிவான உணவு", "பதிவான தண்ணீர்", "என்ன சாப்பிட்டீர்கள்?", "உணவுப் புகைப்படத்தைப் பதிவு செய்", "குரலில் உணவை விவரிக்கவும்", "இது மாதிரி செயலியின் அம்சம். வெளியீட்டுக்கு முன் AI புகைப்பட அடையாளம் இணைக்கப்பட வேண்டும்.", "குரல் பதிவு பின்னர் வரும். இப்போது கீழே உணவைத் தட்டச்சு செய்யவும்.", "விரைவு உணவுப் பதிவு", "உணவு (எ.கா. 2 இட்லி, சாம்பார்)", "மதிப்பிடப்பட்ட கலோரி (எ.கா. 300)", "உணவைச் சேர்", "உணவு மற்றும் கலோரி மதிப்பீட்டை உள்ளிடவும்.", "சரியான கலோரி எண்ணை உள்ளிடவும்.", "தண்ணீர் கண்காணிப்பு", "தண்ணீர் குடிக்கும்போது ஒரு கண்ணாடி பதிவு செய்யவும்.", "+ ஒரு கண்ணாடி சேர்", "சிறிய நினைவூட்டல்", "உணவு மற்றும் கலோரி மதிப்புகள் மதிப்பீடுகள். சீராகச் சாப்பிட்டு, கடுமையான உணவுக் கட்டுப்பாட்டைத் தவிர்க்கவும்.", "அடுத்து", "AI புகைப்பட அடையாளம், குரல் பதிவு, ஊட்டச்சத்து விவரங்கள், தனிப்பட்ட உணவுத் திட்டங்கள், வாராந்திர முன்னேற்றம், தண்ணீர் நினைவூட்டல்கள்.", "சரி", " கண்ணாடி", " கண்ணாடிகள்", "VENI • மாதிரி 0.2"},
      {"ನಿಮ್ಮ ಆರೋಗ್ಯದ ಸಂಗಾತಿ", "ಸಣ್ಣ ಹೆಜ್ಜೆಗಳೂ ಮುಖ್ಯ. ಪ್ರತಿ ಊಟದೊಂದಿಗೆ ಆರೋಗ್ಯಕರ ಅಭ್ಯಾಸ ಬೆಳೆಸಿಕೊಳ್ಳಿ.", "ಸೆಟ್ಟಿಂಗ್‌ಗಳು / ಭಾಷೆ", "ಆ್ಯಪ್ ಭಾಷೆಯನ್ನು ಆಯ್ಕೆಮಾಡಿ", "ದಾಖಲಿಸಿದ ಆಹಾರ", "ದಾಖಲಿಸಿದ ನೀರು", "ನೀವು ಏನು ತಿಂದಿರಿ?", "ಆಹಾರದ ಫೋಟೋ ದಾಖಲಿಸಿ", "ಧ್ವನಿಯಲ್ಲಿ ಊಟವನ್ನು ವಿವರಿಸಿ", "ಇದು ಮಾದರಿ ಆ್ಯಪ್‌ನ ವೈಶಿಷ್ಟ್ಯ. ಬಿಡುಗಡೆಗೂ ಮೊದಲು AI ಫೋಟೋ ಗುರುತಿಸುವಿಕೆಯನ್ನು ಜೋಡಿಸಬೇಕು.", "ಧ್ವನಿ ದಾಖಲಿಕೆ ಮುಂದಿನ ಹಂತದಲ್ಲಿ. ಈಗ ಕೆಳಗೆ ಆಹಾರವನ್ನು ಟೈಪ್ ಮಾಡಿ.", "ತ್ವರಿತ ಆಹಾರ ದಾಖಲಿಕೆ", "ಆಹಾರ (ಉದಾ. 2 ಇಡ್ಲಿ, ಸಾಂಬಾರ್)", "ಅಂದಾಜು ಕ್ಯಾಲೊರಿಗಳು (ಉದಾ. 300)", "ಊಟ ಸೇರಿಸಿ", "ಆಹಾರ ಮತ್ತು ಕ್ಯಾಲೊರಿ ಅಂದಾಜು ನಮೂದಿಸಿ.", "ಸರಿಯಾದ ಕ್ಯಾಲೊರಿ ಸಂಖ್ಯೆಯನ್ನು ನಮೂದಿಸಿ.", "ನೀರಿನ ಟ್ರ್ಯಾಕರ್", "ನೀರು ಕುಡಿದಾಗ ಒಂದು ಗ್ಲಾಸ್ ದಾಖಲಿಸಿ.", "+ ಒಂದು ಗ್ಲಾಸ್ ಸೇರಿಸಿ", "ಸಣ್ಣ ನೆನಪಿನ ಸೂಚನೆ", "ಆಹಾರ ಮತ್ತು ಕ್ಯಾಲೊರಿ ಮೌಲ್ಯಗಳು ಅಂದಾಜು ಮಾತ್ರ. ನಿಯಮಿತವಾಗಿ ತಿನ್ನಿ; ಅತಿಯಾದ ಡಯಟ್ ತಪ್ಪಿಸಿ.", "ಮುಂದೆ", "AI ಫೋಟೋ ಗುರುತಿಸುವಿಕೆ, ಧ್ವನಿ ದಾಖಲಿಕೆ, ಪೋಷಕಾಂಶ ವಿವರಗಳು, ವೈಯಕ್ತಿಕ ಆಹಾರ ಯೋಜನೆಗಳು, ವಾರದ ಪ್ರಗತಿ ಮತ್ತು ನೀರಿನ ನೆನಪಿನ ಸೂಚನೆಗಳು.", "ಸರಿ", " ಗ್ಲಾಸ್", " ಗ್ಲಾಸ್‌ಗಳು", "VENI • ಮಾದರಿ 0.2"},
      {"നിങ്ങളുടെ ആരോഗ്യ കൂട്ടുകാരൻ", "ചെറിയ ചുവടുകൾ പ്രധാനമാണ്. ഓരോ ഭക്ഷണത്തിലൂടെയും ആരോഗ്യകരമായ ശീലങ്ങൾ വളർത്തൂ.", "ക്രമീകരണങ്ങൾ / ഭാഷ", "ആപ്പിന്റെ ഭാഷ തിരഞ്ഞെടുക്കുക", "രേഖപ്പെടുത്തിയ ഭക്ഷണം", "രേഖപ്പെടുത്തിയ വെള്ളം", "നിങ്ങൾ എന്താണ് കഴിച്ചത്?", "ഭക്ഷണ ഫോട്ടോ രേഖപ്പെടുത്തുക", "ശബ്ദത്തിൽ ഭക്ഷണം വിവരിക്കുക", "ഇത് പ്രോട്ടോടൈപ്പിലെ സവിശേഷതയാണ്. പുറത്തിറക്കുന്നതിന് മുമ്പ് AI ഫോട്ടോ തിരിച്ചറിയൽ ബന്ധിപ്പിക്കണം.", "ശബ്ദ രേഖപ്പെടുത്തൽ പിന്നീട്. ഇപ്പോൾ താഴെ ഭക്ഷണം ടൈപ്പ് ചെയ്യുക.", "വേഗത്തിലുള്ള ഭക്ഷണ രേഖ", "ഭക്ഷണം (ഉദാ. 2 ഇഡ്ഡലി, സാമ്പാർ)", "കണക്കാക്കിയ കലോറി (ഉദാ. 300)", "ഭക്ഷണം ചേർക്കുക", "ഭക്ഷണവും കലോറി കണക്കും നൽകുക.", "ശരിയായ കലോറി സംഖ്യ നൽകുക.", "വെള്ളം ട്രാക്കർ", "വെള്ളം കുടിക്കുമ്പോൾ ഒരു ഗ്ലാസ് രേഖപ്പെടുത്തുക.", "+ ഒരു ഗ്ലാസ് ചേർക്കുക", "ഒരു ചെറിയ ഓർമ്മപ്പെടുത്തൽ", "ഭക്ഷണവും കലോറിയും ഏകദേശ കണക്കുകളാണ്. പതിവായി ഭക്ഷണം കഴിക്കുക; കടുത്ത ഡയറ്റ് ഒഴിവാക്കുക.", "അടുത്തതായി", "AI ഫോട്ടോ തിരിച്ചറിയൽ, ശബ്ദ രേഖപ്പെടുത്തൽ, പോഷക വിവരങ്ങൾ, വ്യക്തിഗത ഭക്ഷണ പദ്ധതികൾ, ആഴ്ചയിലെ പുരോഗതി, വെള്ളം ഓർമ്മപ്പെടുത്തലുകൾ.", "ശരി", " ഗ്ലാസ്", " ഗ്ലാസുകൾ", "VENI • പ്രോട്ടോടൈപ്പ് 0.2"},
      {"तुमचा आरोग्य साथीदार", "लहान पावले महत्त्वाची आहेत. प्रत्येक जेवणासोबत आरोग्यदायी सवयी लावा.", "सेटिंग्ज / भाषा", "अॅपची भाषा निवडा", "नोंदवलेले अन्न", "नोंदवलेले पाणी", "तुम्ही काय खाल्ले?", "अन्नाचा फोटो नोंदवा", "आवाजाने जेवण सांगा", "हा प्रोटोटाइपमधील भाग आहे. सुरू करण्यापूर्वी AI फोटो ओळख जोडावी लागेल.", "आवाज नोंदणी पुढे येईल. आत्ता खाली अन्न टाइप करा.", "जलद अन्न नोंद", "अन्न (उदा. 2 इडली आणि सांबार)", "अंदाजे कॅलरी (उदा. 300)", "जेवण जोडा", "अन्न आणि कॅलरीचा अंदाज भरा.", "कॅलरीची योग्य संख्या भरा.", "पाणी ट्रॅकर", "पाणी प्यायल्यावर एक ग्लास नोंदवा.", "+ एक ग्लास जोडा", "एक छोटी आठवण", "अन्न आणि कॅलरीची मूल्ये अंदाजे आहेत. नियमित खा आणि अतिशय कठोर डाएट टाळा.", "पुढे", "AI फोटो ओळख, आवाजाने नोंद, पोषण माहिती, वैयक्तिक आहार योजना, साप्ताहिक प्रगती आणि पाण्याच्या आठवणी.", "ठीक आहे", " ग्लास", " ग्लास", "VENI • प्रोटोटाइप 0.2"},
      {"আপনার স্বাস্থ্যসঙ্গী", "ছোট পদক্ষেপ গুরুত্বপূর্ণ। প্রতিটি খাবারের সঙ্গে স্বাস্থ্যকর অভ্যাস গড়ুন।", "সেটিংস / ভাষা", "অ্যাপের ভাষা বেছে নিন", "নথিভুক্ত খাবার", "নথিভুক্ত জল", "আপনি কী খেয়েছেন?", "খাবারের ছবি নথিভুক্ত করুন", "কণ্ঠে খাবারের বিবরণ দিন", "এটি প্রোটোটাইপের অংশ। চালুর আগে AI ছবি শনাক্তকরণ যুক্ত করতে হবে।", "কণ্ঠে নথিভুক্তি পরে আসবে। আপাতত নিচে খাবার লিখুন।", "দ্রুত খাবার নথি", "খাবার (যেমন ২টি ইডলি ও সাম্বার)", "আনুমানিক ক্যালোরি (যেমন ৩০০)", "খাবার যোগ করুন", "খাবার ও ক্যালোরির অনুমান লিখুন।", "সঠিক ক্যালোরি সংখ্যা লিখুন।", "জল ট্র্যাকার", "জল পান করলে এক গ্লাস নথিভুক্ত করুন।", "+ এক গ্লাস যোগ করুন", "একটি ছোট মনে করানো", "খাবার ও ক্যালোরির মান আনুমানিক। নিয়মিত খান এবং অতিরিক্ত কঠোর ডায়েট এড়িয়ে চলুন।", "এরপর আসছে", "AI ছবি শনাক্তকরণ, কণ্ঠে খাবার নথি, পুষ্টির বিবরণ, ব্যক্তিগত খাদ্য পরিকল্পনা, সাপ্তাহিক অগ্রগতি ও জলের স্মরণ।", "ঠিক আছে", " গ্লাস", " গ্লাস", "VENI • প্রোটোটাইপ ০.২"},
      {"آپ کا صحت کا ساتھی", "چھوٹے قدم اہم ہیں۔ ہر کھانے کے ساتھ صحت مند عادتیں بنائیں۔", "ترتیبات / زبان", "ایپ کی زبان منتخب کریں", "درج شدہ کھانا", "درج شدہ پانی", "آپ نے کیا کھایا؟", "کھانے کی تصویر درج کریں", "آواز سے کھانا بتائیں", "یہ پروٹوٹائپ کا حصہ ہے۔ لانچ سے پہلے AI تصویر کی شناخت جوڑنی ہوگی۔", "آواز سے اندراج بعد میں آئے گا۔ فی الحال نیچے کھانا ٹائپ کریں۔", "فوری کھانے کا اندراج", "کھانا (مثلاً 2 اِڈلی اور سامبر)", "اندازاً کیلوریز (مثلاً 300)", "کھانا شامل کریں", "کھانا اور کیلوریز کا اندازہ درج کریں۔", "درست کیلوری نمبر درج کریں۔", "پانی ٹریکر", "پانی پینے پر ایک گلاس درج کریں۔", "+ ایک گلاس شامل کریں", "ایک نرم یاد دہانی", "کھانے اور کیلوریز کی قدریں اندازے ہیں۔ باقاعدگی سے کھائیں اور سخت ڈائٹ سے بچیں۔", "اگلا", "AI تصویر کی شناخت، آواز سے اندراج، غذائیت کی تفصیل، ذاتی خوراک کے منصوبے، ہفتہ وار پیش رفت اور پانی کی یاد دہانیاں۔", "ٹھیک ہے", " گلاس", " گلاس", "VENI • پروٹوٹائپ 0.2"}
    };

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("veni_settings", MODE_PRIVATE);
        language = prefs.getString("language", "English");
        water = prefs.getInt("water", 0);
        calories = prefs.getInt("calories", 0);
        darkMode = prefs.getBoolean("dark_mode", false);
        getWindow().setStatusBarColor(darkMode ? Color.rgb(20, 35, 29) : green);
        getWindow().setNavigationBarColor(darkMode ? Color.rgb(20, 35, 29) : green);
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(darkMode ? Color.rgb(18, 24, 21) : Color.rgb(248,249,245));
        ScrollView scroll = new ScrollView(this); scroll.addView(root); setContentView(scroll); buildUI();
    }
    int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density); }
    int langIndex() { for(int i=0;i<languages.length;i++) if(languages[i].equals(language)) return i; return 0; }
    String tr(String key) { for(int i=0;i<KEYS.length;i++) if(KEYS[i].equals(key)) return T[langIndex()][i]; return key; }
    int bodyTextColor() { return darkMode ? Color.rgb(235,239,236) : Color.DKGRAY; }
    TextView text(String s, int size, int color, boolean bold) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(darkMode && (color==Color.DKGRAY || color==Color.GRAY) ? bodyTextColor() : color); if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(dp(2),dp(3),dp(2),dp(3)); return t; }
    LinearLayout column() { LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setPadding(dp(18),dp(12),dp(18),dp(12)); return l; }
    LinearLayout.LayoutParams params() { return new LinearLayout.LayoutParams(-1,-2); }
    void card(String title, String body) { LinearLayout c=column(); c.setBackgroundColor(darkMode?Color.rgb(34,45,39):Color.WHITE); c.addView(text(title,18,darkMode?Color.rgb(175,220,187):green,true)); c.addView(text(body,14,bodyTextColor(),false)); LinearLayout.LayoutParams p=params(); p.setMargins(dp(16),dp(6),dp(16),dp(6)); root.addView(c,p); }
    Button button(String label) { Button b=new Button(this); b.setText(label); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(green)); return b; }
    void openLanguageSettings() {
        int current=langIndex();
        new android.app.AlertDialog.Builder(this).setTitle(tr("Choose app language")).setSingleChoiceItems(languages,current,(dialog,which)->{
            language=languages[which]; getSharedPreferences("veni_settings",MODE_PRIVATE).edit().putString("language",language).apply();
            dialog.dismiss(); buildUI();
        }).setNegativeButton(tr("OK"),null).show();
    }
    void buildUI() {
        root.removeAllViews();
        LinearLayout hero=column(); hero.setBackgroundColor(green); hero.addView(text("VENI",30,Color.WHITE,true)); hero.addView(text(tr("Your caring health companion"),15,Color.rgb(220,238,222),false)); hero.addView(text(tr("Small steps matter. Build healthier habits one meal at a time."),16,Color.WHITE,false)); root.addView(hero,params());
        Button settings=button("⚙  "+tr("Settings / Language")+" · "+language);
        LinearLayout settingWrap=column(); settingWrap.addView(settings,params());
        settings.setOnClickListener(v->openLanguageSettings());
        Button appearance=button((darkMode ? "☀  Light appearance" : "🌙  Dark appearance"));
        settingWrap.addView(appearance,params());
        appearance.setOnClickListener(v->{
            darkMode=!darkMode;
            prefs.edit().putBoolean("dark_mode",darkMode).apply();
            buildUI();
            getWindow().setStatusBarColor(darkMode ? Color.rgb(20,35,29) : green);
            getWindow().setNavigationBarColor(darkMode ? Color.rgb(20,35,29) : green);
        });
        root.addView(settingWrap,params());
        LinearLayout summary=new LinearLayout(this); summary.setPadding(dp(16),dp(10),dp(16),dp(4));
        LinearLayout a=new LinearLayout(this); a.setOrientation(1); a.setBackgroundColor(pale); a.setPadding(dp(12),dp(12),dp(12),dp(12));
        LinearLayout d=new LinearLayout(this); d.setOrientation(1); d.setBackgroundColor(pale); d.setPadding(dp(12),dp(12),dp(12),dp(12));
        caloriesText=text(calories+" kcal",22,green,true); waterText=text(water+(water==1?tr(" glass"):tr(" glasses")),22,green,true);
        a.addView(text(tr("FOOD LOGGED"),12,green,true)); a.addView(caloriesText); d.addView(text(tr("WATER LOGGED"),12,green,true)); d.addView(waterText);
        summary.addView(a,new LinearLayout.LayoutParams(0,-2,1)); LinearLayout.LayoutParams gap=new LinearLayout.LayoutParams(0,-2,1); gap.setMargins(dp(8),0,0,0); summary.addView(d,gap); root.addView(summary,params());
        LinearLayout actions=column(); actions.addView(text(tr("What did you eat?"),20,green,true)); Button photo=button("🥗  Scan Your Food · Nutrition & Benefits"); actions.addView(photo,params()); photo.setOnClickListener(v->openFoodScanner()); Button voice=button("🎙  "+tr("Describe meal by voice")); actions.addView(voice,params()); voice.setOnClickListener(v->startVoiceInput()); root.addView(actions,params());
        LinearLayout entry=column(); entry.addView(text(tr("Quick food log"),20,green,true)); EditText food=new EditText(this); food.setHint(tr("Food (e.g. 2 idlis with sambar)")); entry.addView(food,params()); EditText kcal=new EditText(this); kcal.setHint(tr("Estimated calories (e.g. 300)")); kcal.setInputType(InputType.TYPE_CLASS_NUMBER); entry.addView(kcal,params()); Button add=button(tr("Add meal")); entry.addView(add,params()); foodList=new LinearLayout(this); foodList.setOrientation(1); entry.addView(foodList,params());
        add.setOnClickListener(v->{String f=food.getText().toString().trim(), k=kcal.getText().toString().trim(); if(f.isEmpty()||k.isEmpty()){show(tr("Enter a food and calorie estimate."));return;} try{int n=Integer.parseInt(k);if(n<0||n>10000)throw new NumberFormatException();calories+=n;caloriesText.setText(calories+" kcal");prefs.edit().putInt("calories",calories).apply();foodList.addView(text("• "+f+" — "+n+" kcal",15,bodyTextColor(),false));food.setText("");kcal.setText("");}catch(Exception e){show(tr("Please enter a valid calorie number."));}}); root.addView(entry,params());
        LinearLayout waterCard=column(); waterCard.addView(text(tr("Water tracker"),20,green,true)); waterCard.addView(text(tr("Log a glass whenever you drink water."),14,Color.DKGRAY,false)); Button waterBtn=button(tr("+ Add one glass")); waterCard.addView(waterBtn,params()); waterBtn.setOnClickListener(v->{water++;waterText.setText(water+(water==1?tr(" glass"):tr(" glasses")));prefs.edit().putInt("water",water).apply();}); root.addView(waterCard,params());
        Button assistant=button("✨  VENI AI Assistant");
        LinearLayout assistantWrap=column(); assistantWrap.addView(assistant,params());
        assistant.setOnClickListener(v->openAssistant());
        root.addView(assistantWrap,params());
        Button login=button("👤  Sign in / Create account");
        LinearLayout loginWrap=column(); loginWrap.addView(login,params());
        login.setOnClickListener(v->openLogin());
        root.addView(loginWrap,params());
        Button reminders=button("⏰  Reminders");
        LinearLayout reminderWrap=column(); reminderWrap.addView(reminders,params());
        reminders.setOnClickListener(v->openReminderSettings());
        root.addView(reminderWrap,params());
        card(tr("A gentle reminder"),tr("Food and calorie values are estimates. Eat regularly, avoid extreme diets, and choose changes that feel sustainable."));
        TextView footer=text("VENI • Prototype 0.4",12,darkMode?Color.LTGRAY:Color.GRAY,false); footer.setGravity(Gravity.CENTER); footer.setPadding(0,dp(18),0,dp(24)); root.addView(footer,params());
    }
    void openFoodScanner() {
        LinearLayout panel = column();
        panel.addView(text("Discover what is in your food", 20, green, true));
        panel.addView(text("Type a food name, or choose a photo and then confirm what the food is. Nutrition values are estimates for a typical serving.", 14, bodyTextColor(), false));
        Button choosePhoto = button("📷  Choose food photo");
        panel.addView(choosePhoto, params());
        TextView photoStatus = text(selectedFoodPhoto == null ? "No photo selected" : "Photo selected", 12, bodyTextColor(), false);
        panel.addView(photoStatus, params());
        EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("Food name (e.g. egg, banana, rice)");
        panel.addView(name, params());
        choosePhoto.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
            pick.setType("image/*");
            try { startActivityForResult(Intent.createChooser(pick, "Choose a food photo"), 42); }
            catch (ActivityNotFoundException e) { show("No photo picker is available on this phone."); }
        });
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
            .setTitle("VENI Smart Food Scanner")
            .setView(panel)
            .setPositiveButton("Analyze food", null)
            .setNegativeButton("Cancel", null).create();
        dialog.setOnShowListener(x -> dialog.getButton(-1).setOnClickListener(v -> {
            String foodName = name.getText().toString().trim();
            if (foodName.isEmpty()) { name.setError("Enter the food name"); return; }
            showFoodNutrition(foodName);
            dialog.dismiss();
        }));
        dialog.show();
    }

    void showFoodNutrition(String input) {
        String key = input.toLowerCase(Locale.ROOT).trim();
        String title, serving, macros, vitamins, benefits;
        if (key.contains("egg")) {
            title="Egg"; serving="1 large boiled egg (about 50 g)";
            macros="78 kcal\nProtein: 6.3 g\nCarbohydrates: 0.6 g\nFat: 5.3 g";
            vitamins="Vitamin B12, vitamin D, vitamin A, riboflavin (B2), choline, selenium and phosphorus.";
            benefits="Protein supports muscle maintenance and repair. Choline supports normal brain and nerve function. Vitamin B12 supports nerves and red blood cells. Vitamin D contributes to bone health.";
        } else if (key.contains("banana")) {
            title="Banana"; serving="1 medium banana (about 118 g)";
            macros="105 kcal\nProtein: 1.3 g\nCarbohydrates: 27 g\nFat: 0.4 g\nFiber: 3.1 g";
            vitamins="Vitamin B6, vitamin C, potassium and manganese.";
            benefits="Carbohydrates provide energy. Fiber supports digestion. Potassium supports normal muscle and nerve function.";
        } else if (key.contains("apple")) {
            title="Apple"; serving="1 medium apple with skin (about 182 g)";
            macros="95 kcal\nProtein: 0.5 g\nCarbohydrates: 25 g\nFat: 0.3 g\nFiber: 4.4 g";
            vitamins="Vitamin C, vitamin K and potassium; also contains plant polyphenols.";
            benefits="Fiber supports digestive health and helps you feel satisfied. Whole fruit provides fluid and naturally occurring carbohydrates.";
        } else if (key.contains("milk")) {
            title="Cow's milk"; serving="1 cup whole milk (about 244 g)";
            macros="149 kcal\nProtein: 7.7 g\nCarbohydrates: 11.7 g\nFat: 8 g";
            vitamins="Calcium, vitamin B12, riboflavin (B2), phosphorus and vitamin D when fortified.";
            benefits="Protein supports body tissue maintenance. Calcium and phosphorus support bones and teeth. Nutrition varies with fat level and fortification.";
        } else if (key.contains("rice")) {
            title="Cooked white rice"; serving="1 cup cooked (about 158 g)";
            macros="205 kcal\nProtein: 4.3 g\nCarbohydrates: 44.5 g\nFat: 0.4 g\nFiber: 0.6 g";
            vitamins="Small amounts of B vitamins and minerals; enriched rice may contain added iron and B vitamins.";
            benefits="Provides carbohydrates for energy. Pairing rice with pulses, vegetables or other protein foods makes a more varied meal.";
        } else if (key.contains("oat")) {
            title="Oats, cooked"; serving="1 cup cooked (about 234 g)";
            macros="154 kcal\nProtein: 5.4 g\nCarbohydrates: 27.4 g\nFat: 2.6 g\nFiber: 4 g";
            vitamins="Manganese, phosphorus, magnesium, iron, zinc and vitamin B1.";
            benefits="Beta-glucan soluble fiber can help support healthy cholesterol levels as part of a balanced diet. Fiber also supports fullness and digestion.";
        } else if (key.contains("spinach")) {
            title="Spinach, cooked"; serving="½ cup cooked (about 90 g)";
            macros="21 kcal\nProtein: 2.7 g\nCarbohydrates: 3.4 g\nFat: 0.2 g\nFiber: 2.2 g";
            vitamins="Vitamin K, vitamin A, folate, vitamin C, iron and magnesium.";
            benefits="Provides folate for normal cell function and vitamin K for normal blood clotting and bone health. Plant iron is better absorbed with vitamin-C-rich foods.";
        } else if (key.contains("chicken")) {
            title="Chicken breast, cooked"; serving="100 g, skinless";
            macros="165 kcal\nProtein: 31 g\nCarbohydrates: 0 g\nFat: 3.6 g";
            vitamins="Niacin (B3), vitamin B6, selenium and phosphorus.";
            benefits="A protein-rich food that helps maintain and repair body tissues. Values change with cut, skin and cooking method.";
        } else {
            show("I don't have a verified nutrition entry for ‘"+input+"’ in this offline starter catalogue yet. Try egg, banana, apple, milk, cooked rice, oats, spinach or chicken. We can connect a broader nutrition database in a later update.");
            return;
        }
        LinearLayout result = column();
        result.setBackgroundColor(darkMode ? Color.rgb(34,45,39) : Color.WHITE);
        result.addView(text("🥗  FOOD IDENTIFIED", 12, green, true));
        result.addView(text(title, 26, darkMode ? Color.rgb(175,220,187) : green, true));
        result.addView(text("Serving: "+serving, 13, bodyTextColor(), false));
        result.addView(text("ENERGY & MACROS", 13, green, true));
        TextView macroView = text(macros, 18, bodyTextColor(), true);
        result.addView(macroView);
        result.addView(text("VITAMINS & MINERALS", 13, green, true));
        result.addView(text(vitamins, 15, bodyTextColor(), false));
        result.addView(text("HOW THIS FOOD HELPS", 13, green, true));
        result.addView(text(benefits, 15, bodyTextColor(), false));
        result.addView(text("Estimates for the stated serving. Variety, brand, portion and preparation change nutrition. A photo alone cannot reliably determine exact ingredients or portion size.", 12, Color.GRAY, false));
        ScrollView scroller = new ScrollView(this); scroller.addView(result);
        android.app.AlertDialog resultDialog = new android.app.AlertDialog.Builder(this)
            .setTitle("VENI Nutrition Report")
            .setView(scroller)
            .setPositiveButton("Add to food log", (d,w) -> {
                // Keep the logged calories aligned with the displayed example serving.
                int kcal = 0;
                try { kcal = Integer.parseInt(macros.substring(0, macros.indexOf(" kcal"))); } catch(Exception ignored) {}
                calories += kcal;
                if (caloriesText != null) caloriesText.setText(calories+" kcal");
                prefs.edit().putInt("calories", calories).apply();
                if (foodList != null) foodList.addView(text("• "+title+" — "+kcal+" kcal", 15, bodyTextColor(), false));
            })
            .setNegativeButton("Close", null).create();
        resultDialog.setOnShowListener(v -> {
            AlphaAnimation fade = new AlphaAnimation(0f, 1f); fade.setDuration(350); result.startAnimation(fade);
        });
        resultDialog.show();
    }

    void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, codes[langIndex()]);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe your meal");
        try { startActivityForResult(intent, 41); }
        catch (ActivityNotFoundException e) { show("Voice input is not available on this phone. You can still type your meal."); }
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 41 && resultCode == RESULT_OK && data != null) {
            java.util.ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) show("Voice captured: " + results.get(0) + "\nPlease add it in Quick food log and enter an estimated calorie value.");
        } else if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            selectedFoodPhoto = data.getData();
            show("Food photo selected. For this version, please type the food name so VENI can show the matching nutrition report. Automatic photo recognition will be added with a dedicated on-device model.");
        }
    }
    boolean hasInternetConnection() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm == null) return false;
            Network network = cm.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities caps = cm.getNetworkCapabilities(network);
            return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            return false;
        }
    }

    String offlineAnswer(String question) {
        String q = question.toLowerCase(Locale.ROOT);
        if (q.contains("water")) {
            return "Offline mode: You have logged " + water + " glass(es) of water today. Fluid needs vary by person.";
        } else if (q.contains("calorie") || q.contains("food") || q.contains("meal") || q.contains("protein")) {
            return "Offline mode: Your logged calorie estimate is " + calories
                + " kcal. For food nutrients, try VENI's Food Scanner with a food name such as egg, banana, apple, milk, rice, oats, spinach or chicken. Values are estimates.";
        } else if (q.contains("hello") || q.contains("hi") || q.contains("hey")) {
            return "Hello! I'm VENI. I can help with basic food, water and healthy-habit questions while offline.";
        }
        return "Offline mode: I can help with basic food, water and healthy-habit information. Connect to the internet and configure VENI's secure AI service for more detailed conversations.";
    }

    void askHybridAI(String question, TextView status, android.app.AlertDialog dialog) {
        if (!hasInternetConnection()) {
            status.setText("● Offline AI · No internet connection");
            show(offlineAnswer(question));
            return;
        }
        if (BuildConfig.VENI_AI_ENDPOINT == null || BuildConfig.VENI_AI_ENDPOINT.trim().isEmpty()) {
            status.setText("● Offline AI · Online service not configured");
            show(offlineAnswer(question) + "\n\nInternet is available, but VENI's secure online AI endpoint has not been configured yet.");
            return;
        }
        status.setText("● Connecting to Online AI…");
        new AsyncTask<Void, Void, String>() {
            String error = null;
            @Override protected String doInBackground(Void... ignored) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(BuildConfig.VENI_AI_ENDPOINT);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(12000);
                    connection.setReadTimeout(20000);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    JSONObject body = new JSONObject();
                    body.put("message", question);
                    byte[] payload = body.toString().getBytes("UTF-8");
                    connection.setFixedLengthStreamingMode(payload.length);
                    try (OutputStream out = connection.getOutputStream()) { out.write(payload); }
                    int code = connection.getResponseCode();
                    InputStream in = (code >= 200 && code < 300) ? connection.getInputStream() : connection.getErrorStream();
                    if (in == null) throw new Exception("Online service returned HTTP " + code);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] chunk = new byte[4096]; int count;
                    while ((count = in.read(chunk)) != -1) buffer.write(chunk, 0, count);
                    in.close();
                    if (code < 200 || code >= 300) throw new Exception("Online service returned HTTP " + code);
                    JSONObject response = new JSONObject(buffer.toString("UTF-8"));
                    String reply = response.optString("reply", "").trim();
                    if (reply.isEmpty()) throw new Exception("Online service response did not contain 'reply'");
                    return reply;
                } catch (Exception e) {
                    error = e.getMessage();
                    return null;
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }
            @Override protected void onPostExecute(String reply) {
                if (isFinishing()) return;
                if (reply != null) {
                    status.setText("● Online AI");
                    show(reply);
                } else {
                    status.setText("● Offline AI · Online request failed");
                    show(offlineAnswer(question) + "\n\nOnline AI was unavailable, so VENI switched back to offline mode.");
                }
            }
        }.execute();
    }

    void openAssistant() {
        LinearLayout panel = column();
        TextView status = text(hasInternetConnection()
            ? "● Internet connected · Checking online AI service"
            : "● Offline AI · No internet connection", 13, green, true);
        panel.addView(status, params());
        EditText question = new EditText(this);
        question.setHint("Ask about food, nutrients, water or healthy habits");
        panel.addView(question, params());
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
            .setTitle("VENI Hybrid AI")
            .setMessage("Automatic mode: VENI uses the online AI service when configured and reachable; otherwise it answers with its built-in offline helper.")
            .setView(panel)
            .setPositiveButton("Ask VENI", null)
            .setNegativeButton("Cancel", null).create();
        dialog.setOnShowListener(v -> dialog.getButton(-1).setOnClickListener(btn -> {
            String q = question.getText().toString().trim();
            if (q.isEmpty()) { question.setError("Type your question"); return; }
            askHybridAI(q, status, dialog);
        }));
        dialog.show();
    }
    void openLogin() {
        LinearLayout form = column();
        EditText email = new EditText(this); email.setHint("Email"); email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        EditText password = new EditText(this); password.setHint("Password"); password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        form.addView(email,params()); form.addView(password,params());
        new android.app.AlertDialog.Builder(this).setTitle("VENI account").setMessage("Demo form only. It does not create a real account or securely authenticate users.")
            .setView(form).setPositiveButton("Continue", (d,w) -> show("Account sign-in is not connected yet. A secure authentication service is required before launch."))
            .setNegativeButton("Cancel",null).show();
    }
    void openReminderSettings() {
        boolean enabled = prefs.getBoolean("reminders_enabled", false);
        new android.app.AlertDialog.Builder(this).setTitle("Reminders")
            .setMessage("Daily wellness reminders: " + (enabled ? "currently marked on" : "currently off") + ". Actual scheduled notifications require a notification receiver and manifest setup in the full Android project.")
            .setPositiveButton(enabled ? "Turn off" : "Turn on", (d,w) -> {
                prefs.edit().putBoolean("reminders_enabled", !enabled).apply();
                show("Reminder preference saved. Notification scheduling still needs to be wired into the full Android project.");
            }).setNegativeButton("Close",null).show();
    }
    void show(String s) { new android.app.AlertDialog.Builder(this).setTitle("VENI").setMessage(s).setPositiveButton(tr("OK"),null).show(); }
}
