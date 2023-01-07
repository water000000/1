package seat.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@Configuration
@PropertySource("classpath:info.properties")
public class NacosMetadataConfig {
    @Value("${version}")
    private String version;

    @Autowired
    private Environment environment;

    @Bean
    public NacosDiscoveryProperties nacosProperties() throws IOException {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        Map<String, String> metadata = properties.getMetadata();
//        ProjectVersion version = new ProjectVersion();
        System.out.println(version);
        metadata.put("version", version);

        updateYaml();
//        updateApplicationName();

        System.out.println(environment.getProperty("spring.cloud.nacos.discovery.service"));
        System.out.println(environment.getProperty("spring.application.name"));

        return properties;
    }

    private void updateYaml() throws IOException {
        String url = NacosDiscoveryProperties.class.getClassLoader().getResource("application.yml").getPath();
        Yaml yaml = new Yaml();
        FileWriter writer;
        FileInputStream inputStream = new FileInputStream(new File(url));
        Map<String, Object> yamlMap = yaml.load(inputStream);
        String name = environment.getProperty("spring.application.name");
        yamlMap.get("spring.application.name");
        String regex = "v[0-9]";
        int pos = getChar(version, '.');
        String majorVersion = version.substring(0, pos);
        yamlMap.put("spring.cloud.nacos.discovery.service", name + "-v" + majorVersion);
//        yamlMap.put("spring.application.name", "service");
        writer = new FileWriter(new File(url));
        writer.write(yaml.dumpAsMap(yamlMap));
        writer.flush();
        writer.close();
        inputStream.close();
    }

    private int getChar(String str, char ch) {
        for(int i = 0; i < str.length(); ++i) {
            if(ch == str.charAt(i))
                return i;
        }

        return -1;
    }
}
