const fs = require('fs');
const path = require('path');

const engStrings = `
    <string name="about_app_desc">Smart Village App is designed to digitally connect villagers with essential services. It provides easy access to contacts, businesses, notices, and government schemes in one place, making rural life more efficient and connected.</string>
    <string name="features_title">Features</string>
    <string name="features_desc">• About Village – Village overview\\n• Contacts – Important contacts\\n• Businesses – Local services\\n• Market Prices – Latest rates\\n• Notices – Announcements\\n• Bills – Bill services\\n• Locations – Key places\\n• Complaints – Report issues\\n• Schemes – Govt benefits\\n• Development – Progress updates\\n• Agricultural – Farming info\\n• Jobs – Opportunities</string>
    <string name="tech_used_title">Technology Used</string>
    <string name="tech_used_desc">• Android (Java)\\n• Firebase Authentication\\n• Firestore Database\\n• Firebase Storage\\n• Material UI Components</string>
    <string name="dev_subtitle">Second Year IT Engineering Student, WCE</string>
    <string name="smart_village_project_desc">Smart Village Project</string>
`;

const mrStrings = `
    <string name="about_app_desc">स्मार्ट व्हिलेज ॲप हे गावकर्‍यांना अत्यावश्यक सेवांशी डिजिटल पद्धतीने जोडण्यासाठी डिझाइन केले आहे. हे संपर्क, व्यवसाय, सूचना आणि सरकारी योजनांमध्ये एकाच ठिकाणी सहज प्रवेश प्रदान करते, ज्यामुळे ग्रामीण जीवन अधिक कार्यक्षम होते.</string>
    <string name="features_title">वैशिष्ट्ये</string>
    <string name="features_desc">• गावाबद्दल – ग्राम आढावा\\n• संपर्क – महत्त्वाचे संपर्क\\n• व्यवसाय – स्थानिक सेवा\\n• बाजारभाव – ताजे दर\\n• सूचना – घोषणा\\n• बिले – बिल भरणे सेवा\\n• ठिकाणे – महत्त्वाची ठिकाणे\\n• तक्रारी – समस्यांची तक्रार\\n• योजना – सरकारी फायदे\\n• विकासकामे – प्रगतीचे अद्यतने\\n• शेती – शेतीविषयक माहिती\\n• नोकऱ्या – रोजगाराच्या संधी</string>
    <string name="tech_used_title">वापरलेले तंत्रज्ञान</string>
    <string name="tech_used_desc">• Android (Java)\\n• फायरबेस ऑथेंटिकेशन\\n• फायरस्टोर डेटाबेस\\n• फायरबेस स्टोरेज\\n• मटेरियल UI घटके</string>
    <string name="dev_subtitle">द्वितीय वर्ष आयटी अभियांत्रिकी विद्यार्थी, वालचंद</string>
    <string name="smart_village_project_desc">स्मार्ट व्हिलेज प्रकल्प</string>
`;

function injectStrings(filePath, newStrings) {
    let content = fs.readFileSync(filePath, 'utf8');
    content = content.replace(/<\/resources>\s*$/i, '');
    content += newStrings + '\n</resources>\n';
    fs.writeFileSync(filePath, content, 'utf8');
}

const engFile = path.join(__dirname, 'app/src/main/res/values/strings.xml');
const mrFile = path.join(__dirname, 'app/src/main/res/values-mr/strings.xml');

injectStrings(engFile, engStrings);
injectStrings(mrFile, mrStrings);

console.log("Success");
