package com.tyc.provider.util;

import java.util.Set;

import com.tyc.provider.annotation.RpcService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.util.HashSet;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 14:26:28
 */
public class ScanUtill {


    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new SimpleMetadataReaderFactory();

    /**
     * 扫描带有特定注解的类
     */
    public static Set<Class<?>> scanAnnotation(String scanPath,Class clazz) {
        String path = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/**/*.class";

        Set<Class<?>> classes = new HashSet<>();
        try {
            Resource[] resources = RESOLVER.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                    Set<String> annotationTypes = annotationMetadata.getAnnotationTypes();
                    for (String annotationType : annotationTypes) {
                        Class<?> aClass = Class.forName(annotationType);
                        if(aClass == clazz){
                            classes.add(Class.forName(classMetadata.getClassName()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 扫描带有任一注解的所有类
     */
    public static Set<Class<?>> scanAnnotations(String scanPath,Class... clazzs) {
        Set<Class<?>> classes = new HashSet<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        for (Class clazz : clazzs) {
            provider.addIncludeFilter(new AnnotationTypeFilter(clazz));
        }
        Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents(scanPath);
        for (BeanDefinition bd : beanDefinitionSet) {
            Class clz = null;
            try {
                clz = Class.forName(bd.getBeanClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            classes.add(clz);
        }
        return classes;
    }

    /**
     * 扫描接口
     */
    public static Set<Class<?>> scanInterface(String scanPath) {
        String path = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/**/*.class";

        Set<Class<?>> classes = new HashSet<>();
        try {
            Resource[] resources = RESOLVER.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    if(classMetadata.isInterface()){
                        classes.add(Class.forName(classMetadata.getClassName()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 扫描所有
     */
    public static Set<Class<?>> scanAll(String scanPath) {
        String path = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/**/*.class";

        Set<Class<?>> classes = new HashSet<>();
        try {
            Resource[] resources = RESOLVER.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    classes.add(Class.forName(classMetadata.getClassName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void main(String[] args) {
        String scanPath = "com.tyc.provider";
        Set<Class<?>> classes = scanAnnotation(scanPath, RpcService.class);
//        Set<Class<?>> classes = scanAnnotations(scanPath, RPCAutowired.class,Service.class);
//        Set<Class<?>> classes = scanAll(scanPath);
//        Set<Class<?>> classes = scanInterface(scanPath);
        for (Class<?> aClass : classes) {
            System.out.println(aClass.getName());
            System.out.println(aClass.getSimpleName());
        }
    }
}
