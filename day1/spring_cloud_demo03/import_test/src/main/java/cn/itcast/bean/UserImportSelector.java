package cn.itcast.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.util.Set;

@Slf4j
public class UserImportSelector implements ImportSelector{

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata){
        Set<String> annotationTypes = importingClassMetadata.getAnnotationTypes();
        log.info("annotationTypes: {}", annotationTypes);

        //获取配置类的名称
        return new String[]{(UserConfiguration.class.getName())};
    }
}
