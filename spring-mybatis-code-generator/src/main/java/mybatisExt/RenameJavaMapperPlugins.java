package mybatisExt;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameJavaMapperPlugins extends PluginAdapter {
    private String searchString;
    private String replaceString;
    private Pattern pattern;

    @Override
    public boolean validate(List<String> warnings) {
        searchString = properties.getProperty("searchString");
        replaceString = properties.getProperty("replaceString");

        boolean valid = StringUtility.stringHasValue(searchString)
                && StringUtility.stringHasValue(replaceString);

        if (valid) {
            pattern = Pattern.compile(searchString);
        } else {
            if (!StringUtility.stringHasValue(searchString)) {
                warnings.add(Messages.getString("ValidationError.18",
                        "RenameExampleClassPlugin",
                        "searchString"));
            }
            if (!StringUtility.stringHasValue(replaceString)) {
                warnings.add(Messages.getString("ValidationError.18",
                        "RenameExampleClassPlugin",
                        "replaceString"));
            }
        }
        return valid;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        String oldType = introspectedTable.getMyBatis3JavaMapperType();
        Matcher matcher = pattern.matcher(oldType);
        oldType = matcher.replaceAll(replaceString);

        introspectedTable.setMyBatis3JavaMapperType(oldType);
    }
}