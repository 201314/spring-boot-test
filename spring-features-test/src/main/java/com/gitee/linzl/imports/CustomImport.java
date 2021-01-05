package com.gitee.linzl.imports;

import com.gitee.linzl.bean.cycle.BeanLifeCycle;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 自定义逻辑返回需要导入的组件
 **/
public class CustomImport implements ImportSelector {
    /**
     * 返回值，就是导入到容器中的组件类名
     *
     * @param annotationMetadata 用来获取被@Import标注的类上面所有的注解信息
     * @return
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        // 方法不要返回null值
        return new String[]{BeanLifeCycle.class.getName()};
    }
}
