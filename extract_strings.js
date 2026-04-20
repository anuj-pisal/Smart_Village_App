const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'app/src/main');
let foundStrings = {}; // map of key to original text

function generateKey(text) {
    // Generate a snake_case key based on the original string
    let key = text.trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '_')
        .replace(/^_+|_+$/g, '');
    
    if (key.length > 30) {
        key = key.substring(0, 30).replace(/_+$/, '');
    }
    
    if (!key || /^\d/.test(key)) {
        key = 'string_' + Math.floor(Math.random() * 1000000);
    }
    
    // Ensure uniqueness
    let finalKey = key;
    let counter = 1;
    while (foundStrings[finalKey] && foundStrings[finalKey] !== text) {
        finalKey = `${key}_${counter}`;
        counter++;
    }
    return finalKey;
}

function processDirectory(directory) {
    const files = fs.readdirSync(directory);
    
    files.forEach(file => {
        const fullPath = path.join(directory, file);
        const stat = fs.statSync(fullPath);
        
        if (stat.isDirectory()) {
            processDirectory(fullPath);
        } else if (file.endsWith('.xml') && directory.includes('layout')) {
            processXmlFile(fullPath);
        } else if (file.endsWith('.java')) {
            processJavaFile(fullPath);
        }
    });
}

function sanitizeForXml(text) {
    return text.replace(/&/g, '&amp;')
               .replace(/</g, '&lt;')
               .replace(/>/g, '&gt;')
               .replace(/"/g, '\\"')
               .replace(/'/g, "\\'");
}

function processXmlFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    
    // Look for android:text="something" or android:hint="something"
    const regex = /android:(text|hint)\s*=\s*"([^@+].*?)"/g;
    let match;
    
    while ((match = regex.exec(content)) !== null) {
        const text = match[2];
        if (text && text.trim() !== '') {
            const key = generateKey(text);
            foundStrings[key] = text;
        }
    }
}

function processJavaFile(filePath) {
    const content = fs.readFileSync(filePath, 'utf8');
    
    // Look for .makeText(..., "string", ...)
    // .setError("string")
    // .setText("string")
    // new AdminItem("string", ...) -> specific to this project
    // new DashboardItem(..., "string") -> specific to this project
    
    const regexList = [
        /\.makeText\s*\(\s*[^,]+,\s*"([^"]+)"/g,
        /\.setError\s*\(\s*"([^"]+)"/g,
        /\.setText\s*\(\s*"([^"]+)"/g,
        /new\s+AdminItem\s*\(\s*"([^"]+)"/g,
        /new\s+DashboardItem\s*\(\s*[^,]+,\s*"([^"]+)"/g,
        /setToolbarTitle\s*\(\s*"([^"]+)"/g
    ];

    let matches = [];

    regexList.forEach(regex => {
        let match;
        while ((match = regex.exec(content)) !== null) {
            const text = match[1];
            if (text && text.trim() !== '' && !text.includes('%') && text.length > 1) { // avoid format strings or very short things if possible, but let's include them for completeness
                const key = generateKey(text);
                foundStrings[key] = text;
            }
        }
    });
}

processDirectory(srcDir);

fs.writeFileSync(path.join(__dirname, 'discovered_strings.json'), JSON.stringify(foundStrings, null, 2));

console.log(`Found ${Object.keys(foundStrings).length} hardcoded strings. Check discovered_strings.json`);
