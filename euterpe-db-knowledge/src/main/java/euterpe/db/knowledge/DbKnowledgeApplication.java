package euterpe.db.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ryan.wang
 */
@SpringBootApplication
@MapperScan("euterpe.db.knowledge.mybatis.mapper")
public class DbKnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbKnowledgeApplication.class, args);
    }

}
