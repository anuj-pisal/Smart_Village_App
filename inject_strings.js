const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'app/src/main');
const engStringsFile = path.join(srcDir, 'res/values/strings.xml');
const marStringsFile = path.join(srcDir, 'res/values-mr/strings.xml');

const translatedStrings = JSON.parse(fs.readFileSync(path.join(__dirname, 'translated_strings.json'), 'utf8'));

// 1. Inject into strings.xml
function injectStringsXml(filePath, lang) {
    let content = fs.readFileSync(filePath, 'utf8');
    // Remove </resources> at the end
    content = content.replace(/<\/resources>\s*$/i, '');
    
    let toAppend = '';
    
    Object.keys(translatedStrings).forEach(englishText => {
        const item = translatedStrings[englishText];
        const key = item.key;
        const val = lang === 'mr' ? item.mr : englishText;
        
        // Escape characters cleanly
        let escapedVal = val
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '\\"')
            .replace(/'/g, "\\'");
            
        // Check if key already exists
        const keyRegex = new RegExp(`name="${key}"`);
        if (!keyRegex.test(content)) {
            toAppend += `    <string name="${key}">${escapedVal}</string>\n`;
        }
    });

    content += toAppend + '</resources>\n';
    fs.writeFileSync(filePath, content, 'utf8');
}

injectStringsXml(engStringsFile, 'en');
injectStringsXml(marStringsFile, 'mr');

// 2. Replace hardcoded strings in code
function processDirectory(directory) {
    const files = fs.readdirSync(directory);
    
    files.forEach(file => {
        const fullPath = path.join(directory, file);
        const stat = fs.statSync(fullPath);
        
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (file.endsWith('.xml') && directory.includes('layout')) {
            replaceInXml(fullPath);
        } else if (file.endsWith('.java')) {
            replaceInJava(fullPath);
        }
    });
}

function escapeRegExp(string) {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
}

function replaceInXml(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    let changed = false;

    Object.keys(translatedStrings).forEach(englishText => {
        const item = translatedStrings[englishText];
        const searchStr1 = `android:text="${englishText}"`;
        const replaceStr1 = `android:text="@string/${item.key}"`;
        
        const searchStr2 = `android:hint="${englishText}"`;
        const replaceStr2 = `android:hint="@string/${item.key}"`;

        if (content.includes(searchStr1)) {
            content = content.split(searchStr1).join(replaceStr1);
            changed = true;
        }
        if (content.includes(searchStr2)) {
            content = content.split(searchStr2).join(replaceStr2);
            changed = true;
        }
    });

    if (changed) {
        fs.writeFileSync(filePath, content, 'utf8');
    }
}

function replaceInJava(filePath) {
    let content = fs.readFileSync(filePath, 'utf8');
    let changed = false;
    
    const isAdapter = filePath.includes('Adapter');
    const isFragment = filePath.includes('Fragment');
    
    // Simplistic heuristic for getString call context
    const getStringPrefix = (isAdapter || content.includes('RecyclerView.ViewHolder')) ? 'context.getString' : 'getString';
    const altGetStringPrefix = content.includes('requireContext()') ? 'requireContext().getString' : 'getString';

    Object.keys(translatedStrings).forEach(englishText => {
        if (englishText.includes('\\n')) return; // skip regex complexities for multiline strings if any
        
        const item = translatedStrings[englishText];
        const searchStr = `"${englishText}"`;
        
        let prefix = getStringPrefix;
        
        // Handle Toast.makeText specially to avoid issues
        const toastRegexStr = escapeRegExp(`Toast.makeText(context, "${englishText}"`);
        const toastRegexStrThis = escapeRegExp(`Toast.makeText(this, "${englishText}"`);
        const toastRegexStrGet = escapeRegExp(`Toast.makeText(getContext(), "${englishText}"`);
        const toastRegexStrClassThis = new RegExp(`Toast\\.makeText\\([A-Za-z0-9_]+\\.this,\\s*"${escapeRegExp(englishText)}"`, 'g');
        
        if (content.includes(`Toast.makeText(`)) {
            if (content.includes(`Toast.makeText(context, "${englishText}"`)) {
                content = content.split(`Toast.makeText(context, "${englishText}"`).join(`Toast.makeText(context, context.getString(R.string.${item.key})`);
                changed = true;
            }
            if (content.includes(`Toast.makeText(this, "${englishText}"`)) {
                content = content.split(`Toast.makeText(this, "${englishText}"`).join(`Toast.makeText(this, getString(R.string.${item.key})`);
                changed = true;
            }
            if (content.includes(`Toast.makeText(getContext(), "${englishText}"`)) {
                content = content.split(`Toast.makeText(getContext(), "${englishText}"`).join(`Toast.makeText(getContext(), getString(R.string.${item.key})`);
                changed = true;
            }
            
            content = content.replace(toastRegexStrClassThis, (match) => {
                changed = true;
                return match.replace(`"${englishText}"`, `getString(R.string.${item.key})`);
            });
        }

        // Generic setError
        const errorRegex = new RegExp(`\\.setError\\(\\s*"${escapeRegExp(englishText)}"`, 'g');
        if (errorRegex.test(content)) {
            content = content.replace(errorRegex, `.setError(${isAdapter ? 'context.' : ''}getString(R.string.${item.key})`);
            changed = true;
        }

        // Generic setText
        const textRegex = new RegExp(`\\.setText\\(\\s*"${escapeRegExp(englishText)}"`, 'g');
        if (textRegex.test(content)) {
            content = content.replace(textRegex, `.setText(${isAdapter ? 'context.' : ''}getString(R.string.${item.key})`);
            changed = true;
        }

        // new AdminItem("English", ...)
        const adminRegex = new RegExp(`new\\s+AdminItem\\(\\s*"${escapeRegExp(englishText)}"`, 'g');
        if (adminRegex.test(content)) {
            content = content.replace(adminRegex, `new AdminItem(getString(R.string.${item.key})`);
            changed = true;
        }
    });

    if (changed) {
        fs.writeFileSync(filePath, content, 'utf8');
    }
}

processDirectory(srcDir);
console.log('Injection completed successfully.');
