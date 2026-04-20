const fs = require('fs');
const path = require('path');

const dashPath = path.join(__dirname, 'app/src/main/java/com/example/smartvillageapp/DashboardAdapter.java');
const adminPath = path.join(__dirname, 'app/src/main/java/com/example/smartvillageapp/AdminDashboardAdapter.java');

function fixDashboardAdapter() {
    let content = fs.readFileSync(dashPath, 'utf8');

    const switchRegex = /switch \s*\(\s*item\.getTitle\(\)\s*\)\s*\{[\s\S]*?case "नोकऱ्या":[\s\S]*?break;\s*\}/m;
    
    // I will replace it with a series of if-else
    const replacements = `
            String title = item.getTitle();
            if (title.equals(context.getString(R.string.about_village))) {
                intent = new Intent(context, AboutVillageActivity.class);
            } else if (title.equals(context.getString(R.string.contacts))) {
                intent = new Intent(context, ContactsActivity.class);
            } else if (title.equals(context.getString(R.string.businesses))) {
                intent = new Intent(context, BusinessActivity.class);
            } else if (title.equals(context.getString(R.string.market_prices))) {
                intent = new Intent(context, MarketActivity.class);
            } else if (title.equals(context.getString(R.string.notices))) {
                intent = new Intent(context, NoticesActivity.class);
            } else if (title.equals(context.getString(R.string.bills))) {
                intent = new Intent(context, BillsActivity.class);
            } else if (title.equals(context.getString(R.string.locations))) {
                intent = new Intent(context, LocationsActivity.class);
            } else if (title.equals(context.getString(R.string.complaints))) {
                intent = new Intent(context, ComplaintsActivity.class);
            } else if (title.equals(context.getString(R.string.schemes))) {
                intent = new Intent(context, SchemesActivity.class);
            } else if (title.equals(context.getString(R.string.developments))) {
                intent = new Intent(context, DevelopmentActivity.class);
            } else if (title.equals(context.getString(R.string.agricultural))) {
                intent = new Intent(context, AgriculturalActivity.class);
            } else if (title.equals(context.getString(R.string.jobs))) {
                intent = new Intent(context, JobsActivity.class);
            }
`;

    content = content.replace(switchRegex, replacements);
    fs.writeFileSync(dashPath, content, 'utf8');
}

function fixAdminAdapter() {
    let content = fs.readFileSync(adminPath, 'utf8');

    const switchRegex = /switch \s*\(\s*item\.title\s*\)\s*\{[\s\S]*?case "नोंदी":[\s\S]*?break;\s*\}/m;

    const replacements = `
            String t = item.title;
            if (t.equals(context.getString(R.string.contacts))) {
                context.startActivity(new Intent(context, AddContactActivity.class));
            } else if (t.equals(context.getString(R.string.notices))) {
                context.startActivity(new Intent(context, AddNoticeActivity.class));
            } else if (t.equals(context.getString(R.string.bills))) {
                context.startActivity(new Intent(context, BillsUserListActivity.class));
            } else if (t.equals(context.getString(R.string.locations))) {
                context.startActivity(new Intent(context, AddLocationActivity.class));
            } else if (t.equals(context.getString(R.string.complaints))) {
                context.startActivity(new Intent(context, AdminComplaintActivity.class));
            } else if (t.equals(context.getString(R.string.schemes))) {
                context.startActivity(new Intent(context, AddSchemeActivity.class));
            } else if (t.equals(context.getString(R.string.developments))) {
                context.startActivity(new Intent(context, AddDevelopmentActivity.class));
            } else if (t.equals(context.getString(R.string.agriculture))) {
                context.startActivity(new Intent(context, AgricultureActivity.class));
            } else if (t.equals(context.getString(R.string.users))) {
                context.startActivity(new Intent(context, AdminUserManagementActivity.class));
            } else if (t.equals(context.getString(R.string.logs))) {
                context.startActivity(new Intent(context, AdminLogsActivity.class));
            }
`;

    content = content.replace(switchRegex, replacements);
    fs.writeFileSync(adminPath, content, 'utf8');
}

fixDashboardAdapter();
fixAdminAdapter();
console.log('Adapters fixed');
