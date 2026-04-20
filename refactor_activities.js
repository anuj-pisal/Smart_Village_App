const fs = require('fs');
const path = require('path');

const directoryPath = path.join(__dirname, 'app/src/main/java/com/example/smartvillageapp');

function walkDir(dir) {
    let results = [];
    const list = fs.readdirSync(dir);
    list.forEach(file => {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);
        if (stat && stat.isDirectory()) {
            results = results.concat(walkDir(fullPath));
        } else if (file.endsWith('.java')) {
            results.push(fullPath);
        }
    });
    return results;
}

const javaFiles = walkDir(directoryPath);

javaFiles.forEach(file => {
    // Skip BaseActivity itself
    if (file.endsWith('BaseActivity.java')) return;

    let content = fs.readFileSync(file, 'utf8');

    // 1. Replace extends AppCompatActivity with extends BaseActivity
    if (content.includes('extends AppCompatActivity')) {
        content = content.replace(/extends AppCompatActivity/g, 'extends BaseActivity');
        
        // Remove import androidx.appcompat.app.AppCompatActivity; since it's redundant if no other usage, but it's safe to leave or remove.
        // Actually, let's keep it to be safe, but just replace the class extension.
    }

    // 2. Remove manual attachBaseContext overrides from all files since BaseActivity now handles it
    const attachBaseContextRegex = /@Override\s+protected\s+void\s+attachBaseContext\s*\(\s*Context\s+[a-zA-Z_]+\s*\)\s*\{\s*super\.attachBaseContext\s*\(\s*LocaleHelper\.applyLocale\s*\(\s*[a-zA-Z_]+\s*\)\s*\);\s*\}/g;
    
    if (attachBaseContextRegex.test(content)) {
        content = content.replace(attachBaseContextRegex, '');
    }

    fs.writeFileSync(file, content, 'utf8');
});

console.log('Successfully refactored all activities to extend BaseActivity and removed redundant attachBaseContext methods.');
