package com.gitee.linzl.filter;

import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * 自定义过滤规则
 *
 * @author YSW
 * @create 2019-12-16 22:34
 **/
public class CustomFilter implements TypeFilter {
    /**
     * 此方法作为过滤的方法
     *
     * @param metadataReader        代表当前正在扫描的类
     * @param metadataReaderFactory 可以通过此对象获取到的其他扫描过的类
     * @return
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {
        // 获取的是当前类的所有注解
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        // 获取到所有的类关系，如父类子类接口等
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        // 获取类的资源 如路径地址等
        Resource resource = metadataReader.getResource();
        // 获取类的名称
        String className = classMetadata.getClassName();
        System.out.println("--->" + className);
        if (className.contains("er")) {
            System.out.println("er--》" + className);
            return true;
        }
        return false;
    }
}
