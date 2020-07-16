package mybatisExt;

import com.qihoo.finance.msf.api.annotation.MyBatisDao;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;

import java.util.List;


public class MyBatisDaoAnnotationPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(MyBatisDao.class.getName());
        interfaze.addImportedType(javaType);
        interfaze.addAnnotation("@MyBatisDao");
        return super.clientGenerated(interfaze, introspectedTable);
    }
}
