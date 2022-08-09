package com.tyc.consumer.util;

import com.tyc.consumer.annotation.RpcReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 扫描特定 bin
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 14:26:28
 */
public class ScanUtill {

    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new SimpleMetadataReaderFactory();

    /**
     * 扫描带有特定注解的属性
     */
    public static Set<Class<?>> scanAnnotationField(String scanPath,Class clazz) {
        String path = ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPath));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path + "/**/*.class";

        Set<Class<?>> classes = new HashSet<>();
        try {
            Resource[] resources = RESOLVER.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    Class<?> aClass1 = Class.forName(classMetadata.getClassName());
                    Field[] fields = aClass1.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Annotation[] annotations = field.getAnnotations();
                        for (Annotation annotation : annotations) {
                            Class<? extends Annotation> annotationClass = annotation.annotationType();
                            if(annotationClass == clazz){
                                classes.add(field.getType());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }


    public static void main(String[] args) {
        String scanPath = "com.tyc.consumer";
        Set<Class<?>> classes = scanAnnotationField(scanPath, RpcReference.class);
//        Set<Class<?>> classes = scanAnnotations(scanPath, RPCAutowired.class,Service.class);
//        Set<Class<?>> classes = scanAll(scanPath);
//        Set<Class<?>> classes = scanInterface(scanPath);
        for (Class<?> aClass : classes) {
            System.out.println(aClass.getName());
        }
    }
}
